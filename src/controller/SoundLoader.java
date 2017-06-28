package controller;

import java.io.File;
import java.util.EnumMap;

import javafx.scene.media.Media;
import models.Flag;

/**
 * Created on 15/02/2017.
 * Klasse zum Laden des Sounds: parallel zu ImageLoader aufgebaut. Der path der übergeben wird ist das im Index definierte design
 * So kann für ein Design ein spezifischer Song ausgewählt werden
 * Bei erschaffen eines Levels wird der SoundLoader aufgerufen, der Backgroundsong geladen sowie die Geräusche in eine Map gelade
 */
public class SoundLoader {
    private EnumMap<Flag, Media> sounds;
    Media backgroundSong;
    public final String GAMEOVER_FILE = "soundeffects/gameOver.wav";
    public final String RICH_FILE = "soundeffects/rich.wav";
    public final String BACKGROUND_SOUND = "backgroundsong.wav";


	public SoundLoader(String songPath) {
		backgroundSong = new Media(new File(songPath + BACKGROUND_SOUND).toURI().toString());
		sounds = new EnumMap<>(Flag.class);
		sounds.put(Flag.GAMEOVER, new Media(new File(GAMEOVER_FILE).toURI().toString()));
		sounds.put(Flag.RICH, new Media(new File(RICH_FILE).toURI().toString()));
		sounds.put(Flag.TIMERICH, new Media(new File(RICH_FILE).toURI().toString()));
	}


    public Media getSound(Flag flag) {
        return sounds.get(flag);
    }

    public Media getBackgroundSong() {
        return backgroundSong;
    }
}
