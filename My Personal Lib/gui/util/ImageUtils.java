package gui.util;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import util.DesktopUtils;

public abstract class ImageUtils {

	public static Image rotateImage(Image image, double angle) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage rotatedImage = new WritableImage(width, height);
		PixelReader pixelReader = image.getPixelReader();
		PixelWriter pixelWriter = rotatedImage.getPixelWriter();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				double sourceX = Math.cos(Math.toRadians(angle)) * (x - width / 2) - Math.sin(Math.toRadians(angle)) * (y - height / 2) + width / 2;
				double sourceY = Math.sin(Math.toRadians(angle)) * (x - width / 2) + Math.cos(Math.toRadians(angle)) * (y - height / 2) + height / 2;
				if (sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height) {
					Color color = pixelReader.getColor((int) sourceX, (int) sourceY);
					pixelWriter.setColor(x, y, color);
				}
			}

		return rotatedImage;
	}

	/**
	 * Remove o branco ou tons similares de branco da imagem (de acordo com o valor
	 * de {@code toleranceThreshold} (Que vai de 0 a 255) Quanto mais próximo de
	 * 255, menos tons de branco serão removidos (Se usar 255, apenas o branco puro
	 * será removido da imagem).
	 * 
	 * @param image              A imagem a ter a cor de fundo removida
	 * @param toleranceThreshold Valor de tolerância
	 * @return A imagem com a cor de fundo removida
	 */

	public static Image removeBgColor(Image image, Color transparentColor, int toleranceThreshold) {
		int W = (int) image.getWidth();
		int H = (int) image.getHeight();
		WritableImage outputImage = new WritableImage(W, H);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = outputImage.getPixelWriter();
		for (int y = 0; y < H; y++)
			for (int x = 0; x < W; x++) {
				int argb = reader.getArgb(x, y);
				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = argb & 0xFF;
				int rr = (int) (transparentColor.getRed() * 255);
				int gg = (int) (transparentColor.getGreen() * 255);
				int bb = (int) (transparentColor.getBlue() * 255);
				int r2 = (rr - toleranceThreshold) < 0 ? 0 : rr - toleranceThreshold;
				int g2 = (gg - toleranceThreshold) < 0 ? 0 : gg - toleranceThreshold;
				int b2 = (bb - toleranceThreshold) < 0 ? 0 : bb - toleranceThreshold;
				int r3 = (rr + toleranceThreshold) > 255 ? 255 : rr + toleranceThreshold;
				int g3 = (gg + toleranceThreshold) > 255 ? 255 : gg + toleranceThreshold;
				int b3 = (bb + toleranceThreshold) > 255 ? 255 : bb + toleranceThreshold;
				if (r <= r3 && r >= r2 && g <= g3 && g >= g2 && b <= b3 && b >= b2)
					argb &= 0x00FFFFFF;
				writer.setArgb(x, y, argb);
			}
		return outputImage;
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

	public static Image replaceColor(Image image, int[] beforeArgb, int[] afterArgb, Rectangle affectedArea) {
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		boolean ok;
		WritableImage outputImage = new WritableImage(w, h);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = outputImage.getPixelWriter();
		for (int z, x, y = 0; y < h; y++)
			for (x = 0; x < w; x++) {
				if (affectedArea == null || (x >= affectedArea.getX() && x <= affectedArea.getX() + affectedArea.getWidth() &&
						y >= affectedArea.getX() && y <= affectedArea.getX() + affectedArea.getHeight())) {
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
	
	public static Image replaceColor(Image image, Color[] beforeColors, Color[] afterColors, Rectangle affectedArea) {
		int[] before = new int[beforeColors.length];
		int[] after = new int[beforeColors.length];
		for (int n = 0; n < before.length; n++) {
			before[n] = colorToArgb(beforeColors[n]);
			after[n] = colorToArgb(afterColors[n]);
		}
		return replaceColor(image, before, after, affectedArea);
	}
	
	public static Image replaceColor(Image image, Color beforeColor, Color afterColor, Rectangle affectedArea)
		{ return replaceColor(image, new Color[] {beforeColor}, new Color[] {afterColor}, affectedArea); }
	
	public static Image replaceColor(Image image, int beforeArgb, int afterArgb, Rectangle affectedArea)
		{ return replaceColor(image, new int[] {beforeArgb}, new int[] {afterArgb}, affectedArea); }

	public static Image replaceColor(Image image, Color[] beforeColors, Color[] afterColors)
		{ return replaceColor(image, beforeColors, afterColors, null); }
	
	public static Image replaceColor(Image image, int[] beforeArgb, int[] afterArgb)
		{ return replaceColor(image, beforeArgb, afterArgb, null); }

	public static Image replaceColor(Image image, Color beforeColor, Color afterColor)
		{ return replaceColor(image, new Color[] {beforeColor}, new Color[] {afterColor}, null); }
	
	public static Image replaceColor(Image image, int beforeArgb, int afterArgb)
		{ return replaceColor(image, new int[] {beforeArgb}, new int[] {afterArgb}, null); }

	public static Image removeBgColor(Image image, int toleranceThreshold)
		{ return removeBgColor(image, Color.valueOf("#00FF00"), toleranceThreshold); }

	public static Image removeBgColor(Image image, Color transparentColor)
		{ return removeBgColor(image, transparentColor, 0); }

	public static Image removeBgColor(Image image)
		{ return removeBgColor(image, Color.WHITE, 0); }

	/** As coordenadas usadas nesse método se referem á resolução da tela levando em conta a escala de DPI do sistema */
	public static WritableImage getScreenShotToWritableImage(Rectangle2D rectangle)
		{ return new Robot().getScreenCapture(null, rectangle); }

	/** A screen shot obtida terá a resolução da tela levando em conta a escala de DPI do sistema */
	public static WritableImage getScreenShotToWritableImage()
		{ return getScreenShotToWritableImage(new Rectangle2D(0, 0, DesktopUtils.getSystemScreenWidth(), DesktopUtils.getSystemScreenHeight())); }

	/** A screen shot obtida terá a resolução real da tela, sem levar em conta a escala de DPI do sistema */
	public static Image getScreenShotToImage() {
		try {
			MultiResolutionImage multiResolutionImage = new java.awt.Robot().createMultiResolutionScreenCapture(new Rectangle(0, 0, DesktopUtils.getSystemScreenWidth(), DesktopUtils.getSystemScreenHeight()));
			return SwingFXUtils.toFXImage((java.awt.image.BufferedImage) multiResolutionImage.getResolutionVariant(DesktopUtils.getHardwareScreenWidth(), DesktopUtils.getHardwareScreenHeight()), null);
		}
		catch (AWTException e)
			{ throw new RuntimeException("Unable to retrieve the screen shot\n\t" + e.getMessage()); }
	}
	
	public static void copyWritableImageArea(WritableImage source, WritableImage target, Rectangle sourceArea, Point targetCoordinate) {
		target.getPixelWriter().setPixels(
				(int)targetCoordinate.getX(), (int)targetCoordinate.getY(),
				(int)sourceArea.getWidth(), (int)sourceArea.getHeight(),
				source.getPixelReader(),
				(int)sourceArea.getX(),
				(int)sourceArea.getY());
	}

	public static void copyWritableImageArea(WritableImage source, WritableImage target, Rectangle sourceArea)
		{ copyWritableImageArea(source, target, sourceArea, new Point(0, 0)); }

	public static void copyWritableImageArea(WritableImage source, WritableImage target)
		{ copyWritableImageArea(source, target, new Rectangle(0, 0, (int)target.getWidth(), (int)target.getHeight())); }

	public static WritableImage copyFromAnotherWritableImageArea(WritableImage target, Rectangle targetArea) {
		WritableImage newWI = new WritableImage((int)targetArea.getWidth(), (int)targetArea.getHeight());
		copyWritableImageArea(target, newWI, targetArea, new Point(0, 0));
		return newWI;
	}
	
	public static boolean areEqualImages(WritableImage image1, WritableImage image2) {
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight())
			return false;
		PixelReader reader1 = image1.getPixelReader();
		PixelReader reader2 = image2.getPixelReader();
		int width = (int) image1.getWidth();
		int height = (int) image1.getHeight();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (reader1.getArgb(x, y) != reader2.getArgb(x, y))
					return false;
		return true;
	}

	public static int getRGBAValueFromRGBAValues(int red, int green, int blue, int alpha)
		{ return (alpha << 24) | (blue << 16) | (green << 8) | red; }

	public static int getRGBAValueFromRGBAValues(int red, int green, int blue)
		{ return getRGBAValueFromRGBAValues(red, green, blue, 0); }

	public static int getRGBAValueFromRGBAValues(int[] rgba) {
		if (rgba == null)
			throw new RuntimeException("'rgba' is null");
		if (rgba.length != 4)
			throw new RuntimeException("'rgba' must be a 4 lenght array");
		for (int i = 0; i < 4; i++)
			if (rgba[i] < 0 || rgba[i] > 255)
				throw new RuntimeException("'rgba must have values between 0-255");
		return getRGBAValueFromRGBAValues(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public static int[] getRGBAValuesFromRGBAValue(int rgbaValue)
		{ return new int[] { rgbaValue & 0xFF, (rgbaValue >> 8) & 0xFF, (rgbaValue >> 16) & 0xFF, (rgbaValue >> 24) & 0xFF }; }

	public static WritableImage convertToWritableImage(Image image) {
		int width = (int) image.getWidth ();
		int height = (int) image.getHeight ();
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage (image, null);
		WritableImage writableImage = new WritableImage (width, height);
		return SwingFXUtils.toFXImage(bufferedImage, writableImage);
	}
	
	public static WritableImage loadWritableImageFromDisk(String imagePath) {
		Image image = new Image(imagePath);
		int w = (int)image.getWidth();
		int h = (int)image.getHeight();
		System.out.println(w + "," + h);
		WritableImage writableImage = new WritableImage(w, h);
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(w);
		imageView.setFitHeight(h);
		imageView.snapshot(null, writableImage);
		return writableImage;
	}

	public static void saveImage(Image image, File file) {
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		try
			{ ImageIO.write(bufferedImage, "png", file); }
		catch (IOException e)
			{ e.printStackTrace(); }
	}
	
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
	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage, Rectangle searchingArea, int tolerance) {
		if (tolerance < 0 || tolerance > 255)
			throw new RuntimeException("'tolerance' value must be between 0-255");
		PixelReader smallerReader = smallerImage.getPixelReader();
		PixelReader largerReader = largerImage.getPixelReader();
		int smallerWidth = (int) smallerImage.getWidth();
		int smallerHeight = (int) smallerImage.getHeight();
		int largerWidth = (int) largerImage.getWidth();
		int largerHeight = (int) largerImage.getHeight();
		if (searchingArea == null)
			searchingArea = new Rectangle(0, 0, largerWidth, largerHeight);
		int startX = (int)searchingArea.getX();
		int startY = (int)searchingArea.getY();
		int width = (int)searchingArea.getWidth();
		int height = (int)searchingArea.getHeight();
		if (smallerWidth >= largerWidth || smallerHeight >= largerHeight)
			throw new RuntimeException("The 'smallerImage' must be smaler in width and height than 'largerImage'");
		if (width < smallerWidth || height < smallerHeight)
			throw new RuntimeException("'searchingArea' must be equal or higher in size than 'smallerImage' size");
		boolean match = true;
		int xxx, yyy, xx, yy, y, x, i;
		for (xxx = 0; (startX + xxx) <= (largerWidth - smallerWidth) && xxx < width; xxx++)
			for (yyy = 0; (startY + yyy) <= (largerHeight - smallerHeight) && yyy < height; yyy++) {
				x = startX + xxx;
				y = startY + yyy;
				match = true;
				for (xx = 0; match && xx < smallerWidth; xx++)
					for (yy = 0; match && yy < smallerHeight; yy++) {
						if (tolerance != 0) {
							int[] rgb1 = ImageUtils.getRGBAValuesFromRGBAValue(smallerReader.getArgb(xx, yy));
							int[] rgb2 = ImageUtils.getRGBAValuesFromRGBAValue(largerReader.getArgb(x + xx, y + yy));
							for (i = 0; match && i < 3; i++)
								if (rgb1[i] < rgb2[i] - tolerance || rgb1[i] > rgb2[i] + tolerance)
									match = false;
						}
						else if (smallerReader.getArgb(xx, yy) != largerReader.getArgb(x + xx, y + yy))
							match = false;
					}
				if (match)
					return new Point(x, y);
			}
		return null;
	}
	
	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage, Rectangle searchingArea)
		{ return isImageContained(smallerImage, largerImage, searchingArea, 0); }
	
	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage, int tolerance)
		{ return isImageContained(smallerImage, largerImage, new Rectangle(0, 0, (int)largerImage.getWidth(), (int)largerImage.getHeight()), tolerance); }

	public static Point isImageContained(WritableImage smallerImage, WritableImage largerImage)
		{ return isImageContained(smallerImage, largerImage, new Rectangle(0, 0, (int)largerImage.getWidth(), (int)largerImage.getHeight()), 0); }

	public static Point screenContainsAnImage(Image image, Rectangle searchingArea, int tolerance) {
		return isImageContained(convertToWritableImage(image),
														convertToWritableImage(getScreenShotToImage()),
														searchingArea, tolerance);
	}
	
	public static Point screenContainsAnImage(Image image, Rectangle searchingArea) {
		return isImageContained(convertToWritableImage(image),
					 convertToWritableImage(getScreenShotToImage()), searchingArea);
	}
	
	public static Point screenContainsAnImage(Image image, int tolerance) {
		return isImageContained(convertToWritableImage(image),
					 convertToWritableImage(getScreenShotToImage()), tolerance);
	}
	
	public static Point screenContainsAnImage(Image image) {
		return isImageContained(convertToWritableImage(image),
					 convertToWritableImage(getScreenShotToImage()));
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
	
}

