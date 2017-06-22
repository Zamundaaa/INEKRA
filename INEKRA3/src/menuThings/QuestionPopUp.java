package menuThings;

import entities.graphicsParts.Texes;
import gameStuff.Models;

public abstract class QuestionPopUp extends PopUpMenu {

	private Button yes, no;

	public QuestionPopUp(Rectangle bounds) {
//		super(bounds);
//		yes = new Button("YES", new Rectangle(0, 0, size * bounds.w, size * bounds.h), SC.getTex("texPack/green").getID(), false) {
//			@Override
//			public void leftClick() {
//				yes();
//				visible = false;
//			}
//		};
//		no = new Button("NO", new Rectangle(0, 0, size * bounds.w, size * bounds.h), SC.getTex("texPack/red").getID(), false) {
//			@Override
//			public void leftClick() {
//				no();
//				visible = false;
//			}
//		};
//		attach(yes, (bounds.w * left) - size * bounds.w * 0.5f, bounds.h * 2 / 3f, size, size);
//		attach(no, (bounds.w * right) - size * bounds.w * 0.5f, bounds.h * 2 / 3f, size, size);
//		yes.boundsOfParent = currentBounds;
//		no.boundsOfParent = currentBounds;
//
//		yes.bounds.w = bounds.w * size;
//		yes.bounds.h = bounds.h * size;
//
//		no.bounds.w = bounds.w * size;
//		no.bounds.h = bounds.h * size;
//
//		yes.setFontSize(yesnoFontSize);
//		no.setFontSize(yesnoFontSize);
//
//		textYOffset = TO;
//		setTextColor(1, 0.15f, 0);
		this("", bounds, 0, false);
	}

	private static final float left = 0.25f, right = 0.75f;
	private static final float size = 0.3f, TO = -30, yesnoFontSize = 1.5f;

	public QuestionPopUp(String text, Rectangle bounds, int texID, boolean texTransparent) {
		super(text, bounds, texID, texTransparent);
		yes = new Button("YES", new Rectangle(0, 0, size * bounds.w, size * bounds.h), Models.getLoadedTex(Texes.green), false) {
			@Override
			public void leftClick() {
				visible = false;
				yes();
			}
		};
		no = new Button("NO", new Rectangle(0, 0, size * bounds.w, size * bounds.h), Models.getLoadedTex(Texes.red), false) {
			@Override
			public void leftClick() {
				visible = false;
				no();
			}
		};
		attach(yes, (bounds.w * left) - size * bounds.w * 0.5f, bounds.h * 2 / 3f, size, size);
		attach(no, (bounds.w * right) - size * bounds.w * 0.5f, bounds.h * 2 / 3f, size, size);
		// attach(yes, (bounds.w*left) - size*bounds.w*0.5f, bounds.h*2/3f, 0,
		// 0);
		// attach(no, (bounds.w*right) - size*bounds.w*0.5f, bounds.h*2/3f, 0,
		// 0);

		yes.boundsOfParent = currentBounds;
		no.boundsOfParent = currentBounds;

		yes.bounds.w = size * bounds.w;
		yes.bounds.h = size * bounds.h;

		no.bounds.w = size * bounds.w;
		no.bounds.h = size * bounds.h;

		yes.setFontSize(yesnoFontSize);
		no.setFontSize(yesnoFontSize);

		textYOffset = TO;
		setTextColor(1, 0.20f, 0);

	}

	public QuestionPopUp(String text, Rectangle bounds) {
		this(text, bounds, Models.getLoadedTex(Texes.questionBackground), false);
	}

	@Override
	public void update() {
		super.update();
		setTextPos();
		if (this.text != null) {
			this.text.setAlpha(currentBounds.w / bounds.w);
		}
//		if (yes.text != null) {
//			if(yes.text.getNumberOfLines() > 1) {
//				yes.text.setText("Y");
//				System.out.println(yes.text.getNumberOfLines());
//			}
//			yes.text.getPosition().set(Mouse.getAX(), Mouse.getAY());
//			yes.text.setDisplayLevel(90);
//			no.text.setDisplayLevel(90);
//			text2.getPosition().set(yes.text.getPosition());
//			System.out.println(yes.text.displayLevel());
//		} else if (yes.bounds.w >= 150) {
//			yes.setText("YES");
//			no.setText("NO");
//			yes.setTextColor(1, 0, 0, 1);
//			no.setTextColor(0, 1, 0, 1);
//			yes.setTextPos();
//			no.setTextPos();
//		}

		// System.out.println(yes.bounds + " ||| " + yes.text.getPosition());

		// text.getPosition().set(Mouse.getAX(), Mouse.getAY());
		// text.setText(text.getPosition().toString());

		yes.setDisplayLevel(displayLevel + 1);
		no.setDisplayLevel(displayLevel + 1);

		// yes.updateRelativePos();
		// no.updateRelativePos();

		// System.out.println(yes.displayLevel + " T: " +
		// yes.text.displayLevel());
	}

	@Override
	public void fullScaleMessage() {
		
		yes.text.cleanUp();
		yes.text = null;
		yes.textYOffset = -10;
		
		no.text.cleanUp();
		no.text = null;
		no.textYOffset = -10;
		
		yes.setText("YES");
		no.setText("NO");
		yes.setTextColor(1, 0, 0, 1);
		no.setTextColor(0, 1, 0, 1);
		yes.setTextPos();
		no.setTextPos();
		yes.setDisplayLevel(displayLevel + 1);
		no.setDisplayLevel(displayLevel + 1);
	}

	public abstract void yes();

	public abstract void no();

}
