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

import util.Misc;

public class GlobalMouseListener implements NativeMouseInputListener {

	private static Consumer<NativeMouseEvent> onMouseClickedEvent = null;
	private static Consumer<NativeMouseEvent> onMousePressedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseReleasedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseMovedEvent = null;
	private static Consumer<NativeMouseEvent> onMouseDraggedEvent = null;
	private static List<Integer> pressedButtons = null;
	private static NativeMouseInputListener nativeMouseInputListener = null;
	private static NativeMouseMotionListener nativeMouseMotionListener = null;
	private static NativeMouseWheelListener nativeMouseWheelListener = null;

	private GlobalMouseListener() {
		super();
	}

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
		if (onMouseDraggedEvent != null)
			onMouseDraggedEvent.accept(e);
	}

	public static boolean isButtonPressed(int button) {
		return pressedButtons.contains(button);
	}

	private static void startListener() {
		if (nativeMouseInputListener == null) {
			try {
				nativeMouseInputListener = new GlobalMouseListener();
				nativeMouseMotionListener = new GlobalMouseListener();
				nativeMouseWheelListener = new MyMouseWheelListener();
				if (!GlobalKeyListener.nativeHookStarted) {
					GlobalScreen.registerNativeHook();
					GlobalKeyListener.nativeHookStarted = true;
				}
				GlobalScreen.addNativeMouseListener(nativeMouseInputListener);
				GlobalScreen.addNativeMouseMotionListener(nativeMouseMotionListener);
				GlobalScreen.addNativeMouseWheelListener(nativeMouseWheelListener);
				
				Misc.addShutdownEvent(GlobalMouseListener::stopListener);
				pressedButtons = new ArrayList<>();
			}
			catch (Exception e) {
				throw new RuntimeException("Unable to start the Global Mouse Listener\n\t" + e.getMessage());
			}
		}
	}

	private static void stopListener() {
		if (nativeMouseInputListener != null) {
			try {
				GlobalScreen.removeNativeMouseListener(nativeMouseInputListener);
				GlobalScreen.removeNativeMouseMotionListener(nativeMouseMotionListener);
				GlobalScreen.removeNativeMouseWheelListener(nativeMouseWheelListener);
				if (GlobalKeyListener.nativeHookStarted) {
					GlobalScreen.unregisterNativeHook();
					GlobalKeyListener.nativeHookStarted = false;
				}
				nativeMouseInputListener = null;
				nativeMouseMotionListener = null;
				nativeMouseWheelListener = null;
			}
			catch (NativeHookException e) {}
		}
	}

	public static Consumer<NativeMouseEvent> getOnMouseClickedEvent() {
		return onMouseClickedEvent;
	}

	public static Consumer<NativeMouseEvent> getOnMousePressedEvent() {
		return onMousePressedEvent;
	}

	public static Consumer<NativeMouseEvent> getOnMouseReleasedEvent() {
		return onMouseReleasedEvent;
	}

	public static Consumer<NativeMouseEvent> getOnMouseMovedEvent() {
		return onMouseMovedEvent;
	}

	public static void setOnMouseClickedEvent(Consumer<NativeMouseEvent> consumer) {
		startListener();
		onMouseClickedEvent = consumer;
	}

	public static void setOnMousePressedEvent(Consumer<NativeMouseEvent> consumer) {
		startListener();
		onMousePressedEvent = consumer;
	}

	public static void setOnMouseReleasedEvent(Consumer<NativeMouseEvent> consumer) {
		startListener();
		onMouseReleasedEvent = consumer;
	}

	public static void setOnMouseMoveEvent(Consumer<NativeMouseEvent> consumer) {
		startListener();
		onMouseMovedEvent = consumer;
	}

	public static void setOnMouseDraggedEvent(Consumer<NativeMouseEvent> consumer) {
		startListener();
		onMouseDraggedEvent = consumer;
	}

	public static void setOnMouseWheelEvent(Consumer<NativeMouseWheelEvent> consumer) {
		startListener();
		MyMouseWheelListener.setOnMouseWheelEvent(consumer);
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
		mouseWheelEvent = consumer;
	}

}
