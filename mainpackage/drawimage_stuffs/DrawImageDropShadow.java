package drawimage_stuffs;

import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class DrawImageDropShadow {

	private double offsetX;
	private double offsetY;
	private Color color;
	private BlendMode blendMode;

	public DrawImageDropShadow(DrawImageDropShadow drawImageColorTint)
		{ setValues(drawImageColorTint.offsetX, drawImageColorTint.offsetY, drawImageColorTint.color, drawImageColorTint.blendMode); }

	public DrawImageDropShadow(double offsetX, double offsetY, Color color, BlendMode blendMode)
		{ setValues(offsetX, offsetY, color, blendMode); }

	public DrawImageDropShadow(double offsetX, double offsetY, Color color)
		{ setValues(offsetX, offsetY, color); }

	public DrawImageDropShadow(double offsetX, double offsetY, BlendMode blendMode)
		{ setValues(offsetX, offsetY, blendMode); }

	public DrawImageDropShadow(double offsetX, double offsetY)
		{ setValues(offsetX, offsetY); }

	public double getOffsetX()
		{ return offsetX; }

	public double getOffsetY()
		{ return offsetY; }

	public Color getColor()
		{ return color; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double offsetX, double offsetY, Color color, BlendMode blendMode) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.color = color == null ? Color.BLACK : color;
		this.blendMode = blendMode == null ? BlendMode.MULTIPLY : blendMode;
	}	

	public void setValues(double offsetX, double offsetY, BlendMode blendMode)
		{ setValues(offsetX, offsetY, blendMode); }
	
	public void setValues(double offsetX, double offsetY, Color color)
		{ setValues(offsetX, offsetY, color, null); }
	
	public void setValues(double offsetX, double offsetY)
		{ setValues(offsetX, offsetY, null, null); }
	
	public void setOffsetX(double offsetX)
		{ this.offsetX = offsetX; }
	
	public void setOffsetY(double offsetY)
		{ this.offsetY = offsetY; }

	public void incValues(double offsetX, double offsetY) {
		incOffsetX(offsetX);
		incOffsetY(offsetY);
	}
	
	public void incOffsetX(double value)
		{ offsetX += value; }
	
	public void incOffsetY(double value)
		{ offsetY += value; }

	public void setColor(Color color)
		{ this.color = color; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
