package enums;

public enum SpeechQueue {
	
	NO_QUEUE(0),
	REGULAR(1),
	PRIORITY(2);
	
	private int value;
	
	private SpeechQueue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
