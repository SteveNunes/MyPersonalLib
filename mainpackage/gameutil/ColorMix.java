package gameutil;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class ColorMix {
	
	private List<Color> pallete;
	
	public ColorMix(List<Color> colorMixPallete) {
		pallete = new ArrayList<>(colorMixPallete);
	}
	
	public ColorMix() {
		pallete = PalleteTools.newColorMixPallete();
	}
	
	public List<Color> getColorMixPallete() {
		return pallete;
	}
	
	public int getRedIndex() {
		return (int)(pallete.get(1).getRed() * 5);
	}
	
	public ColorMix setRedIndex(int index) {
		if (index < 0 || index > 2)
			throw new RuntimeException("Invalid index (Expected: 0 - 2)");
		pallete.set(1, new Color((double)index / 5, pallete.get(1).getGreen(), pallete.get(1).getBlue(), 1));
		return this;
	}
	
	public double getRedStrenght() {
		return pallete.get(2).getRed();
	}
	
	public ColorMix setRedStrenght(double strenght) {
		if (strenght < 0 || strenght > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(2, new Color(strenght, pallete.get(2).getGreen(), pallete.get(2).getBlue(), 1));
		return this;
	}
	
	public int getGreenIndex() {
		return (int)(pallete.get(1).getGreen() * 5);
	}
	
	public ColorMix setGreenIndex(int index) {
		if (index < 0 || index > 2)
			throw new RuntimeException("Invalid index (Expected: 0 - 2)");
		pallete.set(1, new Color(pallete.get(1).getRed(), (double)index / 5, pallete.get(1).getBlue(), 1));
		return this;
	}
	
	public double getGreenStrenght() {
		return pallete.get(2).getGreen();
	}
	
	public ColorMix setGreenStrenght(double strenght) {
		if (strenght < 0 || strenght > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(2, new Color(pallete.get(2).getRed(), strenght, pallete.get(2).getBlue(), 1));
		return this;
	}
	
	public int getBlueIndex() {
		return (int)(pallete.get(1).getBlue() * 5);
	}
	
	public ColorMix setBlueIndex(int index) {
		if (index < 0 || index > 2)
			throw new RuntimeException("Invalid index (Expected: 0 - 2)");
		pallete.set(1, new Color(pallete.get(1).getRed(), pallete.get(1).getGreen(), (double)index / 5, 1));
		return this;
	}
	
	public double getBlueStrenght() {
		return pallete.get(2).getBlue();
	}
	
	public ColorMix setBlueStrenght(double strenght) {
		if (strenght < 0 || strenght > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(2, new Color(pallete.get(2).getRed(), pallete.get(2).getGreen(), strenght, 1));
		return this;
	}
	
	public double getGlobalOpacity() {
		return pallete.get(6).getRed();
	}
	
	public ColorMix setGlobalOpacity(double opacity) {
		if (opacity < 0 || opacity > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(6, new Color(opacity, pallete.get(6).getGreen(), pallete.get(6).getBlue(), 1));
		return this;
	}
	
	public ColorMix setColorsIndex(int redIndex, int greenIndex, int blueIndex) {
		setRedIndex(redIndex);
		setGreenIndex(greenIndex);
		setBlueIndex(blueIndex);
		return this;
	}
	
	public ColorMix setColorsStrenght(double redStrenght, double greenStrenght, double blueStrenght) {
		setRedStrenght(redStrenght);
		setGreenStrenght(greenStrenght);
		setBlueStrenght(blueStrenght);
		return this;
	}
	
	public double getColorAdjustHue() {
		double v = pallete.get(3).getRed();
		return pallete.get(4).getRed() == 1d ? -v : v;
	}
	
	public ColorMix setColorAdjustHue(double hue) {
		if (hue < -1 || hue > 1)
			throw new RuntimeException("Invalid value (Expected: -1.0 - 1.0)");
		pallete.set(3, new Color(Math.abs(hue), pallete.get(3).getGreen(), pallete.get(3).getBlue(), 1));
		pallete.set(4, new Color(hue < 0d ? 1 : 0, pallete.get(4).getGreen(), pallete.get(4).getBlue(), 1));
		return this;
	}
	
	public double getColorAdjustSaturation() {
		double v = pallete.get(3).getGreen();
		return pallete.get(4).getGreen() == 1d ? -v : v;
	}
	
	public ColorMix setColorAdjustSaturation(double saturation) {
		if (saturation < -1 || saturation > 1)
			throw new RuntimeException("Invalid value (Expected: -1.0 - 1.0)");
		pallete.set(3, new Color(pallete.get(3).getRed(), Math.abs(saturation), pallete.get(3).getBlue(), 1));
		pallete.set(4, new Color(pallete.get(4).getRed(), saturation < 0d ? 1 : 0, pallete.get(4).getBlue(), 1));
		return this;
	}
	
	public double getColorAdjustBrightness() {
		double v = pallete.get(3).getBlue();
		return pallete.get(4).getBlue() == 1d ? -v : v;
	}
	
	public ColorMix setColorAdjustBrightness(double brightness) {
		if (brightness < -1 || brightness > 1)
			throw new RuntimeException("Invalid value (Expected: -1.0 - 1.0)");
		pallete.set(3, new Color(pallete.get(3).getRed(), pallete.get(3).getGreen(), Math.abs(brightness), 1));
		pallete.set(4, new Color(pallete.get(4).getRed(), pallete.get(4).getGreen(), brightness < 0d ? 1 : 0, 1));
		return this;
	}
	
	public boolean getColorAdjustState() {
		return (int)pallete.get(6).getGreen() == 1;
	}
	
	public ColorMix setColorAdjustState(boolean state) {
		pallete.set(6, new Color(pallete.get(6).getRed(), state ? 1 : 0, pallete.get(6).getBlue(), 1));
		return this;
	}
	
	public ColorMix setColorAdjustValues(double hue, double saturation, double brightness) {
		setColorAdjustHue(hue);
		setColorAdjustSaturation(saturation);
		setColorAdjustBrightness(brightness);
		return this;
	}
	
	public ColorMix setColorAdjustValues(double hue, double saturation, double brightness, boolean state) {
		setColorAdjustHue(hue);
		setColorAdjustSaturation(saturation);
		setColorAdjustBrightness(brightness);
		setColorAdjustState(state);
		return this;
	}
	
	public double getColorTintRed() {
		return pallete.get(5).getRed();
	}
	
	public ColorMix setColorTintRed(double red) {
		if (red < 0 || red > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(5, new Color(red, pallete.get(5).getGreen(), pallete.get(5).getBlue(), 1));
		return this;
	}
	
	public double getColorTintGreen() {
		return pallete.get(5).getGreen();
	}
	
	public ColorMix setColorTintGreen(double green) {
		if (green < 0 || green > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(5, new Color(pallete.get(5).getRed(), green, pallete.get(5).getBlue(), 1));
		return this;
	}
	
	public double getColorTintBlue() {
		return pallete.get(5).getBlue();
	}
	
	public ColorMix setColorTintBlue(double blue) {
		if (blue < 0 || blue > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(5, new Color(pallete.get(5).getRed(), pallete.get(5).getGreen(), blue, 1));
		return this;
	}
	
	public double getColorTintOpacity() {
		return pallete.get(6).getBlue();
	}
	
	public ColorMix setColorTintOpacity(double opacity) {
		if (opacity < 0 || opacity > 1)
			throw new RuntimeException("Invalid value (Expected: 0.0 - 1.0)");
		pallete.set(6, new Color(pallete.get(6).getRed(), pallete.get(6).getGreen(), opacity, 1));
		return this;
	}
	
	public ColorMix setColorTintValues(double red, double green, double blue) {
		setColorTintRed(red);
		setColorTintGreen(green);
		setColorTintBlue(blue);
		return this;
	}
	
	public ColorMix setColorTintValues(double red, double green, double blue, double opacity) {
		setColorTintRed(red);
		setColorTintGreen(green);
		setColorTintBlue(blue);
		setColorTintOpacity(opacity);
		return this;
	}
	
	public boolean getSepiaToneState() {
		return (int)pallete.get(7).getRed() == 1;
	}
	
	public ColorMix setSepiaToneState(boolean state) {
		pallete.set(7, new Color(state ? 1 : 0, pallete.get(7).getGreen(), pallete.get(7).getBlue(), 1));
		return this;
	}
	
	public double getSepiaToneLevel() {
		return pallete.get(7).getGreen();
	}
	
	public ColorMix setSepiaToneLevel(double level) {
		if (level < 0 || level > 1)
			throw new RuntimeException("Invalid level (Expected: 0.0 - 1.0)");
		pallete.set(7, new Color(pallete.get(7).getRed(), level, pallete.get(7).getBlue(), 1));
		return this;
	}

	public ColorMix setSepiaToneValues(double level, boolean state) {
		setSepiaToneLevel(level);
		setSepiaToneState(state);
		return this;
	}

	public boolean getBloomState() {
		return (int)pallete.get(8).getRed() == 1;
	}
	
	public ColorMix setBloomState(boolean state) {
		pallete.set(8, new Color(state ? 1 : 0, pallete.get(8).getGreen(), pallete.get(8).getBlue(), 1));
		return this;
	}
	
	public double getBloomThreshold() {
		return pallete.get(8).getGreen();
	}
	
	public ColorMix setBloomThreshold(double threshold) {
		if (threshold < 0 || threshold > 1)
			throw new RuntimeException("Invalid threshold (Expected: 0.0 - 1.0)");
		pallete.set(8, new Color(pallete.get(8).getRed(), threshold, pallete.get(8).getBlue(), 1));
		return this;
	}
	
	public ColorMix setBloomValues(double threshold, boolean state) {
		setBloomThreshold(threshold);
		setBloomState(state);
		return this;
	}

	public boolean getGlowState() {
		return (int)pallete.get(7).getBlue() == 1;
	}
	
	public ColorMix setGlowState(boolean state) {
		pallete.set(7, new Color(pallete.get(7).getRed(), pallete.get(7).getGreen(), state ? 1 : 0, 1));
		return this;
	}
	
	public double getGlowLevel() {
		return pallete.get(8).getBlue();
	}
	
	public ColorMix setGlowLevel(double level) {
		if (level < 0 || level > 1)
			throw new RuntimeException("Invalid threshold (Expected: 0.0 - 1.0)");
		pallete.set(8, new Color(pallete.get(8).getRed(), pallete.get(8).getGreen(), level, 1));
		return this;
	}
	
	public ColorMix setGlowValues(double level, boolean state) {
		setGlowLevel(level);
		setGlowState(state);
		return this;
	}
	
}
