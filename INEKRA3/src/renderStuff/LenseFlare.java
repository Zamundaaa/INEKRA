package renderStuff;

import org.joml.Vector2f;

import gameStuff.SC;
import gameStuff.WorldObjects;
import guis.GuiTexture;
import toolBox.Vects;

public class LenseFlare {
	
	private GuiTexture[] texes;
	private float intensity;
	private float spacing;
	
	public LenseFlare(float intensity, float spacing, GuiTexture... texes){
		this.texes = texes;
		for(int i = 0; i < texes.length; i++){
			texes[i].setDisplayLevel(0);
		}
		this.intensity = intensity;
		this.spacing = spacing;
	}
	
	public LenseFlare(float intensity, float spacing){
		this(intensity, spacing,
				new GuiTexture(SC.getTex("lensFlare/tex2").getID(), new Vector2f(), new Vector2f(0.05f, 0.05f*DisplayManager.desiredRatioForGUI), true),
				new GuiTexture(SC.getTex("lensFlare/tex4").getID(), new Vector2f(), new Vector2f(0.06f, 0.06f*DisplayManager.desiredRatioForGUI), true),
				new GuiTexture(SC.getTex("lensFlare/tex6").getID(), new Vector2f(), new Vector2f(0.1f, 0.1f*DisplayManager.desiredRatioForGUI), true),
				new GuiTexture(SC.getTex("lensFlare/tex9").getID(), new Vector2f(), new Vector2f(0.24f, 0.24f*DisplayManager.desiredRatioForGUI), true),
				new GuiTexture(SC.getTex("lensFlare/tex8").getID(), new Vector2f(), new Vector2f(0.4f, 0.4f*DisplayManager.desiredRatioForGUI), true));
	}
	
	public void update(){
		Vector2f sunSP = MasterRenderer.toScreenSpace(WorldObjects.sun.getPosition());
		if(sunSP == null){
			hide();
			return;
		}
		Vector2f sunToCenter = Vects.NULL2.sub(sunSP, Vects.calcVect2D);
		float brightness = 1 - (sunToCenter.length() / 0.6f);
		if(brightness > 0){
			for(int i = 0; i < texes.length; i++){
				texes[i].getPos().set(sunSP.x + i*spacing*sunToCenter.x, sunSP.y + i*spacing*sunToCenter.y);
//				System.out.println(texes[i].getPos());
				texes[i].setHighlight(intensity*brightness);
				texes[i].setAlphaHighlight(0.4f*intensity*brightness);
				if(hidden)
					texes[i].show();
			}
			hidden = false;
		}else{
			hide();
		}
	}
	
	public void hide(){
		if(!hidden){
			for(int i = 0; i < texes.length; i++)
				texes[i].hide();
			hidden = true;
		}
	}
	
	public void show(){
		if(hidden){
			for(int i = 0; i < texes.length; i++)
				texes[i].show();
			hidden = false;
		}
	}
	
	private boolean hidden = true;
	
	public void cleanUp(){
		hide();
	}

}
