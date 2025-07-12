package util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.util.Duration;

public abstract class MyConverters {
	
	/**
	 *  Converte String de duração no formato ISO 8601 para javafx.util.Duration
	 */
	public static Duration parseISODuration(String isoDuration) {
		Pattern pattern = Pattern.compile("PT(?:(\\d+)D)?(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
		Matcher matcher = pattern.matcher(isoDuration);
		if (matcher.matches()) {
			int days = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
			int hours = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
			int minutes = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
			int seconds = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
			return Duration.hours(24 * days)
								.add(Duration.hours(hours))
								.add(Duration.minutes(minutes))
								.add(Duration.seconds(seconds));
		}
		throw new IllegalArgumentException("Formato inválido: " + isoDuration);
	}
	
	/**
	 *  Converte long decimal em IP
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
	  byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
	            byteBuffer.position(), byteBuffer.limit());
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
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede o parâmetro {@code regexSeparator} (É usado o espaço como padrão)
	 */
	public static String arrayToString(String[] array, int startIndex, int endIndex)
		{ return arrayToString(array, startIndex, endIndex, " "); }

	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede o parâmetro {@code endIndex} (Pega a {@code Array} inteira á partir do índice informado como inciial)
	 */
	public static String arrayToString(String[] array, int startIndex, String separator)
		{ return arrayToString(array, startIndex, array.length - 1, separator); }

	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code endIndex} e {@code regexSeparator}<br>
	 */
	public static String arrayToString(String[] array, int startIndex)
		{ return arrayToString(array, startIndex, array.length - 1); }
	
	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code startIndex} e {@code endIndex}<br>
	 */
	public static String arrayToString(String[] array, String separator)
		{ return arrayToString(array, 0, array.length - 1, separator); }
	
  /**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code startIndex}, {@code endIndex} e {@code regexSeparator}<br>
	 */
	public static String arrayToString(String[] array)
		{ return arrayToString(array, 0, array.length - 1); }

	public static String intArrayToString(int[][] array, String valuesSeparator) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < array.length; y++)
			for (int x = 0; x < array[0].length; x++)
				sb.append((x > 0 ? " " : sb.isEmpty() ? "" : "\n") + array[y][x]);
		return sb.toString();
	}
	
	public static String intArrayToString(int[][] array)
		{ return intArrayToString(array, " "); }

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
		catch (PatternSyntaxException e)
			{ throw new RuntimeException(valuesSeparator + " - Invalid regex for a separator"); }
		catch (NumberFormatException e)
			{ throw new RuntimeException("Invalid int value inside one of the string elements - " + invalid); }
	}
	
	public static int[][] stringToIntArray(String string)
		{ return stringToIntArray(string, " "); }

}
