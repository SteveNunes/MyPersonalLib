package gui.util;

import javafx.scene.control.Tooltip;

public class GuiMisc {

	public static Tooltip getNewTooltip(String text, String fontFamily, int fontSize)
		{ return getNewTooltip(text, "-fx-font-size: " + fontSize + "px; -fx-font-family: \"" + fontFamily + "\";"); }
	
	public static Tooltip getNewTooltip(String text, String css) {
		Tooltip tp = new Tooltip(text);
		if (css != null)
			tp.setStyle(css);
		return tp;
	}

	public static Tooltip getNewTooltip(String text)
		{ return getNewTooltip(text, null); }

}
