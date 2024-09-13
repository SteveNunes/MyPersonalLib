package util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public abstract class Encrypter { 

	private static Random random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));
	
  /**
   * Codifica/Decodifica uma palavra/codigo baseado em uma palavra chave
   * 
   * @param palavra a palavra á ser codificada/descodificada
   * @param opcao 0 - Codificar, 1 - Descodificar	
   * @param chave a palavra-chave para codificar/descodificar a palavra	
   */
	public static String vigenere(String palavra, int opcao, String chave) {
	  while (chave.length() < palavra.length())
	  	chave += chave;
	  StringBuilder resultado = new StringBuilder();
	  for(int n = 0, n2 = 0, coord; n < palavra.length(); n++) {
	  	char c1 = palavra.charAt(n);
    	int z1 = Character.isUpperCase(c1) ? 65 : 97;
	    if (c1 != ' ') {
		  	char c2 = chave.charAt(n2++);
	    	int z2 = Character.isUpperCase(c2) ? 65 : 97;
	      coord = opcao == 0 ? (c1 - z1) + (c2 - z2) :
	      	(c1 - z1) - (c2 - z2);
	      if (coord > 25)
	      	coord -= 26;
	      if (coord < 0)
	      	coord += 26;
	      resultado.append((char)(z1 + coord));
	    }
	    else
	    	resultado.append(' ');
	  }
	  return resultado.toString();
	}

	static long stringToByte(String string) {
		long b = 0;
		for (char c : string.toCharArray())
			b += (long)c;
		if (b < 0)
			b += Long.MAX_VALUE + 1;
		return b;
	}
	
	static String decrypt(String code, String password) {
		String str = "", result = "";
		long b = stringToByte(password);
		try {
			for (char s : code.toCharArray())
			str += (char)(((long)s - 31L) / b);
			BigInteger bi = new BigInteger(str);
	    for (int n = 0; n < bi.bitLength(); n += 8)
	    	result += (char)(bi.shiftRight(n).intValue() & 0xFF);
	    return result;
		}
		catch (Exception e) {
			str = "";
			int n = random.nextInt(password.length() + 50) + 50;
			while (str.length() < n)
				str += (char)(random.nextInt(Short.MAX_VALUE - 31) + 31);
			return str;
		}
  }
	
	static String encrypt(String text, String password) {
		BigInteger i = BigInteger.ZERO;
    for (int n = 0, z = 0; n < text.length(); n++, z += 8)
    	i = i.or(BigInteger.valueOf((int)text.charAt(n)).shiftLeft(z));
    String str = i.toString(), result = "";
    long b = stringToByte(password);
    for (char c : str.toCharArray())
    	result += (char)((long)c * b + random.nextLong(b) + 31L);
    
    return result;
	}
	
}
