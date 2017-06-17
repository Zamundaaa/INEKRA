package mainInterface;

import static data.Block.AIR;

import data.ChunkManager;
import gameStuff.WorldObjects;
import network.Server;
import solarSystemRendering.PlanetManager;
import toolBox.Meth;

public class ServerCM extends CM{

	@Override
	public void setB(int x, int y, int z, short ID) {
		Server.notityBlockSet(x, y, z, ID);
		ChunkManager.setBlockID(x, y, z, ID);
	}

	@Override
	public void deleteW(int x, int y, int z) {
		Server.notityBlockSet(x, y, z, AIR);
		ChunkManager.setWaterID(x, y, z, AIR);
	}

	@Override
	public void deleteW(float x, float y, float z) {
		Server.notityBlockSet((int)Meth.toInt(x), (int)Meth.toInt(y), (int)Meth.toInt(z), AIR);
		ChunkManager.setWaterID(x, y, z, AIR);
	}

	@Override
	public void setWater(float x, float y, float z, short ID) {
		Server.notityBlockSet((int)Meth.toInt(x), (int)Meth.toInt(y), (int)Meth.toInt(z), ID);
		ChunkManager.setWaterID(x, y, z, ID);
	}

	@Override
	protected void updateSpecifics() {
		ChunkManager.update();
		WorldObjects.update();
		PlanetManager.update();
	}

}
