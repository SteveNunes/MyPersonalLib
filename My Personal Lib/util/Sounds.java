package util;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sounds {

	private static Clip clip;

	public static void playWav(String wavFilePath) {
		if (clip != null)
			clip.stop();
		try {
			clip = AudioSystem.getClip();
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavFilePath).getAbsoluteFile());
			clip.open(audioInputStream);
			clip.start();
		}
		catch (Exception ex)
			{ throw new RuntimeException("Unable to play the file \"" + wavFilePath + "\""); }
	}
	
	public static void stopWav() {
		if (clip != null) {
			clip.stop();
			clip = null;
		}
	}
	
}
