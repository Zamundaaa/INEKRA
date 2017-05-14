package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger {

	private ImageRenderer renderer;
	private ContrastShader shader;

	public ContrastChanger(int width, int height) {
		shader = new ContrastShader();
		renderer = new ImageRenderer(width, height, false);
	}

	public int getOutputTexture() {
		return renderer.getOutputTexture();
	}

	// public void setBlue(boolean b){
	// blue = b;
	// }
	//
	// private boolean blue;

	public void render(int texture) {
		shader.start();
		// shader.loadBlue(Camera.underWater());
		shader.loadBlue(true);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.stop();
	}

	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}

}
