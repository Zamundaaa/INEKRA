package mainInterface;

import static data.Block.AIR;

import cubyWaterNew.NewWaterUpdater;
import data.ChunkManager;
import entities.MWBE;
import entities.graphicsParts.ModelGraphics;
import gameStuff.WorldObjects;
import network.Server;
import toolBox.Meth;

public class ServerCM extends Intraface{

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
//		PlanetManager.update();
	}
	
	private static final ModelGraphics staticModelGraphics = new ModelGraphics(null, null){
		@Override
		public void update(){
			
		}
	};
	
	@Override
	public ModelGraphics getMG(MWBE m, short modelID, short texID) {
		return staticModelGraphics;
	}

	@Override
	protected void initSpecifics() {
		// TODO Auto-generated method stub
		ChunkManager.init();
		Server.init();
		NewWaterUpdater.init();
	}

}
