package me.wieku.hexagons.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.audio.Mp3;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import me.wieku.hexagons.audio.analysis.BeatDetect;
import me.wieku.hexagons.audio.analysis.FFT;
import me.wieku.hexagons.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AudioPlayer {

	boolean ended;
	FileHandle handle;
	boolean firstStart = false;
	FFT fft;
	BeatDetect detect;
	float[] outBuf = new float[60];
	Music musicPlayer;
	static Field buffr;
	float secPB = 0;

	static {
		try {
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
		musicPlayer.setLooping(true);
		musicPlayer.setOnCompletionListener(m -> ended = true);

		fft = new FFT(1024, ((OpenALMusic)musicPlayer).getRate());
		fft.linAverages(60);

		detect = new BeatDetect(1024, ((OpenALMusic)musicPlayer).getRate());

		try {
			Field bsize = OpenALMusic.class.getDeclaredField("secondsPerBuffer");
			bsize.setAccessible(true);
			secPB = bsize.getFloat((OpenALMusic)musicPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		musicPlayer.setPosition(milis);
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

	/*byte[] bufferByte = new byte[4096*10];
	float[] bufferFloat = new float[2048];
	float[] bufferFloat2 = new float[1024];

	float delta0=0;
	int byteIndex = 0;*/
	public void update(float delta){


		if(timedelta < time){
			timedelta+=delta;

			setVolume(bgvol+((dstvol-bgvol)*timedelta)/time);

		} else {
			time = -2;
			timedelta = -1;
		}

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

	float bgvol, dstvol, timedelta = -1, time = -2;

	public void glideVolume(float volume, float time){
		bgvol = getVolume();
		dstvol = volume;
		this.time = time;
		timedelta=0;
	}

	public float[] analyze(){
		/*try {
			Utils.byteToFloat(bufferByte, byteIndex * 2048, bufferFloat, 0, bufferFloat.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(int i = 0, j = 0; i < bufferFloat2.length; i++, j+=2)
			bufferFloat2[i] = (bufferFloat[j] + bufferFloat[j+1]) / 2;

		fft.forward(bufferFloat2);

		for(int i = 0; i < fft.avgSize(); i++)
			outBuf[i] = fft.getAvg(i);
*/
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
}