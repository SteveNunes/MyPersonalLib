package objmoveutils;

import enums.Direction;
import javafx.scene.input.KeyCode;

public class Position {

	private double x;
	private double y;
	private int tileSize;
	
	public Position()
		{ this(0, 0, 1); }
	
	public Position(double x, double y, int tileSize) {
		this.x = x;
		this.y = y;
		this.tileSize = tileSize;
	}
	
	public Position(double x, double y)
		{ this(x, y, 1); }

	public Position(double x, double y, double incrementalX, double incrementalY, int tileSize)
		{ this(x + incrementalX, y + incrementalY, tileSize); }
	
	public Position(double x, double y, double incrementalX, double incrementalY)
		{ this(x + incrementalX, y + incrementalY, 1); }

	public Position(Position position, int tileSize)
		{ this(position.getX(), position.getY(), tileSize); }
	
	public Position(Position position)
		{ this(position.getX(), position.getY(), position.getTileSize()); }

	public Position(Position position, Position incrementalPosition, int tileSize) {
		this(position.getX() + incrementalPosition.getX(),
				 position.getY() + incrementalPosition.getY(), tileSize);
	}

	public Position(Position position, Position incrementalPosition)
		{ this(position, incrementalPosition, position.getTileSize()); }
	
	public Position(Position position, double incrementalX, double incrementalY, int tileSize)
		{ this(position.getX() + incrementalX, position.getY() + incrementalY, tileSize); }
	
	public Position(Position position, double incrementalX, double incrementalY)
		{ this(position, incrementalX, incrementalY, position.getTileSize()); }
	

	public int getTileSize()
		{ return tileSize; }
	
	public void setTileSize(int size)
		{ tileSize = size; }

	public double getX()
		{ return x; }
	
	public double getY()
		{ return y; }
	
	public void setX(double x)
		{ this.x = x; }
	
	public void setY(double y)
		{ this.y = y; }
	
	public void decX(double value)
		{ setX(getX() - value); }
	
	public void decY(double value)
		{ setY(getY() - value); }
	
	public void incX(double value)
		{ setX(getX() + value); }
	
	public void incY(double value)
		{ setY(getY() + value); }
	
	public Position getPosition()
		{ return this; }

	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public void setPosition(Position position)
		{ setPosition(position.getX(), position.getY()); }
	
	public void decPosition(double incX, double incY) {
		decX(incX);
		decY(incY);
	}

	public void incPosition(double incX, double incY) {
		incX(incX);
		incY(incY);
	}
	
	public void incPosition(Position posWithIncrements)
		{ incPosition(posWithIncrements.getX(), posWithIncrements.getY()); }
	
	public void decPosition(Position posWithIncrements)
		{ incPosition(-posWithIncrements.getX(), -posWithIncrements.getY()); }

	public void incPositionByDirection(Direction direction, double val) {
		if (direction == null)
			throw new RuntimeException("direction is 'null'");
		if (direction == Direction.LEFT)
			incX(-val);
		else if (direction == Direction.RIGHT)
			incX(val);
		else if (direction == Direction.UP)
			incY(-val);
		else if (direction == Direction.DOWN)
			incY(val);
		else if (direction == Direction.UP_LEFT)
			incPosition(-val, -val);
		else if (direction == Direction.UP_RIGHT)
			incPosition(val, -val);
		else if (direction == Direction.DOWN_LEFT)
			incPosition(-val, val);
		else 
			incPosition(val, val);
	}

	public void incPositionByDirection(Direction direction)
		{ incPositionByDirection(direction, 1); }
	
	public void incPositionByDirection(KeyCode code, double val) {
		if (code == KeyCode.LEFT || code == KeyCode.A)
			incPositionByDirection(Direction.LEFT, val);
		else if (code == KeyCode.UP || code == KeyCode.W)
			incPositionByDirection(Direction.UP, val);
		else if (code == KeyCode.RIGHT || code == KeyCode.D)
			incPositionByDirection(Direction.RIGHT, val);
		else if (code == KeyCode.DOWN || code == KeyCode.S)
			incPositionByDirection(Direction.DOWN, val);
		else throw new RuntimeException("Invalid KeyCode for direction - " + code + " Valid KeyCodes: LEFT, DOWN, RIGHT, UP, A, S, D, W");
	}
	
	public void incPositionByDirection(KeyCode code)
		{ incPositionByDirection(code, 1); }
	
	public Position getTilePosition()
		{ return new Position(getTileX(), getTileY()); }
	
	public int getTileX()
		{ return (int)((getX() + (tileSize / 2)) / tileSize); }
	
	public int getTileY()
		{ return (int)((getY() + (tileSize / 2)) / tileSize); }
	
	public static Boolean isOnSameTile(Position position1, Position position2)
		{ return position1.isOnSameTile(position2); }

	public Boolean isOnSameTile(Position position)
		{ return position.getTileX() == getTileX() && position.getTileY() == getTileY(); }
	
	/** Retorna {@code true} se os valores X e Y do Position atual estiverem perfeitamente centralizados em um Tile  */
	public Boolean isPerfectTileCentred()
		{ return (int)x % tileSize == 0 && (int)y % tileSize == 0; }
	
	public static Boolean equals(Position position1, Position position2)
		{ return position1.getX() == position2.getX() && position1.getY() == position2.getY(); }
	
	@Override
	public String toString()
		{ return "[" + getX() + "," + getY() + "]"; }
	
}
