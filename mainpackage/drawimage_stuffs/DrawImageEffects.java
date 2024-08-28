package drawimage_stuffs;

import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class DrawImageEffects {

	private DrawImageColorAdjust drawImageColorAdjust;
	private DrawImageColorTint drawImageColorTint;
	private DrawImageGaussianBlur drawImageGaussianBlur;
	private DrawImageBloom drawImageBloom;
	private DrawImageDropShadow drawImageDropShadow;
	private DrawImageInnerShadow drawImageInnerShadow;
	private DrawImageGlow drawImageGlow;
	private DrawImageSepiaTone drawImageSepiaTone;
	private DrawImageMotionBlur drawImageMotionBlur;
	
	public DrawImageEffects(DrawImageEffects spriteEffects) {
		drawImageColorAdjust = spriteEffects.drawImageColorAdjust == null ? null : new DrawImageColorAdjust(spriteEffects.drawImageColorAdjust);
		drawImageColorTint = spriteEffects.drawImageColorTint == null ? null : new DrawImageColorTint(spriteEffects.drawImageColorTint);
		drawImageGlow = spriteEffects.drawImageGlow == null ? null : new DrawImageGlow(spriteEffects.drawImageGlow);
		drawImageBloom = spriteEffects.drawImageBloom == null ? null : new DrawImageBloom(spriteEffects.drawImageBloom);
		drawImageGaussianBlur = spriteEffects.drawImageGaussianBlur == null ? null : new DrawImageGaussianBlur(spriteEffects.drawImageGaussianBlur);
		drawImageDropShadow = spriteEffects.drawImageDropShadow == null ? null : new DrawImageDropShadow(spriteEffects.drawImageDropShadow);
		drawImageInnerShadow = spriteEffects.drawImageInnerShadow == null ? null : new DrawImageInnerShadow(spriteEffects.drawImageInnerShadow);
		drawImageSepiaTone = spriteEffects.drawImageSepiaTone == null ? null : new DrawImageSepiaTone(spriteEffects.drawImageSepiaTone);
		drawImageMotionBlur = spriteEffects.drawImageMotionBlur == null ? null : new DrawImageMotionBlur(spriteEffects.drawImageMotionBlur);
	}
	
	public DrawImageEffects() {
		drawImageColorAdjust = null;
		drawImageColorTint = null;
		drawImageGlow = null;
		drawImageBloom = null;
		drawImageGaussianBlur = null;
		drawImageDropShadow = null;
		drawImageInnerShadow = null;
		drawImageSepiaTone = null;
		drawImageMotionBlur = null;
	}
	
	public boolean haveEffect() {
		return drawImageColorAdjust != null || drawImageColorTint != null || drawImageGlow != null ||
						drawImageBloom != null || drawImageGaussianBlur != null || drawImageDropShadow != null ||
						drawImageInnerShadow != null || drawImageSepiaTone != null || drawImageMotionBlur != null;
	}
	
	public DrawImageColorAdjust getColorAdjust()
		{ return drawImageColorAdjust; }
	
	public void setColorAdjust(double hue, double saturation, double brightness, BlendMode blendMode) {
		if (drawImageColorAdjust == null)
			drawImageColorAdjust = new DrawImageColorAdjust(hue, saturation, brightness, blendMode);
		else
			drawImageColorAdjust.setValues(hue, saturation, brightness, blendMode);
	}
	
	public void setColorAdjust(double hue, double saturation, double brightness)
		{ setColorAdjust(hue, saturation, brightness, null); }
	
	public void removeColorAdjust()
		{ drawImageColorAdjust = null; }
	
	public DrawImageColorTint getColorTint()
		{ return drawImageColorTint; }
	
	public void setColorTint(double red, double green, double blue, double alpha, BlendMode blendMode) {
		if (drawImageColorTint == null)
			drawImageColorTint = new DrawImageColorTint(red, green, blue, alpha, blendMode);
		else
			drawImageColorTint.setValues(red, green, blue, alpha, blendMode);
	}
	
	public void setColorTint(double red, double green, double blue, BlendMode blendMode)
		{ setColorTint(red, green, blue, 1, blendMode); }
	
	public void setColorTint(double red, double green, double blue, double alpha)
		{ setColorTint(red, green, blue, alpha, null); }
	
	public void setColorTint(double red, double green, double blue)
		{ setColorTint(red, green, blue, 0.5, null); }
	
	public void removeColorTint()
		{ drawImageColorTint = null; }
	
	public DrawImageGaussianBlur getGaussianBlur()
		{ return drawImageGaussianBlur; }
	
	public void setGaussianBlur(Integer radius, BlendMode blendMode) {
		if (drawImageGaussianBlur == null)
			drawImageGaussianBlur = new DrawImageGaussianBlur(radius, blendMode);
		else
			drawImageGaussianBlur.setValues(radius, blendMode);
	}
	
	public void setGaussianBlur(Integer radius)
		{ setGaussianBlur(radius, null); }
	
	public void removeGaussianBlur()
		{ drawImageGaussianBlur = null; }
	
	public DrawImageBloom getBloom()
		{ return drawImageBloom; }
	
	public void setBloom(Double threshold, BlendMode blendMode) {
		if (drawImageBloom == null)
			drawImageBloom = new DrawImageBloom(threshold, blendMode);
		else
			drawImageBloom.setValues(threshold, blendMode);
	}
	
	public void setBloom(Double threshold)
		{ setBloom(threshold, null); }
	
	public void removeBloom()
		{ drawImageBloom = null; }
	
	public DrawImageGlow getGlow()
		{ return drawImageGlow; }
	
	public void setGlow(Integer level, BlendMode blendMode) {
		if (drawImageGlow == null)
			drawImageGlow = new DrawImageGlow(level, blendMode);
		else
			drawImageGlow.setValues(level, blendMode);
	}
	
	public void setGlow(Integer level)
		{ setGlow(level, null); }
	
	public void removeGlow()
		{ drawImageGlow = null; }
	
	public DrawImageInnerShadow getInnerShadow()
		{ return drawImageInnerShadow; }
	
	public void setInnerShadow(double offsetX, double offsetY, Color color, BlendMode blendMode) {
		if (drawImageInnerShadow == null)
			drawImageInnerShadow = new DrawImageInnerShadow(offsetX, offsetY, color, blendMode);
		else
			drawImageInnerShadow.setValues(offsetX, offsetY, color, blendMode);
	}
	
	public void setInnerShadow(double offsetX, double offsetY, Color color)
		{ setInnerShadow(offsetX, offsetY, color, null); }
	
	public void setInnerShadow(double offsetX, double offsetY, BlendMode blendMode)
		{ setInnerShadow(offsetX, offsetY, null, blendMode); }
	
	public void setInnerShadow(double offsetX, double offsetY)
		{ setInnerShadow(offsetX, offsetY, null, null); }
	
	public void removeInnerShadow()
		{ drawImageInnerShadow = null; }

	public DrawImageDropShadow getDropShadow()
		{ return drawImageDropShadow; }
	
	public void setDropShadow(double offsetX, double offsetY, Color color, BlendMode blendMode) {
		if (drawImageDropShadow == null)
			drawImageDropShadow = new DrawImageDropShadow(offsetX, offsetY, color, blendMode);
		else
			drawImageDropShadow.setValues(offsetX, offsetY, color, blendMode);
	}
	
	public void setDropShadow(double offsetX, double offsetY, Color color)
		{ setDropShadow(offsetX, offsetY, color, null); }
	
	public void setDropShadow(double offsetX, double offsetY, BlendMode blendMode)
		{ setDropShadow(offsetX, offsetY, null, blendMode); }
	
	public void setDropShadow(double offsetX, double offsetY)
		{ setDropShadow(offsetX, offsetY, null, null); }
	
	public void removeDropShadow()
		{ drawImageDropShadow = null; }

	public DrawImageSepiaTone getSepiaTone()
		{ return drawImageSepiaTone; }
	
	public void setSepiaTone(Double level, BlendMode blendMode) {
		if (drawImageSepiaTone == null)
			drawImageSepiaTone = new DrawImageSepiaTone(level, blendMode);
		else
			drawImageSepiaTone.setValues(level, blendMode);
	}
	
	public void setSepiaTone(Double level)
		{ setSepiaTone(level, null); }
	
	public void removeSepiaTone()
		{ drawImageSepiaTone = null; }

	public DrawImageMotionBlur getMotionBlur()
		{ return drawImageMotionBlur; }
	
	public void setMotionBlur(double angle, double radius, BlendMode blendMode) {
		if (drawImageMotionBlur == null)
			drawImageMotionBlur = new DrawImageMotionBlur(angle, radius, blendMode);
		else
			drawImageMotionBlur.setValues(angle, radius, blendMode);
	}
	
	public void setMotionBlur(double angle, double radius)
		{ setMotionBlur(angle, radius, null); }
	
	public void removeMotionBlur()
		{ drawImageMotionBlur = null; }

}
