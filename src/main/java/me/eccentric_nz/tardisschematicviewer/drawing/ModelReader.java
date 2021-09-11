package me.eccentric_nz.tardisschematicviewer.drawing;

import org.json.JSONObject;

public class ModelReader {

    private JSONObject modelJson;

    public ModelReader(JSONObject modelJson) {
        this.modelJson = modelJson;
    }

    // TODO Read from Minecraft assets folder to draw block models.
}
