package pathfinding;

import java.util.ArrayList;

import org.joml.Vector3f;

public class Path {

	private ArrayList<Vector3f> positions;
	private int step;

	public Path(ArrayList<Vector3f> poses) {
		positions = poses;
	}

	public ArrayList<Vector3f> getPositions() {
		return positions;
	}

	public void increment() {
		step++;
	}

	public int thisStep() {
		return step;
	}

}
