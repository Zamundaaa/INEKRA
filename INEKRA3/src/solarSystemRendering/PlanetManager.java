package solarSystemRendering;

import java.util.ArrayList;

import toolBox.Meth;

public class PlanetManager {
	
	protected static ArrayList<Planet> planets = new ArrayList<Planet>();
	public static final float earthRadius = 12700 * 0.5f;
//	public static final float earthRadius = 5;
	public static final int earthUmfang = Meth.toInt(earthRadius*2*Meth.PI);
//	public static Earth earth = new Earth(0, -earthRadius, 0, earthRadius);
////	public static Earth earth = new Earth(500, 0, 500, 1);
//	static{
//		planets.add(earth);
//	}
	
	public static void init(){
//		planets.add(new Planet(500, 500, 500, 100));
	}
	
	public static void add(Planet p){
		planets.add(p);
	}
	
	public static void update(){
		for(int i = 0; i < planets.size(); i++)
			planets.get(i).update();
	}
	
	public static void cleanUp(){
		//TODO
	}

}
