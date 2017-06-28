package views;

import java.util.List;

import controller.LevelLoader;
import controller.LevelPackReader;
import controller.ModusController;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.PlayerModus;

import static views.LevelPack.RETURN_ICON;
import static views.LevelPack.RETURN_IMG_SIZE;
import static views.LevelPack.SPALTEN;

/**
 * Created by atanasova on 31.01.2017.
 */

public class PackAuswahl extends VBox {
	public static final String STYLESHEETS_CSS = "/stylesheets/Menue.css";
	public static final String BUTTON_CSS = "menueButton";
	public static final String PANE_CSS = "menuePane";
	public static final String RETURN_BTN_CSS = "returnButton";
	public static final String BUTTONBOX_CSS = "buttonBox";

	private ModusController modusController;


	// TODO: eltern klasse fuer menue-views ?

	/**
	 *
	 * @param modusController um Panes auf der Hauptstage auswechseln zu können
	 * @param playerModus Singlplayer oder Mulitplayer
	 */
	public PackAuswahl(ModusController modusController, PlayerModus playerModus) {
        this.modusController = modusController;

		// CSS
        this.getStylesheets().add(STYLESHEETS_CSS);
        this.getStyleClass().add(PANE_CSS);

		// return button
		Button returnBtn = makeReturnButton();
		returnBtn.setOnAction(event -> modusController.zeigePlayerModusAuswahl());

		// pane für buttons
		GridPane buttonBox = new GridPane();
		buttonBox.getStyleClass().add(BUTTONBOX_CSS);
		fillButtonBox(buttonBox, getPath(playerModus));

		getChildren().addAll(returnBtn, buttonBox);
    }


	private String getPath(PlayerModus playerModus) {
		String path;
		if (playerModus == PlayerModus.SINGLEPLAYER) {
			path = LevelPackReader.PATH_SINGLEPLAYER;
		} else {
			path = LevelPackReader.PATH_MULTIPLAYER;
		}
		return path;
	}


	private void fillButtonBox(GridPane buttonBox, String path) {
		int zeile = 0;
		int spalte = 0;
		int i = 0;

		// Suche nach Ordnern und Erstellung der Buttons
		List<String> packNames = new LevelPackReader(LevelPackReader.PATH_LEVEL_PACKS + path).getPackNames();
		for (String packName : packNames) {									// TODO: exception wenn keine levels drin
			Button packButton = new Button(packName);
			packButton.getStyleClass().add(BUTTON_CSS);

			packButton.setOnAction(event1 -> {
				LevelLoader levelLoader = new LevelLoader(path + "/" + packName, modusController.getSpielerName());
				modusController.zeigeLevelPack(levelLoader, false);
			});

			buttonBox.add(packButton, zeile, spalte);

			zeile++;
			if ((i + 1) % SPALTEN == 0) {
				spalte++;
				zeile = 0;
				i++;
			}
		}
	}


	private Button makeReturnButton() {
		ImageView imgView = new ImageView(new Image(RETURN_ICON));
		imgView.setFitWidth(RETURN_IMG_SIZE);
		imgView.setFitHeight(RETURN_IMG_SIZE);

		Button returnBtn = new Button("", imgView);
		returnBtn.getStyleClass().add(RETURN_BTN_CSS);

		return returnBtn;
	}
}



