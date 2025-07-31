package util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;

public class AlternativeKeyPresser {
	
	private static final int KEYEVENTF_KEYUP = 0x0002;

	public interface User32 extends Library {
		User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class);
		void keybd_event(byte bVk, byte bScan, int dwFlags, WinDef.LPVOID dwExtraInfo);
	}

	public static void keyPress(int keyCode) {
		User32.INSTANCE.keybd_event((byte) keyCode, (byte) 0, 0, null);
	}

	public static void keyRelease(int keyCode) {
		User32.INSTANCE.keybd_event((byte) keyCode, (byte) 0, KEYEVENTF_KEYUP, null);
	}

	public static void delay(int ms) {
		Thread thread = Thread.currentThread();
		if (!thread.isInterrupted()) {
			try {
				Thread.sleep(ms);
			}
			catch (final InterruptedException ignored) {
				thread.interrupt();
			}
		}
	}

}
