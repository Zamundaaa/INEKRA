package mainInterface;

import static data.Block.AIR;

import entities.MWBE;
import entities.graphicsParts.ModelGraphics;
import gameStuff.SC;
import gameStuff.WorldObjects;
import network.Client;
import particles.ParticleMaster;

public class MultiCM extends Intraface{

	@Override
	public void setB(int x, int y, int z, short ID) {
		Client.addBlockSetNotify(x, y, z, ID);
	}

	@Override
	public void deleteW(int x, int y, int z) {
		Client.addBlockSetNotify(x, y, z, AIR);
	}

	@Override
	public void deleteW(float x, float y, float z) {
		Client.addBlockSetNotify((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), AIR);
	}

	@Override
	public void setWater(float x, float y, float z, short ID) {
		Client.addBlockSetNotify((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), ID);
	}
	
//	private long lastShoutout;
	
	@Override
	protected void updateSpecifics() {
		// TODO Auto-generated method stub
		WorldObjects.update();
//		if(Meth.systemTime() > lastShoutout + 3000){
//			System.out.println("Chunks: " + ChunkManager.getLoadedChunkList().size());
//			lastShoutout = Meth.systemTime();
//		}
	}

	@Override
	public ModelGraphics getMG(MWBE m, short modelID, short texID) {
		return new ModelGraphics(m, SC.getModel(modelID, texID));
	}

	@Override
	protected void initSpecifics() {
		Client.init();
		ParticleMaster.particlesEnabled = true;
	}
	
}
