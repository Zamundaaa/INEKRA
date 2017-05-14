package particles;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import gameStuff.TM;
import models.RawModel;
import renderStuff.Loader;
import renderStuff.MasterRenderer;
import toolBox.Meth;
import toolBox.Vects;
import weather.WeatherController;

public class ParticleRenderer {

	// public static final boolean FI = false;
	private static final float[] VERTICES = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	public static final int MAX_INSTANCES = 20000, INSTANCE_DATA_LENGTH = 21;

	// private static final FloatBuffer buffer =
	// BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

	private RawModel quad;
	private static ParticleShader shader;
	public static boolean inited;

	private int vbo;
	private int pointer = 0;

	protected ParticleRenderer(Matrix4f projectionMatrix) {
		this.vbo = Loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		quad = Loader.loadToVAO(VERTICES, 2);
		Loader.addInstancedAtribute(quad.getVaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
		Loader.addInstancedAtribute(quad.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
		Loader.addInstancedAtribute(quad.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
		Loader.addInstancedAtribute(quad.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
		Loader.addInstancedAtribute(quad.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
		Loader.addInstancedAtribute(quad.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);

		shader = new ParticleShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		inited = true;
	}

	protected void render(Map<ParticleTexture, List<Particle>> particles, float planey, boolean upordownside) {
		Matrix4f viewMatrix = Meth.createViewMatrix();
		// int gesC = 0;
		prepare();
		for (ParticleTexture tex : particles.keySet()) {
			bindTexture(tex);
			List<Particle> particleList = particles.get(tex);
			pointer = 0;
			float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];
			if (tex.isTransparent()) {
				glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			} else {
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			}

			// if (tex == PTM.lightning || tex == PTM.cloudy) {
			// shader.loadBright(new Vector4f(0.9f, 0.9f, 0.9f, 1.0f));
			// } else if (tex == PTM.fire) {
			// shader.loadBright(new Vector4f(0.7f, 0.5f, 0.5f, 1.0f));
			// } else {
			// shader.loadBright(new Vector4f(0, 0, 0, 1.0f));
			// }

			for (Particle p : particles.get(tex)) {
				if ((upordownside ? p.getPosition().y >= planey : p.getPosition().y <= planey)) {// &&
																									// (!FI
																									// ||
																									// MasterRenderer.FI.testPoint(p.getPosition()))
					updateModelViewMatrix(p.getPosition(), p.getRotation(), p.getScale(), viewMatrix, vboData);
					updateTexCoordInfo(p, vboData);
				}
			}
			Loader.updateVBO(vbo, vboData);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particleList.size());
		}
		finishRendering();
	}

	protected void render(Map<ParticleTexture, List<Particle>> particles, Matrix4f vm) {
		prepare();
		for (ParticleTexture tex : particles.keySet()) {
			bindTexture(tex);
			List<Particle> particleList = particles.get(tex);
			pointer = 0;
			int partics = particleList.size();
			float[] vboData = new float[partics * INSTANCE_DATA_LENGTH];
			if (tex.isTransparent()) {
				glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			} else {
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			}
			int count = 0;
			for (int i = 0; i < particleList.size() && i < partics && count < MAX_INSTANCES; i++) {
				Particle p = particles.get(tex).get(i);
				count++;
				updateModelViewMatrix(p.getPosition(), p.getRotation(), p.getScale(), vm, vboData);
				updateTexCoordInfo(p, vboData);
			}
			Loader.updateVBO(vbo, vboData);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particleList.size());
		}
		finishRendering();
	}

	public void bindTexture(ParticleTexture tex) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, tex.getTextureID());
		shader.loadNOR(tex.getNOR());
		if (tex.timeAndWeatherDarkening()) {
			shader.loadColorMult(
					((1-WeatherController.blendFactor())*(1/0.7f))*TM.particleColorMult() * TM.particleColorMult() * TM.particleColorMult() * 0.75f + 0.25f);
		} else {
			shader.loadColorMult(1);
		}
		shader.loadBright(tex.brightness());
	}

	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f modelView = new Matrix4f();

	public void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix,
			float[] vboData) {
		modelMatrix.identity();
		modelMatrix.translate(position);
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.rotate(rotation * Meth.angToRad, Vects.ZP);
		modelMatrix.scale(scale);
		storeMatrixData(viewMatrix.mul(modelMatrix, modelView), vboData);
	}

	private void updateTexCoordInfo(Particle p, float[] data) {
		data[pointer++] = p.getTexOffSet1().x;
		data[pointer++] = p.getTexOffSet1().y;
		data[pointer++] = p.getTexOffSet2().x;
		data[pointer++] = p.getTexOffSet2().y;
		data[pointer++] = p.getBlend();
	}

	private void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00();
		vboData[pointer++] = matrix.m01();
		vboData[pointer++] = matrix.m02();
		vboData[pointer++] = matrix.m03();
		vboData[pointer++] = matrix.m10();
		vboData[pointer++] = matrix.m11();
		vboData[pointer++] = matrix.m12();
		vboData[pointer++] = matrix.m13();
		vboData[pointer++] = matrix.m20();
		vboData[pointer++] = matrix.m21();
		vboData[pointer++] = matrix.m22();
		vboData[pointer++] = matrix.m23();
		vboData[pointer++] = matrix.m30();
		vboData[pointer++] = matrix.m31();
		vboData[pointer++] = matrix.m32();
		vboData[pointer++] = matrix.m33();
	}

	protected void cleanUp() {
		shader.cleanUp();
	}

	private void prepare() {
		shader.start();
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		glEnableVertexAttribArray(5);
		glEnableVertexAttribArray(6);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glDepthMask(false);
		shader.loadDensity(WeatherController.getFogDensity());// !!!
		shader.loadGradient(WeatherController.getFogGradient());
		shader.loadSkyColor(MasterRenderer.r, MasterRenderer.g, MasterRenderer.b);
	}

	private void finishRendering() {
		glDepthMask(true);
		glDisable(GL_BLEND);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		glDisableVertexAttribArray(5);
		glDisableVertexAttribArray(6);
		glBindVertexArray(0);
		shader.stop();
	}

	public static void setProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

}
