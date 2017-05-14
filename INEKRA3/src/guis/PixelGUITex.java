package guis;

import org.joml.Vector2f;

import renderStuff.DisplayManager;

public class PixelGUITex extends GuiTexture {

	private Vector2f relPos = new Vector2f(), relScale = new Vector2f();

	public PixelGUITex(int textureID, Vector2f pos, Vector2f scale) {
		super(textureID, pos, scale, false);
	}

	@Override
	public Vector2f getPos() {
		float w = DisplayManager.getWidth();
		float h = DisplayManager.getHeight();
		relPos.x = pos.x / w;
		relPos.y = pos.y / h;
		return relPos;
	}

	@Override
	public Vector2f getScale() {
		relScale.x = scale.x / DisplayManager.getWidth();
		relScale.y = scale.y / DisplayManager.getHeight();
		return relScale;
	}

	public Vector2f getPixelPos() {
		return pos;
	}

	public Vector2f getPixelScale() {
		return scale;
	}

}
