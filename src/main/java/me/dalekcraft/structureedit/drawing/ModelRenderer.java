package me.dalekcraft.structureedit.drawing;

import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.util.texture.Texture;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.PropertyUtils;
import me.dalekcraft.structureedit.util.Tint;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.StringTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static com.jogamp.opengl.GL4bc.*;
import static me.dalekcraft.structureedit.drawing.SchematicRenderer.SCALE;

public final class ModelRenderer {

    private static final int TEXTURE_SIZE = 16;
    private static final long TICK_LENGTH = 50L;
    private static final Logger LOGGER = LogManager.getLogger(ModelRenderer.class);

    private ModelRenderer() {
        throw new UnsupportedOperationException();
    }

    public static void readBlockState(GL4bc gl, String namespacedId, CompoundTag properties) {
        Color tint = Tint.getTint(namespacedId, properties);
        JSONObject blockState = Assets.getBlockState(namespacedId);
        String propertiesString = "";
        try {
            propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties)).replace('{', '[').replace('}', ']').replace(':', '=').replace("\"", "");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
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
                    if (variants.has(variantName) && variants.get(variantName) instanceof JSONObject variant) {
                        sendBlockState(gl, variant, tint);
                        return;
                    } else if (variants.has(variantName) && variants.get(variantName) instanceof JSONArray variantArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject variant = variantArray.getJSONObject(0);
                        sendBlockState(gl, variant, tint);
                        return;
                    }
                }
            }
        } else if (blockState.has("multipart")) {
            JSONArray multipart = blockState.getJSONArray("multipart");
            for (Object partObject : multipart) {
                JSONObject part = (JSONObject) partObject;
                if (part.has("when")) {
                    JSONObject when = part.getJSONObject("when");
                    if (when.has("OR")) {
                        JSONArray or = when.getJSONArray("OR");
                        boolean contains = true;
                        for (Object orEntryObject : or) {
                            contains = true;
                            JSONObject orEntry = (JSONObject) orEntryObject;
                            Set<String> keySet = orEntry.keySet();
                            for (String state : keySet) {
                                List<String> values = Arrays.asList(orEntry.getString(state).split("\\|"));
                                if (properties.get(state) instanceof StringTag) {
                                    if (!values.contains(properties.getString(state))) {
                                        contains = false;
                                        break;
                                    }
                                } else if (properties.get(state) instanceof IntTag) {
                                    if (!values.contains(String.valueOf(properties.getInt(state)))) {
                                        contains = false;
                                        break;
                                    }
                                }
                            }
                            if (contains) {
                                break;
                            }
                        }
                        if (contains) {
                            if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                                sendBlockState(gl, apply, tint);
                            } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                                // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                                JSONObject apply = applyArray.getJSONObject(0);
                                sendBlockState(gl, apply, tint);
                            }
                        }
                    } else {
                        Set<String> keySet = when.keySet();
                        boolean contains = true;
                        for (String state : keySet) {
                            List<String> values = Arrays.asList(when.getString(state).split("\\|"));
                            if (properties.get(state) instanceof StringTag) {
                                if (!values.contains(properties.getString(state))) {
                                    contains = false;
                                    break;
                                }
                            } else if (properties.get(state) instanceof IntTag) {
                                if (!values.contains(String.valueOf(properties.getInt(state)))) {
                                    contains = false;
                                    break;
                                }
                            }
                        }
                        if (contains) {
                            if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                                sendBlockState(gl, apply, tint);
                            } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                                // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                                JSONObject apply = applyArray.getJSONObject(0);
                                sendBlockState(gl, apply, tint);
                            }
                        }
                    }
                } else {
                    if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                        sendBlockState(gl, apply, tint);
                    } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject apply = applyArray.getJSONObject(0);
                        sendBlockState(gl, apply, tint);
                    }
                }
            }
        }
    }

    private static void sendBlockState(GL4bc gl, @NotNull JSONObject jsonObject, Color tint) {
        String modelPath = jsonObject.getString("model");
        JSONObject model = Assets.getModel(modelPath);
        int x = 0;
        int y = 0;
        boolean uvlock = false;
        if (jsonObject.has("x")) {
            x = jsonObject.getInt("x");
        }
        if (jsonObject.has("y")) {
            y = jsonObject.getInt("y");
        }
        if (jsonObject.has("uvlock")) {
            uvlock = jsonObject.getBoolean("uvlock");
        }
        drawModel(gl, model, x, y, uvlock, tint);
    }

    private static void drawModel(@NotNull GL4bc gl, JSONObject model, int x, int y, boolean uvlock, Color tint) {
        gl.glPushMatrix();

        gl.glTranslated(0.5, 0.5, 0.5);
        gl.glRotatef(-y, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(-x, 1.0f, 0.0f, 0.0f);
        gl.glTranslated(-0.5, -0.5, -0.5);

        Map<String, String> textures = getTextures(model, new HashMap<>());

        JSONArray elements = getElements(model);
        if (elements != null) {
            for (Object elementObject : elements) {
                gl.glPushMatrix();

                JSONObject element = (JSONObject) elementObject;
                JSONArray from = element.getJSONArray("from");
                JSONArray to = element.getJSONArray("to");
                JSONObject rotation = element.has("rotation") ? element.getJSONObject("rotation") : null;
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
                boolean shade = !element.has("shade") || element.getBoolean("shade");

                double fromX = from.getDouble(0) / TEXTURE_SIZE;
                double fromY = from.getDouble(1) / TEXTURE_SIZE;
                double fromZ = from.getDouble(2) / TEXTURE_SIZE;
                double toX = to.getDouble(0) / TEXTURE_SIZE;
                double toY = to.getDouble(1) / TEXTURE_SIZE;
                double toZ = to.getDouble(2) / TEXTURE_SIZE;

                if (axis != null && origin != null) {
                    double originX = origin.getDouble(0) / TEXTURE_SIZE;
                    double originY = origin.getDouble(1) / TEXTURE_SIZE;
                    double originZ = origin.getDouble(2) / TEXTURE_SIZE;
                    gl.glTranslated(originX, originY, originZ);
                    double rescaleFactor = Math.sqrt(Math.pow(16.0, 2.0) + Math.pow(16.0, 2.0)) / TEXTURE_SIZE; // TODO Do not assume that the angle is 45.0.
                    switch (axis) {
                        case "x" -> {
                            gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
                            if (rescale) {
                                gl.glScaled(1.0, rescaleFactor, rescaleFactor);
                            }
                        }
                        case "y" -> {
                            gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                            if (rescale) {
                                gl.glScaled(rescaleFactor, 1.0, rescaleFactor);
                            }
                        }
                        case "z" -> {
                            gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
                            if (rescale) {
                                gl.glScaled(rescaleFactor, rescaleFactor, 1.0);
                            }
                        }
                    }
                    gl.glTranslated(-originX, -originY, -originZ);
                }

                if (shade) {
                    gl.glEnable(GL_LIGHTING); // enable lighting
                    gl.glEnable(GL_LIGHT1);
                }

                JSONObject faces = element.getJSONObject("faces");
                Set<String> faceSet = faces.keySet();
                for (String faceName : faceSet) {
                    JSONObject face = faces.getJSONObject(faceName);

                    JSONArray uv = face.has("uv") ? face.getJSONArray("uv") : null;
                    String faceTexture = face.has("texture") ? face.getString("texture").substring(1) : null;
                    String cullface = face.has("cullface") ? face.getString("cullface") : null; // TODO Implement culling.
                    int faceRotation = face.has("rotation") ? face.getInt("rotation") : 0;
                    int tintIndex = face.has("tintindex") ? face.getInt("tintindex") : -1;

                    if (tintIndex == -1) {
                        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    } else {
                        float[] components = tint.getComponents(null);
                        gl.glColor4f(components[0], components[1], components[2], components[3]);
                    }

                    Texture texture = Assets.getTexture(textures.getOrDefault(faceTexture, "minecraft:missing"));
                    texture.enable(gl);
                    texture.bind(gl);

                    double textureLeft = uv != null ? uv.getDouble(0) / TEXTURE_SIZE : switch (faceName) {
                        case "up", "down", "north", "south" -> fromX;
                        default -> fromZ;
                    };
                    double textureTop = uv != null ? uv.getDouble(1) / TEXTURE_SIZE : switch (faceName) {
                        case "up" -> fromZ;
                        case "down" -> SCALE - toZ;
                        default -> SCALE - toY;
                    };
                    double textureRight = uv != null ? uv.getDouble(2) / TEXTURE_SIZE : switch (faceName) {
                        case "up", "down", "north", "south" -> toX;
                        default -> toZ;
                    };
                    double textureBottom = uv != null ? uv.getDouble(3) / TEXTURE_SIZE : switch (faceName) {
                        case "up" -> toZ;
                        case "down" -> SCALE - fromZ;
                        default -> SCALE - fromY;
                    };

                    JSONObject fullAnimation = Assets.getAnimation(textures.getOrDefault(faceTexture, "minecraft:missing"));
                    if (fullAnimation != null) {
                        JSONObject animation = fullAnimation.getJSONObject("animation");
                        boolean interpolate = animation.has("interpolate") && animation.getBoolean("interpolate"); // TODO Interpolation.
                        int width = animation.has("width") ? animation.getInt("width") : TEXTURE_SIZE;
                        int height = animation.has("height") ? animation.getInt("height") : TEXTURE_SIZE;
                        int frametime = animation.has("frametime") ? animation.getInt("frametime") : 1;

                        int widthFactor = Math.abs(texture.getWidth() / width);
                        int heightFactor = Math.abs(texture.getHeight() / height);

                        JSONArray frames;
                        if (animation.has("frames")) {
                            frames = animation.getJSONArray("frames");
                        } else {
                            frames = new JSONArray();
                            for (int i = 0; i < heightFactor; i++) {
                                frames.put(i, i);
                            }
                        }

                        // Set all texture coordinates to the first frame
                        textureLeft /= widthFactor;
                        textureTop /= heightFactor;
                        textureRight /= widthFactor;
                        textureBottom /= heightFactor;

                        long currentTick = System.currentTimeMillis() / (TICK_LENGTH * frametime);
                        long index = (currentTick % (frames.length()));
                        Object frame = frames.get((int) index);
                        double frameDouble = 0.0;
                        if (frame instanceof Integer frameInt) {
                            frameDouble = (double) frameInt;
                        } else if (frame instanceof JSONObject frameObject) {
                            frameDouble = frameObject.getInt("index");
                            // TODO Implement the "time" tag.
                            int time = frameObject.has("time") ? frameObject.getInt("time") : frametime;
                        }

                        // Change to a frame in the animation
                        textureTop += frameDouble / heightFactor;
                        textureBottom += frameDouble / heightFactor;
                    }

                    for (int i = 0; i < faceRotation; i += 90) {
                        double temp = textureLeft;
                        textureLeft = SCALE - textureBottom;
                        textureBottom = textureRight;
                        textureRight = SCALE - textureTop;
                        textureTop = temp;
                    }

                    gl.glMatrixMode(GL_TEXTURE);
                    gl.glLoadIdentity();
                    gl.glTranslated(0.5, 0.5, 0.0);
                    gl.glRotated(faceRotation, 0.0, 0.0, 1.0);
                    if (uvlock) {
                        switch (faceName) {
                            case "up" -> {
                                if (x == 180) {
                                    gl.glRotated(y, 0.0, 0.0, 1.0);
                                } else {
                                    gl.glRotated(-y, 0.0, 0.0, 1.0);
                                }
                            }
                            case "down" -> {
                                if (x == 180) {
                                    gl.glRotated(-y, 0.0, 0.0, 1.0);
                                } else {
                                    gl.glRotated(y, 0.0, 0.0, 1.0);
                                }
                            }
                            default -> gl.glRotated(-x, 0.0, 0.0, 1.0);
                        }
                    }
                    gl.glScaled(1.0, -1.0, 1.0);
                    gl.glTranslated(-0.5, -0.5, 0.0);
                    gl.glMatrixMode(GL_MODELVIEW);

                    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                    gl.glEnable(GL_TEXTURE_2D);

                    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    gl.glEnable(GL_BLEND);

                    gl.glBegin(GL_QUADS);

                    switch (faceName) {
                        case "up" -> {
                            gl.glNormal3d(0.0f, 1.0f, 0.0f);
                            gl.glTexCoord2d(textureLeft, textureBottom);
                            gl.glVertex3d(fromX, toY, toZ);
                            gl.glTexCoord2d(textureRight, textureBottom);
                            gl.glVertex3d(toX, toY, toZ);
                            gl.glTexCoord2d(textureRight, textureTop);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glTexCoord2d(textureLeft, textureTop);
                            gl.glVertex3d(fromX, toY, fromZ);
                        }
                        case "down" -> {
                            gl.glNormal3d(0.0f, -1.0f, 0.0f);
                            gl.glTexCoord2d(textureLeft, textureBottom);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glTexCoord2d(textureRight, textureBottom);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glTexCoord2d(textureRight, textureTop);
                            gl.glVertex3d(toX, fromY, toZ);
                            gl.glTexCoord2d(textureLeft, textureTop);
                            gl.glVertex3d(fromX, fromY, toZ);
                        }
                        case "north" -> {
                            gl.glNormal3d(0.0f, 0.0f, -1.0f);
                            gl.glTexCoord2d(textureLeft, textureBottom);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glTexCoord2d(textureRight, textureBottom);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glTexCoord2d(textureRight, textureTop);
                            gl.glVertex3d(fromX, toY, fromZ);
                            gl.glTexCoord2d(textureLeft, textureTop);
                            gl.glVertex3d(toX, toY, fromZ);
                        }
                        case "south" -> {
                            gl.glNormal3d(0.0f, 0.0f, 1.0f);
                            gl.glTexCoord2d(textureLeft, textureBottom);
                            gl.glVertex3d(fromX, fromY, toZ); // bottom-left of the quad
                            gl.glTexCoord2d(textureRight, textureBottom);
                            gl.glVertex3d(toX, fromY, toZ); // bottom-right of the quad
                            gl.glTexCoord2d(textureRight, textureTop);
                            gl.glVertex3d(toX, toY, toZ); // top-right of the quad
                            gl.glTexCoord2d(textureLeft, textureTop);
                            gl.glVertex3d(fromX, toY, toZ); // top-left of the quad

                        }
                        case "west" -> {
                            gl.glNormal3d(-1.0f, 0.0f, 0.0f);
                            gl.glTexCoord2d(textureLeft, textureBottom);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glTexCoord2d(textureRight, textureBottom);
                            gl.glVertex3d(fromX, fromY, toZ);
                            gl.glTexCoord2d(textureRight, textureTop);
                            gl.glVertex3d(fromX, toY, toZ);
                            gl.glTexCoord2d(textureLeft, textureTop);
                            gl.glVertex3d(fromX, toY, fromZ);
                        }
                        case "east" -> {
                            gl.glNormal3d(1.0f, 0.0f, 0.0f);
                            gl.glTexCoord2d(textureLeft, textureBottom);
                            gl.glVertex3d(toX, fromY, toZ);
                            gl.glTexCoord2d(textureRight, textureBottom);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glTexCoord2d(textureRight, textureTop);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glTexCoord2d(textureLeft, textureTop);
                            gl.glVertex3d(toX, toY, toZ);
                        }
                    }
                    gl.glEnd();
                    texture.disable(gl);
                    gl.glDisable(GL_TEXTURE_2D);
                    gl.glDisable(GL_BLEND);
                }
                gl.glPopMatrix();
                gl.glDisable(GL_LIGHTING); // enable lighting
                gl.glDisable(GL_LIGHT1);
            }
        }
        gl.glPopMatrix();
    }

    @Nullable
    private static JSONArray getElements(@NotNull JSONObject model) {
        if (model.has("elements")) {
            return model.getJSONArray("elements");
        } else if (model.has("parent")) {
            return getElements(Assets.getModel(model.getString("parent")));
        } else {
            return null;
        }
    }

    @Contract("_, _ -> param2")
    private static Map<String, String> getTextures(@NotNull JSONObject model, Map<String, String> textures) {
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

    private static void getTextureFromId(@NotNull JSONObject model, Map<String, String> textures, String name) {
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
                        getTextureFromId(model, textures, substring);
                    } else if (textures.containsKey(substring)) {
                        textures.put(name, textures.get(substring));
                    } else if (parent != null) {
                        getTextureFromId(parent, textures, substring);
                    } else {
                        textures.put(substring, "minecraft:missing");
                    }
                } else if (!textures.containsKey(name) || textures.get(name).equals("minecraft:missing")) {
                    textures.put(name, path);
                }
            }
        }
        if (parent != null) {
            getTextureFromId(parent, textures, name);
        }
    }
}
