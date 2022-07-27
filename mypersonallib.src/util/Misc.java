package util;

import java.util.List;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class Misc {
	
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
	 * Retorna {@code true} se o {@code wildcard} especificado for encontrado na {@code String} informada.
	 */
	public static Boolean iswm(String text, String wildcard) 
		{ return text.matches(("\\Q" + wildcard + "\\E").replace("*", "\\E.*\\Q")); }
	
	
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
