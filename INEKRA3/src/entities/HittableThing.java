package entities;

import org.joml.Vector3f;

import hitbox.Hitbox;

public interface HittableThing {

	public boolean hit(float damage);

	public boolean inHitbox(Vector3f point);

	public Vector3f getPosition();
	
	public void influence(float x, float y, float z);
	
	public void influence(Vector3f i);

	public Hitbox getHitbox();

	public void destroy();

}
