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
		//System.out.println(wenv);
		//printStatus(wenv);
		if (bump == true || glitter == true || breeze == true || stench == true || scream == true) {
			// Rule 01: When feel "Glitter" perform "GRAB"
			if (glitter) {
				return Action.GRAB;
			}

			// Rule 02: if has actions pending, then, perform those.
			if(!wenv.getPendingActions().isEmpty()) {
				return move(wenv.getPendingActions().remove(), wenv, null);
			}
			
			if(breeze && stench) {
				wenv.getSquares().get(squareKey).setState(State.BREEZE_AND_STENCH);
				wenv.updateAdjacents(State.POSSIBLE_PIT_OR_WUMPUS);
				if(wenv.getCurrentAgentPosition()==11) {
					return Action.GRAB;
				}else {
					// TODO: Analyze history is no history , exit
					wenv.foundRisk();						
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}
				//Set queue actions of returning back (R, R, F) // TODO ANALYZE possible scenarios
			}else if (breeze) {
				//Mark current one as OK-withBREEZE
				wenv.getSquares().get(squareKey).setState(State.BREEZE);
				//Mark adjacent squares as possible PIT
				wenv.updateAdjacents(State.POSSIBLE_PIT);
				//Set queue actions of returning back (R, R, F) // TODO ANALYZE possible scenarios
				
				if(wenv.getCurrentAgentPosition()==11) {
					return Action.GRAB;
				}else {
					// TODO: Analyze history is no history , exit
					wenv.foundRisk();						
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}
				
			}else if (stench) {
				//Mark current one as OK-withSTENCH
				wenv.getSquares().get(squareKey).setState(State.STENCH);
				//Mark adjacent squares as possible WUMPUS
				wenv.updateAdjacents(State.POSSIBLE_WUMPUS);
				if(wenv.getCurrentAgentPosition()==11) {
					return Action.GRAB;
				}else {
					// TODO: Analyze history is no history , exit
					wenv.foundRisk();						
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}

			}
			
			// Rule 02: You Killed the Wumpus in your last move, you could move forward.
			// But there could be a Pit but also the Gold.
			if (scream) {
				return Action.GO_FORWARD;
			}

			// Rule 03: When sense "Bump", just turn to either of the sides.
			// We have no percept history to determine our last turn or direction.
			if (bump) {
				
				Queue<Move> pendingMoves = wenv.moveToSides();
				wenv.setPendingActions(pendingMoves);
				if(!pendingMoves.isEmpty()) {
					Move move = wenv.getPendingActions().remove();
					return move(move, wenv, null);
				}
			
			}
			
		}else {
			wenv.getSquares().get(squareKey).setState(State.OK);
			wenv.setOKAdjacent();
			Queue<Move> pendingMoves = wenv.moveToSides();
			wenv.setPendingActions(pendingMoves);
			
			Move nextMove = wenv.getPendingActions().remove();
			
			return move(nextMove, wenv, State.OK);
		}
		return Action.GO_FORWARD; //randomMove
	}
	
	public int randomMove() {
		int num = rand.nextInt(3);
		switch (num) {
		case 1:
			return Action.GO_FORWARD;
		case 2:
			return Action.TURN_LEFT;
		default:
			return Action.TURN_RIGHT;
		}
	}

	
	public void printStatus(WEnvironment we) {
		int x = we.getCurrentAgentCoordinate()[0];
		int y = we.getCurrentAgentCoordinate()[1];
		int pos = we.getCurrentAgentPosition();
		char dir = we.getCurrentDirection();
		Move mov = we.getLastMove();
		int[] lastsqCor = we.getLastSquareCoordinate();
		int lastSqPos = we.getLastSquarePosition();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Ag Curr Coord: "+x+","+y+"Ag Curr Pos: "+pos+"\n");
		sb.append("Agent Current Direction : "+dir+"\n");
		sb.append("Last Sq Coo: "+lastsqCor[0]+"-"+lastsqCor[1]+"Last Sq Pos: "+lastSqPos+"\n");
		System.out.println(sb.toString());
	}
	
	
	public int move(Move move, WEnvironment wenv, State state) {
		int[] curr_pos = wenv.getCurrentAgentCoordinate();
		int[] new_pos = {curr_pos[0], curr_pos[1]};
		
		char direction = wenv.getCurrentDirection();
		char newdirection = '>';
		if (state!=null)
			wenv.getSquares().get(wenv.getCurrentAgentPosition()).setState(state);

		switch(move) {
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
				//break;
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
				//break;
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
				//break;
		}
			
		
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}