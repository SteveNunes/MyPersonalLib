package light_spot_effects;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import objmoveutils.Position;
import util.MyMath;

public class ColoredLightSpot extends LightSpot {
	
	private static List<ColoredLightSpot> spots = new ArrayList<>();
	private static List<ColoredLightSpot> tempSpots = new ArrayList<>();

	private Color color;
	private double opacity;

	public ColoredLightSpot()
		{ this(0, 0); }
	
	public ColoredLightSpot(int x, int y)
		{ this(x, y, 10, Color.WHITE); }

	public ColoredLightSpot(int x, int y, double radius)
		{ this(x, y, radius, Color.WHITE); }
	
	public ColoredLightSpot(int x, int y, double radius, double opacity)
		{ this(x, y, radius, Color.WHITE, opacity); }
	
	public ColoredLightSpot(int x, int y, double radius, Color color)
		{ this(x, y, radius, color, 0.6); }
	
	public ColoredLightSpot(int x, int y, double radius, Color color, double opacity)
		{ this(new Position(x, y), radius, color, 0.6); }
	
	public ColoredLightSpot(Position position)
		{ this(position, 10, Color.WHITE); }

	public ColoredLightSpot(Position position, double radius)
		{ this(position, radius, Color.WHITE); }

	public ColoredLightSpot(Position position, double radius, double opacity)
		{ this(position, radius, Color.WHITE, opacity); }

	public ColoredLightSpot(Position position, double radius, Color color)
		{ this(position, radius, color, 0.6); }
	
	public ColoredLightSpot(Position position, double radius, Color color, double opacity) {
		super(position, radius);
		super.getPosition().setPosition(position.getX() / 2, position.getY() / 2);
		this.color = color;
		this.opacity = opacity;
	}
	
	public double getOpacity()
		{ return opacity; }
	
	public ColoredLightSpot setOpacity(double opacity) {
		this.opacity = opacity;
		return this;
	}
	
	public Color getColor()
		{ return color; }
	
	public ColoredLightSpot setColor(Color color) {
		this.color = color;
		return this;
	}
	
	public ColoredLightSpot setOffsetX(int offsetX) {
		super.setOffsetX(offsetX); 
		return this;
	}
	
	public ColoredLightSpot setOffsetY(int offsetY) {
		super.setOffsetY(offsetY); 
		return this;
	}

	public ColoredLightSpot setOffset(int offsetX, int offsetY) {
		super.setOffset(offsetX, offsetY); 
		return this;
	}
	
	@Override
	public int getX() {
		int x = (int)position.getX();
		if (xVariance != 0)
			x = (int)MyMath.getRandom(x - xVariance, x + xVariance);
		return x;
	}
	
	@Override
	public int getY() {
		int y = (int)position.getY();
		if (yVariance != 0)
			y = (int)MyMath.getRandom(y - yVariance, y + yVariance);
		return y;
	}
	
	public ColoredLightSpot setRadiusVariance(double minRadius, double maxRadius, double incRadiusAmount) {
		super.setRadiusVariance(minRadius, maxRadius, incRadiusAmount);
		return this;
	}
	
	public ColoredLightSpot setSpotVariance(int xVariance, int yVariance) {
		super.setSpotVariance(xVariance, yVariance);
		return this;
	}
	
	public ColoredLightSpot setRadius(double radius) {
		this.setRadius(radius);
		return this;
	}
	
	public static void clearTempColoredLightSpots()
		{ tempSpots.clear(); }
	
	public static void addTempColoredLightSpot(ColoredLightSpot spot)
		{ tempSpots.add(spot); }

	public static void addColoredLightSpot(ColoredLightSpot spot)
		{ spots.add(spot); }
	
	public static void removeColoredLightSpot(ColoredLightSpot spot)
		{ spots.remove(spot); }

	public static void setMultipleColoredLightSpots(GraphicsContext gc) {
		double alpha = gc.getGlobalAlpha();
    for (int n = 0; n < 2; n++)
	    for (ColoredLightSpot spot : n == 0 ? spots : tempSpots) {
	    	int x = spot.getX() + spot.offsetX, y = spot.getY() + spot.offsetY;
				RadialGradient gradient = new RadialGradient(0, 0, x, y, spot.getRadius(), false, CycleMethod.NO_CYCLE, new Stop(0, spot.getColor()), new Stop(1, Color.TRANSPARENT));
				gc.setFill(gradient);
				gc.setGlobalAlpha(spot.getOpacity());
				gc.fillOval(x - spot.getRadius(), y - spot.getRadius(), spot.getRadius() * 2, spot.getRadius() * 2);
	    }
		gc.setGlobalAlpha(alpha);
	}

}