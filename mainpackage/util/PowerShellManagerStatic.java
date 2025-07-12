package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PowerShellManagerStatic {

	public static void execute(String command) {
		execute(command, 0);
	}
	
	public static List<String> execute(String command, int timeout) {
		try {
			Process powerShellProcess = new ProcessBuilder("powershell.exe", "-Command", command).start();
			powerShellProcess.waitFor();
			if (timeout == 0)
				return null;
			BufferedReader powerShellReader = timeout == 0 ? null : new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
	    powerShellProcess.destroy();
	    return executeBuffer(powerShellReader, timeout);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Falha ao executar comando do PowerShell -\n" + command);
		}
	}
	
	private static List<String> executeBuffer(BufferedReader powerShellReader, int timeout) {
		try {
			List<String> lines = new ArrayList<>();
			String line;
			while ((line = powerShellReader.readLine()) != null) {
				lines.add(line);
				if (line.isBlank())
					break;
			}
			return lines;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Falha ao receber resposta do PowerShell");
		}
	}
	
}
