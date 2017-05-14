package guis;

import java.util.List;

import org.lwjgl.opengl.*;

import models.RawModel;
import renderStuff.Loader;
import toolBox.Meth;

public class GuiRenderer {

	private static RawModel quad;
	private static GuiShader shader;

	public static void init() {
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = Loader.loadToVAO(positions, 2);
		shader = new GuiShader();
	}

	public static void render(List<GuiTexture> guis) {
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		for (GuiTexture gui : guis) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			shader.loadTransformation(Meth.createTransformationMatrix(gui.getPos(), gui.getScale()));
			shader.loadHighLight(gui.highlight());
			shader.loadDisplayLevel(gui.displayLevel());
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for (GuiTexture gui : GUIManager.transparents()) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			shader.loadTransformation(Meth.createTransformationMatrix(gui.getPos(), gui.getScale()));
			shader.loadHighLight(gui.highlight());
			shader.loadDisplayLevel(gui.displayLevel());
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public static void cleanUp() {
		shader.cleanUp();
		Loader.unload(quad);
	}
}
