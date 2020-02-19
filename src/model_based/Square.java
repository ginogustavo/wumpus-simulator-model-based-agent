package model_based;

public class Square {
	public Square() {
	}

	public Square(int[] pos, State state) {
		setPosition(pos);
		setState(state);
	}

	private State state;
	private int[] position;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int[] getPosition() {
		return position;
	}

	public void setPosition(int[] position) {
		this.position = position;
	}
}
