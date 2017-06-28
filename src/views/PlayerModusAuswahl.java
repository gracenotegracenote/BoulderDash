package views;

import controller.SelectionController;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import models.PlayerModus;

/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 17-Feb-17
 */
public class PlayerModusAuswahl extends HBox {
	public static final String BUTTON_CSS = "menueButton";
	public static final String PANE_CSS = "menuePane";
	public static final String STYLESHEETS_CSS = "/stylesheets/Menue.css";
	public static final int IMG_SIZE = 90;


	/**
	 *
	 * @param selectionController
	 */

	public PlayerModusAuswahl(SelectionController selectionController) {
		// CSS
		getStylesheets().add(STYLESHEETS_CSS);
		getStyleClass().add(PANE_CSS);

		// backgrounds for buttons
		ImageView imgSP = new ImageView(new Image("file:images/Einzelspieler_Levels.jpg"));
		ImageView imgMP = new ImageView(new Image("file:images/Mehrspieler_Levels.jpg"));
		ImageView imgMyLevels = new ImageView(new Image("file:images/Meine_Level.jpg"));

		imgSP.setFitWidth(IMG_SIZE); imgSP.setFitHeight(IMG_SIZE);
		imgMP.setFitWidth(IMG_SIZE); imgMP.setFitHeight(IMG_SIZE);
		imgMyLevels.setFitWidth(IMG_SIZE); imgMyLevels.setFitHeight(IMG_SIZE);

		Button einzelSpielerLevelsButton = erstelleButton("", imgSP,
				event -> selectionController.zeigePackAuswahl(PlayerModus.SINGLEPLAYER));
		Button mehrSpielerLevelsButton = erstelleButton("", imgMP,
				event -> selectionController.zeigePackAuswahl(PlayerModus.MULTIPLAYER));
		Button meineLevelsButton = erstelleButton("", imgMyLevels,
				event -> selectionController.zeigeEditorPack());

		this.getChildren().addAll(einzelSpielerLevelsButton, mehrSpielerLevelsButton, meineLevelsButton);
	}

	private Button erstelleButton(String text, Node graphic, EventHandler e){
		Button button = new Button(text, graphic);
		button.getStyleClass().add(BUTTON_CSS);
		button.setOnAction(e);
		return button;
	}

}
