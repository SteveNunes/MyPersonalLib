package joystick;

import java.util.ArrayList;
import java.util.List;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;

public class MyJInputJoystickComponent {

	private Component component;
	private String name;
	private long startHold;
	private int heldTime;
	private float value;
	private float prevValue;
	private float minTriggerValue;
	private float maxTriggerValue;
	private float deadZone;
	private List<Float> onlyTriggerValues;
	
	public MyJInputJoystickComponent(Component component, String name, float minTriggerValue, float maxTriggerValue) {
		this.component = component;
		this.name = name;
		this.minTriggerValue = minTriggerValue;
		this.maxTriggerValue = maxTriggerValue;
		
		deadZone = 0f;
		value = 0f;
		prevValue = 0f;
		startHold = 0L;
		heldTime = 0;
	}
	
	public MyJInputJoystickComponent(Component component, String name, float triggerValue)
		{ this(component, name, triggerValue, triggerValue); }
	
	public MyJInputJoystickComponent(Component component, float minTriggerValue, float maxTriggerValue)
		{ this(component, component.getName(), minTriggerValue, maxTriggerValue); }

	public MyJInputJoystickComponent(Component component, float triggerValue)
		{ this(component, component.getName(), triggerValue, triggerValue); }

	public MyJInputJoystickComponent(Component component, String name)
		{ this(component, name, 0f, 1f); }

	public MyJInputJoystickComponent(Component component)
		{ this(component, component.getName(), 0f, 1f); }
	
	public MyJInputJoystickComponent(MyJInputJoystickComponent component, String name, float minTriggerValue, float maxTriggerValue)
		{ this(component.getComponent(), name, minTriggerValue, maxTriggerValue); }
	
	public MyJInputJoystickComponent(MyJInputJoystickComponent component, String name, float triggerValue)
		{ this(component.getComponent(), name, triggerValue, triggerValue); }

	public MyJInputJoystickComponent(MyJInputJoystickComponent component, float minTriggerValue, float maxTriggerValue)
		{ this(component, component.getName(), minTriggerValue, maxTriggerValue); }
	
	public MyJInputJoystickComponent(MyJInputJoystickComponent component, float triggerValue)
		{ this(component, component.getName(), triggerValue, triggerValue); }
	
	public MyJInputJoystickComponent(MyJInputJoystickComponent component, String name)
		{ this(component, name, component.minTriggerValue, component.maxTriggerValue); }
	
	public MyJInputJoystickComponent(MyJInputJoystickComponent component)
		{ this(component, component.name, component.minTriggerValue, component.maxTriggerValue); }

	public void setExactlyTriggerValues(float ... values) {
		onlyTriggerValues = new ArrayList<>();
		for (float f : values)
			onlyTriggerValues.add(f);
	}
	
	private void setStartHold() {
		startHold = System.currentTimeMillis();
		heldTime = -1;
	}
	
	private void setHoldTime() {
		heldTime = (int)(System.currentTimeMillis() - startHold);
		startHold = -1;
	}
	
	/** Call this method always after doing a Controller.poll() */
	public void poll() {
		if (startHold == -1)
			startHold = 0;
		if (heldTime == -1)
			heldTime = 0;
		prevValue = value;
		value = component.getPollData();
		if (onlyTriggerValues != null) { // Botões e POV com valores exatos para ativação
			if (!onlyTriggerValues.contains(prevValue) && onlyTriggerValues.contains(value))
				setStartHold();
			else if (onlyTriggerValues.contains(prevValue) && !onlyTriggerValues.contains(value))
				setHoldTime();
		}
		else if (isPov() || isButton()) { // Botões e POV sem valores exatos para ativação
			if (prevValue != value) {
				if (value == minTriggerValue)
					setStartHold();
				else
					setHoldTime();
			}
		}
		else { // Componentes analógicos (Axis e Triggers)
			if (minTriggerValue == 0) {
				if (maxTriggerValue > 0) { // Axis que vai de 0 a 1f
					if (prevValue <= deadZone && value > deadZone)
						setStartHold();
					else if (prevValue > deadZone && value <= deadZone)
						setHoldTime();
				}
				else { // Axis que vai de 0 a -1f
					if (prevValue >= deadZone * -1 && value < deadZone * -1)
						setStartHold();
					else if (prevValue < deadZone * -1 && value >= deadZone * -1)
						setHoldTime();
				}
			}
			else { // Axis que vai de -1f a 1f
				if (prevValue > deadZone * -1 && prevValue < deadZone &&
						(value <= deadZone * -1 || value >= deadZone))
							setStartHold();
				else if ((prevValue <= deadZone * -1 || prevValue >= deadZone) &&
									value > deadZone * -1 && value < deadZone)
										setHoldTime();
			}
		}
	}
	
	public boolean isAnalogicComponent()
		{ return isAxis() || isTrigger(); }
	
	public boolean isButton()
		{ return component.getIdentifier() instanceof Button; }
	
	public boolean isAxis()
		{ return component.getIdentifier() instanceof Axis && !isTrigger(); }

	public boolean isTrigger() {
		return component.getIdentifier() == Component.Identifier.Axis.Z ||
						component.getIdentifier() == Component.Identifier.Axis.RZ;
	}

	public boolean isPov()
		{ return component.getIdentifier() == Component.Identifier.Axis.POV; }
	
	public void setDeadZone(float value) {
		if (!isAnalogicComponent())
			throw new RuntimeException("You can't set deadzone on a non-analogic component");
		if (value < 0 || value > 1)
			throw new RuntimeException("Value must be a float value between 0.0 and 1.0");
		deadZone = value;
	}
	
	public float getDeadZone()
		{ return deadZone; }
	
	/** Return the component */
	public Component getComponent()
		{ return component; }

	/** Return the component name */
	public String getName()
		{ return name; }
	
	/** Return the component poll value */
	public float getValue()
		{ return value; }
	
	/** Return the preview component poll value (Before the last poll() call) */
	public float getPreviewValue()
		{ return prevValue; }
	
	/** Return how long the component is hold, in milissegundos */
	public int getHoldTime()
		{ return startHold > 0 ? (int)(System.currentTimeMillis() - startHold) : heldTime; }
	
	/** Component is hold */
	public boolean isHold()
		{ return startHold > 0; }

	/** Component was pressed right now, after the last call of poll() */
	public boolean wasPressed()
		{ return startHold > 0 && heldTime == -1; }

	/** Component was released right now, after the last call of poll() */
	public boolean wasReleased()
		{ return heldTime > 0 && startHold == -1; }

}