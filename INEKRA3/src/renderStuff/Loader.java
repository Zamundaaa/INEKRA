package renderStuff;

import static org.lwjgl.glfw.GLFW.glfwCreateCursor;
import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import collectionsStuff.ArrayListL;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import fontMeshCreator.GUIText;
import gameStuff.Err;
import models.RawModel;
import models.TexturedModel;
import textures.TextureData;
import toolBox.PIC;

public class Loader {

	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();

	public static final String büdlLocation = "res/büdln/";
	public static final String skyLocation = "res/büdln/sky/";

	public static void unloadVAO(int vaoID) {
		vaos.remove(Integer.valueOf(vaoID));
		GL30.glDeleteVertexArrays(vaoID);
	}

	public static void unload(RawModel mod) {
		int vaoID = mod.getVaoID();
		unloadVAO(vaoID);
		unloadVBO(mod.vboID());
		unloadVBO(mod.vboNorm());
		unloadVBO(mod.vboPos());
		unloadVBO(mod.vboTan());
		unloadVBO(mod.vboTex());
	}

	public static void unloadTex(int texID) {
		GL11.glDeleteTextures(texID);
		textures.remove(Integer.valueOf(texID));
	}

	public static void unload(TexturedModel mod) {
		unload(mod.getRawMod());
		unloadTex(mod.getTex().getID());
	}

	public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		int vboID = bindIndicesBuffer(indices);
		int posID = storeDataInAttributeList(0, 3, positions);
		int texID = storeDataInAttributeList(1, 2, textureCoords);
		int normID = storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		RawModel rawmod = new RawModel(vaoID, indices.length, vboID, posID, texID, normID);
		return rawmod;
	}
	
	public static void updateVAO(RawModel mod, float[] positions, float[] texCoords, float[] normals, int[] indices){
		GL30.glBindVertexArray(mod.getVaoID());
		updateIndicesBuffer(mod.vboID(), indices);
		updateDataInAttributeList(mod.vboPos(), 0, 3, positions);
		updateDataInAttributeList(mod.vboTex(), 1, 2, texCoords);
		updateDataInAttributeList(mod.vboNorm(), 2, 3, normals);
		mod.setVertexCount(indices.length);
		unbindVAO();
	}
	
	/**
	 * if some parameter is null then it won't update it. Uses the RawModels attributeList values! (so obviously the best Method for updating!)
	 * @param mod
	 * @param positions 3D
	 * @param texCoords 2D
	 * @param normals 3D
	 * @param tangents 3D
	 * @param indices 1D
	 */
	public static void updateVAO(RawModel mod, float[] positions, float[] texCoords, float[] normals, float[] tangents, int[] indices){
		GL30.glBindVertexArray(mod.getVaoID());
		if(indices != null){
			updateIndicesBuffer(mod.vboID(), indices);
			mod.setVertexCount(indices.length);
		}
		if(positions != null)
			updateDataInAttributeList(mod.vboPos(), mod.attributeListPositions(), 3, positions);
		if(texCoords != null)
			updateDataInAttributeList(mod.vboTex(), mod.attributeListTextureCoords(), 2, texCoords);
		if(normals != null)
			updateDataInAttributeList(mod.vboNorm(), mod.attributeListNormals(), 3, normals);
		if(tangents != null)
			updateDataInAttributeList(mod.vboTan(), mod.attributeListTangents(), 3, tangents);
		unbindVAO();
	}
	
	/**
	 * @comment saves the texCoords as 3Dimensional; additionalData is a vec4!!!
	 */
	public static RawModel loadToVAO3DTex(float[] positions, float[] textureCoords, float[] normals, int[] indices,
			float[] additionalData) {
		int vaoID = createVAO();
		int vboID = bindIndicesBufferD(indices);
		int posID = storeDataInAttributeListD(0, 3, positions);
		int texID = storeDataInAttributeListD(1, 3, textureCoords);
		int normID = storeDataInAttributeListD(2, 3, normals);
		int tanID = storeDataInAttributeListD(3, 4, additionalData);
		unbindVAO();
		RawModel rawmod = new RawModel(vaoID, indices.length, vboID, posID, texID, normID, tanID);
		return rawmod;
	}

	/**
	 * @comment only works with a RawModel from 'loadToVAO3DTex'! additionalData
	 *          is a vec4!!!
	 */
	public static void updateVAO3DTex(RawModel mod, float[] positions, float[] texCoords, float[] normals,
			int[] indices, float[] additionalData) {
		GL30.glBindVertexArray(mod.getVaoID());
		updateIndicesBuffer(mod.vboID(), indices);
		updateDataInAttributeList(mod.vboPos(), 0, 3, positions);
		updateDataInAttributeList(mod.vboTex(), 1, 3, texCoords);
		updateDataInAttributeList(mod.vboNorm(), 2, 3, normals);
		updateDataInAttributeList(mod.vboTan(), 3, 4, additionalData);
		mod.setVertexCount(indices.length);
		unbindVAO();
	}

	public static void updateVBO(int vbo, float[] data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.length * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
			int[] indices) {
		int vaoID = createVAO();
		int vboID = bindIndicesBuffer(indices);
		int pos = storeDataInAttributeList(0, 3, positions);
		int tex = storeDataInAttributeList(1, 2, textureCoords);
		int norm = storeDataInAttributeList(2, 3, normals);
		int tan = storeDataInAttributeList(3, 3, tangents);
		unbindVAO();
		return new RawModel(vaoID, indices.length, vboID, pos, tex, norm, tan);
	}

	public static void updateVAO(RawModel mod, float[] positions, float[] textureCoords, float[] normals,
			float[] tangents) {
		GL30.glBindVertexArray(mod.getVaoID());
		updateDataInAttributeList(mod.vboPos(), 0, 3, positions);
		updateDataInAttributeList(mod.vboTex(), 1, 2, textureCoords);
		updateDataInAttributeList(mod.vboNorm(), 2, 3, normals);
		updateDataInAttributeList(mod.vboTan(), 3, 3, tangents);
		unbindVAO();
	}
	
	public static void updateVAO(RawModel mod, float[] positions, float[] texCoords, int[] indices) {
		GL30.glBindVertexArray(mod.getVaoID());
		updateIndicesBuffer(mod.vboID(), indices);
		updateDataInAttributeList(mod.vboPos(), mod.attributeListPositions(), 3, positions);
		updateDataInAttributeList(mod.vboTex(), mod.attributeListTextureCoords(), 2, texCoords);
		mod.setVertexCount(indices.length);
		unbindVAO();
	}
	
//	public static void loadToVAO(float[] positions, float[] texCoords, int[] indices){
//		
//	}

	public static RawModel loadLineToVAO(float[] positions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, 3, positions);
		unbindVAO();
		return new RawModel(vaoID, 2);
	}

	public static RawModel loadToVAO(float[] positions, int[] indices) {
		int vaoID = createVAO();
		int ib = bindIndicesBuffer(indices);
		int pos = storeDataInAttributeList(0, 3, positions);
		unbindVAO();
		RawModel raw = new RawModel(vaoID, indices.length, ib, pos, 0, 0, 0);
		return raw;
	}
	
	public static RawModel loadToVAO(float[] positions, float[] normals, int[] indices) {
		int vaoID = createVAO();
		int ib = bindIndicesBuffer(indices);
		int pos = storeDataInAttributeList(0, 3, positions);
		int norm = storeDataInAttributeList(1, 3, normals);
		unbindVAO();
		RawModel raw = new RawModel(vaoID, indices.length, ib, pos, 0, norm, 0);
		raw.setAttributeListNormals(1);
		return raw;
	}
	
	public static void updateVAO(RawModel mod, float[] positions, int[] indices) {
		GL30.glBindVertexArray(mod.getVaoID());
		updateDataInAttributeList(mod.vboPos(), 0, 3, positions);
		updateIndicesBuffer(mod.vboID(), indices);
		mod.setVertexCount(indices.length);
		unbindVAO();
	}

	public static int createEmptyVBO(int floatCount) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public static void addInstancedAtribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength,
			int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL20.glEnableVertexAttribArray(attribute);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//		unbindVAO();
	}

	public static int loadToVAO(float[] positions, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return vaoID;
	}

	public static int loadCubeMap(String[] textureFiles) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile(skyLocation + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(),
					data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		textures.add(texID);
		return texID;
	}

	/**
	 * @param textureFiles
	 *            all textures have to be of the same size!!
	 * @return
	 */
	public static int loadTextureArray(String[] textureFiles, boolean[] repeatOrClampTo0) {
		int texID = GL11.glGenTextures();
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texID);
		TextureData texd = decodeTextureFile(büdlLocation + textureFiles[0] + ".png");
		ByteBuffer bb = texd.getBuffer();
		GL12.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL11.GL_RGBA, texd.getWidth(), texd.getHeight(),
				textureFiles.length, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, texd.getWidth(), texd.getHeight(), 0, GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, bb);
		for (int i = 1; i < textureFiles.length; i++) {
			texd = decodeTextureFile(büdlLocation + textureFiles[i] + ".png");
			bb = texd.getBuffer();
			GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, texd.getWidth(), texd.getHeight(), 1, GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, bb);
			// if(repeatOrClampTo0[i]){
			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
			// GL11.GL_REPEAT);
			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
			// GL11.GL_REPEAT);
			// }else{
			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
			// GL12.GL_CLAMP_TO_EDGE);
			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
			// GL12.GL_CLAMP_TO_EDGE);
			// GL11.glTexParameterfv(GL11.GL_TEXTURE_2D,
			// GL11.GL_TEXTURE_BORDER_COLOR, new float[]{0, 0, 0, 0});
			// }
			GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, GL14.GL_TEXTURE_LOD_BIAS, 0);
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4f,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY,
						EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else {
				Err.err.println("Anisotropic Filtering not supported!");
			}
		}
		// c.close();
		textures.add(texID);
		return texID;
	}

	private static TextureData decodeTextureFile(String filename) {
		int w = 0;
		int h = 0;
		ByteBuffer buffer = null;
		try {
			InputStream in = Loader.class.getClassLoader().getResourceAsStream(filename);
			if (in == null) {
				Err.err.println("In == null: NO INPUT! Path of File: " + filename);
			}
			PNGDecoder decoder = new PNGDecoder(in);
			w = decoder.getWidth();
			h = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * w * h);
			decoder.decode(buffer, w * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace(Err.err);
		}
		return new TextureData(w, h, buffer);
	}

	public static RawModel loadToVAO(float[] positions, int dimensions) {
		int vaoID = createVAO();
		int vbo = storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		RawModel mod = new RawModel(vaoID, positions.length / dimensions);
		mod.setVBO(vbo);
		return mod;
	}

	public static int loadTexture(String fileName) {
		return loadTexture(büdlLocation, fileName);
	}

	private static final String particleLocation = "res/particles/";

	public static int loadParticleTexture(String fileName) {
		return loadTexture(particleLocation, fileName);
	}

	public static MemoryStack stack = MemoryStack.stackPush();

	public static int loadTexture(String path, String fileName) {
		int tex = 0;
		try {
			tex = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
			BufferedImage buffimg = PIC.loadImage(path + fileName + ".png");
			ByteBuffer img = PIC.imgToBuffer(buffimg);

			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, buffimg.getWidth(), buffimg.getHeight(), 0, GL_RGBA,
					GL_UNSIGNED_BYTE, img);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4f,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
						amount);
			} else {
				Err.err.println("Anisotropic Filtering not supported!");
			}

		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("Tried to load texture " + path + fileName + ".png , didn't work");
			System.exit(-1);
		}
		textures.add(tex);
		return tex;
	}

	public static int loadTextureWithoutMipMappingOrLinearFiltering(String path, String fileName) {
		int tex = 0;
		try {
			tex = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
			BufferedImage buffimg = PIC.loadImage(path + fileName + ".png");

			ByteBuffer img = PIC.imgToBuffer(buffimg);

			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, buffimg.getWidth(), buffimg.getHeight(), 0, GL_RGBA,
					GL_UNSIGNED_BYTE, img);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("Tried to load texture " + path + fileName + ".png , didn't work");
			System.exit(-1);
		}
		textures.add(tex);
		return tex;
	}

	private static final String fontLocation = "res/fonts/";

	public static int loadTextureForFonts(String fileName) {
		int tex = 0;
		try {
			tex = GL11.glGenTextures();
			glBindTexture(GL_TEXTURE_2D, tex);
			BufferedImage buffimg = PIC.loadImage(fontLocation + fileName + ".png");
			PIC.flipImage(buffimg);
			ByteBuffer img = PIC.imgToBuffer(buffimg);
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, buffimg.getWidth(), buffimg.getHeight(), 0, GL_RGBA,
					GL_UNSIGNED_BYTE, img);
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("Tried to load texture " + fileName + ".png , didn't work");
			System.exit(-1);
		}
		textures.add(tex);
		return tex;
	}

	public static void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	public static void unloadVBO(int vbo) {
		vbos.remove((Integer) vbo);
		GL15.glDeleteBuffers(vbo);
	}

	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		if (vaos.contains(vaoID)) {
			new Exception("created a new VAO and it's already used ?!?").printStackTrace(Err.err);
		}
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	private static void updateDataInAttributeList(int vboID, int attributeNumber, int coordinateSize, float[] data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private static int storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL20.glEnableVertexAttribArray(attributeNumber);
		return vboID;
	}

	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	private static int bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		return vboID;
	}

	private static void updateIndicesBuffer(int vboID, int[] indices) {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		// IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_DYNAMIC_DRAW);
	}

	private static int storeDataInAttributeListD(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL20.glEnableVertexAttribArray(attributeNumber);
		return vboID;
	}

	private static int bindIndicesBufferD(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
//		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_DYNAMIC_DRAW);
		return vboID;
	}

	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static void loadToVAO(GUIText text, float[] vertexPositions, float[] textureCoords) {
		int vaoID = createVAO();
		int vpos = storeDataInAttributeList(0, 2, vertexPositions);
		int tex = storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		text.setMeshInfo(vaoID, vpos, tex, vertexPositions.length / 2);
	}

	public static void updateText(GUIText text, float[] vertexPositions, float[] texCoords) {
		GL30.glBindVertexArray(text.getMesh());
		updateDataInAttributeList(text.posVBO(), 0, 2, vertexPositions);
		updateDataInAttributeList(text.texVBO(), 1, 2, texCoords);
		unbindVAO();
		text.setMeshInfo(text.getMesh(), text.posVBO(), text.texVBO(), vertexPositions.length / 2);
	}

	public static int vaos() {
		return vaos.size();
	}

	public static int vbos() {
		return vbos.size();
	}

	public static int createEmptyCubeMap(int size) {
		int texID = glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < 6; i++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

		textures.add(texID);
		return texID;
	}

	/**
	 * applies the specified filter to the given texture (only GL_TEXTURE_2D allowed!)
	 * @param texture
	 * @param filter GL11.GL_NEAREST or GL11.GL_LINEAR or whatever
	 */
	public static void applyFilter(int texture, int filter) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	/**
	 * Applies the specified filter to the given texture (only GL_TEXTURE_2D allowed!)
	 * Also creates the required Mipmaps for rendering and applies anasotropic rendering if possible
	 * @param texture
	 * @param filter GL11.GL_NEAREST or GL11.GL_LINEAR or whatever
	 */
	public static void applyFilterAndCreateMipMaps(int texture, int filter){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			float amount = Math.min(4f,
					GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
					amount);
		} else {
			Err.err.println("Anisotropic Filtering not supported!");
		}
	}
	
	private static final String[] icons = new String[] { "16", "32", "128" };
	public static final String iconLocation = büdlLocation + "Icons/";

	public static void setWindowIcon(long window) throws IOException {
		int w, h;
		GLFWImage[] gimgs = new GLFWImage[icons.length];
		for (int i = 0; i < icons.length; i++) {
			InputStream in = Loader.class.getClassLoader().getResourceAsStream(iconLocation + icons[i] + ".png");
			PNGDecoder decoder = new PNGDecoder(in);
			w = decoder.getWidth();
			h = decoder.getHeight();
			ByteBuffer buffer = ByteBuffer.allocateDirect(4 * w * h);
			decoder.decode(buffer, w * 4, Format.RGBA);
			buffer.flip();
			gimgs[i] = GLFWImage.malloc();
			gimgs[i].set(w, h, buffer);
		}
		GLFWImage.Buffer buff = GLFWImage.malloc(icons.length);
		for (int i = 0; i < icons.length; i++) {
			buff.put(i, gimgs[i]);
		}
		GLFW.glfwSetWindowIcon(window, buff);
		for (int i = 0; i < gimgs.length; i++) {
			gimgs[i].free();
		}
		buff.free();
	}
	
	private static ArrayListL cursors = new ArrayListL();
	private static int currentCursor;
	private static final String cursorPath = "MousePointers/";
	private static final String[] cursorPaths;
	static{
		final int maxCursor = 14;
		cursorPaths = new String[maxCursor];
		for(int i = 0; i < maxCursor; i++)
			cursorPaths[i] = "mouse" + (i+1);
	}
	
	public static void loadAllCursors(long window){
		for(String p : cursorPaths)
			addWindowCursor(cursorPath + p);
	}
	
	/**
	 * @param window
	 * @param cursorPath the cursor has to be in büdlLocation and has to be in .png format!
	 */
	public static void addWindowCursor(String cursorPath){
		ByteBuffer img = PIC.loadImg(büdlLocation + cursorPath + ".png", 32, 32);
		GLFWImage i = GLFWImage.malloc();
		i.set(32, 32, img);
		long cursor = glfwCreateCursor(i, 0, 0);
		cursors.add(cursor);
	}
	
	public static void setWindowCursor(long window, long cursor){
		GLFW.glfwSetCursor(window, cursor);
	}
	
	public static void setLoadedWindowCursor(long window, int cursorIndex){
		setWindowCursor(window, cursors.get(cursorIndex));
		if(cursors.get(cursorIndex) <= 0){
			Err.err.println(cursorIndex);
		}
	}
	
	private static float timeSinceLastCursorSwitch;
	private static final float cooldown = 0.03f;
	
	public static void updateCursor(long window){
		timeSinceLastCursorSwitch += DisplayManager.getFrameTimeSeconds();
		if(timeSinceLastCursorSwitch > cooldown){
			currentCursor++;
			if(currentCursor >= cursors.size()){
				currentCursor = 0;
			}
			setLoadedWindowCursor(window, currentCursor);
			timeSinceLastCursorSwitch = 0;
		}
	}

}