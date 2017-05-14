package guis;

import java.util.ArrayList;

public class GUIManager {

	public static int DOWN = 0, UP = 1;

	private static ArrayList<GuiTexture> texes = new ArrayList<GuiTexture>();

	private static ArrayList<GuiTexture> transparents = new ArrayList<GuiTexture>();

	public static void reset() {
		texes.clear();
		transparents.clear();
	}

	public static ArrayList<GuiTexture> getTex() {
		return texes;
	}

	public static void addGuiTexture(GuiTexture tex) {
		if (tex.isTransparent()) {
			transparents.add(tex);
		} else {
			texes.add(tex);
		}
	}

	public static void removeGuiTexture(GuiTexture tex) {
		if(tex.isTransparent()){
			transparents.remove(tex);
		}else{
			texes.remove(tex);
		}
	}

	public static ArrayList<GuiTexture> transparents() {
		sortTransparents();
		return transparents;
	}
	
	private static void sortTransparents(){
		for(int i = 0; i < transparents.size()-1; i++){
			if(transparents.get(i).displayLevel() > transparents.get(i+1).displayLevel()){
				GuiTexture buff = transparents.get(i);
				transparents.set(i, transparents.get(i+1));
				transparents.set(i+1, buff);
			}
		}
	}
}
