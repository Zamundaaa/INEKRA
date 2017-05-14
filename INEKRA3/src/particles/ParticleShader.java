package particles;

import org.joml.Matrix4f;

import shaders.ShaderProgram;
import toolBox.Vects;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "particles/particleFShader.txt";

	private int location_NOR;
	private int location_projectionMatrix;
	private int location_bright;
	private int location_density;
	private int location_gradient;
	private int location_skyColor;
	private int location_colorMult;
	// private int location_TD;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_NOR = super.getUniformLocation("NOR");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_bright = super.getUniformLocation("bright");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_skyColor = super.getUniformLocation("skyColor");
		location_colorMult = super.getUniformLocation("colorMult");
		// location_TD = super.getUniformLocation("TD");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffset");
		super.bindAttribute(6, "blendFactor");
		// super.bindAttribute(7, "distanceToCam");
	}

	protected void loadDensity(float d) {
		super.loadFloat(location_density, d);
	}

	protected void loadGradient(float g) {
		super.loadFloat(location_gradient, g);
	}

	protected void loadBright(float brightness) {
		super.loadFloat(location_bright, brightness);
	}

	protected void loadNOR(float NOR) {
		super.loadFloat(location_NOR, NOR);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, Vects.setCalcVect(r, g, b));
	}

	public void loadColorMult(float particleColorMult) {
		super.loadFloat(location_colorMult, particleColorMult);
	}

	// public void loadTimeDarkening(boolean timeDarkening) {
	// super.loadBoolean(location_TD, timeDarkening);
	// }

}
