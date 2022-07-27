package util;

import java.math.BigDecimal;
import java.security.SecureRandom;

public class MyMath {
	
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
