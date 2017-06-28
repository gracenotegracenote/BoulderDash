package models;

/**
 * Created by gracenote on 16-Dec-16.
 */
public enum Situation {
    ANY("any"),
    RARE("rare"),

    // player 1
    LEFT("left"),
    RIGHT("right"),
    UP("up"),
    DOWN("down"),
    METALEFT("metaleft"),
    METARIGHT("metaright"),
    METAUP("metaup"),
    METADOWN("metadown"),

    // player 2
    LEFT2("left2"),
    RIGHT2("right2"),
    UP2("up2"),
    DOWN2("down2"),
    METALEFT2("metaleft2"),
    METARIGHT2("metaright2"),
    METAUP2("metaup2"),
    METADOWN2("metadown2"),

    // player 3
    LEFT3("left3"),
    RIGHT3("right3"),
    UP3("up3"),
    DOWN3("down3"),
    METALEFT3("metaleft3"),
    METARIGHT3("metaright3"),
    METAUP3("metaup3"),
    METADOWN3("metadown3"),

    // player 4
    LEFT4("left4"),
    RIGHT4("right4"),
    UP4("up4"),
    DOWN4("down4"),
    METALEFT4("metaleft4"),
    METARIGHT4("metaright4"),
    METAUP4("metaup4"),
    METADOWN4("metadown4");

    private String situation;


    Situation(String situation) {
        this.situation = situation;
    }


    public static Situation fromString(String situation) {
        for (Situation s : Situation.values()) {
            if (s.getSituation().equals(situation)) return s;
        }

        return null;
    }


    public String getSituation() {
        return situation;
    }
}
