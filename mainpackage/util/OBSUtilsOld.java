package util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OBSUtilsOld {

	private WebSocketClient client;
	private final String password;
	private boolean debug;
	private volatile boolean identified;

	private final Map<String, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();
	private final Map<String, Consumer<String>> mediaEndListeners = new ConcurrentHashMap<>();

	public OBSUtilsOld(String password) throws Exception {
		this(password, false);
	}

	public OBSUtilsOld(String password, boolean debug) throws Exception {
		this("localhost", 4455, password, debug);
	}

	public OBSUtilsOld(String ip, int port, String password, boolean debug) throws Exception {
		this.password = password;
		this.debug = debug;
		connect(ip, port);
	}

	private void connect(String ip, int port) throws Exception {
		URI uri = new URI("ws://" + ip + ":" + port);

		client = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake handshake) {
				if (debug)
					System.out.println("[OBS WebSocket] Conectado.");
			}

			@Override
			public void onMessage(String message) {
				if (debug)
					System.out.println("[OBS WebSocket] Data Received: " + message);

				JsonObject json = JsonParser.parseString(message).getAsJsonObject();

				if (!json.has("op"))
					return;

				int op = json.get("op").getAsInt();
				JsonObject d = json.has("d") && json.get("d").isJsonObject() ? json.getAsJsonObject("d") : new JsonObject();

				switch (op) {
					case 0:
						auth(d);
						break;

					case 2:
						identified = true;
						if (debug)
							System.out.println("[OBS WebSocket] Sessão identificada com sucesso.");
						break;

					case 5:
						handleEvent(d);
						break;

					case 7:
						handleRequestResponse(d);
						break;

					default:
						break;
				}
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				identified = false;

				for (CompletableFuture<JsonObject> future : pendingRequests.values())
					future.completeExceptionally(new IllegalStateException("OBS WebSocket desconectado: " + reason));

				pendingRequests.clear();
				mediaEndListeners.clear();

				if (debug)
					System.out.println("[OBS WebSocket] Desconectado. Motivo: " + reason);
			}

			@Override
			public void onError(Exception ex) {
				ex.printStackTrace();
			}
		};

		client.connectBlocking();
		Misc.addShutdownEvent(() -> close());

		waitUntilIdentified(10_000);
	}

	public void close() {
		if (client != null && !client.isClosed()) {
			client.close();
			client = null;
		}
	}

	private void auth(JsonObject data) {
		try {
			JsonObject request = new JsonObject();
			request.addProperty("op", 1);

			JsonObject d = new JsonObject();
			d.addProperty("rpcVersion", 1);

			if (data.has("authentication") && data.get("authentication").isJsonObject()) {
				JsonObject authObj = data.getAsJsonObject("authentication");
				String challenge = authObj.get("challenge").getAsString();
				String salt = authObj.get("salt").getAsString();

				String secret = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((password + salt).getBytes(StandardCharsets.UTF_8)));

				String authResponse = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((secret + challenge).getBytes(StandardCharsets.UTF_8)));

				d.addProperty("authentication", authResponse);

				if (debug)
					System.out.println("[OBS WebSocket] Autenticação enviada ao OBS.");
			}
			else if (debug)
				System.out.println("[OBS WebSocket] OBS sem autenticação. Identify enviado sem senha.");

			request.add("d", d);
			client.send(request.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleRequestResponse(JsonObject d) {
		String requestId = d.has("requestId") ? d.get("requestId").getAsString() : null;
		if (requestId == null)
			return;

		CompletableFuture<JsonObject> future = pendingRequests.remove(requestId);
		if (future == null)
			return;

		JsonObject status = d.getAsJsonObject("requestStatus");
		boolean result = status.has("result") && status.get("result").getAsBoolean();

		if (result) {
			JsonObject responseData = d.has("responseData") && d.get("responseData").isJsonObject() ? d.getAsJsonObject("responseData") : new JsonObject();
			future.complete(responseData);
		}
		else {
			int code = status.has("code") ? status.get("code").getAsInt() : -1;
			String comment = status.has("comment") ? status.get("comment").getAsString() : "Sem detalhes";
			future.completeExceptionally(new RuntimeException("OBS request falhou. code=" + code + ", comment=" + comment));
		}
	}

	private void handleEvent(JsonObject d) {
		if (!d.has("eventType"))
			return;

		String eventType = d.get("eventType").getAsString();

		if (!"MediaInputPlaybackEnded".equals(eventType))
			return;

		JsonObject eventData = d.has("eventData") && d.get("eventData").isJsonObject() ? d.getAsJsonObject("eventData") : new JsonObject();

		if (!eventData.has("inputName"))
			return;

		String inputName = eventData.get("inputName").getAsString();
		Consumer<String> callback = mediaEndListeners.get(inputName);

		if (callback == null)
			return;

		new Thread(() -> {
			try {
				if (debug)
					System.out.println("[OBS WebSocket] Midia finalizada em '" + inputName + "'");

				callback.accept(inputName);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}, "OBS-MediaEnded-" + inputName).start();
	}

	private void waitUntilIdentified(long timeoutMillis) throws InterruptedException {
		long start = System.currentTimeMillis();

		while (!identified) {
			if (System.currentTimeMillis() - start > timeoutMillis)
				throw new IllegalStateException("Tempo esgotado aguardando identificação no OBS WebSocket.");
			Thread.sleep(25);
		}
	}

	private JsonObject sendRequest(String requestType, JsonObject requestData) {
		try {
			if (client == null || client.isClosed())
				throw new IllegalStateException("Cliente OBS WebSocket não está conectado.");

			waitUntilIdentified(10_000);

			String requestId = UUID.randomUUID().toString();
			CompletableFuture<JsonObject> future = new CompletableFuture<>();
			pendingRequests.put(requestId, future);

			JsonObject req = new JsonObject();
			req.addProperty("op", 6);

			JsonObject d = new JsonObject();
			d.addProperty("requestType", requestType);
			d.addProperty("requestId", requestId);
			d.add("requestData", requestData != null ? requestData : new JsonObject());

			req.add("d", d);
			client.send(req.toString());

			return future.get(10, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			throw new RuntimeException("Erro ao enviar request ao OBS: " + requestType, e);
		}
	}

	public void setOnEndMediaEvent(String inputName, Consumer<String> event) {
		if (inputName == null || inputName.isBlank())
			throw new IllegalArgumentException("inputName não pode ser null ou vazio.");
		if (event == null)
			throw new IllegalArgumentException("event não pode ser null.");

		mediaEndListeners.put(inputName, event);
	}

	public void removeOnEndMediaEvent(String inputName) {
		if (inputName != null)
			mediaEndListeners.remove(inputName);
	}

	public void changeScene(String sceneName) {
		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		sendRequest("SetCurrentProgramScene", args);

		if (debug)
			System.out.println("[OBS WebSocket] Cena alterada para: " + sceneName);
	}

	private int getSceneItemId(String sceneName, String sourceName) {
		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		args.addProperty("sourceName", sourceName);

		JsonObject response = sendRequest("GetSceneItemId", args);

		if (!response.has("sceneItemId"))
			throw new IllegalStateException("OBS não retornou sceneItemId para source '" + sourceName + "' na cena '" + sceneName + "'.");

		return response.get("sceneItemId").getAsInt();
	}

	public void setSceneItemVisible(String sceneName, String sourceName, boolean visible) {
		int sceneItemId = getSceneItemId(sceneName, sourceName);

		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		args.addProperty("sceneItemId", sceneItemId);
		args.addProperty("sceneItemEnabled", visible);

		sendRequest("SetSceneItemEnabled", args);

		if (debug)
			System.out.println("[OBS WebSocket] Item '" + sourceName + "' na cena '" + sceneName + "' -> visible=" + visible);
	}

	public boolean isSceneItemVisible(String sceneName, String sourceName) {
		int sceneItemId = getSceneItemId(sceneName, sourceName);
		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		args.addProperty("sceneItemId", sceneItemId);
		JsonObject response = sendRequest("GetSceneItemEnabled", args);
		if (!response.has("sceneItemEnabled"))
			throw new IllegalStateException("OBS não retornou sceneItemEnabled para source '" + sourceName + "' na cena '" + sceneName + "'.");
		return response.get("sceneItemEnabled").getAsBoolean();
	}

	public void showSceneItem(String sceneName, String sourceName) {
		setSceneItemVisible(sceneName, sourceName, true);
	}

	public void hideSceneItem(String sceneName, String sourceName) {
		setSceneItemVisible(sceneName, sourceName, false);
	}

	public void setMediaInputFile(String inputName, String filePath) {
		setMediaInputFile(inputName, filePath, true);
	}

	public void setMediaInputFile(String inputName, String filePath, boolean restartPlayback) {
		JsonObject inputSettings = new JsonObject();
		inputSettings.addProperty("local_file", filePath);
		inputSettings.addProperty("is_local_file", true);

		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);
		args.add("inputSettings", inputSettings);
		args.addProperty("overlay", true);

		sendRequest("SetInputSettings", args);

		if (restartPlayback)
			restartMediaInput(inputName);

		if (debug)
			System.out.println("[OBS WebSocket] Midia do input '" + inputName + "' alterada para: " + filePath);
	}

	public String getMediaInputFile(String inputName) {
		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);

		JsonObject response = sendRequest("GetInputSettings", args);

		if (!response.has("inputSettings") || !response.get("inputSettings").isJsonObject())
			return null;

		JsonObject inputSettings = response.getAsJsonObject("inputSettings");
		return inputSettings.has("local_file") ? inputSettings.get("local_file").getAsString() : null;
	}

	public void setMediaInputLooping(String inputName, boolean looping) {
		JsonObject inputSettings = new JsonObject();
		inputSettings.addProperty("looping", looping);

		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);
		args.add("inputSettings", inputSettings);
		args.addProperty("overlay", true);

		sendRequest("SetInputSettings", args);

		if (debug)
			System.out.println("[OBS WebSocket] Loop do input '" + inputName + "' -> " + looping);
	}

	public boolean isMediaInputLooping(String inputName) {
		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);

		JsonObject response = sendRequest("GetInputSettings", args);

		if (!response.has("inputSettings") || !response.get("inputSettings").isJsonObject())
			return false;

		JsonObject inputSettings = response.getAsJsonObject("inputSettings");
		return inputSettings.has("looping") && inputSettings.get("looping").getAsBoolean();
	}

	public void restartMediaInput(String inputName) {
		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);
		args.addProperty("mediaAction", "OBS_WEBSOCKET_MEDIA_INPUT_ACTION_RESTART");

		sendRequest("TriggerMediaInputAction", args);

		if (debug)
			System.out.println("[OBS WebSocket] Reinício de mídia disparado para o input: " + inputName);
	}

	public void stopMediaInput(String inputName) {
		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);
		args.addProperty("mediaAction", "OBS_WEBSOCKET_MEDIA_INPUT_ACTION_STOP");

		sendRequest("TriggerMediaInputAction", args);

		if (debug)
			System.out.println("[OBS WebSocket] Stop de mídia disparado para o input: " + inputName);
	}

	public void pauseMediaInput(String inputName) {
		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);
		args.addProperty("mediaAction", "OBS_WEBSOCKET_MEDIA_INPUT_ACTION_PAUSE");

		sendRequest("TriggerMediaInputAction", args);

		if (debug)
			System.out.println("[OBS WebSocket] Pause de mídia disparado para o input: " + inputName);
	}

	public void playMediaInput(String inputName) {
		JsonObject args = new JsonObject();
		args.addProperty("inputName", inputName);
		args.addProperty("mediaAction", "OBS_WEBSOCKET_MEDIA_INPUT_ACTION_PLAY");

		sendRequest("TriggerMediaInputAction", args);

		if (debug)
			System.out.println("[OBS WebSocket] Play de mídia disparado para o input: " + inputName);
	}

	public void setSceneItemPosition(String sceneName, String sourceName, double x, double y) {
		int sceneItemId = getSceneItemId(sceneName, sourceName);

		JsonObject transform = new JsonObject();
		transform.addProperty("positionX", x);
		transform.addProperty("positionY", y);

		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		args.addProperty("sceneItemId", sceneItemId);
		args.add("sceneItemTransform", transform);

		sendRequest("SetSceneItemTransform", args);

		if (debug)
			System.out.println("[OBS WebSocket] Posição de '" + sourceName + "' -> X=" + x + ", Y=" + y);
	}

	public void setSceneItemSize(String sceneName, String sourceName, double width, double height) {
		int sceneItemId = getSceneItemId(sceneName, sourceName);

		JsonObject transform = new JsonObject();
		transform.addProperty("boundsType", "OBS_BOUNDS_STRETCH");
		transform.addProperty("boundsWidth", width);
		transform.addProperty("boundsHeight", height);

		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		args.addProperty("sceneItemId", sceneItemId);
		args.add("sceneItemTransform", transform);

		sendRequest("SetSceneItemTransform", args);

		if (debug)
			System.out.println("[OBS WebSocket] Tamanho de '" + sourceName + "' -> W=" + width + ", H=" + height);
	}

	public void setSceneItemBounds(String sceneName, String sourceName, double x, double y, double width, double height) {
		int sceneItemId = getSceneItemId(sceneName, sourceName);

		JsonObject transform = new JsonObject();
		transform.addProperty("positionX", x);
		transform.addProperty("positionY", y);
		transform.addProperty("boundsType", "OBS_BOUNDS_STRETCH");
		transform.addProperty("boundsWidth", width);
		transform.addProperty("boundsHeight", height);

		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		args.addProperty("sceneItemId", sceneItemId);
		args.add("sceneItemTransform", transform);

		sendRequest("SetSceneItemTransform", args);

		if (debug)
			System.out.println("[OBS WebSocket] Bounds de '" + sourceName + "' -> X=" + x + ", Y=" + y + ", W=" + width + ", H=" + height);
	}

}