package models;

/**
 * Created by gracenote on 16-Dec-16...
 */

public enum Feldinhalt {
    ME("me", "me.gif"),
    ME2("me2", "me2.gif"),
    ME3("me3", "me3.gif"),
    ME4("me4", "me4.gif"),
    MUD("mud", "mud.gif"),
    STONE("stone", "stone.gif"),
    GEM("gem", "gem.gif"),
    SIEVE("sieve", "sieve.gif"),
    EXIT("exit", "exit.gif"),
    WALL("wall", "wall.gif"),
    BRICKS("bricks", "bricks.gif"),
    PATH("path", "path.gif"),
    EXPLOSION("explosion", "explosion.gif"),
    SWAPLING("swapling", "swapling.gif"),
    BLOCKLING("blockling", "blockling.gif"),
    XLING("xling", "xling.gif"),
    GHOSTLING("ghostling", "ghostling.gif"),
    FIRE("fire", "fire.gif"),
    WATER("water", "water.gif"),
    ICE("ice", "ice.gif"),
    MACHINE("machine", "machine.gif"),
    NORTHTHING("norththing", "norththing.gif"),
    EASTTHING("eastthing", "eastthing.gif"),
    SOUTHTHING("souththing", "souththing.gif"),
    WESTTHING("westthing", "westthing.gif"),
    BALLOON("balloon", "balloon.gif"),
    SLIME("slime", "slime.gif"),
    EXTRATIME("extratime", "extratime.gif");

    private String name;
    private String imageUrl;


    Feldinhalt(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }


    public static Feldinhalt fromString(String name) {
        if (name != null) {
            for (Feldinhalt f : Feldinhalt.values()) {
                if (f.getName().equals(name)) return f;
            }
        }

        return Feldinhalt.PATH; // Beim unbekannten Feldinhalt PATH zurückgeben
    }


    public String getName() {
        return name;
    }


    public String getImageUrl() {
        return imageUrl;
    }
}
