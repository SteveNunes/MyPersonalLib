package util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class Sounds {
	
	private static Map<String, AdvancedPlayer> players = new HashMap<>();
	
	public static Clip playWav(String wavFilePath) {
		Clip clip;
		try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavFilePath).getAbsoluteFile())) {
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			new Thread(new Runnable() {
				@Override
				public void run()
					{ clip.start(); }
			}).start();
		}
		catch (Exception e)
			{ throw new RuntimeException("playWav(): Unable to play the file \"" + wavFilePath + "\"\n" + e.getMessage()); }
		return clip;
	}

	public static void stopWav(Clip clip) {
		if (clip != null)
			clip.close();
	}

	public static void playMp3(String mp3FilePath) {
		new Thread(new Runnable() {      @Override	    public void run() {
    		try (FileInputStream fileInputStream = new FileInputStream(mp3FilePath)) {
    			if (players.containsKey(mp3FilePath))
    				players.get(mp3FilePath).close();
    			AdvancedPlayer player = new AdvancedPlayer(fileInputStream);
    			players.put(mp3FilePath, player);
    			player.play();
    			players.remove(mp3FilePath, player);
      	}
    		catch (Exception e)
      		{ throw new RuntimeException("playMp3(): Unable to play the file \"" + mp3FilePath + "\"\n" + e.getMessage()); }
	    }		}).start();
	}
	
	public static void stopMp3(String mp3FilePath) {
		if (players.containsKey(mp3FilePath)) {
			players.get(mp3FilePath).close();
			players.remove(mp3FilePath);
		}
	}

}
