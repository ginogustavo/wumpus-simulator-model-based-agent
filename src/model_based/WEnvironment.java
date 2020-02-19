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
	private boolean isArrowAvailable;
	private boolean isEnvUpdatedAfterShot;
	private boolean isWumpusAlive;

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
		setArrowAvailable(true);
		setCurrentAgentCoordinate(new int[] { 1, 1 });
		setCurrentDirection('>');
		setLastMove(Move.START);
		setLastSquareCoordinate(new int[] { 0, 0 });
		setPendingActions(new LinkedList<Move>());
		setEnvUpdatedAfterShot(false);
		setWumpusAlive(true);
	}

	public Hashtable<Integer, Square> getSquares() {
		return squares;
	}

	public void setSquares(Hashtable<Integer, Square> squares) {
		this.squares = squares;
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

	public boolean isWumpusAlive() {
		return isWumpusAlive;
	}

	public void setWumpusAlive(boolean isWumpusAlive) {
		this.isWumpusAlive = isWumpusAlive;
	}

	public boolean isArrowAvailable() {
		return isArrowAvailable;
	}

	public void setArrowAvailable(boolean isArrowAvailable) {
		this.isArrowAvailable = isArrowAvailable;
	}

	public boolean isEnvUpdatedAfterShot() {
		return isEnvUpdatedAfterShot;
	}

	public void setEnvUpdatedAfterShot(boolean isEnvUpdatedAfterShot) {
		this.isEnvUpdatedAfterShot = isEnvUpdatedAfterShot;
	}

	/**
	 * Update the adjacent squares with the given state. Additional validations
	 * apply.
	 * 
	 * @param state
	 */
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
						&& getSquares().get(key).getState() != State.OK_NOT_VISITED
						&& getSquares().get(key).getState() != State.OK_MORE_1_VISIT) {
					getSquares().get(key).setState(state);
				}
			}
		}
	}

	public int getNumber(int[] coordinates) {
		return Integer.parseInt(coordinates[0] + "" + coordinates[1]);
	}

	public List<Integer> getAdjacentLocation() {
		return getAdjacentList(getCurrentAgentCoordinate(), getLastSquarePosition());
	}

	public List<Integer> getAdjacentLocation(int[] currentPosition, int lastPosition) {
		return getAdjacentList(currentPosition, lastPosition);
	}

	/**
	 * Retrieve the adjacent squares based on current position and last position.
	 * 
	 * @param currPos
	 * @param lastPosition
	 * @return
	 */
	public List<Integer> getAdjacentList(int[] currPos, int lastPosition) {
		int x = currPos[0];
		int y = currPos[1];
		int lastPos = lastPosition;

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

	/**
	 * Based on next location, we can calculate the next moves required. (based on ,
	 * TURN_RIGHT, TURN_LEFT, GO_FORWARD.
	 * 
	 * @param nextPosition
	 * @return
	 */
	public Queue<Move> getNextMoves(int nextPosition) {
		int x = getCurrentAgentCoordinate()[0];
		int y = getCurrentAgentCoordinate()[1];

		String strNextPos = String.valueOf(nextPosition);
		int newX = Integer.parseInt(strNextPos.substring(0, 1));
		int newY = Integer.parseInt(strNextPos.substring(1));

		Queue<Move> pendingMoves;

		if (x == newX) { // Horizontal
			if (y < newY) { // To Right
				pendingMoves = processDirection('>', getCurrentDirection());
			} else { // To Left
				pendingMoves = processDirection('<', getCurrentDirection());
			}
		} else { // Vertical
			if (x < newX) { // Up
				pendingMoves = processDirection('A', getCurrentDirection());
			} else { // Down
				pendingMoves = processDirection('V', getCurrentDirection());
			}
		}
		return pendingMoves;
	}

	/**
	 * Based on the current direction and the desired move, get the needed moves in
	 * a queue.
	 * 
	 * @param move
	 * @param currentDirection
	 * @return
	 */
	public Queue<Move> processDirection(char move, char currentDirection) {
		char cur_dir = currentDirection;
		Queue<Move> pendingMoves = new LinkedList<Move>();

		if ((cur_dir == '>' && move == '>') || (cur_dir == '<' && move == '<') || (cur_dir == 'A' && move == 'A')
				|| (cur_dir == 'V' && move == 'V')) {
			// pendingMoves.add(Move.GO_FORWARD);

		} else if ((cur_dir == '>' && move == '<') || (cur_dir == '<' && move == '>') || (cur_dir == 'A' && move == 'V')
				|| (cur_dir == 'V' && move == 'A')) {
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.TURN_RIGHT);

		} else if ((cur_dir == '>' && move == 'V') || (cur_dir == '<' && move == 'A') || (cur_dir == 'A' && move == '>')
				|| (cur_dir == 'V' && move == '<')) {
			pendingMoves.add(Move.TURN_RIGHT);

		} else if ((cur_dir == '>' && move == 'A') || (cur_dir == '<' && move == 'V') || (cur_dir == 'A' && move == '<')
				|| (cur_dir == 'V' && move == '>')) {
			pendingMoves.add(Move.TURN_LEFT);
		}

		return pendingMoves;
	}

	/**
	 * Rule 08: From the Adjacent squares, take the preference to move next
	 * followinG: UNKNOWN, NOT_VISITED, OK, VISITED_ALREADY, STENCH, BREEZE
	 * 
	 * @return
	 */
	public Queue<Move> moveToSides() {
		List<Integer> list = getAdjacentLocation();
		boolean needToShoot = false;
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
		} else if (adjMap.get(State.OK_MORE_1_VISIT) != null) {
			nextPosition = adjMap.get(State.OK_MORE_1_VISIT);
			needToShoot = true;
		} else if (adjMap.get(State.STENCH) != null) {
			nextPosition = adjMap.get(State.STENCH);
			needToShoot = true; // go to a stench and get possible Wumpus, turn to it and shoot
		} else if (adjMap.get(State.BREEZE) != null) {
			nextPosition = adjMap.get(State.BREEZE);
		}
		Queue<Move> pendingMoves = getNextMoves(nextPosition);
		pendingMoves.add(Move.GO_FORWARD);

		if (needToShoot) {
			if (isArrowAvailable) {
				pendingMoves.add(Move.LOOK_FOR_WUMPUS);
				setArrowAvailable(false);
			}
		}
		return pendingMoves;
	}

	/**
	 * Once Decided to move and shot, just add the respective movements to the
	 * queue.
	 */
	public void addMovesToPointAndShoot() {
		List<Integer> nextPositions = getAdjacentLocation();
		Hashtable<State, Integer> adjMap = new Hashtable<>(4);
		for (Integer key : nextPositions) {
			State st = getSquares().get(key).getState();
			adjMap.put(st, key);
		}
		int possWumpusPos = getLastSquarePosition();
		if (adjMap.get(State.POSSIBLE_WUMPUS) != null) {
			possWumpusPos = adjMap.get(State.POSSIBLE_WUMPUS);
		}
		Queue<Move> pendingMoves = getNextMoves(possWumpusPos);
		pendingMoves.add(Move.SHOT);
		setPendingActions(pendingMoves);
	}

	/**
	 * For no sense just update to OK to the adjacent squares. Extra validations
	 * apply
	 */
	public void setOKAdjacent() {
		List<Integer> list = getAdjacentLocation();
		for (Integer key : list) {
			State st = getSquares().get(key).getState();
			if (st != State.OK && st != State.OK_MORE_1_VISIT && st != State.BREEZE && st != State.STENCH
					&& st != State.BREEZE_AND_STENCH && st != State.WUMPUS && st != State.PIT) {
				getSquares().get(key).setState(State.OK_NOT_VISITED); // Interpreted as OK
			}
		}
	}

	/**
	 * Once Breeze is found, just go back to try new paths
	 */
	public void foundRisk() {
		Queue<Move> pendingMoves = new LinkedList<Move>();
		pendingMoves.add(Move.TURN_RIGHT);
		pendingMoves.add(Move.TURN_RIGHT);
		pendingMoves.add(Move.GO_FORWARD);
		setPendingActions(pendingMoves);
	}

	/**
	 * Once Stench is sensed, just validate and see if it has to turn back or look
	 * for th Wumpus(to kill it)
	 */
	public void foundWumpusRisk() {
		Queue<Move> pendingMoves = new LinkedList<Move>();
		State prevSate = getSquares().get(getLastSquarePosition()).getState();

		if (isArrowAvailable() && prevSate == State.OK_MORE_1_VISIT) {
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.LOOK_FOR_WUMPUS);
			setPendingActions(pendingMoves);

		} else {
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.TURN_RIGHT);
			pendingMoves.add(Move.GO_FORWARD);
			setPendingActions(pendingMoves);
		}
	}

	/**
	 * Update the squares properly depending if the Wumpus was killed or not
	 * 
	 * @param killed
	 */
	public void updateAfterShot(boolean killed) {
		if (killed) {
			setWumpusAlive(false);
			List<Integer> list = getListArrowWent();
			for (Integer key : list) {
				State st = getSquares().get(key).getState();
				if (st == State.BREEZE_AND_STENCH) {
					getSquares().get(key).setState(State.BREEZE);
				}
				if (st == State.POSSIBLE_PIT_OR_WUMPUS) {
					getSquares().get(key).setState(State.POSSIBLE_PIT);
				}
				if (st != State.BREEZE && st != State.POSSIBLE_PIT) {
					getSquares().get(key).setState(State.OK);
				}
			}
		} else {
			List<Integer> list = getListArrowWent();
			for (Integer key : list) {
				State st = getSquares().get(key).getState();
				if (st == State.POSSIBLE_WUMPUS) {
					getSquares().get(key).setState(State.OK);
				}
				if (st == State.POSSIBLE_PIT_OR_WUMPUS) {
					getSquares().get(key).setState(State.POSSIBLE_PIT);
				}
			}
		}
	}

	/**
	 * Used when arrow was shot, get those squares impacted
	 * 
	 * @return
	 */
	public List<Integer> getListArrowWent() {
		int x = getCurrentAgentCoordinate()[0];
		int y = getCurrentAgentCoordinate()[1];
		char curr_dir = getCurrentDirection();
		List<Integer> list = new ArrayList<Integer>();

		if (curr_dir == '>') {
			for (int i = y + 1; i <= 4; i++) {
				list.add(Integer.parseInt(x + "" + i));
			}
		} else if (curr_dir == '<') {
			for (int i = 1; i < y; i++) {
				list.add(Integer.parseInt(x + "" + i));
			}
		} else if (curr_dir == 'A') {
			for (int i = x + 1; i <= 4; i++) {
				list.add(Integer.parseInt(i + "" + y));
			}
		} else { // V
			for (int i = 1; i < x; i++) {
				list.add(Integer.parseInt(i + "" + y));
			}
		}
		return list;
	}

}