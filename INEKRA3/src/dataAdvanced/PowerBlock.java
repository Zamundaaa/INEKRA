package dataAdvanced;

import java.util.ArrayList;

import collectionsStuff.SmartByteBuffer;
import data.SpecialBlock;

public abstract class PowerBlock extends SpecialBlock{
	
	public static final int metaDataLength = 4;
	protected static final ArrayList<PowerBlock> powers = new ArrayList<>();
	protected float charge;
	
	public PowerBlock(int x, int y, int z) {
		super(x, y, z);
		powers.add(this);
	}

	@Override
	public abstract void update();

	@Override
	public void cleanUp(){
		powers.remove(this);
	}

	@Override
	public abstract void initAfterGen();
	
	public abstract float power(float f);
	
	@Override
	public String toString(){
		return "power: " + charge;
	}
	
	/* 
	 * in bytes!
	 */
	@Override
	public int metaDataLength() {
		return metaDataLength;
	}

	@Override
	public void applyMetaData(SmartByteBuffer data) {
		charge = data.readFloat();
	}
	
	@Override
	public void addMetaData(SmartByteBuffer data){
		data.addFloat(charge);
	}
	
}
