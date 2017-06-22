package mainInterface;

import static data.Block.AIR;

import data.ChunkManager;
import data.Key3D;
import data.chunkLoading.QueueKeeper;
import entities.MWBE;
import entities.graphicsParts.ModelGraphics;
import gameStuff.SC;
import gameStuff.WorldObjects;
import particles.ParticleMaster;
import solarSystemRendering.PlanetManager;

public class SingleCM extends Intraface {
	
	@Override
	public void setB(int x, int y, int z, short ID) {
		ChunkManager.setBlockID(x, y, z, ID);
	}

	@Override
	public void deleteW(int x, int y, int z) {
		ChunkManager.setWaterID(x, y, z, AIR);
	}

	@Override
	public void deleteW(float x, float y, float z) {
		ChunkManager.setWaterID(x, y, z, AIR);
	}

	@Override
	public void setWater(float x, float y, float z, short ID) {
		ChunkManager.setWaterID(x, y, z, ID);
	}

	@Override
	protected void updateSpecifics() {
		WorldObjects.update();
		PlanetManager.update();
		ChunkManager.update();
		Key3D k = QueueKeeper.next();
		ChunkManager.markChunkForLoading(k.getX(), k.getY(), k.getZ());
//		System.out.println("marked " + k.getX() + ", " + k.getY() + ", " + k.getZ());
	}
	
	@Override
	protected void initSpecifics(){
		ParticleMaster.particlesEnabled = true;
		ChunkManager.init();
	}

	@Override
	public ModelGraphics getMG(MWBE m, short modelID, short texID) {
		return new ModelGraphics(m, SC.getModel(modelID, texID));
	}

}
