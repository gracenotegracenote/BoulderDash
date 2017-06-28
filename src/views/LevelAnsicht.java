package views;

import java.util.EnumSet;
import java.util.Set;

import controller.ImageLoader;
import controller.ModusController;
import controller.SoundLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import models.Feld;
import models.Feldinhalt;
import models.Flag;
import models.Koordinate;
import models.Level;
import models.LevelData;
import models.PunkteRechner;
import models.Situation;

/**
 * Hier wird die Levelansicht zusammengebaut, die von der view angezeigt werden kann
 */
public class LevelAnsicht extends BorderPane {
	public static final int INSETS = 10;
	public static final int KOPFLEISTENPUFFER = 45;

	public static final String KOPFLEISTE_GEMS = "Noch: ";
	public static final String DESIGN_BTN = "DESIGN";
	public static final String PAUSE_TEXT = "PAUSE";
	public static final String NEU_SPIELEN_BTN_TEXT = "Neu spielen";
	public static final String RETURN_BUTTON_TEXT = "Return";

	public static final String DESIGNPATH = "images/default/";
	public static final String SOUND_ON_BTN_IMG = "file:images/soundon.png";
	public static final String SOUND_OFF_BTN_IMG = "file:images/soundoff.png";

	public static final String STYLESHEETS_CSS = "/stylesheets/Levelansicht.css";
	public static final String PANE_CSS = "levelAnsicht";
	public static final String ANZEIGE_CSS = "anzeige";
	public static final String MINIMAP_CSS = "minimap";
	public static final String KOPFLEISTE_CSS = "kopfleiste";
	public static final String ERG_FENSTER_LABEL_CSS = "ergFensterLabel";	// TODO: erg fenster scc in die klasse erg fenster uebertragen
	public static final String STYLESHEETS_ERG_FENSTER_CSS = "/stylesheets/ErgFenster.css";
	public static final String ERG_FENSTER_CSS = "ergebnisFenster";
	public static final int PAUSE_STAGE_WIDTH = 200;
	public static final int PAUSE_STAGE_HEIGHT = 100;
	public static final String KOPFLEISTE_BUTTON_PANE_CSS = "kopfleisteButtonPane";
	public static final String KOPFLEISTE_LABEL_PANE_CSS = "kopfleisteLabelPane";

	public static final int MINDEST_FELDGRÖßE = 30; //größe eines Kästchens
	public static final double MINIMAP_FELDGRÖSSE = 9; //größe eines Felds auf der Minimap
	public static final double VIEW_ANZEIGE_BILDGRÖSSE= 25;

	private final int levelIndex;
	private int feldSize;
	private int maxFelderW;
	private int maxFelderH;
	private double ansichtBreite;
	private double ansichtHoehe;
	private double screenWidth, screenHeight;

	private String designIndividuel;
    private boolean designIndivid;
    private ModusController modusController;
    private ImageLoader imageLoader;
    private SoundLoader soundLoader;
    private GridPane mapView;

	private Level level;
	private int levelBreite, levelHoehe;
    private Label countdownAnzeige;
    private Label gemAnzeige;
    private ImageView[][] imageViews;
    private AufMEzentrierer aufMEzentrierer;
    private Canvas minimap;
    private Stage pauseStage;
    private MediaPlayer backgroundPlayer;
    private boolean soundon;


    public LevelAnsicht(ModusController modusController, LevelData levelData, int levelIndex) {
		this.modusController = modusController;
		this.levelIndex = levelIndex;

		// LevelData
		level = levelData.getLevel();
		if (level == null) System.out.println("LEVEL NULL");	// TODO: exception
		levelBreite = level.getBreite();
		levelHoehe = level.getHoehe();

		screenWidth = modusController.getScreenWidth();
		screenHeight = modusController.getScreenHeight();

		maxFelderW = (int) ((screenWidth * 0.75)/ MINDEST_FELDGRÖßE);
		maxFelderH = (int) ((screenHeight * 0.75)/ MINDEST_FELDGRÖßE);
		passeFeldanScreenAn();
		if (feldSize < MINDEST_FELDGRÖßE) { //Sollte das level zu groß sein, werden die felder nicht verkleinert
			feldSize = MINDEST_FELDGRÖßE;
		}

		int mapBreite = Math.min(maxFelderW, levelBreite) * feldSize;
		int mapHoehe = Math.min(maxFelderH, levelHoehe) * feldSize;
		ansichtBreite = mapBreite + 2 * INSETS;
		ansichtHoehe = mapHoehe + 2 * KOPFLEISTENPUFFER + 2 * INSETS;

		// CSS
		getStylesheets().add(STYLESHEETS_CSS);
		getStyleClass().add(PANE_CSS);

		// wenn Enter gedrückt, dann Pause	// TODO: space statt enter
		addEventHandler(KeyEvent.KEY_PRESSED, new PauseHandler());
		initPauseView();

		// View-Elemente initialisieren
		mapView = new GridPane();
		countdownAnzeige = new Label();
		gemAnzeige = new Label();

		// Design
		designIndividuel = levelData.getDesignPath();
        imageLoader = new ImageLoader(designIndividuel);
        soundLoader = new SoundLoader(designIndividuel);
		designIndivid = true;

		// Pane Anpassung
        aufMEzentrierer = new AufMEzentrierer();
        aufMEzentrierer.aufMEzentrieren();

		minimap = new Canvas(levelBreite * MINIMAP_FELDGRÖSSE, levelHoehe * MINIMAP_FELDGRÖSSE);

		zeichneKopfleiste(gemAnzeige, mapBreite);
		zeichneMaps(mapBreite, mapHoehe);
		zeichneFooter(mapBreite);

		// music player
		backgroundPlayer = new MediaPlayer(soundLoader.getBackgroundSong());
		startSounds();
    }

    private void initPauseView() {
        Label label = new Label(PAUSE_TEXT);
        label.getStyleClass().add(ERG_FENSTER_LABEL_CSS);

        BorderPane pane = new BorderPane(label);
        pane.getStylesheets().add(STYLESHEETS_ERG_FENSTER_CSS);
        pane.getStyleClass().add(ERG_FENSTER_CSS);

        pauseStage = new Stage();
        pauseStage.setScene(new Scene(pane));
        pauseStage.setWidth(PAUSE_STAGE_WIDTH);
        pauseStage.setHeight(PAUSE_STAGE_HEIGHT);

        pauseStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                modusController.startTimeline(level, LevelAnsicht.this);
                startSounds();
                pauseStage.close();
            }
        });
    }

    private void startSounds(){
		if (!backgroundPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
			backgroundPlayer.play();
		}
    }

	private void playAdditionalSounds() {
		for (Flag soundFlag : level.getSoundFlags()) {
			MediaPlayer mediaPlayer = new MediaPlayer(soundLoader.getSound(soundFlag));
			mediaPlayer.play();
		}
	}

	public void stopSounds(){
		if (backgroundPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
			backgroundPlayer.stop();
		}
	}

	public void zeichneKopfleiste(Label gemAnzeige, int mapBreite) {
		// return button
		Button returnButton = new Button(RETURN_BUTTON_TEXT);
		returnButton.setOnAction(new ReturnButtonHandler());

		// "Neu spielen" button
		Button neuSpielenButton = new Button(NEU_SPIELEN_BTN_TEXT);
		neuSpielenButton.setOnAction(event -> {
			modusController.zeigeLevelAnsicht(levelIndex);
			stopSounds();
		});

		HBox buttonPane = new HBox(returnButton, neuSpielenButton);
		buttonPane.getStyleClass().add(KOPFLEISTE_BUTTON_PANE_CSS);

		ImageView gemImg = new ImageView(imageLoader.getImages(Feldinhalt.GEM));
		gemImg.setFitWidth(VIEW_ANZEIGE_BILDGRÖSSE);
		gemImg.setPreserveRatio(true);
		ImageView timeImg = new ImageView(imageLoader.getImages(Feldinhalt.EXTRATIME));
		timeImg.setFitWidth(VIEW_ANZEIGE_BILDGRÖSSE);
		timeImg.setPreserveRatio(true);
		HBox gemPane = new HBox(INSETS);
		HBox timePane = new HBox(INSETS);
		gemPane.getStyleClass().add(ANZEIGE_CSS);
		timePane.getStyleClass().add(ANZEIGE_CSS);
		gemPane.getChildren().addAll(gemImg, gemAnzeige);
		timePane.getChildren().addAll(timeImg, countdownAnzeige);


		HBox labelPane = new HBox(gemPane, timePane);
		labelPane.getStyleClass().add(KOPFLEISTE_LABEL_PANE_CSS);
		HBox.setHgrow(labelPane, Priority.ALWAYS);

		HBox kopfleiste = new HBox();
		kopfleiste.setMaxWidth(mapBreite);
		kopfleiste.getStyleClass().add(KOPFLEISTE_CSS);
		kopfleiste.getChildren().addAll(buttonPane, labelPane);

		setTop(kopfleiste);
	}


	public void zeichneFooter(int mapBreite) {
		// design button
		Button designButton = new Button(DESIGN_BTN);
		designButton.setOnAction(event -> {
			stopSounds();

			if (designIndivid) {
				imageLoader = new ImageLoader(DESIGNPATH);
				soundLoader = new SoundLoader(DESIGNPATH);
			} else {
				imageLoader = new ImageLoader(designIndividuel);
				soundLoader = new SoundLoader(designIndividuel);
			}
			designIndivid = !designIndivid;
			backgroundPlayer = new MediaPlayer(soundLoader.getBackgroundSong());
			if(soundon){startSounds();}
		});

		// sound button
		soundon = true;	// TODO: delete ?
		ImageView soundImg = new ImageView(SOUND_ON_BTN_IMG);
		soundImg.setFitWidth(VIEW_ANZEIGE_BILDGRÖSSE);
		soundImg.setPreserveRatio(true);

		Button soundBtn = new Button("", soundImg);
		soundBtn.setOnMouseClicked(event -> {
			if (soundon) {
				stopSounds();
				soundon = false;
				soundImg.setImage(new Image(SOUND_OFF_BTN_IMG));
			} else {
				startSounds();
				soundon = true;
				soundImg.setImage(new Image(SOUND_ON_BTN_IMG));
			}
		});

		HBox footer = new HBox();
		footer.getStyleClass().addAll(KOPFLEISTE_CSS, KOPFLEISTE_BUTTON_PANE_CSS);
		footer.getChildren().addAll(designButton, soundBtn);
		setBottom(footer);
		setAlignment(footer, Pos.BOTTOM_LEFT);
	}

	/**
	 * Hier wird die MapBox gezeichnet: Stack Pane, um Minimap über der Map schwebend zu zeichnen
	 * @param mapBreite
	 * @param mapHoehe
	 */
    private void zeichneMaps(int mapBreite, int mapHoehe) {
		fillMaps();

		Pane minimapBox = new Pane();
		minimapBox.getStyleClass().add(MINIMAP_CSS);
		minimapBox.setMaxWidth(levelBreite * MINIMAP_FELDGRÖSSE);
		minimapBox.setMaxHeight(levelHoehe * MINIMAP_FELDGRÖSSE);
		minimapBox.getChildren().add(minimap);
		StackPane.setAlignment(minimapBox, Pos.TOP_LEFT);

		Pane mapStack = new StackPane();
		mapStack.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		mapStack.setMaxSize(mapBreite, mapHoehe);
		mapStack.setMinSize(mapBreite,mapHoehe);
		//TODO: nich zentrieren wenn nicht noetig
		mapStack.setClip(new Rectangle(mapBreite, mapHoehe));	// Verschiebung der Karte erfolgt im Bereich des Rechtecks, andere Elemente der LevelAnsicht bleiben sichtbar
		mapStack.getChildren().addAll(mapView, minimapBox);
		StackPane.setAlignment(mapView, Pos.TOP_LEFT);

		setCenter(mapStack);
}
	//Die Map und die Minimap werden zu Beginn gezeichnet: Für die Images der Map wird ein ImageView[][]
	//erstellt, der auf der Map abgebildet wird: so muss nur der Array, nicht die Gridpane aktualisiert werden
    private void fillMaps() {
        Feld[][] karte = level.getKarte();
        imageViews = new ImageView[levelHoehe][levelBreite];
        for (int i = 0; i < levelHoehe; i++) {
            for (int j = 0; j < levelBreite; j++) {
                Feldinhalt feldInhalt = karte[i][j].getFeldinhalt();
                Image image = imageLoader.getImages(feldInhalt);
				ImageView iv = new ImageView(image);
                iv.setFitWidth(feldSize);
                iv.setPreserveRatio(true);
                imageViews[i][j] = iv;
                mapView.add(imageViews[i][j], j, i);
                minimap.getGraphicsContext2D().drawImage(image, j * MINIMAP_FELDGRÖSSE, i * MINIMAP_FELDGRÖSSE, MINIMAP_FELDGRÖSSE, MINIMAP_FELDGRÖSSE);
            }
        }
	}

	/**
	 * neue daten aus level werden jeden tick neu in der View abgebildet: gemAnzeige, countdownAnzeige
	 * und zentrierung auf mePosition werden angepasst
	 */
    public void update() {
		PunkteRechner punkteRechner = level.getPunkteRechner();

		int verfuegbareZeit = (int) ((punkteRechner.getTicks()[0] - modusController.getTicks()) * level.getTicklänge());	// TODO: time soll keine ticks sein -> anders rechnen
        countdownAnzeige.setText(""+verfuegbareZeit);

		int gems = punkteRechner.gemsZumNächstenPunkt(level.getGesammelteEdelsteine());
        gemAnzeige.setText(KOPFLEISTE_GEMS+ gems);

        aufMEzentrierer.aufMEzentrieren();
        updateMap();
        playAdditionalSounds();
    }

	/**
	 * Images in ImageViews werden auf neue Spielsituation geupdated
	 * GridPane mapGrid bildet den imageViews[][] array ab,
	 * wird dieser aktualiseirt passt sich der grid inhalt dynamisch an
	 */
    private void updateMap() {
        Feld[][] karte = level.getKarte();
        for (int i = 0; i < levelHoehe; i++) {
            for (int j = 0; j < levelBreite; j++) {
                Feldinhalt feldInhalt = karte[i][j].getFeldinhalt();
                Image image = waehleImage(feldInhalt);
                imageViews[i][j].setImage(image);
                minimap.getGraphicsContext2D().drawImage(image, j * MINIMAP_FELDGRÖSSE, i * MINIMAP_FELDGRÖSSE, MINIMAP_FELDGRÖSSE, MINIMAP_FELDGRÖSSE);
            }
        }
    }

    private Image waehleImage(Feldinhalt feldInhalt) {
		// Felder mit Animation, Me begewet sich abhängig von situation ist
		Set<Situation> playerSituations = null;
		Set<Situation> situations = null;

		if (feldInhalt == Feldinhalt.ME) {
			playerSituations = EnumSet.of(Situation.RIGHT, Situation.LEFT, Situation.UP, Situation.DOWN);
			situations = modusController.getSituations();
		}

		if (feldInhalt == Feldinhalt.ME2) {
			playerSituations = EnumSet.of(Situation.RIGHT2, Situation.LEFT2, Situation.UP2, Situation.DOWN2);
			situations = modusController.getSituations();
		}

		if (feldInhalt == Feldinhalt.ME3) {
			playerSituations = EnumSet.of(Situation.RIGHT3, Situation.LEFT3, Situation.UP3, Situation.DOWN3);
			situations = modusController.getSituations();
		}

		if (feldInhalt == Feldinhalt.ME4) {
			playerSituations = EnumSet.of(Situation.RIGHT4, Situation.LEFT4, Situation.UP4, Situation.DOWN4);
			situations = modusController.getSituations();
		}

		if (situations != null) {
			for (Situation situation : situations) {
				if (playerSituations.contains(situation)) {
					return imageLoader.getAnimatedImages(feldInhalt);
				}
			}
		}
		//Exit wird offen dargestellt
		if (feldInhalt == Feldinhalt.EXIT && level.getGesammelteEdelsteine() >= level.getPunkteRechner().getGems()[0]) {
			return imageLoader.getAnimatedImages(feldInhalt);
		}

		// Felder ohne Animation
        return imageLoader.getImages(feldInhalt);
    }

    private void passeFeldanScreenAn() {
        //kleiner Null bedeutet paneBreite<paneHoehe ergo: nach paneHoehe richten
		double seitenverhaeltnis = levelBreite / levelHoehe;

        //wir rechnen mit -bildschirmbreite*0.75, damit das fenster leicht kleiner als der screen ist
        if (seitenverhaeltnis <= 1) {
            feldSize = (int) (screenHeight * 0.75) / levelHoehe;
        } else {
            feldSize = (int) (screenWidth * 0.75) / levelBreite;
        }
    }

    private class ReturnButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //List<LevelData> levelDatas = modusController.getLevelPack().getLevelDatas();
            modusController.zeigeLevelPack(modusController.getLevelLoader(), false);
            stopSounds();
        }
    }

    private class PauseHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                modusController.pauseTimeline();
                stopSounds();
                pauseStage.show();
            }
        }
    }

	/**
	 * Interne Klasse, da die Felder nur einmal berechnet werden müssen, nicht bei jeder Zentrierung
	 * Felder können hier gespeichert werden, ohne oben zu nerven
	 */
	private class AufMEzentrierer {
		private Koordinate mePos;
		private double maxXVerschiebung, maxYVerschiebung;
		private double xMitte, yMitte;
		private double minimapPuffer;

		public AufMEzentrierer() {
			minimapPuffer = Math.ceil(level.getHoehe() * MINIMAP_FELDGRÖSSE / feldSize); //Platz oben für minimap
			mePos = level.getMEposition();
			maxXVerschiebung = Math.min((maxFelderW - levelBreite), 0); //damit nicht über die grenzen der map geschoben wird
			maxYVerschiebung = Math.min((maxFelderH - levelHoehe), 0);
			xMitte = Math.min(maxFelderW, levelBreite) * 0.5; //mitte der anzeige
			yMitte = Math.min(maxFelderH, levelHoehe) * 0.5;
		}

		/**
		 // Me soll ins zentrum der LevelAnsicht gerückt werden,
		 // damit nicht über die Ränder geschoben wird:
		 // - keine zentrierung wenn me oberhalb der Mittelachse(-Minimapgröße, damit man nicht darunter verschwindet)
		 // - verschiebung nie größer als errechnete maxVerschiebung
		 */

		public void aufMEzentrieren() {
			double deltaX = 0;
			double deltaY = minimapPuffer;

			//xVerschiebung
			int mePosX = mePos.getX();
			if (mePosX > xMitte) {
				deltaX = Math.max((xMitte - ((double) mePosX)), maxXVerschiebung);
			}
			//yVerschiebung
			int mePosY = mePos.getY();
			if (mePosY > yMitte - minimapPuffer) {
				deltaY = Math.max((yMitte - mePosY), maxYVerschiebung);
			}
			//alte Verschiebung wird ersetzt durch neue Verschiebung
			mapView.getTransforms().setAll(new Translate(deltaX * feldSize, deltaY * feldSize));
		}

	}

	public Level getLevel() {
		return level;
	}

	public double getAnsichtHoehe(){
		return  ansichtHoehe;
	}

	public double getAnsichtBreite(){
		return  ansichtBreite;
	}
}

