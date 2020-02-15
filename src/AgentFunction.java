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

import java.util.Random;

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

	public int process(TransferPercept tp)
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
		
		if (bump == true || glitter == true || breeze == true || stench == true || scream == true) {
			// Rule 01: When feel "Glitter" perform "GRAB"
			if (glitter) {
				return Action.GRAB;
			}
			// Rule 02: You Killed the Wumpus in your last move, you could move forward.
			// But there could be a Pit but also the Gold.
			if (scream) {
				return Action.GO_FORWARD;
			}

			// Rule 03: When sense "Bump", just turn to either of the sides.
			// We have no percept history to determine our last turn or direction.
			if (bump) {
				return (rand.nextBoolean()) ? Action.TURN_LEFT : Action.TURN_RIGHT;
			}

			// Rule 04: When sense "Stench", since we dont have history, perform randomly:
			// Go forward, turn left, turn right or shoot.
			// Combination of stench and scream will be effective only with historical
			// percepts
			if (stench) {
				return Action.GRAB;
				/*int num = rand.nextInt(4);
				switch (num) {
				case 1:
					return Action.GO_FORWARD;
				case 2:
					return Action.TURN_LEFT;
				case 3:
					return Action.TURN_RIGHT;
				default:
					return Action.SHOOT;
				}
				*/
			}
			// Rule 05: When sense "Breeze", since we have no historical percepts.
			// Decide randomly between "Go forward" and "Turn to any side"
			if (breeze) {
				return Action.GRAB;
				//return randomMove();
			}
		}
		return randomMove();
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

	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}