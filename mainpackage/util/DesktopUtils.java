package util;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

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

}
