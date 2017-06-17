package gameStuff;

import java.io.*;
import java.lang.Math;
import java.util.ArrayList;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import audio.MusicManager;
import audio.SourcesManager;
import blockRendering.BlockRenderer;
import chatStuff.Chat;
import controls.*;
import cubyWater.*;
import data.Block;
import data.ChunkManager;
import entities.Camera;
import entities.Projectil;
import fontRendering.Out;
import fontRendering.TextMaster;
import gameStuff2.CommandProcessor;
import guis.*;
import inventory.Inv2D;
import inventory.ItemRenderer;
import menuThings.*;
import menuThings2.ProgressBar;
import particles.ParticleMaster;
import particles.ParticleSystemMaster;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderStuff.*;
import skybox.SkyRenderer;
import solarSystemRendering.PlanetManager;
import threadingStuff.ThreadManager;
import toolBox.*;
import weather.WeatherController;

public abstract class MainLoop {

	public static int GUISWITCHKEY = GLFW.GLFW_KEY_X;
	public static boolean running = true, firstRender = true, MENUOPEN = false, CLEANUPNOW = false, alive = true;
	public static boolean ANYMENUOPEN = true;
	public static Fbo outputFbo, outputFbo2, multisampledFbo, guiFbo;
	public static WaterFrameBuffers wfbo;

	public static boolean renderGUI = true;

	public static void main(String[] args) {
		new Thread("commandListener") {// REPLACE WITH UPDATE METHOD IN e.g.
										// DISPLAYMANAGER.UPDATE ? 
			@Override
			public void run() {
				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
					while (true) {// !!!
						while (!r.ready()) {
							try {
								sleep(500);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						String input = r.readLine();
						if (input.equalsIgnoreCase("stop")) {
							running = false;
							CLEANUPNOW = true;
						} else if (CommandProcessor.addCommand(input)) {
							Err.err.println("User input: \"" + input + "\"");
						} else {
							Err.err.println("HÄ? \"" + input + "\"");
						}
					}
				} catch (IOException i) {
					i.printStackTrace();
				}
			}
		}.start();
		// if(args.length > 0 && args[0].equals("server")){
		// ServerLoop.run();
		// }else{
		DisplayManager.run();
		ResourceManager.loadResources();
		ThreadManager.goOn();
		multisampledFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		outputFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
		outputFbo2 = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
		guiFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
		wfbo = new WaterFrameBuffers();
		PreferenceSaver.applyPrefs();
		Frame.start();
		alive = false;
		System.exit(0);
		// }
	}
	
	private static Menü playerGUI = new Menü(new ArrayList<MenuThing>());
	private static Button coords, show;
	
	public static void addPlayerGUI(){
		show = new Button("", new Rectangle(610, 20-10*DisplayManager.desiredRatioForGUI, 
				80, 80*DisplayManager.desiredRatioForGUI), Frame.button, true);
		show.setHOVER(false);
		
		int blackBorder = SC.getTex("BlackBorder").getID();
		
		Button forText = new Button("nothing", new Rectangle(310, 30, 
				290, 80), blackBorder, true);
		forText.setHOVER(false);
		
		headUpDisplay = new Button("", new Rectangle(300, 20, 400, 100), blackBorder, false){
			final short zero = 0;
			final boolean showSunLightLevel = false;
			@Override
			public void update(){
				Vector3f c = MousePicker.getNextFilledBlockCoord(100, Camera.underWater());
				if(c != null){
//					show.show();
					short b = ChunkManager.getBlockID(c);
					show.setTex(ItemRenderer.getItemTex(b));
					
					String s = ChunkManager.getBlockString(c);
					if(showSunLightLevel){
						c.add(MousePicker.calcVect);
						s += " " + ChunkManager.getSunLight(c);
					}
					forText.setText(s);
				}else{
//					show.hide();
					show.setTex(blackBorder);
					forText.setText(Block.string(zero));
				}
				forText.setTextColor(FontColorManager.one);
				super.update();// maybe remove, maybe add new features to make it viable?
			}
		};//, SC.getTex("BlackBorder").getID(), true
		headUpDisplay.setHOVER(false);
		headUpDisplay.setTextYOffset(-40);
		headUpDisplay.attach(show);
		headUpDisplay.attach(forText);
		playerGUI.add(headUpDisplay);
		
		coords = new Button("HI!", new Rectangle(775, 900, 200, 80)){
			@Override
			public void update(){
				setText("X: " + Meth.toInt(WorldObjects.player.getPosition().x) + 
						" Y: " + Meth.toInt(WorldObjects.player.getPosition().y) + 
						" Z: " + Meth.toInt(WorldObjects.player.getPosition().z));
				super.update();
			}
		};
		coords.setHOVER(false);
		playerGUI.add(coords);
		life = new ProgressBar(new Rectangle(100, 920, 150, 30), SC.getTex("texPack/red").getID(), false, 1);
		life.show();
		playerGUI.add(life);
		Button clock = new Button("clock", new Rectangle(10, 10*DisplayManager.desiredRatioForGUI, 250, 75)){
			@Override
			public void update(){
				int hours = (int) TM.getDayTime();
				int minutes = (int) Math.floor((TM.getDayTime() % 1) * 60f);
				StringBuilder time = new StringBuilder();
				if (minutes < 10) {
					time.append(hours);
					time.append(":0");
					time.append(minutes);
				} else {
					time.append(hours);
					time.append(":");
					time.append(minutes);
				}
				time.append("; Day: ");
				time.append(Meth.toInt((float) TM.inGameDays()));
				time.append("; It's ");
				time.append(TM.season());
				setText(time.toString());
				super.update();
			}
		};
//		clock.setFontSize(0.9f);
		clock.setHOVER(false);
		playerGUI.add(clock);
		Button fps = new Button("ok", new Rectangle(990 - 100, 10*DisplayManager.desiredRatioForGUI, 100, 50)){
			@Override
			public void update(){
				if(Meth.systemTime() > lastFps + 250){
					setText("FPS: " + Meth.toInt(1f/DisplayManager.getDFTS()));
					lastFps = Meth.systemTime();
				}
				super.update();
			}
		};
		fps.setTextYOffset(-7);
		//		fps.setFontSize(0.9f);
		fps.setHOVER(false);
		fps.setTextPos();
		playerGUI.add(fps);
		playerGUI.add(WorldObjects.player.getInventory().getPanel());
	}

	private static Menü debugPanel = new Menü(new ArrayList<>());
	
	public static void addDebugGUI(){
		debugPanel.add(new Button("", new Rectangle(900, 400, 100, 200), SC.getTex("BlackBorder").getID(), true){
			@Override
			public void update(){
				setText("Verts: " + BlockRenderer.VERTICES);
			}
		});
		debugPanel.hide();
	}
	
	public static void runGame() {
		MENUOPEN = false;
		ANYMENUOPEN = false;
		
		Projectil.GAIN = Projectil.NORMGAIN;
		icon16.hide();
		ThreadManager.goOn();
		WorldObjects.init();
		PlanetManager.init();
		
		WeatherController.init();
		while (Meth.systemTime() < ResourceManager.loadStart + ResourceManager.loadingTime && !Mouse.isButtonDown(0)
				&& !Mouse.isButtonDown(1)) {
			renderLoadingScreen();
			Meth.wartn(50);
		}
		Mouse.setGrabbed(true);
		firstRender = true;
		SkyRenderer.setMoonThings();

		addPlayerGUI();
		addDebugGUI();
		
		boolean resetDMouse = false;
		while (running && !DisplayManager.isCloseRequested()) {
			if (CLEANUPNOW) {
				running = false;
				cleanUp();
				break;
			}
			if (KeyManager.escapeEquivalentPressed() && !Inv2D.open) {
				playerGUI.hide();
				debugPanel.hide();
//				Err.err.println("Show shown? " + show.visible() + " tv? " + show.getTex().visible());
//				Err.err.println("GUIManager.transparents().contains(show.getTex()): " + GUIManager.transparents().contains(show.getTex()));
				Chat.hideCompletely();
				MENUOPEN = true;
				ANYMENUOPEN = true;
				Frame.startInMenu();
				DisplayManager.setFrameTimeSeconds(0.000000000001f);
				WorldObjects.player.setCooldowns();
				MENUOPEN = false;
				ANYMENUOPEN = false;
				resetDMouse = true;
				Mouse.getDX();
				Mouse.getDY();
				Chat.showAgain();
				debugPanel.show();
				playerGUI.show();
			}
			if (Keyboard.keyTipped(GUISWITCHKEY)) {
				renderGUI = !renderGUI;
			}
			
			update();
			
			updateIngameText();
			ParticleSystemMaster.update();
			ParticleMaster.update();

			if (renderGUI)
				renderGUIandText();
			render();
			
			DisplayManager.disableVsyncMessage();
			DisplayManager.updateWindow();
			if (firstRender) {
				firstRender = false;
				DisplayManager.setFrameTimeSeconds(0.00001f);
			}
			if (resetDMouse) {
				Mouse.getDY();
				Mouse.getDX();
				resetDMouse = false;
			}
		}
		cleanUpGame();
		
		GUIManager.reset();
		TextMaster.clearDeleteList();
		
		playerGUI.cleanUpAndClear();

		Camera.setPosition(0, 0, 0);
		while (Meth.systemTime() < ResourceManager.loadStart + ResourceManager.loadingTime && !Mouse.isButtonDown(0)
				&& !Mouse.isButtonDown(1)) {
			renderLoadingScreen();
			Meth.wartn(50);
		}

		icon16.show();

		Projectil.GAIN = Projectil.MENGAIN;
		
		ANYMENUOPEN = true;
		

	}
	
	public static void renderGUIandText() {
		guiFbo.bindFrameBuffer();
//		GL30.glClearBufferfv(GL11.GL_DEPTH, 0, depthClear);
//		GL30.glClearBufferfv(GL11.GL_COLOR, 0, colorClear);
		guiFbo.clearColor1AndDepthBuffer();
//		GL11.glDisable(GL11.GL_ALPHA);
		GuiRenderer.render(GUIManager.getTex());
		TextMaster.render();
		guiFbo.unbindFrameBuffer();
	}

	private static Vector4f fi = new Vector4f(0, 1, 0, -(Meth.waterHeight + 1));
	private static Vector4f sec = new Vector4f(0, -1, 0, (Meth.waterHeight + 1));

	public static boolean renderForWater = false, renderingRefraction = false;
	private static int WHU = 0, updateAllXFrames = 10;
	private static float dwh;
	private static float[] dwhs = new float[10];

	public static void render() {
		MasterRenderer.clear();

		if (!MasterRenderer.dontUseShadowsAtAll && MasterRenderer.SHADOWS && TM.isDay()) {
			MasterRenderer.renderShadowMap(EntityManager.getList(), WorldObjects.sun);
		}
		
		if (WaterRenderer.REFLECTIVE) {
			if (WHU == 0) {
				for (int i = dwhs.length - 2; i >= 0; i--) {
					dwhs[i + 1] = dwhs[i];
				}
				dwhs[0] = WaterManager.getAverageAbsHeight() + WaterRenderer.WAVEHEIGHT;
				float X = 0;
				for (int i = 0; i < dwhs.length; i++) {
					X += dwhs[i];
				}
				X /= dwhs.length;
				dwh = X;
				WHU++;
			} else {
				WHU++;
				if (WHU == updateAllXFrames) {
					WHU = 0;
				}
			}
			fi.w = -dwh;
			sec.w = dwh;
			renderForWater = true;
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			wfbo.bindReflectionFrameBuffer();
			float distance = 2 * (Camera.getPosition().y - dwh);
			if (distance < 0) {
				fi.w *= -1;
				sec.w *= -1;
				fi.y = -1;
				sec.y = 1;
			} else {
				fi.y = 1;
				sec.y = -1;
			}
			Camera.getPosition().y -= distance;
			Camera.invertPitch();
			MasterRenderer.renderAll(EntityManager.getList(), null, WorldObjects.getLightsToRender(), fi);
			Camera.invertPitch();
			Camera.getPosition().y += distance;
			renderingRefraction = true;
			wfbo.bindRefractionFrameBuffer();
			MasterRenderer.renderAll(EntityManager.getList(), null, WorldObjects.getLightsToRender(), sec);
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			wfbo.unbindCurrentFrameBuffer();
			renderingRefraction = false;
			renderForWater = false;
		}

		multisampledFbo.bindFrameBuffer();
		MasterRenderer.renderAll(EntityManager.getList(), null, WorldObjects.getLightsToRender(), Vects.NULL4);
		multisampledFbo.unbindFrameBuffer();
		
		multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
		multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
		PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture(), guiFbo.getColourTexture(), renderGUI || MENUOPEN);
//		guiFbo.resolveToScreen();
	}

//	private static String fps = "";
	private static long lastFps = 0;
	private static Button headUpDisplay;
	private static ProgressBar life;
//	private static GUIText text;

	public static void updateIngameText() {
		FontColorManager.update();
		Out.update();
		if (!MENUOPEN) {
//			float x = WorldObjects.player.getPosition().x;
//			float y = WorldObjects.player.getPosition().y;
//			float z = WorldObjects.player.getPosition().z;
			
//			if (Meth.systemTime() > lastFPS + 500) {
//				lastFPS = Meth.systemTime();
//				fps = " FPS: " + (int) (1.0f / DisplayManager.getDFTS());
//			}
//			String TEXT = fps + " rendering " + BlockRenderer.VERTICES + " verts in the terrain";
//			if (text == null) {
//				text = new GUIText(// live +
//						TEXT, 1.4f, font, tpos, 0.75f, true);
//			} else {
//				text.setText(TEXT);
//			}
			
//			headUpDisplay.setText(TEXT);
//			headUpDisplay.setTextPos();
		}
//		text.setColour(FontColorManager.one);
		headUpDisplay.setTextColor(FontColorManager.one.x, FontColorManager.one.y, FontColorManager.one.z);
		
		life.setProgress(WorldObjects.player.lives()*0.1f);
		
		playerGUI.update();
		
		if(KeyManager.switchDebugPanel())
			if(debugPanel.visible())
				debugPanel.hide();
			else
				debugPanel.show();
		
		if(debugPanel.visible())
			debugPanel.update();
		
	}

//	private static Vector2f tpos = new Vector2f(0.15f, 0.03f);

	public static void update() {
		// time shit
		TM.update();
		// color shit
		FontColorManager.update();
		// sounds/music
		SourcesManager.update();
		MusicManager.update();

		// inGameStuff
		WorldObjects.update();
		
		PlanetManager.update();
		
		TickManager.update();
	}

	public static void cleanUpGame() {
		Err.err.println("Saving stuff...");
		ThreadManager.shutdown();
		WeatherController.save();
		Err.err.println("Weather settings saved!");
		WorldObjects.save();
		WorldObjects.cleanUp();
		Err.err.println("World saved!");
		PlanetManager.cleanUp();
		Err.err.println("Planets cleanedUp!");
		TM.save();
		Err.err.println("Time settings saved!");
		PreferenceSaver.savePrefs();
		Err.err.println("Preferences saved!");
	}

	public static void cleanUp() {
		cleanUpGame();
		Err.err.println("Cleaning up!");
		ResourceManager.cleanUp();
		Err.err.println("Good bye!");
		DisplayManager.closeDisplay();
	}

	public static void recreateFrameBuffers() {
		if (multisampledFbo != null) {
			multisampledFbo.cleanUp();
			outputFbo.cleanUp();
			outputFbo2.cleanUp();
			multisampledFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT);
			outputFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
			outputFbo2 = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
			guiFbo.cleanUp();
			guiFbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_TEXTURE);
			PostProcessing.recreate();
			WaterFrameBuffers.setSizesNew();
			wfbo.cleanUp();
			wfbo = new WaterFrameBuffers();
		}
	}

	public static void renderMenuWithSkyBox() {
		MasterRenderer.clear();
		multisampledFbo.bindFrameBuffer();
//		GL30.glClearBufferfv(GL11.GL_DEPTH, 0, depthClear);
//		GL30.glClearBufferfv(GL11.GL_COLOR, 0, colorClear);
//		GL30.glClearBufferfv(GL11.GL_COLOR, 1, colorClear);
		multisampledFbo.clearMultisampled();
		MasterRenderer.renderSkyForMenu();
		multisampledFbo.unbindFrameBuffer();
		renderGUIandText();
		multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
		multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
		PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture(), guiFbo.getColourTexture(), true);
		DisplayManager.updateWindow();
	}

	private static GuiTexture loading;
	public static int chunksRendered;

	public static void renderLoadingScreen() {
		if (loading == null)
			loading = new GuiTexture(SC.getTex("LoadingScreens/L1quad").getID(), new Vector2f(0, 0),
					new Vector2f(1, 1), false);
		// loading.setDisplayLevel(GUIManager.UP);

		MasterRenderer.clear();
		GUIManager.addGuiTexture(loading);
		GuiRenderer.render(GUIManager.getTex());
		GUIManager.removeGuiTexture(loading);
		DisplayManager.updateWindow();
	}

	private static GuiTexture icon16;

	public static void renderStartAnimation() {
		// renderUWE_THE_KILLERAnimation();
		icon16 = new GuiTexture(SC.getTex("Icons/16").getID(), new Vector2f(), new Vector2f(0), false);// 0.5f,
																								// 0.5f*DisplayManager.desiredRatioForGUI
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, icon16.getTexture());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		icon16.show();
		// float dist = 25;
		// WeatherController.flare(dist*(float)Math.sin(95*Meth.angToRad),
		// dist*(float)Math.sin(35*Meth.angToRad),
		// dist*(float)Math.cos(95*Meth.angToRad), 0.25f);
		renderMenuWithSkyBox();
		DisplayManager.setFrameTimeSeconds(0.000001f);
		float speed = 0;
		while (icon16.getScale().x < 0.2f) {
			speed += DisplayManager.getFrameTimeSeconds();
			icon16.getScale().x += speed * DisplayManager.getFrameTimeSeconds() * 0.25f;
			icon16.getScale().y = icon16.getScale().x * DisplayManager.desiredRatioForGUI;
			renderMenuWithSkyBox();
		}
		icon16.getScale().set(0.2f, 0.2f * DisplayManager.desiredRatioForGUI);
		while (icon16.getPos().x > -0.6f) {
			icon16.getPos().x -= 0.5f * DisplayManager.getFrameTimeSeconds();
			icon16.getPos().y = -(5 / 6f) * icon16.getPos().x;
			renderMenuWithSkyBox();
		}
		icon16.getPos().x = -0.6f;
		icon16.getPos().y = (5 / 6f) * 0.6f;
		DisplayManager.setFrameTimeSeconds(0.00001f);
	}

	private static GuiTexture UWE_THE_KILLER_STUDIOS, rlyDarkGreen1, rlyDarkGreen2, rlyDarkGreen3;

	public static void renderUWE_THE_KILLERAnimation() {
		DisplayManager.setFrameTimeSeconds(0.0000001f);
		UWE_THE_KILLER_STUDIOS = new GuiTexture(SC.getTex("LoadingScreens/uwe_the_killer_studios").getID(),
				new Vector2f(0, 0), new Vector2f(0), false);
		UWE_THE_KILLER_STUDIOS.show();
		rlyDarkGreen1 = new GuiTexture(SC.getTex("LoadingScreens/rlyDarkGreen").getID(), new Vector2f(0, 0.3f),
				new Vector2f(0, 0.01f), false);
		rlyDarkGreen1.show();
		rlyDarkGreen2 = new GuiTexture(rlyDarkGreen1.getTexture(), new Vector2f(0, -0.05f), new Vector2f(0, 0.01f), false);
		rlyDarkGreen2.show();
		rlyDarkGreen3 = new GuiTexture(rlyDarkGreen1.getTexture(), new Vector2f(0, -0.4f), new Vector2f(0, 0.01f), false);
		rlyDarkGreen3.show();
		while (UWE_THE_KILLER_STUDIOS.getScale().x < 1) {
			UWE_THE_KILLER_STUDIOS.getScale()
					.set(UWE_THE_KILLER_STUDIOS.getScale().x + 0.5f * DisplayManager.getFrameTimeSeconds());
			UWE_THE_KILLER_STUDIOS.setHighlight(UWE_THE_KILLER_STUDIOS.getScale().x - 1);
			renderGUIandText();
			DisplayManager.updateWindow();
		}
		while (rlyDarkGreen1.getScale().x < 0.1f) {
			rlyDarkGreen1.getScale().x += DisplayManager.getFrameTimeSeconds();
			renderGUIandText();
			DisplayManager.updateWindow();
		}
		while (rlyDarkGreen2.getScale().x < 0.75f) {
			rlyDarkGreen2.getScale().x += 1.5f * DisplayManager.getFrameTimeSeconds();
			renderGUIandText();
			DisplayManager.updateWindow();
		}
		while (rlyDarkGreen3.getScale().x < 0.5f) {
			rlyDarkGreen3.getScale().x += DisplayManager.getFrameTimeSeconds();
			renderGUIandText();
			DisplayManager.updateWindow();
		}
		UWE_THE_KILLER_STUDIOS.hide();
		rlyDarkGreen1.hide();
		rlyDarkGreen2.hide();
		rlyDarkGreen3.hide();
		Meth.wartn(1000);
	}

}
