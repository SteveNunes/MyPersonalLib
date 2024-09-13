package objmoveutils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

import enums.Direction;
import enums.PathFindIgnoreReverseDirectionAtStartPosition;
import enums.PathFindType;

public class PathFind {
	
	private static List<Direction> allDirs = new ArrayList<Direction>(Arrays.asList(Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN));
	private PathFindIgnoreReverseDirectionAtStartPosition ignoreReverseDirectionAtStartPosition;
	private PathFindType pathFindType;
	private Position startPosition, currentPosition, targetPosition;
	private Direction startDirection;	
	private Function<Position, Boolean> tileIsFree;
	private List<Direction> startFreeDirs;
	private List<Position> tempTiles, currentPath;
	private List<List<Position>> foundPaths;
	private Boolean reversing = false, debug = false, firstStep;
	private Random random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));
	private Scanner sc;
	
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
	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree, PathFindType pathFindType, PathFindIgnoreReverseDirectionAtStartPosition ignoreReverseDirectionAtStartPosition) {
		this.ignoreReverseDirectionAtStartPosition = ignoreReverseDirectionAtStartPosition;
		this.tileIsFree = tileIsFree;
		this.pathFindType = pathFindType;
		this.currentPosition = new Position();
		this.targetPosition = new Position();
		startPosition = new Position();
		tempTiles = new ArrayList<>();
		currentPath = new ArrayList<>();
		foundPaths = new ArrayList<>();
		sc = new Scanner(System.in);
		updatePath(currentPosition, currentDirection, targetPosition);
	}

	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree, PathFindIgnoreReverseDirectionAtStartPosition ignoreReverseDirectionAtStartPosition)
		{ this(currentPosition, currentDirection, targetPosition, tileIsFree, PathFindType.AVERAGE_PATH, ignoreReverseDirectionAtStartPosition); }
	
	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree, PathFindType pathFindType)
		{ this(currentPosition, currentDirection, targetPosition, tileIsFree, pathFindType, PathFindIgnoreReverseDirectionAtStartPosition.ALWAYS_IGNORE); }

	public PathFind(Position currentPosition, Direction currentDirection, Position targetPosition, Function<Position, Boolean> tileIsFree)
		{ this(currentPosition, currentDirection, targetPosition, tileIsFree, PathFindType.AVERAGE_PATH, PathFindIgnoreReverseDirectionAtStartPosition.ALWAYS_IGNORE); }

	private Boolean tryToConnectCurrentPathToNewTarget(Position cPos, Position tPos) {
		if (!currentPath.isEmpty()) {
			/**
			 * Verifica se a posição atual continua á mesma desde a última chamada do
			 * método atual. Se sim, verifica se o o alvo mudou de lugar, para alguma
			 * posição que seja acessível a partir da posicao final do caminho atual,
			 * para atualizar o caminho sem refaze-lo do 0, mantendo uma rota constante.
			 */
		}
		return false;
	}
	
	private Boolean tryToTrimPath(Position tPos) {
		int pathSize = currentPath.size();
		for (int n = 0, n2; pathSize > 1 && n < pathSize; n++) {
			/**
			 * Verifica se a posição atual continua á mesma desde a última chamada do
			 * método atual. Se sim, verifica se o o alvo mudou de lugar, para alguma
			 * posição que ainda esteja na reta do percurso, para encurtar o caminho
			 * sem refaze-lo do 0, mantendo uma rota constante.
			 */
		}
		return false;
	}
	
	public void setDebug(Boolean b) { debug = b; }
	
	public void updatePath(Position currentPosition, Direction currentDirection, Position targetPosition) {
		firstStep = true;
		tempTiles.clear();
		foundPaths.clear();
		
		if (currentPosition.equals(targetPosition))
			return;
		
		/* Corta o início do 'currentPath' se o 'currentPosition' não for mais
		 * a posição do index 0, mas ainda estiver dentro de 'currentPath'
		 */
		int foundTargetVal, n;
		if (!this.currentPosition.equals(currentPosition) &&
				!pathNotFound() && currentPath.contains(currentPosition) &&
				(n = currentPath.indexOf(currentPosition)) < currentPath.size() - 1) {
					currentPath = currentPath.subList(n + 1, currentPath.size());
					this.startDirection = Direction.getDirectionThroughPositions(currentPosition, currentPath.get(0));
					this.currentPosition.setPosition(currentPosition);
					return;
		}

		if (this.currentPosition != null && !currentPath.isEmpty() &&
				currentPosition.equals(currentPath.get(0))) {
					if (tryToConnectCurrentPathToNewTarget(currentPosition, targetPosition))
						return;
					if (tryToTrimPath(targetPosition))
						return;
		}

		currentPath.clear();
		this.currentPosition.setPosition(currentPosition);
		this.targetPosition.setPosition(targetPosition);
		startPosition.setPosition(currentPosition);
		startDirection = currentDirection;
		startFreeDirs = getRandomizedListOfFreeDirections();
		
		// Garante que a direção oposta fique por último, e só será usada caso nenhum caminho tiver sido encontrado nas outras direções
		if (ignoreReverseDirectionAtStartPosition == PathFindIgnoreReverseDirectionAtStartPosition.ONLY_IF_ITS_THE_ONLY_AVAILABLE_DIRECTION &&
				tileIsFree(startPosition, startDirection.getReverseDirection())) {
					if (startFreeDirs == null)
						startFreeDirs = Arrays.asList(startDirection.getReverseDirection());
					else {
						startFreeDirs.remove(startDirection.getReverseDirection());
						startFreeDirs.add(startDirection.getReverseDirection());
					}
		}
		
		if (startFreeDirs == null || isTargetFound())
			return;

		for (Direction firstDir : startFreeDirs) {
			firstStep = firstDir != startDirection.getReverseDirection() || !foundPaths.isEmpty();
			this.currentPosition.setPosition(startPosition);
			currentPath.clear();
			foundTargetVal = 3;
			addNewPosition(firstDir);
			echoField();
			do {
				reversing = false;
				while (!isStucked() && !isTargetFound()) { // Vai seguindo em frente em direções aleatórias que estejam LIVRES, até ficar preso ou encontrar o alvo
					addNewPosition(getLogicalFreeDirection());
					echoField();
					if (++foundTargetVal == 3) 
						tempTiles.remove(this.targetPosition);
				} 
				if (isTargetFound()) { // Se encontrou o tile alvo, otimiza o caminho gerado
					optmizePath();
					foundPaths.add(new ArrayList<>(currentPath));
					foundTargetVal = 1;
				}
				do {
					/* Se o caminho atual ficou sem saída ou encontrou o tile-alvo,
					 * faz o caminho reverso até achar outra direção alternativa livre.
					 */
					reversing = true;
					n = currentPath.size() - 1;
					tempTiles.add(new Position(currentPath.get(n)));
					currentPath.remove(n);
					if (!currentPath.isEmpty())
						this.currentPosition.setPosition(currentPath.get(n - 1));
					else
						this.currentPosition.setPosition(startPosition);
					echoField();
				}
				while (isStucked() && !currentPath.isEmpty());
			}
			while (!isStucked());
		}
		n = foundPaths.size();
		if (n == 0)
			currentPath.clear();
		else {
			if (pathFindType == PathFindType.AVERAGE_PATH)
				n = random.nextInt(n);
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
		}
		//foundPaths.clear();
		this.currentPosition.setPosition(startPosition);
		tempTiles.clear();
	}
	
	public List<List<Position>> getFoundPaths() { return foundPaths; }

	private void echoField() {
		if (debug) {
			Position p = new Position();
			for (int x, y = 0; y < 19; y++) {
				for (x = 0; x < 25; x++) {
					p.setPosition(x, y);
					System.out.print(currentPosition.equals(p) ? (reversing ? '#' : '@') :
													 targetPosition.equals(p) ? '!' :
													 tempTiles.contains(p) ? 'T' : currentPath.contains(p) ? 'O' :
													 tileIsFree(p) ? ' ' : 'X');
				}
				System.out.println();
			}
			System.out.println();
			sc.nextLine();
		}
	}
	
	private void addNewPosition(Direction direction) {
		currentPosition.incPositionByDirection(direction);
		currentPath.add(new Position(currentPosition));
		firstStep = false;
	}

	public void updatePath(Position currentPosition, Direction currentDirection)
		{ updatePath(currentPosition, currentDirection, targetPosition); }
	
	public void setFindType(PathFindType type) {
		pathFindType = type;
		updatePath(currentPosition, getCurrentDirection(), targetPosition);
	}
	
	/**
	 * Verifica se algum dos tiles que formam o percurso atual até o alvo foi bloqueado
	 */
	public Boolean isCurrentPathBlocked() {
		if (currentPath.isEmpty())
			return true;
		for (Position position : currentPath)
			if (!position.equals(getNextPosition()) && !tileIsFree(position, true))
				return true;
		return false;
	}
	
	private void optmizePath() {
		Boolean found = false;
		Position po = new Position(), tPos = new Position();
		for (int t, i, p = 0; p < (currentPath.size() - 1); p++) {
			if (found)
				p = 0;
			po.setPosition(currentPath.get(p));
			found = false;
			for (Direction d : allDirs) {
				tPos.setPosition(po);
				t = 0;
				do {
					tPos.incPositionByDirection(d);
					t++;
					i = currentPath.indexOf(tPos);
					if ((t > 1 && tPos.equals(targetPosition)) || i > p + 5) {
						if (t > 1 && tPos.equals(targetPosition))
							i = currentPath.size() - 1;
						while (--i >= p)
							currentPath.remove(p);
						while (--t >= 0) {
							tPos.incPositionByDirection(d.getReverseDirection());
							currentPath.add(p, new Position(tPos));
							tempTiles.remove(tPos);
						}
						found = true;
					}
				}
				while (!found && (tileIsFree(tPos) || tempTiles.contains(tPos)));
			}
		}
	}

	private List<Direction> getRandomizedListOfFreeDirections() {
		int n, totalAvailableDirs = 0;
		List<Direction> availableDirs = new ArrayList<>();
		
		while (totalAvailableDirs < 4) {
			n = random.nextInt(4);
			while (availableDirs.contains(allDirs.get(n)))
				if (++n == 4)
					n = 0;
			availableDirs.add(allDirs.get(n));
			totalAvailableDirs++;
		}
		if (ignoreReverseDirectionAtStartPosition != PathFindIgnoreReverseDirectionAtStartPosition.NO_IGNORE &&
				firstStep && availableDirs.contains(startDirection.getReverseDirection())) {
					availableDirs.remove(startDirection.getReverseDirection());
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
		Position coord = new Position(position);
		if (direction != null)
			coord.incPositionByDirection(direction);
		return tileIsFree.apply(coord) && !tempTiles.contains(coord) && !startPosition.equals(coord) &&
						(ignoreCurrentPathPositions || !currentPath.contains(coord));
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
							getCurrentDirection() == Direction.UP &&
							tileIsFree(currentPosition, Direction.UP))
								d = Direction.UP;
					else if (targetPosition.getY() > currentPosition.getY() &&
							getCurrentDirection() == Direction.DOWN &&
							tileIsFree(currentPosition, Direction.DOWN))
								d = Direction.DOWN;
					else if (targetPosition.getX() < currentPosition.getX() &&
							getCurrentDirection() == Direction.LEFT &&
							tileIsFree(currentPosition, Direction.LEFT))
								d = Direction.LEFT;
					else if (targetPosition.getX() > currentPosition.getX() &&
							getCurrentDirection() == Direction.RIGHT &&
							tileIsFree(currentPosition, Direction.RIGHT))
								d =	Direction.RIGHT;
					if (d != null && (getCurrentDirection().getReverseDirection() != d || !firstStep ||
							ignoreReverseDirectionAtStartPosition == PathFindIgnoreReverseDirectionAtStartPosition.NO_IGNORE))
								return d;
		}
		if (getCurrentDirection() != null)
			for (Direction dir : allDirs) // Direção baseada na posição do tile alvo em relação a posição atual
				if (((dir == Direction.LEFT && targetPosition.getX() < currentPosition.getX()) ||
						(dir == Direction.RIGHT && targetPosition.getX() > currentPosition.getX()) ||
						(dir == Direction.UP && targetPosition.getY() < currentPosition.getY()) ||
						(dir == Direction.DOWN && targetPosition.getY() > currentPosition.getY())) &&
						(getCurrentDirection() != dir &&
						(getCurrentDirection().getReverseDirection() != dir || !firstStep ||
						ignoreReverseDirectionAtStartPosition == PathFindIgnoreReverseDirectionAtStartPosition.NO_IGNORE) &&
						tileIsFree(currentPosition, dir)))
							return dir;
		List<Direction> dirs = getRandomizedListOfFreeDirections();
		return dirs == null ? null : dirs.get(random.nextInt(dirs.size()));
	}

	public Direction getCurrentDirection() {
		if (currentPath.isEmpty())
			return null;
		return Direction.getDirectionThroughPositions(currentPosition, currentPath.get(0));
	}

	private Boolean isStucked()
		{ return getRandomizedListOfFreeDirections() == null; }
	
	public void removeCurrentPosition() {
		if (!currentPath.isEmpty()) {
			currentPosition.setPosition(currentPath.get(0));
			currentPath.remove(0);
		}
	}

	public Position getNextPosition()
		{ return currentPath.isEmpty() ? null : currentPath.get(0); }

	public List<Position> getPathPositions()
		{ return currentPath; }
	
	public Boolean isTargetFound()
		{ return currentPosition.equals(targetPosition); }
	
	public Boolean pathNotFound()
		{ return !isTargetFound() && (isCurrentPathBlocked() || currentPath.isEmpty()); }

}
