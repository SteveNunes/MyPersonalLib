package objmoveutils;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import enums.Direction;
import javafx.scene.shape.Line;

public abstract class ShapeUtils {
	
	private static Random random = ThreadLocalRandom.current();

	public static Point getExtendLinePoint(Line line, double extensionDistance)
		{ return getRealocatedPoint(new Point((int)line.getStartX(), (int)line.getStartY()), extensionDistance, getAngleFromLineInRadius(line)); }
	
	public static Point getRealocatedPoint(Point startPoint, double realocDistance, double realocAngle)
		{ return new Point((int) (startPoint.getX() + realocDistance * Math.cos(realocAngle)), (int) (startPoint.getY() + realocDistance * Math.sin(realocAngle))); }
	
	public static void realocPointByDirection(Point point, Direction direction, int incVal) {
		if (direction == null)
			throw new RuntimeException("direction is 'null'");
		if (direction == Direction.LEFT)
			point.setLocation(point.getX() - incVal, point.getY());
		else if (direction == Direction.RIGHT)
			point.setLocation(point.getX() + incVal, point.getY());
		else if (direction == Direction.UP)
			point.setLocation(point.getX(), point.getY() - incVal);
		else if (direction == Direction.DOWN)
			point.setLocation(point.getX(), point.getY() + incVal);
		else if (direction == Direction.UP_LEFT)
			point.setLocation(point.getX() - incVal, point.getY() - incVal);
		else if (direction == Direction.UP_RIGHT)
			point.setLocation(point.getX() + incVal, point.getY() - incVal);
		else if (direction == Direction.DOWN_LEFT)
			point.setLocation(point.getX() - incVal, point.getY() + incVal);
		else 
			point.setLocation(point.getX() + incVal, point.getY() + incVal);
	}

	public static double getAngleFromLineInRadius(Line line)
		{ return Math.atan2(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY());  }

  /** Informe o raio do circulo, e o total de pontos usados para formá-lo, para
   *  retornar o incremento para fazer a posição sair do centro e chegar nesse ponto.
   * 
   * @param pts - Quantidade de pontos para formar o círculo
   * @param coord - O ponto do círculo desejado. Ex: Se {@code qts} for {@code 10}, será calculado {@code 10} pontos separados igualmente de forma á formar o círculo desejado. Então, {@code coord 2} retorna a coordenada do segundo ponto usado para formar o círculo.
  */
	public static Point getPointFromCircle(double radius, int totalPoints, int point)
		{ return getPointFromEllipse(radius, radius, totalPoints, point); }
	
  /** Informe os raios da ellipse, e o total de pontos usados para formá-la, para
   *  retornar o incremento para fazer a posição sair do centro e chegar nesse ponto.
   * 
   * @param pts - Quantidade de pontos para formar a elipse
   * @param coord - O ponto da elipse desejado. Ex: Se {@code qts} for {@code 10}, será calculado {@code 10} pontos separados igualmente de forma á formar a elipse desejada. Então, {@code coord 2} retorna a coordenada do segundo ponto usado para formar a elipse.
  */
	public static Point getPointFromEllipse(double vRadius, double hRadius, int totalPoints, int point) {
	  double dis = 2 * Math.PI / totalPoints;
	  double co = Math.cos(point * dis);
	  double si = Math.sin(point * dis);
	  return new Point((int)(co * vRadius), (int)(si * hRadius));
	}

  /**
   * Calcula a coordenada de um ponto na borda de uma elipse, dado o centro, raios horizontal e vertical,
   * e a porcentagem do ângulo total.
   *
   * @param centerX    O valor da coordenada X do centro da elipse.
   * @param centerY    O valor da coordenada Y do centro da elipse.
   * @param radiusW    O raio da elipse na direção horizontal (largura).
   * @param radiusH    O raio da elipse na direção vertical (altura).
   * @param percentage A porcentagem do ângulo total (0.0 a 100.0) para encontrar a coordenada na borda.
   * @return Um objeto Point contendo as coordenadas (x, y) do ponto na borda da elipse.
   */
	public static Point getEllipseDotByPercentage(int centerX, int centerY, double radiusW, double radiusH, double percentage) {
		if (percentage < 0 || percentage > 100)
			throw new IllegalArgumentException("A porcentagem deve estar entre 0.0 e 100.0.");
		double grau = (percentage / 100.0) * 360.0;
		double radiano = Math.toRadians(grau);
		int x = (int) (centerX + radiusW * Math.cos(radiano));
		int y = (int) (centerY - radiusH * Math.sin(radiano)); // Subtrai porque o eixo Y cresce para baixo em Java Graphics
		return new Point(x, y);
	}

	public static Point getCircleDotByPercentage(int centerX, int centerY, double radius, double percentage) {
		return getEllipseDotByPercentage(centerX, centerY, radius, radius, percentage);
	}

	public static Point getEllipseDotByDegree(int centerX, int centerY, int radiusW, int radiusH, int degree) {
		return getEllipseDotByPercentage(centerX, centerY, radiusW, radiusW, (double)degree / 100d);
	}
	
	public static Point getCircleDotByDegree(int centerX, int centerY, int radius, int degree) {
		return getCircleDotByPercentage(centerX, centerY, radius, (double)degree / 100d);
	}
	
  /*
   * Retorna um {@code Position} com valores {@code X, Y} referentes
   * ao incremento para que um objeto na coordenada {@code position1}
   * chegue até a coordenada {@code position2} no total de {@code frames}
   */
	public static Position getIncrementValueForMoveBetweenPositions(Position position1, Position position2, int frames) {
	  double x = (position2.getX() - position1.getX()) / frames;
	  double y = (position2.getY() - position1.getY()) / frames;
	  return new Position(x, y);
	}

	public static Position getIncrementValueForMoveBetweenPositions(double startX, double startY, double endX, double endY, int frames)
		{ return getIncrementValueForMoveBetweenPositions(new Position(startX, startY), new Position(endX, endY), frames); }
	
	public static Position getIncrementValueForMoveBetweenPositions(double startX, double startY, Position endPosition, int frames)
		{ return getIncrementValueForMoveBetweenPositions(startX, startY, endPosition.getX(), endPosition.getY(), frames); }
	
	public static Position getIncrementValueForMoveBetweenPositions(Position startPosition, double endX, double endY, int frames)
		{ return getIncrementValueForMoveBetweenPositions(startPosition.getX(), startPosition.getY(), endX, endY, frames); }

	public static Point getRandomPositionFromAnEllipse(Ellipse2D ellipse) {
    double centerX = ellipse.getCenterX();
    double centerY = ellipse.getCenterY();
    double semiMajorAxis = ellipse.getWidth() / 2.0;
    double semiMinorAxis = ellipse.getHeight() / 2.0;
    double randomAngle = 2 * Math.PI * random.nextDouble();
    double randomRadius = Math.sqrt(random.nextDouble());
    double randomX = centerX + semiMajorAxis * randomRadius * Math.cos(randomAngle);
    double randomY = centerY + semiMinorAxis * randomRadius * Math.sin(randomAngle);
    return new Point((int)randomX, (int)randomY);
	}
	
	public static Point getRandomPositionFromASquare(Rectangle rectangle) {
    int randomX = rectangle.x + random.nextInt(rectangle.width);
    int randomY = rectangle.y + random.nextInt(rectangle.height);
    return new Point((int)randomX, (int)randomY);
	}
	
}
