package fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.*;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;

public class FontRenderer {

	// private float fact = 1;

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}

	public void render(Map<FontType, List<GUIText>> texts) {
		prepare();
		for (FontType font : texts.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for (GUIText text : texts.get(font)) {
				renderText(text);
			}
		}
		endRendering();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

	private void prepare() {
		// fact = DisplayManager.RATIO / DisplayManager.desiredRatioForGUI;//W/H
		// / 16/9 --> (9*W)/(H*16)
		// offset = 1 - fact;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		shader.start();
	}

	private void renderText(GUIText text) {
		GL30.glBindVertexArray(text.getMesh());
		
		shader.loadColor(text.getColour());
		shader.loadTranslation(text.getPosition());
		shader.loadDisplayLevel(text.displayLevel());
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		
		GL30.glBindVertexArray(0);
	}

	private void endRendering() {
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}
