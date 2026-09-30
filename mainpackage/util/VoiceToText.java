package util;

import java.io.File;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.vosk.Model;
import org.vosk.Recognizer;

import com.google.gson.JsonParser;

public class VoiceToText {

	private static boolean closed = true;

	public static void close() {
		if (!closed) {
			closed = true;
		}
	}
	
	public static void init(String modelPath, Consumer<String> onVoiceListen) {
		if (closed) {
			closed = false;
			System.setProperty("jna.encoding", "UTF-8");
			if (!new File(modelPath).exists())
				throw new RuntimeException(modelPath + " - Pasta inválida para modelo de reconhecimento de voz do Vosk.");
			AudioFormat format = new AudioFormat(16000, // sample rate
			    16, // bits
			    1, // mono
			    true, // signed
			    false // little endian
			);
	
			Misc.executorService.execute(() -> {
				try {
					DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
					TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
					microphone.open(format);
					microphone.start();
					Model model = new Model(modelPath);
					Recognizer recognizer = new Recognizer(model, 16000);
					byte[] buffer = new byte[4096];
					while (!closed) {
						int bytesRead = microphone.read(buffer, 0, buffer.length);
						if (recognizer.acceptWaveForm(buffer, bytesRead)) {
							String resultadoJson = recognizer.getResult();
							String texto = extrairTextoVosk(resultadoJson);
							onVoiceListen.accept(texto);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
	
			});
		}
	}

	private static String extrairTextoVosk(String json) {
		return JsonParser.parseString(json).getAsJsonObject().get("text").getAsString();
	}

}