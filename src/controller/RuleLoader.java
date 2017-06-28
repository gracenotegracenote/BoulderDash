package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import parser.RuleJson;
import parser.Token;
import parser.TokenDeserializer;

/**
 * Created by gracenote on 05-Jan-17.
 */
public class RuleLoader {
	public static final String EXTENSION = ".json";
	public static final String PATH_PREPRERULES = "levels/PrePrerules" + EXTENSION;
	public static final String PATH_HAUPTREGELN = "levels/Hauptregeln" + EXTENSION;

    private RuleJson[] hauptregeln;

	private static RuleLoader instance;


	private RuleLoader() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Token.class, new TokenDeserializer());
        Gson gson = gsonBuilder.create();

        hauptregeln = loadRules(gson, PATH_HAUPTREGELN);
    }


    private RuleJson[] loadRules(Gson gson, String path) {
        try (InputStream is = new FileInputStream(path)) {
            return gson.fromJson(new InputStreamReader(is), new TypeToken<RuleJson[]>(){}.getType());
        } catch (IOException e) {
			System.out.println("Rules konnten nicht abgelesen werden!");
		}

        return null;
    }


	public RuleJson[] getHauptregeln() {
		return hauptregeln;
	}


	public static RuleLoader getInstance() {
		if (instance == null) {
			instance = new RuleLoader();
		}

		return instance;
	}
}
