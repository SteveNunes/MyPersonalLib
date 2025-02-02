package gui.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.NoSuchElementException;

// https://code.makery.ch/blog/javafx-dialogs-official/

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

public abstract class Alerts {
	
	/**
	 * Função para criar janelas de alerta, que retornam um {@code Optional} com informações sobre o botão que o usuário pressionou nessa janela. O fluxo do programa é interrompido enquanto a janela estiver aberta.
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela (opcional, informe {@code null} para janela sem cabeçalho.
	 * @param content				Texto da janela.
	 * @param type					Tipo de alerta (Consultar o {@code enum AlertType} para mais informações.
	 * @return							um tipo {@code Optional} contendo informações sobre o botão clicado (Ok, Cancel, etc) 
	 */
	private static Optional<ButtonType> createAlert(String title, String header, String content, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		if (header != null)
			alert.setHeaderText(header);
		alert.setContentText(content);
		return alert.showAndWait();
	}

	/**
	 * Sobrecarga do método {@code showAndWait(String title, String header, String content, AlertType type)}
	 * que dispensa o parâmetro {@code header}
	 * @param title					Título da janela.
	 * @param content				Texto da janela.
	 * @param type					Tipo de alerta (Consultar o {@code enum AlertType} para mais informações.
	 */
	private static Optional<ButtonType> createAlert(String title, String content, AlertType type)
		{ return createAlert(title, null, content, type); }

	/**
	 * Cria uma janela de confirmação, que retorna {@code true} se o usuário clicar em 'Ok'
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela (opcional, informe {@code null} para janela sem cabeçalho.
	 * @param content				Texto da janela.
	 */
	public static Boolean confirmation(String title, String header, String content)
		{ return createAlert(title, header, content, AlertType.CONFIRMATION).get() == ButtonType.OK; }

	/**
	 * Sobrecarga do método {@code confirmation(String title, String header, String content)}
	 * que dispensa o parâmetro {@code header}
	 * @param title					Título da janela.
	 * @param content				Texto da janela.
	 */
	public static Boolean confirmation(String title, String content)
		{ return createAlert(title, content, AlertType.CONFIRMATION).get() == ButtonType.OK; }

	/**
	 * Cria uma janela de erro
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela (opcional, informe {@code null} para janela sem cabeçalho.
	 * @param content				Texto da janela.
	 */
	public static void error(String title, String header, String content)
		{ createAlert(title, header, content, AlertType.ERROR); }
	
	/**
	 * Sobrecarga do método {@code error(String title, String header, String content)}
	 * que dispensa o parâmetro {@code header}
	 * @param title					Título da janela.
	 * @param content				Texto da janela.
	 */
	public static void error(String title, String content)
		{ createAlert(title, content, AlertType.ERROR); }
	
	public static void exception(String title, String header, String content, Exception ex) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		if (header != null)
			alert.setHeaderText(header);
		alert.setContentText(content);
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		Label label = new Label("Exception stacktrace:");
		TextArea textArea = new TextArea(sw.toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}
	
	public static void exception(String title, String content, Exception ex)
		{ exception(title, null, content, ex); }
	
	/**
	 * Cria uma janela de informação
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela (opcional, informe {@code null} para janela sem cabeçalho.
	 * @param content				Texto da janela.
	 */
	public static void information(String title, String header, String content)
		{ createAlert(title, header, content, AlertType.INFORMATION); }
	
	/**
	 * Sobrecarga do método {@code information(String title, String header, String content)}
	 * que dispensa o parâmetro {@code header}
	 * @param title					Título da janela.
	 * @param content				Texto da janela.
	 */
	public static void information(String title, String content)
		{ createAlert(title, content, AlertType.INFORMATION); }

	/**
	 * Cria uma janela de alerta
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela (opcional, informe {@code null} para janela sem cabeçalho.
	 * @param content				Texto da janela.
	 */
	public static void warning(String title, String header, String content)
		{ createAlert(title, header, content, AlertType.WARNING); }
	
	/**
	 * Sobrecarga do método {@code warning(String title, String header, String content)}
	 * que dispensa o parâmetro {@code header}
	 * @param title					Título da janela.
	 * @param content				Texto da janela.
	 */
	public static void warning(String title, String content)
		{ createAlert(title, content, AlertType.WARNING); }

	/**
	 * Cria uma janela de mensagem simples (sem botões)
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela (opcional, informe {@code null} para janela sem cabeçalho.
	 * @param content				Texto da janela.
	 */
	public static void msg(String title, String header, String content)
		{ createAlert(title, header, content, AlertType.NONE); }
	
	/**
	 * Sobrecarga do método {@code msg(String title, String header, String content)}
	 * que dispensa o parâmetro {@code header}
	 * @param title					Título da janela.
	 * @param content				Texto da janela.
	 */
	public static void msg(String title, String content)
		{ createAlert(title, content, AlertType.NONE); }

	/**
	 * Abre uma caixa para que  usuário entre com um valor
	 * @param title					Título da janela.
	 * @param header				Cabeçalho da janela
	 * @param defaultText		Texto inicial na caixa de texto
	 * @param content				Texto a esquerda do campo onde o usuário entrará com o texto
	 * @return							O texto informado pelo usuário
	 */
	public static String textPrompt(String title, String header, String defaultText, String content) {
		TextInputDialog dialog = new TextInputDialog(defaultText != null ? defaultText : "");
		dialog.setTitle(title);
		if (header != null)
			dialog.setHeaderText(header);
		dialog.setContentText(content);
		Optional<String> result = dialog.showAndWait();
		return result.isPresent() ? result.get() : null;
	}

	public static String textPrompt(String title, String defaultText, String content)
		{	return textPrompt(title, null, defaultText, content); }
	
	public static String textPrompt(String title, String content)
		{	return textPrompt(title, null, null, content); }

	public static int customConfirmation(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		if (header != null)
			alert.setHeaderText(header);
		alert.setContentText(content);
		ButtonType buttonTypeOne = new ButtonType("One");
		ButtonType buttonTypeTwo = new ButtonType("Two");
		ButtonType buttonTypeThree = new ButtonType("Three");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeCancel);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == buttonTypeOne){
		    // ... user chose "One"
		} 
		else if (result.get() == buttonTypeTwo) {
		    // ... user chose "Two"
		} 
		else if (result.get() == buttonTypeThree) {
		    // ... user chose "Three"
		} 
		else {
		    // ... user chose CANCEL or closed the dialog
		}
		return 0;
	}
	
	public static String choiceCombo(String title, String header, String content, List<String> choices) {
		return choiceCombo(title, header, content, choices, null);
	}
	
	public static String choiceCombo(String title, String header, String content, List<String> choices, String startSelectedValue) {
		ChoiceDialog<String> dialog = new ChoiceDialog<>(startSelectedValue != null ? startSelectedValue : choices.get(0), choices);
		dialog.setTitle(title);
		if (header != null)
			dialog.setHeaderText(header);
		dialog.setContentText(content);
		Optional<String> result = dialog.showAndWait();
		try
			{ return result.get(); }
		catch (NoSuchElementException e)
			{ return null; }
	}
	
	public static String choiceCombo(String title, String content, List<String> choices, String startSelectedValue)
		{ return choiceCombo(title, null, content, choices, startSelectedValue); }
	
	public static String choiceCombo(String title, String content, List<String> choices)
		{ return choiceCombo(title, null, content, choices); }

	public void loginWindow(String title, String header) {
	// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		if (header != null)
			dialog.setHeaderText(header);

		// Set the icon (must be included in the project).
		dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(username.getText(), password.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
		    System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		});
	}
	
	/*	Outras dicas:

			Como alterar o icone da janela de erro:
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(this.getClass().getResource("arquivo.png").toString()));
			
			Como criar uma janela sem barras de contorno e sem titulo
			dialog.initStyle(StageStyle.UTILITY);
			
			You can specify the modality for a dialog.
			The modality must be one of Modality.NONE, Modality.WINDOW_MODAL, or Modality.APPLICATION_MODAL.
			dialog.initModality(Modality.NONE);
	 */
	
}