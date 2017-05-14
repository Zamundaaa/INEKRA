package fontRendering;

import java.util.*;

import fontMeshCreator.*;
import renderStuff.Loader;

public class TextMaster {

	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static ArrayList<FontType> fonts = new ArrayList<FontType>();
	private static FontRenderer renderer;

	public static boolean CREATED = false;

	public static void init() {
		renderer = new FontRenderer();
		CREATED = true;
	}

	/**
	 * does NOT delete the text. it just prevents them from being drawn. use
	 * with CAUTION!
	 */
	public static void clearList() {
		texts.clear();
		fonts.clear();
	}

	public static void loadTex(GUIText text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		Loader.loadToVAO(text, data.getVertexPositions(), data.getTextureCoords());
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}

	/**
	 * @param text
	 *            IMPORTANT! HAS TO BE LOADED (and hidden) BEVORE
	 */
	public static void showTex(GUIText text) {
		FontType font = text.getFont();
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		if (!textBatch.contains(text)) {
			textBatch.add(text);
		}
	}

	public static void render() {
		renderer.render(texts);
	}

	public static void cleanUp() {
		renderer.cleanUp();
		clearDeleteList();// !!!
		CREATED = false;
	}

	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		if (text != null && textBatch != null) {
			textBatch.remove(text);
			if (textBatch.isEmpty()) {
				texts.remove(text.getFont());
			}
		}

	}

	public static void clearDeleteList() {
		Set<FontType> fs = texts.keySet();
		for (FontType f : fs) {
			fonts.add(f);
		}
		while (fonts.size() > 0) {
			List<GUIText> ts = texts.get(fonts.get(fonts.size() - 1));
			while (ts.size() > 0) {
				ts.get(ts.size() - 1).cleanUp();
			}
			fonts.remove(fonts.size() - 1);
		}
	}

	public static void switchFontType(FontType OLD, FontType NEW) {
		List<GUIText> ts = texts.get(OLD);
		// List<GUIText> removed = new ArrayList<>();
		if (ts != null) {
			while (ts.size() > 0) {
				ts.get(ts.size() - 1).setFont(NEW);
			}
		}
		// for(int i = 0; i < removed.size(); i++){
		// removed.get(i).setFont(NEW);
		// removed.get(i).loadAgain();
		// }
	}
}
