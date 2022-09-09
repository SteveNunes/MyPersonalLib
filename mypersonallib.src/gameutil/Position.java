package gameutil;

import java.util.Objects;

import enums.Direction;

public class Position {

	private int x;
	private int y;
	private int tileSize;
	
	public Position()
		{ this(0, 0, 1); }
	
	public Position(int x, int y, int tileSize) {
		this.x = x;
		this.y = y;
		this.tileSize = tileSize;
	}
	
	public Position(int x, int y)
		{ this(x, y, 1); }

	public Position(Position position, int tileSize)
		{ this(position.getX(), position.getY(), tileSize); }
	
	public Position(Position position)
		{ this(position.getX(), position.getY(), position.getTileSize()); }

	public int getTileSize()
		{ return tileSize; }
	
	public void setTileSize(int size)
		{ tileSize = size; }

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
	
	public void incPositionByDirection(Direction direction, int val) {
		if (direction == Direction.LEFT)
			incX(-val);
		else if (direction == Direction.RIGHT)
			incX(val);
		else if (direction == Direction.UP)
			incY(-val);
		else if (direction == Direction.DOWN)
			incY(val);
		else if (direction == Direction.TOP_LEFT)
			incPosition(-val, -val);
		else if (direction == Direction.TOP_RIGHT)
			incPosition(val, -val);
		else if (direction == Direction.DOWN_LEFT)
			incPosition(-val, val);
		else 
			incPosition(val, val);
	}
	
	public void incPositionByDirection(Direction direction)
		{ incPositionByDirection(direction, 1); }
	
	public int getDX()
		{ return (getX() + (tileSize / 2)) / tileSize; }
	
	public int getDY()
		{ return (getY() + (tileSize / 2)) / tileSize; }
	
	public Boolean isOnSameTile(Position position)
		{ return position.getDX() == getDX() && position.getDY() == getDY(); }

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
