package menuThings;

import java.util.ArrayList;
import java.util.List;

public class Menü {

	private List<MenuThing> inputs;
//	private List<Button> buttons = new ArrayList<Button>();
//	private List<TextField> tfs = new ArrayList<TextField>();
	// private List<GuiTexture> texs = new ArrayList<GuiTexture>();
	
	private ArrayList<MenuThing> ka = new ArrayList<MenuThing>();
	
	public Menü(List<MenuThing> inps) {
		this.inputs = inps;
//		for (int i = 0; i < inputs.size(); i++) {
//			if (inputs.get(i).getClass() == Button.class) {
//				buttons.add((Button) inputs.get(i));
//			} else if (inputs.get(i).getClass() == TextField.class) {
//				tfs.add((TextField) inputs.get(i));
//			}
//		}
		// for (int i = 0; i < inputs.size(); i++) {
		// texs.add(inputs.get(i).getGuiTex());
		// }
		
	}
	
	public void add(MenuThing m){
		inputs.add(m);
	}
	
	public void remove(MenuThing m){
		inputs.remove(m);
	}

	public void update() {
		for (int i = 0; i < inputs.size(); i++) {
			inputs.get(i).update();
		}
		
		for(int i = 0; i < inputs.size(); i++){
			inputs.get(i).putClickable(ka);
		}
		
		sortList(ka, 0, ka.size()-1);
		
		MenuThing.clickUsed = false;
		
//		System.out.println("______________________________________________________");
		
		for(int i = ka.size()-1; i >= 0 && !MenuThing.clickUsed; i--){
			ka.get(i).updateClicks();//check for consistency!!! 
//			System.out.println(ka.get(i).displayLevel);
		}
		
		ka.clear();
	}
	
//	public void cleanUp(){
//		for(int i = 0; i < inputs.size(); i++)
//			inputs.get(i).cleanUp();
//	}
	
	private boolean hidden = false;
	
	public void hide() {
		if(!hidden){
			for (int i = 0; i < inputs.size(); i++) {
				inputs.get(i).hide();
			}
			hidden = true;
		}
	}

	public void show() {
		if(hidden){
			for (int i = 0; i < inputs.size(); i++) {
				inputs.get(i).show();
			}
			hidden = false;
		}
	}

//	public List<Button> getButtons() {
//		return buttons;
//	}
	
	private void sortList(List<MenuThing> ka, int start, int end){
		if(start >= end)
			return;
		
		int i = start;
		int k = end - 1;
		MenuThing pivot = ka.get(end);
		do{
			while(ka.get(i).displayLevel <= pivot.displayLevel && i < end)
				i++;
			while(ka.get(k).displayLevel >= pivot.displayLevel && k > start)
				k--;
			if(i<k){
				MenuThing temp = ka.get(i);
				ka.set(i, ka.get(k));
				ka.set(k, temp);
			}
		}while(i < k);
		
		if(ka.get(i).displayLevel > pivot.displayLevel){
			MenuThing temp = ka.get(i);
			ka.set(i, ka.get(end));
			ka.set(end, temp);
		}
		
		sortList(ka, start, i-1);
		sortList(ka, i+1, end);
		
	}

	public void cleanUpAndClear() {
		for(int i = 0; i < inputs.size(); i++)
			inputs.get(i).cleanUp();
		inputs.clear();
	}

	public boolean visible() {
		return !hidden;
	}

}
