package textureGenerator;

import shaders.ShaderProgram;

public class FlipShader extends ShaderProgram {

	private static final String vertex = "textureGenerator/FlipQuadVertex.txt",
			fragment = "textureGenerator/QuadFragment.txt";

	private int location_tex;

	public FlipShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		location_tex = super.getUniformLocation("texture2D");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "pos");

		super.bindFragOutput(0, "out_Color");
	}

	/**
	 * @param texNumber 0 for TEXTURE_0, 1 for TEXTURE_1 usw...
	 */
	public void loadTexture(int texNumber) {
		super.loadInt(location_tex, texNumber);
	}

}
