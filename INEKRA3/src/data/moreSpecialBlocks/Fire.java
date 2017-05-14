package data.moreSpecialBlocks;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import data.*;
import entities.Camera;
import entities.Projectil;
import gameStuff.TM;
import inventory.Item3D;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class Fire extends SpecialBlock{
	
	public static final int metaDataLength = 8;
	
	private double creationTime = TM.inGameDays();
	private float burnTime = Meth.randomFloat(0.1f/24, 0.3f/24);
	
	public Fire(int x, int y, int z){
		super(x, y, z);
	}
	
	private static final float spreadChance = 0.1f;
	
	@Override
	public void update() {// light updates!!! + Light from fire  +  eliminate item3Ds from projectiles + reduce particles more!
		ChunkManager.dropItems = false;
		ChunkManager.dropParticles = false;
		
		float dist = Camera.getPosition().distance(x+0.5f, y+0.5f, z+0.5f);
		float fact = dist;
		if(fact > 10){
			fact = 1/(0.1f*fact);
		}else{
			fact = 1;
		}
		
		if(TM.inGameDays() > creationTime + burnTime){
			ChunkManager.deleteBlock(x, y, z);
		}else{
			if(Meth.doChance(15*DisplayManager.getFrameTimeSeconds()*fact)){
				Vector3f vel = Vects.randomVector3f(-0.1f, 0.1f, -0.1f, 0, -0.1f, 0.1f);
				if(dist >= 10)
					ParticleMaster.addNewParticle(PTM.fire, Vects.randomVector3f(x, x+1, y, y+0.2f, z, z+1), 
							vel, -0.03f, Meth.randomFloat(2, 3), 0, Meth.randomFloat(0.4f, 1.25f)*(2 - 100f/(dist*dist)));
				else
					ParticleMaster.addNewParticle(PTM.fire, Vects.randomVector3f(x, x+1, y, y+0.2f, z, z+1), 
							vel, -0.03f, Meth.randomFloat(2, 3), 0, Meth.randomFloat(0.4f, 1.25f));
			}
			if(Meth.doChance(DisplayManager.getFrameTimeSeconds()*spreadChance)){
				Vector3f pos;
				switch(Meth.randomInt(0, 5)){
				case XP:
					pos = new Vector3f(x+1.5f, y+0.5f, z+0.5f);
					break;
				case XM:
					pos = new Vector3f(x-0.5f, y+0.5f, z+0.5f);
					break;
				case ZP:
					pos = new Vector3f(x+0.5f, y+0.5f, z+1.5f);
					break;
				case ZM:
					pos = new Vector3f(x+0.5f, y+0.5f, z-0.5f);
					break;
				default:
					pos = new Vector3f(x+0.5f, y+1.5f, z+0.5f);
				}
				Projectil p = new Projectil(pos, Vects.randomVector3f(-5, 5, 1, 5, -5, 5), null, false);
				p.setPT(PTM.fire);
				p.setParticleChanceMult(ptc);
				p.setRandomParticleOffset(0.1f);
				p.setRandomParticleVelocity(0.1f);
				p.setParticleGravity(-0.1f);
				p.setParticleLifeTime(1);
				p.setBlock(FIRE);
			}
			if(Meth.doChance(DisplayManager.getFrameTimeSeconds())){
				int remx = x;
				int remy = y-1;
				int remz = z;
				short underme = ChunkManager.getBlockForBlocksOnly(remx, remy, remz);
				if(underme == FIRE){
					ChunkManager.deleteBlock(x, y, z);
					Vector3f pos = new Vector3f(x+0.5f, y+0.5f, z+0.5f);
					Projectil p = new Projectil(pos, Vects.randomVector3f(-5, 5, 1, 5, -5, 5), null, false);
					p.setNumberOfDestroyBlocks(2);
					p.setPT(PTM.fire);
					p.setParticleChanceMult(ptc);
					p.setRandomParticleOffset(0.1f);
					p.setRandomParticleVelocity(0.1f);
					p.setParticleGravity(-0.1f);
					p.setParticleLifeTime(1);
					p.setBlock(FIRE);
				}else{
					int r = Meth.randomInt(0, 5);
					switch(r){
					case UP:
						remy = y+1;
						break;
					case XP:
						remx = x+1;
						remy = y;
						break;
					case XM:
						remx = x-1;
						remy = y;
						break;
					case ZP:
						remz = z+1;
						remy = y;
						break;
					case ZM:
						remz = z-1;
						remy = y;
						break;
					}
					if(r != 0)
						underme = ChunkManager.getBlockForBlocksOnly(remx, remy, remz);
					short burned = Block.burnedID(underme);
					if(burned != underme){
						ChunkManager.setBlockID(remx, remy, remz, burned == 0 ? FIRE : burned);
						if(Meth.doChance(0.5f)){
							Vector3f pos;
							switch(Meth.randomInt(0, 5)){
							case XP:
								pos = new Vector3f(x+1.5f, y+0.5f, z+0.5f);
								break;
							case XM:
								pos = new Vector3f(x-0.5f, y+0.5f, z+0.5f);
								break;
							case ZP:
								pos = new Vector3f(x+0.5f, y+0.5f, z+1.5f);
								break;
							case ZM:
								pos = new Vector3f(x+0.5f, y+0.5f, z-0.5f);
								break;
							default:
								pos = new Vector3f(x+0.5f, y+1.5f, z+0.5f);
							}
							Projectil p = new Projectil(pos, Vects.randomVector3f(-5, 5, 1, 5, -5, 5), null, false);
							p.setPT(PTM.fire);
							p.setParticleChanceMult(ptc);
							p.setRandomParticleOffset(0.1f);
							p.setRandomParticleVelocity(0.1f);
							p.setParticleGravity(-0.1f);
							p.setParticleLifeTime(1);
							p.setBlock(FIRE);
						}
					}
				}
			}
		}
		
		if(Meth.doChance(10*DisplayManager.getFrameTimeSeconds())){
			for(int i = 0; i < Item3D.is.size(); i++)
				if(Item3D.is.get(i).getPosition().distanceSquared(x+0.5f, y+0.5f, z+0.5f) <= 1){
					Item3D.is.get(i).cleanUp();
					i--;
				}
		}
		
		ChunkManager.dropParticles = true;
		ChunkManager.dropItems = true;
	}
	
	private static final float ptc = 0.75f;
	
	@Override
	public void cleanUp() {
		
	}

	@Override
	public void initAfterGen() {
		short b = ChunkManager.getBlockID(x, y-1, z);
		if(!Block.burnable(b))
			burnTime = 0.01f/24;
	}

	@Override
	public int metaDataLength() {
		return metaDataLength;
	}

	@Override
	public void applyMetaData(SmartByteBuffer data) {
//		System.out.println("applying MetaData. I: " + data.position());
		creationTime = data.readDouble();
//		System.out.println("I: " + data.position());
	}

	@Override
	public void addMetaData(SmartByteBuffer data) {
		data.addDouble(creationTime);
	}
	
}
