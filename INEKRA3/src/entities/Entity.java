package entities;

import org.joml.Vector3f;

import gameStuff.EntityManager;
import hitbox.BoundingSphere;
import hitbox.Hitbox;
import models.RawModel;
import models.TexturedModel;
import renderStuff.DisplayManager;
import toolBox.Meth;

public class Entity extends MWBE {

	public boolean isChunk = false;

	protected Vector3f position, velocity;

	protected Hitbox hit;
	protected BoundingSphere boundingSphere;
	protected boolean highlight = false;
	protected boolean hittable = false;

	protected Vector3f outSideSpeed = new Vector3f();

	public Entity(TexturedModel model, int tIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			Hitbox h) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.tIndex = tIndex;

		boundingSphere = Meth.createBoundingSphere(this);
		if (h != null) {
			this.hit = h;
		} else {
			this.hit = boundingSphere;
		}
		this.velocity = new Vector3f();
		EntityManager.addEntity(this);
	}

	public Entity(TexturedModel model, int tIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			boolean createBoundingSphere) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.tIndex = tIndex;

		if (createBoundingSphere) {
			boundingSphere = Meth.createBoundingSphere(this);
		}

		this.velocity = new Vector3f();
		EntityManager.addEntity(this);
	}

	public Entity(TexturedModel model, int tIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			boolean createBoundingSphere, boolean autoInsert) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;

		this.scale = scale;
		this.tIndex = tIndex;

		this.velocity = new Vector3f();

		if (createBoundingSphere) {
			boundingSphere = Meth.createBoundingSphere(this);
		}
		if (autoInsert) {
			EntityManager.addEntity(this);
		}
	}

	public Entity(TexturedModel model, int tIndex, Vector3f position, Vector3f velocity, float rotX, float rotY,
			float rotZ, float scale, boolean createBoundingSphere, boolean autoInsert) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;

		this.scale = scale;
		this.tIndex = tIndex;

		this.velocity = velocity;

		if (createBoundingSphere) {
			boundingSphere = Meth.createBoundingSphere(this);
		}
		if (autoInsert) {
			EntityManager.addEntity(this);
		}
	}

	protected Entity() {

	}

	public void setNonWeirdRotation(float x, float y, float z) {
		rotZ = z;

	}

	public void setRotation(float x, float y, float z) {
		rotX = x;
		rotY = y;
		rotZ = z;
	}

	public void setHittable(boolean bool) {
		hittable = bool;
	}

	public boolean hittable() {
		return hittable;
	}

	@Override
	public void update() {
		if (velocity != null) {
			position.x += velocity.x * DisplayManager.getFrameTimeSeconds();
			position.y += velocity.y * DisplayManager.getFrameTimeSeconds();
			position.z += velocity.z * DisplayManager.getFrameTimeSeconds();

			if (Math.abs(velocity.x) < 0.05f) {
				velocity.x = 0;
			}
			if (Math.abs(velocity.y) < 0.05f) {
				velocity.y = 0;
			}
			if (Math.abs(velocity.z) < 0.05f) {
				velocity.z = 0;
			}
		}
		if (outSideSpeed != null) {
			position.x += outSideSpeed.x * DisplayManager.getFrameTimeSeconds();
			position.y += outSideSpeed.y * DisplayManager.getFrameTimeSeconds();
			position.z += outSideSpeed.z * DisplayManager.getFrameTimeSeconds();

			if (Math.abs(outSideSpeed.x) < 0.05f) {
				outSideSpeed.x = 0;
			}
			if (Math.abs(outSideSpeed.z) < 0.05f) {
				outSideSpeed.z = 0;
			}
			if (Math.abs(outSideSpeed.y) < 0.05f) {
				outSideSpeed.y = 0;
			}
			outSideSpeed.x *= 0.9f;
			outSideSpeed.z *= 0.9f;
			outSideSpeed.y *= 0.9f;
		}
	}

	protected void update(boolean x, boolean y, boolean z) {
		if (x) {
			position.x += velocity.x * DisplayManager.getFrameTimeSeconds();
			position.x += outSideSpeed.x * DisplayManager.getFrameTimeSeconds();
		}
		if (y) {
			position.y += velocity.y * DisplayManager.getFrameTimeSeconds();
			position.y += outSideSpeed.y * DisplayManager.getFrameTimeSeconds();
		}
		if (z) {
			position.z += velocity.z * DisplayManager.getFrameTimeSeconds();
			position.z += outSideSpeed.z * DisplayManager.getFrameTimeSeconds();
		}

		if (x && outSideSpeed.x < 0.1f) {
			outSideSpeed.x = 0;
		}
		if (z && outSideSpeed.z < 0.1f) {
			outSideSpeed.z = 0;
		}
		if (y && outSideSpeed.y < 0.1f) {
			outSideSpeed.y = 0;
		}
		// outSideSpeed.x *= 0.9f;
		// outSideSpeed.z *= 0.9f;
		// outSideSpeed.y *= 0.9f;
		outSideSpeed.x *= 1 - DisplayManager.getFrameTimeSeconds();
		outSideSpeed.z *= 1 - DisplayManager.getFrameTimeSeconds();
		outSideSpeed.y *= 1 - DisplayManager.getFrameTimeSeconds();
	}

	@Override
	public void increasePos(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
	}

	@Override
	public float getX() {
		return position.x;
	}

	@Override
	public float getY() {
		return position.y;
	}

	@Override
	public float getZ() {
		return position.z;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setHighlight(boolean bool) {
		highlight = bool;
	}

	public boolean highlighted() {
		return highlight;
	}

	// public boolean onGround(){
	// float TERRAINHEIGHT = TerrainManager.terrs.getTerrHeight(position.x,
	// position.z);
	// TERRAINHEIGHT = Meth.frozenWater ? Math.max(Terrain.waterHeight,
	// TERRAINHEIGHT) : TERRAINHEIGHT;
	// return position.y <= TERRAINHEIGHT + 0.5f;
	// }

	public BoundingSphere getBoundingSphere() {
		return boundingSphere;
	}

	public boolean inBoundingSphere(Vector3f point) {
		boolean in = false;
		if (boundingSphere.intersects(point)) {
			in = true;
		}
		return in;
	}

	public boolean inHitbox(Vector3f point) {
		if (hit != null) {
			return hit.intersects(point);
		} else {
			return false;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public boolean hit(float damage) {

		return false;
	}

	public void influenceUnrealistic(Vector3f whatever) {
		outSideSpeed.add(whatever);
	}

	public void influence(Vector3f whatever) {
		velocity.add(whatever);
	}

	public void influence(float x, float y, float z) {
		velocity.x += x;
		velocity.y += y;
		velocity.z += z;
	}

	/**
	 * @param coof
	 *            xD
	 */
	public void addReibung(float coof) {
		velocity.x /= coof;
		velocity.y /= coof;
		velocity.z /= coof;
		if (Math.abs(velocity.x) < 0.1f) {
			velocity.x = 0;
		}
		if (Math.abs(velocity.y) < 0.1f) {
			velocity.y = 0;
		}
		if (Math.abs(velocity.z) < 0.1f) {
			velocity.z = 0;
		}
	}

	private boolean hidden = false;

	public void hide() {
		if (!hidden) {
			EntityManager.removeEntity(this);
			hidden = true;
		}
	}

	public void show() {
		if (hidden) {
			EntityManager.addEntity(this);
			hidden = false;
		}
	}

	@Override
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	/**
	 * @param x
	 * @param delta
	 * @param z
	 * @see increasePos(float x, float y, float z)
	 */
	public void translate(float x, float delta, float z) {
		increasePos(x, delta, z);
	}

	public void setRawModel(RawModel mod) {
		this.model.setRawMod(mod);
	}

}
