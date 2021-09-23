package me.eccentric_nz.tardisschematicviewer.util;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import me.eccentric_nz.tardisschematicviewer.Main;
import me.eccentric_nz.tardisschematicviewer.drawing.Block;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Assets {
    private static final File ASSETS;
    private static final Map<String, JSONObject> BLOCK_STATES = new HashMap<>();
    private static final Map<String, JSONObject> MODELS = new HashMap<>();
    private static final Map<String, Texture> TEXTURES = new HashMap<>();
    private static final Map<String, JSONObject> ANIMATIONS = new HashMap<>();

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.
    static {
        ASSETS = Main.assets;
        for (Block block : Block.values()) {
            String namespacedId = "minecraft:" + block.name().toLowerCase(Locale.ROOT);
            JSONObject blockState = getBlockState(namespacedId);
            BLOCK_STATES.put(namespacedId, blockState);
        }
        try {
            BLOCK_STATES.put("minecraft:missing", toJson(Main.class.getClassLoader().getResourceAsStream("assets/minecraft/blockstates/missing.json")));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
            MODELS.put("minecraft:block/missing", toJson(Main.class.getClassLoader().getResourceAsStream("assets/minecraft/models/block/missing.json")));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
            TEXTURES.put("minecraft:missing", TextureIO.newTexture(Main.class.getClassLoader().getResourceAsStream("assets/minecraft/textures/missing.png"), false, "png"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        ANIMATIONS.put("minecraft:missing", null);
    }

    private Assets() {
        throw new UnsupportedOperationException();
    }

    public static InputStream getAsset(String namespacedId, String folder, String extension) throws IOException {
        String[] split = namespacedId.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String id = split.length > 1 ? split[1] : split[0];
        String internalPath = "assets/" + namespace + "/" + folder + "/" + id + "." + extension;
        InputStream internalStream = Main.class.getClassLoader().getResourceAsStream(internalPath);
        if (internalStream != null) {
            System.out.println("Getting internal asset from " + internalPath);
            return internalStream;
        }
        File file = new File(ASSETS, namespace + File.separator + folder + File.separator + id + "." + extension).getCanonicalFile();
        if (file.exists()) {
            System.out.println("Getting asset from " + file);
        }
        return new FileInputStream(file);
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
            blockState = BLOCK_STATES.get("minecraft:missing");
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
            model = MODELS.get("minecraft:block/missing");
        }
        MODELS.put(namespacedId, model);
        return model;
    }

    public static Texture getTexture(String namespacedId) {
        if (TEXTURES.containsKey(namespacedId)) {
            return TEXTURES.get(namespacedId);
        }
        Texture texture = null;
        try (InputStream inputStream = getAsset(namespacedId, "textures", "png")) {
            texture = TextureIO.newTexture(inputStream, false, TextureIO.PNG);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("assets/minecraft/textures/missing.png")) {
                texture = TextureIO.newTexture(inputStream, false, TextureIO.PNG);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        TEXTURES.put(namespacedId, texture);
        return texture;
    }

    public static JSONObject getAnimation(String namespacedId) {
        if (ANIMATIONS.containsKey(namespacedId)) {
            return ANIMATIONS.get(namespacedId);
        }
        JSONObject animation = null;
        try {
            animation = toJson(namespacedId, "textures", "png.mcmeta");
        } catch (IOException ignored) {
        }
        ANIMATIONS.put(namespacedId, animation);
        return animation;
    }

    public static JSONObject toJson(String namespacedId, String folder, String extension) throws IOException {
        try (InputStream inputStream = getAsset(namespacedId, folder, extension); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); StringWriter stringWriter = new StringWriter()) {
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
}
