package audio;

import entities.Camera;

public class MusicManager {

	public static final int AdventureMeme = AudioMaster.loadSound("audio/Adventure Meme.ogg"),
			CyborgNinja = AudioMaster.loadSound("audio/Cyborg Ninja.ogg");

	public static final int MAX = 1;

	private static Source backplay = new Source();

	private static int current = 0;
	private static boolean justpaused = false;

	public static void play() {
		if (AudioMaster.soundEnabled && AudioMaster.musicEnabled) {
			if (!backplay.isPlaying()) {
				if (justpaused) {
					backplay.continuePlaying();
					justpaused = false;
				} else {
					backplay.play(get(current));
					current++;
					if (current > MAX) {
						current = 0;
					}
				}
			}
			backplay.setPosition(Camera.getPosition());
			// if(Keyboard.isKeyDown(GLFW.GLFW_KEY_O)){
			// backplay.stop();
			// }
		}
	}

	private static int get(int current) {
		if (current == 0) {
			return AdventureMeme;
		} else {
			return CyborgNinja;
		}
	}

	public static void stop() {
		backplay.pause();
		justpaused = true;
	}

	public static void update() {
		play();
	}

	// private int music;
	// private int soundBuffer;
	// private Source sound = new Source();
	// private String[] musics;
	//
	// musics = Tools.getFiles("sounds");
	// if (AudioMaster.soundEnabled && AudioMaster.musicEnabled) {
	// startSound();
	// }
	//
	//
	// public void startSound() {// MAYBE LATER
	// if (musics.length > 0) {
	// music = Meth.randomInt(0, musics.length - 1);
	// Out.println("Now playing: " + musics[music]);
	// soundBuffer = AudioMaster.loadSoundFromOutside(Tools.getFolderPath() +
	// "/sounds/" + musics[music]);
	// sound.play(soundBuffer);
	// sound.setVolume(0.3f);
	// }
	// }

}
