package me.dalekcraft.structureedit.util;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class Assets {

    private static final Logger LOGGER = LogManager.getLogger(Assets.class);
    private static final Map<String, JSONObject> BLOCK_STATES = new HashMap<>();
    private static final Map<String, JSONObject> MODELS = new HashMap<>();
    private static final Map<String, Texture> TEXTURES = new HashMap<>(); // TODO Make this not use the JOGL Texture class, because it makes things difficult due to threads.
    private static final Map<String, JSONObject> ANIMATIONS = new HashMap<>();
    private static final ClassLoader LOADER = Assets.class.getClassLoader();
    private static Path assets;

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.

    @Contract(value = " -> fail", pure = true)
    private Assets() {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    public static Path getAssets() {
        return assets;
    }

    public static void setAssets(Path assets) {
        Assets.assets = assets;
        load();
    }

    public static void load() {
        CompletableFuture.runAsync(() -> {
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.assets.loading"));
            BLOCK_STATES.clear();
            MODELS.clear();
            TEXTURES.clear();
            ANIMATIONS.clear();
            String protocol = Assets.class.getResource("").getProtocol();
            if (Objects.equals(protocol, "jar")) {
                // run in jar
                try (FileSystem fileSystem = FileSystems.newFileSystem(Path.of(Assets.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                    Path internalAssets = fileSystem.getPath("assets");
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                        directoryStream.forEach(Assets::loadNamespace);
                    }
                } catch (URISyntaxException | IOException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                }
            } else if (Objects.equals(protocol, "file")) {
                // run in ide
                try {
                    Path internalAssets = Path.of(LOADER.getResource("assets").toURI());
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                        directoryStream.forEach(Assets::loadNamespace);
                    }
                } catch (URISyntaxException | IOException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                }
            }
            try {
                if (assets != null) {
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(assets)) {
                        directoryStream.forEach(Assets::loadNamespace);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.assets.loaded"));
        }).join();
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
                directoryStream.forEach((path) -> {
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
                directoryStream.forEach((path) -> {
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
                directoryStream.forEach((path) -> {
                    if (Files.isDirectory(path)) {
                        loadTextures(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path)) {
                        if (path.toString().endsWith(".png")) {
                            //                            String namespacedId = currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".png"));
                            //                            getTexture(namespacedId);
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
        String internalPath = "assets/" + namespace + "/" + folder + "/" + id + "." + extension;
        InputStream internalStream = LOADER.getResourceAsStream(internalPath);
        if (internalStream != null) {
            LOGGER.log(Level.TRACE, Configuration.LANGUAGE.getProperty("log.assets.getting_internal"), internalPath);
            return internalStream;
        }
        Path path = Path.of(assets.toString(), namespace + File.separator + folder + File.separator + id + "." + extension).toRealPath();
        if (Files.exists(path)) {
            LOGGER.log(Level.TRACE, Configuration.LANGUAGE.getProperty("log.assets.getting"), path);
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

    public static String @NotNull [] getBlockStateArray() {
        Set<String> keySet = new HashSet<>(BLOCK_STATES.keySet());
        keySet.remove("minecraft:missing");
        String[] array = keySet.toArray(new String[0]);
        Arrays.sort(array);
        return array;
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
            model = MODELS.get("minecraft:block/missing");
        }
        MODELS.put(namespacedId, model);
        return model;
    }

    public static Texture getTexture(@NotNull String namespacedId) {
        if (!namespacedId.contains(":")) {
            namespacedId = "minecraft:" + namespacedId;
        }
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
}
