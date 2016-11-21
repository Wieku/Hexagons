package xyz.hexagons.client.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.audio.Ogg;
import com.badlogic.gdx.backends.lwjgl.audio.OggInputStream;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import me.wieku.audio.analysis.BeatDetect;
import me.wieku.audio.analysis.FFT;
import org.lwjgl.BufferUtils;
import xyz.hexagons.client.audio.AudioPlayer;
import xyz.hexagons.client.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DesktopAudioPlayer implements AudioPlayer{

	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	private boolean ended;
	private boolean pause;
	private FileHandle handle;
	private boolean firstStart = false;
	private FFT fft;
	private BeatDetect detect;
	private float[] outBuf = new float[60];
	private OpenALMusic musicPlayer;

	private static Field buffr;

	private static final int SIZE = 4096;
	private byte[] bufferByte = new byte[SIZE*2];
	private float[] bufferFloat = new float[SIZE*2];
	private float[] bufferFloat2 = new float[SIZE];

	private float secPerBuffer;
	private float audioLength;

	private float bgvol, dstvol, timedelta = -1, time = -2;


	public DesktopAudioPlayer(FileHandle fileHandle) {
		if(!fileHandle.exists()) throw new IllegalStateException("Cannot find audio");
		handle = fileHandle;

		musicPlayer = (OpenALMusic) Gdx.audio.newMusic(handle);

		try {
			Field sPB = OpenALMusic.class.getDeclaredField("secondsPerBuffer");
			sPB.setAccessible(true);
			sPB.set(musicPlayer, (float)SIZE*2/(musicPlayer.getChannels()*musicPlayer.getRate()*2));
			secPerBuffer = (float) sPB.get(musicPlayer);

			Field inp = Ogg.Music.class.getDeclaredField("input");
			inp.setAccessible(true);

			OggInputStream str = (OggInputStream) inp.get(musicPlayer);

			audioLength = ((str.getLength()*1f)/SIZE*2)/secPerBuffer;
			System.out.println(audioLength);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		musicPlayer.setOnCompletionListener(m -> ended = true);

		fft = new FFT(SIZE, musicPlayer.getRate());
		fft.linAverages(60);

		detect = new BeatDetect(SIZE*2, musicPlayer.getRate());
		detect.setSensitivity(10);
	}

	@Override
	public FileHandle getFile() {
		return handle;
	}

	@Override
	public float getVolume() {
		return musicPlayer.getVolume();
	}

	@Override
	public void setVolume(float volume) {
		musicPlayer.setVolume(volume);
	}

	@Override
	public float getPosition() {
		return musicPlayer.getPosition();
	}

	@Override
	public void setPosition(float milis) {
		new Thread(()->{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			float vol = musicPlayer.getVolume();
			setVolume(0);
			musicPlayer.setPosition(milis);
			if(pause) musicPlayer.pause();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setVolume(vol);
			System.out.println(musicPlayer.getPosition());
		}).start();
	}

	@Override
	public void play(){
		musicPlayer.play();
		firstStart = true;
		ended = false;
		pause = false;
	}

	@Override
	public void play(float time){
		musicPlayer.play();
		if(firstStart || time > 0){
			setPosition(time);
		}
		firstStart = true;
		ended = false;

	}

	@Override
	public void update(float delta){


		if(timedelta < time){
			timedelta+=delta;
			setVolume(bgvol+((dstvol-bgvol)*timedelta)/time);
		}


		try {
			bufferByte = (byte[]) buffr.get(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Utils.byteToFloat(bufferByte, 0, bufferFloat, 0, bufferFloat.length);

		for(int i = 0, j = 0; i < bufferFloat2.length; i++, j+=2)
			bufferFloat2[i] = (bufferFloat[j] + bufferFloat[j+1]) / 2;

		fft.forward(bufferFloat2);

		for(int i = 0; i < fft.avgSize(); i++)
			outBuf[i] = fft.getAvg(i);

		detect.detect(bufferFloat);
	}

	@Override
	public void glideVolume(float volume, float time){
		bgvol = getVolume();
		dstvol = volume;
		this.time = time;
		timedelta=0;
	}

	@Override
	public float[] getFFT(){
		return outBuf;
	}

	@Override
	public boolean isOnset() {
		return detect.isRange(1,3,2);// || detect.isHat()/*Range(1,3,2) || detect.isRange(4,6,2)*/;
	}

	@Override
	public void pause(){
		pause = true;
		musicPlayer.pause();
	}

	@Override
	public boolean hasEnded(){
		return ended;
	}

	@Override
	public void stop(){
		musicPlayer.stop();
		ended = true;
	}

	@Override
	public void dispose(){
		musicPlayer.dispose();
	}

	@Override
	public boolean isPaused() {
		return !musicPlayer.isPlaying() && !ended;
	}

	@Override
	public void setLooping(boolean looping) {
		musicPlayer.setLooping(looping);
	}

	static {
		try {

			setFinalStatic(OpenALMusic.class.getDeclaredField("bufferSize"), SIZE*2);
			setFinalStatic(OpenALMusic.class.getDeclaredField("tempBytes"), new byte[SIZE*2]);
			setFinalStatic(OpenALMusic.class.getDeclaredField("tempBuffer"), BufferUtils.createByteBuffer(SIZE*2));

			buffr = OpenALMusic.class.getDeclaredField("tempBytes");
			buffr.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}