package models;

/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 13-Jan-17
 */
public class PunkteRechner {
	private int[] ticks;
	private int[] gems;


    public PunkteRechner(int[] time, int[] gems, double ticklänge) {
		//umrechnung in Ticks
		ticks = new int[time.length];
		for (int i = 0; i < time.length; i++) {
			ticks[i] = (int) (time[i] / ticklänge);
		}

		this.gems = gems;
    }


    public int rechnePunkte(int vergangeneTicks, int gesammelteEdelsteine) {
        int punkte = Math.min(rechneZeitPunkte(vergangeneTicks), rechneGemPunkte(gesammelteEdelsteine));
        return punkte;
    }


    public int rechneZeitPunkte(int vergangeneTicks) {
        int punkte = 0;
        if (vergangeneTicks < ticks[4]) {
            punkte = 5;
        } else if (vergangeneTicks < ticks[3]) {
            punkte = 4;
        } else if (vergangeneTicks < ticks[2]) {
            punkte = 3;
        } else if (vergangeneTicks < ticks[1]) {
            punkte = 2;
        } else if (vergangeneTicks < ticks[0]) {
            punkte = 1;
        }
        return punkte;
    }


    public int rechneGemPunkte(int gesammelteEdelsteine) {
        int punkte = 0;
        if (gesammelteEdelsteine >= gems[4]) {
            punkte = 5;
        } else if (gesammelteEdelsteine >= gems[3]) {
            punkte = 4;
        } else if (gesammelteEdelsteine >= gems[2]) {
            punkte = 3;
        } else if (gesammelteEdelsteine >= gems[1]) {
            punkte = 2;
        } else if (gesammelteEdelsteine >= gems[0]) {
            punkte = 1;
        }
        return punkte;
    }


    public int gemsZumNächstenPunkt(int gesammelteEdelsteine){
        int nochEdelsteine = gems[0];
        if(gesammelteEdelsteine < gems[0]){
            nochEdelsteine = gems[0] - gesammelteEdelsteine;
        }
        else if (gesammelteEdelsteine < gems[1]){
            nochEdelsteine = gems[1] - gesammelteEdelsteine;
        }
        else if (gesammelteEdelsteine < gems[2]){
            nochEdelsteine = gems[2] - gesammelteEdelsteine;
        }
        else if (gesammelteEdelsteine < gems[3]){
            nochEdelsteine = gems[3] - gesammelteEdelsteine;
        }
        else if (gesammelteEdelsteine < gems[4]){
            nochEdelsteine = gems[4] - gesammelteEdelsteine;
        }
        return nochEdelsteine;
    }


	public int[] getTicks() {
		return ticks;
	}


	public int[] getGems() {
		return gems;
	}
}

