package enums;

import java.util.Arrays;
import java.util.List;

public enum Direction {
	DOWN(0),
	DOWN_RIGHT(1),
	RIGHT(2),
	TOP_RIGHT(3),
	UP(4),
	TOP_LEFT(5),
	LEFT(6),
	DOWN_LEFT(7);
	
	final int value;
	
	final static List<Direction> listOfAll = 
		Arrays.asList(DOWN, DOWN_RIGHT, RIGHT, TOP_RIGHT, UP, TOP_LEFT, LEFT, DOWN_LEFT);
	
	Direction(int val)
		{ value = val; }
	
	public int getValue()
		{ return value; }
	
	public List<Direction> getListOfAll()
		{ return listOfAll; }
	
	public static Direction getReverseDirection(Direction direction)
		{ return getClockwiseDirection(direction, 4); }
	
	public Direction getReverseDirection()
		{ return getReverseDirection(this); }

	public static Direction getClockwiseDirection(Direction direction, int inc) {
		int p = direction.getValue() + inc;
		while (p > 7)
			p -= 8;
		while (p < 0)
			p += 8;
		return listOfAll.get(p);
	}

	public Direction getClockwiseDirection(int inc)
		{ return getClockwiseDirection(this, inc); }

}
