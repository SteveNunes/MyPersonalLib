package shapesobj;

import objmoveutils.Position;

public class SquareObj extends Position {
	
	private double sqrSize;
	
	public SquareObj(double x, double y, double sqrSize) {
		super(x, y);
		this.sqrSize = sqrSize;
	}
	
	public void setSize(double sqrSize)
		{ this.sqrSize = sqrSize; }
	
	public double getSize()
		{ return sqrSize; }

	public static Boolean squareColidedWithAnotherSquare(double x1, double y1, double sqrSize1, double x2, double y2, double sqrSize2) {
		return Position.coordIsInsideOfASquare(x1, y1, x2, y2, sqrSize2) ||
				Position.coordIsInsideOfASquare(x1 + sqrSize1, y1, x2, y2, sqrSize2) ||
				Position.coordIsInsideOfASquare(x1, y1 + sqrSize1, x2, y2, sqrSize2) ||
				Position.coordIsInsideOfASquare(x1 + sqrSize1, y1 + sqrSize1, x2, y2, sqrSize2);
	}

	public static Boolean squareColidedWithAnotherSquare(double x, double y, double sqrSize, SquareObj squareObj)
		{ return squareColidedWithAnotherSquare(x, y, sqrSize, squareObj.getX(), squareObj.getY(), squareObj.getSize()); }
	
	public static Boolean squareColidedWithAnotherSquare(SquareObj squareObj, double x, double y, double sqrSize)
		{ return squareColidedWithAnotherSquare(squareObj.getX(), squareObj.getY(), squareObj.getSize(), x, y, sqrSize); }
	
	public static Boolean squareColidedWithAnotherSquare(SquareObj squareObj1, SquareObj squareObj2) {
		return squareColidedWithAnotherSquare(squareObj1.getX(), squareObj1.getY(), squareObj1.getSize(),
				squareObj2.getX(), squareObj2.getY(), squareObj2.getSize());
	}
	
	public Boolean colidedWithASquare(double x, double y, double sqrSize)
		{ return squareColidedWithAnotherSquare(this, x, y, sqrSize); }

	public Boolean colidedWithASquare(SquareObj squareObj)
		{ return squareColidedWithAnotherSquare(this, squareObj); }

}
