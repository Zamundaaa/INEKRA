package mainInterface;

import static data.Block.AIR;

import data.ChunkManager;
import data.Key3D;
import data.chunkLoading.QueueKeeper;
import gameStuff.WorldObjects;
import solarSystemRendering.PlanetManager;

public class SingleCM extends CM {

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
	}

}
