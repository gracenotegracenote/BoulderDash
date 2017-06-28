package models;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import parser.Token;

/**
 * Created by gracenote on 16-Dec-16.
 */
public class Feld {
    private Feldinhalt feldinhalt;
    private EnumSet<Flag> flags;

    private int x;
    private int y;

    public Feld(Feldinhalt name, EnumSet<Flag> flags) {
        this.feldinhalt = name;
        this.flags = flags;
    }

    public void flagsZurücksetzen() {
        if (flags == null) {
            flags = EnumSet.noneOf(Flag.class);
        } else {
            EnumSet<Flag> zuLoeschendeFlags = EnumSet.of(Flag.RUTSCHT, Flag.RUTSCHTL, Flag.RUTSCHTR, Flag.MOVED,
                    Flag.FALLING, Flag.RICH, Flag.LOOSE, Flag.SLIPPERY, Flag.PUSHABLE, Flag.BAM, Flag.BAMRICH,
                    Flag.EXIT, Flag.GAMEOVER, Flag.TIMERICH);
            flags.removeAll(zuLoeschendeFlags);
        }

        switch (feldinhalt) {
            case STONE:
                flags.add(Flag.SLIPPERY);
                flags.add(Flag.PUSHABLE);
                flags.add(Flag.LOOSE);
                break;
            case GEM:
                flags.add(Flag.SLIPPERY);
                flags.add(Flag.LOOSE);
                break;
            case BRICKS:
                flags.add(Flag.SLIPPERY);
                break;
        }
    }


    /**
     * Methode zum sicheren Holen eines Feldes auf der Karte
     * <p>
     * Durch Boundary-Checks wird verhindert, dass auf ein Feld zugegriffen wird, welches nicht auf der Karte liegt.
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return Sofern die Koordinaten auf der Karte liegen, wird das entsprechende Feld zurückgegeben. Andernfalls wird
     * null zurückgegeben!
     */
    public Feld FeldIstAufDerKarte(int x, int y, Feld[][] karte) {
        boolean feldAufKarte = (x >= 0 && x < karte[0].length && y >= 0 && y < karte.length);
        return feldAufKarte ? karte[y][x] : null;
    }


    public Token toToken() {
        Token token = new Token();
        token.setToken(this.getFeldinhalt().getName());
        if (!this.getFlags().isEmpty()) {
            Map<String, Boolean> flags = new HashMap<>();
            for (Flag flag : this.getFlags()) {
                flags.put(flag.getFlag(), true);
            }
            token.setFlags(flags);
        }
        return token;
    }


    public Feldinhalt getFeldinhalt() {
        return feldinhalt;
    }


    public void setFeldinhalt(Feldinhalt feldinhalt) {
        this.feldinhalt = feldinhalt;
    }


    public EnumSet<Flag> getFlags() {
        return flags;
    }


    public void setFlags(EnumSet<Flag> flags) {
        this.flags = flags;
    }


    public void setX(int x) {
        this.x = x;
    }


    public void setY(int y) {
        this.y = y;
    }


    public int getX() {
        return this.x;
    }


    public int getY() {
        return this.y;
    }
}


