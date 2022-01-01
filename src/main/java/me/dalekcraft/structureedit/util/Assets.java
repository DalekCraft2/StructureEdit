package me.dalekcraft.structureedit.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Objects;

public class Assets {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Assets INSTANCE = new Assets();
    private final ObservableMap<String, JsonObject> blockStates = FXCollections.observableHashMap();
    private final ObservableMap<String, JsonObject> models = FXCollections.observableHashMap();
    private final ObservableMap<String, TextureData> textures = FXCollections.observableHashMap();
    private final ObservableMap<String, JsonObject> animations = FXCollections.observableHashMap();
    private Path path = Path.of("");

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.

    public static Assets getInstance() {
        return INSTANCE;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = Objects.requireNonNullElseGet(path, () -> Path.of(""));
        Configuration.CONFIG.setProperty("assets_path", path.toString());
        load();
    }

    public void load() {
        LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.assets.loading"), path);
        if (path == null || !Files.exists(path)) {
            LOGGER.log(Level.WARN, Language.LANGUAGE.getString("log.assets.invalid"), path);
        }
        blockStates.clear();
        models.clear();
        textures.forEach((s, textureData) -> textureData.destroy());
        textures.clear();
        animations.clear();
        String protocol = Objects.requireNonNull(getClass().getResource("")).getProtocol();
        if (protocol.equals("jar")) {
            // run in jar
            try (FileSystem fileSystem = FileSystems.newFileSystem(Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                Path internalAssets = fileSystem.getPath("assets");
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                    directoryStream.forEach(this::loadNamespace);
                }
            } catch (URISyntaxException | IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } else if (protocol.equals("file")) {
            // run in ide
            try {
                Path internalAssets = Path.of(getClass().getResource("/assets").toURI());
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                    directoryStream.forEach(this::loadNamespace);
                }
            } catch (URISyntaxException | IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
        try {
            if (path != null) {
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                    directoryStream.forEach(this::loadNamespace);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.assets.loaded"));
    }

    public void loadNamespace(Path namespace) {
        if (Files.exists(namespace) && Files.isDirectory(namespace)) {
            FileSystem fileSystem = namespace.getFileSystem();
            loadBlockStates(fileSystem.getPath(namespace.toString(), "blockstates"), namespace.getFileName() + ":");
            loadModels(fileSystem.getPath(namespace.toString(), "models"), namespace.getFileName() + ":");
            loadTextures(fileSystem.getPath(namespace.toString(), "textures"), namespace.getFileName() + ":");
        }
    }

    public void loadBlockStates(@NotNull Path directory, String currentNamespace) {
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

    public void loadModels(@NotNull Path directory, String currentNamespace) {
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

    public void loadTextures(@NotNull Path directory, String currentNamespace) {
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
    public InputStream getAsset(@NotNull String namespacedId, String folder, String extension) throws IOException {
        String[] split = namespacedId.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String id = split.length > 1 ? split[1] : split[0];
        String internalPath = "/assets/" + namespace + "/" + folder + "/" + id + "." + extension;
        InputStream internalStream = getClass().getResourceAsStream(internalPath);
        if (internalStream != null) {
            LOGGER.log(Level.TRACE, Language.LANGUAGE.getString("log.assets.getting_internal"), internalPath);
            return internalStream;
        }
        Path path = this.path.resolve(namespace + File.separator + folder + File.separator + id + "." + extension);
        if (Files.exists(path)) {
            LOGGER.log(Level.TRACE, Language.LANGUAGE.getString("log.assets.getting"), path);
        }
        return Files.newInputStream(path);
    }

    public JsonObject getBlockState(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
        if (blockStates.containsKey(namespacedId)) {
            return blockStates.get(namespacedId);
        }
        JsonObject blockState;
        try {
            blockState = toJson(namespacedId, "blockstates", "json");
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
            blockState = blockStates.get("minecraft:missing");
        }
        blockStates.put(namespacedId, blockState);
        return blockState;
    }

    public ObservableMap<String, JsonObject> getBlockStateMap() {
        return blockStates;
    }

    public JsonObject getModel(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
        if (models.containsKey(namespacedId)) {
            return models.get(namespacedId);
        }
        JsonObject model;
        try {
            model = toJson(namespacedId, "models", "json");
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
            model = models.get("minecraft:missing");
        }
        models.put(namespacedId, model);
        return model;
    }

    public ObservableMap<String, JsonObject> getModelMap() {
        return models;
    }

    public TextureData getTexture(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
        if (textures.containsKey(namespacedId)) {
            return textures.get(namespacedId);
        }
        TextureData texture = null;
        try (InputStream inputStream = getAsset(namespacedId, "textures", "png")) {
            texture = TextureIO.newTextureData(GLProfile.getDefault(), inputStream, false, TextureIO.PNG);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
            try (InputStream inputStream = getClass().getResourceAsStream("/assets/minecraft/textures/missing.png")) {
                assert inputStream != null;
                texture = TextureIO.newTextureData(GLProfile.getDefault(), inputStream, false, TextureIO.PNG);
            } catch (IOException e1) {
                LOGGER.log(Level.TRACE, e.getMessage());
            }
        }
        textures.put(namespacedId, texture);
        return texture;
    }

    public ObservableMap<String, TextureData> getTextureMap() {
        return textures;
    }

    public JsonObject getAnimation(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
        if (animations.containsKey(namespacedId)) {
            return animations.get(namespacedId);
        }
        JsonObject animation = null;
        try {
            animation = toJson(namespacedId, "textures", "png.mcmeta");
        } catch (IOException ignored) {
        }
        animations.put(namespacedId, animation);
        return animation;
    }

    public ObservableMap<String, JsonObject> getAnimationMap() {
        return animations;
    }

    @NotNull
    public JsonObject toJson(String namespacedId, String folder, String extension) throws IOException {
        try (InputStream inputStream = getAsset(namespacedId, folder, extension); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader); StringWriter stringWriter = new StringWriter()) {
            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }
            return JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        }
    }
}
