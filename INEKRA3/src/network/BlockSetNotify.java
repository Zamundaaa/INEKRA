package network;

import collectionsStuff.SmartByteBuffer;
import data.ChunkManager;
import mainInterface.Intraface;

public class BlockSetNotify {
	
	private int x, y, z;
	private short ID;
	
	public BlockSetNotify(int x, int y, int z, short ID) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.ID = ID;
	}
	
	public BlockSetNotify set(int x, int y, int z, short ID){
		this.x = x;
		this.y = y;
		this.z = z;
		this.ID = ID;
		return this;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public short getID() {
		return ID;
	}

	public void putData(SmartByteBuffer buffer) {
		buffer.addInt(x);
		buffer.addInt(y);
		buffer.addInt(z);
		buffer.addShort(ID);
	}
	
	public static void applyDatas(int count, SmartByteBuffer buffer){
		for(int i = 0; i < count; i++){
			int x = buffer.readInt();
			int y = buffer.readInt();
			int z = buffer.readInt();
			short ID = buffer.readShort();
			if(Intraface.isServer)
				Intraface.setBlock(x, y, z, ID);
			else
				ChunkManager.setBlockID(x, y, z, ID);
//			System.out.println("applyed data!");
		}
	}
	
}
