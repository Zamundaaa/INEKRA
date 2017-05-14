package line;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import gameStuff.TM;
import renderStuff.MasterRenderer;

public class LineRenderer {

	private static ArrayList<Line> lines = new ArrayList<Line>();
	private static LineShader ls;
	public static boolean inited;

	public static void init() {
		ls = new LineShader();
		inited = true;
	}

	public static void render(Matrix4f viewMatrix, Vector4f clipplingPlane) {
		startRendering(viewMatrix);
		ls.loadPlane(clipplingPlane);
		ls.loadMODE(MasterRenderer.TRANSITIONMODE);
		ls.loadTIME((float) (TM.inGameDays() * 0.1f));
		ls.loadDIST(MasterRenderer.TRANSITION_DISTANCE);
		// bruteforce-like, unefficient! BUT: works!
		for (int i = 0; i < lines.size(); i++) {
			Line l = lines.get(i);
			ls.loadOne(l.getX1(), l.getY1(), l.getZ1());
			// Err.err.println("X: " + l.getX1() + " Y: " + l.getY1() + " Z:
			// " + l.getZ1());
			ls.loadTwo(l.getX2(), l.getY2(), l.getZ2());
			// Err.err.println("X2: " + l.getX2() + " Y2: " + l.getY2() + "
			// Z2: " + l.getZ2());
			ls.loadColor(l.getR(), l.getG(), l.getB());
			GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);
		}

		endRendering();
	}

	private static void startRendering(Matrix4f viewMatrix) {
		ls.start();
		ls.loadViewMatrix(viewMatrix);
		GL30.glBindVertexArray(Line.getRawModel().getVaoID());
		

		// GL11.glEnable(GL11.GL_LINE_SMOOTH); soll extremst schlecht für
		// performance sein! (AMD --> Software rendering!)
		// GL11.glLineWidth(3);

	}

	private static void endRendering() {
		ls.stop();
		GL30.glBindVertexArray(0);
		
	}

	public static void logIn(Line l) {
		if (!lines.contains(l)) {
			lines.add(l);
		}
	}

	public static void logOut(Line l) {
		lines.remove(l);
	}

	public static void setProjectionMatrix(Matrix4f projectionMatrix) {
		ls.start();
		ls.loadProjectionMatrix(projectionMatrix);
		ls.stop();
	}

	public static void cleanUp() {
		ls.cleanUp();
	}

}
