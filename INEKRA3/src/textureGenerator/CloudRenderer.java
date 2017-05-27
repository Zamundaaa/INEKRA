package textureGenerator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import models.RawModel;
import postProcessing.Fbo;
import renderStuff.Loader;
import renderStuff.MasterRenderer;
import weather.Cloud;

public class CloudRenderer {

	public static float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
	public static RawModel mod = Loader.loadToVAO(positions, 2);

	public static int getCloudTexture(float color, short seed) {
//		Err.err.println("creating one FBO for Cloud rendering...");
		Fbo fbo = new Fbo(128, 128, Fbo.DEPTH_TEXTURE);
//		Err.err.println("binding FBO for Cloud rendering...");
		fbo.bindFrameBuffer();
		MasterRenderer.disableCulling();
//		Err.err.println("Loading posmod for Cloud rendering...");
//		Err.err.println("Creating CloudShader rendering...");
		CloudShader cs = new CloudShader();
		cs.start();
		cs.loadColor(color);
		cs.loadSeed(seed);
		cs.loadAlpha(Cloud.ALPHA);
		GL30.glBindVertexArray(mod.getVaoID());
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, mod.getVertexCount());

		cs.stop();
		cs.cleanUp();
		MasterRenderer.enableCulling();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		fbo.unbindFrameBuffer();
		return fbo.getColourTexture();
		// VerticalBlur vb = new VerticalBlur(128, 128);
		// HorizontalBlur hb = new HorizontalBlur(128, 128);
		// hb.render(fbo.getColourTexture());
		// vb.render(vb.getOutputTexture());
		// fbo.cleanUp();
		// hb.cleanUp();
		// return vb.getOutputTexture();
	}

}
