package enums;

public enum TTSVoice {

	MARIA(0),
	LETICIA(1),
	FELIPE(2),
	FERNANDA(3),
	LUCIANA(4),
	CATARINA(5),
	JOANA(6),
	JOAQUIM(7);
	
	private int value;
	private static String[][] voices = {
			{"Microsoft Maria Desktop", "Maria (BR)"},
			{"Letícia-F123", "Letícia (BR)"},
			{"VE_Brazilian_Portuguese_Fernanda_22kHz", "Fernanda (BR)"},
			{"VE_Brazilian_Portuguese_Luciana_22kHz", "Luciana (BR)"},
			{"VE_Brazilian_Portuguese_Felipe_22kHz", "Felipe (BR)"},
			{"VE_Portuguese_Catarina_22kHz", "Catarina (PT)"},
			{"VE_Portuguese_Joana_22kHz", "Joana (PT)"},
			{"VE_Portuguese_Joaquim_22kHz", "Joaquim (PT)"}
	};
	
	private TTSVoice(int value) {
		this.value = value;
	}
	
	public String getSystemName() {
		return voices[value][0];
	}
	
	public String getName() {
		return voices[value][1];
	}
	
}
