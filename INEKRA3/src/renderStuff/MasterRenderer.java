package renderStuff;

import static org.lwjgl.opengl.GL11.*;

import java.lang.Math;
import java.util.*;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import audio.AudioMaster;
import blockRendering.BlockRenderer;
import cubyWater.WaterRenderer;
import entities.*;
import gameStuff.MainLoop;
import gameStuff.TickManager;
import line.LineRenderer;
import models.TexturedModel;
import particles.ParticleMaster;
import particles.ParticleRenderer;
import shaders.StaticShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyRenderer;
import solarSystemRendering.PlanetRenderer;
import toolBox.*;
import weather.WeatherController;

public abstract class MasterRenderer {
	
	public static final boolean lenseFlare = true;

	public static int POLYLINES = GLFW.GLFW_KEY_COMMA;

	public static final Vector3f renderOrigin = new Vector3f();

	public static int ZOOMKEY = GLFW.GLFW_KEY_Z;

	public static final float NEAR_PLANE = 0.01f, FAR_PLANE = Meth.pow(2, 16);
	public static int TRANSITIONMODE = (int) Tools.loadLongPreference("TRANSITIONMODE", 1);
	public static float TRANSITION_DISTANCE = Tools.loadFloatPreference("TRANSITIONDISTANCE", 50);
	public static final int TRANSITION_MAX = 2;
//	public static boolean SHADOWS = Tools.loadBoolPreference("SHADOWS", false);
	public static boolean SHADOWS = false;
	public static boolean dontUseShadowsAtAll = true;

	public static float FOV = 70;
	public static float r = 0.5444f;
	public static float g = 0.62f;
	public static float b = 0.69f;

	private static Matrix4f projectionMatrix;

	private static StaticShader shader = new StaticShader();
	private static EntityRenderer renderer;
	// private CubeSideRenderer csrenderer;
	// private CubeSideShader csshader;
	private static SkyRenderer skyRenderer;
	private static LenseFlare lf;
	// private NormalMappingRenderer nmr;
	// private MeshRenderer mr;

	// private static ShadowMapMasterRenderer smmr;

	private static Map<TexturedModel, List<MWBE>> entities = new HashMap<TexturedModel, List<MWBE>>(),
			normalMapEntites = new HashMap<TexturedModel, List<MWBE>>();

	public static Matrix4f viewMatrix;
	public static FrustumIntersection FI;

	public static void init() {
		enableCulling();
		createProjectionMatrix();
//		Err.err.println("---------creating EntityRenderer");
		renderer = new EntityRenderer(shader, projectionMatrix);
//		Err.err.println("---------creating SkyRenderer");
		if (skyRenderer == null)
			skyRenderer = new SkyRenderer(projectionMatrix);

		// smmr = new ShadowMapMasterRenderer(cam);
//		Err.err.println("---------creating ShadowRenderer");
		if(!dontUseShadowsAtAll)
			ShadowMapMasterRenderer.init();
//		Err.err.println("---------creating BlockRenderer");
		BlockRenderer.init();
//		Err.err.println("---------creating WaterRenderer");
		WaterRenderer.init();
//		Err.err.println("---------creating ParticleRenderer");
		if (!ParticleRenderer.inited)
			ParticleMaster.init(projectionMatrix);

		FI = new FrustumIntersection();
		
		if(lenseFlare)
			lf = new LenseFlare(1, 1.1f);

	}

	// public static void initForMenu() {
	// enableCulling();
	// createProjectionMatrix();
	// skyRenderer = new SkyRenderer(projectionMatrix);
	// if (!ParticleRenderer.inited)
	// ParticleMaster.init(projectionMatrix);
	// }

	public static void setFogColor(float r, float g, float b) {
		MasterRenderer.r = r;
		MasterRenderer.g = g;
		MasterRenderer.b = b;
	}

	public static Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public static void renderAll(List<Entity> ents, List<Entity> normalEnts, List<Light> lights, Vector4f clipPlane) {
		for (int i = 0; i < ents.size(); i++) {
			processEntity(ents.get(i));
		}
		if (normalEnts != null) {
			for (int i = 0; i < normalEnts.size(); i++) {
				processNormalMapEntity(normalEnts.get(i));
			}
		}
		render(lights, clipPlane);
		// mr.render(cam);
	}

	private static final Matrix4f calcMat = new Matrix4f();

//	private static boolean ZOOM = false;
//	private static final float ZOOMDIST = 20;

	public static void render(List<Light> lights, Vector4f clipPlane) {

		renderOrigin.x = 500*(((int)Camera.getPosition().x) / 500);
		renderOrigin.y = 500*(((int)Camera.getPosition().y) / 500);
		renderOrigin.z = 500*(((int)Camera.getPosition().z) / 500);

		// GL11.glViewport(0, DisplayManager.getYGUIOffset(),
		// DisplayManager.WIDTH,
		// DisplayManager.HEIGHT-DisplayManager.getYGUIOffset()*2);
//		ZOOM = false;
		if (!MainLoop.renderForWater) {
//			ZOOM = Keyboard.isKeyDown(ZOOMKEY);
//			if (ZOOM) {
//				Vects.calcVect2.set(Camera.getPosition());
//				MousePicker.getPoint(ZOOMDIST, Vects.calcVect);
//				Camera.setPosition(Vects.calcVect.x, Vects.calcVect.y, Vects.calcVect.z);
//			}
			if(lenseFlare)
				lf.update();
		}

		viewMatrix = Meth.createViewMatrix();// build in to all to
												// minize
												// cumputation
												// power!
		FI.set(projectionMatrix.mul(viewMatrix, calcMat));

		prepare();

		skyRenderer.setUpFogThings();
		skyRenderer.render(viewMatrix, r, g, b);

		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(r, g, b);
		shader.loadLights(lights);
		shader.loadViewMatrix(viewMatrix);

		renderer.render(entities, ShadowMapMasterRenderer.getToShadowMapSpaceMatrix()); //
		shader.stop();

		viewMatrix.translate(Camera.getPosition());
		viewMatrix.translate(-renderOrigin.x, -renderOrigin.y, -renderOrigin.z);
		if (SHADOWS) {
			BlockRenderer.render(ShadowMapMasterRenderer.getToShadowMapSpaceMatrix(), clipPlane, lights, viewMatrix);
		} else {
			BlockRenderer.render(clipPlane, lights, viewMatrix);
		}
		// viewMatrix.setTranslation(-Camera.getPosition().x,
		// -Camera.getPosition().y, -Camera.getPosition().z);
		viewMatrix.translate(renderOrigin);
		viewMatrix.translate(-Camera.getPosition().x, -Camera.getPosition().y, -Camera.getPosition().z);

		LineRenderer.render(viewMatrix, clipPlane);
		// nmr.render(normalMapEntites, clipPlane, lights, cam);
		// if(!WeatherController.isRaining()){

		if (!MainLoop.renderForWater) {
			WaterRenderer.render(viewMatrix);
		}

		// }
		// gui.update();
		
		PlanetRenderer.render();
		
		if (clipPlane == Vects.NULL4) {
			ParticleMaster.renderParticles(viewMatrix);
		} else {
			ParticleMaster.renderParticles(Meth.waterHeight, clipPlane.y > 0);
		}

		entities.clear();
		normalMapEntites.clear();

//		if (ZOOM) {
//			Camera.setPosition(Vects.calcVect2);
//		}

	}

	public static void renderShadowMap(List<Entity> entityList, Light sun) {
		if (SHADOWS) {
			for (MWBE ent : entityList) {
				processEntity(ent);
			}
			// for (MWBE ent : BlockRenderer.entities) {
			// processEntity(ent);
			// }
			ShadowMapMasterRenderer.render(entities, BlockRenderer.entities, sun);
			entities.clear();
		}
	}

	// public static int getShadowMap() {
	// return smmr.getShadowMap();
	// }

	public static void enableDepthTest() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthTest() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableTranslucency() {
		glDepthMask(false);
		// glDisable(GL_DEPTH_WRITEMASK);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void disableTranslucency() {
		glDepthMask(true);
		glDisable(GL_BLEND);
		// glEnable(GL_DEPTH_WRITEMASK);
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
//		GL11.glEnable(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
//		GL11.glDisable(GL11.GL_BACK);
	}

	public static void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) DisplayManager.getRenderWidth() / (float) DisplayManager.getRenderHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
	}

	public static void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		clear();
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		if(SHADOWS)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ShadowMapMasterRenderer.getShadowMap());
	}

	public static void clear() {
		// glDepthMask(true);
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public static void processEntity(MWBE ent) {
		TexturedModel entModel = ent.getModel();
		List<MWBE> batch = entities.get(entModel);
		if (batch != null) {
			batch.add(ent);
		} else {
			List<MWBE> newBatch = new ArrayList<MWBE>();
			newBatch.add(ent);
			entities.put(entModel, newBatch);
		}
	}

	public static void processNormalMapEntity(Entity ent) {
		TexturedModel entModel = ent.getModel();
		List<MWBE> batch = entities.get(entModel);
		if (batch != null) {
			batch.add(ent);
		} else {
			List<MWBE> newBatch = new ArrayList<MWBE>();
			newBatch.add(ent);
			normalMapEntites.put(entModel, newBatch);
		}
	}

	public static void cleanUp() {

		if (shader != null)
			shader.cleanUp();
		if(!dontUseShadowsAtAll)
			ShadowMapMasterRenderer.cleanUp();
		
		BlockRenderer.cleanUp();
		WaterRenderer.cleanUp();

		ParticleMaster.cleanUp();

		Tools.setLongPreference("TRANSITIONMODE", TRANSITIONMODE);
		Tools.setFloatPreference("TRANSITIONDISTANCE", TRANSITION_DISTANCE);
		if(!dontUseShadowsAtAll)
			Tools.setBoolPreference("SHADOWS", SHADOWS);
		
		if(lenseFlare)
			lf.cleanUp();
		
	}

	public static void reload() {
		createProjectionMatrix();
		// csrenderer.setProjectionMatrix(projectionMatrix);
		if (renderer != null)
			renderer.setProjectionMatrix(projectionMatrix);
		if (skyRenderer != null)
			skyRenderer.setProjectionMatrix(projectionMatrix);
		if (LineRenderer.inited)
			LineRenderer.setProjectionMatrix(projectionMatrix);
		if (WaterRenderer.inited)
			WaterRenderer.setProjectionMatrix(projectionMatrix);
		if (ParticleRenderer.inited)
			ParticleRenderer.setProjectionMatrix(projectionMatrix);
		BlockRenderer.setProjectionMatrix(projectionMatrix);
	}

	public static float menuRot = 85, menuYRot = -30;

	public static void renderSkyForMenu() {
		// boolean rot = true;
		// if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
		// menuRot -= 10 * DisplayManager.getFrameTimeSeconds();
		// rot = false;
		// }
		// if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D)) {
		// menuRot += 10 * DisplayManager.getFrameTimeSeconds();
		// rot = false;
		// }
		// if (rot) {
		menuRot += DisplayManager.getFrameTimeSeconds();
		menuRot %= 360;
		// }
		// if (Keyboard.isKeyDown(GLFW.GLFW_KEY_S)) {
		// menuYRot -= 10 * DisplayManager.getFrameTimeSeconds();
		// }
		// if (Keyboard.isKeyDown(GLFW.GLFW_KEY_W)) {
		// menuYRot += 10 * DisplayManager.getFrameTimeSeconds();
		// }
		viewMatrix = Meth.createViewMatrix(menuRot, menuYRot, 0, 0, 0);

		skyRenderer.renderMenu(r, g, b);

		WeatherController.updateForMenu(skyRenderer.getTime());

		TickManager.update();
		ParticleMaster.update();
		ParticleMaster.renderParticles(viewMatrix);

		AudioMaster.setListenerData(Camera.getPosition(), Vects.NULL, menuRot);

	}
	
	private static Matrix4f mat = new Matrix4f();

	public static Matrix4f createAProjectionMatrix(int w, int h) {
		float aspectRatio = (float) w / (float) h;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		mat.m00(x_scale);
		mat.m11(y_scale);
		mat.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		mat.m23(-1);
		mat.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		mat.m33(0);
		return mat;
	}
	
	public static Vector2f toScreenSpace(Vector3f worldPos){
		return convertToScreenSpace(worldPos, viewMatrix, projectionMatrix);
	}
	
	public static Vector2f convertToScreenSpace(Vector3f worldPos, Matrix4f viewMat, Matrix4f projectionMat) {
        Vector4f coords = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
        viewMat.transform(coords);
        projectionMat.transform(coords);
        if (coords.w <= 0) {
            return null;
        }
        //no need for conversion below
        return new Vector2f(coords.x / coords.w , coords.y / coords.w);
    }

}
