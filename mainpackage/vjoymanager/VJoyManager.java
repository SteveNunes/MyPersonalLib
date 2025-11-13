package vjoymanager;

import java.util.HashSet;
import java.util.Set;

import util.Misc;

public class VJoyManager {
	
	/**
	 * O VJoy emula joysticks do tipo DirectInput, o que o torna mais
	 * compatível com jogos antigos, mas pode não ser detectados por
	 * jogos modernos.
	 * 
	 * Padrão controle XBOX/PSX
	 * POV de 4 direções (Direcional digital)
	 * 2 eixos analógicos (Analógicos esquerdo e direito)
	 * 10 Botões (A(X), B(BOLA), X(QUADRADO), Y(TRIANGULO), L1(LB), R1(RB), SELECT, START, L3, R3
	 * 2 triggers analógicos (L2(LT), R2(RT))
	 * 
	 * Configurar o VJoy da seguinte forma:
	 * Axes: X, Y, Z, Rx, Ry
	 * Buttons: 10
	 * Povs: 1 (Continuous)
	 * Effects: Nenhum
	 */

	private int joyID;
	private int povValue;
	private Set<PovDirection> holdPovDirections;
	
	public VJoyManager(int joyID) {
		if (!VJoyInterface.INSTANCE.vJoyEnabled())
			throw new RuntimeException("vJoy is not enabled.");

		if (!VJoyInterface.INSTANCE.AcquireVJD(joyID))
			throw new RuntimeException("Unable to retrieve the virtual joystick.");
		
		this.joyID = joyID;
		povValue = 0;
		holdPovDirections = new HashSet<>();
		Misc.addShutdownEvent(() -> VJoyInterface.INSTANCE.RelinquishVJD(joyID));
		for (int buttonId : ButtonNamesPSX.values)
			setButtonState(buttonId, false);
		setLeftAxisXValue(0);
		setLeftAxisYValue(0);
		setRightAxisXValue(0);
		setRightAxisYValue(0);
		setZAxisValue(0);
	}
	
	public int getJoyID() {
		return joyID;
	}
	
	private void setButtonState(int buttonID, boolean state) {
		VJoyInterface.INSTANCE.SetBtn(state, joyID, buttonID);
	}
	
	public void pressButton(int buttonID) {
		try {
			holdButton(buttonID);
			Thread.sleep(25);
		}
		catch (InterruptedException e) {}
		releaseButton(buttonID);
	}

	public void holdButton(int buttonID) {
		setButtonState(buttonID, true);
	}

	public void releaseButton(int buttonID) {
		setButtonState(buttonID, false);
	}

	public void setPovDirection(int direction) {
		setPovDirection(direction, 1);
	}
	
	public void holdPovDirection(PovDirection direction) {
		holdPovDirection(direction, 1);
	}
	
	public void releasePovDirection(PovDirection direction) {
		releasePovDirection(direction, 1);
	}
	
	public void setPovDirection(int direction, int povIndex) {
		int value = direction == -1 ? -1 : direction == 0 ? 4500 * 7 : 4500 * (direction - 1);
		VJoyInterface.INSTANCE.SetContPov(value, joyID, povIndex);
	}
	
	public void holdPovDirection(PovDirection direction, int povIndex) {
		if (!holdPovDirections.contains(direction)) {
			holdPovDirections.add(direction);
			povValue += direction.getValue();
			VJoyInterface.INSTANCE.SetContPov(VJoyConstants.povValues[povValue], joyID, povIndex);
		}
	}

	public void releasePovDirection(PovDirection direction, int povIndex) {
		if (holdPovDirections.contains(direction)) {
			holdPovDirections.remove(direction);
			povValue -= direction.getValue();
			VJoyInterface.INSTANCE.SetContPov(VJoyConstants.povValues[povValue], joyID, povIndex);
		}
	}

	public void setLeftAxisXValue(int value) {
		value = getAxisValueFromPercent(value);
		VJoyInterface.INSTANCE.SetAxis(value, joyID, VJoyConstants.HID_USAGE_X);
	}
	
	public void setLeftAxisYValue(int value) {
		value = getAxisValueFromPercent(value);
		VJoyInterface.INSTANCE.SetAxis(value, joyID, VJoyConstants.HID_USAGE_Y);
	}
	
	public void setRightAxisXValue(int value) {
		value = getAxisValueFromPercent(value);
		VJoyInterface.INSTANCE.SetAxis(value, joyID, VJoyConstants.HID_USAGE_RX);
	}
	
	public void setRightAxisYValue(int value) {
		value = getAxisValueFromPercent(value);
		VJoyInterface.INSTANCE.SetAxis(value, joyID, VJoyConstants.HID_USAGE_RY);
	}
	
	public void setZAxisValue(int value) {
		value = getAxisValueFromPercent(value);
		VJoyInterface.INSTANCE.SetAxis(value, joyID, VJoyConstants.HID_USAGE_Z);
	}
	
	private int getAxisValueFromPercent(double percent) {
		if (percent < -100 || percent > 100)
			throw new RuntimeException("Axis value must be a percentage between -100 and 100");
		return (int)(VJoyConstants.CENTER_AXIS_VALUE + VJoyConstants.CENTER_AXIS_VALUE * percent / 100d);
	}
	
}
