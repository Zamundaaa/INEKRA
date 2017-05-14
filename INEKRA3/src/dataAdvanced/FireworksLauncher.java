package dataAdvanced;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import entities.Projectil;
import toolBox.Meth;

public class FireworksLauncher extends PowerBlock{
	
	public static final int metaDataLength = 8;
	
	public FireworksLauncher(int x, int y, int z) {
		super(x, y, z);
	}
	
	private int mode = 0;
	private long lastShot = Meth.systemTime(), cool = 0;

	@Override
	public void update() {
		if(charge > 50 && mode == 0 && Meth.systemTime() > lastShot + 5000){
			charge -= 50;
			mode = 1;
			lastShot = Meth.systemTime();
		}
//		if(charge > 5){
//			Projectil p = new Projectil(new Vector3f(x+0.5f, y+1.5f, z+0.5f), new Vector3f(0, 20, 0), null, true);
//			p.setFlare(100);
//			p.setGravity(0.1f);
//			charge -= 5;
//		}
		if(mode > 0 && Meth.systemTime() > lastShot + cool){
			if(mode < 10){
				Projectil p = new Projectil(new Vector3f(x+0.5f, y+1.5f, z+0.5f), new Vector3f(0, 20, 0), null, true);
				p.setFlare(100);
				p.setGravity(0.1f);
				cool = 750;
			}else if(mode < 20){
				Projectil p = new Projectil(new Vector3f(x+0.5f, y+1.5f, z+0.5f), new Vector3f(0, 20, 0), null, true);
				p.setFlare(y+mode*2);
				p.setGravity(0.1f);
				cool = 750;
			}else if(mode < 50){
				Projectil p = new Projectil(new Vector3f(x+0.5f, y+1.5f, z+0.5f), new Vector3f((mode - 35)*0.1f, 15, 0), null, true);
				p.setParticleLifeTime(1);
				p.setGravity(0.1f);
				Projectil p2 = new Projectil(new Vector3f(x+0.5f, y+1.5f, z+0.5f), new Vector3f(0, 15, (mode - 35)*0.1f), null, true);
				p2.setParticleLifeTime(1);
				p2.setGravity(0.1f);
				cool = 200;
			}else if(mode < 70){
				Projectil p = new Projectil(new Vector3f(x+0.5f, y+1.5f, z+0.5f), new Vector3f((mode - 60)*0.1f, 20, (mode - 60)*0.1f), null, true);
				p.setFlare(100);
				p.setGravity(0.1f);
				cool = 500;
			}else{
				mode = -1;
			}
			lastShot = Meth.systemTime();
			mode++;
		}
	}
	
	@Override
	public void addMetaData(SmartByteBuffer data){
		data.addInt(mode);
		data.addFloat(charge);
	}
	
	@Override
	public void applyMetaData(SmartByteBuffer data){
		mode = data.readInt();
		charge = data.readFloat();
	}
	
	@Override
	public int metaDataLength(){
		return metaDataLength;
	}

	@Override
	public void initAfterGen() {
		
	}

	@Override
	public float power(float f) {
		charge += f;
		return f;
	}
	
	@Override
	public String toString(){
		return "A FireworksLauncher. " + super.toString();
	}
	
}
