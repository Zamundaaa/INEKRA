package audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.libc.LibCStdlib;

import gameStuff.Err;
import mainInterface.Intraface;
import toolBox.Tools;

public class AudioMaster {

	public static final float sounddistmult = 10;
	public static final float soundvelmult = 0.1f;

	private static float GAIN = Tools.loadFloatPreference("GAIN", 1);

	public static boolean soundEnabled = GAIN != 0;
	public static boolean musicEnabled = Tools.loadBoolPreference("music", true);
	public static boolean CREATED = false;

	private static List<Integer> buffers = new ArrayList<Integer>();
	private static List<Source> sources = new ArrayList<Source>();

	private static long context;
	private static long device;

	public static void init() throws Exception {
		if(Intraface.isServer)return;
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);
		contextAttribList.put(ALC_REFRESH);
		contextAttribList.put(60);
		contextAttribList.put(ALC_SYNC);
		contextAttribList.put(ALC_FALSE);

		// Don't worry about this for now; deals with effects count
		contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
		contextAttribList.put(2);

		contextAttribList.put(0);
		contextAttribList.flip();

		context = ALC10.alcCreateContext(device, contextAttribList);

		if (!ALC10.alcMakeContextCurrent(context)) {
			throw new Exception("Failed to make context current");
		}

		AL.createCapabilities(deviceCaps);
		CREATED = true;

		setGain(GAIN);

		Err.err.println("AudioMaster inited!");

	}

	public static void setGain(float gain) {
		GAIN = gain;
		if(Intraface.isServer)return;
		if (GAIN > 0) {
			alListenerf(AL_GAIN, gain);
			if (!soundEnabled) {
				enableSound();
			}
		} else {
			disableSound();
		}
	}

	public static float gain() {
		return GAIN;
	}

	public static void enableSound() {
		for (int i = 0; i < sources.size(); i++) {
			sources.get(i).continuePlaying();
		}
		soundEnabled = true;
	}

	public static void disableSound() {
		for (int i = 0; i < sources.size(); i++) {
			sources.get(i).pause();
		}
		soundEnabled = false;
	}

	static void insertSource(Source s) {
		sources.add(s);
	}

	static void deleteSource(Source s) {
		sources.remove(s);
	}

	public static int loadSound(String file) {
		if(Intraface.isServer)return 0;
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		// WaveData wd = WaveData.create(file);
		// AL10.alBufferData(buffer, wd.format, wd.data, wd.samplerate);
		// wd.dispose();

		IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer sampleRateBuffer = BufferUtils.createIntBuffer(1);

		ByteBuffer b = Tools.readBytesRel(file);
		ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_memory(b, channelsBuffer, sampleRateBuffer);

		// Retreive the extra information that was stored in the buffers by the
		// function
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();

		int format = -1;
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}

		alBufferData(buffer, format, rawAudioBuffer, sampleRate);

		LibCStdlib.free(rawAudioBuffer);

		return buffer;
	}

	public static void unloadSound(int soundbuffer) {
		AL10.alDeleteBuffers(soundbuffer);
	}

	/**
	 * @param file
	 *            complete (absolute) file path
	 * @return
	 */
	public static int loadSoundFromOutside(String file) {
		// int buffer = AL10.alGenBuffers();
		// buffers.add(buffer);
		// FileInputStream in = null;
		// WaveData waveFile = null;
		// try {
		// in = new FileInputStream(new File(file));
		// BufferedInputStream b = new BufferedInputStream(in);
		// waveFile = WaveData.create(b);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace(Err.err);
		// } finally {
		// if (in != null) {
		// try {
		// in.close();
		// } catch (IOException e) {
		// e.printStackTrace(Err.err);
		// }
		// }
		// }
		// AL10.alBufferData(buffer, waveFile.format, waveFile.data,
		// waveFile.samplerate);
		// waveFile.dispose();
		// return buffer
		return 0;
	}

	public static void setListenerData(Vector3f pos, Vector3f velocity, float rotY) {
		if(Intraface.isServer)return;
		AL10.alListener3f(AL10.AL_POSITION, pos.x * sounddistmult, pos.y * sounddistmult, pos.z * sounddistmult);
		AL10.alListener3f(AL10.AL_VELOCITY, velocity.x * soundvelmult, velocity.y * soundvelmult,
				velocity.z * soundvelmult);
		AL10.alListenerf(AL10.AL_ORIENTATION, rotY);
	}

	public static void cleanUp() {
		if(Intraface.isServer)return;
		for (int buffer : buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		ALC10.alcCloseDevice(device);
		ALC.destroy();
		CREATED = false;
		Tools.setFloatPreference("GAIN", GAIN);
		Tools.setBoolPreference("music", musicEnabled);
		Err.err.println("AUDIO CLEANUP!");
	}

	public static boolean enabled() {
		return CREATED;
	}

}
