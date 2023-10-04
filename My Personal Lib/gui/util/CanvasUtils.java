package gui.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

public abstract class CanvasUtils {

	public static void copyCanvas(Canvas sourceCanvas, Canvas targetCanvas) {
		int sourceW = (int)sourceCanvas.getWidth();
		int sourceH = (int)sourceCanvas.getHeight();
		int targetW = (int)targetCanvas.getWidth();
		int targetH = (int)targetCanvas.getHeight();
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		GraphicsContext gc = targetCanvas.getGraphicsContext2D();
		WritableImage snapshot = sourceCanvas.snapshot(params, null);
		if (sourceW == targetW && sourceH == targetH)
			gc.drawImage(snapshot, 0, 0);
		else {
	    ImageView imageView = new ImageView(snapshot);
	    WritableImage resizedImage = new WritableImage((int) targetW, (int) targetH);
	    imageView.setFitWidth(targetW);
	    imageView.setFitHeight(targetH);
	    imageView.setPreserveRatio(true);
	    imageView.snapshot(null, resizedImage);
	    gc.drawImage(resizedImage, 0, 0);
		}
	}
	
	public static void saveCanvasToFile(Canvas canvas, String filePath) {
		int width = (int)canvas.getWidth(), height = (int)canvas.getHeight();
    PixelReader pixelReader = canvas.snapshot(null, null).getPixelReader();
    WritablePixelFormat<IntBuffer> pixelFormat = WritablePixelFormat.getIntArgbInstance();
    int[] buffer = new int[width * height];
    pixelReader.getPixels(0, 0, width, height, pixelFormat, buffer, 0, width);
    ByteBuffer byteBuffer = ByteBuffer.allocate(width * height * 4);
    for (int i = 0; i < buffer.length; i++) {
    	int argb = buffer[i];
    	byteBuffer.putInt(argb);
    }
    try {
    	BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	byte[] imageData = byteBuffer.array();
    	bufferedImage.setRGB(0, 0, width, height, byteArrauToIntArray(imageData), 0, width);
    	File file = new File(filePath);
    	ImageIO.write(bufferedImage, "png", file);
    }
    catch (IOException e)
    	{ e.printStackTrace(); }
	}

  private static int[] byteArrauToIntArray(byte[] byteArray) {
    int[] intArray = new int[byteArray.length / 4];
    ByteBuffer.wrap(byteArray).asIntBuffer().get(intArray);
    return intArray;
  }

}
