package gui3D;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class GuiTex {

	private int tex;

	private Vector3f pos;
	private Vector2f scale;

	/**
	 * @param tex
	 * @param pos
	 * @param scale
	 *            1 means it fills the display (in height, not in width!!!)
	 */
	public GuiTex(int tex, Vector3f pos, Vector2f scale) {
		this.tex = tex;
		this.pos = pos;
		this.scale = scale;
	}

	/**
	 * @param tex
	 * @param pos
	 * @param scale
	 *            1 means it fills the display (in height, not in width!!!)
	 */
	public GuiTex(int tex, Vector2f pos, Vector2f scale) {
		this.tex = tex;
		this.pos = new Vector3f(pos, 0);
		this.scale = scale;
	}

	public Vector3f getPosition() {
		return pos;
	}

	public Vector2f getScale() {
		return scale;
	}

	public int getTex() {
		return tex;
	}

	public void show() {
		G3DM.add(this);
	}

	public void hide() {
		G3DM.remove(this);
	}

	public void setTexture(int tex) {
		this.tex = tex;
	}

}
