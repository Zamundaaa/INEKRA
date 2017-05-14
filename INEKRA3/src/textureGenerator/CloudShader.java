package textureGenerator;

import shaders.ShaderProgram;

public class CloudShader extends ShaderProgram {

	private static final String vertex = "textureGenerator/cloudVertex.txt",
			fragment = "textureGenerator/cloudFragment.txt";

	private int location_seed;
	private int location_color;
	private int location_alpha;

	public CloudShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		location_seed = super.getUniformLocation("seed");
		location_color = super.getUniformLocation("color");
		location_alpha = super.getUniformLocation("alpha");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "pos");

		super.bindFragOutput(0, "out_Color");
	}

	public void loadSeed(short value) {
		super.loadInt(location_seed, value);
	}

	public void loadColor(float color) {
		super.loadFloat(location_color, color);
	}

	public void loadAlpha(float alpha) {
		super.loadFloat(location_alpha, alpha);
	}

}
