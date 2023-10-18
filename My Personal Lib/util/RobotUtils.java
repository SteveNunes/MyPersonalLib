package util;

import java.awt.AWTException;
import java.awt.event.InputEvent;

public abstract class RobotUtils {

	private static java.awt.Robot robot = null;

	public static void initializeRobot() {
		if (robot != null)
			throw new RuntimeException("Robot was already initialized");
		try
			{ robot = new java.awt.Robot(); }
		catch (AWTException e) {
		 	robot = null;
			throw new RuntimeException("Unable to initialize Robot\n\t" + e.getMessage());
		}
	}
	
	private static void checkRobot() {
		if (robot == null)
			throw new RuntimeException("Robot was not initialized. Call 'initializeRobot()' before do any Robot operations");
	}
	
	public static void doMouseMove(double x, double y, double scaleDpi) {
		checkRobot();
		robot.mouseMove((int)(x * (1 / scaleDpi)), (int)(y * (1 / scaleDpi)));
	}
	
	public static void doMouseMove(double x, double y)
		{ doMouseMove(x, y, 1); }
	
	private static void doPressMouseButton(int inputEvent, boolean moveToCoordBeforeClick, double x, double y, double scaleDpi) {
		checkRobot();
		if (moveToCoordBeforeClick) {
			doMouseMove(x, y, scaleDpi);
			doDelay(50);
		}
		robot.mousePress(inputEvent);
	}
	
	private static void doReleaseMouseButton(int inputEvent, boolean moveToCoordBeforeClick, double x, double y, double scaleDpi) {
		checkRobot();
		if (moveToCoordBeforeClick) {
			doMouseMove(x, y, scaleDpi);
			doDelay(50);
		}
		robot.mouseRelease(inputEvent);
	}
	
	public static void doPressLeftMouseButton()
		{ doPressMouseButton(InputEvent.BUTTON1_DOWN_MASK, false, 0, 0, 0); }
	
	public static void doPressLeftMouseButton(double x, double y)
		{ doPressMouseButton(InputEvent.BUTTON1_DOWN_MASK, true, x, y, 0); }

	public static void doPressLeftMouseButton(double x, double y, double scaleDpi)
		{ doPressMouseButton(InputEvent.BUTTON1_DOWN_MASK, true, x, y, scaleDpi); }

	public static void doReleaseLeftMouseButton()
		{ doReleaseMouseButton(InputEvent.BUTTON1_DOWN_MASK, false, 0, 0, 0); }
	
	public static void doReleaseLeftMouseButton(double x, double y)
		{ doReleaseMouseButton(InputEvent.BUTTON1_DOWN_MASK, true, x, y, 0); }
	
	public static void doReleaseLeftMouseButton(double x, double y, double scaleDpi)
		{ doReleaseMouseButton(InputEvent.BUTTON1_DOWN_MASK, true, x, y, scaleDpi); }

	public static void doPressRightMouseButton()
		{ doPressMouseButton(InputEvent.BUTTON2_DOWN_MASK, false, 0, 0, 0); }
	
	public static void doPressRightMouseButton(double x, double y)
		{ doPressMouseButton(InputEvent.BUTTON2_DOWN_MASK, true, x, y, 0); }
	
	public static void doPressRightMouseButton(double x, double y, double scaleDpi)
		{ doPressMouseButton(InputEvent.BUTTON2_DOWN_MASK, true, x, y, scaleDpi); }
	
	public static void doReleaseRightMouseButton()
		{ doReleaseMouseButton(InputEvent.BUTTON2_DOWN_MASK, false, 0, 0, 0); }
	
	public static void doReleaseRightMouseButton(double x, double y)
		{ doReleaseMouseButton(InputEvent.BUTTON2_DOWN_MASK, true, x, y, 0); }
	
	public static void doReleaseRightMouseButton(double x, double y, double scaleDpi)
		{ doReleaseMouseButton(InputEvent.BUTTON2_DOWN_MASK, true, x, y, scaleDpi); }

	public static void doPressMiddleMouseButton()
		{ doPressMouseButton(InputEvent.BUTTON3_DOWN_MASK, false, 0, 0, 0); }
	
	public static void doPressMiddleMouseButton(double x, double y)
		{ doPressMouseButton(InputEvent.BUTTON3_DOWN_MASK, true, x, y, 0); }
	
	public static void doPressMiddleMouseButton(double x, double y, double scaleDpi)
		{ doPressMouseButton(InputEvent.BUTTON3_DOWN_MASK, true, x, y, scaleDpi); }
	
	public static void doReleaseMiddleMouseButton()
		{ doReleaseMouseButton(InputEvent.BUTTON3_DOWN_MASK, false, 0, 0, 0); }
	
	public static void doReleaseMiddleMouseButton(double x, double y)
		{ doReleaseMouseButton(InputEvent.BUTTON3_DOWN_MASK, true, x, y, 0); }
	
	public static void doReleaseMiddleMouseButton(double x, double y, double scaleDpi)
		{ doReleaseMouseButton(InputEvent.BUTTON3_DOWN_MASK, true, x, y, scaleDpi); }
	
	public static void doKeyPress(int keyCode) {
		checkRobot();
		robot.keyPress(keyCode);
	}
	
	public static void doKeyRelease(int keyCode) {
		checkRobot();
		robot.keyRelease(keyCode);
	}
	
	public static void doDelay(int ms) {
		checkRobot();
		robot.delay(ms);
	}

}
