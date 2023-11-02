package gui.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Trata-se de um HBox que é recebido por parâmetro, e deve
 * conter um Node do tipo Text(). Ao dar um duplo-clique
 * nesse Node, ele será alterado ou para um TextField ou
 * para um ComboBox, permitindo a edição do item.
 */

public class EditableHBox {
	
	private HBox editHBox;
	private TextField textFieldEditHBox;
	private ComboBox<?> comboBoxEditHBox;
	private String previewHBoxText;
	private long startedCTime;
	
	public long getStartedCTime()
		{ return startedCTime; }
	
	public EditableHBox() {}
	
	public EditableHBox(HBox hBox, Double width, String defaultText, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, width, defaultText, consumerAfterPressingEnter); }
	
	public EditableHBox(HBox hBox, Double width, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, width, consumerAfterPressingEnter); }

	public EditableHBox(HBox hBox, String defaultText, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, defaultText, consumerAfterPressingEnter); }

	public EditableHBox(HBox hBox, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, consumerAfterPressingEnter); }

	public void setEditableHBoxThroughTextField(HBox hBox, Double width, String defaultText, Consumer<String> consumerAfterPressingEnter) {
		hBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
    		if (event.getButton() == MouseButton.PRIMARY) {
    			if (editHBox != null)
    				resetBackToText();
		    	if (event.getClickCount() == 2) {
		    		startedCTime = System.currentTimeMillis();
		    		previewHBoxText = defaultText != null ? defaultText : ((Text) hBox.getChildren().get(0)).getText();
		    		textFieldEditHBox = new TextField(previewHBoxText);
    				if (hBox.getChildren().isEmpty())
  		    		hBox.getChildren().add(textFieldEditHBox);
    				else
    					hBox.getChildren().set(0, textFieldEditHBox);
		    		editHBox = hBox;
		    		textFieldEditHBox.requestFocus();
		    		textFieldEditHBox.selectAll();
		    		if (width != null)
		    			textFieldEditHBox.setPrefWidth(width);
		    		textFieldEditHBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    	    @Override
		    	    public void handle(KeyEvent key) {
	    	        if (key.getCode().equals(KeyCode.ENTER) || key.getCode().equals(KeyCode.ESCAPE)) {
	    	        	if (key.getCode().equals(KeyCode.ENTER)) {
	    	        		hBox.getChildren().set(0, new Text(textFieldEditHBox.getText()));
	    	        		consumerAfterPressingEnter.accept(textFieldEditHBox.getText());
	    	        	}
	    	        	else
	    	        		hBox.getChildren().set(0, new Text(previewHBoxText));
	    	        	textFieldEditHBox = null;
	    	        	
	    	        }
		    	    }
		    		});
		    	}
    		}
	    }
		});
	}

	public void setEditableHBoxThroughTextField(HBox hBox, Double width, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, width, null, consumerAfterPressingEnter); }
	
	public void setEditableHBoxThroughTextField(HBox hBox, String defaultText, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, null, defaultText, consumerAfterPressingEnter); }

	public void setEditableHBoxThroughTextField(HBox hBox, Consumer<String> consumerAfterPressingEnter)
		{ setEditableHBoxThroughTextField(hBox, null, null, consumerAfterPressingEnter); }
	
	public void resetBackToText() {
		if (editHBox != null) {
			editHBox.getChildren().set(0, new Text(previewHBoxText));
			comboBoxEditHBox = null;
			editHBox = null;
			previewHBoxText = null;
			textFieldEditHBox = null;
			comboBoxEditHBox = null;
		}
	}
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, Integer selectedIndex, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener) {
		hBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
    		if (event.getButton() == MouseButton.PRIMARY) {
    			if (editHBox != null)
    				resetBackToText();
		    	if (event.getClickCount() == 2) {
		    		startedCTime = System.currentTimeMillis();
		    		ComboBox<T> comboBox = new ComboBox<T>();
		    		ControllerUtils.setListToComboBox(comboBox, comboBoxItems);
		    		if (comboBoxMask != null)
		    			ControllerUtils.changeHowComboBoxDisplayItens(comboBox, comboBoxMask);
		    		if (selectedIndex != null)
		    			comboBox.getSelectionModel().select(selectedIndex);
		    		else
		    			comboBox.getSelectionModel().select(selectedItem);
		    		comboBox.valueProperty().addListener(valuePropertyListener);
		    		comboBoxEditHBox = comboBox;
		    		editHBox = hBox;
		    		if (width > -1)
		    			comboBoxEditHBox.setPrefWidth(width);
		    		previewHBoxText = ((Text) hBox.getChildren().get(0)).getText();
    				if (hBox.getChildren().isEmpty())
  		    		hBox.getChildren().add(comboBoxEditHBox);
    				else
    					hBox.getChildren().set(0, comboBoxEditHBox);
    				comboBox.show();
    				comboBoxEditHBox = null;
		    	}
    		}
	    }
		});
	}

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, Integer selectedIndex, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, selectedIndex, null, comboBoxMask, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, null, selectedItem, comboBoxMask, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, Integer selectedIndex, T selectedItem, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, selectedIndex, selectedItem, null, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, Integer selectedIndex, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, selectedIndex, null, null, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, T selectedItem, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, null, selectedItem, null, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, null, null, comboBoxMask, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Double width, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, width, null, null, null, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Integer selectedIndex, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, selectedIndex, selectedItem, comboBoxMask, valuePropertyListener); }

	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Integer selectedIndex, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, selectedIndex, null, comboBoxMask, valuePropertyListener); }
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, T selectedItem, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, null, selectedItem, comboBoxMask, valuePropertyListener); }
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Integer selectedIndex, T selectedItem, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, selectedIndex, selectedItem, null, valuePropertyListener); }
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Integer selectedIndex, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, selectedIndex, null, null, valuePropertyListener); }
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, T selectedItem, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, null, selectedItem, null, valuePropertyListener); }
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, Function<T, S> comboBoxMask, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, null, null, comboBoxMask, valuePropertyListener); }
	
	public <T, S> void setEditableHBoxThroughComboBox(HBox hBox, List<T> comboBoxItems, ChangeListener<T> valuePropertyListener)
		{ setEditableHBoxThroughComboBox(hBox, comboBoxItems, null, null, null, null, valuePropertyListener); }

}
