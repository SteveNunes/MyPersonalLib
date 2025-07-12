package gameutil;

import java.io.File;
import java.util.function.Consumer;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import util.DurationTimerFX;

public class VideoPlayer {

	private MediaView mediaView;
  private MediaPlayer mediaPlayer;
	private Media media;
	private String videoPath;
	private Canvas outputCanvas;
	private Rectangle2D outputBounds;
	private SnapshotParameters params;
	private GraphicsContext gc;
	private WritableImage frameImage;
	private AnimationTimer animationTimer;
	private Consumer<MediaPlayer> consumerOnReady;
	private Consumer<MediaPlayer> consumerOnPlaying;
	private Consumer<WritableImage> consumerOnDrawFrame;
	private Consumer<MediaPlayer> consumerOnEndOfMedia;
	private Consumer<MediaException> consumerOnPlayError;
	private int fps;
	private boolean closed;
	
	public VideoPlayer(String videoPath) {
		this.videoPath = videoPath;
		closed = false;
		fps = 24;
		if (!new File(videoPath).exists())
			throw new RuntimeException("Unable to play video \"" + videoPath + "\"\nFile not exists");
		setMediaPlayer();
	}
	
	private void closeCurrentMediaPlayer() {
		stopAnimationTimer();
		if (mediaPlayer != null)
			try {
				mediaPlayer.stop();
				mediaPlayer.setOnError(null);
				mediaPlayer.setOnReady(null);
				mediaPlayer.setOnPlaying(null);
				mediaPlayer.setOnEndOfMedia(null);
				mediaPlayer.dispose();
				mediaPlayer = null;
				if (mediaView != null)
					mediaView.setMediaPlayer(null);
				media = null;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private void setMediaPlayer() {
		closeCurrentMediaPlayer();
		media = new Media(new File(videoPath).toURI().toString());
		if (media.getError() != null)
			throw new RuntimeException("Unable to play video \"" + videoPath + "\"\n" + media.getError().getMessage());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setOnError(() -> {
			if (closed)
				return;
			if (mediaPlayer.getError().getMessage().contains("ERROR_MEDIA_INVALID")) {
				if (consumerOnPlayError != null)
					consumerOnPlayError.accept(mediaPlayer.getError());
				else
					DurationTimerFX.createTimer("tryPlayVideo@" + hashCode(), Duration.millis(100), () -> {
						if (!closed) {
							setMediaPlayer();
							play();
						}
					});
			}
			else
				throw new RuntimeException("Unable to play video\n\"" + videoPath + "\"\n" + mediaPlayer.getError().getMessage());
		});
		mediaPlayer.setOnReady(() -> {
			if (consumerOnReady != null)
				consumerOnReady.accept(mediaPlayer);
      mediaPlayer.setOnEndOfMedia(() -> {
      	if (consumerOnEndOfMedia != null)
      		consumerOnEndOfMedia.accept(mediaPlayer);
      });
      mediaPlayer.setOnPlaying(() -> {
      	resetMediaView();
    		startAnimationTimer();
      	if (consumerOnPlaying != null)
      		consumerOnPlaying.accept(mediaPlayer);
      });
		});
	}
	
	private void resetMediaView() {
		stopAnimationTimer();
		mediaView = new MediaView(mediaPlayer);
		mediaView.setPreserveRatio(false);
		mediaView.setFitWidth(media.getWidth());
		mediaView.setFitHeight(media.getHeight());
  	gc = outputCanvas == null ? null : outputCanvas.getGraphicsContext2D();
  	int w = (int)mediaView.getFitWidth(), h = (int)mediaView.getFitHeight();
		frameImage = new WritableImage(w, h);
		if (outputBounds == null)
			outputBounds = new Rectangle2D(0, 0, w, h);
		params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, w, h));
	}
	
	private void stopAnimationTimer() {
		if (animationTimer != null) {
			animationTimer.stop();
			animationTimer = null;
		}
	}
	
	private void startAnimationTimer() {
		stopAnimationTimer();
  	animationTimer = GameUtils.createAnimationTimer(fps, (load, fps) -> closed, () -> {
			if (mediaView != null) {
				mediaView.snapshot(params, frameImage);
				if (consumerOnDrawFrame != null)
					consumerOnDrawFrame.accept(frameImage);
				if (gc != null)
					gc.drawImage(frameImage, outputBounds.getMinX(), outputBounds.getMinY(), outputBounds.getWidth(), outputBounds.getHeight());
			}
  	});
  	animationTimer.start();
	}

	public MediaPlayer getMediaPlayer() {
		validade();
		return mediaPlayer;
	}

	public MediaView getMediaView() {
		validade();
		return mediaView;
	}

	public Media getMedia() {
		validade();
		return media;
	}

	public String getVideoPath() {
		validade();
		return videoPath;
	}	

	public Canvas getOutputCanvas() {
		validade();
		return outputCanvas;
	}

	public void setOutputCanvas(Canvas outputCanvas) {
		validade();
		this.outputCanvas = outputCanvas;
	}

	public Rectangle2D getOutputBounds() {
		validade();
		return outputBounds;
	}

	public void setOutputBounds(Rectangle2D outputBounds) {
		validade();
		this.outputBounds = new Rectangle2D(outputBounds.getMinX(), outputBounds.getMinY(), outputBounds.getWidth(), outputBounds.getHeight());
	}
	
	public void setOutputBounds(int x, int y, int width, int height) {
		validade();
		outputBounds = new Rectangle2D(x, y, width, height);
	}

	public void setOutputLocation(int x, int y) {
		validade();
		outputBounds = new Rectangle2D(x, y, outputBounds.getWidth(), outputBounds.getHeight());
	}

	public int getFps() {
		validade();
		return fps;
	}
	
	public void setFps(int fps) {
		validade();
		this.fps = fps;
		setMediaPlayer();
	}
	
	public void play() {
		validade();
		mediaPlayer.play();
	}
	
	public void stop() {
		validade();
		stopAnimationTimer();
		mediaPlayer.seek(Duration.ZERO);
		mediaPlayer.pause();
	}

	public void close() {
		validade();
		closeCurrentMediaPlayer();
		closed = true;
	}

	public void setConsumerOnReady(Consumer<MediaPlayer> consumerOnReady) {
		validade();
		this.consumerOnReady = consumerOnReady;
	}

	public void setConsumerOnPlaying(Consumer<MediaPlayer> consumerOnPlaying) {
		validade();
		this.consumerOnPlaying = consumerOnPlaying;
	}

	public void setConsumerOnDrawFrame(Consumer<WritableImage> consumerOnDrawFrame) {
		validade();
		this.consumerOnDrawFrame = consumerOnDrawFrame;
	}

	public void setConsumerOnEndOfMedia(Consumer<MediaPlayer> consumerOnEndOfMedia) {
		validade();
		this.consumerOnEndOfMedia = consumerOnEndOfMedia;
	}

	public void setConsumerOnPlayError(Consumer<MediaException> consumerOnPlayError) {
		validade();
		this.consumerOnPlayError = consumerOnPlayError;
	}
	
	private void validade() {
		if (mediaPlayer == null && !closed)
			setMediaPlayer();
	}

}
