package model_based;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WEnvironment {
	private Hashtable<Integer, Square> squares;
	private Queue<Move> pendingActions;
	private int[] currentAgentCoordinate;
	private char currentDirection;
	private Move lastMove;
	private int[] lastSquareCoordinate;

	public WEnvironment() {
		Hashtable<Integer, Square> squares = new Hashtable<>(16);
		int key;
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				key = Integer.parseInt(i + "" + j);
				squares.put(key, new Square(new int[] { i, j }, State.UNKNOWN));
			}
		}
		setSquares(squares);
	}

	public Hashtable<Integer, Square> getSquares() {
		return squares;
	}

	public void setSquares(Hashtable<Integer, Square> squares) {
		this.squares = squares;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Hashtable<Integer, Square> squares = getSquares();
		int key;
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				key = Integer.parseInt(i + "" + j);
				Square s = squares.get(key);
				sb.append(" -- " + key + " state:" + s.getState().name() + "\n");
			}
		}
		return sb.toString();
	}

	public Queue<Move> getPendingActions() {
		return pendingActions;
	}

	public void setPendingActions(Queue<Move> pendingActions) {
		this.pendingActions = pendingActions;
	}

	public char getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(char currentDirection) {
		this.currentDirection = currentDirection;
	}

	public Move getLastMove() {
		return lastMove;
	}

	public void setLastMove(Move lastMove) {
		this.lastMove = lastMove;
	}

	public int[] getCurrentAgentCoordinate() {
		return currentAgentCoordinate;
	}

	public void setCurrentAgentCoordinate(int[] currentAgentCoordinate) {
		this.currentAgentCoordinate = currentAgentCoordinate;
	}

	public int getCurrentAgentPosition() {
		return Integer.parseInt(getCurrentAgentCoordinate()[0] + "" + getCurrentAgentCoordinate()[1]);
	}

	public int[] getLastSquareCoordinate() {
		return lastSquareCoordinate;
	}

	public void setLastSquareCoordinate(int[] lastSquareCoordinate) {
		this.lastSquareCoordinate = lastSquareCoordinate;
	}

	public int getLastSquarePosition() {
		return Integer.parseInt(getLastSquareCoordinate()[0] + "" + getLastSquareCoordinate()[1]);
	}

	public void updateAdjacents(State state) {
		List<Integer> adjacentLocation = getAdjacentLocation();

		if (adjacentLocation.size() == 1) {
			if (state == State.POSSIBLE_PIT)
				getSquares().get(adjacentLocation.get(0)).setState(State.PIT);
			if (state == State.POSSIBLE_WUMPUS)
				getSquares().get(adjacentLocation.get(0)).setState(State.WUMPUS);
		} else {
			for (Integer key : adjacentLocation) {
				if (getSquares().get(key).getState() != State.OK
						|| getSquares().get(key).getState() != State.OK_NOT_VISITED) {
					getSquares().get(key).setState(state);
				}
			}
		}
	}

	public int getNumber(int[] coordinates) {
		return Integer.parseInt(coordinates[0] + "" + coordinates[1]);
	}

	/**
	 * Return adjacent squares less the previous location
	 * 
	 * @return
	 */
	public List<Integer> getAdjacentLocation() {
		int x = getCurrentAgentCoordinate()[0];
		int y = getCurrentAgentCoordinate()[1];

		int lastPos = getLastSquarePosition();

		List<Integer> adjacentPositions = new ArrayList<Integer>();

		int new_num;
		int new_position;

		new_num = y + 1;
		new_position = Integer.parseInt(x + "" + new_num);
		if ((new_num >= 1 && new_num <= 4) && (new_position != lastPos)) {
			adjacentPositions.add(new_position);
		}
		new_num = y - 1;
		new_position = Integer.parseInt(x + "" + new_num);
		if ((new_num >= 1 && new_num <= 4) && (new_position != lastPos)) {
			adjacentPositions.add(new_position);
		}
		new_num = x + 1;
		new_position = Integer.parseInt(new_num + "" + y);
		if ((new_num >= 1 && new_num <= 4) && (new_position != lastPos)) {
			adjacentPositions.add(new_position);
		}
		new_num = x - 1;
		new_position = Integer.parseInt(new_num + "" + y);
		if ((new_num >= 1 && new_num <= 4) && (new_position != lastPos)) {
			adjacentPositions.add(new_position);
		}

		return adjacentPositions;
	}

	public Queue<Move> getNextMoves(int nextPosition) {
		int x = getCurrentAgentCoordinate()[0];
		int y = getCurrentAgentCoordinate()[1];

		String strNextPos = String.valueOf(nextPosition);
		int newX = Integer.parseInt(strNextPos.substring(0, 1));
		int newY = Integer.parseInt(strNextPos.substring(1));

		if (x == newX) { // Horizontal
			if (y < newY) { // To Right
				return processDirection('>');
			} else { // To Left
				return processDirection('<');
			}
		} else { // Vertical
			if (x < newX) { // Up
				return processDirection('A');
			} else { // Down
				return processDirection('V');
			}
		}
	}

	public Queue<Move> processDirection(char move) {
		char cur_dir = getCurrentDirection();
		Queue<Move> pendingMoves = new LinkedList<Move>();

		if ((cur_dir == '>' && move == '>') || (cur_dir == '<' && move == '<') || (cur_dir == 'A' && move == 'A')
				|| (cur_dir == 'V' && move == 'V')) {
			pendingMoves.add(Move.GO_FORWARD);

		} else if ((cur_dir == '>' && move == '<') || (cur_dir == '<' && move == '>') || (cur_dir == 'A' && move == 'V')
				|| (cur_dir == 'V' && move == 'A')) {
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.GO_FORWARD);

		} else if ((cur_dir == '>' && move == 'V') || (cur_dir == '<' && move == 'A') || (cur_dir == 'A' && move == '>')
				|| (cur_dir == 'V' && move == '<')) {
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.GO_FORWARD);

		} else if ((cur_dir == '>' && move == 'A') || (cur_dir == '<' && move == 'V') || (cur_dir == 'A' && move == '<')
				|| (cur_dir == 'V' && move == '>')) {
			pendingMoves.add(Move.TURN_LEFT);
			pendingMoves.add(Move.GO_FORWARD);
		}

		return pendingMoves;
	}

	public boolean isValidtoMoveFoward() {
		int x = getCurrentAgentCoordinate()[0];
		int y = getCurrentAgentCoordinate()[1];
		char cur_dir = getCurrentDirection();

		if (x == 1 && cur_dir == 'V') {
			return false;
		}
		if (x == 4 && cur_dir == 'A') {
			return false;
		}
		if (y == 1 && cur_dir == '<') {
			return false;
		}
		if (y == 4 && cur_dir == '>') {
			return false;
		}
		return true;
	}

	public Queue<Move> moveToSides() {
		List<Integer> list = getAdjacentLocation();
		Hashtable<State, Integer> adjMap = new Hashtable<>(4);
		for (Integer key : list) {
			State st = getSquares().get(key).getState();
			adjMap.put(st, key);
		}
		int nextPosition = getLastSquarePosition();
		if (adjMap.get(State.UNKNOWN) != null) {
			nextPosition = adjMap.get(State.UNKNOWN);
		} else if (adjMap.get(State.OK_NOT_VISITED) != null) {
			nextPosition = adjMap.get(State.OK_NOT_VISITED);
		} else if (adjMap.get(State.OK) != null) {
			nextPosition = adjMap.get(State.OK);
		} else if (adjMap.get(State.BREEZE) != null) {
			nextPosition = adjMap.get(State.BREEZE);
		} else if (adjMap.get(State.STENCH) != null) {
			nextPosition = adjMap.get(State.STENCH);
		}
		Queue<Move> pendingMoves = getNextMoves(nextPosition);
		return pendingMoves;
	}

	public void setOKAdjacent() {
		List<Integer> list = getAdjacentLocation();
		for (Integer key : list) {
			State st = getSquares().get(key).getState();
			if (st != State.OK && st != State.BREEZE && st != State.STENCH && st != State.BREEZE_AND_STENCH
					&& st != State.WUMPUS && st != State.PIT) {
				getSquares().get(key).setState(State.OK_NOT_VISITED); // Interpreted as OK
			}
		}
	}

	public void foundRisk() {
		Queue<Move> pendingMoves = new LinkedList<Move>();
		pendingMoves.add(Move.TURN_RIGHT);
		pendingMoves.add(Move.TURN_RIGHT);
		pendingMoves.add(Move.GO_FORWARD);
		setPendingActions(pendingMoves);
	}

}
