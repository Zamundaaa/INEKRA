package solarSystemRendering;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import blockRendering.BlockRenderer;
import entities.Camera;
import gameStuff.TM;
import gameStuff.WorldObjects;
import renderStuff.MasterRenderer;
import toolBox.Meth;
import toolBox.Vects;

public class PlanetRenderer {
	
	private static PlanetShader ps;
	
	public static void init(){
		ps = new PlanetShader();
	}
	
	private static Matrix4f viewMat = new Matrix4f(), transMat = new Matrix4f();
	
	public static void render(){
		ArrayList<Planet> planets = PlanetManager.planets;
		if(planets.size() == 0)
			return;
		ps.start();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		if(BlockRenderer.WIREFRAME){
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			MasterRenderer.disableCulling();
		}
		
		final float fact = 1;
		viewMat = Meth.createViewMatrix(viewMat, Camera.getYaw(), Camera.getPitch(), Camera.getRoll(), Camera.getPosition().x*fact, Camera.getPosition().y*fact, Camera.getPosition().z*fact);
		ps.loadViewMatrix(viewMat);
		ps.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());// do loading this separately! Just because.
		
		for(Planet p : planets){
			ps.loadTransformationMatrix(p.buildTransformationMatrix(transMat));
			ps.loadDrehMatrix(p.buildDrehMatrix(transMat));
//			ps.loadSunDir(Vects.setCalcVect(1, 0.2f, 0).normalize());
			ps.loadSunDir(WorldObjects.getSunDirection(Vects.calcVect, (float)TM.getDayTime()));
			ps.loadSunColor(WorldObjects.sun.getColour());
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, p.getTex());
			
			GL30.glBindVertexArray(p.getLODModel().getVaoID());
			GL11.glDrawElements(GL11.GL_TRIANGLES, p.getLODModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		GL30.glBindVertexArray(0);
		ps.stop();
		
		
		if(BlockRenderer.WIREFRAME){
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			MasterRenderer.enableCulling();
		}
	}
	
	public static void cleanUp(){
		ps.cleanUp();
		ps = null;
	}
	
}
