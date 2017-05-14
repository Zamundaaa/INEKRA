package dataAdvanced;

import org.joml.Vector3f;

import audio.SourcesManager;
import data.Block;
import data.ChunkManager;
import particles.PTM;
import particles.ParticleMaster;
import toolBox.Vects;

public class SimpleConstructs {

	public static void fill(int sx, int sy, int sz, int ex, int ey, int ez, short ID) {
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				for (int z = sz; z <= ez; z++) {
					ChunkManager.setBlockID(x, y, z, ID);
				}
			}
		}
	}

	public static void fill(int sx, int sy, int sz, int ex, int ey, int ez, short ID, boolean ersetzen) {
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				for (int z = sz; z <= ez; z++) {
					if (ersetzen || ChunkManager.getBlockID(x, y, z) == Block.AIR)
						ChunkManager.setBlockID(x, y, z, ID);
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
						ChunkManager.setBlockID(x + sx, y + sy, z + sz, ID);
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
						ChunkManager.setBlockID(x + sx, y + sy, z + sz, ID);
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
		ChunkManager.dropItems = false;
		// ChunkManager.dropParticles = true;
		fillSphere(x, y, z, r, Block.AIR);
		ChunkManager.dropItems = true;
		// ChunkManager.dropParticles = false;
		for (int i = 0; i < 20; i++) {
			ParticleMaster.addNewParticle(PTM.fire, new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f),
					Vects.randomVector3f(-2 * r, 2 * r, -2 * r, 2 * r, -2 * r, 2 * r), 0, 0.3f, 0, 2);
		}
	}

	public static void EXPLOSION(Vector3f position, int r) {
		EXPLOSION((int) position.x, (int) position.y, (int) position.z, r);
	}

	public static void replaceAllAncients(Vector3f v, short repID, int CAP) {
		replaceAllAncients((int) v.x, (int) v.y, (int) v.z, repID, CAP);
	}

	public static void replaceAllAncients(int x, int y, int z, short repID, int CAP) {
		short id = ChunkManager.getBlockID(x, y, z);
		if (id != Block.AIR) {
			ChunkManager.dropItems = false;
			counter = 0;
			ChunkManager.setBlockID(x, y, z, repID);
			replaceHelp(x, y, z, id, repID, CAP);
			ChunkManager.dropItems = true;
		}
	}

	private static int counter = 0;

	private static void replaceHelp(int x, int y, int z, short startID, short repID, int CAP) {
		if (counter > CAP) {
			return;
		}
		short b = ChunkManager.getBlockID(x + 1, y, z);
		if (b == startID) {
			ChunkManager.setBlockID(x + 1, y, z, repID);
			counter++;
			replaceHelp(x + 1, y, z, startID, repID, CAP);
		}
		b = ChunkManager.getBlockID(x - 1, y, z);
		if (b == startID) {
			ChunkManager.setBlockID(x - 1, y, z, repID);
			counter++;
			replaceHelp(x - 1, y, z, startID, repID, CAP);
		}

		b = ChunkManager.getBlockID(x, y, z + 1);
		if (b == startID) {
			ChunkManager.setBlockID(x, y, z + 1, repID);
			counter++;
			replaceHelp(x, y, z + 1, startID, repID, CAP);
		}
		b = ChunkManager.getBlockID(x, y, z - 1);
		if (b == startID) {
			ChunkManager.setBlockID(x, y, z - 1, repID);
			counter++;
			replaceHelp(x, y, z - 1, startID, repID, CAP);
		}

		b = ChunkManager.getBlockID(x, y + 1, z);
		if (b == startID) {
			ChunkManager.setBlockID(x, y + 1, z, repID);
			counter++;
			replaceHelp(x, y + 1, z, startID, repID, CAP);
		}
		b = ChunkManager.getBlockID(x, y - 1, z);
		if (b == startID) {
			ChunkManager.setBlockID(x, y - 1, z, repID);
			counter++;
			replaceHelp(x, y - 1, z, startID, repID, CAP);
		}
	}

}
