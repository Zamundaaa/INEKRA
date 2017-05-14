package entities;

import static particles.PTM.*;

import org.joml.Vector3f;

import toolBox.Meth;

public class Sternschnuppn extends Projectil {

	private boolean meteorite = false;

	public Sternschnuppn(Vector3f position, Vector3f velocity, boolean blockDestroying) {
		super(position, velocity, null, blockDestroying);
		super.lifeTime = Meth.randomFloat(3, 6);
		super.particleLifeTime = 0.5f;
		super.llParticles = 0;
		switch (Meth.randomInt(1, 4)) {
		case 1:
			flyParticle = fire;
			break;
		case 2:
			flyParticle = projectile;
			break;
		case 3:
			flyParticle = cosmic;
			break;
		case 4:
			flyParticle = star;
			break;
		}
	}

	public void setMeteorite(boolean m) {
		meteorite = m;
		if (meteorite) {
			lifeTime = 100;
		}
	}

	float hitY;
	boolean hit;
	boolean ex;

	@Override
	public boolean update() {
		particleScale = (float) Math.sin((Timegone / lifeTime) * Meth.PI) * (Timegone / lifeTime);
		// particleScale = (Timegone/lifeTime);
		if (meteorite) {
			particleScale = 1;
			// short b = ChunkManager.getBlockID(position);
			// if(b != Block.AIR){
			// hitY = b.getY();
			// hit = true;
			// }
			// if(!ex && hit && position.y < hitY+5){
			// SimpleConstructs.EXPLOSION(position.x, position.y+1, position.z,
			// 4);
			// ex = true;
			// TickManager.removeTickingThing(this);
			// return true;
			// }
			// if(ex){
			// SimpleConstructs.buildSphere(Block.ORE, false, position, 2,
			// true);
			// return true;
			// }
		}
		return super.update();
	}

}
