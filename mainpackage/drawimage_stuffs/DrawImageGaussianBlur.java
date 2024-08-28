package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageGaussianBlur {

	private int radius;
	private BlendMode blendMode;

	public DrawImageGaussianBlur(DrawImageGaussianBlur drawImageGaussianBlur)
		{ setValues(drawImageGaussianBlur.radius, drawImageGaussianBlur.blendMode); }
	
	public DrawImageGaussianBlur(int radius, BlendMode blendMode)
		{ setValues(radius, blendMode); }

	public DrawImageGaussianBlur(int radius)
		{ setValues(radius); }

	public DrawImageGaussianBlur()
		{ setValues(5); }

	public int getRadius()
		{ return radius; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(int radius, BlendMode blendMode) {
		this.radius = radius;
		this.blendMode = blendMode == null ? BlendMode.EXCLUSION : blendMode;
	}	

	public void setValues(int radius)
		{ setValues(radius, null); }
	
	public void setRadius(int radius)
		{ this.radius = radius; }
	
	public void incRadius(int value)
		{ radius += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
