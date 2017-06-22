package mainInterface;

import static data.Block.AIR;

import org.joml.Vector3f;

import data.ChunkManager;
import entities.MWBE;
import entities.graphicsParts.ModelGraphics;

public abstract class Intraface {
	
	public static boolean isServer = false;
	public static boolean singlePlayer = false;
	
	private static Intraface instance;
	
	public static void singlePlayer(){
		instance = new SingleCM();
	}
	
	public static void multiPlayer(){
		instance = new MultiCM();
	}
	
	public static void server(){
		instance = new ServerCM();
	}
	
	public static void setBlock(int x, int y, int z, short ID){
		instance.setB(x, y, z, ID);
	}
	
	public static void setBlock(float x, float y, float z, short ID){
		instance.setB(x, y, z, ID);
	}
	
	public static void setBlock(Vector3f v, short ID){
		instance.setB(v, ID);
	}
	
	public static void deleteBlock(int x, int y, int z){
		instance.deleteB(x, y, z);
	}
	
	public static void deleteBlock(float x, float y, float z){
		instance.deleteB(x, y, z);
	}
	
	public static void deleteBlock(Vector3f v){
		instance.deleteB(v);
	}
	
	public abstract void setB(int x, int y, int z, short ID);
	
	public void setB(float x, float y, float z, short ID) {
		int X = (int)Math.floor(x);
		int Y = (int)Math.floor(y);
		int Z = (int)Math.floor(z);
		setB(X, Y, Z, ID);
	}

	public void setB(Vector3f v, short ID) {
		setB(v.x, v.y, v.z, ID);
	}

	public void deleteB(int x, int y, int z) {
		setB(x, y, z, AIR);
	}

	public void deleteB(float x, float y, float z) {
		setB(x, y, z, AIR);
	}

	public void deleteB(Vector3f v) {
		deleteB(v.x, v.y, v.z);
	}

	public static void deleteWater(float x, float y, float z){
		instance.deleteW(x, y, z);
	}
	
	public abstract void deleteW(int x, int y, int z);
	
	public abstract void deleteW(float x, float y, float z);

	public static void setWaterID(float x, float y, float z, short ID) {
		instance.setWater(x, y, z, ID);
	}
	
	public abstract void setWater(float x, float y, float z, short ID);
	
	public abstract ModelGraphics getMG(MWBE m, short modelID, short texID);
	
	public static ModelGraphics getModelGraphics(MWBE m, short modelID, short texID){
		return instance.getMG(m, modelID, texID);
	}
	
	public static void init() {
		if(isServer){
			server();
		}else if(singlePlayer){
			singlePlayer();
		}else{
			multiPlayer();
		}
	}
	
	public static void finishInit(){
		instance.initSpecifics();
	}

	public static void update() {
		instance.updateSpecifics();
	}

	public static void cleanUp() {
		if(isServer || singlePlayer){
			ChunkManager.cleanUp();
		}
	}
	
	protected abstract void updateSpecifics();

	protected abstract void initSpecifics();
	
}
