package enums;

public enum ImageAlignment {
	
	NONE(0),
	LEFT_TOP(1),
	TOP(2),
	RIGHT_TOP(3),
	RIGHT(4),
	RIGHT_BOTTOM(5),
	BOTTOM(6),
	LEFT_BOTTOM(7),
	LEFT(8),
	CENTER(9);
	
	private static ImageAlignment[] list = {NONE, LEFT_TOP, TOP, RIGHT_TOP, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, LEFT, CENTER};
	private int value;
	
	ImageAlignment(int value)
		{ this.value = value;	}
	
	public int getValue()
		{ return value; }
	
	public ImageAlignment getNext() {
		int i = value + 1;
		if (i == list.length)
			i = 0;
		return list[i];
	}
	
	public ImageAlignment getPreview() {
		int i = value - 1;
		if (i == -1)
			i = list.length -1;
		return list[i];
	}

}
