package data.chunkLoading;

import static data.Chunk.SIZE;

import collectionsStuff.SmartByteBuffer;
import data.*;
import entities.Player;
import gameStuff.Err;
import gameStuff.TM;
import mainInterface.Intraface;
import toolBox.Tools;
import toolBox.configStuff.Config;

public class ChunkSaver {

	public static final String trenner = ";";
	public static final String kindTrenner = ":";
	public static final String METADATA = "#";
	public static String worldName = "fourth";
	
	private static Config config, playerDatas;
	
	public static void saveStandardData() {
////		if (WorldObjects.player != null) {
//			StringBuilder save = new StringBuilder();
//			save.append(Generator.seed);
//			save.append(trenner);
////			save.append(WorldObjects.player.getPosition().x);
////			save.append(trenner);
////			save.append(WorldObjects.player.getPosition().y);
////			save.append(trenner);
////			save.append(WorldObjects.player.getPosition().z);
////			save.append(trenner);
//			save.append(TM.inGameDays());
////			save.append(trenner);
////			save.append(TM.getDayTime());
//
//			Tools.writeToFile("ChunksSave/" + worldName + "/" + "dataSave.txt", save.toString());
////		}
		
		config.setConfig("seed", Generator.seed);
		config.setConfig("ingameDays", TM.inGameDays());
		
		config.save();
		
		Tools.writeToFile("ChunksSave/" + worldName, "/saveVersion.txt", "2", true);
	}
	
	public static Player restoreData(Player p){
		if(Intraface.isServer || Intraface.singlePlayer){
			if(!playerDatas.isKey(p.playerID() + "x")){
				return getFirstSpawnPoint(p);
			}
		}else{
			return p;
		}
		float x = playerDatas.getFloatConfig(p.playerID() + "x");
		float y = playerDatas.getFloatConfig(p.playerID() + "y");
		float z = playerDatas.getFloatConfig(p.playerID() + "z");
		float rotY = playerDatas.getFloatConfig(p.playerID()+"rotY");
		float pitch = playerDatas.getFloatConfig(p.playerID()+"pitch");
		p.setPosition(x, y, z);
		p.setRotY(rotY);
		p.setPitch(pitch);
		return p;
	}

	public static Player getFirstSpawnPoint(Player p) {
		p.getPosition().set(5000, 100, 5000);
		return p;
	}

	public static void restoreStandardData() {
		checkAndUpdateSaveVersion();
		
		config = new Config("ChunksSave/" + worldName + "/dataSave.conf");
		
		playerDatas = new Config("ChunkSave/" + worldName + "/playerDatas.save");
		
		if(config.entryCount() == 0){
			String save = Tools.readFile("ChunksSave/" + worldName + "/dataSave.txt");
			if (save != null) {
				String[] datas = save.split(trenner);
				int i = 0;
				Generator.seed = Long.parseLong(datas[i]);
//				WorldObjects.player.setPosition(Float.parseFloat(datas[1]), Float.parseFloat(datas[2]),
//						Float.parseFloat(datas[3]));
				i = 3;
				if(!datas[i++].isEmpty() && !datas[i].equals("\n"))
					TM.setIngameDays(Double.parseDouble(datas[i]));
			}
		}else{
			Generator.seed = config.getLongConfig("seed");
			TM.setIngameDays(config.getDoubleConfig("ingameDays"));
		}
	}

	public static void saveChunk(Chunk c) {
		SmartByteBuffer save = new SmartByteBuffer();
		c.getData(save);
		Tools.writeBytes(getFilePath(c.cx(), c.cy(), c.cz()), getFileName(c.cx(), c.cy(), c.cz()), save.capToArray());
	}

	public static String getChunkData(Chunk tileChunk) {
		String data = Tools.readFile(getFilePath(tileChunk));
		return data;
	}

	public static boolean saveForChunkExists(int X, int Y, int Z) {
		return Tools.fileThere(getFilePath(X, Y, Z));
	}

	private static String getFilePath(int X, int Y, int Z) {
		return ("ChunksSave/" + worldName + "/");
	}

	private static String getFileName(int X, int Y, int Z) {
		return "x" + X + "y" + Y + "z" + Z + ".chunksave";
	}

	private static String getFilePath(Chunk c) {
		return ("ChunksSave/" + worldName + "/x" + c.cx() + "y" + c.cy() + "z" + c.cz() + ".chunksave");
	}

	public static byte[] load(Chunk c) {
		return Tools.readBytes(getFilePath(c));
	}

	private static void checkAndUpdateSaveVersion() {
		String version = Tools.readFile("ChunksSave/" + worldName + "/saveVersion.txt");
		if (version == null) {
			try {
				updateWorldFrom1To2();
			} catch (Exception e) {
				Err.err.println("ChunksSave/" + worldName + "/saveVersion.txt");
				e.printStackTrace(Err.err);
				System.exit(-1);
			}
		} else {
			try {
				version = removeWhiteSpace(version);
				int v = Integer.parseInt(version);
				if (v != 2) {// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					updateWorldFrom1To2();
				}
			} catch (Exception e) {
				e.printStackTrace();
				updateWorldFrom1To2();
			}
		}
	}

	private static String removeWhiteSpace(String x) {
		// String[] wl = x.split(" ");
		// StringBuilder ret = new StringBuilder();
		// for(int i = 0; i < wl.length; i++)
		// ret.append(wl[i]);
		// x = ret.toString();
		// ret = new StringBuilder();
		// wl = x.split("\n");
		x = x.replaceAll(" ", "");
		x = x.replaceAll("\n", "");
		return x;
	}

	private static void updateWorldFrom1To2() {
		String[] files = Tools.getFiles("ChunksSave/" + worldName + "/");
		if (files != null) {
			for (String s : files) {
				if (s.endsWith(".chunksave")) {
					loadAndAdd0s(s);
				}
			}
		}
	}

	private static void loadAndAdd0s(String chunkFile) {
		String chunkPath = "ChunksSave/" + worldName + "/" + chunkFile;
		byte[] bytes = Tools.readBytes(chunkPath);
		if (bytes == null)
			return;
		SmartByteBuffer d = new SmartByteBuffer(bytes);
		d.setToOldShortByteOrder();
		SmartByteBuffer buffer = new SmartByteBuffer();
		byte one = 1;
		byte zero = 0;
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
					if (id >= 0) {
						buffer.add(count);
						buffer.addShort(id);
					} else {
						for (i = 0; i < count; i++) {
							buffer.add(one);
							buffer.addShort(id);
							for (int i2 = 0; i2 < SpecialBlock.metaDataLength(id); i2++) {
								buffer.add(zero);
							}
						}
					}
				}
			}
		}
		Tools.writeBytes(chunkPath, "", d.capToArray());
	}

}
