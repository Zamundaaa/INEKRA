package menuThings;

import org.joml.Vector2f;

public interface Inputthing {

	public static final float hoverHighlight = 0.5f;

	public void update();

	public void hide();

	public void show();

	// public GuiTexture getGuiTex();

	public static Vector2f toGUITexCoords(float x, float y, float width, float height, Vector2f vect) {
		vect.set((float) ((x * 0.001f) + (width * 0.0005f)), (float) ((y * 0.001f) + (height * 0.0005f)));
		vect.x = vect.x * 2 - 1;
		vect.y = vect.y * 2 - 1;
		vect.y *= -1;
		return vect;
	}

	public static Vector2f toGUITexCoords(Rectangle r, Vector2f vect) {
		return toGUITexCoords(r.x, r.y, r.w, r.h, vect);
	}

	public void setBounds(float x, float y, float w, float h);

	public void setBounds(Rectangle r);

	// public void reloadSize();

}
