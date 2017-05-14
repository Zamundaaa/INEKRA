package cubyWater;

import org.joml.Vector3f;

import data.Block;
import data.ChunkManager;
import gameStuff.SC;
import models.RawModel;

public class Water {

	public static final int tex = SC.getTex("WATER").getID();
	public static final int blinkTex = SC.getTex("waterDUDV").getID();
	public static RawModel side;
	private Vector3f pos;
	private float height;
	private boolean visible = false;

	/**
	 * @param pos
	 *            *** NO autoinsert to watermanager ***
	 * @param height
	 */
	public Water(Vector3f pos, float height) {
		// this.pos = new Vector3f(pos.x + 0.5f, pos.y, pos.z + 0.5f);
		this.pos = pos.add(0.5f, 0, 0.5f);
		this.height = height;

		// update();
	}

	public void update() {
		short b = ChunkManager.getBlockID(pos.x, pos.y + 1, pos.z);
		boolean bevore = visible;
		visible = !Block.isWater(b) && Block.isTransparent(b);

		if (bevore && !visible) {
			WaterManager.remove(this);
		} else if (!bevore && visible) {
			WaterManager.add(this);
		}
	}

	public float height() {
		return height;
	}

	public RawModel getMod() {
		return side;
	}

	public boolean up() {
		return visible;
	}

	public Vector3f getUpperPos(float f) {
		return new Vector3f(pos).add(0, height + f, 0);
	}

	public Vector3f getXMPos(float f) {
		return new Vector3f(pos).add(-0.499f, 0.499f - (1 - height) + f, 0);// just
																			// for
																			// testing.
																			// sorry
	}

	public Vector3f getXPPos(float f) {
		return new Vector3f(pos).add(0.499f, 0.499f - (1 - height) + f, 0);
	}

	public Vector3f getZMPos(float f) {
		return new Vector3f(pos).add(0, 0.499f - (1 - height) + f, -0.499f);
	}

	public Vector3f getZPPos(float f) {
		return new Vector3f(pos).add(0, 0.499f - (1 - height) + f, 0.499f);
	}

	public Vector3f getPos(float f) {
		return new Vector3f(pos).add(0, 0.001f + f, 0);
	}

	public Vector3f getPos() {
		return new Vector3f(pos).add(0, 0.001f, 0);
	}

	public Vector3f getSavedPos() {
		return pos;
	}

	public void cleanUp() {
		WaterUpdater.remove(this);
		hide();
	}

	public void setUp() {
		WaterUpdater.add(this);
		show();
	}

	private boolean hidden = false;

	public void hide() {
		if (!hidden) {
			WaterManager.remove(this);
			WaterRenderer.UPDATE = true;
			hidden = true;
		}
	}

	public void show() {
		if (hidden) {
			WaterManager.add(this);
			WaterRenderer.UPDATE = true;
			hidden = false;
		}
	}

	public static int getTex() {
		return tex;
	}

	public static int getBlinkTex() {
		return blinkTex;
	}

	public void setPosition(float x, float y, float z) {
		pos.x = x + 0.5f;
		pos.y = y;
		pos.z = z + 0.5f;
	}

	public void setHeight(float h) {
		height = h;
	}

}
