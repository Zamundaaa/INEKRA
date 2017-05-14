package data;

import java.util.*;

import org.joml.Vector3f;

import blockRendering.BlockRenderer;
import blockRendering.ChunkEntity;
import collectionsStuff.*;
import cubyWater.Water;
import cubyWaterNew.NewWaterUpdater;
import gameStuff.Err;
import gameStuff.MainLoop;
import inventory.Item3D;
import objConverter.ModelData;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.*;
import toolBox.Meth;

/**
 * represents a certain chunk of space, contains all the blocks of that area and
 * pretty much all additional data of that area
 * 
 * @author xaver
 *
 */
public class Chunk {// OPT: genMask Vector3fs to Floats!

	public static final float AMBIENT = 0.1f;
	public static final int MIN_GRASS = 7, MAX_GRASS = 20;
	public static final float GRASSDIVERGENCE = 0.49f;

	public static final int SIZE = 16;
	public static final int LIGHTR = SIZE;
	public static final int MAXL = LIGHTR - 1;
	public static final float INVLIGHTR = 1.0f / LIGHTR;
	public static final float extraOffset = 0.001f;
	public static boolean particleCheckGeneration = true;
	public static final float DISPLAYOFFSET = 0.5f;
	public static final boolean staticWater = false;

	public static boolean useLightValuesForGreedyMesh = true, smoothLighting = false;// implementation
																						// too...
																						// lazy
																						// for
																						// that!

	// ALS NÄCHSTES WIEDER BÄUME, AUCH MAL HÖHLEN, ETC. ( Häuser durch Skript
	// vllt? versch.Modelle? )

	private static final ArrayList<Water> buffer = new ArrayList<Water>();
	public static final int MINTREEHEIGHT = -9, MAXTREEHEIGHT = 80;
	public static boolean genWater = true, SAVE = true, LOAD = true;

	private final int x, y, z, realX, realY, realZ;
	private short[][][] blocks;
	private static final short[][][] copy = new short[SIZE][SIZE][SIZE], lightCopy = new short[SIZE][SIZE][SIZE];

	private ArrayList<SpecialBlock> specials = new ArrayList<SpecialBlock>();
	private Map<Integer, Water> waters;

	private ChunkEntity e;
	private boolean mask = true;// ob beim nächsten Update genMask() neu
								// aufgerufen werden muss
	// private boolean afterGen;// ob noch nachgeneriert werden muss

	// private Quad borders;

	public Chunk(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		realX = this.x * SIZE;
		realY = this.y * SIZE;
		realZ = this.z * SIZE;
		blocks = new short[SIZE][SIZE][SIZE];
		light = new short[SIZE][SIZE][SIZE];
		genBlocks();
		genMask();
	}

	public Chunk(int x, int y, int z, boolean genMask) {
		this.x = x;
		this.y = y;
		this.z = z;
		realX = this.x * SIZE;
		realY = this.y * SIZE;
		realZ = this.z * SIZE;
		blocks = new short[SIZE][SIZE][SIZE];
		light = new short[SIZE][SIZE][SIZE];
		genBlocks();
		if (genMask)
			genMask();
	}

	public Chunk() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		realX = this.x * SIZE;
		realY = this.y * SIZE;
		realZ = this.z * SIZE;
		blocks = new short[SIZE][SIZE][SIZE];
		light = new short[SIZE][SIZE][SIZE];
		fake = true;
	}

	private boolean fake = false;
	private boolean uw = true;

	public boolean waterChanged() {
		return uw;
	}

	public void update(boolean updateSomeBlocks) {
		// TODO: GROWTH Sapling (+later plant) growth rate !!! change to :
		// questioned constant OR: !extra update method!
		for (int i = 0; i < 5; i++) {
			updateBlock(Meth.randomInt(0, SIZE - 1) + realX(), Meth.randomInt(0, SIZE - 1) + realY(),
					Meth.randomInt(0, SIZE - 1) + realZ());
		}
		if (uw && Meth.doChance(100 * DisplayManager.getFrameTimeSeconds())) {
			updateWaters();
			uw = false;
		}

		if (!specialsInited) {
			for (int i = 0; i < specials.size(); i++) {
				specials.get(i).initAfterGen();
			}
			LightMaster.loadSunLight(this);
			specialsInited = true;
		}
		for (int i = 0; i < specials.size(); i++) {
			specials.get(i).update();
		}
		if (mask) {
			mask = false;
			genMask();
		}
		unloadStart = 0;

		if (needsSaving && Meth.systemTime() > lastSave + 30000
				&& Meth.doChance(0.1f * DisplayManager.getFrameTimeSeconds())) {
			needsSaving = false;
			ChunkSaver.saveChunk(this);
			lastSave = Meth.systemTime();
		}
		// if(((z < 1 && z > -5)) &&borders == null)
		// borders = new Quad(realX, realY, realZ, realX+SIZE, realY+SIZE,
		// realZ+SIZE);
	}

	private long lastSave = Meth.systemTime();

	private void updateBlock(int x, int y, int z) {
		short id = get(x, y, z);
		if (id < 0) {
			for (int i = 0; i < specials.size(); i++) {
				if (specials.get(i).is(x, y, z)) {
					specials.get(i).update();
					break;
				}
			}
		} else if (id > 0) {
			Block.update(this, id, x, y, z);
		}
	}

	public void updateWaters() {
		if (NewWaterUpdater.useWaterMesh)
			return;
		FramePerformanceLogger.stopTime();
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				for (int z = 0; z < SIZE; z++) {
					int wi = waterIndex(x, y, z);
					if (wi != -1) {
						if (Block.isWater(blocks[x][y][z])) {
							waters.get(wi).setHeight(0.01f * (blocks[x][y][z] - 1000));
						} else {
							if (buffer.size() < 200) {
								Water w = waters.remove(wi);
								// w.hide();
								w.cleanUp();
								buffer.add(w);
							} else {
								Water w = waters.remove(wi);
								w.cleanUp();
							}
						}
					} else if (Block.isWater(blocks[x][y][z])) {
						Water w;
						if (buffer.isEmpty()) {
							w = new Water(new Vector3f(x + realX, y + realY, z + realZ),
									(blocks[x][y][z] - 1000) * 0.01f);
							w.setUp();
						} else {
							w = buffer.get(buffer.size() - 1);
							buffer.remove(buffer.size() - 1);
							w.setPosition(x + realX, y + realY, z + realZ);
							w.setHeight((blocks[x][y][z] - 1000) * 0.01f);
							// w.show();
							w.setUp();
						}
						add(x, y, z, w);
					}
				}
			}
		}
		if (waters != null) {
			for (int i : waters.keySet()) {
				waters.get(i).update();
			}
		}
		FramePerformanceLogger.writeStoppedTime("Chunk.updateWaters()");
	}

	private int waterIndex(int x, int y, int z) {
		if (waters == null) {
			return -1;
		}
		int i = x + 100 * y + 10000 * z;
		if (waters.get(i) == null) {
			return -1;
		} else {
			return i;
		}
	}

	private void add(int x, int y, int z, Water w) {
		if (waters == null) {
			waters = new HashMap<Integer, Water>();
		}
		waters.put(x + y * 100 + z * 10000, w);
		// waters.put(k, w);
	}

	/**
	 * unloads the chunk model and saves the data of this chunk, if something's
	 * changed since creation. Also removes the contained waters, if
	 * {@link gameStuff.MainLoop#running} is false (for performance reasons)
	 */
	public void unload() {
		if (e != null) {
			BlockRenderer.entities.remove(e);
			e.unload();
		}
		if (SAVE && needsSaving)
			ChunkSaver.saveChunk(this);

		if (waters != null && MainLoop.running) {
			for (int i : waters.keySet()) {
				waters.get(i).cleanUp();
			}
		}

		unloaded = true;
	}

	public void cleanUp() {
		if (e != null) {
			BlockRenderer.entities.remove(e);
			Loader.unload(e.getModel().getRawMod());
		}
		if (waters != null && MainLoop.running) {
			for (int i : waters.keySet()) {
				waters.get(i).cleanUp();
			}
		}
		unloaded = true;
		// if(borders != null)
		// borders.cleanUp();
	}

	public boolean unloaded = false;
	private boolean needsSaving = false;

	private void genBlocks() {
		if (!LOAD) {
			randomGen();
		} else {
			byte[] data = ChunkSaver.load(this);
			if (data != null) {
				SmartByteBuffer d = new SmartByteBuffer(data);
				int i, currY;
				byte count;
				short id;
				for (int x = 0; x < SIZE; x++) {
					for (int z = 0; z < SIZE; z++) {
						currY = 0;
						while (currY < SIZE) {
							// count = data[counter++];
							// lo = data[counter++];
							// hi = data[counter++];
							// id = bytesToShort(hi, lo);
							count = d.read();
							id = d.readShort();
							// try{
							for (i = 0; i < count; i++) {
								blocks[x][currY][z] = id;
								if (id < 0) {
									specials.add(SpecialBlock.getInstance(id, realX + x, realY + currY, realZ + z));

									// int pos = d.position();
									specials.get(specials.size() - 1).applyMetaData(d);
									// if(i > 0)
									// d.setPosition(pos);
								}
								currY++;
							}
							// }catch(Exception e){
							// Err.err.println(count + " x " + id);
							// e.printStackTrace(Err.err);
							// System.exit(-1);
							// }
						}
					}
				}
			} else {
				randomGen();
			}
		}
	}

	private void randomGen() {
		Generator g = Generator.getG();
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				int i = (int) g.generateHeight(x + realX, z + realZ);
				for (int y = 0; y < SIZE; y++) {
					if (y + realY < i) {
						if (y + realY == i - 1) {
							if (!genWater || y + realY > Meth.waterHeight) {
								blocks[x][y][z] = Block.jahresZeitGras();
							} else if (y + realY == Meth.waterHeight) {
								blocks[x][y][z] = g.genThing((x + realX) * 10, (z + realZ) * 10, 53269) > -0.3f
										? Block.SAND : Block.GRAVEL;
							} else {
								blocks[x][y][z] = g.genThing((x + realX) * 10, (z + realZ) * 10, 53269) > 0f
										? Block.SAND : Block.GRAVEL;
							}
						} else if (y + realY >= i - 4) {
							blocks[x][y][z] = Block.DIRT;
						} else {
							blocks[x][y][z] = Block.STONE;
						}
					} else if (genWater && y + realY <= Meth.waterHeight) {
						blocks[x][y][z] = Block.max_water;
					} else if (y + realY == i) {
						if (g.genThing((x + realX) * 100, (z + realZ) * 100, 3163) > 0.43f) {
							blocks[x][y][z] = Block.SAPLING;
						}
					}
				}
			}
		}
	}

	private int upcount, downcount, xpcount, xmcount, zpcount, zmcount;
	private boolean restMaskNeeded = false, specialsInited = false;

	private static final ArrayListF verts = new ArrayListF(), texes = new ArrayListF(), lightValues = new ArrayListF(),
			norms = new ArrayListF();
	private static final ArrayListI indices = new ArrayListI();

	public ChunkEntity genMask() {
		verts.clear();
		indices.clear();
		texes.clear();
		lightValues.clear();
		norms.clear();

		restMaskNeeded = false;
		yMask(verts, indices, texes, lightValues, true);
		yMask(verts, indices, texes, lightValues, false);

		xMask(verts, indices, texes, lightValues, true);
		xMask(verts, indices, texes, lightValues, false);

		zMask(verts, indices, texes, lightValues, true);
		zMask(verts, indices, texes, lightValues, false);

		if (restMaskNeeded) {
			restMask(verts, indices, texes, norms, lightValues);
		}
		if (verts.size() > 0) {

			float[] vertices = new float[verts.size()];
			float[] texcoords = new float[verts.size()];
			for (int i = 0; i < verts.size(); i += 3) {
				vertices[i] = verts.get(i);
				vertices[i + 1] = verts.get(i + 1);
				vertices[i + 2] = verts.get(i + 2);
				texcoords[i] = texes.get(i);
				texcoords[i + 1] = texes.get(i + 1);
				texcoords[i + 2] = texes.get(i + 2);
			}
			float[] normals = new float[verts.size()];
			int I = 0;
			for (int i = 0; i < upcount; i++) {
				normals[I++] = 0;
				normals[I++] = 1;
				normals[I++] = 0;
			}
			for (int i = 0; i < downcount; i++) {
				normals[I++] = 0;
				normals[I++] = -1;
				normals[I++] = 0;
			}
			for (int i = 0; i < xpcount; i++) {
				normals[I++] = 1;
				normals[I++] = 0;
				normals[I++] = 0;
			}
			for (int i = 0; i < xmcount; i++) {
				normals[I++] = -1;
				normals[I++] = 0;
				normals[I++] = 0;
			}
			for (int i = 0; i < zpcount; i++) {
				normals[I++] = 0;
				normals[I++] = 0;
				normals[I++] = 1;
			}
			for (int i = 0; i < zmcount; i++) {
				normals[I++] = 0;
				normals[I++] = 0;
				normals[I++] = -1;
			}
			for (int i = 0; i < norms.size(); i++) {
				normals[I++] = norms.get(i);
			}
			int[] indis = new int[indices.size()];
			for (int i = 0; i < indices.size(); i++) {
				indis[i] = indices.get(i);
			}
			float[] lights = new float[lightValues.size()];
			for (int i = 0; i < lightValues.size(); i++) {
				lights[i] = lightValues.get(i);
			}
			if (e == null) {
				e = new ChunkEntity(vertices, texcoords, normals, indis, lights, realX + DISPLAYOFFSET,
						realY + DISPLAYOFFSET, realZ + DISPLAYOFFSET);
				BlockRenderer.entities.add(e);
			} else {
				// Loader.updateVAO3DTex(e.getModel().getRawMod(), vertices,
				// texcoords, normals, indis);
				e.updateModel(vertices, texcoords, normals, indis, lights);
			}
		} else {
			if (e != null) {
				e.unload();
			}
			BlockRenderer.entities.remove(e);
			e = null;
		}
		return e;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * @comment use with caution!
	 */
	public short getWithWithinChunkCoords(int x, int y, int z) {
		return blocks[x][y][z];
	}

	public short get(int x, int y, int z) {
		try {
			return blocks[x - realX][y - realY][z - realZ];
		} catch (ArrayIndexOutOfBoundsException a) {
			a.printStackTrace(Err.err);
			Err.err.println("X: " + x + " Y: " + y + " Z: " + z + " this.X: " + this.x + " this.Y: " + this.y
					+ " this.Z: " + this.z);
			System.exit(-1);
			return 0;
		}
	}

	private static final float ddd = 1, particleTime = 0.5f;

	public short set(int x, int y, int z, short ID) {
		int X = x - realX;
		int Y = y - realY;
		int Z = z - realZ;
		short old = blocks[X][Y][Z];
		if (old != ID) {
			if (old < 0) {
				for (int i = 0; i < specials.size(); i++) {
					if (specials.get(i).is(x, y, z)) {
						specials.get(i).cleanUp();
						specials.remove(i);
						break;
					}
				}
			}
			if (Block.isWater(old) || Block.isWater(ID)) {
				uw = true;
			}
			if (old != Block.AIR && !Block.isWater(blocks[X][Y][Z])
					&& Thread.currentThread().getName().equals("main")) {// !!!!!!!!
				if (ChunkManager.dropItems) {
					Item3D i = Item3D.getBlockInstance(blocks[X][Y][Z], new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f),
							true);
					i.influence(Meth.randomFloat(-1, 1), Meth.randomFloat(3, 5), Meth.randomFloat(-1, 1));
				} else if (ChunkManager.dropParticles) {
					ParticleMaster.addNewParticle(PTM.stony,
							new Vector3f(x + DISPLAYOFFSET, y + DISPLAYOFFSET, z + DISPLAYOFFSET),
							new Vector3f(Meth.randomFloat(-ddd, ddd), 1, Meth.randomFloat(-ddd, ddd)), 1, particleTime,
							0, 0.25f);
				}
			}
			blocks[X][Y][Z] = ID;
			// light[X][Y][Z] = 0;
			if (ID < 0) {
				specials.add(SpecialBlock.getInstance(ID, x, y, z));
				specials.get(specials.size() - 1).initAfterGen();
			}
			mask = true;
			if (X == 0) {
				Chunk c = ChunkManager.getWithBlockCoords(x - 1, y, z);
				if (c != null)
					c.mask = true;
			} else if (X == SIZE - 1) {
				Chunk c = ChunkManager.getWithBlockCoords(x + 1, y, z);
				if (c != null)
					c.mask = true;
			}
			if (Y == 0) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y - 1, z);
				if (c != null)
					c.mask = true;
			} else if (Y == SIZE - 1) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y + 1, z);
				if (c != null)
					c.mask = true;
			}
			if (Z == 0) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y, z - 1);
				if (c != null)
					c.mask = true;
			} else if (Z == SIZE - 1) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y, z + 1);
				if (c != null)
					c.mask = true;
			}
			ChunkManager.scheduleBlockUpdate(x, y, z);
			// ChunkManager.blockUpdate(x, y, z);
			// if (Block.isLightSource(ID)) {// && !Block.isLightSource(old)
			// // LightMaster.
			// } else
			if (!Block.isLightSource(old) && Block.isTransparent(old) != Block.isTransparent(ID)) {
				LightMaster.checkForLightUpdates(x, y, z);
			}
			if (Block.lightReduction(old) != Block.lightReduction(ID)) {// Block.isTransparent(old)
																		// !=
																		// Block.isTransparent(ID)
																		// ||
				LightMaster.updateSunLight(x, y, z);// why doesn't this work
													// properly now?!?
				// System.out.println("lightUpdate at " + x + " " + y + " " +
				// z);
			}
			needsSaving = true;
		}
		return old;
	}

	public void set(int x, int y, int z, short ID, boolean blockUpdate) {
		int X = x - realX;
		int Y = y - realY;
		int Z = z - realZ;
		if (blocks[X][Y][Z] != ID) {
			short old = blocks[X][Y][Z];
			if (old < 0) {
				for (int i = 0; i < specials.size(); i++) {
					if (specials.get(i).is(x, y, z)) {
						specials.get(i).cleanUp();
						specials.remove(i);
						break;
					}
				}
			}
			if (Block.isWater(old) || Block.isWater(ID)) {
				uw = true;
			}
			if (old != Block.AIR && !Block.isWater(blocks[X][Y][Z])
					&& Thread.currentThread().getName().equals("main")) {// !!!!!!!!
				if (ChunkManager.dropItems) {
					Item3D i = Item3D.getBlockInstance(blocks[X][Y][Z], new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f),
							true);
					i.influence(Meth.randomFloat(-1, 1), Meth.randomFloat(3, 5), Meth.randomFloat(-1, 1));
				} else if (ChunkManager.dropParticles) {
					ParticleMaster.addNewParticle(PTM.stony,
							new Vector3f(x + DISPLAYOFFSET, y + DISPLAYOFFSET, z + DISPLAYOFFSET),
							new Vector3f(Meth.randomFloat(-ddd, ddd), 1, Meth.randomFloat(-ddd, ddd)), 1, particleTime,
							0, 0.25f);
				}
			}
			blocks[X][Y][Z] = ID;
			if (ID < 0) {
				specials.add(SpecialBlock.getInstance(ID, x, y, z));
				specials.get(specials.size() - 1).initAfterGen();
			}
			// light[X][Y][Z] = 0;
			mask = true;
			if (X == 0) {
				Chunk c = ChunkManager.getWithBlockCoords(x - 1, y, z);
				if (c != null)
					c.mask = true;
			} else if (X == SIZE - 1) {
				Chunk c = ChunkManager.getWithBlockCoords(x + 1, y, z);
				if (c != null)
					c.mask = true;
			}
			if (Y == 0) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y - 1, z);
				if (c != null)
					c.mask = true;
			} else if (Y == SIZE - 1) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y + 1, z);
				if (c != null)
					c.mask = true;
			}
			if (Z == 0) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y, z - 1);
				if (c != null)
					c.mask = true;
			} else if (Z == SIZE - 1) {
				Chunk c = ChunkManager.getWithBlockCoords(x, y, z + 1);
				if (c != null)
					c.mask = true;
			}
			if (blockUpdate)
				ChunkManager.scheduleBlockUpdate(x, y, z);
			// if (blockUpdate)
			// ChunkManager.blockUpdate(x, y, z);
			if (!Block.isLightSource(old) && Block.isTransparent(old) != Block.isTransparent(ID)) {
				LightMaster.checkForLightUpdates(x, y, z);
			}
			if (Block.isTransparent(old) != Block.isTransparent(ID)) {
				LightMaster.updateSunLight(x, y, z);
			}
			needsSaving = true;
		}
	}

	public void blockUpdate(int x, int y, int z) {
		Block.blockUpdate(this, x, y, z, get(x, y, z));
	}

	public void setWater(int x, int y, int z, short ID) {
		int X = x - realX;
		int Y = y - realY;
		int Z = z - realZ;
		if (blocks[X][Y][Z] != ID) {
			blocks[X][Y][Z] = ID;
			needsSaving = true;
			uw = true;
			LightMaster.checkForLightUpdates(x, y, z);
		}
	}

	// private static final float restMaskBrightness = 0.5f;

	private void restMask(ArrayListF verts2, ArrayListI indices2, ArrayListF texes2, ArrayListF norms2,
			ArrayListF lightvalues2) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (!Block.normalModel(blocks[x][y][z])) {
						ModelData m = Block.getModel(blocks[x][y][z]);
						if (m != null) {// blocks[x][y][z] != Block.TALL_GRASS
							int is = (verts2.size() / 3);
							float size = Block.getModelScale(blocks[x][y][z]);
							float yoffset = Block.getYOffset(blocks[x][y][z]);
							for (int i = 0; i < m.getVertices().length; i += 3) {
								verts2.add(m.getVertices()[i] * size + x);
								verts2.add(m.getVertices()[i + 1] * size + y + yoffset);
								verts2.add(m.getVertices()[i + 2] * size + z);
								lightvalues2.add(torchLight(light[x][y][z]) * INVLIGHTR);// more
								// sophisticated:
								// check
								// 6
								// sides
								// and
								// average
								// based
								// on
								// the
								// normal!
								lightvalues2.add(torchLight(light[x][y][z]) * INVLIGHTR);
								lightvalues2.add(torchLight(light[x][y][z]) * INVLIGHTR);
								lightvalues2.add(sunLight(light[x][y][z]) * INVLIGHTR);
							}
							for (int i = 0; i < m.getIndices().length; i++) {
								indices2.add(is + m.getIndices()[i]);
							}
							for (int i = 0; i < m.getTextureCoords().length;) {
								texes2.add(m.getTextureCoords()[i++]);
								texes2.add(m.getTextureCoords()[i++]);
								texes2.add((float) Block.getRenderID(blocks[x][y][z], -1));
							}
							for (int i = 0; i < m.getNormals().length; i++) {
								norms2.add(m.getNormals()[i]);
							}
						}
					}
				}
			}
		}
	}

	private void yMask(ArrayListF verts, ArrayListI indices, ArrayListF texes, ArrayListF lightvalues, boolean up) {
		int SIDE = up ? Block.UP : Block.DOWN;
		int x = 0, y = 0, z = 0, startx, startz, endx = SIZE, endz = SIZE;
		short currID;
		int count = verts.size();
		short[][][] copy = getCopyY(up);
		x = 0;
		z = 0;
		short currentLight;
		short[][][] LV = lightValuesY(up);
		for (y = 0; y < SIZE; y++) {// Yrepeat
			boolean repeat = true;
			while (repeat) {// holeRepeat
				x = 0;
				z = 0;
				while (copy[x][y][z] == 0) {//
					x++;
					if (x == SIZE) {
						x = 0;
						z++;
						if (z == SIZE) {
							break;
						}
					}
				}
				startx = x;
				startz = z;
				if (z < SIZE) {
					currID = copy[startx][y][startz];
					currentLight = LV[x][y][z];
					for (x = startx; x < SIZE; x++) {
						if (copy[x][y][z] != currID
								|| (Block.needsPerLightPrimitives(currID) && LV[x][y][z] != currentLight)) {//
							break;
						} else {
							copy[x][y][z] = 0;
						}
					}
					endx = x - 1;
					boolean fail = false;
					endz = startz;
					for (z = startz + 1; z < SIZE; z++) {
						for (x = startx; x <= endx; x++) {
							if (copy[x][y][z] != currID
									|| (Block.needsPerLightPrimitives(currID) && LV[x][y][z] != currentLight)) {
								fail = true;
								break;
							}
						}
						if (fail) {
							break;
						} else {
							for (x = startx; x <= endx; x++) {
								copy[x][y][z] = 0;
							}
							endz = z;
						}
					}
					float S = 0.5f + extraOffset;
					int ss = (verts.size()) / 3;
					if (Block.isUpperSlab(currID)) {
						float slabheight = Block.slabHeight(currID);
						verts.add(startx - S);
						verts.add(up ? (y + S) : (y + S - slabheight));
						verts.add(startz - S);
						verts.add(startx - S);
						verts.add(up ? (y + S) : (y + S - slabheight));
						verts.add(endz + S);
						verts.add(endx + S);
						verts.add(up ? (y + S) : (y + S - slabheight));
						verts.add(startz - S);
						verts.add(endx + S);
						verts.add(up ? (y + S) : (y + S - slabheight));
						verts.add(endz + S);
					} else if (Block.isLesserSlab(currID)) {
						float slabheight = Block.slabHeight(currID);
						verts.add(startx - S);
						verts.add(up ? y - S + slabheight : y - S);
						verts.add(startz - S);
						verts.add(startx - S);
						verts.add(up ? y - S + slabheight : y - S);
						verts.add(endz + S);
						verts.add(endx + S);
						verts.add(up ? y - S + slabheight : y - S);
						verts.add(startz - S);
						verts.add(endx + S);
						verts.add(up ? y - S + slabheight : y - S);
						verts.add(endz + S);
					} else {
						verts.add(startx - S);
						verts.add(up ? y + S : y - S);
						verts.add(startz - S);

						verts.add(startx - S);
						verts.add(up ? y + S : y - S);
						verts.add(endz + S);

						verts.add(endx + S);
						verts.add(up ? y + S : y - S);
						verts.add(startz - S);

						verts.add(endx + S);
						verts.add(up ? y + S : y - S);
						verts.add(endz + S);
					}

					// if(!smoothLighting){
					for (int i = 0; i < 4; i++) {
						lightvalues.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues.add(sunLight(currentLight) * INVLIGHTR);
					}
					// }else{
					// lights.add((getTorchLightIC(startx, y,
					// startz)+getTorchLightIC(startx-1, y,
					// startz-1))*0.5f*INVLIGHTR);
					// lights.add(torchLight(currentLight)*INVLIGHTR);
					// lights.add(torchLight(currentLight)*INVLIGHTR);
					// lights.add(sunLight(currentLight)*INVLIGHTR);
					// }

					texes.add(0f);
					texes.add(0f);
					texes.add((float) Block.getRenderID(currID, SIDE));

					texes.add(0f);
					texes.add((float) (endz - startz + 1));
					texes.add((float) Block.getRenderID(currID, SIDE));

					texes.add((float) (endx - startx + 1));
					texes.add(0f);
					texes.add((float) Block.getRenderID(currID, SIDE));

					texes.add((float) (endx - startx + 1));
					texes.add((float) (endz - startz + 1));
					texes.add((float) Block.getRenderID(currID, SIDE));

					if (up) {
						indices.add(ss);
						indices.add(ss + 1);
						indices.add(ss + 2);
						indices.add(ss + 1);
						indices.add(ss + 3);
						indices.add(ss + 2);
					} else {
						indices.add(ss);
						indices.add(ss + 2);
						indices.add(ss + 1);
						indices.add(ss + 1);
						indices.add(ss + 2);
						indices.add(ss + 3);
					}

				}
				repeat = false;
				for (x = 0; x < SIZE && !repeat; x++) {
					// for(int y = 0; y < SIZE; y++){
					for (z = 0; z < SIZE && !repeat; z++) {
						if (copy[x][y][z] != 0) {
							repeat = true;
						}
					}
					// }
				}
			} // holeRepeat
		} // Yrepeat

		if (up) {
			upcount = (verts.size() - count) / 3;
		} else {
			downcount = (verts.size() - count) / 3;
		}
	}

	private void xMask(ArrayListF verts2, ArrayListI indices2, ArrayListF texes2, ArrayListF lightvalues2,
			boolean plus) {
		int SIDE = plus ? Block.XP : Block.XM;
		int x = 0, y = 0, z = 0, starty, startz, endy = SIZE, endz = SIZE;
		short currID;
		int count = verts2.size();
		short[][][] copy = getCopyX(plus);
		short currentLight;
		short[][][] LV = lightValuesX(plus);
		for (x = 0; x < SIZE; x++) {
			boolean running = true;
			while (running) {
				y = 0;
				z = 0;
				while (y < SIZE && copy[x][y][z] == 0) {
					z++;
					if (z == SIZE) {
						z = 0;
						y++;
					}
				}
				if (y < SIZE) {
					starty = y;
					startz = z;
					currID = copy[x][y][z];
					currentLight = LV[x][y][z];
					endz = startz;
					for (z = startz; z < SIZE; z++) {
						if (copy[x][y][z] == currID
								&& (!Block.needsPerLightPrimitives(currID) || LV[x][y][z] == currentLight)) {
							copy[x][y][z] = 0;
							endz = z;
						} else {
							break;
						}
					}

					endy = starty;
					if (Block.isConnectableY(currID)) {
						boolean fail = false;
						for (y = starty + 1; y < SIZE; y++) {
							for (z = startz; z <= endz; z++) {
								if (copy[x][y][z] != currID
										|| (Block.needsPerLightPrimitives(currID) && LV[x][y][z] != currentLight)) {
									fail = true;
									break;
								}
							}
							if (fail) {
								break;
							} else {
								endy = y;
								for (z = startz; z <= endz; z++) {
									copy[x][y][z] = 0;
								}
							}
						}
					}
					float S = 0.5f + extraOffset;
					int ss = verts2.size() / 3;
					boolean slab = false;
					boolean us = false;
					float slabheight = 0;
					if (Block.isUpperSlab(currID)) {
						slab = true;
						us = true;
						slabheight = Block.slabHeight(currID);
						verts2.add(plus ? x + S : x - S);
						verts2.add((float) starty + S - slabheight);
						verts2.add(startz - S);
						verts2.add(plus ? x + S : x - S);
						verts2.add(endy + S);
						verts2.add(startz - S);
						verts2.add(plus ? x + S : x - S);
						verts2.add((float) starty + S - slabheight);
						verts2.add(endz + S);
						verts2.add(plus ? x + S : x - S);
						verts2.add(endy + S);
						verts2.add(endz + S);
					} else if (Block.isLesserSlab(currID)) {
						slab = true;
						slabheight = Block.slabHeight(currID);
						verts2.add(plus ? x + S : x - S);
						verts2.add(starty - S);
						verts2.add(startz - S);
						verts2.add(plus ? x + S : x - S);
						verts2.add((float) endy - S + slabheight);
						verts2.add(startz - S);
						verts2.add(plus ? x + S : x - S);
						verts2.add(starty - S);
						verts2.add(endz + S);
						verts2.add(plus ? x + S : x - S);
						verts2.add((float) endy - S + slabheight);
						verts2.add(endz + S);
					} else {
						verts2.add(plus ? x + S : x - S);
						verts2.add(starty - S);
						verts2.add(startz - S);

						verts2.add(plus ? x + S : x - S);
						verts2.add(endy + S);
						verts2.add(startz - S);

						verts2.add(plus ? x + S : x - S);
						verts2.add(starty - S);
						verts2.add(endz + S);

						verts2.add(plus ? x + S : x - S);
						verts2.add(endy + S);
						verts2.add(endz + S);
					}

					for (int i = 0; i < 4; i++) {
						lightvalues2.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues2.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues2.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues2.add(sunLight(currentLight) * INVLIGHTR);
					}

					if (!plus) {
						indices2.add(ss);
						indices2.add(ss + 3);
						indices2.add(ss + 1);
						indices2.add(ss);
						indices2.add(ss + 2);
						indices2.add(ss + 3);
					} else {
						indices2.add(ss);
						indices2.add(ss + 1);
						indices2.add(ss + 3);
						indices2.add(ss);
						indices2.add(ss + 3);
						indices2.add(ss + 2);
					}

					texes2.add(0f);
					texes2.add(slab ? (us ? slabheight : 1f) : (float) (endy - starty + 1));
					texes2.add((float) Block.getRenderID(currID, SIDE));

					texes2.add(0f);
					texes2.add(slab ? (us ? 0f : 1 - slabheight) : 0f);
					texes2.add((float) Block.getRenderID(currID, SIDE));

					texes2.add((float) (endz - startz + 1));
					texes2.add(slab ? (us ? slabheight : 1f) : (float) (endy - starty + 1));
					texes2.add((float) Block.getRenderID(currID, SIDE));

					texes2.add((float) (endz - startz + 1));
					texes2.add(slab ? (us ? 0f : 1 - slabheight) : 0f);
					texes2.add((float) Block.getRenderID(currID, SIDE));

				}
				running = false;
				for (y = 0; y < SIZE; y++) {
					for (z = 0; z < SIZE; z++) {
						if (copy[x][y][z] != 0) {
							running = true;
							break;
						}
					}
				}
			}
		}
		if (plus) {
			xpcount = (verts2.size() - count) / 3;
		} else {
			xmcount = (verts2.size() - count) / 3;
		}
	}

	private void zMask(ArrayListF verts2, ArrayListI indices2, ArrayListF texes2, ArrayListF lightvalues2,
			boolean plus) {
		int SIDE = plus ? Block.ZP : Block.ZM;
		int x = 0, y = 0, z = 0, startx, starty, endx = SIZE, endy = SIZE;
		short currID;
		short currentLight;
		int count = verts2.size();
		short[][][] copy = getCopyZ(plus);
		short[][][] LV = lightValuesZ(plus);
		for (z = 0; z < SIZE; z++) {
			boolean running = true;
			while (running) {
				y = 0;
				x = 0;
				while (y < SIZE && copy[x][y][z] == 0) {
					x++;
					if (x == SIZE) {
						x = 0;
						y++;
					}
				}
				if (y < SIZE) {
					starty = y;
					startx = x;
					currID = copy[x][y][z];
					currentLight = LV[x][y][z];
					endx = startx;
					for (x = startx; x < SIZE; x++) {
						if (copy[x][y][z] == currID
								&& (!Block.needsPerLightPrimitives(currID) || LV[x][y][z] == currentLight)) {
							copy[x][y][z] = 0;
							endx = x;
						} else {
							break;
						}
					}

					endy = starty;
					if (Block.isConnectableY(currID)) {
						boolean fail = false;
						for (y = starty + 1; y < SIZE; y++) {
							for (x = startx; x <= endx; x++) {
								if (copy[x][y][z] != currID
										|| (Block.needsPerLightPrimitives(currID) && LV[x][y][z] != currentLight)) {
									fail = true;
									break;
								}
							}
							if (fail) {
								break;
							} else {
								endy = y;
								for (x = startx; x <= endx; x++) {
									copy[x][y][z] = 0;
								}
							}
						}
					}
					float S = 0.5f + extraOffset;
					int ss = verts2.size() / 3;
					boolean slab = false;
					boolean us = false;
					float slabheight = 0;
					if (Block.isUpperSlab(currID)) {
						slab = true;
						us = true;
						slabheight = Block.slabHeight(currID);
						verts2.add(startx - S);
						verts2.add((float) starty + S - slabheight);
						verts2.add(plus ? z + S : z - S);
						verts2.add(startx - S);
						verts2.add(endy + S);
						verts2.add(plus ? z + S : z - S);
						verts2.add(endx + S);
						verts2.add((float) starty + S - slabheight);
						verts2.add(plus ? z + S : z - S);
						verts2.add(endx + S);
						verts2.add(endy + S);
						verts2.add(plus ? z + S : z - S);
					} else if (Block.isLesserSlab(currID)) {
						slab = true;
						slabheight = Block.slabHeight(currID);
						verts2.add(startx - S);
						verts2.add(starty - S);
						verts2.add(plus ? z + S : z - S);
						verts2.add(startx - S);
						verts2.add((float) endy - S + slabheight);
						verts2.add(plus ? z + S : z - S);
						verts2.add(endx + S);
						verts2.add(starty - S);
						verts2.add(plus ? z + S : z - S);
						verts2.add(endx + S);
						verts2.add((float) endy - S + slabheight);
						verts2.add(plus ? z + S : z - S);
					} else {
						verts2.add(startx - S);
						verts2.add(starty - S);
						verts2.add(plus ? z + S : z - S);
						verts2.add(startx - S);
						verts2.add(endy + S);
						verts2.add(plus ? z + S : z - S);
						verts2.add(endx + S);
						verts2.add(starty - S);
						verts2.add(plus ? z + S : z - S);
						verts2.add(endx + S);
						verts2.add(endy + S);
						verts2.add(plus ? z + S : z - S);
					}
					for (int i = 0; i < 4; i++) {
						lightvalues2.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues2.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues2.add(torchLight(currentLight) * INVLIGHTR);
						lightvalues2.add(sunLight(currentLight) * INVLIGHTR);
					}

					if (plus) {
						indices2.add(ss);
						indices2.add(ss + 3);
						indices2.add(ss + 1);
						indices2.add(ss);
						indices2.add(ss + 2);
						indices2.add(ss + 3);
					} else {
						indices2.add(ss);
						indices2.add(ss + 1);
						indices2.add(ss + 3);
						indices2.add(ss);
						indices2.add(ss + 3);
						indices2.add(ss + 2);
					}

					texes2.add(0f);
					texes2.add(slab ? (us ? slabheight : 1f) : (float) (endy - starty + 1));
					texes2.add((float) Block.getRenderID(currID, SIDE));

					texes2.add(0f);
					texes2.add(slab ? (us ? 0f : 1 - slabheight) : 0f);
					texes2.add((float) Block.getRenderID(currID, SIDE));

					texes2.add((float) (endx - startx + 1));
					texes2.add(slab ? (us ? slabheight : 1f) : (float) (endy - starty + 1));
					texes2.add((float) Block.getRenderID(currID, SIDE));

					texes2.add((float) (endx - startx + 1));
					texes2.add(slab ? (us ? 0f : 1 - slabheight) : 0f);
					texes2.add((float) Block.getRenderID(currID, SIDE));

				}
				running = false;
				for (y = 0; y < SIZE; y++) {
					for (x = 0; x < SIZE; x++) {
						if (copy[x][y][z] != 0) {
							running = true;
							break;
						}
					}
				}
			}
		}
		if (plus) {
			zpcount = (verts2.size() - count) / 3;
		} else {
			zmcount = (verts2.size() - count) / 3;
		}
	}

	private short[][][] getCopyX(boolean plus) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (blocks[x][y][z] != 0 && (staticWater || !Block.isWater(blocks[x][y][z]))
							&& checkX(x, y, z, plus)) {
						boolean normal = Block.normalModel(blocks[x][y][z]);
						if (normal) {
							copy[x][y][z] = blocks[x][y][z];
						} else {
							restMaskNeeded = true;
						}
					} else {
						copy[x][y][z] = 0;
					}
				}
			}
		}
		return copy;
	}

	private short[][][] getCopyZ(boolean plus) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (blocks[x][y][z] != 0 && (staticWater || !Block.isWater(blocks[x][y][z]))
							&& checkZ(x, y, z, plus)) {
						boolean normal = Block.normalModel(blocks[x][y][z]);
						if (normal) {
							copy[x][y][z] = blocks[x][y][z];
						} else {
							restMaskNeeded = true;
						}
					} else {
						copy[x][y][z] = 0;
					}
				}
			}
		}
		return copy;
	}

	private short[][][] getCopyY(boolean up) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (blocks[x][y][z] != 0 && (staticWater || !Block.isWater(blocks[x][y][z]))
							&& checkY(x, y, z, up)) {
						boolean normal = Block.normalModel(blocks[x][y][z]);
						if (normal) {
							copy[x][y][z] = blocks[x][y][z];
						} else {
							restMaskNeeded = true;
						}
					} else {
						copy[x][y][z] = 0;
					}
				}
			}
		}
		return copy;
	}

	private boolean checkX(int x, int y, int z, boolean plus) {
		boolean ret;
		if (plus) {
			ret = Block.isTransparent(blocks[x][y][z]) || Block.isTransparent(x < SIZE - 1 ? (blocks[x + 1][y][z])
					: ChunkManager.getBlockID(realX + x + 1, realY + y, realZ + z));
		} else {
			ret = Block.isTransparent(blocks[x][y][z]) || Block.isTransparent(
					x > 0 ? this.blocks[x - 1][y][z] : ChunkManager.getBlockID(realX + x - 1, realY + y, realZ + z));
		}
		return ret;
	}

	private boolean checkZ(int x, int y, int z, boolean plus) {
		boolean ret = Block.isTransparent(blocks[x][y][z]) || Block.isTransparent(plus
				? (z < SIZE - 1 ? this.blocks[x][y][z + 1]
						: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y, this.z * SIZE + z + 1))
				: (z > 0 ? this.blocks[x][y][z - 1]
						: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y, this.z * SIZE + z - 1)));
		return ret;
	}

	private boolean checkY(int x, int y, int z, boolean up) {
		boolean ret;
		if (up) {
			ret = Block.isTransparent(blocks[x][y][z]) || Block.isTransparent(y < SIZE - 1 ? this.blocks[x][y + 1][z]
					: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y + 1, this.z * SIZE + z));
		} else {
			ret = Block.isTransparent(blocks[x][y][z]) || Block.isTransparent(y > 0 ? this.blocks[x][y - 1][z]
					: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y - 1, this.z * SIZE + z));
		}
		return ret;
	}

	private static short fakeLight;
	static {
		short value = MAXL - 4;
		fakeLight = (short) ((fakeLight & ((0x00 << 8) | 0xFF)) | (value << 8));
		fakeLight = (short) ((fakeLight & (0x00 | (0xFF << 8))) | value);
	}

	private short[][][] lightValuesX(boolean plus) {
		if (fake) {
			for (int x = 0; x < SIZE; x++)
				for (int y = 0; y < SIZE; y++)
					for (int z = 0; z < SIZE; z++)
						lightCopy[x][y][z] = fakeLight;
			return lightCopy;
		}
		if (plus) {
			Chunk c = ChunkManager.getWithChunkCoords(x + 1, y, z);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					for (int z = 0; z < SIZE; z++) {
						if (x < SIZE - 1) {
							lightCopy[x][y][z] = light[x + 1][y][z];
						} else if (c != null) {
							lightCopy[x][y][z] = c.light[0][y][z];
						} else {
							lightCopy[x][y][z] = 0;
						}
					}
				}
			}
		} else {
			Chunk c = ChunkManager.getWithChunkCoords(x - 1, y, z);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					for (int z = 0; z < SIZE; z++) {
						if (x > 0) {
							lightCopy[x][y][z] = light[x - 1][y][z];
						} else if (c != null) {
							lightCopy[x][y][z] = c.light[SIZE - 1][y][z];
						} else {
							lightCopy[x][y][z] = 0;
						}
					}
				}
			}
		}
		return lightCopy;
	}

	private short[][][] lightValuesY(boolean up) {
		if (fake) {
			for (int x = 0; x < SIZE; x++)
				for (int y = 0; y < SIZE; y++)
					for (int z = 0; z < SIZE; z++)
						lightCopy[x][y][z] = fakeLight;
			return lightCopy;
		}
		if (up) {
			Chunk c = ChunkManager.getWithChunkCoords(x, y + 1, z);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					for (int z = 0; z < SIZE; z++) {
						if (y < SIZE - 1) {
							lightCopy[x][y][z] = light[x][y + 1][z];
						} else if (c != null) {
							lightCopy[x][y][z] = c.light[x][0][z];
						} else {
							lightCopy[x][y][z] = Chunk.MAXL - 4;
						}
					}
				}
			}
		} else {
			Chunk c = ChunkManager.getWithChunkCoords(x, y - 1, z);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					for (int z = 0; z < SIZE; z++) {
						if (y > 0) {
							lightCopy[x][y][z] = light[x][y - 1][z];
						} else if (c != null) {
							lightCopy[x][y][z] = light[x][SIZE - 1][z];
						} else {
							lightCopy[x][y][z] = 0;
						}
					}
				}
			}
		}
		return lightCopy;
	}

	private short[][][] lightValuesZ(boolean plus) {
		if (fake) {
			for (int x = 0; x < SIZE; x++)
				for (int y = 0; y < SIZE; y++)
					for (int z = 0; z < SIZE; z++)
						lightCopy[x][y][z] = fakeLight;
			return lightCopy;
		}
		if (plus) {
			Chunk c = ChunkManager.getWithChunkCoords(x, y, z + 1);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					for (int z = 0; z < SIZE; z++) {
						if (z < SIZE - 1) {
							lightCopy[x][y][z] = light[x][y][z + 1];
						} else if (c != null) {
							lightCopy[x][y][z] = c.light[x][y][0];
						} else {
							lightCopy[x][y][z] = 0;
						}
					}
				}
			}
		} else {
			Chunk c = ChunkManager.getWithChunkCoords(x, y, z - 1);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					for (int z = 0; z < SIZE; z++) {
						if (z > 0) {
							lightCopy[x][y][z] = light[x][y][z - 1];
						} else if (c != null) {
							lightCopy[x][y][z] = c.light[x][y][SIZE - 1];
						} else {
							lightCopy[x][y][z] = 0;
						}
					}
				}
			}
		}
		return lightCopy;
	}

	public int realX() {
		return realX;
	}

	public int realY() {
		return realY;
	}

	public int realZ() {
		return realZ;
	}

	public int cx() {
		return this.x;
	}

	public int cy() {
		return this.y;
	}

	public int cz() {
		return this.z;
	}

	public static final long unloadTime = 5000;
	private long unloadStart = 0;

	public boolean unloadCheck() {
		if (unloadStart == 0) {
			unloadStart = Meth.systemTime();
			return false;
		} else {
			return Meth.systemTime() > unloadStart + unloadTime && Meth.doChance(DisplayManager.getFrameTimeSeconds());
		}
	}

	public void scheduleMaskCreation() {
		mask = true;
	}

	public void getData(SmartByteBuffer save) {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				getColumnData(x, z, save);
			}
		}
	}

	public void getColumnData(int x, int z, SmartByteBuffer save) {
		byte currentCount = 1;
		for (int i = 0; i < SIZE - 1; i++) {
			if (blocks[x][i + 1][z] == blocks[x][i][z]
					&& (blocks[x][i][z] >= 0 || SpecialBlock.metaDataLength(blocks[x][i][z]) == 0)) {
				currentCount++;
			} else {
				save.add(currentCount);
				save.addShort(blocks[x][i][z]);
				if (blocks[x][i][z] < 0) {
					getSpecial(realX + x, realY + i, realZ + z).addMetaData(save);
				}
				currentCount = 1;

			}
		}
		// save.add(currentCount);
		save.add(currentCount);
		save.addShort(blocks[x][SIZE - 1][z]);
		if (blocks[x][SIZE - 1][z] < 0) {
			getSpecial(realX + x, realY + SIZE - 1, realZ + z).addMetaData(save);
		}
		// add(save, blocks[x][SIZE - 1][z]);
	}

	/**
	 * xyz are worldspace coordinates!
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public SpecialBlock getSpecial(int x, int y, int z) {
		for (int i = 0; i < specials.size(); i++)
			if (specials.get(i).is(x, y, z))
				return specials.get(i);
		return null;
	}

	// private static void add(ArrayList<Byte> data, short x) {
	// data.add((byte) (x & 0xff));// lo
	// data.add((byte) ((x >> 8) & 0xff));// hi
	// }
	//
	// private static short bytesToShort(byte hi, byte lo) {
	// return (short) (((hi & 0xFF) << 8) | (lo & 0xFF));
	// }

	public void scheduleWaterUpdate() {
		uw = true;
	}

	private short[][][] light;

	public int getTorchLightIC(int x, int y, int z) {
		int ret = light[x][y][z] & 0xFF;
		// if (ret > Chunk.LIGHTR || ret < 0) {
		// System.err.println(
		// "value too big/small? " + ret + " PATTERN: " +
		// Integer.toBinaryString((int) light[x][y][z]));
		// new Exception().printStackTrace();
		// System.exit(-1);
		// }
		return ret;
	}

	public void setTorchLightIC(int x, int y, int z, int value) {
		light[x][y][z] = (short) ((light[x][y][z] & (0x00 | (0xFF << 8))) | value);
		// if (value > Chunk.LIGHTR || value < 0) {
		// System.err.println("Value too big/small! " + value);
		// new Exception().printStackTrace();
		// System.exit(-1);
		// }
		mask = true;
	}

	public int getSunLightIC(int x, int y, int z) {
		return (light[x][y][z] >> 8) & 0xFF;
	}

	public void setSunLightIC(int x, int y, int z, int value) {
		light[x][y][z] = (short) ((light[x][y][z] & ((0x00 << 8) | 0xFF)) | (value << 8));
		mask = true;
	}

	public int getTorchLight(int x, int y, int z) {
		return getTorchLightIC(x - realX, y - realY, z - realZ);
	}

	public void setTorchLight(int x, int y, int z, int value) {
		setTorchLightIC(x - realX, y - realY, z - realZ, value);
		Chunk c = ChunkManager.getWithBlockCoords(x - 1, y, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x + 1, y, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y - 1, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y + 1, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y, z - 1);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y, z + 1);
		if (c != null)
			c.mask = true;
	}

	public int getSunLight(int x, int y, int z) {
		return getSunLightIC(x - realX, y - realY, z - realZ);
	}

	public void setSunLight(int x, int y, int z, int value) {
		setSunLightIC(x - realX, y - realY, z - realZ, value);
		Chunk c = ChunkManager.getWithBlockCoords(x - 1, y, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x + 1, y, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y - 1, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y + 1, z);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y, z - 1);
		if (c != null)
			c.mask = true;
		c = ChunkManager.getWithBlockCoords(x, y, z + 1);
		if (c != null)
			c.mask = true;
	}

	public short getIC(int x, int y, int z) {
		return blocks[x][y][z];
	}

	private static int torchLight(short val) {
		return val & 0xFF;
	}

	private static int sunLight(short val) {
		return (val >> 8) & 0xFF;
	}

	/**
	 * uses the {@link data.LightMaster} to update every block on the given side
	 * which has a (torch) light value over 0
	 * 
	 * @param side
	 *            use the side constants in {@link data.Block}
	 */
	public void updateLightAtSide(int side) {
		switch (side) {
		case Block.XP:
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (getTorchLightIC(SIZE - 1, y, z) > 0) {
						LightMaster.addLightUpdate(realX + SIZE - 1, realY + y, realZ + z);
					}
				}
			}
			break;
		case Block.XM:
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (getTorchLightIC(0, y, z) > 0) {
						LightMaster.addLightUpdate(realX, realY + y, realZ + z);
					}
				}
			}
			break;
		case Block.UP:
			for (int x = 0; x < SIZE; x++) {
				for (int z = 0; z < SIZE; z++) {
					if (getTorchLightIC(x, SIZE - 1, z) > 0) {
						LightMaster.addLightUpdate(realX + x, realY + SIZE - 1, realZ + z);
					}
				}
			}
			break;
		case Block.DOWN:
			for (int x = 0; x < SIZE; x++) {
				for (int z = 0; z < SIZE; z++) {
					if (getTorchLightIC(x, 0, z) > 0) {
						LightMaster.addLightUpdate(realX + x, realY, realZ + z);
					}
				}
			}
			break;
		case Block.ZP:
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					if (getTorchLightIC(x, y, SIZE - 1) > 0) {
						LightMaster.addLightUpdate(realX + x, realY + y, realZ + SIZE - 1);
					}
				}
			}
			break;
		case Block.ZM:
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					if (getTorchLightIC(x, y, 0) > 0) {
						LightMaster.addLightUpdate(realX + x, realY + y, realZ);
					}
				}
			}
			break;
		}
	}

	public void mapWater(ArrayListF verts, ArrayListI indices, ArrayListF normals) {
		// int s = verts.size();
		yMaskWater(verts, indices, normals, true);
		yMaskWater(verts, indices, normals, false);
		xMaskWater(verts, indices, normals, true);
		xMaskWater(verts, indices, normals, false);
		zMaskWater(verts, indices, normals, true);
		zMaskWater(verts, indices, normals, false);
		// if(s > 0)
		// for(int i = s-1; i < verts.size(); ){
		// verts.set(i, verts.get(i++)+realX);
		// verts.set(i, verts.get(i++)+realY);
		// verts.set(i, verts.get(i++)+realZ);
		// }
	}

	private void yMaskWater(ArrayListF verts, ArrayListI indices, ArrayListF normals, boolean up) {
		short[][][] copy = getCopyYWater(up);
		for (int x = 0; x < SIZE; x++) {// Yrepeat
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (copy[x][y][z] != 0) {

						int ss = verts.size() / 3;

						float h = Block.waterHeight(copy[x][y][z]);
						float S = 0.5f;

						verts.add(realX + x);
						verts.add(realY + (up ? y + h : y));
						verts.add(realZ + z);

						verts.add(realX + x);
						verts.add(realY + (up ? y + h : y));
						verts.add(realZ + z + S * 2);

						verts.add(realX + x + S * 2);
						verts.add(realY + (up ? y + h : y));
						verts.add(realZ + z);

						verts.add(realX + x + S * 2);
						verts.add(realY + (up ? y + h : y));
						verts.add(realZ + z + S * 2);

						if (up) {
							indices.add(ss);
							indices.add(ss + 1);
							indices.add(ss + 2);
							indices.add(ss + 1);
							indices.add(ss + 3);
							indices.add(ss + 2);
						} else {
							indices.add(ss);
							indices.add(ss + 2);
							indices.add(ss + 1);
							indices.add(ss + 1);
							indices.add(ss + 2);
							indices.add(ss + 3);
						}

						int dir = 1;
						if (!up)
							dir = -1;

						for (int i = 0; i < 4; i++) {
							normals.add(0);
							normals.add(dir);
							normals.add(0);
						}

					}
				}
			}
		}
	}

	private void xMaskWater(ArrayListF verts, ArrayListI indices, ArrayListF normals, boolean plus) {
		short[][][] copy = getCopyXWater(plus);
		for (int x = 0; x < SIZE; x++) {// Yrepeat
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (copy[x][y][z] != 0) {

						int ss = verts.size() / 3;

						float h = Block.waterHeight(copy[x][y][z]);
						float S = 0.5f;

						verts.add(realX + (plus ? x + S * 2 : x));
						verts.add(realY + y);
						verts.add(realZ + z);

						verts.add(realX + (plus ? x + S * 2 : x));
						verts.add(realY + y + h);
						verts.add(realZ + z);

						verts.add(realX + (plus ? x + S * 2 : x));
						verts.add(realY + y);
						verts.add(realZ + z + S * 2);

						verts.add(realX + (plus ? x + S * 2 : x));
						verts.add(realY + y + h);
						verts.add(realZ + z + S * 2);

						if (!plus) {
							indices.add(ss);
							indices.add(ss + 3);
							indices.add(ss + 1);
							indices.add(ss);
							indices.add(ss + 2);
							indices.add(ss + 3);
						} else {
							indices.add(ss);
							indices.add(ss + 1);
							indices.add(ss + 3);
							indices.add(ss);
							indices.add(ss + 3);
							indices.add(ss + 2);
						}

						int dir = 1;
						if (!plus)
							dir = -1;

						for (int i = 0; i < 4; i++) {
							normals.add(dir);
							normals.add(0);
							normals.add(0);
						}

					}
				}
			}
		}
	}

	private void zMaskWater(ArrayListF verts, ArrayListI indices, ArrayListF normals, boolean plus) {
		short[][][] copy = getCopyZWater(plus);
		for (int x = 0; x < SIZE; x++) {// Yrepeat
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (copy[x][y][z] != 0) {

						int ss = verts.size() / 3;

						float h = Block.waterHeight(copy[x][y][z]);
						float S = 0.5f;

						verts.add(realX + x);
						verts.add(realY + y);
						verts.add(realZ + (plus ? z + S * 2 : z));

						verts.add(realX + x);
						verts.add(realY + y + h);
						verts.add(realZ + (plus ? z + S * 2 : z));

						verts.add(realX + x + S * 2);
						verts.add(realY + y);
						verts.add(realZ + (plus ? z + S * 2 : z));

						verts.add(realX + x + S * 2);
						verts.add(realY + y + h);
						verts.add(realZ + (plus ? z + S * 2 : z));

						if (plus) {
							indices.add(ss);
							indices.add(ss + 3);
							indices.add(ss + 1);
							indices.add(ss);
							indices.add(ss + 2);
							indices.add(ss + 3);
						} else {
							indices.add(ss);
							indices.add(ss + 1);
							indices.add(ss + 3);
							indices.add(ss);
							indices.add(ss + 3);
							indices.add(ss + 2);
						}

						int dir = 1;
						if (!plus)
							dir = -1;

						for (int i = 0; i < 4; i++) {
							normals.add(dir);
							normals.add(0);
							normals.add(0);
						}

					}
				}
			}
		}
	}

	private static final short[][][] waterCopy = new short[SIZE][SIZE][SIZE];

	private short[][][] getCopyXWater(boolean plus) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (Block.isWater(blocks[x][y][z]) && checkXWater(x, y, z, plus)) {
						waterCopy[x][y][z] = blocks[x][y][z];
					} else {
						waterCopy[x][y][z] = 0;
					}
				}
			}
		}
		return waterCopy;
	}

	private short[][][] getCopyZWater(boolean plus) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (Block.isWater(blocks[x][y][z]) && checkZWater(x, y, z, plus)) {
						waterCopy[x][y][z] = blocks[x][y][z];
					} else {
						waterCopy[x][y][z] = 0;
					}
				}
			}
		}
		return waterCopy;
	}

	private short[][][] getCopyYWater(boolean up) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (Block.isWater(blocks[x][y][z]) && checkYWater(x, y, z, up)) {
						waterCopy[x][y][z] = blocks[x][y][z];
					} else {
						waterCopy[x][y][z] = 0;
					}
				}
			}
		}
		return waterCopy;
	}

	private boolean checkXWater(int x, int y, int z, boolean plus) {
		short s;
		if (plus) {
			s = x < SIZE - 1 ? (blocks[x + 1][y][z])
					: ChunkManager.getBlockID(this.x * SIZE + x + 1, this.y * SIZE + y, this.z * SIZE + z);
		} else {
			s = x > 0 ? this.blocks[x - 1][y][z]
					: ChunkManager.getBlockID(this.x * SIZE + x - 1, this.y * SIZE + y, this.z * SIZE + z);
		}
		return !Block.isWater(s) && Block.isTransparent(s);
	}

	private boolean checkZWater(int x, int y, int z, boolean plus) {
		short s;
		if (plus) {
			s = z < SIZE - 1 ? this.blocks[x][y][z + 1]
					: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y, this.z * SIZE + z + 1);
		} else {
			s = z > 0 ? this.blocks[x][y][z - 1]
					: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y, this.z * SIZE + z - 1);
		}
		return !Block.isWater(s) && Block.isTransparent(s);
	}

	private boolean checkYWater(int x, int y, int z, boolean up) {
		short s;
		if (up) {
			s = y < SIZE - 1 ? this.blocks[x][y + 1][z]
					: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y + 1, this.z * SIZE + z);
		} else {
			s = y > 0 ? this.blocks[x][y - 1][z]
					: ChunkManager.getBlockID(this.x * SIZE + x, this.y * SIZE + y - 1, this.z * SIZE + z);
		}
		return !Block.isWater(s) && Block.isTransparent(s);
	}

}
