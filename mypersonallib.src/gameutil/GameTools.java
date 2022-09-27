package gameutil;

import java.util.function.Consumer;

import javafx.application.Platform;
import util.MyMath;

public class GameTools {

	/**
	 * Chame esse método no final do seu main loop, passando no consumer a chamada do mesmo método
	 * para um efetivo loop infinito sem dar freezing
	 */
	public static void callMethodAgain(Consumer<?> consumer)
		{ Platform.runLater(new Runnable() { public void run() { consumer.accept(null); } }); }

	/**
	 * Retorna se a coordenada {@code x, y} está dentro de um retângulo em {@code dx, dy} com a largura {@code dw, dh}
	 * 
	 * @param x coordenada x á ser verificada se está dentro do retângulo
	 * @param y coordenada y á ser verificada se está dentro do retângulo
	 * @param dx coordenada x do retângulo 
	 * @param dy coordenada y do retângulo
	 * @param dw largura do retângulo
	 * @param dh altyra do retângulo
	 */
	public static Boolean coordIsInRect(int x, int y, int dx, int dy, int dw, int dh)
		{ return x >= dx && x <= dx + dw && y >= dy && y <= dy + dh; }
	
	/**
	 * Retorna se o retângulo em {@code x1, y1} com largura {@code w1, h1} está dentro de um retângulo em {@code x2, y2} com largura {@code w2, h2}
	 * 
	 * @param x1 coordenada x do primeiro retângulo
	 * @param y1 coordenada y do primeiro retângulo
	 * @param w1 largura do primeiro retângulo 
	 * @param h1 altura do primeiro retângulo
	 * @param x2 coordenada x do segundo retângulo
	 * @param y2 coordenada y do segundo retângulo
	 * @param w2 largura do segundo retângulo 
	 * @param h2 altura do segundo retângulo
	 */
	public static Boolean rectIsInRect(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		return coordIsInRect(x1, y1, x2, y2, w2, h2) || coordIsInRect(x1 + w1, y1, x2, y2, w2, h2) ||
			coordIsInRect(x1, y1 + h1, x2, y2, w2, h2) || coordIsInRect(x1 + w1, y1 + h1, x2, y2, w2, h2);
	}
	
	/**
	 * Retorna se a coordenada {@code x, y} está dentro de um círculo em {@code dx, dy} com  raio {@code r}
	 * 
	 * @param x coordenada x á ser verificada se está dentro do círculo
	 * @param y coordenada y á ser verificada se está dentro do círculo
	 * @param dx coordenada x do centro do círculo 
	 * @param dy coordenada y do centro do círculo
	 * @param r raio do círculo
	 */
	public static Boolean coordIsInCircle(int x1, int y1, int x2, int y2, int r)
		{ return ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) <= r * r); }
	
	/**
	 * Retorna se a coordenada {@code x, y} está dentro de uma elipse em {@code dx, dy} com os raios {@code rw, rh}
	 * 
	 * @param x coordenada x á ser verificada se está dentro da elipse
	 * @param y coordenada y á ser verificada se está dentro da elipse
	 * @param dx coordenada x do centro da elipse 
	 * @param dy coordenada y do centro da elipse
	 * @param rw raio horizontal da elipse
	 * @param rh raio vertical da elipse
	 */
	public static Boolean coordIsInEllipse(int x1, int y1, int x2, int y2, int rx, int ry) {
		double c = (Math.pow((x2 - x1), 2) / Math.pow(rx, 2)) +
								(Math.pow((y2 - y1), 2) / Math.pow(ry, 2));
		return c == 0;
	}
	
  /** Informe as coordenadas {@code x, y} do centro do círculo, e seu raio  para retornar
   * a coordenada de um dos pontos utilizados para formar esse círculo.
   * 
   * @param pts - Quantidade de pontos para desenhar o circulo
   * @param pos - O ponto do circulo desejado. Ex: Se {@code qts} for {@code 10}, será calculado {@code 10} pontos separados igualmente de forma á formar o circulo desejado. Então, {@code pos 2} retorna a coordenada do segundo ponto usado para formar o circulo.
  */
	public static double[] circleDot(double x, double y, double r, int pts, int pos) {
	  double dis = 2 * Math.PI / pts;
	  double co = Math.cos(pos * dis);
	  double si = Math.sin(pos * dis);
	  return new double[] {co * r, si * r};
	}
	
  /** Informe as coordenadas {@code x, y} do centro da elipse, e seu raio  para retornar
   * a coordenada de um dos pontos utilizados para formar essa elipse.
   * 
   * @param pts - Quantidade de pontos para desenhar a elipse
   * @param pos - O ponto da elipse desejado. Ex: Se {@code qts} for {@code 10}, será calculado {@code 10} pontos separados igualmente de forma á formar a elipse desejada. Então, {@code pos 2} retorna a coordenada do segundo ponto usado para formar a elipse.
  */
	public static double[] ellipseDot(double x, double y, double rw, double rh, int pts, int pos) {
	  double dis = 2 * Math.PI / pts;
	  double co = Math.cos(pos * dis);
	  double si = Math.sin(pos * dis);
	  return new double[] {co * rw, si * rh};
	}
	
	/**
	 * Retorna uma coordenada aleatória dentro de um círculo.
	 * 
	 * @param x coordenada x do círculo
	 * @param y coordenada y do círculo
	 * @param r raio do círculo
	 */
	public static int[] getRandCoordFromACircle(int x, int y, int r) {
		int xx, yy;
	  do {
	  	xx = (int)MyMath.rand(x - r, x + r);
	  	yy = (int)MyMath.rand(y - r, y + r);
	  }
	  while (!coordIsInCircle(xx, yy, x, y, r));
	  return new int[] {xx, yy};
	}
	
	/**
	 * Retorna uma coordenada aleatória dentro de uma elipse.
	 * 
	 * @param x coordenada x da elipse
	 * @param y coordenada y da elipse
	 * @param rw raio horizontal da elipse
	 * @param rh raio vertical da elipse
	 */
	public static int[] getRandCoordFromAnEllipse(int x, int y, int rw, int rh) {
		int xx, yy;
	  do {
	  	xx = (int)MyMath.rand(x - rw, x + rw);
	  	yy = (int)MyMath.rand(y - rh, y + rh);
	  }
	  while (!coordIsInEllipse(xx, yy, x, y, rw, rh));
	  return new int[] {xx, yy};
	}
	
	/**
	 * Retorna uma coordenada aleatória dentro de um retângulo
	 * 
	 * @param x coordenada x do retângulo
	 * @param y coordenada y do retângulo
	 * @param w largura do retângulo
	 * @param h altura do retângulo
	 */
	public static int[] getRandCoordFromAnRect(int x, int y, int w, int h) {
		int xx, yy;
	  do {
	  	xx = (int)MyMath.rand(x - w, x + w);
	  	yy = (int)MyMath.rand(y - h, y + h);
	  }
	  while (!coordIsInRect(xx, yy, x, y, w, h));
	  return new int[] {xx, yy};
	}
	
  /*
   * Retorna valores {@x, y} de incremento para que um objeto nas coordenadas {@code x1, y1}
   * chegue até as coordenadas {@code x2, y2} no total de {@code frames}
   */
	public static double[] incrementForGoToCoord(double x1, double y1, double x2, double y2, double frames) {
	  double x = (x2 - x1) / 100;
	  double y = (y2 - y1) / 100;
	  while ((x != 0 && Math.abs(x) < frames) || (y != 0 && Math.abs(y) < frames)) {
	  	x += x / 10;
	  	y += y / 10;
	  }
	  while ((x != 0 && Math.abs(x) > frames) || (y != 0 && Math.abs(y) > frames)) {
	  	x -= x / 10;
	  	y -= y / 10;
	  }
	  return new double[] {x, y};
	}
	
}
