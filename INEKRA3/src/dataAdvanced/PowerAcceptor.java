package dataAdvanced;

import java.util.ArrayList;

public class PowerAcceptor extends PowerBlock{
	
	protected static ArrayList<PowerAcceptor> acceptors = new ArrayList<PowerAcceptor>();
	
	private static final float MAX = 100;
	
	public PowerAcceptor(int x, int y, int z) {
		super(x, y, z);
		acceptors.add(this);
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
	public void update() {
		for(int i = 0; i < powers.size() && charge > 0.05f; i++){
			PowerBlock p = powers.get(i);
			int dx = x-p.x;
			int dy = y-p.y;
			int dz = z-p.z;
			float distsq = dx*dx+dy*dy+dz*dz;
			if(distsq < 1.25 && distsq != 0){
				charge -= p.power(charge);
			}
		}
	}
	
	@Override
	public void cleanUp() {
		super.cleanUp();
		acceptors.remove(this);
	}
	
	@Override
	public void initAfterGen() {
		
	}
	
}
