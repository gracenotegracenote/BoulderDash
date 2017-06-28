package editor;

import controller.ImageLoader;
import controller.LevelLoader;
import controller.LevelPackReader;
import controller.ModusController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import models.*;
import parser.LevelDataJson;
import parser.LevelJson;
import parser.Token;

import java.util.*;

/**
 * Created by Admin on 13/02/2017.
 */
public class BasicEditor extends BorderPane { //erstes Fenster der Editor View
    private final double INSETS = 10;
    private final int STYLE_BUTTON_GROESSE = 50;
    public final String IMAGEPATH = "images/default/";

    private ImageLoader imageLoader;
    ModusController modusController;
    EditorManager manager;
    LevelDataJson levelData;
    LevelJson level;

    private double centerHoehe;
    private double centerBreite;
    private Feld[][] map;
    Set<SmartImage> markierteFelder;



    public BasicEditor(double breite, double hoehe, EditorManager manager, ModusController modusController) {
        centerBreite = breite * 0.6;
        centerHoehe = hoehe * 0.8;
        this.manager = manager;

        this.modusController = modusController;
        imageLoader = new ImageLoader(IMAGEPATH);
        levelData = manager.getLevelData();
        levelData.setDesign(IMAGEPATH);
        level = manager.getLevel();

        level.setWidth(16); //Anfangsgröße der gestaltbaren Map
        level.setHeight(9);
        level.setGems(new int[5]);
        level.setTime(new int[5]); //NeuesLevel: werte müssen initialisiert werden

        //angeklickte felder werden in diese Liste gepackt um inhalte/ flags zu ändern
        markierteFelder = new HashSet<>();
        map = initialiseMap();

        this.setTop(initTopBox());
        this.setLeft(initLeftBox());
        this.setRight(initRightBox());
        drawMapBox(map);
        //void methode, da sie auch nach jedem resize aufgerufen wird und
        // dann automatisch dem center hinzugefügt werden soll

        this.getStylesheets().add("/stylesheets/Editor.css");
        macheMeldung("Erklärung: Befülle alle Textboxen. Klicke auf die Karte, um ein Kästchen zu " +
                "markieren und dann auf einen Inhalt oder ein Flag, um es dort hizuzufügen.");
    }

    /**
     * Baut die topBox zusammen:
     * TextFelder: name, breite, hoehe, filename, needpoints, ticks per second
     * ComboBox: Flags, zur Flag auswahl
     * jeweils mit Listener, der eingaben prüft und dann ins JsonLevel bzw. LevelData einträgt
     * @return HBox topBox
     */
    private HBox initTopBox() {
        HBox topBox = new HBox(INSETS);
        TextField name = new TextField();
        name.setPromptText(("Benenne dein Level"));
        //focusedProperty, damit erst nach ende des eintrags aktualisiert wird, nicht nach jedem change(Buchstaben)
        name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) {
                level.setName(name.getText());
            }
        });

        TextField breiteFeld = new TextField();
        breiteFeld.setPromptText(("Breite"));
        breiteFeld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false && !breiteFeld.getText().equals("")) {
                if (breiteFeld.getText().matches("\\d*")) {
                    level.setWidth(Integer.parseInt(breiteFeld.getText()));
                    resizeMap(level.getWidth(), level.getHeight());
                } else {
                    onlyIntAlert(breiteFeld);
                }
            }
        });

        TextField hoeheFeld = new TextField();
        hoeheFeld.setPromptText("Höhe");
        hoeheFeld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false && !hoeheFeld.getText().equals("")) {
                if (hoeheFeld.getText().matches("\\d*")) {
                    level.setHeight(Integer.parseInt(hoeheFeld.getText()));
                    resizeMap(level.getWidth(), level.getHeight());
                } else {
                    onlyIntAlert(hoeheFeld);
                }
            }
        });
        //Metadata
        TextField fileName = new TextField();
        fileName.setPromptText(("Dateiname"));
        fileName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) {
                levelData.setPath(fileName.getText());
            }
        });

        TextField needpoints = new TextField();
        needpoints.setPromptText(("Needpoints"));
        needpoints.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false && !needpoints.getText().equals("")) {
                if (needpoints.getText().matches("\\d*")) {
                    levelData.setNeedpoints(Integer.parseInt(needpoints.getText()));
                } else {
                    onlyIntAlert(needpoints);
                }
            }
        });

        TextField ticksPerSecons = new TextField();
        ticksPerSecons.setPromptText(("Ticks/Sekunde"));
        ticksPerSecons.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false && !ticksPerSecons.getText().equals("")) {
                if (ticksPerSecons.getText().matches("\\d*")) {
                    level.setTicksPerSecond(Integer.parseInt(ticksPerSecons.getText()));
                } else {
                    onlyIntAlert(ticksPerSecons);
                }
            }
        });

        //Liste aller Flags, die ich hier für brauchbar halte, für eine Combo box braucht man eine observableList
        ObservableList<Flag> flags = FXCollections.observableArrayList();
        flags.addAll(Flag.UP, Flag.RIGHT, Flag.DOWN, Flag.LEFT,
                Flag.A, Flag.B, Flag.C, Flag.D);
        final ComboBox flagBox = new ComboBox(flags);
        flagBox.setPromptText("Flags");
        flagBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (SmartImage clickedImg : markierteFelder) {
                clickedImg.fuegeFlagHinzu((Flag) newValue);
            }
            markierteFelder.clear();
        });
        topBox.getChildren().addAll(name, breiteFeld, hoeheFeld,flagBox, fileName, needpoints,ticksPerSecons);
        return topBox;
    }

    /**
     * Initialiseirt die linke Box
     * Für alle Feldinhalte wird ein anklickbares SmartImage erstellt, das beim klick markiertenFelder(liste)
     * in ein Feld mit angeklicktem feldinhalt verwandelt, flags werden dabei gelöscht, um chaos zu vermeiden
     * @return ScrollPane leftBox
     */
    private ScrollPane initLeftBox() {
        ScrollPane leftBox = new ScrollPane();
        GridPane grid = new GridPane();
        int spalte = 0;
        int zeile = 0;
        EnumSet<Feldinhalt> waelbareInhalte = EnumSet.allOf(Feldinhalt.class);
        EnumSet<Feldinhalt> zuLoeschende = EnumSet.of(Feldinhalt.ME2, Feldinhalt.ME3, Feldinhalt.ME4);
        waelbareInhalte.removeAll(zuLoeschende);
        for (Feldinhalt inhalt : waelbareInhalte) {
            SmartImage styleButton = new SmartImage(new Feld(inhalt, EnumSet.noneOf(Flag.class)), STYLE_BUTTON_GROESSE);
            grid.add(styleButton, spalte, zeile);
            spalte++;
            if (spalte == 2) {
                spalte = 0;
                zeile++;
            }
            styleButton.setOnMouseClicked(event -> {
                for (SmartImage clickedImg : markierteFelder) {
                    clickedImg.wandleFeld(inhalt);
                }
                markierteFelder.clear();
            });
            leftBox.setContent(grid);
        }
        return leftBox;
    }

    public Feld[][] initialiseMap() {
        map = new Feld[level.getWidth()][level.getHeight()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = new Feld(Feldinhalt.PATH, EnumSet.noneOf(Flag.class));
            }
        }
        return map;
    }

    /**
     * Wandelt die map in eine grid auf SmartImages um, um sie anzuzeigen und klickbar zu machen
     * @param map
     * @see SmartImage
     */
    private void drawMapBox(Feld[][] map) {
        ScrollPane mapBox = new ScrollPane();
        GridPane mapGrid = new GridPane();
        double feldGroesse = Math.max(centerBreite / map.length, centerHoehe / map[0].length);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                SmartImage feld = new SmartImage(map[i][j], feldGroesse);
                feld.setOnMouseClicked(event -> {
                    markierteFelder.add(feld);
                    feld.markiereFeld();
                });
                mapGrid.add(feld, i, j);
            }
        }
        mapBox.setContent(mapGrid);
        this.setCenter(mapBox);
    }

    /**
     * für jeden eintrag im gem[] und time[] array wird in schleife ein eingabefeld mit listener erstellt
     * Knöpfe: save, clear & return werden erstellt
     * @return VBox rightBox
     */
    private VBox initRightBox() {
        VBox rightBox = new VBox();
        VBox gemBox = new VBox();
        VBox timeBox = new VBox();
        for (int i = 0; i < level.getGems().length; i++) {
            TextField gemFeld = new TextField();
            gemFeld.setPromptText("gems" + (i + 1));
            int finalI = i;
            gemFeld.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == false && !gemFeld.getText().equals("")) {
                    if (gemFeld.getText().matches("\\d*")) {
                        level.getGems()[finalI] = Integer.parseInt(gemFeld.getText());
                    } else {
                        onlyIntAlert(gemFeld);
                    }
                }
            });

            TextField timeFeld = new TextField();
            timeFeld.setPromptText("time" + (i + 1));
            timeFeld.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == false && !timeFeld.getText().equals("")) {
                    if (timeFeld.getText().matches("\\d*")) {
                        level.getTime()[finalI] = Integer.parseInt(timeFeld.getText());
                    } else {
                        onlyIntAlert(timeFeld);
                    }
                }
            });
            gemBox.getChildren().add(gemFeld);
            timeBox.getChildren().add(timeFeld);
        }
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            Feld[][] newMap = initialiseMap();
            drawMapBox(newMap); //Map wird durch neue map aus "path" ersetzt und so geleert
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            if (eingabenRichtig()) {
                level.setMap(convertMap(map));
                manager.saveLevel(level, levelData);
                macheMeldung("Dein Level wurde gespeichert! Kehre zurück, um es zu spielen oder kreiere weitere Level!");
            }
        });
        Button returnButton = new Button("Return");
        returnButton.setOnAction(event -> {
            LevelLoader loader = new LevelLoader(LevelPackReader.PATH_MY_LEVELS, null);
            modusController.zeigeLevelPack(loader, true); //Rückkehr zum Level Pack, true steht für pack mit editor
        });
        rightBox.getChildren().addAll(gemBox, timeBox, clearButton, saveButton, returnButton);
        return rightBox;
    }

    /**
     * da die größe eines arrays nicht verändert werden kann, tausche ich den alten array gegen einen neuen
     * alle felder die im alten array schon gefüllt sind werden auf den neuen übertragen
     * @param breite
     * @param hoehe
     */
    private void resizeMap(int breite, int hoehe) {
        Feld[][] newMap = new Feld[breite][hoehe];
        int maxBreite = Math.min(newMap.length, map.length);
        int maxHoehe = Math.min(newMap[0].length, map[0].length);
        for (int i = 0; i < newMap.length; i++) {
            for (int j = 0; j < newMap[0].length; j++) {
                if (i < maxBreite && j < maxHoehe) {
                    newMap[i][j] = map[i][j];
                } else {
                    newMap[i][j] = new Feld(Feldinhalt.PATH, EnumSet.noneOf(Flag.class));
                }
            }
        }
        map = newMap;
        drawMapBox(map); //die neue map muss für die anzeige gespeichert werden
    }

    /**
     * wenn eine texteingabe in ein numerisches TextField eingegeben wird: kommt alert, TextField toClear wird geleert
     * @param toClear
     */
    private void onlyIntAlert(TextField toClear) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nur ganze Zahlen");
        alert.showAndWait();
        toClear.setText("");
    }

    private void macheMeldung(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    /**
     * Da felder handlicher sind als Tokens, habe ich zunächst mit Feld gearbeitet,
     * zur speicherung in Json wandle ich hier meine Feld[][] map in Token[][] map um
     * @param map
     * @return
     */
    private Token[][] convertMap(Feld[][] map) {
        Token[][] tokenMap = new Token[map[0].length][map.length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                tokenMap[j][i] = map[i][j].toToken();
            }
        }
        return tokenMap;
    }

    private boolean eingabenRichtig() {
        boolean richtig = true;
        if (level.getName() == null) {
            macheMeldung("Levelname fehlt.");
            richtig = false;
        }
        for (int i = 1; i < level.getGems().length; i++) {
            if (level.getGems()[i] <= level.getGems()[i - 1]) {
                macheMeldung("gems" + i + " muss größer sein als gems" + (i - 1));
                richtig = false;
            }
        }
        for (int i = 1; i < level.getTime().length; i++) {
            if (level.getTime()[i] >= level.getTime()[i - 1]) {
                macheMeldung("time" + i + " muss kleiner sein als time" + (i - 1));
                richtig = false;
            }
        }
        return richtig;
    }

    /**
     * Pane, die ein ImageView enthält und das ImageView, sowie einen Feldinhalt als Feld speichert
     * Zweck: pane hat farbigenBackground: durch herabsetzen der Opacity des ImageView entsteht markierungs- Effekt
     * Zweck2: Durch Felder ist Zugriff auf Feld und Bild jederzeit möglich
     */
    private class SmartImage extends Pane {
        Feld feld;
        ImageView anzeige;

        public SmartImage(Feld feld, double size) {
            this.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.feld = feld;
            Image img = imageLoader.getImages(feld.getFeldinhalt());
            anzeige = new ImageView(img);
            anzeige.setFitWidth(size);
            anzeige.setPreserveRatio(true);
            this.getChildren().add(anzeige);
        }

        public void markiereFeld() {
            anzeige.setOpacity(0.5);
        }

        public void wandleFeld(Feldinhalt neuerFeldinhalt) {
            this.feld.setFeldinhalt(neuerFeldinhalt);
            this.feld.getFlags().clear();
            anzeige.setImage(imageLoader.getImages(neuerFeldinhalt));
            anzeige.setOpacity(1);
        }

        public void fuegeFlagHinzu(Flag flag) {
            this.feld.getFlags().add(flag);
            anzeige.setOpacity(1);
        }
    }

}