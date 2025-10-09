package gui.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import drawimage_stuffs.DrawImageEffects;
import enums.ImageFlip;
import enums.ImageScanOrientation;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import util.DesktopUtils;

public abstract class ImageUtils {

	private static Robot robot;
	private static Object ignoreColor = Color.TRANSPARENT;
	private static ImageScanOrientation imageScanOrientation = ImageScanOrientation.HORIZONTAL;
	
	static {
		try {
			robot = new Robot();
		}
		catch (Exception e) {
			robot = null;
		}
	}

	public static BufferedImage cloneBufferedImage(BufferedImage image) {
		return removeBgColor(image, -Integer.MAX_VALUE, -1);
	}

	public static WritableImage cloneWritableImage(WritableImage image) {
		return removeBgColor(image, -Integer.MAX_VALUE, -1);
	}

	/**
	 * Remove a cor especificada da imagem (de acordo com o valor de
	 * {@code toleranceThreshold} (Que vai de 0 a 255) Quanto mais próximo de 255,
	 * menos tons da cor especificada serão removidos (Se usar 255, apenas a cor
	 * informada será removido da imagem).
	 * 
	 * @param image              - A imagem a ter a cor de fundo removida
	 * @param toleranceThreshold - Valor de tolerância
	 * @return A imagem com a cor de fundo removida
	 */

	public static BufferedImage removeBgColor(BufferedImage image, int removeColorArgb, int toleranceThreshold) {
		int w = (int) image.getWidth(), h = (int) image.getHeight();
		BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] rgba = getRgbaArray(removeColorArgb);
		int r = rgba[0], g = rgba[1], b = rgba[2];
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				int[] rgba2 = getRgbaArray(image.getRGB(x, y));
				int rr = rgba2[0], gg = rgba2[1], bb = rgba2[2];
				int r2 = (r - toleranceThreshold) < 0 ? 0 : r - toleranceThreshold;
				int g2 = (g - toleranceThreshold) < 0 ? 0 : g - toleranceThreshold;
				int b2 = (b - toleranceThreshold) < 0 ? 0 : b - toleranceThreshold;
				int r3 = (r + toleranceThreshold) > 255 ? 255 : r + toleranceThreshold;
				int g3 = (g + toleranceThreshold) > 255 ? 255 : g + toleranceThreshold;
				int b3 = (b + toleranceThreshold) > 255 ? 255 : b + toleranceThreshold;
				if (removeColorArgb != -Integer.MAX_VALUE && toleranceThreshold != -1 && rr <= r3 && rr >= r2 && gg <= g3 && gg >= g2 && bb <= b3 && bb >= b2)
					outputImage.setRGB(x, y, getRgba(0, 0, 0, 0));
				else
					outputImage.setRGB(x, y, image.getRGB(x, y));
			}
		return outputImage;
	}

	public static BufferedImage removeBgColor(BufferedImage image, int removeColorArgb) {
		return removeBgColor(image, removeColorArgb, 0);
	}

	public static WritableImage removeBgColor(WritableImage image, int removeColorArgb, int toleranceThreshold) {
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		WritableImage outputImage = new WritableImage(w, h);
		PixelReader pixelReader = image.getPixelReader();
		PixelWriter pixelWriter = outputImage.getPixelWriter();
		int[] rgba = getRgbaArray(removeColorArgb);
		int r = rgba[0], g = rgba[1], b = rgba[2];
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				int[] rgba2 = getRgbaArray(pixelReader.getArgb(x, y));
				int rr = rgba2[0], gg = rgba2[1], bb = rgba2[2];
				int r2 = (r - toleranceThreshold) < 0 ? 0 : r - toleranceThreshold;
				int g2 = (g - toleranceThreshold) < 0 ? 0 : g - toleranceThreshold;
				int b2 = (b - toleranceThreshold) < 0 ? 0 : b - toleranceThreshold;
				int r3 = (r + toleranceThreshold) > 255 ? 255 : r + toleranceThreshold;
				int g3 = (g + toleranceThreshold) > 255 ? 255 : g + toleranceThreshold;
				int b3 = (b + toleranceThreshold) > 255 ? 255 : b + toleranceThreshold;
				if (removeColorArgb != -1 && toleranceThreshold != -1 && rr <= r3 && rr >= r2 && gg <= g3 && gg >= g2 && bb <= b3 && bb >= b2)
					pixelWriter.setArgb(x, y, getRgba(0, 0, 0, 0));
				else
					pixelWriter.setArgb(x, y, pixelReader.getArgb(x, y));
			}
		return outputImage;
	}

	public static WritableImage removeBgColor(WritableImage image, int removeColorArgb) {
		return removeBgColor(image, removeColorArgb, 0);
	}

	public static Image removeBgColor(Image image, Color removeColor, int toleranceThreshold) {
		return SwingFXUtils.toFXImage(removeBgColor(SwingFXUtils.fromFXImage(image, null), colorToArgb(removeColor), toleranceThreshold), null);
	}

	public static Image removeBgColor(Image image, int toleranceThreshold) {
		return removeBgColor(image, Color.valueOf("#FF00FF"), toleranceThreshold);
	}

	public static Image removeBgColor(Image image, Color transparentColor) {
		return removeBgColor(image, transparentColor, 0);
	}

	public static Image removeBgColor(Image image) {
		return removeBgColor(image, Color.WHITE, 0);
	}

	public static BufferedImage replaceColor(BufferedImage image, Integer[] beforeArgb, Integer[] afterArgb, Rectangle affectedArea) {
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		boolean ok;
		BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int z, x, y = 0; y < h; y++)
			for (x = 0; x < w; x++) {
				if (affectedArea == null || (x >= affectedArea.getX() && x <= affectedArea.getX() + affectedArea.getWidth() && y >= affectedArea.getX() && y <= affectedArea.getX() + affectedArea.getHeight())) {
					ok = false;
					for (z = 0; !ok && z < beforeArgb.length; z++)
						if (image.getRGB(x, y) == beforeArgb[z]) {
							outputImage.setRGB(x, y, afterArgb[z]);
							ok = true;
						}
					if (!ok)
						outputImage.setRGB(x, y, image.getRGB(x, y));
				}
				else
					outputImage.setRGB(x, y, image.getRGB(x, y));
			}
		return outputImage;
	}

	public static BufferedImage replaceColor(BufferedImage image, int beforeColor, int afterColor, Rectangle affectedArea) {
		return replaceColor(image, new Integer[] { beforeColor }, new Integer[] { afterColor }, affectedArea);
	}

	public static BufferedImage replaceColor(BufferedImage image, Integer[] beforeColors, Integer[] afterColors) {
		return replaceColor(image, beforeColors, afterColors, null);
	}

	public static BufferedImage replaceColor(BufferedImage image, int beforeColor, int afterColor) {
		return replaceColor(image, new Integer[] { beforeColor }, new Integer[] { afterColor }, null);
	}

	public static WritableImage replaceColor(WritableImage image, Integer[] beforeArgb, Integer[] afterArgb, Rectangle affectedArea) {
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		boolean ok;
		WritableImage outputImage = new WritableImage(w, h);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = outputImage.getPixelWriter();
		for (int z, x, y = 0; y < h; y++)
			for (x = 0; x < w; x++) {
				if (affectedArea == null || (x >= affectedArea.getX() && x <= affectedArea.getX() + affectedArea.getWidth() && y >= affectedArea.getX() && y <= affectedArea.getX() + affectedArea.getHeight())) {
					ok = false;
					for (z = 0; !ok && z < beforeArgb.length; z++)
						if (reader.getArgb(x, y) == beforeArgb[z]) {
							writer.setArgb(x, y, afterArgb[z]);
							ok = true;
						}
					if (!ok)
						writer.setArgb(x, y, reader.getArgb(x, y));
				}
				else
					writer.setArgb(x, y, reader.getArgb(x, y));
			}
		return outputImage;
	}

	public static WritableImage replaceColor(WritableImage image, int beforeColor, int afterColor, Rectangle affectedArea) {
		return replaceColor(image, new Integer[] { beforeColor }, new Integer[] { afterColor }, affectedArea);
	}

	public static WritableImage replaceColor(WritableImage image, Integer[] beforeColors, Integer[] afterColors) {
		return replaceColor(image, beforeColors, afterColors, null);
	}

	public static WritableImage replaceColor(WritableImage image, int beforeColor, int afterColor) {
		return replaceColor(image, new Integer[] { beforeColor }, new Integer[] { afterColor }, null);
	}

	public static Image replaceColor(Image image, Color[] beforeColors, Color[] afterColors, Rectangle affectedArea) {
		Integer[] before = new Integer[beforeColors.length];
		Integer[] after = new Integer[beforeColors.length];
		for (int n = 0; n < before.length; n++) {
			before[n] = colorToArgb(beforeColors[n]);
			after[n] = colorToArgb(afterColors[n]);
		}
		return SwingFXUtils.toFXImage(replaceColor(SwingFXUtils.fromFXImage(image, null), before, after, affectedArea), null);
	}

	public static Image replaceColor(Image image, Color beforeColor, Color afterColor, Rectangle affectedArea) {
		return replaceColor(image, new Color[] { beforeColor }, new Color[] { afterColor }, affectedArea);
	}

	public static Image replaceColor(Image image, Color[] beforeColors, Color[] afterColors) {
		return replaceColor(image, beforeColors, afterColors, null);
	}

	public static Image replaceColor(Image image, Color beforeColor, Color afterColor) {
		return replaceColor(image, new Color[] { beforeColor }, new Color[] { afterColor }, null);
	}

	public static BufferedImage getScreenShot(Rectangle captureArea) {
		int w = (int)captureArea.width, h = (int)captureArea.height;
		MultiResolutionImage multiResolutionImage = robot.createMultiResolutionScreenCapture(new Rectangle(0, 0, w, h));
		return (BufferedImage) multiResolutionImage.getResolutionVariant(w, h);
	}

	public static BufferedImage getScreenShot() {
		return getScreenShot(new Rectangle(0, 0, DesktopUtils.getSystemScreenWidth(), DesktopUtils.getSystemScreenHeight()));
	}

	public static WritableImage convertToWritableImage(BufferedImage bufferedImage) {
		return SwingFXUtils.toFXImage(bufferedImage, null);
	}

	public static BufferedImage convertToBufferedImage(WritableImage writableImage) {
		return SwingFXUtils.fromFXImage(writableImage, null);
	}

	public static WritableImage convertToWritableImage(Image image) {
		return new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
	}

	/**
	 * Converte uma java.awt.Image em javafx.Image, redimensionando o tamanho final
	 * 
	 * @param awtImage - java.awt.Image á ser convertida
	 * @param width    - Largura da imagem convertida
	 * @param height   - Altura da imagem convertida
	 * @return - Uma javafx.Image redimensionada
	 */
	public static Image toResizedFXImage(java.awt.Image awtImage, Dimension newSize) {
		BufferedImage bufferedImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bufferedImage.getGraphics().drawImage(awtImage, 0, 0, null);
		int width = (int) newSize.getWidth(), height = (int) newSize.getHeight();
		BufferedImage novaBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		novaBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
		return SwingFXUtils.toFXImage(novaBufferedImage, null);
	}

	/** Converte uma java.awt.Image em javafx.Image */
	public static Image toFXImage(java.awt.Image awtImage) {
		return SwingFXUtils.toFXImage((BufferedImage) awtImage, null);
	}

	/**
	 * Converte uma javafx.Image em java.awt.Image, redimensionando o tamanho final
	 * 
	 * @param fxImage - javafx.Image á ser convertida
	 * @param width   - Largura da imagem convertida
	 * @param height  - Altura da imagem convertida
	 * @param scale   - Escala da imagem convertida (Usar uma constante de
	 *                java.awt.Image.* para pegar um valor compatível)
	 * @return - Uma java.awt.Image redimensionada
	 */
	public static java.awt.Image toResizedAWTImage(Image fxImage, Dimension newSize, int scale) {
		int width = (int) newSize.getWidth(), height = (int) newSize.getHeight();
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
		BufferedImage novaBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		novaBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
		return novaBufferedImage.getScaledInstance(width, height, scale);
	}

	public static java.awt.Image toResizedAWTImage(Image fxImage, Dimension newSize) {
		return toResizedAWTImage(fxImage, newSize, java.awt.Image.SCALE_DEFAULT);
	}

	public static java.awt.Image toAWTImage(Image fxImage) {
		return SwingFXUtils.fromFXImage(fxImage, null);
	}

	public static int[][] convertToRgbArray(BufferedImage image) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		int[][] array = new int[height][width];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				array[y][x] = image.getRGB(x, y);
		return array;
	}

	public static BufferedImage convertToBufferedImage(int[][] rgbArray) {
		BufferedImage bufferedImage = new BufferedImage((int) rgbArray[0].length, (int) rgbArray.length, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < rgbArray.length; y++)
			for (int x = 0; x < rgbArray[y].length; x++)
				bufferedImage.setRGB(x, y, rgbArray[y][x]);
		return bufferedImage;
	}

	// Retorna uma BufferedImage contendo uma parte de outra BufferedImage
	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, Rectangle sourceArea, int rotateAngle, ImageFlip flip) {
		BufferedImage bufferedImage = sourceImage.getSubimage((int) sourceArea.getX(), (int) sourceArea.getY(), (int) sourceArea.getWidth(), (int) sourceArea.getHeight());
		BufferedImage targetImage = new BufferedImage((int) sourceArea.getWidth(), (int) sourceArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = targetImage.createGraphics();
		AffineTransform flipAndRotate = new AffineTransform();
		int imageWidth = targetImage.getWidth(), imageHeight = targetImage.getHeight();
		if (flip != null && flip != ImageFlip.NONE) {
			flipAndRotate.scale(flip == ImageFlip.HORIZONTAL || flip == ImageFlip.BOTH ? -1 : 1, flip == ImageFlip.VERTICAL || flip == ImageFlip.BOTH ? -1 : 1);
			flipAndRotate.translate(-imageWidth, 0);
		}
		if (rotateAngle != 0) {
			double angle = Math.toRadians(rotateAngle);
			flipAndRotate.rotate(angle, imageWidth / 2, imageHeight / 2);
		}
		if (flip != ImageFlip.NONE || rotateAngle != 0)
			g2d.setTransform(flipAndRotate);
		g2d.drawImage(bufferedImage, 0, 0, null);
		g2d.dispose();
		return targetImage;
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, Rectangle sourceArea) {
		return copyAreaFromBufferedImage(sourceImage, sourceArea, 0, null);
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, Rectangle sourceArea, ImageFlip flip) {
		return copyAreaFromBufferedImage(sourceImage, sourceArea, 0, flip);
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, Rectangle sourceArea, int rotateAngle) {
		return copyAreaFromBufferedImage(sourceImage, sourceArea, rotateAngle, null);
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, int rotateAngle, ImageFlip flip) {
		return copyAreaFromBufferedImage(sourceImage, new Rectangle(0, 0, sourceImage.getWidth(), sourceImage.getHeight()), rotateAngle, flip);
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage) {
		return copyAreaFromBufferedImage(sourceImage, 0, null);
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, ImageFlip flip) {
		return copyAreaFromBufferedImage(sourceImage, 0, flip);
	}

	public static BufferedImage copyAreaFromBufferedImage(BufferedImage sourceImage, int rotateAngle) {
		return copyAreaFromBufferedImage(sourceImage, rotateAngle, null);
	}

	// Retorna uma WritableImage contendo uma parte de outra WritableImage
	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		if (sourceArea == null)
			sourceArea = new Rectangle(0, 0, (int) sourceImage.getWidth(), (int) sourceImage.getHeight());
		if (targetSize == null)
			targetSize = sourceArea.getSize();
		if (rotateAngle == null)
			rotateAngle = 0;
		if (opacity == null)
			opacity = 1d;
		if (flip == null)
			flip = ImageFlip.NONE;
		Canvas canvas = new Canvas(targetSize.getWidth(), targetSize.getHeight());
		canvas.getGraphicsContext2D().setImageSmoothing(false);
		drawImage(canvas.getGraphicsContext2D(), sourceImage, (int) sourceArea.getX(), (int) sourceArea.getY(), (int) sourceArea.getWidth(), (int) sourceArea.getHeight(), 0, 0, (int) targetSize.getWidth(), (int) targetSize.getHeight(), flip, rotateAngle, null, null);
		return canvas.snapshot(null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, Integer rotateAngle, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, Integer rotateAngle) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, null, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Dimension targetSize) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, targetSize, null, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Integer rotateAngle, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Integer rotateAngle) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, null, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Rectangle sourceArea) {
		return copyAreaFromWritableImage(sourceImage, sourceArea, null, null, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, rotateAngle, flip, opacity, effects);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, Integer rotateAngle, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, Integer rotateAngle) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, null, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Dimension targetSize) {
		return copyAreaFromWritableImage(sourceImage, null, targetSize, null, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, null, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, null, null, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Integer rotateAngle, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, null, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Integer rotateAngle) {
		return copyAreaFromWritableImage(sourceImage, null, null, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, ImageFlip flip, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, null, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, ImageFlip flip) {
		return copyAreaFromWritableImage(sourceImage, null, null, null, flip, null, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage, Double opacity) {
		return copyAreaFromWritableImage(sourceImage, null, null, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromWritableImage(WritableImage sourceImage) {
		return copyAreaFromWritableImage(sourceImage, null, null, null, ImageFlip.NONE, null, null);
	}

	// Retorna uma WritableImage contendo uma parte de um Canvas
	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		if (sourceArea == null)
			sourceArea = new Rectangle2D(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
		if (targetSize == null)
			targetSize = new Dimension((int) canvas.getWidth(), (int) canvas.getHeight());
		if (rotateAngle == null)
			rotateAngle = 0;
		if (opacity == null)
			opacity = 1d;
		if (flip == null)
			flip = ImageFlip.NONE;
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(sourceArea);
		WritableImage capturedImage = canvas.snapshot(params, null);
		int width = (int) targetSize.getWidth(), height = (int) targetSize.getHeight();
		Canvas canvas2 = new Canvas(width, height);
		canvas2.getGraphicsContext2D().setImageSmoothing(false);
		drawImage(canvas2.getGraphicsContext2D(), capturedImage, (int) sourceArea.getMinX(), (int) sourceArea.getMinY(), (int) sourceArea.getWidth(), (int) sourceArea.getHeight(), 0, 0, (int) targetSize.getWidth(), (int) targetSize.getHeight(), flip, rotateAngle, null, null);
		return canvas2.snapshot(params, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, Integer rotateAngle, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, Integer rotateAngle) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, null, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Dimension targetSize) {
		return copyAreaFromCanvas(canvas, sourceArea, targetSize, null, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, null, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, sourceArea, null, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Integer rotateAngle, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, null, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Integer rotateAngle) {
		return copyAreaFromCanvas(canvas, sourceArea, null, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, null, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, sourceArea, null, null, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea, Double opacity) {
		return copyAreaFromCanvas(canvas, sourceArea, null, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Rectangle2D sourceArea) {
		return copyAreaFromCanvas(canvas, sourceArea, null, null, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return copyAreaFromCanvas(canvas, null, targetSize, rotateAngle, flip, opacity, effects);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, null, targetSize, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, null, targetSize, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, Integer rotateAngle, Double opacity) {
		return copyAreaFromCanvas(canvas, null, targetSize, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, Integer rotateAngle) {
		return copyAreaFromCanvas(canvas, null, targetSize, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, null, targetSize, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, null, targetSize, null, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize, Double opacity) {
		return copyAreaFromCanvas(canvas, null, targetSize, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Dimension targetSize) {
		return copyAreaFromCanvas(canvas, null, targetSize, null, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, null, null, rotateAngle, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Integer rotateAngle, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, null, null, rotateAngle, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Integer rotateAngle, Double opacity) {
		return copyAreaFromCanvas(canvas, null, null, rotateAngle, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Integer rotateAngle) {
		return copyAreaFromCanvas(canvas, null, null, rotateAngle, ImageFlip.NONE, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, ImageFlip flip, Double opacity) {
		return copyAreaFromCanvas(canvas, null, null, null, flip, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, ImageFlip flip) {
		return copyAreaFromCanvas(canvas, null, null, null, flip, null, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas, Double opacity) {
		return copyAreaFromCanvas(canvas, null, null, null, ImageFlip.NONE, opacity, null);
	}

	public static WritableImage copyAreaFromCanvas(Canvas canvas) {
		return copyAreaFromCanvas(canvas, null, null, null, ImageFlip.NONE, null, null);
	}

	// Copia o conteudo de uma WritableImage, sobrepondo a outra WritableImage, e
	// retorna uma nova WritableImage com o resultado da sobreposição
	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		if (sourceArea == null)
			sourceArea = new Rectangle2D(0, 0, (int) sourceImage.getWidth(), (int) sourceImage.getHeight());
		if (targetArea == null)
			targetArea = new Rectangle2D(0, 0, (int) sourceArea.getWidth(), (int) sourceArea.getHeight());
		if (rotateAngle == null)
			rotateAngle = 0;
		if (opacity == null)
			opacity = 1d;
		if (flip == null)
			flip = ImageFlip.NONE;
		Canvas tCanvas = new Canvas(targetImage.getWidth(), targetImage.getHeight());
		GraphicsContext tGc = tCanvas.getGraphicsContext2D();
		tCanvas.getGraphicsContext2D().setImageSmoothing(false);
		tGc.drawImage(targetImage, 0, 0);
		drawImage(tGc, sourceImage, (int) sourceArea.getMinX(), (int) sourceArea.getMinY(), (int) sourceArea.getWidth(), (int) sourceArea.getHeight(), (int) targetArea.getMinX(), (int) targetArea.getMinY(), (int) targetArea.getWidth(), (int) targetArea.getHeight(), flip, rotateAngle, opacity, effects);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		return tCanvas.snapshot(params, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, rotateAngle, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Rectangle2D targetArea) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, targetArea, null, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle, ImageFlip flip) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Integer rotateAngle) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, rotateAngle, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, ImageFlip flip) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage, Double opacity) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, Rectangle2D sourceArea, WritableImage targetImage) {
		return drawImageToImage(sourceImage, sourceArea, targetImage, null, null, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle, ImageFlip flip) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Integer rotateAngle) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, rotateAngle, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, ImageFlip flip) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Rectangle2D targetArea) {
		return drawImageToImage(sourceImage, null, targetImage, targetArea, null, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, flip, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, ImageFlip flip, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, flip, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Double opacity, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, null, opacity, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, DrawImageEffects effects) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, null, null, effects);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, ImageFlip flip, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, flip, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle, ImageFlip flip) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Integer rotateAngle) {
		return drawImageToImage(sourceImage, null, targetImage, null, rotateAngle, null, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, ImageFlip flip) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, flip, null, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage, Double opacity) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, null, opacity, null);
	}

	public static WritableImage drawImageToImage(WritableImage sourceImage, WritableImage targetImage) {
		return drawImageToImage(sourceImage, null, targetImage, null, null, null, null, null);
	}

	public static boolean areEqualImages(WritableImage image1, WritableImage image2, int tolerance) {
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight())
			return false;
		PixelReader reader1 = image1.getPixelReader();
		PixelReader reader2 = image2.getPixelReader();
		int width = (int) image1.getWidth(), height = (int) image1.getHeight();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (tolerance != 0) {
					int[] rgb1 = getRgbaArray(reader1.getArgb(x, y));
					int[] rgb2 = getRgbaArray(reader2.getArgb(x, y));
					for (int i = 0; i < 3; i++)
						if (rgb1[i] < rgb2[i] - tolerance || rgb1[i] > rgb2[i] + tolerance)
							return false;
				}
				else if (reader1.getArgb(x, y) != reader2.getArgb(x, y))
					return false;
		return true;
	}

	public static boolean areEqualImages(WritableImage image1, WritableImage image2) {
		return areEqualImages(image1, image2, 0);
	}

	public static boolean areEqualImages(BufferedImage image1, BufferedImage image2, int tolerance) {
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight())
			return false;
		int width = (int) image1.getWidth(), height = (int) image1.getHeight();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (tolerance != 0) {
					int[] rgb1 = getRgbaArray(image1.getRGB(x, y));
					int[] rgb2 = getRgbaArray(image2.getRGB(x, y));
					for (int i = 0; i < 3; i++)
						if (rgb1[i] < rgb2[i] - tolerance || rgb1[i] > rgb2[i] + tolerance)
							return false;
				}
				else if (image1.getRGB(x, y) != image2.getRGB(x, y))
					return false;
		return true;
	}

	public static boolean areEqualImages(BufferedImage image1, BufferedImage image2) {
		return areEqualImages(image1, image2, 0);
	}

	public static int getRgba(int red, int green, int blue, int alpha) {
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public static int getRgba(int red, int green, int blue) {
		return getRgba(red, green, blue, 255);
	}

	public static int[] getRgbaArray(int rgba) {
		int alpha = (rgba >> 24) & 0xFF;
		int red = (rgba >> 16) & 0xFF;
		int green = (rgba >> 8) & 0xFF;
		int blue = rgba & 0xFF;
		return new int[] { alpha, red, green, blue };
	}

	public static BufferedImage convertImageToBufferedImage(Image image) {
		return SwingFXUtils.fromFXImage(image, new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB));
	}

	public static Image convertWritableImageToImage(WritableImage writableImage) {
		return new ImageView(writableImage).snapshot(new SnapshotParameters(), null);
	}

	public static Image convertBufferedImageToImage(BufferedImage bufferedImage) {
		return SwingFXUtils.toFXImage(bufferedImage, null);
	}
	
	public static BufferedImage loadBufferedImageFromFile(File file, Color removeBgColor) {
		try {
			if (!file.exists())
				throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
			BufferedImage image = ImageIO.read(file);
			if (image == null)
				throw new IOException("Unsupported imagem file: " + file.getAbsolutePath());
			return image;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage loadBufferedImageFromFile(File file) {
		return loadBufferedImageFromFile(file, null);
	}

	public static BufferedImage loadBufferedImageFromFile(String filePath, Color removeBgColor) {
		return loadBufferedImageFromFile(new File(filePath), removeBgColor);
	}

	public static BufferedImage loadBufferedImageFromFile(String filePath) {
		return loadBufferedImageFromFile(filePath, null);
	}

	public static java.awt.Image loadAwtImage(File file) {
		try {
			if (!file.exists())
				throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
			java.awt.Image image = ImageIO.read(file);
			if (image == null)
				throw new IOException("Unsupported imagem file: " + file.getAbsolutePath());
			return image;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static java.awt.Image loadAwtImage(String filePath) {
		return loadAwtImage(new File(filePath));
	}

	public static WritableImage loadWritableImageFromFile(File file, Color removeBgColor) {
		Image image = new Image("file:" + file.getAbsolutePath());
		if (removeBgColor != null)
			image = removeBgColor(image, removeBgColor);
		return convertToWritableImage(image);
	}

	public static WritableImage loadWritableImageFromFile(File file) {
		return loadWritableImageFromFile(file, null);
	}

	public static WritableImage loadWritableImageFromFile(String filePath, Color removeBgColor) {
		return loadWritableImageFromFile(new File(filePath), removeBgColor);
	}

	public static WritableImage loadWritableImageFromFile(String filePath) {
		return loadWritableImageFromFile(filePath, null);
	}

	public static void saveImageToFile(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "png", file);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to save image to disk\n" + e.getMessage());
		}
	}

	public static void saveImageToFile(WritableImage image, File file) {
		saveImageToFile(convertToBufferedImage(image), file);
	}

	public static void saveImageToFile(Image image, File file) {
		saveImageToFile(convertImageToBufferedImage(image), file);
	}

	public static void saveImageToFile(BufferedImage image, String filePath) {
		saveImageToFile(image, new File(filePath));
	}

	public static void saveImageToFile(WritableImage image, String filePath) {
		saveImageToFile(image, new File(filePath));
	}

	public static void saveImageToFile(Image image, String filePath) {
		saveImageToFile(image, new File(filePath));
	}

	public static List<Color> getColorList() {
		List<Color> colorList = new ArrayList<>();
		Field[] fields = Color.class.getDeclaredFields();
		for (Field field : fields)
			if (field.getType().equals(Color.class))
				try {
					Color color = (Color) field.get(null);
					colorList.add(color);
				}
				catch (IllegalAccessException e) {}
		return colorList;
	}

	public static javafx.scene.paint.Color awtColorToFXColor(java.awt.Color awtColor) {
		double red = awtColor.getRed() / 255.0;
		double green = awtColor.getGreen() / 255.0;
		double blue = awtColor.getBlue() / 255.0;
		double alpha = awtColor.getAlpha() / 255.0;
		return new javafx.scene.paint.Color(red, green, blue, alpha);
	}

	public static java.awt.Color fxColorToAwtColor(javafx.scene.paint.Color fxColor) {
		int red = (int) (fxColor.getRed() * 255);
		int green = (int) (fxColor.getGreen() * 255);
		int blue = (int) (fxColor.getBlue() * 255);
		int alpha = (int) (fxColor.getOpacity() * 255);
		return new java.awt.Color(red, green, blue, alpha);
	}

	public static int colorToArgb(Color color) {
		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);
		int alpha = (int) (color.getOpacity() * 255);
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public static Color argbToColor(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		return Color.rgb(r, g, b, a / 255.0);
	}

	public static BufferedImage tintImage(BufferedImage image, Color tint, float tintStrenght) {
		BufferedImage tintedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < tintedImage.getWidth(); x++)
			for (int y = 0; y < tintedImage.getHeight(); y++) {
				Color originalColor = ImageUtils.argbToColor(image.getRGB(x, y));
				Color tintedColor = originalColor.interpolate(tint, tintStrenght);
				if (!originalColor.equals(Color.TRANSPARENT))
					tintedImage.setRGB(x, y, ImageUtils.colorToArgb(tintedColor));
			}
		return tintedImage;
	}

	public static WritableImage tintImage(Image image, Color tint, float tintStrenght) {
		WritableImage tintedImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelReader pixelReader = image.getPixelReader();
		PixelWriter pixelWriter = tintedImage.getPixelWriter();
		for (int x = 0; x < tintedImage.getWidth(); x++)
			for (int y = 0; y < tintedImage.getHeight(); y++) {
				Color originalColor = pixelReader.getColor(x, y);
				Color tintedColor = originalColor.interpolate(tint, tintStrenght);
				if (!originalColor.equals(Color.TRANSPARENT))
					pixelWriter.setColor(x, y, tintedColor);
			}
		return tintedImage;
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, DrawImageEffects effects) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight) {
		drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, opacity, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, Integer rotateAngle, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, Double opacity, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, DrawImageEffects effects) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, null, null, effects);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, Integer rotateAngle) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, Double opacity) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY, ImageFlip flip) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer targetX, Integer targetY) {
		drawImage(gc, image, null, null, null, null, targetX, targetY, null, null, null, null, null, null);
	}

	public static void drawImage(GraphicsContext gc, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		if (sourceX == null)
			sourceX = 0;
		if (sourceY == null)
			sourceY = 0;
		if (sourceWidth == null)
			sourceWidth = (int) image.getWidth();
		if (sourceHeight == null)
			sourceHeight = (int) image.getHeight();
		if (targetWidth == null)
			targetWidth = sourceWidth;
		if (targetHeight == null)
			targetHeight = sourceHeight;
		gc.save();
		gc.translate(targetX + targetWidth / 2, targetY + targetHeight / 2);
		if (flip == null)
			flip = ImageFlip.NONE;
		gc.scale((flip == ImageFlip.HORIZONTAL || flip == ImageFlip.BOTH) ? -1 : 1, (flip == ImageFlip.VERTICAL || flip == ImageFlip.BOTH) ? -1 : 1);
		if (rotateAngle != null && rotateAngle != 0 && rotateAngle % 360 != 0)
			gc.rotate(rotateAngle);
		gc.translate(-targetWidth / 2, -targetHeight / 2);
		if (opacity != null)
			gc.setGlobalAlpha(opacity);
		if (effects != null) {
			Blend blend = new Blend();
			boolean hasEffect = false;
			if (effects.getColorTint() != null) {
				Blend blend2 = new Blend(effects.getColorTint().getBlendMode());
				ColorInput colorInput = new ColorInput();
				colorInput.setPaint(new Color(effects.getColorTint().getRed(), effects.getColorTint().getGreen(), effects.getColorTint().getBlue(), effects.getColorTint().getAlpha()));
				colorInput.setX(0);
				colorInput.setY(0);
				colorInput.setWidth(targetWidth);
				colorInput.setHeight(targetHeight);
				blend2.setTopInput(colorInput);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getColorAdjust() != null) {
				Blend blend2 = new Blend(effects.getColorAdjust().getBlendMode());
				ColorAdjust colorAdjust = new ColorAdjust();
				colorAdjust.setHue(effects.getColorAdjust().getHue());
				colorAdjust.setSaturation(effects.getColorAdjust().getSaturation());
				colorAdjust.setBrightness(effects.getColorAdjust().getBrightness());
				blend2.setTopInput(colorAdjust);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getGlow() != null) {
				Blend blend2 = new Blend(effects.getGlow().getBlendMode());
				Glow glow = new Glow();
				glow.setLevel(effects.getGlow().getLevel());
				blend2.setTopInput(glow);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getBloom() != null) {
				Blend blend2 = new Blend(effects.getBloom().getBlendMode());
				Bloom bloom = new Bloom();
				bloom.setThreshold(effects.getBloom().getThreshold());
				blend2.setTopInput(bloom);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getSepiaTone() != null) {
				Blend blend2 = new Blend(effects.getSepiaTone().getBlendMode());
				SepiaTone sepiaTone = new SepiaTone();
				sepiaTone.setLevel(effects.getSepiaTone().getLevel());
				blend2.setTopInput(sepiaTone);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getInnerShadow() != null) {
				Blend blend2 = new Blend(effects.getInnerShadow().getBlendMode());
				InnerShadow innerShadow = new InnerShadow();
				innerShadow.setOffsetX(effects.getInnerShadow().getOffsetX());
				innerShadow.setOffsetY(effects.getInnerShadow().getOffsetY());
				innerShadow.setColor(effects.getInnerShadow().getColor());
				blend2.setTopInput(innerShadow);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getDropShadow() != null) {
				Blend blend2 = new Blend(effects.getDropShadow().getBlendMode());
				DropShadow dropShadow = new DropShadow();
				dropShadow.setOffsetX(effects.getDropShadow().getOffsetX());
				dropShadow.setOffsetY(effects.getDropShadow().getOffsetY());
				dropShadow.setColor(effects.getDropShadow().getColor());
				blend2.setTopInput(dropShadow);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getMotionBlur() != null) {
				Blend blend2 = new Blend(effects.getMotionBlur().getBlendMode());
				MotionBlur motionBlur = new MotionBlur();
				motionBlur.setAngle(effects.getMotionBlur().getAngle());
				motionBlur.setRadius(effects.getMotionBlur().getRadius());
				blend2.setTopInput(motionBlur);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (effects.getGaussianBlur() != null) {
				Blend blend2 = new Blend(effects.getGaussianBlur().getBlendMode());
				GaussianBlur gaussianBlur = new GaussianBlur();
				gaussianBlur.setRadius(effects.getGaussianBlur().getRadius());
				blend2.setTopInput(gaussianBlur);
				blend2.setBottomInput(blend);
				blend = blend2;
				hasEffect = true;
			}
			if (hasEffect)
				gc.setEffect(blend);
		}
		gc.drawImage(image, sourceX, sourceY, sourceWidth, sourceHeight, 0, 0, targetWidth, targetHeight);
		gc.restore();
	}

	public static Image getRoundedBorderImage(Image image, int size) {
		Canvas canvas = new Canvas(size, size);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.TRANSPARENT);
		gc.clearRect(0, 0, size, size);
		gc.beginPath();
		gc.arc(size / 2, size / 2, size / 2, size / 2, 0, 360);
		gc.clip();
		gc.drawImage(image, 0, 0, size, size);
		WritableImage roundImage = new WritableImage((int) size, (int) size);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		return canvas.snapshot(params, roundImage);
	}

	/** Define a cor a ser ignorada no método isImageContained() e outros
	 *  métodos que invoquem esse método. Nesse caso, todos os pixels com
	 *  essa cor serão ignorados, e só serão comparados os 'outros' pixels.
	 *  Se os 'outros' pixels forem iguais, as imagens são tratadas como iguais. 
	 */
	public static void setImageScanIgnoreColor(Color color)
		{ ignoreColor = color; }
	
	/**
	 * Por padrão, o scan pixel-a-pixel do método isImageContained() é feito
	 * horizontalmente. Em alguns casos, isso pode prejudicar o resultado obtido.
	 * Então use esse método para alterar a orientação do scan pixel-a-pixel.
	 */
	public static void setImageScanOrientation(ImageScanOrientation orientation)
		{ imageScanOrientation = orientation; }
	
	/** Procura uma imagem menor dentro de uma maior. O processo pode ser demorado ou não,
	 *  dependendo do tamanho das imagens, do valor de {@code tolerance} e de {@code thickness}
	 * 
	 * @param smallerImage - Imagem menor á ser procurada dentro da maior
	 * @param largerImage - Imagem maior aonde será procurada a imagem menor
	 * @param searchingArea - Área dentro da imagem grande onde a imagem menor será procurada.
	 *                        Se você tem certeza do local aproximado onde a imagem menor
	 *                        será localizada na imagem maior, defina essa área, fazendo com
	 *                        que a pesquisa seja mais rápida e eficaz.
	 * @param tolerance - Valor de 0 a 255 para a tolerância de cor por pixel (Quanto maior,
	 *                    mais fácil reconhecer a imagem se houver diferença de cor entre os
	 *                    pixels comparados, porém torna o processo mais demorado).
	 * @return - Se a imagem for encontrada, retorna um {@code Point} contendo a coordenada
	 * 					 do primeiro pixel onde a imagem foi encontrada. Caso contrário, retorna {@code null}
	 */
	public static Point isImageContained(BufferedImage smallerImage, BufferedImage largerImage, Rectangle searchingArea, int tolerance) {
		if (tolerance < 0 || tolerance > 255)
			throw new RuntimeException("'tolerance' value must be between 0-255");
		int smallerWidth = (int)smallerImage.getWidth(), smallerHeight = (int)smallerImage.getHeight();
		int largerWidth = (int)largerImage.getWidth(), largerHeight = (int)largerImage.getHeight();
		if (searchingArea == null)
			searchingArea = new Rectangle(0, 0, largerWidth, largerHeight);
		int startX = (int)searchingArea.getX(), startY = (int)searchingArea.getY();
		int width = (int)searchingArea.getWidth(), height = (int)searchingArea.getHeight();
		if (smallerWidth >= largerWidth || smallerHeight >= largerHeight)
			throw new RuntimeException("The 'smallerImage' must be smaler in width and height than 'largerImage'");
		if (width < smallerWidth || height < smallerHeight)
			throw new RuntimeException("'searchingArea' must be equal or higher in size than 'smallerImage' size");
		boolean match = true;
		int xxx = 0, yyy = 0, xx, yy, y, x, i;
		while ((startX + xxx) <= (largerWidth - smallerWidth) && xxx < width && (startY + yyy) <= (largerHeight - smallerHeight) && yyy < height) {
			x = startX + xxx;
			y = startY + yyy;
			match = true;
			for (xx = 0; match && xx < smallerWidth; xx++)
				for (yy = 0; match && yy < smallerHeight; yy++)
					try {
						if (!argbToColor(smallerImage.getRGB(xx, yy)).equals(ignoreColor)) {
							if (tolerance != 0) {
								int[] rgb1 = getRgbaArray(smallerImage.getRGB(xx, yy));
								int[] rgb2 = getRgbaArray(largerImage.getRGB(x + xx, y + yy));
								for (i = 0; match && i < 3; i++)
									if (rgb1[i] < rgb2[i] - tolerance || rgb1[i] > rgb2[i] + tolerance)
										match = false;
							}
							else if (smallerImage.getRGB(xx, yy) != largerImage.getRGB(x + xx, y + yy))
								match = false;
						}
					}
				catch (Exception e) {
					match = false;
					continue;
				}
			if (match)
				return new Point(x, y);
			if (imageScanOrientation  == ImageScanOrientation.HORIZONTAL) {
				if ((startX + ++xxx) >= (largerWidth - smallerWidth)) {
					xxx = 0;
					yyy++;
				}
			}
			else if ((startY + ++yyy) >= (largerHeight - smallerHeight)) {
				yyy = 0;
				xxx++;
			}
		}
		return null;
	}

	public static Point isImageContained(BufferedImage smallerImage, BufferedImage largerImage, Rectangle searchingArea)
		{ return isImageContained(smallerImage, largerImage, searchingArea, 0); }
	
	public static Point isImageContained(BufferedImage smallerImage, BufferedImage largerImage, int tolerance)
		{ return isImageContained(smallerImage, largerImage, new Rectangle(0, 0, (int)largerImage.getWidth(), (int)largerImage.getHeight()), tolerance); }
	
	public static Point isImageContained(BufferedImage smallerImage, BufferedImage largerImage)
		{ return isImageContained(smallerImage, largerImage, 0); }
	
	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage, Rectangle searchingArea, int tolerance) {
		if (tolerance < 0 || tolerance > 255)
			throw new RuntimeException("'tolerance' value must be between 0-255");
		PixelReader smallerReader = smallerImage.getPixelReader();
		PixelReader largerReader = largerImage.getPixelReader();
		int smallerWidth = (int)smallerImage.getWidth(), smallerHeight = (int)smallerImage.getHeight();
		int largerWidth = (int)largerImage.getWidth(), largerHeight = (int)largerImage.getHeight();
		if (searchingArea == null)
			searchingArea = new Rectangle(0, 0, largerWidth, largerHeight);
		int startX = (int)searchingArea.getX(), startY = (int)searchingArea.getY();
		int width = (int)searchingArea.getWidth(), height = (int)searchingArea.getHeight();
		if (smallerWidth >= largerWidth || smallerHeight >= largerHeight)
			throw new RuntimeException("The 'smallerImage' must be smaler in width and height than 'largerImage'");
		if (width < smallerWidth || height < smallerHeight)
			throw new RuntimeException("'searchingArea' must be equal or higher in size than 'smallerImage' size");
		boolean match = true;
		int xxx = 0, yyy = 0, xx, yy, y, x, i;
		while ((startX + xxx) <= (largerWidth - smallerWidth) && xxx < width && (startY + yyy) <= (largerHeight - smallerHeight) && yyy < height) {
			x = startX + xxx;
			y = startY + yyy;
			match = true;
			for (xx = 0; match && xx < smallerWidth; xx++)
				for (yy = 0; match && yy < smallerHeight; yy++)
					try {
						if (!argbToColor(smallerReader.getArgb(xx, yy)).equals(ignoreColor)) {
							if (tolerance != 0) {
								int[] rgb1 = getRgbaArray(smallerReader.getArgb(xx, yy));
								int[] rgb2 = getRgbaArray(largerReader.getArgb(x + xx, y + yy));
								for (i = 0; match && i < 3; i++)
									if (rgb1[i] < rgb2[i] - tolerance || rgb1[i] > rgb2[i] + tolerance)
										match = false;
							}
							else if (smallerReader.getArgb(xx, yy) != largerReader.getArgb(x + xx, y + yy))
								match = false;
						}
					}
				catch (Exception e) {
					match = false;
					continue;
				}
			if (match)
				return new Point(x, y);
			if (imageScanOrientation  == ImageScanOrientation.HORIZONTAL) {
				if ((startX + ++xxx) >= (largerWidth - smallerWidth)) {
					xxx = 0;
					yyy++;
				}
			}
			else if ((startY + ++yyy) >= (largerHeight - smallerHeight)) {
				yyy = 0;
				xxx++;
			}
		}
		return null;
	}
	
	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage, Rectangle searchingArea)
		{ return isImageContained(smallerImage, largerImage, searchingArea, 0); }
	
	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage, int tolerance)
		{ return isImageContained(smallerImage, largerImage, new Rectangle(0, 0, (int)largerImage.getWidth(), (int)largerImage.getHeight()), tolerance); }

	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage)
		{ return isImageContained(smallerImage, largerImage, 0); }
	
	public static Color getMircColor(int color) {
		double[] d = mircColors[color];
		return Color.color(d[0], d[1], d[2], d[3]);
	}

	public static double[][] mircColors = { { 1, 1, 1, 1 }, { 0, 0, 0, 1 }, { 0, 0, 0.498, 1 }, { 0, 0.576, 0, 1 }, { 1, 0, 0, 1 }, { 0.498, 0, 0, 1 }, { 0.611, 0, 0.611, 1 }, { 0.988, 0.498, 0, 1 }, { 1, 1, 0, 1 }, { 0, 0.988, 0, 1 }, { 0, 0.576, 0.576, 1 }, { 0, 1, 1, 1 }, { 0, 0, 0.988, 1 }, { 1, 0, 1, 1 }, { 0.498, 0.498, 0.498, 1 }, { 0.823, 0.823, 0.823, 1 }, { 0.278, 0, 0, 1 }, { 0.278, 0.129, 0, 1 }, { 0.278, 0.278, 0, 1 }, { 0.196, 0.278, 0, 1 }, { 0, 0.278, 0, 1 }, { 0, 0.278, 0.172, 1 }, { 0, 0.278, 0.278, 1 }, { 0, 0.152, 0.278, 1 }, { 0, 0, 0.278, 1 }, { 0.180, 0, 0.278, 1 }, { 0.278, 0, 0.278, 1 }, { 0.278, 0, 0.164, 1 }, { 0.454, 0, 0, 1 }, { 0.454, 0.227, 0, 1 }, { 0.454, 0.454, 0, 1 }, { 0.317, 0.454, 0, 1 }, { 0, 0.454, 0, 1 }, { 0, 0.454, 0.286, 1 }, { 0, 0.454, 0.454, 1 }, { 0, 0.250, 0.454, 1 }, { 0, 0, 0.454, 1 }, { 0.294, 0, 0.454, 1 }, { 0.454, 0, 0.454, 1 }, { 0.454, 0, 0.270, 1 }, { 0.709, 0, 0, 1 }, { 0.709, 0.388, 0, 1 }, { 0.709, 0.709, 0, 1 },
	    { 0.490, 0.709, 0, 1 }, { 0, 0.709, 0, 1 }, { 0, 0.709, 0.443, 1 }, { 0, 0.709, 0.709, 1 }, { 0, 0.388, 0.709, 1 }, { 0, 0, 0.709, 1 }, { 0.458, 0, 0.709, 1 }, { 0.709, 0, 0.709, 1 }, { 0.709, 0, 0.419, 1 }, { 1, 0, 0, 1 }, { 1, 0.549, 0, 1 }, { 1, 1, 0, 1 }, { 0.698, 1, 0, 1 }, { 0, 1, 0, 1 }, { 0, 1, 0.627, 1 }, { 0, 1, 1, 1 }, { 0, 0.549, 1, 1 }, { 0, 0, 1, 1 }, { 0.647, 0, 1, 1 }, { 1, 0, 1, 1 }, { 1, 0, 0.596, 1 }, { 1, 0.349, 0.349, 1 }, { 1, 0.705, 0.349, 1 }, { 1, 1, 0.443, 1 }, { 0.811, 1, 0.376, 1 }, { 0.435, 1, 0.435, 1 }, { 0.396, 1, 0.788, 1 }, { 0.427, 1, 1, 1 }, { 0.349, 0.705, 1, 1 }, { 0.349, 0.349, 1, 1 }, { 0.768, 0.349, 1, 1 }, { 1, 0.4, 1, 1 }, { 1, 0.349, 0.737, 1 }, { 1, 0.611, 0.611, 1 }, { 1, 0.827, 0.611, 1 }, { 1, 1, 0.611, 1 }, { 0.886, 1, 0.611, 1 }, { 0.611, 1, 0.611, 1 }, { 0.611, 1, 0.858, 1 }, { 0.611, 1, 1, 1 }, { 0.611, 0.827, 1, 1 }, { 0.611, 0.611, 1, 1 }, { 0.862, 0.611, 1, 1 }, { 1, 0.611, 1, 1 }, { 1, 0.580, 0.827, 1 }, { 0, 0, 0, 1 },
	    { 0.074, 0.074, 0.074, 1 }, { 0.156, 0.156, 0.156, 1 }, { 0.211, 0.211, 0.211, 1 }, { 0.301, 0.301, 0.301, 1 }, { 0.396, 0.396, 0.396, 1 }, { 0.505, 0.505, 0.505, 1 }, { 0.623, 0.623, 0.623, 1 }, { 0.737, 0.737, 0.737, 1 }, { 0.886, 0.886, 0.886, 1 }, { 1, 1, 1, 1 } };

	public static int drawTextWithFormaters(GraphicsContext gc, String text, Font font, int x, int y) {
		if (text == null || text.isBlank())
			return -1;
		String[] split = text.split("\\{");
		gc.setLineWidth(1);
		gc.setStroke(Color.WHITE);
		gc.setFill(Color.WHITE);
		gc.setFont(font);
		Font[] font2 = { font };
		Text tempText = new Text();
		int fontHeight[] = { 0 };
		boolean[] isBold = { false }, isItalic = { false }, isSublined = { false };
		Runnable updateFont = () -> {
			font2[0] = Font.font(font2[0].getName(), isBold[0] ? FontWeight.BOLD : FontWeight.NORMAL, isItalic[0] ? FontPosture.ITALIC : FontPosture.REGULAR, font2[0].getSize());
			tempText.setFont(font2[0]);
			fontHeight[0] = (int) tempText.getBoundsInLocal().getHeight();
		};
		updateFont.run();
		gc.setTextAlign(TextAlignment.LEFT);
		int xx = 0;
		for (int z = 0; z < split.length; z++) {
			String word = (z > 0 ? "{" : "") + split[z];

			Matcher matcher1 = Pattern.compile("^(\\{[ibou]\\}).*").matcher(word);
			Matcher matcher2 = Pattern.compile("^(\\{l(\\d+)\\}).*").matcher(word);
			Matcher matcher3 = Pattern.compile("^(\\{k(\\d+)(\\,(\\d+))*\\}).*").matcher(word);
			Matcher matcher4 = Pattern.compile("^(\\{(#[0-9a-fA-F]{6,8})(\\,(#[0-9a-fA-F]{6,8}))*\\}).*").matcher(word);
			Matcher matcher5 = Pattern.compile("^(\\{([a-zA-Z]+)(\\,([a-zA-Z]+))*\\}).*").matcher(word);
			if (matcher1.matches()) {
				word = word.replace(matcher1.group(1), "");
				char c = matcher1.group(1).toLowerCase().charAt(1);
				if (c == 'b') { // Negrito
					isBold[0] = !isBold[0];
					updateFont.run();
				}
				else if (c == 'i') { // Italico
					isItalic[0] = !isItalic[0];
					updateFont.run();
				}
				else if (c == 'u')// Sublinhado
					isSublined[0] = !isSublined[0];
				else if (c == 'o') { // Cancela tudo
					isBold[0] = false;
					isItalic[0] = false;
					isSublined[0] = false;
				}
			}
			else {
				try {
					if (matcher2.matches()) {
						word = word.replace(matcher2.group(1), "");
						gc.setLineWidth(Integer.parseInt(matcher2.group(2)));
					}
					else if (matcher3.matches()) {
						word = word.replace(matcher3.group(1), "");
						Color color = getMircColor(Integer.parseInt(matcher3.group(2)));
						gc.setFill(color);
						if (matcher3.group(3) != null)
							gc.setStroke(getMircColor(Integer.parseInt(matcher3.group(3).substring(1))));
						else
							gc.setStroke(color);
					}
					else if (matcher4.matches()) {
						word = word.replace(matcher4.group(1), "");
						Color color = Color.valueOf(matcher4.group(2));
						gc.setFill(color);
						if (matcher4.group(3) != null)
							gc.setStroke(Color.valueOf(matcher4.group(3).substring(1)));
						else
							gc.setStroke(color);
					}
					else if (matcher5.matches()) {
						word = word.replace(matcher5.group(1), "");
						Color color = Color.valueOf(matcher5.group(2));
						gc.setFill(color);
						if (matcher5.group(3) != null)
							gc.setStroke(Color.valueOf(matcher5.group(3).substring(1)));
						else
							gc.setStroke(color);
					}
				}
				catch (Exception e) {}
			}
			gc.fillText(word, x + xx, y);
			gc.strokeText(word, x + xx, y);
			gc.setFont(font2[0]);
			tempText.setText(word);
			int xxx = xx;
			xx += (int) tempText.getBoundsInLocal().getWidth();
			if (isSublined[0])
				gc.strokeLine(x + xxx, y + fontHeight[0] * 0.05, x + xx, y + fontHeight[0] * 0.05);
		}
		return xx;
	}

	public static WritableImage getCanvasSnapshot(Canvas canvas, WritableImage outputImage) {
		return getCanvasSnapshot(canvas, 0, 0, (int) canvas.getWidth(), (int) canvas.getHeight(), outputImage);
	}

	public static WritableImage getCanvasSnapshot(Canvas canvas, int w, int h, WritableImage outputImage) {
		return getCanvasSnapshot(canvas, 0, 0, w, h, outputImage);
	}

	public static WritableImage getCanvasSnapshot(Canvas canvas) {
		return getCanvasSnapshot(canvas, 0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
	}

	public static WritableImage getCanvasSnapshot(Canvas canvas, int w, int h) {
		return getCanvasSnapshot(canvas, 0, 0, w, h);
	}

	public static WritableImage getCanvasSnapshot(Canvas canvas, int x, int y, int w, int h) {
		return getCanvasSnapshot(canvas, x, y, w, h, null);
	}

	public static WritableImage getCanvasSnapshot(Canvas canvas, int x, int y, int w, int h, WritableImage outputImage) {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(x, y, w, h));
		return canvas.snapshot(params, outputImage);
	}

	public static WritableImage applyPerspectiveTransform(Image input, Point2D topLeft, Point2D topRight, Point2D bottomLeft, Point2D bottomRight) {
		int width = (int) input.getWidth();
		int height = (int) input.getHeight();
		WritableImage output = new WritableImage(width, height);
		PixelReader reader = input.getPixelReader();
		double[] srcX = { 0, width, 0, width };
		double[] srcY = { 0, 0, height, height };
		double[] dstX = { topLeft.getX(), topRight.getX(), bottomLeft.getX(), bottomRight.getX() };
		double[] dstY = { topLeft.getY(), topRight.getY(), bottomLeft.getY(), bottomRight.getY() };
		double[][] matrix = computePerspectiveTransform(srcX, srcY, dstX, dstY);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				double[] mapped = applyMatrix(matrix, x, y);
				int mappedX = (int) Math.round(mapped[0]);
				int mappedY = (int) Math.round(mapped[1]);
				Color c = reader.getColor(x, y);
				for (int yy = mappedY - 1; yy <= mappedY + 1; yy++)
					for (int xx = mappedX - 1; xx <= mappedX + 1; xx++)
						if (xx >= 0 && xx < width && yy >= 0 && yy < height)
							output.getPixelWriter().setColor(xx, yy, c);
			}
		return output;
	}

	private static double[][] computePerspectiveTransform(double[] srcX, double[] srcY, double[] dstX, double[] dstY) {
		double[][] matrix = new double[3][3];
		double[][] A = { { srcX[0], srcY[0], 1, 0, 0, 0, -dstX[0] * srcX[0], -dstX[0] * srcY[0] }, { srcX[1], srcY[1], 1, 0, 0, 0, -dstX[1] * srcX[1], -dstX[1] * srcY[1] }, { srcX[2], srcY[2], 1, 0, 0, 0, -dstX[2] * srcX[2], -dstX[2] * srcY[2] }, { srcX[3], srcY[3], 1, 0, 0, 0, -dstX[3] * srcX[3], -dstX[3] * srcY[3] }, { 0, 0, 0, srcX[0], srcY[0], 1, -dstY[0] * srcX[0], -dstY[0] * srcY[0] }, { 0, 0, 0, srcX[1], srcY[1], 1, -dstY[1] * srcX[1], -dstY[1] * srcY[1] }, { 0, 0, 0, srcX[2], srcY[2], 1, -dstY[2] * srcX[2], -dstY[2] * srcY[2] }, { 0, 0, 0, srcX[3], srcY[3], 1, -dstY[3] * srcX[3], -dstY[3] * srcY[3] } };
		double[] B = { dstX[0], dstX[1], dstX[2], dstX[3], dstY[0], dstY[1], dstY[2], dstY[3] };
		double[] H = solveLinearSystem(A, B);
		matrix[0][0] = H[0];
		matrix[0][1] = H[1];
		matrix[0][2] = H[2];
		matrix[1][0] = H[3];
		matrix[1][1] = H[4];
		matrix[1][2] = H[5];
		matrix[2][0] = H[6];
		matrix[2][1] = H[7];
		matrix[2][2] = 1;
		return matrix;
	}

	private static double[] applyMatrix(double[][] matrix, double x, double y) {
		double[] result = new double[2];
		double w = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2];
		result[0] = (matrix[0][0] * x + matrix[0][1] * y + matrix[0][2]) / w;
		result[1] = (matrix[1][0] * x + matrix[1][1] * y + matrix[1][2]) / w;
		return result;
	}

	private static double[] solveLinearSystem(double[][] A, double[] B) {
		int n = B.length;
		double[] x = new double[n];

		for (int i = 0; i < n; i++) {
			int maxRow = i;
			for (int k = i + 1; k < n; k++)
				if (Math.abs(A[k][i]) > Math.abs(A[maxRow][i]))
					maxRow = k;

			double[] temp = A[i];
			A[i] = A[maxRow];
			A[maxRow] = temp;

			double t = B[i];
			B[i] = B[maxRow];
			B[maxRow] = t;

			for (int k = i + 1; k < n; k++) {
				double factor = A[k][i] / A[i][i];
				B[k] -= factor * B[i];
				for (int j = i; j < n; j++)
					A[k][j] -= factor * A[i][j];
			}
		}

		for (int i = n - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < n; j++)
				sum += A[i][j] * x[j];
			x[i] = (B[i] - sum) / A[i][i];
		}

		return x;
	}

}
