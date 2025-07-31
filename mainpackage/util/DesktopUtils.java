package util;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public abstract class DesktopUtils {
	
	public static int getSystemScreenWidth()
		{ return Toolkit.getDefaultToolkit().getScreenSize().width; }

	public static int getSystemScreenHeight()
		{ return Toolkit.getDefaultToolkit().getScreenSize().height; }
	
	public static int getHardwareScreenWidth()
		{ return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth(); }
	
	public static int getHardwareScreenHeight()
		{ return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight(); }
	
	public static float getSystemDpiScale()
		{ return (float)getHardwareScreenWidth() / (float)getSystemScreenWidth(); }
	
	public static void editFile(File file) {
    try
    	{ Desktop.getDesktop().edit(file); }
    catch (IOException e)
    		{ throw new RuntimeException("Não foi possível editar o arquivo \"" + file.getAbsolutePath() + "\"\n\t" + e.getMessage()); }
	}
	
	public static void runProcess(String processName, String processParam) {
		ProcessBuilder processBuilder = new ProcessBuilder(processName, processParam);
		try {
	    Process process = processBuilder.start();
	    process.waitFor();
		}
		catch (IOException | InterruptedException e)
			{ throw new RuntimeException("Não foi possível executar o processo \"" + processName + " " + processParam + "\"\n\t" + e.getMessage()); }
	}

	public static boolean isProcessRunning(String processName) {
		try {
			Process process = Runtime.getRuntime().exec("tasklist");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(processName))
					return true;
			}
			return false;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void taskKillByPid(Long processPid) {
		try {
			new ProcessBuilder("taskkill", "/F", "/PID", "" + processPid).start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
  
	public static void taskKillByName(String processName) {
		try {
			new ProcessBuilder("taskkill", "/F", "/IM", processName).start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean proccessExists(String processName, Long processPid) {
		Optional<ProcessHandle> processHandleOptional = ProcessHandle.of(processPid);
		if (processHandleOptional.isPresent()) {
			ProcessHandle processHandle = processHandleOptional.get();
			if (processHandle.isAlive()) {
				ProcessHandle.Info info = processHandle.info();
				Optional<String> commandOptional = info.command();
				if (commandOptional.isPresent()) {
					String command = commandOptional.get();
					String actualExecutableName = new File(command).getName();
					return actualExecutableName.toLowerCase().contains(processName.toLowerCase());
				}
			}
		}
		return false;
	}
	
}
