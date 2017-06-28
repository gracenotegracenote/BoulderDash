package models;

import java.util.*;

/**
 * Created by gracenote on 16-Dec-16.
 */
public class Level {
    private String name;
    private PunkteRechner punkteRechner;
    private List<Regel> regeln;
    private Feld[][] karte;
	private double ticklänge;
	private boolean extrazeit;

	private int breite;
    private int hoehe;
    private boolean gewonnen;
    private boolean verloren;
    private int punkte;
    private int gesammelteEdelsteine;
    private int vergangeneTicks;
    private Koordinate MEposition;
    private EnumSet<Flag> soundFlags; //für sound


	public Level(String name, PunkteRechner punkteRechner, List<Regel> regeln, Feld[][] karte, double ticklänge) {
		this.name = name;
		this.punkteRechner = punkteRechner;
		this.regeln = regeln;
		this.karte = karte;
		this.ticklänge = ticklänge;

		breite = karte[0].length;
		hoehe = karte.length;
        soundFlags = EnumSet.noneOf(Flag.class);

		MEpositionInitialisieren();
	}


	/**
	 * Singleplayer
	 * @param situation
	 * @param ticks
	 */
    public void macheTick(Situation situation, int ticks) {
		vergangeneTicks = ticks;
		soundFlags.clear();
		flagsZuruecksetzen();

		regelnAusfuehren(situation);

		updateGameStatus();
	}


	/**
	 * Multiplayer
	 * @param situations
	 * @param ticks
	 */
    public void macheTick(Set<Situation> situations, int ticks) {
        if (situations.isEmpty()) {	// TODO: check
            Situation situation = null;
            macheTick(situation, ticks);	// macheTick() wird mind. einmal aufgerufen
            return;
        }

        for (Situation situation : situations) {
            macheTick(situation, ticks);
        }
    }


	/**
	 * Prueft auf gewonnen/verloren/gem gesammelt
	 */
	private void updateGameStatus() {
		gewonnen = false;
		verloren = false;
		for (int i = 0; i < hoehe; i++) {
			for (int j = 0; j < breite; j++) {
				EnumSet<Flag> flags = karte[i][j].getFlags();
                // verloren, wenn ME weg oder zeit abgelaufen
                if(flags.contains(Flag.GAMEOVER) || vergangeneTicks > punkteRechner.getTicks()[0]){
                    verloren = true;
                    soundFlags.add(Flag.GAMEOVER);
                }
				// Anzahl Edelsteine updaten
				if (flags.contains(Flag.RICH)) {
					gesammelteEdelsteine++;
                    soundFlags.add(Flag.RICH);
				}

				// extrazeit
				if (flags.contains(Flag.TIMERICH)) {
                    extrazeit = true;
                    soundFlags.add(Flag.TIMERICH);
                }

				// Gewonnen: Me bei exit
				if (flags.contains(Flag.EXIT) && gesammelteEdelsteine >= punkteRechner.getGems()[0]) {
					gewonnen = true;
					punkte = punkteRechner.rechnePunkte(vergangeneTicks, gesammelteEdelsteine);
				}

				if (karte[i][j].getFeldinhalt() == Feldinhalt.ME) {
					MEposition.setX(j);
					MEposition.setY(i);
				}
			}
		}
	}
/**
 * Flags werden zurückgesetzt
 */
	private void flagsZuruecksetzen() {
		for (int i = 0; i < hoehe; i++) {
			for (int j = 0; j < breite; j++) {
				karte[i][j].flagsZurücksetzen();
			}
		}
	}

    /**
     * Methode die die Regeln nacheinander ausführt
     * Es wird geprüft ob die Regel Probability hat und ob sie zutrift, dann wird die Situation geprüft
     * @param situation
     */
    private void regelnAusfuehren(Situation situation) {
        for (Regel regel : regeln) {
            if (regel.getProbability() == 0 || probabilityCheck(regel.getProbability())) {    // probability wird geprueft
                Situation regelSituation = regel.getSituation();

                //Situation wird geprüft
                if (regelSituation == situation || regelSituation == Situation.ANY) {
                    regel.regelAusführen(this.karte);
                } else if (regelSituation == Situation.RARE && vergangeneTicks % 3 == 0) {
                    regel.regelAusführen(this.karte);
                }
            }
        }
    }


	public boolean probabilityCheck(double probability) {
        Random r = new Random();
        double zufallszahl = r.nextDouble();
        return (zufallszahl <= probability ? true : false);
    }


	public boolean isGewonnen() {
		return gewonnen;
	}


	public boolean isVerloren() {
		return verloren;
	}


	public void setVerloren(boolean verloren) {
		this.verloren = verloren;
	}


	public int getBreite() {
        return breite;
    }


    public int getHoehe() {
        return hoehe;
    }


    public String getName() {
        return name;
    }


    public Feld[][] getKarte() {
        return karte;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getGesammelteEdelsteine() {
        return gesammelteEdelsteine;
    }


    public PunkteRechner getPunkteRechner() {
        return punkteRechner;
    }


    public int getPunkte() {
        return punkte;
    }


    public Koordinate getMEposition() {
        return MEposition;
    }


    public double getTicklänge() {
        return ticklänge;
    }


    //Für zentrierung in der View
    private void MEpositionInitialisieren(){
        for (int i = 0; i < karte.length; i++) {
            for (int j = 0; j < karte[0].length; j++) {
                if(karte[i][j].getFeldinhalt() == Feldinhalt.ME){
                    MEposition = new Koordinate(j,i);
                }
            }
        }
    }

    public String zeichneKarte() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < hoehe; i++) {
            for (int j = 0; j < breite; j++) {
                if (karte[i][j] == null) {
                    builder.append("null, ");
                } else {
                    builder.append(karte[i][j].getFeldinhalt() + ", ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }


    public boolean isExtrazeit() {
        return extrazeit;
    }


    public void setExtrazeit(boolean extrazeit) {
        this.extrazeit = extrazeit;
    }


    public EnumSet<Flag> getSoundFlags(){
        return soundFlags;
    }
}
