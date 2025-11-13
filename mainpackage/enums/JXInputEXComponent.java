package enums;

public enum JXInputEXComponent {

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
	LEFT_AXIS_LEFT(17),
	LEFT_AXIS_UP(18),
	LEFT_AXIS_RIGHT(19),
	LEFT_AXIS_DOWN(20),
	RIGHT_AXIS_LEFT(21),
	RIGHT_AXIS_UP(22),
	RIGHT_AXIS_RIGHT(23),
	RIGHT_AXIS_DOWN(24);
	
	private static String[] shortNames = {"LEFT", "UP", "RIGHT", "DOWN", "A", "B", "X", "Y", "LB", "RB", "BACK", "START", "GUIDE", "LS", "RS", "LT", "RT", "LX-", "LY-", "LX+", "LY+", "RX-", "RY-", "RX+", "RY+"};
	private static JXInputEXComponent[] axes = {LEFT_AXIS_LEFT, LEFT_AXIS_UP, LEFT_AXIS_RIGHT, LEFT_AXIS_DOWN, RIGHT_AXIS_RIGHT, RIGHT_AXIS_UP, RIGHT_AXIS_RIGHT, RIGHT_AXIS_DOWN};
	private static JXInputEXComponent[] triggers = {LEFT_TRIGGER, RIGHT_TRIGGER};
	private static JXInputEXComponent[] dpads = {DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, DPAD_UP};
	private static JXInputEXComponent[] buttons = {BUTTON_A, BUTTON_B, BUTTON_X, BUTTON_Y, BUTTON_LB, BUTTON_RB, BUTTON_LS, BUTTON_RS, BUTTON_BACK, BUTTON_START, BUTTON_GUIDE};

	private int id;
	
	private JXInputEXComponent(int id) {
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
		return isAxis() ? id - LEFT_AXIS_LEFT.id : -1;
	}
	
	public int getTriggerId() {
		return isAxis() ? id - LEFT_TRIGGER.id : -1;
	}
	
	public String getShortName() {
		return shortNames[id];
	}
	
	public JXInputEXComponent[] getAxes() {
		return axes;
	}
	
	public JXInputEXComponent[] getTriggers() {
		return triggers;
	}
	
	public JXInputEXComponent[] getDPads() {
		return dpads;
	}
	
	public JXInputEXComponent[] getButtons() {
		return buttons;
	}
	
	public boolean isAxis() {
		return this == LEFT_AXIS_LEFT || this == LEFT_AXIS_UP || this == LEFT_AXIS_RIGHT || this == LEFT_AXIS_DOWN ||
				this == RIGHT_AXIS_LEFT || this == RIGHT_AXIS_UP || this == RIGHT_AXIS_RIGHT || this == RIGHT_AXIS_DOWN;
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
