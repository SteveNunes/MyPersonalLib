package light_spot_effects;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import objmoveutils.Position;
import util.MyMath;

public class LightSpot {

	private static List<LightSpot> spots = new ArrayList<>();
	private static List<LightSpot> tempSpots = new ArrayList<>();
	private static List<LightSpot> spotsInDarkness = new ArrayList<>();

	Position position;
	int offsetX;
	int offsetY;
	int xVariance;
	int yVariance;
	double radius;
	double minRadius;
	double maxRadius;
	double incRadius;
	Light.Point lightPoint;

	public LightSpot() {
		this(0, 0);
	}

	public LightSpot(int x, int y) {
		this(x, y, 10);
	}

	public LightSpot(int x, int y, double radius) {
		this(new Position(x, y), radius);
	}

	public LightSpot(Position position) {
		this(position, 10);
	}

	public LightSpot(Position position, double radius) {
		this.position = position;
		lightPoint = new Light.Point();
		setRadiusVariance(radius, radius, 0);
		setSpotVariance(0, 0);
		setOffset(0, 0);
	}

	public LightSpot setRadiusVariance(double minRadius, double maxRadius, double incRadiusAmount) {
		radius = minRadius;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		incRadius = incRadiusAmount;
		return this;
	}

	public LightSpot setSpotVariance(int xVariance, int yVariance) {
		this.xVariance = xVariance;
		this.yVariance = yVariance;
		return this;
	}

	public Light.Point getLightPoint() {
		lightPoint.setX(getX() + offsetX);
		lightPoint.setY(getY() + offsetY);
		lightPoint.setZ(getRadius());
		return lightPoint;
	}

	public Position getPosition() {
		return position;
	}

	public int getX() {
		int x = (int) position.getX() * 2;
		if (xVariance != 0)
			x = (int) MyMath.getRandom(x - xVariance, x + xVariance);
		return x;
	}

	public int getY() {
		int y = (int) position.getY() * 2;
		if (yVariance != 0)
			y = (int) MyMath.getRandom(y - yVariance, y + yVariance);
		return y;
	}

	public double getRadius() {
		if (incRadius > 0 && (radius += incRadius) >= maxRadius) {
			radius = maxRadius;
			incRadius = -incRadius;
		}
		else if (incRadius < 0 && (radius += incRadius) <= minRadius) {
			radius = minRadius;
			incRadius = -incRadius;
		}
		return radius;
	}

	public LightSpot setOffsetX(int offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public LightSpot setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public LightSpot setOffset(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		return this;
	}

	public LightSpot setRadius(double radius) {
		return setRadiusVariance(radius, radius, 0);
	}

	public static void clearTempLightSpots() {
		tempSpots.clear();
	}

	public static void addTempLightSpot(LightSpot spot) {
		tempSpots.add(spot);
	}

	public static void addLightSpot(LightSpot spot) {
		spots.add(spot);
	}

	public static void removeLightSpot(LightSpot spot) {
		spots.remove(spot);
	}

	public static void addLightSpotInDarkness(LightSpot spot) {
		spotsInDarkness.add(spot);
	}

	public static void removeLightSpotInDarkness(LightSpot spot) {
		spotsInDarkness.remove(spot);
	}

	public static void setMultipleLightSpotsInDarkness(GraphicsContext gc) {
		setMultipleLightSpots(gc, true);
	}

	public static void setMultipleLightSpots(GraphicsContext gc) {
		setMultipleLightSpots(gc, false);
	}

	private static void setMultipleLightSpots(GraphicsContext gc, boolean startWithDarkness) {
		gc.save();
		Blend blend = null, blend2 = null;
		for (int n = 0; n < 3; n++)
			for (LightSpot spot : n == 0 ? spots : n == 1 ? tempSpots : spotsInDarkness) {
				blend2 = new Blend(blend == null && startWithDarkness ? null : BlendMode.ADD);
				blend2.setTopInput(new Lighting(spot.getLightPoint()));
				if (blend != null)
					blend2.setBottomInput(blend);
				blend = blend2;
			}
		gc.applyEffect(blend);
		gc.restore();
	}

}