package vjoymanager;

public enum PovDirection {

	CENTER(0),
	UP(1),
	RIGHT(2),
	DOWN(4),
	LEFT(8);
	
	private int value;
	
	private PovDirection(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
