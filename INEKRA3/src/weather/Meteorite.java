package weather;

import static data.Block.*;

import org.joml.Vector3f;

import data.ChunkManager;
import dataAdvanced.SimpleConstructs;
import entities.Projectil;
import gameStuff.SC;
import mainInterface.CM;
import models.TexturedModel;
import particles.*;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class Meteorite extends Projectil{
	
	private static final TexturedModel mod = SC.getModel("meteo", "grey");
	
	private float krassigkeit = 1;
	
	public Meteorite(Vector3f position, Vector3f velocity, float scale) {
		super(position, velocity, null, true);
		particleScale = 4*scale;
		attatch(mod, 10*scale);
		lifeTime = 60;
		particleRandomOffset = 10*scale;
		particleChanceMult = 10*scale;
		randomVel = 10*scale;
	}
	
	public Meteorite(Vector3f position, Vector3f velocity){
		this(position, velocity, 1);
	}
	
	private static final int max = 16;
	
	private int exprad = max;
	
	@Override
	public boolean update() {
		Timegone += DisplayManager.getFrameTimeSeconds();
		velocity.y += gravityfact * Meth.GRAVITY * DisplayManager.getFrameTimeSeconds();
		change = new Vector3f(velocity);
		change.x *= DisplayManager.getFrameTimeSeconds();
		change.y *= DisplayManager.getFrameTimeSeconds();
		change.z *= DisplayManager.getFrameTimeSeconds();
		position.add(change.x, change.y, change.z);
		if (ChunkManager.getBlockID(position) != 0) {
			CM.deleteBlock(position);
			numberOfDestroyBlocks--;
			if (numberOfDestroyBlocks == 0) {
//				ChunkManager.dontDropItems();
//				ChunkManager.dropParticles = false;
				SimpleConstructs.EXPLOSION(position, exprad);
//				for(int i = 0; i < 5; i++){
//					Projectil p = new Projectil(new Vector3f(position), Vects.randomVector3f(-5, 5, 1, 5, -5, 5), null, false);
//					p.setPT(PTM.fire);
//					p.setParticleChanceMult(0.75f);
//					p.setRandomParticleOffset(0.1f);
//					p.setRandomParticleVelocity(0.1f);
//					p.setParticleGravity(-0.1f);
//					p.setParticleLifeTime(1);
//					p.setBlock(Block.FIRE);
//				}
//				ChunkManager.dropItems();
				float vel = velocity.length()*0.1f;
				for(int i = 0; i < 20; i++){
					ParticleTexture p = PTM.sand;
					if(Meth.doChance(0.3f)){
						p = PTM.fire;
					}else if(Meth.doChance(0.3f)){
						p = PTM.cosmic;
					}
					ParticleMaster.addNewParticle(p, new Vector3f(position), Vects.randomVector3f(10*vel),
							1, 3, 0, Meth.randomFloat(0.5f, 2));
				}
				if(exprad == max){
					vel *= 2;
					for(int i = 0; i < 10*krassigkeit; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, -0.01f, 0.2f, -1, 1), vel), 0.5f);
						m.exprad = 14;
					}
					vel *= 3;
					for(int i = 0; i < 50*krassigkeit; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, 0.1f, 0.5f, -1, 1), vel), 0.3f);
//						m.particleScale = 2.5f;
//						m.attatched.setScale(2);
						m.exprad = 10;
					}
					vel *= 1.25f;
					for(int i = 0; i < 75*krassigkeit; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-5, 5, -0.1f, 2.5f, -5, 5), vel), 0.1f);
//						m.particleScale = 2.5f;
//						m.attatched.setScale(2);
						m.exprad = 7;
					}
					vel *= 1.5f;
					for(int i = 0; i < 150*krassigkeit; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-3, 3, -0.1f, 3, -3, 3), vel), 0.05f);
//						m.particleScale = 2.5f;
//						m.attatched.setScale(2);
						m.exprad = 5;
					}
					for(int i = 0; i < 100*krassigkeit; i++){
						short b;
						switch(Meth.randomInt(0, 10)){
						case 1: b = GLASS;break;
						case 2: b = BLACK; break;
						case 3: b = DIRT;break;
						case 4: b = FIRE;break;
						case 5: b = GRAVEL;break;
						case 6: b = SAND; break;
						case 7: b = LAMP;break;
						default:
							b = STONE;
						}
						Projectil p = new Projectil(Vects.addRandom(new Vector3f(position), 10), Vects.randomVector3f(10), null, false);
						p.setBlock(b);
						p.attatch(SC.sandmod);
					}
				}
//				else if(exprad == 7){
//					float vel = velocity.length()*0.4f;
//					for(int i = 0; i < 5; i++){
//						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, -0.1f, 0.5f, -1, 1), vel));
//						m.particleScale = 2;
//						m.attatched.setScale(1);
//						m.exprad = 5;
//					}
//					for(int i = 0; i < 10; i++){
//						ParticleMaster.addNewParticle(PTM.sand, new Vector3f(position), Vects.randomVector3f(1.5f*vel),
//								1, 5, 0, Meth.randomFloat(1, 3));
//					}
//				}
				removeAttatched();
				return true;
			}
		}

//		handleThings();

		if (Timegone >= lifeTime) {
			removeAttatched();
			return true;
		}
		if (Meth.doChance(particleChanceMult * DisplayManager.getFrameTimeSeconds() * SC.particleMult
				* (1000.0f / Math.max(1, ParticleMaster.NOP(flyParticle))))) {
			Vector3f pos = new Vector3f(position);
			if (particleRandomOffset != 0) {
				Vects.addRandom(pos, -particleRandomOffset, particleRandomOffset, -particleRandomOffset,
						particleRandomOffset, -particleRandomOffset, particleRandomOffset);
			}
			Vector3f vel = Vects.NULL;
			if (randomVel != 0) {
				vel = Vects.randomVector3f(randomVel);
			} else if (particleGravity != 0) {
				vel = new Vector3f();
			}
			ParticleMaster.addNewParticle(flyParticle, pos, vel, particleGravity, particleLifeTime, 0, particleScale,
					llParticles);
		}

		if (sound != null) {
			sound.setPosition(position);
			sound.setVelocity(velocity);
		}

		return false;
	}
	
}
