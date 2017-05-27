package cubyWater;

import java.util.ArrayList;

import org.joml.Vector3f;

import cubyWaterNew.NewWaterUpdater;
import data.Block;
import data.ChunkManager;
import entities.Projectil;
import gameStuff.MainLoop;
import gameStuff.TickManager;
import particles.PTM;
import renderStuff.DisplayManager;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class WaterUpdater {

	public static final boolean MULTITHREADING = true;
	public static final float UPS = 10;// Updates per second!
	public static final float WATERSPEED = 0.5f;// chance for a single water to
												// be updated per update ...
												// think about it!
	public static Thread updater;
	private static volatile boolean updating = false;

	// water updates without list!!! Should remove lag spikes and performance
	// killing on slower machines!
	// build mesh from chunk data in extra thread,
	// pass updated model to WaterRenderer which then loads it to the VAO

	public static void init() {
		if (NewWaterUpdater.useWaterMesh)
			return;
		if (MULTITHREADING) {
			updater = new Thread("waterUpdater") {
				@Override
				public void run() {
					long cooldown = (long) (1000 / UPS);
					long utime = 0;
					updating = true;
					while (ThreadManager.running()) {
						if (utime < cooldown)
							Meth.wartn(cooldown - utime);
						utime = Meth.systemTime();
						update();
						while (MainLoop.MENUOPEN && ThreadManager.running()) {
							Meth.wartn(100);
						}
						utime = Meth.systemTime() - utime;
					}
					updating = false;
				}
			};
			updater.setName("WaterUpdater");
			updater.start();
		}
	}

	public static void waitAndStop() {
		while (updating) {
			Meth.wartn(100);
		}
	}

	// private static long lastTime = Meth.systemTime();

	public static void update() {
		// ArrayList<Water> ws = WaterManager.getWater();
		if (!NewWaterUpdater.useWaterMesh)
			for (int i = 0; i < waters.size() && ThreadManager.running(); i++) {
				update(waters.get(i));
			}
	}

	public static final float RANDOMSHIFTPS = 0.1f;

	private static void update(Water w) {
		if (WATERSPEED == 1 || Meth.doChance(WATERSPEED)) {
			float x = w.getSavedPos().x, y = w.getSavedPos().y, z = w.getSavedPos().z;
			short ID = ChunkManager.getBlockID(x, y, z);
			if (!Block.isWater(ID)) {
				// Err.err.println("WASNHIERLOS?!? " + ID + " X: " + x + " Y:
				// " + y + " Z: " + z);
			} else {
				short downID = ChunkManager.getBlockForBlocksOnly(x, y - 1, z);
				if (downID == Block.AIR) {
					ChunkManager.deleteWater(x, y, z);
					// w.getSavedPos().y -= 1;
					if (ChunkManager.getBlockID(x, y - 2, z) != 0) {
						ChunkManager.setWaterID(x, y - 1, z, ID);
					} else {
						while (TickManager.updating) {
							Meth.wartn(1);
						}
						Projectil p = new Projectil(new Vector3f(x, (float) Math.floor(y) + 0.5f, z), new Vector3f(),
								null, false);
						p.setBlock(ID);
						p.setGravity(1);
						p.setRandomParticleOffset(0.5f);
						p.setParticleChanceMult(5);
						p.setPT(PTM.raindrop);
						p.setParticleScale(0.2f);
						p.setParticleLifeTime(1);
						p.setRandomParticleVelocity(0.5f);
						p.setParticleGravity(1);
					}
				} else if (downID != Block.max_water && Block.isWater(downID)) {
					int diff = Math.min(Block.max_water - downID, ID - 1000);
					short i = (short) (downID + diff);
					ChunkManager.setWaterID(x, y - 1, z, i);
					// if(!Block.isWater(i)){
					// Err.err.println("a water block is set to " + i + " ???
					// original ID: " + downID + " own ID: " + ID + " diff: " +
					// diff
					// + " X: " + x + " Y: " + y + " Z: " + z);
					// }
					int newID = (ID - diff);
					if (newID >= Block.min_water) {
						ChunkManager.setWaterID(x, y, z, (short) newID);
					} else {
						ChunkManager.setWaterID(x, y, z, Block.AIR);
					}
				} else {
					if (ID > Block.evaporation_treshold) {
						short xp = ChunkManager.getBlockForBlocksOnly(x + 1, y, z);
						short xm = ChunkManager.getBlockForBlocksOnly(x - 1, y, z);
						short zp = ChunkManager.getBlockForBlocksOnly(x, y, z + 1);
						short zm = ChunkManager.getBlockForBlocksOnly(x, y, z - 1);
						if (xp != ID && Block.isWater(xp)) {// xp > ID+1 || xp <
															// ID-1 instead of
															// xp !=
															// ID weil: 1/2 = 0
							int hd = ((xp - ID) / 2);
							if ((xp - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (xp - ID) % 2;
							ChunkManager.setWaterID(x + 1, y, z, (short) (xp - hd));
							ChunkManager.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						if (xm != ID && Block.isWater(xm)) {
							int hd = ((xm - ID) / 2);
							if ((xm - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (xm - ID) % 2;
							ChunkManager.setWaterID(x - 1, y, z, (short) (xm - hd));
							ChunkManager.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						if (zp != ID && Block.isWater(zp)) {
							int hd = ((zp - ID) / 2);
							if ((zp - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (zp - ID) % 2;
							ChunkManager.setWaterID(x, y, z + 1, (short) (zp - hd));
							ChunkManager.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						if (zm != ID && Block.isWater(zm)) {
							int hd = ((zm - ID) / 2);
							if ((zm - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (zm - ID) % 2;
							ChunkManager.setWaterID(x, y, z - 1, (short) (zm - hd));
							ChunkManager.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						int n = 1;
						if (xp == Block.AIR)
							n++;
						if (xm == Block.AIR)
							n++;
						if (zp == Block.AIR)
							n++;
						if (zm == Block.AIR)
							n++;
						short aow = (short) (1000 + ((ID - 1000) / n));
						if (aow > Block.min_water) {
							n = 0;
							if (xp == Block.AIR) {
								ChunkManager.setWaterID(x + 1, y, z, aow);
								n++;
							}
							if (xm == Block.AIR) {
								ChunkManager.setWaterID(x - 1, y, z, aow);
								n++;
							}
							if (zp == Block.AIR) {
								ChunkManager.setWaterID(x, y, z + 1, aow);
								n++;
							}
							if (zm == Block.AIR) {
								ChunkManager.setWaterID(x, y, z - 1, aow);
								n++;
							}
							if (n > 0) {
								ChunkManager.setWaterID(x, y, z, aow);
							}
						} else {
							ChunkManager.setBlockID(x, y, z, Block.AIR);
						}
					} else {
						ID--;
						if (ID >= Block.min_water) {
							ChunkManager.setWaterID(x, y, z, ID);
						} else {
							ChunkManager.setBlockID(x, y, z, Block.AIR);
						}
					}
				}
			}
		}

	}

	private static final ArrayList<Water> waters = new ArrayList<Water>();

	public static void add(Water w) {
		waters.add(w);
	}

	public static void remove(Water w) {
		waters.remove(w);
	}

	public static void clearList() {
		waters.clear();
	}

}
