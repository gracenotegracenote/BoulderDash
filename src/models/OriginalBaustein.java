package models;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Created by Ivana on 11.01.2017.
 */
public class OriginalBaustein extends RegelBaustein {
    private String sonderzeichen;        // in unserem Fall konmmt nur * vor
    private EnumSet<Feldinhalt> namen;       // Felder "name" und "namen" wurden in "namen" vereinigt


    /**
     * Konstruktor der ein Originalbaustein erstellt
     *
     * @param sonderzeichen kann nur Stern(*) vorkommen, trifft auf alles zu
     * @param namen EnumSet von Feldinhalten, die gesucht werden
     * @param flags EnumMap vom Flags als Keys und Werte als Values, die verlangt werden
     */
    public OriginalBaustein(String sonderzeichen, EnumSet<Feldinhalt> namen, EnumMap<Flag, Boolean> flags) {
        super(flags);
        this.sonderzeichen = sonderzeichen;
        this.namen = namen;
    }


    public String getSonderzeichen() {
        return sonderzeichen;
    }

    public EnumSet<Feldinhalt> getNamen() {
        return namen;
    }

}