package data;

import static audio.SourcesManager.block;
import static data.Chunk.SIZE;

import java.util.*;

import org.joml.Vector3f;

import audio.AudioMaster;
import audio.SourcesManager;
import cubyWater.WaterUpdater;
import gameStuff.Err;
import gameStuff.WorldObjects;

public class ChunkManagerMulti {

	public static float range = 6;
	public static float yrange = 2;
	public static float genRad = 6;
	public static float genRadSq = genRad * genRad;
	private static Map<Key3D, Chunk> chunks = new HashMap<Key3D, Chunk>();
	private static ArrayDeque<Integer> lx = new ArrayDeque<>(), ly = new ArrayDeque<>(), lz = new ArrayDeque<>();
	private static int cx, cy, cz;
	private static ArrayList<Chunk> clist = new ArrayList<Chunk>();
	private static Key3D placeholder = new Key3D(0, 0, 0);
	private static Key3D placeholder_WaterUpdater = new Key3D(0, 0, 0);
//	private static Key3D placeholder_LightUpdater = new Key3D(0, 0, 0);

	public static boolean generate = true;
	private static boolean generateAllAtOnce = true;
	public static boolean dropItems = true;

	public static boolean unloadingAll = false;
	public static boolean dropParticles = true;

	public static void init() {
		cx = toChunkCoord(WorldObjects.player.getPosition().x);
		lx.add(cx);
		cy = toChunkCoord(WorldObjects.player.getPosition().y);
		ly.add(cy);
		cz = toChunkCoord(WorldObjects.player.getPosition().z);
		lz.add(cz);
		ChunkSaver.restoreStandardData();
		if (!generate) {
			int X = 165;
			int Z = 169;
			generateChunk(X, 0, Z);
			generateChunk(X + 1, 0, Z);
		} else {
			update();
			generateAllAtOnce = false;
		}
		LightMaster.init();
	}

	public static void update() {
		Vector3f pos = WorldObjects.player.getPosition();
		if (generateAllAtOnce) {
			for (float x = -range; x <= range; x++) {
				for (float z = -range; z <= range; z++) {
					for (float y = -yrange; y <= yrange; y++) {
						float div = (x * x + y * y + z * z) / (range * range);
						if (div < 1) {
							generateChunk((int) (toChunkCoord(pos.x) + x), (int) (toChunkCoord(pos.y) + y),
									(int) (toChunkCoord(pos.z) + z));
						}
					}
				}
			}
		} else if (generate) {
			int px = toChunkCoord(WorldObjects.player.getPosition().x);
			int py = toChunkCoord(WorldObjects.player.getPosition().y);
			int pz = toChunkCoord(WorldObjects.player.getPosition().z);
			if (px != cx || py != cy || pz != cz) {
				cx = px;
				cy = py;
				cz = pz;
				lx.clear();
				ly.clear();
				lz.clear();
				lx.add(cx);
				ly.add(cy);
				lz.add(cz);
			}
			if (lx.size() > 0) {
				int x = lx.poll();
				int y = ly.poll();
				int z = lz.poll();
				generateChunk(x, y, z);
				if (WorldObjects.player.getPosition().distanceSquared((x + 1.5f) * SIZE, (y + 0.5f) * SIZE,
						(z + 0.5f) * SIZE) <= genRadSq && getWithChunkCoords(x + 1, y, z) == null) {
					lx.add(x + 1);
					ly.add(y);
					lz.add(z);
				}
				if (WorldObjects.player.getPosition().distanceSquared((x - 0.5f) * SIZE, (y + 0.5f) * SIZE,
						(z + 0.5f) * SIZE) <= genRadSq && getWithChunkCoords(x - 1, y, z) == null) {
					lx.add(x - 1);
					ly.add(y);
					lz.add(z);
				}
				if (WorldObjects.player.getPosition().distanceSquared((x + 0.5f) * SIZE, (y + 1.5f) * SIZE,
						(z + 0.5f) * SIZE) <= genRadSq && getWithChunkCoords(x, y + 1, z) == null) {
					lx.add(x);
					ly.add(y + 1);
					lz.add(z);
				}
				if (WorldObjects.player.getPosition().distanceSquared((x + 0.5f) * SIZE, (y - 0.5f) * SIZE,
						(z + 0.5f) * SIZE) <= genRadSq && getWithChunkCoords(x, y - 1, z) == null) {
					lx.add(x);
					ly.add(y - 1);
					lz.add(z);
				}
				if (WorldObjects.player.getPosition().distanceSquared((x + 0.5f) * SIZE, (y + 0.5f) * SIZE,
						(z + 1.5f) * SIZE) <= genRadSq && getWithChunkCoords(x, y, z + 1) == null) {
					lx.add(x);
					ly.add(y);
					lz.add(z + 1);
				}
				if (WorldObjects.player.getPosition().distanceSquared((x + 0.5f) * SIZE, (y + 0.5f) * SIZE,
						(z - 0.5f) * SIZE) <= genRadSq && getWithChunkCoords(x, y, z - 1) == null) {
					lx.add(x);
					ly.add(y);
					lz.add(z - 1);
				}
			}
		}

		for (int i = 0; i < clist.size(); i++) {
			float X = clist.get(i).realX() - pos.x;
			float Y = clist.get(i).realY() - pos.y;
			float Z = clist.get(i).realZ() - pos.z;
			if (X * X + Y * Y + Z * Z > (range + 2) * (range + 2) * Chunk.SIZE * Chunk.SIZE) {
				if (clist.get(i).unloadCheck()) {
					unloadChunk(clist.get(i).cx(), clist.get(i).cy(), clist.get(i).cz());
				}
			} else {
				clist.get(i).update(true);// bottleneck!
			}
		}

		if (!WaterUpdater.MULTITHREADING) {
			WaterUpdater.update();
		}

	}

	public static void unloadChunk(int x, int y, int z) {
		placeholder.set(x, y, z);
		Chunk c = chunks.get(placeholder);
		if (c != null)
			c.unload();
		clist.remove(c);
		chunks.remove(placeholder);
	}

	public static Chunk getWithChunkCoords(int x, int y, int z) {
		Key3D key;
//		if (Thread.currentThread() == LightMaster.lightUpdater) {
//			key = placeholder_LightUpdater;
//		} else 
		if (Thread.currentThread() == WaterUpdater.updater) {
			key = placeholder_WaterUpdater;
		} else {
			key = placeholder;
		}
		key.set(x, y, z);
		return chunks.get(key);
	}

	public static Chunk getWithBlockCoords(float x, float y, float z) {
		int cx = toChunkCoord(x);// ...-32-(-17); // -16-(-1); 0-15:0; //
									// 16-31:1...
		int cy = toChunkCoord(y);// ...-32-(-17); // -16-(-1); 0-15:0; //
									// 16-31:1...
		int cz = toChunkCoord(z);// ...-32-(-17); // -16-(-1); 0-15:0; //
									// 16-31:1...
		return getWithChunkCoords(cx, cy, cz);
	}

	public static int toChunkCoord(float number) {
		return (int) Math.floor(number / (float) Chunk.SIZE);
	}

	private static boolean generateChunk(int x, int y, int z) {
		if (getWithChunkCoords(x, y, z) == null) {
			Chunk c = new Chunk(x, y, z);
			clist.add(c);
			chunks.put(new Key3D(x, y, z), c);
			Chunk t = getWithChunkCoords(x + 1, y, z);
			if (t != null) {
				t.scheduleMaskCreation();
			}
			t = getWithChunkCoords(x - 1, y, z);
			if (t != null) {
				t.scheduleMaskCreation();
			}
			t = getWithChunkCoords(x, y + 1, z);
			if (t != null) {
				t.scheduleMaskCreation();
			}
			t = getWithChunkCoords(x, y - 1, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.scheduleWaterUpdate();
			}
			t = getWithChunkCoords(x, y, z + 1);
			if (t != null) {
				t.scheduleMaskCreation();
			}
			t = getWithChunkCoords(x, y, z - 1);
			if (t != null) {
				t.scheduleMaskCreation();
			}
			return true;
		} else {
			return false;
		}
	}

	public static short getBlockID(int x, int y, int z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.get(x, y, z);
		}
		return 0;
	}

	public static short getBlockID(float x, float y, float z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.get((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));// Math.floor//ceil
		}
		return 0;
	}

	public static boolean setBlockID(int x, int y, int z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set(x, y, z, ID);
			return true;
		} else {
			return false;
		}
	}

	public static boolean setBlockID(float x, float y, float z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), ID);
			return true;
		} else {
			return false;
		}
	}

	public static boolean setBlockIDWithNoise(int x, int y, int z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set(x, y, z, ID);
			if (AudioMaster.soundEnabled)
				SourcesManager.play(block, 50, new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f));
			return true;
		} else {
			return false;
		}
	}

	public static boolean setBlockIDWithNoise(float x, float y, float z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), ID);
			if (AudioMaster.soundEnabled)
				SourcesManager.play(block, 50, new Vector3f(x, y, z));
			return true;
		} else {
			return false;
		}
	}

	public static short getBlockID(Vector3f position) {
		return getBlockID(position.x, position.y, position.z);
	}

	public static boolean deleteBlock(Vector3f position) {
		return deleteBlock(position.x, position.y, position.z);
	}

	public static boolean deleteBlock(float x, float y, float z) {
		return setBlockID(x, y, z, Block.AIR);
	}

	public static boolean deleteBlock(int x, int y, int z) {
		return setBlockID(x, y, z, Block.AIR);
	}

	public static void reloadMasks() {
		for (int i = 0; i < clist.size(); i++) {
			clist.get(i).genMask();
		}
	}

	public static void cleanUp() {
		unloadingAll = true;
		WaterUpdater.waitAndStop();
		while (clist.size() > 0) {
			Chunk c = clist.get(clist.size() - 1);
			unloadChunk(c.cx(), c.cy(), c.cz());
		}
		ChunkSaver.saveStandardData();
		Err.err.println("Unloaded Chunks!");
		unloadingAll = false;
	}

	public static void setBlockID(Vector3f vect, short id) {
		setBlockID(vect.x, vect.y, vect.z, id);
	}

	public static void setBlockIDCG(int x, int y, int z, Short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set(x, y, z, ID);
		} else {
			generateChunk(toChunkCoord(x), toChunkCoord(y), toChunkCoord(z));
			c = getWithBlockCoords(x, y, z);
			c.set(x, y, z, ID);
		}
	}

	public static short getBlockForBlocksOnly(float x, float y, float z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.get((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));// Math.floor//ceil
		}
		return Block.DUMMY;
	}

	public static short getBlockForBlocksOnly(int x, int y, int z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.get(x, y, z);// Math.floor//ceil
		}
		return Block.DUMMY;
	}

	public static void moveBlock(float x, float y, float z, float x2, float y2, float z2) {
		short id = getBlockID(x, y, z);
		if (id != Block.AIR) {
			deleteBlock(x, y, z);
			setBlockID(x, y, z, id);
		}
	}

	public static void setBlockIDWithNoise(Vector3f vect, short id) {
		setBlockIDWithNoise(vect.x, vect.y, vect.z, id);
	}

	public static void setBlockIDCGWithNoise(int x, int y, int z, Short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set(x, y, z, ID);
			if (AudioMaster.soundEnabled)
				SourcesManager.play(block, 50, new Vector3f(x, y, z));
		} else {
			generateChunk(toChunkCoord(x), toChunkCoord(y), toChunkCoord(z));
			c = getWithBlockCoords(x, y, z);
			c.set(x, y, z, ID);
			if (AudioMaster.soundEnabled)
				SourcesManager.play(block, 50, new Vector3f(x, y, z));
		}
	}

	public static boolean deleteBlockWithNoise(Vector3f position) {
		return deleteBlockWithNoise(position.x, position.y, position.z);
	}

	public static boolean deleteBlockWithNoise(float x, float y, float z) {
		return setBlockIDWithNoise(x, y, z, Block.AIR);
	}

	public static boolean deleteBlockWithNoise(int x, int y, int z) {
		return setBlockIDWithNoise(x, y, z, Block.AIR);
	}

	public static int getUppestBlockY(int x, int z) {
		int X = toChunkCoord(x);
		int Z = toChunkCoord(z);
		Chunk c = getWithChunkCoords(X, 100, Z);
		for (int y = 10; y > -10 && c == null; y--) {
			c = getWithChunkCoords(X, y, Z);
		}
		if (c != null) {
			for (int y = c.realY() + Chunk.SIZE - 1; y > c.realY() - 5 * Chunk.SIZE; y--) {
				if (ChunkManager.getBlockID(x, y, z) != Block.AIR) {
					return y;
				}
			}
		}
		return Integer.MIN_VALUE;
	}

	public static boolean deleteWater(float x, float y, float z) {
		return setWaterID(x, y, z, Block.AIR);
	}

	public static boolean setWaterID(float x, float y, float z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.setWater((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), ID);
			return true;
		} else {
			return false;
		}
	}

	public static void blockUpdate(int x, int y, int z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null)
			c.blockUpdate(x, y, z);

		c = getWithBlockCoords(x + 1, y, z);
		if (c != null)
			c.blockUpdate(x + 1, y, z);

		c = getWithBlockCoords(x - 1, y, z);
		if (c != null)
			c.blockUpdate(x - 1, y, z);

		c = getWithBlockCoords(x, y + 1, z);
		if (c != null)
			c.blockUpdate(x, y + 1, z);

		c = getWithBlockCoords(x, y - 1, z);
		if (c != null)
			c.blockUpdate(x, y - 1, z);

		c = getWithBlockCoords(x, y, z + 1);
		if (c != null)
			c.blockUpdate(x, y, z + 1);

		c = getWithBlockCoords(x, y, z - 1);
		if (c != null)
			c.blockUpdate(x, y, z - 1);
	}

	public static void deleteBlock(int x, int y, int z, boolean blockUpdate) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set(x, y, z, Block.AIR, blockUpdate);
		}
	}

	public static void setTorchLight(int x, int y, int z, int value) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.setTorchLight(x, y, z, value);
		}
	}

	public static int getTorchLight(int x, int y, int z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.getTorchLight(x, y, z);
		} else {
			return 0;
		}
	}

	public static int getTorchLight(Vector3f pos) {
		return getTorchLight((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
	}

	public static int getSunLight(Vector3f pos) {
		Chunk c = getWithBlockCoords(pos.x, pos.y, pos.z);
		if (c != null) {
			return c.getSunLight((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
		} else {
			return 0;
		}
	}

}
