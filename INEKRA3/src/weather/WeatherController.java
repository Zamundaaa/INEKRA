package weather;

import static audio.SourcesManager.thundersound;
import static particles.PTM.*;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import audio.AudioMaster;
import audio.Source;
import controls.Keyboard;
import data.Block;
import data.ChunkManager;
import entities.*;
import fontRendering.Out;
import gameStuff.TM;
import gameStuff.WorldObjects;
import mainInterface.Intraface;
import particles.*;
import renderStuff.DisplayManager;
import renderStuff.MasterRenderer;
import toolBox.*;

public class WeatherController {

	public static boolean spawnStars = false;
	public static boolean snowLayers = false;
	public static boolean ROBOTS = false;
	public static final float ROBOTCHANCE = 0.001f;// ca 1x alle 1000s

	private static final Source thunder = new Source();

	private static boolean itMightRain = Tools.loadBoolPreference("rainallowed", true);
	private static boolean rainMakesWater = Tools.loadBoolPreference("rainmakeswater", true);

	private static long lastTime = Tools.loadLongPreference("lastTimeRained", Meth.systemTime() - 10000);

	private static boolean rain = Tools.loadBoolPreference("rain", false);
	private static boolean snow = Tools.loadBoolPreference("snow", false);// NOT
																			// REALLY
																			// NECESSARY
	// private static boolean snowcandosomething =
	// Tools.loadBoolPreference("snowcandosomething", true);

	private static final float minCooldown = 600000.0f;
	private static final float minLength = 100000.0f;

	private static final float CC = 600000f;// 24.0f;
	private static final float CL = 300000f;// 12.0f;

	public static final float rainGravity = 1.0f;
	public static final float minVel = -1.0f;
	public static final float maxVel = -2.0f;

	public static final float snowGravity = 0.3f;
	public static final float minSnowVel = -0.1f;
	public static final float maxSnowVel = -0.2f;

	private static Vector3f ppos;

	// private static final float lightningFire = 0.1f;

	public static final float PARTICLEMULT = 0.5f;

	public static void update() {
		snow = TM.jahresZeit() == TM.WINTER;// Meth.doChance(0.2f)
		if (itMightRain) {
			if (!rain) {
				if (Meth.systemTime() - lastTime > minCooldown) {
					if (Meth.doChance(((Meth.systemTime() - lastTime) / CC) * DisplayManager.getFrameTimeSeconds())) {
						rain();
					}
				}
			} else {
				if (Meth.systemTime() - lastTime > minLength) {
					if (Meth.doChance(((Meth.systemTime() - lastTime) / CL) * DisplayManager.getFrameTimeSeconds())) {
						rain = false;
						lastTime = Meth.systemTime();
					}
				}
			}

		}
		ppos = Camera.getPosition();
		if (rain) {
			if (!snow) {
				// float timemult = 0.01f;
				// float number1 =
				// Generator.getG().genThing(timemult*TimeManager.gameTimeMillis())*10;
				// float number2 =
				// Generator.getG().genThing(timemult*42*TimeManager.gameTimeMillis()
				// - 4104)*10;
				// float vx = 5;// Meth.randomFloat(-3, 3) + number1
				// float vz = 4;// Meth.randomFloat(-3, 3) + number2
				// for (int i2 = 0; i2 < 5 * PARTICLEMULT; i2++) {
				// Particle rain = ParticleMaster.addNewParticle(raindrop,
				// new Vector3f(Meth.randomFloat(-200, 200) +
				// WorldObjects.player.getPosition().x,
				// Meth.randomFloat(100, 200) +
				// WorldObjects.player.getPosition().y,
				// Meth.randomFloat(-200, 200) +
				// WorldObjects.player.getPosition().z),
				// new Vector3f(vx, Meth.randomFloat(maxVel, minVel), vz),
				// rainGravity, 6, 0, 1);
				// if (rain != null) {
				// rain.setWindFactor(1);
				// }
				// }
				// if (Meth.doChance(0.05f *
				// DisplayManager.getFrameTimeSeconds())) {
				// lstrike();
				// }
				if (rainMakesWater && Meth.doChance(0.1f * DisplayManager.getFrameTimeSeconds())) {
					int x = (int) (Meth.randomFloat(-100, 100) + ppos.x);
					int z = (int) (Meth.randomFloat(-100, 100) + ppos.z);
					int y = ChunkManager.getUppestBlockY(x, z);
					if (y != Integer.MIN_VALUE) {
						y += 5;
						Intraface.setBlock(x, y, z, Block.getWater(0.2f));
					}
				}
			} else {
				// fogDensity = 0.1f * (blendFactor()+0.3f);
				// if(fogDensity < snowDensity){
				// fogDensity += DisplayManager.getFrameTimeSeconds()*0.01f;
				// }else{
				// fogDensity = snowDensity;
				// }
				// for (int i2 = 0; i2 < 10 * PARTICLEMULT; i2++) {
				// ParticleMaster.addNewParticle(snowflake,
				// new Vector3f(Meth.randomFloat(-200, 200) + ppos.x,
				// Meth.randomFloat(50, 150) + ppos.y,
				// Meth.randomFloat(-150, 150) + ppos.z),
				// new Vector3f(0, Meth.randomFloat(maxSnowVel, minSnowVel), 0),
				// snowGravity, 10, 0, 1);
				// }
				// if(snowcandosomething){
				// // machs Biomabhängig, ob Schnee oder Wasser fällt (Partikel
				// und auch Blöcke) und so weiter
				// // UND AB JETZT AUCH JAHRESZEIT!!!
				// int TPT = trysPerTick;
				// int APT = attemptsPerTry;
				// if(Keyboard.isKeyDown(GLFW.GLFW_KEY_B)){
				// TPT *= 5;
				// APT *= 5;
				// }
				// for(int X = 0; X < TPT; X++){
				// for(int i = 0; i < APT; i++){
				// int x = Meth.toInt(Meth.randomFloat(-range*Chunk.SIZE,
				// range*Chunk.SIZE) + ppos.x);
				// int z = Meth.toInt(Meth.randomFloat(-range*Chunk.SIZE,
				// range*Chunk.SIZE) + ppos.z);
				// Block b = ChunkManager.getUppestBlock(x, z);
				// if(b != null && b.id() != Block.SNOWLAYER && b.id() !=
				// Block.SNOW && b.id() != Block.ICE && !b.isGrass() &&
				// !b.isLeaves()){
				// if(b.id() == Block.WATER){
				// int y = b.getY();
				// if(CM.blockAncientBut(x, y, z, Block.WATER)){
				// CM.setBlock(x, y, z, new Block(Block.ICE, x, y, z,
				// false));
				// }
				// // else{
				// // ParticleMaster.addNewParticle(cosmic, new Vector3f(x+0.5f,
				// y+1.5f, z+0.5f), new Vector3f(), 0, 5, 0, 1);
				// // }
				// break;
				// }else if(snowLayers && b.id() != Block.FERN){
				// int y = b.getY() + 1;
				// CM.setBlock(x, y, z, new Block(Block.SNOWLAYER, x,
				// y, z, false));
				// break;
				// }
				//// else{
				//// b.switchID(Block.SNOW);
				//// break;
				//// }
				// }
				// }
				// }
				// }
			}
		} else {
			// if(fogDensity > normalDensity){
			// fogDensity -= DisplayManager.getFrameTimeSeconds()*0.01f;
			// }else{
			// fogDensity = normalDensity;
			// }

			// if(ROBOTS &&
			// Meth.doChance(ROBOTCHANCE*DisplayManager.getFrameTimeSeconds())){
			// float X = Meth.randomFloat(-50,
			// 50)+WorldObjects.player.getPosition().x;
			// float Z = Meth.randomFloat(-50,
			// 50)+WorldObjects.player.getPosition().z;
			// Block b = ChunkManager.getUppestBlock((int)X, (int)Z);
			// if(b != null){
			// new Robot(new Vector3f(X, b.getY()+2, Z));
			// }
			// }
		}

		float x = ppos.x;
		float y = ppos.y;
		float z = ppos.z;

		float gravityeffect = 0.05f;
		float minySpeed = 0;
		float maxySpeed = 5;
		float lifeTime = 10;

		float spawnpersecond = 1;
		float range = 30;

		switch (TM.jahresZeit()) {
		case TM.FRÜHLING:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(frühlingsblatt,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 0.5f);
			}
			break;
		case TM.SOMMER:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(fire,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 1);
			}
			break;
		case TM.HERBST:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(herbstblatt,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 0.5f);
			}
			break;
		case TM.WINTER:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(snowflake,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 0.5f);
			}
			break;
		}

		if (TM.isNight()) {
			float d = 100;
			LSP += 30 * DisplayManager.getFrameTimeSeconds();
			while (LSP >= 1) {
				LSP -= 1;
				if (Meth.doChance(0.02f)) {
					Vector3f v = Vects.pointOnRay(ppos, Vects.randomVector3f(min, max).normalize(), d, new Vector3f());
					Sternschnuppn noname = new Sternschnuppn(v, Vects.randomVector3f(-10, 10, -10, 10, -10, 10), false);
					noname.setGravity(0);
				}
			}
		}
		// else {
		boolean C = Keyboard.isKeyDown(GLFW.GLFW_KEY_C);
		if (Meth.doChance(WeatherMap.getSpawnChance())) {
			spawnACloud();
		}
		if (C) {
			CLOUDVEL.x = 50;
			CLOUDVEL.z = 50;
		} else {
			CLOUDVEL.x = 5;
			CLOUDVEL.z = 5;
		}
		// }

		WeatherMap.update();

		doFlashsThing();

		// if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)){
		// fogDisabled = true;
		// }else if(Keyboard.isKeyDown(Keyboard.KEY_ADD)){
		// fogDisabled = false;
		// }
		// if(fogDisabled){
		// fogDensity = 0;
		// }

	}

	private static final float minCloudHeight = 200, maxCloudHeight = 220;
	// private static final float minCloudAngle = -Meth.PI*1.5f, maxCloudAngle =
	// -Meth.PI*0.5f;
	private static final Vector3f CLOUDVEL = new Vector3f(3, 0, 3), minC = new Vector3f(-400, 0, -400),
			maxC = new Vector3f(0, 0, 0);

	private static void spawnACloud() {
		Vector3f p = Camera.getPosition();
		long seed = Meth.randomInt(0, 6237527);
		Vector3f cloudCenter = new Vector3f(p.x + Meth.randomFloat(minC.x, maxC.x, seed),
				Meth.randomFloat(minCloudHeight, maxCloudHeight, seed + 21743),
				p.z + Meth.randomFloat(minC.z, maxC.z, seed + 87965));
		WeatherMap.add(new Cloud(cloudCenter));
	}

	public static void updateForMenu(float time) {
		ppos = Camera.getPosition();
		float x = ppos.x;
		float y = ppos.y;
		float z = ppos.z;

		float gravityeffect = 0.05f;
		float minySpeed = 0;
		float maxySpeed = 5;
		float lifeTime = 10;

		float spawnpersecond = 1;
		float range = 30;

		switch (TM.jahresZeit()) {
		case TM.FRÜHLING:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(frühlingsblatt,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 0.5f);
			}
			break;
		case TM.SOMMER:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(fire,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 1);
			}
			break;
		case TM.HERBST:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(herbstblatt,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 0.5f);
			}
			break;
		case TM.WINTER:
			if (Meth.doChance(spawnpersecond * DisplayManager.getFrameTimeSeconds())) {
				ParticleMaster.addNewParticle(snowflake,
						Vects.randomVector3f(x - range, x + range, 10, Math.max(y + range, 100), z - range, z + range),
						Vects.randomVector3f(-10, 10, minySpeed, maxySpeed, -10, 10), gravityeffect, lifeTime, 0, 0.5f);
			}
			break;
		}

		if (TM.isNight()) {
			float d = 100;
			LSP += 30 * DisplayManager.getFrameTimeSeconds();
			while (LSP >= 1) {
				LSP -= 1;
				if (Meth.doChance(0.02f)) {
					Vector3f v = Vects.pointOnRay(ppos, Vects.randomVector3f(min, max).normalize(), d, new Vector3f());
					// ParticleMaster.addNewParticle(white, v, Vects.UP, 0, 5,
					// 0, Meth.randomFloat(0.1f, 0.3f));
					// Out.println("P" + v);
					// } else {
					Sternschnuppn noname = new Sternschnuppn(v, Vects.randomVector3f(-10, 10, -10, 10, -10, 10), false);
					noname.setGravity(0);
				}
			}
		}
		// else {
		boolean C = Keyboard.isKeyDown(GLFW.GLFW_KEY_C);
		if (Meth.doChance(WeatherMap.getSpawnChance())) {
			spawnACloud();
		}
		if (C) {
			CLOUDVEL.x = 50;
			CLOUDVEL.z = 50;
		} else {
			CLOUDVEL.x = 5;
			CLOUDVEL.z = 5;
		}
		// }

		if (Meth.systemTime() > lastFlare + 2000) {
			float rotDivergence = 20;
			float dist = Meth.randomFloat(50, 100);
			float roty = Meth.randomFloat(-rotDivergence, rotDivergence) + MasterRenderer.menuRot;

			float sin = (float) Math.sin(roty * Meth.angToRad);
			float cos = (float) Math.cos(roty * Meth.angToRad);
			Vector3f pos = new Vector3f(dist * sin, 0, -dist * cos);
			Projectil p = new Projectil(pos, Vects.UP30, null, false);
			p.setFlare(dist);
			p.setGravity(0);
			lastFlare = Meth.systemTime();
		}

	}

	private static long lastFlare = Meth.systemTime();

	// private static boolean fogDisabled = false;
	//
	// private static int attemptsPerTry =
	// Meth.toInt(range*range*Meth.PI*0.01f);
	// private static int trysPerTick = Meth.toInt(range*range*Meth.PI*0.01f);

	// PLAN: Chunks speichern Windgeschwindigkeiten ab! (--> nur geringe
	// Veränderungen) // ODER: MAP!

	// public static float getWindX(float x, float z){
	// return Generator.getG().genThing(x/100, 4180, z/100, 4170, 4281) * 100;
	// }

	// public static float getWindZ(float x, float z){
	// return Generator.getG().genThing(x/100, 48230, z/100, 41530, 432) * 100;
	// }

	private static void doFlashsThing() {
		boolean f = flashPresent();
		// if(!startFlashLightUpdate && f){
		// CM.LightUpdate((int)lastFlashPos.x, (int)lastFlashPos.y,
		// (int)lastFlashPos.z);
		// startFlashLightUpdate = true;
		// }
		// if(!f && !endFlashLightUpdate){
		// CM.LightUpdate((int)lastFlashPos.x, (int)lastFlashPos.y,
		// (int)lastFlashPos.z);
		// endFlashLightUpdate = true;
		// }
		if (f) {
			WorldObjects.sun.setColour(lightninglight);
		}
	}

	private static Vector3f lightninglight = new Vector3f(1, 1, 1);
	private static Vector3f min = new Vector3f(-1, 0, -1), max = new Vector3f(1, 1, 1);

	private static float LSP;
	private static final float transitionTime = 5000;

	/**
	 * @return range: 0 - 0.7f; 1 if a lightning bolt is(/was shortly) there
	 */
	public static float blendFactor() {
		// if(Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_8))Out.println(rain);
		float ret = 1;
		if (rain) {
			float raintime = Meth.systemTime() - lastTime;
			if (raintime > transitionTime) {
				ret = 0.7f;
			} else {
				ret = Meth.clamp((raintime / transitionTime) - 0.3f, 0f, 0.7f);
			}
			if (Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_8))
				Out.println(ret + " RT: " + raintime);
		} else {
			// float full = minLength/transitionTime;
			float timeout = Meth.systemTime() - lastTime;
			if (timeout > transitionTime) {
				ret = 0;
			} else {
				ret = Meth.clamp(1 - (timeout / transitionTime), 0f, 0.7f);
			}
			if (Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_8))
				Out.println(ret + " TO: " + timeout);
		}
		if (flashPresent()) {
			ret = 1;
		}
		return ret;
	}

	private static final float maxwavefact = 1.4f, wavefact = 2;

	public static float waveFactor() {
		float ret = 1;
		if (!rain) {
			long timeout = Meth.systemTime() - lastTime;
			if (timeout > transitionTime) {
				ret = 1;
			} else {
				ret = Meth.clamp(((timeout / transitionTime)) * wavefact, 1, maxwavefact);
			}
		} else {
			long raintime = Meth.systemTime() - lastTime;
			if (raintime > transitionTime) {
				ret = 2.1f;
			} else {
				ret = Meth.clamp(((raintime / transitionTime) - 0.3f) * wavefact, 1, maxwavefact);
			}
		}
		return ret;
	}

	// private static final float rainr = 0, raing = 0.29f, rainb = 0.498f;
	private static final Vector3f rainColor = new Vector3f(0.1f, 0.1f, 0.1f);
	private static final Vector3f snowColor = new Vector3f(0.5f, 0.5f, 0.5f);
	private static final Vector3f flash = new Vector3f(1, 1, 1);

	public static Vector3f getWeatherColor() {
		if (flashPresent()) {
			return flash;
		} else {
			if (snow) {
				return snowColor;
			} else {
				return rainColor;
			}
		}
	}

	private static final float lightningstep = 1f, cD = 3, lightningParticlesSize = 3f, thundervolume = 300;
	private static final float lightningParticlesDuration = 5/60f;

	private static long lastFlash;
	private static Vector3f lastFlashPos = new Vector3f();
	private static final float lightningFire = 0.99f;

	public static void lstrikeback(Vector3f struckpoint) {
		Vector3f pos = new Vector3f(struckpoint);
		lastFlashPos = new Vector3f(pos);
//		if (CM.deleteBlock(pos)) {
		Intraface.deleteBlock(pos);
			// for(int i = 0; i < 30; i++){
			// Item3D I = Item3D.getInstance(Meth.doChance(0.5f) ? "cube" :
			// "dragonSkin",
			// new Vector3f(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f), true);
			// I.influence(Vects.randomVector3f(-3f, 3f, 3f, 5f, -3f, 3f));
			// }
			for (int i = 0; i < 15; i++) {
				ParticleMaster.addNewParticle(lightning, new Vector3f(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f),
						Vects.randomVector3f(-3f, 3f, -3f, 3f, -3f, 3f), 0, 1, 0, 0.2f);
			}
//		}
		 if(Meth.doChance(lightningFire)){
			 Intraface.setBlock(pos, Block.FIRE);
		 }
		if (AudioMaster.soundEnabled) {
			thunder.setVolume(thundervolume);
			thunder.setPosition(pos);
			thunder.play(thundersound);
			// SourcesManager.play(thundersound, thundervolume, pos);
		}
		Vector3f dir = Vects.randomVector3f(-1, 1, 1, 1, -1, 1);
		short b = Block.AIR;
		float dist = 0;
		while (b == Block.AIR && pos.y <= struckpoint.y + 100) {
			pos = MousePicker.getPointOnRay(pos, dir, lightningstep);
			dist += lightningstep;
			if (dist >= cD) {
				dist -= cD;
				dir = Vects.randomVector3f(-1, 1, 1, 1, -1, 1);
			}
			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.NULL, 0,
					(float) Math.max(lightningParticlesDuration, 5 * DisplayManager.getFrameTimeSeconds()), 0,
					lightningParticlesSize);

			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.randomVector3f(5), 0,
					Meth.randomFloat(1, 3), 0,
					lightningParticlesSize*0.1f);
			
			b = ChunkManager.getBlockID(pos);
		}
		lastFlash = Meth.systemTime();
	}

	public static void lstrike(float x, float z) {
		Vector3f start = new Vector3f(x, 100, z);
		Vector3f pos = new Vector3f(start);
		lastFlashPos = new Vector3f(pos);
		Vector3f dir = Vects.randomVector3f(-1, 1, -1, -1, -1, 1);
		short b = Block.AIR;
		float dist = 0;
		while (b == Block.AIR && pos.y >= 0) {
			pos = MousePicker.getPointOnRay(pos, dir, lightningstep);
			dist += lightningstep;
			if (dist >= cD) {
				dist -= cD;
				dir = Vects.randomVector3f(-1, 1, -1, -1, -1, 1);
			}
			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.NULL, 0,
					(float) Math.max(lightningParticlesDuration, 5 * DisplayManager.getFrameTimeSeconds()), 0,
					lightningParticlesSize);
			
			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.randomVector3f(5), 0,
					Meth.randomFloat(1, 3), 0,
					lightningParticlesSize*0.1f);
			
			b = ChunkManager.getBlockID(pos);
		}
//		if (CM.deleteBlock(pos)) {
		Intraface.deleteBlock(pos);
			// for(int i = 0; i < 30; i++){
			// Item3D I = Item3D.getInstance(Meth.doChance(0.5f) ? "cube" :
			// "dragonSkin",
			// new Vector3f(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f), true);
			// I.influence(Vects.randomVector3f(-3f, 3f, 3f, 5f, -3f, 3f));
			// }
			for (int i = 0; i < 15; i++) {
				ParticleMaster.addNewParticle(lightning, new Vector3f(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f),
						Vects.randomVector3f(-3f, 3f, -3f, 3f, -3f, 3f), 0, 1, 0, 0.25f);
			}
//		}
		 if(Meth.doChance(lightningFire)){
		 Intraface.setBlock(pos, Block.FIRE);
		 }
		if (AudioMaster.soundEnabled) {
			thunder.setVolume(thundervolume);
			thunder.setPosition(pos);
			thunder.play(thundersound);
		}
		lastFlash = Meth.systemTime();
	}

	public static void lstrike() {
		Vector3f start = new Vector3f(Meth.randomFloat(-200, 200) + ppos.x, 100, Meth.randomFloat(-200, 200) + ppos.z);
		Vector3f pos = new Vector3f(start);
		lastFlashPos = new Vector3f(pos);
		Vector3f dir = Vects.randomVector3f(-1, 1, -1, -1, -1, 1);
		short b = Block.AIR;
		float dist = 0;
		while (b == Block.AIR && pos.y >= 0) {
			pos = MousePicker.getPointOnRay(pos, dir, lightningstep);
			dist += lightningstep;
			if (dist >= cD) {
				dist -= cD;
				dir = Vects.randomVector3f(-1, 1, -1, -1, -1, 1);
			}
			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.NULL, 0,
					(float) Math.max(lightningParticlesDuration, 5 * DisplayManager.getFrameTimeSeconds()), 0,
					lightningParticlesSize);
			
			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.randomVector3f(5), 0,
					Meth.randomFloat(1, 3), 0,
					lightningParticlesSize*0.1f);
			
			b = ChunkManager.getBlockID(pos);
		}
//		if (CM.deleteBlock(pos)) {
		Intraface.deleteBlock(pos);
			// for(int i = 0; i < 30; i++){
			// Item3D I = Item3D.getInstance(Meth.doChance(0.5f) ? "cube" :
			// "dragonSkin",
			// new Vector3f(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f), true);
			// I.influence(Vects.randomVector3f(-3f, 3f, 3f, 5f, -3f, 3f));
			// }
			for (int i = 0; i < 15; i++) {
				ParticleMaster.addNewParticle(lightning, new Vector3f(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f),
						Vects.randomVector3f(-3f, 3f, -3f, 3f, -3f, 3f), 0, 1, 0, 0.25f);
			}
//		}
		 if(Meth.doChance(lightningFire)){
			 Intraface.setBlock(pos, Block.FIRE);
		 }
		if (AudioMaster.soundEnabled) {
			thunder.setVolume(thundervolume);
			thunder.setPosition(pos);
			thunder.play(thundersound);
		}
		lastFlash = Meth.systemTime();
	}
	
	public static void lstrike(Vector3f startPos, Vector3f endPos){
//		Vector3f pos = new Vector3f(endPos);
		lastFlashPos = new Vector3f(startPos);
//		if (CM.deleteBlock(endPos)) {
		Intraface.deleteBlock(endPos);
			for (int i = 0; i < 15; i++) {
				ParticleMaster.addNewParticle(lightning, new Vector3f(endPos.x + 0.5f, endPos.y + 0.5f, endPos.z + 0.5f),
						Vects.randomVector3f(-3f, 3f, -3f, 3f, -3f, 3f), 0, 1, 0, 0.2f);
			}
//		}
		if(Meth.doChance(lightningFire)){
			Intraface.setBlock(endPos, Block.FIRE);
		}
		if (AudioMaster.soundEnabled) {
			thunder.setVolume(thundervolume);
			thunder.setPosition(endPos);
			thunder.play(thundersound);
			// SourcesManager.play(thundersound, thundervolume, pos);
		}
		float d = startPos.distance(endPos);
		Vector3f dir = endPos.sub(startPos, new Vector3f()).normalize();
		for(int i = 0; i < d; i++){
			Vector3f v = MousePicker.getPointOnRay(startPos, dir, i, new Vector3f());
			ParticleMaster.addNewParticle(lightning, Vects.addRandom(new Vector3f(v), 0.5f), Vects.NULL, 0,
				(float) Math.max(lightningParticlesDuration, 5 * DisplayManager.getFrameTimeSeconds()), 0,
				lightningParticlesSize);
			for(int i2 = 0; i2 < WorldObjects.getHits().size(); i2++){
				HittableThing h = WorldObjects.getHits().get(i2);
				if(h.getPosition().distanceSquared(v) < 25){
					h.influence(10/((h.getPosition().x-v.x)*(h.getPosition().x-v.x)),
							10/((h.getPosition().y-v.y)*(-v.y+h.getPosition().y)),
							10/((-v.z+h.getPosition().z)*(-v.z+h.getPosition().z)));
				}
			}
			ParticleMaster.addNewParticle(lightning, Vects.addRandom(v, 0.5f), Vects.randomVector3f(5), 0,
					Meth.randomFloat(1, 3), 0,
					lightningParticlesSize*0.1f);
		}
//		Vector3f dir = Vects.randomVector3f(-1, 1, 1, 1, -1, 1);
//		short b = Block.AIR;
//		float dist = 0;
//		while (b == Block.AIR && pos.y <= endPos.y + 100) {
//			pos = MousePicker.getPointOnRay(pos, dir, lightningstep);
//			dist += lightningstep;
//			if (dist >= cD) {
//				dist -= cD;
//				dir = Vects.randomVector3f(-1, 1, 1, 1, -1, 1);
//			}
//			ParticleMaster.addNewParticle(lightning, new Vector3f(pos), Vects.NULL, 0,
//					(float) Math.max(lightningParticlesDuration, 5 * DisplayManager.getFrameTimeSeconds()), 0,
//					lightningParticlesSize);
//			b = ChunkManager.getBlockID(pos);
//		}
		lastFlash = Meth.systemTime();
	}

	public static Vector3f flashPoint() {
		return lastFlashPos;
	}

	public static boolean flashPresent() {
		return Meth.systemTime() < lightningParticlesDuration*1000 + lastFlash;// *
																			// 0.000025f
	}

	private static void rain() {
		if (!rain) {
			rain = true;
			lastTime = Meth.systemTime();
		}
	}

	public static void makeItSNOW() {
		snow = true;
		rain();
	}

	public static void makeItRAIN() {
		rain();
	}

	public static boolean isRaining() {
		return rain;
	}

	public static void stopItNOW() {
		if (rain) {
			rain = false;
			lastTime = Meth.systemTime();
		}
	}

	public static void save() {
		Tools.setBoolPreference("rainallowed", itMightRain);
		Tools.setBoolPreference("rain", rain);
		Tools.setLongPreference("lastTimeRained", lastTime);
		Tools.setBoolPreference("snow", snow);
		Tools.setBoolPreference("rainmakeswater", rainMakesWater);
	}

	// private static final float snowDensity = 0.025f;
	// private static final float normalDensity = 0.01f;
	private static float fogDensity = 0;//0.01f;
	private static float fogGradient = 5;

	public static float getFogDensity() {
		return fogDensity;
	}

	public static float getFogGradient() {
		return fogGradient;
	}

	public static void init() {
		for (int i = 0; i < 500; i++) {
			WeatherMap.update();
			if (i % 50 == 0) {
				spawnACloud();
			}
		}
	}

	public static void flare(float x, float y, float z, Vector3f veladd, float size, int count) {
		ParticleTexture tex = fireworks;
		for (int i = 0; i < count; i++) {
			// switch (Meth.randomInt(1, 7)) {
			// case 1:
			// tex = fire;
			// break;
			// case 2:
			// tex = projectile;
			// break;
			// case 3:
			// tex = cosmic;
			// break;
			// case 4:
			// tex = star;
			// break;
			// case 5:
			// tex = raindrop;
			// break;
			// case 6:
			// tex = snowflake;
			// break;
			// default:
			// tex = lightning;
			// break;
			// }
			Vector3f s = Vects.randomVector3f(-10, 10, -10, 10, -10, 10);
			s.add(veladd);
			Particle p = ParticleMaster.addNewParticle(tex, new Vector3f(x, y, z), s, 0,
					Math.min(Meth.randomFloat(0.75f, 1.25f), 15 / s.length()), Meth.randomFloat(0, 360), size,
					Meth.randomFloat(0, 0.75f));
			if (p != null)
				p.enlargeEffect();
		}
	}

	public static void flare(Vector3f v, Vector3f veladd, float size, int count) {
		flare(v.x, v.y, v.z, veladd, size, count);
	}

	public static boolean isSnowing() {
		return snow;
	}

}
