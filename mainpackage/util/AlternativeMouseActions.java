package util;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import enums.AlternativeMouseButton;

public class AlternativeMouseActions {

	public interface User32 extends Library {
		User32 INSTANCE = Native.load("user32", User32.class);

		int SendInput(int nInputs, INPUT[] pInputs, int cbSize);
	}

	// Estrutura base INPUT
	@Structure.FieldOrder({ "type", "mi" })
	public static class INPUT extends Structure {
		public static class ByReference extends INPUT implements Structure.ByReference {}

		public int type; // 0 = mouse, 1 = keyboard, 2 = hardware
		public MOUSEINPUT mi;
	}

	// Estrutura MOUSEINPUT
	@Structure.FieldOrder({ "dx", "dy", "mouseData", "dwFlags", "time", "dwExtraInfo" })
	public static class MOUSEINPUT extends Structure {
		public int dx;
		public int dy;
		public int mouseData;
		public int dwFlags;
		public int time;
		public Pointer dwExtraInfo;
	}

	// Constantes
	private static final int INPUT_MOUSE = 0;
	private static final int MOUSEEVENTF_MOVE = 0x0001;
	private static final int MOUSEEVENTF_ABSOLUTE = 0x8000;
	private static final int MOUSEEVENTF_LEFTDOWN = 0x0002;
	private static final int MOUSEEVENTF_LEFTUP = 0x0004;
	private static final int MOUSEEVENTF_RIGHTDOWN = 0x0008;
	private static final int MOUSEEVENTF_RIGHTUP = 0x0010;
	private static final int MOUSEEVENTF_MIDDLEDOWN = 0x0020;
	private static final int MOUSEEVENTF_MIDDLEUP = 0x0040;
	private static final int MOUSEEVENTF_WHEEL = 0x0800;

	private static final int WHEEL_DELTA = 120;

	/** Move o mouse para coordenadas absolutas (x,y da tela) */
	public static void mouseMove(int x, int y) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();

		// Normalizar para 0-65535
		int normalizedX = x * 65535 / screenWidth;
		int normalizedY = y * 65535 / screenHeight;

		INPUT input = new INPUT();
		input.type = INPUT_MOUSE;
		input.mi = new MOUSEINPUT();
		input.mi.dx = normalizedX;
		input.mi.dy = normalizedY;
		input.mi.mouseData = 0;
		input.mi.dwFlags = MOUSEEVENTF_MOVE | MOUSEEVENTF_ABSOLUTE;
		input.mi.time = 0;
		input.mi.dwExtraInfo = null;

		INPUT[] inputs = { input };
		User32.INSTANCE.SendInput(inputs.length, inputs, input.size());
	}

	public static void press(AlternativeMouseButton button) {
		if (button == AlternativeMouseButton.LEFT_BUTTON)
			sendMouseEvent(MOUSEEVENTF_LEFTDOWN, 0);
		else if (button == AlternativeMouseButton.MIDDLE_BUTTON)
			sendMouseEvent(MOUSEEVENTF_MIDDLEDOWN, 0);
		else
			sendMouseEvent(MOUSEEVENTF_RIGHTDOWN, 0);
	}
	
	public static void release(AlternativeMouseButton button) {
		if (button == AlternativeMouseButton.LEFT_BUTTON)
			sendMouseEvent(MOUSEEVENTF_LEFTUP, 0);
		else if (button == AlternativeMouseButton.MIDDLE_BUTTON)
			sendMouseEvent(MOUSEEVENTF_MIDDLEUP, 0);
		else
			sendMouseEvent(MOUSEEVENTF_RIGHTUP, 0);
	}
	
	public static void click(AlternativeMouseButton button) {
		press(button);
		AlternativeKeyPresser.delay(50);
		release(button);
		AlternativeKeyPresser.delay(50);
	}
	
	public static void doubleClick(AlternativeMouseButton button) {
		for (int n = 0; n < 2; n++)
			click(button);
	}
	
	/** Scroll do mouse */
	public static void wheel(int value) {
		sendMouseEvent(MOUSEEVENTF_WHEEL, value * WHEEL_DELTA);
	}

	/** Envia evento genérico */
	private static void sendMouseEvent(int flag, int data) {
		INPUT input = new INPUT();
		input.type = INPUT_MOUSE;
		input.mi = new MOUSEINPUT();
		input.mi.dx = 0;
		input.mi.dy = 0;
		input.mi.mouseData = data;
		input.mi.dwFlags = flag;
		input.mi.time = 0;
		input.mi.dwExtraInfo = null;

		INPUT[] inputs = { input };
		User32.INSTANCE.SendInput(inputs.length, inputs, input.size());
	}
	
}
