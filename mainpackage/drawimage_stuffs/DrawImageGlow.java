package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageGlow {

	private int level;
	private BlendMode blendMode;

	public DrawImageGlow(DrawImageGlow drawImageGlow)
		{ setValues(drawImageGlow.level, drawImageGlow.blendMode); }

	public DrawImageGlow(int level, BlendMode blendMode)
		{ setValues(level, blendMode); }

	public DrawImageGlow(int level)
		{ setValues(level); }

	public DrawImageGlow()
			{ setValues(1); }
		
	public int getLevel()
		{ return level; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(int level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode == null ? BlendMode.SRC_ATOP : blendMode;
	}	

	public void setValues(int level)
		{ setValues(level, null); }
	
	public void setLevel(int level)
		{ this.level = level; }
	
	public void incLevel(int value)
		{ level += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
