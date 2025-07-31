package util;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.win32.W32APIOptions;

public class JNAUtils {

	private interface MyUser32 extends User32 {
		MyUser32 INSTANCE = Native.load("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);

		int SendInput(int nInputs, INPUT[] pInputs, int cbSize);

		short VkKeyScan(char ch);
	}

	private interface MyGDI32 extends GDI32 {
		MyGDI32 INSTANCE = Native.load("gdi32", MyGDI32.class, W32APIOptions.DEFAULT_OPTIONS);

		int GetPixel(WinDef.HDC hdc, int x, int y);
	}

	public static WinDef.RECT getCurrentWindowCoordinates() {
		WinDef.HWND hWnd = User32.INSTANCE.GetForegroundWindow();
		if (hWnd != null) {
			WinDef.RECT rect = new WinDef.RECT();
			User32.INSTANCE.GetWindowRect(hWnd, rect);
			return rect;
		}
		return null;
	}

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

	public static Point getCursorPos() {
		POINT point = new WinDef.POINT();
		User32.INSTANCE.GetCursorPos(point);
		return new Point(point.x, point.y);
	}

	public static void sendText(String text) {
		List<INPUT> inputsList = new ArrayList<>();

		for (char c : text.toCharArray()) {
			INPUT pressInput = new INPUT();
			pressInput.type = new DWORD(INPUT.INPUT_KEYBOARD);
			pressInput.input.setType("ki");
			pressInput.input.ki.wScan = new WinDef.WORD(c);
			pressInput.input.ki.wVk = new WinDef.WORD(0);
			pressInput.input.ki.dwFlags = new DWORD(WinUser.KEYBDINPUT.KEYEVENTF_UNICODE);
			inputsList.add(pressInput);

			INPUT releaseInput = new INPUT();
			releaseInput.type = new DWORD(INPUT.INPUT_KEYBOARD);
			releaseInput.input.setType("ki");
			releaseInput.input.ki.wScan = new WinDef.WORD(c);
			releaseInput.input.ki.wVk = new WinDef.WORD(0);
			releaseInput.input.ki.dwFlags = new DWORD(WinUser.KEYBDINPUT.KEYEVENTF_UNICODE | WinUser.KEYBDINPUT.KEYEVENTF_KEYUP);
			inputsList.add(releaseInput);
		}

		if (!inputsList.isEmpty()) {
			INPUT[] inputsArray = (INPUT[]) new INPUT().toArray(inputsList.size());
			for (int i = 0; i < inputsList.size(); i++) {
				inputsArray[i].type = inputsList.get(i).type;
				inputsArray[i].input.setType("ki");
				inputsArray[i].input.ki.wVk = inputsList.get(i).input.ki.wVk;
				inputsArray[i].input.ki.wScan = inputsList.get(i).input.ki.wScan;
				inputsArray[i].input.ki.dwFlags = inputsList.get(i).input.ki.dwFlags;
			}
			MyUser32.INSTANCE.SendInput(inputsArray.length, inputsArray, inputsArray[0].size());
		}
	}

	public static Color getPixelColor(int x, int y) {
		WinDef.HDC hdc = MyUser32.INSTANCE.GetDC(null);
		if (hdc == null)
			throw new RuntimeException("Não foi possível obter o contexto de dispositivo da tela.");
		try {
			int colorRef = MyGDI32.INSTANCE.GetPixel(hdc, x, y);
			if (colorRef == -1)
				throw new RuntimeException("Não foi possível obter a cor do pixel.");
			int b = (colorRef >> 16) & 0xFF;
			int g = (colorRef >> 8) & 0xFF;
			int r = colorRef & 0xFF;
			return new Color(r, g, b);
		}
		finally {
			MyUser32.INSTANCE.ReleaseDC(null, hdc);
		}
	}

}
