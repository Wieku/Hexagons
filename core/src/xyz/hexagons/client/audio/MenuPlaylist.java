package xyz.hexagons.client.audio;


import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.eventbus.Subscribe;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.map.MapJson;
import xyz.hexagons.client.menu.settings.event.SettingsChanged;
import xyz.hexagons.client.resources.ArchiveFileHandle;


public class MenuPlaylist {

	static ArrayList<Map> playlist = new ArrayList<>();

	static int currentId = -1;
	static Map current;
	static float volume = 0f;
	static AudioPlayer player;
	static boolean looping = false;
	private static float speed = 1f;

	public static void previousSong(){
		loadAndPlay(currentId>0?currentId-=1:0);
	}

	public static void start(){
		if(Instance.maps.size()>0){
			nextSong();
			skipToPreview();
			player.setVolume(0f);
			player.glideVolume(volume = ((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.menuMusicVolume) / 10000f, 5f);
		}
	}

	public static void nextSong(){

		if(playlist.size()-1 == currentId){
			ArrayList<Map> maps = Instance.maps;
			Map map;
			do {
				map = maps.get(MathUtils.random(maps.size()-1));
			} while(isInPrevious((maps.size()<10?maps.size():10), currentId, map) != -1);
			playlist.add(map);
		}
		loadAndPlay(++currentId);
	}

	public static void setVolume(float vol){

		volume = vol;

		if(player != null){
			player.setVolume(volume);
		}

	}


	public static void setPosition(float secs){
		if(player != null){
			player.setPosition(secs);
		}
	}

	public static void skipToPreview(){
		if(current != null){
			if(current.info.previewTime > 0)
				setPosition(current.info.previewTime);
		}
	}


	private static int isInPrevious(int previous, int id, Map map){

		previous = (id-previous < 0 ? id : previous);
		MapJson mapInfo = map.info;

		for(int i = id;i > id-previous;i--){
			MapJson data = playlist.get(i).info;
			if(data.songAuthor.equals(mapInfo) && data.songName.equals(mapInfo.songName)){
				return i;
			}
		}

		return -1;
	}

	private static int isInNext(int next, int id, Map map){

		next = (id+next > playlist.size()-1? playlist.size()-id : next);

		MapJson mapData = map.info;

		for(int i = id;i<id+next;i++){
			MapJson data = playlist.get(i).info;
			if(data.songAuthor.equals(mapData.songAuthor) && data.songName.equals(mapData.songName)){
				return i;
			}
		}

		return -1;
	}

	public static void replaceCurrent(Map map){
		
		if(current.info.audioFileName.equals(map.info.audioFileName)) return;

		int prev = isInPrevious(5, currentId, map);
		int nxt = isInNext(5, currentId, map);

		int id = currentId;

		if(prev != -1 || nxt != -1){
			id = (prev != -1?prev:(nxt != -1?nxt:currentId));
			return;
		} else {
			playlist.set(id=currentId, map);
		}

		loadAndPlay(id);
	}

	public static AudioPlayer getCurrentPlayer(){
		return player;
	}

	public static void update(float delta){
		if(player != null){
			player.update(delta);
			if(player.hasEnded()){
				nextSong();
			}

		}
	}

	public static void play(){
		if(player != null){
			player.setVolume(volume);
			player.setLooping(looping);
			//player.setSpeed(1.5f);
			player.play();
		}
	}

	public static void pause(){
		if(player != null){
			player.pause();
		}
	}

	public static void stop(){
		if(player != null){
			player.pause();
			setPosition(0);
		}
	}

	public static void stopAndDispose(){
		if(player != null){
			player.stop();
			player.dispose();
		}
	}

	private static void load(Map map){
		stopAndDispose();
		current = map;
		//System.out.println(map.getMetaData().getAudioFileName());
		player = Instance.audioPlayerFactory.instance(map.file.getFileHandle(map.info.audioFileName));
	}

	public static float getPosition(){
		return (player != null ? player.getPosition() : 0);
	}

	/*public static int getLength(){
		return (player != null ? player.get : 0);
	}*/

	public static int getCurrentId(){
		return currentId;
	}

	public static void loadAndPlay(int id){
		stopAndDispose();
		currentId = id;
		load(playlist.get(id));
		play();
	}

	public static void setLooping(boolean state) {
		if(player != null)
			player.setLooping(looping = state);
	}

	public static void replacePreview(Map map){
		replaceCurrent(map);
		skipToPreview();
	}

	public static Map getCurrent() {
		return current;
	}

	public static boolean isPaused() {
		return player == null || player.isPaused();
	}

	@Subscribe
	public void onVolumeChange(SettingsChanged e) {
		if(e.getElement().getId().contains("Volume")) {
			MenuPlaylist.setVolume((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.menuMusicVolume / 10000f);
		}
	}

	static {
		Instance.eventBus.register(new MenuPlaylist());
	}
}
