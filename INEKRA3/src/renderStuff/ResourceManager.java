package renderStuff;

import audio.AudioMaster;
import audio.SourcesManager;
import controls.Keyboard;
import controls.Mouse;
import fontRendering.TextMaster;
import gameStuff.*;
import guis.GuiRenderer;
import line.LineRenderer;
import particles.PTM;
import postProcessing.PostProcessing;
import solarSystemRendering.PlanetRenderer;
import toolBox.Meth;
import toolBox.Tools;

public abstract class ResourceManager {

	public static long loadingTime = Tools.loadLongPreference("Ladezeit", 10000);

	public static long loadStart;
	
	public static void loadOpenGLResources(){
		Models.loadAllRawModelsAndTextures();
		SC.init();
		PTM.init();
		MasterRenderer.init();
		PlanetRenderer.init();
		GuiRenderer.init();
		loadStart = Meth.systemTime();
		MainLoop.renderLoadingScreen();
		try {
			AudioMaster.init();
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			System.exit(-1);
		}
		SourcesManager.init();
		TextMaster.init();
		LineRenderer.init();
		PostProcessing.init();
		Keyboard.init();
		Mouse.init();
		loadModelsAndTextures();
		
	}

	/**
	 * OOPS. Only loads OpenGL Resources whatsoever!
	 */
	public static void loadResources() {
		loadOpenGLResources();
	}

	public static void cleanUp() {
		SourcesManager.cleanUp();
		AudioMaster.cleanUp();
		MasterRenderer.cleanUp();
		TextMaster.cleanUp();
		WorldObjects.cleanUp();
		LineRenderer.cleanUp();
		PostProcessing.cleanUp();
		Loader.cleanUp();
		PlanetRenderer.cleanUp();
		// G3DRenderer.cleanUp();
		SC.cleanUp();
		Tools.setLongPreference("Ladezeit", loadingTime);
	}

	/**
	 * models and textures, at least those needed by other threads than the main
	 * thread can be loaded here to bypass the openGL restriction to one thread
	 */
	public static void loadModelsAndTextures() {
		// TODO
	}

}
