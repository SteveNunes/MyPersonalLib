package drawimage_stuffs;

import javafx.scene.effect.BlendMode;

public class DrawImageColorAdjust {

	private double hue;
	private double saturation;
	private double brightness;
	private BlendMode blendMode;
	
	public DrawImageColorAdjust(DrawImageColorAdjust drawImageColorAdjust)
		{ setValues(drawImageColorAdjust.hue, drawImageColorAdjust.saturation, drawImageColorAdjust.brightness, drawImageColorAdjust.blendMode); }

	public DrawImageColorAdjust(double hue, double saturation, double brightness, BlendMode blendMode)
		{ setValues(hue, saturation, brightness, blendMode); }

	public DrawImageColorAdjust(double hue, double saturation, double brightness)
		{ setValues(hue, saturation, brightness); }

	public double getHue()
		{ return hue; }

	public double getSaturation()
		{ return saturation; }

	public double getBrightness()
	 { return brightness; }

	public BlendMode getBlendMode()
		{ return blendMode; }

	public void setValues(double hue, double saturation, double brightness, BlendMode blendMode) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
		this.blendMode = blendMode == null ? BlendMode.SRC_ATOP : blendMode;
	}	
	
	public void setValues(double hue, double saturation, double brightness)
		{ setValues(hue, saturation, brightness, null); }
	
	public void setHue(double hue)
		{ this.hue = hue; }

	public void setSaturation(double saturation)
		{ this.saturation = saturation; }

	public void setBrightness(double brightness)
		{ this.brightness = brightness; }

	public void incValues(double hue, double saturation, double brightness) {
		incHue(hue);
		incSaturation(saturation);
		incBrightness(brightness);
	}

	public void incHue(double value)
		{ hue += value; }
	
	public void incSaturation(double value)
		{ saturation += value; }
	
	public void incBrightness(double value)
		{ brightness += value; }

	public void setBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

}
