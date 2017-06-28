package views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import controller.SelectionController;
import controller.SpielerNamen;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


/**
 * Created by Ivana on 11.02.2017.
 */
public class SpielerAuswahl extends VBox {
    private static final String STYLESHEETS_CSS = "/stylesheets/SpielerAuswahl.css";
	private static final String PANE_SCC = "SpielerAuswahl";
	private static final String TEXT_CSS = "textField";
	private static final String KNOPF_CSS = "knopf";
	private static final String NEUER_SPIELER_BTN_CSS = "neuerSpielerButton";
	private static final String LABEL_CSS = "label";
	private static final String VBOX_CSS = "vBox";

	private static final String DEFAULT_PLAYER_IMG = "images/default.jpg";
	private static final String NAME_TEXT = "Name: ";
	private static final String SPIELER_TEXT = "Spieler: ";
	private static final String SPIELER_HINZUFUEGEN_BTN = "Spieler hinzufügen";
	private static final String BILD_AUSWAEHLEN_BTN = "Bild auswählen";
	private static final String DEFAULT_SPIELER_NAME = "Default";
	private static final String FILECHOOSER_TITLE = "Open Resource File";
	private static final String SPIELER_NAMEN_PATH = "spieler.namen";

	private static final String LESEN_IO_TEXT = "Datei mit Spielernamen ist noch nicht vorhanden!";
	private static final String SPEICHERN_IO_TEXT = "Spielernamen Datei konnte nicht gespeichert werden!";
	private static final String LESEN_NOT_FOUND_TEXT = "Klasse, die serialisiert werden soll, existiert nicht.";
	private static final String KEIN_SPIELERNAME = "Es muss ein Spielername angegeben werden!";
	private static final String LIMIT_ERREICHT = "Das maximale Limit an Spielern ist erreicht. Weitere Spieler können nicht angelegt werden!";
	private static final String GLEICHER_SPIELERNAME = "Der angegeben Spielername existiert schon! Bitte einen anderen Spielernamen wählen.";

	private static final int DEFAULT_SPIELER_BTN_SIZE = 90;
	private static final int SPEICHER_LIMIT = 7;

    private GridPane spielerBox;
    private String bildPfad;
    private SpielerNamen spielerNamen;

    /**
     * Klassenkonstruktor:
     * erstellt die Oberfläche des Fensters, erzeugt einen Defaultspieler und
     * zeigt alle gespeicherten Spieler an.
     *
     * @param selectionController
     */
    public SpielerAuswahl(SelectionController selectionController) {
        bildPfad = DEFAULT_PLAYER_IMG;
        spielerBox = new GridPane();
        spielerNamen = new SpielerNamen();

        //CSS
        getStylesheets().add(STYLESHEETS_CSS);
        getStyleClass().add(PANE_SCC);

        //Labels erstellen
        Label nameLabel = erstelleLabel(NAME_TEXT);
        Label spielerLabel = erstelleLabel(SPIELER_TEXT);

        //textfeld erstellen
        TextField textField = erstelleTextfeld("");

        //buttons erstellen
        EventHandler hKnopfEvent = new ButtonHandler(selectionController, textField);
        EventHandler fKopfEvent = new FotoAuswahlHandler(selectionController);
        Button hinzufuegeKnopf = erstelleButton(SPIELER_HINZUFUEGEN_BTN, KNOPF_CSS, hKnopfEvent);
        Button fotoAuswahl = erstelleButton(BILD_AUSWAEHLEN_BTN, KNOPF_CSS, fKopfEvent);

        //VBoxes erstellen
        VBox vbButtons = erstelleVBox();
        vbButtons.getChildren().addAll(nameLabel, textField, fotoAuswahl, hinzufuegeKnopf);
        VBox vbÜbersicht = erstelleVBox();
        vbÜbersicht.getChildren().addAll(spielerLabel, spielerBox);

        //BorderPane erstellen
        BorderPane border = erstelleBorderPane(vbÜbersicht, vbButtons);
        getChildren().add(border);

        //default Spieler wird erzeugt
        erzeugeSpielerButton(DEFAULT_SPIELER_NAME, bildPfad, selectionController);

        // vorhandene Spieler werden geladen und angezeigt
        ladeSpielernamen();
        vorhandeneSpielerAnzeigen(selectionController);
    }

    /**
     * Methode, die Informationen(Spielername und Bildpfad) aus einer Datei liest.
     */
	private void ladeSpielernamen() {
		FileInputStream fileIn = null;
		ObjectInputStream in = null;

		try {
            fileIn = new FileInputStream(SPIELER_NAMEN_PATH);
            in = new ObjectInputStream(fileIn);
			this.spielerNamen = (SpielerNamen) in.readObject();
        } catch (IOException i) {
            System.out.println(LESEN_IO_TEXT);
        } catch (ClassNotFoundException c) {
            System.out.println(LESEN_NOT_FOUND_TEXT);
        } finally {
			closeInputStreams(fileIn, in);
		}
    }

    private void closeInputStreams(FileInputStream fileIn, ObjectInputStream in) {
		try {
			if (fileIn != null) {
				fileIn.close();
			}

			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			System.out.println("InputStreams schliessen sich nicht.");
		}
	}

    /**
     * Methode die Informationen(Spielernamen und Bildpfad) aller Spieler in einer Datei speichert.
     */
    private void speichereSpielernamen() {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;

		try {
            fileOut = new FileOutputStream(SPIELER_NAMEN_PATH);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(this.spielerNamen);
        } catch (IOException i) {
            System.out.println(SPEICHERN_IO_TEXT);
        } finally {
			closeOutputStreams(fileOut, out);
		}
    }


	private void closeOutputStreams(FileOutputStream fileOut, ObjectOutputStream out) {
		try {
			if (fileOut != null) {
				fileOut.close();
			}

			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			System.out.println("OutputStreams schliessen sich nicht.");
		}
	}

    /**
     * Methode, die alle Vorhandenen Spieler anzeigt.
     *
     * @param selectionController
     */
    private void vorhandeneSpielerAnzeigen(SelectionController selectionController) {
        this.spielerNamen.getSpielerBilderMap().forEach((spielerName, spielerBild) ->    // TODO: consumer ?
                erzeugeSpielerButton(spielerName.toString(), spielerBild.toString(), selectionController));
        selectionController.getStage().sizeToScene();
        selectionController.getStage().setResizable(false);
    }


    /**
     * Methode, die einen neuen Spielerknopf erzeugt.
     *
     * @param spielerName         Name des Spielers
     * @param spielerBild         Bild des Spielers(Defaultbild, wenn kein anderes ausgewählt)
     * @param selectionController
     */
    private void erzeugeSpielerButton(String spielerName, String spielerBild, SelectionController selectionController) {
        if (spielerBild == null) {
            spielerBild = DEFAULT_PLAYER_IMG;
        }

        Image bild = new Image("file:" + spielerBild);

        //Image View für das Bild auf dem SpielerButton
        ImageView buttonBild = new ImageView(bild);
        buttonBild.setPreserveRatio(true);
        buttonBild.setFitHeight(DEFAULT_SPIELER_BTN_SIZE);
        buttonBild.setFitWidth(DEFAULT_SPIELER_BTN_SIZE);

        //Spieler Button wird erzeugt
        EventHandler spielerEvent = new NeuerSpielerHandler(selectionController, spielerName);
        Button neuerSpielerButton = erstelleButton(spielerName, NEUER_SPIELER_BTN_CSS, spielerEvent);
        neuerSpielerButton.setGraphic(buttonBild);
        neuerSpielerButton.setContentDisplay(ContentDisplay.TOP);

        int x = spielerBox.getChildren().size() % 3;
        int y = spielerBox.getChildren().size() / 3;

        spielerBox.add(neuerSpielerButton, x, y);
    }

    /**
     * Methode, die einen Knopf mit dem passenden Stylesheet und dem passenden EventHandler erstellt.
     *
     * @param text         Text, der auf dem Knopf stehen soll.
     * @param css          passender Stylesheet-Name
     * @param eventHandler EventHandler für den Knopf
     * @return liefert einen Knopf zurück.
     */
    private Button erstelleButton(String text, String css, EventHandler eventHandler) {
        Button button = new Button(text);
        button.getStyleClass().add(css);
        button.setOnAction(eventHandler);
        return button;
    }

    /**
     * Methode, die ein Textfeld mit dem passenden Stylesheet erstellt
     *
     * @param text Text, der auf dem Textfeld stehen soll.
     * @return Liefert ein Textfeld zurück
     */
    private TextField erstelleTextfeld(String text) {
        TextField txt = new TextField(text);
        txt.getStyleClass().add(TEXT_CSS);
        return txt;
    }

    /**
     * Methode, die ein Label mit dem passenden Stylesheet erstellt.
     *
     * @param text Der Text, der in dem Label steht.
     * @return liefert ein Label zurück
     */
    private Label erstelleLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add(LABEL_CSS);
        return label;
    }

    /**
     * Methode die eine BorderPane erstellt.
     *
     * @param vbÜbersicht VBox, die an die BorderPane links angehängt wird.
     * @param vbButtons   VBox, die an die BorderPane rechts angehängt wird.
     * @return liefert eine BorderPane zurück.
     */
    private BorderPane erstelleBorderPane(VBox vbÜbersicht, VBox vbButtons) {
        BorderPane border = new BorderPane();
        border.setLeft(vbÜbersicht);
        border.setRight(vbButtons);
        return border;
    }

    /**
     * Methode, die eine VBox erstellt.
     * Es wird eine neue VBox mit dem passenden Stylesheet erstellt.
     *
     * @return liefert eine VBox zurück.
     */
    private VBox erstelleVBox() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add(VBOX_CSS);
        return vBox;

    }


	/**
	 * Methode, die eine Warnnachricht anzeigt
	 *
	 * @param nachricht Text, der die Warnung beschreibt
	 */
	private void zeigeWarnung(String nachricht) {
		new Alert(Alert.AlertType.WARNING, nachricht).showAndWait();
	}


    /**
     * EventHandler der einen Dateiauswahlsdialog anzeigt.
     * <p>
     * Es dürfen nur Dateien, die die Endungen .png,.jpg,.gif enthalten, ausgewählt werden.
     */
    private class FotoAuswahlHandler implements EventHandler<ActionEvent> {
        SelectionController selectionController;

        public FotoAuswahlHandler(SelectionController selectionController) {
            this.selectionController = selectionController;
        }

        @Override
        public void handle(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Bild Dateien ", "*.png", "*.jpg", "*.gif");

            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle(FILECHOOSER_TITLE);
            File ausgewählteDatei = fileChooser.showOpenDialog(selectionController.getStage());

            if (ausgewählteDatei != null) {
                SpielerAuswahl.this.bildPfad = ausgewählteDatei.getAbsolutePath();
            }
        }
    }

    /**
     * EventHandler, der neue Spieler anlegt.
     * <p>
     * Zuerst wird überprüft, ob ein neuer Spieler angelegt werden kann.
     * Damit ein neuer Spieler angelegt werden kann, müssen folgenede Bedingungen erfüllt sein:
     * 1.Spielername ist nicht leer
     * 2.Spielername ist noch nicht vergeben
     * 3.Die Spielerliste enthält weniger als 10 Einträge(inklusive Defaultspieler)
     * <p>
     * Fenstergröße wird automatisch an die Spielerliste angepasst.
     */
    private class ButtonHandler implements EventHandler<ActionEvent> {
        private SelectionController selectionController;
        private TextField textField;

        public ButtonHandler(SelectionController selectionController, TextField textField) {
            this.selectionController = selectionController;
            this.textField = textField;
        }

        @Override
        public void handle(ActionEvent event) {
            boolean spielernameAngegeben = !textField.getText().isEmpty();
            boolean spielerKannHinzugefügtWerden = spielerNamen.getSpielerBilderMap().size() <= SPEICHER_LIMIT;
            boolean spielerExistiertSchon = spielerNamen.getSpielerBilderMap().containsKey(textField.getText());

            if (!spielernameAngegeben) {
                zeigeWarnung(KEIN_SPIELERNAME);
                return;
            }

            if (!spielerKannHinzugefügtWerden) {
                zeigeWarnung(LIMIT_ERREICHT);
                return;
            }

            if (spielerExistiertSchon) {
                zeigeWarnung(GLEICHER_SPIELERNAME);
                return;
            }

            erzeugeSpielerButton(textField.getText(), bildPfad, selectionController);
            spielerNamen.getSpielerBilderMap().put(textField.getText(), bildPfad);
            speichereSpielernamen();
            textField.clear();
            selectionController.getStage().sizeToScene();
        }
    }


    /**
     * Implementiert EventHandler für neuen Spieler.
     * Spielername wird gesetzt und es wird eine Verbindug zwischen Spieler und PlayerModusAuswahl erstellt.
     */
    private class NeuerSpielerHandler implements EventHandler<ActionEvent> {
		private SelectionController selectionController;
		private String spielerName;

		public NeuerSpielerHandler(SelectionController selectionController, String spielerName) {
			this.selectionController = selectionController;
			this.spielerName = spielerName;
		}

		@Override
		public void handle(ActionEvent event) {
			selectionController.setSpielerName(spielerName);
			selectionController.zeigePlayerModusAuswahl();
		}
	}
}

