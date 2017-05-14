package cubyWater;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import blockRendering.BlockRenderer;
import controls.Keyboard;
import cubyWaterNew.NewWaterRenderer;
import cubyWaterNew.NewWaterUpdater;
import gameStuff.*;
import renderStuff.*;
import toolBox.*;
import weather.WeatherController;

public class WaterRenderer {

	private static WaterShader w = new WaterShader();
	private static float random = Meth.randomFloat(0, 1);
	public static volatile boolean RENDERING = false;
	public static float WAVEHEIGHT = Tools.loadFloatPreference("WAVEHEIGHT", 0.05f);

	public static final int MAXWAVEMOD = 6;
	public static int WAVEMODEL = 1;
	public static boolean REFLECTIVE = Tools.loadBoolPreference("reflectiveWater", true);

	private static final int MAX_INSTANCES = 100000, INSTANCE_DATA_LENGTH = 3;
	private static final float[] vboData = new float[MAX_INSTANCES * INSTANCE_DATA_LENGTH];
	// private static final FloatBuffer buffer =
	// BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

	private static int pointer;
	private static int vbo;
	private static int watersize;

	public static void init() {
		int W = (int) Tools.loadFloatPreference("Wavemodel", 5);
		if (W <= 0) {
			W = 1;
		}
		if (W > MAXWAVEMOD) {
			W = MAXWAVEMOD;
		}
		WAVEMODEL = W;
		vbo = Loader.createEmptyVBO(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
		Loader.addInstancedAtribute(Water.side.getVaoID(), vbo, 2, 3, INSTANCE_DATA_LENGTH, 0);
		inited = true;
		
		NewWaterRenderer.init();
		
		Err.err.println("WaterRenderer inited!");
	}

	public static void render(Matrix4f viewMatrix) {
		
		if(NewWaterUpdater.useWaterMesh){
			NewWaterRenderer.render(viewMatrix);
			return;
		}
		
		// Err.err.println("rendering Water...");
		boolean WIREFRAME = BlockRenderer.WIREFRAME;
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		ArrayList<Water> waters = WaterManager.getWater();

		prepare(viewMatrix);
		bindTextures(Water.tex, Water.blinkTex);

		float divider = 1000;
		float xfact = 1;
		//
		float zfact = 1;

		float add = 0;

		boolean plattn = false;

		int funnyColors = 1;

		float wavelength = 1;
		float waveprogressinwavelengthpersecond = 0.25f;

		boolean scale = true;

		if (WAVEMODEL == 1) {
			xfact = (float) Math.sin(TM.inGameDays() / divider);
			zfact = (float) Math.cos(TM.inGameDays() / divider);
		} else if (WAVEMODEL == 2) {
			xfact = (float) Math.sin(TM.inGameDays() / divider);
			zfact = (float) Math.cos(TM.inGameDays() / divider);
			wavelength = 5 * Meth.PI;
		} else if (WAVEMODEL == 3) {
			add = 0.1f;
			funnyColors = 3;
			xfact = 0.5f;
			zfact = 0.5f;
			wavelength = 8;
			// scale = false;
			waveprogressinwavelengthpersecond = 0.05f;
		} else if (WAVEMODEL == 4) {
			add = 0.1f;
			funnyColors = 2;
			wavelength = 2;
			waveprogressinwavelengthpersecond = 1;
		} else if (WAVEMODEL == 5) {
			// add = -0.01f;
			xfact = (float) Math.sin(TM.inGameDays() / divider);
			zfact = (float) Math.cos(TM.inGameDays() / divider);
			wavelength = 5;
		} else if (WAVEMODEL == 6) {
			plattn = true;

			xfact = (float) Math.sin(TM.inGameDays() / divider);
			zfact = (float) Math.cos(TM.inGameDays() / divider);
			add = -0.01f;

			wavelength = 7;
		}

		if (scale) {
			Vects.calcVect2D.x = xfact;
			Vects.calcVect2D.y = zfact;
			Vects.scaleToLength(Vects.calcVect2D, (1 / wavelength) * 2 * Meth.PI);
			xfact = Vects.calcVect2D.x;
			zfact = Vects.calcVect2D.y;
		}

		w.loadPlattn(plattn);
		w.loadFunnyColors(funnyColors);

		random += waveprogressinwavelengthpersecond * 2 * Meth.PI * DisplayManager.getFrameTimeSeconds();
		if (random >= 2 * Meth.PI) {
			random -= 2 * Meth.PI;
		}
		w.loadRandom(random);

		w.loadTIME(TM.fromStartMillis() * 0.0005f);

		w.connectTextureUnits();
		w.loadFacts(xfact, zfact);
		float mult = Keyboard.isKeyDown(GLFW.GLFW_KEY_H) ? 5 : 1;
		w.loadWaveHeight(add + mult * WAVEHEIGHT);// *(float)Math.sin(TimeManager.gameTimeMillis())

		// for(int i = 0; i < waters.size(); i++){
		// Water x = waters.get(i);
		// if(x != null){
		// //w.loadRandom((random*(x.getPos().x + x.getPos().z)));//+
		// float add;
		//// if(!x.frozen()){
		// add = random*((xfact*1.1f*wavefact*x.getPos().x +
		// zfact*0.9f*wavefact*x.getPos().z));
		//// }else{
		//// add = 0;
		//// }
		//
		//// float add = random*((0.55f*wavefact*x.getPos().x +
		// 0.45f*wavefact*x.getPos().z));
		//// float xfact = (x.getPos().x / x.getPos().z)*5;
		//// float zfact = (x.getPos().z / x.getPos().x)*5;
		//// float add = random*(xfact*x.getPos().x + zfact*x.getPos().z);
		//
		//// float h = x.height();
		//
		//// add = Meth.clamp((float)Math.sin(add) * wavefact/30,
		// -(h*wavefact/3) + 0.01f, (h*wavefact/3) - 0.01f));
		//
		//// w.loadTransformationMatrix(Meth.createTransformationMatrix(x.getUpperPos(add),
		// 0, 0, 0, 1));
		// GL11.glDrawElements(GL11.GL_TRIANGLES,
		// CubeSide.cubeside.getRawMod().getVertexCount(), GL11.GL_UNSIGNED_INT,
		// 0);
		// }else{
		// Err.err.println("a 'null' water found in the water renderer");
		// waters.remove(i);
		// }
		// }
		// if(waters.size() != watersize || UPDATE){
		RENDERING = true;
		watersize = waters.size();
		pointer = 0;
		// vboData = new float[MAX_INSTANCES*INSTANCE_DATA_LENGTH];
		for (int i = 0; i < waters.size() && i < watersize && i < MAX_INSTANCES; i++) {
			if (waters.get(i) != null) {
				savePos(waters.get(i), vboData);
			}
			// else{
			// waters.remove(i);
			// }
		}
		Loader.updateVBO(vbo, vboData);
		RENDERING = false;
		// }
		if (waters.size() > 0) {
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, Math.min(watersize, waters.size()));
		}

		finishRendering();
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public static boolean UPDATE = false;
	public static boolean inited;

	private static void savePos(Water w, float[] vboData) {
		if (w != null) {
			vboData[pointer++] = w.getUpperPos(0).x;
			vboData[pointer++] = w.getUpperPos(0).y;// MAYBE A HEIGHT VALUE
			vboData[pointer++] = w.getUpperPos(0).z;// AGAINST WAVES?
		}
	}

	public static void bindTextures(int texID, int blinkID) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texID);
		// glActiveTexture(GL_TEXTURE1);
		// glBindTexture(GL_TEXTURE_2D, blinkID);
		if (REFLECTIVE) {
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainLoop.wfbo.getReflectionTexture());
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainLoop.wfbo.getRefractionTexture());
			GL13.glActiveTexture(GL13.GL_TEXTURE4);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainLoop.wfbo.getRefractionDepthTexture());
		}
	}

	private static void prepare(Matrix4f viewMatrix) {
		w.start();

		GL30.glBindVertexArray(Water.side.getVaoID());
		
		MasterRenderer.disableCulling();
		MasterRenderer.enableTranslucency();

		w.loadReflective(REFLECTIVE);
		w.loadSkyColor(MasterRenderer.r, MasterRenderer.g, MasterRenderer.b);
		w.loadDensity(WeatherController.getFogDensity());
		w.loadGradient(WeatherController.getFogGradient());

		w.loadTMODE(MasterRenderer.TRANSITIONMODE);
		w.loadDIST(MasterRenderer.TRANSITION_DISTANCE);
		w.loadViewMatrix(viewMatrix);
		w.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		w.loadLightValue(Vects.setCalcVect(1.0f));
		w.loadSunDir(WorldObjects.getSunDirection(Vects.calcVect, (float) TM.getDayTime()));
		
//		loadTransmat();
		
	}
	
//	private static void loadTransmat(){
//		Vects.mat4.identity();
//		if(Camera.getPosition().x != 0 || Camera.getPosition().z != 0){
//			Vector3f ang = Vects.calcVect.set(Camera.getPosition().x, 0, Camera.getPosition().z).div(PlanetManager.earthUmfang*0.5f);
//			float rad = -(ang.x+ang.z)*Meth.PI;
////			System.out.println(ang + " winkel: " + (rad*Meth.radToAng));
//			ang.cross(Vects.UP).normalize();
//			Vects.mat4.rotate(rad, ang);
////			Vects.mat4.rotateTowards
//		}
//		w.loadTransformationMatrix(Vects.mat4);
//	}

	private static void finishRendering() {
		
		GL30.glBindVertexArray(0);
		w.stop();
		MasterRenderer.disableTranslucency();
		MasterRenderer.enableCulling();

	}

	public static void cleanUp() {
		Tools.setFloatPreference("Wavemodel", WAVEMODEL);
		Tools.setFloatPreference("WAVEHEIGHT", WAVEHEIGHT);
		Tools.setBoolPreference("reflectiveWater", REFLECTIVE);
		Tools.setLongPreference("reflectionHeightMode", WaterManager.reflectionHeightMode);
		w.cleanUp();
		NewWaterRenderer.cleanUp();// seperate!!!
	}

	public static void setProjectionMatrix(Matrix4f projectionMatrix) {
		w.start();
		w.loadProjectionMatrix(projectionMatrix);
		w.stop();
	}

}
