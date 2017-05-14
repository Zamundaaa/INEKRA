package chatStuff;

import org.joml.Vector2f;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import gameStuff.SC;
import toolBox.FontColorManager;
import toolBox.Meth;

public class Message {

	private static final float maxLineLength = 0.6f;

	private FontType f = SC.font;
	private float fontSize = 1.2f;

	// private String text;
	private GUIText t;
	private boolean hidden = false;
	private long creationTime = Meth.systemTime();

	public Message(String text) {
		// this.text = text;
		t = new GUIText(text, fontSize, f, new Vector2f(), maxLineLength, false);
		t.setColour(FontColorManager.one);
	}

	public void setPos(float x, float y) {
		t.getPosition().set(x, y);
	}

	public void show() {
		if (hidden) {
			t.show();
			hidden = false;
		}
	}

	public void hide() {
		if (!hidden) {
			t.hide();
			hidden = true;
		}
	}

	public void cleanUp() {
		t.cleanUp();
	}

	public long timeSinceCreation() {
		return System.currentTimeMillis() - creationTime;
	}

	public float getDY() {
		return (t.getNumberOfLines() * 2 - 1) * fontSize * 0.03f;
	}

}
