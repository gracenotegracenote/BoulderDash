package models;

import java.util.EnumMap;

/**
 * Created by Ivana on 11.01.2017.
 */
public class ResultBaustein extends RegelBaustein {
    private int reference;
    private Feldinhalt name;


    /**
     * Konstruktor erstellt einen Resultbaustein
     *
     * @param reference Integerwert, der auf die Position eines Elementes von Originalregel referenziert
     * @param name Feldinhalt, der auf das Feld gesetzt werden soll
     * @param flags Flags, die auf das Feld gesetzt bzw. vom Feld entfernt werden sollen
     */
    public ResultBaustein(int reference, Feldinhalt name, EnumMap<Flag, Boolean> flags){
        super(flags);
        this.reference = reference;
        this.name = name;
    }


    public int getReference() {
        return reference;
    }


    public void setReference(int reference) {
        this.reference = reference;
    }


    public Feldinhalt getName() {
        return name;
    }


    public void setName(Feldinhalt name) {
        this.name = name;
    }
}
