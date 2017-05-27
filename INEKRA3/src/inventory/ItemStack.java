package inventory;

import static blockRendering.BlockRenderer.ordner;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import controls.Keyboard;
import controls.Mouse;
import data.Block;
import data.ChunkManager;
import dataAdvanced.SimpleConstructs;
import entities.*;
import gameStuff.SC;
import gameStuff.WorldObjects;
import line.Quad;
import mobs.Cube;
import network.MultiplayerData;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.*;
import weather.Meteorite;
import weather.WeatherController;

public class ItemStack {

	public static final int MAX_ITEMS = 16;
	public static final int STONE = 0, SAND = 1, GRASS = 2, DIRT = 3, LEAVES = 4, SAPLING = 5, STONESLAB = 6,
			GRAVEL = 7, TNT = 8, GLASS = 9, WATER = 10, MARBLE = 11, WOOD = 12, TORCH = 13, BOOM = 14, KA = 15,
			NONE = Integer.MAX_VALUE;

	public int get3DTex() {
		String path;
		switch (ID) {
		case STONE:
			path = ordner + "Stone";
			break;
		case SAND:
			path = ordner + "Sand";
			break;
		case DIRT:
			path = ordner + "Dirt";
			break;
		case GRASS:
			path = ordner + "GrassTop";
			break;
		case GLASS:
			path = ordner + "Glass";
			break;
		case MARBLE:
			path = ordner + "Marble";
			break;
		case STONESLAB:
			path = ordner + "unique";
			break;
		case WOOD:
			path = ordner + "WoodSide";
			break;
		case GRAVEL:
			path = ordner + "Gravel";
			break;
		case TORCH:
			path = "button";
			break;
		case BOOM:
			path = "boom";
			break;
		case KA:
			path = ordner + "ka";
			break;
		case LEAVES:
			path = ordner + "Leave";
			break;
		default:
			path = "water";
		}
		return SC.getTex(path).getID();
	}
	
	public int getTex(){
		return ItemRenderer.getItemTex(this);
	}
	
	private static int[] stackSizes = new int[MAX_ITEMS];
	static {
		stackSizes[STONE] = 64;
		stackSizes[SAND] = 64;
		stackSizes[GRASS] = 64;
		stackSizes[DIRT] = 64;
		stackSizes[LEAVES] = 64;
		stackSizes[SAPLING] = 64;
		stackSizes[STONESLAB] = 64;
		stackSizes[GRAVEL] = 64;
		stackSizes[TNT] = 64;
		stackSizes[GLASS] = 64;
		stackSizes[WATER] = 64;
		stackSizes[MARBLE] = 64;
		stackSizes[WOOD] = 64;
		stackSizes[TORCH] = 64;
		stackSizes[BOOM] = 64;
		stackSizes[KA] = 64;
	}

	private int ID, size;
//	private int tex;

	/**
	 * @param ID
	 *            ItemID. Stehen in {@link ItemStack}
	 * @param NoI
	 *            Number of Items
	 */
	public ItemStack(int ID, int NoI) {
		this.ID = ID;
		this.size = NoI;
		// tex = new GuiTexture(getTex(ID), new Vector2f(0.9f, 0), texScale);
		// text = new GUIText("" + NoI, 1, SC.font, new Vector2f(), 0.1f,
		// true);// Block.string(blockID(ID))
		// +
		// text.setColour(0, 0, 1);
		// if (Inv2DDep.open) {
		// tex.show();
		// } else {
		// text.hide();
		// }
//		tex = ItemRenderer.getItemTex(this);
	}

	// private static Vector2f texScale = new Vector2f(0.05f, 0.05f);

	private static long lastTime = Meth.systemTime();
	private static boolean lastL = false, lastR = false;
	private static short lookAtB, lastB;
	private static Vector3f pos;

	private static Quad quader = new Quad(0, 0, 0, 0, 0, 0);
	static {
		quader.setBlocky();
	}
	private static boolean set1 = false, set2 = false, built = false, nextblock = true, repSet = false;
	private static float dist = 5;
	private static Vector3f someVect;

	public boolean action() {
		if (ID == BOOM) {
			if (Mouse.isButtonDown(1) && Meth.systemTime() > lastTime + 500) {
				if(!Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
					Vector3f p = MousePicker.getPoint(30).sub(Camera.getPosition());
					Projectil pro = new Projectil(new Vector3f(Camera.getPosition()), p, PTM.projectile,
							WorldObjects.player, 5, 10, 1, true);
					pro.setGravity(0.2f);
					pro.setFlare();
					WorldObjects.player.influence(new Vector3f(-p.x / 10, -p.y / 10, -p.z / 10));
				}else{
					Vector3f p = MousePicker.getPoint(50).sub(Camera.getPosition());
					new Meteorite(new Vector3f(Camera.getPosition()), p);
					WorldObjects.player.influence(new Vector3f(-p.x / 10, -p.y / 10, -p.z / 10));
				}
				lastTime = Meth.systemTime();
				
			} else if (Mouse.isButtonDown(0) && Meth.systemTime() > lastTime + 333) {
				Vector3f ipos = new Vector3f(Camera.getPosition());
				ipos.x += 2*Meth.sinDeg(180-Camera.getYaw()-40);
				ipos.z += 2*Meth.cosDeg(180-Camera.getYaw()-40);
				
				Vector3f s = MousePicker.getNextFilledBlockCoord(100, true, true);
				if(s == null)
					s = MousePicker.getPoint(100);
				WeatherController.lstrike(ipos, s);
				lastTime = Meth.systemTime();
//				Vector3f p = Meth.scaleToLength(MousePicker.getPoint(1000).sub(ipos), 30);
//				LASER l = new LASER(ipos.x, ipos.y, ipos.z, p.x, p.y, p.z);
//				l.setDestroying(true);
//				p.normalize();
//				WorldObjects.player.influence(new Vector3f(-p.x, -p.y, -p.z));
//				// ParticleMaster.addNewParticle(PTM.l, new Vector3f(ipos), new
//				// Vector3f(p), 0, 5, 0, 1);
			}
		} else if (this.ID == STONESLAB) {
			if (Mouse.isButtonDown(0)) {
				pos = MousePicker.getNextFilledBlockCoord(5);
				if (pos != null) {
					lookAtB = ChunkManager.getBlockID(pos);
					if (lookAtB != lastB) {
						lastTime = Meth.systemTime();
						lastB = lookAtB;
					}
				}
				if (!lastL) {
					if (pos != null) {
						lastL = true;
						lastTime = Meth.systemTime();
					}
				} else if (pos != null && Meth.systemTime() >= lastTime + Block.getBreakCool(lookAtB)) {
					ChunkManager.deleteBlockWithNoise(pos);
					lastTime = Meth.systemTime();
					lastL = false;
				} else if (pos != null) {
					ParticleMaster.addNewParticle(PTM.projectile, new Vector3f(pos).add(0.5f, 0.5f, 0.5f),
							Vects.randomVector3f(-1, 1, -1, 1, -1, 1), 0.01f, 1, 0, 0.2f);
				}
			} else if (Mouse.isButtonDown(1)) {
				lastL = false;
				if (!lastR) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						lastR = true;
						lastTime = Meth.systemTime();
					}
				} else if (Meth.systemTime() >= lastTime + 200) {
					pos = MousePicker.getLastEmptyBlockCoordWithOrientation(5);
					if (pos != null) {
						ChunkManager.setBlockID(pos,
								MousePicker.calcVect.y < 0 ? Block.STONESLAB_UP : Block.STONESLAB_DOWN);
						size--;
						if (size <= 0) {
							return false;
						} else {
							// text.setText("" + size);
						}
					}
					lastR = false;
					lastTime = Meth.systemTime();
				}
			} else {
				lastL = false;
				if (Keyboard.isKeyDown(GLFW.GLFW_KEY_M) && Meth.systemTime() > lastTime + 500) {
					Vector3f vect = MousePicker.getLastEmptyBlockCoord(20);
					if (vect != null) {
						new Cube(vect);
					} else {
						vect = MousePicker.getPoint(5);
						new Cube(vect);
					}
					lastTime = Meth.systemTime();
				}
			}
		} else if (this.ID == KA) {
			if (Keyboard.keyTipped(GLFW.GLFW_KEY_N)) {
				nextblock = !nextblock;
			}
			Vector3f v;
			if (nextblock) {
				v = MousePicker.getLastEmptyBlockCoord(100);
			} else {
				Vector3f p = MousePicker.getPoint(dist);
				if (Meth.doChance(10 * DisplayManager.getFrameTimeSeconds()))// Vects.randomVector3f(-1,
																				// 1,
																				// -1,
																				// 1,
																				// -1,
																				// 1)
					ParticleMaster.addNewParticle(PTM.cosmic, new Vector3f(p), Vects.NULL, 0, 1, 0,
							Meth.randomFloat(0.1f, 0.3f));
				Vects.floor(p);
				v = p;
			}
			if (Keyboard.keyTipped(GLFW.GLFW_KEY_T)) {
				Vector3f x = MousePicker.getNextFilledBlockCoord(100);
				if (x != null)
					WeatherController.lstrikeback(x);
			}
			if (Mouse.isButtonDown(0)) {
				if (v != null) {
					if (built) {
						set1 = false;
						set2 = false;
						built = false;
					}
					if (!set1 || set2) {
						set1 = true;
						set2 = false;
						// Out.println(1);
						quader.set1(v.x, v.y, v.z);
					} else if (!set2) {
						quader.set2(v.x, v.y, v.z);
					}
				}
			} else {
				if (set1 && !set2 && v != null) {
					set2 = true;
				}
			}
			boolean del = Keyboard.isKeyDown(GLFW.GLFW_KEY_DELETE);
			if (Meth.systemTime() > lastTime + 500 && set1 && set2 && (Mouse.buttonClickedThisFrame(1) || del)) {
				short id;
				if (!del) {
					v = MousePicker.getNextFilledBlockCoord(105, false);
					id = v == null ? Block.AIR : ChunkManager.getBlockID(v);
				} else {
					id = Block.AIR;
				}
				built = true;
				int x1, x2, y1, y2, z1, z2;
				if (quader.x1() < quader.x2()) {
					x1 = (int) Math.floor(quader.x1());
					x2 = (int) Math.floor(quader.x2());
				} else {
					x1 = (int) Math.floor(quader.x2());
					x2 = (int) Math.floor(quader.x1());
				}
				if (quader.y1() < quader.y2()) {
					y1 = (int) Math.floor(quader.y1());
					y2 = (int) Math.floor(quader.y2());
				} else {
					y1 = (int) Math.floor(quader.y2());
					y2 = (int) Math.floor(quader.y1());
				}
				if (quader.z1() < quader.z2()) {
					z1 = (int) Math.floor(quader.z1());
					z2 = (int) Math.floor(quader.z2());
				} else {
					z1 = (int) Math.floor(quader.z2());
					z2 = (int) Math.floor(quader.z1());
				}
				ChunkManager.dontDropItems();
				SimpleConstructs.fill(x1, y1, z1, x2, y2, z2, id);
				ChunkManager.dropItems();
				// Out.println("built! " + Block.string(id));
				lastTime = Meth.systemTime();
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_U) && !repSet) {
				someVect = MousePicker.getNextFilledBlockCoord(100);
				if (someVect != null) {
					repSet = true;
					Vects.floor(someVect);
				}
			} else {
				if (repSet && !Keyboard.isKeyDown(GLFW.GLFW_KEY_U)) {
					v = MousePicker.getNextFilledBlockCoord(100);
					if (v != null) {
						repSet = false;
						SimpleConstructs.replaceAllAncients(someVect, ChunkManager.getBlockID(v), 1000);
					}
				} else if (Keyboard.isKeyDown(GLFW.GLFW_KEY_U) && someVect != null
						&& Meth.doChance(10 * DisplayManager.getFrameTimeSeconds())) {
					ParticleMaster.addNewParticle(PTM.projectile, new Vector3f(someVect).add(0.5f, 0.5f, 0.5f),
							Vects.randomVector3f(-1, 1, -1, 1, -1, 1), 0, 2, 0, 0.2f);
				}
			}

			if (Meth.systemTime() > lastTime + 5000) {
				if (Keyboard.isKeyDown(GLFW.GLFW_KEY_O)) {
					v = MousePicker.getLastEmptyBlockCoord(100);
					if (v != null) {
						Vects.floor(v);
						for (short id = 0; id <= Block.lastNormalBlock(); id++) {
							ChunkManager.setBlockID(v, id);
							v.x++;
						}
						lastTime = Meth.systemTime();
					}
				}
			}

			if (Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_5)) {
				v = MousePicker.getLastEmptyBlockCoord(100);
				if (v != null) {
					ChunkManager.setBlockID(v, Block.MARK);
				}
			}
		} else if (this.ID == TORCH) {
			if (Mouse.isButtonDown(0)) {
				lastR = false;
				if (!lastL) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						lastL = true;
						lastTime = Meth.systemTime();
					}
				} else if (Meth.systemTime() >= lastTime + 200) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						ChunkManager.setBlockID(pos, Block.LAMP);
						size--;
						if (size <= 0) {
							return false;
						} else {
							// text.setText("" + size);
						}
					}
					lastL = false;
					lastTime = Meth.systemTime();
				}
			}
			if (Mouse.isButtonDown(1)) {
				lastL = false;
				if (!lastR) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						lastR = true;
						lastTime = Meth.systemTime();
						// lookAtB = ChunkManager.getBlockID(pos);
					}
				} else if (Meth.systemTime() >= lastTime + 200) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						ChunkManager.setBlockID(pos, Block.TORCH);
						size--;
						if (size <= 0) {
							return false;
						} else {
							// text.setText("" + size);
						}
					}
					lastR = false;
					lastTime = Meth.systemTime();
				}
			}
		} else if (this.ID == NONE) {
			if (Mouse.isButtonDown(0)) {
				pos = MousePicker.getNextFilledBlockCoord(5);
				if (pos != null) {
					lookAtB = ChunkManager.getBlockID(pos);
					if (lookAtB != lastB) {
						lastTime = Meth.systemTime();
						lastB = lookAtB;
					}
				}
				if (!lastL) {
					if (pos != null) {
						lastL = true;
						lastTime = Meth.systemTime();
					}
				} else if (pos != null && Meth.systemTime() >= lastTime + Block.getBreakCool(lookAtB)) {
					ChunkManager.deleteBlockWithNoise(pos);
					lastTime = Meth.systemTime();
					lastL = false;
				} else if (pos != null) {
					ParticleMaster.addNewParticle(PTM.projectile, new Vector3f(pos).add(0.5f, 0.5f, 0.5f),
							Vects.randomVector3f(-1, 1, -1, 1, -1, 1), 0.01f, 1, 0, 0.2f);
				}
			} else {
				lastL = false;
			}
		} else if (this.ID != TNT) {
			if (Mouse.isButtonDown(0)) {
				pos = MousePicker.getNextFilledBlockCoord(5);
				if (pos != null) {
					lookAtB = ChunkManager.getBlockID(pos);
					if (lookAtB != lastB) {
						lastTime = Meth.systemTime();
						lastB = lookAtB;
					}
				}
				if (!lastL) {
					if (pos != null) {
						lastL = true;
						lastTime = Meth.systemTime();
					}
				} else if (pos != null && Meth.systemTime() >= lastTime + Block.getBreakCool(lookAtB)) {
					ChunkManager.deleteBlockWithNoise(pos);
					lastTime = Meth.systemTime();
					lastL = false;
				} else if (pos != null) {
					ParticleMaster.addNewParticle(PTM.projectile, new Vector3f(pos).add(0.5f, 0.5f, 0.5f),
							Vects.randomVector3f(-1, 1, -1, 1, -1, 1), 0.01f, 1, 0, 0.2f);
				}
			} else if (Mouse.isButtonDown(1) && blockID(ID) != Block.AIR) {
				lastL = false;
				if (!lastR) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						lastR = true;
						lastTime = Meth.systemTime();
						// lookAtB = ChunkManager.getBlockID(pos);
					}
				} else if (Meth.systemTime() >= lastTime + 200) {
					pos = MousePicker.getLastEmptyBlockCoord(5);
					if (pos != null) {
						ChunkManager.setBlockID(pos, blockID(ID));
						size--;
						if (size <= 0) {
							return false;
						} else {
							// text.setText("" + size);
						}
					}
					lastR = false;
					lastTime = Meth.systemTime();
				}
			} else {
				lastL = false;
				if (Keyboard.isKeyDown(GLFW.GLFW_KEY_M) && Meth.systemTime() > lastTime + 500) {
					Vector3f vect = MousePicker.getLastEmptyBlockCoord(20);
					if (vect != null) {
						new Cube(vect);
					} else {
						vect = MousePicker.getPoint(5);
						new Cube(vect);
					}
					lastTime = Meth.systemTime();
				} else if (Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_K)) {
					Vector3f vect = MousePicker.getLastEmptyBlockCoord(20);
					if (vect != null) {
						new Entity(SC.playermod, 0, MultiplayerData.otherPos, 0, 0, 0, 0.15f, false) {
							@Override
							public void update() {
								rotY = MultiplayerData.otherRotY;
								velocity = MultiplayerData.otherVel;
								super.update();
							}
						};
					} else {
						vect = MousePicker.getPoint(5);
						new Entity(SC.playermod, 0, MultiplayerData.otherPos, 0, 0, 0, 0.15f, false) {
							@Override
							public void update() {
								rotY = MultiplayerData.otherRotY;
								velocity = MultiplayerData.otherVel;
								super.update();
							}
						};
					}
				}
			}
		} else {
			if ((Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && Meth.systemTime() > lastTime + 1000) {
				Vector3f vect = MousePicker.getNextFilledBlockCoord(20);
				if (vect != null) {
					SimpleConstructs.EXPLOSION(vect, 5);
					size--;
					lastTime = Meth.systemTime();
					if (size <= 0) {
						return false;
					} else {
						// text = new GUIText(Block.string(blockID(ID)) + size,
						// 1, SC.font, text.getPosition(), 0.1f,
						// true);
						// text.setColour(0, 0, 1);
						// text.setText(Block.string(blockID(ID)) + size);
					}
				}
			}
		}
		return true;
	}

	public int ID() {
		return ID;
	}

	public int size() {
		return size;
	}

	public static short blockID(int item) {
		switch (item) {
		case DIRT:
			return Block.DIRT;
		case GRASS:
			return Block.GRASS;
		case STONE:
			return Block.STONE;
		case SAND:
			return Block.SAND;
		case STONESLAB:
			return Block.STONESLAB_DOWN;
		case GLASS:
			return Block.GLASS;
		case WATER:
			return Block.max_water;
		case MARBLE:
			return Block.MARBLE;
		case WOOD:
			return Block.WOOD;
		case TORCH:
			return Block.TORCH;
		case GRAVEL:
			return Block.GRAVEL;
		case KA:
			// return Block.KA;
			return Block.TALL_GRASS;
		case LEAVES:
			return Block.LEAVES;
		default:
			return Block.AIR;
		}
	}

	public static int itemID(short blockID) {
		switch (blockID) {
		case Block.DIRT:
			return DIRT;
		case Block.GRASS:
			return GRASS;
		case Block.STONE:
			return STONE;
		case Block.SAND:
			return SAND;
		case Block.STONESLAB_UP:
		case Block.STONESLAB_DOWN:
			return STONESLAB;
		case Block.GLASS:
			return GLASS;
		case Block.MARBLE:
			return MARBLE;
		case Block.WOOD:
			return WOOD;
		case Block.TORCH:
			return TORCH;
		case Block.TALL_GRASS:
			return KA;
		case Block.GRAVEL:
			return GRAVEL;
		case Block.LEAVES:
			return LEAVES;
		default:
			if (Block.isWater(blockID)) {
				return WATER;
			} else {
				return 0;
			}
		}
	}

	// private GUIText text;

	// public void addTex(int i) {
	// tex.getPos().y = i * 0.1f;
	// tex.show();
	// text.setPosition(0.9f, 0.5f - i * 0.05f);
	// text.show();
	// }
	//
	// public void setTexPos(int i) {
	// tex.getPos().y = i * 0.1f;
	// text.setPosition(0.9f, 0.5f - i * 0.05f);
	// }
	//
	// public void removeTex() {
	// tex.hide();
	// text.hide();
	// }
	//
	// public void cleanUp() {
	// removeTex();
	// text.cleanUp();
	// }

	/**
	 * check for size limit?
	 */
	public boolean add() {
		if (remainingSpace() > 0) {
			size++;
			return true;
		} else {
			return false;
		}
		// text.setText("" + size);
		// text.setColour(0, 0, 1);
	}

	public int remainingSpace() {
		return stackSizes[ID] - size;
	}

	@Override
	public String toString() {
		return "" + size;// Block.string(blockID(ID)) + " " + size
	}

	public short blockID() {
		return blockID(ID);
	}

	public boolean isBlockItem() {
		return blockID() != 0;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	// public boolean clicked() {
	// return (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
	// && Mouse.getX()-DisplayManager.getWidth()*0.5f >= tex.getPixelPos().x &&
	// Mouse.getX()-DisplayManager.getWidth()*0.5f <= tex.getPixelPos().x +
	// tex.getPixelScale().x
	// && Mouse.getY()-DisplayManager.getHeight()*0.5f >= tex.getPixelPos().y &&
	// Mouse.getY()-DisplayManager.getHeight()*0.5f <= tex.getPixelPos().y -
	// tex.getPixelScale().y;
	// }
	//
	// public String getBorders(){
	// Vects.calcVect2D.set(tex.getPixelPos());
	// return "MIN: " + tex.getPixelPos() + " MAX: " +
	// Vects.calcVect2D.sub(tex.getPixelScale());
	// }
	//
	// public void setAbsTexPos(int x, int y) {
	// tex.getPixelPos().set(x, y);
	// }
	//
	// public String texPos() {
	// return tex.getPos().toString();
	// }

}
