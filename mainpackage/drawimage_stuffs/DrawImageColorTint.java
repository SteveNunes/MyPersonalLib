package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageColorTint {

	private double red;
	private double green;
	private double blue;
	private double alpha;
	private BlendMode blendMode;

	public DrawImageColorTint(DrawImageColorTint drawImageColorTint)
		{ setValues(drawImageColorTint.red, drawImageColorTint.green, drawImageColorTint.blue, drawImageColorTint.alpha, drawImageColorTint.blendMode); }

	public DrawImageColorTint(double red, double green, double blue, double alpha, BlendMode blendMode)
		{ setValues(red, green, blue, alpha, blendMode); }

	public DrawImageColorTint(double red, double green, double blue, double alpha)
		{ setValues(red, green, blue, alpha); }

	public DrawImageColorTint(double red, double green, double blue, BlendMode blendMode)
		{ setValues(red, green, blue, blendMode); }
	
	public DrawImageColorTint(double red, double green, double blue)
		{ setValues(red, green, blue); }

	public double getRed()
		{ return red; }

	public double getGreen()
		{ return green; }

	public double getBlue()
		{ return blue; }

	public double getAlpha()
		{ return alpha; }
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double red, double green, double blue, double alpha, BlendMode blendMode) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.blendMode = blendMode == null ? BlendMode.SRC_ATOP : blendMode;
	}	

	public void setValues(double red, double green, double blue, double alpha)
		{ setValues(red, green, blue, alpha, null); }
	
	public void setValues(double red, double green, double blue, BlendMode blendMode)
		{ setValues(red, green, blue, 0.5, blendMode); }
	
	public void setValues(double red, double green, double blue)
		{ setValues(red, green, blue, 1); }

	public void setRed(double red)
		{ this.red = red; }

	public void setGreen(double green)
		{ this.green = green; }

	public void setBlue(double blue)
		{ this.blue = blue; }

	public void setAlpha(double alpha)
		{ this.alpha = alpha; }

	public void incValues(double red, double green, double blue, double alpha) {
		incRed(red);
		incGreen(green);
		incBlue(blue);
		incAlpha(alpha);
	}
	
	public void incRed(double value)
		{ red += value; }
	
	public void incGreen(double value)
		{ green += value; }
	
	public void incBlue(double value)
		{ blue += value; }
	
	public void incAlpha(double value)
		{ alpha += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
