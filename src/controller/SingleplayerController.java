package controller;

import java.util.EnumSet;

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
public class SingleplayerController extends ModusController {
    // TODO: falls Exception abgefangen, zurück zur LevelPack

    private Situation situation;


	public SingleplayerController(SelectionController selectionController, String spielerName) {
		super(selectionController, spielerName, PlayerModus.SINGLEPLAYER);

		situation = null;
		System.out.println("singleplayer controller");
    }


    @Override
    public void onTick(Level level) {
        level.macheTick(situation, getTicks());
		if (situation == null) {
			setSituations(null);	// fuer Animation in der LevelAnsicht
		} else {
			setSituations(EnumSet.of(situation));
		}
		situation = null;
    }



	@Override
    public void addEventHandler(Scene levelScene) {
        levelScene.addEventHandler(KeyEvent.KEY_PRESSED, new LevelAnsichtHandler());
    }


    private class LevelAnsichtHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            KeyCode code = event.getCode();

            if (event.isShiftDown()) {
                switch (code) {
                    case LEFT:
                        situation = Situation.METALEFT;
                        break;
                    case RIGHT:
						situation = Situation.METARIGHT;
                        break;
                    case UP:
						situation = Situation.METAUP;
                        break;
                    case DOWN:
						situation = Situation.METADOWN;
                        break;
                }
            } else {
                switch (code) {
                    case LEFT:
						situation = Situation.LEFT;
                        break;
                    case RIGHT:
						situation = Situation.RIGHT;
                        break;
                    case UP:
						situation = Situation.UP;
                        break;
                    case DOWN:
						situation = Situation.DOWN;
                        break;
                }
            }
        }
    }
}
