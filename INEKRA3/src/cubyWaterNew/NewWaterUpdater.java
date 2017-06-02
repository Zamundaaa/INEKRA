package cubyWaterNew;

import static cubyWater.WaterUpdater.RANDOMSHIFTPS;
import static cubyWater.WaterUpdater.WATERSPEED;
import static data.Chunk.SIZE;

import java.util.ArrayList;

import org.joml.Vector3f;

import collectionsStuff.ArrayListF;
import collectionsStuff.ArrayListI;
import data.*;
import entities.Projectil;
import gameStuff.TickManager;
import particles.PTM;
import renderStuff.DisplayManager;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class NewWaterUpdater {
	
	public static final boolean useWaterMesh = true;
	public static Thread modelUpdater;
	public static Thread updater;
	
	public static boolean waterChanged = false;
	
	public static void init(){
		if(!useWaterMesh)return;
		modelUpdater = new Thread("WaterMeshUpdater"){
			@Override
			public void run(){
				ArrayList<Chunk> chunks = ChunkManager.getLoadedChunkList();
				ArrayListF vertices = new ArrayListF();
				ArrayListF normals = new ArrayListF();
				ArrayListI indices = new ArrayListI();
				while(ThreadManager.running()){
					if(waterChanged){
						waterChanged = false;
						vertices.clear();
						normals.clear();
						indices.clear();
						for(int i = 0; i < chunks.size() && ThreadManager.running(); i++){
							Chunk c = chunks.get(i);
	//						if(c.waterChanged()){
								c.mapWater(vertices, indices, normals);
	//						}
						}
						while(NewWaterRenderer.change){
							Meth.wartn(3);
						}
						NewWaterRenderer.positions = vertices.capToArray();
						NewWaterRenderer.norms = normals.capToArray();
						NewWaterRenderer.indices = indices.capToArray();
						NewWaterRenderer.change = true;
					}
					Meth.wartn(200);
				}
			}
		};
		modelUpdater.start();
		updater = new Thread("WaterUpdater"){
			@Override
			public void run(){
				ArrayList<Chunk> chunks = ChunkManager.getLoadedChunkList();
				while(ThreadManager.running()){
					if(chunks.size() > 0){
						int i = Meth.randomInt(0, chunks.size()-1);
						Chunk c = chunks.get(i);
						short[][][] wc = c.giveWaterCopy();
						int count = 0;
						while(wc == null && count++ < 5){
							i = Meth.randomInt(0, chunks.size()-1);
							c = chunks.get(i);
							wc = c.giveWaterCopy();
						}
						if(wc != null)
							for(int x = 0; x < SIZE; x++){
								for(int y = 0; y < SIZE; y++){
									for(int z = 0; z < SIZE; z++){
										if(wc[x][y][z] != 0)
											update(c.realX()+x+0.5f, c.realY()+y, c.realZ()+z+0.5f, wc[x][y][z]);//+Block.waterHeight(wc[x][y][z])
									}
								}
							}
					}
//					if(watersUpdated > 500){
//						watersUpdated -= 500;
//						Meth.wartn(1);
//					}
				}
			}
		};
		updater.start();
	}
	
//	private static int watersUpdated;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param ID Block.isWater(ID) HAS to be true!
	 */
	public static void update(float x, float y, float z, short ID){
		if (WATERSPEED == 1 || Meth.doChance(WATERSPEED)) {
//			float x = w.getSavedPos().x, y = w.getSavedPos().y, z = w.getSavedPos().z;
			if (!Block.isWater(ID)) {
				// Err.err.println("WASNHIERLOS?!? " + ID + " X: " + x + " Y:
				// " + y + " Z: " + z);
			} else {
//				watersUpdated++;
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
	
}
