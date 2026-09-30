package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

	public static double getDoubleRandom(double min, double max) {
		if (min > max)
			throw new IllegalArgumentException("O valor mínimo não pode ser maior que o valor máximo.");
		return ThreadLocalRandom.current().nextDouble(min, max + Double.MIN_VALUE);
	}

	public static long getLongRandom(long min, long max) {
		if (min > max)
			throw new IllegalArgumentException("O valor mínimo não pode ser maior que o valor máximo.");
		return ThreadLocalRandom.current().nextLong(min, max + 1);
	}

	public static int getRandom(int min, int max) {
		if (min > max)
			throw new IllegalArgumentException("O valor mínimo não pode ser maior que o valor máximo.");
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	/*
	 * Um valor entre 1 e {@code max} é sorteado internamente.
	 * Se o valor sorteado for igual ou menor que {@code proc}, retorna {@code true}
	 */
	public static boolean testProc(int proc, int max) {
		return getRandom(1, max) <= proc;
	}

	/*
	 * Um valor entre 1 e 100 é sorteado internamente.
	 * Se o valor sorteado for igual ou menor que {@code proc}, retorna {@code true}
	 */
	public static boolean testProc(int proc) {
		return testProc(proc, 100);
	}

	public static <T> T getRandomObjectFromArray(T[] array) {
		return getRandomObjectFromArray(array, 0, array.length - 1);
	}
	
	public static <T> T getRandomObjectFromArray(T[] list, int minIdex, int maxIndex) {
		if (list == null || list.length == 0)
			return null;
		int i = getRandom(minIdex, maxIndex);
		return list[i];
	}
	
	public static <T> T getRandomObjectFromList(List<T> list) {
		return getRandomObjectFromList(list, 0, list.size() - 1);
	}
	
	public static <T> T getRandomObjectFromList(List<T> list, int minIdex, int maxIndex) {
		if (list == null || list.isEmpty())
			return null;
		int i = getRandom(minIdex, maxIndex);
		return list.get(i);
	}
	
	public static <T> T getRandomObjectFromSet(Set<T> set) {
		if (set == null || set.isEmpty())
			return null;
		return getRandomObjectFromList(new ArrayList<>(set));
	}
	
	public static <T> T getRandomKeyFromMap(Map<T, ?> map) {
		if (map == null || map.isEmpty())
			return null;
		return getRandomObjectFromList(new ArrayList<>(map.keySet()));
	}
	
}
