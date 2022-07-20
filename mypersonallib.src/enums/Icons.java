package enums;

import gui.util.Controller;
import javafx.scene.image.ImageView;

public enum Icons {

	ICON_BLANK(""),
	ICON_CALENDAR(".\\icons\\calendar.png"),
	ICON_COPY(".\\icons\\copy.png"),
	ICON_DELETE(".\\icons\\delete.png"),
	ICON_EDIT(".\\icons\\edit.png"),
	ICON_EXCEL(".\\icons\\excel.png"),
	ICON_MOVEDOWN(".\\icons\\movedown.png"),
	ICON_MOVELEFT(".\\icons\\moveleft.png"),
	ICON_MOVERIGHT(".\\icons\\moveright.png"),
	ICON_MOVEUP(".\\icons\\moveup.png"),
	ICON_MOVEMAXDOWN(".\\icons\\movemaxdown.png"),
	ICON_MOVEMAXLEFT(".\\icons\\movemaxleft.png"),
	ICON_MOVEMAXRIGHT(".\\icons\\movemaxright.png"),
	ICON_MOVEMAXUP(".\\icons\\movemaxup.png"),
	ICON_NEWITEM(".\\icons\\newitem.png"),
	ICON_OK(".\\icons\\ok.png"),
	ICON_PAIR(".\\icons\\pair.png"),
	ICON_PASTE(".\\icons\\paste.png"),
	ICON_PIN(".\\icons\\pin.png"),
	ICON_RANDOM(".\\icons\\random.png"),
	ICON_REFRESH(".\\icons\\refresh.png"),
	ICON_SAVE(".\\icons\\save.png"),
	ICON_TEXT(".\\icons\\text.png"),
	ICON_UNPIN(".\\icons\\unpin.png"),
	ICON_WHATSAPP(".\\icons\\whatsapp.png");
	
	private final String value;

	Icons(String value)
		{ this.value = value; }

	public String getValue()
		{ return value; }
	
	public ImageView getImageView(double width, double height)
		{ return getImageView(this, width, height); }
	
	public static ImageView getImageView(Icons icon, double width, double height) {
		if (icon == ICON_BLANK) {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(width);
			imageView.setFitHeight(height);
			return imageView;
		}
		return Controller.getImageViewFromImagePath(icon.getValue(), width, height, 200);
	}
	
}
