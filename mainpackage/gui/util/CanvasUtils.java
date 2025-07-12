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
import javafx.scene.paint.Color;

public abstract class CanvasUtils {

	private static WritableImage copyCanvas;
	private static SnapshotParameters copyCanvasParam;
	
	public static void copyAndRotateAndStretchToFillCanvas(Canvas sourceCanvas, Canvas targetCanvas, double rotationDegrees) {

		if (sourceCanvas == null || targetCanvas == null)
			throw new RuntimeException("Erro: Canvas de origem ou destino é nulo.");

		copyCanvas = new WritableImage((int) sourceCanvas.getWidth(), (int) sourceCanvas.getHeight());

		if (copyCanvas == null || (int)copyCanvas.getWidth() != (int)sourceCanvas.getWidth() ||
				(int)copyCanvas.getHeight() != (int)sourceCanvas.getHeight()) {
					copyCanvasParam = new SnapshotParameters();
					copyCanvasParam.setFill(Color.TRANSPARENT);
					copyCanvasParam.setViewport(new Rectangle2D(0, 0, sourceCanvas.getWidth(), sourceCanvas.getHeight()));
		}
		sourceCanvas.snapshot(copyCanvasParam, copyCanvas);

		GraphicsContext gcTarget = targetCanvas.getGraphicsContext2D();
		gcTarget.clearRect(0, 0, targetCanvas.getWidth(), targetCanvas.getHeight());

		double targetWidth = targetCanvas.getWidth();
		double targetHeight = targetCanvas.getHeight();

		gcTarget.save();
		gcTarget.translate(targetWidth / 2, targetHeight / 2);
		gcTarget.rotate(rotationDegrees);

		double newImageWidth;
		double newImageHeight;

		double normalizedDegrees = rotationDegrees % 360;
		if (normalizedDegrees < 0)
			normalizedDegrees += 360;

		if (normalizedDegrees == 90 || normalizedDegrees == 270) {
			newImageWidth = targetHeight;
			newImageHeight = targetWidth;
		}
		else {
			newImageWidth = targetWidth;
			newImageHeight = targetHeight;
		}

		gcTarget.drawImage(copyCanvas, -newImageWidth / 2, -newImageHeight / 2, newImageWidth, newImageHeight);
		gcTarget.restore();
	}
	
	public static void copyCanvas(Canvas sourceCanvas, Rectangle2D sourceArea, Canvas targetCanvas, Rectangle2D targetArea) {
    GraphicsContext gc = targetCanvas.getGraphicsContext2D();
    SnapshotParameters params = new SnapshotParameters();
    params.setViewport(sourceArea);
		params.setFill(Color.TRANSPARENT);
    WritableImage snapshot = sourceCanvas.snapshot(params, null);
    gc.drawImage(snapshot, sourceArea.getMinX(), sourceArea.getMinY(), sourceArea.getWidth(), sourceArea.getHeight(), targetArea.getMinX(), targetArea.getMinY(), targetArea.getWidth(), targetArea.getHeight());
  }
	
	public static void copyCanvas(Canvas sourceCanvas, Rectangle2D sourceArea, Canvas targetCanvas)
		{ copyCanvas(sourceCanvas, sourceArea, targetCanvas, new Rectangle2D(0, 0, targetCanvas.getWidth(), targetCanvas.getHeight())); }
	
	public static void copyCanvas(Canvas sourceCanvas, Canvas targetCanvas, Point point)
		{ copyCanvas(sourceCanvas, new Rectangle2D(0, 0, sourceCanvas.getWidth(), sourceCanvas.getHeight()), targetCanvas, new Rectangle2D(point.getX(), point.getY(), sourceCanvas.getWidth(), sourceCanvas.getHeight())); }

	public static void copyCanvas(Canvas sourceCanvas, Canvas targetCanvas, Rectangle2D outputArea)
		{ copyCanvas(sourceCanvas, new Rectangle2D(0, 0, sourceCanvas.getWidth(), sourceCanvas.getHeight()), targetCanvas, outputArea); }

	public static void copyCanvas(Canvas sourceCanvas, Canvas targetCanvas)
		{ copyCanvas(sourceCanvas, new Rectangle2D(0, 0, sourceCanvas.getWidth(), sourceCanvas.getHeight()), targetCanvas); }

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
