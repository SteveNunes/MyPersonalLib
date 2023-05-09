package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Encrypter { 

	public static String encrypt(List<String> strings, String password) {
		int plus = (int)(int)MyMath.rand(50, 10);
		StringBuilder result = new StringBuilder();
		strings = new ArrayList<>(strings);
		String x = "" + (char)plus + (char)(password.length() + 2000);
		strings.set(0, x + password + strings.get(0));
		char c;
		for (int p = 0; p < strings.size(); p++) {
			String s = strings.get(p);
			for (int n = 0; n < s.length(); n++) {
				c = s.charAt(n);
				if (p == 0 && n < 2)
					result.append(c);
				else if (c == ' ')
					for (int z = 0, z2 = (int)(int)MyMath.rand(1, plus / 5); z < z2; z++)
						result.append((char)(int)MyMath.rand(30000, 30999));
				else
					result.append((char)(int)MyMath.rand((int)c * plus, (int)c * plus + (plus - 1)));
			}
			for (int z = 0, z2 = (int)(int)MyMath.rand(1, plus / 5); z < z2; z++)
				result.append((char)(int)MyMath.rand(31000, 31999));
		}
		return result.toString();
	}

	public static String encrypt(String string, String password)
		{ return encrypt(Arrays.asList(string), password); }

	public static List<String> decrypt(String string, String password) {
		List<String> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		char c;
		int plus = (int)string.charAt(0);
		int passLen = (int)string.charAt(1) - 2000;
		if (string.length() < passLen + 3)
			throw new RuntimeException("Wrong decrypter password");
		for (int n = 2, z; n < string.length(); n++) {
			if ((n - 2) == passLen) {
				if (!sb.toString().equals(password))
					throw new RuntimeException("Wrong decrypter password");
				sb = new StringBuilder();
			}
			c = string.charAt(n);
			z = (int)c;
			if (z >= 31000 && z < 31999) {
				result.add(sb.toString());
				sb = new StringBuilder();
				for (; n < string.length() && (z = (int)string.charAt(n)) >= 31000 && z < 32000; n++);
				n--;
			}
			else if (z >= 30000 && z < 30999) {
				sb.append(' ');
				for (; n < string.length() && (z = (int)string.charAt(n)) >= 30000 && z < 31000; n++);
				n--;
			}
			else
				sb.append((char)(z / plus));
			
		}
		return result;
	}
	
	public static void main(String[] args) {
		List<String> teste = new ArrayList<>(Arrays.asList("Steve Nunes da Silva"));
		String en = encrypt(teste, "abazaba");
		System.out.println(en);
		for (String s : decrypt(en, "abazaba"))
			System.out.println(s);

	}

}
