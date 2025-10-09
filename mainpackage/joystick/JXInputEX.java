package joystick;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputBatteryInformation;
import com.github.strikerx3.jxinput.XInputCapabilities;
import com.github.strikerx3.jxinput.XInputCapsResolutions;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputBatteryDeviceType;
import com.github.strikerx3.jxinput.enums.XInputBatteryLevel;
import com.github.strikerx3.jxinput.enums.XInputBatteryType;
import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.enums.XInputDeviceSubType;
import com.github.strikerx3.jxinput.enums.XInputDeviceType;
import com.github.strikerx3.jxinput.listener.SimpleXInputDeviceListener;
import com.github.strikerx3.jxinput.listener.XInputDeviceListener;

import javafx.util.Pair;
import util.Timer;

public class JXInputEX {

	/**
	 * Como usar:
	 * 
	 * Defina os métodos setOnJoystickConnectedEvent() e
	 * setOnJoystickDisconnectedEvent()
	 * 
	 * Agora, chame o método refreshJoysticks() para gera a lista dos controles
	 * conectados NO MOMENTO. Atualmente, se novos controles se conectarem, a lista
	 * não é atualizada, sendo necessário rechamar o método refreshJoysticks() cada
	 * vez que um novo controle for conectado. Os eventos definidos no inicio só são
	 * aplicados aos controles que o evento refreshJoysticks() detectar.
	 * 
	 */

	private static boolean mainClose = false;
	private static List<JXInputEX> devices = new ArrayList<>();
	private final static int startID = 15; // Primeiro ID dos analogicos convertidos em digital
	private final static Map<XInputButton, String> buttonList = new HashMap<>() {
		{
			put(XInputButton.A, "A");
			put(XInputButton.B, "B");
			put(XInputButton.X, "X");
			put(XInputButton.Y, "Y");
			put(XInputButton.LEFT_SHOULDER, "Left Shoulder");
			put(XInputButton.RIGHT_SHOULDER, "Right Shoulder");
			put(XInputButton.LEFT_THUMBSTICK, "Left Alalogic");
			put(XInputButton.RIGHT_THUMBSTICK, "Right Analogic");
			put(XInputButton.BACK, "Select");
			put(XInputButton.START, "Start");
			put(XInputButton.GUIDE_BUTTON, "PS");
			put(XInputButton.DPAD_LEFT, "DPAD (Left)");
			put(XInputButton.DPAD_UP, "DPAD (Up)");
			put(XInputButton.DPAD_RIGHT, "DPAD (Right)");
			put(XInputButton.DPAD_DOWN, "DPAD (Down)");
		}
	};

	private static Consumer<JXInputEX> onJoystickConnectedEvent;
	private static Consumer<JXInputEX> onJoystickDisconnectedEvent;
	private static boolean autoPollIsRunning = false;
	private static int pollTimerId = 0;

	private XInputDevice14 device;
	private XInputComponents components;
	private XInputAxes axes;
	private XInputCapabilities caps;
	private XInputBatteryInformation batteryInfo;
	private XInputBatteryType batteryType;
	private XInputBatteryLevel batteryLevel;
	private BiConsumer<Integer, String> onPressButtonEvent;
	private BiConsumer<Pair<Integer, String>, Long> onHoldButtonEvent;
	private BiConsumer<Pair<Integer, String>, Long> onReleaseButtonEvent;
	private BiConsumer<Pair<Integer, String>, Float> onTriggerChangeEvent;
	private BiConsumer<Pair<Integer, String>, Float> onAxisChangeEvent;
	private BiConsumer<Integer, String> onPressAnyComponentEvent;
	private BiConsumer<Pair<Integer, String>, Long> onHoldAnyComponentEvent;
	private BiConsumer<Pair<Integer, String>, Long> onReleaseAnyComponentEvent;
	private List<Float> axesValues;
	private List<Float> deltaAxesValues;
	private Map<Integer, Long> isHold;

	public static void setOnJoystickConnectedEvent(Consumer<JXInputEX> consumer) {
		onJoystickConnectedEvent = consumer;
	}

	public static void setOnJoystickDisconnectedEvent(Consumer<JXInputEX> consumer) {
		onJoystickDisconnectedEvent = consumer;
	}

	public static void refreshJoysticks() {
		if (XInputDevice14.isAvailable()) {
			try {
				for (XInputDevice14 device : XInputDevice14.getAllDevices()) {
					JXInputEX d = new JXInputEX(device);
					devices.add(d);
				}
			}
			catch (Exception e) {
				devices = null;
				throw new RuntimeException("Unnable to find connected XInput joysticks\n" + e.getMessage());
			}
		}
		else
			throw new RuntimeException("XInput is not available on your system");
		startPollThread();
	}
	
	private static void startPollThread() {
		if (!autoPollIsRunning) {
			autoPollIsRunning = true;
			pollJoysticks();
		}
	}

	public static JXInputEX getJoystick(int joystickID) {
		if (devices == null)
			refreshJoysticks();
		startPollThread();
		if (joystickID < 0 || joystickID > devices.size())
			throw new RuntimeException(joystickID + " - ID de joystick inválido (" + (devices.size() - 1));
		return devices.get(joystickID);
	}

	public static List<JXInputEX> getJoystickList() {
		if (devices == null)
			refreshJoysticks();
		startPollThread();
		return Collections.unmodifiableList(devices);
	}

	private static void pollJoysticks() {
		for (JXInputEX device : devices)
			if (device.getXInputDevice().isConnected())
				device.poll();
		if (!mainClose)
			Timer.createTimer("pollJoysticks@" + pollTimerId++, Duration.ofMillis(1), JXInputEX::pollJoysticks);
	}

	public void setOnHoldButtonEvent(BiConsumer<Pair<Integer, String>, Long> biConsumer) {
		onHoldButtonEvent = biConsumer;
	}

	public void setOnReleaseButtonEvent(BiConsumer<Pair<Integer, String>, Long> biConsumer) {
		onReleaseButtonEvent = biConsumer;
	}

	public void setOnTriggerChangeEvent(BiConsumer<Pair<Integer, String>, Float> biConsumer) {
		onTriggerChangeEvent = biConsumer;
	}

	public void setOnAxisChangeEvent(BiConsumer<Pair<Integer, String>, Float> biConsumer) {
		onAxisChangeEvent = biConsumer;
	}

	public void setOnPressAnyComponentEvent(BiConsumer<Integer, String> consumer) {
		onPressAnyComponentEvent = consumer;
	}

	public void setOnHoldAnyComponentEvent(BiConsumer<Pair<Integer, String>, Long> biConsumer) {
		onHoldAnyComponentEvent = biConsumer;
	}

	public void setOnReleaseAnyComponentEvent(BiConsumer<Pair<Integer, String>, Long> biConsumer) {
		onReleaseAnyComponentEvent = biConsumer;
	}

	private JXInputEX(final XInputDevice14 device) {
		if (device == null)
			throw new RuntimeException("'device' value is null");
		try {
			this.device = device;
			components = device.getComponents();
			axes = components.getAxes();
			axesValues = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f));
			deltaAxesValues = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f));
			isHold = new HashMap<>();
			caps = device.getCapabilities();
			batteryInfo = device.getBatteryInformation(XInputBatteryDeviceType.GAMEPAD);
			batteryType = batteryInfo.getType();
			batteryLevel = batteryInfo.getLevel();

			final JXInputEX thisDevice = this;
			XInputDeviceListener listener = new SimpleXInputDeviceListener() {
				@Override
				public void connected() {
					if (onJoystickConnectedEvent != null)
						onJoystickConnectedEvent.accept(thisDevice);
				}

				@Override
				public void disconnected() {
					if (onJoystickDisconnectedEvent != null)
						onJoystickDisconnectedEvent.accept(thisDevice);
				}

				@Override
				public void buttonChanged(final XInputButton button, final boolean pressed) {
					int n = 0;
					for (XInputButton x : buttonList.keySet()) {
						if (button == x) {
							onButtonChanged(n, pressed ? 1 : 0, buttonList.get(x));
							break;
						}
						n++;
					}
				}
			};
			device.addListener(listener);
		}
		catch (Exception e) {}
	}

	private void poll() {
		for (int n = 0; n < 6; n++)
			deltaAxesValues.set(n, axesValues.get(n));
		device.poll();
		axesValues.set(0, axes.lt >= -0.0001 && axes.lt <= 0.0001 ? 0 : axes.lt);
		axesValues.set(1, axes.rt >= -0.0001 && axes.rt <= 0.0001 ? 0 : axes.rt);
		axesValues.set(2, axes.lx >= -0.0001 && axes.lx <= 0.0001 ? 0 : axes.lx);
		axesValues.set(3, axes.ly >= -0.0001 && axes.ly <= 0.0001 ? 0 : axes.ly);
		axesValues.set(4, axes.rx >= -0.0001 && axes.rx <= 0.0001 ? 0 : axes.rx);
		axesValues.set(5, axes.ry >= -0.0001 && axes.ry <= 0.0001 ? 0 : axes.ry);
		String[] names = { "Left Trigger", "Right Trigger", "Left Analogic X" + (axes.lx > 0 ? "+" : "-"), "Left Analogic Y" + (axes.ly > 0 ? "-" : "+"), "Right Analogic X" + (axes.rx > 0 ? "+" : "-"), "Right Analogic Y" + (axes.ry > 0 ? "-" : "+") };
		for (int i = 0; i < 6; i++) {
			float value = axesValues.get(i);
			float delta = deltaAxesValues.get(i);
			if (delta != value) {
				int buttonID = startID + 2 * i;
				if (value > 0 || delta > 0)
					onButtonChanged(buttonID + 1, Math.abs(delta <= 0 ? 0 : delta), Math.abs(value), names[i]);
				if (value < 0 || delta < 0)
					onButtonChanged(buttonID, Math.abs(delta >= 0 ? 0 : delta), Math.abs(value), names[i]);
			}
		}
		for (Integer i : isHold.keySet()) {
			if (onHoldButtonEvent != null)
				onHoldButtonEvent.accept(new Pair<>(i, ""), isHold.get(i));
			if (onHoldAnyComponentEvent != null)
				onHoldAnyComponentEvent.accept(new Pair<>(i, ""), isHold.get(i));
		}

	}

	public boolean isHold(int buttonID) {
		return isHold.containsKey(buttonID);
	}

	public long getHoldTime(int buttonID) {
		if (!isHold.containsKey(buttonID))
			return 0;
		return System.currentTimeMillis() - isHold.get(buttonID);
	}

	private boolean addToIsHold(int buttonID) {
		if (!isHold.containsKey(buttonID)) {
			isHold.put(buttonID, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	private void onButtonChanged(int buttonID, Float deltaValue, float value, String buttonName) {
		if (buttonID > startID + 1 && onAxisChangeEvent != null) {
			addToIsHold(buttonID);
			onAxisChangeEvent.accept(new Pair<>(buttonID, buttonName), value);
		}
		if ((buttonID == startID || buttonID == startID + 1) && onTriggerChangeEvent != null) {
			addToIsHold(buttonID);
			onTriggerChangeEvent.accept(new Pair<>(buttonID, buttonName), value);
		}
		if (Math.abs(value) >= 0.5 && (deltaValue == null || Math.abs(deltaValue) < 0.5)) {
			if (addToIsHold(buttonID)) {
				if (onPressAnyComponentEvent != null)
					onPressAnyComponentEvent.accept(buttonID, buttonName);
				if (onPressButtonEvent != null)
					onPressButtonEvent.accept(buttonID, buttonName);
			}
		}
		else if (Math.abs(value) < 0.5 && (deltaValue == null || Math.abs(deltaValue) >= 0.5)) {
			if (onReleaseAnyComponentEvent != null)
				onReleaseAnyComponentEvent.accept(new Pair<>(buttonID, buttonName), getHoldTime(buttonID));
			if (onReleaseButtonEvent != null)
				onReleaseButtonEvent.accept(new Pair<>(buttonID, buttonName), getHoldTime(buttonID));
			isHold.remove(buttonID);
		}
	}

	private void onButtonChanged(int buttonID, float value, String buttonName) {
		onButtonChanged(buttonID, null, value, buttonName);
	}

	public XInputDevice14 getXInputDevice() {
		return device;
	}

	public String getJoystickName() {
		return "XInput " + device.getPlayerNum();
	}

	public XInputBatteryType getBatteryType() {
		return batteryType;
	}

	public XInputBatteryLevel getBatteryLevel() {
		return batteryLevel;
	}

	public XInputDeviceType getType() {
		return caps.getType();
	}

	public XInputDeviceSubType getSubType() {
		return caps.getSubType();
	}

	public EnumSet<XInputButton> getSupportedButtons() {
		return caps.getSupportedButtons();
	}

	public XInputCapsResolutions getResolutions() {
		return caps.getResolutions();
	}

}
