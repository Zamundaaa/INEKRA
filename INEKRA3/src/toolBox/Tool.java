package toolBox;

import org.joml.Vector3f;

import particles.ParticleTexture;

public class Tool {

	/**
	 * Klasse nur zum überschreiben. Für einfacher einstellbare Zufallssachen
	 */
	public Tool() {

	}

	/**
	 * @return selfexplaining
	 */
	public Vector3f returnCustomVect() {
		return new Vector3f();
	}

	/**
	 * @return selfexplaining
	 */
	public float returnCustomFloat() {
		return 0;
	}

	public ParticleTexture returnCustomParticleTex() {
		return null;
	}

}
