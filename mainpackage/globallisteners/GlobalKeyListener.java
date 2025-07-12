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

	private GlobalKeyListener()
		{ super(); }
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent n) {
		if (!isKeyPressed(n.getRawCode()) && onKeyPressedEvent != null)
			onKeyPressedEvent.accept(n);
		else if (onKeyRepeatedEvent != null)
			onKeyRepeatedEvent.accept(n);
		if (!pressedKeys.contains(n.getRawCode()))
			pressedKeys.add(n.getRawCode());
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent n) {
		pressedKeys.remove(Integer.valueOf(n.getRawCode()));
		if (onKeyReleasedEvent != null)
			onKeyReleasedEvent.accept(n);
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent n) {
		if (!isKeyPressed(n.getRawCode()) && onKeyTypedEvent != null)
			onKeyTypedEvent.accept(n);
		else if (onKeyTypedRepeatedEvent != null)
			onKeyTypedRepeatedEvent.accept(n);
		if (!pressedKeys.contains(n.getRawCode()))
			pressedKeys.add(n.getRawCode());
	}

	public static boolean isKeyPressed(int rawCode)
		{ return pressedKeys.contains(rawCode); }

	public static void startListener() {
		if (nativeKeyListener != null)
			throw new RuntimeException("Global Key Listener is already active");
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
		catch (Exception e)
			{ throw new RuntimeException("Unable to start the Global Key Listener\n\t" + e.getMessage()); }
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
	
	private static void checkIfListenerIsRunning() {
		if (nativeKeyListener == null)
			throw new RuntimeException("Global Key Listener is not active. Call 'startListener()' first.");
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
		checkIfListenerIsRunning();
		onKeyTypedEvent = consumer;
	}
	
	public static void setOnKeyPressedEvent(Consumer<NativeKeyEvent> consumer) {
		checkIfListenerIsRunning();
		onKeyPressedEvent = consumer;
	}
	
	public static void setOnKeyRepeatedEvent(Consumer<NativeKeyEvent> consumer) {
		checkIfListenerIsRunning();
		onKeyRepeatedEvent = consumer;
	}
	
	public static void setOnKeyTypedRepeatedEvent(Consumer<NativeKeyEvent> consumer) {
		checkIfListenerIsRunning();
		onKeyTypedRepeatedEvent = consumer;
	}
	
	public static void setOnKeyReleasedEvent(Consumer<NativeKeyEvent> consumer) {
		checkIfListenerIsRunning();
		onKeyReleasedEvent = consumer;
	}
	
}
