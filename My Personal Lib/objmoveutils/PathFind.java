package objmoveutils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import enums.Direction;
import enums.PathFindType;

public class PathFind {
	
	private static List<Direction> allDirs = new ArrayList<Direction>(Arrays.asList(Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN));
	private Boolean canTurn180OnFirstStep, firstStep;
	private PathFindType pathFindType;
	private Position startPosition, currentPosition, targetPosition;
	private Direction startDirection, currentDirection;	
	private Function<Position, Boolean> tileIsFree;
	private List<Direction> currentPath, startFreeDirs;
	private List<Position> tempTiles, currentPathPositions;
	private List<List<Direction>> foundPaths;
	private List<List<Position>> foundPathsPositions;
	
	public PathFind() {}
	
	/**
	 * Especifique a posição e direção atuais de um objeto em um board,
	 * 	o tile alvo á ser alcançado, um {@code Function} contendo uma forma
	 * 	do algorítimo saber que tiles são considerados como ocupados ou não,
	 *  e um tipo {@code Boolean} indicando se entre as direções do primeiro
	 *  passo, pode ser incluida a direção oposta á direção atual, e será gerado
	 * 	uma lista de direções que o objeto deve seguir para chegar no tile alvo.
	 * 
	 * @param currentPosition - Posição atual do objeto no board
	 * @param currentDirection - Direção atual do objeto no board
	 * @param targetPosition - Tile alvo á ser alcançado
	 * @param tileIsFree - Um tipo {@code Function} contendo uma forma do algorítimo
	 * 										 saber que tiles são considerados como ocupados ou não
	 * @param canTurn180OnFirstStep - {@code true} se entre as direções do primeiro passo,
	 *  															 pode ser incluida a direção oposta á direção atual
	 *  
	 *  OBS: A mecânica do seu jogo deve ser responsável por detectar quando o objeto está
	 *  em uma coordenada elegível para troca de direção de movimento. Sendo assim, cada
	 *  vez que isso ocorrer, chame o método {@code getNextDirection()} para saber qual
	 *  direção tomar para chegar corretamente ao tile alvo desejado.
	 */
	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree, PathFindType pathFindType, Boolean canTurn180OnFirstStep) {
		this.canTurn180OnFirstStep = canTurn180OnFirstStep;
		this.tileIsFree = tileIsFree;
		startPosition = new Position(currentPosition);
		this.currentPosition = new Position(currentPosition);
		this.targetPosition = new Position(targetPosition);
		this.pathFindType = pathFindType;
		startDirection = currentDirection;
		tempTiles = new ArrayList<>();
		foundPaths = new ArrayList<>();
		foundPathsPositions = new ArrayList<>();
		currentPath = new ArrayList<>();
		currentPathPositions = new ArrayList<>();
		firstStep = true;
		updatePath(currentPosition, currentDirection, targetPosition);
	}

	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree, Boolean canTurn180OnFirstStep)
		{ this(currentPosition, currentDirection, targetPosition, tileIsFree, PathFindType.AVERAGE_PATH, canTurn180OnFirstStep); }
	
	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree, PathFindType pathFindType)
		{ this(currentPosition, currentDirection, targetPosition, tileIsFree, pathFindType, true); }

	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree)
		{ this(currentPosition, currentDirection, targetPosition, tileIsFree, PathFindType.AVERAGE_PATH, true); }

	public void updatePath(Position currentPosition, Direction currentDirection, Position targetPosition) {
		this.currentPosition.setPosition(currentPosition);
		this.targetPosition.setPosition(targetPosition);
		this.currentDirection = currentDirection;
		tempTiles.clear();
		currentPath.clear();
		currentPathPositions.clear();
		foundPaths.clear();
		foundPathsPositions.clear();
		startFreeDirs = getRandomizedListOfFreeDirections();
		if (startFreeDirs == null || isPathFound())
			return;
		int foundTargetVal, n;
		Position pos = new Position(currentPosition);
		while (!isStucked()) {
			for (Direction firstDir : startFreeDirs) {
				firstStep = true;
				this.currentDirection = firstDir;
				this.currentPosition.setPosition(pos);
				currentPath.clear();
				currentPathPositions.clear();
				markTileAndIncPositionByDir();
				foundTargetVal = 3;
				do {
					while (!isStucked() && !isPathFound()) { // Vai seguindo em frente em direções aleatórias que estejam LIVRES, até ficar preso ou encontrar o alvo
						this.currentDirection = getLogicalFreeDirection();
						markTileAndIncPositionByDir();
						if (++foundTargetVal == 3)
							tempTiles.remove(this.targetPosition);
					} 
					if (isPathFound()) { // Se encontrou o tile alvo, otimiza o caminho gerado
						optmizePath();
						foundPaths.add(new ArrayList<>(currentPath));
						foundPathsPositions.add(new ArrayList<>(currentPathPositions));
						foundTargetVal = 1;
					}
					while ((isStucked() || foundTargetVal == 1) && !currentPath.isEmpty()) {
						/* Se o caminho atual ficou sem saída ou encontrou o tile-alvo,
						 * faz o caminho reverso até achar outra direção alternativa livre.
						 */
						n = currentPath.size() - 1;
						this.currentDirection = currentPath.get(n);
						currentPathPositions.remove(n);
						currentPath.remove(n);
						tempTiles.add(new Position(this.currentPosition));
						this.currentPosition.incPositionByDirection(this.currentDirection.getReverseDirection());
					}
				}
				while (!isStucked() && !isPathFound());
			}
		}
		n = foundPaths.size();
		if (n == 0) {
			currentPath.clear();
			currentPathPositions.clear();
		}
		else {
			if (pathFindType == PathFindType.AVERAGE_PATH)
				n = new SecureRandom().nextInt(n);
			else {
				int min = Integer.MAX_VALUE, max = 0, index = 0;
				for (n = 0; n < foundPaths.size(); n++) {
					if (pathFindType == PathFindType.SHORTEST_PATH && foundPaths.get(n).size() < min) {
						min = foundPaths.get(n).size();
						index = n;
					}
					else if (pathFindType == PathFindType.LONGEST_PATH && foundPaths.get(n).size() > max) {
						max = foundPaths.get(n).size();
						index = n;
					}
				}
				n = index;
			}
			currentPath = foundPaths.get(n);
			currentPathPositions = foundPathsPositions.get(n);
		}
		foundPaths.clear();
		foundPathsPositions.clear();
		tempTiles.clear();
	}

	public void updatePath(Position newCurrentPosition, Direction newCurrentDirection)
		{ updatePath(newCurrentPosition, newCurrentDirection, targetPosition); }
	
	public void updatePath(Position newTargetPosition)
		{ updatePath(startPosition, startDirection, newTargetPosition); }
	
	public void updatePath()
		{ updatePath(startPosition, startDirection, targetPosition); }

	public void setFindType(PathFindType type) {
		pathFindType = type;
		updatePath();
	}
	
	/**
	 * Verifica se algum dos tiles que formam o percurso atual até o alvo foi bloqueado
	 */
	public Boolean isCurrentPathBlocked() {
		if (isUnableToReachTargetPosition())
			return true;
		for (Position position : currentPathPositions)
			if (!position.equals(getNextPosition()) && !tileIsFree(position, true))
				return true;
		return false;
	}
	
	private void optmizePath() {
		Boolean found = false;
		Position po = new Position(), tPos = new Position();
		for (int incs, foundIndex, p = 0, t = currentPathPositions.size(); p < (t - 1); p++) {
			if (found)
				p = 0;
			po.setPosition(currentPathPositions.get(p));
			found = false;
			for (Direction d : allDirs) {
				tPos.setPosition(po);
				incs = 0;
				do {
					tPos.incPositionByDirection(d);
					incs++;
					foundIndex = currentPathPositions.indexOf(tPos);
					if (foundIndex > p + 5) {
						while (--foundIndex >= p) {
							currentPathPositions.remove(p);
							currentPath.remove(p);
							t--;
						}
						while (--incs >= 0) {
							tPos.incPositionByDirection(d.getReverseDirection());
							currentPathPositions.add(p, new Position(tPos));
							currentPath.add(p, d);
							tempTiles.remove(tPos);
							t++;
						}
						found = true;
					}
				}
				while (tileIsFree(tPos) || tempTiles.contains(tPos));
			}
		}
	}

	private void markTileAndIncPositionByDir() {
		if (currentDirection != null) {
			currentPathPositions.add(new Position(currentPosition));
			currentPath.add(currentDirection);
			currentPosition.incPositionByDirection(currentDirection);
			firstStep = false;
		}
	}

	private List<Direction> getRandomizedListOfFreeDirections() {
		int n, totalAvailableDirs = 0;
		List<Direction> availableDirs = new ArrayList<>();
		
		while (totalAvailableDirs < 4) {
			n = new SecureRandom().nextInt(4);
			while (availableDirs.contains(allDirs.get(n)))
				if (++n == 4)
					n = 0;
			availableDirs.add(allDirs.get(n));
			totalAvailableDirs++;
		}
		if (currentDirection != null && firstStep && !canTurn180OnFirstStep &&
				availableDirs.contains(currentDirection.getReverseDirection())) {
					availableDirs.remove(currentDirection.getReverseDirection());
					totalAvailableDirs--;
		}
		for (n = 0; n < totalAvailableDirs; n++)
			if (!tileIsFree(currentPosition, availableDirs.get(n))) {
				availableDirs.remove(n--);
				totalAvailableDirs--;
			}
		return totalAvailableDirs == 0 ? null : availableDirs;
	}
	
	private Boolean tileIsFree(Position position, Direction direction, Boolean ignoreCurrentPathPositions) {
		Position pos = new Position(position);
		if (direction != null)
			pos.incPositionByDirection(direction);
		return tileIsFree.apply(pos) && !tempTiles.contains(pos) &&
						(ignoreCurrentPathPositions || !currentPathPositions.contains(pos));
	}

	private Boolean tileIsFree(Position position, Direction direction)
		{ return tileIsFree(position, direction, false); }
	
	private Boolean tileIsFree(Position position, Boolean ignoreCurrentPathPositions)
		{ return tileIsFree(position, null, ignoreCurrentPathPositions); }

	private Boolean tileIsFree(Position position)
		{ return tileIsFree(position, null, false); }
	
	private Direction getLogicalFreeDirection() {
		if (targetPosition.getX() == currentPosition.getX() ||
				targetPosition.getY() == currentPosition.getY()) {
					Direction d = null;
					// Se manter na mesma direção se estiver indo reto ao tile alvo
					if (targetPosition.getY() < currentPosition.getY() &&
							currentDirection == Direction.UP &&
							tileIsFree(currentPosition, Direction.UP))
								d = Direction.UP;
					else if (targetPosition.getY() > currentPosition.getY() &&
							currentDirection == Direction.DOWN &&
							tileIsFree(currentPosition, Direction.DOWN))
								d = Direction.DOWN;
					else if (targetPosition.getX() < currentPosition.getX() &&
							currentDirection == Direction.LEFT &&
							tileIsFree(currentPosition, Direction.LEFT))
								d = Direction.LEFT;
					else if (targetPosition.getX() > currentPosition.getX() &&
							currentDirection == Direction.RIGHT &&
							tileIsFree(currentPosition, Direction.RIGHT))
								d =	Direction.RIGHT;
					if (d != null && ((firstStep && canTurn180OnFirstStep) ||
							(!firstStep && currentDirection.getReverseDirection() != d)))
								return d;
		}
		for (Direction dir : allDirs) // Direção baseada na posição do tile alvo em relação a posição atual
			if (((dir == Direction.LEFT && targetPosition.getX() < currentPosition.getX()) ||
					(dir == Direction.RIGHT && targetPosition.getX() > currentPosition.getX()) ||
					(dir == Direction.UP && targetPosition.getY() < currentPosition.getY()) ||
					(dir == Direction.DOWN && targetPosition.getY() > currentPosition.getY())) &&
					(currentDirection != dir && ((firstStep && canTurn180OnFirstStep) ||
					(!firstStep && currentDirection.getReverseDirection() != dir)) &&
					tileIsFree(currentPosition, dir)))
						return dir;
		List<Direction> dirs = getRandomizedListOfFreeDirections();
		return dirs == null ? null : dirs.get(new SecureRandom().nextInt(dirs.size()));
	}

	private Boolean isStucked()
		{ return getRandomizedListOfFreeDirections() == null; }
	
	public Direction getNextDirection()
		{ return currentPath.isEmpty() ? null : currentPath.get(0); }
	
	public void removeCurrentDirection() {
		if (!currentPath.isEmpty()) {
			currentPath.remove(0);
			currentPathPositions.remove(0);
		}
	}

	public Position getNextPosition()
		{ return currentPath.isEmpty() ? null : currentPathPositions.get(0); }

	public List<Direction> getPathDirections()
		{ return currentPath; }
	
	public List<Position> getPathPositions()
		{ return currentPathPositions; }
	
	public Boolean isPathFound()
		{ return currentPosition.equals(targetPosition); }
	
	public Boolean isUnableToReachTargetPosition()
		{ return currentPath.isEmpty(); }

}
