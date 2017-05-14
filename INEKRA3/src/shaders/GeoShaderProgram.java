package shaders;

import java.io.*;
import java.nio.FloatBuffer;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import gameStuff.Err;

public abstract class GeoShaderProgram {

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int geoShaderID;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public GeoShaderProgram(String vertexFile, String fragmentFile, String geoFile) {
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		geoShaderID = loadShader(geoFile, GL32.GL_GEOMETRY_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		GL20.glAttachShader(programID, geoShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}

	protected abstract void getAllUniformLocations();

	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}

	protected void bindFragOutput(int attachment, String variableName) {
		GL30.glBindFragDataLocation(programID, attachment, variableName);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.set(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}

	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ShaderProgram.class.getClassLoader().getResourceAsStream(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(Err.err);
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Err.err.println(GL20.glGetShaderInfoLog(shaderID, 500));
			Err.err.println("Could not compile shader!");
			System.exit(-1);
		}
		return shaderID;
	}

}
