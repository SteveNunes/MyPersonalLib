package gameutil;

public class EliticMove {
	
	private Position centerPosition, currentPosition;
	private double x, y, radiusW, radiusH, speed, angle = 0;
	private Position linkedPosition;

	public EliticMove(Position centerPosition, double radiusW, double radiusH, double speed) {
		this.centerPosition = new Position(centerPosition);
		currentPosition = new Position();
		this.radiusW = radiusW;
		this.radiusH = radiusH;
		this.speed = speed;
		linkedPosition = null;
	}

	public void move() {
		angle += speed;
		x = linkedPosition == null ? 0 : linkedPosition.getPosition().getX();
		y = linkedPosition == null ? 0 : linkedPosition.getPosition().getY();
		currentPosition.setX(x + centerPosition.getX() + radiusW * Math.cos(Math.toRadians(angle)));
		currentPosition.setY(y + centerPosition.getY() + radiusH * Math.sin(Math.toRadians(angle)));
	}

	public Position getPosition()
		{ return currentPosition; }
	
	public void setCenterPosition(Position position)
		{ centerPosition.setPosition(position); }
	
	public void linkCenterPositionToPosition(Position position)
		{ linkedPosition = position; }
	
	public void unlinkCenterPosition()
		{ linkedPosition = null; }

}
