package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.Pair;

public abstract class SoundsFX {

	private static long uniqueTimerId = 0;
	private static MediaPlayer currentMediaPlayer = null;
	private static Map<String, AudioClip> waves = new LinkedHashMap<>();
	private static Map<String, MediaPlayer> mp3s = new LinkedHashMap<>();
	private static Map<MediaPlayer, String> mp3Timers = new LinkedHashMap<>();
	private static double masterGain = 1;
	private static Map<String, Integer> mp3tries = new HashMap<>();
	private static Consumer<String> onError = null;
	
	public static double getMasterGain() {
		return masterGain;
	}
	
	public static void setMasterGain(double gain) {
		masterGain = gain;
	}

	// ============================ PLAY Mp3 =====================================
	
	public static MediaPlayer getCurrentMediaPlayer() {
		return currentMediaPlayer;
	}

	public static MediaPlayer getMediaPlayer(String mp3Path) {
		return mp3s.containsKey(mp3Path) ? mp3s.get(mp3Path) : null;
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path) {
		return playMp3(mp3Path, 1, 0, masterGain, false, null);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, boolean stopCurrent) {
		return playMp3(mp3Path, 1, 0, masterGain, stopCurrent, null);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume) {
		return playMp3(mp3Path, rate, balance, volume, false, null);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume, boolean stopCurrent) {
		return playMp3(mp3Path, rate, balance, volume, stopCurrent, null);
	}
	
	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, Pair<Duration, Duration> doLoop) {
		return playMp3(mp3Path, 1, 0, masterGain, false, doLoop);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, boolean stopCurrent, Pair<Duration, Duration> doLoop) {
		return playMp3(mp3Path, 1, 0, masterGain, stopCurrent, doLoop);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume, Pair<Duration, Duration> doLoop) {
		return playMp3(mp3Path, rate, balance, volume, false, doLoop);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume, boolean stopCurrent, Pair<Duration, Duration> doLoop) {
		if (stopCurrent) {
			if (currentMediaPlayer != null && mp3s.get(mp3Path) != currentMediaPlayer)
				forceStopMp3(currentMediaPlayer);
			if (mp3s.containsKey(mp3Path)) {
				forceStopMp3(mp3s.get(mp3Path));
				mp3s.remove(mp3Path);
			}
		}

		File file = new File(mp3Path);
		if (!file.exists())
			throw new RuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Arquivo não encontrado no local informado.");

		return CompletableFuture.supplyAsync(() -> {
			try {
				MediaPlayer mp3;
				if (mp3s.containsKey(mp3Path)) {
					mp3 = mp3s.get(mp3Path);
					mp3Timers.remove(mp3s.get(mp3Path));
					mp3s.remove(mp3Path);
					try {
						mp3.seek(Duration.ZERO);
					}
					catch (Exception ignored) {}
				}
				else
					mp3 = new MediaPlayer(new Media(file.toURI().toString()));
				if (stopCurrent)
					mp3s.put(mp3Path, mp3);
				currentMediaPlayer = mp3;
				mp3.setRate(rate);
				mp3.setBalance(balance);
				mp3.setVolume(volume);
				if (!mp3tries.containsKey(mp3Path))
					mp3tries.put(mp3Path, 0);

				if (doLoop != null) {
					mp3.setOnPlaying(() -> {
						mp3tries.remove(mp3Path);
						String timerName = "playMp3DoLoop@" + uniqueTimerId++;
						long seekEnd = (long) doLoop.getKey().toMillis();
						long seekStart = (long) doLoop.getValue().toMillis();
						mp3Timers.put(mp3, timerName);
						if (seekEnd < 0)
							seekEnd += (long) mp3.getTotalDuration().toMillis();
						if (seekStart < 0)
							seekStart += (long) mp3.getTotalDuration().toMillis();
						final long seekEnd2 = seekEnd, seekStart2 = seekStart;
						DurationTimerFX.createTimer(timerName, Duration.millis(20), 0, () -> {
							if (mp3.getCurrentTime().toMillis() >= seekEnd2)
								mp3.seek(Duration.millis(seekStart2));
						});
					});
				}

				mp3.setOnReady(() -> mp3.play());

				mp3.setOnError(() -> {
					mp3tries.put(mp3Path, mp3tries.get(mp3Path) + 1);
					if (mp3tries.get(mp3Path) <= 5)
						playMp3(mp3Path, rate, balance, volume, stopCurrent, doLoop);
					else {
						mp3tries.remove(mp3Path);
						if (onError != null)
							onError.accept(mp3Path);
						else
							throw new RuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Houve alguma dificuldade interna ao reproduzir. Verifique se o caminho do arquivo contém caracteres acentuados ou caracteres especiais.\n" + mp3.getError().getMessage());
					}
				});

				return mp3;
			}
			catch (Exception e) {
				e.printStackTrace();
				mp3tries.remove(mp3Path);
				currentMediaPlayer = null;
				return null;
			}
		});
	}
	
	private static void forceStopMp3(MediaPlayer mp3) {
		if (mp3 != null) {
			MediaPlayer.Status status = mp3.getStatus();
			if (status != null && (status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED))
				mp3.stop();
			mp3.dispose();
		}
	}
	
	public static void stopMp3(String mp3Path) {
		if (mp3Timers.containsKey(mp3s.get(mp3Path))) {
			if (mp3s.containsKey(mp3Path)) {
				DurationTimerFX.stopTimer(mp3Timers.get(mp3s.get(mp3Path)));
				mp3Timers.remove(mp3s.get(mp3Path));
				forceStopMp3(mp3s.get(mp3Path));
			}
			mp3s.remove(mp3Path);
		}
	}

	public static void stopAllMp3s() {
		new ArrayList<>(mp3s.keySet()).forEach(mp3Path -> forceStopMp3(mp3s.get(mp3Path)));
	}

	// ============================ PLAY Wav =====================================

	public static AudioClip getAudioClip(String wavPath) {
		return waves.containsKey(wavPath) ? waves.get(wavPath) : null;
	}

	public static CompletableFuture<AudioClip> playWav(String wavPath) {
		return playWav(wavPath, 1, 0, 0, masterGain, false);
	}

	public static CompletableFuture<AudioClip> playWav(String wavPath, boolean stopCurrent) {
		return playWav(wavPath, 1, 0, 0, masterGain, stopCurrent);
	}

	public static CompletableFuture<AudioClip> playWav(final String wavPath, double rate, double pan, double balance, double volume) {
		return playWav(wavPath, rate, pan, balance, volume, false);
	}

	public static CompletableFuture<AudioClip> playWav(String wavPath, double rate, double pan, double balance, double volume, boolean stopCurrent) {
		if (stopCurrent && waves.containsKey(wavPath))
			waves.get(wavPath).stop();
		return CompletableFuture.supplyAsync(() -> {
			File file = new File(wavPath);
			if (!file.exists())
				throw new RuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Arquivo não encontrado no local informado.");
			try {
				final AudioClip clip;
				if (waves.containsKey(wavPath))
					clip = waves.get(wavPath);
				else
					clip = new AudioClip(file.toURI().toString());
				if (stopCurrent)
					waves.put(wavPath, clip);
				clip.setRate(rate);
				clip.setPan(pan);
				clip.setBalance(balance);
				clip.setVolume(volume);
				clip.play();
				return clip;
			}
			catch (Exception e) {
				if (onError != null)
					onError.accept(wavPath);
				else
					e.printStackTrace();
				return null;
			}
		});
	}

	public static void stopAllWaves() {
		waves.values().forEach(clip -> clip.stop());
	}
	
	public static void setOnErrorEvent(Consumer<String> consumer) {
		onError = consumer;
	}

}
