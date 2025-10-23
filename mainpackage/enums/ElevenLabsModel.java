package enums;

public enum ElevenLabsModel {
	
	ELEVEN_V3("eleven_v3"),
	ELEVEN_TURBO_V2("eleven_turbo_v2"),
	ELEVEN_TURBO_V3("eleven_turbo_v3"),
	ELEVEN_FLASH_V2("eleven_flash_v2"),
	ELEVEN_FLASH_V2_5("eleven_flash_v2_5"),
	ELEVEN_MULTILINGUAL_V2("eleven_multilingual_v2"),
	ELEVEN_MULTILINGUAL_TTV_V2("eleven_multilingual_ttv_v2"),
	ELEVEN_ENGLISH_STS_V2("eleven_english_sts_v2"),
	ELEVEN_MONOLINGUAL_V1("eleven_monolingual_v1");
	
	private String model;
	
	private ElevenLabsModel(String model) {
		this.model = model;
	}

	@Override
	public String toString() {
		return model;
	}
	
}
