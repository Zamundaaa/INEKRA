package dataAdvanced;

import java.util.ArrayDeque;

import org.joml.Vector3f;
import org.joml.Vector4i;

import audio.SourcesManager;
import data.Block;
import data.ChunkManager;
import mainInterface.CM;
import particles.PTM;
import particles.ParticleMaster;
import threadingStuff.ThreadManager;
import toolBox.Meth;
import toolBox.Vects;

public class SimpleConstructs {

	public static void fill(int sx, int sy, int sz, int ex, int ey, int ez, short ID) {
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				for (int z = sz; z <= ez; z++) {
					CM.setBlock(x, y, z, ID);
				}
			}
		}
	}

	public static void fill(int sx, int sy, int sz, int ex, int ey, int ez, short ID, boolean ersetzen) {
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				for (int z = sz; z <= ez; z++) {
					if (ersetzen || ChunkManager.getBlockID(x, y, z) == Block.AIR)
						CM.setBlock(x, y, z, ID);
				}
			}
		}
	}

	public static void fillSphere(int sx, int sy, int sz, int r, short ID) {
		int r2 = r * r;
		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					if (x * x + y * y + z * z <= r2) {
						CM.setBlock(x + sx, y + sy, z + sz, ID);
					}
				}
			}
		}
	}

	public static void fillSphere(int sx, int sy, int sz, int r, short ID, boolean ersetzen) {
		int r2 = r * r;
		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					if (x * x + y * y + z * z <= r2
							&& (ersetzen || ChunkManager.getBlockID(x + sx, y + sy, z + sz) == Block.AIR)) {
						CM.setBlock(x + sx, y + sy, z + sz, ID);
					}
				}
			}
		}
	}

	public static void buildTree(int x, int y, int z, int h) {
		fill(x, y, z, x, y + h, z, Block.WOOD);
		fillSphere(x, (int) (y + (h * 0.8f)), z, (int) (h / 3), Block.LEAVES, false);
	}

	public static void EXPLOSION(int x, int y, int z, int r) {
		SourcesManager.play(SourcesManager.boom, 100, new Vector3f(x, y, z));
		ChunkManager.dontDropItems();
		// ChunkManager.dropParticles = true;
		fillSphere(x, y, z, r, Block.AIR);
		ChunkManager.dropItems();
		// ChunkManager.dropParticles = false;
		if(Thread.currentThread().getName().equals("main"))
			for (int i = 0; i < 20; i++) {
				ParticleMaster.addNewParticle(PTM.fire, new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f),
						Vects.randomVector3f(-2 * r, 2 * r, -2 * r, 2 * r, -2 * r, 2 * r), 0, 0.3f, 0, 2);
			}
		
	}

	public static void EXPLOSION(Vector3f position, int r) {
		explosionQueue.add(Vects.getV4i((int) position.x, (int) position.y, (int) position.z, r));
		for (int i = 0; i < 20; i++) {
			ParticleMaster.addNewParticle(PTM.fire, new Vector3f(position.x + 0.5f, position.y + 0.5f, position.z + 0.5f),
					Vects.randomVector3f(-2 * r, 2 * r, -2 * r, 2 * r, -2 * r, 2 * r), 0, 0.3f, 0, 2);
		}
//		EXPLOSION((int) position.x, (int) position.y, (int) position.z, r);
	}
	
	public static void EXPLOSIONNOW(Vector3f position, int r){
		EXPLOSION((int) position.x, (int) position.y, (int) position.z, r);
		for (int i = 0; i < 20; i++) {
			ParticleMaster.addNewParticle(PTM.fire, new Vector3f(position.x + 0.5f, position.y + 0.5f, position.z + 0.5f),
					Vects.randomVector3f(-2 * r, 2 * r, -2 * r, 2 * r, -2 * r, 2 * r), 0, 0.3f, 0, 2);
		}
	}
	
	private static ArrayDeque<Vector4i> explosionQueue = new ArrayDeque<>();
	
	public static Thread explosionHelper;
	
	public static void init(){
		explosionHelper = new Thread("ExplosionHelper"){
			@Override
			public void run(){
				while(ThreadManager.running()){
					while(explosionQueue.isEmpty()){
						Meth.wartn(5);
					}
					Vector4i v = explosionQueue.pop();
					EXPLOSION(v.x, v.y, v.z, v.w);
				}
			}
		};
		explosionHelper.start();
	}

	public static void replaceAllAncients(Vector3f v, short repID, int CAP) {
		replaceAllAncients((int) v.x, (int) v.y, (int) v.z, repID, CAP);
	}

	public static void replaceAllAncients(int x, int y, int z, short repID, int CAP) {
		short id = ChunkManager.getBlockID(x, y, z);
		if (id != Block.AIR) {
			ChunkManager.dontDropItems();
			counter = 0;
			CM.setBlock(x, y, z, repID);
			replaceHelp(x, y, z, id, repID, CAP);
			ChunkManager.dropItems();
		}
	}

	private static int counter = 0;

	private static void replaceHelp(int x, int y, int z, short startID, short repID, int CAP) {
		if (counter > CAP) {
			return;
		}
		short b = ChunkManager.getBlockID(x + 1, y, z);
		if (b == startID) {
			CM.setBlock(x + 1, y, z, repID);
			counter++;
			replaceHelp(x + 1, y, z, startID, repID, CAP);
		}
		b = ChunkManager.getBlockID(x - 1, y, z);
		if (b == startID) {
			CM.setBlock(x - 1, y, z, repID);
			counter++;
			replaceHelp(x - 1, y, z, startID, repID, CAP);
		}

		b = ChunkManager.getBlockID(x, y, z + 1);
		if (b == startID) {
			CM.setBlock(x, y, z + 1, repID);
			counter++;
			replaceHelp(x, y, z + 1, startID, repID, CAP);
		}
		b = ChunkManager.getBlockID(x, y, z - 1);
		if (b == startID) {
			CM.setBlock(x, y, z - 1, repID);
			counter++;
			replaceHelp(x, y, z - 1, startID, repID, CAP);
		}

		b = ChunkManager.getBlockID(x, y + 1, z);
		if (b == startID) {
			CM.setBlock(x, y + 1, z, repID);
			counter++;
			replaceHelp(x, y + 1, z, startID, repID, CAP);
		}
		b = ChunkManager.getBlockID(x, y - 1, z);
		if (b == startID) {
			CM.setBlock(x, y - 1, z, repID);
			counter++;
			replaceHelp(x, y - 1, z, startID, repID, CAP);
		}
	}

	public static void fillSphere(float x, float y, float z, int r, short id, boolean replace) {
		fillSphere((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), r, id, replace);
	}

}
