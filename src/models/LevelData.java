package models;

import java.io.Serializable;

/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 02-Feb-17
 */
public class LevelData implements Serializable {
    private int needpoints;
    private int rekordPunkte;
    private int rekordEdelsteine;
    private String designPath;

    private transient Level level;


    public LevelData(int needpoints, Level level, String designPath) {
        this.needpoints = needpoints;
        this.level = level;
        this.designPath = designPath;
    }


    public int getNeedpoints() {
        return needpoints;
    }


    public int getRekordPunkte() {
        return rekordPunkte;
    }


    public void setRekordPunkte(int rekordPunkte) {
        this.rekordPunkte = rekordPunkte;
    }


    public int getRekordEdelsteine() {
        return rekordEdelsteine;
    }


    public void setRekordEdelsteine(int rekordEdelsteine) {
        this.rekordEdelsteine = rekordEdelsteine;
    }


    public Level getLevel() {
        return level;
    }


    public void setLevel(Level level) {
        this.level = level;
    }


    public String getDesignPath() {
        return designPath;
    }
}
