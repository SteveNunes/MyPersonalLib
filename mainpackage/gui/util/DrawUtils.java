package gui.util;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public abstract class DrawUtils {

	public static Rectangle drawRectangle(ObservableList<Node> childrens, double x, double y, double width, double height, Color fillColor, Color strokeColor, int strokeWidth, double opacity, double rotate) {
		Rectangle rectangle = new Rectangle();
		rectangle.setX(x);
		rectangle.setY(y);
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		rectangle.setFill(fillColor);
		rectangle.setStrokeWidth(strokeWidth);
		rectangle.setStroke(strokeColor);
		rectangle.setOpacity(opacity);
		rectangle.setRotate(rotate);
		childrens.add(rectangle);
		return rectangle;
	}

	public static Rectangle drawRectangle(ObservableList<Node> childrens, double x, double y, double width, double height, Color fillColor, Color strokeColor, int strokeWidth) {
		return drawRectangle(childrens, x, y, width, height, fillColor, strokeColor, strokeWidth, 1, 0);
	}

	public static Line drawLine(ObservableList<Node> childrens, double startX, double startY, double endX, double endY, Color strokeColor, int strokeWidth, double opacity, double rotate) {
		Line line = new Line();
		line.setStartX(startX);
		line.setStartY(startY);
		line.setEndX(endX);
		line.setEndY(endY);
		line.setStrokeWidth(strokeWidth);
		line.setStroke(strokeColor);
		line.setOpacity(opacity);
		line.setRotate(rotate);
		childrens.add(line);
		return line;
	}

	public static Line drawLine(ObservableList<Node> childrens, double startX, double startY, double endX, double endY, Color strokeColor, int strokeWidth) {
		return drawLine(childrens, startX, startY, endX, endY, strokeColor, strokeWidth, 1, 0);
	}

	public static Text drawText(ObservableList<Node> childrens, double x, double y, Font font, String text, Color color, int strokeWidth, double opacity, double rotate) {
		Text txt = new Text();
		txt.setText(text);
		txt.setX(x);
		txt.setY(y);
		txt.setFont(font);
		txt.setFill(color);
		txt.setStrokeWidth(strokeWidth);
		txt.setOpacity(opacity);
		txt.setRotate(rotate);
		childrens.add(txt);
		return txt;
	}

	public static Text drawText(ObservableList<Node> childrens, double x, double y, Font font, String text, Color color, int strokeWidth) {
		return drawText(childrens, x, y, font, text, color, strokeWidth, 1, 0);
	}

	public static Polygon drawPolygon(ObservableList<Node> childrens, List<Double> points, Color strokeColor, int strokeWidth, Color fillColor, double opacity, double rotate) {
		Polygon polygon = new Polygon();
		polygon.getPoints().setAll(400.0, 200.0, 500.0, 300.0, 400.0, 300.0);
		polygon.setFill(fillColor);
		polygon.setStrokeWidth(strokeWidth);
		polygon.setStroke(strokeColor);
		polygon.setOpacity(opacity);
		polygon.setRotate(rotate);
		childrens.add(polygon);
		return polygon;
	}

	public static Polygon drawPolygon(ObservableList<Node> childrens, List<Double> points, Color strokeColor, int strokeWidth, Color fillColor) {
		return drawPolygon(childrens, points, strokeColor, strokeWidth, fillColor, 1, 0);
	}

}
