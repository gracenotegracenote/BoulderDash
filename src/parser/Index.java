package parser;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gracenote on 14-Dec-16.
 */
public class Index {
    @SerializedName("name")
    private String name;
    @SerializedName("levels")
    private LevelDataJson[] levelDataJsons;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public LevelDataJson[] getLevelDataJsons() {
        return levelDataJsons;
    }


    public void setLevelDataJsons(LevelDataJson[] levelDataJsons) {
        this.levelDataJsons = levelDataJsons;
    }
}
