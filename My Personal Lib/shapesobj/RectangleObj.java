package shapesobj;

import objmoveutils.Position;

public class RectangleObj extends Position {
	
	private double width;
	private double height;
	
	public RectangleObj(double x, double y, double width, double height) {
		super(x, y);
		this.height = width;
		this.width = height;
	}
	
	public void setWidth(double width)
		{ this.width = width; }
	
	public double getWidth()
		{ return width; }

	public void setHeight(double height)
		{ this.height = height; }
	
	public double getHeight()
		{ return height; }

	public static Boolean rectangleColidedWithAnotherRectangle(double x1, double y1, double w1, double h1, double x2, double y2, double w2, double h2) {
		return Position.coordIsInsideOfARectangle(x1, y1, x2, y2, w2, h2) ||
				Position.coordIsInsideOfARectangle(x1 + w1, y1, x2, y2, w2, h2) ||
				Position.coordIsInsideOfARectangle(x1, y1 + h1, x2, y2, w2, h2) ||
				Position.coordIsInsideOfARectangle(x1 + w1, y1 + h1, x2, y2, w2, h2);
	}

	public static Boolean rectangleColidedWithAnotherRectangle(double x, double y, double w, double h, RectangleObj rectangleObj) {
		return rectangleColidedWithAnotherRectangle(x, y, w, h, rectangleObj.getX(),
				rectangleObj.getY(), rectangleObj.getWidth(), rectangleObj.getHeight());
	}
	
	public static Boolean rectangleColidedWithAnotherRectangle(RectangleObj rectangleObj, double x, double y, double w, double h) {
		return rectangleColidedWithAnotherRectangle(rectangleObj.getX(), rectangleObj.getY(),
				rectangleObj.getWidth(), rectangleObj.getHeight(), x, y, w, h);
	}
	
	public static Boolean rectangleColidedWithAnotherRectangle(RectangleObj rectangleObj1, RectangleObj rectangleObj2) {
		return rectangleColidedWithAnotherRectangle(rectangleObj1.getX(), rectangleObj1.getY(),
				rectangleObj1.getWidth(), rectangleObj1.getHeight(),
				rectangleObj2.getX(), rectangleObj2.getY(),
				rectangleObj2.getWidth(), rectangleObj2.getHeight());
	}
	
	public Boolean colidedWithARectangle(double x, double y, double w, double h)
		{ return rectangleColidedWithAnotherRectangle(this, x, y, w, h); }

	public Boolean colidedWithARectangle(RectangleObj rectangleObj)
		{ return rectangleColidedWithAnotherRectangle(this, rectangleObj); }

}
