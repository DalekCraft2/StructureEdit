package me.eccentric_nz.tardisschematicviewer.drawing;

import me.eccentric_nz.tardisschematicviewer.Main;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.Set;

public class ModelReader {

    private static Path assets;

    static {
        assets = Main.assets;
    }

    // TODO Read from Minecraft assets folder to draw block models.

    public JSONObject getBlockState() {
        return null;
    }

    public JSONObject readBlockState(JSONObject modelJson) {
        if (modelJson.has("variants")) {
            JSONObject variants = modelJson.getJSONObject("variants");
            Set<String> keySet = variants.keySet();

        }
        return null;
    }

    public void drawModel(JSONObject model) {
    }
}
