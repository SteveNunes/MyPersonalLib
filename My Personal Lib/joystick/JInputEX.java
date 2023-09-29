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
import util.Misc;

public class JInputEX {
	
	private static List<JInputEX> joysticks = new ArrayList<>();
	private static String povDirs[] = {"UL", "U", "UR", "R", "DR", "D", "DL", "L"};
	private static Consumer<JInputEX> onJoystickConnected;
	private static Consumer<JInputEX> onJoystickDisconnected;
	
	private Controller controller;
	private List<JInputEXComponent> components;
	private List<JInputEXComponent> buttons;
	private List<JInputEXComponent> axes;
	private List<JInputEXComponent> triggers;
	private List<JInputEXComponent> povs;
	private boolean allAsButton;
	private boolean pauseThread;
	private boolean close;
	private int joystickID;
	private BiConsumer<JInputEX, JInputEXComponent> onPressButton;
	private BiConsumer<JInputEX, JInputEXComponent> onHoldButton;
	private BiConsumer<JInputEX, JInputEXComponent> onReleaseButton;
	private BiConsumer<JInputEX, JInputEXComponent> onPressAnyComponent;
	private BiConsumer<JInputEX, JInputEXComponent> onHoldAnyComponent;
	private BiConsumer<JInputEX, JInputEXComponent> onReleaseAnyComponent;
	private BiConsumer<JInputEX, JInputEXComponent> onPovChanges;
	private BiConsumer<JInputEX, JInputEXComponent> onAxisChanges;
	private BiConsumer<JInputEX, JInputEXComponent> onTriggerChanges;
	
	public JInputEX(Controller controller)
		{ this(controller, JoystickEXStyle.DEFAULT); }
	
	public JInputEX(Controller controller, JoystickEXStyle joystickStyle) {
		JInputEX thisJoystick = this;
		this.controller = controller;
		allAsButton = joystickStyle == JoystickEXStyle.ALL_ANALOGIC_AS_DIGITAL;
		components = new ArrayList<>();
		buttons = new ArrayList<>();
		axes = new ArrayList<>();
		povs = new ArrayList<>();
		triggers = new ArrayList<>();
		
		for (Component component : controller.getComponents()) {
			Identifier identifier = component.getIdentifier();
			if (identifier instanceof Button) {
				if (!allAsButton)
					buttons.add(new JInputEXComponent(component));
				else
					components.add(new JInputEXComponent(component));
			}
			else if (identifier == Component.Identifier.Axis.POV) {
				if (!allAsButton)
					povs.add(new JInputEXComponent(component, 0f, 1f));
				else {
					float pVals[] = {POV.CENTER, POV.UP_LEFT, POV.UP, POV.UP_RIGHT, POV.RIGHT, POV.DOWN_RIGHT, POV.DOWN, POV.DOWN_LEFT, POV.LEFT};
					for (int n = 2; n <= 8; n += 2) {
						JInputEXComponent comp = new JInputEXComponent(component, "Pov (" + povDirs[n - 1] + ")");
						comp.setExactlyTriggerValues(pVals[n - 1], pVals[n], pVals[n == 8 ? 1 : n + 1]);
							components.add(comp);
					}
				}
			}
			else if (identifier instanceof Component.Identifier.Axis) {
				if (!allAsButton) {
					if (identifier == Component.Identifier.Axis.Z || identifier == Component.Identifier.Axis.RZ)
						triggers.add(new JInputEXComponent(component, -1f, 1f));
					else
						axes.add(new JInputEXComponent(component, -1f, 1f));
				}
				else {
					components.add(new JInputEXComponent(component, component.getName() + "-", 0f, -1f));
					components.add(new JInputEXComponent(component, component.getName() + "+", 0f, 1f));
				}
			}
		}
		close = false;
		components.sort((c1, c2) -> {
			Identifier i1 = c1.getComponent().getIdentifier();
			Identifier i2 = c2.getComponent().getIdentifier();
			int v1 = i1 == Axis.POV ? 3 : i1 instanceof Axis ?
							(i1 == Axis.POV.Z || i1 == Axis.POV.RZ ? 0 : 2) : 1;
			int v2 = i2 == Axis.POV ? 3 : i2 instanceof Axis ?
							(i2 == Axis.POV.Z || i2 == Axis.POV.RZ ? 0 : 2) : 2;
			return v2 - v1;
		});
		
		if (allAsButton) {
			for (int n = 0; n < components.size(); n++)
				if (components.get(n).isAnalogicComponent())
					components.get(n).setDeadZone(0.67f);
		}
		else {
			for (int n = 0; n < axes.size(); n++)
				axes.get(n).setDeadZone(0.1f);
			for (int n = 0; n < triggers.size(); n++)
				triggers.get(n).setDeadZone(0.1f);
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!close) {
					synchronized (controller) {
						if (!controller.poll()) {
							if (onJoystickDisconnected != null)
								onJoystickDisconnected.accept(thisJoystick);
							close = true;
							return;
						}
					}
					
					if (!allAsButton) {
						synchronized (axes) {
							for (JInputEXComponent axis : axes) {
								axis.poll();
								if (onAxisChanges != null &&
										((axis.isHold() && axis.getPreviewValue() != axis.getValue()) ||
											axis.wasPressed() || axis.wasReleased()))
												onAxisChanges.accept(thisJoystick, axis);
							}
						}
						synchronized (triggers) {
							for (JInputEXComponent trigger : triggers) {
								trigger.poll();
								if (onTriggerChanges != null &&
										((trigger.isHold() && trigger.getPreviewValue() != trigger.getValue()) ||
											trigger.wasPressed() || trigger.wasReleased()))
												onTriggerChanges.accept(thisJoystick, trigger);
							}
						}
						synchronized (povs) {
							for (JInputEXComponent pov : povs) {
								pov.poll();
								if (onPovChanges != null &&
										((pov.isHold() && pov.getPreviewValue() != pov.getValue()) ||
											pov.wasPressed() || pov.wasReleased()))
												onPovChanges.accept(thisJoystick, pov);
							}
						}
						synchronized (buttons) {
							for (JInputEXComponent button : buttons) {
								button.poll();
								if (button.wasPressed() && onPressButton != null)
									onPressButton.accept(thisJoystick, button);
								else if (button.wasReleased() && onReleaseButton != null)
									onReleaseButton.accept(thisJoystick, button);
								else if (button.isHold() && onHoldButton != null)
									onHoldButton.accept(thisJoystick, button);
							}
						}
					}
					else
						synchronized (components) {
							for (JInputEXComponent comp : components) {
								comp.poll();
								if (comp.wasOnDeadZone() && !comp.isOnDeadZone() && onPressAnyComponent != null)
									onPressAnyComponent.accept(thisJoystick, comp);
								else if (!comp.wasOnDeadZone() && comp.isOnDeadZone() && onReleaseAnyComponent != null)
									onReleaseAnyComponent.accept(thisJoystick, comp);
								else if (!comp.isOnDeadZone() && onHoldAnyComponent != null)
									onHoldAnyComponent.accept(thisJoystick, comp);
							}
						}

					Misc.sleep(1);
					while (!close && pauseThread)
						Misc.sleep(100);
				}
			}
		}).start();
	}
	
	public void setPauseThread(boolean value)
		{ pauseThread = value; }
	
	public boolean isThreadPaused()
		{ return pauseThread; }
	
	public static int initializeControllers()
		{ return initializeControllers(JoystickEXStyle.DEFAULT); }
	
	public static int initializeControllers(JoystickEXStyle joystickStyle) {
		closeAllJoysticks();
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.GAMEPAD ||
					controller.getType() == Controller.Type.STICK) {
						JInputEX joystick = new JInputEX(controller, joystickStyle);
						joystick.joystickID = joysticks.size();
						joysticks.add(joystick);
						if (onJoystickConnected != null)
							onJoystickConnected.accept(joystick);
			}
		}
		return joysticks.size();
	}
	
	public static int getTotalJoysticks()
		{ return joysticks.size(); }
	
	public static JInputEX getJoystick(int joystickID) {
		if (joystickID < 0 || joystickID >= joysticks.size())
			throw new RuntimeException(joystickID + " - Invalid Joystick ID");
		return joysticks.get(joystickID);
	}
	
	public static List<JInputEX> getJoysticks()
		{ return joysticks; }
	
	public static void closeAllJoysticks() {
		for (JInputEX j : joysticks) {
			j.close();
			onJoystickDisconnected.accept(j);
		}
		joysticks.clear();
	}
	
	public static void setPauseAllJoysticks(boolean value) {
		for (JInputEX j : joysticks)
			j.setPauseThread(value);		
	}
	
	public void setAllAnalogicComponentsDeadZone(float value) {
		for (JInputEXComponent c : axes)
			c.setDeadZone(value);		
		for (JInputEXComponent c : triggers)
			c.setDeadZone(value);		
	}
	
	public boolean isConnected()
		{ return !close; }
	
	public String getName()
		{ return controller.getName(); }
	
	public int getID()
		{ return joystickID; }

	public Controller getController()
		{ return controller; }
	
	public int getTotalButtons()
		{ return buttons.size(); }
	
	public List<JInputEXComponent> getButtons()
		{ return Collections.unmodifiableList(buttons); }

	public JInputEXComponent getButton(int buttonID) {
		if (buttonID < 0 || buttonID >= buttons.size())
			throw new RuntimeException(buttonID + " - Invalid Button ID");
		return buttons.get(buttonID);
	}

	public int getTotalAxes()
		{ return axes.size(); }
	
	public List<JInputEXComponent> getAxes()
		{ return Collections.unmodifiableList(axes); }
	
	public JInputEXComponent getAxis(int axisID) {
		if (axisID < 0 || axisID >= axes.size())
			throw new RuntimeException(axisID + " - Invalid Axis ID");
		return axes.get(axisID);
	}

	public int getTotalTriggers()
		{ return triggers.size(); }
	
	public List<JInputEXComponent> getTriggers()
		{ return Collections.unmodifiableList(triggers); }
	
	public JInputEXComponent getTrigger(int triggerID) {
		if (triggerID < 0 || triggerID >= triggers.size())
			throw new RuntimeException(triggerID + " - Invalid Trigger ID");
		return triggers.get(triggerID);
	}

	public int getTotalPovs()
		{ return povs.size(); }
	
	public List<JInputEXComponent> getPovs()
		{ return Collections.unmodifiableList(povs); }
	
	public JInputEXComponent getPov(int povID) {
		if (povID < 0 || povID >= povs.size())
			throw new RuntimeException(povID + " - Invalid Pov ID");
		return povs.get(povID);
	}	
		
	public int getTotalComponents()
		{ return components.size(); }
	
	public List<JInputEXComponent> getComponents()
		{ return Collections.unmodifiableList(components); }
	
	public JInputEXComponent getComponent(int componentID) {
		if (componentID < 0 || componentID >= components.size())
			throw new RuntimeException(componentID + " - Invalid Button ID");
		return components.get(componentID);
	}

	public void setOnPressButtonEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onPressButton = consumer; }

	public void setOnHoldButtonEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onHoldButton = consumer; }

	public void setOnReleaseButtonEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onReleaseButton = consumer; }

	public void setOnPovChangesEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onPovChanges = consumer; }

	public void setOnAxisChangesEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onAxisChanges = consumer; }

	public void setOnTriggerChangesEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onTriggerChanges = consumer; }

	public void setOnPressAnyComponentEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onPressAnyComponent = consumer; }

	public void setOnHoldAnyComponentEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onHoldAnyComponent = consumer; }

	public void setOnReleaseAnyComponentEvent(BiConsumer<JInputEX, JInputEXComponent> consumer)
		{ onReleaseAnyComponent = consumer; }

	public static void setOnJoystickConnectedEvent(Consumer<JInputEX> consumer)
		{ onJoystickConnected = consumer; }

	public static void setOnJoystickDisconnectedEvent(Consumer<JInputEX> consumer)
		{ onJoystickDisconnected = consumer; }

	public void close()
		{ close = true; }
	
}

