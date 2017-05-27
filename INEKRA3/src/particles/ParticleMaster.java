package particles;

import java.util.*;
import java.util.Map.Entry;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import entities.Camera;
import gameStuff.Err;

public class ParticleMaster {

	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static volatile ArrayDeque<Particle> deadParticles = new ArrayDeque<Particle>();
	private static ParticleRenderer renderer;

	public static void init(Matrix4f projectionMatrix) {
		renderer = new ParticleRenderer(projectionMatrix);
	}

	public static void update() {
		try{
			Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Entry<ParticleTexture, List<Particle>> entry = mapIterator.next();
				List<Particle> list = entry.getValue();
				Iterator<Particle> iterator = list.iterator();
				while (iterator.hasNext()) {
					Particle p = iterator.next();
	//				if(p == null)continue;
					boolean stillAlive = p.update(Camera.getPosition());
					if (!stillAlive) {
						iterator.remove();
						// if (p.getClass() == BlockHitParticle.class) {
						// deadBlockHitParticles.add((BlockHitParticle) p);
						// } else {
						deadParticles.add(p);
						// }
						// NOP -= 1;
						if (list.isEmpty()) {
							mapIterator.remove();
						}
					}
				}
				if (!entry.getKey().isTransparent())
//					InsertionSort.sortHighToLow(list);
					sortHighToLow(list);
	
			}
		}catch(Exception c){
			Err.err.println("Particle updater failed again...");
			c.printStackTrace(Err.err);
		}

		// if (NOP >= ParticleRenderer.MAX_INSTANCES) {
		// Err.err.println("TOO MANY PARTICLES!!!");
		// }

	}
	
	private static void sortHighToLow(List<Particle> list){
		for(int i = 1; i < list.size(); i++){
			if(list.get(i-1).distance < list.get(i).distance){
				Particle p = list.get(i);
				list.set(i, list.get(i-1));
				list.set(i-1, p);
			}
		}
	}

	// private static long NOP = 0;

	/**
	 * @return NumberOfParticles
	 */
	public static int NOP(ParticleTexture tex) {
		List<Particle> ps = particles.get(tex);
		if (ps != null) {
			return ps.size();
		} else {
			return 0;
		}
	}

	// private static ArrayList<BlockHitParticle> deadBlockHitParticles = new
	// ArrayList<BlockHitParticle>();

	// public static BlockHitParticle addNewBlockHitParticle(ParticleTexture
	// tex, Vector3f position, Vector3f velocity,
	// float gravityEffect, float lifeLength, float rotation, float scale) {
	// BlockHitParticle ret;
	// if (NOP <= ParticleRenderer.MAX_INSTANCES) {
	// if (!deadBlockHitParticles.isEmpty()) {
	// deadBlockHitParticles.get(0).setActive(tex, position, velocity,
	// gravityEffect, lifeLength, 0, rotation,
	// scale);
	// ret = deadBlockHitParticles.get(0);
	// deadBlockHitParticles.remove(0);
	// while (deadBlockHitParticles.size() > 2000) {
	// deadBlockHitParticles.remove(deadBlockHitParticles.size() - 1);
	// }
	// } else {
	// ret = new BlockHitParticle(tex, position, velocity, gravityEffect,
	// lifeLength, rotation, scale, 0);
	// }
	// NOP += 1;
	// } else {
	// ret = null;
	// }
	// return ret;
	// }
	
//	public static Particle addNewParticleT(ParticleTexture tex, Vector3f position, Vector3f velocity,
//			float gravityEffect, float lifeLength, float rotation, float scale){
//		Particle ret;
//		if (NOP(tex) <= ParticleRenderer.MAX_INSTANCES) {
//			if (!deadParticles.isEmpty()) {
//				ret = deadParticles.pop();
//				ret.setActive(tex, position, velocity, gravityEffect, lifeLength, 0, rotation, scale);
//			} else {
//				ret = new Particle(tex, position, velocity, gravityEffect, lifeLength, rotation, scale, 0);
//			}
//			// NOP += 1;
//		} else {
//			ret = null;
//		}
//		return ret;
//	}
	
	public static Particle addNewParticle(ParticleTexture tex, Vector3f position, Vector3f velocity,
			float gravityEffect, float lifeLength, float rotation, float scale) {
		Particle ret;
		if (NOP(tex) <= ParticleRenderer.MAX_INSTANCES) {
			if (!deadParticles.isEmpty()) {
				ret = deadParticles.pop();
				ret.setActive(tex, position, velocity, gravityEffect, lifeLength, 0, rotation, scale);
//				while (deadParticles.size() > 2000) {
//					deadParticles.remove(deadParticles.size() - 1);
//				}
			} else {
				ret = new Particle(tex, position, velocity, gravityEffect, lifeLength, rotation, scale, 0);
			}
			// NOP += 1;
		} else {
			ret = null;
		}
		return ret;
	}

	public static Particle addNewParticle(ParticleTexture tex, Vector3f position, Vector3f velocity,
			float gravityEffect, float lifeLength, float rotation, float scale, float elapsedTime) {
		Particle ret;
		if (NOP(tex) <= ParticleRenderer.MAX_INSTANCES) {
			if (!deadParticles.isEmpty()) {
				ret = deadParticles.pop();
				ret.setActive(tex, position, velocity, gravityEffect, lifeLength, elapsedTime,
						rotation, scale);
//				while (deadParticles.size() > 2000) {
//					deadParticles.remove(deadParticles.size() - 1);
//				}
			} else {
				ret = new Particle(tex, position, velocity, gravityEffect, lifeLength, rotation, scale, elapsedTime);
			}
			// NOP += 1;
		} else {
			ret = null;
		}
		return ret;
	}

	public static void renderParticles(float planey, boolean upordownside) {
		renderer.render(particles, planey, upordownside);
	}

	public static void renderParticles(Matrix4f viewMat) {
		renderer.render(particles, viewMat);
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}

	public static void addParticle(Particle p) {
		if (p != null) {
			List<Particle> ps = particles.get(p.getTex());
			if (ps == null) {
				particles.put(p.getTex(), new ArrayList<Particle>());
				ps = particles.get(p.getTex());
			}
			ps.add(p);
		}
		if(!Thread.currentThread().getName().equals("main")){
			System.out.println(Thread.currentThread().getName());
		}
	}

	public static void removeParticle(Particle p) {
		if (p != null) {
			List<Particle> ps = particles.get(p.getTex());
			if (ps != null) {
				ps.remove(p);
			}
		}
	}

}
