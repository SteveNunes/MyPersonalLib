package enums;

import java.util.Arrays;
import java.util.List;

public enum Direction {
	DOWN(0),
	UP(1),
	RIGHT(2),
	LEFT(3),
	TOP_LEFT(4),
	TOP_RIGHT(5),
	BOTTOM_LEFT(6),
	BOTTOM_RIGHT(7);
	
	final int value;
	
	final static List<Direction> listOfAll = Arrays.asList(UP, DOWN, LEFT, RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT, TOP_LEFT);
	
	Direction(int val)
		{ value = val; }
	
	public int getValue()
		{ return value; }
	
	public List<Direction> getListOfAll()
		{ return listOfAll; }
	
	public static Direction getReverseDirection(Direction direction)
		{ return listOfAll.get(direction.getValue()); }
	
	public Direction getReverseDirection()
		{ return getReverseDirection(this); }
	
}
