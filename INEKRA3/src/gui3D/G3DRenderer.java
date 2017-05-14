package gui3D;

import org.lwjgl.opengl.*;

import models.RawModel;
import renderStuff.Loader;
import renderStuff.MasterRenderer;

public class G3DRenderer {

	private static final float standardDistance = 1;
	private static G3DShader shader;
	private static RawModel mod;

	public static void init() {
		mod = Loader.loadToVAO(new float[] { -1, 1, -standardDistance, -1, -1, -standardDistance, 1, 1,
				-standardDistance, 1, -1, -standardDistance }, 3);
		shader = new G3DShader();
	}

	public static void render() {
		if (G3DM.getTexes().size() > 0) {
			prepare();
			for (int i = 0; i < G3DM.getTexes().size(); i++) {
				GuiTex gt = G3DM.getTexes().get(i);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, gt.getTex());
				shader.loadPos(gt.getPosition());
				shader.loadScale(gt.getScale());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, mod.getVertexCount());
			}
			stopRendering();
		}
	}

	private static void prepare() {
		shader.start();
		GL30.glBindVertexArray(mod.getVaoID());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
	}

	private static void stopRendering() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public static void cleanUp() {
		shader.cleanUp();
	}

}
