package inventory;

import java.util.ArrayList;

import org.joml.Vector3f;

import blockRendering.BlockRenderer;
import data.Block;
import data.ChunkManager;
import entities.Entity;
import gameStuff.*;
import models.TexturedModel;
import renderStuff.DisplayManager;
import toolBox.Meth;

/**
 * a entity representing a Item in 3D. Is rendered using the
 * {@link renderStuff.EntityRenderer}. The items rotate!
 * 
 * @author xaver
 *
 */
public class Item3D extends Entity {

	public static final ArrayList<Item3D> is = new ArrayList<Item3D>();

	private float x = Meth.randomFloat(-30, 30);
	private float y = Meth.randomFloat(-30, 30);
	private float z = Meth.randomFloat(-30, 30);

	private float plusrot = 0;

	private static float lifeTime = 7.5f;

	private String id;
	private short bID;
	private int maxStackSize;
	private int stackSize = 1;
	private boolean moving;

	private float timeCreated;

	private Item3D(TexturedModel m, Vector3f position, float size, float plusrot) {
		super(m, 0, position, 0, 0, 0, size, false);
		this.plusrot = plusrot;
		timeCreated = Meth.time();
		is.add(this);
	}

	public static Item3D getBlockInstance(short blockID, Vector3f position, boolean moving) {
		float size = 0.075f;
		float plusrot = 0;
		Item3D i = new Item3D(SC.getModel("cube", BlockRenderer.ordner + Block.getFileName(blockID)), position, size,
				plusrot);
		i.bID = blockID;
		i.moving = moving;
		return i;
	}

	public static Item3D getInstance(String id, Vector3f position, boolean moving) {
		TexturedModel m = null;
		float size = 0.075f;
		float plusrot = 0;
		boolean[] rots = new boolean[3];
		switch (id) {
		case "pick":
			m = SC.getModel("pick-90", id);
			size = 0.3f;
			rots[0] = false;
			rots[1] = true;
			rots[2] = false;
			plusrot = -90;
			break;
		case "treeplanter":
			m = SC.getModel("lowPolyTree", "tree");
			size = 0.02f;
			rots[0] = false;
			rots[1] = true;
			rots[2] = false;
			break;
		case "gun":
			m = SC.getModel("gun-90", "gun");
			size = 0.3f;
			rots[0] = true;
			rots[1] = true;
			rots[2] = true;
			plusrot = 180;
			break;
		case "snowlayer":
			m = SC.getModel(id, id);
			size = 0.2f;
			rots[0] = true;
			rots[1] = true;
			rots[2] = true;
			break;
		case "waterbucket":
			m = SC.getModel("fern", "WATER");
			size = 0.05f;
			rots[0] = true;
			rots[1] = true;
			rots[2] = true;
			break;
		case "bucket":
			m = SC.getModel("fern", "heal");
			size = 0.05f;
			rots[0] = true;
			rots[1] = true;
			rots[2] = true;
			break;
		// case "chainsaw":
		// m = SC.getModel("Playerthing", "playerTexture");
		// size = 0.01f;
		// rots[1] = true;
		default:
			m = SC.getModel("cube", id);
			rots[0] = true;
			rots[1] = true;
			rots[2] = true;
		}
		Item3D i = new Item3D(m, position, size, plusrot);
		i.id = id;
		i.moving = moving;
		// i.test();
		if (!rots[0]) {
			i.x = 0;
		}
		if (!rots[1]) {
			i.y = 0;
		}
		if (!rots[2]) {
			i.z = 0;
		}
		return i;
	}

	private boolean used;

	public void setUsed(boolean b) {
		used = b;
	}
	
	private static final float pickupDistSq = 1;
	
	@Override
	public void update() {
		if(stackSize < 64 && Meth.doChance(0.1f)){
			int i = Meth.randomInt(0, is.size()-1);
			if(i != is.indexOf(this)){
				Item3D I = is.get(i);
//				Err.err.println("HALLO! " + (I == null));
				if(I.bID == bID && I.stackSize <= stackSize && I.stackSize < 64){
					if(I.position.distanceSquared(position) < pickupDistSq){
						int rem = 64 - stackSize;
						if(rem > I.stackSize)rem = I.stackSize;
						I.stackSize -= rem;
						stackSize += rem;
						if(I.stackSize <= 0){
							I.cleanUp();
						}
					}
				}
			}
		}
		
		if (!used) {
			rotate(x * DisplayManager.getFrameTimeSeconds(), y * DisplayManager.getFrameTimeSeconds(),
					z * DisplayManager.getFrameTimeSeconds());
		}
		if (moving) {
			short b = ChunkManager.getBlockID(position.x, position.y - 0.25f, position.z);
			if (b == Block.AIR) {
				velocity.y += Meth.GRAVITY * DisplayManager.getFrameTimeSeconds();
			} else if (!Block.passable(b)) {
				position.y = (int) Math.floor(position.y - 0.25f) + 0.2f + (Block.BLOCKSIZE);
				this.addReibung(10);
			}
			if (!alive()) {
				hide();
			}
			super.update();
		}
	}

	public float getPlusRot() {
		return plusrot;
	}

	public boolean alive() {
		return ((Meth.time() - timeCreated < lifeTime) || !Meth.doChance(DisplayManager.getFrameTimeSeconds()));
	}

	public void hide() {
		EntityManager.removeEntity(this);
	}

	public void show() {
		EntityManager.addEntity(this);
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}
	
	public int getCurrentSize(){
		return stackSize;
	}

	public boolean isBlock() {
		return bID != Block.AIR;
	}

	public short BID() {
		return bID;
	}

	public String id() {
		return id;
	}

	// private void test() {
	// boolean passt = false;
	// for (int i = 0; i < possibleItems.length; i++) {
	// if (id.equals(possibleItems[i])) {
	// passt = true;
	// maxStackSize = stackSizes[i];
	// }
	// }
	// if (!passt) {
	// Err.err.println("Item nicht zulässig!");
	// MainLoop.cleanUp();
	// System.exit(-1);
	// }
	// }

	public void cleanUp() {
		EntityManager.removeEntity(this);
		is.remove(this);
	}

	public boolean removeOne() {
		stackSize--;
		if(stackSize == 0){
			cleanUp();
			return false;
		}else{
			return true;
		}
	}

	public void setNOB(int size) {
		stackSize = size;
	}

}
