package entities.graphicsParts;

import entities.MWBE;
import models.TexturedModel;

public class ModelGraphics extends GraphicsPart{
	
	private MWBE parent;
	private TexturedModel model;
	
	public ModelGraphics(MWBE parent, TexturedModel model){
		this.parent = parent;
		this.model = model;
		
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void cleanUp() {
		parent = null;
		model = null;
	}
	
	public float getTextureXOffset() {
		int column = parent.texIndex() % model.getTex().getNOR();
		return (float) column / (float) model.getTex().getNOR();
	}

	public float getTextureYOffset() {
		int row = (int) ((float) parent.texIndex() / (float) model.getTex().getNOR());
		return (float) row / (float) model.getTex().getNOR();
	}
	
	public TexturedModel getModel(){
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}
	
	protected boolean hidden = false;
	
	public void show(){
		if(hidden){
			
			hidden = false;
		}
	}
	
	public void hide(){
		if(!hidden){
			
			hidden = true;
		}
	}

	public void setParent(MWBE mwbe) {
		this.parent = mwbe;
	}
	
}
