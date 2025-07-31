package gameutil;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import enums.Icons;
import gui.util.Alerts;
import gui.util.ControllerUtils;
import gui.util.ImageUtils;
import gui.util.ListenerHandle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Misc;
import util.MyFile;

public class PalleteEditor {
	
	@FXML
	private HBox hBoxCanvas;
	@FXML
	private VBox vBoxControls;
	@FXML
	private Button buttonAutoGenerateOriginalPallete;
	@FXML
	private Button buttonLoadFromDisk;
	@FXML
	private Button buttonSaveToDisk;
  @FXML
  private Button buttonAddColor;
  @FXML
  private Button buttonAddPallete;
  @FXML
  private Button buttonRemovePallete;
  @FXML
  private Button buttonCopyOriginalPallete;
  @FXML
  private Button buttonPasteOriginalPallete;
  @FXML
  private Button buttonCopyCurrentPallete;
  @FXML
  private Button buttonPasteCurrentPallete;
  @FXML
  private Canvas canvasMain;
  @FXML
  private ComboBox<Integer> comboBoxPalleteIndex;
  @FXML
  private FlowPane flowPaneOriginalColors;
  @FXML
  private FlowPane flowPanePalleteColors;
  @FXML
  private HBox hBoxPalleteColors;
  @FXML
  private ColorPicker colorPickerTransparentColor;

  private static Stage stage;
  private static Scene scene;

  private Color transparentColor;
  private ContextMenu colorContextMenu;
  private Point mousePosition;
  private Point previewMousePosition;
  private ImageView pickingColorImageView;
  private Canvas colorSquareCanvas = new Canvas(20, 20);
  private GraphicsContext colorSquareGc = colorSquareCanvas.getGraphicsContext2D();
  private ListenerHandle<Integer> listenerHandle;
	private WritableImage originalSprite;
	private WritableImage currentSprite;
	private GraphicsContext gcMain;
	private String originalSpriteFileName;
	private Robot robot;
	private List<Color> copiedPallete;
	private List<List<Color>> palletes;
  private int pickingColorIndex;
	private int palleteIndex;
	private int blinkIndex;
	private boolean showOriginal;
  private boolean close;
	
  public static void openEditor() {
  	try {
  		stage = new Stage();
			FXMLLoader loader = new FXMLLoader(ColorMixEditor.class.getResource("/gameutil/PalleteEditorView.fxml"));
			scene = new Scene(loader.load());
			stage.setScene(scene);
			stage.setTitle("Pallete Editor");
			((PalleteEditor)loader.getController()).init();
			stage.sizeToScene();
			stage.show();
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
  }

	public void init() {
		try {
			transparentColor = Color.color(1, 0, 1);
			robot = new Robot();
			mousePosition = new Point();
			gcMain = canvasMain.getGraphicsContext2D();
			gcMain.setImageSmoothing(false);
			vBoxControls.setDisable(true);
			buttonSaveToDisk.setDisable(true);
			showOriginal = false;
			pickingColorImageView = null;
			currentSprite = null;
			colorContextMenu = null;
			copiedPallete = null;
			blinkIndex = -1;
			setNodeEvents();
			drawMainCanvas();
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			e.printStackTrace();
		}
	}
	
	private void setNodeEvents() {
		colorPickerTransparentColor.valueProperty().addListener((o, oldV, newV) -> {
			transparentColor = newV;
			PalleteTools.setTransparentColor(newV);
			Alerts.warning("Aviso", "Você deve recarregar a imagem para que a alteração entre em efeito.");
		});
		buttonPasteCurrentPallete.setDisable(true);
		buttonPasteOriginalPallete.setDisable(true);
		buttonCopyCurrentPallete.setDisable(true);
		buttonCopyCurrentPallete.setOnAction(e -> {
			copiedPallete = currentPallete();
			buttonPasteCurrentPallete.setDisable(false);
			buttonPasteOriginalPallete.setDisable(false);
		});
		buttonCopyOriginalPallete.setOnAction(e -> {
			copiedPallete = originalPallete();
			buttonPasteCurrentPallete.setDisable(false);
			buttonPasteOriginalPallete.setDisable(false);
		});
		buttonPasteCurrentPallete.setOnAction(e -> {
			currentPallete().clear();
			currentPallete().addAll(copiedPallete);
			fillOtherPalletesToMatchOriginalPalleteSize();
			regeneratePalleteColors(flowPanePalleteColors);
		});
		buttonPasteOriginalPallete.setOnAction(e -> {
			originalPallete().clear();
			originalPallete().addAll(copiedPallete);
			regeneratePalleteColors(flowPaneOriginalColors);
		});
		buttonAutoGenerateOriginalPallete.setOnAction(e -> autoGenerateOriginalPallete());
		ControllerUtils.addIconToButton(buttonAutoGenerateOriginalPallete, Icons.REFRESH.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonAddColor, Icons.PLUS.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonCopyCurrentPallete, Icons.COPY.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonCopyOriginalPallete, Icons.COPY.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonPasteCurrentPallete, Icons.PASTE.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonPasteOriginalPallete, Icons.PASTE.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonAddPallete, Icons.PLUS.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonRemovePallete, Icons.DELETE.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonLoadFromDisk, Icons.OPEN_FILE.getValue(), 16, 16);
		ControllerUtils.addIconToButton(buttonSaveToDisk, Icons.SAVE.getValue(), 16, 16);
		listenerHandle = new ListenerHandle<>(comboBoxPalleteIndex.valueProperty(), (obs, olvV, newV) -> {
			blinkIndex = -1;
			palleteIndex = newV;
			hBoxPalleteColors.setDisable(newV == 0);
			if (newV > 0)
				regeneratePalleteColors(flowPanePalleteColors);
			else
				flowPanePalleteColors.getChildren().clear();
			buttonRemovePallete.setDisable(newV == 0);
			buttonCopyCurrentPallete.setDisable(newV == 0);
		});
		for (FlowPane flowPane : Arrays.asList(flowPaneOriginalColors, flowPanePalleteColors)) {
			flowPane.setHgap(5);
			flowPane.setOrientation(Orientation.HORIZONTAL);
		}
		canvasMain.setOnMouseMoved(e -> mousePosition.setLocation(e.getX(), e.getY()));
		canvasMain.setOnMouseClicked(e -> {
			canvasMain.requestFocus();
			if (e.getButton() == MouseButton.PRIMARY && pickingColorImageView != null) {
				Color color = originalSprite.getPixelReader().getColor((int)e.getX() / 3, (int)e.getY() / 3);
				if (originalPallete().contains(color)) {
					Alerts.error("Erro", "Essa cor já está presente na paleta principal");
					pickingColorImageView.setImage(getColoredSquare(originalPallete().get(pickingColorIndex)));
					pickingColorImageView = null;
					return;
				}
				originalPallete().set(pickingColorIndex, color);
				updateOriginalSpritePalletes();
				robot.mouseMove((int)previewMousePosition.getX(), (int)previewMousePosition.getY());
				pickingColorImageView = null;
			}
		});
		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.SPACE)
				showOriginal = true;
		});
		scene.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.SPACE)
				showOriginal = false;
		});
		buttonAddPallete.setOnAction(e -> {
			palletes.add(new ArrayList<>(currentPallete()));
			comboBoxPalleteIndex.getItems().add(palletes.size() - 1);
			comboBoxPalleteIndex.getSelectionModel().select(palletes.size() - 1);
		});
		buttonRemovePallete.setOnAction(e -> {
			if (palletes.size() == 1) {
				Alerts.error("Erro", "Você nâo pode apagar a única paleta");
				return;
			}
			if (Alerts.confirmation("Excluir paleta", "Deseja mesmo excluir a paleta de cores atual?")) {
				listenerHandle.detach();
				comboBoxPalleteIndex.getItems().clear();
				palletes.remove(palleteIndex);
				if (palleteIndex == palletes.size())
					palleteIndex--;
				for (int n = 0; n < palletes.size(); n++)
					comboBoxPalleteIndex.getItems().add(n);
				listenerHandle.attach();
				comboBoxPalleteIndex.getSelectionModel().select(palleteIndex);
			}
		});
		buttonLoadFromDisk.setOnAction(e -> loadFromDisk());
		buttonSaveToDisk.setOnAction(e -> saveToDisk());
		buttonAddColor.setOnAction(e -> {
			if (originalPallete().get(originalPallete().size() - 1).equals(transparentColor)) {
				Alerts.error("Erro", "Edite a cor adicionada previamente para adicionar novas cores");
				return;
			}
			for (int n = 0; n < palletes.size(); n++)
				palletes.get(n).add(transparentColor);
			regeneratePalleteColors(flowPaneOriginalColors);
			regeneratePalleteColors(flowPanePalleteColors);
		});
	}

	private void autoGenerateOriginalPallete() {
		if (palletes.isEmpty()) {
			palletes.add(PalleteTools.getPalleteFromImage(originalSprite));
			comboBoxPalleteIndex.getItems().add(0);
			comboBoxPalleteIndex.getSelectionModel().select(0);
		}
		fillOtherPalletesToMatchOriginalPalleteSize();
		regeneratePalleteColors(flowPaneOriginalColors);
		regeneratePalleteColors(flowPanePalleteColors);
	}

	private void fillOtherPalletesToMatchOriginalPalleteSize() {
		for (int n = 1; n < palletes.size(); n++) {
			List<Color> colors1 = palletes.get(0);
			List<Color> colors2 = palletes.get(n);
			while (colors2.size() > colors1.size())
				colors2.remove(colors2.size() - 1);
			while (colors2.size() < colors1.size())
				colors2.add(colors1.get(colors2.size()));
		}
	}

	private void loadFromDisk() {
    File file = MyFile.selectFileToOpenJavaFX(stage, "./", new FileChooser.ExtensionFilter("Imagens PNG", "*.png"), "Selecione o arquivo de sprite");
    if (file != null) {
			originalSpriteFileName = file.getAbsoluteFile().toString();
			originalSprite = ImageUtils.loadWritableImageFromFile(originalSpriteFileName);
			palleteIndex = 0;
			palletes = PalleteTools.getPalleteListFromImage(originalSprite, transparentColor);
			if (palletes == null) {
				palletes = new ArrayList<>();
				palletes.add(PalleteTools.getPalleteFromImage(originalSprite, transparentColor));
				int w = (int)originalSprite.getWidth(), h = (int)originalSprite.getHeight() + 1;
				Canvas c = new Canvas(w, h);
				GraphicsContext gc = c.getGraphicsContext2D();
				gc.setImageSmoothing(false);
				gc.setFill(transparentColor);
				gc.fillRect(0, 0, w, h);
				gc.drawImage(originalSprite, 0, 0, w, h - 1, 0, 1, w, h - 1);
				originalSprite = ImageUtils.getCanvasSnapshot(c);
			}
			canvasMain.setWidth(originalSprite.getWidth() * 3);
			canvasMain.setHeight(originalSprite.getHeight() * 3 - 3);
			vBoxControls.setDisable(false);
			buttonSaveToDisk.setDisable(false);
			listenerHandle.detach();
			comboBoxPalleteIndex.getItems().clear();
			for (int n = 0; n < palletes.size(); n++)
				comboBoxPalleteIndex.getItems().add(n);
			listenerHandle.attach();
			comboBoxPalleteIndex.getSelectionModel().select(0);
			originalSprite = (WritableImage)ImageUtils.removeBgColor(originalSprite, transparentColor);
			regeneratePalleteColors(flowPaneOriginalColors);
			stage.sizeToScene();
    }
	}
	
	private void saveToDisk() {
		File file = MyFile.selectFileToSaveJavaFX(stage, "C:\\", "Informe o arquivo de destino");
		if (file == null)
			return;
		int x = 0;
		PixelWriter pw = originalSprite.getPixelWriter();
		for (List<Color> colors : palletes) {
			if (x > 0)
				pw.setColor(x++, 0, transparentColor);
			for (Color color : colors)
				pw.setColor(x++, 0, color);
		}
		while (x < originalSprite.getWidth())
			pw.setColor(x++, 0, transparentColor);
		ImageUtils.saveImageToFile(originalSprite, file.getAbsolutePath());
	}
	
	private WritableImage getColoredSquare(Color color) {
		colorSquareGc.setFill(color);
		colorSquareGc.fillRect(0, 0, colorSquareCanvas.getWidth(), colorSquareCanvas.getHeight());
		return ImageUtils.getCanvasSnapshot(colorSquareCanvas);
	}
	
	private void drawMainCanvas() {
		GameUtils.createAnimationTimer(60, (totalFrames, fps) -> close, () -> {
			if (originalSprite != null) {
				updateCurrentSprite();
				gcMain.setFill(transparentColor);
				gcMain.fillRect(0, 0, canvasMain.getWidth(), canvasMain.getHeight());
				gcMain.drawImage(showOriginal ? originalSprite : currentSprite, 0, 0, (int)currentSprite.getWidth(), (int)currentSprite.getHeight(), 0, 0, (int)currentSprite.getWidth() * 3, (int)currentSprite.getHeight() * 3);
				if (pickingColorImageView != null) {
					int x = (int)mousePosition.getX(), y = (int)mousePosition.getY();
					Color color = originalSprite.getPixelReader().getColor(x / 3, y / 3);
					pickingColorImageView.setImage(getColoredSquare(color));
					gcMain.setStroke(Color.BLACK);
					gcMain.setLineWidth(4);
					gcMain.setFill(transparentColor);
					gcMain.fillRect(x - 150, y - 150, 300, 300);
					gcMain.drawImage(originalSprite, x / 3 - 15, y / 3 - 15, 30, 30, x - 150, y - 150, 300, 300);
					gcMain.strokeRect(x - 150, y - 150, 300, 300);
				}
			}
		}).start();
	}

	private List<Color> originalPallete() {
		if (palletes.isEmpty())
			autoGenerateOriginalPallete();
		return palletes.get(0);
	}
	
	private List<Color> currentPallete() {
		return palletes.get(palleteIndex);
	}
	
	private void regeneratePalleteColors(FlowPane flowPane) {
		List<Color> colors = flowPane == flowPaneOriginalColors ? originalPallete() : currentPallete();
		if (PalleteTools.isColorMixPallete(colors)) {
			flowPane.getChildren().clear();
			flowPane.getChildren().add(new Text("Paleta de cores editavel somente através do \"COLOR_MIX_EDITOR\""));
			hBoxPalleteColors.setDisable(true);
			return;
		}
		hBoxPalleteColors.setDisable(false);
		flowPane.getChildren().clear();
		flowPane.setPrefWidth(colors.size() * 25);
		for (int n = 0; n < colors.size(); n++) {
			ImageView iv = new ImageView(getColoredSquare(colors.get(n)));
			final int n2 = n;
			iv.setOnMouseMoved(e -> blinkIndex = n2);
			iv.setOnMouseExited(e -> blinkIndex = -1);
			if (flowPane == flowPanePalleteColors)
				setColorPicker(iv, n);
			else {
				iv.setOnMouseClicked(e -> {
					if (e.getButton() == MouseButton.PRIMARY) {
						pickingColorIndex = n2;
						pickingColorImageView = iv;
		      	previewMousePosition = MouseInfo.getPointerInfo().getLocation();
		      	robot.mouseMove((int) (canvasMain.localToScreen(0, 0).getX() + canvasMain.getWidth() / 2),
		      												(int) (canvasMain.localToScreen(0, 0).getY() + canvasMain.getHeight() / 2));
		      }
					else if (e.getButton() == MouseButton.SECONDARY) {
						colorContextMenu = new ContextMenu(); 
						MenuItem menuItem = new MenuItem("Excluir cor");
						menuItem.setOnAction(ex -> {
							if (Alerts.confirmation("Excluir cor", "Deseja mesmo excluir a cor selecionada da paleta de cores atual?")) {
								originalPallete().remove(n2);
								currentPallete().remove(n2);
								regeneratePalleteColors(flowPaneOriginalColors);
								regeneratePalleteColors(flowPanePalleteColors);
							}
						});
						colorContextMenu.getItems().add(menuItem);
						colorContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
					}
				});
			}
			flowPane.getChildren().add(iv);
		}
		stage.sizeToScene();
	}

	private void setColorPicker(ImageView iv, int colorIndex) {
		iv.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				ColorPicker colorPicker = new ColorPicker(currentPallete().get(colorIndex));
				colorPicker.valueProperty().addListener((o, oldC, newC) ->{
					currentPallete().set(colorIndex, newC);
					iv.setImage(getColoredSquare(newC));
				});
				Stage stage = new Stage();
				VBox vBox = new VBox(colorPicker);
				Scene scene = new Scene(vBox);
				stage.setTitle("Select a color");
				stage.setOnCloseRequest(ex -> updateOriginalSpritePalletes());
				stage.setScene(scene);
				stage.show();
			}
		});
	}

	private void updateCurrentSprite() {
		if (PalleteTools.isColorMixPallete(currentPallete())) {
			currentSprite = PalleteTools.applyColorMixPalleteOnImage(originalSprite, currentPallete());
			return;
		}
		List<Color> pallete = new ArrayList<>(currentPallete());
		if (Misc.blink(100) && blinkIndex >= 0 &&  blinkIndex < pallete.size())
			pallete.set(blinkIndex, transparentColor);
		currentSprite = PalleteTools.applyColorPalleteOnImage(originalSprite, originalPallete(), pallete);
	}
	
	private void updateOriginalSpritePalletes() {
		int x = 0;
		PixelWriter pw = originalSprite.getPixelWriter();
		for (int xx = 0; xx < originalSprite.getWidth(); xx++)
			pw.setColor(xx++, 0, transparentColor);
		for (List<Color> colors : palletes) {
			for (Color color : colors)
				pw.setColor(x++, 0, color);
			pw.setColor(x++, 0, transparentColor);
		}
	}

}
