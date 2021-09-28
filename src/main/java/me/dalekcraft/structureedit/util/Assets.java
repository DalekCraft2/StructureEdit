package me.dalekcraft.structureedit.util;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import me.dalekcraft.structureedit.drawing.Block;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class Assets {

    private static final Logger LOGGER = LogManager.getLogger(Assets.class);
    private static final Map<String, JSONObject> BLOCK_STATES = new HashMap<>();
    private static final Map<String, JSONObject> MODELS = new HashMap<>();
    private static final Map<String, Texture> TEXTURES = new HashMap<>();
    private static final Map<String, JSONObject> ANIMATIONS = new HashMap<>();
    private static final ClassLoader LOADER = Assets.class.getClassLoader();
    private static File assets;

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.

    private Assets() {
        throw new UnsupportedOperationException();
    }

    public static File getAssets() {
        return assets;
    }

    public static void setAssets(File assets) {
        Assets.assets = assets;
        load();
    }

    public static void load() {
        CompletableFuture.runAsync(() -> {
            //            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.assets.loading"));
            BLOCK_STATES.clear();
            MODELS.clear();
            TEXTURES.clear();
            ANIMATIONS.clear();
            try {
                BLOCK_STATES.put("minecraft:missing", toJson(LOADER.getResourceAsStream("assets/minecraft/blockstates/missing.json")));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            try {
                MODELS.put("minecraft:block/missing", toJson(LOADER.getResourceAsStream("assets/minecraft/models/block/missing.json")));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            try {
                // TODO Make this not use the JOGL Texture class, because it makes things difficult due to threads.
                TEXTURES.put("minecraft:missing", TextureIO.newTexture(LOADER.getResourceAsStream("assets/minecraft/textures/missing.png"), false, "png"));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            ANIMATIONS.put("minecraft:missing", null);
            for (Block block : Block.values()) {
                String namespacedId = block.toId();
                JSONObject blockState = getBlockState(namespacedId);
                BLOCK_STATES.put(namespacedId, blockState);
            }
            //            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.assets.loaded"));
        });
    }

    @NotNull
    public static InputStream getAsset(@NotNull String namespacedId, String folder, String extension) throws IOException {
        String[] split = namespacedId.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String id = split.length > 1 ? split[1] : split[0];
        String internalPath = "assets/" + namespace + "/" + folder + "/" + id + "." + extension;
        InputStream internalStream = LOADER.getResourceAsStream(internalPath);
        if (internalStream != null) {
            LOGGER.log(Level.TRACE, Configuration.LANGUAGE.getProperty("log.assets.getting_internal"), internalPath);
            return internalStream;
        }
        File file = new File(assets, namespace + File.separator + folder + File.separator + id + "." + extension).getCanonicalFile();
        if (file.exists()) {
            LOGGER.log(Level.TRACE, Configuration.LANGUAGE.getProperty("log.assets.getting"), file);
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
            LOGGER.log(Level.TRACE, e.getMessage());
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
            LOGGER.log(Level.TRACE, e.getMessage());
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
            LOGGER.log(Level.TRACE, e.getMessage());
            try (InputStream inputStream = LOADER.getResourceAsStream("assets/minecraft/textures/missing.png")) {
                texture = TextureIO.newTexture(inputStream, false, TextureIO.PNG);
            } catch (IOException e1) {
                LOGGER.log(Level.TRACE, e.getMessage());
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

    @Contract("_, _, _ -> new")
    @NotNull
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

    @Contract("_ -> new")
    @NotNull
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
