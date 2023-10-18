package globallisteners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

	private static BiConsumer<String, Integer> onKeyTypedEvent = null;
	private static BiConsumer<String, Integer> onKeyRepeatedEvent = null;
	private static BiConsumer<String, Integer> onKeyPressedEvent = null;
	private static BiConsumer<String, Integer> onKeyReleasedEvent = null;
	private static List<Integer> pressedKeys = null;
	private static boolean started = false;
	
	private GlobalKeyListener()
		{ super(); }
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (!isKeyPressed(e.getKeyCode()) && onKeyPressedEvent != null)
			onKeyPressedEvent.accept(NativeKeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
		else if (isKeyPressed(e.getKeyCode()) && onKeyRepeatedEvent != null)
			onKeyPressedEvent.accept(NativeKeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
		if (!pressedKeys.contains(e.getKeyCode()))
			pressedKeys.add(e.getKeyCode());
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		pressedKeys.remove(Integer.valueOf(e.getKeyCode()));
		if (onKeyReleasedEvent != null)
			onKeyReleasedEvent.accept(NativeKeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		if (!isKeyPressed(e.getKeyCode()) && onKeyTypedEvent != null)
			onKeyTypedEvent.accept(NativeKeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
		else if (isKeyPressed(e.getKeyCode()) && onKeyRepeatedEvent != null)
			onKeyPressedEvent.accept(NativeKeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
		if (!pressedKeys.contains(e.getKeyCode()))
			pressedKeys.add(e.getKeyCode());
	}

	public static boolean isKeyPressed(int button)
		{ return pressedKeys.contains(button); }

	public static void startListener() {
		if (started)
			throw new RuntimeException("Global Key Listener is already active");
		try {
			started = true;
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
			pressedKeys = new ArrayList<>();
		}
		catch (Exception e)
			{ throw new RuntimeException("Unable to start the Global Key Listener\n\t" + e.getMessage()); }
	}
	
	private static void checkIfListenerIsRunning() {
		if (!started)
			throw new RuntimeException("Global Key Listener is not active. Call 'startListener()' first.");
	}
	
	public static void setOnKeyClickedEvent(BiConsumer<String, Integer> consumer) {
		checkIfListenerIsRunning();
		onKeyTypedEvent = consumer;
	}
	
	public static void setOnKeyPressedEvent(BiConsumer<String, Integer> consumer) {
		checkIfListenerIsRunning();
		onKeyPressedEvent = consumer;
	}
	
	public static void setOnKeyRepeatedEvent(BiConsumer<String, Integer> consumer) {
		checkIfListenerIsRunning();
		onKeyRepeatedEvent = consumer;
	}
	
	public static void setOnKeyReleasedEvent(BiConsumer<String, Integer> consumer) {
		checkIfListenerIsRunning();
		onKeyReleasedEvent = consumer;
	}
	
}
