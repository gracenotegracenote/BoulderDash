package controller;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 07-Feb-17
 */
public class LevelPackReader {
	public static final String PATH_LEVEL_PACKS = "levels/";
	public static final String PATH_SINGLEPLAYER = "singleplayer";
	public static final String PATH_MULTIPLAYER = "multiplayer";
	public static final String PATH_MY_LEVELS = "myLevels";

	private List<String> packNames;


	public LevelPackReader(String path) {
		File levelDirectory = new File(path);
		String[] directories = levelDirectory.list((current, name) -> new File(current, name).isDirectory());
		packNames = Arrays.asList(directories);
	}


	public List<String> getPackNames() {
		return packNames;
	}
}
