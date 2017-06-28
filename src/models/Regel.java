package models;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gracenote on 16-Dec-16.
 */
public class Regel {
	public static final String SONDERZEICHEN = "*";
	private double probability;
    private Situation situation;
    private Direction direction;
    private List<OriginalBaustein> original;
    private List<ResultBaustein> result;

	private Feldinhalt[] gefundeneFeldinhalte;	// fuer die Regelnausfuerung


    public Regel(double probability, Situation situation, Direction direction, List<OriginalBaustein> original, List<ResultBaustein> result) {
		this.probability = probability;
        this.situation = situation;
        this.direction = direction;
        this.original = original;
        this.result = result;
    }


    /**
     * Methode zum Ausführen einer Regel auf der Karte.
     *
     * Die Direction wird von der Regel genommen. Abhängig davon werden die Start- und Endkoordinate gesetzt.
     * Die Richtung von der Regel, Start-und Endkoordinate werden der Methode regelAusführen übergeben
     */
    public void regelAusführen(Feld[][] karte) {
        Direction direction = this.getDirection();
        int breite = karte[0].length;
        int hoehe = karte.length;

		Koordinate startKoord = null;
		Koordinate endKoord = null;
        switch (direction) {
            case EAST:
            	startKoord = new Koordinate(0, 0);
				endKoord = new Koordinate(breite, hoehe);
                break;

            case WEST:
				startKoord = new Koordinate(breite - 1, 0);
				endKoord = new Koordinate(0, hoehe);
                break;

            case NORTH:
				startKoord = new Koordinate(0, hoehe - 1);
				endKoord = new Koordinate(breite, 0);
                break;

            case SOUTH:
				startKoord = new Koordinate(0, 0);
				endKoord = new Koordinate(breite, hoehe);
                break;

            case NORTHEAST:
				startKoord = new Koordinate(0, hoehe - 1);
				endKoord = new Koordinate(breite, 0);
                break;

            case NORTHWEST:
				startKoord = new Koordinate(breite - 1, hoehe - 1);
				endKoord = new Koordinate(0, 0);
                break;

            case SOUTHEAST:
				startKoord = new Koordinate(0, 0);
				endKoord = new Koordinate(breite, hoehe);
                break;

            case SOUTHWEST:
				startKoord = new Koordinate(breite - 1, 0);
				endKoord = new Koordinate(0, hoehe);
                break;
        }

		regelAusführen(startKoord, endKoord, direction, original, result, karte);
    }


    /**
     * Methode zum Ausführen einer Regel
     *
     * Es wird durch die Karte iteriert, beginnend bei startKoordinate bis Endkoordinate.
     * Es wird in die entsprechende Richtung nach den Werten aus Original gesucht.
     * Wenn der Startwert kleiner als der Endwert ist, so weiß man, dass man von links nach rechts gehen soll und die Schritt-
     * weite erhöht werden muss. Analog dazu: wenn der Startwert größer ist als der Endwert, so muss die Schritweite
     * erniedrigt werden
     *
     * * Man könnte jetzt eine Fallunterscheidung machen:
     * <pre> {@code
     *
     * if (startIKleinerEndI) {
     *  for (int i = 0; i < 9; i = i + 1) { ... }
     * } else {
     *    for (int i = 9; i ->= 0; i = i -1) { ... }
     * }
     * } </pre>
     *
     * Stattdessen wird diese Fallunterscheidung gleich in der Abbruchbedingung der for-Schleife
     * mittels ternärem Operator gemacht:
     * <pre>{@code
     * if (a){ dann b} else { dann c}
     * }</pre>
     * Kann ausgedrückt werden als a ? b : c
     *
     * Analag dazu gibt es eine Fallunterscheidung für das dritte Argument der for-Schleife (für den Counter), die
     * auch mittels ternärem Operator implementiert wurde.
     *
     * Man sucht mit Hilfe der Methode zuAktuallisierendenFeldern nach Feldern die noch nicht Aktualisiert wurden
     * und mit original übereinstimmen.
     * Wenn das Array der gefundene Feldern nicht leer ist, ersetzt man die gefundene Felder durch result mit Hilfe
     * der Methode aktualisiereFelder.
     *
     * @param startKoordinate Startkoordinate
     * @param endKoordinate   Endkoordinate
     * @param direction       Richtung
     * @param original        Liste an OriginalBaustein Regeln
     * @param result          Liste an ResultBaustein Regeln
     * @param karte           Die Karte von dem Level
     */

    private void regelAusführen(Koordinate startKoordinate, Koordinate endKoordinate, Direction direction,
                                List<OriginalBaustein> original, List<ResultBaustein> result, Feld[][]karte) {
        int startX = startKoordinate.getX();
        int startY = startKoordinate.getY();
        int endX = endKoordinate.getX();
        int endY = endKoordinate.getY();
        boolean startXKleinerEndX = startX < endX;
        boolean startYKleinerEndY = startY < endY;
        Set<Feld> alleAktualisierteFelder = new HashSet<>();

        for (int y = startY; startYKleinerEndY ? y < endY : y > endY; y = (startYKleinerEndY ? y + 1 : y - 1)) {
            for (int x = startX; startXKleinerEndX ? x < endX : x > endX; x = (startXKleinerEndX ? x + 1 : x - 1)) {
                if (!alleAktualisierteFelder.contains(karte[y][x])) {
                    Feld[] gefundeneFelder = this.zuAktualisierendeFelder(karte[y][x], original, direction,karte);
                    if (gefundeneFelder.length != 0) {
                        Set<Feld> aktualisierteFelder1 = this.aktualisiereFelder(gefundeneFelder, gefundeneFeldinhalte, result);
                        alleAktualisierteFelder.addAll(aktualisierteFelder1);
                    }
                }
            }
        }
    }


    /**
     * Methode zum Überprüfen, ob OriginalBaustein mit einem Feld übereinstimmt
     *
     * Es wird geprüft ob der OriginalBaustein ein leeres Set von Feldinhalten und ein Sonderzeichen(*) hat
     * oder ein EnumSet von Feldinhalten, in dem der Feldinhalt vom Feld enthalten ist.
     * Wenn eins der beiden zutrifft, wird weiter geprüft ob das Feld die passende Flags enthält.
     *
     * @param feld Das zu überprüfende Feld
     * @param originalBaustein OriginalBaustein Regeln
     * @return Falls eine OriginalBaustein mit dem Spielfeld übereinstimmt wird das Feld zurückgegeben.
     *         Sonst wird null zurückgegeben!
     */
    private Feld originalBausteinPasstAufFeld(Feld feld, OriginalBaustein originalBaustein) {
        Feldinhalt fi = feld.getFeldinhalt();
        EnumSet<Feldinhalt> originalNamen = originalBaustein.getNamen();
        Boolean originalHatNamen = !originalNamen.isEmpty();
        Boolean originalHatSternchen = !originalHatNamen && originalBaustein.getSonderzeichen().equals(SONDERZEICHEN);

        if (originalHatNamen && originalNamen.contains(fi) || originalHatSternchen) {
            EnumSet<Flag> feldFlags = feld.getFlags().clone();

            if (!feldFlags.containsAll(originalBaustein.getTrueFlags())) {
                return null;
            }

            feldFlags.retainAll(originalBaustein.getFalseFlags());

            if (!feldFlags.isEmpty()) {
                return null;
            }

            return feld;
        }

        return null;
    }


    /**
     * Methode, die überprüft ob nachfolgende Felder mit der Originalregel übereinstimmen.
     *
     * Man beginnt bei dem aktuellen Feld und geht in die angegebene Richtung so viele Felder weiter, wie es im original
     * vorhandene Elemente gibt. Bei jedem Feld wird geprüft ob das Feld mit dem pasenden Element von original übereinstimmt.
     * Die Überprüfing hört auf, sobald ein Feld nicht mit dem Element aus original übereinstimmt oder
     * wenn die Liste von Originalbausteinen  komplett bearbeitet wurde.
     * Die gefundenen Felder werden in einem Array gespeichert.
     * Die gefundenen Feldinhalte werden ebenso in einem Array gespeichert.
     *
     * @param feld Aktuelles Feld (Startfeld)
     * @param original Liste von Originalbausteinen
     * @param direction Die Richtung in der die Felder gesucht werden
     * @param karte Die Karte auf der die Felder gesucht werden
     * @return Falls alle Felder mit Original übereinstimmen wird ein Array von Feldern zurückgeliefert,
     *         die aktualisiert werden sollen. Sonst wird ein leeres Array zurückgeliefert.
     */
    private Feld[] zuAktualisierendeFelder(Feld feld, List<OriginalBaustein> original, Direction direction, Feld[][] karte) {
        Feld[] gefundeneFelder = new Feld[original.size()];
        gefundeneFeldinhalte = new Feldinhalt[original.size()];
        int aktuellesFeldX = feld.getX();
        int aktuellesFeldY = feld.getY();

        for (int i = 0; i < original.size(); ++i) {

			// prüft ob das Feld auf der Karte liegt
            Feld aktuellesFeld = feld.FeldIstAufDerKarte(aktuellesFeldX, aktuellesFeldY,karte);
            if (aktuellesFeld == null) {
                return new Feld[0];
            }

            // prüft ob das Feld mit dem Feld aus der Regel übereinstimmt
            Feld gefundenesFeld = this.originalBausteinPasstAufFeld(aktuellesFeld, original.get(i));
            if (gefundenesFeld == null) {
                return new Feld[0];
            }

            gefundeneFelder[i] = gefundenesFeld;
            gefundeneFeldinhalte[i] = gefundenesFeld.getFeldinhalt();
            aktuellesFeldX += direction.getRichtungX();
            aktuellesFeldY += direction.getRichtungY();
        }
        return gefundeneFelder;
    }


    /**
     * Methode, die die Felder aktualisiert
     *
     * Man geht durch das Array von gefundenen Feldern und ersetzt das i-te Element aus gefundeneFeldern durch das
     * i-te Element von result. Wenn an der i-ten Stelle in result eine Referenz vorkommt, ersetzt man den Feldinhalt
     * vom i-ten Feld in gefundenen Feldern durch den Feldinhalt von gefundeneFeldinhalten auf dessen Position die Referenz zeigt.
     * Die Flags, die in i-ten Element von result true sind, werden dem EnumSet<Flags> von dem i-ten Feld hinzugefügt.
     * Die Flags, die in i-ten Element von result false sind, werden von dem EnumSet<Flags> von dem i-ten Feld entfernt.
     *
     * @param gefundeneFelder ein Array von gefundenen Feldern
     * @param gefundeneFeldinhalte ein Array von gefundenen Feldinhalten
     * @param result eine Liste von Resultbausteinen, die auf den Feldern gesetzt werden sollen
     * @return liefert ein Set von aktualisierten Feldern zurück
     */
    private Set<Feld> aktualisiereFelder(Feld[] gefundeneFelder, Feldinhalt[] gefundeneFeldinhalte, List<ResultBaustein> result) {
        Set<Feld> aktualisierteFelder = new HashSet<>();
        for (int i = 0; i < result.size(); i++) {
            int reference = result.get(i).getReference();

            if (reference > -1) {
                gefundeneFelder[i].setFeldinhalt(gefundeneFeldinhalte[reference]);

            } else {
                gefundeneFelder[i].setFeldinhalt(result.get(i).getName());
            }

            gefundeneFelder[i].getFlags().addAll(result.get(i).getTrueFlags());
            gefundeneFelder[i].getFlags().removeAll(result.get(i).getFalseFlags());

            aktualisierteFelder.add(gefundeneFelder[i]);
        }

        return aktualisierteFelder;
    }


    public double getProbability() {
		return probability;
	}


	public void setProbability(double probability) {
		this.probability = probability;
	}


	public Situation getSituation() {
        return situation;
    }


    public void setSituation(Situation situation) {
        this.situation = situation;
    }


    public Direction getDirection() {
        return direction;
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    public List<OriginalBaustein> getOriginal() {
        return original;
    }


    public void setOriginal(List<OriginalBaustein> original) {
        this.original = original;
    }


    public List<ResultBaustein> getResult() {
        return result;
    }


    public void setResult(List<ResultBaustein> result) {
        this.result = result;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ " + probability + ", " + situation + ", " + direction + ", original = ");
        for (RegelBaustein regelBaustein : original) {
            builder.append(regelBaustein + ", ");
        }

        builder.append("result = ");
        for (RegelBaustein regelBaustein : result) {
            builder.append(regelBaustein + ", ");
        }
        builder.append("}");

        return builder.toString();
    }
}
