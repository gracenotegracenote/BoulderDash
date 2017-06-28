package models;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gracenote on 11-Jan-17.
 */
public abstract class RegelBaustein {
    private Map<Flag, Boolean> flags;	//TODO: Parser soll statt EnumMap 2 EnumSets liefern


    public RegelBaustein(EnumMap<Flag, Boolean> flags) {
        this.flags = flags;
    }


    /**
     * Methode, die Flags nach dem übergebenen Wert filtert
     *
     * @param valueWert Wert(true oder false)
     * @return Liefert ein EnumSet mit den gefilterten Flags zurück
     */
    public EnumSet<Flag> filtereFlagsNachValue(boolean valueWert) {
        return !this.flags.isEmpty() ?
                this.flags
                        .entrySet()
                        .stream()
                        .filter(b -> b.getValue().equals(valueWert))
                        .map(b -> b.getKey())
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(Flag.class)))
                : EnumSet.noneOf(Flag.class);
    }


    public EnumSet<Flag> getTrueFlags() {
        return this.filtereFlagsNachValue(true);
    }


    public EnumSet<Flag> getFalseFlags() {
        return this.filtereFlagsNachValue(false);
    }


    public Map<Flag, Boolean> getFlags() {
        return flags;
    }


    public void setFlags(Map<Flag, Boolean> flags) {
        this.flags = flags;
    }
}
