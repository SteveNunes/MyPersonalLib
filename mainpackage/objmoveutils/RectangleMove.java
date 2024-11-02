package objmoveutils;

import enums.DirectionOrientation;

public class RectangleMove extends MoveBetweenDots {
	
	public RectangleMove(RectangleMove rectangleMove)
		{ super((MoveBetweenDots) rectangleMove); }
		
	/**
	 * 
	 * @param outputPosition - {@code Position} pertencente á um objeto externo, que será atualizado
	 * 	de maneira á fazer com que esse objeto externo realize um movimento retângular
	 * @param orientation - Forma de progresso entre as coordenadas (Horário/Anti-horário)
	 * @param topLeftStartPosition - {@code Position} referente ao canto superior esquerdo, de onde
	 * 	iniciará a movimentação retângular. Se esse {@code Position} pertencer á um objeto externo,
	 * 	as coordenadas processadas relativas ao {@code Position} desse objeto externo, mesmo que ele
	 * 	se mova. Caso contrário, será {@code Position} absoluta da janela.
	 * @param width - Largura do retângulo de movimento á partir da posição inicial
	 * @param height - Altura do retângulo de movimento á partir da posição inicial
	 * @param durationFrames - Velocidade em frames que o objeto levará para realizar o movimento
	 * 	retângular completo.
	 * @param resetAfterFullCycle - {@code true} se o objeto deve repetir o ciclo após chegar
	 * 	na coordenada final.
	 */
	public RectangleMove(Position outputPosition, DirectionOrientation orientation, Position topLeftStartPosition, int width, int height, int durationFrames, Boolean resetAfterFullCycle) {
		super(outputPosition, orientation, topLeftStartPosition, durationFrames, resetAfterFullCycle);
		addDot(new Position());
		addDot(new Position(width, 0));
		addDot(new Position(width, height));
		addDot(new Position(0, height));
	}
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code outputPosition}. 
	 */
	public RectangleMove(DirectionOrientation orientation, Position topLeftStartPosition, int width, int height, int durationFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), orientation, topLeftStartPosition, width, height, durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que não pede o parâmetro {@code resetAfterFullCycle}. 
	 */
	public RectangleMove(Position outputPosition, DirectionOrientation orientation, Position topLeftStartPosition, int width, int height, int durationFrames)
		{ this(outputPosition, orientation, topLeftStartPosition, width, height, durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que não pede os parâmetros {@code outputPosition} e {@code resetAfterFullCycle}. 
	 */
	public RectangleMove(DirectionOrientation orientation, Position topLeftStartPosition, int width, int height, int durationFrames)
		{ this(new Position(topLeftStartPosition), orientation, topLeftStartPosition, width, height, durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public RectangleMove(Position outputPosition, DirectionOrientation orientation, double topLeftX, double topLeftY, int width, int height, int durationFrames, Boolean resetAfterFullCycle)
		{ this(outputPosition, orientation, new Position(topLeftX, topLeftY), width, height, durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public RectangleMove(DirectionOrientation orientation, double topLeftX, double topLeftY, int width, int height, int durationFrames, Boolean resetAfterFullCycle)
		{ this(new Position(), orientation, new Position(topLeftX, topLeftY), width, height, durationFrames, resetAfterFullCycle); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public RectangleMove(Position outputPosition, DirectionOrientation orientation, double topLeftX, double topLeftY, int width, int height, int durationFrames)
		{ this(outputPosition, orientation, new Position(topLeftX, topLeftY), width, height, durationFrames, true); }
	
	/**
	 * Sobrecarga do construtor que recebe valores literais das coordenadas em vez de um tipo {@code Position} 
	 */
	public RectangleMove(DirectionOrientation orientation, double topLeftX, double topLeftY, int width, int height, int durationFrames)
		{ this(new Position(), orientation, new Position(topLeftX, topLeftY), width, height, durationFrames, true); }
	
}