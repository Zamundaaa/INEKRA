package entities;

import org.joml.Vector3f;

public interface HarmingThing {

	public Vector3f getPos();

	public float getDamage();

	public void handleThings();

}
