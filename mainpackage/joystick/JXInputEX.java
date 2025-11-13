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

import enums.JXInputEXButton;
import enums.JXInputEXComponent;
import util.Pair;
import util.Timer;

public class JXInputEX {

	/**
	 * Como usar:
	 * 
	 * Inicie a aplicação chamando o método refreshJoysticks();
	 * 
	 * Se quizer ler as ações do joystick normalmente, use os eventos
	 * que não contém "Component" no nome.
	 * Os eventos com "Component" no nome tratam todos os controles
	 * analógicos como digitais (cada direção em um stick analógico é
	 * disparado como um botão único)
	 */

	private static boolean mainClose = false;
	private static List<JXInputEX> devices = new ArrayList<>();
	
	private final static Map<XInputButton, Pair<JXInputEXButton, JXInputEXComponent>> buttonList = new HashMap<>() {{
		put(XInputButton.A, new Pair<>(JXInputEXButton.BUTTON_A, JXInputEXComponent.BUTTON_A));
		put(XInputButton.B, new Pair<>(JXInputEXButton.BUTTON_B, JXInputEXComponent.BUTTON_B));
		put(XInputButton.X, new Pair<>(JXInputEXButton.BUTTON_X, JXInputEXComponent.BUTTON_X));
		put(XInputButton.Y, new Pair<>(JXInputEXButton.BUTTON_Y, JXInputEXComponent.BUTTON_Y));
		put(XInputButton.LEFT_SHOULDER, new Pair<>(JXInputEXButton.BUTTON_LB, JXInputEXComponent.BUTTON_LB));
		put(XInputButton.RIGHT_SHOULDER, new Pair<>(JXInputEXButton.BUTTON_RB, JXInputEXComponent.BUTTON_RB));
		put(XInputButton.LEFT_THUMBSTICK, new Pair<>(JXInputEXButton.BUTTON_LS, JXInputEXComponent.BUTTON_LS));
		put(XInputButton.RIGHT_THUMBSTICK, new Pair<>(JXInputEXButton.BUTTON_RS, JXInputEXComponent.BUTTON_RS));
		put(XInputButton.BACK, new Pair<>(JXInputEXButton.BUTTON_BACK, JXInputEXComponent.BUTTON_BACK));
		put(XInputButton.START, new Pair<>(JXInputEXButton.BUTTON_START, JXInputEXComponent.BUTTON_START));
		put(XInputButton.GUIDE_BUTTON, new Pair<>(JXInputEXButton.BUTTON_GUIDE, JXInputEXComponent.BUTTON_GUIDE));
		put(XInputButton.DPAD_LEFT, new Pair<>(JXInputEXButton.DPAD_LEFT, JXInputEXComponent.DPAD_LEFT));
		put(XInputButton.DPAD_UP, new Pair<>(JXInputEXButton.DPAD_UP, JXInputEXComponent.DPAD_UP));
		put(XInputButton.DPAD_RIGHT, new Pair<>(JXInputEXButton.DPAD_RIGHT, JXInputEXComponent.DPAD_RIGHT));
		put(XInputButton.DPAD_DOWN, new Pair<>(JXInputEXButton.DPAD_DOWN, JXInputEXComponent.DPAD_DOWN));
	}};
	
	private static JXInputEXButton[] analogComponents = {
		JXInputEXButton.LEFT_TRIGGER,
		JXInputEXButton.RIGHT_TRIGGER,
		JXInputEXButton.LEFT_AXIS_X,
		JXInputEXButton.LEFT_AXIS_Y,
		JXInputEXButton.RIGHT_AXIS_X,
		JXInputEXButton.RIGHT_AXIS_Y,
		JXInputEXButton.LEFT_AXIS_X,
		JXInputEXButton.LEFT_AXIS_Y,
		JXInputEXButton.RIGHT_AXIS_X,
		JXInputEXButton.RIGHT_AXIS_Y
	};
	private static JXInputEXComponent[] analogComponentsB = {
			JXInputEXComponent.LEFT_TRIGGER,
			JXInputEXComponent.RIGHT_TRIGGER,
			JXInputEXComponent.LEFT_AXIS_LEFT,
			JXInputEXComponent.LEFT_AXIS_DOWN,
			JXInputEXComponent.RIGHT_AXIS_LEFT,
			JXInputEXComponent.RIGHT_AXIS_DOWN,
			JXInputEXComponent.LEFT_AXIS_RIGHT,
			JXInputEXComponent.LEFT_AXIS_UP,
			JXInputEXComponent.RIGHT_AXIS_RIGHT,
			JXInputEXComponent.RIGHT_AXIS_UP
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
	private Consumer<JXInputEXButton> onPressButtonEvent;
	private BiConsumer<JXInputEXButton, Integer> onReleaseButtonEvent;
	private BiConsumer<JXInputEXButton, Pair<Float, Float>> onTriggerChangeEvent;
	private BiConsumer<JXInputEXButton, Pair<Float, Float>> onAxisChangeEvent;
	private Consumer<JXInputEXComponent> onPressAnyComponentEvent;
	private BiConsumer<JXInputEXComponent, Integer> onReleaseAnyComponentEvent;
	private List<Float> axesValues;
	private List<Float> deltaAxesValues;
	private Map<JXInputEXButton, Long> isHoldButton;
	private Map<JXInputEXComponent, Long> isHoldComponent;

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

	public void setOnPressButtonEvent(Consumer<JXInputEXButton> consumer) {
		onPressButtonEvent = consumer;
	}

	public void setOnReleaseButtonEvent(BiConsumer<JXInputEXButton, Integer> biConsumer) {
		onReleaseButtonEvent = biConsumer;
	}

	public void setOnTriggerChangeEvent(BiConsumer<JXInputEXButton, Pair<Float, Float>> biConsumer) {
		onTriggerChangeEvent = biConsumer;
	}

	public void setOnAxisChangeEvent(BiConsumer<JXInputEXButton, Pair<Float, Float>> biConsumer) {
		onAxisChangeEvent = biConsumer;
	}

	public void setOnPressAnyComponentEvent(Consumer<JXInputEXComponent> consumer) {
		onPressAnyComponentEvent = consumer;
	}

	public void setOnReleaseAnyComponentEvent(BiConsumer<JXInputEXComponent, Integer> biConsumer) {
		onReleaseAnyComponentEvent = biConsumer;
	}

	private JXInputEX(final XInputDevice14 device) {
		if (device == null)
			throw new RuntimeException("'device' value is null");
		try {
			this.device = device;
			isHoldComponent = new HashMap<>();
			isHoldButton = new HashMap<>();
			components = device.getComponents();
			axes = components.getAxes();
			axesValues = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f));
			deltaAxesValues = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f));
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
					for (XInputButton x : buttonList.keySet())
						if (button == x) {
							onComponentChanged(buttonList.get(x).getKey(), pressed ? 0 : 1, pressed ? 1 : 0);
							onComponentChanged(buttonList.get(x).getValue(), pressed ? 0 : 1, pressed ? 1 : 0);
							break;
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
		for (int i = 0; i < 6; i++) {
			float value = axesValues.get(i);
			float delta = deltaAxesValues.get(i);
			if (value != delta) {
				int v = i < 2 || value < 0 || delta < 0 ? i : i + 4; 
				JXInputEXButton button = analogComponents[v];
				JXInputEXComponent comp = analogComponentsB[v];
				if (i > 2 && i % 2 != 0) {
					delta = -delta;
					value = -value;
				}
				onComponentChanged(button, delta, value);
				onComponentChanged(comp, Math.abs(delta), Math.abs(value));
			}
		}
	}

	private void onComponentChanged(JXInputEXComponent button, float deltaValue, float value) {
		if (value >= 0.5 && deltaValue < 0.5) {
			isHoldComponent.put(button, System.currentTimeMillis());
			if (onPressAnyComponentEvent != null)
				onPressAnyComponentEvent.accept(button);
		}
		else if (value < 0.5 && deltaValue >= 0.5) {
			if (onReleaseAnyComponentEvent != null)
				onReleaseAnyComponentEvent.accept(button, getHoldTime(button));
			isHoldComponent.remove(button);
		}
	}
	
	private void onComponentChanged(JXInputEXButton button, float deltaValue, float value) {
		if (button.isAxis()) {
			if (onAxisChangeEvent != null)
				onAxisChangeEvent.accept(button, new Pair<Float, Float>(deltaValue, value));
		}
		else if (button.isTrigger()) {
			if (onTriggerChangeEvent != null)
				onTriggerChangeEvent.accept(button, new Pair<Float, Float>(deltaValue, value));
		}
		else if (value >= 0.5 && deltaValue < 0.5) {
			isHoldButton.put(button, System.currentTimeMillis());
			if (onPressButtonEvent != null)
				onPressButtonEvent.accept(button);
		}
		else if (value < 0.5 && deltaValue >= 0.5) {
			if (onReleaseButtonEvent != null)
				onReleaseButtonEvent.accept(button, getHoldTime(button));
			isHoldButton.remove(button);
		}
	}
	
	public boolean buttonIsHold(JXInputEXButton button) {
		return isHoldButton.containsKey(button);
	}

	public boolean buttonIsHold(JXInputEXComponent button) {
		return isHoldComponent.containsKey(button);
	}

	public int getHoldTime(JXInputEXButton button) {
		return !buttonIsHold(button) ? -1 : (int)(System.currentTimeMillis() - isHoldButton.get(button));
	}

	public int getHoldTime(JXInputEXComponent button) {
		return !buttonIsHold(button) ? -1 : (int)(System.currentTimeMillis() - isHoldComponent.get(button));
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
