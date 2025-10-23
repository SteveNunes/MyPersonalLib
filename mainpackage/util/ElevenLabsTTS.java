package util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import enums.ElevenLabsModel;

public class ElevenLabsTTS {
	private String apiKey;
	private String voiceId;
	private ElevenLabsModel model;
	private ObjectNode voiceSettings;

	private static final String API_BASE = "https://api.elevenlabs.io/v1/text-to-speech/";

	public ElevenLabsTTS(String apiKey) {
		this.apiKey = apiKey;
		this.voiceSettings = new ObjectMapper().createObjectNode();
	}

	public void setVoiceId(String voiceId) {
		this.voiceId = voiceId;
	}

	public void setModelId(ElevenLabsModel model) {
		this.model = model;
	}

	public void setVoiceSettings(Double stability, Double similarityBoost) {
		if (stability != null)
			voiceSettings.put("stability", stability);
		if (similarityBoost != null)
			voiceSettings.put("similarity_boost", similarityBoost);
	}

	public void synthesizeToFile(String text, Path outputFile) throws Exception {
		if (voiceId == null)
			throw new RuntimeException("Voice ID não definido");

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode body = mapper.createObjectNode();
		body.put("text", text);

		if (model != null)
			body.put("model_id", model.toString());

		if (voiceSettings.size() > 0)
			body.set("voice_settings", voiceSettings);

		String url = API_BASE + voiceId;
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(60)).header("xi-api-key", apiKey).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();

		HttpClient client = HttpClient.newBuilder().build();
		HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

		if (response.statusCode() != 200)
			throw new RuntimeException("Erro na API ElevenLabs: HTTP " + response.statusCode() + " — " + new String(response.body()));

		Files.write(outputFile, response.body());
	}

}
