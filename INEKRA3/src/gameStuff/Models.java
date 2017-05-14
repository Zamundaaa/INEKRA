package gameStuff;

import java.util.ArrayList;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderStuff.Loader;
import textures.ModelTexture;

public class Models {

	private static ArrayList<TexturedModel> mods = new ArrayList<TexturedModel>();
	private static ArrayList<String> mod = new ArrayList<String>();
	private static ArrayList<ModelTexture> texts = new ArrayList<ModelTexture>();
	private static ArrayList<String> text = new ArrayList<String>();
	private static ArrayList<RawModel> datas = new ArrayList<RawModel>();
	private static ArrayList<String> data = new ArrayList<String>();
	private static ArrayList<ModelData> datamods = new ArrayList<ModelData>();
	private static ArrayList<String> datamodns = new ArrayList<String>();

	public static TexturedModel getModel(String obj, String texture, int texIndex) {
		TexturedModel ThingModel = getContainedModel(obj, texture);
		if (ThingModel == null) {
			RawModel model = getData(obj);
			ModelTexture modeltex = getTex(texture);
			ThingModel = new TexturedModel(model, modeltex);
			mod.add(new String(obj + texture));
			mods.add(ThingModel);
		}
		return ThingModel;
	}

	public static TexturedModel getContainedModel(String obj, String texture) {
		TexturedModel ret = null;
		String sup = obj + texture;
		for (int i = 0; i < mod.size(); i++) {
			if (mod.get(i).equals(sup)) {
				ret = mods.get(i);
				break;
			}
		}
		return ret;
	}

	public static ModelTexture getTex(String texture) {
		ModelTexture modeltex = null;
		boolean dat = false;
		for (int i = 0; i < text.size(); i++) {
			if (text.get(i).equals(texture)) {
				dat = true;
				modeltex = texts.get(i);
				break;
			}
		}
		if (!dat) {
			modeltex = new ModelTexture(Loader.loadTexture(texture));
			text.add(texture);
			texts.add(modeltex);
			modeltex.setReflectivity(0.1f);
			modeltex.setShineDamper(30);
		}
		return modeltex;
	}

	public static RawModel getData(String obj) {
		RawModel modelthing = null;
		boolean dat = false;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).equals(obj)) {
				dat = true;
				modelthing = datas.get(i);
				break;
			}
		}
		if (!dat) {
			ModelData d = getModelData(obj);
			modelthing = Loader.loadToVAO(d.getVertices(), d.getTextureCoords(), d.getNormals(), d.getIndices());
			// modelthing.saveThings(d);
			data.add(obj);
			datas.add(modelthing);
		}
		return modelthing;
	}

	public static TexturedModel getModel(String obj, String texture) {
		TexturedModel ThingModel = getContainedModel(obj, texture);
		if (ThingModel == null) {
			RawModel model = getData(obj);
			ModelTexture modeltex = getTex(texture);
			ThingModel = new TexturedModel(model, modeltex);
			mod.add(new String(obj + texture));
			mods.add(ThingModel);
		}
		return ThingModel;
	}

	public static TexturedModel getModel(String obj, String texture, float rflect, float shineDamp) {
		TexturedModel ThingModel = new TexturedModel(getData(obj), getTex(texture));
		ThingModel.getTex().setReflectivity(rflect);
		ThingModel.getTex().setShineDamper(shineDamp);
		return ThingModel;
	}

	public static ModelData getModelData(String obj) {
		for (int i = 0; i < datamodns.size(); i++) {
			if (datamodns.get(i).equals(obj)) {
				return datamods.get(i);
			}
		}
		ModelData d = OBJFileLoader.loadOBJ(obj);
		datamodns.add(obj);
		datamods.add(d);
		return d;
	}
}
