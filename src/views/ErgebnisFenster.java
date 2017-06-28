package views;

import controller.ModusController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Level;

/**
 * Created by atanasova on 22.01.2017.
 */
public class ErgebnisFenster extends Stage {
    public static final int SPACING = 10;
    public static final String GAME_OVER_TEXT = "Game Over";
    public static final String WIN_TEXT = "Gewonnen!!";
    public static final String ERREICHTE_PUNKTE_TEXT = "In diesem Level erreichte Punkte: ";
    public static final String MAX_PUNKTE_TEXT = "Maximal erreichte Punkte: ";
    public static final String GESAMMELTE_EDELSTEINE_TEXT = "Gesammelte Edelsteine: ";
    public static final String MAX_EDELSTEINE_TEXT = "Maximal gesammelte Edelsteine: ";
    public static final String NEU_SPIELEN_BTN_TEXT = "Neu spielen";
    public static final String ZUR_AUSWAHL_BTN_TEXT = "Zurück zur Auswahl";
    public static final String NEXT_LEVEL_BTN_TEXT = "Nächstes Level";
    public static final String EXIT_BTN_TEXT = "Exit";
    public static final String BUTTON_CSS = "ergFensterButton";
    public static final String LABEL_CSS = "ergFensterLabel";
	public static final String PANE_CSS = "ergebnisFenster";
	public static final String STYLESHEETS_CSS = "/stylesheets/ErgFenster.css";

	private ModusController modusController;
    private int levelIndex;


    /**
     *
     * @param modusController um Panes auf der Hauptstage auswechseln zu können
     * @param gespieltesLevel das gespielte Level, um Daten aus ihm zu holen
     * @param levelIndex index von dem Level, um ein neues Level zu erstellen
     * @param labelText
     */

    public ErgebnisFenster(ModusController modusController, Level gespieltesLevel, int levelIndex, String labelText) {
        this.modusController = modusController;
        this.levelIndex = levelIndex;

		// labels
        int maxPunkte = modusController.getLevelLoader().getLevelDatas().get(levelIndex).getRekordPunkte();
        int maxEdelsteine = modusController.getLevelLoader().getLevelDatas().get(levelIndex).getRekordEdelsteine();
		Label punkteLabel = erstelleLabel(ERREICHTE_PUNKTE_TEXT + gespieltesLevel.getPunkte());
        Label edelsteineLabel = erstelleLabel(GESAMMELTE_EDELSTEINE_TEXT + gespieltesLevel.getGesammelteEdelsteine());
		Label maxPunkteLabel = erstelleLabel(MAX_PUNKTE_TEXT + maxPunkte);
		Label maxEdelsteineLabel = erstelleLabel(MAX_EDELSTEINE_TEXT + maxEdelsteine);

		HBox buttonBox = initButtonBox();

		// main pain
		VBox vbox = new VBox(punkteLabel, maxPunkteLabel, edelsteineLabel, maxEdelsteineLabel, buttonBox);
		vbox.getStylesheets().add(STYLESHEETS_CSS);
		vbox.getStyleClass().add(PANE_CSS);

		// stage
		setOnCloseRequest(new CloseHandler());  // fordert WindowEvent
		setTitle(labelText);
		initModality(Modality.APPLICATION_MODAL);   // ErgebnisFenster-Stage blockiert andere Stages
		setScene(new Scene(vbox));
	}


	private HBox initButtonBox() {
		// buttons
		Button neuSpielenButton = erstelleButton(NEU_SPIELEN_BTN_TEXT, new NeuesSpielHandler());
		Button zurAuswahlButton = erstelleButton(ZUR_AUSWAHL_BTN_TEXT, new ZurAuswahlHandler());
		Button nextLevelButton = erstelleButton(NEXT_LEVEL_BTN_TEXT, new NächstesLevelHandler());
		Button exitButton = erstelleButton(EXIT_BTN_TEXT, new ExitHandler());

		// button box
		HBox buttonBox = new HBox(neuSpielenButton, zurAuswahlButton, nextLevelButton, exitButton);
		buttonBox.setSpacing(SPACING);

		return buttonBox;
	}


	private Label erstelleLabel(String name){
        Label label = new Label(name);
        label.getStyleClass().add(LABEL_CSS);
        return label;
    }

    private Button erstelleButton(String text, EventHandler eventHandler){
        Button name = new Button(text);
        name.setWrapText(true);
        name.setOnAction(eventHandler);
        name.getStyleClass().add(BUTTON_CSS);

        return name;
    }

    private class NeuesSpielHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            close();
            modusController.zeigeLevelAnsicht(levelIndex);
        }
    }

    private class ZurAuswahlHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            close();
            //List<LevelData> levelDatas = modusController.getLevelPack().getLevelDatas();
            modusController.zeigeLevelPack(modusController.getLevelLoader(), false);
        }
    }

    private class NächstesLevelHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (modusController.levelKannFreigeschaltenWerden(levelIndex + 1)) {
                close();
                modusController.zeigeLevelAnsicht(levelIndex + 1);
            } else {
                zeigeWarnung("Es sind nicht genügend Punkte vorhanden - Level kann nicht freigeschalten werden!");
            }
        }
    }

    private void zeigeWarnung(String nachricht) {
        Alert alert = new Alert(Alert.AlertType.WARNING, nachricht);
        alert.showAndWait();
    }

    private class ExitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            System.exit(0);
        }
    }

    private class CloseHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            System.exit(0);
        }
    }

}
