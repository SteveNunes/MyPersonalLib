package gui.util;

import java.awt.Point;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Rectangle2DEditor {

	private int corner;
	private int cornerOver;
	private int cornerMove[][];
	private Point clickedPos;
	private Point[] corners;
	private Rectangle2D[] editingBounds;
	private Rectangle2D previewBounds;
	private Stage stage;
	private StackPane stackPane;
	private Canvas canvas;
	private GraphicsContext gc;
	private GraphicsContext gcOut;
	private boolean opaqueRectangleArea;
	private ContextMenu contextMenu;
	
	public Rectangle2DEditor(String title, Canvas outputCanvas, Rectangle2D currentBounds, Consumer<Rectangle2D> onEditEvent, Consumer<Rectangle2D> onCloseEvent) {
		opaqueRectangleArea = false;
		editingBounds = new Rectangle2D[]{currentBounds};
		corner = -1;
		cornerOver = -1;
		cornerMove = new int[][] {{1, 1}, {0, 1}, {2, 1}, {1, 0}, {2, 0}, {1, 2}, {0, 2}, {2, 2}, {3, 3}};
		clickedPos = null;
		corners = null;
		stage = new Stage();
		stage.setTitle(title);
		stage.setResizable(false);
		stackPane = new StackPane();
		canvas = new Canvas(outputCanvas.getWidth(), outputCanvas.getHeight());
		gc = canvas.getGraphicsContext2D();
		gcOut = outputCanvas.getGraphicsContext2D();
		clickedPos = new Point();
		canvas.setOnMousePressed(e -> {
			int[] mousePos = getMousePos(e);
			if(e.getButton() == MouseButton.PRIMARY) {
				previewBounds = new Rectangle2D(editingBounds[0].getMinX(), editingBounds[0].getMinY(), editingBounds[0].getWidth(), editingBounds[0].getHeight());
				updateCorners();
				corner = mouseIsInsideRect(mousePos) ? 8 : -1;
				for (int n = 0; n < 8; n++) {
					int xx = (int)corners[n].getX(), yy = (int)corners[n].getY();
					if (mousePos[0] >= xx - 8 && mousePos[0] <= xx + 8 &&
							mousePos[1] >= yy - 8 && mousePos[1] <= yy + 8)
								corner = n;
				}
				clickedPos.setLocation(mousePos[0], mousePos[1]);
				updateCanvas(onEditEvent);
			}
			else if(e.getButton() == MouseButton.SECONDARY) {
				BiConsumer<Integer, Integer> consumer = (width, height) -> {
					double x = editingBounds[0].getMinX(), y = editingBounds[0].getMinY(),
								 w = editingBounds[0].getWidth(), h = editingBounds[0].getHeight();
					if (width + height == 0) {
						x = 0;
						y = 0;
						w = canvas.getWidth();
						h = canvas.getHeight();
					}
					else
						h = w / width * height;
					editingBounds[0] = new Rectangle2D(x, y, w, h);
					updateCorners();
					updateCanvas(onEditEvent);
				};
				contextMenu = new ContextMenu();
				MenuItem menuItem = new MenuItem((opaqueRectangleArea ? "Desativar" : "Ativar") + " preenchimento");
				menuItem.setOnAction(ex -> opaqueRectangleArea = !opaqueRectangleArea);
				Menu menu = new Menu("Forçar formato");
				MenuItem menuItem1 = new MenuItem("Janela inteira");
				menuItem1.setOnAction(ex -> consumer.accept(0,0));
				MenuItem menuItem2 = new MenuItem("1:1");
				menuItem2.setOnAction(ex -> consumer.accept(1,1));
				MenuItem menuItem3 = new MenuItem("4:3");
				menuItem3.setOnAction(ex -> consumer.accept(4,3));
				MenuItem menuItem4 = new MenuItem("3:4");
				menuItem4.setOnAction(ex -> consumer.accept(3,4));
				MenuItem menuItem5 = new MenuItem("16:9");
				menuItem5.setOnAction(ex -> consumer.accept(16,9));
				MenuItem menuItem6 = new MenuItem("9:16");
				menuItem6.setOnAction(ex -> consumer.accept(9,16));
				menu.getItems().addAll(menuItem1, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6);
				contextMenu.getItems().addAll(menuItem, menu);
				contextMenu.show(stage, e.getScreenX(), e.getScreenY());
			}
			updateCanvas(onEditEvent);
		});
		canvas.setOnMouseMoved(e -> {
			int[] mousePos = getMousePos(e);
			cornerOver = mouseIsInsideRect(mousePos) ? 8 : -1;
			updateCorners();
			for (int n = 0; n < 8; n++) {
				int xx = (int)corners[n].getX(), yy = (int)corners[n].getY();
				if (mousePos[0] >= xx - 8 && mousePos[0] <= xx + 8 &&
						mousePos[1] >= yy - 8 && mousePos[1] <= yy + 8)
							cornerOver = n;
			}
			updateCanvas(onEditEvent);
		});
		canvas.setOnMouseDragged(e -> {
			int[] mousePos = getMousePos(e);
			if (corner > -1) {
				int x = cornerMove[corner][0], y = cornerMove[corner][1];
				int incX = (int)(mousePos[0] - clickedPos.getX()),
						incY = (int)(mousePos[1] - clickedPos.getY());
				int boundsX1 = (int)previewBounds.getMinX() + (x == 1 || x == 3 ? incX : 0),
						boundsY1 = (int)previewBounds.getMinY() + (y == 1 || y == 3 ? incY : 0),
						boundsX2 = (int)previewBounds.getMaxX() + (x == 2 || x == 3 ? incX : 0),
						boundsY2 = (int)previewBounds.getMaxY() + (y == 2 || y == 3 ? incY : 0);
				editingBounds[0] = new Rectangle2D(boundsX1, boundsY1, boundsX2 - boundsX1, boundsY2 - boundsY1);
				updateCorners();
			}
			updateCanvas(onEditEvent);
		});
		canvas.setOnMouseReleased(e -> {
			if(e.getButton() == MouseButton.PRIMARY)
				corner = -1;
			updateCanvas(onEditEvent);
		});
		stackPane.getChildren().add(canvas);
		Scene scene = new Scene(stackPane);
		stage.setScene(scene);
		stage.setWidth((int)outputCanvas.getWidth()); 
		stage.setHeight((int)outputCanvas.getHeight());
		scene.setOnKeyPressed(e -> {
			int x = (int)editingBounds[0].getMinX(),
					y = (int)editingBounds[0].getMinY(),
					w = (int)editingBounds[0].getWidth(),
					h = (int)editingBounds[0].getHeight(),
					v = e.isControlDown() ? 10 : e.isShiftDown() ? 5 : 1;
			if (e.isShiftDown()) {
				if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN)
					w += e.getCode() == KeyCode.UP ? -v : v;
				else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT)
					w += e.getCode() == KeyCode.LEFT ? -v : v;
			}
			else if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN)
				y += e.getCode() == KeyCode.UP ? -v : v;
			else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT)
				x += e.getCode() == KeyCode.LEFT ? -v : v;
			editingBounds[0] = new Rectangle2D(x, y, w, h);
			updateCorners();
			updateCanvas(onEditEvent);
		});
		updateCorners();
		updateCanvas(onEditEvent);
		stage.setOnCloseRequest(e -> {
			outputCanvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			if (onCloseEvent != null)
				onCloseEvent.accept(currentBounds);
		});
		stage.sizeToScene();
		stage.show();
	}
	
	private int[] getMousePos(MouseEvent e) {
		int x = (int)e.getX(), y = (int)e.getY();
		if (x < 0)
			x = 0;
		if (x > canvas.getWidth())
			x = (int)canvas.getWidth();
		if (y < 0)
			y = 0;
		if (y > canvas.getHeight())
			y = (int)canvas.getHeight();
		return new int[] {x, y};
	}

	public void close() {
		if (stage != null) {
			stage.close();
			stage = null;
		}
	}

	private boolean mouseIsInsideRect(int[] mousePos) {
		int x = (int)mousePos[0], y = (int)mousePos[1],
				x1 = (int)editingBounds[0].getMinX(), y1 = (int)editingBounds[0].getMinY(),
				x2 = (int)editingBounds[0].getMaxX(), y2 = (int)editingBounds[0].getMaxY();
		return x >= x1 && y >= y1 && x <= x2 && y <= y2;
	}

	private void updateCorners() {
		int x1 = (int)editingBounds[0].getMinX(), y1 = (int)editingBounds[0].getMinY(),
				x2 = (int)editingBounds[0].getMaxX(), y2 = (int)editingBounds[0].getMaxY(),
				w = (int)editingBounds[0].getWidth(), h = (int)editingBounds[0].getHeight();
		corners = new Point[] {
				new Point(x1, y1),
				new Point(x1 + w / 2, y1),
				new Point(x2, y1),
				new Point(x1, y1 + h / 2),
				new Point(x2, y1 + h / 2),
				new Point(x1, y2),
				new Point(x1 + w / 2, y2),
				new Point(x2, y2)
		};
	}

	private void updateCanvas(Consumer<Rectangle2D> onEditEvent) {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gcOut.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (GraphicsContext gc : Arrays.asList(gc, gcOut)) {
			if (opaqueRectangleArea) {
				gc.setFill(corner == 8 ? Color.GREEN : cornerOver == 8 ? Color.ORANGE : Color.RED);
				gc.fillRect((int)editingBounds[0].getMinX() + 1, (int)editingBounds[0].getMinY(), (int)editingBounds[0].getWidth(), (int)editingBounds[0].getHeight());
			}
			else {
				gc.setStroke(corner == 8 ? Color.GREEN : cornerOver == 8 ? Color.ORANGE : Color.RED);
				gc.setLineWidth(2);
				gc.strokeRect((int)editingBounds[0].getMinX() + 1, (int)editingBounds[0].getMinY(), (int)editingBounds[0].getWidth(), (int)editingBounds[0].getHeight());
			}
		}
		for (int n = 0; n < 8; n++) {
			gc.setFill(corner == n ? Color.GREEN : cornerOver == n ? Color.ORANGE : Color.YELLOW);
			gc.fillRect(corners[n].getX() - 8, corners[n].getY() - 8, 16, 16);
		}
		if (onEditEvent != null)
			onEditEvent.accept(editingBounds[0]);
	}
	
}
