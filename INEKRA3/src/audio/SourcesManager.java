package audio;

import java.util.ArrayList;

import org.joml.Vector3f;

public class SourcesManager {
	
	// TODO remove loading of audio files. Do that on client only. Constants shall be standard here... then convertion!

	private static ArrayList<Source> sources = new ArrayList<Source>();
	private static ArrayList<Source> nis = new ArrayList<Source>();

	public static int missle3;
	// public static final int shotgunReload =
	// AudioMaster.loadSound("shotreload.ogg");
	public static int thundersound;
	public static int boom;
	public static int block;
	public static int glass;
	// public static final int LUCIFERWATCHTOWER =
	// AudioMaster.loadSound("audio/Lucifer - All Along the Watchtower.ogg");
	
	public static int BLASTERSOUND;
	
	public static void init() {
		missle3 = AudioMaster.loadSound("audio/missile 3.ogg");
		thundersound = AudioMaster.loadSound("audio/thunder3.ogg");
		boom = AudioMaster.loadSound("audio/bomb-03.ogg");
		block = AudioMaster.loadSound("blockStuff/dropsound.ogg");
		glass = AudioMaster.loadSound("blockStuff/dropsound_glass.ogg");
		BLASTERSOUND = AudioMaster.loadSound("audio/Laser_Blaster-SoundBible.ogg");
	}
	
	public static Source getSource() {
		if (nis.size() == 0) {
			return new Source();
		} else {
			return nis.get(0);
		}
	}

	public static void update() {
		for (int i = 0; i < sources.size(); i++) {
			if (!sources.get(i).isPlaying()) {
				// sources.get(i).stop();
				nis.add(sources.get(i));
				sources.remove(i);
			}
		}
		while (nis.size() > 50) {
			nis.get(nis.size() - 1).delete();
			nis.remove(nis.size() - 1);
		}
	}

	public static Source playSource(int soundbuffer, float volume, Vector3f position) {
		Source s;
		if (nis.size() == 0) {
			s = new Source();
		} else {
			s = nis.get(nis.size() - 1);
			nis.remove(nis.size() - 1);
		}
		s.setPosition(position);
		s.setVolume(volume);
		s.play(soundbuffer);
		sources.add(s);
		return s;
	}

	public static void play(int soundbuffer, float volume, Vector3f position) {
		Source s;
		if (nis.size() == 0) {
			s = new Source();
		} else {
			s = nis.get(nis.size() - 1);
			nis.remove(nis.size() - 1);
		}
		s.setPosition(position);
		s.setVolume(volume);
		s.play(soundbuffer);
		sources.add(s);
	}

	public static void play(int soundbuffer, float volume, Vector3f position, Vector3f speed) {
		Source s;
		if (nis.size() == 0) {
			s = new Source();
		} else {
			s = nis.get(nis.size() - 1);
			nis.remove(nis.size() - 1);
		}
		s.setPosition(position);
		s.setVolume(volume);
		s.setVelocity(speed);
		s.play(soundbuffer);
		sources.add(s);
	}

	public static void addSource(Source s) {
		if (!sources.contains(s)) {
			sources.add(s);
		}
	}

	public static void removeSource(Source s) {
		sources.remove(s);
	}

	public static void cleanUp() {
		for (int i = 0; i < sources.size(); i++) {
			sources.get(i).delete();
		}
	}

}
