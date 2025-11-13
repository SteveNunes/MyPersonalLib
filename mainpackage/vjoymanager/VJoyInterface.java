package vjoymanager;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface VJoyInterface extends Library {
	
	VJoyInterface INSTANCE = Native.load("vJoyInterface", VJoyInterface.class);

	boolean vJoyEnabled();

	boolean AcquireVJD(int joystickID);

	void RelinquishVJD(int joystickID);

	boolean SetBtn(boolean isPressed, int joystickID, int buttonID);

	boolean SetAxis(int value, int joystickID, int usage);

	boolean SetContPov(int value, int joystickID, int povIndex);
	
}
