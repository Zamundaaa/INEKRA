package entities.graphicsParts;

import java.util.HashMap;
import java.util.Map;

import blockRendering.BlockRenderer;
import data.Block;
import gameStuff.Models;

public class Texes {
	
	public static final short NONE = 0;
	public static final short playerTex = 1;//playerTexture
	public static final short sand = 2;// texPack/Sand
	public static final short WATER = 3;// WATER
	public static final short white = 4;// white
	public static final short button = 5;// button
	public static final short buttonClicked = 6;// "button_clicked"
	public static final short textfield = 7;// textfield
	public static final short textfieldChosen = 8;// "textfield chosen"
	public static final short trashcan = 9;// "trashcan_pic"
	public static final short green = 10;// texPack/green
	public static final short red = 11;// texPack/red
	public static final short questionBackground = 12;// QuestionBackground
	public static final short blackBorder = 13;// BlackBorder
	public static final short fadenkreuz = 14;// fadenkreuz
	public static final short icon16 = 15;// "Icons/16"
	public static final short loadingScreen = 16;// LoadingScreens/L1quad
	public static final short invStackBack = 17;// invStackBack
	public static final short menu = 18;// menu
	public static final short gun = 19;// gun
	public static final short pick = 20;// pick
	public static final short moon = 21;// sky/moon
	public static final short grey = 22;// grey
	
	public static String[] getFiles(){
		return new String[]{"noTex", "playerTexture", "texPack/Sand", "WATER", "white", "button",
				"button_clicked", "textfield", "textfield_chosen", "trashcan_pic", "texPack/green", "texPack/red", 
				"QuestionBackground", "BlackBorder", "fadenkreuz", "Icons/16", "LoadingScreens/L1quad", "invStackBack", 
				"menu", "gun", "pick", "sky/moon", "grey"};
	}
	
	private static final Map<Short, Short> blockTexes = new HashMap<Short, Short>();
	private static short currentMaxBlock;
	
	public static short getBlockTex(short blockID) {
		if(Block.isWater(blockID)){
			return WATER;
		}
		Short s = blockTexes.get(blockID);
		if(s != null){
			return s;
		}else{
			currentMaxBlock--;
			blockTexes.put(blockID, currentMaxBlock);
			Models.loadTexture(currentMaxBlock, BlockRenderer.ordner + Block.getFileName(blockID));
			return currentMaxBlock;
		}
	}

	public static int getTex(short ID) {
		return Models.getLoadedTex(ID);
	}
	
}
