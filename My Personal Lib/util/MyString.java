package util;

import java.text.Normalizer;
import java.util.Locale;

public abstract class MyString {

	public static Boolean isShort(String str) {
    try {
      Short.parseShort(str);
      return true;
    }
    catch (NumberFormatException e)
    	{ return false; }
	}
	
	public static Boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    }
    catch (NumberFormatException e)
    	{ return false; }
	}
	
	public static Boolean isDouble(String str) {
    try {
      Double.parseDouble(str);
      return true;
    }
    catch (NumberFormatException e)
    	{ return false; }
	}
	
	public static Boolean isFloat(String str) {
    try {
      Float.parseFloat(str);
      return true;
    }
    catch (NumberFormatException e)
    	{ return false; }
	}
	
	public static Boolean isLong(String str) {
    try {
      Long.parseLong(str);
      return true;
    }
    catch (NumberFormatException e)
    	{ return false; }
	}
	
	public static Boolean isBoolean(String str) {
    try {
      Boolean.parseBoolean(str);
      return true;
    }
    catch (NumberFormatException e)
    	{ return false; }
	}
	
	public static String ignoraAcentos(String text) {
    return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.getDefault());
	}
	
	/**
	 * Retorna a {@code String} informada contendo um valor numérico, preenchida
	 * com zeros á esquerda, até chegar ao total de digitos especificados.<p>
	 */
	public static String fillWithZerosAtLeft(String number, int totalDigits) {
		while (number.length() < totalDigits)
			number = "0" + number;
		return number;
	}
	
	/**
	 * Retorna a string informada com no máximo {@code limitLenght} de letras.
	 * Se a string for maior que {@code limitLenght}, poe '...' ao final da string.
	 */
	public static String limitedString(String string, int limitLenght) {
		StringBuilder sb = new StringBuilder();
		for (String s : string.split(" ")) {
			if (sb.length() + s.length() >= limitLenght) {
				sb.append("...");
				return sb.toString();
			}
			if (!sb.isEmpty())
				sb.append(" ");
			sb.append(s);
			
		}
		return sb.toString();
	}

}
