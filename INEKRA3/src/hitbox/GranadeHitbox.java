package hitbox;

import org.joml.Vector3f;

import toolBox.Meth;
import toolBox.Vects;

public class GranadeHitbox extends Hitbox {

	public GranadeHitbox(Vector3f position, float scale) {
		super(position, scale);
	}

	@Override
	public boolean intersects(Vector3f v) {
		return v.sub(position, Vects.calcVect).lengthSquared() < Meth.pow(scale, 2);
	}

}
