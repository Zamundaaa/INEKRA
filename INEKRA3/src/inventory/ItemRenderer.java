package inventory;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import blockRendering.BlockRenderer;
import blockRendering.ChunkEntity;
import data.Block;
import data.Chunk;
import entities.Camera;
import entities.graphicsParts.Texes;
import models.RawModel;
import postProcessing.Fbo;
import renderStuff.Loader;
import renderStuff.MasterRenderer;
import textureGenerator.Flipper;
import toolBox.Meth;
import toolBox.Vects;

public class ItemRenderer {
	
	private static final Chunk testC = new Chunk();
	
	private static final int ITEM_QUALI = 512;
	private static final int art = Fbo.DEPTH_RENDER_BUFFER;
	private static final Fbo renderFbo = new Fbo(ITEM_QUALI, ITEM_QUALI, art),
			bufferFbo = new Fbo(ITEM_QUALI, ITEM_QUALI, art);
	
	private static Map<Short, Integer> texes = new HashMap<Short, Integer>();
	
	public static int getItemTex(ItemStack i){
		if(i.isBlockItem()){
			return getItemTex(i.blockID());
		}else{
			return i.get3DTex();
		}
	}

	public static int getItemTex(short blockID) {
		if(Block.isWater(blockID)){
			return Texes.getTex(Texes.WATER);
		}
		Integer I = texes.get(blockID);
		if(I != null){
			return I;
		}
		testC.set(0, 0, 0, blockID);
		ChunkEntity e = testC.genMask();
		if(e == null){
			return 0;
		}
		RawModel raw = Loader.loadToVAO3DTex(e.verts(), e.texCs(), e.norms(), e.indis(), e.lightData());
		
		renderFbo.bindFrameBuffer();
		renderFbo.clearColor1AndDepthBuffer();
		
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL11.glClearColor(0, 0.25f, 1, 0);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
//		MasterRenderer.disableCulling();
//		ColoredRectShader cs = new ColoredRectShader();
//		cs.start();
//		cs.loadColor(Vects.calcVect4D.set(1, 0, 0, 1));
//		float z = 0;
//		RawModel rect = Loader.loadToVAO(new float[]{-1, 1, z, -1, -1, z, 1, 1, z, 1, -1, z}, 3);
//		GL30.glBindVertexArray(rect.getVaoID());
//		
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
//
//		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, rect.getVertexCount());
//		
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//
//		cs.stop();
//		cs.cleanUp();
//		MasterRenderer.enableCulling();
		
		Vects.calcVect.set(Camera.getPosition());
		float pitch = Camera.getPitch();
		float rotY = Camera.getYaw();
		Camera.getPosition().set(0.75f, 1, -1);
		Camera.setPitch(40);
		Camera.setYaw(180 + 45*0.75f);
		MasterRenderer.viewMatrix = Meth.createViewMatrix();
		Matrix4f proj = MasterRenderer.createAProjectionMatrix(ITEM_QUALI, ITEM_QUALI);
		
		BlockRenderer.renderThis(raw, proj);
		
		Camera.getPosition().set(Vects.calcVect);
		Camera.setPitch(pitch);
		Camera.setYaw(rotY);
		MasterRenderer.viewMatrix = Meth.createViewMatrix();
		
		renderFbo.unbindFrameBuffer();
		
		Loader.unload(raw);
		
//		renderFbo.resolveToScreen();
//		DisplayManager.updateWindow();
//		Meth.wartn(1000);
		
		int ret = Flipper.flipFboToTexture(renderFbo, bufferFbo);
		texes.put(blockID, ret);
		return ret;
	}
	
}
