package enums;

import java.util.Arrays;
import java.util.List;

import objmoveutils.Position;

public enum Direction {
	UP(0),
	UP_RIGHT(1),
	RIGHT(2),
	DOWN_RIGHT(3),
	DOWN(4),
	DOWN_LEFT(5),
	LEFT(6),
	UP_LEFT(7);
	
	final int value;
	
	final static List<Direction> listOfAll = 
		Arrays.asList(UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT);
	
	Direction(int val)
		{ value = val; }
	
	public static Direction get8DirectionFromValue(int value)
		{ return listOfAll.get(value); }
	
	public static Direction get4DirectionFromValue(int value)
		{ return listOfAll.get(value * 2); }
	
	public int get4DirValue()
		{ return value / 2; }
	
	public int get8DirValue()
		{ return value; }

	public List<Direction> getListOfAll()
		{ return listOfAll; }
	
	public static Direction getReverseDirection(Direction direction)
		{ return getClockwiseDirection(direction, 4); }
	
	public Direction getReverseDirection()
		{ return getReverseDirection(this); }

	public static Direction getClockwiseDirection(Direction direction, int inc) {
		int p = direction.get8DirValue() + inc;
		while (p > 7)
			p -= 8;
		while (p < 0)
			p += 8;
		return listOfAll.get(p);
	}
	
	public Direction getNext4WayClockwiseDirection()
		{ return getClockwiseDirection(this, 2); }

	public Direction getPreview4WayClockwiseDirection()
		{ return getClockwiseDirection(this, -2); }

	public Direction getNext8WayClockwiseDirection()
		{ return getClockwiseDirection(this, 1); }
	
	public Direction getPreview8WayClockwiseDirection()
		{ return getClockwiseDirection(this, -1); }
	
	public Direction getNext4WayClockwiseDirection(int value)
		{ return getClockwiseDirection(this, 2 * value); }
	
	public Direction getPreview4WayClockwiseDirection(int value)
		{ return getClockwiseDirection(this, 2 * -value); }
	
	public Direction getNext8WayClockwiseDirection(int value)
		{ return getClockwiseDirection(this, value); }
	
	public Direction getPreview8WayClockwiseDirection(int value)
		{ return getClockwiseDirection(this, -value); }

	public static Direction getDirectionThroughPositions(Position p1, Position p2) {
		if (p2.getX() < p1.getX()) {
			if (p2.getY() < p1.getY())
				return Direction.UP_LEFT;
			if (p2.getY() > p1.getY())
				return Direction.DOWN_LEFT;
			return Direction.LEFT;
		}
		if (p2.getX() > p1.getX()) {
			if (p2.getY() < p1.getY())
				return Direction.UP_RIGHT;
			if (p2.getY() > p1.getY())
				return Direction.DOWN_RIGHT;
			return Direction.RIGHT;
		}
		if (p2.getY() < p1.getY())
			return Direction.UP;
		if (p2.getY() > p1.getY())
			return Direction.DOWN;
		return null;
	}

}
