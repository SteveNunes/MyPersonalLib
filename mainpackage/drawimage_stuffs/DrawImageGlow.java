package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageGlow {

	private double level;
	private BlendMode blendMode;

	public DrawImageGlow(DrawImageGlow drawImageGlow)
		{ setValues(drawImageGlow.level, drawImageGlow.blendMode); }

	public DrawImageGlow(double level, BlendMode blendMode)
		{ setValues(level, blendMode); }

	public DrawImageGlow(double level)
		{ setValues(level); }

	public DrawImageGlow()
			{ setValues(1); }
		
	public double getLevel()
		{ return level; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode == null ? BlendMode.SRC_ATOP : blendMode;
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
