package audio;

import static audio.AudioMaster.sounddistmult;
import static audio.AudioMaster.soundvelmult;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class Source {

	private int sourceId;

	public Source() {
		sourceId = AL10.alGenSources();
		// AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, 2);
		// AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, 6);
		// AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, 50);

		AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.5f);
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 0);

		AudioMaster.insertSource(this);
	}

	public void pause() {
		AL10.alSourcePause(sourceId);
	}

	public void continuePlaying() {
		AL10.alSourcePlay(sourceId);
	}

	public void stop() {
		AL10.alSourceStop(sourceId);
	}

	public void setPosition(Vector3f pos) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, pos.x * sounddistmult, pos.y * sounddistmult,
				pos.z * sounddistmult);
	}

	public void setPosition(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x * sounddistmult, y * sounddistmult, z * sounddistmult);
	}

	public void setPitch(float pitch) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}

	public void setVolume(float gain) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
	}

	public void setVelocity(Vector3f velo) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, velo.x * soundvelmult, velo.y * soundvelmult,
				velo.z * soundvelmult);
	}

	public void setLooping(boolean loop) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void play(int buffer) {
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(sourceId);
	}

	public void delete() {
		stop();
		AudioMaster.deleteSource(this);
		AL10.alDeleteSources(sourceId);
	}

}
