package editor;

import controller.ModusController;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Created by Ella on 05/02/2017.
 * TabPane enthält RuleEditor und BasicEditor,
 * der manager wird übergeben um seine leeren LevelContainer mit Inhalt zu füllen
 */
public class EditorView extends TabPane {
    private double breite, hoehe;
    private BorderPane basicsBox, ruleEditorBox;
    EditorManager editorManager;

    public EditorView(double screenWidth, double screenHeight, ModusController modusController) {
        editorManager = new EditorManager();
        breite = screenWidth * 0.7;
        hoehe = screenHeight * 0.7;
        this.setMaxWidth(breite);
        this.setMaxHeight(hoehe);

        basicsBox = new BasicEditor(breite, hoehe, editorManager, modusController);
        Tab basics = new Tab("BasicEditor", basicsBox);

        ruleEditorBox = new RuleEditor(editorManager);
        Tab ruleEditor = new Tab("Rule Editor", ruleEditorBox);

        this.getTabs().add(basics);
        this.getTabs().add(ruleEditor);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    public double getBreite() {
        return breite;
    }

    public double getHoehe() {
        return hoehe;
    }


}

