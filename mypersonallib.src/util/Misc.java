package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import enums.TextMatchType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class Misc {
	
	private static Map<String, Map<Long, ?>> uniqueId = new HashMap<>();
	
	public static Boolean alwaysTrue()
		{ return true; }
	
	@SuppressWarnings("unchecked")
	public static <T> long getUniqueId(T c, Boolean negativeId) {
		long id = 0;
		if (!uniqueId.containsKey(c.getClass().toString()))
			uniqueId.put(c.getClass().toString(), new HashMap<Long, T>());
		Map<Long, T> m = (Map<Long, T>) uniqueId.get(c.getClass().toString());
		while ((!negativeId && m.containsKey(++id)) || (negativeId && m.containsKey(--id)));
		m.put(id, c);
		return id;
	}
	
	public static <T> long getUniqueId(T c)
		{ return getUniqueId(c, false); }
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getUniqueIdListOf(T c) {
		Map<Long, T> m = (Map<Long, T>) uniqueId.get(c.getClass().toString());
		return m.values().stream().collect(Collectors.toList());
	}
	
	/**
	 * Passado dois objetos, retorna o que não for {@code null}.
	 * Se ambos objetos não forem {@code null}, é retornado o primeiro objeto.
	 */
	public static <T> T notNull(T o1, T o2)
		{ return o1 == null ? o2 : o1; }
	
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
	 * Converte uma {@code Array} de {@code String} uníca.
	 */
	public static String arrayToString(String[] array, int startIndex, int endIndex, String spacing) {
		StringBuilder result = new StringBuilder(); 
		for (int n = startIndex; n <= endIndex; n++) {
			if (n > startIndex)
				result.append(spacing);
			result.append(array[n]);
		}
		return result.toString();
	}
	
	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede o parâmetro {@code spacing} (É usado o espaço como padrão)
	 */
	public static String arrayToString(String[] array, int startIndex, int endIndex)
		{ return arrayToString(array, startIndex, endIndex, " "); }

	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede o parâmetro {@code endIndex} (Pega a {@code Array} inteira á partir do índice informado como inciial)
	 */
	public static String arrayToString(String[] array, int startIndex, String spacing)
		{ return arrayToString(array, startIndex, array.length - 1, spacing); }

	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code endIndex} e {@code spacing}<br>
	 */
	public static String arrayToString(String[] array, int startIndex)
		{ return arrayToString(array, startIndex, array.length - 1); }
	
	/**
	 * Sobrecarga do método {@code arrayToString(String[] array, int startIndex, int endIndex, String spacing)}<br>
	 * que não pede os parâmetros {@code startIndex}, {@code endIndex} e {@code spacing}<br>
	 */
	public static String arrayToString(String[] array)
		{ return arrayToString(array, 0, array.length - 1); }

	/**
	 * Coloca a {@code String} informada na área de transferência.
	 * @param text
	 */
	public static void putTextOnClipboard(String text) {
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		Clipboard.getSystemClipboard().setContent(content);
	}
	
	/**
	 * Move um item para cima ou para baixo em uma [@code List} (Se {@code inc} for um
	 * valor negativo, move para cima, caso contrário, move para baixo. Ex: Se informar
	 * o valor {@code 3} para {@code inc}, o item será movido 3 posições para baixo.
	 * Se informar o valor {@code -4} para {@code inc}, o item será movido 4 posições
	 * para cima.
	 * 
	 * @param List<T>		A lista a ter o item movido
	 * @param index			Indice do item á ser movido
	 * @param inc				Incremento do indice atual do item especificado
	 * @return					Um valor indicando o total de indices que o item foi movida.
	 * 									Ex: Se o item estiver no indice 2, e você mandar mover o item
	 * 									10 indices para baixo, irá retornar -2 pois ao mover 2 indices
	 * 									para cima, o item chega ao indice 0.
	 */
	public static <T> int moveItemIndex(List<T> list, int index, int inc) {
		if (index >= list.size() || index < 0)
			throw new IndexOutOfBoundsException();
		int moved = 0;
		while (inc != 0 && ((inc < 0 && index > 0) || (inc > 0 && index < (list.size() - 1)))) {
			T obj = list.get(index);
			list.remove(index);
			list.add(index += inc < 0 ? -1 : 1, obj);
			inc += inc < 0 ? 1 : -1;
			moved += inc < 0 ? -1 : 1;
		}
		return moved;
	}

}

class Tt1 {
	int i;
	Long id;
	
	Tt1 (int i) { 
		this.i = i;
		id = Misc.getUniqueId(this);
	}

	@Override
	public String toString() { return "Tt1 [i=" + i + "] id=" + id + "]"; }
	
}

class Tt2 {
	int i;
	Long id;
	
	Tt2 (int i) { 
		this.i = i;
		id = Misc.getUniqueId(this);
	}

	@Override
	public String toString() { return "Tt2 [i=" + i + "] id=" + id + "]"; }
}