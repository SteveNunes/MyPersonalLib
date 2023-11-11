package gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class NewWindow {
	
	private FXMLLoader loader;
	private Stage stage;
	
	@SuppressWarnings("unused")
	private NewWindow() {}
	
	public NewWindow(Window parentWindow, Modality modality, StageStyle style, String fxml) {
		try {
			loader = new FXMLLoader(NewWindow.class.getResource(fxml));
			Pane pane = loader.load();
			stage = new Stage();
			stage.setScene(new Scene(pane));
			if (parentWindow != null)
				stage.initOwner(parentWindow);
			if (modality != null)
				stage.initModality(modality);
			if (style != null)
				stage.initStyle(style);
			stage.show();
		}
		catch (Exception e)
			{ throw new RuntimeException("Unable to create window"); }
	}

	public NewWindow(Window parentWindow, Modality modality, String fxml)
		{ this(parentWindow, modality, null, fxml); }

	public NewWindow(Window parentWindow, StageStyle style, String fxml)
		{ this(parentWindow, null, style, fxml); }
	
	public NewWindow(Window parentWindow, String fxml)
		{ this(parentWindow, null, null, fxml); }
	
	public NewWindow(Modality modality, String fxml)
		{ this(null, modality, null, fxml); }
	
	public NewWindow(StageStyle style, String fxml)
		{ this(null, null, style, fxml); }
	
	public NewWindow(String fxml)
		{ this(null, null, null, fxml); }

	
	public Stage getStage()
		{ return stage; }
	
	public FXMLLoader getLoader()
		{ return loader; }
	
	public <T> T getController()
		{ return loader.getController(); }
	
}

