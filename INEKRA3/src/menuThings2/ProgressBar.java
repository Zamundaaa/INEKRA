package menuThings2;

import org.joml.Vector2f;

import guis.GuiTexture;
import menuThings.*;

public class ProgressBar extends MenuThing{
	
	private GuiTexture texture;
	private float progress;
	
	/**
	 * @param progress a value between 0 and 1
	 */
	public ProgressBar(Rectangle bounds, int tex, boolean texTransparent, float progress){
		this.bounds = bounds;
		texture = new GuiTexture(tex, new Vector2f(), new Vector2f(), texTransparent);
		texture.show();
		hidden = false;
		setProgress(progress);
	}
	
	/**
	 * @param progress a value between 0 and 1
	 */
	public void setProgress(float progress){
		this.progress = progress;
		Inputthing.toGUITexCoords(bounds.x, bounds.y, bounds.w*progress, bounds.h, texture.getPos());
		texture.getScale().set(bounds.w*0.001f*progress, bounds.h*0.001f);
	}
	
	public float progress(){
		return progress;
	}
	
	@Override
	public void hide(){
		if(!hidden){
			texture.hide();
			super.hide();
		};
	}
	
	@Override
	public void show(){
		if(hidden){
			texture.show();
			super.show();
		}
	}
	
	@Override
	public void cleanUp(){
		texture.hide();
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
		// TODO Auto-generated method stub
		
	}
	
}
