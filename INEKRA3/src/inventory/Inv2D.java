package inventory;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import controls.*;
import data.ChunkSaver;
import entities.*;
import gameStuff.*;
import guis.GUIManager;
import guis.GuiTexture;
import menuThings.*;
import menuThings2.Pane;
import models.RawModel;
import models.TexturedModel;
import renderStuff.DisplayManager;
import textures.ModelTexture;
import toolBox.*;

public class Inv2D {

	public static int INV_SWITCH = GLFW.GLFW_KEY_E;
	public static boolean open = false;

	public static final int emptyStackTex = SC.getTex("invStackBack").getID();

	private GuiTexture background, chosenItemStackIndicator;
	private ItemStack[][] stacks;
	private Button[][] buttons;
	private int cx, cy;
	private int chosen = 0;
	private boolean buttonClick = false;

	private static final int yrows = 4;
	private static final int exy = yrows - 1;
	private final float W = 30, H;
	private final float space = 1.5f;
	private final float ka = 0.5f;
	private final float YO = 500;

	private Entity handcontents;
	
	private Pane pane;

	public Inv2D() {
		pane = new Pane();
		
		background = new GuiTexture(SC.getTex("menu").getID(), new Vector2f(), new Vector2f(0.5f, 0.5f), false);
		stacks = new ItemStack[9][yrows];
		buttons = new Button[9][yrows];
		H = (int) (W * DisplayManager.desiredRatioForGUI);
		chosenItemStackIndicator = new GuiTexture(SC.getTex("texPack/Border").getID(), new Vector2f(),
				new Vector2f(W / 1000.0f, H / 1000.0f), true);
		chosenItemStackIndicator.setDisplayLevel(10);
		chosenItemStackIndicator.show();
		pane.attach(chosenItemStackIndicator);
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < yrows; y++) {
				final int X = x;
				final int Y = y;
				buttons[x][y] = new Button(new Rectangle(((x + ka - 4.5f) * space * W + 500) - W / 2,
						((y + ka - yrows * 0.5f) * space * H + YO) - H / 2, W, H)) {
					@Override
					public void leftClick() {
						if (stacks[X][Y] != null) {
							cx = X;
							cy = Y;
							buttonClick = true;
							cb = this;
							gtex.setDisplayLevel(GUIManager.UP);
						}
					}

					@Override
					public void leftHold() {
						if (buttonClick && cb == this)
							setPosition((Mouse.getAX() * 1000) - W / 2, (Mouse.getAY() * 1000) - H / 2);
					}

					@Override
					public void leftLeft() {
						if (!buttonClick)
							return;
						float mx = Mouse.getAX() * 1000;
						float my = Mouse.getAY() * 1000;
						int sx = (int) (((mx + W / 2 - 500) / (space * W)) + 4.5f - ka);
						int sy = (int) (((my + H / 2 - YO) / (space * H)) + yrows * 0.5f - ka);
						
						if(sx == cx && sy == cy){
							setPosition(((X + ka - 4.5f) * space * W + 500) - W / 2,
									((Y + ka - yrows * 0.5f) * space * H + YO) - H / 2);
							buttonClick = false;
							cb = null;
							gtex.setDisplayLevel(GUIManager.DOWN);
							return;
						}
						
						if (sx >= 0 && sx < 9 && sy >= 0 && sy < yrows) {
							ItemStack lastStack = stacks[sx][sy];
							stacks[sx][sy] = stacks[cx][cy];
							if(lastStack != null && lastStack.ID() == stacks[sx][sy].ID()){
								int size = Math.max(lastStack.size() - stacks[sx][sy].remainingSpace(), 0);
								for(int i = lastStack.size(); i > size; i--){
									stacks[sx][sy].add();
								}
								if(size > 0){
									lastStack.setSize(size);
									stacks[cx][cy] = lastStack;
//									return;
								}else{
									stacks[cx][cy] = null;
									lastStack = null;
								}
							}else{
								stacks[cx][cy] = lastStack;
							}
//							if(stacks[sx][sy] == null){
//								Err.err.println("stacks[" + sx + "][" + sy + "] is null!!! *WHY**?*");
//								return;
//							}
							if(stacks[sx][sy] != null){
								buttons[sx][sy].setTex(stacks[sx][sy].getTex());
								buttons[sx][sy].setText(stacks[sx][sy].toString());
//								buttons[sx][sy].setPosition(((sx + ka - 4.5f) * space * W + 500) - W / 2,
//										((sy + ka - yrows * 0.5f) * space * H + YO) - H / 2);
							}else{
								Err.err.println("stacks[" + sx + "][" + sy + "] is null!!! *WHY**?*");
								buttons[sx][sy].setTex(emptyStackTex);
								buttons[sx][sy].setText("");
							}
							if (lastStack == null) {
								buttons[cx][cy].setTex(emptyStackTex);
								buttons[cx][cy].setText("");
							} else {
								buttons[cx][cy].setTex(lastStack.getTex());
								buttons[cx][cy].setText(lastStack.toString());
							}
						}else{
							final float push = 3;
							Item3D i = Item3D.getBlockInstance(stacks[cx][cy].blockID(), MousePicker.getPoint(3, new Vector3f()), true);
							i.influence(push*(i.getPosition().x-Camera.getPosition().x),
									push*(i.getPosition().y-Camera.getPosition().y),
									push*(i.getPosition().z-Camera.getPosition().z));
							i.setNOB(stacks[cx][cy].size());
							stacks[cx][cy] = null;
							buttons[cx][cy].setTex(emptyStackTex);
							buttons[cx][cy].setText("");
						}
						setPosition(((X + ka - 4.5f) * space * W + 500) - W / 2,
								((Y + ka - yrows * 0.5f) * space * H + YO) - H / 2);
						buttonClick = false;
						cb = null;
						gtex.setDisplayLevel(GUIManager.DOWN);
					}
				};
				buttons[x][y].setTex(emptyStackTex);
				buttons[x][y].setTextYOffset(25);
				buttons[x][y].setFontSize(0.75f);
				buttons[x][y].setTextColor(1, 1, 1);
				buttons[x][y].hide();
				buttons[x][y].setDisplayLevel(20);
				pane.attach(buttons[x][y]);
			}
		}

		if (!restoreSave()) {
			add(new ItemStack(ItemStack.KA, 10000));
			add(new ItemStack(ItemStack.BOOM, 10000));
			add(new ItemStack(ItemStack.MARBLE, 64));
			add(new ItemStack(ItemStack.TORCH, 64));
			add(new ItemStack(ItemStack.SAND, 64));
			add(new ItemStack(ItemStack.WATER, 64));
			add(new ItemStack(ItemStack.GLASS, 64));
			add(new ItemStack(ItemStack.STONESLAB, 64));
		}
		
		for (int x = 0; x < 9; x++) {
			buttons[x][exy].setPosition(((x + ka - 4.5f) * space * W + 500) - W / 2, 950 - H / 2);
			buttons[x][exy].show();
		}
		handcontents = new Entity(gun, 0, new Vector3f(), 0, 0, 0, 0.3f, false, false);
		handcontents.hide();
		handcontents.show();
	}

	private static ItemStack hand = new ItemStack(ItemStack.NONE, 1);

	public void add(ItemStack is) {
		for (int x = 0; x < 9; x++) {
			if (stacks[x][exy] == null) {
				stacks[x][exy] = is;
				buttons[x][exy].setTex(is.getTex());
				buttons[x][exy].setText(is.toString());
				return;
			}
		}
		for (int y = 0; y < yrows; y++) {
			if(y != exy)
				for (int x = 0; x < 9; x++) {
					if (stacks[x][y] == null) {
						stacks[x][y] = is;
						buttons[x][y].setTex(is.getTex());
						buttons[x][y].setText(is.toString());
						return;
					}
				}
		}
	}

	private Button cb;

	public void update() {// clearing the button text on deletion of Stack; Q with closed INV!!! 
		checkForItem3Ds();
		if (Keyboard.keyPressedThisFrame(INV_SWITCH)) {
			open = !open;
			if (open) {
				background.show();
				chosenItemStackIndicator.hide();
				for (int x = 0; x < 9; x++) {
					for (int y = 0; y < yrows; y++) {
						if (y != exy) {
							buttons[x][y].show();
						} else {
							buttons[x][exy].hide();
							buttons[x][y].setPosition(((x + ka - 4.5f) * space * W + 500) - W / 2,
									((y + ka - yrows * 0.5f) * space * H + YO) - H / 2);
							buttons[x][exy].show();
						}
					}
				}
				Mouse.setGrabbed(false);
				Player.setNOCONTROL(true);
			} else {
				for (int x = 0; x < 9; x++) {
					for (int y = 0; y < yrows; y++) {
						if (y != exy)
							buttons[x][y].hide();
					}
				}
				for (int x = 0; x < 9; x++) {
					buttons[x][exy].setPosition(((x + ka - 4.5f) * space * W + 500) - W / 2, 950 - H / 2);
				}
				Mouse.setGrabbed(true);
				background.hide();
				chosenItemStackIndicator.show();
				Player.setNOCONTROL(false);
			}
		} else if (open && KeyManager.escapeEquivalentPressed()) {
			open = false;
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < yrows; y++) {
					if (y != exy)
						buttons[x][y].hide();
				}
			}
			for (int x = 0; x < 9; x++) {
				buttons[x][exy].setPosition(((x + ka - 4.5f) * space * W + 500) - W / 2, 950 - H / 2);
			}
			Mouse.setGrabbed(true);
			background.hide();
			chosenItemStackIndicator.show();
			Player.setNOCONTROL(false);
		}
		
		if (!open) {
			if(Controller.USECONTROLLER){
				if(Controller.buttonTippedThisFrame(Controller.B))
					chosen += 1;
				else if(Controller.buttonTippedThisFrame(Controller.A))
					chosen -= 1;
			}else{
				chosen -= Mouse.getDWheelFrame();
			}
			
			if (chosen < 0) {
				chosen = 8;
			} else if (chosen > 8) {
				chosen = 0;
			}
			for (int i = 0; i < 9; i++) {
				if (Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_1 + i)) {
					chosen = i;
				}
			}

			if (stacks[chosen][exy] != null) {
				if (!stacks[chosen][exy].action()) {
					stacks[chosen][exy] = null;
					buttons[chosen][exy].setTex(emptyStackTex);
					buttons[chosen][exy].setText("");
				}else{
					buttons[chosen][exy].setText(stacks[chosen][exy].toString());
				}
			} else {
				hand.action();
			}
			chosenItemStackIndicator.getPos().set(((chosen + ka - 4.5f) * space * W + 500) * 0.001f * 2 - 1, -0.9f);
			for (int x = 0; x < 9; x++) {
				buttons[x][exy].update();
			}
			if (stacks[chosen][exy] != null) {
				if (stacks[chosen][exy].ID() == ItemStack.BOOM) {
					Vector3f v = getRightPos(true);
					float alpha = WorldObjects.player.getRotY();
					handcontents.setRotZ(-Camera.getPitch());
					handcontents.setRotY(alpha - 90);
					v.y += 0.5f;
					handcontents.setPosition(v.x, v.y, v.z);
					handcontents.setModel(gun);
					handcontents.setScale(0.3f);
					handcontents.show();
				} else if (stacks[chosen][exy].isBlockItem()) {
					Vector3f v = getRightPos(false);
					float alpha = WorldObjects.player.getRotY();

					handcontents.setRotZ(-Camera.getPitch());
					handcontents.setRotY(alpha);
					v.y += 0.5f;
					handcontents.setPosition(v.x, v.y, v.z);
					cube.getTex().setTex(stacks[chosen][exy].get3DTex());
					handcontents.setModel(cube);
					handcontents.setScale(0.01f);
					handcontents.show();
				} else {
					handcontents.hide();
				}
			} else {
				handcontents.hide();
			}
			if(Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_Q)){
				final float push = 3;
				Item3D I = Item3D.getBlockInstance(stacks[chosen][exy].blockID(), MousePicker.getPoint(3, new Vector3f()), true);
				I.influence(push*(I.getPosition().x-Camera.getPosition().x),
						push*(I.getPosition().y-Camera.getPosition().y),
						push*(I.getPosition().z-Camera.getPosition().z));
				I.setNOB(stacks[chosen][exy].size());
				stacks[chosen][exy] = null;
				buttons[chosen][exy].setTex(emptyStackTex);
				buttons[chosen][exy].setText("");
			}
		} else {
			Mouse.updateControllerInputForMouse();
			if (!buttonClick) {
				for (int x = 0; x < 9; x++) {
					for (int y = 0; y < yrows; y++) {
						buttons[x][y].update();
						buttons[x][y].updateClicks();
					}
				}
			} else if (Mouse.isButtonDown(0)) {
				cb.leftHold();
			} else {
				cb.leftLeft();
			}
			if(Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_Q) || (Controller.USECONTROLLER && Controller.buttonTippedThisFrame(Controller.Y))){
				float mx = Mouse.getAX() * 1000;
				float my = Mouse.getAY() * 1000;
				int sx = (int) (((mx + W / 2 - 500) / (space * W)) + 4.5f - ka);
				int sy = (int) (((my + H / 2 - YO) / (space * H)) + yrows * 0.5f - ka);
				if(sx >= 0 && sx < 9 && sy >= 0 && sy < yrows && stacks[sx][sy] != null){
					final float push = 3;
					Item3D I = Item3D.getBlockInstance(stacks[sx][sy].blockID(), MousePicker.getPoint(3, new Vector3f()), true);
					I.influence(push*(I.getPosition().x-Camera.getPosition().x),
							push*(I.getPosition().y-Camera.getPosition().y),
							push*(I.getPosition().z-Camera.getPosition().z));
					I.setNOB(stacks[sx][sy].size());
					stacks[sx][sy] = null;
					buttons[sx][sy].setTex(emptyStackTex);
					buttons[sx][sy].setText("");
				}
			}
		}
	}

	private RawModel cubeRaw = SC.getModel("cube", "button").getRawMod();
	private TexturedModel cube = new TexturedModel(cubeRaw, new ModelTexture(0));
	private TexturedModel gun = SC.getModel("gun-90", "gun");

	private boolean addItem(int ID) {
		for (int x = 0; x < 9; x++) {
			if (stacks[x][exy] != null) {
				if (stacks[x][exy].ID() == ID && stacks[x][exy].remainingSpace() > 0) {
					stacks[x][exy].add();
					buttons[x][exy].setText(stacks[x][exy].toString());
					return true;
				}
			} else {
				add(new ItemStack(ID, 1));
				return true;
			}
		}
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < yrows; y++) {
				if (stacks[x][y] != null) {
					if (stacks[x][y].ID() == ID && stacks[x][y].remainingSpace() > 0) {
						stacks[x][y].add();
						buttons[x][y].setText(stacks[x][y].toString());
						return true;
					}
				} else {
					add(new ItemStack(ID, 1));
					return true;
				}
			}
		}
		return false;
	}

	private void checkForItem3Ds() {
		for (int i = 0; i < Item3D.is.size(); i++) {
			if (Item3D.is.get(i).isBlock()
					&& Vects.xydistSq(Item3D.is.get(i).getPosition(), WorldObjects.player.getPosition()) <= 1) {
				for(int i2 = 0; i2 < Item3D.is.get(i).getCurrentSize(); i2++){
					if(!addItem(ItemStack.itemID(Item3D.is.get(i).BID()))){
						break;
					}else{
						if(!Item3D.is.get(i).removeOne()){
							break;
						}
					}
				}
//				if (addItem(ItemStack.itemID(Item3D.is.get(i).BID())))
//					Item3D.is.get(i).cleanUp();
			}
		}
	}

	private Vector3f getRightPos(boolean gun) {
		Vector3f ret = Vects.setCalcVect(Camera.getPosition());
		float r = 0.2f;
		if (gun)
			r = 0.5f;
		float ysin = (float) Math.sin(Math.toRadians(WorldObjects.player.getRotY() - 40));
		float ycos = (float) Math.cos(Math.toRadians(WorldObjects.player.getRotY() - 40));
		ret.y -= 0.6f;
		ret.x += r * ysin;
		ret.z += r * ycos;
		return ret;
	}

	public void cleanUp() {
		save();
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < yrows; y++) {
				buttons[x][y].hide();
				buttons[x][y].cleanUp();
			}
		}
	}

	private boolean restoreSave() {
		String ka = Tools.readFile("ChunksSave/" + ChunkSaver.worldName + "/invSave.txt");
		if (ka != null) {
			ka = decrypt(ka);
			String[] lines = ka.split("\n");
			int x = 0, y = 0;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].length() > 0) {
					String[] ka2 = lines[i].split(";");
					int ID = Integer.parseInt(ka2[0]);
					int size = Integer.parseInt(ka2[1]);
					stacks[x][y] = new ItemStack(ID, size);
					buttons[x][y].setTex(stacks[x][y].getTex());
					buttons[x][y].setText(stacks[x][y].toString());
				}
				x++;
				if (x == 9) {
					x = 0;
					y++;
					if (y == yrows) {
						return true;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void save() {
		StringBuilder data = new StringBuilder();
		for (int y = 0; y < yrows; y++) {
			for (int x = 0; x < 9; x++) {
				if (stacks[x][y] != null) {
					data.append(stacks[x][y].ID());
					data.append(";");
					data.append(stacks[x][y].size());
				}
				data.append("\n");
			}
		}
		Tools.writeToFile("ChunksSave/" + ChunkSaver.worldName + "/invSave.txt", encrypt(data.toString()));
	}

	private String encrypt(String data) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < data.length(); i++) {
			ret.append((char) (data.charAt(i) + 5));
		}
		return ret.toString();
	}

	private String decrypt(String data) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < data.length(); i++) {
			ret.append((char) (data.charAt(i) - 5));
		}
		return ret.toString();
	}

	public MenuThing getPanel() {
		return pane;
	}

}