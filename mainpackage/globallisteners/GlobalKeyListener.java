package globallisteners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import util.Misc;

public class GlobalKeyListener implements NativeKeyListener {

	private static Consumer<NativeKeyEvent> onKeyTypedEvent = null;
	private static Consumer<NativeKeyEvent> onKeyRepeatedEvent = null;
	private static Consumer<NativeKeyEvent> onKeyTypedRepeatedEvent = null;
	private static Consumer<NativeKeyEvent> onKeyPressedEvent = null;
	private static Consumer<NativeKeyEvent> onKeyReleasedEvent = null;
	private static List<Integer> pressedKeys = null;
	private static NativeKeyListener nativeKeyListener = null;
	public static boolean nativeHookStarted = false;

	private GlobalKeyListener() {
		super();
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent n) {
		startListener();
		if (!isKeyPressed(n.getRawCode()) && onKeyPressedEvent != null)
			onKeyPressedEvent.accept(n);
		else if (onKeyRepeatedEvent != null)
			onKeyRepeatedEvent.accept(n);
		if (!pressedKeys.contains(n.getKeyCode()))
			pressedKeys.add(n.getKeyCode());
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent n) {
		startListener();
		pressedKeys.remove(Integer.valueOf(n.getKeyCode()));
		if (onKeyReleasedEvent != null)
			onKeyReleasedEvent.accept(n);
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent n) {
		startListener();
		if (!isKeyPressed(n.getRawCode()) && onKeyTypedEvent != null)
			onKeyTypedEvent.accept(n);
		else if (onKeyTypedRepeatedEvent != null)
			onKeyTypedRepeatedEvent.accept(n);
		if (!pressedKeys.contains(n.getKeyCode()))
			pressedKeys.add(n.getKeyCode());
	}

	public static boolean isKeyPressed(int keyCode) {
		startListener();
		return pressedKeys.contains(keyCode);
	}

	private static void startListener() {
		if (nativeKeyListener == null) {
			try {
				nativeKeyListener = new GlobalKeyListener();
				if (!nativeHookStarted) {
					GlobalScreen.registerNativeHook();
					nativeHookStarted = true;
				}
				GlobalScreen.addNativeKeyListener(nativeKeyListener);
				pressedKeys = new ArrayList<>();
				Misc.addShutdownEvent(GlobalKeyListener::stopListener);
			}
			catch (Exception e) {
				throw new RuntimeException("Unable to start the Global Key Listener\n\t" + e.getMessage());
			}
		}
	}

	private static void stopListener() {
		if (nativeKeyListener != null) {
			try {
				GlobalScreen.removeNativeKeyListener(nativeKeyListener);
				if (nativeHookStarted) {
					GlobalScreen.unregisterNativeHook();
					nativeHookStarted = false;
				}
				nativeKeyListener = null;
			}
			catch (NativeHookException e) {}
		}
	}

	public static Consumer<NativeKeyEvent> getOnKeyTypedEvent() {
		return onKeyTypedEvent;
	}

	public static Consumer<NativeKeyEvent> getOnKeyRepeatedEvent() {
		return onKeyRepeatedEvent;
	}

	public static Consumer<NativeKeyEvent> getOnKeyTypedRepeatedEvent() {
		return onKeyTypedRepeatedEvent;
	}

	public static Consumer<NativeKeyEvent> getOnKeyPressedEvent() {
		return onKeyPressedEvent;
	}

	public static Consumer<NativeKeyEvent> getOnKeyReleasedEvent() {
		return onKeyReleasedEvent;
	}

	public static void setOnKeyTypedEvent(Consumer<NativeKeyEvent> consumer) {
		startListener();
		onKeyTypedEvent = consumer;
	}

	public static void setOnKeyPressedEvent(Consumer<NativeKeyEvent> consumer) {
		startListener();
		onKeyPressedEvent = consumer;
	}

	public static void setOnKeyRepeatedEvent(Consumer<NativeKeyEvent> consumer) {
		startListener();
		onKeyRepeatedEvent = consumer;
	}

	public static void setOnKeyTypedRepeatedEvent(Consumer<NativeKeyEvent> consumer) {
		startListener();
		onKeyTypedRepeatedEvent = consumer;
	}

	public static void setOnKeyReleasedEvent(Consumer<NativeKeyEvent> consumer) {
		startListener();
		onKeyReleasedEvent = consumer;
	}

}
