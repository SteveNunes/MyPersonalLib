package gui.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import enums.HBoxWitnButtonsEditMode;
import enums.HBoxWitnButtonsMode;
import enums.Icons;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class HBoxWithButtons {
	
	private HBox hBoxMain;
	private HBox hBoxButtons;
	private HBox hBoxText;
	private HBoxWitnButtonsMode mode;
	private HBoxWitnButtonsEditMode editMode;
	private ImageView icon;
	private BorderPane borderPane;
	private double buttonSize;
	private double buttonSpacing;
	private EditableHBox editableHBox;
	private String previewText;
	
	public HBoxWithButtons(HBoxWitnButtonsMode mode) {
		previewText = "";
		hBoxText = new HBox();
		hBoxText.setAlignment(Pos.CENTER_LEFT);
		hBoxText.getChildren().add(new Text());
		hBoxButtons = new HBox();
		hBoxButtons.setAlignment(Pos.CENTER);
		setButtonSizeAndSpacing(32, 5);
		hBoxMain = new HBox();
		hBoxMain.setSpacing(5);
		hBoxMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
	  		if (event.getButton() == MouseButton.PRIMARY && editableHBox != null &&
	  				(System.currentTimeMillis() - editableHBox.getStartedCTime()) > 50)
	  					editableHBox.resetBackToText(previewText);
	    }
		});
		setMode(mode);
		editMode = HBoxWitnButtonsEditMode.DISABLED;
		if (editMode != HBoxWitnButtonsEditMode.DISABLED)
			editableHBox = new EditableHBox();
	}
	
	public void setButtonSize(double size)
		{ buttonSize = size; }

	public void setButtonSpacing(int spacing) {
		buttonSpacing = spacing;
		hBoxButtons.setSpacing(buttonSpacing);
	}

	public void setButtonSizeAndSpacing(double size, int spacing) {
		setButtonSize(size);
		setButtonSpacing(spacing);
	}
	
	private void setBorderPane(Node... nodes) {
		if (nodes.length < 5)
			throw new RuntimeException("You must specify 5 nodes (CENTER, TOP, RIGHT, BOTTOM, LEFT)");
		List<Pos> aligs = Arrays.asList(Pos.CENTER, Pos.TOP_CENTER, Pos.CENTER_RIGHT, Pos.BOTTOM_CENTER, Pos.CENTER_LEFT);
		for (int n = 0; n < 5; n++)
			if (nodes[n] != null)
				BorderPane.setAlignment(nodes[n], aligs.get(n));
		borderPane = new BorderPane(nodes[0], nodes[1], nodes[2], nodes[3], nodes[4]);
		HBox.setHgrow(borderPane, Priority.ALWAYS);
		if (hBoxMain.getChildren().isEmpty())
			hBoxMain.getChildren().add(borderPane);
		else
			hBoxMain.getChildren().set(0, borderPane);
	}
	
	public HBoxWitnButtonsMode getMode()
		{ return mode; }
	
	public void setMode(HBoxWitnButtonsMode mode) {
		if ((this.mode = mode) == HBoxWitnButtonsMode.ONLY_BUTTONS)
			setBorderPane(hBoxButtons, null, null, null, null);
		else if ((this.mode = mode) == HBoxWitnButtonsMode.ONLY_TEXT)
			setBorderPane(hBoxText, null, null, null, null);
		else
			setBorderPane(null, null, hBoxButtons, null, hBoxText);
	}
	
	public void setEditMode(HBoxWitnButtonsEditMode editMode) {
		this.editMode = editMode;
		if (editMode != HBoxWitnButtonsEditMode.DISABLED)
			editableHBox = new EditableHBox();
		else if (editableHBox != null) {
			editableHBox.resetBackToText(previewText);
			editableHBox = null;
		}
	}
	
	public void setEditableTextField(Double width, String defaultText, Consumer<String> consumerAfterPressingEnter)
		{ editableHBox.setEditableHBoxThroughTextField(hBoxText, width, defaultText, consumerAfterPressingEnter); }

	public void setEditableTextField(Double width, Consumer<String> consumerAfterPressingEnter)
		{ editableHBox.setEditableHBoxThroughTextField(hBoxText, width, consumerAfterPressingEnter); }

	public void setEditableTextField(String defaultText, Consumer<String> consumerAfterPressingEnter)
		{ editableHBox.setEditableHBoxThroughTextField(hBoxText, defaultText, consumerAfterPressingEnter); }

	public void setEditableTextField(Consumer<String> consumerAfterPressingEnter)
		{ editableHBox.setEditableHBoxThroughTextField(hBoxText, consumerAfterPressingEnter); }

	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, Integer selectedIndex, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, selectedIndex, selectedItem, comboBoxMask, comboBoxChangeListener); }

	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, Integer selectedIndex, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, selectedIndex, comboBoxMask, comboBoxChangeListener); }

	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, selectedItem, comboBoxMask, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, Integer selectedIndex, T selectedItem, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, selectedIndex, selectedItem, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, Integer selectedIndex, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, selectedIndex, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, T selectedItem, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, selectedItem, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, comboBoxMask, comboBoxChangeListener); }

	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Double width, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, width, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Integer selectedIndex, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, selectedIndex, selectedItem, comboBoxMask, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Integer selectedIndex, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, selectedIndex, comboBoxMask, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, selectedItem, comboBoxMask, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Integer selectedIndex, T selectedItem, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, selectedIndex, selectedItem, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Integer selectedIndex, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, selectedIndex, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, T selectedItem, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, selectedItem, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, Function<T, S> comboBoxMask, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, comboBoxMask, comboBoxChangeListener); }
	
	public <T, S> void setEditableComboBox(List<T> comboBoxItems, ChangeListener<T> comboBoxChangeListener)
		{ editableHBox.setEditableHBoxThroughComboBox(hBoxText, comboBoxItems, comboBoxChangeListener); }
	
	public void addIcon()
		{ setIcon(Icons.ICON_BLANK); }

	public void setIcon(Icons icon) {
		Boolean add = this.icon == null;
		this.icon = icon.getImageView(buttonSize, buttonSize);
		this.icon.setFitWidth(buttonSize);
		this.icon.setFitHeight(buttonSize);
		if (add)
			hBoxMain.getChildren().add(0, this.icon);
		else
			hBoxMain.getChildren().set(0, this.icon);
	}
	
	public void removeIcon() {
		if (icon != null) {
			hBoxMain.getChildren().remove(0);
			icon = null;
		}
	}
	
	public ImageView getIconImageView()
		{ return icon; }
	
	public String getText() {
		if (mode == HBoxWitnButtonsMode.ONLY_BUTTONS || 
				editMode == HBoxWitnButtonsEditMode.COMBO_BOX ||
				(!(hBoxText.getChildren().get(0) instanceof Text) &&
				 !(hBoxText.getChildren().get(0) instanceof TextField)))
					return "";
		if (hBoxText.getChildren().get(0) instanceof TextField)
			return ((TextField) hBoxText.getChildren().get(0)).getText();
		return ((Text) hBoxText.getChildren().get(0)).getText();
	}

	public void setText(String text) {
		if (text == null)
			return;
		if (previewText != null && !text.equals(previewText) && editableHBox != null)
			editableHBox.resetBackToText(previewText);
		previewText = text;
		if (hBoxText.getChildren().get(0) instanceof Text)
			((Text) hBoxText.getChildren().get(0)).setText(text);
		else
			((TextField) hBoxText.getChildren().get(0)).setText(text);
	}
	
	public void addButton(Icons icon, EventHandler<ActionEvent> onActionEvent, Boolean visible, Boolean disabled) {
		Button bt = new Button();
		bt.setMinSize(buttonSize, buttonSize);
		bt.setMaxSize(buttonSize, buttonSize);
		bt.setPrefSize(buttonSize, buttonSize);
		bt.setOnAction(onActionEvent);
		bt.setGraphic(icon.getImageView(buttonSize * 0.7, buttonSize * 0.7));
		bt.setDisable(disabled);
		bt.setVisible(visible);
		bt.hoverProperty().addListener((obs, wasHover, isHover) -> {
			if (isHover)
				bt.requestFocus();
		});
		hBoxButtons.getChildren().add(bt);
	}
	
	public void addButton(Icons icon, EventHandler<ActionEvent> onActionEvent)
		{ addButton(icon, onActionEvent, true, false); }
	
	public void addButton(Icons icon)
		{ addButton(icon, null); }

	public void addButton()
		{ addButton(null); }
	
	public void removeButton(int index) {
		if (hBoxButtons.getChildren().isEmpty())
			throw new RuntimeException("Não há botões adicionados para serem removidos");
		if (index >= hBoxButtons.getChildren().size())
			throw new RuntimeException(index + " - Indice de botão inválido (Max: " + (hBoxButtons.getChildren().size() - 1));
		hBoxButtons.getChildren().remove(index);
	}
	
	public Button getButton(int index)
		{ return (Button) hBoxButtons.getChildren().get(index); }
	
	public void setButtonOnAction(int index, EventHandler<ActionEvent> onActionEvent)
		{ getButton(index).setOnAction(onActionEvent); }

	public void setButtonIcon(int index, Icons icon)
		{ getButton(index).setGraphic(icon.getImageView(buttonSize * 0.7, buttonSize * 0.7)); }

	public HBox getHBox()
		{ return hBoxMain;}
	
	public HBox getHBoxButtons()
		{ return hBoxButtons; }

}