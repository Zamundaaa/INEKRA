package bloom;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import gameStuff.WorldObjects;
import postProcessing.ImageRenderer;
import postProcessing.PostProcessing;
import renderStuff.MasterRenderer;
import toolBox.Vects;

public class CombineFilter {

	private ImageRenderer renderer;
	private CombineShader shader;

	public CombineFilter() {
		shader = new CombineShader();
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
		renderer = new ImageRenderer();
	}

	public void render(int colourTexture, int highlightTexture, int GUI) {
		shader.start();
		shader.loadBrightness(PostProcessing.brightness);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GUI);
		renderer.renderQuad();
		shader.stop();
	}

	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}

	public void render(int colourTexture, int highlightTexture, int GUI, boolean renderGUI, Vector3f v) {
		shader.start();
		shader.loadBrightness(PostProcessing.brightness);
		shader.loadRenderGUI(renderGUI);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GUI);
		shader.loadSunDir(v);
		shader.loadInvProj(Vects.mat4.set(MasterRenderer.getProjectionMatrix()).invert());
		shader.loadInvView(Vects.mat4.set(MasterRenderer.viewMatrix).invert());
		if(WorldObjects.sun != null)
			shader.loadSunColour(WorldObjects.sun.getColour());
		else
			shader.loadSunColour(Vects.ONE);
		renderer.renderQuad();
		shader.stop();
	}

}
