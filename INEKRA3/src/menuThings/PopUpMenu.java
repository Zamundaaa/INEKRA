package menuThings;

import org.joml.Vector2f;

import renderStuff.DisplayManager;

public class PopUpMenu extends Button{
	
	protected Rectangle currentBounds = new Rectangle();
	
	protected boolean visible = false;
	
	public PopUpMenu(Rectangle bounds){
		super(bounds);
		HOVER = false;
	}
	
	public PopUpMenu(Rectangle bounds, int texID, boolean texTransparent){
		super("", bounds, texID, texTransparent);
		HOVER = false;
	}
	
	protected PopUpMenu(String text, Rectangle bounds, int texID, boolean texTransparent){
		super(text, bounds, texID, texTransparent);
		HOVER = false;
	}
	
	public void popUp(float x, float y){
		currentBounds.setBounds(x, y, 0, 0);
		bounds.setLocation(x, y);
		if(!visible){
			super.show();
			visible = true;
		}
//		System.out.println(bounds);
		update();
	}
	
	private static final float fact = 1000;
	
	@Override
	public void update(){
		super.update();
		if(visible){
			if(currentBounds.w < bounds.w){
				currentBounds.w += fact*DisplayManager.getFrameTimeSeconds();
			}
			if(currentBounds.w > bounds.w){
				currentBounds.w = bounds.w;
				fullScaleMessage();
			}
			
			if(currentBounds.h < bounds.h){
				currentBounds.h += fact*DisplayManager.getFrameTimeSeconds();
			}
			if(currentBounds.h > bounds.h){
				currentBounds.h = bounds.h;
			}
//			Inputthing.toGUITexCoords(currentBounds, gtex.getPos());
			Vector2f gpos = gtex.getPos().set((float) ((currentBounds.x * 0.001f) + (bounds.getWidth() * 0.0005f)),
					(float) ((currentBounds.y * 0.001f) + (bounds.getHeight() * 0.0005f)));
			gpos.x = gpos.x * 2 - 1;
			gpos.y = gpos.y * 2 - 1;
			gpos.y *= -1;
			gtex.getScale().set(currentBounds.w*0.001f, currentBounds.h*0.001f);
		}else{
			if(currentBounds.w > 0){
				currentBounds.w -= fact*DisplayManager.getFrameTimeSeconds();
			}else{
				currentBounds.w = 0;
			}
			
			if(currentBounds.h > 0){
				currentBounds.h -= fact*DisplayManager.getFrameTimeSeconds();
			}else{
				currentBounds.h = 0;
			}
			if(currentBounds.noSize()){
				hide();
			}else{
//				Inputthing.toGUITexCoords(currentBounds, gtex.getPos());
				Vector2f gpos = gtex.getPos().set((float) ((currentBounds.x * 0.001f) + (bounds.getWidth() * 0.0005f)),
						(float) ((currentBounds.y * 0.001f) + (bounds.getHeight() * 0.0005f)));
				gpos.x = gpos.x * 2 - 1;
				gpos.y = gpos.y * 2 - 1;
				gpos.y *= -1;
				gtex.getScale().set(currentBounds.w*0.001f, currentBounds.h*0.001f);
			}
		}
		
		for(int i = 0; i < attached.size(); i++){
			attached.get(i).updateRelativePos();
		}
		
	}
	
	protected void fullScaleMessage(){
		
	}
	
	@Override
	public void show(){
		
	}
	
//	@Override
//	public void updateClicks() {
//		
//	}
	
	@Override
	public void clickOutside(){
		visible = false;
	}
	
	@Override
	public void hide(){
		super.hide();
		visible = false;
	}
	
}
