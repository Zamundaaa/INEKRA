package gameStuff;

import static gameStuff.TM.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import cubyWater.WaterUpdater;
import data.ChunkManager;
import data.LightMaster;
import entities.*;
import gameStuff2.CommandProcessor;
import mobs.MobMaster;
import network.SensorData;
import toolBox.*;
import weather.WeatherController;

public abstract class WorldObjects {

	public static Player player;
	public static Light sun;
	public static MousePicker picker;
	public static Vector3f spawnPoint = new Vector3f(5000, 50, 5000);

	private static ArrayList<Player> players = new ArrayList<Player>();
	private static ArrayList<HittableThing> hits = new ArrayList<HittableThing>();
	// private static ArrayList<Mob> stupidMobs = new ArrayList<Mob>();

	public static void init() {

		createStartEntities();
		Camera.getPosition().set(player.getPosition());

		sun = new Light(new Vector3f(200000, 300000, 200000), new Vector3f(0.5f, 0.5f, 0.5f));
		ltr.add(sun);
		ChunkManager.init();

		if (WaterUpdater.MULTITHREADING)
			WaterUpdater.init();

		MousePicker.init();

		SensorData.init();

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

		MousePicker.update();

		if (Player.MANUUPDATE)
			player.update();

		EntityManager.updateAll();
		TickManager.update();

		ChunkManager.update();

//		Vector3f bpos = MousePicker.getNextFilledBlockCoord(50, false);
//		if (bpos != null) {
//			short b = ChunkManager.getBlockID(bpos);
//			bpos = MousePicker.getLastEmptyBlockCoordWithOrientation(50);
//			if (bpos != null) {
//				bpos.add(MousePicker.calcVect);
//			} else {
//				Vects.setCalcVect(0);
//				bpos = Vects.calcVect;
//			}
//			int tL = ChunkManager.getTorchLight(bpos);
//			int sL = ChunkManager.getSunLight(bpos);
//			if (b != lastID || tL != lastTorchLight || sL != lastSunLight) {
//				lastID = b;
//				lastTorchLight = tL;
//				String text = Block.string(b);
//				text += " light level: " + tL + " sunlight: " + sL;
//				if (block != null) {
//					block.setText(text);
//				} else {
//					block = new GUIText(text, 1.5f, SC.font, blockPos, mLL, true);
//				}
//			}
//			block.setColour(FontColorManager.CV, FontColorManager.CV, FontColorManager.CV);
//		} else {
//			if (block != null) {
//				block.cleanUp();
//				block = null;
//				lastID = Block.AIR;
//			}
//		}

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
		if (player == null) {
			player = new Player(SC.playermod, spawnPoint, 0, 0, 0, 0.15f);
			hits.add(player);
			players.add(player);
		} else {
			player.setPosition(spawnPoint);
			players.add(player);
			hits.add(player);
		}
	}

	public static float dayColor = 0.6f, nightColor = 0.1f;

	public static void timeUpdate() {
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
		float redcap = 0.97f;
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
		if (player != null) {
			Tools.setFloatPreference("PX", player.getPosition().x);
			Tools.setFloatPreference("PY", player.getPosition().y);
			Tools.setFloatPreference("PZ", player.getPosition().z);
			Tools.setBoolPreference("flight", player.flight());
			// ChunkManager.saveAll();
		}
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
		ChunkManager.cleanUp();
		// System.err.println("Time to clean up ChunkManager: " +
		// (System.currentTimeMillis()-millis));
		EntityManager.cleanUp();
		if (player != null) {
			player.cleanUp();
			player = null;
		}
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
