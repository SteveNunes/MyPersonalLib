package util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DecimalFormat;

public class MyMath {
	
	public static String shortNumber(BigInteger num, int afterComma, String[] abreviations) {
		int c = 0;
		BigInteger div = new BigInteger("1000");
		BigInteger div2 = new BigInteger("0");
		while (num.divide(div).signum() > 0) {
			div2 = new BigInteger(div.toString());
			div = div.multiply(new BigInteger("1000"));
			c++;
		}
		if (div2.equals(new BigInteger("0")))
			return num.toString();
		String let;
		if (c < abreviations.length)
			let = abreviations[c];
		else {
			int n = abreviations.length;
			char c1 = 'a', c2 = 'a' - 1;
			while (n++ <= c) {
				if (++c2 == 'z') {
					c2 = 'a';
					c1++;
				}
			}
			let = "" + c1 + c2;
		}
		String f = "#,###";
		while (--afterComma >= 0)
			f += f.length() == 5 ? ".0" : "0";
		DecimalFormat df = new DecimalFormat(f);
		return df.format(new BigDecimal(num).divide(new BigDecimal(div2))) + let;
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
