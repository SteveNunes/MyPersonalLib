package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Encrypter { 

	public static String encrypt(List<String> strings) {
		StringBuilder result = new StringBuilder();
		char c;
		int x = 0, plus = Misc.rand(21, 200);
		result.append((char)plus);
		for (String s : strings) {
			for (int n = 0; n < s.length(); n++) {
				c = s.charAt(n);
				if (c == ' ')
					result.append((char)((int)Misc.rand(11, 20)));
				else
					result.append((char)((int)c * (x + plus)));
			}
			result.append((char)Misc.rand(1, 10));
			x = 0;
		}
		return result.toString();
	}

	public static String encrypt(String string)
		{ return encrypt(Arrays.asList(string)); }

	public static List<String> decrypt(String string) {
		List<String> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		char c;
		int plus = (int)string.charAt(0);
		for (int n = 1, x = 0, z; n < string.length(); n++) {
			c = string.charAt(n);
			z = (int)c;
			if (z <= 10) {
				result.add(sb.toString());
				sb = new StringBuilder();
				x = 0;
			}
			else if (z <= 20)
				sb.append(' ');
			else
				sb.append((char)(z / (x + plus)));
		}
		return result;
	}
	
	public static void main(String[] args) {
		List<String> teste = Arrays.asList("Steve Nunes da Silva");
		String en = encrypt(teste);
		System.out.println(en);
	}

}
