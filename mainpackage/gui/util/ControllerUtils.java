package gui.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import util.CollectionUtils;

public abstract class ControllerUtils {
	
	private static final String COLOR_FOR_SELECTED_FOCUSED_HOVERED_EVEN = "#8FAFFF";
	private static final String COLOR_FOR_SELECTED_FOCUSED_HOVERED_ODD = "#7F9FEF";
	private static final String COLOR_FOR_SELECTED_FOCUSED_EVEN = "#6F8FDF";
	private static final String COLOR_FOR_SELECTED_FOCUSED_ODD = "#5F7FCF";
	private static final String COLOR_FOR_SELECTED_HOVERED_EVEN = "#DDDDDD";
	private static final String COLOR_FOR_SELECTED_HOVERED_ODD = "#CCCCCC";
	private static final String COLOR_FOR_SELECTED_EVEN = "#BBBBBB";
	private static final String COLOR_FOR_SELECTED_ODD = "#AAAAAA";
	private static final String COLOR_FOR_HOVERED_EVEN = "#FFFEAA";
	private static final String COLOR_FOR_HOVERED_ODD = "#EEFEAA";
	private static final String COLOR_FOR_REGULAR_EVEN = "#FFFFFF";
	private static final String COLOR_FOR_REGULAR_ODD = "#EEEEEE";
	
	/**
	 * Facilita a edição das celulas de um ComboBox
	 * @param propertyGetter - Passe a Classe::metodoQueRecebeOValorTipo<T>
	 * @param cell - Retorna um consumer contendo a ListCell como parâmetro1, e um boolean que retorna false caso a linha esteja vazia ou o objeto contido nela seja null
	 */
	public static <T> void setComboBoxCell(ComboBox<T> comboBox, BiConsumer<ListCell<T>, Boolean> cell) {
		Callback<ListView<T>, ListCell<T>> factory = lv -> new ListCell<T>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				cell.accept(this, item == null || empty);
			}
		};
		comboBox.setCellFactory(factory);
		comboBox.setButtonCell(factory.call(null));
	}
	
	/**
	 * Facilita a edição das celulas de um ListView
	 * @param propertyGetter - Passe a Classe::metodoQueRecebeOValorTipo<T>
	 * @param cell - Retorna um consumer contendo a ListCell como parâmetro1, e um boolean que retorna false caso a linha esteja vazia ou o objeto contido nela seja null
	 */
	public static <T> void setListViewCell(ListView<T> listView, BiConsumer<ListCell<T>, Boolean> cell) {
		Callback<ListView<T>, ListCell<T>> factory = lv -> new ListCell<T>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				cell.accept(this, item == null || empty);
			}
		};
		listView.setCellFactory(factory);
	}

	/**
	 * Facilita a edição das celulas de um TableColumn
	 * @param propertyGetter - Passe a Classe::metodoQueRecebeOValorTipo<B>
	 * @param cell - Retorna um consumer contendo a TableCell como parâmetro1, e um boolean que retorna false caso a linha esteja vazia ou o objeto contido nela seja null
	 */
	public static <A, B> void setTableColumnCell(TableColumn<A, B> tableColumn, Function<A, B> propertyGetter, BiConsumer<TableCell<A, B>, Boolean> cell) {
		tableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(propertyGetter.apply(cellData.getValue())));
		tableColumn.setCellFactory(column -> {
			return new TableCell<A, B>() {
				@Override
				protected void updateItem(B item, boolean empty) {
					super.updateItem(item, empty);
					cell.accept(this, item == null || empty);
				}
			};
		});
	}
	
	/**
	 * Altera a forma como os itens de uma Combo Box irão ser exibidos.
	 * @param <T>				Tipo dos itens da Combo Box
	 * @param <S>				Tipo de retorno de cada item da Combo Box
	 * @param comboBox	Combo Box de destino
	 * @param showMask	{@code Function<T, S>} que retorna como o valor de cada item será exibido
	 * 
	 * @Exemplo Com uma lista de {@code Pessoa}, para fazer com que nessa ComboBox seja exibido
	 * o valor do método {@code .getName()}, chame o método da seguinte forma:
	 * {@code changeHowComboBoxDisplayItems(comboBox, p -> p.getName());}
	 */
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Pos alignment, Color textColor, Font textFont, Function<T, S> showMask) {
		setComboBoxCell(comboBox, (cell, empty) -> {
			cell.setAlignment(alignment != null ? alignment : Pos.TOP_LEFT);
			cell.setTextFill(textColor != null ? textColor : Color.BLACK);
			if (textFont != null)
				cell.setFont(textFont);
			cell.setText(empty ? "" : showMask.apply(cell.getItem()).toString());
		});
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Pos alignment, Color textColor, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, alignment, textColor, null, showMask);
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Pos alignment, Font textFont, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, alignment, null, textFont, showMask);
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Pos alignment, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, alignment, null, null, showMask);
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Color textColor, Font textFont, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, null, textColor, textFont, showMask);
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Color textColor, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, null, textColor, null, showMask);
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Font textFont, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, null, null, textFont, showMask);
	}
	
	public static <T, S> void changeHowComboBoxDisplayItems(ComboBox<T> comboBox, Function<T, S> showMask) {
		changeHowComboBoxDisplayItems(comboBox, null, null, null, showMask);
	}
	
	/**
	 * Altera a forma como os itens de uma List View irão ser exibidos.
	 * @param <T>				Tipo dos itens da List View
	 * @param <S>				Tipo de retorno de cada item da List View
	 * @param listView	List View de destino
	 * @param showMask	{@code Function<T, S>} que retorna como o valor de cada item será exibido
	 * 
	 * @Exemplo Com uma lista de {@code Pessoa}, para fazer com que nessa ListView seja exibido
	 * o valor do método {@code .getName()}, chame o método da seguinte forma:
	 * {@code changeHowListViewDisplayItems(listView, p -> p.getName());}
	 */
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Pos alignment, Color textColor, Font textFont, Function<T, S> showMask) {
		setListViewCell(listView, (cell, empty) -> {
			cell.setAlignment(alignment != null ? alignment : Pos.TOP_LEFT);
			cell.setTextFill(textColor != null ? textColor : Color.BLACK);
			if (textFont != null)
				cell.setFont(textFont);
			cell.setText(empty ? "" : showMask.apply(cell.getItem()).toString());
		});
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Pos alignment, Color textColor, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, alignment, textColor, null, showMask);
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Pos alignment, Font textFont, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, alignment, null, textFont, showMask);
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Pos alignment, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, alignment, null, null, showMask);
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Color textColor, Font textFont, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, null, textColor, textFont, showMask);
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Color textColor, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, null, textColor, null, showMask);
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Font textFont, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, null, null, textFont, showMask);
	}
	
	public static <T, S> void changeHowListViewDisplayItems(ListView<T> listView, Function<T, S> showMask) {
		changeHowListViewDisplayItems(listView, null, null, null, showMask);
	}
	
	/**
	 * Altera a forma como os itens de uma TableColumn irão ser exibidos.
	 * @param <T>						Tipo dos itens da TableColumn
	 * @param <S>						Tipo de retorno de cada item da TableColumn
	 * @param tableColumn		TableColumn de destino
	 * @param showMask	{@code Function<T, S>} que retorna como o valor de cada item será exibido
	 * 
	 * @Exemplo Com uma lista de {@code Pessoa}, para fazer com que nessa ListView seja exibido
	 * o valor do método {@code .getName()}, chame o método da seguinte forma:
	 * {@code changeHowTableColumnDisplayItems(tableColumn, p -> p.getName());}
	 */
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Pos alignment, Color textColor, Font textFont, Function<T, S> showMask) {
		setTableColumnCell(tableColumn, showMask, (cell, empty) -> {
			cell.setAlignment(alignment != null ? alignment : Pos.TOP_LEFT);
			cell.setTextFill(textColor != null ? textColor : Color.BLACK);
			if (textFont != null)
				cell.setFont(textFont);
			cell.setText(empty ? "" : showMask.apply(cell.getTableRow().getItem()).toString());
		});
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Pos alignment, Color textColor, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, alignment, textColor, null, showMask);
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Pos alignment, Font textFont, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, alignment, null, textFont, showMask);
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Pos alignment, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, alignment, null, null, showMask);
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Color textColor, Font textFont, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, null, textColor, textFont, showMask);
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Color textColor, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, null, textColor, null, showMask);
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Font textFont, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, null, null, textFont, showMask);
	}
	
	public static <T, S> void changeHowTableColumnDisplayItems(TableColumn<T, S> tableColumn, Function<T, S> showMask) {
		changeHowTableColumnDisplayItems(tableColumn, null, null, null, showMask);
	}
	
	/**
	 * Adiciona CheckBoxes a um ComboBox
	 * @param comboBox - ComboBox que receberá a lista de CheckBox (No total de itens da lista checkBoxesTexts)
	 * @param checkBoxesTexts - Lista de textos que serão atribuidos aos CheckBoxes
	 * @param onCheckBoxSelect - Evento disparado ao marcar/desmarcar um CheckBox (Retorna o CheckBox marcado)
	 */
	public static void addCheckBoxesOnComboBox(ComboBox<String> comboBox, List<String> checkBoxesTexts, Consumer<CheckBox> onCheckBoxSelect) {
		List<String> selecteds = new ArrayList<>();
		comboBox.setCellFactory(lv -> new ListCell<>() {
			{
				lv.setOnMouseExited(e -> comboBox.hide());
			}
			
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null)
					setGraphic(null);
				else {
					CheckBox checkBox = new CheckBox(item);
					checkBox.setPrefWidth(comboBox.getPrefWidth());
					checkBox.setOnMouseReleased(e -> {
						boolean b = !checkBox.isSelected();
						checkBox.setSelected(b);
						if (b)
							selecteds.add(item);
						else
							selecteds.remove(item);
						comboBox.setPromptText(String.join(", ", selecteds));
						comboBox.show();
						if (onCheckBoxSelect != null)
							onCheckBoxSelect.accept(checkBox);
					});
					setGraphic(checkBox);
				}
			}
		});
		comboBox.getItems().addAll(checkBoxesTexts);
  }

	/** Adiciona a propriedade de listar valores para auto-completar o TextField, baseado no conteudo digitado, comparado com o Set informado.
	 *  OBS: Para funcionar corretamente, o TextField deve ser isolado dentro de um VBox.
	 */
	public static void setAutoCompletionOnTextField(TextField textField, int maxResults, Set<String> completionList) {
		VBox parentVBox = (VBox)textField.getParent();
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.setVisible(false);
		comboBox.setMinHeight(0);
		comboBox.setPrefHeight(0);
		comboBox.setMaxHeight(0);
		
		comboBox.setOnAction(e -> {
			String s = comboBox.getSelectionModel().getSelectedItem();
			if (s != null)
				Platform.runLater(() -> {
					textField.setText(s);
					parentVBox.getChildren().remove(comboBox);
				});
		});
		
		textField.textProperty().addListener((o, oldText, newText) -> {
			if (newText.isEmpty()) 
				parentVBox.getChildren().remove(comboBox);
			else {
				Set<String> filteredItems = completionList.stream()
						.filter(item -> item.toLowerCase().contains(newText.toLowerCase()))
						.limit(maxResults)
						.collect(Collectors.toSet());

				if (!filteredItems.isEmpty()) {
					comboBox.getItems().setAll(filteredItems);
					if (!parentVBox.getChildren().contains(comboBox))
						parentVBox.getChildren().add(comboBox);
					comboBox.show();
				}
				else
					parentVBox.getChildren().remove(comboBox);
			}
		});
	}
	
	public static void setNodeFont(Node buttonPlay, String fontName, int fontSize) {
		buttonPlay.setStyle("-fx-font-family: '" + fontName + "'; -fx-font-size: " + fontSize + "px;");
	}

	public static void setListViewCellBackgroundColor(ListCell<?> cell, String evenColor, String oddColor, String hoveredEvenColor, String hoveredOddColor) {
		if (cell.isEmpty() || cell.getItem() == null) {
	 		cell.setBackground(Background.fill(Paint.valueOf("#FFFFFF")));
	 		return;
		}
		String color = getBackgroundColor(cell.getIndex(), cell.isSelected(), cell.getListView().isFocused(), cell.isHover(), evenColor, oddColor, hoveredEvenColor, hoveredOddColor); 
 		cell.setBackground(Background.fill(Paint.valueOf(color)));
	}
	
	public static void setListViewCellBackgroundColor(ListCell<?> cell)
		{ setListViewCellBackgroundColor(cell, null, null, null, null); }

	public static void setTableViewCellBackgroundColor(TableCell<?, ?> cell, String evenColor, String oddColor, String hoveredEvenColor, String hoveredOddColor) {
		if (cell.isEmpty() || cell.getItem() == null) {
	 		cell.setBackground(Background.fill(Paint.valueOf("#FFFFFF")));
	 		return;
		}
		String color = getBackgroundColor(cell.getIndex(), cell.isSelected(), cell.getTableView().isFocused(), cell.isHover(), evenColor, oddColor, hoveredEvenColor, hoveredOddColor); 
 		cell.setBackground(Background.fill(Paint.valueOf(color)));
	}
	
	public static void setTableViewCellBackgroundColor(TableCell<?, ?> cell) {
		setTableViewCellBackgroundColor(cell, null, null, null, null);
	}

	public static void setTableViewRowBackgroundColor(TableRow<?> row, String evenColor, String oddColor, String hoveredEvenColor, String hoveredOddColor) {
		String color = getBackgroundColor(row.getIndex(), row.isSelected(), row.getTableView().isFocused(), row.isHover(), evenColor, oddColor, hoveredEvenColor, hoveredOddColor); 
 		row.setBackground(Background.fill(Paint.valueOf(color)));
	}
	
	public static void setTableViewRowBackgroundColor(TableRow<?> row) {
		setTableViewRowBackgroundColor(row, null, null, null, null);
	}

	private static String getBackgroundColor(int rowIndex, Boolean isSelected, Boolean isFocused, Boolean isHovered, String evenColor, String oddColor, String hoveredEvenColor, String hoveredOddColor) {
		String color = null;
		if (isSelected) {
			if (isHovered) {
				if (isFocused)
	      	color = rowIndex % 2 != 0 ? COLOR_FOR_SELECTED_FOCUSED_HOVERED_EVEN : COLOR_FOR_SELECTED_FOCUSED_HOVERED_ODD;
				else
	      	color = rowIndex % 2 != 0 ? COLOR_FOR_SELECTED_HOVERED_EVEN : COLOR_FOR_SELECTED_HOVERED_ODD;
			}
			else if (isFocused)
      	color = rowIndex % 2 != 0 ? COLOR_FOR_SELECTED_FOCUSED_EVEN : COLOR_FOR_SELECTED_FOCUSED_ODD;
			else
      	color = rowIndex % 2 != 0 ? COLOR_FOR_SELECTED_EVEN : COLOR_FOR_SELECTED_ODD;
		}
		else if (evenColor != null && oddColor != null && hoveredEvenColor != null && hoveredOddColor != null) {
			if (isHovered)
      	color = rowIndex % 2 != 0 ? hoveredEvenColor : hoveredOddColor;
			else
      	color = rowIndex % 2 != 0 ? evenColor : oddColor;
		}
		else if (isHovered)
    	color = rowIndex % 2 != 0 ? COLOR_FOR_HOVERED_EVEN : COLOR_FOR_HOVERED_ODD;
  	else
    	color = rowIndex % 2 != 0 ? COLOR_FOR_REGULAR_EVEN : COLOR_FOR_REGULAR_ODD;
		return color;
	}

	/**
	 * Adiciona um evento ao ComboBox para selecionar no popup o item selecionado,
	 * em caso desse item não ter sido selecionado manualmente pelo usuário. Exemplo:
	 * Em uma ComboBox com uma lista de ANOS, você pode definir para ser selecionado
	 * um certo ano dessa lista ao inicializar, mas quando o usuário abrir o popup
	 * dessa ComboBox, o scroll não estará apontando para o ano selecionado. Isso só
	 * começa a acontecer depois que o usuário selecionar um ano da lista manualmente
	 * pelo menos uma vez. 
	 */
	public static void comboBoxAddEventToScrollToSelectedItem(ComboBox<?> comboBox)	{
		comboBox.setOnShowing(e -> comboBoxScrollTo(comboBox, comboBox.getSelectionModel().getSelectedIndex()));
	} 
	
	public static Tooltip getNewTooltip(String text, String fontFamily, int fontSize) {
		return getNewTooltip(text, "-fx-font-size: " + fontSize + "px; -fx-font-family: \"" + fontFamily + "\";");
	}
	
	public static Tooltip getNewTooltip(String text, String css) {
		Tooltip tp = new Tooltip(text);
		if (css != null)
			tp.setStyle(css);
		return tp;
	}
	
	public static Tooltip getNewTooltip(String text) {
		return getNewTooltip(text, null);
	}

	
	/** Permite executar um .scrollTo() em uma ComboBox, pois isso não é possível por padrão.
	 */
  public static void comboBoxScrollTo(ComboBox<?> comboBox, int index) {
    ListView<?> listView = (ListView<?>) comboBox.getEditor().lookup(".list-view");
    if (listView != null)
       listView.scrollTo(index);
    /* CODIGO ANTIGO (REMOVER SE O NOVO FUNCIONAR BEM)
  	ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) comboBox.getSkin();
		ListView<?> list = (ListView<?>) skin.getPopupContent();
		selectAndScrollTo(list, index);
		*/
  }

	public static void forceButtonSize(Button button, int width, int height) {
		button.setMinSize(width, height);
		button.setPrefSize(width, height);
		button.setMaxSize(width, height);
	}

	public static <T> void forceComboBoxSize(ComboBox<T> comboBox, int width, int height) {
		comboBox.setMinSize(width, height);
		comboBox.setPrefSize(width, height);
		comboBox.setMaxSize(width, height);
	}

	public static Stage getCurrentStage(ActionEvent event)
		{ return (Stage) ((Node) event.getSource()).getScene().getWindow(); }

	/**
	 * Move um item para cima ou para baixo em uma [@code ListView} , assim como
	 * o item da lista associada a {@code ListView}. Se {@code inc} for um valor
	 * negativo, move para cima, caso contrário, move para baixo. Ex: Se informar
	 * o valor {@code 3} para {@code inc}, o item será movido 3 posições para baixo.
	 * Se informar o valor {@code -4} para {@code inc}, o item será movido 4 posições
	 * para cima.
	 * 
	 * @param ListView<T>		A {@code ListView} que terá seu item movido
	 * @param List<T>				A lista associada a ListView que terá seu item movido
	 * @param index					Indice do item á ser movido
	 * @param inc						Incremento do indice atual do item especificado
	 * @return							Um valor indicando o total de indices que o item foi movida.
	 * 											Ex: Se o item estiver no indice 2, e você mandar mover o item
	 * 											10 indices para baixo, irá retornar -2 pois ao mover 2 indices
	 * 											para cima, o item chega ao indice 0.
	 */
	public static <T> void changeItemPosFromListView(ListView<T> listView, List<T> list, int index, int val) {
		listView.getSelectionModel().clearSelection();
		val = CollectionUtils.moveItemIndex(list, index, val);
		listView.getItems().setAll(list);
		listView.scrollTo(index + val);
	}
	
	public static ImageView getImageViewFromImagePath(String imagePath, double imageWidth, double imageHeight, Color removeColor, int removeBGColorTolerance) {
		Image image = new Image(imagePath);
		ImageView imageView;
		if (removeBGColorTolerance > -1)
			imageView = new ImageView(ImageUtils.removeBgColor(image, removeColor, removeBGColorTolerance));
		else imageView = new ImageView(image);
		imageView.setFitWidth(imageWidth);
		imageView.setFitHeight(imageHeight);
	  return imageView;
	}
	
	public static ImageView getImageViewFromImagePath(String imagePath, double imageWidth, double imageHeight, Color removeColor)
		{ return getImageViewFromImagePath(imagePath, imageWidth, imageHeight, removeColor, -1); }
	
	public static ImageView getImageViewFromImagePath(String imagePath, double imageWidth, double imageHeight, int removeBGColorTolerance)
		{ return getImageViewFromImagePath(imagePath, imageWidth, imageHeight, Color.WHITE, removeBGColorTolerance); }
	
	public static ImageView getImageViewFromImagePath(String imagePath, double imageWidth, double imageHeight)
		{ return getImageViewFromImagePath(imagePath, imageWidth, imageHeight, Color.WHITE, -1); }

	public static void addIconToButton(Button button, String imagePath, double imageWidth, double imageHeight) {
		button.setGraphic(getImageViewFromImagePath(imagePath, imageWidth, imageHeight, Color.WHITE, -1));
	}

	public static void addIconToButton(Button button, String imagePath) {
		Bounds bounds = button.getLayoutBounds();
		addIconToButton(button, imagePath, bounds.getWidth(), bounds.getHeight());
	}
	
	public static void addOnKeyPressedToPasteFormatCodes(TextField textField) {
		textField.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			String text = textField.getText() == null ? "" : textField.getText();
			if (e.getCode() == KeyCode.F1) {
				Stage stage = new Stage();
				VBox vBox = new VBox();
				vBox.setPadding(new Insets(10, 10, 10, 10));
				vBox.setSpacing(3);
				vBox.getChildren().addAll(
						new Label("{l?} - Define expessura do contorno do texto (Atalho: CTRL+L)"),
						new Label("{b} - Liga/Desliga negrito (Atalho: CTRL+B)"),
						new Label("{i} - Liga/Desliga itálico (Atalho: CTRL+I)"),
						new Label("{u} - Liga/Desliga sublinhado (Atalho: CTRL+U)"),
						new Label("{o} - Desliga qualquer efeito ligado previamente (Atalho: CTRL+O)"),
						new Label("{n} - Quebra de linha (Atalho: CTRL+N)"),
						new Label("{k?} - Define cor do texto (Atalho: CTRL+K)"),
						new Label("{k?,?} - Define cor do texto e cor do contorno (Atalho: CTRL+K)"),
						new Label("{#????????} - Define cor do texto usando formato hexadecimal #RRGGBBAA"),
						new Label("{#????????,#????????} - Define cor do texto e contorno usando formato hexadecimal #RRGGBBAA"),
						new Label("{COLOR} - Define cor do texto usando nome da cor em inglês"),
						new Label("{COLOR,COLOR - Define cor do texto e contorno usando nome da cor em inglês}")
				);
				Scene scene = new Scene(vBox);
				stage.setScene(scene);
				stage.setResizable(false);
				stage.setTitle("Lista de formatadores");
				stage.show();
			}
			else if (e.isControlDown()) {
				int caretPos = textField.getCaretPosition();
				String code = null;
				if (e.getCode() == KeyCode.B)
					code = "{b}";
				else if (e.getCode() == KeyCode.I)
					code = "{i}";
				else if (e.getCode() == KeyCode.O)
					code = "{o}";
				else if (e.getCode() == KeyCode.U)
					code = "{u}";
				else if (e.getCode() == KeyCode.L)
					code = "{l1}";
				else if (e.getCode() == KeyCode.N)
					code = "{n}";
				else if (e.getCode() == KeyCode.K) {
					if (!text.isBlank()) {
						openMircColorPicker(i -> {
							String s = "{k" + i +"}";
							textField.setText(text.substring(0, caretPos) + s + text.substring(caretPos));
							textField.positionCaret(caretPos + s.length());
						});
					}
					else
						openMircColorPicker(i -> {
							String s = "{k" + i +"}";
							textField.setText(s);
							textField.positionCaret(s.length());
						});
				}
				else
					return;
				if (code != null) {
					if (!text.isBlank()) {
						textField.setText(text.substring(0, caretPos) + code + text.substring(caretPos));
						textField.positionCaret(caretPos + code.length());
					}
					else {
						textField.setText(code);
						textField.positionCaret(code.length());
					}
				}
			}				
		});
	}
	
	public static void scrollTo(ScrollPane scrollPane, Node node) {
		Platform.runLater(() -> {
			double scrollPaneHeight = scrollPane.getViewportBounds().getHeight();
			double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
			double yPosition = node.getBoundsInParent().getMinY(); // Melhor maneira de obter posição relativa
			double targetScroll = yPosition / (contentHeight - scrollPaneHeight);
			scrollPane.setVvalue(Math.max(0, Math.min(1, targetScroll)));
		});
	}

	public static Font loadFontFromFile(String fontAbsolutePath, int fontSize) {
    try (InputStream fontStream = new FileInputStream(fontAbsolutePath)) {
    	return Font.loadFont(fontStream, fontSize);
    }
    catch (Exception e) {
    	e.printStackTrace();
    	return null;
    }
	}
	
	public static void makeItBlink(Node node) {
		node.setOpacity(1.0);
		FadeTransition fade = new FadeTransition(Duration.seconds(0.5), node);
		fade.setFromValue(1.0);
		fade.setToValue(0.3);
		fade.setCycleCount(4);
		fade.setAutoReverse(true);
		fade.play();
	}
  
	public static void openMircColorPicker(Consumer<Integer> onClickEvent) {
		Stage stage = new Stage();
		VBox vBox = new VBox();
		vBox.setSpacing(3);
		HBox hBox = null;
		for (int n = 0; n < ImageUtils.mircColors.length; n++) {
			if (n % 10 == 0) {
				if (n != 0)
					vBox.getChildren().add(hBox);
				hBox = new HBox();
				hBox.setSpacing(6);
			}
			HBox hBox2 = new HBox();
			hBox2.setAlignment(Pos.CENTER_LEFT);
			hBox2.setSpacing(1);
			hBox2.getChildren().add(new Label((n < 10 ? "0" : "") + n));
			WritableImage square = new WritableImage(20, 20);
			for (int y = 0; y < 20; y++)
				for (int x = 0; x < 20; x++) {
					if (x > 1 && y > 1 && x < 18 && y < 18)
						square.getPixelWriter().setColor(y, x, Color.color(ImageUtils.mircColors[n][0], ImageUtils.mircColors[n][1], ImageUtils.mircColors[n][2], ImageUtils.mircColors[n][3]));
					else 
						square.getPixelWriter().setColor(y, x, Color.BLACK);
				}
			hBox2.getChildren().add(new ImageView(square));
			final int n2 = n;
			hBox2.setOnMouseClicked(e -> {
				if (onClickEvent != null)
					onClickEvent.accept(n2);
				stage.close();
			});
			hBox.getChildren().add(hBox2);
		}				
		vBox.getChildren().add(hBox);
		stage.setScene(new Scene(vBox));
		stage.sizeToScene();
		stage.initStyle(StageStyle.UNDECORATED);
		stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
			if (!isNowFocused)
				stage.close();
		});
		stage.show();
	}
	
}