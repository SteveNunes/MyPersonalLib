package gui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.Misc;

public class Controller {
	
	private static Map<TableView<?>, Integer> hoveredTableViewRowIndex = new HashMap<>();
	private static Map<ListView<?>, Integer> hoveredListViewRowIndex = new HashMap<>();
	private static Map<TableView<?>, TableRow<?>> hoveredTableRow = new HashMap<>();
  private static Map<TableView<?>, TableCell<?, ?>> hoveredTableCell = new HashMap<>();
  private static Map<ListView<?>, ListCell<?>> hoveredListCell = new HashMap<>();

  /** Métodos para obter e definir {@code Index}, {@code Row} e {@code Cell} da
   * 	ultima linha selecionada em uma {@code TableView} ou {@code ListView}
   */
  public static Integer getHoveredTableViewRowIndex(TableView<?> tableView)
  	{ return hoveredTableViewRowIndex.get(tableView); }
  public static Integer hoverListViewRowIndex(ListView<?> listView)
  	{ return hoveredListViewRowIndex.get(listView); }
  public static void setHoveredTableViewRowIndex(TableView<?> tableView, Integer index)
  	{ hoveredTableViewRowIndex.put(tableView, index); }
  public static void setHoveredListViewRowIndex(ListView<?> listView, Integer index)
  	{ hoveredListViewRowIndex.put(listView, index); }
  public static TableRow<?> getHoveredTableRow(TableView<?> tableView)
		{ return hoveredTableRow.get(tableView); }
	public static void setHoveredTableRow(TableView<?> tableView, TableRow<?> row)
		{ hoveredTableRow.put(tableView, row); }
  public static TableCell<?, ?> getHoveredTableCell(TableView<?> tableView)
		{ return hoveredTableCell.get(tableView); }
	public static void setHoveredTableCell(TableView<?> tableView, TableCell<?, ?> cell)
		{ hoveredTableCell.put(tableView, cell); }
  public static ListCell<?> getHoveredListCell(ListView<?> listView)
		{ return hoveredListCell.get(listView); }
	public static void setHoveredListCell(ListView<?> listView, ListCell<?> cell)
		{ hoveredListCell.put(listView, cell); }
	
	private static final String COLOR_FOR_SELECTED_FOCUSED_HOVERED_EVEN = "#6F8FFF";
	private static final String COLOR_FOR_SELECTED_FOCUSED_HOVERED_ODD = "#5F7FEF";
	private static final String COLOR_FOR_SELECTED_FOCUSED_EVEN = "#4F6FDF";
	private static final String COLOR_FOR_SELECTED_FOCUSED_ODD = "#3F5FCF";
	private static final String COLOR_FOR_SELECTED_HOVERED_EVEN = "#BBBBBB";
	private static final String COLOR_FOR_SELECTED_HOVERED_ODD = "#AAAAAA";
	private static final String COLOR_FOR_SELECTED_EVEN = "#999999";
	private static final String COLOR_FOR_SELECTED_ODD = "#888888";
	private static final String COLOR_FOR_HOVERED_EVEN = "#FFFE88";
	private static final String COLOR_FOR_HOVERED_ODD = "#EEFE88";
	private static final String COLOR_FOR_REGULAR_EVEN = "#FFFFFF";
	private static final String COLOR_FOR_REGULAR_ODD = "#EEEEEE";
	
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
	
	public static void setTableViewCellBackgroundColor(TableCell<?, ?> cell)
		{ setTableViewCellBackgroundColor(cell, null, null, null, null); }

	public static void setTableViewRowBackgroundColor(TableRow<?> row, String evenColor, String oddColor, String hoveredEvenColor, String hoveredOddColor) {
		String color = getBackgroundColor(row.getIndex(), row.isSelected(), row.getTableView().isFocused(), row.isHover(), evenColor, oddColor, hoveredEvenColor, hoveredOddColor); 
 		row.setBackground(Background.fill(Paint.valueOf(color)));
	}
	
	public static void setTableViewRowBackgroundColor(TableRow<?> row)
		{ setTableViewRowBackgroundColor(row, null, null, null, null); }

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
	public static void comboBoxAddEventToScrollToSelectedItem(ComboBox<?> comboBox)
		{ comboBox.setOnShowing(e -> comboBoxScrollTo(comboBox, comboBox.getSelectionModel().getSelectedIndex())); } 
	
	/**
	 * Seleciona a linha desejada na {@code ListView}  e mantém
	 * o item selecionado na metade da lista visivel.
	 * @param ListView<?>		A {@code ListView} de onde será selecionada a linha.
	 * @param index					O indice da linha desejada.
	 */
	public static void selectAndScrollTo(ListView<?> listView, int index) {
		int n = (int)(listView.getHeight() / listView.getFixedCellSize() / 2);
		listView.getSelectionModel().select(index);
		listView.scrollTo(index - n);
	}

	/**
	 * Seleciona a linha desejada na {@code TableView}  e mantém
	 * o item selecionado na metade da lista visivel.
	 * @param TableView<?>		A {@code TableView} de onde será selecionada a linha.
	 * @param index						O indice da linha desejada.
	 */
	public static void selectAndScrollTo(TableView<?> tableView, int index) {
		int n = (int)(tableView.getHeight() / tableView.getFixedCellSize() / 2);
		tableView.getSelectionModel().select(index);
		tableView.scrollTo(index - n);
	}
	
	/** Permite executar um .scrollTo() em uma ComboBox, pois isso não é possível por padrão.
	 */
  public static void comboBoxScrollTo(ComboBox<?> comboBox, int index) {
		ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) comboBox.getSkin();
		ListView<?> list = (ListView<?>) skin.getPopupContent();
		selectAndScrollTo(list, index);
  }

	/**
	 * Atualiza a Table View com o conteudo provindo de uma lista
	 * @param <?>					Tipo da lista provedora de conteudo para a Table View
	 * @param tableView		Table View á ser atualizada con o conteudo da lista informada
	 * @param list				Lista provedora de conteudo para a Table View
	 */
	public static <T> void setListToTableView(TableView<T> tableView, List<T> list, int selectedLineIndex) {
		ObservableList<T> obsList = FXCollections.observableArrayList(list);
		tableView.setItems(obsList);
		if (selectedLineIndex > -1)
			selectAndScrollTo(tableView, selectedLineIndex);
	}
	
	public static <T> void setListToTableView(TableView<T> tableView, List<T> list)
		{ setListToTableView(tableView, list, -1); }
	
	public static <T> void setListToListView(ListView<T> listView, List<T> list, int selectedLineIndex) {
		listView.setItems(FXCollections.observableArrayList(list));
		if (selectedLineIndex > -1)
			selectAndScrollTo(listView, selectedLineIndex);
	}

	public static <T> void setListToListView(ListView<T> listView, List<T> list)
		{ setListToListView(listView, list, -1);	}

	/**
	 * Adiciona uma lista de itens a uma Combo Box. Cada item será exibido de
	 * acordo com seu método {@code .toString()}. Para alterar a forma como os
	 * itens de uma Combo Box serão exibidos, consulte o método {@code changeHowComboBoxDisplayItens()}
	 * @param <T>				Tipo dos itens da Combo Box
	 * @param comboBox	Combo Box de destino
	 * @param list			Lista á ter seus elementos adicionados á Combo Box
	 */
	public static <T> void setListToComboBox(ComboBox<T> comboBox, List<T> list, int selectedLineIndex) {
		comboBox.setItems(FXCollections.observableArrayList(list));
		if (selectedLineIndex > -1)
			comboBox.getSelectionModel().select(selectedLineIndex);
	}
	
	/**
	 * Sobrecarga do método {@code setListToComboBox(ComboBox<T> comboBox, List<T> list, int selectedLineIndex)}
	 * que não pede o parâmetro {@code selectedLineIndex} (Nenhum item será selecionado ao iniciar o ComboBox)
	 */
	public static <T> void setListToComboBox(ComboBox<T> comboBox, List<T> list)
		{ setListToComboBox(comboBox, list, -1);	}
	
	/**
	 * Altera a forma como os itens de uma Combo Box irão ser exibidos.
	 * @param <T>				Tipo dos itens da Combo Box
	 * @param <S>				Tipo de retorno de cada item da Combo Box
	 * @param comboBox	Combo Box de destino
	 * @param showMask	{@code Function<T, S>} que retorna como o valor de cada item será exibido
	 * 
	 * @Exemplo Com uma lista de {@code Pessoa}, para fazer com que nessa ComboBox seja exibido
	 * o valor do método {@code .getName()}, chame o método da seguinte forma:
	 * {@code initializeComboBox(comboBox, p -> p.getName());}
	 */
	public static <T, S> void changeHowComboBoxDisplayItens(ComboBox<T> comboBox, Function<T, S> showMask) {
		Callback<ListView<T>, ListCell<T>> factory = lv -> new ListCell<T>() {
			@Override
			protected void updateItem(T oper, boolean empty) {
				super.updateItem(oper, empty);
				setText(empty || oper == null || showMask.apply(oper) == null ? "" : showMask.apply(oper).toString());
			}
		};
		comboBox.setCellFactory(factory);
		comboBox.setButtonCell(factory.call(null));
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
	 * {@code initializeListView(listView, p -> p.getName());}
	 */
	public static <T, S> void changeHowListViewDisplayItens(ListView<T> listView, Function<T, S> showMask) {
		Callback<ListView<T>, ListCell<T>> factory = lv -> new ListCell<T>() {
			@Override
			protected void updateItem(T oper, boolean empty) {
				super.updateItem(oper, empty);
				setText(empty || oper == null || showMask.apply(oper) == null ? "" : showMask.apply(oper).toString());
			}
		};
		listView.setCellFactory(factory);
	}

	public static Stage currentStage(ActionEvent event)
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
	public static <T> void changeItemPosFromList(ListView<T> listView, List<T> list, int index, int val) {
		listView.getSelectionModel().clearSelection();
		val = Misc.moveItemIndex(list, index, val);
		Controller.setListToListView(listView, list, index + val);
		int n = (int)(listView.getHeight() / listView.getFixedCellSize() / 2);
		listView.scrollTo(n);
	}
	
	public static void addIconToButton(Button button, String imagePath, double imageWidth, double imageHeight, int removeBGColorTolerance)
		{ button.setGraphic(getImageViewFromImagePath(imagePath, imageWidth, imageHeight, removeBGColorTolerance)); }
	
	public static ImageView getImageViewFromImagePath(String imagePath, double imageWidth, double imageHeight, int removeBGColorTolerance) {
		Image image = new Image(imagePath);
		ImageView imageView;
		if (removeBGColorTolerance != -2)
			imageView = new ImageView(removeBgColor(image, removeBGColorTolerance));
		else imageView = new ImageView(image);
		imageView.setFitWidth(imageWidth);
		imageView.setFitHeight(imageHeight);
	  return imageView;
	}
	
	public static void addIconToButton(Button button, String imagePath, double imageWidht, double imageHeight)
		{ addIconToButton(button, imagePath, imageWidht, imageHeight, -2); }
	
	
	
	/** Remove o branco ou tons similares de branco da imagem (de acordo com o valor
	 * 	de {@code toleranceThreshold} (Que vai de 0 a 255) Quanto mais próximo de 255,
	 * 	menos tons de branco serão removidos (Se usar 255, apenas o branco puro será
	 * 	removido da imagem). Se usar -1, será removido a cor padrão para background
	 * 	(0x00FF00 (Verde))
	 * 
	 * @param image		A imagem a ter a cor de fundo removida
	 * @param toleranceThreshold		Valor de tolerância
	 * @return		A imagem com a cor de fundo removida
	 */
	
	public static Image removeBgColor(Image image, int toleranceThreshold) {
		int W = (int) image.getWidth();
		int H = (int) image.getHeight();
		WritableImage outputImage = new WritableImage(W, H);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = outputImage.getPixelWriter();
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				int argb = reader.getArgb(x, y);
	
				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = argb & 0xFF;
				if (toleranceThreshold == -1) {
					if (r == 0 && g == 255 && b == 0)
						argb &= 0x00FFFFFF;
				}
				else if (r >= toleranceThreshold && g >= toleranceThreshold && b >= toleranceThreshold)
					argb &= 0x00FFFFFF;
	
				writer.setArgb(x, y, argb);
			}
		}
		return outputImage;
	}

	/* COMO PEGAR AS COORDENADAS DE TELA DE UM NODE
					Bounds bounds = node.localToScreen(node.getBoundsInLocal());
					bounds.XXXXX()
					
			 COMO MOVER O MOUSE PARA UM LOCAL ESPECIFICO DA TELA
					new Robot().mouseMove(x, y);
		 */
	
}