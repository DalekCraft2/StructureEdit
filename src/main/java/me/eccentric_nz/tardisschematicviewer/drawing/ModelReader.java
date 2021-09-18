package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
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
import java.util.List;
import java.util.*;

import static com.jogamp.opengl.GL4bc.GL_QUADS;
import static me.eccentric_nz.tardisschematicviewer.drawing.SchematicRenderer.SCALE;

public class ModelReader {

    private static final Path ASSETS;
    private static final Map<String, JSONObject> BLOCK_STATES = new HashMap<>();
    private static final Map<String, JSONObject> MODELS = new HashMap<>();
    private static final Map<String, Texture> TEXTURES = new HashMap<>();

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.
    static {
        ASSETS = Main.assets;
        for (Block block : Block.values()) {
            String namespacedId = "minecraft:" + block.name().toLowerCase(Locale.ROOT);
            JSONObject blockState = getBlockState(namespacedId);
            BLOCK_STATES.put(namespacedId, blockState);
        }
        try {
            BLOCK_STATES.put("custom:missing", toJson(Main.class.getClassLoader().getResourceAsStream("assets/custom/blockstates/missing.json")));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
            MODELS.put("custom:block/missing", toJson(Main.class.getClassLoader().getResourceAsStream("assets/custom/models/block/missing.json")));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
            TEXTURES.put("custom:missing", TextureIO.newTexture(Main.class.getClassLoader().getResourceAsStream("assets/custom/textures/missing.png"), true, "png"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // TODO Read from Minecraft assets folder to draw block models.

    public static File getAsset(String namespacedId, String folder, String extension) {
        String[] split = namespacedId.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String id = split.length > 1 ? split[1] : split[0];
        File file = new File(ASSETS.toString() + File.separator + namespace + File.separator + folder + File.separator + id + "." + extension);
        System.out.println("Getting asset from \"" + file.getAbsolutePath() + "\"");
        return file;
    }

    public static JSONObject getBlockState(String namespacedId) {
        if (BLOCK_STATES.containsKey(namespacedId)) {
            return BLOCK_STATES.get(namespacedId);
        }
        JSONObject blockState;
        try {
            blockState = toJson(namespacedId, "blockstates", "json");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            blockState = BLOCK_STATES.get("custom:missing");
        }
        BLOCK_STATES.put(namespacedId, blockState);
        return blockState;
    }

    public static JSONObject getModel(String namespacedId) {
        if (MODELS.containsKey(namespacedId)) {
            return MODELS.get(namespacedId);
        }
        JSONObject model;
        try {
            model = toJson(namespacedId, "models", "json");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            model = MODELS.get("custom:block/missing");
        }
        MODELS.put(namespacedId, model);
        return model;
    }

    public static Texture getTexture(String namespacedId) {
        if (TEXTURES.containsKey(namespacedId)) {
            return TEXTURES.get(namespacedId);
        }
        Texture texture = null;
        try (InputStream inputStream = new FileInputStream(getAsset(namespacedId, "textures", "png"))) {
            texture = TextureIO.newTexture(inputStream, true, TextureIO.PNG);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("assets/custom/textures/missing.png")) {
                texture = TextureIO.newTexture(inputStream, true, TextureIO.PNG);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        TEXTURES.put(namespacedId, texture);
        return texture;
    }

    public static JSONObject toJson(String namespacedId, String folder, String extension) throws IOException {
        try (InputStream inputStream = new FileInputStream(getAsset(namespacedId, folder, extension)); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); StringWriter stringWriter = new StringWriter()) {
            char[] buffer = new char[1024 * 16];
            int length;
            while ((length = inputStreamReader.read(buffer)) > 0) {
                stringWriter.write(buffer, 0, length);
            }
            return new JSONObject(stringWriter.toString());
        }
    }

    public static JSONObject toJson(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); StringWriter stringWriter = new StringWriter()) {
            char[] buffer = new char[1024 * 16];
            int length;
            while ((length = inputStreamReader.read(buffer)) > 0) {
                stringWriter.write(buffer, 0, length);
            }
            return new JSONObject(stringWriter.toString());
        }
    }

    public static void readBlockState(GL4bc gl, String namespacedId, CompoundTag properties) {
        String blockName = namespacedId.split(":")[1].toUpperCase(Locale.ROOT);
        Block block = Block.valueOf(blockName);
        Color color = block.getColor();
        JSONObject blockState = getBlockState(namespacedId);
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
                        JSONObject model = getModel(modelPath);
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
                        return;
                    } else if (variants.get(variantName) instanceof JSONArray variantArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject variant = variantArray.getJSONObject(0);
                        String modelPath = variant.getString("model");
                        JSONObject model = getModel(modelPath);
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
                                    JSONObject model = getModel(modelPath);
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
                                    drawModel(gl, model, x, y, uvlock);
                                } else if (part.get("apply") instanceof JSONArray applyArray) {
                                    // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                                    JSONObject apply = applyArray.getJSONObject(0);
                                    String modelPath = apply.getString("model");
                                    JSONObject model = getModel(modelPath);
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
                                    drawModel(gl, model, x, y, uvlock);
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
                                JSONObject model = getModel(modelPath);
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
                                drawModel(gl, model, x, y, uvlock);
                            } else if (part.get("apply") instanceof JSONArray applyArray) {
                                // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                                JSONObject apply = applyArray.getJSONObject(0);
                                String modelPath = apply.getString("model");
                                JSONObject model = getModel(modelPath);
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
                                drawModel(gl, model, x, y, uvlock);
                            }
                        }
                    }
                } else {
                    if (part.get("apply") instanceof JSONObject apply) {
                        String modelPath = apply.getString("model");
                        JSONObject model = getModel(modelPath);
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
                        drawModel(gl, model, x, y, uvlock);
                    } else if (part.get("apply") instanceof JSONArray applyArray) {
                        // TODO Random model selection. Especially difficult when combined with the constant re-rendering of the schematic.
                        JSONObject apply = applyArray.getJSONObject(0);
                        String modelPath = apply.getString("model");
                        JSONObject model = getModel(modelPath);
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
                        drawModel(gl, model, x, y, uvlock);
                    }
                }
            }
        }
    }

    public static void drawModel(GL4bc gl, JSONObject model, int x, int y, boolean uvlock) {
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

        Map<String, String> textures = getTextures(model, new HashMap<>());

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

                JSONObject faces = jsonElement.getJSONObject("faces");
                Set<String> faceSet = faces.keySet();
                for (String faceName : faceSet) {
                    JSONObject face = faces.getJSONObject(faceName);

                    JSONArray uv = face.has("uv") ? face.getJSONArray("uv") : null;
                    String texture = face.has("texture") ? face.getString("texture").substring(1) : null;
                    String cullface = face.has("cullface") ? face.getString("cullface") : null;
                    int faceRotation = face.has("rotation") ? face.getInt("rotation") : 0;
                    int tintIndex = face.has("tintindex") ? face.getInt("tintindex") : -1;

                    Texture texture1 = getTexture(textures.getOrDefault(texture, "custom:missing"));
                    texture1.enable(gl);
                    texture1.bind(gl);

                    gl.glBegin(GL_QUADS);

                    TextureCoords textureCoords = texture1.getImageTexCoords();

                    double textureBottom = uv != null ? uv.getDouble(0) : textureCoords.bottom();
                    double textureLeft = uv != null ? uv.getDouble(1) : textureCoords.left();
                    double textureTop = uv != null ? uv.getDouble(2) : textureCoords.top();
                    double textureRight = uv != null ? uv.getDouble(3) : textureCoords.right();

                    switch (faceName) {
                        case "up" -> {
                            gl.glNormal3d(0.0f, 1.0f, 0.0f);
                            gl.glVertex3d(fromX, toY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureLeft);
                            gl.glVertex3d(fromX, toY, toZ);
                            gl.glTexCoord2d(textureBottom, textureRight);
                            gl.glVertex3d(toX, toY, toZ);
                            gl.glTexCoord2d(textureTop, textureRight);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glTexCoord2d(textureTop, textureLeft);
                        }
                        case "down" -> {
                            gl.glNormal3d(0.0f, -1.0f, 0.0f);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureLeft);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureRight);
                            gl.glVertex3d(toX, fromY, toZ);
                            gl.glTexCoord2d(textureTop, textureRight);
                            gl.glVertex3d(fromX, fromY, toZ);
                            gl.glTexCoord2d(textureTop, textureLeft);
                        }
                        case "north" -> {
                            gl.glNormal3d(0.0f, 0.0f, -1.0f);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureLeft);
                            gl.glVertex3d(fromX, toY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureRight);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glTexCoord2d(textureTop, textureRight);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glTexCoord2d(textureTop, textureLeft);
                        }
                        case "south" -> {
                            gl.glNormal3d(0.0f, 0.0f, 1.0f);
                            gl.glVertex3d(fromX, fromY, toZ); // bottom-left of the quad
                            gl.glTexCoord2d(textureBottom, textureLeft);
                            gl.glVertex3d(toX, fromY, toZ); // bottom-right of the quad
                            gl.glTexCoord2d(textureBottom, textureRight);
                            gl.glVertex3d(toX, toY, toZ); // top-right of the quad
                            gl.glTexCoord2d(textureTop, textureRight);
                            gl.glVertex3d(fromX, toY, toZ); // top-left of the quad
                            gl.glTexCoord2d(textureTop, textureLeft);
                            gl.glTexCoord2d(textureTop, textureLeft);
                        }
                        case "west" -> {
                            gl.glNormal3d(-1.0f, 0.0f, 0.0f);
                            gl.glVertex3d(fromX, fromY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureLeft);
                            gl.glVertex3d(fromX, fromY, toZ);
                            gl.glTexCoord2d(textureBottom, textureRight);
                            gl.glVertex3d(fromX, toY, toZ);
                            gl.glTexCoord2d(textureTop, textureRight);
                            gl.glVertex3d(fromX, toY, fromZ);
                            gl.glTexCoord2d(textureTop, textureLeft);
                        }
                        case "east" -> {
                            gl.glNormal3d(1.0f, 0.0f, 0.0f);
                            gl.glVertex3d(toX, fromY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureLeft);
                            gl.glVertex3d(toX, toY, fromZ);
                            gl.glTexCoord2d(textureBottom, textureRight);
                            gl.glVertex3d(toX, toY, toZ);
                            gl.glTexCoord2d(textureTop, textureRight);
                            gl.glVertex3d(toX, fromY, toZ);
                            gl.glTexCoord2d(textureTop, textureLeft);
                        }
                    }
                    gl.glEnd();
                    //texture1.disable(gl);
                }
                gl.glPopMatrix();
            }
        }
    }

    private static JSONArray getElements(JSONObject model) {
        if (model.has("elements")) {
            return model.getJSONArray("elements");
        } else if (model.has("parent")) {
            return getElements(getModel(model.getString("parent")));
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
            getTextures(getModel(model.getString("parent")), textures);
        }
        return textures;
    }

    private static void getTextureFromId(JSONObject model, Map<String, String> textures, String name) {
        JSONObject parent = null;
        if (model.has("parent")) {
            parent = getModel(model.getString("parent"));
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
