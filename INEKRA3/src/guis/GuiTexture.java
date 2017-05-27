package guis;

import org.joml.Vector2f;

import menuThings.MenuThing;

public class GuiTexture extends MenuThing{

//	private int displayLevel = GUIManager.DOWN;
	private boolean transparent, applyHighLightToAlpha;
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
		this.displayLevel = GUIManager.DOWN;
		hidden = true;
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
	
	@Override
	public void show() {
		if(hidden)
			GUIManager.addGuiTexture(this);
		super.show();
	}

	public void hide() {
		if(!hidden)
			GUIManager.removeGuiTexture(this);
		super.hide();
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

	private float highlight, alphaHighlight = 1;

	public void setHighlight(float h) {
		highlight = h;
		if(applyHighLightToAlpha)
			alphaHighlight = h;
	}

	public boolean isTransparent() {
		return transparent;
	}
	
	public void setTransparency(boolean b){
		transparent = b;
	}

	public void setHighlightToAlpha(boolean b) {
		applyHighLightToAlpha = b;
	}
	
	public void setAlphaHighlight(float a){
		alphaHighlight = a;
	}

	public float alphaHighLight() {
		return alphaHighlight;
	}

	@Override
	public void updateClicks() {
		
	}

	@Override
	public void setTextAlpha(float a) {
		
	}

	@Override
	public void setTextColor(float r, float g, float b, float a) {
		
	}

	@Override
	public void setTextColor(float r, float g, float b) {
		
	}

}
