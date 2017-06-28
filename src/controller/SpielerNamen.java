package controller;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Ivana on 15.02.2017.
 */
public class SpielerNamen implements Serializable {
    /**
     * HashMap, die Spielername als Key und Bildpfad des Spielers als Value speichert.
     */
    private HashMap spielerBilderMap = new HashMap<String, String>();

    /**
     * Gettermethode für Spielernamen HashMap
     *
     * @return liefert die Spielernamen Hashmap zurück
     */
    public HashMap getSpielerBilderMap() {
        return spielerBilderMap;
    }
}
