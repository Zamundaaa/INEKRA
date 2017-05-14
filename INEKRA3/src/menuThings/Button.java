package menuThings;

import static gameStuff.SC.font;

import org.joml.Vector2f;

import controls.Mouse;
import fontMeshCreator.GUIText;
import gameStuff.SC;
import guis.GUIManager;
import guis.GuiTexture;
import renderStuff.DisplayManager;

public class Button extends MenuThing {

	protected GUIText text;
	protected GuiTexture gtex;

	protected String t;

	protected boolean somebool;

	private float fontSize = 1.5f;

	private Vector2f textPos;

	public Button(String text, Rectangle bounds, int texID, boolean texTransparent) {
		clickable = true;
		this.bounds = bounds;
		t = text;
		setTextPos();
		this.text = new GUIText(t, fontSize, font, textPos, (float) bounds.getWidth() / 1000f, true);
		this.text.setColour(1, 1, 1);
		if (this.text.getNumberOfLines() > 1) {
			this.text.setPosition((float) (bounds.getX() / 1000f), (float) ((bounds.getY() + (bounds.getHeight() / 5)) / 1000f));
		}
		this.text.setDisplayLevel(displayLevel+1);
		Vector2f gpos = new Vector2f((float) ((bounds.getX() * 0.001f) + (bounds.getWidth() * 0.0005f)),
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		gpos.x = gpos.x * 2 - 1;
		gpos.y = gpos.y * 2 - 1;
		gpos.y *= -1;
		gtex = new GuiTexture(texID, gpos, new Vector2f((float) bounds.getWidth() * 0.001f, (float) bounds.getHeight() * 0.001f),
				texTransparent);
		GUIManager.addGuiTexture(gtex);
		gtex.setHighlight((hoveradd / maxHover) * hoverHighlight);
		transparent = texTransparent;
	}

	public Button(String text, Rectangle bounds) {
		// r = bounds;
		// t = text;
		// Vector2f pos = new Vector2f((float) (r.getX() / 1000f), (float)
		// ((r.getY() + (r.getHeight() / 3)) / 1000f));
		// this.text = new GUIText(t, fontSize, font, pos, (float) r.getWidth()
		// / 1000f, true);
		// this.text.setColour(1, 1, 1);
		// if (this.text.getNumberOfLines() > 1) {
		// this.text.setPosition((float) (r.getX() / 1000f), (float) ((r.getY()
		// + (r.getHeight() / 5)) / 1000f));
		// }
		// Vector2f gpos = new Vector2f((float) ((r.getX() * 0.001f) +
		// (r.getWidth() * 0.0005f)),
		// (float) ((r.getY() * 0.001f) + (r.getHeight() * 0.0005f)));
		// gpos.x = gpos.x * 2 - 1;
		// gpos.y = gpos.y * 2 - 1;
		// gpos.y *= -1;
		// gtex = new GuiTexture(Frame.button, gpos,
		// new Vector2f((float) r.getWidth() * 0.001f, (float) r.getHeight() *
		// 0.001f));
		// GUIManager.addGuiTexture(gtex);
		// gtex.setHighlight((hoveradd / maxHover) * hoverHighlight);
		this(text, bounds, Frame.button, false);
	}

	public Button(Rectangle bounds) {
		clickable = true;
		this.bounds = bounds;
		t = "";
		Vector2f gpos = new Vector2f((float) ((bounds.getX() * 0.001f) + (bounds.getWidth() * 0.0005f)),
				(float) ((bounds.getY() * 0.001f) + (bounds.getHeight() * 0.0005f)));
		gpos.x = gpos.x * 2 - 1;
		gpos.y = gpos.y * 2 - 1;
		gpos.y *= -1;
		gtex = new GuiTexture(Frame.button, gpos,
				new Vector2f((float) bounds.getWidth() * 0.001f, (float) bounds.getHeight() * 0.001f), false);
		GUIManager.addGuiTexture(gtex);
		gtex.setHighlight((hoveradd / maxHover) * hoverHighlight);
	}

	public void setPosition(float x, float y) {
		bounds.setLocation(x, y);
		if (text != null) {
			if (text.getNumberOfLines() == 1) {
				text.setPosition((float) (x / 1000f), (float) ((y + textYOffset + (bounds.getHeight() / 3)) / 1000f));
			} else {
				text.setPosition((float) (x / 1000f), (float) ((y + textYOffset + (bounds.getHeight() * 0.1f)) / 1000f));
			}
		}
		if (gtex != null) {
			Vector2f gpos = gtex.getPos().set((float) ((x * 0.001f) + (bounds.getWidth() * 0.0005f)),
					(float) ((y * 0.001f) + (bounds.getHeight() * 0.0005f)));
			gpos.x = gpos.x * 2 - 1;
			gpos.y = gpos.y * 2 - 1;
			gpos.y *= -1;
		}
		posUpdate();
	}

	protected boolean hoverText = true, hoverSize = true;
	protected final float hoverSizeFact = 0.5f;

	@Override
	public void hide() {
		if (!hidden) {
			if (text != null)
				text.hide();
			hoveradd = 0;
			gtex.hide();
			super.hide();
		}
	}

	@Override
	public void show() {
		if (hidden) {
			if (text != null) {
				text.show();
			} else if (t.length() > 0 && bounds.h > 1) {
				setText(t);
			}
			gtex.show();
			super.show();
		}
		gtex.getScale().set(0.001f * bounds.w, 0.001f * bounds.h);
		hoveradd = startH;
		gtex.setHighlight((hoveradd / maxHover) * hoverHighlight);
	}

	public void setText(String text) {
		t = text;
		if (text.length() > 0) {
			if (this.text == null) {
				Vector2f pos = new Vector2f((float) (bounds.getX() / 1000f),
						(float) ((bounds.getY() + textYOffset + (bounds.getHeight() / 3)) / 1000f));
				this.text = new GUIText(t, fontSize, font, pos, (float) bounds.getWidth() / 1000f, true);
				this.text.setColour(1, 1, 1);
				if (this.text.getNumberOfLines() > 1) {
					this.text.setPosition((float) (bounds.getX() / 1000f),
							(float) ((bounds.getY() + textYOffset + (bounds.getHeight() / 5)) / 1000f));
				}
				this.text.setDisplayLevel(displayLevel+1);
			} else {
				this.text.setText(text);
			}
			if (hidden) {
				this.text.hide();
			}
		} else if (text != null) {
			this.text.cleanUp();
			this.text = null;
		}
	}

	private float hoveradd = startH;
	protected boolean HOVER = true;

	public void setHOVER(boolean b) {
		HOVER = b;
		
	}

	private static final float maxHover = 0.05f, highlightSpeed = 0.25f, auftauchSpeed = 0.5f * highlightSpeed,
			startH = -1.5f * maxHover;

	private boolean rectC;

	@Override
	public void update() {
		rectC = bounds.contains(Mouse.getAX() * 1000, Mouse.getAY() * 1000);
		if (HOVER) {
			gtex.setHighlight((hoveradd / maxHover) * hoverHighlight);
			if (rectC && hoveradd >= 0) {
				hoveradd += highlightSpeed * DisplayManager.getFrameTimeSeconds();
				if (hoveradd > maxHover)
					hoveradd = maxHover;
				if (hoverSize)
					gtex.getScale().set((0.001f + hoverSizeFact*hoveradd * 0.001f) * bounds.w, (0.001f + hoverSizeFact*hoveradd * 0.001f) * bounds.h);
			} else {
				if (hoveradd > 0) {
					hoveradd -= highlightSpeed * DisplayManager.getFrameTimeSeconds();
					if (hoveradd < 0) {
						hoveradd = 0;
						if (hoverSize)
							gtex.getScale().set(0.001f * bounds.w, 0.001f * bounds.h);
					} else if (hoverSize) {
						gtex.getScale().set((0.001f + hoverSizeFact*hoveradd * 0.001f) * bounds.w, (0.001f + hoverSizeFact*hoveradd * 0.001f) * bounds.h);
					}
				} else if (hoveradd < 0) {
					hoveradd += auftauchSpeed * DisplayManager.getFrameTimeSeconds();
					if (hoveradd > 0) {
						hoveradd = 0;
						if (hoverSize)
							gtex.getScale().set(0.001f * bounds.w, 0.001f * bounds.h);
					} else if (hoverSize) {
						gtex.getScale().set((0.001f + hoverSizeFact*hoveradd * 0.001f) * bounds.w, (0.001f + hoverSizeFact*hoveradd * 0.001f) * bounds.h);
					}
				}
			}
		} else {
			gtex.setHighlight(0);
		}
		super.update();
		
		if(bounds.h > 1 && text == null && t.length() > 0){
			setText(t);
		}
		
//		for(int i = 0; i < attached.size(); i++)
//			attached.get(i).updateRelativePos();
	}

	protected boolean leftHold, rightHold;

	public void leftClick() {

	}

	public void rightClick() {

	}

	public void leftHold() {

	}

	public void rightHold() {

	}

	public void leftLeft() {

	}

	public void rightLeft() {

	}

	public Rectangle getRect() {
		return bounds;
	}
	
	@Override
	public void setBounds(float x, float y, float width, float height) {
		float olh = bounds.h;
//		bounds.setBounds(x, y, width, height);
		super.setBounds(x, y, width, height);
		if (height > 1) {
			if (text != null) {
				if (text.getNumberOfLines() == 1) {
					text.setPosition((float) (x / 1000f), (float) ((y + textYOffset + (height / 3)) / 1000f));
				} else {
					text.setPosition((float) (x / 1000f), (float) ((y + textYOffset + (height * 0.1f)) / 1000f));
				}
			} else if (olh <= 1) {
				text = new GUIText(t, fontSize, SC.font, new Vector2f(), width * 0.001f, true);
				if (text.getNumberOfLines() == 1) {
					text.setPosition((float) (x / 1000f), (float) ((y + textYOffset + (height / 3)) / 1000f));
				} else {
					text.setPosition((float) (x / 1000f), (float) ((y + textYOffset + (height * 0.1f)) / 1000f));
				}
				text.setColour(1, 1, 1);
				text.setDisplayLevel(displayLevel+1);
			}
		} else if (text != null) {
			text.cleanUp();
			text = null;
			// text.hide();
		}
		if (gtex != null) {
			Vector2f gpos = gtex.getPos().set((float) ((x * 0.001f) + (bounds.getWidth() * 0.0005f)),
					(float) ((y * 0.001f) + (bounds.getHeight() * 0.0005f)));
			gpos.x = gpos.x * 2 - 1;
			gpos.y = gpos.y * 2 - 1;
			gpos.y *= -1;
			gtex.getScale().set(0.001f * width, 0.001f * height);
		}
		posUpdate();
	}

	public void setTex(int buttonClicked) {
		if (gtex != null)
			gtex.setTexture(buttonClicked);
	}

	public void posUpdate() {

	}

	public void clickOutside() {

	}

	public void setTextPos() {
		if (textPos == null)
			textPos = new Vector2f((float) (bounds.getX() / 1000f), (float) ((bounds.getY() + (bounds.getHeight() / 3) + textYOffset) / 1000f));
		else
			textPos.set((float) (bounds.getX() / 1000f), (float) ((bounds.getY() + (bounds.getHeight() / 3) + textYOffset) / 1000f));
	}

	public void setTextColor(float r, float g, float b) {
		if (text != null) {
			text.setColour(r, g, b);
		}
	}

	public void resetHover() {
		hoveradd = startH;
	}

	public void cleanUp() {
		hide();
		if (text != null)
			text.cleanUp();
		super.cleanUp();
	}

	public void setFontSize(float fs) {
		fontSize = fs;
		if (text != null) {
			text.setFontSize(fs);
		}
	}

	protected float textYOffset = 0;

	public void setTextYOffset(float yo) {
		textYOffset = yo;
	}

	@Override
	public void updateClicks() {
		if (Mouse.buttonClickedThisFrame(0)) {
			if (rectC) {
				leftClick();
				clickUsed = true;
			} else {
				clickOutside();
			}
		} else if (Mouse.buttonClickedThisFrame(1)) {
			if (rectC) {
				rightClick();
				clickUsed = true;
			} else {
				clickOutside();
			}
		} else if (Mouse.isButtonDown(0)) {
			if (rectC) {
				leftHold();
				leftHold = true;
				clickUsed = true;
			}
		} else if (Mouse.isButtonDown(1)) {
			if (rectC) {
				rightHold();
				rightHold = true;
				clickUsed = true;
			}
		} else if (leftHold) {
			leftLeft();
			leftHold = false;
		} else if (rightHold) {
			rightLeft();
			rightHold = false;
		}
	}

	@Override
	public void setTextAlpha(float a) {
		if (text != null)
			text.setAlpha(a);
	}

	@Override
	public void setTextColor(float r, float g, float b, float a) {
		if (text != null)
			text.setColour(r, g, b, a);
	}
	
	@Override
	public void setDisplayLevel(int displayLevel){
		super.setDisplayLevel(displayLevel);
		if(text != null)
			text.setDisplayLevel(displayLevel+1);
	}
	
}
