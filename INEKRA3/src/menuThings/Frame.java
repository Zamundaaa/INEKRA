package menuThings;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import audio.AudioMaster;
import audio.MusicManager;
import controls.Keyboard;
import controls.Mouse;
import cubyWater.WaterManager;
import cubyWater.WaterRenderer;
import data.ChunkSaver;
import entities.Player;
import fontMeshCreator.GUIText;
import gameStuff.*;
import mobs.MobMaster;
import postProcessing.PostProcessing;
import renderStuff.*;
import toolBox.Meth;
import toolBox.Tools;

public class Frame {

	public static final int LEFT = 200, MIDDLE = 400, RIGHT = 600, LEFTISH = 270, RIGHTISH = 530, VERYRIGHT = 700,
			VERYLEFT = 100, NOWHERE = -1000;

	private static boolean running = true, worldChosen = false;
	private static List<Menü> menus = new ArrayList<Menü>();
	private static Button lastWorld;

	public static int button, textfield, buttonClicked, textfieldChosen;

	private static Menü start, options, world, ingame, inoptions, newWorld, experimental;
	private static Button startButton;
	private static SchiebeRegler gain;
	private static GUIText name;
	private static TextField light;
	private static String[] worlds;
	private static int selectedWorld = 0;

	private static boolean inMenu;
	private static long lT;

	public static void start() {
		running = true;

		button = SC.getTex("button").getID();
		buttonClicked = SC.getTex("button_clicked").getID();
		textfield = SC.getTex("textfield").getID();
		textfieldChosen = SC.getTex("textfield_chosen").getID();

		MusicManager.play();

		while (Meth.systemTime() < ResourceManager.loadStart + ResourceManager.loadingTime && !Mouse.isButtonDown(0)
				&& !Mouse.isButtonDown(1) && !DisplayManager.isCloseRequested()) {
			MainLoop.renderLoadingScreen();
			Meth.wartn(25);
		}
		MainLoop.renderStartAnimation();
		name = new GUIText("INEKRA", 7, SC.specialFont, new Vector2f(0, 0.15f), 1, true);
		name.setColour(0, 1, 1);
		addStartMenu();
		DisplayManager.setFrameTimeSeconds(0.0000001f);
		loop();
	}

	private static void loop() {
		while (running) {
			Mouse.setGrabbed(false);
			if (MainLoop.CLEANUPNOW) {
				MainLoop.cleanUp();
				Err.err.println("CLEANING UP COMPLETELY...");
				break;
			}
			for (int i = 0; i < menus.size(); i++) {
				menus.get(i).update();
			}
			if (running)
				MainLoop.renderMenuWithSkyBox();

			if (!running || DisplayManager.isCloseRequested()) {
				running = false;
				MainLoop.cleanUp();
				DisplayManager.closeDisplay();
			} else {
				MusicManager.update();
			}
		}
		if (AudioMaster.CREATED)
			AudioMaster.cleanUp();
	}

	private static void startGame() {
		MainLoop.renderLoadingScreen();
		ResourceManager.loadStart = Meth.systemTime();
		while (menus.size() > 0) {
			removeMenu(menus.get(menus.size() - 1));
		}
		MainLoop.runGame();
		while (menus.size() > 0) {
			removeMenu(menus.get(menus.size() - 1));
		}
		MainLoop.running = true;
	}

	private static void removeMenu(Menü m) {
		m.hide();
		menus.remove(m);
	}

	private static void addStartMenu() {
		if (start == null) {
			ArrayList<MenuThing> bs = new ArrayList<MenuThing>();

			bs.add(new Button("START", new Rectangle(MIDDLE, 425, 200, 100)) {
				@Override
				public void leftClick() {
					removeMenu(start);
					addWorldMenu();
				}
			});

			bs.add(new Button("OPTIONS", new Rectangle(MIDDLE, 550, 200, 100)) {
				@Override
				public void leftClick() {
					removeMenu(start);
					addOptionsMenuButtons();
				}
			});
			bs.add(new Button("EXIT", new Rectangle(MIDDLE, 675, 200, 100)) {
				@Override
				public void leftClick() {
					running = false;
				}
			});
			start = new Menü(bs);
			menus.add(start);
		} else {
			start.show();
			menus.add(start);
		}
	}
	
	private static QuestionPopUp del, really;
	private static int worldToDel;
	private static ScrollPane worldPane;
	
	private static final boolean popUpAtMouse = false;
	private static float popUpX = 350, popUpY = 20;
	
	private static void addWorldMenu() {
//		if (worlds == null) {
			worlds = Tools.getFiles("ChunksSave/");
//		}
		if (worlds.length == 0) {
			addNewWorldMenu();
		} else {
			if (world == null) {
				ArrayList<MenuThing> bs = new ArrayList<MenuThing>();

				startButton = new Button("START", new Rectangle(LEFTISH, 425, 200, 100), buttonClicked, false) {
					private long textChange;
					private boolean textBack = false;

					@Override
					public void update() {
						if (textBack && Meth.systemTime() > textChange + 1000) {
							textBack = false;
							setText("START");
							text.setColour(0, 0, 1);
						}
						if (worldChosen) {
							text.setColour(0, 1, 0);
						}
						super.update();
					}

					@Override
					public void leftClick() {
						if (worldChosen) {
							removeMenu(world);
							name.cleanUp();
							PreferenceSaver.savePrefs();
							ChunkSaver.worldName = worlds[selectedWorld];
							startGame();
							name = new GUIText("INEKRA", 7, SC.specialFont, new Vector2f(0, 0.15f), 1, true);
							name.setColour(0, 1, 1);
							start.cleanUpAndClear();
							start = null;
							addStartMenu();
						} else {
							setText("Choose a world!");
							textChange = Meth.systemTime();
							textBack = true;
							text.setColour(1, 0, 0);
						}
					}
				};
				startButton.setDisplayLevel(10);
				startButton.setTextColor(0, 0, 1);
				bs.add(startButton);
				
				del = new QuestionPopUp("Do you want to delete?", new Rectangle(0, 0, 300, 150)) {
					@Override
					public void yes() {
						if(popUpAtMouse)
							really.popUp(Mouse.getAX()*1000, Mouse.getAY()*1000);
						else
							really.popUp(popUpX, popUpY);
					}
					@Override
					public void no() {
						really.visible = false;
					}
				};
				
				really = new QuestionPopUp("REALLY?!?", new Rectangle(0, 0, 300, 150)) {
					@Override
					public void yes() {
						if(worldToDel >= 0 && worldToDel < worlds.length){
							try {
								String worldString = worlds[worldToDel];
								Tools.deleteDirectoryInINEKRA("ChunksSave/" + worlds[worldToDel]);
								worlds = Tools.getFiles("ChunksSave/");
								bs.remove(worldPane);

								worldPane.cleanUp();
								worldPane = new ScrollPane(new Rectangle(RIGHTISH, 375, 200, 500));
								for (int i = 0; i < worlds.length; i++) {
									final int COUNT = i;
									Button n = new Button(worlds[i], new Rectangle(0, 0, 200, 100)) {
										final int C = COUNT;
										@Override
										public void leftClick() {
											if (lastWorld != null) {
												lastWorld.setTex(textfield);
											}
											lastWorld = this;
											setTex(textfieldChosen);
											selectedWorld = C;
											startButton.setTex(button);
											worldChosen = true;
										}
										@Override
										public void clickOutside() {
											if (lastWorld == this) {
												selectedWorld = -1;
												worldChosen = false;
												startButton.setTex(buttonClicked);
												setTex(textfield);
											}
										}
									};
									n.setTex(textfield);
									worldPane.attach(n);
									Button b = new Button("", new Rectangle(0, 0, 80, 80), SC.getTex("trashcan_pic").getID(), true){
										@Override
										public void leftClick(){
//											System.out.println(del.hidden);
											worldToDel = COUNT;
											del.setText("Do you want to delete '" + worlds[COUNT] + "'?");
											if(popUpAtMouse)
												del.popUp(Mouse.getAX()*1000, Mouse.getAY()*1000);
											else
												del.popUp(popUpX, popUpY);
										}
									};
									n.attach(b, 5, 5, 0.25f, 0.5f*DisplayManager.desiredRatioForGUI);
								}
								bs.add(worldPane);
								visible = true;
								setText("DELETED " + worldString + "!");
								new Thread(){
									@Override
									public void run(){
										Meth.wartn(1000);
										visible = false;
									}
								}.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					@Override
					public void no() {
						del.visible = false;
					}
				};
				
//				Loader.applyFilter(SC.getTex("trashcan_pic").getID(), GL11.GL_NEAREST);
				
				worldPane = new ScrollPane(new Rectangle(RIGHTISH, 375, 200, 500));
				for (int i = 0; i < worlds.length; i++) {
					final int COUNT = i;
					Button n = new Button(worlds[i], new Rectangle(0, 0, 200, 100)) {
						final int C = COUNT;

						@Override
						public void leftClick() {
							if (lastWorld != null) {
								lastWorld.setTex(textfield);
							}
							lastWorld = this;
							setTex(textfieldChosen);
							selectedWorld = C;
							startButton.setTex(button);
							worldChosen = true;
							// startButton.setBounds(getRect().x+getRect().width-100,
							// getRect().y, 100, 100);
						}

						@Override
						public void clickOutside() {
							if (lastWorld == this) {
								selectedWorld = -1;
								worldChosen = false;
								startButton.setTex(buttonClicked);
								setTex(textfield);
							}
						}

					};
					n.setTex(textfield);
					worldPane.attach(n);
					Button b = new Button("", new Rectangle(0, 0, 80, 80), SC.getTex("trashcan_pic").getID(), true){
						@Override
						public void leftClick(){
//							System.out.println(del.hidden);
							worldToDel = COUNT;
							del.setText("Do you want to delete '" + worlds[COUNT] + "'?");
							if(popUpAtMouse)
								del.popUp(Mouse.getAX()*1000, Mouse.getAY()*1000);
							else
								del.popUp(popUpX, popUpY);
						}
					};
					n.attach(b, 5, 5, 0.25f, 0.5f*DisplayManager.desiredRatioForGUI);
//					b.setDisplayLevel(10);
				}
				bs.add(worldPane);
				
				bs.add(del);
				del.setDisplayLevel(worldPane.displayLevel+3);
				bs.add(really);
				really.setDisplayLevel(worldPane.displayLevel+6);
				
				bs.add(new Button("new World", new Rectangle(LEFTISH, 575, 200, 100)) {
					@Override
					public void leftClick() {
						removeMenu(world);
						addNewWorldMenu();
					}
				});

				bs.add(new Button("back", new Rectangle(LEFTISH, 725, 200, 100)) {
					@Override
					public void leftClick() {
						removeMenu(world);
						addStartMenu();
					}
				});

				world = new Menü(bs);
				menus.add(world);
			} else {
				world.remove(worldPane);
				
				menus.add(world);
				world.show();
				worldPane.cleanUp();
				worldPane = new ScrollPane(new Rectangle(RIGHTISH, 375, 200, 500));
				for (int i = 0; i < worlds.length; i++) {
					final int COUNT = i;
					Button n = new Button(worlds[i], new Rectangle(0, 0, 200, 100)) {
						final int C = COUNT;

						@Override
						public void leftClick() {
							if (lastWorld != null) {
								lastWorld.setTex(textfield);
							}
							lastWorld = this;
							setTex(textfieldChosen);
							selectedWorld = C;
							startButton.setTex(button);
							worldChosen = true;
							// startButton.setBounds(getRect().x+getRect().width-100,
							// getRect().y, 100, 100);
						}

						@Override
						public void clickOutside() {
							if (lastWorld == this) {
								selectedWorld = -1;
								worldChosen = false;
								startButton.setTex(buttonClicked);
								setTex(textfield);
							}
						}

					};
					n.setTex(textfield);
					worldPane.attach(n);
					Button b = new Button("", new Rectangle(0, 0, 80, 80), SC.getTex("trashcan_pic").getID(), true){
						@Override
						public void leftClick(){
//							System.out.println(del.hidden);
							worldToDel = COUNT;
							del.setText("Do you want to delete '" + worlds[COUNT] + "'?");
							if(popUpAtMouse)
								del.popUp(Mouse.getAX()*1000, Mouse.getAY()*1000);
							else
								del.popUp(popUpX, popUpY);
						}
					};
					n.attach(b, 5, 5, 0.25f, 0.5f*DisplayManager.desiredRatioForGUI);
//					b.setDisplayLevel(10);
				}
				world.add(worldPane);
				startButton.text.setColour(0, 0, 1);
			}
		}
	}

	private static void addNewWorldMenu() {
		if (newWorld == null) {
			textfield = SC.getTex("textfield").getID();
			textfieldChosen = SC.getTex("textfield_chosen").getID();

			ArrayList<MenuThing> bs = new ArrayList<MenuThing>();

			bs.add(new Button("START", new Rectangle(MIDDLE, 425, 200, 100)) {
				@Override
				public void leftClick() {
					removeMenu(newWorld);
					name.cleanUp();
					PreferenceSaver.savePrefs();
					ChunkSaver.worldName = light.getContent();
					startGame();
					name = new GUIText("INEKRA", 7, SC.specialFont, new Vector2f(0, 0.15f), 1, true);
					name.setColour(0, 1, 1);
					addStartMenu();
				}
			});

			light = new TextField("new World", new Rectangle(400, 550, 200, 100));
			light.setClearOnFirstClick();
			bs.add(light);

			bs.add(new Button("back", new Rectangle(MIDDLE, 675, 200, 100)) {
				@Override
				public void leftClick() {
					removeMenu(newWorld);
					addWorldMenu();
				}
			});

			newWorld = new Menü(bs);
			menus.add(newWorld);
		} else {
			menus.add(newWorld);
			newWorld.show();
		}
	}

	private static void addOptionsMenuButtons() {
		if (options == null) {
			ArrayList<MenuThing> bs = new ArrayList<MenuThing>();

			Button b = new Button("music? " + AudioMaster.musicEnabled, new Rectangle(MIDDLE, 425, 200, 100)) {
				@Override
				public void leftClick() {
					AudioMaster.musicEnabled = !AudioMaster.musicEnabled;
					this.setText("music? " + AudioMaster.musicEnabled);
					if (!AudioMaster.musicEnabled) {
						MusicManager.stop();
					} else {
						MusicManager.play();
					}
				}
			};
			bs.add(b);

			gain = new SchiebeRegler(new Rectangle(MIDDLE, 550, 200, 100), AudioMaster.gain(), "GAIN") {
				@Override
				public void valueChange(float value) {
					AudioMaster.setGain(value);
					if (value >= 0.5f) {
						gain.setTextColor(value - 0.5f, (1 - value) * 2, 0);
					} else {
						gain.setTextColor(0, value * 2, (0.5f - value) * 2);
					}
				}
			};
			if (AudioMaster.gain() >= 0.5f) {
				gain.setTextColor(AudioMaster.gain() - 0.5f, (1 - AudioMaster.gain()) * 2, 0);
			} else {
				gain.setTextColor(0, AudioMaster.gain() * 2, (0.5f - AudioMaster.gain()) * 2);
			}
			bs.add(gain);

			bs.add(new SchiebeRegler(new Rectangle(MIDDLE, 675, 200, 100),
					((ResourceManager.loadingTime - 3000) * 0.001f) / 57,
					"Loading time: " + (int) (ResourceManager.loadingTime * 0.001f)) {
				@Override
				public void valueChange(float value) {
					ResourceManager.loadingTime = (long) (3000 + value * 57000);
					this.setText("Loading time: " + (int) (ResourceManager.loadingTime * 0.001f));
				}
			});

			bs.add(new Button("back", new Rectangle(MIDDLE, 800, 200, 100)) {
				@Override
				public void leftClick() {
					removeMenu(options);
					addStartMenu();
				}
			});

			bs.add(new Button("Font: " + SC.getFontName(), new Rectangle(VERYRIGHT, 350, 200, 100)) {
				@Override
				public void leftClick() {
					setText("Font: " + SC.swapFont());
				}
			});

			options = new Menü(bs);
			menus.add(options);
		} else {
			options.show();
			menus.add(options);
		}
	}

	public static void startInMenu() {
		// MainLoop.hideNerdScope();
		inMenu = true;
		addInMenu();
		lT = Meth.systemTime();
		while (inMenu) {
			Mouse.setGrabbed(false);
			if (lT + 1000 < Meth.systemTime() && Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
				if (menus.get(0) == ingame) {
					while (menus.size() > 0) {
						removeMenu(menus.get(menus.size() - 1));
					}
					if (EXP != null)
						EXP.setTex(button);
					if (IOptions != null)
						IOptions.setTex(button);
					inMenu = false;
				} else {
					while (menus.size() > 0) {
						removeMenu(menus.get(menus.size() - 1));
					}
					addInMenu();
				}
			}
			for (int i = 0; i < menus.size(); i++) {
				menus.get(i).update();
			}
			if (DisplayManager.isCloseRequested()) {
				running = false;
				inMenu = false;
				MainLoop.CLEANUPNOW = true;
			} else {
				MainLoop.updateIngameText();
				MainLoop.render();
				MainLoop.renderGUIandText();
				DisplayManager.updateWindow();
				TM.update();
			}
			if (MainLoop.CLEANUPNOW) {
				break;
			}
		}
		Meth.wartn(200);
		Mouse.setGrabbed(true);
		// MainLoop.showNerdScope();
	}

	private static Button IOptions, EXP;

	private static void addInMenu() {
		if (ingame == null) {

			ArrayList<MenuThing> bs = new ArrayList<MenuThing>();

			EXP = new Button("EXPERIMENTAL", new Rectangle(MIDDLE, 550, 200, 100)) {
				@Override
				public void leftClick() {
					if (menus.contains(inoptions)) {
						removeMenu(inoptions);
						IOptions.setTex(button);
						setTex(textfield);
						addExperimental();
					} else if (menus.contains(experimental)) {
						removeMenu(experimental);
						setTex(button);
					} else {
						setTex(textfield);
						addExperimental();
					}
				}
			};
			EXP.setTextColor(1, 0, 0);
			bs.add(EXP);

			IOptions = new Button("OPTIONS", new Rectangle(MIDDLE, 425, 200, 100)) {
				@Override
				public void leftClick() {
					if (menus.contains(experimental)) {
						removeMenu(experimental);
						EXP.setTex(button);
						setTex(textfield);
						addOptionsInMenu();
					} else if (menus.contains(inoptions)) {
						removeMenu(inoptions);
						setTex(button);
					} else {
						addOptionsInMenu();
						setTex(textfield);
					}
				}
			};
			bs.add(IOptions);

			bs.add(new Button("BACK", new Rectangle(MIDDLE, 300, 200, 100)) {
				@Override
				public void leftClick() {
					removeMenu(ingame);
					if (menus.contains(inoptions))
						removeMenu(inoptions);
					if (menus.contains(experimental))
						removeMenu(experimental);
					inMenu = false;
					IOptions.setTex(button);
					EXP.setTex(button);
				}
			});

			bs.add(new Button("EXIT", new Rectangle(MIDDLE, 675, 200, 100)) {
				@Override
				public void leftClick() {
					MainLoop.renderLoadingScreen();
					ResourceManager.loadStart = Meth.systemTime();
					inMenu = false;
					MainLoop.running = false;
					Mouse.setGrabbed(false);
					while (menus.size() > 0) {
						removeMenu(menus.get(menus.size() - 1));
					}
					addStartMenu();
					running = true;
				}
			});

			ingame = new Menü(bs);
			menus.add(ingame);
		} else {
			ingame.show();
			EXP.setTextColor(1, 0, 0);
			menus.add(ingame);
		}
	}

	private static void addOptionsInMenu() {
		if (inoptions == null) {

			ArrayList<MenuThing> bs = new ArrayList<MenuThing>();

			int offset = 125;

			bs.add(new SchiebeRegler(new Rectangle(VERYLEFT, 350 - offset, 200, 100), TM.TIMEFACT / 5,
					"TimeSpeed " + (int) (20 * TM.TIMEFACT)) {
				@Override
				public void update() {
					if (TM.TIMEFACT != 5 * value) {
						setValue(TM.TIMEFACT / 5);
						setText("TimeSpeed " + (int) (20 * TM.TIMEFACT));
					}
					super.update();
				}

				@Override
				public void valueChange(float value) {
					TM.TIMEFACT = value * 5;
					// setValue(value);
					setText("TimeSpeed " + (int) (20 * TM.TIMEFACT));
				}
			});

			bs.add(new SchiebeRegler(new Rectangle(VERYRIGHT, 350 - offset, 200, 100), 0.1f,
					"MouseSensitivity " + (int) Mouse.sensitivity) {
				@Override
				public void valueChange(float value) {
					Mouse.sensitivity = value * 100;
					setText("MouseSensitivity " + (int) Mouse.sensitivity);
				}
			});

			Button music = new Button("music? " + AudioMaster.musicEnabled,
					new Rectangle(VERYLEFT, 475 - offset, 200, 100)) {
				@Override
				public void leftClick() {
					AudioMaster.musicEnabled = !AudioMaster.musicEnabled;
					setText("music? " + AudioMaster.musicEnabled);
					if (!AudioMaster.musicEnabled) {
						MusicManager.stop();
					} else {
						MusicManager.play();
					}
				}
			};
			bs.add(music);

			gain = new SchiebeRegler(new Rectangle(VERYRIGHT, 475 - offset, 200, 100), AudioMaster.gain(), "GAIN") {
				@Override
				public void valueChange(float value) {
					AudioMaster.setGain(value);
					if (value >= 0.5f) {
						gain.setTextColor(value - 0.5f, (1 - value) * 2, 0);
					} else {
						gain.setTextColor(0, value * 2, (0.5f - value) * 2);
					}
				}
			};
			if (AudioMaster.gain() >= 0.5f) {
				gain.setTextColor(AudioMaster.gain() - 0.5f, (1 - AudioMaster.gain()) * 2, 0);
			} else {
				gain.setTextColor(0, AudioMaster.gain() * 2, (0.5f - AudioMaster.gain()) * 2);
			}
			bs.add(gain);

			Button b = new Button("WaveMode: " + WaterRenderer.WAVEMODEL,
					new Rectangle(VERYLEFT, 600 - offset, 200, 100)) {
				@Override
				public void leftClick() {
					WaterRenderer.WAVEMODEL++;
					if (WaterRenderer.WAVEMODEL > WaterRenderer.MAXWAVEMOD) {
						WaterRenderer.WAVEMODEL = 1;
					}
					this.setText("WaveMode: " + WaterRenderer.WAVEMODEL);
				}
			};
			bs.add(b);

			SchiebeRegler waveheight = new SchiebeRegler(new Rectangle(VERYRIGHT, 600 - offset, 200, 100), 0.05f,
					"waveheight") {
				@Override
				public void valueChange(float value) {
					WaterRenderer.WAVEHEIGHT = value;
				}
			};
			bs.add(waveheight);
			waveheight.setTextColor(1, 1, 1);

			Button b2 = new Button("TransitionMode: " + MasterRenderer.TRANSITIONMODE,
					new Rectangle(VERYLEFT, 725 - offset, 200, 100)) {
				@Override
				public void leftClick() {
					MasterRenderer.TRANSITIONMODE++;
					if (MasterRenderer.TRANSITIONMODE > MasterRenderer.TRANSITION_MAX) {
						MasterRenderer.TRANSITIONMODE = 0;
					}
					this.setText("TransitionMode: " + MasterRenderer.TRANSITIONMODE);
				}
			};
			bs.add(b2);

			SchiebeRegler transdist = new SchiebeRegler(new Rectangle(VERYRIGHT, 725 - offset, 200, 100),
					MasterRenderer.TRANSITION_DISTANCE * 0.01f, "transition distance") {
				@Override
				public void valueChange(float value) {
					MasterRenderer.TRANSITION_DISTANCE = 100 * value;
				}
			};
			bs.add(transdist);
			transdist.setTextColor(1, 1, 1);

			Button reflect = new Button("Reflective Water? " + WaterRenderer.REFLECTIVE,
					new Rectangle(VERYLEFT, 850 - offset, 200, 100)) {
				@Override
				public void leftClick() {
					WaterRenderer.REFLECTIVE = !WaterRenderer.REFLECTIVE;
					setText("Reflective Water? " + WaterRenderer.REFLECTIVE);
					// if(WaterRenderer.REFLECTIVE){
					// setTextColor(0, 1, 0);
					// }else{
					// setTextColor(1, 0, 0);
					// }
				}
			};
			bs.add(reflect);
			// if(WaterRenderer.REFLECTIVE){
			// reflect.setTextColor(0, 1, 0);
			// }else{
			// reflect.setTextColor(1, 0, 0);
			// }

			bs.add(new Button("Font: " + SC.getFontName(), new Rectangle(VERYRIGHT, 850 - offset, 200, 100)) {
				@Override
				public void leftClick() {
					setText("Font: " + SC.swapFont());
				}
			});

			bs.add(new Button("Vsync? " + DisplayManager.VSYNC, new Rectangle(VERYLEFT, 225-offset, 200, 100)) {
				@Override
				public void leftClick() {
					DisplayManager.VSYNC = !DisplayManager.VSYNC;
					setText("Vsync? " + DisplayManager.VSYNC);
				}
			});

			inoptions = new Menü(bs);
			menus.add(inoptions);

		} else {
			menus.add(inoptions);
			inoptions.show();
		}
	}

	private static void addExperimental() {
		if (experimental == null) {
			ArrayList<MenuThing> bs = new ArrayList<MenuThing>();
			
			bs.add(new Button("Shadows? " + MasterRenderer.SHADOWS, new Rectangle(VERYLEFT, 350, 200, 100)) {
				@Override
				public void update(){
					super.update();
					if(MasterRenderer.dontUseShadowsAtAll){
						if(lt != 0 && Meth.systemTime() > lt + 5000){
							lt = 0;
							setText("Shadows? false");
							setTextColor(1, 1, 1);
						}
					}
				}
				private long lt;
				@Override
				public void leftClick() {
					if(!MasterRenderer.dontUseShadowsAtAll){
						MasterRenderer.SHADOWS = !MasterRenderer.SHADOWS;
						setText("Shadows? " + MasterRenderer.SHADOWS);
					}else{
						setText("DISABLED!");
						setTextColor(1, 0, 0);
						lt = Meth.systemTime();
					}
				}
			});

			bs.add(new Button("reflectionHeight Mode: " + WaterManager.refHMode(),
					new Rectangle(VERYRIGHT, 350, 200, 100)) {
				@Override
				public void leftClick() {
					WaterManager.incrementRefHMode();
					setText("reflectionHeight Mode: " + WaterManager.refHMode());
				}
			});

			// SchiebeRegler ms = new SchiebeRegler(new Rectangle(VERYLEFT, 475,
			// 200, 100), SkyRenderer.getMoonSizeInDegrees()/90.0f, "moonSize" +
			// SkyRenderer.getMoonSizeInDegrees()){
			// @Override
			// public void valueChange(float value){
			// SkyRenderer.setMoonSize(value*90);
			// setText("moonSize" + SkyRenderer.getMoonSizeInDegrees());
			// setTextColor(1, 0, 0);
			// if (value >= 0.5f) {
			// setTextColor(value - 0.5f, (1 - value) * 2, 0);
			// } else {
			// setTextColor(0, value * 2, (0.5f - value) * 2);
			// }
			// }
			// };
			// bs.add(ms);
			// float val = SkyRenderer.getMoonSizeInDegrees()/90;
			// if (val >= 0.5f) {
			// ms.setTextColor(val - 0.5f, (1 - val) * 2, 0);
			// } else {
			// ms.setTextColor(0, val * 2, (0.5f - val) * 2);
			// }

			SchiebeRegler bright = new SchiebeRegler(new Rectangle(VERYRIGHT, 475, 200, 100),
					((PostProcessing.brightness - 0.2f) / 1.8f), ("Brightness: " + PostProcessing.brightness)) {
				@Override
				public void valueChange(float value) {
					PostProcessing.brightness = 0.2f + (value * 1.8f);
					String t = ("Brightness: " + PostProcessing.brightness);
					setText(t.substring(0, Math.min(t.length(), 16)));
				}
			};
			String t = ("Brightness: " + PostProcessing.brightness);
			bright.setText(t.substring(0, Math.min(t.length(), 16)));
			bs.add(bright);

			bs.add(new Button("Screenshots Folder", new Rectangle(VERYLEFT, 475, 200, 100)) {
				@Override
				public void leftClick() {
					try {
						Desktop.getDesktop().open(new File(Tools.screenShotFolder));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			bs.add(new Button("spawn Mobs? " + MobMaster.SPAWNMOBS, new Rectangle(VERYLEFT, 600, 200, 100)) {
				@Override
				public void leftClick() {
					MobMaster.SPAWNMOBS = !MobMaster.SPAWNMOBS;
					setText("spawn Mobs? " + MobMaster.SPAWNMOBS);
				}
			});
			
			bs.add(new SchiebeRegler(new Rectangle(VERYRIGHT, 600, 200, 100), Player.speedMul*0.1f, "SpeedMul: " + Player.speedMul){
				@Override
				public void valueChange(float value){
					Player.speedMul = value*10;
					setText("SpeedMul: " + Player.speedMul);
				}
			});

			experimental = new Menü(bs);
			menus.add(experimental);
		} else {
			experimental.show();
			menus.add(experimental);
		}
	}

}