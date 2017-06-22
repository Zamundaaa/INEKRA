package mobs;

import org.joml.Vector3f;

import data.Block;
import data.ChunkManager;
import dataAdvanced.SimpleConstructs;
import entities.Entity;
import entities.Player;
import entities.graphicsParts.RawMods;
import entities.graphicsParts.Texes;
import gameStuff.EntityManager;
import mainInterface.Intraface;
import renderStuff.DisplayManager;
import toolBox.*;

public class Cube extends Entity {

	public static final float fatness = 0.5f, JUMPPOWER = 10, MAXSPEED = 5, MAXYSPEED = 5, RUNSPEED = 3,
			RUNSPEEDACCEL = 1;
	public static final float bodnoffset = 0.5f;
	public static final float HEIGHT = 1;

//	protected static final TexturedModel model = SC.getModel("cube", "WATER");
	protected static final short modelID = RawMods.cube;
	protected static final short texID = Texes.WATER;

	private float TERRAINHEIGHT, waitingTime;
	private boolean inAir = false;
	private float water;

	public Cube(Vector3f position) {
		super(modelID, texID, 0, position, 0, 0, 0, 0.4f, false);
	}

	@Override
	public void update() {
		setTargets();
		move();
	}

	public void jump() {
		if (!inAir) {
			float somevalue = JUMPPOWER;
			if (velocity.y < somevalue)
				velocity.y = somevalue;
		}
		if (inAir && water > 0) {
			float somevalue = JUMPPOWER;
			somevalue *= (float) Math.sin(water - 0.5f);
			if (velocity.y < somevalue)
				velocity.y = somevalue;
		}

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
		Vector3f bv = MousePicker.getNextFilledBlockCoord(startPos, Vects.DOWN,
				(int) Math.min(Math.abs(5 * velocity.y) + 2, 100));
		if (bv != null) {
			return bv.y + Block.getYDraufTretPos(ChunkManager.getBlockID(bv));
		}
		return -100000;
	}

	private void setTargets() {
		if(Player.players.size() == 0)return;
		Player player = Player.players.get(0);

		if (Meth.doChance(0.1f * DisplayManager.getFrameTimeSeconds())) {
			boostCap = Meth.systemTime() + 1000;
		}
		Vects.calcVect.set(player.getPosition());
		Vects.calcVect.y += 1;

		// if(CommandProcessor.isClient){
		// rotY = MultiplayerData.otherRotY;
		// position = MultiplayerData.otherPos;
		//// System.out.println(rotY);
		// }else{
		float m = (Vects.calcVect.x - position.x) / (Vects.calcVect.z - position.z);
		rotY = (float) (Math.atan(m)) * Meth.radToAng;
		// }

		Vects.calcVect.set(Vects.calcVect.x - position.x, Vects.calcVect.y - position.y, Vects.calcVect.z - position.z);
		// if(Vects.calcVect.x != 0 || Vects.calcVect.z != 0){
		// float l =
		// (float)Math.sqrt(Vects.calcVect.x*Vects.calcVect.x+Vects.calcVect.z*Vects.calcVect.z);
		// Vects.calcVect.x *= RUNSPEED/l;
		// Vects.calcVect.z *= RUNSPEED/l;
		// }
		if (Vects.calcVect.lengthSquared() != 0) {
			Vects.calcVect.normalize();
			Vects.calcVect.mul(RUNSPEED);
		}

		velocity.x = Meth.gehZuWert(velocity.x, RUNSPEEDACCEL * DisplayManager.getFrameTimeSeconds(), Vects.calcVect.x);
		velocity.z = Meth.gehZuWert(velocity.z, RUNSPEEDACCEL * DisplayManager.getFrameTimeSeconds(), Vects.calcVect.z);
		velocity.y = Meth.gehZuWert(velocity.y, RUNSPEEDACCEL * DisplayManager.getFrameTimeSeconds(), Vects.calcVect.y);
		
		if (Meth.systemTime() < boostCap) {
			velocity.set(Vects.calcVect);
			velocity.mul(50);
			// if(scale < BIGSCALE){
			// scale += BIGSCALE*DisplayManager.getFrameTimeSeconds();
			// }else{
			scale = BIGSCALE;
			Vects.calcVect.set(player.getPosition());
			Vects.calcVect.y++;
			if(position.distanceSquared(Vects.calcVect) <= 4) {
				boostCap = 0;
				player.influence(velocity.mul(0.5f));
				destroy();
			}
			
			// }
		} else {
			// if(scale > 0.5f){
			// scale -= BIGSCALE*DisplayManager.getFrameTimeSeconds();
			// }else{
			scale = 0.5f;
			// }
		} 

	}

	private static final float BIGSCALE = 1;
	private long boostCap;// , lastExplosion

	private void move() {
		if (Meth.systemTime() > boostCap) {
			TERRAINHEIGHT = CNH();

			if (position.y - bodnoffset <= TERRAINHEIGHT) {
				inAir = false;
			} else if (position.y - bodnoffset >= TERRAINHEIGHT + 0.1f) {
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
				velocity.y += Meth.GRAVITY * DisplayManager.getFrameTimeSeconds();
			} else {
				if (velocity.y < 0) {
					velocity.y = 0;
				}
				velocity.x *= fact;
				velocity.z *= fact;
			}
			if (water > 0) {
				velocity.y *= fact;
			}
			// if (!flight) {
			short b = 0, firstb;
			if (velocity.y > 0) {
				b = ChunkManager.getBlockID(position.x, position.y - bodnoffset + HEIGHT * 1.1f, position.z);
				if (!Block.passable(b)) {// && !b.isPassable()
					velocity.y = 0;
				}
			}

			// ECKE ... SCHLECHT ---> AABB; nicht in richtung prüfen, sondern
			// plain old 2D collision
			boolean BOTHGO = true;
			int sovx = Meth.vorzeichen(velocity.x);
			float D = DisplayManager.getFrameTimeSeconds();
			b = ChunkManager.getBlockID(position.x + (velocity.x * D) + sovx * fatness, position.y + 0.1f - bodnoffset,
					position.z);
			firstb = b;
			if (!Block.passable(b) && Block.getYDraufTretPos(b) - bodnoffset
					+ (int) Math.floor(position.y + 0.1f) >= position.y + 0.6f - bodnoffset) {
				b = ChunkManager.getBlockID(position.x + (velocity.x * D + sovx * fatness),
						position.y + 1.1f - bodnoffset, position.z);
				if (Block.passable(b)) {
					jump();
				}
				velocity.x = 0;
				waitingTime += DisplayManager.getFrameTimeSeconds();
				BOTHGO = false;
			} else {
				if (!Block.passable(firstb) && velocity.y <= 0 && position.y <= Block.getYDraufTretPos(firstb)
						- bodnoffset + (int) Math.floor(position.y + 0.1f) + 0.05f) {
					position.y = Block.getYDraufTretPos(firstb) - bodnoffset + (int) Math.floor(position.y + 0.1f);
				}
			}
			int sovz = Meth.vorzeichen(velocity.z);
			b = ChunkManager.getBlockID(position.x, position.y + 0.1f - bodnoffset,
					position.z + velocity.z * D + sovz * fatness);
			firstb = b;
			if (!Block.passable(b) && Block.getYDraufTretPos(b)
					+ (int) Math.floor(position.y + 0.1f - bodnoffset) >= position.y + 0.6f - bodnoffset) {
				b = ChunkManager.getBlockID(position.x, position.y + 1.1f - bodnoffset,
						position.z + velocity.z * D + sovz * fatness);
				if (Block.passable(b)) {
					jump();
				}
				velocity.z = 0;
				waitingTime += DisplayManager.getFrameTimeSeconds();
				BOTHGO = false;
			} else {
				if (!Block.passable(firstb) && velocity.y <= 0 && position.y <= Block.getYDraufTretPos(firstb)
						+ (int) Math.floor(position.y + 0.1f - bodnoffset) + 0.05f) {
					position.y = Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f);
				}
			}

			if (BOTHGO) {
				b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness,
						position.y + 0.1f - bodnoffset, position.z + velocity.z * D + sovz * fatness);
				firstb = b;
				if (!Block.passable(b) && Block.getYDraufTretPos(b)
						+ (int) Math.floor(position.y + 0.1f - bodnoffset) >= position.y + 0.6f - bodnoffset) {
					b = ChunkManager.getBlockID(position.x + velocity.x * D + sovx * fatness,
							position.y + 1.1f - bodnoffset, position.z + velocity.z * D + sovz * fatness);
					if (Block.passable(b)) {
						jump();
					}
					velocity.x = 0;
					velocity.z = 0;
					waitingTime += DisplayManager.getFrameTimeSeconds();
				} else {
					if (!Block.passable(firstb) && velocity.y <= 0 && position.y <= Block.getYDraufTretPos(firstb)
							- bodnoffset + (int) Math.floor(position.y + 0.1f) + 0.05f) {
						position.y = Block.getYDraufTretPos(firstb) + (int) Math.floor(position.y + 0.1f);
					}
				}
			}

			if (waitingTime > 1) {
				waitingTime = 0;
				if (Vects.calcVect2D.lengthSquared() != 0) {
					Vects.calcVect2D.normalize();
				}
				Intraface.deleteBlock(position.x + Vects.calcVect2D.x, position.y, position.z + Vects.calcVect2D.y);
			}
		} else {
			if (Meth.doChance(DisplayManager.getFrameTimeSeconds()) || ChunkManager.getBlockID(position) != Block.AIR) {// 10*Di...
				SimpleConstructs.EXPLOSION(position, (int) (BIGSCALE));
			}
		}
		water = 0;

		position.x += velocity.x * DisplayManager.getFrameTimeSeconds();
		position.y += velocity.y * DisplayManager.getFrameTimeSeconds();
		if (position.y < TERRAINHEIGHT) {
			position.y = TERRAINHEIGHT;
		}
		position.z += velocity.z * DisplayManager.getFrameTimeSeconds();

		position.x += outSideSpeed.x * DisplayManager.getFrameTimeSeconds();
		position.y += outSideSpeed.y * DisplayManager.getFrameTimeSeconds();
		position.z += outSideSpeed.z * DisplayManager.getFrameTimeSeconds();

		outSideSpeed.x *= 0.9f;
		outSideSpeed.z *= 0.9f;
		outSideSpeed.y *= 0.9f;

		if (Meth.systemTime() < boostCap) {
			velocity.set(0);
		}

	}

	public void destroy() {
		EntityManager.removeEntity(this);
		SimpleConstructs.EXPLOSION(position, 7);
	}

}
