package gui.util;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import enums.ImageScanOrientation;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.DesktopUtils;

public abstract class ImageUtils {

	private static Robot robot;
	private static Object ignoreColor = Color.TRANSPARENT;
	private static ImageScanOrientation imageScanOrientation = ImageScanOrientation.HORIZONTAL;
	
	private static Robot getRobot() {
		if (robot == null)
			try
				{ robot = new Robot(); }
			catch (Exception e)
				{ robot = null; }
		return robot;
	}
	
	public static BufferedImage getRotatedImage(BufferedImage image, double angle) {
		int width = (int)image.getWidth(), height = (int)image.getHeight();
		BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				double sourceX = Math.cos(Math.toRadians(angle)) * (x - width / 2) - Math.sin(Math.toRadians(angle)) * (y - height / 2) + width / 2;
				double sourceY = Math.sin(Math.toRadians(angle)) * (x - width / 2) + Math.cos(Math.toRadians(angle)) * (y - height / 2) + height / 2;
				if (sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height)
					rotatedImage.setRGB(x, y, image.getRGB((int)sourceX, (int)sourceY));
			}
		return rotatedImage;
	}

	public static WritableImage getRotatedImage(WritableImage image, double angle) {
		int width = (int)image.getWidth(), height = (int)image.getHeight();
		WritableImage rotatedImage = new WritableImage(width, height);
		PixelReader pixelReader = image.getPixelReader();
		PixelWriter pixelWriter = rotatedImage.getPixelWriter();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				double sourceX = Math.cos(Math.toRadians(angle)) * (x - width / 2) - Math.sin(Math.toRadians(angle)) * (y - height / 2) + width / 2;
				double sourceY = Math.sin(Math.toRadians(angle)) * (x - width / 2) + Math.cos(Math.toRadians(angle)) * (y - height / 2) + height / 2;
				if (sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height)
					pixelWriter.setColor(x, y, pixelReader.getColor((int)sourceX, (int)sourceY));
			}
		return rotatedImage;
	}

  public static Image getRotatedImage(Image image, double angle) {
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
    BufferedImage rotatedBufferedImage = getRotatedImage(bufferedImage, angle);
    return SwingFXUtils.toFXImage(rotatedBufferedImage, null);
  }
  
	/**
	 * Remove a cor especificada da imagem (de acordo com o valor
	 * de {@code toleranceThreshold} (Que vai de 0 a 255) Quanto mais próximo de
	 * 255, menos tons da cor especificada serão removidos (Se usar 255, apenas
	 * a cor informada será removido da imagem).
	 * 
	 * @param image - A imagem a ter a cor de fundo removida
	 * @param toleranceThreshold - Valor de tolerância
	 * @return A imagem com a cor de fundo removida
	 */

	public static BufferedImage removeBgColor(BufferedImage image, int removeColorArgb, int toleranceThreshold) {
		int w = (int)image.getWidth();
		int h = (int)image.getHeight();
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
				if (rr <= r3 && rr >= r2 && gg <= g3 && gg >= g2 && bb <= b3 && bb >= b2)
					outputImage.setRGB(x, y, getRgba(0, 0, 0, 0));
				else
					outputImage.setRGB(x, y, image.getRGB(x, y));
			}
		return outputImage;
	}
	
	public static BufferedImage removeBgColor(BufferedImage image, int removeColorArgb)
		{ return removeBgColor(image, removeColorArgb, 0); }

	public static WritableImage removeBgColor(WritableImage image, int removeColorArgb, int toleranceThreshold) {
		int w = (int)image.getWidth();
		int h = (int)image.getHeight();
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
				if (rr <= r3 && rr >= r2 && gg <= g3 && gg >= g2 && bb <= b3 && bb >= b2)
					pixelWriter.setArgb(x, y, getRgba(0, 0, 0, 0));
				else
					pixelWriter.setArgb(x, y, pixelReader.getArgb(x, y));
			}
		return outputImage;
	}

	public static WritableImage removeBgColor(WritableImage image, int removeColorArgb)
		{ return removeBgColor(image, removeColorArgb, 0); }
	
	public static Image removeBgColor(Image image, Color removeColor, int toleranceThreshold)
		{ return SwingFXUtils.toFXImage(removeBgColor(SwingFXUtils.fromFXImage(image, null), colorToArgb(removeColor), toleranceThreshold), null); }
	
	public static Image removeBgColor(Image image, int toleranceThreshold)
		{ return removeBgColor(image, Color.valueOf("#FF00FF"), toleranceThreshold); }
	
	public static Image removeBgColor(Image image, Color transparentColor)
		{ return removeBgColor(image, transparentColor, 0); }
	
	public static Image removeBgColor(Image image)
		{ return removeBgColor(image, Color.WHITE, 0); }

	public static BufferedImage replaceColor(BufferedImage image, int[] beforeArgb, int[] afterArgb, Rectangle affectedArea) {
		int w = (int)image.getWidth();
		int h = (int)image.getHeight();
		boolean ok;
		BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int z, x, y = 0; y < h; y++)
			for (x = 0; x < w; x++) {
				if (affectedArea == null || (x >= affectedArea.getX() && x <= affectedArea.getX() + affectedArea.getWidth() &&
						y >= affectedArea.getX() && y <= affectedArea.getX() + affectedArea.getHeight())) {
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
	
	public static BufferedImage replaceColor(BufferedImage image, int beforeColor, int afterColor, Rectangle affectedArea)
		{ return replaceColor(image, new int[] {beforeColor}, new int[] {afterColor}, affectedArea); }
	
	public static BufferedImage replaceColor(BufferedImage image, int[] beforeColors, int[] afterColors)
		{ return replaceColor(image, beforeColors, afterColors, null); }
	
	public static BufferedImage replaceColor(BufferedImage image, int beforeColor, int afterColor)
		{ return replaceColor(image, new int[] {beforeColor}, new int[] {afterColor}, null); }

	public static WritableImage replaceColor(WritableImage image, int[] beforeArgb, int[] afterArgb, Rectangle affectedArea) {
		int w = (int)image.getWidth();
		int h = (int)image.getHeight();
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
	
	public static WritableImage replaceColor(WritableImage image, int beforeColor, int afterColor, Rectangle affectedArea)
		{ return replaceColor(image, new int[] {beforeColor}, new int[] {afterColor}, affectedArea); }
	
	public static WritableImage replaceColor(WritableImage image, int[] beforeColors, int[] afterColors)
		{ return replaceColor(image, beforeColors, afterColors, null); }
	
	public static WritableImage replaceColor(WritableImage image, int beforeColor, int afterColor)
		{ return replaceColor(image, new int[] {beforeColor}, new int[] {afterColor}, null); }

	public static Image replaceColor(Image image, Color[] beforeColors, Color[] afterColors, Rectangle affectedArea) {
		int[] before = new int[beforeColors.length];
		int[] after = new int[beforeColors.length];
		for (int n = 0; n < before.length; n++) {
			before[n] = colorToArgb(beforeColors[n]);
			after[n] = colorToArgb(afterColors[n]);
		}
		return SwingFXUtils.toFXImage(replaceColor(SwingFXUtils.fromFXImage(image, null), before, after, affectedArea), null);
	}
	
	public static Image replaceColor(Image image, Color beforeColor, Color afterColor, Rectangle affectedArea)
		{ return replaceColor(image, new Color[] {beforeColor}, new Color[] {afterColor}, affectedArea); }
	
	public static Image replaceColor(Image image, Color[] beforeColors, Color[] afterColors)
		{ return replaceColor(image, beforeColors, afterColors, null); }
	
	public static Image replaceColor(Image image, Color beforeColor, Color afterColor)
		{ return replaceColor(image, new Color[] {beforeColor}, new Color[] {afterColor}, null); }

	public static BufferedImage getScreenShot(Rectangle captureArea, double scaleDpi) {
		MultiResolutionImage multiResolutionImage = getRobot().createMultiResolutionScreenCapture(captureArea);
		return (BufferedImage) multiResolutionImage.getResolutionVariant(captureArea.width * scaleDpi, captureArea.height * scaleDpi);
	}

	public static BufferedImage getScreenShot(Rectangle captureArea)
		{ return getScreenShot(captureArea, 1); }
	
	public static BufferedImage getScreenShot(double scaleDpi)
		{ return getScreenShot(new Rectangle(0, 0, DesktopUtils.getHardwareScreenWidth(), DesktopUtils.getHardwareScreenWidth()), scaleDpi); }

	public static BufferedImage getScreenShot()
		{ return getScreenShot(1); }
	
	public static WritableImage convertToWritableImage(BufferedImage bufferedImage) {
		WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				writableImage.getPixelWriter().setArgb(x, y, bufferedImage.getRGB(x, y));
		return writableImage;
	}	
	
	public static BufferedImage convertToBufferedImage(WritableImage writableImage) {
		BufferedImage bufferedImage = new BufferedImage((int)writableImage.getWidth(), (int)writableImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int width = (int)writableImage.getWidth();
		int height = (int)writableImage.getHeight();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				bufferedImage.setRGB(x, y, writableImage.getPixelReader().getArgb(x, y));
		return bufferedImage;
	}	

	public static int[][] convertToIntArray(BufferedImage image) {
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		int[][] array = new int[height][width];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				array[y][x] = image.getRGB(x, y);
		return array;
	}	
	
	public static BufferedImage convertToBufferedImage(int[][] array) {
		BufferedImage bufferedImage = new BufferedImage((int)array[0].length, (int)array.length, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < array.length; y++)
			for (int x = 0; x < array[y].length; x++)
				bufferedImage.setRGB(x, y, array[y][x]);
		return bufferedImage;
	}	

	public static void copyArea(BufferedImage sourceImage, BufferedImage targetImage, Rectangle sourceArea, Point targetCoordinate) {
		BufferedImage bufferedImage = sourceImage.getSubimage((int)sourceArea.getX(), (int)sourceArea.getY(), (int)sourceArea.getWidth(), (int)sourceArea.getHeight());
		Graphics2D g2d = targetImage.createGraphics();
		g2d.drawImage(bufferedImage, (int)targetCoordinate.getX(), (int)targetCoordinate.getY(), null);
		g2d.dispose();
	}
	
	public static void copyArea(BufferedImage source, BufferedImage target, Rectangle sourceArea)
		{ copyArea(source, target, sourceArea, new Point(0, 0)); }
	
	public static void copyArea(BufferedImage source, BufferedImage target)
		{ copyArea(source, target, new Rectangle(0, 0, (int)target.getWidth(), (int)target.getHeight())); }

	public static BufferedImage copyFrom(BufferedImage target, Rectangle targetArea) {
		BufferedImage newWI = new BufferedImage((int)targetArea.getWidth(), (int)targetArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
		copyArea(target, newWI, targetArea, new Point(0, 0));
		return newWI;
	}
	
	public static void copyArea(WritableImage sourceImage, WritableImage targetImage, Rectangle sourceArea, Point targetCoordinate) {
		targetImage.getPixelWriter().setPixels(
				(int)targetCoordinate.getX(), (int)targetCoordinate.getY(),
				(int)sourceArea.getWidth(), (int)sourceArea.getHeight(),
				sourceImage.getPixelReader(),
				(int)sourceArea.getX(),
				(int)sourceArea.getY());
	}

	public static void copyArea(WritableImage source, WritableImage target, Rectangle sourceArea)
		{ copyArea(source, target, sourceArea, new Point(0, 0)); }

	public static void copyArea(WritableImage source, WritableImage target)
		{ copyArea(source, target, new Rectangle(0, 0, (int)target.getWidth(), (int)target.getHeight())); }

	public static WritableImage copyFrom(WritableImage target, Rectangle targetArea) {
		WritableImage newWI = new WritableImage((int)targetArea.getWidth(), (int)targetArea.getHeight());
		copyArea(target, newWI, targetArea, new Point(0, 0));
		return newWI;
	}
	
	public static boolean areEqualImages(WritableImage image1, WritableImage image2, int tolerance) {
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight())
			return false;
		PixelReader reader1 = image1.getPixelReader();
		PixelReader reader2 = image2.getPixelReader();
		int width = (int)image1.getWidth(), height = (int)image1.getHeight();
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

	public static boolean areEqualImages(WritableImage image1, WritableImage image2)
		{ return areEqualImages(image1, image2, 0); }

	public static boolean areEqualImages(BufferedImage image1, BufferedImage image2, int tolerance) {
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight())
			return false;
		int width = (int)image1.getWidth(), height = (int)image1.getHeight();
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

	public static boolean areEqualImages(BufferedImage image1, BufferedImage image2)
		{ return areEqualImages(image1, image2, 0); }
	
	
	
	public static int getRgba(int red, int green, int blue, int alpha)
		{ return (alpha << 24) | (blue << 16) | (green << 8) | red; }

	public static int getRgba(int red, int green, int blue)
		{ return getRgba(red, green, blue, 0); }

	public static int[] getRgbaArray(int rgbaValue)
		{ return new int[] { rgbaValue & 0xFF, (rgbaValue >> 8) & 0xFF, (rgbaValue >> 16) & 0xFF, (rgbaValue >> 24) & 0xFF }; }

	public static WritableImage convertImageToWritableImage(Image image)
		{ return new ImageView(image).snapshot(new SnapshotParameters(), null); }
	
	public static BufferedImage convertImageToBufferedImage(Image image)
		{ return SwingFXUtils.fromFXImage(image, new BufferedImage((int)image.getWidth(), (int)image.getHeight(), BufferedImage.TYPE_INT_ARGB)); }

	public static Image convertWritableImageToImage(WritableImage writableImage)
		{ return new ImageView(writableImage).snapshot(new SnapshotParameters(), null); }
	
	public static Image convertBufferedImageToImage(BufferedImage bufferedImage)
		{ return SwingFXUtils.toFXImage(bufferedImage, null); }

	public static BufferedImage loadBufferedImageFromFile(File file)
		{ return convertImageToBufferedImage(new Image("file:" + file.getAbsolutePath())); }
	
	public static BufferedImage loadBufferedImageFromFile(String filePath)
		{ return loadBufferedImageFromFile(new File(filePath)); }

	public static WritableImage loadWritableImageFromFile(File file)
		{ return convertImageToWritableImage(new Image("file:" + file.getAbsolutePath())); }

	public static WritableImage loadWritableImageFromFile(String filePath)
		{ return loadWritableImageFromFile(new File(filePath)); }

	public static void saveImageToFile(BufferedImage image, File file) {
		try
			{ ImageIO.write(image, "png", file); }
		catch (IOException e)
			{ throw new RuntimeException("Unable to save image to disk\n" + e.getMessage()); }
	}
	
	public static void saveImageToFile(WritableImage image, File file)
		{ saveImageToFile(convertToBufferedImage(image), file); }
	
	public static void saveImageToFile(Image image, File file)
		{ saveImageToFile(convertImageToBufferedImage(image), file); }
	
	public static void saveImageToFile(BufferedImage image, String filePath)
		{	saveImageToFile(image, new File(filePath));}
	
	public static void saveImageToFile(WritableImage image, String filePath)
		{ saveImageToFile(image, new File(filePath)); }
	
	public static void saveImageToFile(Image image, String filePath)
		{ saveImageToFile(image, new File(filePath)); }
	
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

	public static Point screenContainsAnImage(BufferedImage image, Rectangle searchingArea, int tolerance)
		{ return isImageContained(image, getScreenShot(searchingArea), tolerance); }
	
	public static Point screenContainsAnImage(BufferedImage image, Rectangle searchingArea)
		{ return screenContainsAnImage(image, searchingArea, 0); }
	
	public static Point screenContainsAnImage(BufferedImage image, int tolerance)
		{ return screenContainsAnImage(image, new Rectangle(0, 0, DesktopUtils.getHardwareScreenWidth(), DesktopUtils.getHardwareScreenHeight()), tolerance); }
	
	public static Point screenContainsAnImage(BufferedImage image)
		{ return screenContainsAnImage(image, 0); }
	
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
    int red = (int)(fxColor.getRed() * 255);
    int green = (int)(fxColor.getGreen() * 255);
    int blue = (int)(fxColor.getBlue() * 255);
    int alpha = (int)(fxColor.getOpacity() * 255);
    return new java.awt.Color(red, green, blue, alpha);
  }

	public static int colorToArgb(Color color) {
		int red = (int)(color.getRed() * 255);
		int green = (int)(color.getGreen() * 255);
		int blue = (int)(color.getBlue() * 255);
		int alpha = (int)(color.getOpacity() * 255);
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
	
	public static Color argbToColor(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		return Color.rgb(r, g, b, a / 255.0);
	}
	
	/**
	 * Converte uma java.awt.Image em javafx.Image, redimensionando o tamanho final
	 * @param awtImage - java.awt.Image á ser convertida
	 * @param width - Largura da imagem convertida
	 * @param height - Altura da imagem convertida
	 * @return - Uma javafx.Image redimensionada
	 */
	public static Image toResizedFXImage(java.awt.Image awtImage, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bufferedImage.getGraphics().drawImage(awtImage, 0, 0, null);
		BufferedImage novaBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		novaBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
		return SwingFXUtils.toFXImage(novaBufferedImage, null);
	}
	
	/** Converte uma java.awt.Image em javafx.Image */
	public static Image toFXImage(java.awt.Image awtImage)
		{ return SwingFXUtils.toFXImage((BufferedImage) awtImage, null); }
	
	/**
	 * Converte uma javafx.Image em java.awt.Image, redimensionando o tamanho final
	 * @param fxImage - javafx.Image á ser convertida
	 * @param width - Largura da imagem convertida
	 * @param height - Altura da imagem convertida
	 * @param scale - Escala da imagem convertida (Usar uma constante de java.awt.Image.* para pegar um valor compatível)
	 * @return - Uma java.awt.Image redimensionada
	 */
	public static java.awt.Image toResizedAWTImage(Image fxImage, int width, int height, int scale) {
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
    BufferedImage novaBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    novaBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
    return novaBufferedImage.getScaledInstance(width, height, scale);
	}

	public static java.awt.Image toResizedAWTImage(Image fxImage, int width, int height)
		{ return toResizedAWTImage(fxImage, width, height, java.awt.Image.SCALE_DEFAULT); }

	public static java.awt.Image toAWTImage(Image fxImage)
		{ return SwingFXUtils.fromFXImage(fxImage, null); }
	
}

