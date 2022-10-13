package gameutil;

import java.util.Objects;

import enums.Direction;
import shapesobj.CircleObj;
import shapesobj.EllipseObj;
import shapesobj.RectangleObj;
import shapesobj.SquareObj;
import util.MyMath;

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

	public Position(Position position, int tileSize)
		{ this(position.getX(), position.getY(), tileSize); }
	
	public Position(Position position)
		{ this(position.getX(), position.getY(), position.getTileSize()); }

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
	
	public void incPositionByDirection(Direction direction, double val) {
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
	
	public double getDX()
		{ return (getX() + (tileSize / 2)) / tileSize; }
	
	public double getDY()
		{ return (getY() + (tileSize / 2)) / tileSize; }
	
	public static Boolean isOnSameTile(Position position1, Position position2)
		{ return position1.isOnSameTile(position2); }

	public Boolean isOnSameTile(Position position)
		{ return position.getDX() == getDX() && position.getDY() == getDY(); }

	public static Boolean coordIsInsideOfARectangle(double x, double y, double dx, double dy, double dw, double dh)
		{ return x >= dx && x <= dx + dw && y >= dy && y <= dy + dh; }
	
	public static Boolean coordIsInsideOfARectangle(double x, double y, RectangleObj rectangleObj) {
		return coordIsInsideOfARectangle(x, y, rectangleObj.getX(), rectangleObj.getY(),
			rectangleObj.getWidth(), rectangleObj.getHeight());
	}
	
	public static Boolean coordIsInsideOfARectangle(Position position, double dx, double dy, double dw, double dh)
		{ return coordIsInsideOfARectangle(position.getX(), position.getY(), dx, dy, dw, dh); }
	
	public static Boolean coordIsInsideOfARectangle(Position position, RectangleObj rectangleObj) {
		return coordIsInsideOfARectangle(position.getX(), position.getY(),
			rectangleObj.getX(), rectangleObj.getY(),
			rectangleObj.getWidth(), rectangleObj.getHeight());
	}
	
	public Boolean isInsideOfARect(double dx, double dy, double dw, double dh)
		{ return coordIsInsideOfARectangle(this, dx, dy, dw, dh); }

	public Boolean isInsideOfARect(RectangleObj rectangleObj)
		{ return coordIsInsideOfARectangle(this, rectangleObj); }
	
	public static Boolean coordIsInsideOfASquare(double x, double y, double dx, double dy, double sqrSize)
		{ return coordIsInsideOfARectangle(x, y, dx, dy, sqrSize, sqrSize); }
	
	public static Boolean coordIsInsideOfASquare(double x, double y, SquareObj squareangleObj) {
		return coordIsInsideOfASquare(x, y, squareangleObj.getX(), squareangleObj.getY(),
			squareangleObj.getSize());
	}
	
	public static Boolean coordIsInsideOfASquare(Position position, double dx, double dy, double sqrSize)
		{ return coordIsInsideOfASquare(position.getX(), position.getY(), dx, dy, sqrSize); }
	
	public static Boolean coordIsInsideOfASquare(Position position, SquareObj squareangleObj) {
		return coordIsInsideOfASquare(position.getX(), position.getY(),
			squareangleObj.getX(), squareangleObj.getY(),
			squareangleObj.getSize());
	}
	
	public Boolean isInsideOfASquare(double dx, double dy, double sqrSize)
		{ return coordIsInsideOfASquare(this, dx, dy, sqrSize); }
	
	public Boolean isInsideOfASquare(SquareObj squareangleObj)
		{ return coordIsInsideOfASquare(this, squareangleObj); }
	
	public static Boolean coordIsInsideOfAnEllipse(double x, double y, double cx, double cy, double hRadius, double vRadius) {
		double c = (Math.pow((cx - x), 2) / Math.pow(hRadius, 2)) +
								(Math.pow((cy - y), 2) / Math.pow(vRadius, 2));
		return c == 0;
	}
	
	public static Boolean coordIsInsideOfAnEllipse(double x, double y, EllipseObj ellipseObj) {
		return coordIsInsideOfAnEllipse(x, y, ellipseObj.getX(), ellipseObj.getY(),
			ellipseObj.getHorizontalRadius(), ellipseObj.getVerticalRadius());
	}
	
	public static Boolean coordIsInsideOfAnEllipse(Position position, double cx, double cy, double hRadius, double vRadius)
		{ return coordIsInsideOfAnEllipse(position.getX(), position.getY(), cx, cy, hRadius, vRadius); }
	
	public static Boolean coordIsInsideOfAnEllipse(Position position, EllipseObj ellipseObj) {
		return coordIsInsideOfAnEllipse(position.getX(), position.getY(),
			ellipseObj.getX(), ellipseObj.getY(),
			ellipseObj.getHorizontalRadius(), ellipseObj.getVerticalRadius());
	}
	
	public Boolean isInsideOfAnEllipse(double cx, double cy, double hRadius, double vRadius)
		{ return coordIsInsideOfAnEllipse(this, cx, cy, hRadius, vRadius); }
	
	public Boolean isInsideOfAnEllipse(EllipseObj ellipseObj)
		{ return coordIsInsideOfAnEllipse(this, ellipseObj); }

	public static Boolean coordIsInsideOfACircle(double x, double y, double cx, double cy, double radius)
		{ return ((x - cx) * (x - cx) + (y - cy) * (y - cy) <= radius * radius); }
	
	public static Boolean coordIsInsideOfACircle(double x, double y, CircleObj circleObj) {
		return coordIsInsideOfACircle(x, y, circleObj.getX(), circleObj.getY(),
			circleObj.getRadius());
	}
	
	public static Boolean coordIsInsideOfACircle(Position position, double cx, double cy, double sqrSize)
		{ return coordIsInsideOfACircle(position.getX(), position.getY(), cx, cy, sqrSize); }
	
	public static Boolean coordIsInsideOfACircle(Position position, CircleObj circleObj) {
		return coordIsInsideOfACircle(position.getX(), position.getY(),
			circleObj.getX(), circleObj.getY(),
			circleObj.getRadius());
	}
	
	public Boolean isInsideOfACircle(double cx, double cy, double sqrSize)
		{ return coordIsInsideOfACircle(this, cx, cy, sqrSize); }
	
	public Boolean isInsideOfACircle(CircleObj circleObj)
		{ return coordIsInsideOfACircle(this, circleObj); }
	
	public static Position getRandCoordFromACircle(double x, double y, double radius) {
		double xx, yy;
	  do {
	  	xx = MyMath.rand(x - radius, x + radius);
	  	yy = MyMath.rand(y - radius, y + radius);
	  }
	  while (!Position.coordIsInsideOfACircle(xx, yy, x, y, radius));
	  return new Position(xx, yy);
	}
	
	public static Position getRandCoordFromAnEllipse(double x, double y, double hRadius, double vRadius) {
		double xx, yy;
	  do {
	  	xx = MyMath.rand(x - hRadius, x + hRadius);
	  	yy = MyMath.rand(y - vRadius, y + vRadius);
	  }
	  while (!Position.coordIsInsideOfAnEllipse(xx, yy, x, y, hRadius, vRadius));
	  return new Position(xx, yy);
	}
	
	public static Position getRandCoordFromASquare(double x, double y, double size) {
		double xx, yy;
	  do {
	  	xx = MyMath.rand(x - size, x + size);
	  	yy = MyMath.rand(y - size, y + size);
	  }
	  while (!Position.coordIsInsideOfASquare(xx, yy, x, y, size));
	  return new Position(xx, yy);
	}
	
	public static Position getRandCoordFromARecatngle(double x, double y, double w, double h) {
		double xx, yy;
	  do {
	  	xx = MyMath.rand(x - w, x + w);
	  	yy = MyMath.rand(y - h, y + h);
	  }
	  while (!Position.coordIsInsideOfARectangle(xx, yy, x, y, w, h));
	  return new Position(xx, yy);
	}
	
  /*
   * Retorna um {@code Position} com valores {@code X, Y} referentes
   * ao incremento para que um objeto nas coordenadas {@code x1, y1}
   * chegue até as coordenadas {@code x2, y2} no total de {@code frames}
   */
	public static Position getIncrementForGoToCoordinate(double x1, double y1, double x2, double y2, int frames) {
	  double x = (x2 - x1) / 100;
	  double y = (y2 - y1) / 100;
	  while ((x != 0 && Math.abs(x) < frames) || (y != 0 && Math.abs(y) < frames)) {
	  	x += x / 10;
	  	y += y / 10;
	  }
	  while ((x != 0 && Math.abs(x) > frames) || (y != 0 && Math.abs(y) > frames)) {
	  	x -= x / 10;
	  	y -= y / 10;
	  }
	  return new Position(x, y);
	}
	
	public static Position getIncrementForGoToCoordinate(Position position, double dx, double dy, int frames)
		{ return getIncrementForGoToCoordinate(position.getX(), position.getY(), dx, dy, frames); }

	public static Position getIncrementForGoToCoordinate(double dx, double dy, Position position, int frames)
		{ return getIncrementForGoToCoordinate(dx, dy, position.getX(), position.getY(), frames); }

	public static Position getIncrementForGoToCoordinate(Position position1, Position position2, int frames)
		{ return getIncrementForGoToCoordinate(position1.getX(), position1.getY(), position2.getX(), position2.getY(), frames); }


	
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
