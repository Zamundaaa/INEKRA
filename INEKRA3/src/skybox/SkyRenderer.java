package skybox;

import static gameStuff.TM.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import controls.Keyboard;
import data.Generator;
import entities.Camera;
import gameStuff.*;
import models.RawModel;
import postProcessing.Fbo;
import renderStuff.*;
import solarSystemRendering.PlanetManager;
import toolBox.Meth;
import toolBox.Vects;
import weather.WeatherController;

public class SkyRenderer {

	private static final float SIZE = 3*PlanetManager.earthRadius;//Meth.pow(2, 10);
//	public static boolean SKYBOXPIC = false;

	private static final float[] VERTICES = { -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,
			-SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,

			-SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE,
			-SIZE, SIZE,

			SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE,
			-SIZE,

			-SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE,
			SIZE,

			-SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE,
			-SIZE,

			-SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE,
			-SIZE, SIZE };

//	private static String[] TEXTUREFILES = { "right", "left", "topwithoutsun", "bottom", "back", "front" };
//	private static String[] NIGHTTEXTUREFILES = { "nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack",
//			"nightFront" };
	
	// private static String[] RAINTEXTUREFILES = {"clouds", "clouds", "clouds",
	// "clouds", "clouds", "clouds"};

	private RawModel cube;

//	private int tex, nighttex;// CUBEMAP TEXTURES!
	private int moonTex;// normal Texture
	private Fbo stars;
	private static SkyboxShader shader;
	private static float time = 1f;

	public SkyRenderer(Matrix4f projection) {
		cube = Loader.loadToVAO(VERTICES, 3);
//		tex = Loader.loadCubeMap(TEXTUREFILES);
//		nighttex = Loader.loadCubeMap(NIGHTTEXTUREFILES);

		renderStars();

		moonTex = SC.getTex("sky/moon").getID();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, moonTex);
		GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, new float[] { 0, 0, 0, 0 });
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projection);
		shader.loadMoonStuff(ms);
		shader.stop();

	}

	public static final float MOONSIZEPM1 = 2;

	// private static float moonAngleInDegrees =
	// Tools.loadFloatPreference("moonAngleInDegrees", 3);
	private static float moonAngleInDegrees = data.Generator.getG().genThing(0) + MOONSIZEPM1;
	private static final Vector3f ms = new Vector3f((float) (1 - Math.cos(moonAngleInDegrees * Meth.angToRad)),
			(float) Math.sin(moonAngleInDegrees * Meth.angToRad),
			0.5f / ((float) Math.sin(moonAngleInDegrees * Meth.angToRad)));

	public static float getMoonSizeInDegrees() {
		return moonAngleInDegrees;
	}

	public static void setMoonThings() {
		moonAngleInDegrees = data.Generator.getG().genThing(0) + MOONSIZEPM1;
		ms.x = (float) (1 - Math.cos(moonAngleInDegrees * Meth.angToRad));
		ms.y = (float) Math.sin(moonAngleInDegrees * Meth.angToRad);
		ms.z = 0.5f / ms.y;
		shader.start();
		shader.loadMoonStuff(ms);
		shader.stop();
	}

	public static void updateTime(float t) {
		time = t;
	}

	public void render(Matrix4f viewMat, float r, float g, float b) {
		
		if(Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_R)){
			shader.cleanUp();
			shader = new SkyboxShader();
			shader.start();
			shader.connectTextureUnits();
			shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
			shader.loadMoonStuff(ms);
			shader.stop();
		}
		
		shader.start();
		shader.loadViewMatrix(viewMat);
		shader.loadFogColor(r, g, b);
//		shader.loadSTARS(time >= TM.eveningstart || time <= TM.morning);
		
		GL30.glBindVertexArray(cube.getVaoID());
		
		loadTextures();

		prepareSomeThings();

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void prepareSomeThings() {
		boolean inspace = Camera.inSpace();
		if(inspace){
			shader.loadBlendFactor(1);
		}else{
			shader.loadBlendFactor(blendFactor);
		}
		shader.loadShowMoon(!inspace);
		float BF = WeatherController.blendFactor();
		shader.loadWeatherFactor(forMenu ? 0 : BF);
		shader.loadWeatherColor(WeatherController.getWeatherColor());
//		shader.loadSkyPic(SKYBOXPIC);
		shader.loadTIME((float) TM.inGameDays());

		WorldObjects.getSunDirection(Vects.calcVect, time);

		shader.loadSunDirection(Vects.calcVect);
		Vects.setCalcVect(0);

//		if (!SKYBOXPIC) {
			shader.loadTimeColor(timeColor);
//		}

		CM.identity();
		CM.rotate(WorldObjects.getSunAngle(time), 0, 0, 1);
		shader.loadStarCoordTransform(CM);

	}

	private Matrix4f CM = new Matrix4f();
	private static Vector3f timeColor = new Vector3f();
//	private int texture1;
//	private int texture2;
	private float blendFactor;

	public void loadTextures() {

//		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
//		GL13.glActiveTexture(GL13.GL_TEXTURE1);
//		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, moonTex);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, stars.getColourTexture());

	}

	public void setUpFogThings() {
		if ((time >= night) || (time >= 0 && time < morningstart)) {
//			texture1 = nighttex;
//			texture2 = nighttex;
			blendFactor = 1;
		} else if (time >= morningstart && time < morning) {
//			texture1 = nighttex;
//			texture2 = tex;
			blendFactor = (time - morningstart) / (morning - morningstart);
			blendFactor = 1 - blendFactor;
		} else if (time >= morning && time < eveningstart) {
//			texture1 = tex;
//			texture2 = tex;
			blendFactor = 0;
		} else {
//			texture1 = tex;
//			texture2 = nighttex;
			blendFactor = (time - eveningstart) / (night - eveningstart);
		}
//		if (!SKYBOXPIC) {
			timeColor.x = Meth.blend(0.698f, 0, blendFactor);
			timeColor.y = Meth.blend(1, 0, blendFactor);
			timeColor.z = Meth.blend(1, 0, blendFactor);
//			float fog = Meth.blend(0.5f, 0, blendFactor);
			MasterRenderer.setFogColor(timeColor.x*0.5f, timeColor.y*0.5f, timeColor.z*0.5f);
//		}
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	private boolean forMenu = false;

	public void renderMenu(float r, float g, float b) {
		forMenu = true;
		time += DisplayManager.getFrameTimeSeconds() * 0.25f;
		time %= 24;
		setUpFogThings();
		render(MasterRenderer.viewMatrix, r, g, b);
		forMenu = false;
	}

	public float getTime() {
		return time;
	}

	public static final int STAR_QUALITY = 512;

	private void renderStars() {
		stars = new Fbo(STAR_QUALITY);
		stars.bindFrameBuffer();

		MasterRenderer.disableCulling();
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		RawModel mod = Loader.loadToVAO(positions, 2);
		StarShader s = new StarShader();
		s.start();
		GL30.glBindVertexArray(mod.getVaoID());
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (int i = 0; i < 6; i++) {
			stars.createSideForRendering(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i);
			s.loadSeed((float) ((Generator.seed / (double) Integer.MAX_VALUE) + i));
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, mod.getVertexCount());
		}
		s.stop();
		s.cleanUp();
		MasterRenderer.enableCulling();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		stars.unbindFrameBuffer();
	}

	public void cleanUp() {
		stars.cleanUp();
		shader.cleanUp();
	}

	public static float getTimeB() {
		return timeColor.z;
	}

}
