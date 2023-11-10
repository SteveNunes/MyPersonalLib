package util;

public abstract class ANSIUtils {
	
	public static final String resetFormaters = "[0m";
	public static final String fontColorDarkBlack = "[30m";
	public static final String fontColorDarkRed = "[31m";
	public static final String fontColorDarkGreen = "[32m";
	public static final String fontColorDarkYellow = "[33m";
	public static final String fontColorDarkBlue = "[34m";
	public static final String fontColorDarkMagenta = "[35m";
	public static final String fontColorDarkCyan = "[36m";
	public static final String fontColorDarkWhite = "[37m";
	public static final String fontColorBlack = "[90m";

	public static final String fontColorLightRed = "[91m";
	public static final String fontColorLightGreen = "[92m";
	public static final String fontColorLightYellow = "[93m";
	public static final String fontColorLightBlue = "[94m";
	public static final String fontColorLightMagenta = "[95m";
	public static final String fontColorLightCyan = "[96m";
	public static final String fontColorLightWhite = "[97m";
	
	public static final String bgColorDarkBlack = "[40m";
	public static final String bgColorDarkRed = "[41m";
	public static final String bgColorDarkGreen = "[42m";
	public static final String bgColorDarkYellow = "[43m";
	public static final String bgColorDarkBlue = "[44m";
	public static final String bgColorDarkMagenta = "[45m";
	public static final String bgColorDarkCyan = "[46m";
	public static final String bgColorDarkWhite = "[47m";
	
	public static final String bgColorLightBlack = "[100m";
	public static final String bgColorLightRed = "[101m";
	public static final String bgColorLightGreen = "[102m";
	public static final String bgColorLightYellow = "[103m";
	public static final String bgColorLightBlue = "[104m";
	public static final String bgColorLightMagenta = "[105m";
	public static final String bgColorLightCyan = "[106m";
	public static final String bgColorLightWhite = "[107m";
	
	public static final String textFormatBold = "[1m";
	public static final String textFormatItalic = "[3m";
	public static final String textFormatUnderlined = "[4m";
	public static final String textFormatInvertedColors = "[7m";
	public static final String textFormatTraced = "[9m";
	public static final String textFormatDoubleUnderlined = "[21m";
	public static final String textFormatSquared = "[51m";

	public static final String[] allFontColors = {
		resetFormaters, fontColorDarkBlack, fontColorDarkRed, fontColorDarkGreen,
		fontColorDarkYellow, fontColorDarkBlue, fontColorDarkMagenta,
		fontColorDarkCyan, fontColorDarkWhite, fontColorBlack, fontColorLightRed,
		fontColorLightGreen, fontColorLightYellow, fontColorLightBlue,
		fontColorLightMagenta, fontColorLightCyan, fontColorLightWhite
	};
	
	public static final String[] allBgColors = {
		resetFormaters, bgColorDarkBlack, bgColorDarkRed, bgColorDarkGreen,
		bgColorDarkYellow, bgColorDarkBlue, bgColorDarkMagenta,
		bgColorDarkCyan, bgColorDarkWhite, bgColorLightBlack, bgColorLightRed,
		bgColorLightGreen, bgColorLightYellow, bgColorLightBlue,
		bgColorLightMagenta, bgColorLightCyan, bgColorLightWhite
	};
	
	public static void moveCursorTo(int x, int y)
		{ System.out.print("[" + y + ";" + x + "H"); }

	public static void moveCursorToUp(int totalMoves)
		{ System.out.print("[" + totalMoves + "A"); }
	
	public static void moveCursorToDown(int totalMoves)
		{ System.out.print("[" + totalMoves + "B"); }
	
	public static void moveCursorToLeft(int totalMoves)
		{ System.out.print("[" + totalMoves + "C"); }

	public static void moveCursorToRight(int totalMoves)
		{ System.out.print("[" + totalMoves + "D"); }
	
	public static void clearScreen() {
		for (int n = 0; n < 45; n++)
			System.out.println();
	}

}
