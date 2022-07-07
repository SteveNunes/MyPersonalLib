package gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class NewWindow {
	
	private FXMLLoader loader;
	private Stage stage = new Stage();
	
	public NewWindow() {}
	
	public <T> NewWindow(Class<? extends T> cLass, Window parentWindow, String fxml) {
		try {
			loader = new FXMLLoader(cLass.getResource(fxml));
			Pane pane = loader.load();
			stage.setScene(new Scene(pane));
			stage.initOwner(parentWindow);
			stage.initModality(Modality.WINDOW_MODAL);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Não foi possível carregar a janela \"" + fxml + "\"");
		}
	}
	
	public Stage getStage() { return stage; }
	public FXMLLoader getLoader() { return loader; }
	
}

