package entities;

import java.util.*;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import audio.AudioMaster;
import controls.*;
import data.Block;
import data.ChunkManager;
import entities.graphicsParts.RawMods;
import entities.graphicsParts.Texes;
import gameStuff.*;
import gameStuff2.ServerLoop;
import guis.PixelGUITex;
import hitbox.Hitbox;
import hitbox.PlayerHitbox;
import inventory.Inv2D;
import inventory.Inventory;
import line.Line;
import mainInterface.Intraface;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.*;

public class Player extends Entity implements HittableThing {
	
	public static final Map<Integer, Player> playerIDs = new HashMap<Integer, Player>();
	public static volatile ArrayList<Player> players = new ArrayList<Player>();
	
	private static final float RUNSPEED = 3, RUNSPEEDACCEL = 30, SIDESPEED = 2.5f;// ,
																					// SIDESPEEDACCEL
																					// =
																					// 15
	public static float speedMul = Tools.loadFloatPreference("speedMulForPlayer", 1);
	private static final float MAXSPEED = 30, MAXYSPEED = 100;// , HEADY = 1.5f
	// private static final float maxheight = 1.1f;
	private static final float fatness = 0.3f;

	private float TERRAINHEIGHT = 0, JUMPPOWER = 5f;

	private int lives = 10;

	private float headOffset = 0;

	private boolean flight = Tools.loadBoolPreference("flight", false);
	private boolean HEADUNDERWATER = false;
	private boolean inAir = false;

	private Inventory inv;
	private Inv2D inv2D;

	public boolean GHOSTRIDER = Tools.loadBoolPreference("burn", false);

	public static boolean MANUUPDATE = true;
	public static boolean NOCONTROL = false;
	// private static long LS = Meth.systemTime();
	
	private static PixelGUITex nerdscope;
	
	public Player(Vector3f position, float rotX, float rotY, float rotZ, float scale, int ID) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.position = position;
		
		graphics = Intraface.getModelGraphics(this, RawMods.person, Texes.playerTex);
		
			
//		boundingSphere = Meth.createBoundingSphere(this);

		this.velocity = new Vector3f();
		
		hit = new PlayerHitbox(position, scale);
		
//		if (GHOSTRIDER) {
//			ParticleMaster.addNewParticle(fire,
//					new Vector3f(position.x + Meth.randomFloat(-0.1f, 0.1f),
//							position.y + (10f * scale) + Meth.randomFloat(-0.1f, 0.1f),
//							position.z + Meth.randomFloat(-0.1f, 0.1f)),
//					new Vector3f(), Meth.randomFloat(-0.05f, -0.01f), 0.3f, 0, 1);
//		}
		
		if (!MANUUPDATE) {
			EntityManager.addEntity(this);
//			EntityManager.removeEntity(this);
		}

		lines = new Line[12];
		for (int i = 0; i < 12; i++) {
			lines[i] = new Line(0, 0, 0, 0, 0, 0, 0, 0, 0);
		}

//		e = new Entity(SC.getModel("person", "cube"), 0, new Vector3f(), 0, 0, 0, 0.1f, false);
		
		// e2 = new Entity(SC.getModel("cube", "cube"), 0, new Vector3f(), 0, 0,
		// 0, 0.5f, false);
		
		// LINE = new Line(0, 0, 0, 0, 0, 0, 0, 0.5f, 0.5f);
		
		if(!ServerLoop.isServer){
			inv2D = new Inv2D(this);
			inv = inv2D;
			nerdscope = new PixelGUITex(Models.getLoadedTex(Texes.fadenkreuz), new Vector2f(),
						new Vector2f(4, 4));
			nerdscope.show();
		}else{
			inv = new Inventory();
		}
		
		playerIDs.put(ID, this);
		players.add(this);
		this.playerID = ID;

	}
	
	private final int playerID;

	private float water = 0;

	@Override
	public void update() {

		// SensorData.orientation.normalize();
		// LINE.set1(position.x+0.5f*(float)Math.sin(rotY*Meth.angToRad),
		// position.y+1.25f,
		// position.z+0.5f*(float)Math.cos(rotY*Meth.angToRad));
		//// LINE.set2(position.x+SensorData.orientation.x,
		// position.y+SensorData.orientation.y,
		//// position.z+SensorData.orientation.z);
		// e.setPosition(position.x+2*(float)Math.sin(-SensorData.orientation.x+(rotY-90)*Meth.angToRad),
		// position.y+1.25f+(float)Math.sin(-SensorData.orientation.y),
		// position.z+2*(float)Math.cos(-SensorData.orientation.x+(rotY-90)*Meth.angToRad));
		// LINE.set2(e.getPosition());
		// e.getPosition().y -= 0.5f;

		// rotY += -2*SensorData.dorientation.x*Meth.radToAng;
		// Camera.setPitch(Camera.getPitch()+SensorData.dorientation.y*Meth.radToAng);
		
//		Chat.update();
		
		if (!NOCONTROL) {
			checkInputs();
			move();

			// if (!scripted && Keyboard.isKeyDown(GLFW.GLFW_KEY_P)) {
			// Script s = Script.firstScript();
			// Builder.build(position.x, position.y, position.z, s);
			// scripted = true;
			// }
		}
		
		if(!Intraface.isServer)
			Camera.move();
		
		inv.update();
		
		// Vector3f p = MousePicker.getPoint(5);
		// if (Mouse.isButtonDown(0)) {
		// Vector3f b = MousePicker.getNextFilledBlockCoord(100, false);
		// if (b != null && Meth.systemTime() > lastShot + 500) {
		// CM.deleteBlockWithNoise(b);
		// lastShot = Meth.systemTime();
		// }
		// ParticleMaster.addNewParticle(PTM.projectile, b != null ?
		// Vects.add(b, 0.5f) : p,
		// Vects.randomVector3f(-1, 1, -1, 1, -1, 1), 0, 1, 0, 0.15f);
		// } else if (Mouse.isButtonDown(1)) {
		// Vector3f b = MousePicker.getNextFilledBlockCoord(5);
		// short id = ChunkManager.getBlockID(b);
		// if(b != null && Block.isSlab(id)){
		// if(Meth.systemTime() > lastShot + 500){
		// CM.setBlock(b, Block.holeVersion(id));
		// lastShot = Meth.systemTime();
		// }
		// }else{
		// b = MousePicker.getLastEmptyBlockCoord(5);
		// if (b != null && Meth.systemTime() > lastShot + 500) {
		// CM.setBlock(b, Block.STONESLAB_UP);
		// lastShot = Meth.systemTime();
		// }else if(b == null){
		// lastShot = Meth.systemTime();
		// }
		// }
		// ParticleMaster.addNewParticle(PTM.projectile, b != null ?
		// Vects.add(b, 0.5f) : p,
		// Vects.randomVector3f(-1, 1, -1, 1, -1, 1), 0, 1, 0, 0.15f);
		// } else {
		// // LINE.set1(Vects.NULL);
		// // LINE.set2(Vects.NULL);
		// }
		
//		if(Intraface.isServer)
//		System.out.println("setting lines");
		if(!Intraface.isServer)
			setLines();
//		if(Intraface.isServer)
//		System.out.println("lines set");
		
		if (lives < 10 && lives > 0 && Meth.time() > LH + 0.2f) {
			lives += 1;
			if (lives > 10) {
				lives = 10;
			}
			LH = Meth.time();
		}
	}

	public void move() {
		// if(flight){
		// velocity.x = 0;
		// velocity.y = 0;
		// velocity.z = 0;
		// }

		TERRAINHEIGHT = CNH();
		// Block xxx = ChunkManager.getBlock(position.x, TERRAINHEIGHT-1,
		// position.z);
		// if(xxx != null){
		// blockIDunderMe = xxx.id();
		// }else{
		// blockIDunderMe = 0;
		// }

		if (position.y <= TERRAINHEIGHT) {
			// if(TERRAINHEIGHT - position.y < 0.03f){
			// position.y = TERRAINHEIGHT;
			// }
			inAir = false;
		} else if (position.y >= TERRAINHEIGHT + 0.1f) {
			inAir = true;
		}

		velocity.x = Meth.clamp(velocity.x, -MAXSPEED, MAXSPEED);
		velocity.y = Meth.clamp(velocity.y, -MAXYSPEED, MAXYSPEED);
		velocity.z = Meth.clamp(velocity.z, -MAXSPEED, MAXSPEED);

		float fact = 1 - DisplayManager.getFrameTimeSeconds();
		if (water > 0) {
			fact -= 0.01f * water;
		}

		if (inAir) {
			if (!flight) {
				velocity.y += Meth.GRAVITY * DisplayManager.getFrameTimeSeconds();
			}
		} else {
			if (velocity.y < 0) {
				velocity.y = 0;
			}
			velocity.x *= fact;
			velocity.z *= fact;
		}
		if (flight || water > 0) {
			velocity.y *= fact;
		}
		if (!flight) {
			short b = 0, firstb;
			if (velocity.y > 0) {
				b = ChunkManager.getBlockID(position.x, position.y + 2.1f, position.z);
				if (!Block.passable(b)) {// && !b.isPassable()
					velocity.y = 0;
				}
			}

			// ECKE ... SCHLECHT ---> AABB; nicht in richtung prüfen, sondern
			// plain old 2D collision
			boolean BOTHGO = true;
			int sovx = Meth.vorzeichen(velocity.x);
			float D = DisplayManager.getFrameTimeSeconds();
			b = ChunkManager.getBlockID(position.x + (velocity.x * D) + sovx * fatness, position.y + 0.1f, position.z);
			firstb = b;
			if (!Block.passable(b)
					&& Block.getYDraufTretPos(b) + (int) Math.floor(position.y + 0.1f) >= position.y + 0.6f) {
				b = ChunkManager.getBlockID(position.x + (velocity.x * D + sovx * fatness), position.y + 1.1f,
						position.z);
				if (Block.passable(b)) {
					b = ChunkManager.getBlockID(position.x + (velocity.x * D + sovx * fatness), position.y + 1.95f,
							position.z);
					if (Block.passable(b)) {
						jump();
					}
				}
				velocity.x = 0;
				BOTHGO = false;
			} else {
				b = ChunkManager.getBlockID(position.x + (velocity.x * D + sovx * fatness), position.y + 1.1f,
						position.z);
				if (!Block.passable(b)) {// && Block.getYDraufTretPos(b) +
											// (int)Math.floor(position.y+0.1f)
											// >= position.y + 0.5f
					velocity.x = 0;
					BOTHGO = false;
				} else {
					b = ChunkManager.getBlockID(position.x + (velocity.x * D + sovx * fatness), position.y + 1.95f,
							position.z);
					if (!Block.passable(b)) {// && Block.getYDraufTretPos(b)
												// +
												// (int)Math.floor(position.y+0.1f)
												// >= position.y + 0.5f
						velocity.x = 0;
						BOTHGO = false;
					} else if (!Block.passable(firstb) && velocity.y <= 0
							&& position.y <= Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f)
									+ 0.05f) {
						position.y = Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f);
					}
				}
			}
			int sovz = Meth.vorzeichen(velocity.z);
			b = ChunkManager.getBlockID(position.x, position.y + 0.1f, position.z + velocity.z * D + sovz * fatness);
			firstb = b;
			if (!Block.passable(b)
					&& Block.getYDraufTretPos(b) + (int) Math.floor(position.y + 0.1f) >= position.y + 0.6f) {
				b = ChunkManager.getBlockID(position.x, position.y + 1.1f,
						position.z + velocity.z * D + sovz * fatness);
				if (Block.passable(b)) {
					b = ChunkManager.getBlockID(position.x, position.y + 1.95f,
							position.z + velocity.z * D + sovz * fatness);
					if (Block.passable(b)) {
						jump();
					}
				}
				velocity.z = 0;
				BOTHGO = false;
			} else {
				b = ChunkManager.getBlockID(position.x, position.y + 1.1f,
						position.z + velocity.z * D + sovz * fatness);
				if (!Block.passable(b)) {
					velocity.z = 0;
					BOTHGO = false;
				} else {
					b = ChunkManager.getBlockID(position.x, position.y + 1.95f,
							position.z + velocity.z * D + sovz * fatness);
					if (!Block.passable(b)) {
						velocity.z = 0;
						BOTHGO = false;
					} else if (!Block.passable(firstb) && velocity.y <= 0
							&& position.y <= Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f)
									+ 0.05f) {
						position.y = Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f);
					}
				}
			}

			if (BOTHGO) {
				b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness, position.y + 0.1f,
						position.z + velocity.z * D + sovz * fatness);
				firstb = b;
				if (!Block.passable(b)
						&& Block.getYDraufTretPos(b) + (int) Math.floor(position.y + 0.1f) >= position.y + 0.6f) {
					b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness, position.y + 1.1f,
							position.z + velocity.z * D + sovz * fatness);
					if (Block.passable(b)) {
						b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness, position.y + 1.9f,
								position.z + velocity.z * D + sovz * fatness);
						if (Block.passable(b)) {
							jump();
						}
					}
					velocity.x = 0;
					velocity.z = 0;
				} else {
					b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness, position.y + 1.1f,
							position.z + velocity.z * D + sovz * fatness);
					if (!Block.passable(b)) {
						velocity.x = 0;
						velocity.z = 0;
					} else {
						b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness, position.y + 1.95f,
								position.z + velocity.z * D + sovz * fatness);
						if (!Block.passable(b)) {
							velocity.x = 0;
							velocity.z = 0;
						} else if (!Block.passable(firstb) && velocity.y <= 0
								&& position.y <= Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f)
										+ 0.05f) {
							position.y = Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f);
						}
					}
				}
			}

			water = 0;

			// b = ChunkManager.getBlockID(position);
			// if (b != null && b.id() == Block.WATER) {
			// water += b.getY() + ((WaterBlock) b).height() - position.y;
			// if (water < 0) {
			// water = 0;
			// }
			// }
			// b = ChunkManager.getBlockID(position.x, position.y + 1,
			// position.z);
			// if (b != null && b.id() == Block.WATER) {
			// water += ((WaterBlock) b).height();
			// }
			// b = ChunkManager.getBlockID(position.x, position.y + 2,
			// position.z);
			// if (b != null && b.id() == Block.WATER) {
			// float h = ((WaterBlock) b).height();
			// if (b.getY() + h > position.y) {
			// water += position.y + 2 - b.getY();
			// } else {
			// water += h;
			// }
			// }

		} else {
			water = 0;
		}

		// fixed super.update()

		position.x += velocity.x * DisplayManager.getFrameTimeSeconds();
		position.y += velocity.y * DisplayManager.getFrameTimeSeconds();
		if (!flight && position.y < TERRAINHEIGHT) {
			position.y = TERRAINHEIGHT;
		}
		position.z += velocity.z * DisplayManager.getFrameTimeSeconds();

		position.x += outSideSpeed.x * DisplayManager.getFrameTimeSeconds();
		position.y += outSideSpeed.y * DisplayManager.getFrameTimeSeconds();
		position.z += outSideSpeed.z * DisplayManager.getFrameTimeSeconds();

		outSideSpeed.x *= 0.9f;
		outSideSpeed.z *= 0.9f;
		outSideSpeed.y *= 0.9f;

		// b = ChunkManager.getBlock(getHeadPosition());
		// HEADUNDERWATER = (b != null && b.id() == Block.WATER);

		// update sound
		AudioMaster.setListenerData(Camera.getPosition(), velocity, rotY);

		// CAM
		// if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
		// WorldObjects.cam.setCurrent(this);
		// WorldObjects.cam.setFollowing(false);
		// }

		// if (LINE != null) {
		// LINE.set1(position.x + ysin + ysin1 * 0.5f, position.y + 1,
		// position.z + ycos + ycos1 * 0.5f);
		// LINE.set2(position.x + ysin * 5 + ysin1 * 0.5f, position.y + 2,
		// position.z + ycos * 5 + ycos1 * 0.5f);
		// LINE.set1(position.x, position.y + 0.2f, position.z);
		// LINE.set2(position.x + velocity.x / 10, position.y + 0.2f +
		// position.y / 10, position.z + velocity.z / 10);
		// }
		// Projectiles with Lines... LASER GUNS!!!

	}

	private long lastSwitch = 0;
	private static final long cooldown = 300;
	// private int blockIDunderMe;
	private float ysin, ycos, ysin1, ycos1;

	private void checkInputs() {

		float rotYChange;
		if(!Controller.USECONTROLLER){
			float dx = Mouse.getDX();
			rotYChange = 0.017f * Mouse.sensitivity * dx;
		}else{
			rotYChange = DisplayManager.getFrameTimeSeconds()*100*Controller.getAxis(Controller.LR_RIGHT_STICKER);
		}
		rotY -= rotYChange;
		rotY %= 360;
		
		float runspeedfact;
		float forwardspeed = 0;
		if(!Controller.USECONTROLLER){
			if (Meth.systemTime() > lastSwitch + cooldown && Keyboard.isKeyDown(GLFW.GLFW_KEY_F)) {
				lastSwitch = Meth.systemTime();
				flight = !flight;
			}
	
			runspeedfact = speedMul * (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? (flight ? 20 : 2.5f) : 1);
			
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_W)) {
				forwardspeed += RUNSPEED * runspeedfact;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_S)) {
				forwardspeed -= RUNSPEED * runspeedfact;
			}
		}else{
			if (Meth.systemTime() > lastSwitch + cooldown && Controller.isButtonDown(Controller.Y)) {
				lastSwitch = Meth.systemTime();
				flight = !flight;
			}
			
			runspeedfact = speedMul * (Controller.isButtonDown(Controller.RIGHT_BACK) ? (flight ? 20 : 2.5f) : 1);
			
			forwardspeed = RUNSPEED * runspeedfact * -Controller.getAxis(Controller.UD_LEFT_STICKER);
			
		}
		ysin = (float) Math.sin(Math.toRadians(super.getRotY()));
		ycos = (float) Math.cos(Math.toRadians(super.getRotY()));
		// float ysin = (float) Math.sin(super.getRotY()*Meth.PI/180);
		// float ycos = (float) Math.cos(super.getRotY()*Meth.PI/180);
		float sidespeed = 0;
		if(!Controller.USECONTROLLER) {
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
				sidespeed -= SIDESPEED * runspeedfact;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D)) {
				sidespeed += SIDESPEED * runspeedfact;
			}
		}else{
			sidespeed = SIDESPEED*runspeedfact* Controller.getAxis(Controller.LR_LEFT_STICKER);
		}
		ysin1 = (float) Math.sin(Math.toRadians(super.getRotY() - 90));
		ycos1 = (float) Math.cos(Math.toRadians(super.getRotY() - 90));
		float accel = RUNSPEEDACCEL * runspeedfact;
		// if(blockIDunderMe == Block.ICE){
		// if(forwardspeed == 0){
		// accel /= 500;
		// }else{
		// accel /= 50;
		// }
		// }
		if (sidespeed != 0) {
			velocity.x = Meth.gehZuWert(velocity.x, accel * DisplayManager.getFrameTimeSeconds(),
					ysin1 * sidespeed + ysin * forwardspeed);
			velocity.z = Meth.gehZuWert(velocity.z, accel * DisplayManager.getFrameTimeSeconds(),
					ycos1 * sidespeed + ycos * forwardspeed);
		} else {
			velocity.x = Meth.gehZuWert(velocity.x, accel * DisplayManager.getFrameTimeSeconds(), ysin * forwardspeed);
			velocity.z = Meth.gehZuWert(velocity.z, accel * DisplayManager.getFrameTimeSeconds(), ycos * forwardspeed);
		}

		// if(Meth.doChance(10*DisplayManager.getFrameTimeSeconds())){
		// Vects.setCalcVect(position);
		// Vects.calcVect.y += 2;
		// Vector3f vel = Vector3f.sub(MousePicker.getPoint(100),
		// Vects.calcVect, null);
		// vel.y = 0;
		// ParticleMaster.addNewParticle(PTM.frühlingsblatt, new
		// Vector3f(Vects.calcVect),
		// vel, 0, 5, 0, 0.1f);
		// Vects.setCalcVect(Vects.NULL);
		// }

		if ((!Controller.USECONTROLLER && Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)) || 
				(Controller.USECONTROLLER && Controller.isButtonDown(Controller.LEFT_BACK))) {
			if (JETPACK && inAir) {
				if (position.y >= TERRAINHEIGHT + 1) {
					fly();
				}
			} else {
				jump();
			}
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			if (flight) {
				float somevalue = -JUMPPOWER * (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? 10 : 1);
				if (velocity.y > somevalue)
					velocity.y = somevalue;
			}
		}

	}

	private boolean JETPACK = true;
	private static final float JETSPEED = 15;

	private void fly() {
		if (velocity.y < JETSPEED) {
			velocity.y += JETSPEED * DisplayManager.getFrameTimeSeconds();
			if (velocity.y > JETSPEED*speedMul) {
				velocity.y = JETSPEED*speedMul;
			}
			// if(Meth.doChance(100*DisplayManager.getFrameTimeSeconds())){
			if(!Intraface.isServer) {
				float min = -0.2f;
				float max = 0.2f;
				float pvel = 0.2f;
				// boolean cosmic = Meth.doChance(0.1f);
				boolean cosmic = (TM.jahresZeit() == TM.WINTER)
						|| ((TM.jahresZeit() == TM.HERBST || TM.jahresZeit() == TM.FRÜHLING) && Meth.doChance(0.5f));
				float size = Meth.randomFloat(0.1f, 0.3f);
				ParticleMaster.addNewParticle(cosmic ? PTM.cosmic : PTM.projectile,
						new Vector3f(position.x - ysin * 0.2f + ycos * Meth.randomFloat(min, max),
								position.y + 1 + Meth.randomFloat(min, max),
								position.z - ycos * 0.2f + ysin * Meth.randomFloat(min, max)),
						Vects.randomVector3f(-pvel, pvel, -1 + velocity.y * 0.5f, -5 + velocity.y * 0.5f, -pvel, pvel), 0,
						1, 0, size);
			}
		}
	}

	public void jump() {
		if (!inAir || flight) {
			float somevalue = JUMPPOWER * (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? (flight ? 10 : 1.5f) : 1);
			if (velocity.y < somevalue)
				velocity.y = somevalue;
		}
		if (inAir && water > 0) {
			float somevalue = JUMPPOWER * (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? 1 : 0.3f);
			somevalue *= (float) Math.sin(water - 0.5f);
			if (velocity.y < somevalue)
				velocity.y = somevalue;
		}

	}

	private float LH = Meth.time();
	// private long lastShot = Meth.systemTime();
	// private boolean scripted = false;

	public void setCooldowns() {
		LH = Meth.time();
		// lastShot = Meth.systemTime();
	}

	public static void setNOCONTROL(boolean b) {
		NOCONTROL = b;
		if (NOCONTROL) {
			Mouse.setGrabbed(false);
			Tools.mouseGrabbed = false;
		} else {
			Mouse.setGrabbed(true);
			Tools.mouseGrabbed = true;
		}
	}

	private Line[] lines;
	private boolean filled = true;

	private void setLines() {
		if (!MainLoop.renderGUI) {
			for (int i = 0; i < 12; i++) {
				lines[i].set1(Vects.NULL);
				lines[i].set2(Vects.NULL);
			}
			return;
		}
		if (Keyboard.keyTipped(GLFW.GLFW_KEY_G)) {
			filled = !filled;
		}
		Vector3f pp = filled ? MousePicker.getNextFilledBlockCoord(100) : MousePicker.getLastEmptyBlockCoord(100);
		if (pp != null) {
			float S = Block.BLOCKSIZE + 0.002f;
			short id = ChunkManager.getBlockID(pp);
			float SY = Block.isLesserSlab(id) ? 0.002f + Block.slabHeight(id)
					: (Block.isWater(id) ? 0.002f + (id - 1000) * 0.01f : S);
			float s = -0.001f;

			lines[0].set1(pp.x + s, pp.y + s, pp.z + s);
			lines[0].set2(pp.x + s, pp.y + SY, pp.z + s);
			lines[1].set1(pp.x + S, pp.y + s, pp.z + s);
			lines[1].set2(pp.x + S, pp.y + SY, pp.z + s);
			lines[2].set1(pp.x + s, pp.y + SY, pp.z + s);
			lines[2].set2(pp.x + S, pp.y + SY, pp.z + s);
			lines[3].set1(pp.x + s, pp.y + s, pp.z + s);
			lines[3].set2(pp.x + S, pp.y + s, pp.z + s);

			lines[4].set1(pp.x + s, pp.y + s, pp.z + S);
			lines[4].set2(pp.x + s, pp.y + SY, pp.z + S);
			lines[5].set1(pp.x + S, pp.y + s, pp.z + S);
			lines[5].set2(pp.x + S, pp.y + SY, pp.z + S);
			lines[6].set1(pp.x + s, pp.y + SY, pp.z + S);
			lines[6].set2(pp.x + S, pp.y + SY, pp.z + S);
			lines[7].set1(pp.x + s, pp.y + s, pp.z + S);
			lines[7].set2(pp.x + S, pp.y + s, pp.z + S);

			lines[8].set1(pp.x + s, pp.y + s, pp.z + s);
			lines[8].set2(pp.x + s, pp.y + s, pp.z + S);
			lines[9].set1(pp.x + s, pp.y + SY, pp.z + s);
			lines[9].set2(pp.x + s, pp.y + SY, pp.z + S);
			lines[10].set1(pp.x + S, pp.y + SY, pp.z + s);
			lines[10].set2(pp.x + S, pp.y + SY, pp.z + S);
			lines[11].set1(pp.x + S, pp.y + s, pp.z);
			lines[11].set2(pp.x + S, pp.y + s, pp.z + S);

			for (int i = 0; i < 12; i++) {
				// lines[i].setR(FontColorManager.two.x);
				// lines[i].setG(FontColorManager.two.y);
				// lines[i].setB(FontColorManager.two.z);//
				// WorldObjects.blockColor
				lines[i].setColor(FontColorManager.CV, FontColorManager.CV, 1 - FontColorManager.CV);
			}

		} else {
			for (int i = 0; i < 12; i++) {
				lines[i].set1(Vects.NULL);
				lines[i].set2(Vects.NULL);
			}
		}
		// lines[0].set1(position);
		// lines[0].set2(position.x + velocity.x, position.y + velocity.y,
		// position.z + velocity.z);
	}

	@Override
	public boolean hit(float damage) {
		// if (lives > 0) {
		// lives -= damage;
		// if (lives <= 0) {
		// // EntityManager.removeEntity(this);
		// destroy();
		// NOCONTROL = true;
		// Out.println("YOU FAILED! respawn in 5 seconds");
		// new Thread() {
		// @Override
		// public void run() {
		// Meth.wartn(5000);
		// Block b = null;
		// while (b == null) {
		// b = ChunkManager.getUppestBlock(Meth.randomInt(-50, 50) + (int)
		// position.x,
		// Meth.randomInt(-50, 50) + (int) position.z);
		// }
		// position.x = b.getX();
		// position.y = b.getY() + 2;
		// position.z = b.getZ();
		// lives = 10;
		// MainLoop.score = 0;
		// NOCONTROL = false;
		// }
		// }.start();
		// return true;
		// }
		// }
		return false;
	}

	@Override
	public boolean inHitbox(Vector3f point) {
		boolean bool = false;
		if (hit != null) {
			if (hit.intersects(point)) {
				bool = true;
			}
		}
		return bool;
	}

	public float lives() {
		return lives;
	}

	public Vector3f getHeadPosition() {
		return new Vector3f(position.x + ((float) (0.2f * Math.sin(Math.toRadians(super.getRotY())))),
				position.y + (10f * scale) + headOffset,
				position.z + (float) (0.2f * Math.cos(Math.toRadians(super.getRotY()))));
	}

	public Vector3f getGunPosition() {
		return new Vector3f(position.x, position.y + (10f * scale), position.z);
	}

	public boolean flight() {
		return flight;
	}

	public boolean headunderwater() {
		return HEADUNDERWATER;
	}

	private float[] numbers = new float[5];

	private float CNH() {
		Vector3f c = Vects.calcVect;
		Vects.setCalcVect(position.x, position.y + 1, position.z + 0.2f);
		numbers[0] = calcNextHeight(c);
		Vects.setCalcVect(position.x, position.y + 1, position.z - 0.2f);
		numbers[1] = calcNextHeight(c);
		Vects.setCalcVect(position.x + 0.2f, position.y + 1, position.z);
		numbers[2] = calcNextHeight(c);
		Vects.setCalcVect(position.x - 0.2f, position.y + 1, position.z);
		numbers[3] = calcNextHeight(c);
		Vects.setCalcVect(position.x, position.y + 1, position.z);
		numbers[4] = calcNextHeight(c);
		float first = numbers[0];
		for (int i = 1; i < 5; i++) {
			if (numbers[i] > first) {
				first = numbers[i];
			}
		}
		return first;
	}

	private float calcNextHeight(Vector3f startPos) {
		Vector3f bv = MousePicker.getNextFilledBlockCoord(startPos, Vects.DOWN, (int) Math.abs(5 * velocity.y) + 2);
		if (bv != null) {
			return bv.y + Block.getYDraufTretPos(ChunkManager.getBlockID(bv));// des
																				// hier
																				// auch
																				// noch
																				// bei
																				// move()...
		}
		// b = 0;
		// if (bv != null) {
		// b = ChunkManager.getBlockID(bv);
		// while (b == 0) {// || b.isPassableDown()
		// bv.y -= 1;
		// b = ChunkManager.getBlockID(bv);
		// }
		// return b.getRealHeight();
		// }
		return -100000;
	}

	@Override
	public void influence(Vector3f speed) {
		float x = speed.x;
		float y = speed.y;
		float z = speed.z;
		velocity.x += inAir ? speed.x : (Math.signum(x) * (Math.max(0, Math.abs(x) - 0.5f)));
		velocity.y += inAir ? speed.y : (Math.signum(y) * (Math.max(0, Math.abs(y) - 0.5f)));
		velocity.z += inAir ? speed.z : (Math.signum(z) * (Math.max(0, Math.abs(z) - 0.5f)));
	}

	public void killed(Entity e) {

	}

	public void killed(HittableThing h) {

	}

	public float getBoost() {
		return 5 * JUMPPOWER;
	}

	@Override
	public Hitbox getHitbox() {
		return hit;
	}

	@Override
	public void destroy() {
		if(Intraface.isServer)return;
		float v = 50;
		for (int i = 0; i < 200; i++) {
			ParticleMaster.addNewParticle(Meth.doChance(0.5f) ? PTM.cosmic : PTM.projectile, new Vector3f(position),
					Vects.randomVector3f(-v, v, -v, v, -v, v), 0, 2, 0, Meth.randomFloat(1, 5));
		}
	}

	public void cleanUp() {
		inv.cleanUp();
		for (int i = 0; i < lines.length; i++) {
			lines[i].cleanUp();
		}
		Tools.setFloatPreference("speedMulForPlayer", speedMul);
	}

	public Inv2D getInventory() {
		return inv2D;
	}
	
	public int playerID() {
		return playerID;
	}

	public static Player getNearestPlayer(float x, float y, float z) {// TODO can and should be optimized!
		if(players.size() == 0)return null;
		Player ret = players.get(0);
		float distsq = ret.getPosition().distanceSquared(x, y, z);
		for(int i = 1; i < players.size(); i++){
			float dsq = players.get(i).getPosition().distanceSquared(x, y, z);
			if(dsq < distsq){
				ret = players.get(i);
				distsq = dsq;
			}
		}
		return ret;
	}
	
	private float pitch;
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public float pitch(){
		return pitch;
	}
	
	private static ArrayDeque<Integer> toRemove = new ArrayDeque<>();
	
	public static void updateSomeThings(){
		while(!toRemove.isEmpty()){
			int i = toRemove.pop();
			Player p = playerIDs.remove(i);
			if(p != null)
				players.remove(p);
		}
	}
	
	public static void remove(int playerID) {
		toRemove.add(playerID);
	}

}
