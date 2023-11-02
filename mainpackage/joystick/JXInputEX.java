package joystick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.listener.SimpleXInputDeviceListener;
import com.github.strikerx3.jxinput.listener.XInputDeviceListener;

import util.Misc;

public class JXInputEX {
	
	private static List<JXInputEX> devices = new ArrayList<>();
	private final static int startID = 15; // Primeiro ID dos analogicos convertidos em digital
	private final static XInputButton[] buttonList = {
			XInputButton.A,	
			XInputButton.B,	
			XInputButton.X,	
			XInputButton.Y,
			XInputButton.LEFT_SHOULDER,	
			XInputButton.RIGHT_SHOULDER,
			XInputButton.LEFT_THUMBSTICK,
			XInputButton.RIGHT_THUMBSTICK,
			XInputButton.BACK,
			XInputButton.START,
			XInputButton.GUIDE_BUTTON,
			XInputButton.DPAD_LEFT,
			XInputButton.DPAD_UP,
			XInputButton.DPAD_RIGHT,
			XInputButton.DPAD_DOWN,
	};
	
	private static Consumer<XInputDevice> onJoystickConnectedEvent;
	private static Consumer<XInputDevice> onJoystickDisconnectedEvent;

	private XInputDevice device;
	private XInputComponents components;
	private XInputAxes axes;
	private Consumer<Integer> onPressButtonEvent;
	private BiConsumer<Integer, Long> onHoldButtonEvent;
	private BiConsumer<Integer, Long> onReleaseButtonEvent;
	private BiConsumer<Integer, Float> onTriggerChangeEvent;
	private BiConsumer<Integer, Float> onAxisChangeEvent;
	private Consumer<Integer> onPressAnyComponentEvent;
	private BiConsumer<Integer, Long> onHoldAnyComponentEvent;
	private BiConsumer<Integer, Long> onReleaseAnyComponentEvent;
	private List<Float> axesValues;
	private List<Float> deltaAxesValues;
	private Map<Integer, Long> isHold;
	
	public static void setOnJoystickConnectedEvent(Consumer<XInputDevice> consumer)
		{	onJoystickConnectedEvent = consumer; }

	public static void setOnJoystickDisconnectedEvent(Consumer<XInputDevice> consumer)
		{	onJoystickDisconnectedEvent = consumer; }

	public void setOnHoldButtonEvent(BiConsumer<Integer, Long> biConsumer)
		{	onHoldButtonEvent = biConsumer; }

	public void setOnReleaseButtonEvent(BiConsumer<Integer, Long> biConsumer)
		{	onReleaseButtonEvent = biConsumer; }

	public void setOnTriggerChangeEvent(BiConsumer<Integer, Float> biConsumer)
		{	onTriggerChangeEvent = biConsumer; }

	public void setOnAxisChangeEvent(BiConsumer<Integer, Float> biConsumer)
		{	onAxisChangeEvent = biConsumer; }

	public void setOnPressAnyComponentEvent(Consumer<Integer> consumer)
		{	onPressAnyComponentEvent = consumer; }

	public void setOnHoldAnyComponentEvent(BiConsumer<Integer, Long> biConsumer)
		{	onHoldAnyComponentEvent = biConsumer; }

	public void setOnReleaseAnyComponentEvent(BiConsumer<Integer, Long> biConsumer)
		{	onReleaseAnyComponentEvent = biConsumer; }
	
	private JXInputEX(final XInputDevice device) {
		if (device == null)
			throw new RuntimeException("'device' value is null");
		try {
			this.device = device;
			components = device.getComponents();
			axes = components.getAxes();
			axesValues = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f));
			deltaAxesValues = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f));
			isHold = new HashMap<>();
			
			XInputDeviceListener listener = new SimpleXInputDeviceListener() {
				@Override
				public void connected() {
					if (onJoystickConnectedEvent != null)
						onJoystickConnectedEvent.accept(device);
				}
	
				@Override
				public void disconnected() {
					if (onJoystickDisconnectedEvent != null)
						onJoystickDisconnectedEvent.accept(device);
				}

				@Override
				public void buttonChanged(final XInputButton button, final boolean pressed) {
					for (int n = 0; n < buttonList.length; n++)
						if (button == buttonList[n]) {
							onButtonChanged(n, pressed ? 1 : 0);
							break;
						}
				}
			};
			device.addListener(listener);
		}
		catch (Exception e) {}
	}
	
	public static void refreshJoysticks() {
		if (XInputDevice.isAvailable()) {
			try {
				for (XInputDevice device : XInputDevice.getAllDevices()) {
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
	}
	
	public static JXInputEX getJoystick(int joystickID) {
		if (joystickID < 0 || joystickID > devices.size())
			throw new RuntimeException(joystickID + " - ID de joystick inválido (" + (devices.size() - 1));
		return devices.get(joystickID);
	}
	
	public static void pollJoysticks() {
		for (JXInputEX device : devices)
			device.poll();
	}

	public void poll() {
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
			if (delta != value) {
				int buttonID = startID + 2 * i;
				if (value > 0 || delta > 0)
					onButtonChanged(buttonID + 1, Math.abs(delta <= 0 ? 0 : delta), Math.abs(value));
				if (value < 0 || delta < 0)
					onButtonChanged(buttonID, Math.abs(delta >= 0 ? 0 : delta), Math.abs(value));
			}
		}
		for (Integer i : isHold.keySet()) {
			if (onHoldButtonEvent != null)
				onHoldButtonEvent.accept(i, isHold.get(i));
			if (onHoldAnyComponentEvent != null)
				onHoldAnyComponentEvent.accept(i, isHold.get(i));
		}
			
	}
	
	public boolean isHold(int buttonID)
		{ return isHold.containsKey(buttonID); }
	
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

	private void onButtonChanged(int buttonID, Float deltaValue, float value) {
		if (buttonID > startID + 1 && onAxisChangeEvent != null) {
			addToIsHold(buttonID);
			onAxisChangeEvent.accept(buttonID, value);
		}
		if ((buttonID == startID || buttonID == startID + 1) && onTriggerChangeEvent != null) {
			addToIsHold(buttonID);
			onTriggerChangeEvent.accept(buttonID, value);
		}
		if (Math.abs(value) >= 0.5 && (deltaValue == null || Math.abs(deltaValue) < 0.5)) {
			if (addToIsHold(buttonID)) {
				if (onPressAnyComponentEvent != null)
					onPressAnyComponentEvent.accept(buttonID);
				if (onPressButtonEvent != null)
					onPressButtonEvent.accept(buttonID);
			}
		}
		else if (Math.abs(value) < 0.5 && (deltaValue == null || Math.abs(deltaValue) >= 0.5)) {
			if (onReleaseAnyComponentEvent != null)
				onReleaseAnyComponentEvent.accept(buttonID, getHoldTime(buttonID));
			if (onReleaseButtonEvent != null)
				onReleaseButtonEvent.accept(buttonID, getHoldTime(buttonID));
			isHold.remove(buttonID);
		}
	}
	
	private void onButtonChanged(int buttonID, float value)
		{ onButtonChanged(buttonID, null, value); }
	
	public XInputDevice getXInputDevice()
		{ return device; }
	
}
