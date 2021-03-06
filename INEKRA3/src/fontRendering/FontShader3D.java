package fontRendering;

import org.joml.Vector2f;
import org.joml.Vector4f;

import shaders.ShaderProgram;

public class FontShader3D extends ShaderProgram {

	private static final String VERTEX_FILE = "fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "fontRendering/fontFragment.txt";

	private int location_Color;
	private int location_translation;
	private int location_displayLevel;

	public FontShader3D() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_Color = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		location_displayLevel = super.getUniformLocation("displayLevel");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	protected void loadDisplayLevel(int dl){
		super.loadInt(location_displayLevel, dl);
	}
	
	protected void loadColor(Vector4f color) {
		super.loadVector(location_Color, color);
	}

	protected void loadTranslation(Vector2f trans) {
		super.loadVector(location_translation, trans);
	}

}
