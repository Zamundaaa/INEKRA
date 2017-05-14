package textureGenerator;

import org.lwjgl.opengl.*;

import postProcessing.Fbo;

public class Flipper {
	
	private static final FlipShader fs = new FlipShader();
	static{
		fs.start();
		fs.loadTexture(0);
		fs.stop();
	}
	
	public static int flipFboToTexture(Fbo one, Fbo buffer){
		buffer.bindFrameBuffer();
		buffer.clearColor1AndDepthBuffer();
		fs.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, one.getColourTexture());
		GL30.glBindVertexArray(CloudRenderer.mod.getVaoID());
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, CloudRenderer.mod.getVertexCount());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL30.glBindVertexArray(0);
		fs.stop();
		buffer.unbindFrameBuffer();
		return buffer.resolveToNewTexture(GL30.GL_COLOR_ATTACHMENT0);//buffer.getColourTexture()
	}
	
}
