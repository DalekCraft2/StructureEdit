package me.dalekcraft.structureedit.drawing;

import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.util.texture.Texture;
import me.dalekcraft.structureedit.schematic.Schematic;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.PropertyUtils;
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
import static me.dalekcraft.structureedit.ui.UserInterface.SCALE;

public final class ModelRenderer {

    private static final double MODEL_SIZE = 16.0;
    private static final long TICK_LENGTH = 50L;
    private static final Logger LOGGER = LogManager.getLogger(ModelRenderer.class);

    @Contract(value = " -> fail", pure = true)
    private ModelRenderer() {
        throw new UnsupportedOperationException();
    }

    // TODO Add a water model to this list if the block's "waterlogged" property is "true".
    @NotNull
    public static List<JSONObject> getModelsFromBlockState(@NotNull Schematic.Block block, Random random) {
        List<JSONObject> modelList = new ArrayList<>();
        String namespacedId = block.getId();
        CompoundTag properties = block.getProperties().clone();
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
                        modelList.add(variant);
                        return modelList;
                    } else if (variants.has(variantName) && variants.get(variantName) instanceof JSONArray variantArray) {
                        JSONObject variant = chooseRandomModel(variantArray, random);
                        modelList.add(variant);
                        return modelList;
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
                                modelList.add(apply);
                            } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                                JSONObject apply = chooseRandomModel(applyArray, random);
                                modelList.add(apply);
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
                                modelList.add(apply);
                            } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                                JSONObject apply = chooseRandomModel(applyArray, random);
                                modelList.add(apply);
                            }
                        }
                    }
                } else {
                    if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                        modelList.add(apply);
                    } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                        JSONObject apply = chooseRandomModel(applyArray, random);
                        modelList.add(apply);
                    }
                }
            }
        }
        return modelList;
    }

    private static JSONObject chooseRandomModel(@NotNull JSONArray models, Random random) {
        int total = 0;
        NavigableMap<Integer, JSONObject> weightTree = new TreeMap<>();
        for (Object modelObject : models) {
            JSONObject model = (JSONObject) modelObject;
            int weight = model.has("weight") ? model.getInt("weight") : 1;
            if (weight <= 0) {
                continue;
            }
            total += weight;
            weightTree.put(total, model);
        }
        int value = random.nextInt(0, total) + 1;
        return weightTree.ceilingEntry(value).getValue();
    }

    public static void drawModel(@NotNull GL4bc gl, @NotNull JSONObject jsonObject, Color tint) {
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

                double fromX = from.getDouble(0) / MODEL_SIZE;
                double fromY = from.getDouble(1) / MODEL_SIZE;
                double fromZ = from.getDouble(2) / MODEL_SIZE;
                double toX = to.getDouble(0) / MODEL_SIZE;
                double toY = to.getDouble(1) / MODEL_SIZE;
                double toZ = to.getDouble(2) / MODEL_SIZE;

                if (axis != null && origin != null) {
                    double originX = origin.getDouble(0) / MODEL_SIZE;
                    double originY = origin.getDouble(1) / MODEL_SIZE;
                    double originZ = origin.getDouble(2) / MODEL_SIZE;
                    gl.glTranslated(originX, originY, originZ);
                    double rescaleFactor = Math.hypot(MODEL_SIZE, MODEL_SIZE) / MODEL_SIZE; // TODO Do not assume that the angle is 45.0 degrees, nor that the cube is centered.
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

                    double textureLeft = uv != null ? uv.getDouble(0) / MODEL_SIZE : switch (faceName) {
                        case "up", "down", "north", "south" -> fromX;
                        default -> fromZ;
                    };
                    double textureTop = uv != null ? uv.getDouble(1) / MODEL_SIZE : switch (faceName) {
                        case "up" -> fromZ;
                        case "down" -> SCALE - toZ;
                        default -> SCALE - toY;
                    };
                    double textureRight = uv != null ? uv.getDouble(2) / MODEL_SIZE : switch (faceName) {
                        case "up", "down", "north", "south" -> toX;
                        default -> toZ;
                    };
                    double textureBottom = uv != null ? uv.getDouble(3) / MODEL_SIZE : switch (faceName) {
                        case "up" -> toZ;
                        case "down" -> SCALE - fromZ;
                        default -> SCALE - fromY;
                    };

                    JSONObject fullAnimation = Assets.getAnimation(textures.getOrDefault(faceTexture, "minecraft:missing"));
                    if (fullAnimation != null) {
                        JSONObject animation = fullAnimation.getJSONObject("animation");
                        boolean interpolate = animation.has("interpolate") && animation.getBoolean("interpolate"); // TODO Implement interpolation.
                        int width = animation.has("width") ? animation.getInt("width") : texture.getWidth();
                        int height = animation.has("height") ? animation.getInt("height") : texture.getWidth();
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

                        // Change to the current frame in the animation
                        textureTop += frameDouble / heightFactor;
                        textureBottom += frameDouble / heightFactor;
                    } else if (texture.getWidth() != texture.getHeight()) {
                        texture = Assets.getTexture("minecraft:missing");
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
                    texture.enable(gl);
                    texture.bind(gl);

                    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    gl.glEnable(GL_BLEND);

                    gl.glAlphaFunc(GL_GREATER, 0.0f);
                    gl.glEnable(GL_ALPHA_TEST);

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
                    gl.glDisable(GL_ALPHA_TEST);
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

    @NotNull
    public static Color getTint(@NotNull Schematic.Block block) {
        String namespacedId = block.getId();
        CompoundTag properties = block.getProperties();
        switch (namespacedId) {
            case "minecraft:redstone_wire" -> {
                int power = 0;
                if (properties.containsKey("power") && properties.get("power") instanceof IntTag intTag) {
                    power = intTag.asInt();
                } else if (properties.containsKey("power") && properties.get("power") instanceof StringTag stringTag) {
                    try {
                        power = Integer.parseInt(stringTag.getValue());
                    } catch (NumberFormatException ignored) {
                    }
                }
                switch (power) {
                    case 1 -> {
                        return Color.decode("#6F0000");
                    }
                    case 2 -> {
                        return Color.decode("#790000");
                    }
                    case 3 -> {
                        return Color.decode("#820000");
                    }
                    case 4 -> {
                        return Color.decode("#8C0000");
                    }
                    case 5 -> {
                        return Color.decode("#970000");
                    }
                    case 6 -> {
                        return Color.decode("#A10000");
                    }
                    case 7 -> {
                        return Color.decode("#AB0000");
                    }
                    case 8 -> {
                        return Color.decode("#B50000");
                    }
                    case 9 -> {
                        return Color.decode("#BF0000");
                    }
                    case 10 -> {
                        return Color.decode("#CA0000");
                    }
                    case 11 -> {
                        return Color.decode("#D30000");
                    }
                    case 12 -> {
                        return Color.decode("#DD0000");
                    }
                    case 13 -> {
                        return Color.decode("#E70600");
                    }
                    case 14 -> {
                        return Color.decode("#F11B00");
                    }
                    case 15 -> {
                        return Color.decode("#FC3100");
                    }
                    default -> { // 0
                        return Color.decode("#4B0000");
                    }
                }
            }
            case "minecraft:grass_block", "minecraft:grass", "minecraft:tall_grass", "minecraft:fern", "minecraft:large_fern", "minecraft:potted_fern", "minecraft:sugar_cane" -> {
                return Color.decode("#91BD59");
            }
            case "minecraft:oak_leaves", "minecraft:dark_oak_leaves", "minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:vine" -> {
                return Color.decode("#77AB2F");
            }
            case "minecraft:water", "minecraft:water_cauldron" -> {
                return Color.decode("#3F76E4");
            }
            case "minecraft:birch_leaves" -> {
                return Color.decode("#80A755");
            }
            case "minecraft:spruce_leaves" -> {
                return Color.decode("#619961");
            }
            case "minecraft:lily_pad" -> {
                return Color.decode("#208030");
            }
            default -> {
                return new Color(255, 255, 255, 255);
            }
        }
    }
}
