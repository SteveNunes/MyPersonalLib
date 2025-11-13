package joystick;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import enums.JXInputEXComponent;
import sockets.SocketClient;
import util.DesktopUtils;
import util.Misc;

public class XInputRobot {
	
	private static Set<Integer> joystickIds = new HashSet<>();
	private static boolean serverIsRunning = false;
	
	private SocketClient socket;
	private int joyID;
	
	public static void initJoystickServer() {
		if (serverIsRunning)
			throw new RuntimeException("Joystick Server is already running");
		String[] files = {"VirtualGamePadServer.exe", "Nefarius.ViGEm.Client.dll"};
		for (String file : files)
			if (!new File(file).exists())
				throw new RuntimeException("Não foi possível encontrar o arquivo \"" + file + "\" na pasta da aplicação.");
		if (!DesktopUtils.processExists("VirtualGamePadServer.exe")) {
			try {
				new ProcessBuilder(".\\VirtualGamePadServer.exe").start();
				Misc.addShutdownEvent(() -> {
					if (DesktopUtils.processExists("VirtualGamePadServer.exe"))
						DesktopUtils.taskKillByName("VirtualGamePadServer.exe");
				});
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		serverIsRunning = true;
	}

	public XInputRobot() {
		joyID = -1;
	}
	
	public void connect(Consumer<XInputRobot> onConnectEvent, Consumer<XInputRobot> onConnectErrorEvent) {
		if (socket != null)
			throw new RuntimeException("This joystick is already connected or under connection process");
		socket = new SocketClient();
		socket.connect("localhost", 3333);
		socket.setOnConnectEvent(c -> {
			for (joyID = 0; joystickIds.contains(joyID); joyID++);
			joystickIds.add(joyID);
			onConnectEvent.accept(this);
		});
		socket.setOnConnectErrorEvent(c -> onConnectErrorEvent.accept(this));
	}
	
	public void disconnect() {
		if (socket == null)
			throw new RuntimeException("This joystick is already disconnected");
		try {
			socket.disconnect();
		}
		catch (Exception e) {}
		socket = null;
		joystickIds.remove(joyID);
		joyID = -1;
	}
	
	private void validate() {
		if (!serverIsRunning)
			throw new RuntimeException("Joystick Server is not running. Call initJoystickServer() first.");
		if (socket == null)
			throw new RuntimeException("This joystick is disconnected. Call connect() first.");
	}
	
	public int getJoyID() {
		validate();
		return joyID;
	}
	
	public void pressButton(JXInputEXComponent button) {
		validate();
		try {
			holdButton(button);
			Thread.sleep(25);
		}
		catch (InterruptedException e) {}
		releaseButton(button);
	}

	public void holdButton(JXInputEXComponent component) {
		validate();
		socket.sendData(component.getShortName() + " 1");
	}

	public void releaseButton(JXInputEXComponent component) {
		validate();
		socket.sendData(component.getShortName() + " 0");
	}

	public void setLeftAxisValues(int x, int y) {
		validate();
		x = getAxisValueFromPercent(x);
		y = getAxisValueFromPercent(-y);
		socket.sendData("LV " + x + " " + y);
	}
	
	public void setLeftAxisXValue(int value) {
		validate();
		value = getAxisValueFromPercent(value);
		socket.sendData("LX " + value);
	}
	
	public void setLeftAxisYValue(int value) {
		validate();
		value = getAxisValueFromPercent(-value);
		socket.sendData("LY " + value);
	}
	
	public void setRightAxisValues(int x, int y) {
		validate();
		x = getAxisValueFromPercent(x);
		y = getAxisValueFromPercent(-y);
		socket.sendData("RV " + x + " " + y);
	}
	
	public void setRightAxisXValue(int value) {
		validate();
		value = getAxisValueFromPercent(value);
		socket.sendData("RX " + value);
	}
	
	public void setRightAxisYValue(int value) {
		validate();
		value = getAxisValueFromPercent(-value);
		socket.sendData("RY " + value);
	}
	
	public void setLeftTriggerValue(int value) {
		validate();
		value = getTriggerValueFromPercent(value);
		socket.sendData("LT " + value);
	}
	
	public void setRightTriggerValue(int value) {
		validate();
		value = getTriggerValueFromPercent(value);
		socket.sendData("RT " + value);
	}
	
	private int getAxisValueFromPercent(float percent) {
		if (percent < -100 || percent > 100)
			throw new RuntimeException("Axis value must be a percentage between -100 and 100");
		int v = percent < 0 ? -32768 : 32767;
		return (int)(v * Math.abs(percent) / 100d);
	}
	
	private int getTriggerValueFromPercent(float percent) {
		if (percent < 0 || percent > 100)
			throw new RuntimeException("Trigger value must be a percentage between 0 and 100");
		return (int)(255 * percent / 100d);
	}
	
	public SocketClient getSocket() {
		return socket;
	}

}
