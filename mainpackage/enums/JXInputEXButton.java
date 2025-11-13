package enums;

public enum JXInputEXButton {

	DPAD_LEFT(0),
	DPAD_UP(1),
	DPAD_RIGHT(2),
	DPAD_DOWN(3),
	BUTTON_A(4),
	BUTTON_B(5),
	BUTTON_X(6),
	BUTTON_Y(7),
	BUTTON_LB(8),
	BUTTON_RB(9),
	BUTTON_BACK(10),
	BUTTON_START(11),
	BUTTON_GUIDE(12),
	BUTTON_LS(13),
	BUTTON_RS(14),
	LEFT_TRIGGER(15),
	RIGHT_TRIGGER(16),
	LEFT_AXIS_X(17),
	LEFT_AXIS_Y(18),
	RIGHT_AXIS_X(19),
	RIGHT_AXIS_Y(20);
	
	private static String[] shortNames = {"LEFT", "UP", "RIGHT", "DOWN", "A", "B", "X", "Y", "LB", "RB", "BACK", "START", "GUIDE", "LS", "RS", "LT", "RT", "LX", "LY", "RX", "RY"};
	private static JXInputEXButton[] axes = {LEFT_AXIS_X, LEFT_AXIS_Y, RIGHT_AXIS_X, RIGHT_AXIS_Y};
	private static JXInputEXButton[] triggers = {LEFT_TRIGGER, RIGHT_TRIGGER};
	private static JXInputEXButton[] dpads = {DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, DPAD_UP};
	private static JXInputEXButton[] buttons = {BUTTON_A, BUTTON_B, BUTTON_X, BUTTON_Y, BUTTON_LB, BUTTON_RB, BUTTON_LS, BUTTON_RS, BUTTON_BACK, BUTTON_START, BUTTON_GUIDE};
	
	private int id;
	
	private JXInputEXButton(int id) {
		this.id = id;
	}
	
	public int getComponentId() {
		return id;
	}
	
	public int getDPadId() {
		return isDPad() ? id : -1;
	}
	
	public int getButtonId() {
		return isButton() ? id - BUTTON_A.id : -1;
	}
	
	public int getAxisId() {
		return isAxis() ? id - LEFT_AXIS_X.id : -1;
	}
	
	public int getTriggerId() {
		return isAxis() ? id - LEFT_TRIGGER.id : -1;
	}
	
	public String getShortName() {
		return shortNames[id];
	}
	
	public JXInputEXButton[] getAxes() {
		return axes;
	}
	
	public JXInputEXButton[] getTriggers() {
		return triggers;
	}
	
	public JXInputEXButton[] getDPads() {
		return dpads;
	}
	
	public JXInputEXButton[] getButtons() {
		return buttons;
	}
	
	public boolean isAxis() {
		return this == LEFT_AXIS_X || this == LEFT_AXIS_Y || this == RIGHT_AXIS_X || this == RIGHT_AXIS_Y;
	}
	
	public boolean isTrigger() {
		return this == LEFT_TRIGGER || this == RIGHT_TRIGGER;
	}
	
	public boolean isDPad() {
		return this == DPAD_DOWN || this == DPAD_LEFT || this == DPAD_RIGHT || this == DPAD_UP;
	}
	
	public boolean isButton() {
		return !isAxis() && !isDPad() && !isTrigger();
	}
	
}
