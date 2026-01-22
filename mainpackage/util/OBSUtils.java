package util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OBSUtils {

	private WebSocketClient client;
	private final String password;
	private boolean debug;
	
	public OBSUtils(String password) throws Exception {
		this(password, false);
	}
	
	public OBSUtils(String password, boolean debug) throws Exception {
		this("localhost", 4455, password, debug);
	}
	
	public OBSUtils(String ip, int port, String password, boolean debug) throws Exception {
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
				if (json.has("op") && json.get("op").getAsInt() == 0)
					auth(json.getAsJsonObject("d"));
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
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
	}
	
	public void close() {
		if (client != null && !client.isClosed()) {
			client.close();
			client = null;
		}
	}
	
	private void auth(JsonObject data) {
		try {
			JsonObject authObj = data.get("authentication").getAsJsonObject();
			String challenge = authObj.get("challenge").getAsString();
			String salt = authObj.get("salt").getAsString();
			String secret = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((password + salt).getBytes(StandardCharsets.UTF_8)));
			String authResponse = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((secret + challenge).getBytes(StandardCharsets.UTF_8)));
			JsonObject request = new JsonObject();
			request.addProperty("op", 1);
			JsonObject d = new JsonObject();
			d.addProperty("rpcVersion", 1);
			d.addProperty("authentication", authResponse);
			request.add("d", d);
			client.send(request.toString());
			if (debug)
				System.out.println("[OBS WebSocket] Autenticação enviada ao OBS.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeScene(String sceneName) {
		JsonObject req = new JsonObject();
		req.addProperty("op", 6);
		JsonObject d = new JsonObject();
		d.addProperty("requestType", "SetCurrentProgramScene");
		d.addProperty("requestId", UUID.randomUUID().toString());
		JsonObject args = new JsonObject();
		args.addProperty("sceneName", sceneName);
		d.add("requestData", args);
		req.add("d", d);
		client.send(req.toString());
		if (debug)
			System.out.println("[OBS WebSocket] Cena alterada para: " + sceneName);
	}

}
