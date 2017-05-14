package menuThings;

import java.util.ArrayList;

import org.joml.Vector3f;

public abstract class MenuThing implements Inputthing{
	
	public static boolean clickUsed;
	protected boolean hidden;
	protected boolean transparent;
	protected boolean clickable;
	protected int displayLevel;
	protected ArrayList<MenuThing> attached = new ArrayList<MenuThing>();
	protected Rectangle bounds;
	protected Rectangle boundsOfParent;
//	protected Rectangle renderBounds = new Rectangle(0, 0, 1000, 1000);
	protected Rectangle rel;
	
	public MenuThing(Rectangle bounds){
		this.bounds = bounds;
	}
	
	public MenuThing(){
		
	}
	
	public void setDisplayLevel(int displayLevel){
		this.displayLevel = displayLevel;
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).setDisplayLevel(displayLevel+1);
		}
	}
	
	public int displayLevel(){
		return displayLevel;
	}
	
	public void attach(MenuThing m){
		attached.add(m);
//		if(m.displayLevel == 0)
			m.displayLevel = displayLevel+1;
	}
	
	public void attach(MenuThing m, float relx, float rely, float relw, float relh){
		attached.add(m);
		m.setRelativeBounds(bounds, new Rectangle(relx, rely, relw, relh));
		if(m.displayLevel == 0)
			m.displayLevel = displayLevel+1;
	}
	
	public void attach(MenuThing m, float relX, float relY){
		attached.add(m);
		m.setRelativeBounds(bounds, new Rectangle(relX, relY, 0, 0));
		if(m.displayLevel == 0)
			m.displayLevel = displayLevel+1;
	}
	
	public void remove(MenuThing m){
		m.detachFromParent();
		attached.remove(m);
	}
	
	@Override
	public void hide(){
		if(!hidden){
			hidden = true;
			for(int i = 0; i < attached.size(); i++)
				attached.get(i).hide();
		}
	}
	
	@Override
	public void show(){
		if(hidden){
			hidden = false;
			for(int i = 0; i < attached.size(); i++)
				attached.get(i).show();
		}
	}
	
	@Override
	public void update(){
		for(int i = 0; i < attached.size(); i++)
			attached.get(i).update();
	}
	
	@Override
	public void setBounds(float x, float y, float w, float h){
		bounds.setBounds(x, y, w, h);
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).updateRelativePos();
		}
	}
	
	public void updateRelativePos(){
		if(rel != null && !rel.isNull()){
			bounds.x = boundsOfParent.x + rel.x;
			bounds.y = boundsOfParent.y + rel.y;
			if(rel.w != 0)
				bounds.w = boundsOfParent.w * rel.w;
			if(rel.h != 0)
				bounds.h = boundsOfParent.h * rel.h;
			setBounds(bounds);
		}
	}
	
	public void setRelativeBounds(Rectangle boundsOfParent, Rectangle rel){
		this.boundsOfParent = boundsOfParent;
		this.rel = rel;
		bounds.x = boundsOfParent.x + rel.x;
		bounds.y = boundsOfParent.y + rel.y;
		if(rel.w != 0)
			bounds.w = boundsOfParent.w * rel.w;
		if(rel.h != 0)
			bounds.h = boundsOfParent.h * rel.h;
		setBounds(bounds);
	}
	
	public void setRel(float x, float y, float w, float h){
		rel.setBounds(x, y, w, h);
	}
	
	/**
	 * sets the isRelativeToParent to false and removes the link to the parent's bounds Rectangle
	 */
	public void detachFromParent(){
		boundsOfParent = null;
		rel = null;
	}
	
	@Override
	public void setBounds(Rectangle r){
		setBounds(r.x, r.y, r.w, r.h);
	}
	
	public void setAllTextAlpha(float a){
		setTextAlpha(a);
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).setAllTextAlpha(a);
		}
	}

	public void setAllTextColor(float r, float g, float b, float a) {
		setTextColor(r, g, b, a);
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).setAllTextColor(r, g, b, a);
		}
	}

	public MenuThing getHighestClickable(){
		MenuThing ret;
		if(clickable){
			ret = this;
		}else{
			ret = null;
		}
		int maxL = displayLevel;
		for(int i = 0; i < attached.size(); i++){
			MenuThing hc = attached.get(i).getHighestClickable();
			if(hc != null && hc.displayLevel >= maxL){
				maxL = hc.displayLevel;
				ret = hc;
			}
		}
		return ret;
	}
	
	protected boolean mouseInBounds;
	
	public void putClickable(ArrayList<MenuThing> ka){
		if(clickable && !hidden && !bounds.noSize()){
			ka.add(this);
		}
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).putClickable(ka);
		}
	}

	public abstract void updateClicks();

	public void cleanUp(){
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).cleanUp();
		}
	}

	public abstract void setTextAlpha(float a);
	
	public abstract void setTextColor(float r, float g, float b, float a);
	
	public abstract void setTextColor(float r, float g, float b);
	
	public void setTextColor(Vector3f color){
		setTextColor(color.x, color.y, color.z);
	}
	
}
