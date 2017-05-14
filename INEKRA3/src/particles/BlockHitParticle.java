package particles;

import org.joml.Vector3f;

import renderStuff.DisplayManager;
import toolBox.Meth;

/**
 * @author xaver
 * @deprecated
 */
public class BlockHitParticle extends Particle {

	public BlockHitParticle(ParticleTexture tex, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeLength, float rotation, float scale) {
		super(tex, position, velocity, gravityEffect, lifeLength, rotation, scale);
		if (F) {
			first = true;
			F = false;
		}
	}

	public BlockHitParticle(ParticleTexture tex, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeLength, float rotation, float scale, float elapsedTime) {
		super(tex, position, velocity, gravityEffect, lifeLength, rotation, scale, elapsedTime);
		if (F) {
			first = true;
			F = false;
		}
	}

	private static boolean F = true;
	private boolean first = false;

	@Override
	protected boolean update(Vector3f camPos) {
		velocity.y += Meth.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		// if(windfactor != 0){
		// velocity.x = WeatherController.getWindX(position.x, position.z);
		// velocity.z = WeatherController.getWindZ(position.x, position.z);
		// }
		// Block b = ChunkManager.getBlock(position);
		// if (b != null && !b.isPassable()) {
		// velocity.x = 0;
		// velocity.y = 0;
		// velocity.z = 0;
		// if (autodelete) {
		// elapsedTime = lifeLength;
		// return false;
		// } else {
		// elapsedTime += 10 * DisplayManager.getFrameTimeSeconds();
		// }
		// // if(first){
		// // Err.err.println("BDA!");
		// // }
		// }
		changeVectReusable.set(velocity);
		// changeVectReusable.scale(DisplayManager.getFrameTimeSeconds());
		// Vector3f.add(changeVectReusable, position, position);

		super.updateTextureCoordInfo();
		// distance = Vector3f.sub(cam.getPosition(), position,
		// null).lengthSquared();
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		alive = elapsedTime < lifeLength;
		// if(!alive){
		// Err.err.println("GONE!");
		// }
		return alive;
	}

	public boolean first() {
		return first;
	}

	// private boolean autodelete = true;

	// public void setAutoDeletion(boolean b) {
	// autodelete = b;
	// }

}
