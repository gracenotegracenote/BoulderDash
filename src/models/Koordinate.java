package models;

/**
 * Created by Ivana on 20.01.2017.
 */
public class Koordinate {
    private int x;
    private int y;


    /**
     * erstellt eine Koordinate mit (x,y) Wert
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     */
    public Koordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return this.x;
    }


    public int getY() {
        return this.y;
    }


    public void setX(int x) {
        this.x = x;
    }


    public void setY(int y) {
        this.y = y;
    }
}
