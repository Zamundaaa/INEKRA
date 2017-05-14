package entities;

import org.joml.Vector3f;

import hitbox.Hitbox;

public interface HittableThing {

	public boolean hit(float damage);

	public boolean inHitbox(Vector3f point);

	public Vector3f getPosition();

	public Hitbox getHitbox();

	public void destroy();

}
