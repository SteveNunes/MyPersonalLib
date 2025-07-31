package util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.jarnbjo.vorbis.VorbisAudioFileReader;
import enums.OggState;

public final class OggPlayer {

	private static final AtomicLong nextLineId = new AtomicLong();
	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	private static final Map<String, PreLoadedOgg> preloaded = new ConcurrentHashMap<>();
	private static final Map<Long, SourceDataLine> dataLineFromId = new ConcurrentHashMap<>();
	private static final Map<Long, String> pathFromId = new ConcurrentHashMap<>();
	private static final Map<Long, OggState> state = new ConcurrentHashMap<>();
	private static final Map<String, Set<Long>> dataLineIds = new ConcurrentHashMap<>();

	private static BiConsumer<String, SourceDataLine> onPlayOgg;
	private static BiConsumer<String, SourceDataLine> onPauseOgg;
	private static BiConsumer<String, SourceDataLine> onResumeOgg;
	private static BiConsumer<String, SourceDataLine> onStopOgg;
	private static BiConsumer<String, SourceDataLine> onEndOgg;

	static {
		Misc.addShutdownEvent(OggPlayer::shutdown);
	}

	public static void setOnEndEvent(BiConsumer<String, SourceDataLine> onEndOgg) {
		OggPlayer.onEndOgg = onEndOgg;
	}

	public static void setOnPlayEvent(BiConsumer<String, SourceDataLine> onPlayOgg) {
		OggPlayer.onPlayOgg = onPlayOgg;
	}

	public static void setOnPauseEvent(BiConsumer<String, SourceDataLine> onPauseOgg) {
		OggPlayer.onPauseOgg = onPauseOgg;
	}

	public static void setOnResumeEvent(BiConsumer<String, SourceDataLine> onResumeOgg) {
		OggPlayer.onResumeOgg = onResumeOgg;
	}

	public static void setOnStopEvent(BiConsumer<String, SourceDataLine> onStopOgg) {
		OggPlayer.onStopOgg = onStopOgg;
	}

	public static Duration getDuration(String oggPath) {
		return getDuration(oggPath, 1.0f);
	}

	public static Duration getDuration(String oggPath, float rate) {
		PreLoadedOgg ogg = getOrLoadOgg(oggPath);
		if (ogg == null)
			return Duration.ZERO;

		try {
			long totalFrames = ogg.getAudioBytes().length / ogg.getOriginalFormat().getFrameSize();
			float adjustedFrameRate = ogg.getOriginalFormat().getFrameRate() * rate;
			if (adjustedFrameRate > 0)
				return Duration.ofMillis((long) (totalFrames / adjustedFrameRate * 1000.0));
		}
		catch (Exception e) {
			System.err.println("Erro ao calcular duração do OGG '" + oggPath + "' com rate " + rate + ": " + e.getMessage());
			e.printStackTrace();
		}
		return Duration.ZERO;
	}

	private static PreLoadedOgg getOrLoadOgg(String oggPath) {
		return preloaded.computeIfAbsent(oggPath, key -> {
			File file = new File(key);
			if (!file.exists() || !file.isFile() || !file.canRead())
				throw new RuntimeException("Erro: Arquivo OGG não encontrado, não é um arquivo, ou não pode ser lido: " + key);
			if (!key.toLowerCase().endsWith(".ogg"))
				throw new RuntimeException("Erro: O arquivo '" + key + "' não parece ser um OGG.");

			try (AudioInputStream audioInputStream = new VorbisAudioFileReader().getAudioInputStream(file)) {
				AudioFormat originalFormat = audioInputStream.getFormat();
				byte[] allAudioBytes = audioInputStream.readAllBytes();
				return new PreLoadedOgg(allAudioBytes, originalFormat);
			}
			catch (UnsupportedAudioFileException e) {
				throw new RuntimeException("Erro: Formato de áudio não suportado para '" + key + "'. Pode não ser um OGG válido ou faltam codecs: " + e.getMessage());
			}
			catch (IOException e) {
				throw new RuntimeException("Erro de I/O ao carregar OGG '" + key + "': " + e.getMessage());
			}
			catch (Exception e) {
				throw new RuntimeException("Erro inesperado ao carregar OGG '" + key + "': " + e.getMessage());
			}
		});
	}

	public static SourceDataLine getDataLineFromId(long id) {
		return dataLineFromId.get(id);
	}

	public static String getPathFromId(long id) {
		return pathFromId.get(id);
	}

	public static Set<Long> getDataLinesFromPath(String oggPath) {
		if (oggPath == null)
			return Collections.emptySet();
		return dataLineIds.getOrDefault(oggPath, Collections.emptySet());
	}

	public static void stopAll() {
		for (String path : new ArrayList<>(dataLineIds.keySet()))
			stopDataLines(path);
	}

	public static void stopDataLines(String path) {
		Set<Long> idsToStop = dataLineIds.get(path);
		if (idsToStop != null)
			for (long id : new ArrayList<>(idsToStop))
				stopById(id);
	}

	public static void stopById(long id) {
		SourceDataLine line = dataLineFromId.get(id);
		if (line != null) {
			if (getState(id) != OggState.STOPPED && getState(id) != OggState.ENDED) {
				state.put(id, OggState.STOPPED);
				return;
			}
			line.stop();
			line.close();
			dataLineFromId.remove(id);
			pathFromId.remove(id);
			state.remove(id);

			String oggPath = pathFromId.get(id);
			if (oggPath != null) {
				if (getState(id) == OggState.STOPPED && onStopOgg != null)
					onStopOgg.accept(oggPath, line);
				if (getState(id) == OggState.ENDED && onEndOgg != null)
					onEndOgg.accept(oggPath, line);
				Set<Long> currentIds = dataLineIds.get(oggPath);
				if (currentIds != null) {
					currentIds.remove(id);
					if (currentIds.isEmpty())
						dataLineIds.remove(oggPath);
				}
			}
		}
	}

	public static long play(String path) {
		return play(path, 100, 1.0f);
	}

	public static long play(String oggPath, int volume, float rate) {
		if (executorService.isShutdown())
			throw new RuntimeException("Erro: OggPlayer foi encerrado. Não é possível reproduzir áudio.");
		final PreLoadedOgg ogg = getOrLoadOgg(oggPath);

		if (ogg == null)
			return -1;

		final long lineId = nextLineId.getAndIncrement();
		executorService.execute(() -> {
			SourceDataLine line = null;
			AudioInputStream audioStreamToPlay = null;

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(ogg.getAudioBytes());
				audioStreamToPlay = new AudioInputStream(bais, ogg.getOriginalFormat(), ogg.getAudioBytes().length / ogg.getOriginalFormat().getFrameSize());
				AudioFormat actualPlayFormat = ogg.getOriginalFormat();

				if (rate != 1.0f && rate > 0f)
					actualPlayFormat = new AudioFormat(actualPlayFormat.getEncoding(), actualPlayFormat.getSampleRate() * rate, actualPlayFormat.getSampleSizeInBits(), actualPlayFormat.getChannels(), actualPlayFormat.getFrameSize(), actualPlayFormat.getFrameRate() * rate, actualPlayFormat.isBigEndian());

				DataLine.Info info = new DataLine.Info(SourceDataLine.class, actualPlayFormat);
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(actualPlayFormat);

				dataLineFromId.put(lineId, line);
				pathFromId.put(lineId, oggPath);
				state.put(lineId, OggState.PLAYING);
				dataLineIds.computeIfAbsent(oggPath, k -> ConcurrentHashMap.newKeySet()).add(lineId);

				line.start();
				if (onPlayOgg != null)
					onPlayOgg.accept(oggPath, line);
				

				if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
					if (volume == 0)
						volumeControl.setValue(volumeControl.getMinimum());
					else {
						float min = -60;
						float max = volumeControl.getMaximum();
						float volume2 = Math.min(100f, Math.max(0f, volume));
						float gainValue = min + (volume2 / 100f) * (max - min);
						volumeControl.setValue(gainValue);
					}
				}

				byte[] buffer = new byte[4096];
				int bytesRead;
				OggState stt;
				while (state.containsKey(lineId) && (stt = getState(lineId)) != OggState.STOPPED) {
					if (stt == OggState.PAUSED)
						Misc.sleep(50);
					else {
						bytesRead = audioStreamToPlay.read(buffer, 0, buffer.length);
						if (bytesRead == -1 || stt == OggState.STOPPED || !dataLineFromId.containsKey(lineId)) {
							if (bytesRead == -1 && state.containsKey(lineId) && stt != OggState.STOPPED)
								state.put(lineId, OggState.ENDED);
							break;
						}
						else 
							line.write(buffer, 0, bytesRead);
					}
				}
				line.drain();
			}
			catch (LineUnavailableException e) {
				System.err.println("Erro: Linha de áudio indisponível para '" + oggPath + "' (Pode haver muitas linhas abertas ou problema com hardware de áudio): " + e.getMessage());
			}
			catch (Exception e) {
				System.err.println("Erro durante a reprodução de áudio '" + oggPath + "': " + e.getMessage());
				e.printStackTrace();
			}
			finally {
				stopById(lineId);
				if (audioStreamToPlay != null) {
					try {
						audioStreamToPlay.close();
					}
					catch (IOException e) {
						System.err.println("Erro ao fechar AudioInputStream para '" + oggPath + "': " + e.getMessage());
					}
				}
			}
		});
		return lineId;
	}
	
	public static OggState getState(long oggId) {
		return state.containsKey(oggId) ? state.get(oggId) : null;
	}
	
	public static boolean isPaused(long oggId) {
		if (state.containsKey(oggId))
			return getState(oggId) == OggState.PAUSED;
		return false;
	}

	public static void pause(long oggId) {
		OggState stt = getState(oggId);
		if (stt != null && stt != OggState.STOPPED && stt != OggState.ENDED) {
			if (onPauseOgg != null)
				onPauseOgg.accept(getPathFromId(oggId), getDataLineFromId(oggId));
			state.put(oggId, OggState.PAUSED);
		}
	}

	public static void resume(long oggId) {
		OggState stt = getState(oggId);
		if (stt != null && stt != OggState.STOPPED && stt != OggState.ENDED) {
			if (onResumeOgg != null)
				onResumeOgg.accept(getPathFromId(oggId), getDataLineFromId(oggId));
			state.put(oggId, OggState.PLAYING);
		}
	}

	private static void shutdown() {
		if (executorService.isShutdown())
			throw new RuntimeException("Erro: O OggPlayer já foi encerrado.");

		stopAll();

		preloaded.clear();
		dataLineFromId.clear();
		pathFromId.clear();
		dataLineIds.clear();
		state.clear();

		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS))
				executorService.shutdownNow();
		}
		catch (InterruptedException e) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private static class PreLoadedOgg {
		
		private final byte[] audioBytes;
		private final AudioFormat originalFormat;

		public PreLoadedOgg(byte[] audioBytes, AudioFormat originalFormat) {
			this.audioBytes = audioBytes;
			this.originalFormat = originalFormat;
		}

		public byte[] getAudioBytes() {
			return audioBytes;
		}

		public AudioFormat getOriginalFormat() {
			return originalFormat;
		}
		
	}
	
}