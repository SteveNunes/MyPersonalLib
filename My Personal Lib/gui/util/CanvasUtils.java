package gui.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class CanvasUtils {

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
	
}
