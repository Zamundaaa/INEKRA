package textures;

public class ModelTexture {

	private int textureID, normalMap, specularMap;

	private float shineDamper = 1, reflectivity = 0;

	private boolean hasTransparency = false, useFakeLightning = false, hasSpecularMap = false;

	private int NOR = 1;

	public void setSpecularMap(int specMap) {
		this.specularMap = specMap;
		this.hasSpecularMap = true;
	}

	public int getSpecularMap() {
		return specularMap;
	}

	public boolean hasSpecularMap() {
		return hasSpecularMap;
	}

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public int getNOR() {
		return NOR;
	}

	public void setNOR(int nOR) {
		NOR = nOR;
	}

	public boolean isUseFakeLightning() {
		return useFakeLightning;
	}

	public void setUseFakeLightning(boolean useFakeLightning) {
		this.useFakeLightning = useFakeLightning;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public ModelTexture(int id) {
		textureID = id;
	}

	public int getID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setShineDamper(float damp) {
		shineDamper = damp;
	}

	public void setReflectivity(float reflect) {
		reflectivity = reflect;
	}

	/**
	 * USE WITH CAUTION!!!
	 * 
	 * @param tex
	 */
	public void setTex(int tex) {
		textureID = tex;
	}
}
