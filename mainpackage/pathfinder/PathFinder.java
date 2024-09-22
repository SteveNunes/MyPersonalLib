package pathfinder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import enums.Direction;
import javafx.util.Pair;
import util.CollectionUtils;

public class PathFinder {
	
	private Random random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));
	private List<Pair<PathFinderTileCoord, Direction>> directions;
	private Map<PathFinderTileCoord, Boolean> blocks = new HashMap<>();
	private Map<PathFinderTileCoord, Boolean> path = new HashMap<>();
	private Function<PathFinderTileCoord, Boolean> functionIsTileFree;
	private Direction initialDirection;
	private PathFinderIgnoreInitialBackDirection ignoreInitialBackDirection;
	private PathFinderDistance distance;
	private PathFinderOptmize optimize;
	private List<List<Pair<PathFinderTileCoord, Direction>>> foundPaths = new ArrayList<>();
	
	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, null, null, functionIsTileFree); }

	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderIgnoreInitialBackDirection ignoreInitialBackDirection, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, null, null, functionIsTileFree); }
	
	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderOptmize optimize, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, null, optimize, functionIsTileFree); }
	
	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderDistance distance, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, distance, null, functionIsTileFree); }

	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderIgnoreInitialBackDirection ignoreInitialBackDirection, PathFinderDistance distance, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, distance, null, functionIsTileFree); }
	
	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderIgnoreInitialBackDirection ignoreInitialBackDirection, PathFinderOptmize optimize, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, null, optimize, functionIsTileFree); }
	
	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderDistance distance, PathFinderOptmize optimize, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, distance, optimize, functionIsTileFree); }

	public PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderIgnoreInitialBackDirection ignoreInitialBackDirection, PathFinderDistance distance, PathFinderOptmize optimize, Function<PathFinderTileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, distance, optimize, functionIsTileFree, true); }
	
	private PathFinder(PathFinderTileCoord initialCoord, PathFinderTileCoord targetCoord, Direction initialDirection, PathFinderIgnoreInitialBackDirection ignoreInitialBackDirection, PathFinderDistance distance, PathFinderOptmize optimize, Function<PathFinderTileCoord, Boolean> functionIsTileFree, boolean recalculatePath) {
		this.initialDirection = initialDirection == null ? Direction.LEFT : initialDirection;
		this.ignoreInitialBackDirection = ignoreInitialBackDirection == null ? PathFinderIgnoreInitialBackDirection.NO_IGNORE : ignoreInitialBackDirection;
		this.distance = distance == null ? PathFinderDistance.RANDOM : distance;
		this.optimize = optimize == null ? PathFinderOptmize.NOT_OPTIMIZED : optimize;
		this.functionIsTileFree = functionIsTileFree;
		directions = new ArrayList<>();
		if (recalculatePath)
			recalculatePath(initialCoord, targetCoord, this.initialDirection);
	}
	
	public boolean pathWasFound()
		{ return !directions.isEmpty(); }
	
	public Direction getNextDirectionToGo() {
		Direction dir = directions.isEmpty() ? null : directions.get(0).getValue();
		if (!directions.isEmpty())
			directions.remove(0);
		return dir;
	}
	
	public List<Pair<PathFinderTileCoord, Direction>> getCurrentPath()
		{ return directions.isEmpty() ? null : directions; }
	
	public void recalculatePath(PathFinderTileCoord currentCoord, PathFinderTileCoord targetCoord, Direction currentDirection)
		{ recalculatePath(currentCoord, targetCoord, currentDirection, new HashMap<>()); }

	private void recalculatePath(PathFinderTileCoord currentCoord, PathFinderTileCoord targetCoord, Direction currentDirection, Map<PathFinderTileCoord, Boolean> tempBlocks) {
		blocks = new HashMap<>(tempBlocks);
		path = new HashMap<>();
		if (currentCoord.equals(targetCoord) || !isTileFree(currentCoord) || !isTileFree(targetCoord) || isStucked(currentCoord) || isStucked(targetCoord)) {
			directions.clear();
			foundPaths.clear();
			return;
		}
		if (!directions.isEmpty() && continueCurrentPath(currentCoord, targetCoord, currentDirection))
			return;
		directions.clear();
		Direction dir = currentDirection;
		PathFinderTileCoord unMark = null;
		PathFinderTileCoord coord = currentCoord.getNewInstance();
		foundPaths = new ArrayList<>();
		List<Pair<PathFinderTileCoord, Direction>> dirs = new ArrayList<>();
		while (!isStucked(coord)) {
			dir = null;
			if (coord.getX() == targetCoord.getX() || coord.getY() == targetCoord.getY()) {
				dir = coord.getX() == targetCoord.getX() ?
							(coord.getY() < targetCoord.getY() ? Direction.DOWN : Direction.UP) :
							(coord.getX() < targetCoord.getX() ? Direction.RIGHT : Direction.LEFT);
				if (!isTileFree(coord, dir))
					dir = null;
			}
			dir = dir != null ? dir : getRandomFreeDir(coord);
			if (coord.equals(currentCoord) && (ignoreInitialBackDirection == PathFinderIgnoreInitialBackDirection.IGNORE || 
					(ignoreInitialBackDirection == PathFinderIgnoreInitialBackDirection.ONLY_IF_THERES_NO_AVAILABLE_DIRECTION &&
					 (!foundPaths.isEmpty() || getFreeDirs(coord).size() > 1))))
							dir = getRandomFreeDir(coord, initialDirection.getReverseDirection());
			if (dir == null)
				return;
			path.put(coord.getNewInstance(), true);
			dirs.add(new Pair<>(coord.getNewInstance(), dir));
			blocks.put(coord.getNewInstance(), true);
			coord.incByDirection(dir);
			if (unMark != null) {
				blocks.remove(unMark);
				unMark = null;
			}
			if (coord.equals(targetCoord) || isStucked(coord)) {
				blocks.put(coord.getNewInstance(), true);
				if (coord.equals(targetCoord)) {
					foundPaths.add(new ArrayList<>(dirs));
					unMark = coord.getNewInstance();
				}
				do {
					dir = dirs.get(dirs.size() - 1).getValue();
					coord.incByDirection(dir.getReverseDirection());
					path.remove(coord);
					dirs.remove(dirs.size() - 1);
				}
				while (!dirs.isEmpty() && isStucked(coord));
			}
		}
		if (!foundPaths.isEmpty()) {
			for (int n = 0; optimize == PathFinderOptmize.OPTIMIZED && n < foundPaths.size(); n++) {
				List<Pair<PathFinderTileCoord, Direction>> path = foundPaths.get(n);
				optimizePath(path);
				foundPaths.set(n, path);
			}
			if (foundPaths.size() > 1) {
				foundPaths.sort((p1, p2) -> p1.size() - p2.size());
				if (distance == PathFinderDistance.RANDOM)
					directions = new ArrayList<>(CollectionUtils.getRandomItemFromList(foundPaths));
				else if (distance == PathFinderDistance.SHORTEST)
					directions = new ArrayList<>(foundPaths.get(0));
				else if (distance == PathFinderDistance.LONGEST)
					directions = new ArrayList<>(foundPaths.get(foundPaths.size() - 1));
				else
					directions = new ArrayList<>(foundPaths.get((int)((foundPaths.size() - 1) / 2)));
			}
			else
				directions = new ArrayList<>(foundPaths.get(0));
		}
	}

	private void optimizePath(List<Pair<PathFinderTileCoord, Direction>> dirs) {
		boolean restart;
		PathFinderTileCoord current;
		PathFinderTileCoord target = dirs.get(dirs.size() - 1).getKey().getNewInstance();
		target.incByDirection(dirs.get(dirs.size() - 1).getValue());
		Map<PathFinderTileCoord, Boolean> path = new HashMap<>();
		dirs.forEach(t -> path.put(t.getKey().getNewInstance(), true));
		do {
			restart = false;
			out:
	    for (int n = 0; n < dirs.size() - 3; n++) {
				Direction dir = dirs.get(n).getValue();
				current = dirs.get(n).getKey().getNewInstance();
				for (int d = 0; d < 4; d++) {
					dir = dir.getNext4WayClockwiseDirection();
					PathFinderTileCoord c = current.getNewInstance();
					c.incByDirection(dir);
					if (path.containsKey(c) || !isTileFree(c, true))
						continue;
					int steps = 1;
					while (isTileFree(c, true)) {
						if (!c.equals(target)) {
							c.incByDirection(dir);
							steps++;
						}
						if (c.equals(target) || path.containsKey(c)) {
							PathFinderTileCoord c2;
							while (n + 1 < dirs.size() && !(c2 = dirs.get(n + 1).getKey().getNewInstance()).equals(c)) {
								this.path.remove(c2);
								blocks.remove(c2);
								path.remove(c2);
								dirs.remove(n + 1);
							}
							c2 = current.getNewInstance();
							dirs.set(n, new Pair<>(c2.getNewInstance(), dir));
							for (int x = n + 1; --steps > 0; x++) {
								c2.incByDirection(dir);
								path.put(c2.getNewInstance(), true);
								this.path.put(c2.getNewInstance(), true);
								blocks.put(c2.getNewInstance(), true);
								dirs.add(x, new Pair<>(c2.getNewInstance(), dir));
							}
							restart = true;
							break out;
						}
					}
				}
			}
		}
		while (restart);
	}

	private boolean continueCurrentPath(PathFinderTileCoord currentCoord, PathFinderTileCoord targetCoord, Direction currentDirection) {
		if (directions.isEmpty())
			return false;
		boolean ok = false;
		PathFinderTileCoord lastTarget = directions.get(directions.size() - 1).getKey().getNewInstance();
		Direction lastDir = directions.get(directions.size() - 1).getValue();
		lastTarget.incByDirection(lastDir);
		if (directions.size() > 1 && directions.get(0).getKey().equals(currentCoord) && lastTarget.equals(targetCoord))
			return true;
		for (Pair<PathFinderTileCoord, Direction> t : directions)
			if (!isTileFree(t.getKey()))
				return false;
		if (!directions.get(0).getKey().equals(currentCoord))
			for (int n = 1; n < directions.size(); n++) {
				if (directions.get(n).getKey().equals(currentCoord)) {
					directions = directions.subList(n, directions.size());
					currentCoord = directions.get(0).getKey().getNewInstance();
					ok = true;
					break;
				}
				else if (n + 1 == directions.size())
					return false;
			}
		if (!lastTarget.equals(targetCoord)) {
			for (int n = directions.size() - 1; n >= 0; n--)
				if (directions.get(n).getKey().equals(targetCoord)) {
					directions = directions.subList(0, n);
					return true;
				}
			return continueCurrentPathSupport(lastTarget, targetCoord, lastDir) ? true : ok;
		}
		return ok;
	}
	
	private boolean continueCurrentPathSupport(PathFinderTileCoord current, PathFinderTileCoord target, Direction dir) {
		PathFinder pf = new PathFinder(current, target, dir, null, null, null, functionIsTileFree, false);
		Map<PathFinderTileCoord, Boolean> blocks = new HashMap<>();
		directions.forEach(p -> blocks.put(p.getKey().getNewInstance(), true));
		pf.recalculatePath(current, target, dir, blocks);
		if (pf.getCurrentPath() == null)
			return false;
		pf.getCurrentPath().forEach(p -> directions.add(p));
		directions.forEach(d -> path.put(d.getKey(), true));
		optimizePath(directions);
		return true;
	}

	private List<Direction> getFreeDirs(PathFinderTileCoord coord)
		{ return getFreeDirs(coord, null); }
		
	private List<Direction> getFreeDirs(PathFinderTileCoord coord, Direction ignoredDir) {
		Direction d = Direction.LEFT;
		List<Direction> freeDirs = new ArrayList<>();
		for (int n = 0; n < 4; n++) {
			PathFinderTileCoord coord2 = coord.getNewInstance();
			coord2.incByDirection(d);
			if ((ignoredDir == null || d != ignoredDir) && isTileFree(coord2))
				freeDirs.add(d);
			d = d.getNext4WayClockwiseDirection();
		}
		return freeDirs.isEmpty() ? null : freeDirs;
	}
	
	private Direction getRandomFreeDir(PathFinderTileCoord coord)
		{ return getRandomFreeDir(coord, null); }
	
	private Direction getRandomFreeDir(PathFinderTileCoord coord, Direction ignoredDir) {
		List<Direction> freeDirs = getFreeDirs(coord, ignoredDir);
		return freeDirs == null ? null : freeDirs.get(random.nextInt(freeDirs.size()));
	}
	
	private boolean isTileFree(PathFinderTileCoord coord)
		{ return isTileFree(coord, null, false); }

	private boolean isTileFree(PathFinderTileCoord coord, boolean ignoreBlocks)
		{ return isTileFree(coord, null, ignoreBlocks); }

	private boolean isTileFree(PathFinderTileCoord coord, Direction incCoordToDir)
		{ return isTileFree(coord, incCoordToDir, false); }
	
	private boolean isTileFree(PathFinderTileCoord coord, Direction incCoordToDir, boolean ignoreBlocks) {
		if (incCoordToDir != null) {
			coord = coord.getNewInstance();
			coord.incByDirection(incCoordToDir);
		}
		return functionIsTileFree.apply(coord) && (ignoreBlocks || !blocks.containsKey(coord));
	}
	
	private boolean isStucked(PathFinderTileCoord coord)
		{ return isStucked(coord, null); }
	
	private boolean isStucked(PathFinderTileCoord coord, Direction ignoredDir)
		{ return getFreeDirs(coord, ignoredDir) == null; }

}