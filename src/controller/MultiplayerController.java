package controller;

import java.util.EnumSet;
import java.util.Set;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import models.Level;
import models.PlayerModus;
import models.Situation;

/**
 * Author: Liudmila Kachurina (https://github.com/gracenotegracenote)
 * Date: 11-Feb-17
 */
public class MultiplayerController extends ModusController {
	// TODO: falls Exception abgefangen, zurück zur LevelPack

	private Set<Situation> situations;


	public MultiplayerController(SelectionController selectionController, String spielerName) {
		super(selectionController, spielerName, PlayerModus.MULTIPLAYER);

		situations = EnumSet.noneOf(Situation.class);
		System.out.println("multiplayer controller");
	}


	@Override
	public void onTick(Level level) {
		level.macheTick(situations, getTicks());
		setSituations(situations);	// fuer Animation in der LevelAnsicht
		situations = EnumSet.noneOf(Situation.class);
	}


	@Override
	public void addEventHandler(Scene levelScene) {
		levelScene.addEventHandler(KeyEvent.KEY_PRESSED, new LevelAnsichtHandler());
	}


	private class LevelAnsichtHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			KeyCode code = event.getCode();

			checkPlayer(event, code);	// TODO: immer alle checken oder nur bis code gefunden wird ?
			checkPlayer2(event, code);
			checkPlayer3(event, code);
			checkPlayer4(event, code);
		}
	}


	// TODO: METAsituations bei Spielern 3 und 4 korrekt implementieren
	private void checkPlayer(KeyEvent event, KeyCode code) {
		if (event.isShiftDown()) {
			switch (code) {
				case LEFT:
					situations.add(Situation.METALEFT);
					break;
				case RIGHT:
					situations.add(Situation.METARIGHT);
					break;
				case UP:
					situations.add(Situation.METAUP);
					break;
				case DOWN:
					situations.add(Situation.METADOWN);
					break;
			}
		} else {
			switch (code) {
				case LEFT:
					situations.add(Situation.LEFT);
					break;
				case RIGHT:
					situations.add(Situation.RIGHT);
					break;
				case UP:
					situations.add(Situation.UP);
					break;
				case DOWN:
					situations.add(Situation.DOWN);
					break;
			}
		}
	}

	// control statt shift
	private void checkPlayer2(KeyEvent event, KeyCode code) {
		if (event.isControlDown()) {
			switch (code) {
				case A:
					situations.add(Situation.METALEFT2);
					break;
				case D:
					situations.add(Situation.METARIGHT2);
					break;
				case W:
					situations.add(Situation.METAUP2);
					break;
				case S:
					situations.add(Situation.METADOWN2);
					break;
			}
		} else {
			switch (code) {
				case A:
					situations.add(Situation.LEFT2);
					break;
				case D:
					situations.add(Situation.RIGHT2);
					break;
				case W:
					situations.add(Situation.UP2);
					break;
				case S:
					situations.add(Situation.DOWN2);
					break;
			}
		}
	}


	private BooleanProperty spacePressed = new SimpleBooleanProperty(false);
	private BooleanProperty leftPressed = new SimpleBooleanProperty(false);
	private BooleanProperty rightPressed = new SimpleBooleanProperty(false);
	private BooleanProperty upPressed = new SimpleBooleanProperty(false);
	private BooleanProperty downPressed = new SimpleBooleanProperty(false);

	private void checkPlayer3(KeyEvent event, KeyCode code) {
		BooleanBinding spaceAndLeft = spacePressed.and(leftPressed);
		spaceAndLeft.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METALEFT3);
		});

		BooleanBinding spaceAndRight = spacePressed.and(rightPressed);
		spaceAndRight.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METARIGHT3);
		});

		BooleanBinding spaceAndUp = spacePressed.and(upPressed);
		spaceAndUp.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METAUP3);
		});

		BooleanBinding spaceAndDown = spacePressed.and(downPressed);
		spaceAndDown.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METADOWN3);
		});

		switch (code) {
			case J:
				situations.add(Situation.LEFT3);
				leftPressed.setValue(true);
				break;
			case L:
				situations.add(Situation.RIGHT3);
				rightPressed.setValue(true);
				break;
			case I:
				situations.add(Situation.UP3);
				upPressed.setValue(true);
				break;
			case K:
				situations.add(Situation.DOWN3);
				downPressed.setValue(true);
				break;
			case SPACE:
				spacePressed.setValue(true);
		}

	}

	private void checkPlayer4(KeyEvent event, KeyCode code) {
		BooleanProperty plusPressed = new SimpleBooleanProperty(false);
		BooleanProperty leftPressed = new SimpleBooleanProperty(false);
		BooleanProperty rightPressed = new SimpleBooleanProperty(false);
		BooleanProperty upPressed = new SimpleBooleanProperty(false);
		BooleanProperty downPressed = new SimpleBooleanProperty(false);

		BooleanBinding plusAndLeft = plusPressed.and(leftPressed);
		plusAndLeft.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METALEFT4);
		});

		BooleanBinding plusAndRight = plusPressed.and(rightPressed);
		plusAndRight.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METARIGHT4);
		});

		BooleanBinding plusAndUp = plusPressed.and(upPressed);
		plusAndUp.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METAUP4);
		});

		BooleanBinding plusAndDown = plusPressed.and(downPressed);
		plusAndDown.addListener((observable, oldValue, newValue) -> {
			situations.add(Situation.METADOWN4);
		});

		switch (code) {
			case NUMPAD4:
				situations.add(Situation.LEFT4);
				leftPressed.setValue(true);

				break;
			case NUMPAD6:
				situations.add(Situation.RIGHT4);
				rightPressed.setValue(true);
				break;
			case NUMPAD8:
				situations.add(Situation.UP4);
				upPressed.setValue(true);
				break;
			case NUMPAD2:
				situations.add(Situation.DOWN4);
				downPressed.setValue(true);
				break;
			case ENTER:
				plusPressed.setValue(true);
				break;
		}
	}
}
