package gui3D;

import org.joml.*;

import shaders.ShaderProgram;

public class G3DShader extends ShaderProgram {

	private static final String vertex = "gui3D/vertex.txt", fragment = "gui3D/fragment.txt";

	private int location_pos;
	private int location_scale;
	private int location_projmat;

	public G3DShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		location_pos = super.getUniformLocation("trans");
		location_scale = super.getUniformLocation("scale");
		location_projmat = super.getUniformLocation("projectionMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
		// 2 "normal"

		super.bindFragOutput(0, "out_Color");
	}

	// private Vector2f loadVect = new Vector2f();

	public void loadPos(Vector3f pos) {
		super.loadVector(location_pos, pos);
	}

	public void loadScale(Vector2f scale) {
		// loadVect.set(scale);
		// loadVect.mul(0.014f);
		super.loadVector(location_scale, scale);
	}

	public void loadProjectionMatrix(Matrix4f mat) {
		super.loadMatrix(location_projmat, mat);
	}

}