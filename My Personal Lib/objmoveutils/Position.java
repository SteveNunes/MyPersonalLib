package objmoveutils;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.security.SecureRandom;
import java.util.Objects;

import enums.Direction;

public class Position {

	private double x;
	private double y;
	private int tileSize;
	private static SecureRandom secureRandom = null;
	
	public Position()
		{ this(0, 0, 1); }
	
	public Position(double x, double y, int tileSize) {
		this.x = x;
		this.y = y;
		this.tileSize = tileSize;
		if (secureRandom == null)
			secureRandom = new SecureRandom();
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
	
	public static Position getRandomPositionFromAnEllipse(Ellipse2D ellipse) {
    double centerX = ellipse.getCenterX();
    double centerY = ellipse.getCenterY();
    double semiMajorAxis = ellipse.getWidth() / 2.0;
    double semiMinorAxis = ellipse.getHeight() / 2.0;
    double randomAngle = 2 * Math.PI * secureRandom.nextDouble();
    double randomRadius = Math.sqrt(secureRandom.nextDouble());
    double randomX = centerX + semiMajorAxis * randomRadius * Math.cos(randomAngle);
    double randomY = centerY + semiMinorAxis * randomRadius * Math.sin(randomAngle);
    return new Position(randomX, randomY);
	}
	
	public static Position getRandomPositionFromASquare(Rectangle rectangle) {
    int randomX = rectangle.x + secureRandom.nextInt(rectangle.width);
    int randomY = rectangle.y + secureRandom.nextInt(rectangle.height);
    return new Position(randomX, randomY);
	}
	
	/** Retorna {@code true} se os valores X e Y do Position atual estiverem perfeitamente centralizados em um Tile  */
	public Boolean isPerfectTileCentred()
		{ return (int)x % tileSize == 0 && (int)y % tileSize == 0; }
	
  /*
   * Retorna um {@code Position} com valores {@code X, Y} referentes
   * ao incremento para que um objeto na coordenada {@code position1}
   * chegue até a coordenada {@code position2} no total de {@code frames}
   */
	public static Position getIncrementForMoveBetweenPositions(Position position1, Position position2, int frames) {
	  double x = (position2.getX() - position1.getX()) / frames;
	  double y = (position2.getY() - position1.getY()) / frames;
	  return new Position(x, y);
	}
	
	public static Position getIncrementForMoveBetweenPositions(double startX, double startY, double endX, double endY, int frames)
		{ return getIncrementForMoveBetweenPositions(new Position(startX, startY), new Position(endX, endY), frames); }

	public static Position getIncrementForMoveBetweenPositions(double startX, double startY, Position endPosition, int frames)
		{ return getIncrementForMoveBetweenPositions(startX, startY, endPosition.getX(), endPosition.getY(), frames); }

	public static Position getIncrementForMoveBetweenPositions(Position startPosition, double endX, double endY, int frames)
		{ return getIncrementForMoveBetweenPositions(startPosition.getX(), startPosition.getY(), endX, endY, frames); }
	
  /** Informe as coordenadas {@code x, y} do centro do círculo, e seu raio  para retornar
   * a coordenada de um dos pontos utilizados para formar esse círculo.
   * 
   * @param pts - Quantidade de pontos para desenhar o circulo
   * @param pos - O ponto do circulo desejado. Ex: Se {@code qts} for {@code 10}, será calculado {@code 10} pontos separados igualmente de forma á formar o circulo desejado. Então, {@code pos 2} retorna a coordenada do segundo ponto usado para formar o circulo.
  */
	public static Position circleDot(double x, double y, double r, int pts, long pos) {
	  double dis = 2 * Math.PI / pts;
	  double co = Math.cos(pos * dis);
	  double si = Math.sin(pos * dis);
	  return new Position(co * r, si * r);
	}
	
  /** Informe as coordenadas {@code x, y} do centro da elipse, e seu raio  para retornar
   * a coordenada de um dos pontos utilizados para formar essa elipse.
   * 
   * @param pts - Quantidade de pontos para desenhar a elipse
   * @param pos - O ponto da elipse desejado. Ex: Se {@code qts} for {@code 10}, será calculado {@code 10} pontos separados igualmente de forma á formar a elipse desejada. Então, {@code pos 2} retorna a coordenada do segundo ponto usado para formar a elipse.
  */
	public static Position ellipseDot(double x, double y, double rw, double rh, int pts, long pos) {
	  double dis = 2 * Math.PI / pts;
	  double co = Math.cos(pos * dis);
	  double si = Math.sin(pos * dis);
	  return new Position(co * rw, si * rh);
	}
	
	

	@Override
	public int hashCode()
		{ return Objects.hash(x, y); }

	@Override
	public boolean equals(Object obj) {
		Position other = (Position) obj;
		return obj != null && (int)y == (int)other.y && (int)x == (int)other.x;
	}

	public static Boolean equals(Position position1, Position position2)
		{ return position1.getX() == position2.getX() && position1.getY() == position2.getY(); }
	
	@Override
	public String toString()
		{ return "[" + getX() + "," + getY() + "]"; }
	
}
