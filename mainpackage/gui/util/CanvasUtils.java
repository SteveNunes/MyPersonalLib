package gui.util;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

public abstract class CanvasUtils {

	public static void copyCanvas(Canvas sourceCanvas, Rectangle2D sourceArea, Canvas targetCanvas, Point targetCoordinate) {
    GraphicsContext gc = targetCanvas.getGraphicsContext2D();
    SnapshotParameters params = new SnapshotParameters();
    params.setViewport(sourceArea);
    WritableImage snapshot = sourceCanvas.snapshot(params, null);
    gc.drawImage(snapshot, targetCoordinate.getX(), targetCoordinate.getY());
  }
	
	public static void copyCanvas(Canvas sourceCanvas, Rectangle2D sourceArea, Canvas targetCanvas)
		{ copyCanvas(sourceCanvas, sourceArea, targetCanvas, new Point(0, 0)); }
	
	public static void copyCanvas(Canvas sourceCanvas, Canvas targetCanvas, Point point)
		{ copyCanvas(sourceCanvas, new Rectangle2D(0, 0, sourceCanvas.getWidth(), sourceCanvas.getHeight()), targetCanvas, point); }

	public static void copyCanvas(Canvas sourceCanvas, Canvas targetCanvas)
		{ copyCanvas(sourceCanvas, targetCanvas, new Point(0, 0)); }

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
