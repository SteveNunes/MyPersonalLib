package gameutil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public abstract class KeyHandler {

	private static int repeatDelay = -1;
	private static Map<KeyCode, Integer> repeatKeys = new HashMap<>();
	private static Consumer<KeyCode> onRepeatKeyCall;

	public static void setRepatKeyDelay(int delay) {
		repeatDelay = delay;
	}

	public static boolean isKeyDown(KeyCode keyCode) {
		return repeatKeys.containsKey(keyCode);
	}

	public static void setOnKeyPressEvent(Scene scene, Consumer<KeyCode> consumer) {
		scene.setOnKeyPressed(keyEvent -> {
			repeatKeys.put(keyEvent.getCode(), 1);
			consumer.accept(keyEvent.getCode());
		});
	}

	public static void setOnKeyHoldEvent(Consumer<KeyCode> consumer) {
		onRepeatKeyCall = consumer;
	}

	public static void setOnKeyReleaseEvent(Scene scene, Consumer<KeyCode> consumer) {
		scene.setOnKeyReleased(keyEvent -> {
			repeatKeys.remove(keyEvent.getCode());
			consumer.accept(keyEvent.getCode());
		});
	}

	public static KeyCode[] getAllHoldKeys() {
		return (KeyCode[]) repeatKeys.keySet().toArray();
	}

	public static int getNumberOfHoldKeys() {
		return repeatKeys.size();
	}

	/** Call it on your main loop every frame */
	public static void holdKeyPoll() {
		if (repeatDelay == -1)
			return;
		for (KeyCode e : repeatKeys.keySet()) {
			if (onRepeatKeyCall != null && repeatKeys.get(e) >= repeatDelay)
				onRepeatKeyCall.accept(e);
			repeatKeys.put(e, repeatKeys.get(e) + 1);
		}
	}

}
