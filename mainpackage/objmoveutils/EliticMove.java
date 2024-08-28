package objmoveutils;

import enums.DirectionOrientation;

public class EliticMove {
	
	private Position centerPosition, currentPosition;
	private DirectionOrientation orientation;
	private double radiusW, radiusH, speed, angle;

	/**
	 * 
	 * @param outputPosition - {@code Position} do objeto que será atualizado á cada chamada
	 * 													do método {@code move()}, fazendo ele realizar um movimento
	 * 													circular em torno de {@code centerPosition}
	 * @param centerPosition - Coordenada central onde o objeto ficará circulando em torno de.
	 * 												  Se for informado um {@code Position} pertencente á outro objeto
	 * 													que se mova, essa posição central irá acompanhar esse objeto.
	 * @param orientation - Forma de giro (Horário/Anti-horário)
	 * @param radiusW - Raio horizontal da área circular onde o objeto ficará circulando
	 * @param radiusH - Raio vertical da área circular onde o objeto ficará circulando
	 * @param speed - Velocidade de movimentação do objeto
	 */
	public EliticMove(Position outputPosition, Position centerPosition, DirectionOrientation orientation, double radiusW, double radiusH, double speed) {
		this.centerPosition = centerPosition;
		this.orientation = orientation;
		currentPosition = outputPosition;
		this.radiusW = radiusW;
		this.radiusH = radiusH;
		this.speed = 0;
		move();
		this.speed = speed;
		angle = 0;
	}
	
	public EliticMove(EliticMove eliticMove) {
		centerPosition = eliticMove.centerPosition;
		orientation = eliticMove.orientation;
		currentPosition = eliticMove.currentPosition;
		radiusW = eliticMove.radiusW;
		radiusH = eliticMove.radiusH;
		speed = eliticMove.speed;
		angle = eliticMove.angle;
	}

	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public EliticMove(Position centerPosition, DirectionOrientation orientation, double radiusW, double radiusH, double speed)
		{ this(new Position(centerPosition.getX(), centerPosition.getY()), centerPosition, orientation, radiusW, radiusH, speed); }

	/**
	 * Sobrecarga do construtor que não pede os parâmetros {@code outputPosition} e  {@code centerPosition}. 
	 */
	public EliticMove(DirectionOrientation orientation, double radiusW, double radiusH, double speed)
		{ this(new Position(), new Position(), orientation, radiusW, radiusH, speed); }

	/**
	 * Chame esse método continuamente para que as coordenadas do objeto que realizará o movimento circular sejam atualizadas com a nova posição.
	 */
	public void move() {
		angle += orientation == DirectionOrientation.CLOCKWISE ? speed : -speed;
		refreshPosition();
	}
	
	public double getAngle()
		{ return angle; }
	
	/**
	 * Chame esse método continuamente para que as coordenadas do objeto que realizará o movimento circular sejam sincronizadas com as coordenadas do objeto central
	 */
	public void refreshPosition() {
		currentPosition.setX(centerPosition.getX() + radiusW * Math.cos(Math.toRadians(angle)));
		currentPosition.setY(centerPosition.getY() + radiusH * Math.sin(Math.toRadians(angle)));
	}

	public Position getPosition()
		{ return currentPosition; }
	
	public Position getCenterPosition()
		{ return centerPosition; }
	
	public void setCenterPosition(Position position)
		{ centerPosition.setPosition(position); }
	
	public double getRadiusW()
		{ return radiusW; }

	public void setRadiusW(double value)
		{ radiusW = value; }

	public double getRadiusH()
		{ return radiusH; }

	public void setRadiusH(double value)
		{ radiusH = value; }

	public double getSpeed()
		{ return speed; }

	public void setSpeed(double value)
		{ speed = value; }

	public DirectionOrientation getOrientation()
		{ return orientation; }

	public void setOrientation(DirectionOrientation orientation)
		{ this.orientation = orientation; }

}
