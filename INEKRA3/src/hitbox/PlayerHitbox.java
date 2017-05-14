package hitbox;

import org.joml.Vector3f;

public class PlayerHitbox extends Hitbox {

	public PlayerHitbox(Vector3f position, float scale) {
		super(position, scale);
	}

	@Override
	public boolean intersects(Vector3f point) {
		if (point.x > position.x - (4f * scale) && point.x < position.x + (4f * scale)
				&& point.z > position.z - (4f * scale) && point.z < position.z + (4f * scale) && point.y > position.y
				&& point.y < position.y + (10f * scale)) {
			return true;
		}
		return false;
	}

}
