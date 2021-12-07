package me.dalekcraft.structureedit.util;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Objects;

public final class Assets {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObservableMap<String, JSONObject> BLOCK_STATES = FXCollections.observableHashMap();
    private static final ObservableMap<String, JSONObject> MODELS = FXCollections.observableHashMap();
    private static final ObservableMap<String, TextureData> TEXTURES = FXCollections.observableHashMap();
    private static final ObservableMap<String, JSONObject> ANIMATIONS = FXCollections.observableHashMap();
    private static Path path = Path.of("");

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.

    private Assets() {
        throw new UnsupportedOperationException();
    }

    public static Path getPath() {
        return path;
    }

    public static void setPath(Path path) {
        Assets.path = Objects.requireNonNullElseGet(path, () -> Path.of(""));
        Configuration.CONFIG.setProperty("assets_path", Assets.path.toString());
        load();
    }

    public static void load() {
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.assets.loading"), path);
        if (path == null || !Files.exists(path)) {
            LOGGER.log(Level.WARN, Configuration.LANGUAGE.getString("log.assets.invalid"), path);
        }
        BLOCK_STATES.clear();
        MODELS.clear();
        TEXTURES.forEach((s, textureData) -> textureData.destroy());
        TEXTURES.clear();
        ANIMATIONS.clear();
        String protocol = Objects.requireNonNull(Assets.class.getResource("")).getProtocol();
        if (protocol.equals("jar")) {
            // run in jar
            try (FileSystem fileSystem = FileSystems.newFileSystem(Path.of(Assets.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                Path internalAssets = fileSystem.getPath("assets");
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                    directoryStream.forEach(Assets::loadNamespace);
                }
            } catch (URISyntaxException | IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } else if (protocol.equals("file")) {
            // run in ide
            try {
                Path internalAssets = Path.of(Assets.class.getResource("/assets").toURI());
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                    directoryStream.forEach(Assets::loadNamespace);
                }
            } catch (URISyntaxException | IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
        try {
            if (path != null) {
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                    directoryStream.forEach(Assets::loadNamespace);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.assets.loaded"));
    }

    public static void loadNamespace(Path namespace) {
        if (Files.exists(namespace) && Files.isDirectory(namespace)) {
            FileSystem fileSystem = namespace.getFileSystem();
            loadBlockStates(fileSystem.getPath(namespace.toString(), "blockstates"), namespace.getFileName() + ":");
            loadModels(fileSystem.getPath(namespace.toString(), "models"), namespace.getFileName() + ":");
            loadTextures(fileSystem.getPath(namespace.toString(), "textures"), namespace.getFileName() + ":");
        }
    }

    public static void loadBlockStates(@NotNull Path directory, String currentNamespace) {
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                directoryStream.forEach(path -> {
                    if (Files.isDirectory(path)) {
                        loadBlockStates(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path) && path.toString().endsWith(".json")) {
                        String namespacedId = currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".json"));
                        getBlockState(namespacedId);
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
    }

    public static void loadModels(@NotNull Path directory, String currentNamespace) {
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                directoryStream.forEach(path -> {
                    if (Files.isDirectory(path)) {
                        loadModels(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path) && path.toString().endsWith(".json")) {
                        String namespacedId = currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".json"));
                        getModel(namespacedId);
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
    }

    public static void loadTextures(@NotNull Path directory, String currentNamespace) {
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                directoryStream.forEach(path -> {
                    if (Files.isDirectory(path)) {
                        loadTextures(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path)) {
                        if (path.toString().endsWith(".png")) {
                            String namespacedId = currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".png"));
                            getTexture(namespacedId);
                        } else if (path.toString().endsWith(".png.mcmeta")) {
                            String namespacedId = currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".png.mcmeta"));
                            getAnimation(namespacedId);
                        }
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
    }

    @NotNull
    public static InputStream getAsset(@NotNull String namespacedId, String folder, String extension) throws IOException {
        String[] split = namespacedId.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String id = split.length > 1 ? split[1] : split[0];
        String internalPath = "/assets/" + namespace + "/" + folder + "/" + id + "." + extension;
        InputStream internalStream = Assets.class.getResourceAsStream(internalPath);
        if (internalStream != null) {
            LOGGER.log(Level.TRACE, Configuration.LANGUAGE.getString("log.assets.getting_internal"), internalPath);
            return internalStream;
        }
        Path path = Path.of(Assets.path.toString(), namespace + File.separator + folder + File.separator + id + "." + extension);
        if (Files.exists(path)) {
            LOGGER.log(Level.TRACE, Configuration.LANGUAGE.getString("log.assets.getting"), path);
        }
        return Files.newInputStream(path);
    }

    public static JSONObject getBlockState(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
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

    public static ObservableMap<String, JSONObject> getBlockStateMap() {
        return BLOCK_STATES;
    }

    public static JSONObject getModel(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
        if (MODELS.containsKey(namespacedId)) {
            return MODELS.get(namespacedId);
        }
        JSONObject model;
        try {
            model = toJson(namespacedId, "models", "json");
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
            model = MODELS.get("minecraft:missing");
        }
        MODELS.put(namespacedId, model);
        return model;
    }

    public static ObservableMap<String, JSONObject> getModelMap() {
        return MODELS;
    }

    public static TextureData getTexture(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
        if (TEXTURES.containsKey(namespacedId)) {
            return TEXTURES.get(namespacedId);
        }
        TextureData texture = null;
        try (InputStream inputStream = getAsset(namespacedId, "textures", "png")) {
            texture = TextureIO.newTextureData(GLProfile.getDefault(), inputStream, false, TextureIO.PNG);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
            try (InputStream inputStream = Assets.class.getResourceAsStream("/assets/minecraft/textures/missing.png")) {
                assert inputStream != null;
                texture = TextureIO.newTextureData(GLProfile.getDefault(), inputStream, false, TextureIO.PNG);
            } catch (IOException e1) {
                LOGGER.log(Level.TRACE, e.getMessage());
            }
        }
        TEXTURES.put(namespacedId, texture);
        return texture;
    }

    public static ObservableMap<String, TextureData> getTextureMap() {
        return TEXTURES;
    }

    public static JSONObject getAnimation(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
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

    public static ObservableMap<String, JSONObject> getAnimationMap() {
        return ANIMATIONS;
    }

    @NotNull
    public static JSONObject toJson(String namespacedId, String folder, String extension) throws IOException {
        try (InputStream inputStream = getAsset(namespacedId, folder, extension); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader); StringWriter stringWriter = new StringWriter()) {
            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }
            return new JSONObject(stringWriter.toString());
        }
    }
}
