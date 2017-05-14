package data;

import static audio.SourcesManager.block;

import java.util.*;

import org.joml.Vector3f;
import org.joml.Vector4i;

import audio.AudioMaster;
import audio.SourcesManager;
import cubyWater.WaterManager;
import cubyWater.WaterUpdater;
import gameStuff.Err;
import gameStuff.WorldObjects;
import renderStuff.DisplayManager;
import renderStuff.FramePerformanceLogger;
import toolBox.Meth;
import toolBox.Vects;

/**
 * This class contains and manages all the chunks. Has (pretty) fast several times overloaded methods for getting and setting blocks; for getting chunks as well
 * @author xaver
 */
/**
 * @author xaver
 *
 */
public class ChunkManager {

	/**
	 * pro Frame!
	 */
	public static int generationSpeed = 2;

	/**
	 * xz-range of pregen!
	 */
	public static final float range = 6;
	/**
	 * yrange of pregen!
	 */
	public static final float yrange = 2;
	/**
	 * contains all the Chunks for easy and fast access. see {@link Key3D}
	 */
	protected static Map<Key3D, Chunk> chunks = new HashMap<Key3D, Chunk>();

	/**
	 * contains all the Chunks for fast iteration/updating
	 */
	private static ArrayList<Chunk> clist = new ArrayList<Chunk>();

	/**
	 * a placeholder Key3D for fast access of the chunks map. May only be used
	 * in the main thread
	 */
	private static Key3D placeholder = new Key3D(0, 0, 0);

	/**
	 * a placeholder Key3D for fast access of the chunks map. May only be used
	 * in the WaterUpdater thread
	 */
	private static Key3D placeholder_WaterUpdater = new Key3D(0, 0, 0);
	private static Key3D placeholder2 = new Key3D(0, 0, 0);

//	/**
//	 * a placeholder Key3D for fast access of the chunks map. May only be used
//	 * in the LightUpdater thread
//	 */
//	private static Key3D placeholder_LightUpdater = new Key3D(0, 0, 0);

	// /**
	// * a placeholder Key3D for fast access of the chunks map. May only be used
	// * in the ListUpdater thread
	// */
	// private static Key3D placeholder_ListUpdater = new Key3D(0, 0, 0);

	/**
	 * if chunks shall be generated on the fly (determined on the player's
	 * position
	 */
	public static boolean generate = true;

	/**
	 * if this is true then all chunks near enough to the player will be loaded
	 * at once (every frame) instead of using a queue
	 */
	private static boolean generateAllAtOnce = true;

	/**
	 * when true a {@link inventory.Item3D} will be generated when a block is
	 * destroyed
	 */
	public static boolean dropItems = true;

	public static boolean unloadingAll = false;
	/**
	 * when true (and dropItems is not) a particle will be generated when a
	 * block is destroyed
	 */
	public static boolean dropParticles = true;

	/**
	 * generates the first chunks and sets up the loading queue. Also inits the
	 * {@link LightMaster}
	 */
	public static void init() {
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

	// /**
	// * a queue using {@link data.Key3D} to represent all the chunks that have
	// to
	// * be loaded yet (/have to be checked if they need to be loaded)
	// */
	// private static ArrayDeque<Key3D> toLoad = ChunkLoader.queue;
	// private static int cx, cy, cz;
	public static float genRad = 10, genRadSq = genRad * genRad, genDistY = 5, genDistYSq = genDistY * genDistY;
	// private static volatile boolean rebuildList = true;
	private static ArrayDeque<Chunk> toAdd = new ArrayDeque<Chunk>();

	// /**
	// * updates the chunk generation queue every time a player crosses a chunk
	// * boundary. As this takes some time (10-50 ms) it has to be done in this
	// * extra Thread to remove lags
	// */
	// private static Thread listUpdater;
	private static ChunkLoader chunkLoader;

	/**
	 * generates the chunks using the toLoad - queue, or, if generateAllAtOnce
	 * is true, all at once (obviously...). Then it updates all the chunks that
	 * are still near enough and unloads those too far away
	 */
	public static void update() {
		FramePerformanceLogger.stopTime();
		Vector3f pos = WorldObjects.player.getPosition();
		int px = toChunkCoord(WorldObjects.player.getPosition().x);
		int py = toChunkCoord(WorldObjects.player.getPosition().y);
		int pz = toChunkCoord(WorldObjects.player.getPosition().z);
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
			// int gen = 0;
			// int genCap = generationSpeed;
			// while (toLoad.size() > 0 && gen < genCap) {
			// Key3D ka = toLoad.poll();
			// if (generateChunk(ka.getX(), ka.getY(), ka.getZ()))
			// gen++;
			// }
			if (chunkLoader == null || !chunkLoader.isAlive()) {
				chunkLoader = new ChunkLoader(toAdd);
				chunkLoader.start();
			}
			// for(int i = 0; i < 5 && !toLoad.isEmpty(); i++){
			// ChunkLoader.queue.add(toLoad.poll());
			// }
			int i = 0;
			while (!toAdd.isEmpty() && i < 5) {
				Chunk c = toAdd.poll();
				c.scheduleMaskCreation();
				addChunk(c);
				i++;
			}

		}
		int i = 0;
		for (int c = 0; c < clist.size(); c++) {
			float X = clist.get(c).cx() - px;
			float Y = clist.get(c).cy() - py;
			float Z = clist.get(c).cz() - pz;
			if (X * X + Y * Y + Z * Z > (genRad + 2) * (genRad + 2)) {
				if (clist.get(c).unloadCheck()) {
					unloadChunk(clist.get(c).cx(), clist.get(c).cy(), clist.get(c).cz());
				}
			} else {
				boolean b;
				if (i < numberOfChunksToUpdatePerFrame && c == pointer) {
					i++;
					pointer++;
					if (pointer > clist.size() - 2)
						pointer = 0;
					b = true;
				} else {
					b = false;
				}
				clist.get(c).update(b);
			}
		}
		
		FramePerformanceLogger.writeStoppedTime("ChunkManager Update Without BlockUpdates");
		
		blockUpdates(1 + blockUpdates.size() / 2);
		
		FramePerformanceLogger.writeStoppedTime("BlockUpdates...");

	}

	private static int numberOfChunksToUpdatePerFrame = 100;
	private static int pointer = 0;

	/**
	 * unloads the chunk using {@link Chunk#unload} and removes it from clist
	 * and from chunks
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private static void unloadChunk(int x, int y, int z) {
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
		if (Thread.currentThread() == WaterUpdater.updater ) {
			key = placeholder_WaterUpdater;
			// } else if (Thread.currentThread() == listUpdater) {
			// key = placeholder_ListUpdater;
		}else if(!Thread.currentThread().getName().equals("main")){
			key = placeholder2;
		} else {
			key = placeholder;
		}
		key.set(x, y, z);
		return chunks.get(key);
	}

	public static Chunk getWithChunkCoords(Key3D k) {
		return chunks.get(k);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return basically calls {@link ChunkManager#toChunkCoord(float)} for each
	 *         parameter and then
	 *         {@link ChunkManager#getWithChunkCoords(int, int, int)}
	 */
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

	/**
	 * generates, if not already present, the chunk at chunk coordinates x, y
	 * and z and updates all of the 6 adjacent chunks (if existent)
	 * 
	 * @return if the chunk already existed or not
	 */
	private static boolean generateChunk(int x, int y, int z) {
		if (getWithChunkCoords(x, y, z) == null) {
			Chunk c = new Chunk(x, y, z);
			clist.add(c);
			chunks.put(new Key3D(x, y, z), c);
			Chunk t = getWithChunkCoords(x + 1, y, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.XM);
			}
			t = getWithChunkCoords(x - 1, y, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.XP);
			}
			t = getWithChunkCoords(x, y + 1, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.DOWN);
			}
			t = getWithChunkCoords(x, y - 1, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.scheduleWaterUpdate();
				t.updateLightAtSide(Block.UP);
			}
			t = getWithChunkCoords(x, y, z + 1);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.ZM);
			}
			t = getWithChunkCoords(x, y, z - 1);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.ZP);
			}
			return true;
		} else {
			return false;
		}
	}

	private static void addChunk(Chunk c) {
		int x = c.cx(), y = c.cy(), z = c.cz();
		if (getWithChunkCoords(x, y, z) == null) {
			clist.add(c);
			chunks.put(new Key3D(x, y, z), c);
			Chunk t = getWithChunkCoords(x + 1, y, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.XM);
			}
			t = getWithChunkCoords(x - 1, y, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.XP);
			}
			t = getWithChunkCoords(x, y + 1, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.DOWN);
			}
			t = getWithChunkCoords(x, y - 1, z);
			if (t != null) {
				t.scheduleMaskCreation();
				t.scheduleWaterUpdate();
				t.updateLightAtSide(Block.UP);
			}
			t = getWithChunkCoords(x, y, z + 1);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.ZM);
			}
			t = getWithChunkCoords(x, y, z - 1);
			if (t != null) {
				t.scheduleMaskCreation();
				t.updateLightAtSide(Block.ZP);
			}
		} else {
			c.cleanUp();
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

	/**
	 * sets the block, if the requested chunk exists, and plays a fitting sound.
	 * Only if AudioMaster.soundEnabled is true, of course
	 */
	public static boolean setBlockIDWithNoise(int x, int y, int z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			short b = c.set(x, y, z, ID);
			if (AudioMaster.soundEnabled)
				Block.playBreakSound(b, x, y, z);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * sets the block, if the requested chunk exists, and plays a fitting sound.
	 * Only if AudioMaster.soundEnabled is true, of course
	 */
	public static boolean setBlockIDWithNoise(float x, float y, float z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			short b = c.set((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), ID);
			if (AudioMaster.soundEnabled)
				Block.playBreakSound(b, x, y, z);
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

	/**
	 * reloads all the loaded chunks maps. Performance-wise probably not good
	 */
	public static void reloadMasks() {
		for (int i = 0; i < clist.size(); i++) {
			clist.get(i).genMask();
		}
	}

	/**
	 * unloads all the chunks and saves world-dependent data. Also clears both
	 * the WaterUpdater's and the WaterRenderer's Lists
	 */
	public static void cleanUp() {
		unloadingAll = true;
		WaterUpdater.waitAndStop();
		WaterUpdater.clearList();
		WaterManager.cleanUp();
		while (clist.size() > 0) {
			Chunk c = clist.get(clist.size() - 1);
			unloadChunk(c.cx(), c.cy(), c.cz());
		}
		ChunkSaver.saveStandardData();
		Err.err.println("Unloaded Chunks!");
		// listUpdater = null;
		unloadingAll = false;
	}

	public static void setBlockID(Vector3f vect, short id) {
		setBlockID(vect.x, vect.y, vect.z, id);
	}

	/**
	 * sets the blockID at x, y, and z and generates the needed chunk if
	 * nonexistent
	 */
	public static void setBlockIDCG(int x, int y, int z, short ID) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			c.set(x, y, z, ID);
		} else {
			generateChunk(toChunkCoord(x), toChunkCoord(y), toChunkCoord(z));
			c = getWithBlockCoords(x, y, z);
			c.set(x, y, z, ID);
		}
	}

	/**
	 * @return the block at the specified coordinates, or if the requested chunk
	 *         doesn't exist yet, {@link Block#DUMMY}
	 */
	public static short getBlockForBlocksOnly(float x, float y, float z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.get((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));// Math.floor//ceil
		}
		return Block.DUMMY;
	}

	/**
	 * @return the block at the specified coordinates, or if the requested chunk
	 *         doesn't exist yet, {@link Block#DUMMY}
	 */
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

	/**
	 * @see ChunkManager#setBlockIDWithNoise(float, float, float, short)
	 */
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

	/**
	 * essentially calls {@link Block#blockUpdate(Chunk, int, int, int, short)}
	 * on the block and its neighbours
	 */
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

	public static int getSunLight(int x, int y, int z) {
		Chunk c = getWithBlockCoords(x, y, z);
		if (c != null) {
			return c.getSunLight(x, y, z);
		} else {
			return 0;
		}
	}

	private static ArrayDeque<Vector4i> blockUpdates = new ArrayDeque<>();
	public static int chanceToDo = 0;

	public static void scheduleBlockUpdate(int x, int y, int z) {
		blockUpdates.add(Vects.getV4i(x, y, z, -chanceToDo));
	}

	private static void blockUpdates(int count) {
		for (int i = 0; i < count && i < blockUpdates.size(); i++) {
			Vector4i v = blockUpdates.pop();
			if (v.w < 0) {
				if (Meth.doChance(-v.w * DisplayManager.getFrameTimeSeconds())) {
					blockUpdate(v.x, v.y, v.z);
					Vects.addV4i(v);
				} else {
					blockUpdates.add(v);
				}
			} else {
				blockUpdate(v.x, v.y, v.z);
				Vects.addV4i(v);
			}
		}
	}

	public static ArrayList<Chunk> getLoadedChunkList() {
		return clist;
	}
	
//	public static int getHighestNonEmptyChunk(float x, float y, float z){
//		return 0;
//	}

}
