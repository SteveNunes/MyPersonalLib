package util;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public abstract class DesktopUtils {
	
	public static void removeFocusFromCurrentWindow() {
    User32 user32 = User32.INSTANCE;
    HWND desktop = user32.FindWindow("Progman", null);
    user32.SetForegroundWindow(desktop);
	}

	public static int getSystemScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	public static int getSystemScreenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	public static int getHardwareScreenWidth() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
	}

	public static int getHardwareScreenHeight() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
	}

	public static float getSystemDpiScale() {
		return (float) getSystemScreenWidth() / (float) getHardwareScreenWidth();
	}

	public static void editFile(File file) {
		try {
			Desktop.getDesktop().edit(file);
		}
		catch (IOException e) {
			throw new RuntimeException("Não foi possível editar o arquivo \"" + file.getAbsolutePath() + "\"\n\t" + e.getMessage());
		}
	}

	public static void runProcess(String processName, String processParam) {
		ProcessBuilder processBuilder = new ProcessBuilder(processName, processParam);
		try {
			Process process = processBuilder.start();
			process.waitFor();
		}
		catch (IOException | InterruptedException e) {
			throw new RuntimeException("Não foi possível executar o processo \"" + processName + " " + processParam + "\"\n\t" + e.getMessage());
		}
	}

	public static List<ProcessHandle> getProcessesList() {
		return new ArrayList<>(ProcessHandle.allProcesses().toList());
	}
	
	public static ProcessHandle getProcessByPid(long pid) {
		try {
			return ProcessHandle.of(pid).get();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static long getProcessPidByName(String processName) {
		try {
			for (ProcessHandle process : getProcessesList()) {
				String name = new File(process.info().command().toString()).getName();
				if (name.contains(processName))
					return process.pid();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void main(String[] args) {
		System.out.println(processExists("Notepad++"));
	}
	
	public static boolean processExists(String processName) {
		return processExists(processName, -1, false);
	}
	
	public static boolean processExistsRegex(String processName) {
		return processExists(processName, -1, true);
	}
	
	public static boolean processExists(String processName, long processId) {
		return processExists(processName, processId, false);
	}
	
	public static boolean processExistsRegex(String processName, long processId) {
		return processExists(processName, processId, true);
	}
	
	private static boolean processExists(String processName, long processId, boolean regex) {
		try {
			processName = processName.toLowerCase();
			for (ProcessHandle process : getProcessesList()) {
				String name = new File(process.info().command().toString()).getName();
				if ((processId == -1 || process.pid() == processId) &&
						((!regex && name.toLowerCase().contains(processName)) ||
						(regex && Pattern.compile(processName).matcher(name.toLowerCase()).find())))
							return true;
			}
			return false;
		}
		catch (Exception e) {
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

}
