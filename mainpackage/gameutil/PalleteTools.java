package gameutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import drawimage_stuffs.DrawImageEffects;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public abstract class PalleteTools {

	private static Color transparentColor = Color.TRANSPARENT;
	
	public static Color getTransparentColor() {
		return transparentColor;
	}

	public static void setTransparentColor(Color transparentColor) {
		PalleteTools.transparentColor = transparentColor;
	}

	public static boolean isOldColorMixPallete(List<Color> pallete) { // Retorna true se a palleta for do tipo ColorMix antes da alteração que permite valores negativos para Saturation, Hue e Brightness
		if (pallete == null || pallete.size() != 9)
			return false;
		return pallete.get(0).equals(Color.valueOf("#123456FF")) && pallete.get(8).equals(Color.valueOf("#654321FF"));
	}
	
	public static boolean isColorMixPallete(List<Color> pallete) {
		if (pallete == null || pallete.size() != 10)
			return false;
		return pallete.get(0).equals(Color.valueOf("#123456FF")) && pallete.get(9).equals(Color.valueOf("#654321FF"));
	}
	
	public static List<Color> newColorMixPallete() { //EM NENHUM CASO O ULTIMO VALOR DA COR (OPACITY) DEVE SER DIFERENTE DE 1 POIS ISSO ZOA COM O VALOR FINAL DA COR
		return new ArrayList<>(Arrays.asList(
				Color.valueOf("#123456FF"), //IDENTIFICADOR DE COLOR_MIX_PALLETE
				Color.valueOf("#003366FF"), //R * 5, G * 5, B * 5 (POSICAO DAS CORES)
				Color.WHITE, // R, G, B (%)
				Color.BLACK, // COLOR_ADJUST (HUE, SATURATION, BRIGHTNESS)
				Color.BLACK, // COLOR_ADJUST COMPLEMENTO (HUE, SATURATION, BRIGHTNESS) (0 se for valor positivo, 1 se for valor negativo)
				Color.WHITE, // COLOR_TINT (RED, GREEN, BLUE)
				Color.valueOf("#FF0000FF"), // GLOBAL OPACITY, COLOR_ADJUST STATE, COLOR_TINT OPACITY
				Color.valueOf("#00FF00FF"), // SEPIA_TONE STATE, SEPIA_TONE LEVEL, GLOW STATE
				Color.valueOf("#0000FFFF"),  // BLOOM STATE, BLOOM LEVEL, GLOW LEVEL
				Color.valueOf("#654321FF"))); //IDENTIFICADOR DE COLOR_MIX_PALLETE
	}

	public static WritableImage applyColorMixPalleteOnImage(WritableImage originalImage, List<Color> pattern) {
		return applyColorMixPalleteOnImage(originalImage, pattern, transparentColor);
	}

	public static WritableImage applyColorMixPalleteOnImage(WritableImage originalImage, List<Color> colorMixPallete, Color transparentColor) {
		if (!isColorMixPallete(colorMixPallete))
			throw new RuntimeException("Invalid Color Mix Pallete");
		ColorMix colorMix = new ColorMix(colorMixPallete);
		int w = (int)originalImage.getWidth(), h = (int)originalImage.getHeight() - 1;
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		WritableImage i = new WritableImage(w, h);
		PixelReader pr = originalImage.getPixelReader();
		PixelWriter pw = i.getPixelWriter();
		int r = colorMix.getRedIndex(), g = colorMix.getGreenIndex(), b = colorMix.getBlueIndex();
		double opacity = colorMix.getGlobalOpacity(), rv = colorMix.getRedStrenght(),
					 gv = colorMix.getGreenStrenght(), bv = colorMix.getBlueStrenght();
		for (int y = 1; y <= h; y++)
			for (int x = 0; x < w; x++) {
				Color col = pr.getColor(x, y);
				double[] rgba = {col.getRed(), col.getGreen(), col.getBlue(), col.getOpacity()};
				Color col2 = new Color(rgba[r] * rv, rgba[g] * gv, rgba[b] * bv, opacity);
				pw.setColor(x, y - 1, !col.equals(transparentColor) ?	col2 : transparentColor);
			}
		gc.setFill(transparentColor);
		gc.fillRect(0, 0, w, h);
		DrawImageEffects effects = new DrawImageEffects();
		if (colorMix.getColorAdjustState())
			effects.setColorAdjust(colorMix.getColorAdjustHue(),
														 colorMix.getColorAdjustSaturation(),
														 colorMix.getColorAdjustBrightness(), BlendMode.SRC_ATOP);
		if (colorMix.getColorTintOpacity() > 0.0)
			effects.setColorTint(colorMix.getColorTintRed(),
													 colorMix.getColorTintGreen(),
													 colorMix.getColorTintBlue(),
													 colorMix.getColorTintOpacity(), BlendMode.SRC_ATOP);
		if (colorMix.getSepiaToneState())
			effects.setSepiaTone(colorMix.getSepiaToneLevel(), BlendMode.SRC_ATOP);
		if (colorMix.getBloomState())
			effects.setBloom(colorMix.getBloomThreshold(), BlendMode.SRC_ATOP);
		if (colorMix.getGlowState())
			effects.setGlow(colorMix.getGlowLevel(), BlendMode.SRC_ATOP);
		ImageUtils.drawImage(gc, i, 0, 0, effects);
		return ImageUtils.getCanvasSnapshot(c);
	}

	public static WritableImage applyColorPalleteOnImage(WritableImage originalImage, List<Color> originalPallete, List<Color> currentPallete) {
		return applyColorPalleteOnImage(originalImage, originalPallete, currentPallete, transparentColor);
	}

	public static WritableImage applyColorPalleteOnImage(WritableImage originalImage, List<Color> originalPallete, List<Color> currentPallete, Color transparentColor) {
		int w = (int)originalImage.getWidth(), h = (int)originalImage.getHeight();
		WritableImage wi = new WritableImage(w, h - 1);
		PixelReader pr = originalImage.getPixelReader();
		PixelWriter pw = wi.getPixelWriter();
		for (int y = 1; y < h; y++)
			for (int x = 0; x < w; x++) {
				Color color = pr.getColor(x, y);
				if (y == 0)
					pw.setColor(x, y - 1, transparentColor);
				else if (!color.equals(transparentColor) && originalPallete.contains(color))
					pw.setColor(x, y - 1, currentPallete.get(originalPallete.indexOf(color)));
				else
					pw.setColor(x, y - 1, color);
			}
		return wi;
	}

	public static List<List<Color>> getPalleteListFromImage(WritableImage image) {
		return getPalleteListFromImage(image, transparentColor);
	}

	public static List<List<Color>> getPalleteListFromImage(WritableImage image, Color transparentColor) {
		List<List<Color>> palletes = new ArrayList<>();
		List<Color> pallete = new ArrayList<>();
		PixelReader pr = image.getPixelReader();
		Color previewColor = transparentColor;
		for (int x = 0; x < image.getWidth(); x++) {
			Color color = pr.getColor(x, 0);
			if (color.equals(transparentColor)) {
				if (isOldColorMixPallete(pallete)) // Se a paleta for do tipo ColorMix antigo, converter para o novo formato
					pallete.add(4, Color.valueOf("000000FF"));
				if (!pallete.isEmpty()) {
					palletes.add(new ArrayList<>(pallete));
					pallete.clear();
					if (palletes.size() > 1 && palletes.get(palletes.size() - 1).size() != palletes.get(0).size())
						return null;
				}
				if (color.equals(previewColor))
					break;
			}
			else
				pallete.add(color);
			previewColor = color;
		}
		return palletes.isEmpty() ? null : palletes;
	}

	public static List<Color> getPalleteFromImage(WritableImage image) {
		return getPalleteFromImage(image, transparentColor);
	}

	public static List<Color> getPalleteFromImage(WritableImage image, Color transparentColor) {
		List<Color> pallete = new ArrayList<>();
		int w = (int)image.getWidth(), h = (int)image.getHeight();
		PixelReader pr = image.getPixelReader();
		for (int y = 1; y < h; y++)
			for (int x = 0; x < w; x++) {
				Color color = pr.getColor(x, y);
				if (!color.equals(transparentColor) && !pallete.contains(color))
					pallete.add(color);
			}
		return pallete.isEmpty() ? null : pallete;
	}
	
}
