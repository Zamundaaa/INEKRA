package postProcessing;

import shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "postProcessing/contrastVertex.txt";
	private static final String FRAGMENT_FILE = "postProcessing/contrastFragment.txt";

	private int location_blue;

	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_blue = super.getUniformLocation("blue");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");

	}

	public void loadBlue(boolean headunderwater) {
		super.loadBoolean(location_blue, headunderwater);
	}

}
