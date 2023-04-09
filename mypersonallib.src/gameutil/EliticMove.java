package gameutil;

public class EliticMove {
	
	private Position centerPosition, currentPosition;
	private double radiusW, radiusH, speed, angle = 0;

	public EliticMove(Position centerPosition, double radiusW, double radiusH, double speed) {
		this.centerPosition = new Position(centerPosition);
		currentPosition = new Position();
		this.radiusW = radiusW;
		this.radiusH = radiusH;
		this.speed = speed;
	}

	public void move() {
		angle += speed;
		currentPosition.setX(centerPosition.getX() + radiusW * Math.cos(Math.toRadians(angle)));
		currentPosition.setY(centerPosition.getY() + radiusH * Math.sin(Math.toRadians(angle)));
	}

	public Position getPosition()
		{ return currentPosition; }

}
