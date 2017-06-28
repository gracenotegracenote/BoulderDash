package models;

/**
 * Created by gracenote on 16-Dec-16.
 */
public enum Flag {
    NO_FLAG(""),
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    LEFT("left"),
    RIGHT("right"),
    UP("up"),
    DOWN("down"),
    MOVED("moved"),
    FALLING("falling"),
    RICH("rich"),
    LOOSE("loose"),
    SLIPPERY("slippery"),
    PUSHABLE("pushable"),
    BAM("bam"),
    BAMRICH("bamrich"),
    RUTSCHTR("rutschtr"),
    RUTSCHTL("rutschtl"),
    RUTSCHT("rutscht"),
    EXIT("exit"),
    LOCKEDV("lockedv"),
    GAMEOVER("gameover"),
    TIMERICH("timerich");

    private String flag;


    Flag(String flag) {
        this.flag = flag;
    }


    public static Flag fromString(String flag) {
        for (Flag f : Flag.values()) {
            if (f.getFlag().equals(flag)) return f;
        }

        return NO_FLAG; // wenn unbekannter Flag, dann NO_FLAG zurueckgeben
    }


    public String getFlag() {
        return flag;
    }
}
