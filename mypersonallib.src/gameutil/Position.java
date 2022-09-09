package gameutil;

import java.util.Objects;

import enums.Direction;

public class Position {

	private int x;
	private int y;
	
	public Position()
		{ this(0, 0); }
	
	public Position(int x, int y)
		{ setPosition(x, y); }
	
	public Position(Position position)
		{ setPosition(position); }
	
	public int getX()
		{ return x; }
	
	public int getY()
		{ return y; }
	
	public void setX(int x)
		{ this.x = x; }
	
	public void setY(int y)
		{ this.y = y; }
	
	public void decX(int value)
		{ setX(getX() - value); }
	
	public void decY(int value)
		{ setY(getY() - value); }
	
	public void incX(int value)
		{ setX(getX() + value); }
	
	public void incY(int value)
		{ setY(getY() + value); }
	
	public Position getPosition()
		{ return this; }

	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void setPosition(Position position)
		{ setPosition(position.getX(), position.getY()); }
	
	public void decPosition(int incX, int incY) {
		decX(incX);
		decY(incY);
	}

	public void incPosition(int incX, int incY) {
		incX(incX);
		incY(incY);
	}
	
	public void incPositionByDirection(Direction direction) {
		if (direction == Direction.LEFT)
			incX(-1);
		else if (direction == Direction.RIGHT)
			incX(1);
		else if (direction == Direction.UP)
			incY(-1);
		else if (direction == Direction.DOWN)
			incY(1);
		else if (direction == Direction.TOP_LEFT)
			incPosition(-1, -1);
		else if (direction == Direction.TOP_RIGHT)
			incPosition(1, -1);
		else if (direction == Direction.DOWN_LEFT)
			incPosition(-1, 1);
		else 
			incPosition(1, 1);
	}
	
	public Boolean colidedWith(Position position)
		{ return this.equals(position); }

	@Override
	public int hashCode()
		{ return Objects.hash(x, y); }

	@Override
	public boolean equals(Object obj) {
		Position other = (Position) obj;
		return obj != null && y == other.y && x == other.x;
	}

	public static Boolean equals(Position position1, Position position2)
		{ return position1.getX() == position2.getX() && position1.getY() == position2.getY(); }
	
	@Override
	public String toString()
		{ return "[" + getX() + "," + getY() + "]"; }
	
}
