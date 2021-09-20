package me.eccentric_nz.tardisschematicviewer.drawing;

import me.eccentric_nz.tardisschematicviewer.util.Assets;
import me.eccentric_nz.tardisschematicviewer.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static me.eccentric_nz.tardisschematicviewer.drawing.SchematicRenderer.SCALE;
import static org.lwjgl.opengl.GL46.*;

public final class ModelRenderer {

    private ModelRenderer() {
        throw new UnsupportedOperationException();
    }

    public static void readBlockState(String namespacedId, CompoundTag properties) {
        JSONObject blockState = Assets.getBlockState(namespacedId);
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
                if (contains) {
                    if (variants.get(variantName) instanceof JSONObject variant) {
                        String modelPath = variant.getString("model");
                        JSONObject model = Assets.getModel(modelPath);
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
                        drawModel(model, x, y, uvlock);
                        return;
                    } else if (variants.get(variantName) instanceof JSONArray variantArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject variant = variantArray.getJSONObject(0);
                        String modelPath = variant.getString("model");
                        JSONObject model = Assets.getModel(modelPath);
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
                        drawModel(model, x, y, uvlock);
                        return;
                    }
                }
            }
        } else if (blockState.has("multipart")) { // TODO Multipart models.
            JSONArray multipart = blockState.getJSONArray("multipart");
            for (Object partObject : multipart) {
                JSONObject part = (JSONObject) partObject;
                if (part.has("when")) {
                    JSONObject when = part.getJSONObject("when");
                    if (when.has("OR")) {
                        JSONArray or = when.getJSONArray("OR");
                        for (Object orEntry : or) {
                            JSONObject jsonEntry = (JSONObject) orEntry;
                            Set<String> keySet = jsonEntry.keySet();
                            boolean contains = false;
                            for (String state : keySet) {
                                List<String> values = Arrays.asList(jsonEntry.getString(state).split("\\|"));
                                if (values.contains(properties.getString("state"))) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (contains) {
                                if (part.get("apply") instanceof JSONObject apply) {
                                    String modelPath = apply.getString("model");
                                    JSONObject model = Assets.getModel(modelPath);
                                    int x = 0;
                                    int y = 0;
                                    boolean uvlock = false;
                                    if (apply.has("x")) {
                                        x = apply.getInt("x");
                                    }
                                    if (apply.has("y")) {
                                        y = apply.getInt("y");
                                    }
                                    if (apply.has("uvlock")) {
                                        uvlock = apply.getBoolean("uvlock");
                                    }
                                    drawModel(model, x, y, uvlock);
                                } else if (part.get("apply") instanceof JSONArray applyArray) {
                                    // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                                    JSONObject apply = applyArray.getJSONObject(0);
                                    String modelPath = apply.getString("model");
                                    JSONObject model = Assets.getModel(modelPath);
                                    int x = 0;
                                    int y = 0;
                                    boolean uvlock = false;
                                    if (apply.has("x")) {
                                        x = apply.getInt("x");
                                    }
                                    if (apply.has("y")) {
                                        y = apply.getInt("y");
                                    }
                                    if (apply.has("uvlock")) {
                                        uvlock = apply.getBoolean("uvlock");
                                    }
                                    drawModel(model, x, y, uvlock);
                                }
                            }
                        }
                    } else {
                        Set<String> keySet = when.keySet();
                        boolean contains = false;
                        for (String state : keySet) {
                            List<String> values = Arrays.asList(when.getString(state).split("\\|"));
                            if (values.contains(properties.getString("state"))) {
                                contains = true;
                                break;
                            }
                        }
                        if (contains) {
                            if (part.get("apply") instanceof JSONObject apply) {
                                String modelPath = apply.getString("model");
                                JSONObject model = Assets.getModel(modelPath);
                                int x = 0;
                                int y = 0;
                                boolean uvlock = false;
                                if (apply.has("x")) {
                                    x = apply.getInt("x");
                                }
                                if (apply.has("y")) {
                                    y = apply.getInt("y");
                                }
                                if (apply.has("uvlock")) {
                                    uvlock = apply.getBoolean("uvlock");
                                }
                                drawModel(model, x, y, uvlock);
                            } else if (part.get("apply") instanceof JSONArray applyArray) {
                                // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                                JSONObject apply = applyArray.getJSONObject(0);
                                String modelPath = apply.getString("model");
                                JSONObject model = Assets.getModel(modelPath);
                                int x = 0;
                                int y = 0;
                                boolean uvlock = false;
                                if (apply.has("x")) {
                                    x = apply.getInt("x");
                                }
                                if (apply.has("y")) {
                                    y = apply.getInt("y");
                                }
                                if (apply.has("uvlock")) {
                                    uvlock = apply.getBoolean("uvlock");
                                }
                                drawModel(model, x, y, uvlock);
                            }
                        }
                    }
                } else {
                    if (part.get("apply") instanceof JSONObject apply) {
                        String modelPath = apply.getString("model");
                        JSONObject model = Assets.getModel(modelPath);
                        int x = 0;
                        int y = 0;
                        boolean uvlock = false;
                        if (apply.has("x")) {
                            x = apply.getInt("x");
                        }
                        if (apply.has("y")) {
                            y = apply.getInt("y");
                        }
                        if (apply.has("uvlock")) {
                            uvlock = apply.getBoolean("uvlock");
                        }
                        drawModel(model, x, y, uvlock);
                    } else if (part.get("apply") instanceof JSONArray applyArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject apply = applyArray.getJSONObject(0);
                        String modelPath = apply.getString("model");
                        JSONObject model = Assets.getModel(modelPath);
                        int x = 0;
                        int y = 0;
                        boolean uvlock = false;
                        if (apply.has("x")) {
                            x = apply.getInt("x");
                        }
                        if (apply.has("y")) {
                            y = apply.getInt("y");
                        }
                        if (apply.has("uvlock")) {
                            uvlock = apply.getBoolean("uvlock");
                        }
                        drawModel(model, x, y, uvlock);
                    }
                }
            }
        }
    }

    public static void drawModel(JSONObject model, int x, int y, boolean uvlock) {
        glTranslated(0.5, 0.5, 0.5);
        glRotatef(-y, 0.0f, 1.0f, 0.0f);
        glRotatef(-x, 1.0f, 0.0f, 0.0f);
        glTranslated(-0.5, -0.5, -0.5);

        Map<String, String> textures = getTextures(model, new HashMap<>());

        JSONArray elements = getElements(model);
        if (elements != null) {
            for (Object element : elements) {
                glPushMatrix();

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

                if (axis != null && origin != null) {
                    double originX = origin.getDouble(0) / 16.0;
                    double originY = origin.getDouble(1) / 16.0;
                    double originZ = origin.getDouble(2) / 16.0;
                    glTranslated(originX, originY, originZ);
                    switch (axis) {
                        case "x" -> glRotatef(angle, 1.0f, 0.0f, 0.0f);
                        case "y" -> glRotatef(angle, 0.0f, 1.0f, 0.0f);
                        case "z" -> glRotatef(angle, 0.0f, 0.0f, 1.0f);
                    }
                    glTranslated(-originX, -originY, -originZ);
                }

                if (shade) {
                    glEnable(GL_LIGHTING); // enable lighting
                    glEnable(GL_LIGHT1);
                }

                JSONObject faces = jsonElement.getJSONObject("faces");
                Set<String> faceSet = faces.keySet();
                for (String faceName : faceSet) {
                    JSONObject face = faces.getJSONObject(faceName);

                    JSONArray uv = face.has("uv") ? face.getJSONArray("uv") : null;
                    String faceTexture = face.has("texture") ? face.getString("texture").substring(1) : null;
                    String cullface = face.has("cullface") ? face.getString("cullface") : null; // TODO Implement culling.
                    int faceRotation = face.has("rotation") ? face.getInt("rotation") : 0;
                    int tintIndex = face.has("tintindex") ? face.getInt("tintindex") : -1;

                    if (tintIndex == -1) {
                        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    }

                    // TODO Fix sizes of textures on blocks what are not 16x16x16.
                    BufferedImage texture = Assets.getTexture(textures.getOrDefault(faceTexture, "custom:missing"));

                    double textureLeft = uv != null ? uv.getDouble(0) / texture.getWidth() : fromX;
                    double textureTop = uv != null ? uv.getDouble(1) / texture.getHeight() : (SCALE - toY) / texture.getHeight() * 16.0;
                    double textureRight = uv != null ? uv.getDouble(2) / texture.getWidth() : toX;
                    double textureBottom = uv != null ? uv.getDouble(3) / texture.getHeight() : (SCALE - fromY) / texture.getHeight() * 16.0;

                    for (int i = 0; i < faceRotation; i += 90) {
                        double temp = textureLeft;
                        textureLeft = SCALE - textureBottom;
                        textureBottom = textureRight;
                        textureRight = SCALE - textureTop;
                        textureTop = temp;
                    }

                    JSONObject animation = Assets.getAnimation(textures.get(faceTexture));
                    if (animation != null) {
                        int width = animation.has("width") ? animation.getInt("width") : 16;
                        int height = animation.has("height") ? animation.getInt("height") : 16;
                        int frameCount = Math.abs(texture.getHeight() / height);
                        // textureTop /= frameCount;
                        // textureBottom /= frameCount;
                    }

                    glMatrixMode(GL_TEXTURE);
                    glLoadIdentity();
                    glTranslated(0.5, 0.5, 0.0);
                    glRotated(faceRotation, 0.0, 0.0, 1.0);
                    if (uvlock) {
                        switch (faceName) {
                            case "up", "down" -> glRotated(-y, 0.0, 0.0, 1.0);
                            default -> glRotated(-x, 0.0, 0.0, 1.0);
                        }
                    }
                    glTranslated(-0.5, -0.5, 0.0);
                    glMatrixMode(GL_MODELVIEW);

                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                    glEnable(GL_TEXTURE_2D);

                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glEnable(GL_BLEND);

                    glBegin(GL_QUADS);

                    switch (faceName) {
                        case "up" -> {
                            glNormal3d(0.0f, 1.0f, 0.0f);
                            glTexCoord2d(textureLeft, SCALE - textureTop);
                            glVertex3d(fromX, toY, fromZ);
                            glTexCoord2d(textureRight, SCALE - textureTop);
                            glVertex3d(toX, toY, fromZ);
                            glTexCoord2d(textureRight, SCALE - textureBottom);
                            glVertex3d(toX, toY, toZ);
                            glTexCoord2d(textureLeft, SCALE - textureBottom);
                            glVertex3d(fromX, toY, toZ);
                        }
                        case "down" -> {
                            glNormal3d(0.0f, -1.0f, 0.0f);
                            glTexCoord2d(textureLeft, SCALE - textureBottom);
                            glVertex3d(fromX, fromY, fromZ);
                            glTexCoord2d(textureRight, SCALE - textureBottom);
                            glVertex3d(toX, fromY, fromZ);
                            glTexCoord2d(textureRight, SCALE - textureTop);
                            glVertex3d(toX, fromY, toZ);
                            glTexCoord2d(textureLeft, SCALE - textureTop);
                            glVertex3d(fromX, fromY, toZ);
                        }
                        case "north" -> {
                            glNormal3d(0.0f, 0.0f, -1.0f);
                            glTexCoord2d(textureRight, SCALE - textureBottom);
                            glVertex3d(fromX, fromY, fromZ);
                            glTexCoord2d(textureLeft, SCALE - textureBottom);
                            glVertex3d(toX, fromY, fromZ);
                            glTexCoord2d(textureLeft, SCALE - textureTop);
                            glVertex3d(toX, toY, fromZ);
                            glTexCoord2d(textureRight, SCALE - textureTop);
                            glVertex3d(fromX, toY, fromZ);
                        }
                        case "south" -> {
                            glNormal3d(0.0f, 0.0f, 1.0f);
                            glTexCoord2d(textureLeft, SCALE - textureBottom);
                            glVertex3d(fromX, fromY, toZ); // bottom-left of the quad
                            glTexCoord2d(textureRight, SCALE - textureBottom);
                            glVertex3d(toX, fromY, toZ); // bottom-right of the quad
                            glTexCoord2d(textureRight, SCALE - textureTop);
                            glVertex3d(toX, toY, toZ); // top-right of the quad
                            glTexCoord2d(textureLeft, SCALE - textureTop);
                            glVertex3d(fromX, toY, toZ); // top-left of the quad

                        }
                        case "west" -> {
                            glNormal3d(-1.0f, 0.0f, 0.0f);
                            glTexCoord2d(textureLeft, SCALE - textureBottom);
                            glVertex3d(fromX, fromY, fromZ);
                            glTexCoord2d(textureRight, SCALE - textureBottom);
                            glVertex3d(fromX, fromY, toZ);
                            glTexCoord2d(textureRight, SCALE - textureTop);
                            glVertex3d(fromX, toY, toZ);
                            glTexCoord2d(textureLeft, SCALE - textureTop);
                            glVertex3d(fromX, toY, fromZ);
                        }
                        case "east" -> {
                            glNormal3d(1.0f, 0.0f, 0.0f);
                            glTexCoord2d(textureRight, SCALE - textureBottom);
                            glVertex3d(toX, fromY, fromZ);
                            glTexCoord2d(textureLeft, SCALE - textureBottom);
                            glVertex3d(toX, fromY, toZ);
                            glTexCoord2d(textureLeft, SCALE - textureTop);
                            glVertex3d(toX, toY, toZ);
                            glTexCoord2d(textureRight, SCALE - textureTop);
                            glVertex3d(toX, toY, fromZ);
                        }
                    }
                    glEnd();
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_BLEND);
                }
                glPopMatrix();
                glDisable(GL_LIGHTING); // enable lighting
                glDisable(GL_LIGHT1);
            }
        }
    }

    private static JSONArray getElements(JSONObject model) {
        if (model.has("elements")) {
            return model.getJSONArray("elements");
        } else if (model.has("parent")) {
            return getElements(Assets.getModel(model.getString("parent")));
        } else {
            return null;
        }
    }

    private static Map<String, String> getTextures(JSONObject model, Map<String, String> textures) {
        if (model.has("textures")) {
            JSONObject json = model.getJSONObject("textures");
            Set<String> names = json.keySet();
            for (String name : names) {
                getTextureFromId(model, textures, name);
            }
        }
        if (model.has("parent")) {
            getTextures(Assets.getModel(model.getString("parent")), textures);
        }
        return textures;
    }

    private static void getTextureFromId(JSONObject model, Map<String, String> textures, String name) {
        JSONObject parent = null;
        if (model.has("parent")) {
            parent = Assets.getModel(model.getString("parent"));
        }
        if (model.has("textures")) {
            JSONObject texturesJson = model.getJSONObject("textures");
            if (texturesJson.has(name)) {
                String path = texturesJson.getString(name);
                if (path.startsWith("#")) {
                    String substring = path.substring(1);
                    if (texturesJson.has(substring)) {
                        getTextureFromId(model, textures, name);
                    } else if (textures.containsKey(substring)) {
                        textures.put(name, textures.get(substring));
                    } else if (parent != null) {
                        getTextureFromId(parent, textures, substring);
                    } else {
                        textures.put(substring, "custom:missing");
                    }
                } else if (!textures.containsKey(name) || textures.get(name).equals("custom:missing")) {
                    textures.put(name, path);
                }
            }
        }
        if (parent != null) {
            getTextureFromId(parent, textures, name);
        }
    }
}
