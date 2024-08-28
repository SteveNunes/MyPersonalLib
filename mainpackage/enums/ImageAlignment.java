package enums;

public enum ImageAlignment {
	
	LEFT_TOP(0),
	TOP(1),
	RIGHT_TOP(2),
	RIGHT(3),
	RIGHT_BOTTOM(4),
	BOTTOM(5),
	LEFT_BOTTOM(6),
	LEFT(7),
	CENTER(8);
	
	private static ImageAlignment[] list = {LEFT_TOP, TOP, RIGHT_TOP, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, LEFT, CENTER};
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
