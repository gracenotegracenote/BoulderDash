package models;

/**
 * Created by gracenote on 16-Dec-16.
 */
public enum Direction {
    EAST("east",1,0),
    WEST("west",-1,0),
    NORTH("north",0,-1),
    SOUTH("south",0,1),
    NORTHWEST("northwest",-1,-1),
    NORTHEAST("northeast",1,-1),
    SOUTHWEST("southwest",-1,1),
    SOUTHEAST("southeast",1,1);

    private String direction;
    private int richtungX;
    private int richtungY;


    Direction(String direction, int richtungX, int richtungY) {
        this.direction = direction;
        this.richtungX = richtungX;
        this.richtungY = richtungY;
    }


    public static Direction fromString(String direction) {
        for (Direction d : Direction.values()) {
            if (d.getDirection().equals(direction)) return d;
        }

        return null;
    }


    public String getDirection() {
        return direction;
    }


    public int getRichtungX(){ return richtungX;}


	public int getRichtungY(){return richtungY;}
}
