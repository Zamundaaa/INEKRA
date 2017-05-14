package postProcessing;

import org.lwjgl.opengl.*;

import bloom.CombineFilter;
import entities.Camera;
import gameStuff.MainLoop;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.RawModel;
import renderStuff.DisplayManager;
import renderStuff.Loader;
import toolBox.Tools;

public class PostProcessing {

	private static final float[] FINPOSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };

	public static float brightness = Tools.loadFloatPreference("Brightness", 1);

	private static RawModel quad;
	private static HorizontalBlur hblur;
	private static VerticalBlur vblur;
	private static HorizontalBlur hblur2, menuhb;
	private static VerticalBlur vblur2, menuvb;
	private static CombineFilter CF;
	private static ContrastChanger cc;

	public static void init() {
		quad = Loader.loadToVAO(FINPOSITIONS, 2);
		hblur = new HorizontalBlur(DisplayManager.getWidth() / 4, DisplayManager.getHeight() / 4);
		vblur = new VerticalBlur(DisplayManager.getWidth() / 4, DisplayManager.getHeight() / 4);
		hblur2 = new HorizontalBlur(DisplayManager.getWidth() / 2, DisplayManager.getHeight() / 2);
		vblur2 = new VerticalBlur(DisplayManager.getWidth() / 2, DisplayManager.getHeight() / 2);
		menuhb = new HorizontalBlur(DisplayManager.getWidth() / 2, DisplayManager.getHeight() / 2);
		menuvb = new VerticalBlur(DisplayManager.getWidth() / 2, DisplayManager.getHeight() / 2);

		CF = new CombineFilter();
		cc = new ContrastChanger(DisplayManager.getWidth(), DisplayManager.getHeight());
	}

	public static void recreate() {
		cleanUp();
		init();
	}

	// public static int renderRed(int colourTexture){
	// cc.setBlue(false);
	// cc.render(colourTexture);
	// return cc.getOutputTexture();
	// }
	//
	// public static int renderBlue(int colourTexture){
	// cc.setBlue(true);
	// cc.render(colourTexture);
	// return cc.getOutputTexture();
	// }

	public static void cleanUp() {
		hblur.cleanUp();
		vblur.cleanUp();
		hblur2.cleanUp();
		vblur2.cleanUp();
		menuhb.cleanUp();
		menuvb.cleanUp();
		CF.cleanUp();
		Tools.setFloatPreference("Brightness", brightness);
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL30.glBindVertexArray(0);
	}

	public static void doPostProcessing(int colourTexture, int brightTexture, int GUI, boolean renderGUI) {
		start();
		hblur.render(brightTexture);
		vblur.render(hblur.getOutputTexture());
		hblur2.render(vblur.getOutputTexture());
		vblur2.render(hblur2.getOutputTexture());
		if (MainLoop.MENUOPEN) {
			menuhb.render(colourTexture);
			menuvb.render(menuhb.getOutputTexture());
			colourTexture = menuvb.getOutputTexture();
		}
		if (Camera.underWater()) {
			cc.render(colourTexture);
			colourTexture = cc.getOutputTexture();
		}
		CF.render(colourTexture, vblur2.getOutputTexture(), GUI, renderGUI);
		// brightTexture);
		end();
	}

}
