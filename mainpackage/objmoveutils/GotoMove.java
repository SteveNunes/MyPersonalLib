package objmoveutils;

import java.util.function.Consumer;

import enums.DirectionOrientation;

public class GotoMove extends MoveBetweenDots {
	
	private Consumer<GotoMove> onMoveEndEvent;

	public GotoMove(GotoMove gotoMove) {
		super((MoveBetweenDots) gotoMove);
		onMoveEndEvent = null;
	}
	
	/**
	 * 
	 * @param outputPosition - {@code Position} pertencente á um objeto externo, que será atualizado
	 * 	de maneira á fazer com que esse objeto externo realize um movimento reto de 1 ponto á outro
	 * @param startPosition - {@code Position} referente ao {@code Position} inicial. Se esse
	 * 	{@code Position} pertencer á um objeto externo, as coordenadas processadas relativas ao
	 *  {@code Position} desse objeto externo, mesmo que ele se mova. Caso contrário, será o
	 *  {@code Position} absoluta da janela.
	 * @param endPosition - {@code Position} referente ao {@code Position} final. Se esse
	 * 	{@code Position} pertencer á um objeto externo, as coordenadas processadas relativas ao
	 *  {@code Position} desse objeto externo, mesmo que ele se mova. Caso contrário, será o
	 *  {@code Position} absoluta da janela.
	 * @param durationFrames - Velocidade em frames que o objeto levará para realizar o movimento
	 * 	de 1 ponto á outro.
	 * @param resetAfterFullCycle - {@code true} se o objeto deve repetir o ciclo após chegar
	 * 	na coordenada final.
	 */
	public GotoMove(Position outputPosition, Position startPosition, Position endPosition, int durationFrames, Boolean resetAfterFullCycle) {
		super(outputPosition, DirectionOrientation.CLOCKWISE, startPosition, durationFrames, resetAfterFullCycle);
		addDot(new Position(startPosition));
		addDot(new Position(endPosition));
		onMoveEndEvent = null;
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public GotoMove(Position startPosition, Position endPosition, int durationFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), startPosition, endPosition, durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code resetAfterFullCycle}. 
	 */
	public GotoMove(Position outputPosition, Position startPosition, Position endPosition, int durationFrames)
		{ this(outputPosition, startPosition, endPosition, durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que não pede os parâmetros {@code outputPosition} e {@code resetAfterFullCycle}. 
	 */
	public GotoMove(Position startPosition, Position endPosition, int durationFrames)
		{ this(new Position(), startPosition, endPosition, durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(Position outputPosition, double startX, double startY, double endX, double endY, int durationFrames, Boolean resetAfterFullCycle)
		{ this(outputPosition, new Position(startX, startY), new Position(endX, endY), durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(double startX, double startY, double endX, double endY, int durationFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), new Position(startX, startY), new Position(endX, endY), durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(Position outputPosition, double startX, double startY, double endX, double endY, int durationFrames)
		{ this(outputPosition, new Position(startX, startY), new Position(endX, endY), durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(double startX, double startY, double endX, double endY, int durationFrames)
		{ this(new Position(), new Position(startX, startY), new Position(endX, endY), durationFrames, true); }

	@Override
	public void move() {
		super.move();
		if (onMoveEndEvent != null)
			onMoveEndEvent.accept(this);
	}
	
	public void setOnMoveEndEvent(Consumer<GotoMove> event)
		{ onMoveEndEvent = event; }
}