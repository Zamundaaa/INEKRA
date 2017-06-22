package blockRendering;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import collectionsStuff.ArrayListF;
import collectionsStuff.ArrayListI;
import controls.Keyboard;
import data.*;
import entities.Camera;
import entities.Light;
import gameStuff.*;
import models.RawModel;
import renderStuff.*;
import threadingStuff.ThreadManager;
import toolBox.*;
import weather.WeatherController;

public class BlockRenderer {// SHADOWS AS OPTION!!! (in MasterRenderer now)

	private static final Vector3f vergleich = new Vector3f();
	public static final int SWAPKEY = GLFW.GLFW_KEY_F9;
	public static boolean culling = true, allAtOnce = true;

	public static ArrayList<ChunkEntity> entities = new ArrayList<>();
//	public static boolean usePerPixelLighting = Tools.loadBoolPreference("perPixelLighting", false);
	public static boolean usePerPixelLighting = false;

	private static BlockShader shader;

	public static boolean WIREFRAME = false;

	public static String ordner = "texPack/";
	private static String[] texes = new String[] { "unique", "GrassSide", "GrassTop", "Stone", "Dirt", "Sand",
			"WoodSide", "WoodY", "Leave", "Sapling", "Gravel", "Glass", "GrassSideHERBST", "GrassTopHERBST",
			"GrassSideWINTER", "GrassTopWINTER", "LeavesHERBST", "LeavesWINTER", "Marble", "torch", "ka", "Grass_Tall",
			"lampSide", "red", "green", "blue", "black" };

	private static int texArray;
	private static final Light[] ls = new Light[BlockShader.MAX_LIGHTS];
	
	public static float sonarRadius;
	public static boolean sonar = true;

	public static void render(Matrix4f toShadowSpace, Vector4f clipPlane, List<Light> lights, Matrix4f viewMatrix) {
		checkForShaderSwap();
		if(Keyboard.keyPressedThisFrame(MasterRenderer.POLYLINES)){
			WIREFRAME = !WIREFRAME;
		}
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(MasterRenderer.r, MasterRenderer.g, MasterRenderer.b);
		ls[0] = lights.get(0);
		shader.loadViewMatrix(Meth.createViewMatrix(Camera.getYaw(), Camera.getPitch(), MasterRenderer.renderOrigin.x, 
				MasterRenderer.renderOrigin.y, MasterRenderer.renderOrigin.z));
		shader.loadGradient(WeatherController.getFogGradient());
		shader.loadDensity(WeatherController.getFogDensity());
		shader.loadToShadowMapSpaceMatrix(toShadowSpace);
		shader.loadShadow(TM.isDay() ? true : false);
		// shader.loadLighting(usePerPixelLighting);
		shader.loadTime(TM.fromStartMillis() * 0.0005f);
		shader.loadTMODE(MasterRenderer.TRANSITIONMODE);
		shader.loadDIST(MasterRenderer.TRANSITION_DISTANCE);
		shader.loadTimeSin((float) Math.sin(TM.fromStartMillis() * 0.0005f));
		shader.loadSun(WorldObjects.getSunDirection(Vects.calcVect, (float)TM.getDayTime()), WorldObjects.sun.getColour());
		
		loadTransmat();
		
		bindTex();
//		if (!allAtOnce) {
//			if (!culling)
//				MasterRenderer.disableCulling();
//			// MasterRenderer.enableTranslucency();// per chunk test? Later!
//
//			int csrendered = 0;
//			for (int i = 0; i < entities.size(); i++) {
//				ChunkEntity e = entities.get(i);
//				if (MasterRenderer.FI.testAab(e.getX(), e.getY(), e.getZ(), e.getX() + Chunk.SIZE,
//						e.getY() + Chunk.SIZE, e.getZ() + Chunk.SIZE)) {
//					RawModel mod = entities.get(i).getMod();
//					prepareModel(mod);
//					prepareInstance(entities.get(i));
//					// MasterRenderer.enableTranslucency();
//					GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
//					csrendered++;
//					// MasterRenderer.disableTranslucency();
//				}
//			}
//			// MasterRenderer.disableTranslucency();
//			if (!culling)
//				MasterRenderer.enableCulling();
//			MainLoop.chunksRendered = csrendered;
//		} else {
			renderAllAtOnce();
//		}
		unbindTexturedModel();
		shader.stop();
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	private static void prepareModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
	}

	private static void unbindTexturedModel() {
//		
//		
//		
//		
		GL30.glBindVertexArray(0);
	}

//	private static void prepareInstance(ChunkEntity entity) {
//		Matrix4f transformationMatrix = Meth.createTransformationMatrix(entity.getX(), entity.getY(), entity.getZ(),
//				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale(), Vects.mat4);
//		shader.loadTransformationMatrix(transformationMatrix);
//		if (usePerPixelLighting) {
//			LightMaster.getLights(entity, ls);
//		}
//		shader.loadLights(ls);
//	}

	public static void setProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public static void cleanUp() {
		shader.cleanUp();
		Tools.setBoolPreference("perPixelLighting", usePerPixelLighting);
	}

	private static boolean useShadowsShader = false;

	public static void render(Vector4f clipPlane, List<Light> lights, Matrix4f viewMatrix) {
		checkForShaderSwap();
		if(Keyboard.keyPressedThisFrame(MasterRenderer.POLYLINES)){
			WIREFRAME = !WIREFRAME;
		}
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(MasterRenderer.r, MasterRenderer.g, MasterRenderer.b);
		ls[0] = lights.get(0);
		shader.loadViewMatrix(viewMatrix);
		shader.loadGradient(WeatherController.getFogGradient());
		shader.loadDensity(WeatherController.getFogDensity());
		
		loadTransmat();
		
		// shader.loadShadow(false);

		shader.loadLighting(usePerPixelLighting);

		shader.loadTime(TM.fromStartMillis() * 0.0005f);// (float)
														// TM.gameTimeMillis() *
														// 0.1f
		shader.loadTimeSin((float) Math.sin(TM.fromStartMillis() * 0.0005f));
		shader.loadTMODE(MasterRenderer.TRANSITIONMODE);
		shader.loadDIST(MasterRenderer.TRANSITION_DISTANCE);
		shader.loadSun(WorldObjects.getSunDirection(Vects.calcVect, (float)TM.getDayTime()), WorldObjects.sun.getColour());
		
		bindTex();
//		if (!allAtOnce) {
//			if (!culling)
//				MasterRenderer.disableCulling();
//
//			int csrendered = 0;
//			for (int i = 0; i < entities.size(); i++) {
//				ChunkEntity e = entities.get(i);
//				if (MasterRenderer.FI.testAab(e.getX(), e.getY(), e.getZ(), e.getX() + Chunk.SIZE,
//						e.getY() + Chunk.SIZE, e.getZ() + Chunk.SIZE)) {
//					RawModel mod = entities.get(i).getMod();
//					prepareModel(mod);
//					prepareInstance(entities.get(i));
//					GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
//					csrendered++;
//				}
//			}
//
//			// MasterRenderer.disableTranslucency();
//			if (!culling)
//				MasterRenderer.enableCulling();
//
//			MainLoop.chunksRendered = csrendered;
//		} else {
			renderAllAtOnce();
//		}
		unbindTexturedModel();
		shader.stop();
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public static void init() {

		loadShader();
		setProjectionMatrix(MasterRenderer.getProjectionMatrix());

		for (int i = 0; i < texes.length; i++) {
			texes[i] = ordner + texes[i];
		}
		boolean[] args = new boolean[texes.length];
		// for(short i = 0; i < Block.lastNormalBlock(); i++){
		// args[i] = !Block.isTransparent(i);
		// }
		// for(int i = 0; i < args.length; i++){
		// args[i] = true;
		// }
		args[Block.TALL_GRASS] = false;
		texArray = Loader.loadTextureArray(texes, args);

		Err.err.println("BlockRenderer inited!");

	}

	public static void bindTex() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texArray);
	}

	private static void checkForShaderSwap() {
		if (MasterRenderer.SHADOWS) {
			if (!useShadowsShader) {
				useShadowsShader = true;
				reloadShader();
			}
		} else {
			if (useShadowsShader) {
				useShadowsShader = false;
				reloadShader();
			}
		}

		if (Keyboard.keyTipped(SWAPKEY)) {
			reloadShader();
		}
	}

	private static void loadShader() {
		if (useShadowsShader) {
			shader = BlockShader.getNewBlockShaderWithShadows();
		} else {
			shader = BlockShader.getNewBlockShaderWithoutShadows();
		}
	}

	private static void reloadShader() {
		shader.cleanUp();
		loadShader();
		setProjectionMatrix(MasterRenderer.getProjectionMatrix());
	}

	private static RawModel rm;
	private static float[] vertices, texCoords, normals, lightData;
	private static int[] indices;
	private static ArrayList<ChunkEntity> es = new ArrayList<ChunkEntity>();
	
//	private static ArrayList<ChunkEntity> cesOT = new ArrayList<ChunkEntity>();

	public static boolean doneThisFrame = false;
	public static int VERTICES = 0;

	private static void renderAllAtOnce() {
		if(masker == null || !masker.isAlive()){
			startMasker();
		}
//		if (!doneThisFrame) {
//			int key = FramePerformanceLogger.stopTime();
//			// if(!MainLoop.renderingRefraction){
//			doneThisFrame = true;
//			// long millis = System.currentTimeMillis();
//			
//			int indiCount = 0, vertCount = 0;
//			es = entities;
//			for (int i = 0; i < es.size(); i++) {
//				indiCount += es.get(i).indis().length;
//				vertCount += es.get(i).verts().length;
//			}
//			if(indis == null || indiCount != indis.length || vertCount != verts.length
//					|| !MasterRenderer.renderOrigin.equals(vergleich)){
//				vergleich.set(MasterRenderer.renderOrigin);
//				verts = new float[vertCount];
//				texcs = new float[vertCount];
//				norms = new float[vertCount];
//				light = new float[(vertCount / 3) * 4];
//				indis = new int[indiCount];
//				int pointer = 0, ipointer = 0, lpointer = 0;
//				int last = 0, ka = 0;
//				for (int i = 0; i < es.size(); i++) {
//					for (ka = 0; ka < es.get(i).verts().length; ka += 3) {
//						verts[pointer] = es.get(i).verts()[ka]
//								+ (es.get(i).getX() - (Camera.getPosition().x - MasterRenderer.renderOrigin.x));
//						verts[pointer + 1] = es.get(i).verts()[ka + 1]
//								+ (es.get(i).getY() - (Camera.getPosition().y - MasterRenderer.renderOrigin.y));
//						verts[pointer + 2] = es.get(i).verts()[ka + 2]
//								+ (es.get(i).getZ() - (Camera.getPosition().z - MasterRenderer.renderOrigin.z));
//	
//						texcs[pointer] = es.get(i).texCs()[ka];
//						texcs[pointer + 1] = es.get(i).texCs()[ka + 1];
//						texcs[pointer + 2] = es.get(i).texCs()[ka + 2];
//	
//						norms[pointer] = es.get(i).norms()[ka];
//						norms[pointer + 1] = es.get(i).norms()[ka + 1];
//						norms[pointer + 2] = es.get(i).norms()[ka + 2];
//						
//						pointer += 3;
//					}
//					for (ka = 0; ka < es.get(i).lightData().length; ka++) {
//						light[lpointer++] = es.get(i).lightData()[ka];
//					}
//					for (ka = 0; ka < es.get(i).indis().length; ka++) {
//						indis[ipointer++] = es.get(i).indis()[ka] + last;
//					}
//					last += es.get(i).verts().length / 3;
//				}
//				VERTICES = verts.length/3;
//			}
			if(newMaskAvailable){
				if (rm == null) {
					rm = Loader.loadToVAO3DTex(vertices, texCoords, normals, indices, lightData);
				} else {
					Loader.updateVAO3DTex(rm, vertices, texCoords, normals, indices, lightData);
				}
//				if(vertices.length > 0){
//					System.out.println(vertices[0] + ", " + vertices[1] + ", " + vertices[2]);
//				}else{
//					System.out.println("No verts!!!");
//				}
				privateRenderOrigin.set(vergleich);
				newMaskAvailable = false;
			}
//			FramePerformanceLogger.writeStoppedTime(key, keyword);
//		}
		if(rm != null){
			prepareModel(rm);
			ls[0] = WorldObjects.sun;
			shader.loadLights(ls);
			shader.loadViewMatrix(Meth.createViewMatrix(viewMatrix, Camera.getYaw(), Camera.getPitch(), 
					Camera.getPosition().x-privateRenderOrigin.x - 0.5f, 
					Camera.getPosition().y-privateRenderOrigin.y - 0.5f, 
					Camera.getPosition().z-privateRenderOrigin.z - 0.5f));
//			System.out.println("rendering blocks! " + privateRenderOrigin);
			shader.loadSonar(sonar);
			if(sonar){
				shader.loadSonarRadius(sonarRadius);
			}
			GL11.glDrawElements(GL11.GL_TRIANGLES, rm.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
	}
	
	public static final float sonarSpeed = 33*1.5f;
	
	private static Vector3f privateRenderOrigin = new Vector3f();
	
	private static final Matrix4f viewMatrix = new Matrix4f();
	
	private static boolean newMaskAvailable = false;
	public static Thread masker;
	
	private static void startMasker(){
		masker = new Thread("ChunkMasker"){
			@Override
			public void run(){
//				try{
					Err.err.println("ChunkMasker inited!");
					ArrayListF verts = new ArrayListF(), norms = new ArrayListF(), texes = new ArrayListF(), lights = new ArrayListF();
					ArrayListI indis = new ArrayListI();
					ArrayList<Chunk> chunks = ChunkManager.getLoadedChunkList();
					while(ThreadManager.running()){
						boolean change = false;
						while(!change){
							Meth.wartn(20);
//							if(chunks.size() != 0)System.out.println("there are chunks");
							for(int i = 0; i < chunks.size(); i++){
								if(chunks.get(i).maskNeeded()){
									change = true;
									break;
								}
							}
						}
						verts.clear();
						norms.clear();
						texes.clear();
						lights.clear();
						indis.clear();
						ChunkEntity ce;
						vergleich.set(MasterRenderer.renderOrigin);
//						int first = chunks.size();
						for(int i = 0; i < chunks.size(); i++){// && chunks.size() >= first
							Chunk c = chunks.get(i);
							ce = c.getMask();
							if(ce != null){
								int vl = verts.size()/3;
								for(int I = 0; I < ce.verts().length; I+=3){// should probably replace the arrays of ChunkEntity with ArrayListX's
									verts.add(ce.verts()[I]
											+ (c.realX() - vergleich.x));
									verts.add(ce.verts()[I+1]
											+ (c.realY() - vergleich.y));
									verts.add(ce.verts()[I+2]
											+ (c.realZ() - vergleich.z));
									norms.add(ce.norms()[I]);
									norms.add(ce.norms()[I+1]);
									norms.add(ce.norms()[I+2]);
									texes.add(ce.texCs()[I]);
									texes.add(ce.texCs()[I+1]);
									texes.add(ce.texCs()[I+2]);
								}
								for(int I = 0; I < ce.lightData().length; I++)
									lights.add(ce.lightData()[I]);
								for(int I = 0; I < ce.indis().length; I++)
									indis.add(vl+ce.indis()[I]);
							}
						}
//						if(chunks.size() < first){
//							continue;
//						}
						vertices = verts.capToArray();
						VERTICES = vertices.length;
						normals = norms.capToArray();
						texCoords = texes.capToArray();
						lightData = lights.capToArray();
						indices = indis.capToArray();
						
						newMaskAvailable = true;
						while(newMaskAvailable)
							Meth.wartn(5);
					}
//				}catch(Exception e){
//					
//				}
				Err.err.println("ChunkMasker stopped (of natural causes)!");
			}
		};
		masker.start();
	}
	
	public static void renderForShadowMap() {
		if (!doneThisFrame) {
			FramePerformanceLogger.stopTime();
			// if(!MainLoop.renderingRefraction){
			doneThisFrame = true;
			// long millis = System.currentTimeMillis();
			int indiCount = 0, vertCount = 0;
			// es.clear();
			// for (int i = 0; i < entities.size(); i++) {
			// ChunkEntity e = entities.get(i);
			// if (MasterRenderer.FI.testAab(e.getX(), e.getY(), e.getZ(),
			// e.getX() + Chunk.SIZE,
			// e.getY() + Chunk.SIZE, e.getZ() + Chunk.SIZE)) {
			// indiCount += e.indis().length;
			// vertCount += e.verts().length;
			// es.add(e);
			// }
			// }
			//
			es = entities;
			for (int i = 0; i < es.size(); i++) {
				indiCount += es.get(i).indis().length;
				vertCount += es.get(i).verts().length;
			}
			//
			if (indices == null || indices.length != indiCount || vertices.length != vertCount
					|| MasterRenderer.renderOrigin.x != vergleich.x || MasterRenderer.renderOrigin.y != vergleich.y
					|| MasterRenderer.renderOrigin.z != vergleich.z) {
				vergleich.set(MasterRenderer.renderOrigin);
				vertices = new float[vertCount];
				texCoords = new float[vertCount];
				normals = new float[vertCount];
				lightData = new float[(vertCount / 3) * 4];
				indices = new int[indiCount];
				int pointer = 0, ipointer = 0, lpointer = 0;
				int last = 0, ka = 0;
				for (int i = 0; i < es.size(); i++) {
					for (ka = 0; ka < es.get(i).verts().length; ka += 3) {
						vertices[pointer] = es.get(i).verts()[ka]
								+ (es.get(i).getX() - Camera.getPosition().x);//(Camera.getPosition().x - MasterRenderer.renderOrigin.x)
						vertices[pointer + 1] = es.get(i).verts()[ka + 1]
								+ (es.get(i).getY() - Camera.getPosition().y);//(Camera.getPosition().y - MasterRenderer.renderOrigin.y)
						vertices[pointer + 2] = es.get(i).verts()[ka + 2]
								+ (es.get(i).getZ() - Camera.getPosition().z);//(Camera.getPosition().z - MasterRenderer.renderOrigin.z)

						texCoords[pointer] = es.get(i).texCs()[ka];
						texCoords[pointer + 1] = es.get(i).texCs()[ka + 1];
						texCoords[pointer + 2] = es.get(i).texCs()[ka + 2];

						normals[pointer] = es.get(i).norms()[ka];
						normals[pointer + 1] = es.get(i).norms()[ka + 1];
						normals[pointer + 2] = es.get(i).norms()[ka + 2];
						pointer += 3;
					}
					for (ka = 0; ka < es.get(i).lightData().length; ka++) {
						lightData[lpointer++] = es.get(i).lightData()[ka];
					}
					for (ka = 0; ka < es.get(i).indis().length; ka++) {
						indices[ipointer++] = es.get(i).indis()[ka] + last;
					}
					last += es.get(i).verts().length / 3;
				}
				VERTICES = vertices.length;
			}

			if (rm == null) {
				rm = Loader.loadToVAO3DTex(vertices, texCoords, normals, indices, lightData);
			} else {
				Loader.updateVAO3DTex(rm, vertices, texCoords, normals, indices, lightData);
			}
			
			FramePerformanceLogger.writeStoppedTime(keyword + " for ShadowMapping!");
		}
		
		shader.loadViewMatrix(Vects.mat4.set(MasterRenderer.viewMatrix).setTranslation(0, 0, 0));
		
		GL30.glBindVertexArray(rm.getVaoID());
//		
//		
		MasterRenderer.disableCulling();
		GL11.glDrawElements(GL11.GL_TRIANGLES, rm.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
//		
//		
		GL30.glBindVertexArray(0);
	}
	
	private static String keyword = "Putting ChunkData Together";
	
	private static Vector3f dir = new Vector3f(0.75f, 0.5f, 0).normalize();
	
	public static void renderThis(RawModel raw, Matrix4f proj) {
		shader.start();
		shader.loadClipPlane(Vects.NULL4);
		shader.loadSkyColor(MasterRenderer.r, MasterRenderer.g, MasterRenderer.b);
//		shader.loadViewMatrix(viewMatrix);
		shader.loadGradient(WeatherController.getFogGradient());
		shader.loadDensity(WeatherController.getFogDensity());

		// shader.loadShadow(false);

//		shader.loadLighting(usePerPixelLighting);

		shader.loadTime(TM.fromStartMillis() * 0.0005f);// (float)
														// TM.gameTimeMillis() *
														// 0.1f
		
		shader.loadTransformationMatrix(Vects.mat4.identity());
		
//		ls[0] = WorldObjects.sun;
//		shader.loadLights(ls);
		
		shader.loadTimeSin((float) Math.sin(TM.fromStartMillis() * 0.0005f));
		shader.loadTMODE(MasterRenderer.TRANSITIONMODE);
		shader.loadDIST(MasterRenderer.TRANSITION_DISTANCE);
		shader.loadSun(dir, Vects.ONE);
		
		shader.loadViewMatrix(MasterRenderer.viewMatrix);
		shader.loadProjectionMatrix(proj);
		
		bindTex();
		GL30.glBindVertexArray(raw.getVaoID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, raw.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL30.glBindVertexArray(0);
		
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		
		shader.stop();
	}
	
	private static void loadTransmat(){
		Vects.mat4.identity();
//		if(Camera.getPosition().x != 0 || Camera.getPosition().z != 0){
//			Vector3f ang = Vects.calcVect.set(Camera.getPosition().x, 0, Camera.getPosition().z).div(PlanetManager.earthUmfang*0.5f);
//			float rad = -(ang.x+ang.z)*Meth.PI;
////			System.out.println(ang + " winkel: " + (rad*Meth.radToAng));
//			ang.cross(Vects.UP).normalize();
//			Vects.mat4.rotate(rad, ang);
//		}
		shader.loadTransformationMatrix(Vects.mat4);
	}

}