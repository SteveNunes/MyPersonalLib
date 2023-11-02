package util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.math.BigInteger;
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
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import enums.TextMatchType;
import javafx.application.Platform;

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
	public static long bench(Runnable runnable) {
		long start = System.currentTimeMillis();
		runnable.run();
		return System.currentTimeMillis() - start;
	}
	
	public static void benchAndShow(Runnable runnable)
		{ System.out.println("Bench result: " + bench(runnable) + "ms"); }
	
	public static Boolean alwaysTrue()
		{ return true; }
	
	public static Boolean alwaysFalse()
		{ return false; }

	/**
	 * Gera um ID unico sequencial (Negativo ou não) sempre que chamado.
	 * @param c - objeto associado para obter a classe do mesmo. Assim, será possivel gerar IDs unicos para cada tipo de classe.
	 * @param negativeId - {@code true} se for para gerar IDs negativos.
	 * @return - um ID unico sequencial (Negativo ou não) sempre que chamado.
	 */
	
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
	
	/** Recebe a lista de IDs unicos para a classe do objeto informado. */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getUniqueIdListOf(T c) {
		Map<Long, T> m = (Map<Long, T>) uniqueId.get(c.getClass().toString());
		return m.values().stream().collect(Collectors.toList());
	}
	
	/** Passado dois ou mais objetos, retorna o primeiro que não for {@code null}. */
	@SafeVarargs
	public static <T> T notNull(T ... objectList) {
		if (objectList == null)
			return null;
		for (T object : objectList)
			if (object != null)
				return object;
		return null;
	}
	
	/** Coloca a {@code String} informada na área de transferência. */
	public static void putTextOnClipboard(String text) {
		try {
			StringSelection selection = new StringSelection(text);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		}
		catch (Exception e) {}
	}

	/** Recebe o conteudo da área de transferência. */
	public static String getTextFromClipboard() {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Transferable transferable = toolkit.getSystemClipboard().getContents(null);
			if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String str = (String)transferable.getTransferData(DataFlavor.stringFlavor);
				return str;
			}
		}
		catch (Exception e) {}
		return null;
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