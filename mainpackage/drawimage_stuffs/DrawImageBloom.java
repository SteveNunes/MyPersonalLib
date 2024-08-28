package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageBloom {

	private double threshold;
	private BlendMode blendMode;

	public DrawImageBloom(DrawImageBloom drawImageGaussianBlur)
		{ setValues(drawImageGaussianBlur.threshold, drawImageGaussianBlur.blendMode); }
	
	public DrawImageBloom(double threshold, BlendMode blendMode)
		{ setValues(threshold, blendMode); }

	public DrawImageBloom(int threshold)
		{ setValues(threshold, null); }

	public DrawImageBloom()
		{ setValues(5, null); }

	public double getThreshold()
		{ return threshold; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double threshold, BlendMode blendMode) {
		this.threshold = threshold;
		this.blendMode = blendMode == null ? BlendMode.EXCLUSION : blendMode;
	}	

	public void setThreshold(double threshold)
		{ this.threshold = threshold; }
	
	public void incThreshold(double value)
		{ threshold += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
