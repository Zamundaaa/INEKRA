package menuThings2;

import collectionsStuff.ArrayListBool;
import menuThings.MenuThing;

public class Pane extends MenuThing{
	
	private ArrayListBool itemsShown = new ArrayListBool();
	
	public Pane(){
		
	}
	
	@Override
	public void hide(){
		itemsShown.clear();
		for(int i = 0; i < attached.size(); i++){
			itemsShown.add(attached.get(i).visible());
			attached.get(i).hide();
		}
	}
	
	@Override
	public void show(){
		for(int i = 0; i < attached.size(); i++)
			if(itemsShown.size() >= i-1 && itemsShown.get(i))
				attached.get(i).show();
	}
	
	@Override
	public void updateClicks() {
		
	}

	@Override
	public void setTextAlpha(float a) {
		
	}

	@Override
	public void setTextColor(float r, float g, float b, float a) {
		
	}

	@Override
	public void setTextColor(float r, float g, float b) {
		
	}

}
