package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class PowerShellManager {

  private Process powerShellProcess;
  private BufferedWriter powerShellWriter;
  private BufferedReader powerShellReader;

  public PowerShellManager(String launchCommand) {
		try {
			powerShellProcess = new ProcessBuilder("powershell.exe", "-NoExit", "-Command", launchCommand).start();
			powerShellWriter = new BufferedWriter(new OutputStreamWriter(powerShellProcess.getOutputStream()));
			powerShellReader = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Falha ao iniciar PowerShell");
		}
  }

	public void execute(String command) {
		execute(command, 0);
	}
	
	public List<String> execute(String command, int timeout) {
		try {
			powerShellWriter.write(command);
	    powerShellWriter.newLine();
	    powerShellWriter.flush();
	    return timeout == 0 ? null : executeBuffer(timeout);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Falha ao executar comando do PowerShell -\n" + command);
		}
	}
	
	private List<String> executeBuffer(int timeout) {
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
