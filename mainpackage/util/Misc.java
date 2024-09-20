package util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Misc {
	
	private static Map<String, Map<Long, ?>> uniqueId = new HashMap<>();

	/** Retorna alternadamente entre os valores {@code v1} e {@code v2} baseadp no valor atual de {@code var} (Versão para tipos numéricos) */
	public static <T extends Number & Comparable<? super T>> T toogleValues(T var, T v1, T v2)
		{ return var == v1 ? v2 : v1; }
	
	/** Retorna alternadamente entre os valores {@code v1} e {@code v2} baseadp no valor atual de {@code var} (Versão para objetos) */
	public static <T> T toogleValues(T var, T v1, T v2)
		{ return var.equals(v1) ? v2 : v1; }
	
	/** Define um evento que será disparado quando o programa for encerrado */
	public static void setShutdownEvent(Runnable runnable) {
		Thread shutdownHook = new Thread(runnable);
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	/** Atalho para pausar a thread, sem precisar se preocupar com o try catch envolvido. */
	public static void sleep(long millis) {
		try
			{ Thread.sleep(millis); }
		catch (Exception e) {}
	}
	
	/** Apenas imprime no console o valor de System.currentTimeMillis() */
	public static void printCTime()
		{ System.out.println(System.currentTimeMillis()); }
	
	/** Retorna no console, o tempo de processamento do consumer, em milisegundos */
	public static void benchAndShow(Runnable runnable) {
		long start = System.currentTimeMillis();
		runnable.run();
		System.out.println("Bench result: " + (System.currentTimeMillis() - start) + "ms");
	}
	
	public static Boolean alwaysTrue()
		{ return true; }
	
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
	
	public static String getMyIpOnline() {
		try {
			URL url = new URL("https://api64.ipify.org?format=json");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				StringBuilder response = new StringBuilder();

				while ((line = reader.readLine()) != null)
					response.append(line);

				reader.close();
				System.out.println("Public IP Address: " + response.toString());
			}
			else
				System.out.println("Failed to retrieve public IP address. HTTP Response Code: " + connection.getResponseCode());

			connection.disconnect();
		}
		catch (IOException e)
			{ e.printStackTrace(); }
		return null;
	}

	//* Se chamado constantemente (em um loop de jogo por exemplo), retorna alternadamente true e false no intervalo em ms especificado (Util para fazer algo piscar por exemplo) */
	public static boolean blink(int speed)
		{ return System.currentTimeMillis() / speed % 2 == 0; }
	
	/* Para realizar alguma tarefa massiva que num loop normal levaria muito tempo para ser concluida.
		 Esse consumer receberá um valor inteiro (não sequencial pois cada chamada virá de uma
		 thread diferente) que nunca repete, de 0 até totalCycles.
		 
		 Exemplo de uso:
		 
		 // Forma normal, que levaria mais tempo para ser processado.
		 for (int n = 1; n < 1000000; n++)
			 System.out.println("Raiz quadrada de " + n + ": " + Math.sqrt(n));
			 
		 // Forma mais rápida, utilizando o método abaixo
		 doMassiveTest(1000000, n -> System.out.println("Raiz quadrada de " + n + ": " + Math.sqrt(n)));	
	*/
	
	public static void doMassiveTest(int totalCycles, Consumer<Integer> consumer)
		{ doMassiveTest(totalCycles, consumer, false); }

	public static void doMassiveTest(int totalCycles, Consumer<Integer> consumer, boolean showProgressOnConsole) {
		int[] count = {0, 0, 0};
		IntStream.range(0, totalCycles).parallel().forEach(i -> {
			if (showProgressOnConsole) {
		    count[2] = (int) MyMath.getPorcentFrom(count[0]++, 100000);
		    synchronized (count) {
	        if (count[2] != count[1]) {
	          System.out.println("doMassiveTest() progress: " + count[2] + "%...");
	          count[1] = count[2];
	        }
		    }
			}
			consumer.accept(i);
		});
	}

}