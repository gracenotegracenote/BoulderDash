package controller;

import java.util.List;
import java.util.Set;

import editor.EditorView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.util.Duration;
import models.Feld;
import models.Feldinhalt;
import models.Level;
import models.LevelData;
import models.PlayerModus;
import models.Situation;
import views.ErgebnisFenster;
import views.LevelAnsicht;
import views.LevelPack;
import views.PackAuswahl;

import static controller.SelectionController.GAME_TITLE;

/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 11-Feb-17
 */
public abstract class ModusController {
	public static final String LEVEL_AUSWAHL_TITLE = "Wähle ein Level aus";
	public static final String LEVEL_EDITOR_TITLE = "Gestalte deine eigenen Levels! :) ";

	private LevelLoader levelLoader;
	private int ticks;
	private Timeline timeline;
	private int jetzigesLevelIndex;
	private String spielerName;
	private Set<Situation> situations;
	private PlayerModus playerModus;
	private SelectionController selectionController;


	public ModusController(SelectionController selectionController, String spielerName, PlayerModus playerModus) {
		this.selectionController = selectionController;
		this.spielerName = spielerName;
		this.playerModus = playerModus;
	}


	/**
	 * Zeichnet LevelAnsicht.
	 * @param levelIndex	Index des jeweiligen Levels im Pack
	 */
	public void zeigeLevelAnsicht(int levelIndex) {
		if (levelIndex < 0 || levelIndex >= levelLoader.getLevelDatas().size()) {
			System.out.println("Falscher Levelindex!");	// TODO: Fehlermeldung fuer Nutzer
			return;
		}

		jetzigesLevelIndex = levelIndex;

		// Timeline stoppen, wenn es schon tickt
		stopTimeline();

		LevelData levelData = levelLoader.loadLevelJson(levelIndex);

		LevelAnsicht levelAnsicht = new LevelAnsicht(this, levelData, levelIndex);
		Level level = levelData.getLevel();

		Scene levelScene = new Scene(levelAnsicht, levelAnsicht.getAnsichtBreite(), levelAnsicht.getAnsichtHoehe());
		addEventHandler(levelScene);
		selectionController.updateStage(levelScene, level.getName());

		startTimeline(level, levelAnsicht);
	}


	/**
	 * Zeichnet das Menue mit LevelPacks.
	 * @param levelLoader	LevelLoader fuer das jeweilige Pack
	 * @param editor	Flag, das besagt, ob LevelEditor im Pack zugaenglich ist (durch den Plus-Button)
	 */
	public void zeigeLevelPack(LevelLoader levelLoader, boolean editor) {
		this.levelLoader = levelLoader;

		stopTimeline();
		ticks = 0;

		LevelPack levelPack = new LevelPack(this, levelLoader.getLevelDatas(), editor);
		selectionController.updateStage(new Scene(levelPack), LEVEL_AUSWAHL_TITLE);
	}


	/**
	 * Zeichnet EditorView.
	 */
	public void zeigeLevelEditor() {
		EditorView editorAnsicht = new EditorView(selectionController.getScreenWidth(), selectionController.getScreenHeight(), this);
		selectionController.updateStage(new Scene(editorAnsicht), LEVEL_EDITOR_TITLE);
	}


	/**
	 * Beauftragt den SelectionController, die PackAuswahl zu zeichnen.
	 */
	public void zeigePackAuswahl() {
		PackAuswahl packAuswahl = new PackAuswahl(this, playerModus);
		selectionController.updateStage(new Scene(packAuswahl), GAME_TITLE);
	}


	/**
	 * Beauftragt den SelectionController, die PlayerModusAuswahl zu zeichnen.
	 */
	public void zeigePlayerModusAuswahl() {
		selectionController.zeigePlayerModusAuswahl();
	}


	/**
	 * Startet Timeline.
	 * @param level			Level, das zur Zeit gespielt wird
	 * @param levelAnsicht	LevelAnsicht des jeweiligen Levels
	 */
	public void startTimeline(Level level, LevelAnsicht levelAnsicht) {
		double ticklänge = level.getTicklänge();

		timeline = new Timeline(new KeyFrame(Duration.seconds(ticklänge), ae -> {
			ticks++;

			if (level.isExtrazeit()) {
				ticks = (int) (ticks - 10 / ticklänge);
				level.setExtrazeit(false);
			}

			onTick(level);
			levelAnsicht.update();
			pruefeSpielZustand(level, levelAnsicht);	// TODO: interface OnGameOver
		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}


	/**
	 * Stellt Timeline auf Pause.
	 */
	public void pauseTimeline() {
		if (timeline != null) {
			timeline.pause();
		}
	}


	/**
	 * Stoppt Timeline.
	 */
	public void stopTimeline() {
		if (timeline != null) {
			timeline.stop();
			ticks = 0;
		}
	}


	/**
	 * Abstrakte Methode, die bei jedem Tick aufgerufen wird, um Aenderungen im Model vorzunehmen.
	 * @param level		Level, das zur Zeit gespielt wird
	 */
	public abstract void onTick(Level level);


	private void pruefeSpielZustand(Level level, LevelAnsicht levelAnsicht) {
		ErgebnisFenster ergebnisFenster = null;

		if (level.isVerloren()) {
			if (playerModus == PlayerModus.SINGLEPLAYER) {
				ergebnisFenster = new ErgebnisFenster(this, level, jetzigesLevelIndex, ErgebnisFenster.GAME_OVER_TEXT);
			} else {
				// bei multiplayer wird geprueft, ob andere spieler noch da sind
				Feld[][] map = level.getKarte();
				for (int i = 0; i < level.getHoehe(); i++) {
					for (int j = 0; j < level.getBreite(); j++) {
						Feldinhalt feldinhalt = map[i][j].getFeldinhalt();
						if (feldinhalt == Feldinhalt.ME || feldinhalt == Feldinhalt.ME2 || feldinhalt == Feldinhalt.ME3 || feldinhalt == Feldinhalt.ME4) {
							level.setVerloren(false);
						}
					}
				}

				if (level.isVerloren()) ergebnisFenster = new ErgebnisFenster(this, level, jetzigesLevelIndex, ErgebnisFenster.GAME_OVER_TEXT);
			}
		} else if (level.isGewonnen()) {
			LevelData levelData = levelLoader.getLevelDatas().get(jetzigesLevelIndex);

			// update RekordPunkte
			if (level.getPunkte() > levelData.getRekordPunkte()) {
				levelData.setRekordPunkte(level.getPunkte());
			}

			// update RekordEdelsteine
			if (level.getGesammelteEdelsteine() > levelData.getRekordEdelsteine()) {
				levelData.setRekordEdelsteine(level.getGesammelteEdelsteine());
			}

			levelLoader.speichereSpielstand();
			ergebnisFenster = new ErgebnisFenster(this, level, jetzigesLevelIndex, ErgebnisFenster.WIN_TEXT);
		}

		// Timeline stoppen und Ergebnisfenster zeigen
		if (ergebnisFenster != null) {
			stopTimeline();
			levelAnsicht.stopSounds();
			ergebnisFenster.show();
		}
	}


	/**
	 * Methode zum Überprüfen, ob ein Level freigeschalten werden kann
	 *
	 * Diese Methode vergleicht die vorhandenen Punkte mit der Zahl an notwendigen Punkte für ein Level. Wenn der
	 * Spieler gleich viele oder mehr Punkte gesammelt hat, als für das Level notwendig sind, wird das Level
	 * freigegeben.
	 * @param levelIndex Index des Levels, das überprüft werden soll
	 * @return True, falls die notwendige Anzahl an Punkten vorliegt, False andernfalls
	 */
	public boolean levelKannFreigeschaltenWerden(int levelIndex) {
		int erreichtePunkte = 0;

		List<LevelData> levelDatas = levelLoader.getLevelDatas();
		for (LevelData levelData : levelDatas) {
			erreichtePunkte += levelData.getRekordPunkte();
		}

		if (levelIndex < levelDatas.size()) {
			int benötigtePunkte = levelDatas.get(levelIndex).getNeedpoints();
			return erreichtePunkte >= benötigtePunkte;
		}

		return false;
	}


	public abstract void addEventHandler(Scene levelScene);


	public LevelLoader getLevelLoader() {
		return levelLoader;
	}


	public int getTicks() {
		return ticks;
	}


	public String getSpielerName() {
		return spielerName;
	}


	public Set<Situation> getSituations() {
		return situations;
	}


	public void setSituations(Set<Situation> situations) {
		this.situations = situations;
	}


	public double getScreenWidth() {
		return selectionController.getScreenWidth();
	}


	public double getScreenHeight() {
		return selectionController.getScreenHeight();
	}
}
