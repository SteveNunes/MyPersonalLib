package shapesobj;

import objmoveutils.Position;

public class EllipseObj extends Position {
	
	private double hRadius;
	private double vRadius;
	
	public EllipseObj(double x, double y, double hRadius, double vRadius) {
		super(x, y);
		this.hRadius = hRadius;
		this.vRadius = vRadius;
	}
	
	public void setVerticalRadius(double radius)
		{ vRadius = radius; }
	
	public double getVerticalRadius()
		{ return vRadius; }

	public void setHorizontalRadius(double radius)
		{ hRadius = radius; }
	
	public double getHorizontalRadius()
		{ return hRadius; }

	public static Boolean ellipseColidedWithAnotherEllipse(double x1, double y1, double hRadius1, double vRadius1, double x2, double y2, double hRadius2, double vRadius2) {
		return EllipseObj.coordIsInsideOfAnEllipse(x1, y1, x2, y2, hRadius2, vRadius2) ||
				EllipseObj.coordIsInsideOfAnEllipse(x1 + hRadius1 * 0.8, y1, x2, y2, hRadius2, vRadius2) ||
				EllipseObj.coordIsInsideOfAnEllipse(x1, y1 + vRadius1 * 0.8, x2, y2, hRadius2, vRadius2) ||
				EllipseObj.coordIsInsideOfAnEllipse(x1 + hRadius1 * 0.8, y1 + vRadius1 * 0.8, x2, y2, hRadius2, vRadius2);
	}

	public static Boolean ellipseColidedWithAnotherEllipse(double x, double y, double hRadius, double vRadius, EllipseObj ellipseObj) {
		return ellipseColidedWithAnotherEllipse(x, y, hRadius, vRadius, ellipseObj.getX(),
				ellipseObj.getY(), ellipseObj.getHorizontalRadius(), ellipseObj.getVerticalRadius());
	}
	
	public static Boolean ellipseColidedWithAnotherEllipse(EllipseObj ellipseObj, double x, double y, double hRadius, double vRadius) {
		return ellipseColidedWithAnotherEllipse(ellipseObj.getX(), ellipseObj.getY(),
				ellipseObj.getHorizontalRadius(), ellipseObj.getVerticalRadius(), x, y, hRadius, vRadius);
	}
	
	public static Boolean ellipseColidedWithAnotherEllipse(EllipseObj ellipseObj1, EllipseObj ellipseObj2) {
		return ellipseColidedWithAnotherEllipse(ellipseObj1.getX(), ellipseObj1.getY(),
				ellipseObj1.getHorizontalRadius(), ellipseObj1.getVerticalRadius(),
				ellipseObj2.getX(), ellipseObj2.getY(),
				ellipseObj2.getHorizontalRadius(), ellipseObj2.getVerticalRadius());
	}
	
	public Boolean colidedWithAnEllipse(int x, int y, int hRadius, int vRadius)
		{ return ellipseColidedWithAnotherEllipse(this, x, y, hRadius, vRadius); }

	public Boolean colidedWithAnEllipse(EllipseObj ellipseObj)
		{ return ellipseColidedWithAnotherEllipse(this, ellipseObj); }
	
}
