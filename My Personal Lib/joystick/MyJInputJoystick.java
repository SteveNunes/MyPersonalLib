package joystick;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import util.Misc;

public class MyJInputJoystick {
	
	private static List<MyJInputJoystick> joysticks = new ArrayList<>();
	private static String povDirs[] = {"UL", "U", "UR", "R", "DR", "D", "DL", "L"};
	private static Consumer<MyJInputJoystick> onJoystickConnected;
	private static Consumer<MyJInputJoystick> onJoystickDisconnected;
	
	private Controller controller;
	private List<MyJInputJoystickComponent> components;
	private List<MyJInputJoystickComponent> buttons;
	private List<MyJInputJoystickComponent> axes;
	private List<MyJInputJoystickComponent> triggers;
	private List<MyJInputJoystickComponent> povs;
	private boolean allAsButton;
	private boolean pauseThread;
	private boolean close;
	private int joystickID;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onPressPov;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onHoldPov;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onReleasePov;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onPressButton;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onHoldButton;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onReleaseButton;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onPressAnyComponent;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onHoldAnyComponent;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onReleaseAnyComponent;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onAxisChanges;
	private BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> onTriggerChanges;
	
	public MyJInputJoystick(Controller controller)
		{ this(controller, MyJoystickStyle.DEFAULT); }
	
	public MyJInputJoystick(Controller controller, MyJoystickStyle joystickStyle) {
		MyJInputJoystick thisJoystick = this;
		this.controller = controller;
		allAsButton = joystickStyle == MyJoystickStyle.EVERY_COMPONENT_AS_BUTTONS;
		components = new ArrayList<>();
		buttons = new ArrayList<>();
		axes = new ArrayList<>();
		povs = new ArrayList<>();
		triggers = new ArrayList<>();
		
		for (Component component : controller.getComponents()) {
			Identifier identifier = component.getIdentifier();

			if (identifier instanceof Button) {
				if (!allAsButton)
					buttons.add(new MyJInputJoystickComponent(component, 1f));
				else
					components.add(new MyJInputJoystickComponent(component, 1f));
			}
			else if (identifier == Component.Identifier.Axis.POV)
				for (int n = 2; n <= 8; n += 2) {
					MyJInputJoystickComponent joyComponent = new MyJInputJoystickComponent(component, "Pov " + povDirs[n - 1]);
					joyComponent.setExactlyTriggerValues(0.125f * (n - 1), 0.125f * n, 0.125f * (n == 8 ? 1 : n + 1));
					if (!allAsButton)
						povs.add(joyComponent);
					else
						components.add(joyComponent);
				}
			else if (identifier instanceof Component.Identifier.Axis) {
				if (!allAsButton) {
					if (identifier == Component.Identifier.Axis.Z || identifier == Component.Identifier.Axis.RZ) {
						triggers.add(new MyJInputJoystickComponent(component, component.getName() + "-", 0, -1f));
						triggers.add(new MyJInputJoystickComponent(component, component.getName() + "+", 0, 1f));
					}
					else
						axes.add(new MyJInputJoystickComponent(component, -1f, 1f));
				}
				else {
					components.add(new MyJInputJoystickComponent(component, component.getName() + "-", 0f, -1f));
					components.add(new MyJInputJoystickComponent(component, component.getName() + "+", 0f, 1f));
				}
			}
		}
		close = false;
		if (allAsButton) {
			for (int n = 0; n < components.size(); n++)
				if (components.get(n).isAnalogicComponent())
					components.get(n).setDeadZone(0.5f);
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
						if (onAxisChanges != null)
							synchronized (axes) {
								for (MyJInputJoystickComponent axis : axes) {
									axis.pool();
									if (axis.isHold() && axis.getPreviewValue() != axis.getValue())
										onAxisChanges.accept(thisJoystick, axis);
								}
							}
						if (onTriggerChanges != null)
							synchronized (triggers) {
								for (MyJInputJoystickComponent trigger : triggers) {
									trigger.pool();
									if (trigger.isHold() && trigger.getPreviewValue() != trigger.getValue())
										onTriggerChanges.accept(thisJoystick, trigger);
								}
							}
						synchronized (povs) {
							for (MyJInputJoystickComponent pov : povs) {
								pov.pool();
								if (pov.wasPressed() && onPressPov != null)
									onPressPov.accept(thisJoystick, pov);
								else if (pov.wasReleased() && onReleasePov != null)
									onReleasePov.accept(thisJoystick, pov);
								else if (pov.isHold() && onHoldPov != null)
									onHoldPov.accept(thisJoystick, pov);
							}
						}
						synchronized (buttons) {
							for (MyJInputJoystickComponent button : buttons) {
								button.pool();
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
							for (MyJInputJoystickComponent joyComponents : components) {
								joyComponents.pool();
								if (joyComponents.wasPressed() && onPressAnyComponent != null)
									onPressAnyComponent.accept(thisJoystick, joyComponents);
								else if (joyComponents.wasReleased() && onReleaseAnyComponent != null)
									onReleaseAnyComponent.accept(thisJoystick, joyComponents);
								else if (joyComponents.isHold() && onHoldAnyComponent != null)
									onHoldAnyComponent.accept(thisJoystick, joyComponents);
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
		{ return initializeControllers(MyJoystickStyle.DEFAULT); }
	
	public static int initializeControllers(MyJoystickStyle joystickStyle) {
		if (!joysticks.isEmpty()) {
			for (MyJInputJoystick joystick : joysticks)
				joystick.close();
			joysticks.clear();
		}
		
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.GAMEPAD ||
					controller.getType() == Controller.Type.STICK) {
						MyJInputJoystick joystick = new MyJInputJoystick(controller, joystickStyle);
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
	
	public static MyJInputJoystick getJoystick(int joystickID) {
		if (joystickID < 0 || joystickID >= joysticks.size())
			throw new RuntimeException(joystickID + " - Invalid Joystick ID");
		return joysticks.get(joystickID);
	}
	
	public static List<MyJInputJoystick> getJoysticks()
		{ return joysticks; }
	
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
	
	public List<MyJInputJoystickComponent> getButtons()
		{ return buttons; }

	public MyJInputJoystickComponent getButton(int buttonID) {
		if (buttonID < 0 || buttonID >= buttons.size())
			throw new RuntimeException(buttonID + " - Invalid Button ID");
		return buttons.get(buttonID);
	}

	public int getTotalAxes()
		{ return axes.size(); }
	
	public List<MyJInputJoystickComponent> getAxes()
		{ return axes; }
	
	public MyJInputJoystickComponent getAxis(int axisID) {
		if (axisID < 0 || axisID >= axes.size())
			throw new RuntimeException(axisID + " - Invalid Axis ID");
		return axes.get(axisID);
	}

	public int getTotalTriggers()
		{ return triggers.size(); }
	
	public List<MyJInputJoystickComponent> getTriggers()
		{ return triggers; }
	
	public MyJInputJoystickComponent getTrigger(int triggerID) {
		if (triggerID < 0 || triggerID >= triggers.size())
			throw new RuntimeException(triggerID + " - Invalid Trigger ID");
		return triggers.get(triggerID);
	}

	public int getTotalPovs()
		{ return povs.size(); }
	
	public List<MyJInputJoystickComponent> getPovs()
		{ return povs; }
	
	public MyJInputJoystickComponent getPov(int povID) {
		if (povID < 0 || povID >= povs.size())
			throw new RuntimeException(povID + " - Invalid Pov ID");
		return povs.get(povID);
	}	
		
	public int getTotalComponents()
		{ return components.size(); }
	
	public List<MyJInputJoystickComponent> getComponents()
		{ return components; }
	
	public MyJInputJoystickComponent getComponent(int componentID) {
		if (componentID < 0 || componentID >= components.size())
			throw new RuntimeException(componentID + " - Invalid Button ID");
		return components.get(componentID);
	}

	public void setOnPressButtonEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onPressButton = consumer; }

	public void setOnHoldButtonEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onHoldButton = consumer; }

	public void setOnReleaseButtonEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onReleaseButton = consumer; }

	public void setOnPressPovEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onPressPov = consumer; }
	
	public void setOnHoldPovEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onHoldPov = consumer; }
	
	public void setOnReleasePovEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onReleasePov = consumer; }
	
	public void setOnAxisChangesEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onAxisChanges = consumer; }

	public void setOnTriggerChangesEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onTriggerChanges = consumer; }

	public void setOnPressAnyComponentEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onPressAnyComponent = consumer; }

	public void setOnHoldAnyComponentEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onHoldAnyComponent = consumer; }

	public void setOnReleaseAnyComponentEvent(BiConsumer<MyJInputJoystick, MyJInputJoystickComponent> consumer)
		{ onReleaseAnyComponent = consumer; }

	public static void setOnJoystickConnectedEvent(Consumer<MyJInputJoystick> consumer)
		{ onJoystickConnected = consumer; }

	public static void setOnJoystickDisconnectedEvent(Consumer<MyJInputJoystick> consumer)
		{ onJoystickDisconnected = consumer; }

	public void close()
		{ close = true; }
	
}

