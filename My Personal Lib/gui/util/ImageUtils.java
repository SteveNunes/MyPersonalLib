package gui.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

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

	public static Image removeBgColor(Image image, int toleranceThreshold) {
		return removeBgColor(image, Color.valueOf("#00FF00"), toleranceThreshold);
	}

	public static Image removeBgColor(Image image, Color transparentColor) {
		return removeBgColor(image, transparentColor, 0);
	}

	public static Image removeBgColor(Image image) {
		return removeBgColor(image, Color.WHITE, 0);
	}

}
