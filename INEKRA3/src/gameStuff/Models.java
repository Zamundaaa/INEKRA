package gameStuff;

import java.util.*;

import entities.graphicsParts.RawMods;
import entities.graphicsParts.Texes;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderStuff.Loader;
import textures.ModelTexture;

public class Models {

//	private static ArrayList<TexturedModel> mods = new ArrayList<TexturedModel>();
//	private static ArrayList<String> mod = new ArrayList<String>();
//	private static ArrayList<ModelTexture> texts = new ArrayList<ModelTexture>();
//	private static ArrayList<String> text = new ArrayList<String>();
//	private static ArrayList<RawModel> datas = new ArrayList<RawModel>();
//	private static ArrayList<String> data = new ArrayList<String>();
//	private static ArrayList<ModelData> datamods = new ArrayList<ModelData>();
//	private static ArrayList<String> datamodns = new ArrayList<String>();
//
//	public static TexturedModel getModel(String obj, String texture, int texIndex) {
//		TexturedModel ThingModel = getContainedModel(obj, texture);
//		if (ThingModel == null) {
//			RawModel model = getData(obj);
//			ModelTexture modeltex = getTex(texture);
//			ThingModel = new TexturedModel(model, modeltex);
//			mod.add(new String(obj + texture));
//			mods.add(ThingModel);
//		}
//		return ThingModel;
//	}
//
//	public static TexturedModel getContainedModel(String obj, String texture) {
//		TexturedModel ret = null;
//		String sup = obj + texture;
//		for (int i = 0; i < mod.size(); i++) {
//			if (mod.get(i).equals(sup)) {
//				ret = mods.get(i);
//				break;
//			}
//		}
//		return ret;
//	}
//
//	public static ModelTexture getTex(String texture) {
//		ModelTexture modeltex = null;
//		boolean dat = false;
//		for (int i = 0; i < text.size(); i++) {
//			if (text.get(i).equals(texture)) {
//				dat = true;
//				modeltex = texts.get(i);
//				break;
//			}
//		}
//		if (!dat) {
//			modeltex = new ModelTexture(Loader.loadTexture(texture));
//			text.add(texture);
//			texts.add(modeltex);
//			modeltex.setReflectivity(0.1f);
//			modeltex.setShineDamper(30);
//		}
//		return modeltex;
//	}
//
//	public static RawModel getData(String obj) {
//		RawModel modelthing = null;
//		boolean dat = false;
//		for (int i = 0; i < data.size(); i++) {
//			if (data.get(i).equals(obj)) {
//				dat = true;
//				modelthing = datas.get(i);
//				break;
//			}
//		}
//		if (!dat) {
//			ModelData d = getModelData(obj);
//			modelthing = Loader.loadToVAO(d.getVertices(), d.getTextureCoords(), d.getNormals(), d.getIndices());
//			// modelthing.saveThings(d);
//			data.add(obj);
//			datas.add(modelthing);
//		}
//		return modelthing;
//	}
//
//	public static TexturedModel getModel(String obj, String texture) {
//		TexturedModel ThingModel = getContainedModel(obj, texture);
//		if (ThingModel == null) {
//			RawModel model = getData(obj);
//			ModelTexture modeltex = getTex(texture);
//			ThingModel = new TexturedModel(model, modeltex);
//			mod.add(new String(obj + texture));
//			mods.add(ThingModel);
//		}
//		return ThingModel;
//	}
//
//	public static TexturedModel getModel(String obj, String texture, float rflect, float shineDamp) {
//		TexturedModel ThingModel = new TexturedModel(getData(obj), getTex(texture));
//		ThingModel.getTex().setReflectivity(rflect);
//		ThingModel.getTex().setShineDamper(shineDamp);
//		return ThingModel;
//	}
//	
//	public static ModelData getModelData(String obj) {
//		for (int i = 0; i < datamodns.size(); i++) {
//			if (datamodns.get(i).equals(obj)) {
//				return datamods.get(i);
//			}
//		}
//		ModelData d = OBJFileLoader.loadOBJ(obj);
//		datamodns.add(obj);
//		datamods.add(d);
//		return d;
//	}
	
	private static final Map<Short, ModelData> modelDatas = new HashMap<Short, ModelData>();
	
	public static ModelData getModelData(short ID){
		return modelDatas.get(ID);
	}
	
	public static void loadAllRawModelsAndTextures(){
		// TODO what is pretty evident!
		modelDatas.put(RawMods.sapling, OBJFileLoader.loadOBJ("Sapling"));
		modelDatas.put(RawMods.torch, OBJFileLoader.loadOBJ("torch"));
		modelDatas.put(RawMods.grass, OBJFileLoader.loadOBJ("grass"));
		
		String[] files = RawMods.getPositiveFiles();
		
		for(short i = 0; i < files.length; i++){
			ModelData m = OBJFileLoader.loadOBJ(files[i]);
			rawModels.put((short) (i+1), Loader.loadToVAO(m.getVertices(), m.getTextureCoords(), m.getNormals(), m.getIndices()));
		}
		
		files = Texes.getFiles();
		
		for(short i = 0; i < files.length; i++)
			textures.put(i, new ModelTexture(Loader.loadTexture(files[i])));
		
	}
	
	private static final Map<Short, RawModel> rawModels = new HashMap<Short, RawModel>();
	private static final Map<Short, ModelTexture> textures = new HashMap<Short, ModelTexture>();
	private static final ArrayList<TexturedModel> texturedModels = new ArrayList<>();
	private static final boolean useList = true;
	
	public static TexturedModel getModel(short modelID, short texID) {
		RawModel rm = rawModels.get(modelID);
		ModelTexture mt = textures.get(texID);
		if(useList)
			for(int i = 0; i < texturedModels.size(); i++)
				if(texturedModels.get(i).getRawMod() == rm && texturedModels.get(i).getTex() == mt)
					return texturedModels.get(i);
		
		TexturedModel ret = new TexturedModel(rm, mt);
		if(useList)
			texturedModels.add(ret);
		return ret;
	}
	
	public static int getLoadedTex(short texID){
		return textures.get(texID).getID();
	}

	public static ModelData getModelData(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static RawModel getRawModel(short id) {
		return rawModels.get(id);
	}

	public static int loadTexture(short ID, String path) {
		int texID = Loader.loadTexture(path);
		textures.put(ID, new ModelTexture(texID));
		return texID;
	}
}
