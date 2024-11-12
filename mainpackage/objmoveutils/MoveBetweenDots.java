package objmoveutils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import enums.DirectionOrientation;

public class MoveBetweenDots {
	
	private List<Position> dots;
	private DirectionOrientation orientation;
	private Position coord, inc, position, startPosition;
	private int dotIndex, durationFrames, currentFrame;
	private Boolean resetAfterFullCycle, cycleWasCompleted;
	private Consumer<MoveBetweenDots> onCycleCompleteEvent;

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
	 * @param durationFrames - Velocidade em frames que o objeto se offsetará de uma coordenada á outra
	 * @param resetAfterFullCycle - {@code true} se o objeto deve repetir o ciclo após chegar
	 * 	na coordenada final.
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, Position startPosition, int durationFrames, Boolean resetAfterFullCycle) {
		if (durationFrames < 1)
			throw new RuntimeException("'speed' must be equal or higher than 1");
		dots = new ArrayList<>();
		coord = new Position();
	  inc = new Position();
		position = outputPosition;
		this.startPosition = startPosition;
		this.orientation = orientation;
		this.durationFrames = durationFrames;
		this.resetAfterFullCycle = resetAfterFullCycle;
		dotIndex = 0;
		currentFrame = durationFrames;
		cycleWasCompleted = false;
		onCycleCompleteEvent = null;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, Position startPosition, int durationFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), orientation, startPosition, durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code resetAfterFullCycle}. 
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, Position startPosition, int durationFrames)
		{ this(outputPosition, orientation, startPosition, durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que não pede os parâmetros {@code outputPosition} e {@code resetAfterFullCycle}. 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, Position startPosition, int durationFrames)
		{ this(new Position(), orientation, startPosition, durationFrames, true); }

	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, double startX, double startY, int durationFrames, Boolean resetAfterFullCycle)
		{ this(outputPosition, orientation, new Position(startX, startY), durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, double startX, double startY, int durationFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), orientation, new Position(startX, startY), durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(Position outputPosition, DirectionOrientation orientation, double startX, double startY, int durationFrames)
		{ this(outputPosition, orientation, new Position(startX, startY), durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public MoveBetweenDots(DirectionOrientation orientation, double startX, double startY, int durationFrames)
		{ this(new Position(), orientation, new Position(startX, startY), durationFrames, true); }

	public MoveBetweenDots(MoveBetweenDots moveBetweenDots) {
		dots = moveBetweenDots.dots;
		coord = moveBetweenDots.coord;
	  inc = moveBetweenDots.inc;
		position = moveBetweenDots.position;
		startPosition = moveBetweenDots.startPosition;
		orientation = moveBetweenDots.orientation;
		durationFrames = moveBetweenDots.durationFrames;
		resetAfterFullCycle = moveBetweenDots.resetAfterFullCycle;
		dotIndex = moveBetweenDots.dotIndex;
		currentFrame = moveBetweenDots.currentFrame;
		cycleWasCompleted = moveBetweenDots.cycleWasCompleted;
	}

	public void move() {
		checkError();
		if (!cycleWasCompleted) {
	    coord.incPosition(inc);
			if (++currentFrame >= durationFrames) {
				setCoordToNextDot();
				currentFrame = 0;
			}
			position.setPosition(startPosition.getX() + coord.getX(), startPosition.getY() + coord.getY());
		}
		else if (onCycleCompleteEvent != null)
			onCycleCompleteEvent.accept(this);
	}
	
	public void setOnCycleCompleteEvent(Consumer<MoveBetweenDots> event)
		{ onCycleCompleteEvent = event; }

	public Position getIncrements()
		{ return inc; }
		
	private Position getCurrentDot() {
		checkError();
		return dots.get(dotIndex);
	}
	
	public void addDot(Position dotPosition)
		{ dots.add(dotPosition); }
	
	public void removeDot(Position dotPosition) {
		dots.remove(dotPosition);
		if (dotIndex >= dots.size())
			dotIndex = dots.isEmpty() ? 0 : dotIndex - 1;
		setCoordTo(dotIndex);
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
		dotIndex = index;
		coord.setPosition(getCurrentDot());
		dotIndex = getNextDotCoordIndex();
		inc = ShapeUtils.getIncrementValueForMoveBetweenPositions(coord, getCurrentDot(), durationFrames);
		if (!resetAfterFullCycle && index != dotIndex &&
				dotIndex == (orientation == DirectionOrientation.CLOCKWISE ? 0 : dots.size() - 1))
					cycleWasCompleted = true;
	}
	
	private void checkError() {
		if (dots.size() < 1)
			throw new RuntimeException("You must add at least 2 dots using the 'addDot()' method");
	}
	
	public DirectionOrientation getOrientation()
		{ return orientation; }

	public void setOrientation(DirectionOrientation orientation) {
		this.orientation = orientation;
		durationFrames = -durationFrames;
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

	public double getDurationFrames()
		{ return durationFrames; }

	public void setDurationFrames(int durationFrames)
		{ this.durationFrames = durationFrames; }

	public Position getPosition()
		{ return position; }

	public Position getOutputPosition()
		{ return position; }

	public void setOutputPosition(Position position)
		{ this.position = position; }
	
	public void removeOutputPosition()
		{ position = new Position(position); }
	
	public double getOutputX()
		{ return position.getX(); }
	
	public double getOutputY()
		{ return position.getY(); }
	
}