package me.dalekcraft.structureedit.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.image.Image;
import me.dalekcraft.structureedit.assets.blockstates.BlockModelDefinition;
import me.dalekcraft.structureedit.assets.blockstates.MissingBlockModelDefinition;
import me.dalekcraft.structureedit.assets.models.BlockModel;
import me.dalekcraft.structureedit.assets.models.MissingBlockModel;
import me.dalekcraft.structureedit.assets.textures.MissingTexture;
import me.dalekcraft.structureedit.assets.textures.metadata.AnimationMetadataSection;
import me.dalekcraft.structureedit.assets.textures.metadata.TextureMetadataSection;
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
    private final Registry<Image> textures = new Registry<>("texture");
    private final Registry<AnimationMetadataSection> animationMetadataSections = new Registry<>("animationMetadata");
    private final Registry<TextureMetadataSection> textureMetadataSections = new Registry<>("textureMetadata");
    private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
    private Path path = Path.of("");

    {
        blockStates.setDefaultValue(MissingBlockModelDefinition.getInstance());

        models.setDefaultValue(MissingBlockModel.getInstance());

        textures.setDefaultValue(MissingTexture.getInstance());

        animationMetadataSections.setDefaultValue(AnimationMetadataSection.EMPTY);
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
//        textures.forEach(Image::destroy);
        textures.clear();
        animationMetadataSections.clear();
        models.clear();
        blockStates.clear();
        String protocol = Objects.requireNonNull(getClass().getResource("/icon.png")).getProtocol();
        if ("jar".equals(protocol)) {
            // run in .jar
            try (FileSystem fileSystem = FileSystems.newFileSystem(Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                Path internalAssets = fileSystem.getPath("assets");
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(internalAssets)) {
                    directoryStream.forEach(this::loadNamespace);
                }
            } catch (URISyntaxException | IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } else if ("file".equals(protocol)) {
            // run in IDE
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
                            loadMetadataSection(namespacedId);
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
            Image texture = new Image(inputStream);
            // Image texture = new Image(inputStream, 16, 16, true, false);
            textures.register(namespacedId, texture);
        } catch (IOException e) {
            LOGGER.log(Level.TRACE, e.getMessage());
        }
    }

    public Image getTexture(@NotNull ResourceLocation namespacedId) {
        return textures.get(namespacedId);
    }

    public Registry<Image> getTextures() {
        return textures;
    }

    public void loadMetadataSection(@NotNull ResourceLocation namespacedId) {
        try {
            JsonObject json = toJson(namespacedId, "textures", "png.mcmeta");
            if (json.has(TextureMetadataSection.SECTION_NAME)) { // Typically in textures/misc/
                loadTextureMetadataSection(namespacedId, json.getAsJsonObject(TextureMetadataSection.SECTION_NAME));
            }
            // This isn't needed because we don't render Villagers (yet, at least).
            // if (json.has(VillagerMetadataSection.SECTION_NAME)) { // Typically in textures/entity/villager/
            //     loadVillagerMetadataSection(namespacedId, json.getAsJsonObject(VillagerMetadataSection.SECTION_NAME));
            // }
            if (json.has(AnimationMetadataSection.SECTION_NAME)) { // Every other texture directory
                loadAnimationMetadataSection(namespacedId, json.getAsJsonObject(AnimationMetadataSection.SECTION_NAME));
            }
        } catch (IOException ignored) {
        }
    }

    public void loadTextureMetadataSection(@NotNull ResourceLocation namespacedId, JsonObject json) {
        if (textureMetadataSections.containsKey(namespacedId)) {
            return;
        }
        TextureMetadataSection textureMetadataSection = TextureMetadataSection.SERIALIZER.deserialize(json);
        textureMetadataSections.register(namespacedId, textureMetadataSection);
    }

    public TextureMetadataSection getTextureMetadataSection(@NotNull ResourceLocation namespacedId) {
        return textureMetadataSections.get(namespacedId);
    }

    public Registry<TextureMetadataSection> getTextureMetadataSections() {
        return textureMetadataSections;
    }

    public void loadAnimationMetadataSection(@NotNull ResourceLocation namespacedId, JsonObject json) {
        if (animationMetadataSections.containsKey(namespacedId)) {
            return;
        }
        AnimationMetadataSection animation = AnimationMetadataSection.SERIALIZER.deserialize(json);
        animationMetadataSections.register(namespacedId, animation);
    }

    public AnimationMetadataSection getAnimationMetadataSection(@NotNull ResourceLocation namespacedId) {
        return animationMetadataSections.get(namespacedId);
    }

    public Registry<AnimationMetadataSection> getAnimationMetadataSections() {
        return animationMetadataSections;
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
