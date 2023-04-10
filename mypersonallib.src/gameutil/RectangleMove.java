package gameutil;

import java.util.Arrays;

import enums.Direction;
import enums.DirectionOrientation;

public class RectangleMove {
	
	private Direction direction;
	private DirectionOrientation orientation;
	private Position position, topLeftStartPosition;
	private double width, height, speed;
	
	/**
	 * 
	 * @param linkedPosition - {@code Position} do objeto que será atualizado á cada chamada
	 * 													do método {@code move()}, fazendo ele realizar um movimento
	 * 													retangular á partir de {@code topLeftStartPosition}
	 * @param topLeftStartPosition - Coordenada central onde o objeto ficará circulando em torno de.
	 * 												  Se for informado um {@code Position} pertencente á outro objeto
	 * 													que se mova, essa posição central irá acompanhar esse objeto.
	 * @param initialDirection - Direção inicial do objeto
	 * @param orientation - Forma de giro (Horário/Anti-horário)
	 * @param areaWidth - Largura horizontal da área retangular onde o objeto ficará circulando
	 * @param areaHeight - Largura vertical da área retangular onde o objeto ficará circulando
	 * @param speed - Velocidade de movimentação do objeto
	 */
	public RectangleMove(Position linkedPosition, Direction initialDirection, DirectionOrientation orientation, Position topLeftStartPosition, double areaWidth, double areaHeight, double speed) {
		if (!Arrays.asList(Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN).contains(initialDirection))
			throw new RuntimeException("'initialDirection must be LEFT/UP/RIGHT/DOWN'");
		position = linkedPosition;
		this.topLeftStartPosition = topLeftStartPosition;
		width = areaWidth;
		height = areaHeight;
		this.speed = speed;
		direction = initialDirection;
		this.orientation = orientation;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code linkedPosition}. 
	 */
	public RectangleMove(Direction initialDirection, DirectionOrientation orientation, Position topLeftStartPosition, double areaWidth, double areaHeight, double speed)
		{ this(new Position(), initialDirection, orientation, topLeftStartPosition, areaWidth, areaHeight, speed); }
	
	public void move() {
		if (direction == Direction.RIGHT && (position.getX() + speed) > (topLeftStartPosition.getX() + width))
			direction = Direction.DOWN;
		if (direction == Direction.DOWN && (position.getY() + speed) > (topLeftStartPosition.getY() + height))
			direction = Direction.LEFT;
		if (direction == Direction.LEFT && (position.getX() - speed) < topLeftStartPosition.getX())
			direction = Direction.UP;
		if (direction == Direction.UP && (position.getY() - speed) < topLeftStartPosition.getY())
			direction = Direction.RIGHT;
		position.incPositionByDirection(direction, speed);
	}
	
	public Direction getDirection()
		{ return direction; }

	public void setDirection(Direction direction)
		{ this.direction = direction; }

	public DirectionOrientation getOrientation()
		{ return orientation; }

	public void setOrientation(DirectionOrientation orientation)
		{ this.orientation = orientation; }

	public Position getTopLeftStartPosition()
		{ return topLeftStartPosition; }

	public void setTopLeftStartPosition(Position topLeftStartPosition)
		{ this.topLeftStartPosition = topLeftStartPosition; }

	public double getWidth()
		{ return width; }

	public void setWidth(double width)
		{ this.width = width; }

	public double getHeight()
		{ return height; }

	public void setHeight(double height)
		{ this.height = height; }

	public double getSpeed()
		{ return speed; }

	public void setSpeed(double speed)
		{ this.speed = speed; }

	public Position getPosition()
		{ return position; }

	public void setPosition(Position position)
		{ this.position = position; }

}
