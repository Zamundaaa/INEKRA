package cubyWaterNew;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import gameStuff.*;
import models.RawModel;
import renderStuff.Loader;
import renderStuff.MasterRenderer;

public class NewWaterRenderer {
	
	public static boolean REFLECTIVE = true;
	public static float WAVEHEIGHT = 0;
	
	protected static float absHeight;
	
	protected static float[] positions, norms;
	protected static int[] indices;
	protected static boolean change = false;
	
	public static int WAVEMODEL = 1;
	public static int MAXWAVEMOD = 5;
	
	private static RawModel raw = Loader.loadToVAO(new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 1, 2});
	private static NewWaterShader w;
	
	public static void init(){
		w = new NewWaterShader();
		NewWaterUpdater.init();
	}
	
	public static void render(Matrix4f viewMatrix){
		
		if(change){
			Loader.updateVAO(raw, positions, null, norms, null, indices);
			change = false;
		}
		
		MasterRenderer.enableTranslucency();
		MasterRenderer.disableCulling();
		
		w.start();
		w.connectTextureUnits();
		w.loadViewMat(viewMatrix);
		w.loadProjMat(MasterRenderer.getProjectionMatrix());
		w.loadTime((float)TM.getDayTime());
		w.loadReflections(REFLECTIVE);
		w.loadSunLight(WorldObjects.sun.getColour());
		
		if (REFLECTIVE) {
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainLoop.wfbo.getReflectionTexture());
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainLoop.wfbo.getRefractionTexture());
			GL13.glActiveTexture(GL13.GL_TEXTURE4);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainLoop.wfbo.getRefractionDepthTexture());
		}
		
		raw.bindVAO();
		GL11.glDrawElements(GL11.GL_TRIANGLES, raw.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		raw.unbindVAO();
		
		MasterRenderer.enableCulling();
		MasterRenderer.disableTranslucency();
		
		w.stop();
	}
	
	public static void cleanUp(){
		w.cleanUp();
	}

	public static float getAverageAbsHeight() {
		return absHeight;
	}

}
