package objmoveutils;

import enums.Direction;
import javafx.scene.input.KeyCode;

public class Position {

	private static int globalTileSize = 1;
	private double x;
	private double y;
	
	public Position()
		{ this(0, 0); }
	
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Position(Position position)
		{ this(position.getX(), position.getY()); }

	public static int getGlobalTileSize()
	{ return globalTileSize; }

	public static void setGlobalTileSize(int globalTileSize)
		{ Position.globalTileSize = globalTileSize; }

	public double getX()
		{ return x; }
	
	public double getY()
		{ return y; }
	
	public Position setX(double x) {
		this.x = x;
		return this;
	}
	
	public Position setY(double y) {
		this.y = y;
		return this;
	}
	
	public Position decX(double value) {	
		setX(getX() - value);
		return this;
	}
	
	public Position decY(double value) {
		setY(getY() - value);
		return this;
	}
	
	public Position incX(double value) {
		setX(getX() + value);
		return this;
	}
	
	public Position incY(double value) {
		setY(getY() + value);
		return this;
	}
	
	public Position getPosition()
		{ return this; }

	public Position setPosition(double x, double y) {
		setX(x);
		setY(y);
		return this;
	}
	
	public Position setPosition(Position position) {
		setPosition(position.getX(), position.getY());
		return this;
	}
	
	public Position decPosition(double incX, double incY) {
		decX(incX);
		decY(incY);
		return this;
	}

	public Position incPosition(double incX, double incY) {
		incX(incX);
		incY(incY);
		return this;
	}
	
	public Position decPosition(Position positionWithIncrements) {
		decPosition(positionWithIncrements.getX(), positionWithIncrements.getY());
		return this;
	}
	
	public Position incPosition(Position positionWithIncrements) {
		incPosition(positionWithIncrements.getX(), positionWithIncrements.getY());
		return this;
	}
	
	public Position incPositionByDirection(Direction direction, double val) {
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
		return this;
	}

	public Position incPositionByDirection(Direction direction) {
		incPositionByDirection(direction, 1);
		return this;
	}
	
	public Position incPositionByDirection(KeyCode code, double val) {
		if (code == KeyCode.LEFT || code == KeyCode.A)
			incPositionByDirection(Direction.LEFT, val);
		else if (code == KeyCode.UP || code == KeyCode.W)
			incPositionByDirection(Direction.UP, val);
		else if (code == KeyCode.RIGHT || code == KeyCode.D)
			incPositionByDirection(Direction.RIGHT, val);
		else if (code == KeyCode.DOWN || code == KeyCode.S)
			incPositionByDirection(Direction.DOWN, val);
		else throw new RuntimeException("Invalid KeyCode for direction - " + code + " Valid KeyCodes: LEFT, DOWN, RIGHT, UP, A, S, D, W");
		return this;
	}
	
	public Position incPositionByDirection(KeyCode code) {
		incPositionByDirection(code, 1);
		return this;
	}
	
	public TileCoord getTileCoord()
		{ return new TileCoord((int)x / globalTileSize, (int)y / globalTileSize); }

	public TileCoord getTileCoordFromCenter()
		{ return new TileCoord((int)((x + globalTileSize / 2) / globalTileSize), (int)((y + globalTileSize / 2) / globalTileSize)); }
	
	public Boolean isOnSameTile(Position position)
		{ return position.getTileCoord().equals(getTileCoord()); }
	
	public void centerXToTile()
		{ setX((int)((getX() + globalTileSize / 2) / globalTileSize) * globalTileSize); }

	public void centerYToTile()
		{ setY((int)((getY() + globalTileSize / 2) / globalTileSize) * globalTileSize); }

	public void centerToTile() {
		centerXToTile();
		centerYToTile();
	}
	
	public Direction get4wayDirectionToReach(Position positionToReach) {
		if (Math.abs(getX() - positionToReach.getX()) > Math.abs(getY() - positionToReach.getY())) {
			if (getX() > positionToReach.getX())
				return Direction.LEFT;
			if (getX() < positionToReach.getX())
				return Direction.RIGHT;
		}
		else if (getY() > positionToReach.getY())
			return Direction.UP;
		if (getY() < positionToReach.getY())
			return Direction.DOWN;
		return null;
	}
	
	public Direction get8wayDirectionToReach(Position positionToReach) {
		if (getX() > positionToReach.getX()) {
			if (getY() > positionToReach.getY())
				return Direction.UP_LEFT;
			if (getY() < positionToReach.getY())
				return Direction.DOWN_LEFT;
		}
		else {
			if (getY() > positionToReach.getY())
				return Direction.UP_RIGHT;
			if (getY() < positionToReach.getY())
				return Direction.DOWN_RIGHT;
		}
		return get4wayDirectionToReach(positionToReach);
	}
	
	public Position getNewInstance()
		{ return new Position(x, y); }

	/** Retorna {@code true} se os valores X e Y do Position atual estiverem perfeitamente centralizados em um Tile  */
	public Boolean isPerfectTileCentred()
		{ return (int)x % globalTileSize == 0 && (int)y % globalTileSize == 0; }
	
	@Override
	public String toString()
		{ return "[" + getX() + "," + getY() + "]"; }
	
}
