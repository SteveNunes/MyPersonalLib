package util;

import java.security.SecureRandom;
import java.util.List;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class Misc {
	
	public static <T> T notNull(T t1, T t2)
		{ return t1 == null ? t2 : t1; }
	
	public static String fillWithZerosAtLeft(String number, int totalDigits) {
		while (number.length() < totalDigits)
			number = "0" + number;
		return number;
	}

	public static int rand(int n1, int n2) 
		{ return new SecureRandom().nextInt(n2 - n1 + 1) + n1; }

	public static Boolean iswm(String text, String pattern) 
		{ return text.matches(("\\Q" + pattern + "\\E").replace("*", "\\E.*\\Q")); }
	
	public static String arrayToString(String[] array, int startIndex, int endIndex, String spacing) {
		StringBuilder result = new StringBuilder(); 
		for (int n = startIndex; n <= endIndex; n++) {
			if (n > startIndex)
				result.append(spacing);
			result.append(array[n]);
		}
		return result.toString();
	}
	
	public static String arrayToString(String[] array, int startIndex, int endIndex)
		{ return arrayToString(array, startIndex, endIndex, " "); }

	public static String arrayToString(String[] array, int startIndex, String spacing)
		{ return arrayToString(array, startIndex, array.length - 1, spacing); }

	public static String arrayToString(String[] array, int startIndex)
		{ return arrayToString(array, startIndex, array.length - 1); }
	
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
