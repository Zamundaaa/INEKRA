package gameStuff;

import cubyWater.Water;
import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderStuff.Loader;
import textures.ModelTexture;
import toolBox.Tools;

public class SC {

	public static String[] fontTypes = new String[] { "candara", "comicSans", "ubuntuCondensed" };
	private static int chosenFont = (int) Tools.loadLongPreference("Font", 0);

	public static TexturedModel playermod, sandmod;
	public static Loader loader;
	public static FontType font;
	public static FontType specialFont;
	public static float particleMult = 0.7f;

	public static void init() {
//		Err.err.println("-----------------Loading player model");
		playermod = getModel("person", "playerTexture");
//		Err.err.println("-------------------Loading sand model");
		sandmod = getModel("cube", "texPack/Sand");
		if (Water.side == null) {
//			Err.err.println("--------------------loading water model");
			Water.side = getModelExtraLoad("cubeside");
		}
//		Err.err.println("---------------------Loading Font1");
		font = new FontType(Loader.loadTextureForFonts(fontTypes[chosenFont]),
				SC.class.getClassLoader().getResourceAsStream("res/fonts/" + fontTypes[chosenFont] + ".fnt"));
//		Err.err.println("-----------------------Loading Font2");
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
		TextMaster.switchFontType(font, newFont);
		font = newFont;
		return fontTypes[chosenFont];
	}

	public static ModelTexture getTex(String name) {
		return Models.getTex(name);
	}

	public static RawModel getModelExtraLoad(String obj) {
		ModelData d = OBJFileLoader.loadOBJ(obj);
		return Loader.loadToVAO(d.getVertices(), d.getTextureCoords(), d.getNormals(), d.getIndices());
	}
	
	public static RawModel getRawModel(String obj){
		return Models.getData(obj);
	}

	public static TexturedModel getModel(String obj, String texture) {
		return Models.getModel(obj, texture);
	}

	public static TexturedModel getModel(String obj, String texture, float reflect, float shineDamp) {
		return Models.getModel(obj, texture, reflect, shineDamp);
	}

	public static ModelData getModelData(String obj) {
		return Models.getModelData(obj);
	}

	public static String getFontName() {
		return fontTypes[chosenFont];
	}

	/**
	 * SAVES DATA!
	 */
	public static void cleanUp() {
		Tools.setLongPreference("Font", chosenFont);
	}

}
