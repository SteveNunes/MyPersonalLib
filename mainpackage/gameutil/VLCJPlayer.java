package gameutil;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Set;
import java.nio.file.Paths;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

public class VLCJPlayer {

	static {
		System.setProperty("jna.library.path", "C:\\Program Files\\VideoLAN\\VLC");
	}

	// A failed native cleanup must retain its callbacks until process termination.
	private static final Set<VLCJPlayer> LIVE = ConcurrentHashMap.newKeySet();
	private static final Semaphore CAPACITY = new Semaphore(12);
	// Limpeza não confirmada ou criação nativa interrompida. Demora, sozinha, não
	// ativa a trava.
	private static final AtomicBoolean CLEANUP_FAILED = new AtomicBoolean();
	private static final AtomicReference<RuntimeException> FIRST_CLEANUP_FAILURE = new AtomicReference<>();
	private static final ThreadLocal<Boolean> IN_CALLBACK = ThreadLocal.withInitial(() -> false);
	// Uma trava para TODAS as instâncias, inclusive construtores e release.
	// Callbacks nunca adquirem esta trava nem executam comandos VLC síncronos.
	private static final ReentrantLock nativeAccess = new ReentrantLock(true);
	private static final AtomicReference<Throwable> NATIVE_FAILURE = new AtomicReference<>();
	private volatile RuntimeException releaseFailure;
	private volatile boolean cleanupComplete;
	private volatile String cleanupStep = "liberação ainda não iniciada (pode estar aguardando um comando anterior)";
	private boolean permitHeld;
	private MediaPlayerFactory factory;
	private EmbeddedMediaPlayer mediaPlayer;

	/*
	 * ========================================================= REFERÊNCIAS FORTES
	 * DOS CALLBACKS NATIVOS
	 * =========================================================
	 *
	 * IMPORTANTE:
	 *
	 * Esses objetos NÃO devem ser criados apenas como objetos temporários dentro de
	 * videoSurface().set(...).
	 *
	 * Manter referências fortes reduz o risco de a JNA considerar algum callback
	 * elegível para Garbage Collection enquanto o LibVLC ainda estiver
	 * utilizando-o.
	 */

	private final FxBufferFormatCallback bufferFormatCallback = new FxBufferFormatCallback();

	private final FxRenderCallback renderCallback = new FxRenderCallback();

	private FxCanvasVideoSurface videoSurface;

	/*
	 * Impede a criação de uma fila crescente de Platform.runLater() caso a JavaFX
	 * Application Thread esteja mais lenta que o vídeo.
	 *
	 * Para vídeo, é melhor descartar frames intermediários do que acumular frames
	 * antigos.
	 */
	private final AtomicBoolean fxFramePending = new AtomicBoolean(false);

	// FX owns image; pending pixels are Java-owned copies, never native buffers.
	private WritableImage image;
	private final AtomicReference<FxFrame> pendingFx = new AtomicReference<>();

	private static final class FxFrame {
		final byte[] pixels;
		final int width, height;

		FxFrame(byte[] pixels, int width, int height) {
			this.pixels = pixels;
			this.width = width;
			this.height = height;
		}
	}

	private volatile int videoWidth;
	private volatile int videoHeight;

	private volatile boolean ready;
	private volatile boolean released;

	/*
	 * Callback JavaFX.
	 *
	 * Executado na JavaFX Application Thread.
	 */
	private volatile Consumer<WritableImage> onFrame;

	/*
	 * Callback RAW.
	 *
	 * Executado diretamente na thread de renderização do VLCJ.
	 *
	 * O ByteBuffer pertence ao LibVLC.
	 */
	private volatile Consumer<VideoFrame> onRawFrame;

	private volatile Runnable onReady;
	private volatile Runnable onPlaying;
	private volatile Runnable onPaused;
	private volatile Runnable onStopped;
	private volatile Runnable onFinished;

	private volatile Consumer<String> onError;
	private volatile Consumer<Dimension2D> onResolutionChanged;

	// =========================================================
	// Construtor
	// =========================================================

	public VLCJPlayer() {
		this(null);
	}

	/** Callback leve de diagnóstico, chamado após adquirir a trava global. */
	public VLCJPlayer(Runnable onInitializationStarted) {
		checkNativeThread();
		nativeAccess.lock();
		try {
			checkNativeHealth();
			if (!CAPACITY.tryAcquire())
				throw new IllegalStateException("Limite de players atingido; verifique liberações pendentes.");
			if (CLEANUP_FAILED.get()) {
				CAPACITY.release();
				throw new IllegalStateException("Criação bloqueada após falha de limpeza nativa; reinicie o aplicativo.", FIRST_CLEANUP_FAILURE.get());
			}
			permitHeld = true;
			LIVE.add(this);
			try {
				// jna.library.path já define a instalação do VLC no bloco static.
				// O construtor padrão executa descoberta via ServiceLoader em cada player;
				// inicializações simultâneas podem disputar os providers compartilhados.
				// null desativa somente a descoberta: carga JNA e validação do VLC permanecem.
				if (onInitializationStarted != null)
					onInitializationStarted.run();
				factory = new MediaPlayerFactory((NativeDiscovery) null, new String[0]);

				mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

				/*
				 * Guardamos uma referência forte para a VideoSurface.
				 */
				videoSurface = new FxCanvasVideoSurface();

				mediaPlayer.videoSurface().set(videoSurface);

				mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

					@Override
					public void playing(MediaPlayer mediaPlayer) {

						if (released)
							return;

						ready = true;

						runFx(onPlaying);

						Runnable r = onReady;

						if (r != null) {

							/*
							 * Dispara apenas uma vez por play.
							 */
							onReady = null;

							runFx(r);
						}
					}

					@Override
					public void paused(MediaPlayer mediaPlayer) {

						if (released)
							return;

						runFx(onPaused);
					}

					@Override
					public void stopped(MediaPlayer mediaPlayer) {

						ready = false;

						if (released)
							return;

						runFx(onStopped);
					}

					@Override
					public void finished(MediaPlayer mediaPlayer) {

						ready = false;

						if (released)
							return;

						runFx(onFinished);
					}

					@Override
					public void error(MediaPlayer mediaPlayer) {

						ready = false;

						if (released)
							return;

						Consumer<String> callback = onError;

						if (callback != null) {

							Platform.runLater(() -> {

								if (!released) {
									callback.accept("Erro ao reproduzir a mídia.");
								}
							});
						}
					}

					@Override
					public void videoOutput(MediaPlayer mediaPlayer, int newCount) {

						/*
						 * Evento disponível caso futuramente seja necessário monitorar saída de vídeo.
						 */
					}
				});
			}
			catch (RuntimeException | Error failure) {
				if (failure instanceof Error || hasNativeFailure()) {
					// Uma atribuição incompleta de factory NÃO comprova limpeza nativa.
					// Não chama release em uma biblioteca cujo estado é desconhecido.
					reportNativeFailure(failure);
					released = true;
					cleanupStep = "falha nativa na criação; recursos retidos até reiniciar";
					releaseFailure = new IllegalStateException(cleanupStep, failure);
					FIRST_CLEANUP_FAILURE.compareAndSet(null, releaseFailure);
					CLEANUP_FAILED.set(true);
					throw failure;
				}
				try {
					release();
				}
				catch (RuntimeException | Error cleanup) {
					failure.addSuppressed(cleanup);
				}
				throw failure;
			}
		}
		finally {
			nativeAccess.unlock();
		}
	}

	// =========================================================
	// Reprodução
	// =========================================================

	public void play(String videoPath) {
		play(videoPath, null);
	}

	/** onStarting marca o início real, após esperar pela trava global. */
	public void play(String videoPath, Runnable onStarting) {
		enterNative();
		try {

			checkReleased();

			ready = false;

			if (onStarting != null)
				onStarting.run();
			if (!mediaPlayer.media().play(Paths.get(videoPath).toAbsolutePath().toUri().toString()))
				throw new IllegalStateException("VLC recusou a mídia: " + videoPath);

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void playUrl(String url) {
		enterNative();
		try {

			checkReleased();

			ready = false;

			if (!mediaPlayer.media().play(url))
				throw new IllegalStateException("VLC recusou a URL: " + url);

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void pause() {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().pause();
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void resume() {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().play();
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void stop() {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().stop();
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	// =========================================================
	// Release
	// =========================================================

	/** Must run on a worker, never on FX or a native callback. */
	public void release() {
		enterNative();
		try {
			if (cleanupComplete)
				return;
			if (releaseFailure != null)
				throw releaseFailure;
			released = true; // Reject callbacks/commands; this is NOT cleanup success.
			ready = false;
			onRawFrame = null;
			onFrame = null;
			onReady = null;
			onPlaying = null;
			onPaused = null;
			onStopped = null;
			onFinished = null;
			onError = null;
			onResolutionChanged = null;
			pendingFx.set(null);
			try {
				if (mediaPlayer != null) {
					cleanupStep = "parando reprodução: mediaPlayer.controls().stop()";
					mediaPlayer.controls().stop();
					cleanupStep = "liberando player: mediaPlayer.release()";
					mediaPlayer.release();
					mediaPlayer = null;
				}
				if (factory != null) {
					cleanupStep = "liberando factory: factory.release()";
					factory.release();
					factory = null;
				}
				// Preserva as referências até todas as liberações terminarem.
				videoSurface = null;
				cleanupStep = "limpeza concluída";
				cleanupComplete = true;
				LIVE.remove(this);
				if (permitHeld) {
					permitHeld = false;
					CAPACITY.release();
				}
			}
			catch (RuntimeException | Error failure) {
				if (failure instanceof Error)
					reportNativeFailure(failure);
				releaseFailure = new IllegalStateException("Liberação nativa incompleta na etapa [" + cleanupStep + "]. Player retido; a liberação não será repetida porque pode ter sido parcialmente executada.", failure);
				FIRST_CLEANUP_FAILURE.compareAndSet(null, releaseFailure);
				CLEANUP_FAILED.set(true);
				throw releaseFailure;
			}
		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public boolean isCleanupComplete() {
		return cleanupComplete;
	}

	public static boolean hasCleanupFailure() {
		return CLEANUP_FAILED.get();
	}

	/**
	 * Diagnóstico não bloqueante; pode ser consultado durante uma chamada nativa.
	 */
	public String getCleanupStatus() {
		RuntimeException failure = releaseFailure;
		return failure == null ? cleanupStep : failure.getMessage();
	}

	private static void checkNativeThread() {
		if (Platform.isFxApplicationThread() || IN_CALLBACK.get())
			throw new IllegalStateException("Operação VLC síncrona exige executor de comandos, fora da FX e dos callbacks nativos.");
	}

	/** Estado permanente: somente um novo processo permite voltar a usar o VLC. */
	public static boolean hasNativeFailure() {
		return NATIVE_FAILURE.get() != null;
	}

	public static Throwable getNativeFailure() {
		return NATIVE_FAILURE.get();
	}

	// Não adquire nativeAccess: também pode ser chamado de um callback nativo.
	public static void reportNativeFailure(Throwable failure) {
		if (failure != null && NATIVE_FAILURE.compareAndSet(null, failure)) {
			System.err.println("VLC: falha nativa grave. Novos comandos, players e liberações nativas bloqueados; reinicie o aplicativo.");
			failure.printStackTrace();
		}
	}

	private static void checkNativeHealth() {
		Throwable failure = NATIVE_FAILURE.get();
		if (failure != null)
			throw new IllegalStateException("VLC bloqueado após falha nativa; reinicie o aplicativo.", failure);
	}

	private void enterNative() {
		checkNativeThread();
		nativeAccess.lock();
		try {
			checkNativeHealth();
		}
		catch (RuntimeException | Error failure) {
			nativeAccess.unlock();
			throw failure;
		}
	}

	// =========================================================
	// Eventos
	// =========================================================

	/**
	 * Recebe cada frame como WritableImage.
	 *
	 * Executado na JavaFX Application Thread.
	 */
	public void setOnFrame(Consumer<WritableImage> event) {

		this.onFrame = event;
	}

	/**
	 * Recebe diretamente o buffer RAW usado pelo VLCJ.
	 *
	 * Executado na thread de renderização do VLCJ.
	 *
	 * IMPORTANTE:
	 *
	 * Não mantenha o ByteBuffer para processamento posterior sem antes copiá-lo.
	 */
	public void setOnRawFrame(Consumer<VideoFrame> event) {

		this.onRawFrame = event;
	}

	public void setOnReady(Runnable event) {
		this.onReady = event;
	}

	public void setOnPlaying(Runnable event) {
		this.onPlaying = event;
	}

	public void setOnPaused(Runnable event) {
		this.onPaused = event;
	}

	public void setOnStopped(Runnable event) {
		this.onStopped = event;
	}

	public void setOnFinished(Runnable event) {
		this.onFinished = event;
	}

	public void setOnError(Consumer<String> event) {

		this.onError = event;
	}

	public void setOnResolutionChanged(Consumer<Dimension2D> event) {

		this.onResolutionChanged = event;
	}

	// =========================================================
	// Informações
	// =========================================================

	public boolean isReady() {
		return ready;
	}

	/**
	 * True means commands are disabled; use isCleanupComplete() for native cleanup.
	 */
	public boolean isReleased() {
		return released;
	}

	public boolean isPlaying() {
		enterNative();
		try {

			return mediaPlayer != null && !released && mediaPlayer.status().isPlaying();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public Dimension2D getVideoResolution() {

		return new Dimension2D(videoWidth, videoHeight);
	}

	public long getTime() {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return 0;

			return mediaPlayer.status().time();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public long getLength() {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return 0;

			return mediaPlayer.status().length();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public float getPosition() {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return 0f;

			return mediaPlayer.status().position();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public int getVolume() {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return 0;

			return mediaPlayer.audio().volume();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public boolean isMute() {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return false;

			return mediaPlayer.audio().isMute();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public float getRate() {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return 0f;

			return mediaPlayer.status().rate();

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	// =========================================================
	// Controles
	// =========================================================

	public void setVolume(int volume) {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.audio().setVolume(volume);
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void setMute(boolean mute) {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.audio().setMute(mute);
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void setRate(float rate) {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().setRate(rate);
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void setRepeat(boolean repeat) {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().setRepeat(repeat);
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void setTime(long millis) {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().setTime(millis);
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void setPosition(float position) {
		enterNative();
		try {

			if (mediaPlayer != null && !released) {
				mediaPlayer.controls().setPosition(position);
			}

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	/**
	 * Seek para um ponto específico do vídeo em milissegundos.
	 */
	public void seekTo(long millis) {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return;

			if (millis < 0)
				millis = 0;

			long length = getLength();

			if (length > 0 && millis > length)
				millis = length;

			mediaPlayer.controls().setTime(millis);

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	/**
	 * Seek relativo.
	 *
	 * Positivo = avança. Negativo = retrocede.
	 */
	public void seekBy(long deltaMillis) {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return;

			long target = getTime() + deltaMillis;

			if (target < 0)
				target = 0;

			long length = getLength();

			if (length > 0 && target > length)
				target = length;

			mediaPlayer.controls().setTime(target);

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	/**
	 * Seek:
	 *
	 * 0.0f = início 1.0f = final
	 */
	public void seekToPercent(float percent) {
		enterNative();
		try {

			if (mediaPlayer == null || released)
				return;

			if (percent < 0f)
				percent = 0f;
			else if (percent > 1f)
				percent = 1f;

			mediaPlayer.controls().setPosition(percent);

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	public void seekToPercent(int percent) {
		enterNative();
		try {

			seekToPercent(percent / 100f);

		}
		catch (Error failure) {
			reportNativeFailure(failure);
			throw failure;
		}
		finally {
			nativeAccess.unlock();
		}
	}

	// =========================================================
	// Utilidades
	// =========================================================

	private void runFx(Runnable action) {

		if (action == null || released)
			return;

		Platform.runLater(() -> {

			if (!released) {

				try {
					action.run();
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void checkReleased() {

		if (released || mediaPlayer == null) {

			throw new IllegalStateException("VLCJPlayer já foi liberado.");
		}
	}

	// =========================================================
	// VideoFrame
	// =========================================================

	public static final class VideoFrame {

		private final ByteBuffer buffer;

		private final int width;
		private final int height;

		private final int displayWidth;
		private final int displayHeight;

		private VideoFrame(ByteBuffer buffer, int width, int height, int displayWidth, int displayHeight) {

			this.buffer = buffer;

			this.width = width;
			this.height = height;

			this.displayWidth = displayWidth;
			this.displayHeight = displayHeight;
		}

		public ByteBuffer getBuffer() {
			return buffer;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getDisplayWidth() {
			return displayWidth;
		}

		public int getDisplayHeight() {
			return displayHeight;
		}

		public int getBufferSize() {
			return width * height * 4;
		}
	}

	// =========================================================
	// Video Surface
	// =========================================================

	private class FxCanvasVideoSurface extends CallbackVideoSurface {

		FxCanvasVideoSurface() {

			super(bufferFormatCallback, renderCallback, true, VideoSurfaceAdapters.getVideoSurfaceAdapter());
		}
	}

	// =========================================================
	// Buffer Format
	// =========================================================

	private class FxBufferFormatCallback implements BufferFormatCallback {

		@Override
		public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {

			if (released) {

				return new RV32BufferFormat(sourceWidth, sourceHeight);
			}

			boolean changed = sourceWidth != videoWidth || sourceHeight != videoHeight;

			videoWidth = sourceWidth;
			videoHeight = sourceHeight;

			Consumer<Dimension2D> callback = onResolutionChanged;

			if (changed && callback != null) {

				Dimension2D resolution = new Dimension2D(videoWidth, videoHeight);

				Platform.runLater(() -> {

					if (!released && onResolutionChanged == callback) {

						try {
							callback.accept(resolution);
						}
						catch (Throwable e) {
							e.printStackTrace();
						}
					}
				});
			}

			return new RV32BufferFormat(sourceWidth, sourceHeight);
		}

		@Override
		public void newFormatSize(int bufferWidth, int bufferHeight, int displayWidth, int displayHeight) {}

		@Override
		public void allocatedBuffers(ByteBuffer[] buffers) {
			// RAW mode requires no JavaFX image and retains no native ByteBuffer.
		}
	}

	// =========================================================
	// Render Callback
	// =========================================================

	private class FxRenderCallback implements RenderCallback {

		@Override
		public void lock(MediaPlayer mediaPlayer) {}

		@Override
		public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat, int displayWidth, int displayHeight) {
			if (released || hasNativeFailure() || nativeBuffers == null || nativeBuffers.length == 0 || nativeBuffers[0] == null)
				return;
			boolean previousCallback = IN_CALLBACK.get();
			IN_CALLBACK.set(true);
			try {
				int width = bufferFormat.getWidth(), height = bufferFormat.getHeight();
				Consumer<VideoFrame> rawCallback = onRawFrame;
				if (rawCallback != null)
					rawCallback.accept(new VideoFrame(nativeBuffers[0].asReadOnlyBuffer(), width, height, displayWidth, displayHeight));
				if (released || onFrame == null || !fxFramePending.compareAndSet(false, true))
					return;
				try {
					byte[] pixels = new byte[Math.multiplyExact(Math.multiplyExact(width, height), 4)];
					ByteBuffer source = nativeBuffers[0].duplicate();
					source.clear();
					source.get(pixels);
					// RV32 is BGRX, not premultiplied BGRA.
					for (int i = 3; i < pixels.length; i += 4)
						pixels[i] = (byte) 255;
					pendingFx.set(new FxFrame(pixels, width, height));
					Platform.runLater(() -> {
						try {
							FxFrame frame = pendingFx.getAndSet(null);
							Consumer<WritableImage> callback = onFrame;
							if (released || frame == null || callback == null)
								return;
							if (image == null || image.getWidth() != frame.width || image.getHeight() != frame.height)
								image = new WritableImage(frame.width, frame.height);
							image.getPixelWriter().setPixels(0, 0, frame.width, frame.height, PixelFormat.getByteBgraInstance(), frame.pixels, 0, frame.width * 4);
							callback.accept(image);
						}
						catch (RuntimeException | LinkageError failure) {
							failure.printStackTrace();
						}
						finally {
							fxFramePending.set(false);
						}
					});
				}
				catch (RuntimeException | Error failure) {
					pendingFx.set(null);
					fxFramePending.set(false);
					throw failure;
				}
			}
			catch (Throwable failure) {
				// Nunca propaga uma falha Java para dentro da JNA.
				if (failure instanceof Error)
					reportNativeFailure(failure);
				failure.printStackTrace();
			}
			finally {
				IN_CALLBACK.set(previousCallback);
			}
		}

		@Override
		public void unlock(MediaPlayer mediaPlayer) {}
	}
}
