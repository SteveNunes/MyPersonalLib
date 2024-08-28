package util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class JNAUtils {
	
	public static boolean isWindowActive(String windowTitle) {
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
		return hWnd != null && User32.INSTANCE.GetForegroundWindow().equals(hWnd);
	}
	
	public static String getActiveWindowTitle() {
		char[] buffer = new char[1024];
		WinDef.HWND hWnd = User32.INSTANCE.GetForegroundWindow();
		User32.INSTANCE.GetWindowText(hWnd, buffer, buffer.length);
		return Native.toString(buffer);
	}

}
