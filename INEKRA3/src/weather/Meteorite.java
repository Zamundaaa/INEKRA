package weather;

import org.joml.Vector3f;

import data.ChunkManager;
import dataAdvanced.SimpleConstructs;
import entities.Projectil;
import gameStuff.SC;
import models.TexturedModel;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class Meteorite extends Projectil{
	
	private static final TexturedModel mod = SC.getModel("meteo", "grey");
	
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
			ChunkManager.deleteBlock(position);
			numberOfDestroyBlocks--;
			if (numberOfDestroyBlocks == 0) {
				ChunkManager.dropItems = false;
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
				ChunkManager.dropItems = true;
				float vel = velocity.length()*0.1f;
				for(int i = 0; i < 20; i++){
					ParticleMaster.addNewParticle(PTM.sand, new Vector3f(position), Vects.randomVector3f(3*vel),
							1, 5, 0, Meth.randomFloat(1, 3));
				}
				if(exprad == max){
					for(int i = 0; i < 5; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, -0.1f, 3, -1, 1), vel), 0.5f);
						m.exprad = 14;
					}
					vel *= 2;
					for(int i = 0; i < 13; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, -0.1f, 0.5f, -1, 1), vel), 0.3f);
//						m.particleScale = 2.5f;
//						m.attatched.setScale(2);
						m.exprad = 10;
					}
					vel *= 1.5f;
					for(int i = 0; i < 50; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, -0.1f, 4, -1, 1), vel), 0.1f);
//						m.particleScale = 2.5f;
//						m.attatched.setScale(2);
						m.exprad = 7;
					}
					vel *= 1.5f;
					for(int i = 0; i < 75; i++){
						Meteorite m = new Meteorite(new Vector3f(position), Meth.scaleToLength(Vects.randomVector3f(-1, 1, -0.1f, 7, -1, 1), vel), 0.05f);
//						m.particleScale = 2.5f;
//						m.attatched.setScale(2);
						m.exprad = 5;
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
