package editor;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Direction;
import models.Feldinhalt;
import models.Flag;
import models.Situation;
import parser.LevelJson;
import parser.RuleJson;
import parser.Token;

/**
 * Created by Ella on 15/02/2017.
 * Fast fertige View um Rules zusammenzustecken
 * Problem zur Fertigstellung: Parser sollte auch für rules verwendet werden um mit GSON das level zu speichern
 * Token Klasse jedoch für Rules ungeeignet, Workaround: gesamtes Level von Hand in Json Objekte verwandeln
 * Durch den Zeitaufwand für diese Aufgabe war anderes vorrangig
 */
public class RuleEditor extends BorderPane {
    LevelJson level;
    RuleJson rule;
    Token[] original, result;
    public final int ANZAHL_REGELBAUSTEINE = 3;

    public RuleEditor(EditorManager manager) {
        this.level = manager.getLevel();
        rule = new RuleJson();
        original = new Token[ANZAHL_REGELBAUSTEINE];
        result = new Token[ANZAHL_REGELBAUSTEINE];
        rule.setProbability(1);

        this.getStylesheets().add("/stylesheets/Editor.css");
        this.setPadding(new Insets(10));

        //Listen an möglichen Flags, die die View elemente zur auswahl bereitstellen sollen
        ObservableList<String> availableTokensOrig = FXCollections.observableArrayList();
        ObservableList<String> availableTokensResult = FXCollections.observableArrayList();
        for (Feldinhalt inhalt : Feldinhalt.values()) {
            availableTokensOrig.add(inhalt.getName());
            availableTokensResult.add(inhalt.getName());
        }
        availableTokensOrig.add("*");
        availableTokensResult.addAll("0","1","2");

        //Original und Result können per Baukastenprinzip zusammengesteckt werden
        //Eine Regelbausteinbox enthält 3 Regelbausteine --> 3 teilige Regeln möglich
        //jeder Baustein kann mit mehreren Tokens und Flags befüllt werden
        VBox regelbausteinBox = new VBox();
        regelbausteinBox.getChildren().addAll(initBausteinBox(availableTokensOrig,"Original:",original),
                initBausteinBox(availableTokensResult, "Result:",result));

        Button regelMerken = new Button("Regel merken");
        regelMerken.setOnAction(event -> {if(regelRichtig()){
			manager.getGemerkteRegeln().add(rule);
        }});

        this.setTop(initTopBox());
        this.setCenter(regelbausteinBox);
        this.setBottom(regelMerken);
    }

    /**
     * Prüft ob eine regel richig ist, also ob etwas vergessen wurde oder nicht
     * @return
     */
    private Boolean regelRichtig(){
        boolean regelRichtig = true;
        if(rule.getDirection()==null){etwasFehlt("Gib eine Direction ein"); regelRichtig=false;}
        if(rule.getSituation()==null){etwasFehlt("Gib eine Situation ein");regelRichtig=false;}
        if (original[0]==null){etwasFehlt("Gib ein Original an");regelRichtig=false;}
        else {rule.setOriginal(original);}
        if (result[0]==null){etwasFehlt("Gib ein Result an");regelRichtig=false;}
        else {rule.setResult(result);}
        return regelRichtig;
    }

    /**
     * Baut Top Box zusammen: Situation, Probability, Direction, Pre- oder PostRule?
     * @return
     */
    private HBox initTopBox() {
        HBox topBox = new HBox();
        //Situation
        ObservableList<String> situations = FXCollections.observableArrayList();
        for (Situation sit : Situation.values()) {
            situations.add(sit.getSituation());
        }
        ComboBox sitBox = new ComboBox(situations);
        sitBox.setPromptText("Situation");
        sitBox.valueProperty().addListener((observable, oldValue, newValue) -> rule.setSituation((String) newValue));
        //Direction
        ObservableList<String> directions = FXCollections.observableArrayList();
        for (Direction dir : Direction.values()) {
            directions.add(dir.getDirection());
        }
        ComboBox dirBox = new ComboBox(directions);
        dirBox.setPromptText("Direction");
        dirBox.valueProperty().addListener((observable, oldValue, newValue) -> rule.setDirection((String) newValue));
        //Rule Type
        ObservableList<String> ruleTypes = FXCollections.observableArrayList();
        ruleTypes.addAll("Prerule", "Postrule");
        ComboBox ruleBox = new ComboBox(ruleTypes);
        ruleBox.setPromptText("Rule Type");
        ruleBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = (String) newValue;
            /*if (value.matches("Prerule")) {
                level.getPrerules()[level.getPrerules().length - 1] = rule;
            } else if (value.matches("Postrule")) {
                level.getPostrules()[level.getPostrules().length - 1] = rule;
            }*/
        });

        //Probability
        TextField probability = new TextField();
        probability.setPromptText(("% Probability"));
        probability.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false && !probability.getText().equals("")) {
                if (probability.getText().matches("\\d*")) {
                    rule.setProbability(Double.parseDouble(probability.getText()) * 0.01);
                } else {
                    onlyIntAlert(probability);
                }
            }
        });
        topBox.getChildren().addAll(sitBox, dirBox, probability);
        return topBox;
    }

    /**
     * Hier kann ein Regelbausetein, also Original oder Result zusammengebaut werden
     * Für die Feste anzahl an Feldern einer Regel wird die Schleife durchlaufen
     * @param availableTokens Original: alle feldinhalte und *, Result: alle Feldinhalte und 0,1,2
     * @param ueberschrift
     * @param originalOrResult dieses Token[] stellt das fertige Orignal oder Result dar und wierd hier durch eingaben befüllt
     * @return bausteinBox
     */
    private HBox initBausteinBox(ObservableList<String> availableTokens, String ueberschrift, Token[] originalOrResult) {
        HBox bausteinBox = new HBox();
        Label ueberschriftLabel = new Label(ueberschrift);
        bausteinBox.getChildren().add(ueberschriftLabel);
        for (int l = 0; l < ANZAHL_REGELBAUSTEINE; l++) {
            originalOrResult[l] = new Token();
        }

        ObservableList<String> availableFlags = FXCollections.observableArrayList();

        for (int i = 0; i < ANZAHL_REGELBAUSTEINE; i++) {
            int finalI = i; // für Lambda aktionen
            originalOrResult[finalI].setFlags(new HashMap<>());
            VBox regelbauseteinBox = new VBox();
            //TokenAnzeige für jeden Regelbausetein
            Anzeige tokenAnzeige = new Anzeige("Feldinhalte");

            //TokenAuswahl für jeden Regelbausetein
            ComboBox tokenAdder = new ComboBox(availableTokens);
            tokenAdder.setPromptText("wähle Feldinhalte");
            tokenAdder.valueProperty().addListener((observable, oldValue, newValue) -> {
                String newVal = (String) newValue;
                if (originalOrResult[finalI].getNames() == null) {
                    String[] names = new String[]{newVal};
                    originalOrResult[finalI].setNames(names);

                } else {
                    String[] names = new String[originalOrResult[finalI].getNames().length + 1];
                    for (int j = 0; j < originalOrResult[finalI].getNames().length; j++) {
                        names[j] = originalOrResult[finalI].getNames()[j];
                    }
                    names[names.length - 1] = newVal;
                    originalOrResult[finalI].setNames(names);
                }
                String ausgabe = new String();
                for (String name : originalOrResult[finalI].getNames()) {
                    ausgabe = (ausgabe + name + " ");
                }
                tokenAnzeige.setText(ausgabe);
            });
            //Anzeige für false-Flags und true-Flags
            Anzeige trueFlagAnzeige = new Anzeige("True-Flags");
            Anzeige falseFlagAnzeige = new Anzeige("False-Flags");
            //Auswahl der Flags durch Combo Box
            for (Flag flag : Flag.values()) {
                availableFlags.add(flag.getFlag());
            }
            ComboBox trueFlagAdder = new ComboBox(availableFlags);
            trueFlagAdder.setPromptText("wähle True-Flags");
            trueFlagAdder.valueProperty().addListener((observable, oldValue, newValue) -> {
                originalOrResult[finalI].getFlags().put((String) newValue, true);
                trueFlagAnzeige.setText(trueFlagAnzeige.getText() + (String) newValue + " ");
            });
            ComboBox falseFlagAdder = new ComboBox(availableFlags);
            falseFlagAdder.setPromptText("wähle False-Flags");
            falseFlagAdder.valueProperty().addListener((observable, oldValue, newValue) -> {
                originalOrResult[finalI].getFlags().put((String) newValue, false);
                falseFlagAnzeige.setText(falseFlagAnzeige.getText() + (String) newValue+ " ");
            });

            regelbauseteinBox.getChildren().addAll(tokenAnzeige, trueFlagAnzeige, falseFlagAnzeige,
                    tokenAdder, trueFlagAdder, falseFlagAdder);

            bausteinBox.getChildren().add(regelbauseteinBox);
        }
        return bausteinBox;
    }

    private void onlyIntAlert(TextField toClear) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nur ganze Zahlen");
        alert.showAndWait();
        toClear.setText("");
    }

    private void etwasFehlt(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }


    /**
     * Nicht beschreibbares Textfeld zur anzeige von daten, mit beschriftung davor
     */
    private class Anzeige extends HBox {
        TextField anzeige;

        public Anzeige(String beschriftung) {
            Label anzeigeName = new Label(beschriftung);
            this.anzeige = new TextField();
            anzeige.setEditable(false);
            this.getChildren().addAll(anzeigeName, anzeige);
            this.setSpacing(10);
        }

        public void setText(String text) {
            anzeige.setText(text);
        }

        public String getText() {
            return anzeige.getText();
        }

    }
}
