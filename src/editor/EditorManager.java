package editor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controller.LevelLoader;
import parser.Index;
import parser.LevelDataJson;
import parser.LevelJson;
import parser.RuleJson;

/**
 * Created by Ella on 05/02/2017.
 * Klasse die die leeren Level, Leveldaten enthält die dann von der View gefüllt werden können
 * Enthält Methoden um Index und Leveldaten zu speichern
 */
public class EditorManager {
    private LevelLoader levelLoader;
    private LevelJson level;
    private LevelDataJson levelData;

    List<RuleJson> gemerkteRegeln;

    private String packageName = "myLevels";
    public final String LEVEL_PATH = "levels/myLevels/levels/";
    public final String DATEIENDUNG = ".json";
    public final String INDEX_PATH = "levels/myLevels/index.json";

    public EditorManager() {
        level = new LevelJson();
        levelData = new LevelDataJson();
        levelLoader = new LevelLoader(packageName, null);
        gemerkteRegeln = new ArrayList<>();
    }

    /**
     * Speichert LevelData und level.
     * Zusammen in einer Methode, damit nicht ein index ohne passendes level geschrieben wird oder umgekehrt
     * @param level
     * @param levelData
     */
    public void saveLevel(LevelJson level, LevelDataJson levelData) {
        String filename = levelData.getPath();
        levelToJson(level, filename);
        indexToJson(levelData);
    }

    /**
     * Übersetzt ein JsonLevel in eine Json Datei, die unter dem angegebenen Pfad gespeichert wird
     * Dies funktioniert so leicht da die JsonLevel Struktur mit dem wunschergebnis übereinstimmt und Gson es komplett parsen kann
     * Leider entspricht unsere Token Klasse nicht mehr 100% einem Json-Token in einer Rule (da manchmal String, manchmal String[])
     * Deshalb können Rules nicht geparst werden --> Möglich wäre von Hand JsonObjeke zu erschaffen
     * @param level
     * @param filename
     */
    private void levelToJson(LevelJson level, String filename) {
        Gson gson = new Gson();
        String json = gson.toJson(level);
		FileWriter writer = null;

        try {
			writer = new FileWriter(LEVEL_PATH + filename + DATEIENDUNG);
			writer.write(json);
        } catch (IOException e) {
			System.out.println("Daten konnten nicht in json geschrieben werden.");
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				System.out.println("Writer im Editormanager schliesst sich nicht.");
			}
		}
	}

    /**
     * Verwandelt das LevelData Object in einen Index. Damit der bisherige nicht verloren geht, holen wir uns diesen zuerst,
     * damit er ergänzt werden kann. Mit levelData.toJson(Gson) bekommen wir nicht das richtige Ergebnis,
     * deshalb übersetzen wir die LevelData von Hand in einzelne Json-Objekte und setzen den Index zusammen
     * @param levelData
     */

    private void indexToJson(LevelDataJson levelData){
        Index index = levelLoader.getIndex();
        LevelDataJson[] oldLevelData = index.getLevelDataJsons();
        LevelDataJson[] newLevelData = new LevelDataJson[index.getLevelDataJsons().length+1];
        for(int i=0; i <= oldLevelData.length - 1; i++){
            newLevelData[i] = oldLevelData[i];
        }
        newLevelData[newLevelData.length - 1] = levelData;
        index.setLevelDataJsons(newLevelData);

		FileWriter writer = null;
		JsonWriter jsonWriter = null;

        try {
            writer = new FileWriter(INDEX_PATH);
            jsonWriter = new JsonWriter(writer);

            jsonWriter.beginObject();   // index
            jsonWriter.name("name").value("My levels");

            jsonWriter.name("levels");
            jsonWriter.beginArray();    // array aus levelDatas

            for (LevelDataJson data : newLevelData) {
                jsonWriter.beginObject();   // levelData
                jsonWriter.name("path").value(data.getPath());
                jsonWriter.name("design").value(data.getDesign());
                jsonWriter.name("needpoints").value(data.getNeedpoints());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			try {
				if (writer != null) {
					writer.close();
				}

				if (jsonWriter != null) {
					jsonWriter.close();
				}
			} catch (IOException e) {
				System.out.println("Writers im EditorManager schliessen sich nicht!");
			}
		}
    }

    public LevelJson getLevel() {
        return level;
    }

    public LevelDataJson getLevelData() {
        return levelData;
    }

    public List<RuleJson> getGemerkteRegeln() {
        return gemerkteRegeln;
    }
}

