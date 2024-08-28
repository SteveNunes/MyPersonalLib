package gameutil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.madgag.gif.fmsware.GifDecoder;

import javafx.scene.image.WritableImage;

public class GifPlayer {

	private GifDecoder gifDecoder;
	private int frameIndex;
	private long frameDelay;
	private long lastFrameTime;
	
	public GifPlayer(File gifPath)
		{ loadGif(gifPath); }
	
	public GifPlayer(String gifAbsolutePath)
		{ loadGif(gifAbsolutePath); }

	public void loadGif(File gifPath) {
		try (FileInputStream fis = new FileInputStream(gifPath)) {
			frameIndex = 0;
			lastFrameTime = 0;
			gifDecoder = new GifDecoder();
			int status = gifDecoder.read(fis);
			if (status != GifDecoder.STATUS_OK)
				System.err.println("Error loading gif (Error: " + status + ")");
			else
				frameDelay = gifDecoder.getDelay(0);
		}
		catch (IOException e)
			{ System.err.println("Error reading gif file: " + e.getMessage()); }
	}

	private void loadGif(String gifAbsolutePath)
		{ loadGif(new File(gifAbsolutePath)); }

	public BufferedImage getCurrentFrameAsBufferedImage() {
		BufferedImage frame = gifDecoder.getFrame(frameIndex);
    if (System.nanoTime() - lastFrameTime >= frameDelay * 1_000_000) {
  		frameIndex = (frameIndex + 1) % gifDecoder.getFrameCount();
  		frameDelay = gifDecoder.getDelay(frameIndex);
      lastFrameTime = System.nanoTime();
    }
		return frame;
	}
	
	public WritableImage getCurrentFrameAsWritableImage()
		{ return javafx.embed.swing.SwingFXUtils.toFXImage(getCurrentFrameAsBufferedImage(), null); }
	
	public int getCurrentFrameIndex()
		{ return frameIndex; }
	
	public GifDecoder getGifDecoder()
		{ return gifDecoder; }
	
}
