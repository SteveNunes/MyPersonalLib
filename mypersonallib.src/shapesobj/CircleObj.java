package shapesobj;

import gameutil.Position;

public class CircleObj extends Position {
	
	private double radius;
	
	public CircleObj(double x, double y, double radius) {
		super(x, y);
		this.radius = radius;
	}
	
	public void setRadius(double radius)
		{ this.radius = radius; }
	
	public double getRadius()
		{ return radius; }
	
	public static Boolean circleColidedWithAnotherCircle(double x1, double y1, double r1, double x2, double y2, double r2) {
		return Position.coordIsInsideOfAnEllipse(x1, y1, x2, y2, r2, r2) ||
				Position.coordIsInsideOfAnEllipse(x1 + r1, y1, x2, y2, r2, r2) ||
				Position.coordIsInsideOfAnEllipse(x1, y1 + r1, x2, y2, r2, r2) ||
				Position.coordIsInsideOfAnEllipse(x1 + r1, y1 + r1, x2, y2, r2, r2);
	}

	public static Boolean circleColidedWithAnotherCircle(double x, double y, double r, CircleObj circleObj)
		{ return circleColidedWithAnotherCircle(x, y, r, circleObj.getX(), circleObj.getY(), circleObj.getRadius()); }
	
	public static Boolean circleColidedWithAnotherCircle(CircleObj circleObj, double x, double y, double r)
		{ return circleColidedWithAnotherCircle(circleObj.getX(), circleObj.getY(), circleObj.getRadius(), x, y, r); }
	
	public static Boolean circleColidedWithAnotherCircle(CircleObj circleObj1, CircleObj circleObj2) {
		return circleColidedWithAnotherCircle(circleObj1.getX(), circleObj1.getY(), circleObj1.getRadius(),
				circleObj2.getX(), circleObj2.getY(), circleObj2.getRadius());
	}
	
	public Boolean colidedWithACircle(double x, double y, double r)
		{ return circleColidedWithAnotherCircle(this, x, y, r); }

	public Boolean colidedWithACircle(CircleObj circleObj)
		{ return circleColidedWithAnotherCircle(this, circleObj); }

}
