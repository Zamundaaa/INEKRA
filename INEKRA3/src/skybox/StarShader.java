package skybox;

import shaders.ShaderProgram;

public class StarShader extends ShaderProgram {

	private static final String vertex = "skybox/starVertex.txt", fragment = "skybox/starFragment.txt";

	private int location_seed;

	public StarShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		location_seed = super.getUniformLocation("seed");
	}

	@Override
	protected void bindAttributes() {
		// super.bindFragOutput(0, "out_Color");

		super.bindAttribute(0, "pos");
	}

	public void loadSeed(float seed) {
		super.loadFloat(location_seed, seed);
	}

}
