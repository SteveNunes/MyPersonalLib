package gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class NewWindow {
	
	private FXMLLoader loader;
	private Stage stage;
	
	@SuppressWarnings("unused")
	private NewWindow() {}
	
	public NewWindow(String fxml) {
		try {
			loader = new FXMLLoader(NewWindow.class.getResource(fxml));
			Pane pane = loader.load();
			stage = new Stage();
			stage.setScene(new Scene(pane));
			stage.show();
		}
		catch (Exception e)
			{ throw new RuntimeException("Unable to create window"); }
	}

	public Stage getStage()
		{ return stage; }
	
	public FXMLLoader getLoader()
		{ return loader; }
	
	public <T> T getController()
		{ return loader.getController(); }
	
}

