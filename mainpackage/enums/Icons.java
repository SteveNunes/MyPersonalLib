package enums;

import gui.util.ControllerUtils;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public enum Icons {

	BLANK("/icons/blank.png"),
	BRICKED_NUMBER("/icons/blocked_number.png"),
	BRICKED_NUMBER_2("/icons/blocked_number_2.png"),
	BRICKED_NUMBER_3("/icons/blocked_number_3.png"),
	CALENDAR("/icons/calendar.png"),
	CANCEL("/icons/cancel.png"),
	CLIPBOARD("/icons/clipboard.png"),
	CONFIG("/icons/config.png"),
	COPY("/icons/copy.png"),
	DELETE("/icons/delete.png"),
	EDIT("/icons/edit.png"),
	ERASER("/icons/eraser.png"),
	EXCEL("/icons/excel.png"),
	FOWARD("/icons/foward.png"),
	FORMAT_TEXT("/icons/format_text.png"),
	KEY("/icons/key.png"),
	LOOKING_FOR_SOMEONE("/icons/looking_for_someone.png"),
	MINUS("/icons/minus.png"),
	MOVE("/icons/move.png"),
	MOVE_DOWN("/icons/move_down.png"),
	MOVE_LEFT("/icons/move_left.png"),
	MOVE_RIGHT("/icons/move_right.png"),
	MOVE_UP("/icons/move_up.png"),
	MOVE_MAX_DOWN("/icons/move_max_down.png"),
	MOVE_MAX_LEFT("/icons/move_max_left.png"),
	MOVE_MAX_RIGHT("/icons/move_max_right.png"),
	MOVE_MAX_UP("/icons/move_max_up.png"),
	NEW_FILE("/icons/new_file.png"),
	NEW_ITEM("/icons/new_item.png"),
	NOTE("/icons/note.png"),
	OK("/icons/ok.png"),
	OPEN_FILE("/icons/open_file.png"),
	OPEN_TEXT("/icons/open_text.png"),
	PAIR("/icons/pair.png"),
	PASTE("/icons/paste.png"),
	PAUSE("/icons/pause.png"),
	PIN("/icons/pin.png"),
	PLAY("/icons/play.png"),
	PLUS("/icons/plus.png"),
	RANDOM("/icons/random.png"),
	REFRESH("/icons/refresh.png"),
	RESIZE("/icons/resize.png"),
	UNREAD_MESSAGE("/icons/unread_message.png"),
	UNREAD_MESSAGES("/icons/unread_messages.png"),
	SAVE("/icons/save.png"),
	SAVE_TEXT("/icons/save_text.png"),
	STOP("/icons/stop.png"),
	TEXT("/icons/text.png"),
	UNPIN("/icons/unpin.png"),
	VERIFY("/icons/verify.png"),
	VERIFIED("/icons/verified.png"),
	WHATSAPP("/icons/whatsapp.png"),
	ZOOM_PLUS("/icons/zoom+.png"),
	ZOOM_MINUS("/icons/zoom-.png");
	
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
		return ControllerUtils.getImageViewFromImagePath(icon.getValue(), width, height, Color.WHITE, 50);
	}
	
}
