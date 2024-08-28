package objmoveutils;

public class JumpMove {
	
	private Position inc, position, startPosition;
	private int speedInFrames, currentFrame;
	private double jumpStrenght, initialJumpStrenght, strenghtMultipiler, minStrenghtMultipiler;
	
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
	 * @param speedInFrames - Duração em frames do pulo.
	 */
	public JumpMove(Position outputPosition, Position startPosition, double jumpStrenght, double strenghtMultipiler, int speedInFrames) {
		if (speedInFrames < 0)
			throw new RuntimeException("'speed' must be equal or higher than 0");
		position = outputPosition;
		inc = new Position();
		this.startPosition = startPosition;
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		minStrenghtMultipiler = strenghtMultipiler / 100.0;
		initialJumpStrenght = jumpStrenght;
		currentFrame = 0;
		this.speedInFrames = speedInFrames;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public JumpMove(Position startPosition, double jumpStrenght, double strenghtMultipiler, int speedInFrames)
		{ this(new Position(), startPosition, jumpStrenght, strenghtMultipiler, speedInFrames); }

	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public JumpMove(Position outputPosition, double startX, double startY, double jumpStrenght, double strenghtMultipiler, int speedInFrames)
		{ this(outputPosition, new Position(startX, startY), jumpStrenght, strenghtMultipiler, speedInFrames); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public JumpMove(double startX, double startY, double jumpStrenght, double strenghtMultipiler, int speedInFrames)
		{ this(new Position(), new Position(startX, startY), jumpStrenght, strenghtMultipiler, speedInFrames); }

	public JumpMove(JumpMove jumpMove) {
		position = jumpMove.position;
		inc = jumpMove.inc;
		startPosition = jumpMove.startPosition;
		jumpStrenght = jumpMove.jumpStrenght;
		strenghtMultipiler = jumpMove.strenghtMultipiler;
		minStrenghtMultipiler = jumpMove.minStrenghtMultipiler;
		initialJumpStrenght = jumpMove.initialJumpStrenght;
		currentFrame = jumpMove.currentFrame;
		speedInFrames = jumpMove.speedInFrames;
	}
	
	public void move() {
		if (++currentFrame < speedInFrames / 2) {
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
	}
	
	/**
	 * @return {@code true} se a altura do pulo estiver igual ou abaixo da altura inicial.
	 */
	public Boolean jumpReachedFloorAgain()
		{ return currentFrame >= speedInFrames; }

	/**
	 * Se quizer realizar outro movimento de pulo ou resetar o mesmo, chame esse método
	 */
	public void resetJump() {
		jumpStrenght = initialJumpStrenght;
		currentFrame = 0;
	}
	
	/**
	 * Reverte o incremento do pulo de 'subindo' para 'descendo'
	 */
	public void cutToFall() {
		if (currentFrame < speedInFrames / 2)
			currentFrame = speedInFrames - currentFrame;
	}
	
	public Position getStartPosition()
		{ return startPosition; }

	public void setStartPosition(Position position)
		{ startPosition = position; }

	public double getSpeedInFrames()
		{ return speedInFrames; }

	public void setSpeedInFrames(int speed)
		{ speedInFrames = speed; }

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
