package globallisteners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

public class GlobalMouseListener implements NativeMouseInputListener {
	
	private static Consumer<NativeMouseEvent> onMouseClickedEvent = null;
	private static Consumer<NativeMouseEvent> onMousePressedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseReleasedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseMovedEvent = null;
	private static Consumer<NativeMouseEvent> mouseDraggedEvent = null;
	private static List<Integer> pressedButtons = null;
	private static NativeMouseInputListener nativeMouseInputListener = new GlobalMouseListener();
	private static NativeMouseMotionListener nativeMouseMotionListener = new GlobalMouseListener();
	private static NativeMouseWheelListener nativeMouseWheelListener = new MyMouseWheelListener();
	static boolean started = false;
	
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
			if (!GlobalKeyListener.started)
				GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseListener(nativeMouseInputListener);
			GlobalScreen.addNativeMouseMotionListener(nativeMouseMotionListener);
			GlobalScreen.addNativeMouseWheelListener(nativeMouseWheelListener);
			pressedButtons = new ArrayList<>();
		}
		catch (Exception e)
			{ throw new RuntimeException("Unable to start the Global Mouse Listener\n\t" + e.getMessage()); }
	}
	
	public static void stopListener() {
		try {
			GlobalScreen.removeNativeMouseListener(nativeMouseInputListener);
			GlobalScreen.removeNativeMouseMotionListener(nativeMouseMotionListener);
			GlobalScreen.removeNativeMouseWheelListener(nativeMouseWheelListener);
			if (!GlobalKeyListener.started)
				GlobalScreen.unregisterNativeHook();
			started = false;
		}
		catch (NativeHookException e) {}
	}
	
	static void checkIfListenerIsRunning() {
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
	
	public static void setOnMouseWheelEvent(Consumer<NativeMouseWheelEvent> consumer)
		{ MyMouseWheelListener.setOnMouseWheelEvent(consumer); }
	
	public static void main(String[] args) {
		startListener();
	}
	
}

class MyMouseWheelListener implements NativeMouseWheelListener {
	
	private static Consumer<NativeMouseWheelEvent> mouseWheelEvent = null;

	@Override
  public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
		if (mouseWheelEvent != null)
			mouseWheelEvent.accept(e);
  }
  
	public static void setOnMouseWheelEvent(Consumer<NativeMouseWheelEvent> consumer) {
		GlobalMouseListener.checkIfListenerIsRunning();
		mouseWheelEvent = consumer;
	}
  
}
