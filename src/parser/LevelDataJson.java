package parser;

import com.google.gson.annotations.SerializedName;

import models.LevelData;

/**
 * Created by gracenote on 13-Dec-16.
 */
public class LevelDataJson {
    @SerializedName("path")
    private String path;
    @SerializedName("design")
    private String design;
    @SerializedName("needpoints")
    private int needpoints;

    private LevelJson levelJson;


	public LevelData toLevelData() {
		return new LevelData(needpoints, levelJson.toLevel(), design);
	}


    public LevelJson getLevelJson() {
        return levelJson;
    }


    public void setLevel(LevelJson levelJson) {
        this.levelJson = levelJson;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getDesign() {
        return design;
    }


    public void setDesign(String design) {
        this.design = design;
    }


    public int getNeedpoints() {
        return needpoints;
    }


    public void setNeedpoints(int needpoints) {
        this.needpoints = needpoints;
    }


    @Override
    public String toString() {
        return "LevelData{" +
                "path='" + path + '\'' +
                ", design='" + design + '\'' +
                ", needpoints=" + needpoints +
                ", \nlevelJson=" + levelJson +
                '}';
    }
}
