package data;

import collectionsStuff.SmartByteBuffer;
import gameStuff.Err;
import gameStuff.WorldObjects;
import toolBox.Tools;

import static data.Chunk.SIZE;

public class ChunkSaver {

	public static final String trenner = ";";
	public static final String kindTrenner = ":";
	public static final String METADATA = "#";
	public static String worldName = "fourth";

	public static void saveStandardData() {
		if (WorldObjects.player != null) {
			String save = Generator.seed + trenner;
			save += WorldObjects.player.getPosition().x + trenner;
			save += WorldObjects.player.getPosition().y + trenner;
			save += WorldObjects.player.getPosition().z + trenner;

			Tools.writeToFile("ChunksSave/" + worldName + "/" + "dataSave.txt", save);
		}
		Tools.writeToFile("ChunksSave/" + worldName, "/saveVersion.txt", "2", true);
	}

	public static void restoreStandardData() {
		String save = Tools.readFile("ChunksSave/" + worldName + "/dataSave.txt");
		if (save != null) {
			String[] datas = save.split(trenner);
			Generator.seed = Long.parseLong(datas[0]);
			WorldObjects.player.setPosition(Float.parseFloat(datas[1]), Float.parseFloat(datas[2]),
					Float.parseFloat(datas[3]));
		}
		checkAndUpdateSaveVersion();
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
