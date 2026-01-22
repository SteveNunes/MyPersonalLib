package util;

import java.awt.Color;
import java.awt.Point;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

public class JNAUtils {

	private static final User32 INSTANCE_USER32 = User32.INSTANCE;
	private static final Advapi32 INSTANCE_ADVAPI32 = Advapi32.INSTANCE;

	public static final int EWX_LOGOFF = 0x00000000;
	public static final int EWX_SHUTDOWN = 0x00000001;
	public static final int EWX_REBOOT = 0x00000002;
	public static final int EWX_FORCE = 0x00000004;
	public static final int TOKEN_ADJUST_PRIVILEGES = 0x0020;
	public static final int TOKEN_QUERY = 0x0008;
	
	private static Thread JNATimersThread;
	private static List<Consumer<String>> onChangeWindowEvents;
	private static List<Consumer<RECT>> onMoveWindowEvents;
	private static List<Consumer<String>> onClipboardChangeEvents;
	
	private static String previousWindowName;
	private static RECT previewWindowRect;
	private static String previousClipboardText;
	
	public static void addOnChangeWindowEvent(Consumer<String> consumer) {
		if (onChangeWindowEvents == null)
			onChangeWindowEvents = new ArrayList<>();
		onChangeWindowEvents.add(consumer);
		runTimerEventsThread();
	}
	
	public static void addOnMoveWindowEvent(Consumer<RECT> consumer) {
		if (onMoveWindowEvents == null)
			onMoveWindowEvents = new ArrayList<>();
		onMoveWindowEvents.add(consumer);
		runTimerEventsThread();
	}
	
	public static void addOnClipboardChangeEvent(Consumer<String> consumer) {
		if (onClipboardChangeEvents == null)
			onClipboardChangeEvents = new ArrayList<>();
		onClipboardChangeEvents.add(consumer);
		runTimerEventsThread();
	}
	
	private static void runTimerEventsThread() {
		if (JNATimersThread == null) {
			JNATimersThread = new Thread(() -> {
				while (true) {
					if (onChangeWindowEvents != null) {
						String window = getActiveWindowTitle();
						if (previousWindowName == null || !previousWindowName.equals(window)) {
							String finalWindow = window.isBlank() ? "No active windows found" : window;
							for (Consumer<String> consumer : onChangeWindowEvents)
								consumer.accept(finalWindow);
							previousWindowName = window;
						}
					}
					if (onMoveWindowEvents != null) {
						RECT rect = getCurrentWindowCoordinates();
						if (rect == null || previewWindowRect == null ||
								previewWindowRect.bottom != rect.bottom || 
								previewWindowRect.left != rect.left || 
								previewWindowRect.right != rect.right || 
								previewWindowRect.top != rect.top) {
									for (Consumer<RECT> consumer : onMoveWindowEvents)
										consumer.accept(rect);
									previewWindowRect = rect;
						}
					}
					if (onClipboardChangeEvents != null) {
						String text = readFromClipboard();
						if (text != null && (previousClipboardText == null || !previousClipboardText.equals(text))) {
							for (Consumer<String> consumer : onClipboardChangeEvents)
								consumer.accept(text);
							previousClipboardText = text;
						}
					}
					try {
						Thread.sleep(5);
					}
					catch (InterruptedException ex) {
						return;
					}
				}
			});
			JNATimersThread.start();
			Misc.addShutdownEvent(() -> JNATimersThread.interrupt());
		}
	}

	private interface MyUser32 extends User32 {
		MyUser32 INSTANCE = Native.load("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);

		int SendInput(int nInputs, INPUT[] pInputs, int cbSize);

		BOOL ExitWindowsEx(WinDef.UINT uFlags, WinDef.DWORD dwReason);

		WinDef.BOOL LockWorkStation();
	}

	private interface MyGDI32 extends GDI32 {
		MyGDI32 INSTANCE = Native.load("gdi32", MyGDI32.class, W32APIOptions.DEFAULT_OPTIONS);

		int GetPixel(WinDef.HDC hdc, int x, int y);
	}

	private interface MyAdvapi32 extends Advapi32 {
		MyAdvapi32 INSTANCE = Native.load("advapi32", MyAdvapi32.class, W32APIOptions.UNICODE_OPTIONS);

		int RegOpenKeyExW(WinReg.HKEY hKey, String lpSubKey, int ulOptions, int samDesired, WinReg.HKEYByReference phkResult);

		int RegQueryValueExW(WinReg.HKEY hKey, String lpValueName, IntByReference lpReserved, IntByReference lpType, byte[] lpData, IntByReference lpcbData);

		int RegSetValueExW(WinReg.HKEY hKey, String lpValueName, int Reserved, int dwType, byte[] lpData, int cbData);

		int RegCreateKeyExW(WinReg.HKEY hKey, String lpSubKey, int Reserved, String lpClass, int dwOptions, int samDesired, WinBase.SECURITY_ATTRIBUTES lpSecurityAttributes, WinReg.HKEYByReference phkResult, IntByReference lpdwDisposition);

		boolean AdjustTokenPrivileges(WinNT.HANDLE TokenHandle, boolean DisableAllPrivileges, WinNT.TOKEN_PRIVILEGES NewState, int BufferLength, WinNT.TOKEN_PRIVILEGES PreviousState, WinDef.DWORDByReference ReturnLength);

		boolean LookupPrivilegeValue(String lpSystemName, String lpName, WinNT.LUID lpLuid);

		boolean OpenProcessToken(WinNT.HANDLE ProcessHandle, int DesiredAccess, WinNT.HANDLEByReference TokenHandle); // Adicionado para resolver o erro
	}

	public static RECT getCurrentWindowCoordinates() {
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

	public static String readRegistryValue(WinReg.HKEY hkey, String subKey, String valueName) {
		WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
		String result = null;
		int ret = MyAdvapi32.INSTANCE.RegOpenKeyExW(hkey, subKey, 0, WinNT.KEY_READ, phkResult);
		if (ret != WinError.ERROR_SUCCESS)
			return null;
		WinReg.HKEY key = phkResult.getValue();
		try {
			IntByReference lpType = new IntByReference();
			IntByReference lpcbData = new IntByReference();
			ret = MyAdvapi32.INSTANCE.RegQueryValueExW(key, valueName, null, lpType, (byte[]) null, lpcbData);
			if (ret != WinError.ERROR_SUCCESS)
				return null;
			byte[] buffer = new byte[lpcbData.getValue()];
			ret = MyAdvapi32.INSTANCE.RegQueryValueExW(key, valueName, null, lpType, buffer, lpcbData);
			if (ret == WinError.ERROR_SUCCESS) {
				try {
					result = new String(buffer, "UTF-16LE").trim();
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		finally {
			MyAdvapi32.INSTANCE.RegCloseKey(key);
		}

		return result;
	}

	public static boolean writeRegistryValue(WinReg.HKEY hkey, String subKey, String valueName, String value) {
		WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
		int ret = MyAdvapi32.INSTANCE.RegCreateKeyExW(hkey, subKey, 0, null, 0, WinNT.KEY_SET_VALUE, null, phkResult, null);
		if (ret != WinError.ERROR_SUCCESS) {
			System.err.println("Erro ao criar/abrir a chave para escrita. Código: " + ret);
			return false;
		}
		WinReg.HKEY key = phkResult.getValue();
		try {
			byte[] data = value.getBytes("UTF-16LE");
			ret = MyAdvapi32.INSTANCE.RegSetValueExW(key, valueName, 0, 1, data, data.length);
			return ret == WinError.ERROR_SUCCESS;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			MyAdvapi32.INSTANCE.RegCloseKey(key);
		}
	}

	public static String getOpenedWindow(String windowName) {
		List<String> list = getOpenedWindowsList();
		if (list == null || windowName == null || windowName.trim().isBlank())
			return null;
		windowName = windowName.toLowerCase();
		for (String s : list)
			if (s.toLowerCase().contains(windowName))
				return s;
		return null;
	}

	public static String getOpenedWindowRegex(String regex) {
		List<String> list = getOpenedWindowsList();
		if (list == null || regex == null || regex.trim().isBlank())
			return null;
		for (String s : list)
			if (Pattern.compile(regex).matcher(s).find())
				return s;
		return null;
	}

	public static List<String> getOpenedWindowsList() {
		List<String> list = new ArrayList<>();
		MyUser32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
			@Override
			public boolean callback(WinDef.HWND hWnd, Pointer data) {
				if (MyUser32.INSTANCE.IsWindowVisible(hWnd) && MyUser32.INSTANCE.GetWindowTextLength(hWnd) > 0) {
					char[] titleBuffer = new char[1024];
					MyUser32.INSTANCE.GetWindowText(hWnd, titleBuffer, 1024);
					String windowTitle = Native.toString(titleBuffer).trim();
					if (windowTitle.equals("Experiência de Entrada do Windows") || windowTitle.equals("Program Manager"))
						return true;
					char[] classNameBuffer = new char[256];
					MyUser32.INSTANCE.GetClassName(hWnd, classNameBuffer, 256);
					list.add(windowTitle);
				}
				return true;
			}
		}, null);
		return list.isEmpty() ? null : list;
	}

	public static boolean maximizeWindow(String windowTitle) {
		final int SW_MAXIMIZE = 3;
		User32.INSTANCE.FindWindow(null, windowTitle);
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
		if (hWnd != null) {
			User32.INSTANCE.ShowWindow(hWnd, SW_MAXIMIZE);
			return true;
		}
		return false;
	}

	public static boolean minimizeWindow(String windowTitle) {
		final int SW_MINIMIZE = 6;
		User32.INSTANCE.FindWindow(null, windowTitle);
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
		if (hWnd != null) {
			User32.INSTANCE.ShowWindow(hWnd, SW_MINIMIZE);
			return true;
		}
		return false;
	}

	public static boolean closeWindow(String windowTitle) {
		final int WM_CLOSE = 0x0010;
		User32.INSTANCE.FindWindow(null, windowTitle);
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
		if (hWnd != null) {
			User32.INSTANCE.PostMessage(hWnd, WM_CLOSE, null, null);
			return true;
		}
		return false;
	}

	public static boolean isWindowMinimized(String windowTitle) {
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
		if (hWnd == null)
			return false;
		WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
		User32.INSTANCE.GetWindowPlacement(hWnd, placement);
		return placement.showCmd == WinUser.SW_SHOWMINIMIZED;
	}

	public static boolean setWindowsFocused(String windowTitle) {
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
		if (hWnd != null) {
			WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
			User32.INSTANCE.GetWindowPlacement(hWnd, placement);
			if (placement.showCmd == WinUser.SW_SHOWMINIMIZED) {
				final int SW_RESTORE = 9;
				User32.INSTANCE.ShowWindow(hWnd, SW_RESTORE);
			}
			User32.INSTANCE.SetForegroundWindow(hWnd);
			return true;
		}
		return false;
	}

	private static boolean enableShutdownPrivilege() {
		WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
		if (!INSTANCE_ADVAPI32.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, hToken)) {
			System.err.println("Não foi possível abrir o token do processo.");
			return false;
		}
		WinNT.TOKEN_PRIVILEGES tkp = new WinNT.TOKEN_PRIVILEGES(1);
		tkp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES();
		if (!MyAdvapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_SHUTDOWN_NAME, tkp.Privileges[0].Luid)) {
			System.err.println("Não foi possível encontrar o valor do privilégio.");
			Kernel32.INSTANCE.CloseHandle(hToken.getValue());
			return false;
		}
		tkp.Privileges[0].Attributes = new WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED);
		boolean success = INSTANCE_ADVAPI32.AdjustTokenPrivileges(hToken.getValue(), false, tkp, 0, null, null);
		Kernel32.INSTANCE.CloseHandle(hToken.getValue());
		return success;
	}

	public static void systemShutdown() {
		if (enableShutdownPrivilege())
			INSTANCE_USER32.ExitWindowsEx(new WinDef.UINT(EWX_SHUTDOWN | EWX_FORCE), new WinDef.DWORD(0));
		else
			System.err.println("Falha ao habilitar privilégio de desligamento. Execute como administrador.");
	}

	public static void systemReboot() {
		if (enableShutdownPrivilege())
			INSTANCE_USER32.ExitWindowsEx(new WinDef.UINT(EWX_REBOOT | EWX_FORCE), new WinDef.DWORD(0));
		else
			System.err.println("Falha ao habilitar privilégio de desligamento. Execute como administrador.");
	}

	public static void systemLogOff() {
		INSTANCE_USER32.ExitWindowsEx(new WinDef.UINT(EWX_LOGOFF | EWX_FORCE), new WinDef.DWORD(0));
	}

	public static void systemLockWorkstation() {
		INSTANCE_USER32.LockWorkStation();
	}

	private interface MyUser32Clipboard extends User32 {
		MyUser32Clipboard INSTANCE = Native.load("user32", MyUser32Clipboard.class, W32APIOptions.DEFAULT_OPTIONS);

		boolean OpenClipboard(WinDef.HWND hWndNewOwner);

		boolean CloseClipboard();

		boolean EmptyClipboard();

		WinNT.HANDLE GetClipboardData(int uFormat);

		WinNT.HANDLE SetClipboardData(int uFormat, WinNT.HANDLE hMem);
	}

	private interface MyKernel32Clipboard extends Kernel32 {
		MyKernel32Clipboard INSTANCE = Native.load("kernel32", MyKernel32Clipboard.class, W32APIOptions.DEFAULT_OPTIONS);

		int GMEM_MOVEABLE = 0x0002;

		WinNT.HANDLE GlobalAlloc(int uFlags, int dwBytes);

		Pointer GlobalLock(WinNT.HANDLE hMem);

		boolean GlobalUnlock(WinNT.HANDLE hMem);

		WinNT.HANDLE GlobalFree(WinNT.HANDLE hMem);
	}

	public static boolean copyToClipboard(String text) {
		if (text == null)
			return false;

		byte[] data = text.getBytes(java.nio.charset.StandardCharsets.UTF_16LE);
		int size = data.length + 2;

		while (!MyUser32Clipboard.INSTANCE.OpenClipboard(null))
			Misc.sleep(1);

		try {
			MyUser32Clipboard.INSTANCE.EmptyClipboard();
			WinNT.HANDLE hMem = null;
			Pointer ptr = null;

			while (true) {
				hMem = MyKernel32Clipboard.INSTANCE.GlobalAlloc(MyKernel32Clipboard.GMEM_MOVEABLE, size);
				if (hMem != null) {
					ptr = MyKernel32Clipboard.INSTANCE.GlobalLock(hMem);
					if (ptr != null)
						break;
					MyKernel32Clipboard.INSTANCE.GlobalFree(hMem);
				}
				Misc.sleep(1);
			}

			ptr.write(0, data, 0, data.length);
			ptr.setChar(data.length, '\0');

			MyKernel32Clipboard.INSTANCE.GlobalUnlock(hMem);

			while (MyUser32Clipboard.INSTANCE.SetClipboardData(13, hMem) == null)
				Misc.sleep(1);

			return true;
		}
		finally {
			MyUser32Clipboard.INSTANCE.CloseClipboard();
		}
	}

	public static String readFromClipboard() {
		final int MAX_ATTEMPTS = 40; // tenta por ~400 ms
		final int DELAY_MS = 10;
		for (int i = 0; i < MAX_ATTEMPTS; i++) {
			String result = tryReadClipboardOnce();
			if (result != null)
				return result;
			try {
				Thread.sleep(DELAY_MS);
			}
			catch (InterruptedException ignored) {}
		}
		return null;
	}

	private static String tryReadClipboardOnce() {
		if (!MyUser32Clipboard.INSTANCE.OpenClipboard(null))
			return null;
		try {
			WinNT.HANDLE handle = MyUser32Clipboard.INSTANCE.GetClipboardData(13); // CF_UNICODETEXT
			if (handle == null)
				return null;
			Pointer ptr = MyKernel32Clipboard.INSTANCE.GlobalLock(handle);
			if (ptr == null)
				return null;
			try {
				return ptr.getWideString(0);
			}
			finally {
				MyKernel32Clipboard.INSTANCE.GlobalUnlock(handle);
			}
		}
		finally {
			MyUser32Clipboard.INSTANCE.CloseClipboard();
		}
	}
	
}