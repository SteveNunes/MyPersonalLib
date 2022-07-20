package gui.util;

import enums.Icons;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HBoxWithButtons {
	
	private HBox hBoxMain, hBoxText, hBoxButtons;
	private double buttonSize;
	
	public HBoxWithButtons(Pos alignment, double textHBoxWidth, double spacing, double buttonSize) {
		hBoxText = new HBox();
		hBoxText.setAlignment(alignment);
		hBoxText.setMinWidth(textHBoxWidth);
		hBoxText.setMaxWidth(textHBoxWidth);
		hBoxText.setPrefWidth(textHBoxWidth);
		hBoxText.getChildren().add(new Text());
		hBoxButtons = new HBox();
		hBoxButtons.setSpacing(spacing);
		hBoxButtons.setPrefWidth(HBox.USE_COMPUTED_SIZE);
		hBoxMain = new HBox();
		hBoxMain.setAlignment(alignment);
		hBoxMain.getChildren().add(hBoxText);
		hBoxMain.getChildren().add(hBoxButtons);
		hBoxMain.setMinHeight(buttonSize);
		hBoxMain.setMaxHeight(buttonSize);
		hBoxMain.setPrefHeight(buttonSize);
		hBoxMain.setPrefWidth(HBox.USE_COMPUTED_SIZE);
		this.buttonSize = buttonSize;
	}
	
	public Button getButton(int index)
		{ return (Button) hBoxButtons.getChildren().get(index); }
	
	public Text getTextNode()
		{ return (Text) hBoxText.getChildren().get(0); }

	public String getText()
		{ return getTextNode().getText(); }

	public void setText(String text)
		{ getTextNode().setText(text); }
	
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
		getHBoxButtons().getChildren().add(bt);
	}
	
	public void addButton(Icons icon, EventHandler<ActionEvent> onActionEvent)
		{ addButton(icon, onActionEvent, true, false); }
	
	public void addButton(Icons icon)
		{ addButton(icon, null); }

	public void addButton()
		{ addButton(null); }
	
	public void setButtonIcon(int buttonIndex, Icons icon)
		{ getButton(buttonIndex).setGraphic(icon.getImageView(buttonSize * 0.7, buttonSize * 0.7)); }

	public void setButtonOnAction(int buttonIndex, EventHandler<ActionEvent> onActionEvent)
		{ getButton(buttonIndex).setOnAction(onActionEvent); }

	public void setHideButton(int buttonIndex, Boolean visible)
		{ getButton(buttonIndex).setVisible(visible); }

	public void setDisableButton(int buttonIndex, Boolean disabled)
		{ getButton(buttonIndex).setDisable(disabled); }
	
	public HBox getHBox()
		{ return hBoxMain;}
	
	public HBox getHBoxButtons()
		{ return hBoxButtons; }

}
