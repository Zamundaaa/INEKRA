package entities;

import java.util.ArrayList;

import org.joml.Vector3f;

import audio.AudioMaster;
import audio.SourcesManager;
import data.ChunkManager;
import gameStuff.*;
import line.Line;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class LASER implements TickingThing {

	private static final int BLASTERSOUND = AudioMaster.loadSound("audio/Laser_Blaster-SoundBible.ogg");
	private static final float BLASTERVOLUME = 0.2f;
	public static final boolean DESTROYS = true;
	public static boolean hitsthings = false;

	private float x, y, z, xspeed, yspeed, zspeed, r = Meth.randomFloat(0, 1), g = Meth.randomFloat(0, 1),
			b = Meth.randomFloat(0, 1);
	private double creationTime = TM.inGameDays();
	private Line line;
	private boolean dest = DESTROYS;

	public LASER(float x, float y, float z, float xspeed, float yspeed, float zspeed, float r, float g, float b) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xspeed = xspeed;
		this.yspeed = yspeed;
		this.zspeed = zspeed;
		this.r = r;
		this.g = g;
		this.b = b;
		line = new Line(x, y, z, x + xspeed, y + yspeed, z + zspeed, r, g, b);
		TickManager.addTickingThing(this);
		SourcesManager.play(BLASTERSOUND, BLASTERVOLUME, new Vector3f(x, y, z));
	}

	public LASER(float x, float y, float z, float xspeed, float yspeed, float zspeed) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xspeed = xspeed;
		this.yspeed = yspeed;
		this.zspeed = zspeed;
		line = new Line(x, y, z, x - xspeed * 0.1f, y - yspeed * 0.1f, z - zspeed * 0.1f, r, g, b);
		TickManager.addTickingThing(this);
		SourcesManager.play(BLASTERSOUND, BLASTERVOLUME, new Vector3f(x, y, z));
	}

	public void cleanUp() {
		TickManager.removeTickingThing(this);
		line.cleanUp();
	}

	public void setDestroying(boolean b) {
		dest = b;
	}

	@Override
	public boolean update() {
		if (TM.inGameDays() > creationTime + 0.5f) {
			cleanUp();
		}
		float delta = DisplayManager.getFrameTimeSeconds();
		x += xspeed * delta;
		y += yspeed * delta;
		z += zspeed * delta;
		// if(DESTROYS){
		short b = ChunkManager.getBlockID(x, y, z);
		if (b != 0) {
			// ChunkManager.setBlock(Meth.toInt(x), Meth.toInt(y),
			// Meth.toInt(z), null);
			if (dest) {
				// ChunkManager.deleteBlockWithDrops(x, y, z);
				// ChunkManager.BlockUpdate((int)x, (int)y-1, (int)z);
				// ChunkManager.LightUpdate((int)x, (int)y-1, (int)z);
				ChunkManager.deleteBlock(x, y, z);
			}
			ParticleMaster.addNewParticle(PTM.fire, new Vector3f(x, y, z), Vects.NULL, 0, 0.5f, 0,
					Meth.randomFloat(0.5f, 1));
			cleanUp();
		} else {
			b = ChunkManager.getBlockID(x + xspeed * delta * 0.5f, y + yspeed * delta * 0.5f,
					z + zspeed * delta * 0.5f);
			if (b != 0) {
				x += xspeed * delta * 0.5f;
				y += yspeed * delta * 0.5f;
				z += zspeed * delta * 0.5f;
				if (dest) {
					// ChunkManager.deleteBlockWithDrops(x, y, z);
					// ChunkManager.BlockUpdate((int)x, (int)y-1, (int)z);
					// ChunkManager.LightUpdate((int)x, (int)y-1, (int)z);
					ChunkManager.deleteBlock(x, y, z);
				}
				ParticleMaster.addNewParticle(PTM.fire, new Vector3f(x, y, z), Vects.NULL, 0, 0.5f, 0,
						Meth.randomFloat(0.5f, 1));
				cleanUp();
			}
		}
		// }
		if (hitsthings) {
			ArrayList<HittableThing> hs = WorldObjects.getHits();
			Vector3f pos = Vects.calcVect;
			Vects.setCalcVect(x, y, z);
			for (int i = 0; i < hs.size(); i++) {
				// float dx = x - hs.get(i).getPosition().x;
				// float dy = y - hs.get(i).getPosition().y;
				// float dz = z - hs.get(i).getPosition().z;

				// float d = dx*dx+dy*dy+dz*dz;
				// if(d <= 0.25f){
				// hs.get(i).hit(3);
				// cleanUp();
				// return true;
				// TickManager.removeTickingThing(this);
				// }

				if (hs.get(i).inHitbox(pos)) {
					hs.get(i).hit(3);
					cleanUp();
					return true;
				}

			}
		}
		// else{
		// ParticleMaster.addNewParticle(PTM.cosmic, new Vector3f(x, y, z),
		// Vects.NULL, 0, 1 , 0, 1);
		// }

		line.set1(x, y, z);
		line.set2(x - xspeed * 0.1f, y - yspeed * 0.1f, z - zspeed * 0.1f);

		return false;
	}

}
