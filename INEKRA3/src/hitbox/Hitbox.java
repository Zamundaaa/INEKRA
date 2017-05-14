package hitbox;

import org.joml.Vector3f;

public abstract class Hitbox {

	protected Vector3f position;
	protected float scale;

	protected Hitbox(Vector3f position, float scale) {
		this.position = position;
		this.scale = scale;
	}

	public abstract boolean intersects(Vector3f v);

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

}
