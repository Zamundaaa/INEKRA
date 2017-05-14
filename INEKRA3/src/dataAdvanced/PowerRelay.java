package dataAdvanced;

import java.util.ArrayList;

import collectionsStuff.SmartByteBuffer;
import renderStuff.DisplayManager;
import toolBox.Meth;

public class PowerRelay extends PowerBlock{
	
	private static final float MAX = 100;
	private final ArrayList<PowerBlock> acceptors = new ArrayList<PowerBlock>();
	
	public PowerRelay(int x, int y, int z) {
		super(x, y, z);
	}
	
	private static final float UPDATECHANCE = 0.1f;
	
	@Override
	public void update() {
		if(Meth.doChance(DisplayManager.getFrameTimeSeconds()*UPDATECHANCE)){
			for(int i = 0; i < PowerAcceptor.acceptors.size(); i++){
				PowerBlock p = PowerAcceptor.acceptors.get(i);
				int dx = x-p.x;
				int dy = y-p.y;
				int dz = z-p.z;
				float distsq = dx*dx+dy*dy+dz*dz;
				if(distsq < 25*25 && distsq != 0 && (p instanceof PowerAcceptor)){
					acceptors.add(p);
				}
			}
		}
		float give = charge / acceptors.size();
		for(int i = 0; i < acceptors.size(); i++){
			charge -= acceptors.get(i).power(give);
		}
	}

	@Override
	public void cleanUp() {
		
	}

	@Override
	public void initAfterGen() {
		
	}

	@Override
	public float power(float f) {
		float d = MAX-charge;
		if(f > d){
			charge += d;
			return d;
		}else{
			charge += f;
			return f;
		}
	}

	@Override
	public void addMetaData(SmartByteBuffer data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int metaDataLength() {
		return 0;
	}

	@Override
	public void applyMetaData(SmartByteBuffer data) {
		// TODO Auto-generated method stub
		
	}
	
}
