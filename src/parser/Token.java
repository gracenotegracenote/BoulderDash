package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Feld;
import models.Feldinhalt;
import models.Flag;
import models.OriginalBaustein;
import models.RegelBaustein;
import models.ResultBaustein;

/**
 * Created by gracenote on 13-Dec-16.
 */
public class Token {
    public static final int NO_REFERENCE = -1;
    public static final String STERN = "*";

    private int reference = NO_REFERENCE;
    private String token; //Muss token bleiben, damit level editor fnktioniert
    private String[] names;
    private Map<String, Boolean> flags;


    public Feld toFeld() {
        Feldinhalt inhalt = Feldinhalt.fromString(token);
        EnumSet<Flag> feldFlags = convertFeldFlags(flags);
        return new Feld(inhalt, feldFlags);
    }

    public RegelBaustein toOriginal() {
        String sonderzeichen = null;    // einander ausschließende Parameter
        Feldinhalt feldinhalt = null;

        if (token != null) {
            if (token.equals(STERN)) {
                sonderzeichen = token;
            } else {
                feldinhalt = Feldinhalt.fromString(token);
            }
        }

        List<Feldinhalt> inhalteList = new ArrayList<>();   // vereinigt "token" und "names" in sich
        if (feldinhalt != null) inhalteList.add(feldinhalt);
        if (names != null) {
            for (String str : names) {
                inhalteList.add(Feldinhalt.fromString(str));
            }
        }

		// convert from list to enumset
        EnumSet<Feldinhalt> inhalte;
		if (inhalteList.size() != 0) {
			inhalte = EnumSet.copyOf(inhalteList);
		} else {
			inhalte = EnumSet.noneOf(Feldinhalt.class);
		}

        EnumMap<Flag, Boolean> regelBausteinFlags = convertRegelBausteinFlags(flags);

        return new OriginalBaustein(sonderzeichen, inhalte, regelBausteinFlags);
    }


    public RegelBaustein toResult() {
        Feldinhalt inhalt = Feldinhalt.fromString(token);
        EnumMap<Flag, Boolean> regelBausteinFlags = convertRegelBausteinFlags(flags);

        return new ResultBaustein(reference, inhalt, regelBausteinFlags);
    }


    private static EnumSet<Flag> convertFeldFlags(Map<String, Boolean> tokenFlags) {
        if (tokenFlags == null) return EnumSet.noneOf(Flag.class);  // empty EnumSet

        Set<Flag> flags = new HashSet<>();
        for (String key : tokenFlags.keySet()) {
            if (tokenFlags.get(key)) {          // nur Flags = true werden beachtet
                flags.add(Flag.fromString(key));
            }
        }

        return EnumSet.copyOf(flags);   // convert from HashSet to EnumSet
    }


    private static EnumMap<Flag, Boolean> convertRegelBausteinFlags(Map<String, Boolean> tokenFlags) {
        if (tokenFlags == null) return new EnumMap<>(Flag.class);

        EnumMap<Flag, Boolean> flags = new EnumMap<>(Flag.class);
        for (String key : tokenFlags.keySet()) {
            flags.put(Flag.fromString(key), tokenFlags.get(key));   // TODO: wenn NO_FLAG, dann Fehler mitteilen
        }

        return flags;
    }


    public int getReference() {
        return reference;
    }


    public String getToken() {
        return token;
    }


    public String[] getNames() {
        return names;
    }


    public Map<String, Boolean> getFlags() {
        return flags;
    }


    public void setReference(int reference) {
        this.reference = reference;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public void setNames(String[] names) {
        this.names = names;
    }


    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }


    @Override
    public String toString() {
        return "Token{" +
                "reference=" + reference +
                ", token='" + token + '\'' +
                ", names=" + Arrays.toString(names) +
                ", \nflags=" + flags +
                '}';
    }
}
