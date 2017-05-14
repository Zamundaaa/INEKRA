package menuThings;

import org.joml.Vector2f;

import controls.Mouse;
import fontMeshCreator.GUIText;
import gameStuff.SC;
import guis.GuiTexture;
import renderStuff.DisplayManager;
import toolBox.Meth;

public class SchiebeRegler extends MenuThing {

	public static final int reglerWIDTH = 10;
	
	protected float value;
	private GuiTexture back, regler;
	private GUIText text;

	public SchiebeRegler(Rectangle bounds, float startvalue) {
		clickable = true;
		this.bounds = bounds;
		Vector2f gpos = new Vector2f((float) ((bounds.getX() * 0.001f) + (bounds.getWidth() * 0.0005f)),
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		gpos.x = gpos.x * 2 - 1;
		gpos.y = gpos.y * 2 - 1;
		gpos.y *= -1;
		back = new GuiTexture(Frame.buttonClicked, gpos, new Vector2f(bounds.w * 0.001f, bounds.h * 0.001f), displayLevel, false);
		value = startvalue;
		Vector2f rpos = new Vector2f((bounds.x + reglerWIDTH * 0.5f + value * (bounds.w - reglerWIDTH)) * 0.001f,
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		rpos.x = rpos.x * 2 - 1;
		rpos.y = rpos.y * 2 - 1;
		rpos.y *= -1;
		regler = new GuiTexture(Frame.button, rpos, new Vector2f(reglerWIDTH * 0.001f, bounds.h * 0.001f), displayLevel+1, false);
		show();
	}

	public SchiebeRegler(Rectangle bounds, float startvalue, String text) {
		clickable = true;
		this.bounds = bounds;
		Vector2f gpos = new Vector2f((float) ((bounds.getX() * 0.001f) + (bounds.getWidth() * 0.0005f)),
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		gpos.x = gpos.x * 2 - 1;
		gpos.y = gpos.y * 2 - 1;
		gpos.y *= -1;
		back = new GuiTexture(Frame.buttonClicked, gpos, new Vector2f(bounds.w * 0.001f, bounds.h * 0.001f), displayLevel, true);
		value = startvalue;
		Vector2f rpos = new Vector2f((bounds.x + reglerWIDTH * 0.5f + value * (bounds.w - reglerWIDTH)) * 0.001f,
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		rpos.x = rpos.x * 2 - 1;
		rpos.y = rpos.y * 2 - 1;
		rpos.y *= -1;
		regler = new GuiTexture(Frame.button, rpos, new Vector2f(reglerWIDTH * 0.001f, bounds.h * 0.001f),
				displayLevel+1, true);
		Vector2f pos = new Vector2f((float) (bounds.getX() * 0.001f),
				(float) ((bounds.getY() + (bounds.getHeight() / 3)) * 0.001f));
		this.text = new GUIText(text, 1.5f, SC.font, pos, bounds.w * 0.001f, true);
		this.text.setDisplayLevel(displayLevel+1);
		show();
	}

	private boolean focused = false;
	private float hoveradd = -1;
	private static final float maxHoverAdd = 1, auftauchSpeed = 2.5f;

	@Override
	public void update() {
		boolean bc = bounds.contains(Mouse.getAX() * 1000, Mouse.getAY() * 1000);
		if (!focused && Mouse.buttonClickedThisFrame(0) && bc) {
			focused = true;
		}
		if (bc) {
			hoveradd += maxHoverAdd * 5 * DisplayManager.getFrameTimeSeconds();
			if (focused) {
				if (hoveradd > 2 * maxHoverAdd)
					hoveradd = 2 * maxHoverAdd;
			} else {
				if (hoveradd > maxHoverAdd) {
					hoveradd -= maxHoverAdd * 10 * DisplayManager.getFrameTimeSeconds();
					if (hoveradd < maxHoverAdd) {
						hoveradd = maxHoverAdd;
					}
				}
			}
		} else {
			if (hoveradd > 0) {
				hoveradd -= maxHoverAdd * 5 * DisplayManager.getFrameTimeSeconds();
				if (hoveradd < 0)
					hoveradd = 0;
			}
		}
		if (hoveradd < 0) {
			hoveradd += auftauchSpeed * DisplayManager.getFrameTimeSeconds();
		}
		float h = hoveradd * hoverHighlight;
		back.setHighlight(h);
		regler.setHighlight(h * 2);

		if (Mouse.isButtonDown(0)) {
			if (focused && bounds.contains(Mouse.getAX() * 1000, Mouse.getAY() * 1000)) {
				value = (Mouse.getAX() * 1000 - bounds.x - reglerWIDTH * 0.5f) / (bounds.w - reglerWIDTH);
				value = Meth.clamp(value, 0, 1);
				regler.getPos().set((bounds.x + reglerWIDTH * 0.5f + value * (bounds.w - reglerWIDTH)) * 0.001f,
						(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
				regler.getPos().x = regler.getPos().x * 2 - 1;
				regler.getPos().y = regler.getPos().y * 2 - 1;
				regler.getPos().y *= -1;
				valueChange(value);
			}
		} else {
			focused = false;
		}
	}

	public void setValue(float value) {
		this.value = Meth.clamp(value, 0, 1);
		regler.getPos().set((bounds.x + reglerWIDTH * 0.5f + value * (bounds.w - reglerWIDTH)) * 0.001f,
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		regler.getPos().x = regler.getPos().x * 2 - 1;
		regler.getPos().y = regler.getPos().y * 2 - 1;
		regler.getPos().y *= -1;
	}

	public void setText(String x) {
		if (text != null) {
			text.getPosition().set((float) (bounds.getX() * 0.001f),
					(float) ((bounds.getY() + (bounds.getHeight() / 3)) * 0.001f));
			// text = new GUIText(x, 1.5f, SC.font, pos, bounds.width * 0.001f,
			// true);
			text.setText(x);
		} else {
			Vector2f pos = new Vector2f((float) (bounds.getX() * 0.001f),
					(float) ((bounds.getY() + (bounds.getHeight() / 3)) * 0.001f));
			text = new GUIText(x, 1.5f, SC.font, pos, bounds.w * 0.001f, true);
		}
		text.setDisplayLevel(displayLevel+1);
	}
	
	@Override
	public void setDisplayLevel(int displayLevel){
		if(text != null)
			text.setDisplayLevel(displayLevel+1);
	}

	public void setTextColor(float r, float g, float b) {
		if (text != null) {
			text.setColour(r, g, b);
		}
	}

	@Override
	public void hide() {
		back.hide();
		regler.hide();
		if (text != null) {
			text.hide();
		}
	}

	@Override
	public void show() {
		back.show();
		regler.show();
		if (text != null) {
			text.show();
		}
		hoveradd = -1;
		float h = hoveradd * hoverHighlight;
		back.setHighlight(h);
		regler.setHighlight(h * 2);
	}

	public float getValue() {
		return value;
	}

	public void valueChange(float value) {

	}

	@Override
	public void updateClicks() {
		
	}

	@Override
	public void setTextAlpha(float a) {
		if(text != null)
			text.setAlpha(a);
	}

	@Override
	public void setTextColor(float r, float g, float b, float a) {
		if(text != null)
			text.setColour(r, g, b, a);
	}

}
