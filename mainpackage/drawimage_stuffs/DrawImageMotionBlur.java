package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageMotionBlur {

	private double angle;
	private double radius;
	private BlendMode blendMode;
	
	public DrawImageMotionBlur(DrawImageMotionBlur drawImageMotionBlur)
		{ setValues(drawImageMotionBlur.angle, drawImageMotionBlur.radius, drawImageMotionBlur.blendMode); }

	public DrawImageMotionBlur(double angle, double radius, BlendMode blendMode)
		{ setValues(angle, radius, blendMode); }

	public DrawImageMotionBlur(double angle, double radius)
		{ setValues(angle, radius, null); }

	public double getAngle()
		{ return angle; }

	public double getRadius()
		{ return radius; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double angle, double radius, BlendMode blendMode) {
		this.angle = angle;
		this.radius = radius;
		this.blendMode = blendMode == null ? BlendMode.EXCLUSION : blendMode;
	}	
	
	public void setValues(double angle, double radius)
		{ setValues(angle, radius, null); }
	
	public void setAngle(double angle)
		{ this.angle = angle; }
	
	public void setRadius(double radius)
		{ this.radius = radius; }

	public void incValues(double angle, double radius) {
		incAngle(angle);
		incRadius(radius);
	}
	
	public void incAngle(double value)
		{ angle += value; }

	public void incRadius(double value)
		{ radius += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
