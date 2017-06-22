package entities;

import static particles.PTM.cosmic;
import static particles.PTM.projectile;

import org.joml.Vector3f;

import audio.*;
import data.ChunkManager;
import dataAdvanced.SimpleConstructs;
import gameStuff.*;
import mainInterface.Intraface;
import models.TexturedModel;
import particles.*;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;
import weather.WeatherController;

public class Projectil implements TickingThing, HarmingThing {

	public static final int NORMGAIN = 100;
	public static final int MENGAIN = 10;
	public static int GAIN = MENGAIN;

	protected static ParticleTexture particles = projectile, particles2 = cosmic;

	protected Vector3f position;
	protected Vector3f velocity;
	protected Vector3f change;
	protected ParticleTexture flyParticle;
	protected float particleLifeTime = 5;
	protected float lifeTime = 7.5f;
	protected float Timegone;
	protected Entity dest;
	protected float damage = 3;
	protected boolean blockDestroying;
	protected float gravityfact = 1;
	protected float particleRandomOffset = 0;

	protected Source sound;

	// private Vector3f exmin = new Vector3f(-2, -2, -2), exmax = new
	// Vector3f(2, 2, 2);

	public Projectil(Vector3f position, Vector3f velocity, Entity dest, boolean blockDestroying) {
		this.position = position;
		this.velocity = velocity;
		if (velocity.length() > 50) {
			flyParticle = particles2;
		} else {
			flyParticle = particles;
		}
		this.dest = dest;
		this.blockDestroying = blockDestroying;
		if (AudioMaster.soundEnabled && dest != null) {
			sound = new Source();
			sound.setPosition(position);
			sound.setVelocity(velocity);
			sound.setVolume(100);
			sound.play(SourcesManager.missle3);
		}
		TickManager.addTickingThing(this);
	}

	public Projectil(Vector3f position, Vector3f velocity, Entity dest, float damage, boolean blockDestroying) {
		this.position = position;
		this.velocity = velocity;
		// if (particles == null) {
		// particles = new
		// ParticleTexture(loader.loadParticleTexture("particleAtlas"), 4);
		// }
		// if (particles2 == null) {
		// particles2 = new
		// ParticleTexture(loader.loadParticleTexture("cosmic"), 4);
		// }
		if (velocity.length() > 50) {
			flyParticle = particles2;
		} else {
			flyParticle = particles;
		}
		this.dest = dest;
		this.damage = damage;
		this.blockDestroying = blockDestroying;
		if (AudioMaster.soundEnabled && dest != null) {
			sound = new Source();
			sound.setPosition(position);
			sound.setVelocity(velocity);
			sound.setVolume(100);
			sound.play(SourcesManager.missle3);
		}
		TickManager.addTickingThing(this);
	}

	public Projectil(Vector3f position, Vector3f velocity, Entity dest, float damage, float lifeTime,
			boolean blockDestroying) {
		this.position = position;
		this.velocity = velocity;
		if (velocity.length() > 50) {
			flyParticle = particles2;
		} else {
			flyParticle = particles;
		}
		this.dest = dest;
		this.damage = damage;
		this.lifeTime = lifeTime;
		this.blockDestroying = blockDestroying;
		if (AudioMaster.soundEnabled && dest != null) {
			sound = new Source();
			sound.setPosition(position);
			sound.setVelocity(velocity);
			sound.setVolume(100);
			sound.play(SourcesManager.missle3);
		}
		TickManager.addTickingThing(this);
	}

	public Projectil(Vector3f position, Vector3f velocity, ParticleTexture tex, Entity dest, float damage) {
		this.position = position;
		this.velocity = velocity;
		if (velocity.length() > 50) {
			flyParticle = particles2;
		} else {
			flyParticle = particles;
		}
		this.dest = dest;
		this.damage = damage;
		this.flyParticle = tex;
		if (AudioMaster.soundEnabled && dest != null) {
			sound = new Source();
			sound.setPosition(position);
			sound.setVelocity(velocity);
			sound.setVolume(100);
			sound.play(SourcesManager.missle3);
		}
		TickManager.addTickingThing(this);
	}

	protected float particleScale = 0.5f;

	public Projectil(Vector3f position, Vector3f velocity, ParticleTexture tex, Entity dest, float damage,
			float lifeTime, float particleScale, boolean blockDestroying) {
		this.position = position;
		this.velocity = velocity;
		this.blockDestroying = blockDestroying;
		if (velocity.length() > 50) {
			flyParticle = particles2;
		} else {
			flyParticle = particles;
		}
		this.dest = dest;
		this.damage = damage;
		this.flyParticle = tex;
		this.lifeTime = lifeTime;
		this.particleScale = particleScale;
		if (AudioMaster.soundEnabled && dest != null) {
			sound = new Source();
			sound.setPosition(position);
			sound.setVelocity(velocity);
			sound.setVolume(100);
			sound.play(SourcesManager.missle3);
		}
		TickManager.addTickingThing(this);
	}

	protected float llParticles = 3.7f + Meth.randomFloat(0, 0.5f);

	public Projectil(Vector3f position, Vector3f velocity, ParticleTexture tex, Entity dest, float damage,
			float lifeTime, float particleScale, float llParticles) {
		this.position = position;
		this.velocity = velocity;
		if (velocity.length() > 50) {
			flyParticle = particles2;
		} else {
			flyParticle = particles;
		}
		this.dest = dest;
		this.damage = damage;
		this.flyParticle = tex;
		this.lifeTime = lifeTime;
		this.particleScale = particleScale;
		this.llParticles = llParticles;
		if (AudioMaster.soundEnabled && dest != null) {
			sound = new Source();
			sound.setPosition(position);
			sound.setVelocity(velocity);
			sound.setVolume(10);
			sound.play(SourcesManager.missle3);
		}
		TickManager.addTickingThing(this);
	}

	public void setGravity(float fact) {
		gravityfact = fact;
	}

	@Override
	public Vector3f getPos() {
		return position;
	}

	public Entity getDest() {
		return dest;
	}

	@Override
	public float getDamage() {
		return damage;
	}

	protected boolean flare = false;

	public void setFlare() {
		flare = true;
	}

	protected int numberOfDestroyBlocks = 1;
	protected boolean boom = true;
	protected short setBlock = 0;

	public void setBoom(boolean boom) {
		this.boom = boom;
	}

	public void setNumberOfDestroyBlocks(int number) {
		numberOfDestroyBlocks = number;
	}

	@Override
	public boolean update() {
		Timegone += DisplayManager.getFrameTimeSeconds();
		velocity.y += gravityfact * Meth.GRAVITY * DisplayManager.getFrameTimeSeconds();
		change = new Vector3f(velocity);
		change.x *= DisplayManager.getFrameTimeSeconds();
		change.y *= DisplayManager.getFrameTimeSeconds();
		change.z *= DisplayManager.getFrameTimeSeconds();
		position.add(change.x, change.y, change.z);
		if (blockDestroying) {
			if (ChunkManager.getBlockID(position) != 0) {

				Intraface.deleteBlock(position);

				numberOfDestroyBlocks--;
				if (numberOfDestroyBlocks == 0) {
					SimpleConstructs.EXPLOSION(position, 10);
					removeAttatched();
					return true;
				}

			}
		} else if (setBlock != 0) {
			Vects.setCalcVect(velocity);
			Vects.calcVect.mul(DisplayManager.getFrameTimeSeconds() * 2);
			if (ChunkManager.getBlockID(Vects.calcVect.add(position)) != 0) {
				for (int i = 0; i < 3; i++) {
					if (ChunkManager.getBlockID(position.x, position.y + i, position.z) == 0) {
						Intraface.setBlock(position.x, position.y + i, position.z, setBlock);
						break;
					}
				}
				removeAttatched();
				return true;
			}
		}
		if(flare && ParticleMaster.particlesEnabled)
			ParticleMaster.addNewParticle(PTM.fireworks, Vects.addRandom(new Vector3f(position), 0.2f), 
					Vects.addRandom(new Vector3f(velocity).negate().mul(0.1f), 0.1f), 0, 2, 0, Meth.randomFloat(0.01f, 0.15f));
		if (flare && position.y > Camera.getPosition().y + desiredFlareHeight) {
			if (fireworkstack == 1) {
				Vector3f addvel = Meth.doChance(0.5f) ? Vects.NULL : velocity;
				int max = 7;
				for (int i = 0; i < max; i++) {
					WeatherController.flare(position, addvel, i * 0.1f + 0.3f, (max - i) * 10);
				}
			} else {
				int c = Meth.randomInt(5, 10);
				for (int i = 0; i < c; i++) {
					Projectil p = new Projectil(new Vector3f(position), Vects.randomVector3f(-5, 5, -5, 5, -5, 5), null,
							false);
					p.gravityfact = -0.5f;
					p.desiredFlareHeight = desiredFlareHeight + 10;
					p.setFlare();
					p.fireworkstack = 1;
				}
			}
			if (AudioMaster.soundEnabled) {
				SourcesManager.play(SourcesManager.boom, GAIN, new Vector3f(position));
			}
			removeAttatched();
			return true;
		}

		handleThings();

		if (Timegone >= lifeTime) {
			removeAttatched();
			return true;
		}
		if(ParticleMaster.particlesEnabled)
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

	@Override
	public void handleThings() {
		for (int i = 0; i < WorldObjects.getHits().size(); i++) {
			HittableThing e = WorldObjects.getHits().get(i);
			if (e.inHitbox(position) && e != dest) {
				if (e.hit(damage) && dest instanceof Player) {
					((Player)e).killed(e);
				}
				if(ParticleMaster.particlesEnabled)
				for (int i2 = 0; i2 < 30 * SC.particleMult; i2++) {
					ParticleMaster.addNewParticle(particles2, new Vector3f(position),
							Vects.randomVector3f(new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5)), 0, 2, 0,
							Meth.randomFloat(0.3f, 0.6f), 0);
				}
				removeAttatched();
				WorldObjects.removeThingFromWorld(this);
			}
		}
	}

	public void setBlock(short ID) {
		blockDestroying = false;
		setBlock = ID;
	}

	public void setFlare(float f) {
		flare = true;
		desiredFlareHeight = f;
	}

	protected float desiredFlareHeight = 100;

	public void setRandomParticleOffset(float f) {
		particleRandomOffset = f;
	}

	protected float particleChanceMult = 100, randomVel = 0, particleGravity = 0;
	protected int fireworkstack = 0;
	protected Entity attatched;

	public void setParticleChanceMult(float f) {
		particleChanceMult = f;
	}

	public void setPT(ParticleTexture pt) {
		flyParticle = pt;
	}

	public void setParticleScale(float scale) {
		particleScale = scale;
	}

	public void setParticleLifeTime(float f) {
		particleLifeTime = f;
		llParticles = 0;
	}

	public void setRandomParticleVelocity(float f) {
		randomVel = f;
	}

	public void setParticleGravity(float mult) {
		particleGravity = mult;
	}

	public Entity removeAttatched() {
		if (attatched != null) {
			EntityManager.removeEntity(attatched);
		}
		return attatched;
	}
	
	public void attatch(TexturedModel mod){
		attatch(mod, 0.5f);
	}
	
	public void attatch(TexturedModel mod, float scale) {
		attatched = new Entity(mod, 0, position, 0, 0, 0, scale, false);
	}
	
	public void attatch(short raw, short tex, float scale){
		attatched = new Entity(raw, tex, 0, position, 0, 0, 0, scale, false);
	}

}
