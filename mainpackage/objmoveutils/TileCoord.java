package objmoveutils;

import java.util.Objects;

import enums.Direction;
import javafx.scene.input.KeyCode;

public class TileCoord {

	private static int globalTileSize = 1;

	private int x;
	private int y;
	
	public TileCoord()
		{ this(0, 0); }
	
	public TileCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public TileCoord(TileCoord tileCoord)
		{ this(tileCoord.getX(), tileCoord.getY()); }
	
	public TileCoord getNewInstance()
		{ return new TileCoord(x, y); }

	public static int getGlobalTileSize()
		{ return globalTileSize; }
	
	public static void setGlobalTileSize(int globalTileSize)
		{ TileCoord.globalTileSize = globalTileSize; }

	public int getX()
		{ return x; }
	
	public int getY()
		{ return y; }
	
	public TileCoord setX(int x) {
		this.x = x;
		return this;
	}
	
	public TileCoord setY(int y) {
		this.y = y;
		return this;
	}
	
	public TileCoord decX(int value) {	
		setX(getX() - value);
		return this;
	}
	
	public TileCoord decY(int value) {
		setY(getY() - value);
		return this;
	}
	
	public TileCoord incX(int value) {
		setX(getX() + value);
		return this;
	}
	
	public TileCoord incY(int value) {
		setY(getY() + value);
		return this;
	}
	
	public TileCoord setCoords(int x, int y) {
		setX(x);
		setY(y);
		return this;
	}
	
	public TileCoord setCoords(TileCoord tileCoord) {
		setCoords(tileCoord.getX(), tileCoord.getY());
		return this;
	}
	
	public TileCoord decCoords(int incX, int incY) {
		decX(incX);
		decY(incY);
		return this;
	}

	public TileCoord incCoords(int incX, int incY) {
		incX(incX);
		incY(incY);
		return this;
	}
	
	public TileCoord decCoords(TileCoord tileCoordWithIncrements) {
		decCoords(tileCoordWithIncrements.getX(), tileCoordWithIncrements.getY());
		return this;
	}
	
	public TileCoord incCoords(TileCoord tileCoordWithIncrements) {
		incCoords(tileCoordWithIncrements.getX(), tileCoordWithIncrements.getY());
		return this;
	}
	
	public TileCoord incCoordsByDirection(Direction direction, int val) {
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
			incCoords(-val, -val);
		else if (direction == Direction.UP_RIGHT)
			incCoords(val, -val);
		else if (direction == Direction.DOWN_LEFT)
			incCoords(-val, val);
		else 
			incCoords(val, val);
		return this;
	}

	public TileCoord incCoordsByDirection(Direction direction) {
		incCoordsByDirection(direction, 1);
		return this;
	}
	
	public TileCoord incCoordsByDirection(KeyCode code, int val) {
		if (code == KeyCode.LEFT || code == KeyCode.A)
			incCoordsByDirection(Direction.LEFT, val);
		else if (code == KeyCode.UP || code == KeyCode.W)
			incCoordsByDirection(Direction.UP, val);
		else if (code == KeyCode.RIGHT || code == KeyCode.D)
			incCoordsByDirection(Direction.RIGHT, val);
		else if (code == KeyCode.DOWN || code == KeyCode.S)
			incCoordsByDirection(Direction.DOWN, val);
		else throw new RuntimeException("Invalid KeyCode for direction - " + code + " Valid KeyCodes: LEFT, DOWN, RIGHT, UP, A, S, D, W");
		return this;
	}
	
	public TileCoord incCoordsByDirection(KeyCode code) {
		incCoordsByDirection(code, 1);
		return this;
	}
	
	public Position getPosition()
		{ return new Position((int)x * globalTileSize, (int)y * globalTileSize); }
	
	public Boolean isOnSameTile(TileCoord tileCoord)
		{ return tileCoord.equals(this); }
	
	public Direction get4wayDirectionToReach(TileCoord tileCoordToReach) {
		if (Math.abs(getX() - tileCoordToReach.getX()) > Math.abs(getY() - tileCoordToReach.getY())) {
			if (getX() > tileCoordToReach.getX())
				return Direction.LEFT;
			if (getY() > tileCoordToReach.getY())
				return Direction.UP;
		}
		else if (getX() < tileCoordToReach.getX())
			return Direction.RIGHT;
		if (getY() < tileCoordToReach.getY())
			return Direction.DOWN;
		return null;
	}
	
	public Direction get8wayDirectionToReach(TileCoord tileCoordToReach) {
		if (getX() > tileCoordToReach.getX()) {
			if (getY() > tileCoordToReach.getY())
				return Direction.UP_LEFT;
			if (getY() < tileCoordToReach.getY())
				return Direction.DOWN_LEFT;
		}
		else {
			if (getY() > tileCoordToReach.getY())
				return Direction.UP_RIGHT;
			if (getY() < tileCoordToReach.getY())
				return Direction.DOWN_RIGHT;
		}
		return get4wayDirectionToReach(tileCoordToReach);
	}
	
	@Override
	public int hashCode()
		{ return Objects.hash(x, y); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TileCoord other = (TileCoord) obj;
		return x == other.x && y == other.y;
	}
	
	@Override
	public String toString()
		{ return "[" + getX() + "," + getY() + "]"; }
	
}
