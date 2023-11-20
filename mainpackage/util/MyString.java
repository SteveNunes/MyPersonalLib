package util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import enums.TextMatchType;

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

	public static Boolean textMatch(String text, String pattern, TextMatchType matchType, Boolean caseSensitive) {
		if (text == null || text.isEmpty() || pattern == null || pattern.isEmpty())
			return false;
		if (matchType == TextMatchType.WILDCARD &&
				((!caseSensitive && text.toLowerCase().matches(("\\Q" + pattern.toLowerCase() + "\\E").replace("*", "\\E.*\\Q")))) ||
				 (caseSensitive && text.matches(("\\Q" + pattern + "\\E").replace("*", "\\E.*\\Q"))))
						return true;
		if (matchType == TextMatchType.REGEX) {
			String[] regexChars = {"\\", "+", ".", "*", "[", "]", "{", "}", "(", ")", "?", "^", "$", "|"};
			String pat = pattern;
			for (String c : regexChars)
				pat.replace(c, "\\" + c);
			if ((!caseSensitive && text.toLowerCase().matches(pat.toLowerCase())) ||
				  (caseSensitive && text.matches(pat)))
						return true;
		}
		if (matchType == TextMatchType.EXACTLY &&
				((!caseSensitive && text.toLowerCase().contains(pattern.toLowerCase())) ||
				 (caseSensitive && text.contains(pattern))))
						return true;
		return false;
	}

	public static Boolean textMatch(String text, String pattern, TextMatchType matchType)
		{ return textMatch(text, pattern, matchType, true); }
	
	public static Boolean textMatch(String text, String pattern)
		{ return textMatch(text, pattern, TextMatchType.EXACTLY); }
	
	/**
	 * Clone de $strip do mIRC Scripting
	 */
	public static String mircStrip(String text) {
	  StringBuilder result = new StringBuilder();
	  int ignore = 0;
	  List<Character> strips = Arrays.asList((char)2, (char)3, (char)15, (char)29, (char)30, (char)31);
	  for (int n = 0; n < text.length(); n++) {
	  	char c = text.charAt(n);
	    if (c == (char)3) ignore = ignore == 0 ? 1 : -1;
	    else if (ignore != 0) {
	      if (ignore == 1 && c == ',')
	      	ignore = 2;
	      else if (!Character.isDigit(c))
	      	ignore = 0;
	    }
	    if (ignore == 0 && !strips.contains(c))
	    	result.append(c);
	    else if (ignore == -1)
	    	ignore = 0;
	  }
	  return result.toString();
	}
	
	/** remove letras repetidas da frase, se for encontradas mais de 2 ocorrências da mesma letra em sequência */
	public static String removeRepeatedChar(String text) {
		StringBuilder result = new StringBuilder();
		int consecutive = 1;
		char lastChar = 0;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == lastChar)
				consecutive++;
			else
				consecutive = 1;
			if (consecutive < 3)
					result.append(ch);
			lastChar = ch;
		}
		return result.toString();
	}
	
	public static String removeEmojis(String input) {
    String emojiRegex = "[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}\\x{1F700}-\\x{1F77F}\\x{1F780}-\\x{1F7FF}\\x{1F800}-\\x{1F8FF}\\x{1F900}-\\x{1F9FF}\\x{1FA00}-\\x{1FA6F}\\x{1FA70}-\\x{1FAFF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}]";
    Pattern emojiPattern = Pattern.compile(emojiRegex, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    Matcher emojiMatcher = emojiPattern.matcher(input);
    String stringWithoutEmojis = emojiMatcher.replaceAll("");
    return stringWithoutEmojis;
	}

}
