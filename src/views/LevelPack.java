package views;

import java.util.List;

import controller.ModusController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Level;
import models.LevelData;

/**
 * Hier wird das LevelPack zusammengebaut, die von der view angezeigt werden kann,
 * nachdem es in der Packauswhl gewählt wurde
 */
public class LevelPack extends ScrollPane {
    public static final int SPALTEN = 3;
    public static final String NEEDPOINTS_TEXT = "Needpoints: ";
    public static final String RETURN_ICON = "file:images/returnIcon.png";
    public static final String PLUS_IMG = "file:images/addLevelIcon.png";
    public static final String STERN_UNBEFUELLT = "file:images/sternUnbefuellt.png";
    public static final String STERN_BEFUELLT = "file:images/sternBefuellt.png";
	public static final int RETURN_IMG_SIZE = 50;

	public static final String STYLESHEETS_CSS = "/stylesheets/Menue.css";
	public static final String BUTTON_CSS = "menueButton";
	public static final String PANE_CSS = "menuePane";
	public static final String RETURN_BTN_CSS = "returnButton";
	public static final String BUTTONBOX_CSS = "buttonBox";
	public static final String ADD_LEVEL_BTN_CSS = "addLevelButton";
	public static final int PLUS_IMG_SIZE = 100;
	public static final int STERN_SIZE = 20;
	public static final String WARNUNG_PUNKTE = "Es sind nicht genügend Punkte vorhanden - Level kann nicht gespielt werden!";

	private ModusController modusController;
    private List<LevelData> levelDatas;
	private boolean cheatCode;

	/**
	 * @param modusController für kommunikation zwische ansichten
	 * @param levelDatas levelDatas der im LevelPack enthaltenen Level
	 * @param editor enthält dieses Pack die möglichkeit neue level zu erstellenO
	 */
    public LevelPack(ModusController modusController, List<LevelData> levelDatas, boolean editor) {
        this.modusController = modusController;
        this.levelDatas = levelDatas;
        this.getStylesheets().add(STYLESHEETS_CSS);
		this.getStyleClass().add(PANE_CSS);

		// return button
        Button returnButton = makeReturnButton();
		if (editor) {
			returnButton.setOnAction(event -> modusController.zeigePlayerModusAuswahl());
		} else {
			returnButton.setOnAction(event -> modusController.zeigePackAuswahl());
		}

		// pane fuer buttons
        GridPane buttonBox = new GridPane();
		buttonBox.getStyleClass().add(BUTTONBOX_CSS);
		fillButtonBox(buttonBox, editor);
		VBox inhalt = new VBox();
        inhalt.getChildren().addAll(returnButton, buttonBox);
		inhalt.getStyleClass().add(PANE_CSS);
		setContent(inhalt);

		addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
    }

	/**
	 *
	 * @param buttonBox
	 * @param editor
	 */
	private void fillButtonBox(GridPane buttonBox, boolean editor) {
		int zeile = 0;
		int spalte = 0;
		for (int i = 0; i < levelDatas.size(); i++) {
			LevelData levelData = levelDatas.get(i);
			Level level = levelData.getLevel();

			// Erstellung des Knopfes
			Label nameLabel = new Label(level.getName());
			nameLabel.setWrapText(true);

			String needpoints = NEEDPOINTS_TEXT + levelData.getNeedpoints();
			Label pointsLabel = new Label(needpoints);

			VBox btnPane = new VBox(nameLabel, pointsLabel, zeichneSterne(levelData));
			btnPane.setSpacing(10);

			Button levelButton = new Button("", btnPane);
			levelButton.setOnAction(new AuswahlButtonHander(i));
			levelButton.getStyleClass().add(BUTTON_CSS);

			buttonBox.add(levelButton, zeile, spalte);

			zeile++;
			if ((i + 1) % SPALTEN == 0) {
				spalte++;
				zeile = 0;
			}
		}

		// Der Knopf + erscheint immer am Ende der Levelliste
		if (editor) {
			Button addLevelButton = makeAddLevelButton();
			addLevelButton.setOnAction(event -> modusController.zeigeLevelEditor());
			buttonBox.add(addLevelButton, zeile, spalte);
		}
	}

	// AddLevelButton hinzugefügt
	private Button makeAddLevelButton() {
		ImageView imageView = new ImageView(new Image(PLUS_IMG));
		imageView.setFitWidth(PLUS_IMG_SIZE);
		imageView.setFitHeight(PLUS_IMG_SIZE);

		Button addLevelButton = new Button("", imageView);
		addLevelButton.getStyleClass().add(ADD_LEVEL_BTN_CSS);

		return addLevelButton;
	}

	//return Button hinzugefügt
	private Button makeReturnButton() {
		ImageView imgView = new ImageView(new Image(RETURN_ICON));
		imgView.setFitWidth(RETURN_IMG_SIZE);
		imgView.setFitHeight(RETURN_IMG_SIZE);

		Button returnBtn = new Button("", imgView);
		returnBtn.getStyleClass().add(RETURN_BTN_CSS);

		return returnBtn;
	}

	/**
	 *
	 * @param levelData
	 * @return 
	 */
    private HBox zeichneSterne(LevelData levelData) {
        HBox hBox = new HBox();

        int points = levelData.getRekordPunkte();
        ImageView stern;

        for (int i = 1; i <= 5; i++) {
            if (i <= points) {
                stern = new ImageView(STERN_BEFUELLT);
            } else {
                stern = new ImageView(STERN_UNBEFUELLT);
            }

            stern.setFitWidth(STERN_SIZE);
            stern.setFitHeight(STERN_SIZE);
            hBox.getChildren().add(stern);
        }

        return hBox;
    }


    private class AuswahlButtonHander implements EventHandler<ActionEvent> {
		private int levelIndex;

        public AuswahlButtonHander(int levelIndex) {
			this.levelIndex = levelIndex;
        }

        @Override
        public void handle(ActionEvent event) {
            if (modusController.levelKannFreigeschaltenWerden(levelIndex) || cheatCode) {
                modusController.zeigeLevelAnsicht(levelIndex);
            } else {
                zeigeWarnung(WARNUNG_PUNKTE);
            }
        }


		private void zeigeWarnung(String nachricht) {
			Alert alert = new Alert(Alert.AlertType.WARNING, nachricht);
			alert.showAndWait();
		}
	}


	private class KeyHandler implements EventHandler<KeyEvent> {
		private int sequence = 0;

		@Override
		public void handle(KeyEvent event) {
			switch (event.getCode()) {
				case UP:
					checkSequence(0, 1, 2);
					break;
				case DOWN:
					if (checkSequence(3)) cheatCode = true ;
					break;
				default:
					sequence = 0;
					break;
			}
		}

		private boolean checkSequence(Integer... ints) {
			for (int i : ints) {
				if (sequence == i) {
					sequence++;
					return true;
				}
			}

			sequence = 0;
			return false;
		}
	}
}
