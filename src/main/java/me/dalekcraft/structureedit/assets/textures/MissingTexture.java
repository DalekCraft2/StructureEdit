package me.dalekcraft.structureedit.assets.textures;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.ui.MainController;

public final class MissingTexture extends WritableImage {

    public static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
    private static final MissingTexture INSTANCE;

    static {
        INSTANCE = new MissingTexture();
        PixelWriter writer = INSTANCE.getPixelWriter();
        for (int x = 0; x < INSTANCE.getWidth(); x++) {
            for (int y = 0; y < INSTANCE.getHeight(); y++) {
                boolean leftHalf = x < INSTANCE.getWidth() / 2;
                boolean topHalf = y < INSTANCE.getHeight() / 2;
                if (topHalf == leftHalf) {
                    writer.setColor(x, y, MainController.MISSING_COLOR);
                } else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }
    }

    private MissingTexture() {
        super(16, 16);
        // super();
    }

    public static MissingTexture getInstance() {
        return INSTANCE;
    }
}

