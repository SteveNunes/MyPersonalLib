package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.MircColorsStyle;
import javafx.scene.paint.Color;

public class MircUtils {

	public static final char CODE_BOLD = '\u0002';
	public static final char CODE_COLOR = '\u0003';
	public static final char CODE_STRIP = '\u0015';
	public static final char CODE_REVERSE = '\u0022';
	public static final char CODE_ITALIC = '\u0029';
	public static final char CODE_STRIKETHROUGH = '\u0030';
	public static final char CODE_UNDERLINE = '\u0031';

	public static Map<Integer, Color> mircColors = new HashMap<>() {
		{
			put(0, new Color(1.0f, 1.0f, 1.0f, 1f));
			put(1, new Color(0.0f, 0.0f, 0.0f, 1f));
			put(2, new Color(0.0f, 0.0f, 0.49803922f, 1f));
			put(3, new Color(0.0f, 0.5764706f, 0.0f, 1f));
			put(4, new Color(1.0f, 0.0f, 0.0f, 1f));
			put(5, new Color(0.49803922f, 0.0f, 0.0f, 1f));
			put(6, new Color(0.6117647f, 0.0f, 0.6117647f, 1f));
			put(7, new Color(0.9882353f, 0.49803922f, 0.0f, 1f));
			put(8, new Color(1.0f, 1.0f, 0.0f, 1f));
			put(9, new Color(0.0f, 0.9882353f, 0.0f, 1f));
			put(10, new Color(0.0f, 0.5764706f, 0.5764706f, 1f));
			put(11, new Color(0.0f, 1.0f, 1.0f, 1f));
			put(12, new Color(0.0f, 0.0f, 0.9882353f, 1f));
			put(13, new Color(1.0f, 0.0f, 1.0f, 1f));
			put(14, new Color(0.49803922f, 0.49803922f, 0.49803922f, 1f));
			put(15, new Color(0.8235294f, 0.8235294f, 0.8235294f, 1f));
			put(16, new Color(0.2784314f, 0.0f, 0.0f, 1f));
			put(17, new Color(0.2784314f, 0.12941177f, 0.0f, 1f));
			put(18, new Color(0.2784314f, 0.2784314f, 0.0f, 1f));
			put(19, new Color(0.19607843f, 0.2784314f, 0.0f, 1f));
			put(20, new Color(0.0f, 0.2784314f, 0.0f, 1f));
			put(21, new Color(0.0f, 0.2784314f, 0.17254902f, 1f));
			put(22, new Color(0.0f, 0.2784314f, 0.2784314f, 1f));
			put(23, new Color(0.0f, 0.15294118f, 0.2784314f, 1f));
			put(24, new Color(0.0f, 0.0f, 0.2784314f, 1f));
			put(25, new Color(0.18039216f, 0.0f, 0.2784314f, 1f));
			put(26, new Color(0.2784314f, 0.0f, 0.2784314f, 1f));
			put(27, new Color(0.2784314f, 0.0f, 0.16470589f, 1f));
			put(28, new Color(0.45490196f, 0.0f, 0.0f, 1f));
			put(29, new Color(0.45490196f, 0.22745098f, 0.0f, 1f));
			put(30, new Color(0.45490196f, 0.45490196f, 0.0f, 1f));
			put(31, new Color(0.31764707f, 0.45490196f, 0.0f, 1f));
			put(32, new Color(0.0f, 0.45490196f, 0.0f, 1f));
			put(33, new Color(0.0f, 0.45490196f, 0.28627452f, 1f));
			put(34, new Color(0.0f, 0.45490196f, 0.45490196f, 1f));
			put(35, new Color(0.0f, 0.2509804f, 0.45490196f, 1f));
			put(36, new Color(0.0f, 0.0f, 0.45490196f, 1f));
			put(37, new Color(0.29411766f, 0.0f, 0.45490196f, 1f));
			put(38, new Color(0.45490196f, 0.0f, 0.45490196f, 1f));
			put(39, new Color(0.45490196f, 0.0f, 0.27058825f, 1f));
			put(40, new Color(0.70980394f, 0.0f, 0.0f, 1f));
			put(41, new Color(0.70980394f, 0.3882353f, 0.0f, 1f));
			put(42, new Color(0.70980394f, 0.70980394f, 0.0f, 1f));
			put(43, new Color(0.49019608f, 0.70980394f, 0.0f, 1f));
			put(44, new Color(0.0f, 0.70980394f, 0.0f, 1f));
			put(45, new Color(0.0f, 0.70980394f, 0.44313726f, 1f));
			put(46, new Color(0.0f, 0.70980394f, 0.70980394f, 1f));
			put(47, new Color(0.0f, 0.3882353f, 0.70980394f, 1f));
			put(48, new Color(0.0f, 0.0f, 0.70980394f, 1f));
			put(49, new Color(0.45882353f, 0.0f, 0.70980394f, 1f));
			put(50, new Color(0.70980394f, 0.0f, 0.70980394f, 1f));
			put(51, new Color(0.70980394f, 0.0f, 0.41960785f, 1f));
			put(52, new Color(1.0f, 0.0f, 0.0f, 1f));
			put(53, new Color(1.0f, 0.54901963f, 0.0f, 1f));
			put(54, new Color(1.0f, 1.0f, 0.0f, 1f));
			put(55, new Color(0.69803923f, 1.0f, 0.0f, 1f));
			put(56, new Color(0.0f, 1.0f, 0.0f, 1f));
			put(57, new Color(0.0f, 1.0f, 0.627451f, 1f));
			put(58, new Color(0.0f, 1.0f, 1.0f, 1f));
			put(59, new Color(0.0f, 0.54901963f, 1.0f, 1f));
			put(60, new Color(0.0f, 0.0f, 1.0f, 1f));
			put(61, new Color(0.64705884f, 0.0f, 1.0f, 1f));
			put(62, new Color(1.0f, 0.0f, 1.0f, 1f));
			put(63, new Color(1.0f, 0.0f, 0.59607846f, 1f));
			put(64, new Color(1.0f, 0.34901962f, 0.34901962f, 1f));
			put(65, new Color(1.0f, 0.7058824f, 0.34901962f, 1f));
			put(66, new Color(1.0f, 1.0f, 0.44313726f, 1f));
			put(67, new Color(0.8117647f, 1.0f, 0.3764706f, 1f));
			put(68, new Color(0.43529412f, 1.0f, 0.43529412f, 1f));
			put(69, new Color(0.39607844f, 1.0f, 0.7882353f, 1f));
			put(70, new Color(0.42745098f, 1.0f, 1.0f, 1f));
			put(71, new Color(0.34901962f, 0.7058824f, 1.0f, 1f));
			put(72, new Color(0.34901962f, 0.34901962f, 1.0f, 1f));
			put(73, new Color(0.76862746f, 0.34901962f, 1.0f, 1f));
			put(74, new Color(1.0f, 0.4f, 1.0f, 1f));
			put(75, new Color(1.0f, 0.34901962f, 0.7372549f, 1f));
			put(76, new Color(1.0f, 0.6117647f, 0.6117647f, 1f));
			put(77, new Color(1.0f, 0.827451f, 0.6117647f, 1f));
			put(78, new Color(1.0f, 1.0f, 0.6117647f, 1f));
			put(79, new Color(0.8862745f, 1.0f, 0.6117647f, 1f));
			put(80, new Color(0.6117647f, 1.0f, 0.6117647f, 1f));
			put(81, new Color(0.6117647f, 1.0f, 0.85882354f, 1f));
			put(82, new Color(0.6117647f, 1.0f, 1.0f, 1f));
			put(83, new Color(0.6117647f, 0.827451f, 1.0f, 1f));
			put(84, new Color(0.6117647f, 0.6117647f, 1.0f, 1f));
			put(85, new Color(0.8627451f, 0.6117647f, 1.0f, 1f));
			put(86, new Color(1.0f, 0.6117647f, 1.0f, 1f));
			put(87, new Color(1.0f, 0.5803922f, 0.827451f, 1f));
			put(88, new Color(0.0f, 0.0f, 0.0f, 1f));
			put(89, new Color(0.07450981f, 0.07450981f, 0.07450981f, 1f));
			put(90, new Color(0.15686275f, 0.15686275f, 0.15686275f, 1f));
			put(91, new Color(0.21176471f, 0.21176471f, 0.21176471f, 1f));
			put(92, new Color(0.3019608f, 0.3019608f, 0.3019608f, 1f));
			put(93, new Color(0.39607844f, 0.39607844f, 0.39607844f, 1f));
			put(94, new Color(0.5058824f, 0.5058824f, 0.5058824f, 1f));
			put(95, new Color(0.62352943f, 0.62352943f, 0.62352943f, 1f));
			put(96, new Color(0.7372549f, 0.7372549f, 0.7372549f, 1f));
			put(97, new Color(0.8862745f, 0.8862745f, 0.8862745f, 1f));
			put(98, new Color(1.0f, 1.0f, 1.0f, 1f));
		}
	};

	public static Color getMircColorAsJavaFXColor(int color) {
		return mircColors.get(color);
	}

	public static Color getMircColorAsJavaAWTColor(int color) {
		return mircColors.get(color);
	}

	public static String getMircColorText(int textColor) {
		return "" + CODE_COLOR + textColor;
	}

	public static String getMircColorText(int backgroundColor, int textColor) {
		return CODE_COLOR + backgroundColor + "," + textColor;
	}

	public static Integer getMircColorCodeFromColor(Color color) {
		return getMircColorCodeFromColor(color, MircColorsStyle._99_COLORS);
	}

	public static Integer getMircColorCodeFromColor(Color color, double hueWeight, double saturationWeight, double valueWeight, double grayPenaltyWeight) {
		return getMircColorCodeFromColor(color, MircColorsStyle._99_COLORS, hueWeight, saturationWeight, valueWeight, grayPenaltyWeight);
	}

	public static Integer getMircColorCodeFromColor(Color color, MircColorsStyle style) {
		return getMircColorCodeFromColor(color, style, 3.0, 1.5, 1.0, 0.4);
	}
	
	public static Integer getMircColorCodeFromColor(Color color, MircColorsStyle style, double hueWeight, double saturationWeight, double valueWeight, double grayPenaltyWeight) {
		double r = color.getRed(), g = color.getGreen(), b = color.getBlue();

		double[] hsv = rgbToHsv(r, g, b);
		double h = hsv[0], s = hsv[1], v = hsv[2];

		double bestDistance = Double.MAX_VALUE;
		int bestValue = -1;

		int minColors = (style == MircColorsStyle.GRAY_SCALE) ? 88 : 0;
		int maxColors = (style == MircColorsStyle.MONOCHROMATIC) ? 2 : (style == MircColorsStyle._16_COLORS) ? 16 : mircColors.size();

		for (int n = minColors; n < maxColors; n++) {
			Color c = mircColors.get(n);

			double r2 = c.getRed(), g2 = c.getGreen(), b2 = c.getBlue();
			double[] hsv2 = rgbToHsv(r2, g2, b2);
			double h2 = hsv2[0], s2 = hsv2[1], v2 = hsv2[2];

			double dh = Math.min(Math.abs(h - h2), 360.0 - Math.abs(h - h2)) / 180.0;
			double ds = s - s2;
			double dv = v - v2;

			double distance = dh * dh * hueWeight + ds * ds * saturationWeight + dv * dv * valueWeight;

			// Penalização de tons pouco saturados
			distance += (1.0 - s2) * grayPenaltyWeight;

			if (distance < bestDistance) {
				bestDistance = distance;
				bestValue = n;
			}
		}

		return bestValue;
	}

	private static double[] rgbToHsv(double r, double g, double b) {
		double max = Math.max(r, Math.max(g, b));
		double min = Math.min(r, Math.min(g, b));
		double delta = max - min;

		double h;
		if (delta == 0) {
			h = 0;
		}
		else if (max == r) {
			h = 60 * (((g - b) / delta) % 6);
		}
		else if (max == g) {
			h = 60 * (((b - r) / delta) + 2);
		}
		else {
			h = 60 * (((r - g) / delta) + 4);
		}
		if (h < 0)
			h += 360;

		double s = (max == 0) ? 0 : delta / max;
		double v = max;

		return new double[] { h, s, v };
	}

	public static String strip(String text) {
		StringBuilder result = new StringBuilder();
		int ignore = 0;
		List<Character> strips = List.of(CODE_BOLD, CODE_COLOR, CODE_ITALIC, CODE_REVERSE, CODE_STRIKETHROUGH, CODE_STRIP, CODE_UNDERLINE);
		for (char c : strips) {
			if (c == CODE_COLOR)
				ignore = ignore == 0 ? 1 : -1;
			else if (ignore != 0) {
				if (ignore == 1 && c == ',')
					ignore = 2;
				else if (!Character.isDigit(c))
					ignore = 0;
			}
			if (ignore == 0 && !strips.contains(c))
				result.append(c);
			else if (ignore == -1)
				ignore = 0;
		}
		return result.toString();
	}

}
