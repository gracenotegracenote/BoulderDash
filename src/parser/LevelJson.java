package parser;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.RuleLoader;
import models.Feld;
import models.Level;
import models.PunkteRechner;
import models.Regel;

/**
 * Created by gracenote on 13-Dec-16.
 */
public class LevelJson {
    @SerializedName("name")
    private String name;
    @SerializedName("prerules")
    private RuleJson[] prerules;
    @SerializedName("postrules")
    private RuleJson[] postrules;
    @SerializedName("map")
    private Token[][] map;
    @SerializedName("gems")
    private int[] gems;
    @SerializedName("time")
    private int[] time;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("ticksPerSecond")
    private int ticksPerSecond;


	public Level toLevel() {
        double ticklänge;

        if (this == null) return new Level("", null, null, null,0 );	// TODO: catch exception in model

        if(ticksPerSecond==0.0) {
            ticklänge=0.2;
        } else {
            ticklänge = (double) 1 / ticksPerSecond;
        }

        PunkteRechner punkterechner = new PunkteRechner(time, gems, ticklänge);
		List<Regel> rules = initRules();	// alle Regeln werden vereinigt

		return new Level(name, punkterechner, rules, convertMap(map), ticklänge);
	}


	private List<Regel> initRules() {
		List<Regel> rules = new ArrayList<>();

		List<Regel> prerules = convertRules(this.prerules);
		List<Regel> hauptrules = convertRules(RuleLoader.getInstance().getHauptregeln());
		List<Regel> postrules = convertRules(this.postrules);

		if (prerules != null) {
			rules.addAll(prerules);
		}
		if (hauptrules != null) {
			rules.addAll(hauptrules);
		}
		if (postrules != null) {
			rules.addAll(postrules);
		}

		return rules;
	}


	private static List<Regel> convertRules(RuleJson[] rulesJson) {
		if (rulesJson == null) return null;

		List<Regel> rules = new ArrayList<>();
		for (RuleJson ruleJson : rulesJson) {
			rules.add(ruleJson.toRegel());
		}

		return rules;
	}


	public static Feld[][] convertMap(Token[][] map) {
		if (map == null) return null;

		int mapWidth = map[0].length;
		int mapHeight = map.length;

		Feld[][] felder = new Feld[mapHeight][mapWidth];

		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				Feld neuesFeld = map[i][j].toFeld();
				neuesFeld.setX(j);
				neuesFeld.setY(i);
				felder[i][j] = neuesFeld;
			}
		}

		return felder;
	}


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public RuleJson[] getPrerules() {
        return prerules;
    }


    public void setPrerules(RuleJson[] prerules) {
        this.prerules = prerules;
    }


    public RuleJson[] getPostrules() {
        return postrules;
    }


    public void setPostrules(RuleJson[] postrules) {
        this.postrules = postrules;
    }


    public Token[][] getMap() {
        return map;
    }


    public void setMap(Token[][] map) {
        this.map = map;
    }


    public int[] getGems() {
        return gems;
    }


    public int[] getTime(){
	    return time;
    }


    public void setGems(int[] gems){
        this.gems = gems;
    }


    public void setTime(int[] time){
        this.time = time;
    }


    public int getWidth() { return this.width; }


    public void setWidth(int width) { this.width = width; }


    public int getHeight() { return this.height; }


    public void setHeight(int height) { this.height = height; }


    public int getTicksPerSecond() {
        return ticksPerSecond;
    }


    public void setTicksPerSecond(int ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
    }


    @Override
    public String toString() {
        return "LevelJson{" +
                "name='" + name + '\'' +
                ", \nprerules=" + Arrays.toString(prerules) +
                ", \npostrules=" + Arrays.toString(postrules) +
                ", \nmap=" + Arrays.toString(map) +
                ", gems=" + gems +
                ", time=" + time+
                ", width=" + width +
                ", height=" + height +
                ", ticksPerSecond=" + ticksPerSecond +
                '}';
    }
}
