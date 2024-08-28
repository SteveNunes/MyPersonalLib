package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageSepiaTone {

	private double level;
	private BlendMode blendMode;

	public DrawImageSepiaTone(DrawImageSepiaTone drawImageGlow)
		{ setValues(drawImageGlow.level, drawImageGlow.blendMode); }

	public DrawImageSepiaTone(double level, BlendMode blendMode)
		{ setValues(level, blendMode); }

	public DrawImageSepiaTone(double level)
		{ setValues(level); }

	public DrawImageSepiaTone()
			{ setValues(1); }
		
	public double getLevel()
		{ return level; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode == null ? BlendMode.MULTIPLY : blendMode;
	}	

	public void setValues(double level)
		{ setValues(level, null); }
	
	public void setLevel(double level)
		{ this.level = level; }
	
	public void incLevel(double value)
		{ level += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
