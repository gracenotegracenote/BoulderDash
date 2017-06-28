package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import models.Level;
import models.LevelData;
import parser.Index;
import parser.LevelDataJson;
import parser.LevelJson;
import parser.Token;
import parser.TokenDeserializer;

/**
 * Created by gracenote on 14-Dec-16.
 */
public class LevelLoader {
	public static final String EXTENSION = ".json";
	private static final String PATH_FORMAT_INDEX = "levels/%s/index" + EXTENSION;
	private static final String PATH_FORMAT_SAVE = "levels/%s/%s.ser";
	private static final String PATH_FORMAT_LEVEL = "levels/%s/levels/%s" + EXTENSION;
	public static final String DEFAULT_SPIELERNAME = "default";

	private String packName;
	private LevelJson[] levelBackup;

	private List<LevelData> levelDatas;

	private String savePath;
	private final Index index;


	public LevelLoader(String packName, String spielerName) {
		System.out.println("level loader");

		this.packName = packName;

		// Gson mit dem Deserializer verbinden!
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Token.class, new TokenDeserializer());
		Gson gson = gsonBuilder.create();
		index = loadIndex(gson);

		List<LevelData> saveLevelDatas = null;

		System.out.println(spielerName);

		if (spielerName != null) {
			savePath = String.format(PATH_FORMAT_SAVE, packName, spielerName);
		} else {
			savePath = String.format(PATH_FORMAT_SAVE, packName, DEFAULT_SPIELERNAME);
		}
		saveLevelDatas = ladeGespeicherteLevels(spielerName);

		// LevelDaten werden geladen
		LevelDataJson[] dataJsons = index.getLevelDataJsons();
		levelBackup = new LevelJson[dataJsons.length];
		levelDatas = new ArrayList<>(dataJsons.length);
		for (int i = 0; i < dataJsons.length; i++) {
			String fileName = index.getLevelDataJsons()[i].getPath();
			String filePath = String.format(PATH_FORMAT_LEVEL, packName, fileName);
			dataJsons[i].setLevel(loadLevelJson(gson, filePath));

			// backup fuellen
			levelBackup[i] = dataJsons[i].getLevelJson();
			levelDatas.add(dataJsons[i].toLevelData());
		}

		if (saveLevelDatas != null) {
			for (int i = 0; i < dataJsons.length; i++) {
				LevelData data = saveLevelDatas.get(i);
				data.setLevel(levelDatas.get(i).getLevel());
				levelDatas.set(i, data);
			}
		}
	}


	public List<LevelData> getLevelDatas() {
		return levelDatas;
	}


	public LevelData loadLevelJson(int index) {
		LevelData levelData = levelDatas.get(index);
		Level level = levelBackup[index].toLevel();

		levelData.setLevel(level);
		return levelData;
	}


	/**
	 * Methode zum Speichern des Spielstands
	 * <p>
	 * Diese Methode speichert den Spielstand für den aktuellen Spieler. Dazu wird die komplette Datenstruktur
	 * levelDatas serialisiert und in eine Datei gespeichert.
	 */
	public void speichereSpielstand() {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;

		try {
			fileOut = new FileOutputStream(savePath);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(levelDatas);
		} catch (IOException i) {
			System.out.println("Spielstand konnte nicht gespeichert werden!");
		} finally {
			try {
				if (fileOut != null) {
					fileOut.close();
					if (out != null) {
						out.close();
					}
				}
			} catch (IOException e) {
				System.out.println("OutputStreams schliessen sich nicht.");
			}
		}
	}


	/**
	 * Methode zum Laden des Spielstands
	 * <p>
	 * Diese Methode lädt den Spielstand eines Spielers aus der dazugehörigen Spielstandsdatei. Diese Datei wird
	 * deserialisiert und levelDatas zugewiesen.
	 *
	 * @return levelDatas Datenstruktur mit allen Spielständen eines Spielers
	 */
	private List<LevelData> ladeGespeicherteLevels(String spielerName) {
		List<LevelData> levelDatas = null;

		FileInputStream fileIn = null;
		ObjectInputStream in = null;

		try {
			fileIn = new FileInputStream(savePath);
			in = new ObjectInputStream(fileIn);
			levelDatas = (List<LevelData>) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Nichts abgespeichert, da kein Progress vorhanden.");
		} finally {
			if (fileIn != null) {
				try {
					fileIn.close();
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					System.out.println("InputStreams schliessen sich nicht.");
				}
			}
		}

		return levelDatas;
	}


	private Index loadIndex(Gson gson) {
		Index index = new Index();
		String pathIndex = String.format(PATH_FORMAT_INDEX, packName);
		try {
			// read index
			InputStream is = new FileInputStream(pathIndex);
			index = gson.fromJson(new InputStreamReader(is), new TypeToken<Index>(){}.getType());
		} catch (IOException e) {
			System.out.println("Index kann nicht abgelesen werden!");
		}
		return index;
	}


	private LevelJson loadLevelJson(Gson gson, String path) {
		try (InputStream is = new FileInputStream(path)) {
			return gson.fromJson(new InputStreamReader(is), new TypeToken<LevelJson>(){}.getType());
		} catch (IOException e) {
			System.out.println("LevelJson konnte nicht abgelesen werden!");
		}

		return new LevelJson();
	}


	public Index getIndex() {
		return index;
	}
}
