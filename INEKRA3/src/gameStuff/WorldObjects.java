package gameStuff;

import static gameStuff.TM.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import data.LightMaster;
import entities.*;
import gameStuff2.CommandProcessor;
import mainInterface.Intraface;
import mobs.MobMaster;
import toolBox.*;
import weather.WeatherController;

public abstract class WorldObjects {

//	public static Player player;
	public static Light sun;
	public static MousePicker picker;
	public static Vector3f spawnPoint = new Vector3f(5000, 50, 5000);

	private static ArrayList<Player> players = new ArrayList<Player>();
	private static ArrayList<HittableThing> hits = new ArrayList<HittableThing>();
	// private static ArrayList<Mob> stupidMobs = new ArrayList<Mob>();

	public static void init() {
		
		Intraface.init();
		createStartEntities();
		
		if(!Intraface.isServer){
			Camera.getPosition().set(Player.players.get(0).getPosition());
		}
		Intraface.finishInit();

		sun = new Light(new Vector3f(200000, 300000, 200000), new Vector3f(0.5f, 0.5f, 0.5f));
		ltr.add(sun);
		
		MousePicker.init();

//		SensorData.init();

		// tex3d = new GuiTex(SC.getTex("button").getID(), new Vector3f(0, 0,
		// 0), new Vector2f(1));
		// tex3d.show();

		Err.err.println("WorldObjects inited!");

	}

	// private static GuiTex tex3d;

//	private static GUIText block;
//	private static Vector2f blockPos = new Vector2f(0.25f, 0.86f);
//	private static float mLL = 0.5f;
//	private static short lastID;
//	private static int lastTorchLight, lastSunLight;

	public static void update() {
		
		if(!Intraface.isServer)
			MousePicker.update();
		
		if (Player.MANUUPDATE)
			for(int i = 0; i < Player.players.size(); i++)
				Player.players.get(i).update();// something fails here!!!

		EntityManager.updateAll();
		TickManager.update();
		MobMaster.update();

		WeatherController.update();
		
		CommandProcessor.update();

		LightMaster.update();

	}

	public static void removeThingFromWorld(Object o) {
		EntityManager.removeEntity(o);
		hits.remove(o);
		TickManager.removeTickingThing(o);
	}

	private static void createStartEntities() {
		float x = Tools.loadFloatPreference("PX", 5000);
		float y = Tools.loadFloatPreference("PY", 5000);
		float z = Tools.loadFloatPreference("PZ", 5000);
		spawnPoint.x = x;
		spawnPoint.y = y;
		spawnPoint.z = z;
		if(!Intraface.isServer){
			new Player(new Vector3f(500, 50, 500), 0, 0, 0, 0.15f, 0);
		}
//		if (player == null) {
//			player = new Player(spawnPoint, 0, 0, 0, 0.15f, (int)Tools.loadLongPreference("playerID", 0));
//			Err.err.println("created player...");
//			hits.add(player);
//			players.add(player);
//		} else {
//			player.setPosition(spawnPoint);
//			players.add(player);
//			hits.add(player);
//		}
	}

	public static float dayColor = 0.6f, nightColor = 0.1f;

	public static void timeUpdate() {
		Player player = Player.players.get(0);
		float time = (float) TM.getDayTime();
		if ((time >= night || time >= 0) && time < morningstart) {
			sun.setColour(nightColor, nightColor, nightColor);
		} else if (time >= morningstart && time < morning) {
			float fact = (time - morningstart) / (morning - morningstart);
			sun.setColour(((fact * dayColor) > nightColor) ? fact * dayColor : nightColor,
					(fact * dayColor > nightColor) ? fact * dayColor : nightColor,
					(fact * dayColor > nightColor) ? fact * dayColor : nightColor);
		} else if (time >= morning && time < eveningstart) {
			sun.setColour(dayColor, dayColor, dayColor);
		} else {
			float fact = (time - eveningstart) / (night - eveningstart);
			fact = 1 - fact;
			sun.setColour(((fact * dayColor) > nightColor) ? (fact * dayColor) : nightColor,
					((fact * dayColor) > nightColor) ? (fact * dayColor) : nightColor,
					((fact * dayColor) > nightColor) ? (fact * dayColor) : nightColor);
		}
		float cI = time / 24;
		sun.getPosition().set(-(float) Math.sin(cI * 2 * Meth.PI) * SUNDIST + player.getPosition().x, // ((time
																										// >=
																										// 18
																										// ||
																										// time
																										// <=
																										// 6)
																										// ?
																										// -1
																										// :
																										// 1)*
				-(float) Math.cos(cI * 2 * Meth.PI) * SUNDIST + player.getPosition().y,
				zoffset * SUNDIST + player.getPosition().z);
		sun.setColour(
				Vects.blend(sun.getColour(), WeatherController.getWeatherColor(), WeatherController.blendFactor()));

		getSunDirection(Vects.calcVect, (float) TM.getDayTime());
		float reddot = Vects.calcVect.dot(horizon);
		float redcap = 0.95f;
		if (reddot > redcap) {
			Vects.blend(sun.getColour(), red, sun.getColour(), Meth.clamp((1 - (100 * (1 - reddot))) * 0.2f, 0, 0.1f));
		} else {
			reddot = Vects.calcVect.dot(horizon2);
			if (reddot > redcap) {
				Vects.blend(sun.getColour(), red, sun.getColour(),
						Meth.clamp((1 - (100 * (1 - reddot))) * 0.2f, 0, 0.1f));
			}
		}
	}

	private static final Vector3f red = new Vector3f(1, 0.2f, 0.2f), horizon = new Vector3f(1, -0.1f, 0).normalize(),
			horizon2 = new Vector3f(-1, -0.1f, 0);
	public static final float SUNDIST = 200000, zoffset = 0;

	public static Vector3f getSunDirection(Vector3f setVect, float time) {
		float cI = time / 24;
		setVect.set((float) -Math.sin(cI * 2 * Meth.PI), (float) -Math.cos(cI * 2 * Meth.PI), zoffset);
		setVect.normalize();
		return setVect;
	}

	private static List<Light> ltr = new ArrayList<Light>();

	public static List<Light> getLightsToRender() {
		return ltr;
	}

	public static void save() {
		if(Player.players.size() > 0)
			Tools.setBoolPreference("flight", Player.players.get(0).flight());
	}

	public static ArrayList<HittableThing> getHits() {
		return hits;
	}

	public static void addHit(HittableThing h) {
		if (!hits.contains(h)) {
			hits.add(h);
		}
	}

	public static void removeHit(HittableThing h) {
		hits.remove(h);
	}

	public static void cleanUp() {
		// long millis = System.currentTimeMillis();
		Intraface.cleanUp();
		// System.err.println("Time to clean up CM: " +
		// (System.currentTimeMillis()-millis));
		EntityManager.cleanUp();
		
		for(int i = 0; i < Player.players.size(); i++)
			Player.players.get(0).cleanUp();
		
		Player.players.clear();
		
		sun = null;
		// picker = null;
		players.clear();
		hits.clear();

//		if (block != null)
//			block.cleanUp();

		// stupidMobs.clear();
	}

	public static float getSunAngle(float time) {
		return -(time / 24) * 2 * Meth.PI;
	}

}
