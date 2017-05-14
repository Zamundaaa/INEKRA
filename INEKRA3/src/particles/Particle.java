package particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Vects;

public class Particle {

	protected Vector3f position;
	protected Vector3f velocity;
	protected float gravityEffect;
	protected float lifeLength;
	protected float rotation;
	protected float scale;

	protected ParticleTexture tex;

	protected Vector2f texOffSet1 = new Vector2f();
	protected Vector2f texOffSet2 = new Vector2f();
	protected float blend;

	protected float elapsedTime = 0;
	protected float distance;

	protected Vector3f changeVectReusable = new Vector3f();

	protected boolean alive = true;
	protected float windfactor = 0;

	public Particle(ParticleTexture tex, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength,
			float rotation, float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		ParticleMaster.addParticle(this);
	}

	public Particle(ParticleTexture tex, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength,
			float rotation, float scale, float elapsedTime) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		this.elapsedTime = elapsedTime;
		ParticleMaster.addParticle(this);
	}

	public void setWindFactor(float fact) {
		windfactor = fact;
	}

	public void updateElapsedTime() {
		elapsedTime -= DisplayManager.getFrameTimeSeconds();
	}

	public float getBlend() {
		return blend;
	}

	public Vector2f getTexOffSet1() {
		return texOffSet1;
	}

	public Vector2f getTexOffSet2() {
		return texOffSet2;
	}

	public ParticleTexture getTex() {
		return tex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		if (!enlargeEffect) {
			return scale;
		} else {
			if (elapsedTime < lifeLength * 0.5f) {
				return scale * (0.5f + (elapsedTime / lifeLength));
			} else {
				return scale * (0.5f - (elapsedTime / lifeLength));
			}
		}
	}

	public float getDistance() {
		return distance;
	}

	public boolean isAlive() {
		return alive;
	}

	protected boolean update(Vector3f camPos) {
		if (velocity != Vects.NULL) {
			velocity.y += Meth.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
			// if(windfactor != 0){
			// velocity.x = WeatherController.getWindX(position.x, position.z);
			// velocity.z = WeatherController.getWindZ(position.x, position.z);
			// }
			changeVectReusable.set(velocity);
			// changeVectReusable.scale(DisplayManager.getFrameTimeSeconds());
			// changeVectReusable.normalize();
			changeVectReusable.mul(DisplayManager.getFrameTimeSeconds());
			position.add(changeVectReusable);
		}
		updateTextureCoordInfo();
		// distance = Vector3f.sub(cam.getPosition(), position,
		// null).lengthSquared();
		distance = camPos.distanceSquared(position);
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		alive = elapsedTime < lifeLength;
		return alive;
	}

	public void setActive(ParticleTexture tex, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeTime, float rotation, float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeTime;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		elapsedTime = 0;
		windfactor = 0;
		alive = true;
		ParticleMaster.addParticle(this);
	}

	public void setActive(ParticleTexture tex, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeTime, float elapsedTime, float rotation, float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeTime;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		this.elapsedTime = elapsedTime;
		windfactor = 0;
		alive = true;
		ParticleMaster.addParticle(this);
	}

	protected void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		float stageCount = tex.getNOR() * tex.getNOR();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		setTexOffset(texOffSet1, index1);
		setTexOffset(texOffSet2, index2);
	}

	private void setTexOffset(Vector2f offset, int index) {
		int column = index % tex.getNOR();
		int row = index / tex.getNOR();
		offset.x = (float) column / tex.getNOR();
		offset.y = (float) row / tex.getNOR();
	}

	public void setPosition(Vector3f pos) {
		this.position = pos;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void hide() {
		elapsedTime = lifeLength + 1;
	}

	public float remainingLifeTime() {
		return lifeLength - elapsedTime;
	}

	public void enlargeEffect() {
		enlargeEffect = true;
	}

	private boolean enlargeEffect = false;

}
