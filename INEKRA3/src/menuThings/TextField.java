package menuThings;

import java.util.ArrayDeque;

import org.joml.Vector2f;

import controls.Keyboard;
import controls.Mouse;
import fontMeshCreator.GUIText;
import gameStuff.SC;
import guis.GuiTexture;
import toolBox.Meth;

public class TextField extends MenuThing {

	private String content;
	private GUIText t;
	private GuiTexture guit;
	private Vector2f pos;
	private long lT;
	private boolean focused = false;

	public TextField(String startContent, Rectangle r, int texID) {
		clickable = true;
		this.content = startContent;
		this.bounds = r;
		Vector2f gpos = new Vector2f((float) ((r.getX() * 0.001f) + (r.getWidth() * 0.0005f)),
				(float) ((r.getY() * 0.001f) + (r.getHeight() * 0.0005f)));
		gpos.x = gpos.x * 2 - 1;
		gpos.y = gpos.y * 2 - 1;
		gpos.y *= -1;
		guit = new GuiTexture(texID, gpos, new Vector2f((float) r.getWidth() * 0.001f, (float) r.getHeight() * 0.001f), false);
		guit.show();
		this.pos = new Vector2f((float) (bounds.getX() / 1000f),
				(float) ((bounds.getY() + (bounds.getHeight() / 3)) / 1000f));
		t = new GUIText(content, 1.5f, SC.font, pos, (float) bounds.getWidth() / 1000f, true);
		t.setColour(1, 1, 1);
		t.setDisplayLevel(displayLevel+1);
	}

	public TextField(String startContent, Rectangle r) {
		clickable = true;
		this.content = startContent;
		this.bounds = r;
		Vector2f gpos = new Vector2f((float) ((r.getX() * 0.001f) + (r.getWidth() * 0.0005f)),
				(float) ((r.getY() * 0.001f) + (r.getHeight() * 0.0005f)));
		gpos.x = gpos.x * 2 - 1;
		gpos.y = gpos.y * 2 - 1;
		gpos.y *= -1;
		guit = new GuiTexture(Frame.textfield, gpos,
				new Vector2f((float) r.getWidth() * 0.001f, (float) r.getHeight() * 0.001f), false);
		guit.show();
		this.pos = new Vector2f((float) (bounds.getX() / 1000f),
				(float) ((bounds.getY() + (bounds.getHeight() / 3)) / 1000f));
		t = new GUIText(content, 1.5f, SC.font, pos, (float) bounds.getWidth() / 1000f, true);
		t.setColour(1, 1, 1);
		t.setDisplayLevel(displayLevel+1);
	}

	public String getContent() {
		return content;
	}

	// @Override
	// public GuiTexture getGuiTex() {
	// return guit;
	// }

	@Override
	public void update() {
		if (lT + 200 < Meth.systemTime() && focused) {
			lT = Meth.systemTime();
			ArrayDeque<Character> chars = Keyboard.getPressedChars();
			boolean bool = false;
			while (chars.size() > 0) {
				char c = chars.pop();
				if (c != '\u0008') {
					content += c;
				} else {
					if (content.length() > 0) {
						content = content.substring(0, content.length() - 1);
					}
				}
				bool = true;
			}
			if (bool) {
				if (t == null) {
					t = new GUIText(content, 1.5f, SC.font, pos, (float) bounds.getWidth() / 1000f, true);
					t.setColour(1, 1, 1);
					t.setDisplayLevel(displayLevel+1);
				} else {
					t.setText(content);
				}
			}
		}
	}

	public void clear() {
		content = "";
		t.setText("");
	}

	@Override
	public void show() {
		if (t != null) {
			t.show();
		} else {
			t = new GUIText(content, 1.5f, SC.font, pos, (float) bounds.getWidth() / 1000f, true);
			t.setColour(1, 1, 1);
		}
		guit.show();
	}

	@Override
	public void hide() {
		t.hide();
		// t.cleanUp();// vllt. hier sogar lassen. Könnte ein bissl VRAM sparen
		// (so
		// ein paar bytes halt xD)
		guit.hide();
	}

	@Override
	public void setBounds(float x, float y, float w, float h) {
		
	}

	@Override
	public void setBounds(Rectangle r) {
		
	}

	@Override
	public void updateClicks() {
		if (Mouse.buttonClickedThisFrame(0) || Mouse.buttonClickedThisFrame(1)) {
			if (bounds.contains(Mouse.getAX() * 1000, Mouse.getAY() * 1000)) {
				if (!focused) {
					Keyboard.resetChars();
					if(clearOnFirst)
						clear();
				}
				focused = true;
				guit.setTexture(Frame.textfieldChosen);
			} else {
				focused = false;
				guit.setTexture(Frame.textfield);
			}
		}
	}

	@Override
	public void setTextAlpha(float a) {
		if(t != null)
			t.setAlpha(a);
	}

	@Override
	public void setTextColor(float r, float g, float b, float a) {
		if(t != null)
			t.setColour(r, g, b, a);
	}

	public void setClearOnFirstClick() {
		clearOnFirst = true;
	}
	
	private boolean clearOnFirst;
	
	@Override
	public void setDisplayLevel(int displayLevel){
		super.setDisplayLevel(displayLevel);
		if(t != null)
			t.setDisplayLevel(displayLevel+1);
	}

	@Override
	public void setTextColor(float r, float g, float b) {
		if(t != null)
			t.setColour(r, g, b);
	}

	// @Override
	// public GuiTexture getGuiTex() {
	// return guit;
	// }

}
