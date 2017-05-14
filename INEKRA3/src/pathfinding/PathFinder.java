package pathfinding;

import java.util.ArrayList;

import org.joml.Vector3f;

import particles.PTM;
import particles.ParticleMaster;
import toolBox.Meth;
import toolBox.Vects;

public class PathFinder {

	public static final int RANGE = 20;

	public static Path getPath(float sx, float sy, float sz, float ex, float ey, float ez) {
		Path p = new Path(new ArrayList<Vector3f>());

		return p;
	}

	private static float X, Y, Z;
	private static boolean start = false;
	private static long last;

	public static void submit(float x, float y, float z) {
		if (Meth.systemTime() > last + 1000) {
			last = Meth.systemTime();
			if (!start) {
				X = x;
				Y = y;
				Z = z;
			} else {
				Path p = getPath(X, Y, Z, x, y, z);
				for (int i = 0; i < p.getPositions().size(); i++) {
					ParticleMaster.addNewParticle(PTM.cosmic, p.getPositions().get(i), Vects.NULL, 0, 30, 0, 0.5f);
				}
			}
		}
	}

}
