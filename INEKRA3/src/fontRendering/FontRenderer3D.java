package fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.*;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;

public class FontRenderer3D {

	private FontShader3D shader;

	public FontRenderer3D() {
		shader = new FontShader3D();
	}

	public void render(Map<FontType, List<GUIText>> texts) {// DO IT!!! Just use the matrixstuff from the particle rendering system!!!
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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
	}

	private void renderText(GUIText text) {
		GL30.glBindVertexArray(text.getMesh());
//		
//		
		shader.loadColor(text.getColour());
		shader.loadTranslation(text.getPosition());
		shader.loadDisplayLevel(text.displayLevel());
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
//		
//		
		GL30.glBindVertexArray(0);
	}

	private void endRendering() {
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}
