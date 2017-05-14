package particles;

public class ParticleTexture {

	private int textureID;
	private int NOR;
	private boolean transparent = false, td = false;
	private float brightness = 0;

	public int getTextureID() {
		return textureID;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparency(boolean bool) {
		transparent = bool;
	}

	public int getNOR() {
		return NOR;
	}

	public ParticleTexture(int textureID, int nOR) {
		this.textureID = textureID;
		NOR = nOR;
	}

	public ParticleTexture(int textureID, int nOR, boolean Transparent) {
		this.textureID = textureID;
		this.transparent = Transparent;
		NOR = nOR;
	}

	public void setTimeDarkening(boolean b) {
		td = b;
	}

	public boolean timeAndWeatherDarkening() {
		return td;
	}

	public void setBright(float brightness) {
		this.brightness = brightness;
	}

	public float brightness() {
		return brightness;
	}

}
