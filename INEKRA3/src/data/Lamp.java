package data;

import collectionsStuff.SmartByteBuffer;

public class Lamp extends SpecialBlock {

//	private static final Vector3f COLOR = new Vector3f(1);

//	private Light l;

	public Lamp(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void update() {

	}

	@Override
	public void cleanUp() {
//		LightMaster.removeLight(l);
		LightMaster.addLightUpdate(x, y, z);
	}

	@Override
	public void initAfterGen() {
		ChunkManager.setTorchLight(x, y, z, Chunk.MAXL);
//		l = new Light(new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f), COLOR);
//		LightMaster.addLight();
		LightMaster.addLightUpdate(x, y, z);
	}

	@Override
	public int metaDataLength() {
		return 0;
	}

	@Override
	public void applyMetaData(SmartByteBuffer data) {
		
	}

	@Override
	public void addMetaData(SmartByteBuffer data) {
		
	}
	
	@Override
	public String toString(){
		return "Lamp";
	}

}
