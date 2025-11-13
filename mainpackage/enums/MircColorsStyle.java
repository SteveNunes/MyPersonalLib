package enums;

public enum MircColorsStyle {
	
	MONOCHROMATIC("Monochromatic"),
	GRAY_SCALE("Gray scale"),
	_16_COLORS("16 Colors"),
	_99_COLORS("99 Colors");
	
	private String name;
	
	private MircColorsStyle(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
