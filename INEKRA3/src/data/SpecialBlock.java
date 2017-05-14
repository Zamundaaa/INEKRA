package data;

import collectionsStuff.SmartByteBuffer;
import data.moreSpecialBlocks.Fire;
import dataAdvanced.*;

public abstract class SpecialBlock extends Block {

	public int x;
	public int y;
	public int z;

	public SpecialBlock(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static SpecialBlock getInstance(short ID, int x, int y, int z) {
		if (ID == TORCH) {
			return new Torch(x, y, z);
		} else if (ID == LAMP) {
			return new Lamp(x, y, z);
		} else if (ID == MARK) {
			return new Mark(x, y, z);
		} else if (ID == ROCKETLAUNCHER){
			return new FireworksLauncher(x, y, z);
		}else if(ID == SOLARPANEL){
			return new SolarPanel(x, y, z);
		}else if(ID == POWERSENDER){
			return new PowerRelay(x, y, z);
		}else if(ID == POWERACCEPTOR){
			return new PowerAcceptor(x, y, z);
		}else if(ID == FIRE){
			return new Fire(x, y, z);
			//:......................................................................:
		} else {
			return null;
		}
	}

	public abstract void update();

	public boolean is(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}

	public abstract void cleanUp();

	public abstract void initAfterGen();

	public abstract int metaDataLength();

	public abstract void applyMetaData(SmartByteBuffer data);

	public abstract void addMetaData(SmartByteBuffer data);

	public static int metaDataLength(short id) {
		switch(id){
		case ROCKETLAUNCHER:
			return FireworksLauncher.metaDataLength;
		case SOLARPANEL:
			return SolarPanel.metaDataLength;
		case POWERSENDER:
			return PowerRelay.metaDataLength;
		case POWERACCEPTOR:
			return PowerAcceptor.metaDataLength;
		case FIRE:
			return Fire.metaDataLength;
		default:
			return 0;
		}
	}

}
