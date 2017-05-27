package data;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import entities.Light;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.Meth;

public class Torch extends SpecialBlock {

	private static final Vector3f att = new Vector3f(1, 0.5f, 0.5f);
	private static final float flamesOffset = 0.035f, flameGravity = -0.03f, flameSize = 0.35f;

	private Light l;

	public Torch(int x, int y, int z) {
		super(x, y, z);
	}

	public void update() {
		if (Meth.doChance(10 * DisplayManager.getFrameTimeSeconds()))
			ParticleMaster.addNewParticle(PTM.fire,
					new Vector3f(x + 0.5f + Meth.randomFloat(-flamesOffset, flamesOffset),
							y + 0.75f + Meth.randomFloat(-flamesOffset, flamesOffset),
							z + 0.5f + Meth.randomFloat(-flamesOffset, flamesOffset)),
					new Vector3f(), flameGravity, 1, 0, flameSize);
	}

	@Override
	public void cleanUp() {
		LightMaster.removeLight(l);
	}

	@Override
	public void initAfterGen() {
		ChunkManager.setTorchLight(x, y, z, Chunk.MAXL);
		l = new Light(new Vector3f(x + 0.5f, y + 0.8f, z + 0.5f), new Vector3f(1, 0.8f, 0.8f), att);
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
	
	@Override
	public String toString(){
		return "Torch";
	}

}
