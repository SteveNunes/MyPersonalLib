package util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyMath {
	
	public static String shortNumber(BigInteger num, int afterComma, String[] abreviations) {
		if (num.signum() == 0)
			return "0";
		Boolean isNegative = num.signum() == -1; 
		if (isNegative)
			num = new BigInteger(num.toString().substring(1));
		int c = 0;
		BigInteger div = new BigInteger("1000");
		BigInteger div2 = new BigInteger("0");
		while (num.divide(div).signum() > 0) {
			div2 = new BigInteger(div.toString());
			div = div.multiply(new BigInteger("1000"));
			c++;
		}
		if (div2.equals(new BigInteger("0")))
			return (isNegative ? "-" : "") + num.toString();
		List<Character> lets = new ArrayList<>(Arrays.asList('a', (char)('a' - 1)));
		String let;
		if (c < abreviations.length)
			let = abreviations[c];
		else {
			let = "";
			int n = abreviations.length;
			while (n++ <= c) {
				int p = lets.size() - 1;
				lets.set(p, (char)(lets.get(p) + 1));
				while (p >= 0 && lets.get(p) > 'z') {
					lets.set(p, 'a');
					if (p == 0)
						lets.add(0, 'a');
					else {
						p--;
						lets.set(p, (char)(lets.get(p) + 1));
					}
				}
			}
			for (Character ch : lets)
				let += ch;
		}
		String f = "#,###";
		while (--afterComma >= 0)
			f += f.length() == 5 ? ".0" : "0";
		DecimalFormat df = new DecimalFormat(f);
		return (isNegative ? "-" : "") + df.format(new BigDecimal(num).divide(new BigDecimal(div2))) + let;
	}
	
	public static String shortNumber(BigInteger num, int afterComma)
		{ return shortNumber(num, afterComma, new String[] {" ", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D"}); }

	public static String shortNumber(BigInteger num, String[] abreviations)
		{ return shortNumber(num, 0, abreviations); }

	public static String shortNumber(BigInteger num)
		{ return shortNumber(num, 0); }

	public static BigDecimal binaryStringToBigDecimal(String binary) {
		BigDecimal result = new BigDecimal(0);
		BigDecimal n = new BigDecimal(1);
		char[] bin = binary.toCharArray();
		for (int p = bin.length - 1; p >= 0; p--) {
			result = result.add(bin[p] == '1' ? n : new BigDecimal(0));
			n = n.add(n);
		}
		return result;
	}

	public static double rand(double n1, double n2)
		{ return new SecureRandom().nextDouble(n2 - n1 + 1) + n1; }

}
