package objmoveutils;

import java.util.function.Consumer;

public class JumpMove {
	
	private Position inc, position, startPosition;
	private int durationFrames, currentFrame;
	private double jumpStrenght, initialJumpStrenght, strenghtMultipiler, minStrenghtMultipiler;
	private Consumer<JumpMove> onJumpEndsEvent;
	
	/**
	 * 
	 * @param outputPosition - {@code Position} do objeto que será atualizado á cada chamada
	 * 													do método {@code move()}, fazendo ele realizar um movimento
	 * 													simulando um pulo.
	 * @param startPosition - Coordenada inicial de onde o objeto realizará o movimento de pulo.
	 * 												 Se for informado um {@code Position} pertencente á outro objeto
	 * 												 que se mova, essa posição irá acompanhar esse objeto.
	 * @param jumpStrenght - Força do pulo (incremento de altura inicial por frame)
	 * @param strenghtMultipiler - Multiplicador da força por frame
	 * @param durationFrames - Duração em frames do pulo.
	 */
	public JumpMove(Position outputPosition, Position startPosition, double jumpStrenght, double strenghtMultipiler, int durationFrames) {
		if (durationFrames < 0)
			throw new RuntimeException("'speed' must be equal or higher than 0");
		if (durationFrames % 2 != 0)
			throw new RuntimeException("'durationFrames' must be an even number");
		position = outputPosition;
		inc = new Position();
		this.startPosition = startPosition;
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		minStrenghtMultipiler = strenghtMultipiler / 100.0;
		initialJumpStrenght = jumpStrenght;
		currentFrame = 0;
		this.durationFrames = durationFrames;
		onJumpEndsEvent = null;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public JumpMove(Position startPosition, double jumpStrenght, double strenghtMultipiler, int durationFrames)
		{ this(new Position(), startPosition, jumpStrenght, strenghtMultipiler, durationFrames); }

	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public JumpMove(Position outputPosition, double startX, double startY, double jumpStrenght, double strenghtMultipiler, int durationFrames)
		{ this(outputPosition, new Position(startX, startY), jumpStrenght, strenghtMultipiler, durationFrames); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public JumpMove(double startX, double startY, double jumpStrenght, double strenghtMultipiler, int durationFrames)
		{ this(new Position(), new Position(startX, startY), jumpStrenght, strenghtMultipiler, durationFrames); }

	public JumpMove(JumpMove jumpMove)
		{ this(jumpMove.position, jumpMove.startPosition, jumpMove.initialJumpStrenght, jumpMove.strenghtMultipiler, jumpMove.durationFrames); }
	
	public void move() {
		if (++currentFrame < durationFrames / 2) {
			inc.decY(jumpStrenght);
			if (jumpStrenght > minStrenghtMultipiler)
				jumpStrenght /= strenghtMultipiler;
		}
		else {
			inc.incY(jumpStrenght);
			if (jumpStrenght < initialJumpStrenght * 3)
				jumpStrenght *= strenghtMultipiler;
		}
		position.setPosition(startPosition.getX() + inc.getX(), startPosition.getY() + inc.getY());
		if (jumpIsFinished() && onJumpEndsEvent != null)
			onJumpEndsEvent.accept(this);
	}
	
	public void setOnCycleCompleteEvent(Consumer<JumpMove> event)
		{ onJumpEndsEvent = event; }
	
	public Position getIncrements()
		{ return inc; }
	
	/**
	 * @return {@code true} se tiver passado todos os frames do pulo
	 */
	public Boolean jumpIsFinished()
		{ return currentFrame >= durationFrames; }

	/**
	 * Se quizer realizar outro movimento de pulo ou resetar o mesmo, chame esse método
	 */
	public void resetJump() {
		jumpStrenght = initialJumpStrenght;
		currentFrame = 0;
		inc.setPosition(0, 0);
	}

	public void resetJump(double jumpStrenght, double strenghtMultipiler, int durationFrames) {
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		minStrenghtMultipiler = strenghtMultipiler / 100.0;
		initialJumpStrenght = jumpStrenght;
		currentFrame = 0;
		this.durationFrames = durationFrames;
		inc.setPosition(0, 0);
	}
	
	/**
	 * Reverte o incremento do pulo de 'subindo' para 'descendo'
	 */
	public void cutToFall() {
		if (currentFrame < durationFrames / 2)
			currentFrame = durationFrames - currentFrame;
	}
	
	public Position getStartPosition()
		{ return startPosition; }

	public void setStartPosition(Position position)
		{ startPosition = position; }

	public int getDurationFrames()
		{ return durationFrames; }

	public void setDurationFrames(int speed)
		{ durationFrames = speed; }

	public Position getPosition()
		{ return position; }

	public void setOutputPosition(Position position)
		{ this.position = position; }
	
	public void removeOutputPosition()
		{ this.position = new Position(position); }

	public double getStrenghtMultipiler()
		{ return strenghtMultipiler; }

	public void setStrenghtMultipiler(double strenghtMultipiler)
		{ this.strenghtMultipiler = strenghtMultipiler; }
	
}
