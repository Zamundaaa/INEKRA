package data;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import entities.Light;

public class Lamp extends SpecialBlock {

	private static final Vector3f COLOR = new Vector3f(1);

	private Light l;

	public Lamp(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void update() {

	}

	@Override
	public void cleanUp() {
		LightMaster.removeLight(l);
	}

	@Override
	public void initAfterGen() {
		ChunkManager.setTorchLight(x, y, z, Chunk.MAXL);
		l = new Light(new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f), COLOR);
		LightMaster.addLight(l);
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

}
