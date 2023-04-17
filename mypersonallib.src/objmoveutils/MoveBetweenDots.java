package objmoveutils;

import java.util.ArrayList;
import java.util.List;

import enums.DirectionOrientation;

public class MoveBetweenDots {
	
	private List<Position> dots;
	private DirectionOrientation orientation;
	private Position pos, inc, position, startPosition, outputPosition;
	private int dotIndex, speedInFrames, currentFrame;
	private Boolean resetAfterFullCycle, cycleWasCompleted;
	
	/**
	 * 
	 * @param outputPosition - {@code Position} pertencente á um objeto externo, que terá seu
	 * 	{@code Position} atualizado automaticamente a cada chamada do método {@code move()},
	 * 	baseado no valor do método {@code getOutputPosition()} de maneira á fazer com que esse
	 * 	objeto externo realize um movimento entre as posições adicionadas previamente usando
	 * 	o método {@code addDot()}.
	 * @param orientation - Forma de progresso entre as coordenadas (Horário/Anti-horário)
	 * @param startPosition - {@code Position} inicial do trajeto. Se o {@code Position} informado
	 * 	pertencer á um objeto externo, o retorno do método {@code getOutputPosition()} será relativo
	 * 	ao {@code Position} do objeto externo, mesmo que ele se mova. Caso contrário, esse retorno
	 * 	será absoluto referente as coordenadas da janela. 
	 * . 
	 * @param speedInFrames - Velocidade em frames que o objeto se deslocará de uma coordenada á outra
	 * @param resetAfterFullCycle - {@code true} se o objeto deve repetir o ciclo após chegar
	 * 	na coordenada final.
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, Position startPosition, int speedInFrames, Boolean resetAfterFullCycle) {
		if (speedInFrames < 1)
			throw new RuntimeException("'speed' must be equal or higher than 1");
		dots = new ArrayList<>();
		pos = new Position();
	  inc = new Position();
		position = outputPosition;
		this.startPosition = startPosition;
		this.orientation = orientation;
		this.speedInFrames = speedInFrames;
		this.outputPosition = outputPosition;
		this.resetAfterFullCycle = resetAfterFullCycle;
		dotIndex = 0;
		currentFrame = speedInFrames;
		cycleWasCompleted = false;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, Position startPosition, int speedInFrames, Boolean resetAfterFullCycle)
		{ this(new Position(startPosition), orientation, startPosition, speedInFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code resetAfterFullCycle}. 
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, Position startPosition, int speedInFrames)
		{ this(outputPosition, orientation, startPosition, speedInFrames, true); }
	
	/**
	 * Sobrecarga do construtor que não pede os parâmetros {@code outputPosition} e {@code resetAfterFullCycle}. 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, Position startPosition, int speedInFrames)
		{ this(new Position(startPosition), orientation, startPosition, speedInFrames, true); }

	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, double startX, double startY, int speedInFrames, Boolean resetAfterFullCycle)
		{ this(outputPosition, orientation, new Position(startX, startY), speedInFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, double startX, double startY, int speedInFrames, Boolean resetAfterFullCycle)
		{ this(new Position(startX, startY), orientation, new Position(startX, startY), speedInFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, double startX, double startY, int speedInFrames)
		{ this(outputPosition, orientation, new Position(startX, startY), speedInFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, double startX, double startY, int speedInFrames)
		{ this(new Position(startX, startY), orientation, new Position(startX, startY), speedInFrames, true); }

	public void move() {
		checkError();
		if (!cycleWasCompleted) {
	    pos.incPosition(inc);
			if (++currentFrame >= speedInFrames) {
				setCoordToNextDot();
				currentFrame = 0;
			}
			position.setPosition(startPosition.getX() + pos.getX(), startPosition.getY() + pos.getY());
		}
	}
	
	private Position getCurrentDot() {
		checkError();
		return dots.get(dotIndex);
	}
	
	public void addDot(Position dotPosition)
		{ dots.add(dotPosition); }
	
	public void removeDot(Position dotPosition) {
		dots.remove(dotPosition);
		if (dotIndex >= dots.size()) {
			dotIndex = dots.isEmpty() ? 0 : dotIndex - 1;
			setCoordTo(dotIndex);
		}
	}
	
	public void addDot(double x, double y)
		{ addDot(new Position(x, y)); }
	
	public void removeDot(double x, double y)
		{ removeDot(new Position(x, y)); }

	private int getNextDotCoordIndex() {
		checkError();
		int i = dotIndex + (orientation == DirectionOrientation.CLOCKWISE ? 1 : -1);
		if (i < 0)
			i = dots.isEmpty() ? 0 : dots.size() - 1;
		else if (i >= dots.size())
			i = 0;
		return i;
	}
	
	private void setCoordToNextDot()
		{ setCoordTo(dotIndex); }

	private void setCoordTo(int index) {
		checkError();
		pos.setPosition(getCurrentDot());
		dotIndex = index;
		dotIndex = getNextDotCoordIndex();
		inc = Position.getIncrementForGoToCoordinate(pos, getCurrentDot(), speedInFrames);
		if (index != dotIndex && dotIndex == (orientation == DirectionOrientation.CLOCKWISE ? 0 : dots.size() - 1)) {
			if (resetAfterFullCycle)
				resetCycle();
			else
				cycleWasCompleted = true;
		}
	}
	
	private void checkError() {
		if (dots.size() < 1)
			throw new RuntimeException("You must add at least 2 dots using the 'addDot()' method");
	}
	
	private void resetCycle() {
		// TODO Auto-generated method stub
		
	}

	public DirectionOrientation getOrientation()
		{ return orientation; }

	public void setOrientation(DirectionOrientation orientation) {
		this.orientation = orientation;
		speedInFrames = -speedInFrames;
	}

	/**
	 * @return {@code true} se o movimento passou por todas as coordenadas adicionadas e voltou para o inicio.
	 */
	public Boolean isCycleCompleted()
		{ return cycleWasCompleted; }
	
	public Boolean getResetAfterFullCycle()
		{ return resetAfterFullCycle; }

	public void setResetAfterFullCycle(Boolean resetAfterFullCycle)
		{ this.resetAfterFullCycle = resetAfterFullCycle; }

	public double getSpeedInFrames()
		{ return speedInFrames; }

	public void setSpeedInFrames(int speedInFrames)
		{ this.speedInFrames = speedInFrames; }

	public Position getPosition()
		{ return position; }

	public Position getOutputPosition()
		{ return outputPosition; }

	public void setOutputPosition(Position position)
		{ outputPosition = position; }
	
	public void removeOutputPosition()
		{ outputPosition = new Position(outputPosition); }
	
	public double getOutputX()
		{ return outputPosition.getX(); }
	
	public double getOutputY()
		{ return outputPosition.getY(); }
	
}