package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.Main;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

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
        String blockName = namespacedId.split(":")[1].toUpperCase(Locale.ROOT);
        Block block = Block.valueOf(blockName);
        Color color = block.getColor();
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
                            drawModel(gl, model, x, y, uvlock, color);
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

    public void drawModel(GL4bc gl, JSONObject model, int x, int y, boolean uvlock, Color color) {
        float[] components = color.getComponents(null);

        if (components[3] == 0) {
            components[3] = 255;
            gl.glLineWidth(2.0f);
            gl.glBegin(GL_LINES);
        } else {
            gl.glBegin(GL_QUADS);
        }

        gl.glRotatef(x, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(y, 0.0f, 1.0f, 0.0f);

        JSONArray elements = null;
        if (!model.has("elements") && model.has("parent")) {
            //elements = getAssetFile(model.getString("parent"), "models").getJSONArray("elements"); // TODO Make this recursive until a parent does not exist.
        } else if (model.has("elements")) {
            elements = model.getJSONArray("elements");
        }
        if (elements != null) {
            for (Object element : elements) {
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

                if (origin != null) {
                    gl.glTranslatef(origin.getFloat(0) / 16.0f, origin.getFloat(1) / 16.0f, origin.getFloat(2) / 16.0f);
                }
                if (axis != null) {
                    switch (axis) {
                        case "x":
                            gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
                        case "y":
                            gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                        case "z":
                            gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
                    }
                }

                int sizeX = (from.getInt(0) - to.getInt(0)) / 16;
                int sizeY = (from.getInt(1) - to.getInt(1)) / 16;
                int sizeZ = (from.getInt(2) - to.getInt(2)) / 16;

                JSONObject faces = jsonElement.getJSONObject("faces");
                Set<String> faceSet = faces.keySet();
                for (String faceName : faceSet) {
                    JSONObject face = faces.getJSONObject(faceName);

                    JSONArray uv = face.has("uv") ? face.getJSONArray("uv") : null;
                    String texture = face.has("texture") ? face.getString("texture") : null;
                    String cullface = face.has("cullface") ? face.getString("cullface") : null;
                    int faceRotation = face.has("rotation") ? face.getInt("rotation") : 0;
                    int tintIndex = face.has("tintindex") ? face.getInt("tintindex") : -1;

                    // Set color
                    gl.glColor4f(components[0], components[1], components[2], components[3]);

                    switch (faceName) {
                        case "up" -> {
                            gl.glNormal3f(0.0f, 1.0f, 0.0f);
                            gl.glVertex3f(-sizeX, sizeY, -sizeZ);
                            gl.glVertex3f(-sizeX, sizeY, sizeZ);
                            gl.glVertex3f(sizeX, sizeY, sizeZ);
                            gl.glVertex3f(sizeX, sizeY, -sizeZ);
                        }
                        case "down" -> {
                            gl.glNormal3f(0.0f, -1.0f, 0.0f);
                            gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
                            gl.glVertex3f(sizeX, -sizeY, -sizeZ);
                            gl.glVertex3f(sizeX, -sizeY, sizeZ);
                            gl.glVertex3f(-sizeX, -sizeY, sizeZ);
                        }
                        case "north" -> {
                            gl.glNormal3f(0.0f, 0.0f, -1.0f);
                            gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
                            gl.glVertex3f(-sizeX, sizeY, -sizeZ);
                            gl.glVertex3f(sizeX, sizeY, -sizeZ);
                            gl.glVertex3f(sizeX, -sizeY, -sizeZ);
                        }
                        case "south" -> {
                            gl.glNormal3f(0.0f, 0.0f, 1.0f);
                            gl.glVertex3f(-sizeX, -sizeY, sizeZ); // bottom-left of the quad
                            gl.glVertex3f(sizeX, -sizeY, sizeZ); // bottom-right of the quad
                            gl.glVertex3f(sizeX, sizeY, sizeZ); // top-right of the quad
                            gl.glVertex3f(-sizeX, sizeY, sizeZ); // top-left of the quad
                        }
                        case "west" -> {
                            gl.glNormal3f(-1.0f, 0.0f, 0.0f);
                            gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
                            gl.glVertex3f(-sizeX, -sizeY, sizeZ);
                            gl.glVertex3f(-sizeX, sizeY, sizeZ);
                            gl.glVertex3f(-sizeX, sizeY, -sizeZ);
                        }
                        case "east" -> {
                            gl.glNormal3f(1.0f, 0.0f, 0.0f);
                            gl.glVertex3f(sizeX, -sizeY, -sizeZ);
                            gl.glVertex3f(sizeX, sizeY, -sizeZ);
                            gl.glVertex3f(sizeX, sizeY, sizeZ);
                            gl.glVertex3f(sizeX, -sizeY, sizeZ);
                        }
                    }
                }
            }
        }
        gl.glRotatef(-y, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(-x, 1.0f, 0.0f, 0.0f);

        gl.glEnd();
    }
}
