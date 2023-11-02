package joystick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class JInputEX {
	
	private static List<JInputEX> joysticks = new ArrayList<>();
	private static String povDirNames[] = {"POV (UL)", "POV (U)", "POV (UR)", "POV (R)", "POV (DR)", "POV (D)", "POV (DL)", "POV (L)"};
	private static float povValues[] = {POV.CENTER, POV.UP_LEFT, POV.UP, POV.UP_RIGHT, POV.RIGHT, POV.DOWN_RIGHT, POV.DOWN, POV.DOWN_LEFT, POV.LEFT};
	private static Consumer<JInputEX> onJoystickConnected;
	private static Consumer<JInputEX> onJoystickDisconnected;
	
	private boolean pauseThread;
	private boolean close;
	private int joystickID;
	private Controller joystick;
	private List<JInputEXComponent> components;
	private List<JInputEXComponent> buttons;
	private List<JInputEXComponent> axes;
	private List<JInputEXComponent> triggers;
	private List<JInputEXComponent> povs;
	private BiConsumer<JInputEX, JInputEXComponent> onPressButton;
	private BiConsumer<JInputEX, JInputEXComponent> onHoldButton;
	private BiConsumer<JInputEX, JInputEXComponent> onReleaseButton;
	private BiConsumer<JInputEX, JInputEXComponent> onPressComponent;
	private BiConsumer<JInputEX, JInputEXComponent> onHoldComponent;
	private BiConsumer<JInputEX, JInputEXComponent> onReleaseComponent;
	private BiConsumer<JInputEX, JInputEXComponent> onPovChanges;
	private BiConsumer<JInputEX, JInputEXComponent> onAxisChanges;
	private BiConsumer<JInputEX, JInputEXComponent> onTriggerChanges;
	
	static {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.GAMEPAD) {
				JInputEX joystick = new JInputEX(controller);
				joystick.joystickID = joysticks.size();
				joysticks.add(joystick);
				if (onJoystickConnected != null)
					onJoystickConnected.accept(joystick);
			}
		}
	}

	private JInputEX() {}
	
	private JInputEX(Controller joystick) {
		this.joystick = joystick;
		components = new ArrayList<>();
		povs = new ArrayList<>();
		axes = new ArrayList<>();
		buttons = new ArrayList<>();
		triggers = new ArrayList<>();
		close = false;
		scanComponents();
		setDefaultDeadZoneValues();
		sortComponents();
	}
	
	private void scanComponents() {
		for (Component component : joystick.getComponents()) {
			Identifier identifier = component.getIdentifier();
			if (identifier instanceof Button) {
				buttons.add(new JInputEXComponent(component));
				components.add(new JInputEXComponent(component));
			}
			else if (identifier == Component.Identifier.Axis.POV) {
				povs.add(new JInputEXComponent(component, 0f, 1f));
				for (int n = 2; n <= 8; n += 2) {
					JInputEXComponent comp = new JInputEXComponent(component, povDirNames[n - 1]);
					comp.setExactlyTriggerValues(povValues[n - 1], povValues[n], povValues[n == 8 ? 1 : n + 1]);
						components.add(comp);
				}
			}
			else if (identifier instanceof Component.Identifier.Axis) {
				if (identifier == Component.Identifier.Axis.Z || identifier == Component.Identifier.Axis.RZ)
					triggers.add(new JInputEXComponent(component, -1f, 1f));
				else
					axes.add(new JInputEXComponent(component, -1f, 1f));
				components.add(new JInputEXComponent(component, component.getName() + "-", 0f, -1f));
				components.add(new JInputEXComponent(component, component.getName() + "+", 0f, 1f));
			}
		}
	}

	private void setDefaultDeadZoneValues() {
		for (int n = 0; n < components.size(); n++)
			if (components.get(n).isAnalogicComponent())
				components.get(n).setDeadZone(0.67f);
		for (int n = 0; n < axes.size(); n++)
			axes.get(n).setDeadZone(0.1f);
		for (int n = 0; n < triggers.size(); n++)
			triggers.get(n).setDeadZone(0.1f);
	}

	@SuppressWarnings("static-access")
	private void sortComponents() {
		components.sort((c1, c2) -> {
			Identifier i1 = c1.getComponent().getIdentifier();
			Identifier i2 = c2.getComponent().getIdentifier();
			int v1 = i1 == Axis.POV ? 7 : i1 == Axis.X ? 6 : i1 == Axis.Y ? 5 :
							 i1 == Axis.RX ? 4 : i1 == Axis.RY ? 3 :
							 i1 instanceof Button ? 2 : i1 == Axis.POV.Z ? 1 : 0;
			int v2 = i2 == Axis.POV ? 7 : i2 == Axis.X ? 6 : i2 == Axis.Y ? 5 :
							 i2 == Axis.RX ? 4 : i2 == Axis.RY ? 3 :
							 i2 instanceof Button ? 2 : i2 == Axis.POV.Z ? 1 : 0;
			return v2 - v1;
		});
		axes.sort((c1, c2) -> {
			Identifier i1 = c1.getComponent().getIdentifier();
			Identifier i2 = c2.getComponent().getIdentifier();
			int v1 = i1 == Axis.X ? 3 : i1 == Axis.Y ? 2 : i1 == Axis.RX ? 1 : 0;
			int v2 = i2 == Axis.X ? 3 : i2 == Axis.Y ? 2 : i2 == Axis.RX ? 1 : 0;
			return v2 - v1;
		});
		triggers.sort((c1, c2) -> {
			Identifier i1 = c1.getComponent().getIdentifier();
			Identifier i2 = c2.getComponent().getIdentifier();
			return (i1 == Axis.Z ? 0 : 1) - (i2 == Axis.Z ? 0 : 1);
		});
	}
	
	public void poll() {
		if (!joystick.poll())
			throw new RuntimeException("The joystick is disconnected");
		for (JInputEXComponent axis : axes) {
			axis.poll();
			if (onAxisChanges != null && axis.getDelta().getValue() != axis.getValue())
				onAxisChanges.accept(this, axis);
		}
		for (JInputEXComponent trigger : triggers) {
			trigger.poll();
			if (onTriggerChanges != null && trigger.getDelta().getValue() != trigger.getValue())
				onTriggerChanges.accept(this, trigger);
		}
		for (JInputEXComponent pov : povs) {
			pov.poll();
			if (onPovChanges != null && pov.getDelta().getValue() != pov.getValue())
				onPovChanges.accept(this, pov);
		}
		for (JInputEXComponent button : buttons) {
			button.poll();
			if (button.wasPressed() && onPressButton != null)
				onPressButton.accept(this, button);
			if (button.wasReleased() && onReleaseButton != null)
				onReleaseButton.accept(this, button);
			if (button.isHold() && onHoldButton != null)
				onHoldButton.accept(this, button);
		}
		for (JInputEXComponent comp : components) {
			comp.poll();
			if (comp.wasPressed() && onPressComponent != null)
				onPressComponent.accept(this, comp);
			if (comp.wasReleased() && onReleaseComponent != null)
				onReleaseComponent.accept(this, comp);
			if (comp.isHold() && onHoldComponent != null)
				onHoldComponent.accept(this, comp);
		}
	}

	/** Set if thread is paused ({@code true}) or not ({@code false)} */
	public void setPauseThread(boolean value)
		{ pauseThread = value; }
	
	/** Return {@code true} if thread is paused */
	public boolean isThreadPaused()
		{ return pauseThread; }
	
	/** Return the total of initialized joysticks */
	public static int getTotalJoysticks()
		{ return joysticks.size(); }
	
	/** Return a desired Joystick from the initialized Joystick list */
	public static JInputEX getJoystick(int joystickID) {
		if (joystickID < 0 || joystickID >= joysticks.size())
			throw new RuntimeException(joystickID + " - Invalid Joystick ID");
		return joysticks.get(joystickID);
	}
	
	/** Return a unmidifiable list of all initialized joysticks */
	public static List<JInputEX> getJoysticks()
		{ return Collections.unmodifiableList(joysticks); }
	
	/** Close all initialized joysticks */
	public static void closeAllJoysticks() {
		for (JInputEX joy : joysticks) {
			joy.close();
			if (onJoystickDisconnected != null)
				onJoystickDisconnected.accept(joy);
		}
	}
	
	/** Set if all joysticks thread is paused ({@code true}) or not ({@code false)} */
	public static void setPauseAllJoysticks(boolean value) {
		for (JInputEX joy : joysticks)
			joy.setPauseThread(value);		
	}
	
	/** Set all joysticks deadzone value */
	public void setAllAnalogicComponentsDeadZoneValue(float value) {
		for (JInputEXComponent axis : axes)
			axis.setDeadZone(value);		
		for (JInputEXComponent trigger : triggers)
			trigger.setDeadZone(value);		
	}
	
	/** Return ({@code true}) if the joystick is connected */
	public boolean isConnected()
		{ return !close; }
	
	/** Return the joystick name */
	public String getName()
		{ return joystick.getName(); }
	
	/** Return the joystick ID */
	public int getID()
		{ return joystickID; }

	/** Close the joystick */
	public void close()
		{ close = true; }

	/** Return the total of buttons from the joystick */
	public int getTotalButtons()
		{ return buttons.size(); }
	
	/** Return a unmodifiable list of buttons from the joystick */
	public List<JInputEXComponent> getButtons()
		{ return Collections.unmodifiableList(buttons); }

	/** Return a desired button from the joystick */
	public JInputEXComponent getButton(int buttonID) {
		if (buttonID < 0 || buttonID >= buttons.size())
			throw new RuntimeException(buttonID + " - Invalid Button ID");
		return buttons.get(buttonID);
	}

	/** Return the total of axes from the joystick */
	public int getTotalAxes()
		{ return axes.size(); }
	
	/** Return a unmodifiable list of axes from the joystick */
	public List<JInputEXComponent> getAxes()
		{ return Collections.unmodifiableList(axes); }
	
	/** Return a desired axis from the joystick */
	public JInputEXComponent getAxis(int axisID) {
		if (axisID < 0 || axisID >= axes.size())
			throw new RuntimeException(axisID + " - Invalid Axis ID");
		return axes.get(axisID);
	}

	/** Return the total of triggers from the joystick */
	public int getTotalTriggers()
		{ return triggers.size(); }
	
	/** Return a unmodifiable list of triggers from the joystick */
	public List<JInputEXComponent> getTriggers()
		{ return Collections.unmodifiableList(triggers); }
	
	/** Return a desired trigger from the joystick */
	public JInputEXComponent getTrigger(int triggerID) {
		if (triggerID < 0 || triggerID >= triggers.size())
			throw new RuntimeException(triggerID + " - Invalid Trigger ID");
		return triggers.get(triggerID);
	}

	/** Return the total of Povs from the joystick */
	public int getTotalPovs()
		{ return povs.size(); }
	
	/** Return a unmodifiable list of povs from the joystick */
	public List<JInputEXComponent> getPovs()
		{ return Collections.unmodifiableList(povs); }
	
	/** Return a desired pov from the joystick */
	public JInputEXComponent getPov(int povID) {
		if (povID < 0 || povID >= povs.size())
			throw new RuntimeException(povID + " - Invalid Pov ID");
		return povs.get(povID);
	}	
		
	/** Return the total of components (Axes, Povs, Triggers and Buttons) from the joystick,
	 *  threated as buttons. These will be another instance of the original ones, which
	 *  triggers as a button. Axes and Povs will be returned as 4-direction buttons, and all
	 *  other analogic components (such analogic triggers) will be returned as a button.
	 *  These cloned components will trigger only {@code on...ComponentEvent()}. 
	 */
	public int getTotalComponents()
		{ return components.size(); }
	
	/** Return a unmodifiable list of components from the joystick (Read {@code getTotalCOmponents()} note) */
	public List<JInputEXComponent> getComponents()
		{ return Collections.unmodifiableList(components); }
	
	/** Return a desired pov from the joystick (Read {@code getTotalCOmponents()} note) */
	public JInputEXComponent getComponent(int componentID) {
		if (componentID < 0 || componentID >= components.size())
			throw new RuntimeException(componentID + " - Invalid Button ID");
		return components.get(componentID);
	}

	/** Set a consumer which will trigger everytime when a button is pressed */
	public void setOnPressButtonEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onPressButton = consumer; }

	/** Set a consumer which will trigger while a button is hold */
	public void setOnHoldButtonEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onHoldButton = consumer; }

	/** Set a consumer which will trigger everytime when a held button is released */
	public void setOnReleaseButtonEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onReleaseButton = consumer; }

	/** Set a consumer which will trigger everytime when a pov value is changed */
	public void setOnPovChangesEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onPovChanges = consumer; }

	/** Set a consumer which will trigger everytime when an axis value is changed */
	public void setOnAxisChangesEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onAxisChanges = consumer; }

	/** Set a consumer which will trigger everytime when a trigger value is changed */
	public void setOnTriggerChangesEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onTriggerChanges = consumer; }

	/** Set a consumer which will trigger everytime when a button is pressed (Read {@code getTotalCOmponents()} note) */
	public void setOnPressComponentEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onPressComponent = consumer; }

	/** Set a consumer which will trigger while a button is hold (Read {@code getTotalCOmponents()} note) */
	public void setOnHoldComponentEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onHoldComponent = consumer; }

	/** Set a consumer which will trigger everytime when a held button is released (Read {@code getTotalCOmponents()} note) */
	public void setOnReleaseComponentEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onReleaseComponent = consumer; }

	/** Set a consumer which will trigger when a joystick is connected (Triggers only a time, after calling the {@code initializeJoysticks()} method */
	public static void setOnJoystickConnectedEvent(Consumer<JInputEX> consumer)
		{ onJoystickConnected = consumer; }

	/** Set a consumer which will trigger when a joystick is disconnected */
	public static void setOnJoystickDisconnectedEvent(Consumer<JInputEX> consumer)
		{ onJoystickDisconnected = consumer; }
	
}

