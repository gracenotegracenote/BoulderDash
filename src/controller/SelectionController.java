package controller;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import models.PlayerModus;
import views.PlayerModusAuswahl;
import views.SpielerAuswahl;


/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 14-Feb-17
 */
public class SelectionController {
	public static final String GAME_TITLE = "Boulder Dash KI 5.0";
	public static final int STAGE_BREITE = 700;
	public static final int STAGE_HOEHE = 400;

	private Stage stage;
	private double screenWidth;
	private double screenHeight;
	private String spielerName;

	public SelectionController(Stage stage) {
		this.stage = stage;
		screenWidth = calculateScreenWidth();
		screenHeight = calculateScreenHeight();

		// SpielerAuswahl ist als erstes zu sehen
		zeigeSpielerAuswahl();

		zentriereStage(STAGE_BREITE, STAGE_HOEHE);	// TODO: warum zentriert sich die stage nicht richtig ohne die zeile?
		stage.show();
	}


	public void zeigeSpielerAuswahl() {
		SpielerAuswahl spielerAuswahl = new SpielerAuswahl(this);
		updateStage(new Scene(spielerAuswahl), GAME_TITLE);		// TODO: game title nicht immer setten (innerhalb der klasse)
	}


	/**
	 * Zeichnet PackAuswahl.
	 */
	public void zeigePackAuswahl(PlayerModus playerModus) {	// TODO: controller hier erzeugen
		ModusController modusController = null;

		switch (playerModus) {
			case SINGLEPLAYER:
				modusController = new SingleplayerController(this, spielerName);
				break;
			case MULTIPLAYER:
				modusController = new MultiplayerController(this, spielerName);
				break;
		}

		if (modusController == null) {
			System.out.println("Controller null");	// TODO: exception
		}

		modusController.zeigePackAuswahl();
	}


	public void zeigePlayerModusAuswahl() {
		PlayerModusAuswahl playerModusAuswahl = new PlayerModusAuswahl(this);
		updateStage(new Scene(playerModusAuswahl), GAME_TITLE);
	}


	public void zeigeEditorPack() {
		ModusController modusController = new SingleplayerController(this, spielerName);
		LevelLoader levelLoader = new LevelLoader(LevelPackReader.PATH_MY_LEVELS, null);
		modusController.zeigeLevelPack(levelLoader, true);
	}


	/**
	 * Anpassung der Stage an eine neue Scene.
	 * @param scene		die zu zeichnende Scene
	 * @param title		der neue Titel fuer die Stage
	 */
	public void updateStage(Scene scene, String title) {
		stage.setScene(scene);
		stage.setTitle(title);
		zentriereStage(scene.getWidth(), scene.getHeight());
	}


	/**
	 * Zentriert Stage.
	 * @param breite	Breite der Stage
	 * @param hoehe		Hoehe der Stage
	 */
	public void zentriereStage(double breite, double hoehe) {
		double mitteHorizontal = calculateScreenWidth() * 0.5;
		double mitteVertikal = calculateScreenHeight() * 0.5;
		stage.setX(mitteHorizontal - breite * 0.5);
		stage.setY(mitteVertikal - hoehe * 0.5);
	}


	/**
	 * @return	die Breite des Bildschirms
	 */
	public double calculateScreenWidth() {
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		double bildschirmbreite = primaryScreenBounds.getWidth();
		return bildschirmbreite;
	}


	/**
	 * @return	die Hoehe des Bildschirms
	 */
	public double calculateScreenHeight() {
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		double bildschirmhoehe = primaryScreenBounds.getHeight();
		return bildschirmhoehe;
	}


	public void setSpielerName(String spielerName) {
		this.spielerName = spielerName;
	}


	public Stage getStage() {
		return stage;
	}


	public double getScreenWidth() {
		return screenWidth;
	}


	public double getScreenHeight() {
		return screenHeight;
	}
}
