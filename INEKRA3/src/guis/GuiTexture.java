package guis;

import org.joml.Vector2f;

public class GuiTexture {

	private int displayLevel = GUIManager.DOWN;
	private boolean transparent;
	private int texture;
	protected Vector2f pos;
	protected Vector2f scale;
	// protected Rectangle bounds = new Rectangle(-1000, -1000, 1000, 1000);

	/**
	 * @param textureID
	 * @param pos
	 *            0|0 is the middle of the screen; -1|-1 is bottomleft; 1|1 is
	 *            upright
	 * @param scale
	 */
	public GuiTexture(int textureID, Vector2f pos, Vector2f scale, boolean transparent) {
//		this.texture = textureID;
//		this.pos = pos;
//		this.scale = scale;
		this(textureID, pos, scale, 0, transparent);
	}

	public GuiTexture(int textureID, Vector2f pos, Vector2f scale, int displayLevel, boolean transparent) {
		this.texture = textureID;
		this.pos = pos;
		this.scale = scale;
		this.displayLevel = displayLevel;
		this.transparent = transparent;
	}

	public void setDisplayLevel(int level) {
		this.displayLevel = level;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPos() {
		return pos;
	}

	public Vector2f getScale() {
		return scale;
	}

	public void show() {
		GUIManager.addGuiTexture(this);
	}

	public void hide() {
		GUIManager.removeGuiTexture(this);
	}

	public int displayLevel() {
		return displayLevel;
	}

	public void setTexture(int tex) {
		this.texture = tex;
	}

	public float highlight() {
		return highlight;
	}

	private float highlight;

	public void setHighlight(float h) {
		highlight = h;
	}

	public boolean isTransparent() {
		return transparent;
	}
	
	public void setTransparency(boolean b){
		transparent = b;
	}

}
