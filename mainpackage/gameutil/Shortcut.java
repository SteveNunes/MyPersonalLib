package gameutil;

import java.util.Objects;
import java.util.function.Consumer;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import globallisteners.GlobalKeyListener;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Shortcut {
	
	private int keyRaw;
	private int keyCode;
	private int keyModifiers;
	private int keyLocation;
	private boolean isShiftPressed;
	private boolean isCtrlPressed;
	private boolean isAltPressed;
	
	public Shortcut()
		{ this(0, 0, 0, 0); }
	
	public Shortcut(Shortcut copyShortcut)
		{ this(copyShortcut.getKeyRaw(), copyShortcut.getKeyCode(), copyShortcut.getKeyModifiers(), copyShortcut.getKeyLocation()); }

	public Shortcut(int keyRaw, int keyCode, int keyModifiers, int keyLocation) {
		this.keyRaw = keyRaw; 
		this.keyCode = keyCode; 
		this.keyModifiers = keyModifiers; 
		this.keyLocation = keyLocation; 
	}
	
	public boolean isSameKeyCombination(NativeKeyEvent e) {
		return getKeyRaw() == e.getRawCode() &&  getKeyCode() == e.getKeyCode() &&
				 	 getKeyModifiers() == e.getModifiers() && getKeyLocation() == e.getKeyLocation();
	}
	
	public void setShiftPressed(boolean isShiftPressed) {
		this.isShiftPressed = isShiftPressed;
	}

	public void setCtrlPressed(boolean isCtrlPressed) {
		this.isCtrlPressed = isCtrlPressed;
	}

	public void setAltPressed(boolean isAltPressed) {
		this.isAltPressed = isAltPressed;
	}

	public boolean isShiftPressed() {
		return isShiftPressed;
	}

	public boolean isCtrlPressed() {
		return isCtrlPressed;
	}

	public boolean isAltPressed() {
		return isAltPressed;
	}

	public int getKeyModifiers()
		{ return keyModifiers; }

	public void setKeyModifiers(int keyModifiers)
		{ this.keyModifiers = keyModifiers; }

	public int getKeyLocation()
		{ return keyLocation; }
	
	public void setKeyLocation(int keyLocation)
		{ this.keyLocation = keyLocation; }

	public int getKeyCode()
		{ return keyCode; }

	public int getKeyRaw()
		{ return keyRaw; }

	public void setRawCode(int keyRaw)
		{ this.keyRaw = keyRaw; }

	public void setKeyCode(int keyCode)
		{ this.keyCode = keyCode; }

	public String getKeyText() {
		if (keyCode == 0 && keyCode == 0 && keyCode == 0 && keyCode == 0)
			return "Indefinido";
		String keys = "";
		if (keyCode != 42 && keyCode != 29 && keyCode != 56 && keyCode != 3638) {
			if (keyLocation != 1) {
				try {
					Integer.parseInt(NativeKeyEvent.getKeyText(keyCode));
					keys = "Numpad ";
				}
				catch (Exception e) {
					if (NativeKeyEvent.getKeyText(keyCode).equals("Enter"))
						keys = "Numpad ";
				}
			}
			keys += NativeKeyEvent.getKeyText(keyCode);
		}
		if ((1 & keyModifiers) != 0)
			keys = "LShift + " + keys;
		if ((2 & keyModifiers) != 0)
			keys = "LCtrl + " + keys;
		if ((8 & keyModifiers) != 0)
			keys = "LAlt + " + keys;
		if ((16 & keyModifiers) != 0)
			keys = "RShift + " + keys;
		if ((32 & keyModifiers) != 0)
			keys = "RCtrl + " + keys;
		return keys;
	}

	@Override
	public int hashCode()
		{ return Objects.hash(keyRaw, keyCode, keyLocation, keyModifiers, isAltPressed, isCtrlPressed, isShiftPressed); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shortcut other = (Shortcut) obj;
		return keyRaw == other.keyRaw && keyCode == other.keyCode &&
					 keyLocation == other.keyLocation && keyModifiers == other.keyModifiers &&
					 isAltPressed == other.isAltPressed && isCtrlPressed == other.isCtrlPressed &&
					 isShiftPressed == other.isShiftPressed;
	}		
	
	public static Shortcut getShortcutFromUser() {
		int[] keys = { 0 };
		Stage s = new Stage();
		VBox vBox = new VBox();
		Scene scene = new Scene(vBox, 300, 50);
		s.setScene(scene);
		s.setTitle("Definir tecla");
		Label label = new Label("Pressione uma tecla...");
		label.setFont(new Font("Lucida Console", 14));
		vBox.getChildren().add(label);
		vBox.setAlignment(Pos.CENTER);
		Shortcut shortcut = new Shortcut();
		Consumer<NativeKeyEvent> gkl = GlobalKeyListener.getOnKeyPressedEvent();
		GlobalKeyListener.setOnKeyPressedEvent(ex -> {
			Platform.runLater(() -> {
				changeShortcut(shortcut, ex);
				String keyName = NativeKeyEvent.getKeyText(ex.getKeyCode()).toLowerCase();
				label.setText(shortcut.getKeyText());
				if (!keyName.contains("control") &&
						!keyName.contains("ctrl") &&
						!keyName.contains("shift") &&
						!keyName.contains("alt")) {
							GlobalKeyListener.setOnKeyPressedEvent(gkl);
							s.close();
				}
				keys[0]++;
			});
		});
		GlobalKeyListener.setOnKeyReleasedEvent(ex -> {
			Platform.runLater(() -> {
				String keyName = NativeKeyEvent.getKeyText(ex.getKeyCode()).toLowerCase();
				if (keyName.contains("shift"))
					shortcut.setShiftPressed(false);
				if (keyName.contains("control") || keyName.contains("ctrl"))
					shortcut.setCtrlPressed(false);
				if (keyName.contains("shift"))
					shortcut.setAltPressed(false);
				label.setText(--keys[0] > 0 ? shortcut.getKeyText() : "Pressione uma tecla...");
			});
		});
		s.showAndWait();
		return shortcut;
	}

	private static void changeShortcut(Shortcut shortcut, NativeKeyEvent nativeKeyEvent) {
		shortcut.setRawCode(nativeKeyEvent.getRawCode());
		shortcut.setKeyCode(nativeKeyEvent.getKeyCode());
		shortcut.setKeyModifiers(nativeKeyEvent.getModifiers());
		shortcut.setKeyLocation(nativeKeyEvent.getKeyLocation());
		String keyName = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()).toLowerCase();
		if (keyName.contains("shift"))
			shortcut.setShiftPressed(true);
		if (keyName.contains("control") || keyName.contains("ctrl"))
			shortcut.setCtrlPressed(true);
		if (keyName.contains("shift"))
			shortcut.setAltPressed(true);
	}
	
}