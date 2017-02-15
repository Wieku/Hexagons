package com.badlogic.gdx.backends.lwjgl.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;
import org.lwjgl.BufferUtils;
import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_BUFFERS_QUEUED;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_INVALID_VALUE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;
import org.lwjgl.openal.AL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/** @author Nathan Sweet with Sebastian Krajewski modifications */
public class OpenALPlayer extends OpenALMusic {
	static private final int bufferSize = 8192;
	static private final int bufferCount = 3;
	static private final int bytesPerSample = 2;
	static public final byte[] tempBytes = new byte[bufferSize];
	static private final ByteBuffer tempBuffer = BufferUtils.createByteBuffer(bufferSize);
	
	private final OpenALAudio audio;
	private IntBuffer buffers;
	private int sourceID = -1;
	private int format, sampleRate;
	private boolean isLooping, isPlaying;
	private float volume = 1;
	private float pan = 0;
	private float renderedSeconds, secondsPerBuffer;
	
	
	private OggInputStream input;
	private OggInputStream previousInput;
	protected FileHandle file = null;
	protected int bufferOverhead = 0;
	
	private OnCompletionListener onCompletionListener;
	
	public OpenALPlayer () {
		super(null, null);
		this.audio = (OpenALAudio) Gdx.audio;
		this.onCompletionListener = null;
	}
	
	
	public void setFile(FileHandle file) {
		if (audio.noDevice) return;
		this.file = file;
		input = new OggInputStream(file.read());
		setup(input.getChannels(), input.getSampleRate());
	}
	
	float length;
	
	protected void setup (int channels, int sampleRate) {
		this.format = channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
		this.sampleRate = sampleRate;
		secondsPerBuffer = (float)(bufferSize - bufferOverhead)  / (bytesPerSample * channels * sampleRate);
		/*try {
			VorbisFile file1 = new VorbisFile(file.file().getAbsolutePath());
			length = file1.time_total(-1);
		} catch (JOrbisException e) {
			e.printStackTrace();
		}*/
	}
	
	public void play () {
		if (audio.noDevice) return;
		if (sourceID == -1) {
			sourceID = audio.obtainSource(true);
			if (sourceID == -1) return;
			
			audio.music.add(this);
			
			if (buffers == null) {
				buffers = BufferUtils.createIntBuffer(bufferCount);
				alGenBuffers(buffers);
				if (alGetError() != AL_NO_ERROR) throw new GdxRuntimeException("Unable to allocate audio buffers.");
			}
			alSourcei(sourceID, AL_LOOPING, AL_FALSE);
			setPan(pan, volume);
			
			boolean filled = false; // Check if there's anything to actually play.
			for (int i = 0; i < bufferCount; i++) {
				int bufferID = buffers.get(i);
				if (!fill(bufferID)) break;
				filled = true;
				alSourceQueueBuffers(sourceID, bufferID);
			}
			if (!filled && onCompletionListener != null) onCompletionListener.onCompletion(this);
			
			if (alGetError() != AL_NO_ERROR) {
				stop();
				return;
			}
		}
		if (!isPlaying) {
			alSourcePlay(sourceID);
			isPlaying = true;
		}
	}
	
	public void stop () {
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		//audio.music.removeValue(this, true);
		reset();
		//audio.freeSource(sourceID);
		//sourceID = -1;
		renderedSeconds = 0;
		isPlaying = false;
	}
	
	public void pause () {
		if (audio.noDevice) return;
		if (sourceID != -1) alSourcePause(sourceID);
		isPlaying = false;
	}
	
	public boolean isPlaying () {
		if (audio.noDevice) return false;
		if (sourceID == -1) return false;
		return isPlaying;
	}
	
	public void setLooping (boolean isLooping) {
		this.isLooping = isLooping;
	}
	
	public boolean isLooping () {
		return isLooping;
	}
	
	public void setVolume (float volume) {
		this.volume = volume;
		if (audio.noDevice) return;
		if (sourceID != -1) alSourcef(sourceID, AL_GAIN, volume);
	}
	
	public float getVolume () {
		return this.volume;
	}
	
	public void setPan (float pan, float volume) {
		this.volume = volume;
		this.pan = pan;
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		alSource3f(sourceID, AL_POSITION, MathUtils.cos((pan - 1) * MathUtils.PI / 2), 0,
				MathUtils.sin((pan + 1) * MathUtils.PI / 2));
		alSourcef(sourceID, AL_GAIN, volume);
	}
	
	public void setPosition (float position) {
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		boolean wasPlaying = isPlaying;
		isPlaying = false;
		alSourceStop(sourceID);
		alSourceUnqueueBuffers(sourceID, buffers);
		renderedSeconds += (secondsPerBuffer * bufferCount);
		if (position <= renderedSeconds) {
			reset();
			renderedSeconds = 0;
		}
		while (renderedSeconds < (position - secondsPerBuffer)) {
			if (read(tempBytes) <= 0) break;
			renderedSeconds += secondsPerBuffer;
		}
		boolean filled = false;
		for (int i = 0; i < bufferCount; i++) {
			int bufferID = buffers.get(i);
			if (!fill(bufferID)) break;
			filled = true;
			alSourceQueueBuffers(sourceID, bufferID);
		}
		if (!filled) {
			stop();
			if (onCompletionListener != null) onCompletionListener.onCompletion(this);
		}
		alSourcef(sourceID, AL11.AL_SEC_OFFSET, position - renderedSeconds);
		if (wasPlaying) {
			alSourcePlay(sourceID);
			isPlaying = true;
		}
	}
	
	public float getPosition () {
		if (audio.noDevice) return 0;
		if (sourceID == -1) return 0;
		return renderedSeconds + alGetSourcef(sourceID, AL11.AL_SEC_OFFSET);
	}
	
	public float getDuration() {
		//System.out.println(input.getLength());
		return input.getLength() * 1f / bufferSize / secondsPerBuffer;
	}
	
	public int read (byte[] buffer) {
		if (input == null) {
			input = new OggInputStream(file.read(), previousInput);
			setup(input.getChannels(), input.getSampleRate());
			previousInput = null; // release this reference
		}
		
		return input.read(buffer);
	}
	
	public void reset () {
		StreamUtils.closeQuietly(input);
		previousInput = null;
		input = null;
	}
	
	
	protected void loop () {
		StreamUtils.closeQuietly(input);
		previousInput = input;
		input = null;
	}
	
	public int getChannels () {
		return format == AL_FORMAT_STEREO16 ? 2 : 1;
	}
	
	public int getRate () {
		return sampleRate;
	}
	
	public void update () {
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		
		boolean end = false;
		int buffers = alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
		while (buffers-- > 0) {
			int bufferID = alSourceUnqueueBuffers(sourceID);
			if (bufferID == AL_INVALID_VALUE) break;
			renderedSeconds += secondsPerBuffer;
			if (end) continue;
			if (fill(bufferID))
				alSourceQueueBuffers(sourceID, bufferID);
			else
				end = true;
		}
		if (end && alGetSourcei(sourceID, AL_BUFFERS_QUEUED) == 0) {
			stop();
			if (onCompletionListener != null) onCompletionListener.onCompletion(this);
		}
		
		// A buffer underflow will cause the source to stop.
		if (isPlaying && alGetSourcei(sourceID, AL_SOURCE_STATE) != AL_PLAYING) alSourcePlay(sourceID);
	}
	
	private boolean fill (int bufferID) {
		tempBuffer.clear();
		int length = read(tempBytes);
		if (length <= 0) {
			if (isLooping) {
				loop();
				renderedSeconds = 0;
				length = read(tempBytes);
				if (length <= 0) return false;
			} else
				return false;
		}
		tempBuffer.put(tempBytes, 0, length).flip();
		alBufferData(bufferID, format, tempBuffer, sampleRate);
		return true;
	}
	
	public void dispose () {
		stop();
		if (audio.noDevice) return;
		if (buffers == null) return;
		alSourceUnqueueBuffers(sourceID, buffers);
		//audio.freeSource(sourceID);
		//sourceID=-1;
		//alDeleteBuffers(buffers);
		//buffers = null;
		onCompletionListener = null;
	}
	
	public void setOnCompletionListener (OnCompletionListener listener) {
		onCompletionListener = listener;
	}
	
	public int getSourceId () {
		return sourceID;
	}
}

