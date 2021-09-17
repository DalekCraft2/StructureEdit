package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.Main;
import me.eccentric_nz.tardisschematicviewer.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static me.eccentric_nz.tardisschematicviewer.drawing.SchematicRenderer.SCALE;

public class ModelReader {

    private static final Path ASSETS;
    private static final Map<String, JSONObject> BLOCK_STATES = new HashMap<>();
    private static final Map<String, JSONObject> MODELS = new HashMap<>();

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.
    static {
        ASSETS = Main.assets;
        for (Block block : Block.values()) {
            String namespacedId = "minecraft:" + block.name().toLowerCase(Locale.ROOT);
            JSONObject asset = getAssetFile(namespacedId, "blockstates");
            BLOCK_STATES.put(namespacedId, asset);
        }
    }

    // TODO Read from Minecraft assets folder to draw block models.

    public static JSONObject getAssetFile(String namespacedId, String folder) {
        if (folder.equals("blockstates") && BLOCK_STATES.containsKey(namespacedId)) {
            return BLOCK_STATES.get(namespacedId);
        }
        if (folder.equals("models") && MODELS.containsKey(namespacedId)) {
            return MODELS.get(namespacedId);
        }

        String[] split = namespacedId.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String id = split.length > 1 ? split[1] : split[0];
        File file = new File(ASSETS.toString() + File.separator + namespace + File.separator + folder + File.separator + id + ".json");
        System.out.println("Getting asset from " + file.getAbsolutePath());
        JSONObject assetJson = null;
        try (FileInputStream fileInputStream = new FileInputStream(file); InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8); StringWriter stringWriter = new StringWriter()) {
            char[] buffer = new char[1024 * 16];
            int length;
            while ((length = inputStreamReader.read(buffer)) > 0) {
                stringWriter.write(buffer, 0, length);
            }
            assetJson = new JSONObject(stringWriter.toString());
            if (folder.equals("blockstates")) {
                BLOCK_STATES.put(namespacedId, assetJson);
            } else if (folder.equals("models")) {
                MODELS.put(namespacedId, assetJson);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return assetJson;
    }

    public static void readBlockState(GL4bc gl, String namespacedId, CompoundTag properties) {
        String blockName = namespacedId.split(":")[1].toUpperCase(Locale.ROOT);
        Block block = Block.valueOf(blockName);
        Color color = block.getColor();
        JSONObject blockState = getAssetFile(namespacedId, "blockstates");
        String propertiesString = "";
        try {
            propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties)).replace('{', '[').replace('}', ']').replace(':', '=');
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (blockState.has("variants")) {
            JSONObject variants = blockState.getJSONObject("variants");
            Set<String> keySet = variants.keySet();
            for (String variantName : keySet) {
                String[] states = variantName.split(",");
                boolean contains = true;
                for (String state : states) {
                    if (!propertiesString.contains(state)) {
                        contains = false;
                        break;
                    }
                }
                if (!contains) {
                } else {
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
                        drawModel(gl, model, x, y, uvlock, color);
                        return;
                    } else if (variants.get(variantName) instanceof JSONArray variantArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject variant = variantArray.getJSONObject(0);
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
                        drawModel(gl, model, x, y, uvlock, color);
                        return;
                    }
                }
            }
        } else if (blockState.has("multipart")) {
            // TODO Multipart models.
        }
    }

    public static void drawModel(GL4bc gl, JSONObject model, int x, int y, boolean uvlock, Color color) {
        float[] components = color.getComponents(null);

        switch (y) {
            case 90 -> {
                gl.glTranslatef(SCALE, 0.0f, 0.0f);
                gl.glRotatef(180, 0.0f, 1.0f, 0.0f);
            }
            case 180 -> gl.glTranslatef(SCALE, 0.0f, SCALE);
            case 270 -> {
                gl.glTranslatef(0.0f, 0.0f, SCALE);
                gl.glRotatef(180, 0.0f, 1.0f, 0.0f);
            }

        }
        gl.glRotatef(y, 0.0f, 1.0f, 0.0f);
        switch (x) {
            case 90 -> gl.glTranslatef(0.0f, 0.0f, SCALE);
            case 180 -> gl.glTranslatef(0.0f, SCALE, SCALE);
            case 270 -> gl.glTranslatef(0.0f, SCALE, 0.0f);
        }
        gl.glRotatef(-x, 1.0f, 0.0f, 0.0f);

        // Set color
        gl.glColor4f(components[0], components[1], components[2], components[3]);

        JSONArray elements = getElements(model);
        if (elements != null) {
            for (Object element : elements) {
                gl.glPushMatrix();

                JSONObject jsonElement = (JSONObject) element;
                JSONArray from = jsonElement.getJSONArray("from");
                JSONArray to = jsonElement.getJSONArray("to");
                JSONObject rotation = jsonElement.has("rotation") ? jsonElement.getJSONObject("rotation") : null;
                JSONArray origin = null;
                String axis = null;
                float angle = 0.0f;
                boolean rescale = false;
                if (rotation != null) {
                    origin = rotation.getJSONArray("origin");
                    axis = rotation.getString("axis");
                    angle = rotation.has("angle") ? rotation.getFloat("angle") : 0.0f;
                    rescale = rotation.has("rescale") && rotation.getBoolean("rescale");
                }
                boolean shade = !jsonElement.has("shade") || jsonElement.getBoolean("shade");

                double fromX = from.getDouble(0) / 16.0;
                double fromY = from.getDouble(1) / 16.0;
                double fromZ = from.getDouble(2) / 16.0;
                double toX = to.getDouble(0) / 16.0;
                double toY = to.getDouble(1) / 16.0;
                double toZ = to.getDouble(2) / 16.0;

                // TODO Make these rotate about the specified origin. God help me.
                if (axis != null) {
                    switch (axis) {
                        case "x" -> gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
                        case "y" -> gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                        case "z" -> gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
                    }
                }

                if (components[3] == 0) {
                    components[3] = 255;
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL_LINES);
                } else {
                    gl.glBegin(GL_QUADS);
                }

                JSONObject faces = jsonElement.getJSONObject("faces");
                Set<String> faceSet = faces.keySet();
                for (String faceName : faceSet) {
                    JSONObject face = faces.getJSONObject(faceName);

                    JSONArray uv = face.has("uv") ? face.getJSONArray("uv") : null;
                    String texture = face.has("texture") ? face.getString("texture") : null;
                    String cullface = face.has("cullface") ? face.getString("cullface") : null;
                    int faceRotation = face.has("rotation") ? face.getInt("rotation") : 0;
                    int tintIndex = face.has("tintindex") ? face.getInt("tintindex") : -1;

                    switch (faceName) {
                        case "up" -> {
                            gl.glNormal3d(0.0f, 1.0f, 0.0f);
                            gl.glVertex3d(fromX, toY, fromZ);
                            gl.glVertex3d(fromX, toY, toZ);
                            gl.glVertex3d(toX, toY, toZ);
                            gl.glVertex3d(toX, toY, fromZ);
                        }
                        case "down" -> {
                            gl.glNormal3d(0.0f, -1.0f, 0.0f);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glVertex3d(toX, fromY, toZ);
                            gl.glVertex3d(fromX, fromY, toZ);
                        }
                        case "north" -> {
                            gl.glNormal3d(0.0f, 0.0f, -1.0f);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glVertex3d(fromX, toY, fromZ);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glVertex3d(toX, fromY, fromZ);
                        }
                        case "south" -> {
                            gl.glNormal3d(0.0f, 0.0f, 1.0f);
                            gl.glVertex3d(fromX, fromY, toZ); // bottom-left of the quad
                            gl.glVertex3d(toX, fromY, toZ); // bottom-right of the quad
                            gl.glVertex3d(toX, toY, toZ); // top-right of the quad
                            gl.glVertex3d(fromX, toY, toZ); // top-left of the quad
                        }
                        case "west" -> {
                            gl.glNormal3d(-1.0f, 0.0f, 0.0f);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glVertex3d(fromX, fromY, toZ);
                            gl.glVertex3d(fromX, toY, toZ);
                            gl.glVertex3d(fromX, toY, fromZ);
                        }
                        case "east" -> {
                            gl.glNormal3d(1.0f, 0.0f, 0.0f);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glVertex3d(toX, toY, toZ);
                            gl.glVertex3d(toX, fromY, toZ);
                        }
                    }
                }
                gl.glEnd();
                gl.glPopMatrix();
            }
        }
    }

    private static JSONArray getElements(JSONObject model) {
        if (model.has("elements")) {
            return model.getJSONArray("elements");
        } else if (model.has("parent")) {
            return getElements(getAssetFile(model.getString("parent"), "models"));
        } else {
            return new JSONArray();
        }
    }
}
