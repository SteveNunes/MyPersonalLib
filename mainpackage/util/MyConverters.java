package util;

import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

class KeyMap {

	public String keyName;
	public int keyCode;
	public int keyRaw;

	public KeyMap(String keyName, int keyCode, int keyRaw) {
		this.keyName = keyName;
		this.keyCode = keyCode;
		this.keyRaw = keyRaw;
	}

}

@SuppressWarnings("serial")
public abstract class MyConverters {

	private static List<KeyMap> keyMaps;
	private static Map<String, Integer> stringToKeyCode;
	private static Map<String, Integer> stringToKeyRaw;
	private static Map<Integer, Integer> keyCodeToKeyRaw;
	private static Map<Integer, Integer> keyRawToKeyCode;
	private static Map<Integer, String> keyRawToKeyName;
	private static Map<Integer, String> keyCodeToKeyName;
	private static Set<String> keyNames;

	static {
		keyMaps = new ArrayList<>();
		stringToKeyCode = new HashMap<>();
		stringToKeyRaw = new HashMap<>();
		keyCodeToKeyRaw = new HashMap<>();
		keyRawToKeyCode = new HashMap<>();
		keyRawToKeyName = new HashMap<>();
		keyCodeToKeyName = new HashMap<>();
		keyNames = new HashSet<>();
		keyMaps.add(new KeyMap("A", NativeKeyEvent.VC_A, KeyEvent.VK_A));
		keyMaps.add(new KeyMap("B", NativeKeyEvent.VC_B, KeyEvent.VK_B));
		keyMaps.add(new KeyMap("C", NativeKeyEvent.VC_C, KeyEvent.VK_C));
		keyMaps.add(new KeyMap("D", NativeKeyEvent.VC_D, KeyEvent.VK_D));
		keyMaps.add(new KeyMap("E", NativeKeyEvent.VC_E, KeyEvent.VK_E));
		keyMaps.add(new KeyMap("F", NativeKeyEvent.VC_F, KeyEvent.VK_F));
		keyMaps.add(new KeyMap("G", NativeKeyEvent.VC_G, KeyEvent.VK_G));
		keyMaps.add(new KeyMap("H", NativeKeyEvent.VC_H, KeyEvent.VK_H));
		keyMaps.add(new KeyMap("I", NativeKeyEvent.VC_I, KeyEvent.VK_I));
		keyMaps.add(new KeyMap("J", NativeKeyEvent.VC_J, KeyEvent.VK_J));
		keyMaps.add(new KeyMap("K", NativeKeyEvent.VC_K, KeyEvent.VK_K));
		keyMaps.add(new KeyMap("L", NativeKeyEvent.VC_L, KeyEvent.VK_L));
		keyMaps.add(new KeyMap("M", NativeKeyEvent.VC_M, KeyEvent.VK_M));
		keyMaps.add(new KeyMap("N", NativeKeyEvent.VC_N, KeyEvent.VK_N));
		keyMaps.add(new KeyMap("O", NativeKeyEvent.VC_O, KeyEvent.VK_O));
		keyMaps.add(new KeyMap("P", NativeKeyEvent.VC_P, KeyEvent.VK_P));
		keyMaps.add(new KeyMap("Q", NativeKeyEvent.VC_Q, KeyEvent.VK_Q));
		keyMaps.add(new KeyMap("R", NativeKeyEvent.VC_R, KeyEvent.VK_R));
		keyMaps.add(new KeyMap("S", NativeKeyEvent.VC_S, KeyEvent.VK_S));
		keyMaps.add(new KeyMap("T", NativeKeyEvent.VC_T, KeyEvent.VK_T));
		keyMaps.add(new KeyMap("U", NativeKeyEvent.VC_U, KeyEvent.VK_U));
		keyMaps.add(new KeyMap("V", NativeKeyEvent.VC_V, KeyEvent.VK_V));
		keyMaps.add(new KeyMap("W", NativeKeyEvent.VC_W, KeyEvent.VK_W));
		keyMaps.add(new KeyMap("X", NativeKeyEvent.VC_X, KeyEvent.VK_X));
		keyMaps.add(new KeyMap("Y", NativeKeyEvent.VC_Y, KeyEvent.VK_Y));
		keyMaps.add(new KeyMap("Z", NativeKeyEvent.VC_Z, KeyEvent.VK_Z));
		keyMaps.add(new KeyMap("0", NativeKeyEvent.VC_0, KeyEvent.VK_0));
		keyMaps.add(new KeyMap("1", NativeKeyEvent.VC_1, KeyEvent.VK_1));
		keyMaps.add(new KeyMap("2", NativeKeyEvent.VC_2, KeyEvent.VK_2));
		keyMaps.add(new KeyMap("3", NativeKeyEvent.VC_3, KeyEvent.VK_3));
		keyMaps.add(new KeyMap("4", NativeKeyEvent.VC_4, KeyEvent.VK_4));
		keyMaps.add(new KeyMap("5", NativeKeyEvent.VC_5, KeyEvent.VK_5));
		keyMaps.add(new KeyMap("6", NativeKeyEvent.VC_6, KeyEvent.VK_6));
		keyMaps.add(new KeyMap("7", NativeKeyEvent.VC_7, KeyEvent.VK_7));
		keyMaps.add(new KeyMap("8", NativeKeyEvent.VC_8, KeyEvent.VK_8));
		keyMaps.add(new KeyMap("9", NativeKeyEvent.VC_9, KeyEvent.VK_9));
		keyMaps.add(new KeyMap("NUMPAD0", 96, KeyEvent.VK_NUMPAD0));
		keyMaps.add(new KeyMap("NUMPAD1", 97, KeyEvent.VK_NUMPAD1));
		keyMaps.add(new KeyMap("NUMPAD2", 98, KeyEvent.VK_NUMPAD2));
		keyMaps.add(new KeyMap("NUMPAD3", 99, KeyEvent.VK_NUMPAD3));
		keyMaps.add(new KeyMap("NUMPAD4", 100, KeyEvent.VK_NUMPAD4));
		keyMaps.add(new KeyMap("NUMPAD5", 101, KeyEvent.VK_NUMPAD5));
		keyMaps.add(new KeyMap("NUMPAD6", 102, KeyEvent.VK_NUMPAD6));
		keyMaps.add(new KeyMap("NUMPAD7", 103, KeyEvent.VK_NUMPAD7));
		keyMaps.add(new KeyMap("NUMPAD8", 104, KeyEvent.VK_NUMPAD8));
		keyMaps.add(new KeyMap("NUMPAD9", 105, KeyEvent.VK_NUMPAD9));
		keyMaps.add(new KeyMap("LSHIFT", NativeKeyEvent.VC_SHIFT, 160));
		keyMaps.add(new KeyMap("LCONTROL", NativeKeyEvent.VC_CONTROL, 162));
		keyMaps.add(new KeyMap("LALT", NativeKeyEvent.VC_ALT, 164));
		keyMaps.add(new KeyMap("SHIFT", NativeKeyEvent.VC_SHIFT, KeyEvent.VK_SHIFT));
		keyMaps.add(new KeyMap("CONTROL", NativeKeyEvent.VC_CONTROL, KeyEvent.VK_CONTROL));
		keyMaps.add(new KeyMap("ALT", NativeKeyEvent.VC_ALT, KeyEvent.VK_ALT));
		keyMaps.add(new KeyMap("RSHIFT", NativeKeyEvent.VC_SHIFT, 161));
		keyMaps.add(new KeyMap("RCONTROL", NativeKeyEvent.VC_CONTROL, 163));
		keyMaps.add(new KeyMap("RALT", NativeKeyEvent.VC_ALT, 165));
		keyMaps.add(new KeyMap("BACK_SLASH", NativeKeyEvent.VC_BACK_SLASH, KeyEvent.VK_CLOSE_BRACKET));
		keyMaps.add(new KeyMap("BACKQUOTE", NativeKeyEvent.VC_BACKQUOTE, KeyEvent.VK_QUOTE));
		keyMaps.add(new KeyMap("BACKSPACE", NativeKeyEvent.VC_BACKSPACE, KeyEvent.VK_BACK_SPACE));
		keyMaps.add(new KeyMap("CAPS_LOCK", NativeKeyEvent.VC_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK));
		keyMaps.add(new KeyMap("CLOSE_BRACKET", NativeKeyEvent.VC_CLOSE_BRACKET, KeyEvent.VK_OPEN_BRACKET));
		keyMaps.add(new KeyMap("COMMA", NativeKeyEvent.VC_COMMA, KeyEvent.VK_COMMA));
		keyMaps.add(new KeyMap("DELETE", NativeKeyEvent.VC_DELETE, KeyEvent.VK_DELETE));
		keyMaps.add(new KeyMap("END", NativeKeyEvent.VC_END, KeyEvent.VK_END));
		keyMaps.add(new KeyMap("ENTER", NativeKeyEvent.VC_ENTER, KeyEvent.VK_ENTER));
		keyMaps.add(new KeyMap("EQUALS", NativeKeyEvent.VC_EQUALS, KeyEvent.VK_EQUALS));
		keyMaps.add(new KeyMap("ESCAPE", NativeKeyEvent.VC_ESCAPE, KeyEvent.VK_ESCAPE));
		keyMaps.add(new KeyMap("HOME", NativeKeyEvent.VC_HOME, KeyEvent.VK_HOME));
		keyMaps.add(new KeyMap("INSERT", NativeKeyEvent.VC_INSERT, KeyEvent.VK_INSERT));
		keyMaps.add(new KeyMap("MINUS", NativeKeyEvent.VC_MINUS, KeyEvent.VK_MINUS));
		keyMaps.add(new KeyMap("NUM_LOCK", NativeKeyEvent.VC_NUM_LOCK, KeyEvent.VK_NUM_LOCK));
		keyMaps.add(new KeyMap("OPEN_BRACKET", NativeKeyEvent.VC_OPEN_BRACKET, KeyEvent.VK_DEAD_ACUTE));
		keyMaps.add(new KeyMap("PAGE_DOWN", NativeKeyEvent.VC_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN));
		keyMaps.add(new KeyMap("PAGE_UP", NativeKeyEvent.VC_PAGE_UP, KeyEvent.VK_PAGE_UP));
		keyMaps.add(new KeyMap("PAUSE", NativeKeyEvent.VC_PAUSE, KeyEvent.VK_PAUSE));
		keyMaps.add(new KeyMap("PERIOD", NativeKeyEvent.VC_PERIOD, KeyEvent.VK_PERIOD));
		keyMaps.add(new KeyMap("SLASH", NativeKeyEvent.VC_SLASH, KeyEvent.VK_SEMICOLON));
		keyMaps.add(new KeyMap("TAB", NativeKeyEvent.VC_TAB, KeyEvent.VK_TAB));

		for (KeyMap keyMap : keyMaps) {
			keyNames.add(keyMap.keyName);
			stringToKeyCode.put(keyMap.keyName, keyMap.keyCode);
			stringToKeyRaw.put(keyMap.keyName, keyMap.keyRaw);
			keyCodeToKeyRaw.put(keyMap.keyCode, keyMap.keyRaw);
			keyRawToKeyCode.put(keyMap.keyRaw, keyMap.keyCode);
			keyRawToKeyName.put(keyMap.keyRaw, keyMap.keyName);
			keyCodeToKeyName.put(keyMap.keyCode, keyMap.keyName);
		}
	}
	
	public static Set<String> getKeyNames() {
		return keyNames;
	}

	public static List<KeyMap> getKeyMaps() {
		return keyMaps;
	}

	public static String convertKeyRawToKeyName(int keyRaw) {
		return keyRawToKeyName.getOrDefault(keyRaw, "KeyRaw=" + keyRaw);
	}

	public static String convertKeyCodeToKeyName(int keyCode) {
		return keyCodeToKeyName.getOrDefault(keyCode, "KeyCode=" + keyCode);
	}

	public static int convertKeyNameToKeyRaw(String keyName) {
		return stringToKeyRaw.getOrDefault(keyName, -1);
	}

	public static int convertKeyNameToKeyCode(String keyName) {
		return stringToKeyCode.getOrDefault(keyName, -1);
	}

	public static int convertKeyRawToKeyCode(int keyRaw) {
		return keyRawToKeyCode.getOrDefault(keyRaw, -1);
	}

	public static int convertKeyCodeToKeyRaw(int keyCode) {
		return keyCodeToKeyRaw.getOrDefault(keyCode, -1);
	}

	/**
	 * Converte String de duração no formato ISO 8601 para javafx.util.Duration
	 */
	public static Duration parseISODuration(String isoDuration) {
		Pattern pattern = Pattern.compile("PT(?:(\\d+)D)?(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
		Matcher matcher = pattern.matcher(isoDuration);
		if (matcher.matches()) {
			int days = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
			int hours = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
			int minutes = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
			int seconds = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
			return Duration.hours(24 * days).add(Duration.hours(hours)).add(Duration.minutes(minutes)).add(Duration.seconds(seconds));
		}
		throw new IllegalArgumentException("Formato inválido: " + isoDuration);
	}

	/**
	 * Converte long decimal em IP
	 */
	public static long IPToLong(String ip) {
		String[] split = ip.split("\\.");
		long longIP = 0;
		for (int n = 0; n < split.length; n++)
			longIP += Long.parseLong(split[n]) * Math.pow(256, (3 - n));
		return longIP;
	}

	/**
	 * Converte IP em long decimal
	 */
	public static String longToIP(long longIP) {
		String ip = "" + (longIP / 256 / 256 / 256) + "." + (longIP / 256 / 256 % 256) + ".";
		ip += (longIP / 256 % 256) + "." + (longIP % 256);
		return ip;
	}

	public static byte[] charArrayToBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte) 0);
		return bytes;
	}

	/**
	 * Converte uma {@code Array} em {@code String}.
	 */
	public static String arrayToString(String[] array, int startIndex, int endIndex, String separator) {
		StringBuilder result = new StringBuilder();
		for (int n = startIndex; n <= endIndex; n++)
			result.append((n > startIndex ? separator : "") + array[n]);
		return result.toString();
	}

	/**
	 * Sobrecarga do método
	 * {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede o parâmetro {@code regexSeparator} (É usado o espaço como
	 * padrão)
	 */
	public static String arrayToString(String[] array, int startIndex, int endIndex) {
		return arrayToString(array, startIndex, endIndex, " ");
	}

	/**
	 * Sobrecarga do método
	 * {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede o parâmetro {@code endIndex} (Pega a {@code Array} inteira á
	 * partir do índice informado como inciial)
	 */
	public static String arrayToString(String[] array, int startIndex, String separator) {
		return arrayToString(array, startIndex, array.length - 1, separator);
	}

	/**
	 * Sobrecarga do método
	 * {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code endIndex} e {@code regexSeparator}<br>
	 */
	public static String arrayToString(String[] array, int startIndex) {
		return arrayToString(array, startIndex, array.length - 1);
	}

	/**
	 * Sobrecarga do método
	 * {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code startIndex} e {@code endIndex}<br>
	 */
	public static String arrayToString(String[] array, String separator) {
		return arrayToString(array, 0, array.length - 1, separator);
	}

	/**
	 * Sobrecarga do método
	 * {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code startIndex}, {@code endIndex} e
	 * {@code regexSeparator}<br>
	 */
	public static String arrayToString(String[] array) {
		return arrayToString(array, 0, array.length - 1);
	}

	public static String intArrayToString(int[][] array, String valuesSeparator) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < array.length; y++)
			for (int x = 0; x < array[0].length; x++)
				sb.append((x > 0 ? " " : sb.isEmpty() ? "" : "\n") + array[y][x]);
		return sb.toString();
	}

	public static String intArrayToString(int[][] array) {
		return intArrayToString(array, " ");
	}

	public static int[][] stringToIntArray(String string, String valuesSeparator) {
		String invalid = "";
		try {
			String[] lines = string.split("\n");
			int[][] array = null;
			for (int y = 0; y < lines.length; y++) {
				String line = lines[y];
				String[] values = line.split(" ");
				if (array == null)
					array = new int[lines.length][values.length];
				for (int x = 0; x < values.length; x++)
					array[y][x] = Integer.parseInt(invalid = values[x]);
			}
			return array;
		}
		catch (PatternSyntaxException e) {
			throw new RuntimeException(valuesSeparator + " - Invalid regex for a separator");
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Invalid int value inside one of the string elements - " + invalid);
		}
	}

	public static int[][] stringToIntArray(String string) {
		return stringToIntArray(string, " ");
	}

	public static byte[] imageToByteArray(Image image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int width = (int) image.getWidth(), height = (int) image.getHeight();
		PixelReader pixelReader = image.getPixelReader();
		dos.writeInt(width);
		dos.writeInt(height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixelReader.getArgb(x, y);
				dos.writeInt(pixel);
			}
		}
		dos.flush();
		return baos.toByteArray();
	}

	public static Image byteArrayToImage(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		int width = dis.readInt(), height = dis.readInt();
		WritableImage receivedImage = new WritableImage(width, height);
		PixelWriter pixelWriter = receivedImage.getPixelWriter();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = dis.readInt();
				pixelWriter.setArgb(x, y, pixel);
			}
		}
		return receivedImage;
	}

	/**
	 * Converte um objeto Image em uma String Base64. Primeiro, a imagem é
	 * convertida em uma matriz de bytes contendo sua largura, altura e dados de
	 * pixel. Em seguida, essa matriz de bytes é codificada para uma String Base64.
	 *
	 * @param image A imagem a ser convertida.
	 * @return A string Base64 que representa a imagem.
	 * @throws IOException Se ocorrer um erro durante a conversão.
	 */
	public static String imageToString(Image image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		// Escreve a largura e a altura da imagem.
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		dos.writeInt(width);
		dos.writeInt(height);

		// Escreve os dados de pixel ARGB.
		PixelReader pixelReader = image.getPixelReader();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixelReader.getArgb(x, y);
				dos.writeInt(pixel);
			}
		}

		dos.flush();
		byte[] byteArray = baos.toByteArray();

		// Codifica a matriz de bytes em uma String Base64 e retorna.
		return Base64.getEncoder().encodeToString(byteArray);
	}

	/**
	 * Converte uma String Base64 em um objeto Image. Primeiro, a string Base64 é
	 * decodificada em uma matriz de bytes. Em seguida, a matriz de bytes é lida
	 * para reconstruir a largura, altura e os dados de pixel, criando um novo
	 * objeto WritableImage.
	 *
	 * @param dataString A string Base64 que contém os dados da imagem.
	 * @return O objeto Image reconstruído.
	 * @throws IOException Se ocorrer um erro durante a conversão.
	 */
	public static Image stringToImage(String dataString) throws IOException {
		// Decodifica a string Base64 de volta para uma matriz de bytes.
		byte[] data = Base64.getDecoder().decode(dataString);

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);

		// Lê a largura e a altura.
		int width = dis.readInt();
		int height = dis.readInt();

		WritableImage receivedImage = new WritableImage(width, height);
		PixelWriter pixelWriter = receivedImage.getPixelWriter();

		// Lê os dados de pixel e os escreve na nova imagem.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = dis.readInt();
				pixelWriter.setArgb(x, y, pixel);
			}
		}

		return receivedImage;
	}

}
