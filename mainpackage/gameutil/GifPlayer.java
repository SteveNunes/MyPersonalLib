package gameutil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

import com.madgag.gif.fmsware.GifDecoder;

import util.Timer;

public class GifPlayer {

	private GifDecoder gifDecoder;
	private int frameIndex;
	private long frameDelay;
	private long lastFrameTime;
	private Consumer<BufferedImage> onFrameChange;
	private Consumer<Integer> onFrameReachEnd;
	private boolean loop;
	private boolean isPlaying;
	private boolean isPaused;
	
	public GifPlayer(File gifPath)
		{ loadGif(gifPath); }
	
	public GifPlayer(String gifAbsolutePath)
		{ loadGif(gifAbsolutePath); }

	public void loadGif(File gifPath) {
		try (FileInputStream fis = new FileInputStream(gifPath)) {
			frameIndex = 0;
			lastFrameTime = 0;
			gifDecoder = new GifDecoder();
			loop = gifDecoder.getLoopCount() == 0;
			int status = gifDecoder.read(fis);
			if (status != GifDecoder.STATUS_OK)
				throw new RuntimeException("Error loading gif (Error: " + status + ")");
			frameDelay = gifDecoder.getDelay(0);
			onFrameChange = null;
			isPlaying = false;
			isPaused = false;
		}
		catch (IOException e)
			{ System.err.println("Error reading gif file: " + e.getMessage()); }
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
	
	public void setOnFrameChange(Consumer<BufferedImage> consumer) {
		onFrameChange = consumer;
	}

	private void loadGif(String gifAbsolutePath)
		{ loadGif(new File(gifAbsolutePath)); }

	public void seek(int frame) {
		frameIndex = frame % gifDecoder.getFrameCount();
		frameDelay = gifDecoder.getDelay(frameIndex);
    lastFrameTime = System.nanoTime();
  	if (onFrameChange != null)
  		onFrameChange.accept(getCurrentFrameImage());
		if (frame == 0 && !loop)
			createTimer();
	}
	
	public Duration getDuration() {
		long totalDuration = 0;
		for (int i = 0; i < gifDecoder.getFrameCount(); i++)
			totalDuration += gifDecoder.getDelay(i);
		return Duration.ofMillis(totalDuration * (gifDecoder.getLoopCount() == 0 ? 1 : gifDecoder.getLoopCount()));
	}
	
	public BufferedImage getCurrentFrameImage() {
		return gifDecoder.getFrame(frameIndex);
	}
	
	public int getCurrentFrameIndex()
		{ return frameIndex; }
	
	public GifDecoder getGifDecoder()
		{ return gifDecoder; }
	
	public void close() {
		Timer.stopTimer("updateGifFrame@" + hashCode());	
	}
	
}
