package gameStuff;

import static entities.graphicsParts.RawMods.cube;
import static entities.graphicsParts.RawMods.person;
import static entities.graphicsParts.Texes.playerTex;
import static entities.graphicsParts.Texes.sand;

import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderStuff.Loader;
import toolBox.Tools;

public class SC {

	public static String[] fontTypes = new String[] { "candara", "comicSans", "ubuntuCondensed" };
	private static int chosenFont = (int) Tools.loadLongPreference("Font", 0);

	public static TexturedModel playermod, sandmod;
	public static FontType font;
	public static FontType specialFont;
	public static float particleMult = 0.7f;

	public static void init() {
		playermod = getModel(person, playerTex);
		sandmod = getModel(cube, sand);
		
		font = new FontType(Loader.loadTextureForFonts(fontTypes[chosenFont]),
				SC.class.getClassLoader().getResourceAsStream("res/fonts/" + fontTypes[chosenFont] + ".fnt"));
		
		specialFont = new FontType(Loader.loadTextureForFonts("segoe"),
				SC.class.getClassLoader().getResourceAsStream("res/fonts/segoe.fnt"));
	}

	public static String swapFont() {
		chosenFont++;
		if (chosenFont >= fontTypes.length) {
			chosenFont = 0;
		}
		font.cleanUp();
		FontType newFont = new FontType(Loader.loadTextureForFonts(fontTypes[chosenFont]),
				SC.class.getClassLoader().getResourceAsStream("res/fonts/" + fontTypes[chosenFont] + ".fnt"));
		swappingFont = true;
		TextMaster.switchFontType(font, newFont);
		font = newFont;
		swappingFont = false;
		return fontTypes[chosenFont];
	}
	
	public static boolean swappingFont;

//	public static ModelTexture getTex(String name) {
//		return Models.getTex(name);
//	}

	public static RawModel getModelExtraLoad(String obj) {
		ModelData d = OBJFileLoader.loadOBJ(obj);
		return Loader.loadToVAO(d.getVertices(), d.getTextureCoords(), d.getNormals(), d.getIndices());
	}
	
//	public static RawModel getRawModel(String obj){
//		return Models.getData(obj);
//	}
//
//	public static TexturedModel getModel(String obj, String texture) {
//		return Models.getModel(obj, texture);
//	}
//
//	public static TexturedModel getModel(String obj, String texture, float reflect, float shineDamp) {
//		return Models.getModel(obj, texture, reflect, shineDamp);
//	}
//
//	public static ModelData getModelData(String obj) {
//		return Models.getModelData(obj);
//	}

	public static String getFontName() {
		return fontTypes[chosenFont];
	}

	/**
	 * SAVES DATA!
	 */
	public static void cleanUp() {
		Tools.setLongPreference("Font", chosenFont);
	}

	public static TexturedModel getModel(short modelID, short texID) {
		return Models.getModel(modelID, texID);
	}

}
