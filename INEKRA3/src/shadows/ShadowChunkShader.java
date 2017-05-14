package shadows;

import org.joml.Matrix4f;

import shaders.ShaderProgram;

public class ShadowChunkShader extends ShaderProgram {

	private static final String vertex = "shadows/shadowChunkVertex.txt", fragment = "shadows/shadowChunkFragment.txt";

	private int location_mvp;

	public ShadowChunkShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvp = super.getUniformLocation("mvp");
	}

	public void loadMVP(Matrix4f mvp) {
		super.loadMatrix(location_mvp, mvp);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "pos");
		super.bindAttribute(1, "texCoords");
	}

}
