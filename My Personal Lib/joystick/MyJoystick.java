package joystick;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import enums.MyJoystickStyle;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import util.Misc;
import util.MyString;

public class MyJoystick {
	
	private static List<MyJoystick> joysticks = new ArrayList<>();
	private static String povDirs[] = {"UL", "U", "UR", "R", "DR", "D", "DL", "L"};
	private static Consumer<MyJoystick> onJoystickConnected = joystick -> {};
	private static Consumer<MyJoystick> onJoystickDisconnected = joystick -> {};
	
	private Controller controller;
	private List<JoyComponent> components;
	private List<JoyComponent> buttons;
	private List<JoyComponent> axes;
	private List<JoyComponent> triggers;
	private List<JoyComponent> povs;
	private boolean allAsButton;
	private boolean pauseThread;
	private boolean close;
	private int joystickID;
	private Consumer<JoyComponent> onPressPov;
	private Consumer<JoyComponent> onHoldPov;
	private Consumer<JoyComponent> onReleasePov;
	private Consumer<JoyComponent> onPressButton;
	private Consumer<JoyComponent> onHoldButton;
	private Consumer<JoyComponent> onReleaseButton;
	private Consumer<JoyComponent> onPressAnyComponent;
	private Consumer<JoyComponent> onHoldAnyComponent;
	private Consumer<JoyComponent> onReleaseAnyComponent;
	private Consumer<JoyComponent> onAxisChanges;
	private Consumer<JoyComponent> onTriggerChanges;
	
	public MyJoystick(Controller controller)
		{ this(controller, MyJoystickStyle.DEFAULT); }
	
	public MyJoystick(Controller controller, MyJoystickStyle joystickStyle) {
		MyJoystick thisJoystick = this;
		this.controller = controller;
		allAsButton = joystickStyle == MyJoystickStyle.EVERY_COMPONENT_AS_BUTTONS;
		components = new ArrayList<>();
		buttons = new ArrayList<>();
		axes = new ArrayList<>();
		povs = new ArrayList<>();
		triggers = new ArrayList<>();
		onPressButton = joyComponent -> {};
		onHoldButton = joyComponent -> {};
		onReleaseButton = joyComponent -> {};
		onPressPov = joyComponent -> {};
		onHoldPov = joyComponent -> {};
		onReleasePov = joyComponent -> {};
		onPressAnyComponent = joyComponent -> {};
		onHoldAnyComponent = joyComponent -> {};
		onReleaseAnyComponent = joyComponent -> {};
		onAxisChanges = joyComponent -> {};
		onTriggerChanges = joyComponent -> {};
		
		for (Component component : controller.getComponents()) {
			String type = component.getIdentifier().getName();
			if (MyString.isInteger(type)) { // Botão
				if (!allAsButton)
					buttons.add(new JoyComponent(component, 1f));
				else
					components.add(new JoyComponent(component, 1f));
			}
			else if (type.equals("pov")) // POV
				for (int n = 1; n <= 8; n++) {
					JoyComponent joyComponent = new JoyComponent(component, "Pov " + povDirs[n - 1], 0.125f * n);
					if (!allAsButton)
						povs.add(joyComponent);
					else
						components.add(joyComponent);
				}
			else if (type.equals("x") || type.equals("y") || type.equals("z") ||
							 type.equals("rx") || type.equals("ry") || type.equals("rz")) { // Axis (xy) e gatilhos (z)
									if (!allAsButton) {
										if (type.equals("z"))
											triggers.add(new JoyComponent(component, 0, 1f));
										else if (type.equals("rz"))
											triggers.add(new JoyComponent(component, 0, -1f));
										else
											axes.add(new JoyComponent(component, -1f, 1f));
									}
									else
										for (int n = 0; n < 2; n++) {
											if (n == 0)
												components.add(new JoyComponent(component, component.getName() + "-", 0f, -1f));
											else
												components.add(new JoyComponent(component, component.getName() + "+", 0f, 1f));
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
							onJoystickDisconnected.accept(thisJoystick);
							close = true;
							return;
						}
					}
					
					if (!allAsButton) {
						synchronized (axes) {
							for (JoyComponent axis : axes) {
								axis.pool();
								if (axis.isHold() && axis.getPreviewValue() != axis.getValue())
									onAxisChanges.accept(axis);
							}
						}
						synchronized (triggers) {
							for (JoyComponent trigger : triggers) {
								trigger.pool();
								if (trigger.isHold() && trigger.getPreviewValue() != trigger.getValue())
									onTriggerChanges.accept(trigger);
							}
						}
						synchronized (povs) {
							for (JoyComponent pov : povs) {
								pov.pool();
								if (pov.wasPressed())
									onPressPov.accept(pov);
								else if (pov.wasReleased())
									onReleasePov.accept(pov);
								else if (pov.isHold())
									onHoldPov.accept(pov);
							}
						}
						synchronized (buttons) {
							for (JoyComponent button : buttons) {
								button.pool();
								if (button.wasPressed())
									onPressButton.accept(button);
								else if (button.wasReleased())
									onReleaseButton.accept(button);
								else if (button.isHold())
									onHoldButton.accept(button);
							}
						}
					}
					else
						synchronized (components) {
							for (JoyComponent joyComponents : components) {
								joyComponents.pool();
								if (joyComponents.wasPressed())
									onPressAnyComponent.accept(joyComponents);
								else if (joyComponents.wasReleased())
									onReleaseAnyComponent.accept(joyComponents);
								else if (joyComponents.isHold())
									onHoldAnyComponent.accept(joyComponents);
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
			for (MyJoystick joystick : joysticks)
				joystick.close();
			joysticks.clear();
		}
		
		PrintStream error = System.err;
		System.setErr(new PrintStream(new OutputStream() { public void write(int a) {}}));
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		System.setErr(error); 
		
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.GAMEPAD ||
					controller.getType() == Controller.Type.STICK) {
						MyJoystick joystick = new MyJoystick(controller, joystickStyle);
						joystick.joystickID = joysticks.size();
						joysticks.add(joystick);
						onJoystickConnected.accept(joystick);
			}
		}
		return joysticks.size();
	}
	
	public static int getTotalJoysticks()
		{ return joysticks.size(); }
	
	public static MyJoystick getJoystick(int joystickID) {
		if (joystickID < 0 || joystickID >= joysticks.size())
			throw new RuntimeException(joystickID + " - Invalid Joystick ID");
		return joysticks.get(joystickID);
	}
	
	public static List<MyJoystick> getJoysticks()
		{ return Collections.unmodifiableList(joysticks); }
	
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
	
	public List<JoyComponent> getButtons()
		{ return Collections.unmodifiableList(buttons); }

	public JoyComponent getButton(int buttonID) {
		if (buttonID < 0 || buttonID >= buttons.size())
			throw new RuntimeException(buttonID + " - Invalid Button ID");
		return buttons.get(buttonID);
	}

	public int getTotalAxes()
		{ return axes.size(); }
	
	public List<JoyComponent> getAxes()
		{ return Collections.unmodifiableList(axes); }
	
	public JoyComponent getAxis(int axisID) {
		if (axisID < 0 || axisID >= axes.size())
			throw new RuntimeException(axisID + " - Invalid Axis ID");
		return axes.get(axisID);
	}

	public int getTotalTriggers()
		{ return triggers.size(); }
	
	public List<JoyComponent> getTriggers()
		{ return Collections.unmodifiableList(triggers); }
	
	public JoyComponent getTrigger(int triggerID) {
		if (triggerID < 0 || triggerID >= triggers.size())
			throw new RuntimeException(triggerID + " - Invalid Trigger ID");
		return triggers.get(triggerID);
	}

	public int getTotalPovs()
		{ return povs.size(); }
	
	public List<JoyComponent> getPovs()
		{ return Collections.unmodifiableList(povs); }
	
	public JoyComponent getPov(int povID) {
		if (povID < 0 || povID >= povs.size())
			throw new RuntimeException(povID + " - Invalid Pov ID");
		return povs.get(povID);
	}	
		
	public void setOnPressButtonEvent(Consumer<JoyComponent> consumer)
		{ onPressButton = consumer; }

	public void setOnHoldButtonEvent(Consumer<JoyComponent> consumer)
		{ onHoldButton = consumer; }

	public void setOnReleaseButtonEvent(Consumer<JoyComponent> consumer)
		{ onReleaseButton = consumer; }

	public void setOnPressPovEvent(Consumer<JoyComponent> consumer)
		{ onPressPov = consumer; }
	
	public void setOnHoldPovEvent(Consumer<JoyComponent> consumer)
		{ onHoldPov = consumer; }
	
	public void setOnReleasePovEvent(Consumer<JoyComponent> consumer)
		{ onReleasePov = consumer; }
	
	public void setOnAxisChangesEvent(Consumer<JoyComponent> consumer)
		{ onAxisChanges = consumer; }

	public void setOnTriggerChangesEvent(Consumer<JoyComponent> consumer)
		{ onTriggerChanges = consumer; }

	public void setOnPressAnyComponentEvent(Consumer<JoyComponent> consumer)
		{ onPressAnyComponent = consumer; }

	public void setOnHoldAnyComponentEvent(Consumer<JoyComponent> consumer)
		{ onHoldAnyComponent = consumer; }

	public void setOnReleaseAnyComponentEvent(Consumer<JoyComponent> consumer)
		{ onReleaseAnyComponent = consumer; }

	public static void setOnJoystickConnectedEvent(Consumer<MyJoystick> consumer)
		{ onJoystickConnected = consumer; }

	public static void setOnJoystickDisconnectedEvent(Consumer<MyJoystick> consumer)
		{ onJoystickDisconnected = consumer; }

	public void close()
		{ close = true; }
	
}

