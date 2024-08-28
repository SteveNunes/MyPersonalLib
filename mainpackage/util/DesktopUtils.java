package util;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

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

}
