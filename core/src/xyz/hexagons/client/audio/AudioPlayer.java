package xyz.hexagons.client.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.audio.Ogg;
import com.badlogic.gdx.backends.lwjgl.audio.OggInputStream;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import me.wieku.audio.analysis.BeatDetect;
import me.wieku.audio.analysis.FFT;
import org.lwjgl.BufferUtils;
import xyz.hexagons.client.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

public class AudioPlayer {

	private boolean ended;
	private FileHandle handle;
	private boolean firstStart = false;
	private FFT fft;
	private BeatDetect detect;
	private float[] outBuf = new float[60];
	private Music musicPlayer;
	private static Field buffr;
	private static final int SIZE = 4096;

	private static int BUFFER_SIZE;
	private float secPerBuffer;
	private float audioLength;

	static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	static {
		try {

			//setFinalStatic(OpenALMusic.class.getDeclaredField("bufferSize"), SIZE*2);
			//setFinalStatic(OpenALMusic.class.getDeclaredField("tempBytes"), new byte[SIZE*2]);
			//setFinalStatic(OpenALMusic.class.getDeclaredField("tempBuffer"), BufferUtils.createByteBuffer(SIZE*2));
			Field field = OpenALMusic.class.getDeclaredField("bufferSize");
			field.setAccessible(true);
			BUFFER_SIZE = (int) field.get(null);


			buffr = OpenALMusic.class.getDeclaredField("tempBytes");
			buffr.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AudioPlayer(FileHandle file) {
		if(!file.exists()) throw new IllegalStateException("Cannot find audio");
		handle = file;

		musicPlayer = Gdx.audio.newMusic(handle);

		try {
			Field sPB = OpenALMusic.class.getDeclaredField("secondsPerBuffer");
			sPB.setAccessible(true);
			secPerBuffer = (float) sPB.get(musicPlayer);

			Field inp = Ogg.Music.class.getDeclaredField("input");
			inp.setAccessible(true);

			OggInputStream str = (OggInputStream) inp.get(musicPlayer);

			System.out.println(((str.getLength()*1f)/BUFFER_SIZE));
			audioLength = ((str.getLength()*1f)/BUFFER_SIZE)/secPerBuffer;
			System.out.println(audioLength);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		//musicPlayer.setLooping(true);
		musicPlayer.setOnCompletionListener(m -> ended = true);

		fft = new FFT(SIZE, ((OpenALMusic)musicPlayer).getRate());
		fft.linAverages(60);

		detect = new BeatDetect(2048, ((OpenALMusic)musicPlayer).getRate());

	}

	public FileHandle getFile(){
		return handle;
	}

	public float getVolume() {
		return musicPlayer.getVolume();
	}

	public void setVolume(float volume){
		musicPlayer.setVolume(volume);
	}
	
	public float getPosition(){
		return musicPlayer.getPosition();
	}
	
	public void setPosition(float milis){
		new Thread(()->{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			musicPlayer.setPosition(milis);
		}).start();
	}

	public void play(){
		musicPlayer.play();
		firstStart = true;
		ended = false;
	}

	public void play(float time){
		musicPlayer.play();
		if(firstStart || time > 0){
			new Thread(() -> setPosition(time)).start();
		}
		firstStart = true;
		ended = false;
	}

	private byte[] bufferByte = new byte[SIZE*2];
	private float[] bufferFloat = new float[SIZE*2];
	private float[] bufferFloat2 = new float[SIZE];

	public void update(float delta){


		if(timedelta < time){
			timedelta+=delta;

			setVolume(bgvol+((dstvol-bgvol)*timedelta)/time);

		} else {
			time = -2;
			timedelta = -1;
		}


		/*try {
			bufferByte = (byte[]) buffr.get(null);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}*/
		/*delta0+=delta;
		System.out.println(byteIndex);
		//byteIndex = Math.max(0, Math.min(9, (int) (secPB/delta0)));

		if(delta0 > secPB/20f) {
			delta0 = 0;
			++byteIndex;
			if(byteIndex>19){
				byteIndex=0;
				try {
					bufferByte = (byte[]) buffr.get(null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}*/
	}

	private float bgvol, dstvol, timedelta = -1, time = -2;

	public void glideVolume(float volume, float time){
		bgvol = getVolume();
		dstvol = volume;
		this.time = time;
		timedelta=0;
	}

	public float[] analyze(){
		Utils.byteToFloat(bufferByte, 0, bufferFloat, 0, bufferFloat.length);

		for(int i = 0, j = 0; i < bufferFloat2.length; i++, j+=2)
			bufferFloat2[i] = (bufferFloat[j] + bufferFloat[j+1]) / 2;

		fft.forward(bufferFloat2);

		for(int i = 0; i < fft.avgSize(); i++)
			outBuf[i] = fft.getAvg(i);

		return outBuf;
	}

	public void pause(){
		musicPlayer.pause();
	}
	
	public boolean hasEnded(){
		return ended;
	}
	
	public void stop(){
		musicPlayer.stop();
		ended = true;
	}

	public void dispose(){
		musicPlayer.dispose();
	}

	public boolean isPaused() {
		return !musicPlayer.isPlaying() && !ended;
	}

	public void setLooping(boolean looping) {
		musicPlayer.setLooping(looping);
	}
}