package weather;

import static particles.PTM.*;

import org.joml.Vector3f;

import data.Generator;
import gameStuff.TM;
import particles.*;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class Cloud {

	public static final float ALPHA = 0.1f;
	public final int PARTICLE_COUNT, divergance;

	private Vector3f pos, vel = new Vector3f(5, 0, 5);
	private Particle[] particles;// dynamic ParticleCount based on distance?
									// should be possible and make clouds even
									// nicer
	private boolean lightning;

	public Cloud(Vector3f pos) {
		this.pos = pos;
		lightning = Meth.doChance(WeatherMap.getChanceForThunderCloud());
		// if (lightning) {
		// PARTICLE_COUNT = 25;
		// divergance = 60;
		// } else {
		PARTICLE_COUNT = 15;
		divergance = 40;
		// }
		particles = new Particle[PARTICLE_COUNT];
		genParticles();
	}

	public void update() {
		vel.x = 5 + Generator.getG().genThing((float) (pos.x * 100 + TM.fromStartMillis() * 0.0001f));
		vel.z = 5 + Generator.getG().genThing((float) (pos.z * 100 + TM.fromStartMillis() * 0.0001f));
		vel.x *= TM.TIMEFACT;
		vel.z *= TM.TIMEFACT;
		boolean remove = true;
		for (int i = 0; i < particles.length; i++) {
			if (particles[i].isAlive()) {
				remove = false;
				break;
			}
		}
		if (remove) {
			WeatherMap.remove(this);
			// Out.println("removed cloud again...");
		} else {
			if (WeatherController.isRaining()) {
				if (!rainy && Meth.doChance(10 * DisplayManager.getFrameTimeSeconds())) {
					rainy = true;
					int i = Meth.randomInt(0, particles.length - 1);
					if (particles[i].getTex() != cloudy3) {
						Vector3f v = new Vector3f(particles[i].getPosition());
						float rot = particles[i].getRotation();
						float lt = particles[i].remainingLifeTime();
						do {
							particles[i] = ParticleMaster.addNewParticle(cloudy3, v, vel, 0, lt, 0, rot);
						} while (particles[i] == null);
						// System.out.println("ParticleTransformation!1");
					}
					for (i = 0; i < particles.length; i++) {
						if (particles[i].getTex() != cloudy3) {
							rainy = false;
							break;
						}
					}
				}
				if (lightning && Meth.doChance(0.05f * DisplayManager.getFrameTimeSeconds())) {
					WeatherController.lstrike(pos.x, pos.z);
				}
				float vx = 5;// Meth.randomFloat(-3, 3) + number1
				float vz = 4;// Meth.randomFloat(-3, 3) + number2
				// for (int i2 = 0; i2 < WeatherController.PARTICLEMULT; i2++) {
				// Particle rain = ParticleMaster.addNewParticle(raindrop,
				// new Vector3f(Meth.randomFloat(-divergance, divergance),
				// Meth.randomFloat(-10, 10),
				// Meth.randomFloat(-divergance, divergance)).add(pos),
				// new Vector3f(vx, Meth.randomFloat(WeatherController.maxVel,
				// WeatherController.minVel), vz),
				// WeatherController.rainGravity, 6, 0, 1);
				// if (rain != null) {
				// rain.setWindFactor(1);
				// }
				// }
				int i = Meth.randomInt(0, particles.length - 1);
				if (particles[i].isAlive() && Meth.doChance(2000.0f / Math.max(ParticleMaster.NOP(raindrop), 1))) {
					float lifeLength = 7;
					Particle rain = ParticleMaster.addNewParticle(WeatherController.isSnowing() ? snowflake : raindrop,
							Vects.addRandom(
									new Vector3f(Meth.randomFloat(-divergance, divergance), Meth.randomFloat(-10, 10),
											Meth.randomFloat(-divergance, divergance)).add(particles[i].getPosition()),
									particles[i].getScale()),
							new Vector3f(vx, Meth.randomFloat(WeatherController.maxVel, WeatherController.minVel), vz),
							WeatherController.rainGravity, lifeLength, 0, 1);
					// if(doBlockLookUp && rain != null){
					// int y =
					// ChunkManager.getUppestBlockY((int)rain.getPosition().x,
					// (int) rain.getPosition().z);
					// if(y > Integer.MIN_VALUE){
					// lifeLength = -2*rain.getVelocity().y/Meth.GRAVITY;
					// }
					// }
					if (rain != null) {
						rain.setWindFactor(1);
					}
				}
			} else if (rainy && Meth.doChance(10 * DisplayManager.getFrameTimeSeconds())) {
				rainy = false;
				int i = Meth.randomInt(0, particles.length - 1);
				if (particles[i].getTex() == cloudy3) {
					Vector3f v = new Vector3f(particles[i].getPosition());
					float rot = particles[i].getRotation();
					float lt = particles[i].remainingLifeTime();
					do {
						particles[i] = ParticleMaster.addNewParticle(Meth.doChance(0.5f) ? cloudy : cloudy2, v, vel, 0,
								lt, 0, rot);
					} while (particles[i] == null);
					// System.out.println("ParticleTransformation!2");
				}
				for (i = 0; i < particles.length; i++) {
					if (particles[i].getTex() == cloudy3) {
						rainy = true;
						break;
					}
				}
			}
		}
	}

	private boolean rainy = false;

	// private static boolean doBlockLookUp = true;

	private void genParticles() {
		ParticleTexture ptex = rainy ? cloudy3 : (Meth.doChance(0.5f) ? cloudy : cloudy2);// lightning
																							// ?
																							// cloudy3
																							// :
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			Vector3f v = Vects.addRandom(new Vector3f(pos), -divergance, divergance, -10, 10, -divergance, divergance);
			particles[i] = ParticleMaster.addNewParticle(ptex, v, vel, 0, 240, 0, Meth.randomFloat(5, 30));
			while (particles[i] == null) {
				particles[i] = ParticleMaster.addNewParticle(ptex, v, vel, 0, 240, 0, Meth.randomFloat(5, 30));
			}
		}
	}

	public Vector3f getPos() {
		return pos;
	}

}
