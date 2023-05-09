package objmoveutils;

import enums.DirectionOrientation;

public class GotoMove extends MoveBetweenDots {
	
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
	 * @param speedInFrames - Velocidade em frames que o objeto levará para realizar o movimento
	 * 	de 1 ponto á outro.
	 * @param resetAfterFullCycle - {@code true} se o objeto deve repetir o ciclo após chegar
	 * 	na coordenada final.
	 */
	public GotoMove(Position outputPosition, Position startPosition, Position endPosition, int speedInFrames, Boolean resetAfterFullCycle) {
		super(outputPosition, DirectionOrientation.CLOCKWISE, startPosition, speedInFrames, resetAfterFullCycle);
		addDot(new Position(startPosition));
		addDot(new Position(endPosition));
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public GotoMove(Position startPosition, Position endPosition, int speedInFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), startPosition, endPosition, speedInFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code resetAfterFullCycle}. 
	 */
	public GotoMove(Position outputPosition, Position startPosition, Position endPosition, int speedInFrames)
		{ this(outputPosition, startPosition, endPosition, speedInFrames, true); }
	
	/**
	 * Sobrecarga do construtor que não pede os parâmetros {@code outputPosition} e {@code resetAfterFullCycle}. 
	 */
	public GotoMove(Position startPosition, Position endPosition, int speedInFrames)
		{ this(new Position(), startPosition, endPosition, speedInFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(Position outputPosition, double startX, double startY, double endX, double endY, int speedInFrames, Boolean resetAfterFullCycle)
		{ this(outputPosition, new Position(startX, startY), new Position(endX, endY), speedInFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(double startX, double startY, double endX, double endY, int speedInFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), new Position(startX, startY), new Position(endX, endY), speedInFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(Position outputPosition, double startX, double startY, double endX, double endY, int speedInFrames)
		{ this(outputPosition, new Position(startX, startY), new Position(endX, endY), speedInFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public GotoMove(double startX, double startY, double endX, double endY, int speedInFrames)
		{ this(new Position(), new Position(startX, startY), new Position(endX, endY), speedInFrames, true); }
	
}