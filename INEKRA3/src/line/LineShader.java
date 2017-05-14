package line;

import org.joml.*;

import renderStuff.MasterRenderer;
import shaders.ShaderProgram;
import toolBox.Vects;

public class LineShader extends ShaderProgram {

	private static final String vertex = "line/lineVertex.txt";
	private static final String fragment = "line/lineFragment.txt";

	public LineShader() {
		super(vertex, fragment);
		start();
		loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		stop();
	}

	private int location_one;
	private int location_two;
	private int location_color;
	private int location_viewMatrix;
	private int location_ProjectionMatrix;
	private int location_TIME;
	private int location_MODE;
	private int location_DIST;
	private int location_Plane;

	@Override
	protected void getAllUniformLocations() {
		location_one = super.getUniformLocation("one");
		location_two = super.getUniformLocation("two");
		location_color = super.getUniformLocation("color");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_ProjectionMatrix = super.getUniformLocation("projectionMatrix");
		location_TIME = super.getUniformLocation("TIME");
		location_MODE = super.getUniformLocation("MODE");
		location_DIST = super.getUniformLocation("DIST");
		location_Plane = super.getUniformLocation("plane");
	}

	@Override
	protected void bindAttributes() {
		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");

		super.bindAttribute(0, "position");
	}

	public void loadPlane(Vector4f plane) {
		super.loadVector(location_Plane, plane);
	}

	public void loadDIST(float DIST) {
		super.loadFloat(location_DIST, DIST);
	}

	public void loadTIME(float TIME) {
		super.loadFloat(location_TIME, TIME);
	}

	public void loadMODE(int MODE) {
		super.loadInt(location_MODE, MODE);
	}

	public void loadViewMatrix(Matrix4f vm) {
		super.loadMatrix(location_viewMatrix, vm);
	}

	public void loadProjectionMatrix(Matrix4f m) {
		super.loadMatrix(location_ProjectionMatrix, m);
	}

	public void loadOne(float x1, float y1, float z1) {
		Vector3f vect = Vects.calcVect;
		vect.x = x1;
		vect.y = y1;
		vect.z = z1;
		super.loadVector(location_one, vect);
		Vects.setCalcVect(Vects.NULL);
	}

	public void loadTwo(float x2, float y2, float z2) {
		Vector3f vect = Vects.calcVect;
		vect.x = x2;
		vect.y = y2;
		vect.z = z2;
		super.loadVector(location_two, vect);
		Vects.setCalcVect(Vects.NULL);
	}

	public void loadColor(float r, float g, float b) {
		Vector3f vect = Vects.calcVect;
		vect.x = r;
		vect.y = g;
		vect.z = b;
		super.loadVector(location_color, vect);
		Vects.setCalcVect(Vects.NULL);
	}

}
