package gameutil;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.madgag.gif.fmsware.GifDecoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import util.Timer;

public class GifPlayer {

	private int frameIndex;
	private long frameDelay;
	private long lastFrameTime;
	private Consumer<WritableImage> onFrameChange;
	private Consumer<Integer> onFrameReachEnd;
	private boolean loop;
	private boolean isPlaying;
	private boolean isPaused;

	private List<WritableImage> frames;
	private List<Integer> frameDelays;
	private Duration duration;
	
	public static Dimension getGifFrameDimension(File gifPath) {
		try (FileInputStream fis = new FileInputStream(gifPath)) {
			GifDecoder gifDecoder = new GifDecoder();
			int status = gifDecoder.read(fis);
			if (status != GifDecoder.STATUS_OK)
				throw new RuntimeException("Error loading gif (Error: " + status + ")");
			BufferedImage firstFrame = gifDecoder.getFrame(0);
			return new Dimension(firstFrame.getWidth(), firstFrame.getHeight());
		}
		catch (IOException e) {
			throw new RuntimeException("Error reading gif file: " + e.getMessage());
		}
	}

	public GifPlayer(File gifPath) {
		loadGif(gifPath, 1);
	}

	public GifPlayer(File gifPath, float originalSizeMultipiler) {
		loadGif(gifPath, originalSizeMultipiler);
	}

	public GifPlayer(File gifPath, int width, int height) {
		loadGif(gifPath, width, height);
	}

	public void loadGif(File gifPath, float originalSizeMultipiler) {
		Dimension dimension = getGifFrameDimension(gifPath);
		loadGif(gifPath, (int)(dimension.getWidth() * originalSizeMultipiler), (int)(dimension.getHeight() * originalSizeMultipiler));
	}

	public void loadGif(File gifPath, int width, int height) {
		try (FileInputStream fis = new FileInputStream(gifPath)) {
			GifDecoder gifDecoder = new GifDecoder();
			int status = gifDecoder.read(fis);
			if (status != GifDecoder.STATUS_OK)
				throw new RuntimeException("Error loading gif (Error: " + status + ")");

			frameIndex = 0;
			lastFrameTime = 0;
			onFrameChange = null;
			isPlaying = false;
			isPaused = false;
			loop = gifDecoder.getLoopCount() == 0;

			preloadFrames(gifDecoder, width, height);

			frameDelay = frameDelays.isEmpty() ? 0 : frameDelays.get(0);
		}
		catch (IOException e) {
			System.err.println("Error reading gif file: " + e.getMessage());
		}
	}

	private void preloadFrames(GifDecoder gifDecoder, int width, int height) {
		frames = new ArrayList<>(gifDecoder.getFrameCount());
		frameDelays = new ArrayList<>(gifDecoder.getFrameCount());

		for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
			BufferedImage originalFrame = gifDecoder.getFrame(i);
			BufferedImage resizedFrame = resizeFrame(originalFrame, width, height);
			WritableImage fxFrame = SwingFXUtils.toFXImage(resizedFrame, null);

			frames.add(fxFrame);
			frameDelays.add(gifDecoder.getDelay(i));
		}

		long totalDuration = 0;
		for (int delay : frameDelays)
			totalDuration += delay;
		duration = Duration.ofMillis(totalDuration);
	}

	private BufferedImage resizeFrame(BufferedImage source, int width, int height) {
		if (source.getWidth() == width && source.getHeight() == height)
			return source;

		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(source, 0, 0, width, height, null);
		g.dispose();
		return resized;
	}

	private void createTimer() {
		Timer.createTimer("updateGifFrame@" + hashCode(), Duration.ofMillis(8), 0, () -> {
			if (System.nanoTime() - lastFrameTime >= frameDelay * 1_000_000) {
				int f = frameIndex;
				seek(frameIndex + (isPaused ? 0 : 1));
				if (f > 0 && frameIndex <= 0 && onFrameReachEnd != null) {
					onFrameReachEnd.accept(f);
					frameIndex = !loop ? f : 0;
					if (!loop)
						stop();
				}
			}
		});
	}

	public void play() {
		if (!isPlaying) {
			isPlaying = true;
			isPaused = false;
			createTimer();
		}
	}

	public void pause() {
		isPaused = !isPaused;
	}

	private void stop() {
		if (isPlaying) {
			isPlaying = false;
			isPaused = false;
			Timer.stopTimer("updateGifFrame@" + hashCode());
		}
	}

	public void setLoop(boolean state) {
		loop = state;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setOnFrameChange(Consumer<WritableImage> consumer) {
		onFrameChange = consumer;
	}

	public void setOnFrameReachEnd(Consumer<Integer> consumer) {
		onFrameReachEnd = consumer;
	}

	public void seek(int frame) {
		if (frames == null || frames.isEmpty())
			return;

		frameIndex = frame % frames.size();
		if (frameIndex < 0)
			frameIndex += frames.size();

		frameDelay = frameDelays.get(frameIndex);
		lastFrameTime = System.nanoTime();

		if (onFrameChange != null)
			onFrameChange.accept(getCurrentFrameImage());

		if (frame == 0 && !loop)
			createTimer();
	}

	public Duration getDuration() {
		return duration;
	}

	public WritableImage getCurrentFrameImage() {
		return frames.get(frameIndex);
	}

	public int getCurrentFrameIndex() {
		return frameIndex;
	}

	public List<WritableImage> getFrames() {
		return frames;
	}

	public List<Integer> getFrameDelays() {
		return frameDelays;
	}

	public void close() {
		Timer.stopTimer("updateGifFrame@" + hashCode());
	}
}