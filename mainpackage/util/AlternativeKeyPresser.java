package util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.Pointer;

public class AlternativeKeyPresser {

	public interface User32 extends Library {
		User32 INSTANCE = Native.load("user32", User32.class);

		int SendInput(int nInputs, INPUT[] pInputs, int cbSize);
	}

	// Estrutura base INPUT
	@Structure.FieldOrder({ "type", "input" })
	public static class INPUT extends Structure {
		public static class ByReference extends INPUT implements Structure.ByReference {}

		public int type; // 0 = mouse, 1 = keyboard, 2 = hardware
		public InputUnion input;

		public static class InputUnion extends Union {
			public MOUSEINPUT mi;
			public KEYBDINPUT ki;
		}
	}

	// Estrutura KEYBDINPUT
	@Structure.FieldOrder({ "wVk", "wScan", "dwFlags", "time", "dwExtraInfo" })
	public static class KEYBDINPUT extends Structure {
		public short wVk;
		public short wScan;
		public int dwFlags;
		public int time;
		public Pointer dwExtraInfo;
	}

	// Estrutura MOUSEINPUT só pra ocupar o union
	@Structure.FieldOrder({ "dx", "dy", "mouseData", "dwFlags", "time", "dwExtraInfo" })
	public static class MOUSEINPUT extends Structure {
		public int dx, dy, mouseData, dwFlags, time;
		public Pointer dwExtraInfo;
	}

	// Constantes
	private static final int INPUT_KEYBOARD = 1;
	private static final int KEYEVENTF_KEYUP = 0x0002;

	/** Pressiona (keydown) */
	public static void keyPress(int keyCode) {
		sendKeyEvent((short) keyCode, 0);
	}

	/** Solta (keyup) */
	public static void keyRelease(int keyCode) {
		sendKeyEvent((short) keyCode, KEYEVENTF_KEYUP);
	}

	/** Envia evento */
	private static void sendKeyEvent(short keyCode, int flags) {
		INPUT input = new INPUT();
		input.type = INPUT_KEYBOARD;
		input.input = new INPUT.InputUnion();

		KEYBDINPUT ki = new KEYBDINPUT();
		ki.wVk = keyCode;
		ki.wScan = 0;
		ki.dwFlags = flags;
		ki.time = 0;
		ki.dwExtraInfo = null;

		input.input.ki = ki;
		input.input.setType(KEYBDINPUT.class);
		input.write();

		INPUT[] inputs = { input };
		User32.INSTANCE.SendInput(inputs.length, inputs, input.size());
	}

	/** Delay auxiliar */
	public static void delay(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
