package util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.player.advanced.AdvancedPlayer;

public abstract class Sounds {
	
	private static Map<String, List<AdvancedPlayer>> players = new HashMap<>();
	private static Map<String, List<Clip>> clipes = new HashMap<>();

	public static void playWav(String wavFilePath)
		{ playWav(wavFilePath, false); }
	
	public static void playWav(String wavFilePath, Boolean saveOnCache) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavFilePath).getAbsoluteFile())) {
					try (Clip clip = AudioSystem.getClip()) {
	    			if (saveOnCache)
		    			synchronized (clipes) {
								if (clipes.get(wavFilePath) == null)
									clipes.put(wavFilePath, new ArrayList<>());
								clipes.get(wavFilePath).add(clip);
		    			}
						clip.open(audioInputStream);
						clip.start();
						while (!clip.isRunning())
							Misc.sleep(50);
						long end = System.currentTimeMillis() + clip.getMicrosecondLength() / 1000;
						while (System.currentTimeMillis() < end) {
							if (!clip.isActive())
								return;
							Misc.sleep(50);
						}
						clip.close();
						clip.flush();
    				if (saveOnCache)
		    			synchronized (clipes) {
								clipes.get(wavFilePath).remove(clip);
								if (clipes.get(wavFilePath).isEmpty())
				    			clipes.remove(wavFilePath);
							}
					}
				}
				catch (Exception e)
					{ throw new RuntimeException("playWav(): Unable to play the file \"" + wavFilePath + "\"\n" + e.getMessage()); }
			}
		}).start();
	}

	public static void stopWav(Clip clip) {
		if (clip == null)
			throw new RuntimeException("stopWav(): 'clip' is null");
		new Thread(new Runnable() {
			@Override
			public void run() {
				clip.close();
				clip.flush();
				synchronized (clipes) {
					for (String key : clipes.keySet()) {
						clipes.get(key).remove(clip);
						if (clipes.get(key).isEmpty())
							clipes.remove(key);
					}
				}
			}
		}).start();
	}
	
	public static void stopWav(String wavFilePath) {
		if (clipes.get(wavFilePath) == null || clipes.get(wavFilePath).isEmpty())
			throw new RuntimeException("stopWav(): Resource 'wavFilePath' is not active playing");
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (clipes) {
					for (Clip clip : clipes.get(wavFilePath)) {
						clip.close();
						clip.flush();
					}
					clipes.remove(wavFilePath);
				}
			}
		}).start();
	}

	public static void stopAllWaves() {
		if (clipes.isEmpty())
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (clipes) {
					for (String key : clipes.keySet())
						for (Clip clip : clipes.get(key))
							clip.close();
					clipes.clear();
				}
			}
		}).start();
	}
	
	public static List<Clip> getClipes(String wavFilePath) {
		if (clipes.get(wavFilePath) == null)
			return null;
		return clipes.get(wavFilePath);
	}

	public static List<Clip> getAllClipes() {
		List<Clip> list = new ArrayList<>();
		for (String key : clipes.keySet())
			for (Clip clip : getClipes(key))
				list.add(clip);
		return list.isEmpty() ? null : list;
	}
	
	public static void playMp3(String mp3FilePath)
		{ playMp3(mp3FilePath, false); }

	public static void playMp3(String mp3FilePath, Boolean saveOnCache) {
		new Thread(new Runnable() {      @Override	    public void run() {
    		try (FileInputStream fileInputStream = new FileInputStream(mp3FilePath)) {
    			AdvancedPlayer player = new AdvancedPlayer(fileInputStream);
    			/** FORMA DE COMO CRIAR LISTENER PRA DISPARAR QUANDO ARQUIVO COMECAR OU TERMINAR DE TOCAR
					player.setPlayBackListener(new PlaybackListener() {
						@Override
						public void playbackStarted(PlaybackEvent evt) {
							System.out.println("Playback started");
						}

						@Override
						public void playbackFinished(PlaybackEvent evt) {
							System.out.println("Playback finished");
						}
					});
					*/
    			if (saveOnCache)
	    			synchronized (players) {
							if (players.get(mp3FilePath) == null)
								players.put(mp3FilePath, new ArrayList<>());
	    				players.get(mp3FilePath).add(player);
	    			}
    			player.play();
    			if (saveOnCache)
	  				synchronized (players) {
	  					if (players.get(mp3FilePath) != null)
	  						players.get(mp3FilePath).remove(player);
	    			}
      	}
    		catch (Exception e)
    			{ throw new RuntimeException("playMp3(): Unable to play the file \"" + mp3FilePath + "\"\n" + e.getMessage()); }
	    }		}).start();
	}
	
	public static void stopMp3(AdvancedPlayer player) {
		if (player == null)
			throw new RuntimeException("stopMp3(): 'player' is null");
		new Thread(new Runnable() {
			@Override
			public void run() {
				player.close();
				synchronized (players) {
					for (String key : players.keySet()) {
						players.get(key).remove(player);
						if (players.get(key).isEmpty())
							players.remove(key);
					}
				}
			}
		}).start();
	}
	
	public static void stopMp3(String mp3FilePath) {
		if (players.get(mp3FilePath) == null || players.get(mp3FilePath).isEmpty())
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (clipes) {
					for (AdvancedPlayer player : players.get(mp3FilePath))
						player.close();
					clipes.remove(mp3FilePath);
				}
			}
		}).start();
	}

	public static void stopAllMp3() {
		if (players.isEmpty())
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (players) {
					for (String key : players.keySet())
						for (AdvancedPlayer player : players.get(key))
							player.close();
					players.clear();
				}
			}
		}).start();
	}

	public static List<AdvancedPlayer> getPlayers(String mp3FilePath) {
		if (players.get(mp3FilePath) == null)
			return null;
		return players.get(mp3FilePath);
	}

	public static List<AdvancedPlayer> getAllPlayers() {
		List<AdvancedPlayer> list = new ArrayList<>();
		for (String key : players.keySet())
			for (AdvancedPlayer player : getPlayers(key))
				list.add(player);
		return list.isEmpty() ? null : list;
	}
	
	public static void javaFxAudioPlayer(String fileName) {
		try {
			Media media = new Media(new File(fileName).toURI().toString());
			new MediaPlayer(media).play();
		}
		catch (Exception e)
			{ e.printStackTrace(); }
	}
	
}
