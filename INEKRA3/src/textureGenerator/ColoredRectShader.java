package textureGenerator;

import org.joml.Vector4f;

import shaders.ShaderProgram;

public class ColoredRectShader extends ShaderProgram {

	private static final String vertex = "textureGenerator/quadVertex.txt",
			fragment = "textureGenerator/coloredQuadFragment.txt";

	private int location_color;

	public ColoredRectShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "pos");

		super.bindFragOutput(0, "out_Color");
	}

	public void loadColor(Vector4f color) {
		super.loadVector(location_color, color);
	}

}
