package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Ativar ou desativar filtro de mensagens que saem no console.
 * Util para eliminar aquelas mensagens de warning indesejadas.
 */

public class ConsoleFilter {

	private static String logFilePath = null;
	private static FileOutputStream logFileOutputStream = null;
	private static PrintStream logFilePrintStream = null;
	private static final PrintStream originalOut = System.out;
	private static final PrintStream originalErr = System.err;
	private static Consumer<String> onReceiveData = null;
	private static List<String> filterStrings = new ArrayList<>();
	private static List<Pattern> filterPatterns = new ArrayList<>();
	private static boolean useRegex = false;
	private static boolean filterAll = false;

	public static void setOnReceiveData(Consumer<String> onReceiveData) {
		ConsoleFilter.onReceiveData = onReceiveData;
	}

	public static void setFilterStrings(List<String> strings) {
		filterStrings = new ArrayList<>(strings);
		filterPatterns = new ArrayList<>();
		useRegex = false;
	}

	public static void setFilterPatterns(List<String> regexPatterns) {
		filterStrings = new ArrayList<>();
		filterPatterns = new ArrayList<>();
		for (String pattern : regexPatterns) {
			filterPatterns.add(Pattern.compile(pattern));
		}
		useRegex = true;
	}

	public static void enableFilterAll() {
		filterAll = true;
	}

	public static void disableFilterAll() {
		filterAll = false;
	}

	public static void enableFilter() {
		System.setOut(new FilteredPrintStream(originalOut));
		System.setErr(new FilteredPrintStream(originalErr));
	}

	public static void disableFilter() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	public static void setTextLogOutput(String path) {
		logFilePath = path;
		try {
			if (logFileOutputStream != null) {
				logFileOutputStream.close();
				logFilePrintStream.close();
			}
			logFileOutputStream = new FileOutputStream(path, true);
			logFilePrintStream = new PrintStream(logFileOutputStream, true, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			System.err.println("Erro ao configurar o log de texto: " + e.getMessage());
			logFilePath = null;
			logFileOutputStream = null;
			logFilePrintStream = null;
		}
	}

	public static void disableTextLogOutput() {
		if (logFileOutputStream != null) {
			try {
				logFileOutputStream.close();
				logFilePrintStream.close();
			}
			catch (IOException e) {
				System.err.println("Erro ao fechar o arquivo de log: " + e.getMessage());
			}
			finally {
				logFilePath = null;
				logFileOutputStream = null;
				logFilePrintStream = null;
			}
		}
	}

	private static class FilteredPrintStream extends PrintStream {

		private final PrintStream delegate;

		public FilteredPrintStream(PrintStream delegate) {
			super(new OutputStream() {
				@Override
				public void write(int b) {}
			});
			this.delegate = delegate;
		}

		private void logToFile(String message) {
			if (logFilePrintStream != null)
				logFilePrintStream.println(message);
			if (onReceiveData != null)
				onReceiveData.accept(message);
		}

		private void printToDelegateWithLineBreaks(String s) {
			if (s != null) {
				String[] lines = s.split(System.lineSeparator());
				for (String line : lines) {
					delegate.println(line);
				}
			}
		}

		@Override
		public void print(String s) {
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void println(String s) {
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(s);
				logToFile(s);
			}
		}

		@Override
		public void print(boolean b) {
			String s = String.valueOf(b);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void print(char c) {
			String s = String.valueOf(c);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void print(int i) {
			String s = String.valueOf(i);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void print(long l) {
			String s = String.valueOf(l);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void print(float f) {
			String s = String.valueOf(f);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void print(double d) {
			String s = String.valueOf(d);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
		}

		@Override
		public void println() {
			if (!filterAll) {
				delegate.println();
				logToFile("");
			}
		}

		@Override
		public void print(char[] s) {
			String str = new String(s);
			if (!filterAll && shouldBeDisplayed(str)) {
				printToDelegateWithLineBreaks(str);
				logToFile(str);
			}
		}

		@Override
		public void println(boolean x) {
			String s = String.valueOf(x);
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(x);
				logToFile(s);
			}
		}

		@Override
		public void println(char x) {
			String s = String.valueOf(x);
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(x);
				logToFile(s);
			}
		}

		@Override
		public void println(int x) {
			String s = String.valueOf(x);
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(x);
				logToFile(s);
			}
		}

		@Override
		public void println(long x) {
			String s = String.valueOf(x);
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(x);
				logToFile(s);
			}
		}

		@Override
		public void println(float x) {
			String s = String.valueOf(x);
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(x);
				logToFile(s);
			}
		}

		@Override
		public void println(double x) {
			String s = String.valueOf(x);
			if (!filterAll && shouldBeDisplayed(s)) {
				delegate.println(x);
				logToFile(s);
			}
		}

		@Override
		public void println(char[] x) {
			String str = new String(x);
			if (!filterAll && shouldBeDisplayed(str)) {
				delegate.println(x);
				logToFile(str);
			}
		}

		@Override
		public PrintStream append(CharSequence csq) {
			String s = String.valueOf(csq);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq, int start, int end) {
			String s = String.valueOf(csq.subSequence(start, end));
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
			return this;
		}

		@Override
		public PrintStream append(char c) {
			String s = String.valueOf(c);
			if (!filterAll && shouldBeDisplayed(s)) {
				printToDelegateWithLineBreaks(s);
				logToFile(s);
			}
			return this;
		}

		private boolean shouldBeDisplayed(String message) {
			if (useRegex) {
				for (Pattern pattern : filterPatterns)
					if (pattern.matcher(message).find())
						return false;
				return true;
			}
			else {
				for (String filter : filterStrings)
					if (message.contains(filter))
						return false;
				return true;
			}
		}
	}

}