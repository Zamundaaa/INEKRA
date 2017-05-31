package data;

import static data.Chunk.SIZE;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import audio.SourcesManager;
import controls.Keyboard;
import dataAdvanced.SimpleConstructs;
import entities.Projectil;
import gameStuff.*;
import objConverter.ModelData;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class Block {

	public static final short DUMMY = Short.MAX_VALUE, AIR = 0, STONE = 1, SAND = 2, GRASS = 3, DIRT = 4, WOOD = 5,
			LEAVES = 6, SAPLING = 7, STONESLAB_DOWN = 8, STONESLAB_UP = 9, GRAVEL = 10, GLASS = 11, FROZEN_GRASS = 12,
			LEAVY_GRASS = 13, FROZEN_LEAVES = 14, BROWN_LEAVES = 15, MARBLE = 16, KA = 17, TALL_GRASS = 18, RED = 19,
			GREEN = 20, BLUE = 21, BLACK = 22;
	public static final short TORCH = -1, LAMP = -2, MARK = -3,// SpecialBlocks
	ROCKETLAUNCHER = -4, SOLARPANEL = -5, POWERSENDER = -6, POWERACCEPTOR = -7,
	FIRE = -8;
	public static final float BLOCKSIZE = 1;

	public static boolean growTallGrass = false;

	public static short lastNormalBlock() {
		return BLACK;
	}

	public static void update(Chunk c, short ID, int x, int y, int z) {// FALLING SAND/GRAVEL/WATER!!!
		//UPDATECHANCE FOR SEASON CHANGE shall be added, please!
		ChunkManager.dontDropItems();
		ChunkManager.dontDropParticles();
		if (isGrass(ID)) {
			short over = ChunkManager.getBlockID(x, y + 1, z);
			if (!isTransparent(over)) {
				c.set(x, y, z, DIRT);
			} else {
				if (ID != jahresZeitGras())
					c.set(x, y, z, jahresZeitGras());
				if (growTallGrass) {
					if (TM.jahresZeit() == TM.FRÜHLING || TM.jahresZeit() == TM.SOMMER) {
						if (over == AIR && Meth.doChance(0.01f * TM.TIMEFACT * DisplayManager.getFrameTimeSeconds())) {
							ChunkManager.setBlockID(x, y + 1, z, TALL_GRASS);
						}
					}
				}
			}
		} else if (ID == DIRT && TM.jahresZeit() != TM.WINTER) {
			if (isTransparent(ChunkManager.getBlockID(x, y + 1, z)) && (isGrass(ChunkManager.getBlockID(x + 1, y, z))
					|| isGrass(ChunkManager.getBlockID(x - 1, y, z)) || isGrass(ChunkManager.getBlockID(x, y, z + 1))
					|| isGrass(ChunkManager.getBlockID(x, y, z - 1)))) {
				c.set(x, y, z, GRASS);
			}
		} else if (isLeaves(ID)) {
			if (ID != jahresZeitLeaves()) {
				c.set(x, y, z, jahresZeitLeaves());
			}
		} else if (ID == SAPLING) {
			if(TM.isDay()){
				int lightLevel = ChunkManager.getSunLight(x, y+1, z);
				final int growLevel = 7;
				if (lightLevel >= growLevel) {
					int cx = x - c.realX();
					int cz = z - c.realZ();
					if ((cx != 0 || ChunkManager.getWithBlockCoords(x - 1, y, z) != null)
							&& (cx != SIZE || ChunkManager.getWithChunkCoords(x + 1, y, z) != null)
							&& (cz != 0 || ChunkManager.getWithBlockCoords(x, y, z - 1) != null)
							&& (cz != SIZE || ChunkManager.getWithChunkCoords(x, y, z + 1) != null)) {
						if (growSapling(x, y, z) && Meth.doChance(0.01f)) {
							growSapling(x, y + 1, z);
						}
//						new Exception().printStackTrace();
					}
				}
			}
		} else if (ID == GRAVEL || ID == SAND) {
			short d = ChunkManager.getBlockForBlocksOnly(x, y - 1, z);
			if (d == Block.AIR) {
				ChunkManager.chanceToDo = chanceForNextFallingBlockToFall;
				ChunkManager.deleteBlock(x, y, z);
				ChunkManager.chanceToDo = 0;
				// ChunkManager.setBlockID(x, y-1, z, ID);
				Projectil p = new Projectil(new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f), new Vector3f(), null, false);
				p.setPT(PTM.sand);
				p.setBlock(ID);
				p.setGravity(1);
				p.setRandomParticleOffset(0.5f);
				p.setParticleChanceMult(1);
				p.setParticleGravity(1);
				p.attatch(SC.sandmod);
			} else if (Block.isWater(d)) {
				ChunkManager.deleteBlock(x, y, z);
				ChunkManager.setBlockID(x, y - 1, z, ID);
				for (int i = 0; i < 5; i++) {
					ParticleMaster.addNewParticle(PTM.raindrop, new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f),
							Vects.randomVector3f(-1, 1, 2, 2, -1, 1), 1, 2, 0, 0.1f);
				}
			}
		}
		ChunkManager.dropItems();
		ChunkManager.dropParticles();
	}

	private static final int chanceForNextFallingBlockToFall = 50;

	public static boolean isGrass(short ID) {
		return ID == GRASS || ID == FROZEN_GRASS || ID == LEAVY_GRASS;
	}

	public static boolean isLeaves(short ID) {
		return ID == LEAVES || ID == FROZEN_LEAVES || ID == BROWN_LEAVES;
	}

	public static short jahresZeitLeaves() {
		switch (TM.jahresZeit()) {
		case TM.HERBST:
			return BROWN_LEAVES;
		case TM.WINTER:
			return FROZEN_LEAVES;
		default:
			return LEAVES;
		}
	}

	public static short jahresZeitGras() {
		switch (TM.jahresZeit()) {
		case TM.HERBST:
			return LEAVY_GRASS;
		case TM.WINTER:
			return FROZEN_GRASS;
		default:
			return GRASS;
		}
	}

	public static void blockUpdate(Chunk c, int x, int y, int z, short ID) {
		ChunkManager.dontDropItems();
		ChunkManager.dontDropParticles();
		if (ID == GRAVEL || ID == SAND) {
			short d = ChunkManager.getBlockForBlocksOnly(x, y - 1, z);
			if (d == Block.AIR) {
				ChunkManager.chanceToDo = chanceForNextFallingBlockToFall;
				ChunkManager.deleteBlock(x, y, z);
				ChunkManager.chanceToDo = 0;
				// ChunkManager.setBlockID(x, y-1, z, ID);
				Projectil p = new Projectil(new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f), new Vector3f(), null, false);
				p.setPT(PTM.sand);
				p.setBlock(ID);
				p.setGravity(1);
				p.setRandomParticleOffset(0.5f);
				p.setParticleChanceMult(1);
				p.setParticleGravity(1);
				p.attatch(SC.sandmod);
			} else if (Block.isWater(d)) {
				ChunkManager.deleteBlock(x, y, z);
				ChunkManager.setBlockID(x, y - 1, z, ID);
				for (int i = 0; i < 5; i++) {
					ParticleMaster.addNewParticle(PTM.raindrop, new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f),
							Vects.randomVector3f(-1, 1, 2, 2, -1, 1), 1, 2, 0, 0.1f);
				}
			}
		}
		ChunkManager.dropItems();
		ChunkManager.dropParticles();
	}
	
	private static final int[] treeLeaveGrowPlaces = new int[10],
			treeLeaveGrowDiameters = new int[10],
			branchPlaces = new int[10];
	
	private static boolean growSapling(int x, int y, int z) {
		boolean grown = false;
		int h;
		switch(Meth.randomInt(0, 5, x*241+z)){
		case 0:
			h = 1;
			treeLeaveGrowDiameters[0] = 2;
			treeLeaveGrowPlaces[0] = 1;
			break;
		case 2:
		case 3:
		case 4:
			h = 18;
			treeLeaveGrowPlaces[0] = 10;
			treeLeaveGrowPlaces[1] = 15;
			treeLeaveGrowDiameters[0] = 5;
			treeLeaveGrowDiameters[1] = 5;
			break;
		case 5:
			h = 50;
			treeLeaveGrowDiameters[0] = 4;
			treeLeaveGrowDiameters[1] = 3;
			treeLeaveGrowDiameters[2] = 5;
			treeLeaveGrowDiameters[3] = 4;
			treeLeaveGrowPlaces[0] = 30;
			treeLeaveGrowPlaces[1] = 25;
			treeLeaveGrowPlaces[2] = 39;
			treeLeaveGrowPlaces[3] = 46;
			branchPlaces[0] = 15;
			branchPlaces[1] = 17;
			branchPlaces[2] = 19;
			break;
		default:
			h = 7;
			treeLeaveGrowDiameters[0] = 3;
			treeLeaveGrowPlaces[0] = 5;
		}
		for (int i = 1; i <= h; i++) {
			if (ChunkManager.getBlockID(x, y - i, z) != WOOD) {
				ChunkManager.setBlockID(x, y, z, WOOD);
				ChunkManager.setBlockID(x, y + 1, z, SAPLING);
				for(int i2 = 0; i2 < treeLeaveGrowPlaces.length; i2++){
					if(i == treeLeaveGrowPlaces[i2]){
						SimpleConstructs.fillSphere(x, y, z, treeLeaveGrowDiameters[i2], LEAVES, false);
						break;
					}else if(i == branchPlaces[i2]){
						Vects.calcVect.set(0);
						switch(Meth.randomInt(0, 3)){
						case 0:Vects.calcVect.x++;break;
						case 1:Vects.calcVect.x--;break;
						case 2:Vects.calcVect.z++;break;
						case 3:Vects.calcVect.z--;break;
						}
						int d = Meth.randomInt(2, 4);
						for(int i3 = 0; i3 < d; i3++)
							ChunkManager.setBlockID(x+i3*Vects.calcVect.x,
									y+i3*Vects.calcVect.y,
									z+i3*Vects.calcVect.z, WOOD);
						SimpleConstructs.fillSphere(x+d*Vects.calcVect.x, 
								y+d*Vects.calcVect.y, z+d*Vects.calcVect.z, 2, LEAVES, false);
					}
				}
				grown = true;
				break;
			}
		}
		if (!grown) {
			ChunkManager.setBlockID(x, y, z, LEAVES);
		}
		for(int i = 0; i < treeLeaveGrowPlaces.length; i++){
			treeLeaveGrowPlaces[i] = -1;
			branchPlaces[i] = -1;
		}
		return grown;
	}

	public static final int DOWN = 0, UP = 1, XP = 2, XM = 3, ZP = 4, ZM = 5;
	public static final int grassSide = 1, grassTop = 2, stonesides = 3, dirtsides = 4, sandsides = 5, woodsides = 6,
			woodY = 7, leavesides = 8, saplingtex = 9, graveltex = 10, glassTex = 11, leavyGrassSide = 12,
			leavyGrassTop = 13, frozenGrassSide = 14, frozenGrassTop = 15, brownLeavesSides = 16,
			frozenLeavesSides = 17, marbleSides = 18, torchTex = 19, kaTex = 20, tallGrass = 21, lampSides = 22,
			red = 23, green = 24, blue = 25, black = 26;

	public static int getRenderID(short blockID, int SIDE) {
		switch (blockID) {
		case RED:
			return red;
		case GREEN:
			return green;
		case BLUE:
			return blue;
		case BLACK:
			return black;
		case GRASS:
			if (SIDE == UP) {
				return grassTop;
			} else if (SIDE == DOWN) {
				return dirtsides;
			} else {
				return grassSide;
			}
		case TALL_GRASS:
			return tallGrass;
		case LEAVY_GRASS:
			if (SIDE == UP) {
				return leavyGrassTop;
			} else if (SIDE == DOWN) {
				return dirtsides;
			} else {
				return leavyGrassSide;
			}
		case FROZEN_GRASS:
			if (SIDE == UP) {
				return frozenGrassTop;
			} else if (SIDE == DOWN) {
				return dirtsides;
			} else {
				return frozenGrassSide;
			}
		case STONE:
		case STONESLAB_DOWN:
		case STONESLAB_UP:
			return stonesides;
		case DIRT:
			return dirtsides;
		case SAND:
			return sandsides;
		case WOOD:
			if (SIDE == UP || SIDE == DOWN) {
				return woodY;
			} else {
				return woodsides;
			}
		case LEAVES:
			return leavesides;
		case FROZEN_LEAVES:
			return frozenLeavesSides;
		case BROWN_LEAVES:
			return brownLeavesSides;
		case GRAVEL:
			return graveltex;
		case GLASS:
			return glassTex;
		case MARBLE:
			return marbleSides;
		case SAPLING:
			return saplingtex;
		case TORCH:
			return torchTex;
		case KA:
			return kaTex;
		case LAMP:
			return lampSides;
		default:
			return 0;
		}
	}

	public static final short min_water = 1001, max_water = 1100, evaporation_treshold = 1005;

	public static boolean isWater(short b) {
		return b >= min_water && b <= max_water;
	}

	public static boolean isTransparent(short s) {
		return s == AIR || s == SAPLING || s == GLASS || s == TORCH || s == TALL_GRASS || s == MARK
				|| s == FIRE || isWater(s) || isLeaves(s) || isLesserSlab(s) || isUpperSlab(s);
	}

	public static String string(short b) {
		if (isWater(b)) {
			return "Water. Height: " + (b - 1000);
		} else if (isLeaves(b)) {
			return "LEAVES";
		} else if (isGrass(b)) {
			return "GRASS";
		} else {
			switch (b) {
			case AIR:
				return "NOTHING/AIR";
			case DIRT:
				return "DIRT";
			case STONE:
				return "STONE";
			case SAND:
				return "SAND";
			case WOOD:
				return "WOOD";
			case SAPLING:
				return "SAPLING";
			case STONESLAB_DOWN:
			case STONESLAB_UP:
				return "STONESLAB";
			case GLASS:
				return "GLASS";
			case GRAVEL:
				return "GRAVEL";
			case MARBLE:
				return "MARBLE";
			case TORCH:
				return "TORCH";
			case LAMP:
				return "LAMP";
			case FIRE:
				return "FIRE. Do not touch";
			case KA:
				return "No Plan!";
			}
		}
		return "??? ID: " + b;
	}

	public static short getWater(float h) {
		short v = (short) (1000 + h * 100);
		if (v >= min_water && v <= max_water) {
			return v;
		} else {
			return AIR;
		}
	}

	public static boolean normalModel(short ID) {
		return !(ID == SAPLING || ID == TORCH || ID == TALL_GRASS || ID == MARK || ID == FIRE);
	}

	public static boolean isUpperSlab(short ID) {
		if (ID == STONESLAB_UP) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isLesserSlab(short ID) {
		if (ID == STONESLAB_DOWN) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isConnectableY(short ID) {
		return !isLesserSlab(ID) && !isUpperSlab(ID);
	}

	public static float getYDraufTretPos(short ID) {
		if (ID == STONESLAB_DOWN) {
			return 0.5f;
		} else if (ID == AIR || isWater(ID)) {
			return 0;
		} else {
			return 1;
		}
	}

	public static boolean passable(short b) {
		return b == AIR || b == TORCH || b == TALL_GRASS || b == FIRE || isWater(b);
	}

	public static boolean isSlab(short ID) {
		return isLesserSlab(ID) || isUpperSlab(ID);
	}

	public static short holeVersion(short slabID) {
		switch (slabID) {
		case STONESLAB_DOWN:
		case STONESLAB_UP:
			return STONE;
		}
		return AIR;
	}

	public static ModelData getModel(short s) {
		switch (s) {
		case SAPLING:
			return Models.getModelData("Sapling");
		case TORCH:
			return Models.getModelData("torch");
		case TALL_GRASS:
			return Models.getModelData("grass");
		}
		return null;
	}

	public static float slabHeight(short ID) {
		return 0.5f;
	}

	public static long getBreakCool(short ID) {
		long ret;
		switch (ID) {
		case STONE:
		case STONESLAB_DOWN:
		case STONESLAB_UP:
			ret = 1000;
			break;
		case DIRT:
		case GRASS:
		case SAND:
			ret = 500;
			break;
		case GLASS:
		case LAMP:
			return 250;
		case FIRE:
			return 1000000;
		default:
			ret = 750;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			ret *= 0.1f;
		}
		return ret;
	}

	public static String getFileName(short ID) {
		switch (ID) {
		case DIRT:
			return "Dirt";
		case GRASS:
		case FROZEN_GRASS:
		case LEAVY_GRASS:
			return "GrassTop";
		case STONE:
		case STONESLAB_DOWN:
		case STONESLAB_UP:
			return "Stone";
		case GRAVEL:
			return "Gravel";
		case SAND:
			return "Sand";
		case WOOD:
			return "WoodSide";
		case LEAVES:
		case FROZEN_LEAVES:
		case BROWN_LEAVES:
			return "Leave";
		case SAPLING:
			return "Sapling";
		case LAMP:
			return "lampSide";
		default:
			return "unique";
		}
	}

	public static int lightReduction(short s) {
		switch(s){
		case AIR:
		case FIRE:
			return 1;
		default:
			if (isLeaves(s)) {
				return 5;
			}
			if (isWater(s)) {
				int ret = (s - 1000) / 50;
				if (ret == 0)
					ret = 1;
				return ret;
				// return 0;
			}
			return Chunk.MAXL;
		}
	}

	public static int downWardSunLightReduction(short s) {
		switch(s){
		case AIR:
		case FIRE:
//		case GLASS:
			return 0;
		default:
			if(isLeaves(s))
				return 5;
//			if(isWater(s))
//				return 1;
			return 1;
		}
	}

	public static boolean isLightSource(short s) {
		return s == TORCH || s == LAMP;
	}

	public static float getModelScale(short s) {
		switch (s) {
		case TORCH:
			return 0.75f;
		}
		return 1;
	}

	public static float getYOffset(short s) {
		switch (s) {
		case TORCH:
			return -0.1f;
		case TALL_GRASS:
			return -0.5f;
		}
		return 0;
	}

	/**
	 * plays a (more or less) fitting sound for the removal of block b at x, y
	 * and z
	 */
	public static void playBreakSound(short b, float x, float y, float z) {
		if (b == GLASS || b == LAMP) {
			SourcesManager.play(SourcesManager.glass, 50, new Vector3f(x, y, z));
		} else {
			SourcesManager.play(SourcesManager.block, 50, new Vector3f(x, y, z));
		}
	}

	public static boolean needsPerLightPrimitives(short b) {
		return !(b == GLASS);
	}

	public static boolean burnable(short id) {
		return burnedID(id) != id;
	}
	
	public static short burnedID(short id){
		switch(id){
		case WOOD:
		case LEAVES:
			return AIR;
		case GRASS:
			if(Meth.doChance(5f/60/24))
				return DIRT;
		default:
			return id;
		}
	}

	public static boolean potentiallyFlammable(short id) {
		return id == WOOD || id == LEAVES || id == GRASS;
	}

	public static float waterHeight(short currID) {
		return (currID - 1000)*0.01f;
	}

	public static float getBurnTimeInDays(short s) {
		switch(s){
		case GRASS:
			return 20f/60/24;
		case WOOD:
			return 1f/24;
		case LEAVES:
			return 30f/60/24;
		default:
			return 0;
		}
	}

}
