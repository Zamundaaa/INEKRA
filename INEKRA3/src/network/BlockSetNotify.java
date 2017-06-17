package network;

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
	
}
