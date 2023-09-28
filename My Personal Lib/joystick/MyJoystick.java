package joystick;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
	private List<MyJoystickComponent> components;
	private List<MyJoystickComponent> buttons;
	private List<MyJoystickComponent> axes;
	private List<MyJoystickComponent> triggers;
	private List<MyJoystickComponent> povs;
	private boolean allAsButton;
	private boolean pauseThread;
	private boolean close;
	private int joystickID;
	private Consumer<MyJoystickComponent> onPressPov;
	private Consumer<MyJoystickComponent> onHoldPov;
	private Consumer<MyJoystickComponent> onReleasePov;
	private Consumer<MyJoystickComponent> onPressButton;
	private Consumer<MyJoystickComponent> onHoldButton;
	private Consumer<MyJoystickComponent> onReleaseButton;
	private Consumer<MyJoystickComponent> onPressAnyComponent;
	private Consumer<MyJoystickComponent> onHoldAnyComponent;
	private Consumer<MyJoystickComponent> onReleaseAnyComponent;
	private Consumer<MyJoystickComponent> onAxisChanges;
	private Consumer<MyJoystickComponent> onTriggerChanges;
	
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
					buttons.add(new MyJoystickComponent(component, 1f));
				else
					components.add(new MyJoystickComponent(component, 1f));
			}
			else if (type.equals("pov")) // POV
				for (int n = 1; n <= 8; n++) {
					MyJoystickComponent joyComponent = new MyJoystickComponent(component, "Pov " + povDirs[n - 1], 0.125f * n);
					if (!allAsButton)
						povs.add(joyComponent);
					else
						components.add(joyComponent);
				}
			else if (type.equals("x") || type.equals("y") || type.equals("z") ||
							 type.equals("rx") || type.equals("ry") || type.equals("rz")) { // Axis (xy) e gatilhos (z)
									if (!allAsButton) {
										if (type.equals("z") || type.equals("rz")) {
											triggers.add(new MyJoystickComponent(component, component.getName() + "-", 0, -1f));
											triggers.add(new MyJoystickComponent(component, component.getName() + "+", 0, 1f));
										}
										else
											axes.add(new MyJoystickComponent(component, -1f, 1f));
									}
									else {
										components.add(new MyJoystickComponent(component, component.getName() + "-", 0f, -1f));
										components.add(new MyJoystickComponent(component, component.getName() + "+", 0f, 1f));
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
							for (MyJoystickComponent axis : axes) {
								axis.pool();
								if (axis.isHold() && axis.getPreviewValue() != axis.getValue())
									onAxisChanges.accept(axis);
							}
						}
						synchronized (triggers) {
							for (MyJoystickComponent trigger : triggers) {
								trigger.pool();
								if (trigger.isHold() && trigger.getPreviewValue() != trigger.getValue())
									onTriggerChanges.accept(trigger);
							}
						}
						synchronized (povs) {
							for (MyJoystickComponent pov : povs) {
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
							for (MyJoystickComponent button : buttons) {
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
							for (MyJoystickComponent joyComponents : components) {
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
	
	public List<MyJoystickComponent> getButtons()
		{ return Collections.unmodifiableList(buttons); }

	public MyJoystickComponent getButton(int buttonID) {
		if (buttonID < 0 || buttonID >= buttons.size())
			throw new RuntimeException(buttonID + " - Invalid Button ID");
		return buttons.get(buttonID);
	}

	public int getTotalAxes()
		{ return axes.size(); }
	
	public List<MyJoystickComponent> getAxes()
		{ return Collections.unmodifiableList(axes); }
	
	public MyJoystickComponent getAxis(int axisID) {
		if (axisID < 0 || axisID >= axes.size())
			throw new RuntimeException(axisID + " - Invalid Axis ID");
		return axes.get(axisID);
	}

	public int getTotalTriggers()
		{ return triggers.size(); }
	
	public List<MyJoystickComponent> getTriggers()
		{ return Collections.unmodifiableList(triggers); }
	
	public MyJoystickComponent getTrigger(int triggerID) {
		if (triggerID < 0 || triggerID >= triggers.size())
			throw new RuntimeException(triggerID + " - Invalid Trigger ID");
		return triggers.get(triggerID);
	}

	public int getTotalPovs()
		{ return povs.size(); }
	
	public List<MyJoystickComponent> getPovs()
		{ return Collections.unmodifiableList(povs); }
	
	public MyJoystickComponent getPov(int povID) {
		if (povID < 0 || povID >= povs.size())
			throw new RuntimeException(povID + " - Invalid Pov ID");
		return povs.get(povID);
	}	
		
	public void setOnPressButtonEvent(Consumer<MyJoystickComponent> consumer)
		{ onPressButton = consumer; }

	public void setOnHoldButtonEvent(Consumer<MyJoystickComponent> consumer)
		{ onHoldButton = consumer; }

	public void setOnReleaseButtonEvent(Consumer<MyJoystickComponent> consumer)
		{ onReleaseButton = consumer; }

	public void setOnPressPovEvent(Consumer<MyJoystickComponent> consumer)
		{ onPressPov = consumer; }
	
	public void setOnHoldPovEvent(Consumer<MyJoystickComponent> consumer)
		{ onHoldPov = consumer; }
	
	public void setOnReleasePovEvent(Consumer<MyJoystickComponent> consumer)
		{ onReleasePov = consumer; }
	
	public void setOnAxisChangesEvent(Consumer<MyJoystickComponent> consumer)
		{ onAxisChanges = consumer; }

	public void setOnTriggerChangesEvent(Consumer<MyJoystickComponent> consumer)
		{ onTriggerChanges = consumer; }

	public void setOnPressAnyComponentEvent(Consumer<MyJoystickComponent> consumer)
		{ onPressAnyComponent = consumer; }

	public void setOnHoldAnyComponentEvent(Consumer<MyJoystickComponent> consumer)
		{ onHoldAnyComponent = consumer; }

	public void setOnReleaseAnyComponentEvent(Consumer<MyJoystickComponent> consumer)
		{ onReleaseAnyComponent = consumer; }

	public static void setOnJoystickConnectedEvent(Consumer<MyJoystick> consumer)
		{ onJoystickConnected = consumer; }

	public static void setOnJoystickDisconnectedEvent(Consumer<MyJoystick> consumer)
		{ onJoystickDisconnected = consumer; }

	public void close()
		{ close = true; }
	
}

