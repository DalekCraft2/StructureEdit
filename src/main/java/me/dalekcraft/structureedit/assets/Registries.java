package me.dalekcraft.structureedit.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import me.dalekcraft.structureedit.assets.models.BlockModel;
import me.dalekcraft.structureedit.assets.blockstates.BlockModelDefinition;
import me.dalekcraft.structureedit.assets.models.MissingBlockModel;
import me.dalekcraft.structureedit.assets.blockstates.MissingBlockModelDefinition;
import me.dalekcraft.structureedit.util.Configuration;
import me.dalekcraft.structureedit.util.Language;
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

public class Registries {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Registries INSTANCE = new Registries();
    private final Registry<BlockModelDefinition> blockStates = new Registry<>("blockstate");
    private final Registry<BlockModel> models = new Registry<>("model");
    private final Registry<TextureData> textures = new Registry<>("texture");
    private final Registry<JsonObject> animations = new Registry<>("animation");
    private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
    private Path path = Path.of("");

    {
        blockStates.setDefaultValue(MissingBlockModelDefinition.getInstance());

        models.setDefaultValue(MissingBlockModel.getInstance());

        TextureData texture = null;
        try (InputStream inputStream = getClass().getResourceAsStream("/assets/minecraft/textures/missingno.png")) {
            assert inputStream != null;
            texture = TextureIO.newTextureData(GLProfile.getDefault(), inputStream, false, TextureIO.PNG);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
        }
        textures.setDefaultValue(texture);
    }

    // TODO Create custom model files for the blocks what do not have them, like liquids, signs, and heads.

    public static Registries getInstance() {
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
        textures.forEach(TextureData::destroy);
        textures.clear();
        animations.clear();
        models.clear();
        blockStates.clear();
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

        models.forEach(blockModel -> blockModel.fillInheritance(this::getModel));

        LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.assets.loaded"));
    }

    public void loadNamespace(Path namespace) {
        if (Files.exists(namespace) && Files.isDirectory(namespace)) {
            FileSystem fileSystem = namespace.getFileSystem();
            loadTextures(fileSystem.getPath(namespace.toString(), "textures"), namespace.getFileName() + ":");
            loadModels(fileSystem.getPath(namespace.toString(), "models"), namespace.getFileName() + ":");
            loadBlockStates(fileSystem.getPath(namespace.toString(), "blockstates"), namespace.getFileName() + ":");
        }
    }

    public void loadBlockStates(@NotNull Path directory, String currentNamespace) {
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                directoryStream.forEach(path -> {
                    if (Files.isDirectory(path)) {
                        loadBlockStates(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path) && path.toString().endsWith(".json")) {
                        ResourceLocation namespacedId = new ResourceLocation(currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".json")));
                        loadBlockState(namespacedId);
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
    }

    public void loadBlockState(@NotNull ResourceLocation namespacedId) {
        if (blockStates.containsKey(namespacedId)) {
            return;
        }
        try {
            Reader reader = toReader(namespacedId, "blockstates", "json");
            BlockModelDefinition blockModelDefinition = BlockModelDefinition.fromStream(context, reader);
            blockStates.register(namespacedId, blockModelDefinition);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
        }
    }

    public BlockModelDefinition getBlockState(@NotNull ResourceLocation namespacedId) {
        return blockStates.get(namespacedId);
    }

    public Registry<BlockModelDefinition> getBlockStates() {
        return blockStates;
    }

    public void loadModels(@NotNull Path directory, String currentNamespace) {
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                directoryStream.forEach(path -> {
                    if (Files.isDirectory(path)) {
                        loadModels(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path) && path.toString().endsWith(".json")) {
                        ResourceLocation namespacedId = new ResourceLocation(currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".json")));
                        loadModel(namespacedId);
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
    }

    public void loadModel(@NotNull ResourceLocation namespacedId) {
        if (models.containsKey(namespacedId)) {
            return;
        }
        try {
            Reader reader = toReader(namespacedId, "models", "json");
            BlockModel model = BlockModel.fromStream(reader);
            model.name = namespacedId.toString();
            models.register(namespacedId, model);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
        }
    }

    public BlockModel getModel(@NotNull ResourceLocation namespacedId) {
        return models.get(namespacedId);
    }

    public Registry<BlockModel> getModels() {
        return models;
    }

    public void loadTextures(@NotNull Path directory, String currentNamespace) {
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                directoryStream.forEach(path -> {
                    if (Files.isDirectory(path)) {
                        loadTextures(path, currentNamespace + path.getFileName() + "/");
                    } else if (Files.isRegularFile(path)) {
                        if (path.toString().endsWith(".png")) {
                            ResourceLocation namespacedId = new ResourceLocation(currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".png")));
                            loadTexture(namespacedId);
                        } else if (path.toString().endsWith(".png.mcmeta")) {
                            ResourceLocation namespacedId = new ResourceLocation(currentNamespace + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".png.mcmeta")));
                            loadAnimation(namespacedId);
                        }
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }
    }

    public void loadTexture(@NotNull ResourceLocation namespacedId) {
        if (textures.containsKey(namespacedId)) {
            return;
        }
        try (InputStream inputStream = getAsset(namespacedId, "textures", "png")) {
            TextureData texture = TextureIO.newTextureData(GLProfile.getDefault(), inputStream, false, TextureIO.PNG);
            textures.register(namespacedId, texture);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
        }
    }

    public TextureData getTexture(@NotNull ResourceLocation namespacedId) {
        return textures.get(namespacedId);
    }

    public Registry<TextureData> getTextures() {
        return textures;
    }

    public void loadAnimation(@NotNull ResourceLocation namespacedId) {
        if (animations.containsKey(namespacedId)) {
            return;
        }
        try {
            JsonObject animation = toJson(namespacedId, "textures", "png.mcmeta");
            animations.register(namespacedId, animation);
        } catch (IOException ignored) {
        }
    }

    public JsonObject getAnimation(@NotNull ResourceLocation namespacedId) {
        return animations.get(namespacedId);
    }

    public Registry<JsonObject> getAnimations() {
        return animations;
    }

    @NotNull
    public InputStream getAsset(@NotNull ResourceLocation namespacedId, String folder, String extension) throws IOException {
        String namespace = namespacedId.getNamespace();
        String id = namespacedId.getPath();
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

    @NotNull
    public JsonObject toJson(ResourceLocation namespacedId, String folder, String extension) throws IOException {
        try (InputStream inputStream = getAsset(namespacedId, folder, extension); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader); StringWriter stringWriter = new StringWriter()) {
            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }
            return JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        }
    }

    @NotNull
    public Reader toReader(ResourceLocation namespacedId, String folder, String extension) throws IOException {
        InputStream inputStream = getAsset(namespacedId, folder, extension);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }
}
