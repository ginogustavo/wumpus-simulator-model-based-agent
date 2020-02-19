/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */


import java.util.Queue;
import java.util.Random;

import model_based.Move;
import model_based.State;
import model_based.WEnvironment;

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";
	
	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private int[] actionTable;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private Random rand;

	public AgentFunction()
	{
		// for illustration purposes; you may delete all code
		// inside this constructor when implementing your 
		// own intelligent agent

		// this integer array will store the agent actions
		actionTable = new int[8];
				  
		actionTable[0] = Action.GO_FORWARD;
		actionTable[1] = Action.GO_FORWARD;
		actionTable[2] = Action.GO_FORWARD;
		actionTable[3] = Action.GO_FORWARD;
		actionTable[4] = Action.TURN_RIGHT;
		actionTable[5] = Action.TURN_LEFT;
		actionTable[6] = Action.GRAB;
		actionTable[7] = Action.SHOOT;
		
		// new random number generator, for
		// randomly picking actions to execute
		rand = new Random();
	}

	public int process(TransferPercept tp, WEnvironment wenv)
	{
		// To build your own intelligent agent, replace
		// all code below this comment block. You have
		// access to all percepts through the object
		// 'tp' as illustrated here:
		
		// read in the current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();
		
		int squareKey = wenv.getCurrentAgentPosition();

		if (bump == true || glitter == true || breeze == true || stench == true || scream == true) {
			
			// Rule 01: When feel "Glitter" perform "GRAB"
			if (glitter) {
				return Action.GRAB;
			}

			// Rule 02: if has pending actions, then, perform those first.
			if (!wenv.getPendingActions().isEmpty()) {
				Move move = wenv.getPendingActions().remove();
				if (move == Move.LOOK_FOR_WUMPUS) {
					wenv.addMovesToPointAndShoot(); // methods sets more actions.
					move = wenv.getPendingActions().remove();
				}
				return move(move, wenv, null);
			}

			// Rule 03: After Shot (using available arrow property), Wumpus did not get killed.
			if (!wenv.isArrowAvailable() && !wenv.isEnvUpdatedAfterShot()) {
				wenv.updateAfterShot(false); // Not Killed
				wenv.setEnvUpdatedAfterShot(true);
			}

			//Rule 04: Sensors can detect Breeze, Stench and both in the same square
			//So, update it with the appropriate states. 
			if (breeze && stench) {
				wenv.getSquares().get(squareKey).setState(State.BREEZE_AND_STENCH);
				wenv.updateAdjacents(State.POSSIBLE_PIT_OR_WUMPUS);

				if (wenv.getCurrentAgentPosition() == 11) {
					if (wenv.getLastSquarePosition() == 0) {
						wenv.setArrowAvailable(false);
						return Action.SHOOT;
					} else {
						return Action.GRAB;
					}
				} else {
					wenv.foundRisk();
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}
			} else if (breeze) {
				wenv.getSquares().get(squareKey).setState(State.BREEZE);
				wenv.updateAdjacents(State.POSSIBLE_PIT);
				if (wenv.getCurrentAgentPosition() == 11 && wenv.getLastSquarePosition() == 0) {
					return Action.GRAB;
				} else {
					wenv.foundRisk();
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}

			} else if (stench) {
				wenv.getSquares().get(squareKey).setState(State.STENCH);
				wenv.updateAdjacents(State.POSSIBLE_WUMPUS);
				if (wenv.getCurrentAgentPosition() == 11 && wenv.getLastSquarePosition() == 0) {
					wenv.setArrowAvailable(false);
					return Action.SHOOT;
				} else {
					wenv.foundWumpusRisk();
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}
			}
			
			//Rule 05: Once perceive Scream, Wumpus got killed, then update squares appropriately.
			if (scream) {
				wenv.updateAfterShot(true); // Killed
				return move(Move.GO_FORWARD, wenv, State.OK);
			}


			// Rule 06: When sense "Bump", just turn to available side
			if (bump) {
				Queue<Move> pendingMoves = wenv.moveToSides();
				wenv.setPendingActions(pendingMoves);
				if (!pendingMoves.isEmpty()) {
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}
			}
			
		}else {
			
			
			//Rule 07: Sensors, did not catch anything, So, we can update current and Adjacent squares
			//Move to one of the either sides.		
			if (wenv.getSquares().get(squareKey).getState() == State.OK) {
				wenv.getSquares().get(squareKey).setState(State.OK_MORE_1_VISIT);
			}
			if (wenv.getSquares().get(squareKey).getState() != State.OK_MORE_1_VISIT) {
				wenv.getSquares().get(squareKey).setState(State.OK);
			}
			wenv.setOKAdjacent();
			Queue<Move> pendingMoves = wenv.moveToSides();
			wenv.setPendingActions(pendingMoves);
			Move nextMove = wenv.getPendingActions().remove();
			return move(nextMove, wenv, null); //State.OK
			
		}
		return Action.GO_FORWARD;
	}
	

	
	
	/**
	 * Based on the given move, validate the change of direction or location
	 * 
	 * @param move to perform
	 * @param environment variable
	 * @param state to set in the square
	 * @return Action to perform (Framework require type Action)
	 */
	public int move(Move move, WEnvironment wenv, State state) {
		int[] curr_pos = wenv.getCurrentAgentCoordinate();
		int[] new_pos = {curr_pos[0], curr_pos[1]};
		
		char direction = wenv.getCurrentDirection();
		char newdirection = '>';
		if (state!=null)
			wenv.getSquares().get(wenv.getCurrentAgentPosition()).setState(state);

		switch(move) {
			case SHOT: 
				wenv.setArrowAvailable(false);
				return Action.SHOOT; 
			case GO_FORWARD:
				wenv.setLastSquareCoordinate(curr_pos);
				//update position
				switch(direction) {
					case '>':
						new_pos[1] = new_pos[1] + 1; 
						break;
					case '<':
						new_pos[1] = new_pos[1] - 1; 
						break;
					case 'A':
						new_pos[0] = new_pos[0] + 1;
						break;
					default:
						new_pos[0] = new_pos[0] - 1;
						break;
				}
				wenv.setCurrentAgentCoordinate(new_pos);
				wenv.setLastMove(Move.GO_FORWARD);
				return Action.GO_FORWARD;
			case TURN_LEFT:
				//update direction
				switch(direction) {
					case '>':
						newdirection = 'A';
						break;
					case '<':
						newdirection = 'V';
						break;
					case 'A':
						newdirection = '<';
						break;
					default:
						newdirection = '>';
						break;
				}
				wenv.setCurrentDirection(newdirection);
				wenv.setLastMove(Move.TURN_LEFT);
				return Action.TURN_LEFT;
			default: //case TURN_RIGHT
				//update direction
				switch(direction) {
					case '>':
						newdirection = 'V';
						break;
					case '<':
						newdirection = 'A';
						break;
					case 'A':
						newdirection = '>';
						break;
					default:
						newdirection = '<';
						break;
				}
				wenv.setCurrentDirection(newdirection);
				wenv.setLastMove(Move.TURN_RIGHT);
				return Action.TURN_RIGHT;
		}		
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}