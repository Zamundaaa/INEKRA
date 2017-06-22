package cubyWaterNew;

import static data.Chunk.SIZE;

import java.util.ArrayList;

import org.joml.Vector3f;

import collectionsStuff.ArrayListF;
import collectionsStuff.ArrayListI;
import data.*;
import entities.Projectil;
import gameStuff.TickManager;
import mainInterface.Intraface;
import particles.PTM;
import renderStuff.DisplayManager;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class NewWaterUpdater {
	
	public static final float WATERSPEED = 0.5f;// chance for a single water to be updated per update ... think about it!
	public static final float RANDOMSHIFTPS = 0.1f;
	
	public static final boolean useWaterMesh = true;
	public static Thread modelUpdater;
	public static Thread updater;
	
	public static boolean waterChanged = false;
	
	public static void init(){
		if(!useWaterMesh)return;
//		new Exception().printStackTrace();
		if(!Intraface.isServer){
			modelUpdater = new Thread("WaterMeshUpdater"){
				@Override
				public void run(){
					System.out.println("WaterMeshUpdater started!");
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
							double buff = 0;
							int count = 0;
							for(int i = 1; i < vertices.size(); i+=3){
								if(normals.get(i) == 1)
									count++;
							}
							for(int i = 1; i < vertices.size(); i+=3){
								if(normals.get(i) == 1)
									buff += ((double)vertices.get(i))/count;
							}
							NewWaterRenderer.absHeight = (float)buff;
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
					System.out.println("WaterMeshUpdater stopped!");
				}
			};
			modelUpdater.start();
		}
		if(Intraface.singlePlayer || Intraface.isServer){
			updater = new Thread("WaterUpdater"){
				@Override
				public void run(){
					ArrayList<Chunk> chunks = ChunkManager.getLoadedChunkList();
					Chunk last = null;
					while(ThreadManager.running()){
						if(chunks.size() > 0){
							int i = Meth.randomInt(0, chunks.size()-1);
							Chunk c = chunks.get(i);
							short[][][] wc = c.giveWaterCopy();
							int count = 0;
							while(wc == null && count++ < 5){
								i = Meth.randomInt(0, chunks.size()-1);
								c = chunks.get(i);
								if(c != null)
									wc = c.giveWaterCopy();
							}
							if(c == last){
								Meth.wartn(10);
							}
							if(wc != null){
								for(int x = 0; x < SIZE; x++){
									for(int y = 0; y < SIZE; y++){
										for(int z = 0; z < SIZE; z++){
											if(wc[x][y][z] != 0)
												update(c.realX()+x+0.5f, c.realY()+y, c.realZ()+z+0.5f, wc[x][y][z]);//+Block.waterHeight(wc[x][y][z])
										}
									}
								}
								last = c;
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
					Intraface.deleteWater(x, y, z);
					// w.getSavedPos().y -= 1;
					if (ChunkManager.getBlockID(x, y - 2, z) != 0) {
						Intraface.setWaterID(x, y - 1, z, ID);
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
					Intraface.setWaterID(x, y - 1, z, i);
					// if(!Block.isWater(i)){
					// Err.err.println("a water block is set to " + i + " ???
					// original ID: " + downID + " own ID: " + ID + " diff: " +
					// diff
					// + " X: " + x + " Y: " + y + " Z: " + z);
					// }
					int newID = (ID - diff);
					if (newID >= Block.min_water) {
						Intraface.setWaterID(x, y, z, (short) newID);
					} else {
						Intraface.setWaterID(x, y, z, Block.AIR);
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
							Intraface.setWaterID(x + 1, y, z, (short) (xp - hd));
							Intraface.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						if (xm != ID && Block.isWater(xm)) {
							int hd = ((xm - ID) / 2);
							if ((xm - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (xm - ID) % 2;
							Intraface.setWaterID(x - 1, y, z, (short) (xm - hd));
							Intraface.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						if (zp != ID && Block.isWater(zp)) {
							int hd = ((zp - ID) / 2);
							if ((zp - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (zp - ID) % 2;
							Intraface.setWaterID(x, y, z + 1, (short) (zp - hd));
							Intraface.setWaterID(x, y, z, (short) (ID + hd));
							ID += hd;
						}
						if (zm != ID && Block.isWater(zm)) {
							int hd = ((zm - ID) / 2);
							if ((zm - ID) % 2 == 1
									&& Meth.doChance(RANDOMSHIFTPS * DisplayManager.getFrameTimeSeconds()))
								hd += (zm - ID) % 2;
							Intraface.setWaterID(x, y, z - 1, (short) (zm - hd));
							Intraface.setWaterID(x, y, z, (short) (ID + hd));
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
								Intraface.setWaterID(x + 1, y, z, aow);
								n++;
							}
							if (xm == Block.AIR) {
								Intraface.setWaterID(x - 1, y, z, aow);
								n++;
							}
							if (zp == Block.AIR) {
								Intraface.setWaterID(x, y, z + 1, aow);
								n++;
							}
							if (zm == Block.AIR) {
								Intraface.setWaterID(x, y, z - 1, aow);
								n++;
							}
							if (n > 0) {
								Intraface.setWaterID(x, y, z, aow);
							}
						} else {
							Intraface.setBlock(x, y, z, Block.AIR);
						}
					} else {
						ID--;
						if (ID >= Block.min_water) {
							Intraface.setWaterID(x, y, z, ID);
						} else {
							Intraface.setBlock(x, y, z, Block.AIR);
						}
					}
				}
			}
		}
	}
	
}
