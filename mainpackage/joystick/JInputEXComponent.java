package joystick;

import java.util.ArrayList;
import java.util.List;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;

public class JInputEXComponent {

	private Component component;
	private JInputEXComponent delta;
	private String name;
	private int componentId;
	private int triggerId;
	private int povId;
	private int axisId;
	private int buttonId;
	private long startHold;
	private int heldTime;
	private float value;
	private float minTriggerValue;
	private float maxTriggerValue;
	private float deadZone;
	private List<Float> onlyTriggerValues;
	
	JInputEXComponent(JInputEXComponent jInputEXComponent, Component component, String name, float minTriggerValue, float maxTriggerValue) {
		setInitialValues(component, name, minTriggerValue, maxTriggerValue);
		delta = jInputEXComponent;
	}
	
	JInputEXComponent(Component component, String name, float minTriggerValue, float maxTriggerValue) {
		setInitialValues(component, name, minTriggerValue, maxTriggerValue);
		delta = new JInputEXComponent(this, component, name, minTriggerValue, maxTriggerValue);
	}
	
	JInputEXComponent(Component component, String name, float triggerValue)
		{ this(component, name, triggerValue, triggerValue); }
	
	JInputEXComponent(Component component, float minTriggerValue, float maxTriggerValue)
		{ this(component, component.getName(), minTriggerValue, maxTriggerValue); }

	JInputEXComponent(Component component, float triggerValue)
		{ this(component, component.getName(), triggerValue, triggerValue); }

	JInputEXComponent(Component component, String name)
		{ this(component, name, 1, 1); }

	JInputEXComponent(Component component)
		{ this(component, component.getName(), 1, 1); }
	
	private void setInitialValues(Component component, String name, float minTriggerValue, float maxTriggerValue) {
		this.component = component;
		this.name = name;
		this.minTriggerValue = minTriggerValue;
		this.maxTriggerValue = maxTriggerValue;
		if (minTriggerValue == maxTriggerValue)
			setExactlyTriggerValues(minTriggerValue);
		deadZone = 0;
		value = 0;
		startHold = 0;
		heldTime = 0;
	}
	
	void setIds(int componentId, int povId, int buttonId, int axisId, int triggerId) {
		this.componentId = componentId;
		this.triggerId = triggerId;
		this.povId = povId;
		this.axisId = axisId;
		this.buttonId = buttonId;
	}
	
	Component getComponent()
		{ return component; }
	
	/** Set a list of component values that will be caught as 'isHold()' */
	public void setExactlyTriggerValues(float ... values) {
		onlyTriggerValues = new ArrayList<>();
		for (float f : values)
			onlyTriggerValues.add(f);
	}
	
	private void setStartHold() {
		startHold = System.currentTimeMillis();
		heldTime = 0;
	}
	
	private void setHoldTime() {
		heldTime = (int)(System.currentTimeMillis() - startHold);
		startHold = 0;
	}
	
	/** Return a cloned component with delta values (Values before the last poll() call) */
	public JInputEXComponent getDelta()
		{ return delta; }

	private void pollDeltaValues() {
		delta.component = component;
		delta.name = name;
		delta.minTriggerValue = minTriggerValue;
		delta.maxTriggerValue = maxTriggerValue;
		delta.deadZone = deadZone;
		delta.value = value;
		delta.startHold = startHold;
		delta.heldTime = heldTime;
		delta.delta = null;
	}
	
	/** Call this method always after doing a Controller.poll() */
	public void poll() {
		pollDeltaValues();
		value = component.getPollData();
		if (onlyTriggerValues != null) {
			if (!onlyTriggerValues.contains(delta.value) && onlyTriggerValues.contains(value))
				setStartHold();
			else if (onlyTriggerValues.contains(delta.value) && !onlyTriggerValues.contains(value))
				setHoldTime();
		}
		else if (minTriggerValue < maxTriggerValue) {
			if (delta.value <= maxTriggerValue * deadZone && value > maxTriggerValue * deadZone)
				setStartHold();
			else if (value <= maxTriggerValue * deadZone && delta.value > maxTriggerValue * deadZone)
				setHoldTime();
		}
		else {
			if (delta.value >= maxTriggerValue * deadZone && value < maxTriggerValue * deadZone)
				setStartHold();
			else if (value >= maxTriggerValue * deadZone && delta.value < maxTriggerValue * deadZone)
				setHoldTime();
		}
	}
	
	/** Return {@code true} if the component is an analogic component (E.g. Axes, Triggers) */
	public boolean isAnalogicComponent()
		{ return isAxis() || isTrigger(); }
	
	/** Return {@code true} if the component is a Button */
	public boolean isButton()
		{ return component.getIdentifier() instanceof Button; }
	
	/** Return {@code true} if the component is an Axis */
	public boolean isAxis()
		{ return component.getIdentifier() instanceof Axis && !isTrigger(); }

	/** Return {@code true} if the component is a Trigger */
	public boolean isTrigger() {
		return component.getIdentifier() == Component.Identifier.Axis.Z ||
						component.getIdentifier() == Component.Identifier.Axis.RZ;
	}

	/** Return {@code true} if the component is a POV */
	public boolean isPov()
		{ return component.getIdentifier() == Component.Identifier.Axis.POV; }
	
	/** Set the deadzone value */
	public void setDeadZone(float value) {
		if (!isAnalogicComponent())
			throw new RuntimeException("You can't set deadzone on a non-analogic component");
		if (value < 0 || value > 1)
			throw new RuntimeException("Value must be a float value between 0.0 and 1.0");
		deadZone = value;
	}
	
	/** Return the deadzone value */
	public float getDeadZone()
		{ return deadZone; }
	
	private boolean isOnDeadZone(float value) {
		if (onlyTriggerValues != null || !isAnalogicComponent())
			return onlyTriggerValues != null ? !onlyTriggerValues.contains(value) : value == 0;
		if (minTriggerValue == 0)
			return (maxTriggerValue < 0 && value > maxTriggerValue * deadZone) ||
							(maxTriggerValue > 0 && value < maxTriggerValue * deadZone);
		else
			return value < maxTriggerValue * deadZone && value > minTriggerValue * deadZone;
	}
	
	/** Return {@code true} if the component value is on deadzone. */
	public boolean isOnDeadZone()
		{ return isOnDeadZone(value); }

	/** Return the component name */
	public String getName()
		{ return name; }
	
	/** Return the component poll value */
	public float getValue()
		{ return value; }
	
	/** Return how long the component is hold, in milissegundos */
	public int getHoldTime()
		{ return startHold > 0 ? (int)(System.currentTimeMillis() - startHold) : heldTime; }
	
	/** Return {@code true} if the component is hold */
	public boolean isHold()
		{ return startHold > 0; }

	/** Return {@code true} if the component was pressed on the last poll */
	public boolean wasPressed()
		{ return !delta.isHold() && isHold(); }

	/** Return {@code true} if the component was released on the last poll */
	public boolean wasReleased()
		{ return delta.isHold() && !isHold(); }
	
	/** Return the component ID */
	public int getComponentId()
		{ return componentId; }

	/** Return the button ID (If the component is not a button, returns -1) */
	public int getButtonId()
		{ return buttonId; }

	/** Return the trigger ID (If the component is not a trigger, returns -1) */
	public int getTriggerId()
		{ return triggerId; }

	/** Return the axis ID (If the component is not an axis, returns -1) */
	public int getAxisId()
		{ return axisId; }

	/** Return the POV ID (If the component is not a POV, returns -1) */
	public int getPovId()
		{ return povId; }

}