package gameutil;

import java.util.Arrays;

import enums.Direction;
import enums.DirectionOrientation;

public class RectangleMove {
	
	private Direction direction;
	private DirectionOrientation orientation;
	private Position tPos, position, topLeftStartPosition;
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
			throw new RuntimeException("'initialDirection' must be 'LEFT/UP/RIGHT/DOWN'");
		if (speed < 0)
			throw new RuntimeException("'speed' must be equal or higher than 0");
		position = linkedPosition;
		this.orientation = orientation;
		direction = initialDirection;
		this.topLeftStartPosition = topLeftStartPosition;
		width = areaWidth;
		height = areaHeight;
		tPos = new Position(linkedPosition.getX() - topLeftStartPosition.getX(),
												linkedPosition.getY() - topLeftStartPosition.getY());
		this.speed = speed;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code linkedPosition}. 
	 */
	public RectangleMove(Direction initialDirection, DirectionOrientation orientation, Position topLeftStartPosition, double areaWidth, double areaHeight, double speed)
		{ this(new Position(topLeftStartPosition), initialDirection, orientation, topLeftStartPosition, areaWidth, areaHeight, speed); }
	
	public void move() {
		tPos.incPositionByDirection(direction, speed);
		if (tPos.getX() > width || tPos.getX() < 0 ||
				tPos.getY() > height || tPos.getY() < 0) {
					tPos.incPositionByDirection(direction.getReverseDirection(), speed);
					direction = direction.getClockwiseDirection(orientation == DirectionOrientation.CLOCKWISE ? -2 : 2);
		}
		position.setPosition(topLeftStartPosition.getX() + tPos.getX(),
												 topLeftStartPosition.getY() + tPos.getY());
	}
	
	public Direction getDirection()
		{ return direction; }

	public DirectionOrientation getOrientation()
		{ return orientation; }

	public void setOrientation(DirectionOrientation orientation) {
		this.orientation = orientation;
		speed = -speed;
	}

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
