package util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import enums.TextMatchType;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public abstract class Misc {
	
	private static Map<String, Map<Long, ?>> uniqueId = new HashMap<>();
	
	/** Define um evento que será disparado quando o programa for encerrado */
	public static void setShutdownEvent(Runnable runnable) {
		Thread shutdownHook = new Thread(runnable);
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	/** Chame esse método no final do seu main loop, passando no consumer a
	  * chamada do mesmo método para um efetivo loop infinito sem dar freezing */
	public static void runLater(Runnable runnable)
		{ Platform.runLater(runnable); }
	
	/**
	 * Criar um timer que fica executando determinada(s) tarefa(s) em intervalos fixos.
	 * @param timeUnit - O tipo de unidade de tempo que será usado ao especificar os valores de delay
	 * @param startDelay - Delay até o início das tarefas
	 * @param repeatDelay - Delay do intervalo entre a repetição das tarefas
	 * @param runnable - Conjunto de tarefas á serem executadas
	 * @return - Um tipo {@code ScheduledExecutorService} permitindo que você o armazene em uma variável, para poder parar o timer posteriormente, com o método .shutdown().
	 */
	public static ScheduledExecutorService createTimer(TimeUnit timeUnit, long startDelay, long repeatDelay, Runnable runnable) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(runnable, startDelay, repeatDelay, timeUnit);
		return executor;
	}

	public static ScheduledExecutorService createTimer(TimeUnit timeUnit, long repeatDelay, Runnable runnable)
		{ return createTimer(timeUnit, 0, repeatDelay, runnable); }
	
	public static ScheduledExecutorService createTimer(long startDelay, long repeatDelay, Runnable runnable)
		{ return createTimer(TimeUnit.MILLISECONDS, startDelay, repeatDelay, runnable); }
	
	public static ScheduledExecutorService createTimer(long repeatDelay, Runnable runnable)
		{ return createTimer(TimeUnit.MILLISECONDS, 0, repeatDelay, runnable); }

	/** Atalho para pausar a thread, sem precisar se preocupar com o try catch envolvido. */
	public static void sleep(long millis) {
		try
			{ Thread.sleep(millis); }
		catch (Exception e) {}
	}
	
	/** Apenas imprime no console o valor de System.currentTimeMillis() */
	public static void printCTime()
		{ System.out.println(System.currentTimeMillis()); }
	
	/** Retorna o tempo de processamento do consumer, em milisegundos */
	public static <T> long bench(Runnable runnable) {
		long start = System.currentTimeMillis();
		runnable.run();
		return System.currentTimeMillis() - start;
	}
	
	public static byte[] charArrayToBytes(char[] chars) {
	  CharBuffer charBuffer = CharBuffer.wrap(chars);
	  ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
	  byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
	            byteBuffer.position(), byteBuffer.limit());
	  Arrays.fill(byteBuffer.array(), (byte) 0);
	  return bytes;
	}

	/**
	 *  Converte long decimal em IP
	 */
	public static long IPToLong(String ip) {
		String[] split = ip.split("\\.");
	  long longIP = 0;
		for (int n = 0; n < split.length; n++)
			longIP += Long.parseLong(split[n]) * Math.pow(256, (3 - n));
		return longIP;
	}
	
	/**
	 * Converte IP em long decimal
	 */
	public static String longToIP(long longIP) {
		String ip = "" + (longIP / 256 / 256 / 256) + "." + (longIP / 256 / 256 % 256) + ".";
	  ip += (longIP / 256 % 256) + "." + (longIP % 256);
	  return ip;
	}
	
	/**
	 * Clone de $strip do mIRC Scripting
	 */
	public static String mircStrip(String text) {
	  StringBuilder result = new StringBuilder();
	  int ignore = 0;
	  List<Character> strips = Arrays.asList((char)2, (char)3, (char)15, (char)29, (char)30, (char)31);
	  for (int n = 0; n < text.length(); n++) {
	  	char c = text.charAt(n);
	    if (c == (char)3) ignore = ignore == 0 ? 1 : -1;
	    else if (ignore != 0) {
	      if (ignore == 1 && c == ',')
	      	ignore = 2;
	      else if (!Character.isDigit(c))
	      	ignore = 0;
	    }
	    if (ignore == 0 && !strips.contains(c))
	    	result.append(c);
	    else if (ignore == -1)
	    	ignore = 0;
	  }
	  return result.toString();
	}
	
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
	  for(int n = 0, n2 = 0, pos; n < palavra.length(); n++) {
	  	char c1 = palavra.charAt(n);
    	int z1 = Character.isUpperCase(c1) ? 65 : 97;
	    if (c1 != ' ') {
		  	char c2 = chave.charAt(n2++);
	    	int z2 = Character.isUpperCase(c2) ? 65 : 97;
	      pos = opcao == 0 ? (c1 - z1) + (c2 - z2) :
	      	(c1 - z1) - (c2 - z2);
	      if (pos > 25)
	      	pos -= 26;
	      if (pos < 0)
	      	pos += 26;
	      resultado.append((char)(z1 + pos));
	    }
	    else
	    	resultado.append(' ');
	  }
	  return resultado.toString();
	}
	
	public static Boolean alwaysTrue()
		{ return true; }
	
	@SuppressWarnings("unchecked")
	public static <T> long getUniqueId(T c, Boolean negativeId) {
		long id = 0;
		if (!uniqueId.containsKey(c.getClass().toString()))
			uniqueId.put(c.getClass().toString(), new HashMap<Long, T>());
		Map<Long, T> m = (Map<Long, T>) uniqueId.get(c.getClass().toString());
		while ((!negativeId && m.containsKey(++id)) || (negativeId && m.containsKey(--id)));
		m.put(id, c);
		return id;
	}
	
	public static <T> long getUniqueId(T c)
		{ return getUniqueId(c, false); }
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getUniqueIdListOf(T c) {
		Map<Long, T> m = (Map<Long, T>) uniqueId.get(c.getClass().toString());
		return m.values().stream().collect(Collectors.toList());
	}
	
	/**
	 * Passado dois objetos, retorna o que não for {@code null}.
	 * Se ambos objetos não forem {@code null}, é retornado o primeiro objeto.
	 */
	public static <T> T notNull(T o1, T o2)
		{ return o1 == null ? o2 : o1; }
	
	public static Boolean textMatch(String text, String pattern, TextMatchType matchType, Boolean caseSensitive) {
		if (text == null || text.isEmpty() || pattern == null || pattern.isEmpty())
			return false;
		if (matchType == TextMatchType.WILDCARD &&
				((!caseSensitive && text.toLowerCase().matches(("\\Q" + pattern.toLowerCase() + "\\E").replace("*", "\\E.*\\Q")))) ||
				 (caseSensitive && text.matches(("\\Q" + pattern + "\\E").replace("*", "\\E.*\\Q"))))
						return true;
		if (matchType == TextMatchType.REGEX) {
			String[] regexChars = {"\\", "+", ".", "*", "[", "]", "{", "}", "(", ")", "?", "^", "$", "|"};
			String pat = pattern;
			for (String c : regexChars)
				pat.replace(c, "\\" + c);
			if ((!caseSensitive && text.toLowerCase().matches(pat.toLowerCase())) ||
				  (caseSensitive && text.matches(pat)))
						return true;
		}
		if (matchType == TextMatchType.EXACTLY &&
				((!caseSensitive && text.toLowerCase().contains(pattern.toLowerCase())) ||
				 (caseSensitive && text.contains(pattern))))
						return true;
		return false;
	}

	public static Boolean textMatch(String text, String pattern, TextMatchType matchType)
		{ return textMatch(text, pattern, matchType, true); }
	
	public static Boolean textMatch(String text, String pattern)
		{ return textMatch(text, pattern, TextMatchType.EXACTLY); }
	
	/**
	 * Converte uma {@code Array} em {@code String}.
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

class Tt1 {
	int i;
	Long id;
	
	Tt1 (int i) { 
		this.i = i;
		id = Misc.getUniqueId(this);
	}

	@Override
	public String toString() { return "Tt1 [i=" + i + "] id=" + id + "]"; }
	
}

class Tt2 {
	int i;
	Long id;
	
	Tt2 (int i) { 
		this.i = i;
		id = Misc.getUniqueId(this);
	}

	@Override
	public String toString() { return "Tt2 [i=" + i + "] id=" + id + "]"; }
}