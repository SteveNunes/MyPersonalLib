package pathfinder;

import java.util.Objects;

import enums.Direction;

public class PathFinderTileCoord {
	
	private int x;
	private int y;
	
	public PathFinderTileCoord()
		{ this(0, 0); }
	
	public PathFinderTileCoord(PathFinderTileCoord tilePos)
		{ this(tilePos.x, tilePos.y); }
	
	public PathFinderTileCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public PathFinderTileCoord getNewInstance()	
		{ return new PathFinderTileCoord(x, y); }

	public int getX()
		{ return x; }

	public void setX(int x)
		{ this.x = x; }

	public int getY()
	 { return y; }

	public void setY(int y)
		{ this.y = y;	}
	
	public void setCoord(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void setCoord(PathFinderTileCoord tileCoord)
		{ setCoord(tileCoord.getX(), tileCoord.getY()); }

	public void incCoordsByDirection(Direction direction, int inc) {
		x += direction == Direction.LEFT ? -inc : direction == Direction.RIGHT ? inc : 0;
		y += direction == Direction.UP ? -inc : direction == Direction.DOWN ? inc : 0;
	}
	
	public void incCoordsByDirection(Direction direction)
		{ incCoordsByDirection(direction, 1); }
	
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
		PathFinderTileCoord other = (PathFinderTileCoord) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString()
		{ return "[" + x + "," + y + "]"; }

}
