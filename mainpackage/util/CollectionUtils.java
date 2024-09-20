package util;

import java.util.List;

public abstract class CollectionUtils {

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
	
	public static <T> void copyArray(T[] sourceArray, T[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static <T> void copyArray(T[][] sourceArray, T[][] targetArray) {
		for (int y = 0; y < sourceArray.length; y++)
			for (int x = 0; x < sourceArray[0].length; x++)
			targetArray[y][x] = sourceArray[y][x];
	}
	
	public static void copyArray(boolean[] sourceArray, boolean[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static void copyArray(byte[] sourceArray, byte[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static void copyArray(short[] sourceArray, short[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static void copyArray(int[] sourceArray, int[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static void copyArray(long[] sourceArray, long[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static void copyArray(float[] sourceArray, float[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}
	
	public static void copyArray(double[] sourceArray, double[] targetArray) {
		for (int i = 0; i < sourceArray.length; i++)
			targetArray[i] = sourceArray[i];
	}

	public static <T> T getRandomItemFromList(List<T> list)
		{ return list == null || list.size() == 0 ? null : getRandomItemFromList(list, 0, list.size() - 1); }
	
	public static <T> T getRandomItemFromList(List<T> list, int minIdex, int maxIndex)
		{ return list == null || list.size() == 0 ? null : list.size() == 1 ? list.get(0) : list.get((int)MyMath.getRandom(minIdex, maxIndex)); }
	
	public static <T> T getRandomItemFromArray(T[] list)
		{ return list == null || list.length == 0 ? null : getRandomItemFromArray(list, 0, list.length - 1); }
	
	public static <T> T getRandomItemFromArray(T[] list, int minIdex, int maxIndex)
		{ return list == null || list.length == 0 ? null : list.length == 1 ? list[0] : list[(int)MyMath.getRandom(minIdex, maxIndex)]; }
	
}
