package solarSystemRendering;

import org.lwjgl.glfw.GLFW;

import controls.Keyboard;
import controls.Mouse;
import entities.Camera;
import renderStuff.DisplayManager;
import renderStuff.MasterRenderer;

public class Main {

	public static void main(String[] args){
		
		DisplayManager.init();
		
//		Camera.setPosition(3, 0, 5);
		
		Planet one = new Planet(0, -1, -2);
		PlanetRenderer.init();
		PlanetManager.add(one);
		
		Keyboard.init();
		Mouse.init();
		
		MasterRenderer.createProjectionMatrix();
		MasterRenderer.enableCulling();
		
		while(!Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) && !DisplayManager.isCloseRequested()){
			if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A)){
				Camera.setRoll(Camera.getRoll()+DisplayManager.getFrameTimeSeconds()*60);
			}
			if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D)){
				Camera.setRoll(Camera.getRoll()-DisplayManager.getFrameTimeSeconds()*60);
			}
			PlanetManager.update();
			PlanetRenderer.render();
			DisplayManager.updateWindow();
		}
		
		PlanetRenderer.cleanUp();
		
	}

}
