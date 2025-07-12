package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public abstract class Sounds {

	public static ExecutorService executorService = Executors.newCachedThreadPool();

	private static Map<String, Mp3Player> mp3Players = new HashMap<>();
	private static Map<String, WavPlayer> wavPlayers = new HashMap<>();

	public static void playWav(String wavFilePath) {
		playWav(wavFilePath, false);
	}

	public static void playWav(String wavFilePath, Boolean saveOnCache) {
		WavPlayer player;
		if (wavPlayers.containsKey(wavFilePath))
			player = wavPlayers.get(wavFilePath);
		else {
			player = new WavPlayer(wavFilePath);
			if (saveOnCache)
				wavPlayers.put(wavFilePath, player);
		}
		executorService.execute(player);
	}

	public static void stopWav(WavPlayer player) {
		player.stop();
	}

	public static void stopWav(String wavFilePath) {
		if (wavPlayers.containsKey(wavFilePath))
			wavPlayers.get(wavFilePath).stop();
	}

	public static void stopAllWaves() {
		for (WavPlayer player : wavPlayers.values())
			stopWav(player);
	}

	public static List<WavPlayer> getWavPlayers() {
		return new ArrayList<>(wavPlayers.values());
	}

	public static void playMp3(String mp3FilePath) {
		playMp3(mp3FilePath, 0, false);
	}

	public static void playMp3(String mp3FilePath, Boolean saveOnCache) {
		playMp3(mp3FilePath, 0, saveOnCache);
	}

	public static void playMp3(String mp3FilePath, int startingFrame) {
		playMp3(mp3FilePath, startingFrame, false);
	}

	public static void playMp3(String mp3FilePath, int startingFrame, Boolean saveOnCache) {
		Mp3Player player = null;
		if (mp3Players.containsKey(mp3FilePath))
			player = mp3Players.get(mp3FilePath);
		else {
			try {
				player = new Mp3Player(mp3FilePath);
				if (saveOnCache)
					mp3Players.put(mp3FilePath, player);
			}
			catch (JavaLayerException | IOException e) {
				e.printStackTrace();
			}
		}
		try {
			player.play(startingFrame);
		}
		catch (JavaLayerException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopMp3(Mp3Player player) {
		player.stop();
	}

	public static void pauseMp3(Mp3Player player) {
		player.pause();
	}

	public static void resumeMp3(Mp3Player player) {
		player.resume();
	}

	public static void seekMp3(Mp3Player player, int frame) {
		player.seek(frame);
	}

	public static void stopMp3(String mp3FilePath) {
		if (mp3Players.containsKey(mp3FilePath))
			stopMp3(mp3Players.get(mp3FilePath));
	}

	public static void pauseMp3(String mp3FilePath) {
		if (mp3Players.containsKey(mp3FilePath))
			pauseMp3(mp3Players.get(mp3FilePath));
	}

	public static void resumeMp3(String mp3FilePath) {
		if (mp3Players.containsKey(mp3FilePath))
			resumeMp3(mp3Players.get(mp3FilePath));
	}

	public static void seekMp3(String mp3FilePath, int frame) {
		if (mp3Players.containsKey(mp3FilePath))
			seekMp3(mp3Players.get(mp3FilePath), frame);
	}

	public static void stopAllMp3() {
		for (Mp3Player player : mp3Players.values())
			stopMp3(player);
	}

	public static List<Mp3Player> getMp3Players() {
		return new ArrayList<>(mp3Players.values());
	}

	static {
		Misc.addShutdownEvent(Sounds::close);
	}

	private static void close() {
		stopAllMp3();
		stopAllWaves();
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
					executorService.shutdownNow();
				}
			}
			catch (InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
		executorService = null;
	}

}

class WavPlayer implements Runnable {

	private String filePath;
	private SourceDataLine sourceLine;
	private volatile boolean isPlaying = true;

	public WavPlayer(String filePath) {
		this.filePath = filePath;
	}

	public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		File audioFile = new File(filePath);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		if (!AudioSystem.isLineSupported(info))
			throw new LineUnavailableException("Formato de áudio não suportado: " + format);

		sourceLine = (SourceDataLine) AudioSystem.getLine(info);
		sourceLine.open(format);
		sourceLine.start();

		byte[] buffer = new byte[4096];
		int bytesRead = 0;

		while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1 && isPlaying)
			sourceLine.write(buffer, 0, bytesRead);

		sourceLine.drain();
		sourceLine.close();
		audioStream.close();
	}

	public void stop() {
		isPlaying = false;
		if (sourceLine != null && sourceLine.isOpen()) {
			sourceLine.stop();
			sourceLine.close();
		}
	}

	@Override
	public void run() {
		try {
			play();
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

}

class Mp3Player {

	private AdvancedPlayer player;
	private FileInputStream fileInputStream;
	private int frameDiv = 27;
	private int totalFrames;
	private int currentFrame;
	private int pauseFrame;
	private long startCTime;
	private boolean isPaused;
	private String filePath;
	private Consumer<Mp3Player> onMusicEnded;

	public Mp3Player(String filePath) throws JavaLayerException, IOException {
		fileInputStream = new FileInputStream(this.filePath = filePath);
		Bitstream bitstream = new Bitstream(fileInputStream);
		totalFrames = bitstream.readFrame().max_number_of_frames(fileInputStream.available());
		fileInputStream.close();
		isPaused = false;
		onMusicEnded = null;
	}

	public void seek(int frame) {
		if (frame < 0 || frame > totalFrames)
			throw new RuntimeException("Seek value must be between 0 and " + totalFrames);
		try {
			stop();
			play(currentFrame = frame);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resume() {
		isPaused = false;
		try {
			play(pauseFrame > 0 ? pauseFrame : currentFrame);
		}
		catch (JavaLayerException | IOException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		stop();
		setCurrentFrameValue();
		pauseFrame = getCurrentFrame();
		isPaused = true;
	}

	public void setOnMusicEndedEvent(Consumer<Mp3Player> consumer) {
		onMusicEnded = consumer;
	}

	public void play(int startFrame) throws JavaLayerException, IOException {
		if (isPaused)
			resume();
		isPaused = false;
		fileInputStream = new FileInputStream(filePath);
		player = new AdvancedPlayer(fileInputStream);
		final Mp3Player mp3 = this;
		player.setPlayBackListener(new PlaybackListener() {
			@Override
			public void playbackFinished(PlaybackEvent e) {
				setCurrentFrameValue();
				if (onMusicEnded != null)
					onMusicEnded.accept(mp3);
			}
		});

		Sounds.executorService.execute(() -> {
			try {
				pauseFrame = 0;
				startCTime = System.currentTimeMillis() - startFrame * frameDiv;
				if (startFrame > 0)
					fileInputStream.skip(startFrame * frameDiv);
				player.play(startFrame, totalFrames);
			}
			catch (JavaLayerException | IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void stop() {
		if (player != null) {
			setCurrentFrameValue();
			player.close();
			isPaused = false;
			pauseFrame = 0;
		}
	}

	private void setCurrentFrameValue() {
		currentFrame = (int) (System.currentTimeMillis() - startCTime) / frameDiv;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

}