package enums;

import gui.util.Controller;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public enum Icons {

	ICON_BLANK(""),
	ICON_CALENDAR("/icons/calendar.png"),
	ICON_COPY("/icons/copy.png"),
	ICON_CLIPBOARD("/icons/clipboard.png"),
	ICON_DELETE("/icons/delete.png"),
	ICON_EDIT("/icons/edit.png"),
	ICON_ERASER("/icons/eraser.png"),
	ICON_EXCEL("/icons/excel.png"),
	ICON_MINUS("/icons/minus.png"),
	ICON_MOVEDOWN("/icons/movedown.png"),
	ICON_MOVELEFT("/icons/moveleft.png"),
	ICON_MOVERIGHT("/icons/moveright.png"),
	ICON_MOVEUP("/icons/moveup.png"),
	ICON_MOVEMAXDOWN("/icons/movemaxdown.png"),
	ICON_MOVEMAXLEFT("/icons/movemaxleft.png"),
	ICON_MOVEMAXRIGHT("/icons/movemaxright.png"),
	ICON_MOVEMAXUP("/icons/movemaxup.png"),
	ICON_NEWITEM("/icons/newitem.png"),
	ICON_OPENTEXT("/icons/opentext.png"),
	ICON_SAVETEXT("/icons/savetext.png"),
	ICON_OK("/icons/ok.png"),
	ICON_PAIR("/icons/pair.png"),
	ICON_PASTE("/icons/paste.png"),
	ICON_PAUSE("/icons/pause.png"),
	ICON_PIN("/icons/pin.png"),
	ICON_PLAY("/icons/play.png"),
	ICON_PLUS("/icons/plus.png"),
	ICON_RANDOM("/icons/random.png"),
	ICON_REFRESH("/icons/refresh.png"),
	ICON_SAVE("/icons/save.png"),
	ICON_STOP("/icons/stop.png"),
	ICON_TEXT("/icons/text.png"),
	ICON_UNPIN("/icons/unpin.png"),
	ICON_WHATSAPP("/icons/whatsapp.png"),
	ICON_WAITING_A_CONFIRMATION_FOR_START_BIBLE_STUDY("/icons/waitingconfirmation.png"),
	ICON_LOOKING_FOR_SOMEONE_TO_FOWARD_THE_RETURN_VISIT("/icons/lookingforsomeone.png"),
	ICON_BIBLE_STUDY("/icons/biblestudy.png"),
	ICON_RETURN_VISIT("/icons/returnvisit.png"),
	ICON_TRYING_THE_FIRST_CONVERSATION("/icons/tryingfirstconversation.png"),
	ICON_NEVER_PICK_UP_THE_PHONE("/icons/neverpickedup.png"),
	ICON_RETURN_VISIT_FOWARDED_TO_SOMEONE("/icons/fowarded.png"),
	ICON_RESIDENT_ASKED_TO_NOT_CALL_ANYMORE("/icons/askedtonotcallanymore.png"),
	ICON_BLOCKED_BY_RESIDENT("/icons/blockednumber.png"),
	ICON_UNDEFINED(""),
	ICON_NUMBER_NOT_EXISTS("/icons/invalidnumber.png");

	
	private final String value;

	Icons(String value)
		{ this.value = value; }

	public String getValue()
		{ return value; }
	
	public ImageView getImageView(double width, double height)
		{ return getImageView(this, width, height); }
	
	public static ImageView getImageView(Icons icon, double width, double height) {
		if (icon.getValue().isEmpty()) {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(width);
			imageView.setFitHeight(height);
			return imageView;
		}
		return Controller.getImageViewFromImagePath(icon.getValue(), width, height, Color.WHITE, 50);
	}
	
}
