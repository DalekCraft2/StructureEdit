package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.Main;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

public class ModelReader {

    private static final Path ASSETS;

    static {
        ASSETS = Main.assets;
    }

    // TODO Read from Minecraft assets folder to draw block models.

    public JSONObject getAssetFile(String namespacedId, String folder) {
        String[] split = namespacedId.split(":");
        File file = new File(ASSETS.toString() + File.separator + split[0] + File.separator + folder + File.separator + split[1] + ".json");
        JSONObject assetJson = null;
        try (FileInputStream fileInputStream = new FileInputStream(file); InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8); StringWriter stringWriter = new StringWriter()) {
            char[] buffer = new char[1024 * 16];
            int length;
            while ((length = inputStreamReader.read(buffer)) > 0) {
                stringWriter.write(buffer, 0, length);
            }
            assetJson = new JSONObject(stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assetJson;
    }

    public void readBlockState(GL4bc gl, String namespacedId, CompoundTag properties) {
        JSONObject blockState = getAssetFile(namespacedId, "blockstates");
        String propertiesString = "";
        try {
            propertiesString = SNBTUtil.toSNBT(properties).replace('{', '[').replace('}', ']').replace(':', '=');
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (blockState.has("variants")) {
            JSONObject variants = blockState.getJSONObject("variants");
            Set<String> keySet = variants.keySet();
            for (String variantName : keySet) {
                String[] states = variantName.split(",");
                for (String state : states) {
                    if (propertiesString.contains(state)) {
                        if (variants.get(variantName) instanceof JSONObject variant) {
                            String modelPath = variant.getString("model");
                            JSONObject model = getAssetFile(modelPath, "models");
                            int x = 0;
                            int y = 0;
                            boolean uvlock = false;
                            if (variant.has("x")) {
                                x = variant.getInt("x");
                            }
                            if (variant.has("y")) {
                                y = variant.getInt("y");
                            }
                            if (variant.has("uvlock")) {
                                uvlock = variant.getBoolean("uvlock");
                            }
                            drawModel(gl, model, x, y, uvlock);
                        } else if (variants.get(variantName) instanceof JSONArray variantArray) {
                            Set<String> variantSet = variants.keySet();
                            // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        }
                    }
                }
            }
        } else if (blockState.has("multipart")) {
            // TODO Multipart models. I may have accidentally done how these work in the variants section.
        }
    }

    public void drawModel(GL4bc gl, JSONObject model, int x, int y, boolean uvlock) {
        gl.glRotatef(x, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(y, 0.0f, 1.0f, 0.0f);
        JSONArray elements = new JSONArray();
        if (!model.has("elements") && model.has("parent")) {
            elements = getAssetFile(model.getString("parent"), "models").getJSONArray("elements"); // TODO Make this recursive until a parent does not exist.
        } else if (model.has("elements")) {
            for (Object element : elements) {
                JSONObject jsonElement = (JSONObject) element;
                JSONArray from = jsonElement.getJSONArray("from");
                JSONArray to = jsonElement.getJSONArray("to");
                JSONObject rotation = jsonElement.getJSONObject("rotation");

                JSONArray origin = rotation.getJSONArray("origin");
                String axis = rotation.getString("axis");
                float angle = rotation.getFloat("angle");
                boolean rescale = rotation.getBoolean("rescale");

                boolean shade = jsonElement.getBoolean("shade");

                gl.glTranslatef(origin.getFloat(0), origin.getFloat(1), origin.getFloat(2));
                switch (axis) {
                    case "x":
                        gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
                    case "y":
                        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                    case "z":
                        gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
                }

                JSONObject faces = jsonElement.getJSONObject("faces");
                Set<String> faceSet = faces.keySet();
                for (String faceName : faceSet) {
                    JSONObject face = faces.getJSONObject(faceName);
                    switch (faceName) {
                        case "up":
                            gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                        case "down":
                            gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                        case "north":
                            gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        case "south":
                            gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                        case "west":
                            gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        case "east":
                            gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                    }

                    JSONArray uv = face.getJSONArray("uv");
                    String texture = face.getString("texture");
                    String cullface = face.getString("cullface");
                    int faceRotation = face.getInt("rotation");
                    int tintIndex = face.getInt("tintindex");



                    switch (faceName) {
                        case "up":
                            gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                        case "down":
                            gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                        case "north":
                            gl.glRotatef(-180.0f, 0.0f, 1.0f, 0.0f);
                        case "south":
                            gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                        case "west":
                            gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                        case "east":
                            gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    }
                }
            }
        }
        gl.glRotatef(-y, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(-x, 1.0f, 0.0f, 0.0f);
    }
}
