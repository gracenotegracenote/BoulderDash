package controller;


import java.util.EnumMap;
import java.util.Map;

import javafx.scene.image.Image;
import models.Feldinhalt;

/**
 * Created by Ella on 19/01/2017.
 * Klasse um alle Bilder gleich bei Erschaffen einer Anzeige zu Laden, sodass im Spiel keine Ladezeiten mehr auftreten
 * Der Path des Designs ist für jedes Level im Index enthalten und wird dem ImageLoader übergeben,
 * so kann der ImageLoader verschiedene Designs laden
 */
public class ImageLoader {
    private Map<Feldinhalt, Image> images, animatedImages;
    public final String OPEN_EXIT_FILE = "exitopen.gif";
    public final String MOVING_ME_FILE = "memove.gif";
	public final String MOVING_ME2_FILE = "memove2.gif";
	public final String MOVING_ME3_FILE = "memove3.gif";
	public final String MOVING_ME4_FILE = "memove4.gif";
    private String designPath;

    public ImageLoader(String designPath) {
        this.designPath = designPath;
        images = new EnumMap<>(Feldinhalt.class);
        animatedImages = new EnumMap<>(Feldinhalt.class);
        initialiseAnimatedImg();
        for (Feldinhalt inhalt : Feldinhalt.values()) {
            images.put(inhalt, new Image("file:" + designPath + inhalt.getImageUrl()));
        }
    }

    /**
     * Spukt zu einem Feldinhalt das Passende Bild aus der Map images aus
     * @param feldinhalt
     * @return Image
     */
    public Image getImages(Feldinhalt feldinhalt) {
        return images.get(feldinhalt);
    }

    public Image getAnimatedImages(Feldinhalt feldinhalt) {
        return animatedImages.get(feldinhalt);
    }

    private void initialiseAnimatedImg() {
        animatedImages.put(Feldinhalt.EXIT, new Image("file:" + designPath + OPEN_EXIT_FILE));
        animatedImages.put(Feldinhalt.ME, new Image("file:" + designPath + MOVING_ME_FILE));
		animatedImages.put(Feldinhalt.ME2, new Image("file:" + designPath + MOVING_ME2_FILE));
		animatedImages.put(Feldinhalt.ME3, new Image("file:" + designPath + MOVING_ME3_FILE));
		animatedImages.put(Feldinhalt.ME4, new Image("file:" + designPath + MOVING_ME4_FILE));
    }
}
