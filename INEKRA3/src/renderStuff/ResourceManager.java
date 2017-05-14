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

	public static void loadResources() {
//		Err.err.println("----------------loading Resources!");
		SC.init();
//		Err.err.println("----------------------loading PTM");
		PTM.init();
		MasterRenderer.init();
		
		PlanetRenderer.init();
		
//		Err.err.println("-------------------initing GuiRenderer");
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
//		Err.err.println("----------initing TextMaster");
		TextMaster.init();
//		Err.err.println("----------initing LineRenderer");
		LineRenderer.init();
//		Err.err.println("----------initing PostProcessing");
		PostProcessing.init();
//		G3DRenderer.init();
		Keyboard.init();
		Mouse.init();
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

}
