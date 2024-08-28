package enums;

public enum ImageFlip {
	NONE(0),
	HORIZONTAL(1),
	VERTICAL(2),
	BOTH(3);
	
	private static ImageFlip[] list = {NONE, HORIZONTAL, VERTICAL, BOTH};
	private int value;
	
	ImageFlip(int value)
		{ this.value = value;	}
	
	public int getValue()
		{ return value; }
	
	public ImageFlip getNext() {
		int i = value + 1;
		if (i == list.length)
			i = 0;
		return list[i];
	}
	
	public ImageFlip getPreview() {
		int i = value - 1;
		if (i == -1)
			i = list.length -1;
		return list[i];
	}
	
}
