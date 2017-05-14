package gameStuff;

public abstract class ServerMainLoop {

//	public static int GUISWITCHKEY = GLFW.GLFW_KEY_X;
//	public static boolean running = true, firstRender = true, MENUOPEN = false, CLEANUPNOW = false, alive = true;
//	public static Fbo outputFbo, outputFbo2, multisampledFbo;
//	public static WaterFrameBuffers wfbo;
//
//	public static boolean renderGUI = true;
//
//	public static void main(String[] args) {
//		new Thread("commandListener") {// REPLACE WITH UPDATE METHOD IN e.g.
//										// DISPLAYMANAGER.UPDATE
//			@Override
//			public void run() {
//				try {
//					BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
//					while (true) {// !!!
//						while (!r.ready()) {
//							try {
//								sleep(500);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//						String input = r.readLine();
//						if (input.equalsIgnoreCase("stop")) {
//							running = false;
//							CLEANUPNOW = true;
//						} else if (CommandProcessor.addCommand(input)) {
//							Err.err.println("User input: \"" + input + "\"");
//						} else {
//							Err.err.println("HÄ? \"" + input + "\"");
//						}
//					}
//				} catch (IOException i) {
//					i.printStackTrace();
//				}
//			}
//		}.start();
//		DisplayManager.run();
//		// ResourceManager.loadResources();
//		ThreadManager.goOn();
//		PreferenceSaver.applyPrefs();
//		// Frame.start();
//		startServer();
//		alive = false;
//		System.exit(0);
//	}
//
//	public static void startServer() {
//		icon16.hide();
//		ThreadManager.goOn();
//		WorldObjects.init();
//		// WeatherController.init();
//		while (Meth.systemTime() < ResourceManager.loadStart + ResourceManager.loadingTime && !Mouse.isButtonDown(0)
//				&& !Mouse.isButtonDown(1)) {
//			renderLoadingScreen();
//			Meth.wartn(50);
//		}
//		Mouse.setGrabbed(true);
//		firstRender = true;
//		SkyRenderer.setMoonThings();
//		while (running && !DisplayManager.isCloseRequested()) {
//			if (CLEANUPNOW) {
//				running = false;
//				cleanUp();
//				break;
//			}
//			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
//				MENUOPEN = true;
//				Frame.startInMenu();
//				DisplayManager.setFrameTimeSeconds(0.000000000001f);
//				WorldObjects.player.setCooldowns();
//				MENUOPEN = false;
//			}
//			if (Keyboard.keyTipped(GUISWITCHKEY)) {
//				renderGUI = !renderGUI;
//			}
//			update();
//			updateIngameText();
//			ParticleSystemMaster.update();
//			ParticleMaster.update();
//			Camera.move();
//
//			render();
//			if (renderGUI)
//				renderGUIandText();
//
//			DisplayManager.updateWindow();
//			if (firstRender) {
//				firstRender = false;
//				DisplayManager.setFrameTimeSeconds(0.00001f);
//			}
//		}
//		cleanUpGame();
//
//		GUIManager.reset();
//		TextMaster.clearDeleteList();
//
//		Camera.setPosition(0, 0, 0);
//		while (Meth.systemTime() < ResourceManager.loadStart + ResourceManager.loadingTime && !Mouse.isButtonDown(0)
//				&& !Mouse.isButtonDown(1)) {
//			renderLoadingScreen();
//			Meth.wartn(50);
//		}
//
//		icon16.show();
//
//		Projectil.GAIN = Projectil.MENGAIN;
//
//	}
//
//	// public static int getYGUIOffset(){
//	// return yGUIOffset;
//	// }
//
//	// private static int yGUIOffset = 0;
//	// private static GuiTexture t;
//
//	public static void renderGUIandText() {
//
//		// GL11.glViewport(0, DisplayManager.getYGUIOffset(),
//		// DisplayManager.WIDTH,
//		// DisplayManager.HEIGHT-DisplayManager.getYGUIOffset()*2);
//
//		GuiRenderer.render(GUIManager.getTex());
//		TextMaster.render();
//
//		// GL11.glViewport(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT);
//
//		// t.getPos().set(Mouse.getAX()*2-1, -(Mouse.getAY()*2-1));
//
//	}
//
//	private static Vector4f fi = new Vector4f(0, 1, 0, -(Meth.waterHeight + 1));
//	private static Vector4f sec = new Vector4f(0, -1, 0, (Meth.waterHeight + 1));
//
//	public static boolean renderForWater = false;
//	private static int WHU = 0, updateAllXFrames = 10;
//	private static float dwh;
//	private static float[] dwhs = new float[10];
//
//	public static void render() {
//		MasterRenderer.clear();
//		if (MasterRenderer.SHADOWS && TM.isDay()) {
//			MasterRenderer.renderShadowMap(EntityManager.getList(), WorldObjects.sun);
//		}
//		if (WaterRenderer.REFLECTIVE) {
//			if (WHU == 0) {
//				for (int i = dwhs.length - 2; i >= 0; i--) {
//					dwhs[i + 1] = dwhs[i];
//				}
//				dwhs[0] = WaterManager.getAverageAbsHeight() + WaterRenderer.WAVEHEIGHT;
//				float X = 0;
//				for (int i = 0; i < dwhs.length; i++) {
//					X += dwhs[i];
//				}
//				X /= dwhs.length;
//				dwh = X;
//				WHU++;
//			} else {
//				WHU++;
//				if (WHU == updateAllXFrames) {
//					WHU = 0;
//				}
//			}
//			fi.w = -dwh;
//			sec.w = dwh;
//			renderForWater = true;
//			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
//			wfbo.bindReflectionFrameBuffer();
//			float distance = 2 * (Camera.getPosition().y - dwh);
//			if (distance < 0) {
//				fi.w *= -1;
//				sec.w *= -1;
//				fi.y = -1;
//				sec.y = 1;
//			} else {
//				fi.y = 1;
//				sec.y = -1;
//			}
//			Camera.getPosition().y -= distance;
//			Camera.invertPitch();
//			MasterRenderer.renderAll(EntityManager.getList(), null, WorldObjects.getLightsToRender(), fi);
//			Camera.invertPitch();
//			Camera.getPosition().y += distance;
//			wfbo.bindRefractionFrameBuffer();
//			MasterRenderer.renderAll(EntityManager.getList(), null, WorldObjects.getLightsToRender(), sec);
//			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
//			wfbo.unbindCurrentFrameBuffer();
//			renderForWater = false;
//		}
//
//		multisampledFbo.bindFrameBuffer();
//		MasterRenderer.renderAll(EntityManager.getList(), null, WorldObjects.getLightsToRender(), Vects.NULL4);
//		multisampledFbo.unbindFrameBuffer();
//		multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
//		multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
//		PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
//	}
//
//	private static String fps = "";
//	private static long lastFPS = 0;
//	private static GUIText text;
//
//	public static void updateIngameText() {
//		FontColorManager.update();
//		Out.update();
//		if (!MENUOPEN) {
//			int hours = (int) TM.getDayTime();
//			int minutes = (int) Math.floor((TM.getDayTime() % 1) * 60f);
//			String time;
//			if (minutes < 10) {
//				time = hours + ":0" + minutes;
//			} else {
//				time = hours + ":" + minutes;
//			}
//			time += "; GameTimeMillis: " + Meth.toInt((float) TM.gameTimeMillis());
//			time += " Gerade ist es " + TM.season();
//			float x = WorldObjects.player.getPosition().x;
//			float y = WorldObjects.player.getPosition().y;
//			float z = WorldObjects.player.getPosition().z;
//			if (Meth.systemTime() > lastFPS + 500) {
//				lastFPS = Meth.systemTime();
//				fps = " FPS: " + (int) (1.0f / DisplayManager.getDFTS());
//			}
//			String TEXT = "  Current time is " + time + " Your Position: X: " + Meth.toInt(x) + " Y: " + Meth.toInt(y)
//					+ " Z: " + Meth.toInt(z) + " You have " + WorldObjects.player.lives() + " lives left!" + fps
//					+ " rendering " + chunksRendered + " chunks";
//			if (text == null) {
//				text = new GUIText(// live +
//						TEXT, 1.4f, font, tpos, 0.75f, true);
//			} else {
//				text.setText(TEXT);
//			}
//		}
//		text.setColour(FontColorManager.one);
//	}
//
//	private static Vector2f tpos = new Vector2f(0.15f, 0.03f);
//
//	public static void update() {
//		// time shit
//		TM.update();
//		// color shit
//		FontColorManager.update();
//		// sounds/music
//		SourcesManager.update();
//		MusicManager.update();
//
//		// inGameStuff
//		WorldObjects.update();
//		TickManager.update();
//	}
//
//	public static void cleanUpGame() {
//		Err.err.println("Saving stuff...");
//		ThreadManager.shutdown();
//		WeatherController.save();
//		Err.err.println("Weather settings saved!");
//		WorldObjects.save();
//		WorldObjects.cleanUp();
//		Err.err.println("World saved!");
//		TM.save();
//		Err.err.println("Time settings saved!");
//		PreferenceSaver.savePrefs();
//		Err.err.println("Preferences saved!");
//	}
//
//	public static void cleanUp() {
//		cleanUpGame();
//		Err.err.println("Cleaning up!");
//		ResourceManager.cleanUp();
//		Err.err.println("Good bye!");
//		DisplayManager.closeDisplay();
//	}
//
//	public static void recreateFrameBuffers() {
//		if (multisampledFbo != null) {
//			multisampledFbo.cleanUp();
//			outputFbo.cleanUp();
//			outputFbo2.cleanUp();
//			multisampledFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT);
//			outputFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
//			outputFbo2 = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
//			PostProcessing.recreate();
//			WaterFrameBuffers.setSizesNew();
//			wfbo.cleanUp();
//			wfbo = new WaterFrameBuffers();
//		}
//	}
//
//	public static void renderMenuWithSkyBox() {
//		MasterRenderer.clear();
//		MasterRenderer.renderSkyForMenu();
//		renderGUIandText();
//		DisplayManager.updateWindow();
//	}
//
//	private static GuiTexture loading;
//	public static int chunksRendered;
//
//	public static void renderLoadingScreen() {
//		if (loading == null)
//			loading = new GuiTexture(SC.getTex("LoadingScreens/L1quad").getID(), new Vector2f(0, 0),
//					new Vector2f(1, 1));
//		// loading.setDisplayLevel(GUIManager.UP);
//
//		MasterRenderer.clear();
//		GUIManager.addGuiTexture(loading);
//		GuiRenderer.render(GUIManager.getTex());
//		GUIManager.removeGuiTexture(loading);
//		DisplayManager.updateWindow();
//	}
//
//	private static GuiTexture icon16;
//
//	public static void renderStartAnimation() {
//		// renderUWE_THE_KILLERAnimation();
//		icon16 = new GuiTexture(SC.getTex("Icons/16").getID(), new Vector2f(), new Vector2f(0));// 0.5f,
//																								// 0.5f*DisplayManager.desiredRatioForGUI
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, icon16.getTexture());
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//		icon16.show();
//		// float dist = 25;
//		// WeatherController.flare(dist*(float)Math.sin(95*Meth.angToRad),
//		// dist*(float)Math.sin(35*Meth.angToRad),
//		// dist*(float)Math.cos(95*Meth.angToRad), 0.25f);
//		renderMenuWithSkyBox();
//		DisplayManager.setFrameTimeSeconds(0.000001f);
//		float speed = 0;
//		while (icon16.getScale().x < 0.2f) {
//			speed += DisplayManager.getFrameTimeSeconds();
//			icon16.getScale().x += speed * DisplayManager.getFrameTimeSeconds() * 0.25f;
//			icon16.getScale().y = icon16.getScale().x * DisplayManager.desiredRatioForGUI;
//			renderMenuWithSkyBox();
//		}
//		icon16.getScale().set(0.2f, 0.2f * DisplayManager.desiredRatioForGUI);
//		while (icon16.getPos().x > -0.6f) {
//			icon16.getPos().x -= 0.5f * DisplayManager.getFrameTimeSeconds();
//			icon16.getPos().y = -(5 / 6f) * icon16.getPos().x;
//			renderMenuWithSkyBox();
//		}
//		icon16.getPos().x = -0.6f;
//		icon16.getPos().y = (5 / 6f) * 0.6f;
//		DisplayManager.setFrameTimeSeconds(0.00001f);
//	}
//
//	private static GuiTexture UWE_THE_KILLER_STUDIOS, rlyDarkGreen1, rlyDarkGreen2, rlyDarkGreen3;
//
//	public static void renderUWE_THE_KILLERAnimation() {
//		DisplayManager.setFrameTimeSeconds(0.0000001f);
//		UWE_THE_KILLER_STUDIOS = new GuiTexture(SC.getTex("LoadingScreens/uwe_the_killer_studios").getID(),
//				new Vector2f(0, 0), new Vector2f(0));
//		UWE_THE_KILLER_STUDIOS.show();
//		rlyDarkGreen1 = new GuiTexture(SC.getTex("LoadingScreens/rlyDarkGreen").getID(), new Vector2f(0, 0.3f),
//				new Vector2f(0, 0.01f));
//		rlyDarkGreen1.show();
//		rlyDarkGreen2 = new GuiTexture(rlyDarkGreen1.getTexture(), new Vector2f(0, -0.05f), new Vector2f(0, 0.01f));
//		rlyDarkGreen2.show();
//		rlyDarkGreen3 = new GuiTexture(rlyDarkGreen1.getTexture(), new Vector2f(0, -0.4f), new Vector2f(0, 0.01f));
//		rlyDarkGreen3.show();
//		while (UWE_THE_KILLER_STUDIOS.getScale().x < 1) {
//			UWE_THE_KILLER_STUDIOS.getScale()
//					.set(UWE_THE_KILLER_STUDIOS.getScale().x + 0.5f * DisplayManager.getFrameTimeSeconds());
//			UWE_THE_KILLER_STUDIOS.setHighlight(UWE_THE_KILLER_STUDIOS.getScale().x - 1);
//			renderGUIandText();
//			DisplayManager.updateWindow();
//		}
//		while (rlyDarkGreen1.getScale().x < 0.1f) {
//			rlyDarkGreen1.getScale().x += DisplayManager.getFrameTimeSeconds();
//			renderGUIandText();
//			DisplayManager.updateWindow();
//		}
//		while (rlyDarkGreen2.getScale().x < 0.75f) {
//			rlyDarkGreen2.getScale().x += 1.5f * DisplayManager.getFrameTimeSeconds();
//			renderGUIandText();
//			DisplayManager.updateWindow();
//		}
//		while (rlyDarkGreen3.getScale().x < 0.5f) {
//			rlyDarkGreen3.getScale().x += DisplayManager.getFrameTimeSeconds();
//			renderGUIandText();
//			DisplayManager.updateWindow();
//		}
//		UWE_THE_KILLER_STUDIOS.hide();
//		rlyDarkGreen1.hide();
//		rlyDarkGreen2.hide();
//		rlyDarkGreen3.hide();
//		Meth.wartn(1000);
//	}

}
