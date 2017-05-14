package hitbox;

import org.joml.Vector3f;

import toolBox.Meth;
import toolBox.Vects;

public class BoundingSphere extends Hitbox {

	private float radius;

	public BoundingSphere(Vector3f position, float radius) {
		super(position, 0);
		this.radius = radius;
	}

	@Override
	public boolean intersects(Vector3f v) {
		float distsq = Meth.getDistanceSquared(v.x, v.y, v.z, position.x, position.y, position.z);
		if (distsq < radius * radius) {
			return true;
		}
		return false;
	}

	public float radius() {
		return radius;
	}

	public boolean intersects(BoundingSphere b) {
		float dist = b.position.sub(position, Vects.calcVect).length();
		if (dist < radius + b.radius) {
			return true;
		}
		return false;
	}

}
