package dataAdvanced;

import data.ChunkManager;
import renderStuff.DisplayManager;
import skybox.SkyRenderer;
import toolBox.Meth;

public class SolarPanel extends PowerBlock{
	
	private static final float MAX = 100;
	
	public SolarPanel(int x, int y, int z) {
		super(x, y, z);
	}
	
	private int sunLight ;
	
	@Override
	public void update() {
		if(sunLight == 0){
			return;
		}else if(Meth.doChance(DisplayManager.getFrameTimeSeconds())){
			sunLight = ChunkManager.getSunLight(x, y+1, z);
		}
		
		charge += sunLight*SkyRenderer.getTimeB()*DisplayManager.getFrameTimeSeconds();
		if(charge >= MAX){
			charge = MAX;
		}
		if(charge > 0)
			for(int i = 0; i < powers.size(); i++){
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
	public void initAfterGen() {
		sunLight = ChunkManager.getSunLight(x, y+1, z);
	}

	@Override
	public float power(float f) {
		return 0;
	}
	
	@Override
	public String toString(){
		return "A Solar Panel. " + super.toString();
	}
	
}
