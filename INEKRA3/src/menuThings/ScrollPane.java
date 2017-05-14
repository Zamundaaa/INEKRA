package menuThings;

import java.util.ArrayList;

import org.joml.Vector2f;

import controls.Mouse;
import gameStuff.SC;
import guis.GuiTexture;
import renderStuff.DisplayManager;
import toolBox.Meth;

public class ScrollPane extends MenuThing {

	public static final int YD = 25, scrwidth = 15, scrheight = 80;

	private Rectangle bounds;
	private float scroll = 0;
	private float desiredScroll = 0;
	private Button scr;
	private GuiTexture scroller, background;

	public ScrollPane(Rectangle bounds) {
		this.bounds = bounds;
		scr = new Button(new Rectangle((int) (bounds.x + bounds.w), bounds.y, scrwidth, bounds.h)) {
			@Override
			public void leftHold() {
				desiredScroll = ((Mouse.getAY() * 1000f) - bounds.y - scrheight * 0.5f) / (bounds.h - scrheight);
				desiredScroll = Meth.clamp(desiredScroll, 0, 1);
				scrollSpeed = 3;
			}
		};
		scr.setHOVER(false);
		scr.setDisplayLevel(displayLevel+2);
//		scr.setTex(Frame.button);
		scroller = new GuiTexture(Frame.buttonClicked,
				Inputthing.toGUITexCoords(bounds.x + bounds.w, bounds.y, scrwidth, scrheight, new Vector2f()),
				new Vector2f(scrwidth * 0.001f, scrheight * 0.001f), displayLevel+1, false);
//		attach(scr);
//		scroller.setDisplayLevel(displayLevel+1);
		background = new GuiTexture(SC.getTex("BlackBorder").getID(), Inputthing.toGUITexCoords(bounds.x, bounds.y, bounds.w+scrwidth, bounds.h, new Vector2f()), new Vector2f((bounds.w+scrwidth)*0.001f, bounds.h*0.001f), displayLevel, true);
		background.setDisplayLevel(displayLevel);
	}

	@Override
	public void attach(MenuThing mt) {
		super.attach(mt);
		if(mt instanceof Button){
			Button b = (Button)mt;
			b.hoverText = false;
			b.hoverSize = false;
		}
		setPositions();
	}

	private static final int BUTTONHEIGHT = 100;
	private boolean LINEHIDDEN = false;
	private float space;

	private void setPositions() {
		space = attached.size() * BUTTONHEIGHT + (attached.size() - 1) * YD;
		if (space <= bounds.h) {
			float bspace = (bounds.h - space) * 0.5f;
			float pos = bounds.y + bspace;
			for (int i = 0; i < attached.size(); i++) {
				attached.get(i).setBounds(bounds.x, pos, bounds.w, BUTTONHEIGHT);
				pos += YD + BUTTONHEIGHT;
			}
			if (!LINEHIDDEN) {
				scr.hide();
				scroller.hide();
			}
			LINEHIDDEN = true;
		} else {
			if (LINEHIDDEN) {
				scr.show();
				scroller.show();
			}
			scr.setPosition((int) (bounds.x + bounds.w), bounds.y);
			Inputthing.toGUITexCoords(bounds.x + bounds.w, bounds.y + (bounds.h - scrheight) * scroll, scrwidth,
					scrheight, scroller.getPos());
			LINEHIDDEN = false;
			space -= bounds.h;
			float pos = bounds.y - space * scroll;
			for (int i = 0; i < attached.size(); i++) {
				attached.get(i).setBounds(bounds.x, Meth.clamp(pos, bounds.y - 1, bounds.y + bounds.h + 1), bounds.w,
						pos < bounds.y ? (BUTTONHEIGHT - (bounds.y - pos))
								: (pos > bounds.y + bounds.h - BUTTONHEIGHT ? (bounds.y + bounds.h - pos)
										: BUTTONHEIGHT));
				attached.get(i).setAllTextAlpha(pos < bounds.y ? (BUTTONHEIGHT - (bounds.y - pos))/(float)BUTTONHEIGHT
								: (pos > bounds.y + bounds.h - BUTTONHEIGHT ? (bounds.y + bounds.h - pos)/(float)BUTTONHEIGHT
										: 1));
				pos += YD + BUTTONHEIGHT;
			}
		}
	}

	private float scrollSpeed = 1;

	@Override
	public void update() {
		float dw = Mouse.getDWheel();
		if (dw != 0) {
			desiredScroll = Meth.clamp(desiredScroll - dw * 0.2f, 0, 1);
			scrollSpeed = 1;
		}
		if (!Meth.theSame(scroll, desiredScroll, 100)) {
			if (scroll < desiredScroll) {
				scroll += scrollSpeed * DisplayManager.getFrameTimeSeconds() / (space * 0.001f);
				if (scroll > desiredScroll) {
					scroll = desiredScroll;
				}
			} else if (scroll > desiredScroll) {
				scroll -= scrollSpeed * DisplayManager.getFrameTimeSeconds() / (space * 0.001f);
				if (scroll < desiredScroll) {
					scroll = desiredScroll;
				}
			}
			// scroll = Meth.clamp(scroll, 0, 1);
			setPositions();
		} else {
			if (desiredScroll == 0) {
				scroll = 0;
			}
		}
		if (!LINEHIDDEN)
			scr.update();
		for (int i = 0; i < attached.size(); i++) {
			attached.get(i).update();
		}
	}

	@Override
	public void hide() {
		for (int i = 0; i < attached.size(); i++) {
			attached.get(i).hide();
		}
		background.hide();
		if (!LINEHIDDEN) {
			scr.hide();
			scroller.hide();
		}
	}

	@Override
	public void show() {
		for (int i = 0; i < attached.size(); i++) {
			attached.get(i).show();
		}
		background.show();
		if (!LINEHIDDEN) {
			scr.show();
			scroller.show();
		}
	}
	
	@Override
	public void putClickable(ArrayList<MenuThing> ka) {
		super.putClickable(ka);
		ka.add(scr);
	}
	
	@Override
	public void cleanUp(){
		scr.cleanUp();
		scroller.hide();
		background.hide();
		super.cleanUp();
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
