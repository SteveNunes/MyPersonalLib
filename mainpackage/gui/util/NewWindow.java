package gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewWindow {
	
	private FXMLLoader loader;
	private Stage stage;
	
	public NewWindow() {}
	
	public <T> NewWindow(Class<? extends T> cLass, Node anyNodeFromParentWindow, String fxml) {
		try {
			loader = new FXMLLoader(cLass.getResource(fxml));
			Pane pane = loader.load();
			stage = new Stage();
			stage.setScene(new Scene(pane));
			stage.initOwner(anyNodeFromParentWindow.getScene().getWindow());
			stage.initModality(Modality.WINDOW_MODAL);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Não foi possível carregar a janela \"" + fxml + "\"");
		}
	}
	
	public Stage getStage()
		{ return stage; }
	
	public FXMLLoader getLoader()
		{ return loader; }
	
	public <T> T getController()
		{ return loader.getController(); }
	
}

