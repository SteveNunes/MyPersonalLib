package globallisteners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

public class GlobalMouseListener implements NativeMouseInputListener {
	
	private static Consumer<NativeMouseEvent> onMouseClickedEvent = null;
	private static Consumer<NativeMouseEvent> onMousePressedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseReleasedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseMovedEvent = null;
	private static Consumer<NativeMouseEvent> mouseDraggedEvent = null;
	private static List<Integer> pressedButtons = null;
	private static boolean started = false;
	
	private GlobalMouseListener()
		{ super(); }
	
	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		if (!pressedButtons.contains(e.getButton()))
			pressedButtons.add(e.getButton());
		if (onMouseClickedEvent != null)
			onMouseClickedEvent.accept(e);
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		if (!pressedButtons.contains(e.getButton()))
			pressedButtons.add(e.getButton());
		if (onMousePressedEvent != null)
			onMousePressedEvent.accept(e);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		pressedButtons.remove(Integer.valueOf(e.getButton()));
		if (onMouseReleasedEvent != null)
			onMouseReleasedEvent.accept(e);
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		if (onMouseMovedEvent != null)
			onMouseMovedEvent.accept(e);
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
		if (mouseDraggedEvent != null)
			mouseDraggedEvent.accept(e);
	}
	
	public static boolean isButtonPressed(int button)
		{ return pressedButtons.contains(button); }

	public static void startListener() {
		if (started)
			throw new RuntimeException("Global Mouse Listener is already active");
		try {
			started = true;
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseListener(new GlobalMouseListener());
			GlobalScreen.addNativeMouseMotionListener(new GlobalMouseListener());
			pressedButtons = new ArrayList<>();
		}
		catch (Exception e)
			{ throw new RuntimeException("Unable to start the Global Mouse Listener\n\t" + e.getMessage()); }
	}
	
	private static void checkIfListenerIsRunning() {
		if (!started)
			throw new RuntimeException("Global Mouse Listener is not active. Call 'startListener()' first.");
	}
	
	public static void setOnMouseClickedEvent(Consumer<NativeMouseEvent> consumer) {
		checkIfListenerIsRunning();
		onMouseClickedEvent = consumer;
	}
	
	public static void setOnMousePressedEvent(Consumer<NativeMouseEvent> consumer) {
		checkIfListenerIsRunning();
		onMousePressedEvent = consumer;
	}
	
	public static void setOnMouseReleasedEvent(Consumer<NativeMouseEvent> consumer) {
		checkIfListenerIsRunning();
		onMouseReleasedEvent = consumer;
	}
	
	public static void setOnMouseMoveEvent(Consumer<NativeMouseEvent> consumer) {
		checkIfListenerIsRunning();
		onMouseMovedEvent = consumer;
	}
	
}
