package me.dalekcraft.structureedit.ui;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import me.dalekcraft.structureedit.assets.Registries;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.assets.blockstates.BlockModelDefinition;
import me.dalekcraft.structureedit.assets.blockstates.BlockModelRotation;
import me.dalekcraft.structureedit.assets.blockstates.MultiVariant;
import me.dalekcraft.structureedit.assets.blockstates.Variant;
import me.dalekcraft.structureedit.assets.blockstates.multipart.MultiPart;
import me.dalekcraft.structureedit.assets.blockstates.multipart.Selector;
import me.dalekcraft.structureedit.assets.models.*;
import me.dalekcraft.structureedit.assets.textures.metadata.AnimationFrame;
import me.dalekcraft.structureedit.assets.textures.metadata.AnimationMetadataSection;
import me.dalekcraft.structureedit.schematic.container.*;
import me.dalekcraft.structureedit.ui.editor.BlockStateEditorController;
import me.dalekcraft.structureedit.util.Constants;
import me.dalekcraft.structureedit.util.Direction;
import me.dalekcraft.structureedit.util.TintHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class SchematicRendererController {
    private static final double MINIMUM_ZOOM = -0.001;

    private static final double CAMERA_INITIAL_DISTANCE = -30.0;
    private static final double CAMERA_INITIAL_X_ANGLE = 135.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 45.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double ROTATION_SPEED = 1.0;
    private static final double TRACK_SPEED = 1.0;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final float SCALE = 1.0f;
    private static final double RESCALE_22_5 = 1.0 / Math.cos(Math.toRadians(22.5f));
    private static final double RESCALE_45 = 1.0 / Math.cos(Math.toRadians(45.0f));
    private static final float MODEL_SIZE = 16.0f;
    private static final long TICK_LENGTH = 50L;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final TransformGroup cameraTransform = new TransformGroup();
    private final Random random = new Random();
    private final Affine textureMatrix = new Affine();
    private final Group world = new Group();
    public int renderedHeight;
    public Schematic schematic;
    @FXML
    private GridPane pane;
    @FXML
    private SubScene subScene;
    private Affine modelMatrix = new Affine();
    private double mousePosX;
    private double mousePosY;
    private BlockStateEditorController blockStateEditorController;
    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            drawSchematic();
        }
    };



    /* public static double simplifyAngle(double angle) {
        return simplifyAngle(angle, Math.PI);
    }

    public static double simplifyAngle(double angle, double center) {
        return angle - 2 * Math.PI * Math.floor((angle + Math.PI - center) / (2 * Math.PI));
    } */

    @FXML
    public void initialize() {
        /* subScene = new SubScene(pane, pane.getWidth(), pane.getHeight(), true, SceneAntialiasing.DISABLED);
        subScene.setFill(Color.GREY);
        subScene.setFocusTraversable(true);
        subScene.setOnKeyPressed(this::onKeyPressed);
        subScene.setOnKeyReleased(this::onKeyReleased);
        subScene.setOnKeyTyped(this::onKeyTyped);
        subScene.setOnMouseDragged(this::onMouseDragged);
        subScene.setOnMousePressed(this::onMousePressed);
        subScene.setOnScroll(this::onScroll);
        subScene.setPickOnBounds(true);
        pane.getChildren().add(subScene); */
        // onScroll="#onScroll" pickOnBounds="true" GridPane.hgrow="SOMETIMES" GridPane.vgrow="SOMETIMES"

        Group root = new Group();
        root.getChildren().add(world);
        root.getChildren().add(cameraTransform);
        cameraTransform.getChildren().add(camera);
        subScene.setRoot(root);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraTransform.setRotateY(CAMERA_INITIAL_Y_ANGLE);
        cameraTransform.setRotateX(CAMERA_INITIAL_X_ANGLE);
        subScene.setCamera(camera);

        subScene.widthProperty().bind(pane.widthProperty());
        subScene.heightProperty().bind(pane.heightProperty());

        resume();
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        LOGGER.info("key pressed");

        KeyCode keyCode = event.getCode();
        switch (keyCode) {
            // FIXME If UP or DOWN are pressed at all, JavaFX key detection completely breaks; it might be because UP and DOWN change which UI element is focused
            case UP, E -> {
                if (schematic != null) {
                    int[] size = schematic.getSize();
                    if (renderedHeight < size[1]) {
                        renderedHeight++;
                    }
                    if (renderedHeight > size[1]) {
                        renderedHeight = size[1];
                    }
                }
            }
            case DOWN, Q -> {
                if (renderedHeight > 0) {
                    renderedHeight--;
                }
                if (renderedHeight < 0) {
                    renderedHeight = 0;
                }
            }
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent event) {
        LOGGER.info("key released");
    }

    @FXML
    private void onKeyTyped(KeyEvent event) {
        LOGGER.info("key typed");
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        double mouseOldX = mousePosX;
        double mouseOldY = mousePosY;
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();
        double mouseDeltaX = mousePosX - mouseOldX;
        double mouseDeltaY = mousePosY - mouseOldY;

        double modifier = 1.0;
        if (event.isControlDown()) {
            modifier = CONTROL_MULTIPLIER;
        }
        if (event.isShiftDown()) {
            modifier = SHIFT_MULTIPLIER;
        }

        if (event.isPrimaryButtonDown()) {
            cameraTransform.setRotateY(cameraTransform.rotateY.getAngle() - mouseDeltaX * modifier * ROTATION_SPEED);
            cameraTransform.setRotateX(cameraTransform.rotateX.getAngle() - mouseDeltaY * modifier * ROTATION_SPEED);
            if (cameraTransform.rotateX.getAngle() > 270) {
                cameraTransform.setRotateX(270);
            } else if (cameraTransform.rotateX.getAngle() < 90) {
                cameraTransform.setRotateX(90);
            }
        } else if (event.isSecondaryButtonDown()) {
            camera.setTranslateX(camera.getTranslateX() - mouseDeltaX * modifier * TRACK_SPEED);
            camera.setTranslateY(camera.getTranslateY() - mouseDeltaY * modifier * TRACK_SPEED);
        }
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();
    }

    @FXML
    private void onScroll(ScrollEvent event) {
        double modifier = 1.0;
        if (event.isControlDown()) {
            modifier = CONTROL_MULTIPLIER;
        }
        if (event.isShiftDown()) {
            modifier = SHIFT_MULTIPLIER;
        }

        camera.setTranslateZ(camera.getTranslateZ() + event.getDeltaY() * modifier);
        if (camera.getTranslateZ() > MINIMUM_ZOOM) {
            camera.setTranslateZ(MINIMUM_ZOOM);
        }
    }

    public void injectBlockStateEditorController(BlockStateEditorController blockStateEditorController) {
        this.blockStateEditorController = blockStateEditorController;
    }

    public void pause() {
        animationTimer.stop();
    }

    public void resume() {
        animationTimer.start();
    }

    public void drawSchematic() {
        world.getChildren().clear();
        modelMatrix.setToIdentity();
        if (schematic != null) {
            int[] size = schematic.getSize();
            // bottom-left-front corner of schematic is (0,0,0) so we need to center it at the origin
            modelMatrix.appendTranslation(-size[0] / 2.0f, -size[1] / 2.0f, -size[2] / 2.0f);
            // draw schematic border
            drawAxes(size[0], size[1], size[2]);
            modelMatrix = modelMatrix.clone(); // Need to do this so transformations don't affect the axes
            // draw a cube
            for (int x = 0; x < size[0]; x++) {
                for (int y = 0; y < renderedHeight; y++) {
                    for (int z = 0; z < size[2]; z++) {
                        Block block = schematic.getBlock(x, y, z);
                        if (block != null) {
                            BlockState blockState = schematic.getBlockState(block.getBlockStateIndex(), blockStateEditorController.getPaletteIndex());

                            Biome biome = schematic.getBiome(x, y, z);
                            BiomeState biomeState = biome != null ? schematic.getBiomeState(biome.getBiomeStateIndex()) : new BiomeState(Constants.DEFAULT_BIOME);
                            if (biomeState == null) {
                                biomeState = new BiomeState(Constants.DEFAULT_BIOME);
                            }

                            long seed = x + (long) y * size[2] * size[0] + (long) z * size[0];
                            random.setSeed(seed);
                            List<Variant> variants = getVariantsFromBlockState(blockState);
                            Color tint = TintHelper.getTint(blockState, biomeState);

                            try {
                                for (Variant variant : variants) {
                                    // modelMatrix.pushMatrix();
                                    Affine pushedMatrix = modelMatrix.clone();
                                    modelMatrix.appendTranslation(x, y, z);
                                    drawModel(variant, tint, new int[]{x, y, z});
                                    // modelMatrix.popMatrix();
                                    modelMatrix = pushedMatrix;
                                }
                            } catch (IllegalStateException e) {
                                System.exit(1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void drawAxes(float sizeX, float sizeY, float sizeZ) {
        // float[] positions = { //
        //         0.0f, 0.0f, 0.0f, // X-axis (red)
        //         sizeX, 1.0f, 1.0f, //
        //         0.0f, 0.0f, 0.0f, // Y-axis (green)
        //         1.0f, sizeY, 1.0f, //
        //         0.0f, 0.0f, 0.0f, // Z-axis (blue)
        //         1.0f, 1.0f, sizeZ //
        // };

        // X-axis
        // TriangleMesh xMesh = new TriangleMesh();
        // int[] xIndices = { //
        //         0, 0, 1, 1, 0, 0 //
        // };
        // xMesh.getFaces().setAll(xIndices);
        // xMesh.getPoints().setAll(positions);
        //
        // MeshView xAxis = new MeshView(xMesh);
        Box xAxis = new Box(sizeX, 0, 0);
        xAxis.setTranslateX(sizeX / 2.0);
        xAxis.setDrawMode(DrawMode.LINE);
        xAxis.getTransforms().add(modelMatrix);
        PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        WritableImage redMap = new WritableImage(1, 1);
        redMap.getPixelWriter().setColor(0, 0, Color.RED);
        redMaterial.setSelfIlluminationMap(redMap);
        xAxis.setMaterial(redMaterial);

        // Y-axis
        // TriangleMesh yMesh = new TriangleMesh();
        // int[] yIndices = { //
        //         2, 2, 3, 3, 2, 2 //
        // };
        // yMesh.getFaces().setAll(yIndices);
        // yMesh.getPoints().setAll(positions);
        //
        // MeshView yAxis = new MeshView(yMesh);
        Box yAxis = new Box(0, sizeY, 0);
        yAxis.setTranslateY(sizeY / 2.0);
        yAxis.setDrawMode(DrawMode.LINE);
        yAxis.getTransforms().add(modelMatrix);
        PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        WritableImage greenMap = new WritableImage(1, 1);
        greenMap.getPixelWriter().setColor(0, 0, Color.GREEN);
        greenMaterial.setSelfIlluminationMap(greenMap);
        yAxis.setMaterial(greenMaterial);

        // Z-axis
        // TriangleMesh zMesh = new TriangleMesh();
        // int[] zIndices = { //
        //         4, 4, 5, 5, 4, 4 //
        // };
        // zMesh.getFaces().setAll(zIndices);
        // zMesh.getPoints().setAll(positions);
        //
        // MeshView zAxis = new MeshView(zMesh);
        Box zAxis = new Box(0, 0, sizeZ);
        zAxis.setTranslateZ(sizeZ / 2.0);
        zAxis.setDrawMode(DrawMode.LINE);
        zAxis.getTransforms().add(modelMatrix);
        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.BLUE);
        WritableImage blueMap = new WritableImage(1, 1);
        blueMap.getPixelWriter().setColor(0, 0, Color.BLUE);
        blueMaterial.setSelfIlluminationMap(blueMap);
        zAxis.setMaterial(blueMaterial);

        // Add all axes
        world.getChildren().addAll(xAxis, yAxis, zAxis);
    }

    @NotNull
    public List<Variant> getVariantsFromBlockState(@NotNull BlockState blockState) {
        List<Variant> modelList = new ArrayList<>();
        ResourceLocation namespacedId = blockState.getId();
        Map<String, String> properties = blockState.getProperties();
        if (properties.containsKey("waterlogged") && "true".equals(properties.get("waterlogged"))) {
            // TODO Figure out how to get both the block tint and the water tint into drawModel().
            Variant waterModel = new Variant(Constants.WATERLOGGED_BLOCK, BlockModelRotation.X0_Y0, false, 1);
            modelList.add(waterModel);
        }
        BlockModelDefinition blockModelDefinition = Registries.getInstance().getBlockState(namespacedId);
        if (!blockModelDefinition.isMultiPart()) {
            Map<String, MultiVariant> variants = blockModelDefinition.getVariants();
            for (Map.Entry<String, MultiVariant> entry : variants.entrySet()) {
                String variantName = entry.getKey();
                Set<Map.Entry<String, String>> states = BlockState.toPropertyMap(variantName, false).entrySet();
                boolean contains = true;
                for (Map.Entry<String, String> state : states) {
                    if (!state.getValue().equals(properties.get(state.getKey()))) {
                        contains = false;
                        break;
                    }
                }
                if (contains) {
                    Variant variant = chooseRandomVariant(entry.getValue().getVariants());
                    modelList.add(variant);
                    return modelList;
                }
            }
        } else if (blockModelDefinition.isMultiPart()) {
            MultiPart multipart = blockModelDefinition.getMultiPart();
            for (Selector selector : multipart.getSelectors()) {
                Predicate<BlockState> predicate = selector.getPredicate(blockState);
                if (predicate.test(blockState)) {
                    Variant variant = chooseRandomVariant(selector.getVariant().getVariants());
                    modelList.add(variant);
                }
            }
        }
        if (modelList.isEmpty()) {
            Variant variant = chooseRandomVariant(Registries.getInstance().getBlockStates().getDefaultValue().getVariant("").getVariants());
            modelList.add(variant);
        }
        return modelList;
    }

    private Variant chooseRandomVariant(@NotNull List<Variant> variants) {
        int total = 0;
        NavigableMap<Integer, Variant> weightTree = new TreeMap<>();
        for (Variant variant : variants) {
            int weight = variant.getWeight();
            if (weight <= 0) {
                continue;
            }
            total += weight;
            weightTree.put(total, variant);
        }
        int value = random.nextInt(0, total) + 1;
        return weightTree.ceilingEntry(value).getValue();
    }

    public void drawModel(@NotNull Variant variant, Color tint, int[] position) {
        BlockModel model = Registries.getInstance().getModel(variant.getModelLocation());
        BlockModelRotation blockModelRotation = variant.getRotation();
        int x = blockModelRotation.getXRotation();
        int y = blockModelRotation.getYRotation();
        boolean uvLock = variant.isUvLocked();

        Affine rotationMatrix = new Affine();
        rotationMatrix.appendRotation(-y, new Point3D(0.5, 0.5, 0.5), Rotate.Y_AXIS);
        rotationMatrix.appendRotation(-x, new Point3D(0.5, 0.5, 0.5), Rotate.X_AXIS);

        modelMatrix.append(rotationMatrix);

        List<BlockElement> elements = model.getElements();
        if (elements != null) {
            for (BlockElement element : elements) {
                // modelMatrix.pushMatrix();
                Affine pushedMatrix = modelMatrix.clone();

                Vector3f from = new Vector3f(element.from).div(MODEL_SIZE);
                Vector3f to = new Vector3f(element.to).div(MODEL_SIZE);
                BlockElementRotation rotation = element.rotation;
                Vector3d origin = null;
                Direction.Axis axis = null;
                float angle = 0.0f;
                boolean rescale = false;
                if (rotation != null) {
                    origin = new Vector3d(rotation.origin);
                    axis = rotation.axis;
                    angle = rotation.angle;
                    rescale = rotation.rescale;
                }
                boolean shade = element.shade;

                if (axis != null) {
                    modelMatrix.appendTranslation(origin.x, origin.y, origin.z);
                    float rescaleFactor = 1.0f;
                    if (Math.abs(angle) == 22.5) {
                        rescaleFactor = (float) RESCALE_22_5;
                    } else if (Math.abs(angle) == 45.0) {
                        rescaleFactor = (float) RESCALE_45;
                    }
                    switch (axis) {
                        case X -> {
                            modelMatrix.appendRotation(angle, new Point3D(0, 0, 0), Rotate.X_AXIS);
                            if (rescale) {
                                from.mul(1.0f, rescaleFactor, rescaleFactor);
                                to.mul(1.0f, rescaleFactor, rescaleFactor);
                            }
                        }
                        case Y -> {
                            modelMatrix.appendRotation(angle, new Point3D(0, 0, 0), Rotate.Y_AXIS);
                            if (rescale) {
                                from.mul(rescaleFactor, 1.0f, rescaleFactor);
                                to.mul(rescaleFactor, 1.0f, rescaleFactor);
                            }
                        }
                        case Z -> {
                            modelMatrix.appendRotation(angle, new Point3D(0, 0, 0), Rotate.Z_AXIS);
                            if (rescale) {
                                from.mul(rescaleFactor, rescaleFactor, 1.0f);
                                to.mul(rescaleFactor, rescaleFactor, 1.0f);
                            }
                        }
                    }
                    if (rescale) {
                        switch (axis) {
                            case X ->
                                    modelMatrix.appendTranslation(-origin.x, -origin.y * rescaleFactor, -origin.z * rescaleFactor);
                            case Y ->
                                    modelMatrix.appendTranslation(-origin.x * rescaleFactor, -origin.y, -origin.z * rescaleFactor);
                            case Z ->
                                    modelMatrix.appendTranslation(-origin.x * rescaleFactor, -origin.y * rescaleFactor, -origin.z);
                        }
                    } else {
                        modelMatrix.appendTranslation(-origin.x, -origin.y, -origin.z);
                    }
                }

                Map<Direction, BlockElementFace> faces = element.faces;
                Set<Direction> faceSet = faces.keySet();
                for (Direction faceName : faceSet) {
                    BlockElementFace face = faces.get(faceName);

                    BlockFaceUv uv = face.uv;
                    String faceTexture = face.texture;
                    Direction cullface = face.cullForDirection;
                    int faceRotation = uv.rotation;
                    int tintIndex = face.tintIndex;

                    if (cullface != null) {
                        // TODO Make some blocks not cull, because some blocks, like stairs and fences, do not cull in-game
                        cullface = Direction.rotate(rotationMatrix, cullface);

                        Block blockToCheck = null;
                        switch (cullface) {
                            case EAST -> {
                                if (position[0] + 1 < schematic.getSize()[0]) {
                                    blockToCheck = schematic.getBlock(position[0] + 1, position[1], position[2]);
                                }
                            }
                            case WEST -> {
                                if (position[0] - 1 >= 0) {
                                    blockToCheck = schematic.getBlock(position[0] - 1, position[1], position[2]);
                                }
                            }
                            case UP -> {
                                if (position[1] + 1 < /* schematic.getSize()[1] */ renderedHeight) {
                                    blockToCheck = schematic.getBlock(position[0], position[1] + 1, position[2]);
                                }
                            }
                            case DOWN -> {
                                if (position[1] - 1 >= 0) {
                                    blockToCheck = schematic.getBlock(position[0], position[1] - 1, position[2]);
                                }
                            }
                            case SOUTH -> {
                                if (position[2] + 1 < schematic.getSize()[2]) {
                                    blockToCheck = schematic.getBlock(position[0], position[1], position[2] + 1);
                                }
                            }
                            case NORTH -> {
                                if (position[2] - 1 >= 0) {
                                    blockToCheck = schematic.getBlock(position[0], position[1], position[2] - 1);
                                }
                            }
                        }
                        ResourceLocation resourceLocation = blockToCheck == null ? null : schematic.getBlockPalette(blockStateEditorController.getPaletteIndex()).get(blockToCheck.getBlockStateIndex()).getId();
                        // TODO Find a way to check whether blockToCheck is a block what would actually cause culling like cobblestone, and not a block like an anvil.
                        boolean shouldBeCulled = resourceLocation != null && !resourceLocation.equals(new ResourceLocation("minecraft", "air")) && !resourceLocation.equals(new ResourceLocation("minecraft", "water"));
                        if (shouldBeCulled) {
                            continue;
                        }
                    }

                    if (tintIndex == -1) {
                        tint = TintHelper.DEFAULT_TINT;
                    }

                    Image texture = Registries.getInstance().getTexture(model.getMaterial(faceTexture).texture());

                    float textureLeft = uv.uvs[0] / MODEL_SIZE;
                    float textureTop = uv.uvs[1] / MODEL_SIZE;
                    float textureRight = uv.uvs[2] / MODEL_SIZE;
                    float textureBottom = uv.uvs[3] / MODEL_SIZE;

                    float textureLeft2 = textureLeft;
                    float textureTop2 = textureTop;
                    float textureRight2 = textureRight;
                    float textureBottom2 = textureBottom;
                    float mixFactor = 0.0f;
                    AnimationMetadataSection animation = Registries.getInstance().getAnimationMetadataSection(model.getMaterial(faceTexture).texture());
                    if (animation != null && animation != AnimationMetadataSection.EMPTY) {
                        boolean interpolate = animation.isInterpolatedFrames();
                        int width = animation.getFrameWidth((int) texture.getWidth());
                        int height = animation.getFrameHeight((int) texture.getWidth());
                        int frameTime = animation.getDefaultFrameTime();

                        double widthFactor = Math.abs(texture.getWidth() / width);
                        double heightFactor = Math.abs(texture.getHeight() / height);

                        List<AnimationFrame> frames = animation.frames;
                        if (frames.isEmpty()) {
                            frames = new ArrayList<>();
                            for (int i = 0; i < heightFactor; i++) {
                                frames.add(new AnimationFrame(i));
                            }
                        }

                        // Set all texture coordinates to the first frame
                        textureLeft /= widthFactor;
                        textureTop /= heightFactor;
                        textureRight /= widthFactor;
                        textureBottom /= heightFactor;

                        textureLeft2 /= widthFactor;
                        textureTop2 /= heightFactor;
                        textureRight2 /= widthFactor;
                        textureBottom2 /= heightFactor;

                        long currentTime = System.currentTimeMillis();
                        long currentTick = currentTime / TICK_LENGTH;
                        int index = (int) (currentTick / frameTime % frames.size());

                        if (interpolate) {
                            long timeOfStartOfFrame = index * frameTime * TICK_LENGTH;
                            long timeOfEndOfFrame = (index + 1) * frameTime * TICK_LENGTH;
                            // The mix factor should be a value between 0.0f and 1.0f, representing the passage of time from the current frame to the next. 0.0f is the current frame, and 1.0f is the next frame.
                            mixFactor = (currentTime % (timeOfEndOfFrame - timeOfStartOfFrame)) / ((timeOfEndOfFrame - timeOfStartOfFrame) * 1.0f);
                        }

                        AnimationFrame frame = frames.get(index);
                        double frameDouble = frame.getIndex();
                        // TODO Implement the "time" tag.
                        // int time = frame.getTime(frameTime);

                        int index2 = frames.size() > index + 1 ? index + 1 : 0;
                        AnimationFrame frame2 = frames.get(index2);
                        double frameDouble2 = frame2.getIndex();

                        // Change to the current frame in the animation
                        textureTop += frameDouble / heightFactor;
                        textureBottom += frameDouble / heightFactor;

                        textureTop2 += frameDouble2 / heightFactor;
                        textureBottom2 += frameDouble2 / heightFactor;
                    } /* else if (texture.getWidth() != texture.getHeight()) {
                        // This breaks the textures of signs, because those textures are not square, resulting in the rendered signs having the missing texture.
                        // For this reason, I am not using this code, even though the wiki mentions that this is how it works.
                        // It mentions it here: https://minecraft.fandom.com/wiki/Resource_pack#Animation
                        texture = Registries.getInstance().getTexture(MissingTexture.getLocation());
                    } */

                    // I'll be honest, I did trial and error for this part. I have no idea how it works.
                    for (int i = 0; i < faceRotation; i += 90) {
                        float temp = textureLeft;
                        textureLeft = SCALE - textureBottom;
                        textureBottom = textureRight;
                        textureRight = SCALE - textureTop;
                        textureTop = temp;

                        float temp2 = textureLeft2;
                        textureLeft2 = SCALE - textureBottom2;
                        textureBottom2 = textureRight2;
                        textureRight2 = SCALE - textureTop2;
                        textureTop2 = temp2;
                    }

                    textureMatrix.setToIdentity();
                    textureMatrix.appendRotation(-faceRotation, new Point2D(0.5, 0.5)); // Unlike with JOGL, I have to negate faceRotation here
                    if (uvLock) {
                        switch (faceName) {
                            case UP -> {
                                if (x == 180) {
                                    textureMatrix.appendRotation(y, new Point2D(0.5, 0.5));
                                } else {
                                    textureMatrix.appendRotation(-y, new Point2D(0.5, 0.5));
                                }
                            }
                            case DOWN -> {
                                if (x == 180) {
                                    textureMatrix.appendRotation(-y, new Point2D(0.5, 0.5));
                                } else {
                                    textureMatrix.appendRotation(y, new Point2D(0.5, 0.5));
                                }
                            }
                            default -> textureMatrix.appendRotation(-x, new Point2D(0.5, 0.5));
                        }
                    }

                    TriangleMesh mesh = new TriangleMesh();
                    TriangleMesh mesh2 = new TriangleMesh();

                    int[] indices = { //
                            0, 0, 1, 1, 2, 2, //
                            2, 2, 3, 3, 0, 0 //
                    };
                    mesh.getFaces().setAll(indices);
                    mesh2.getFaces().setAll(indices);

                    float[] positions = switch (faceName) { //
                        case EAST -> new float[]{ //
                                to.x, from.y, to.z, //
                                to.x, from.y, from.z, //
                                to.x, to.y, from.z, //
                                to.x, to.y, to.z //
                        };
                        case WEST -> new float[]{ //
                                from.x, from.y, from.z, //
                                from.x, from.y, to.z, //
                                from.x, to.y, to.z, //
                                from.x, to.y, from.z //
                        };
                        case UP -> new float[]{ //
                                from.x, to.y, to.z,//
                                to.x, to.y, to.z, //
                                to.x, to.y, from.z, //
                                from.x, to.y, from.z //
                        };
                        case DOWN -> new float[]{ //
                                from.x, from.y, from.z, //
                                to.x, from.y, from.z, //
                                to.x, from.y, to.z, //
                                from.x, from.y, to.z //
                        };
                        case SOUTH -> new float[]{ //
                                from.x, from.y, to.z, //
                                to.x, from.y, to.z, //
                                to.x, to.y, to.z, //
                                from.x, to.y, to.z //
                        };
                        case NORTH -> new float[]{ //
                                to.x, from.y, from.z, //
                                from.x, from.y, from.z, //
                                from.x, to.y, from.z, //
                                to.x, to.y, from.z //
                        };
                    };
                    mesh.getPoints().setAll(positions);
                    mesh2.getPoints().setAll(positions);

                    float[] normals = switch (faceName) { //
                        case EAST -> new float[]{ //
                                1.0f, 0.0f, 0.0f, //
                                1.0f, 0.0f, 0.0f, //
                                1.0f, 0.0f, 0.0f, //
                                1.0f, 0.0f, 0.0f //
                        };
                        case WEST -> new float[]{ //
                                -1.0f, 0.0f, 0.0f, //
                                -1.0f, 0.0f, 0.0f, //
                                -1.0f, 0.0f, 0.0f, //
                                -1.0f, 0.0f, 0.0f //
                        };
                        case UP -> new float[]{ //
                                0.0f, 1.0f, 0.0f, //
                                0.0f, 1.0f, 0.0f, //
                                0.0f, 1.0f, 0.0f, //
                                0.0f, 1.0f, 0.0f //
                        };
                        case DOWN -> new float[]{ //
                                0.0f, -1.0f, 0.0f, //
                                0.0f, -1.0f, 0.0f, //
                                0.0f, -1.0f, 0.0f, //
                                0.0f, -1.0f, 0.0f //
                        };
                        case SOUTH -> new float[]{ //
                                0.0f, 0.0f, 1.0f, //
                                0.0f, 0.0f, 1.0f, //
                                0.0f, 0.0f, 1.0f, //
                                0.0f, 0.0f, 1.0f //
                        };
                        case NORTH -> new float[]{ //
                                0.0f, 0.0f, -1.0f, //
                                0.0f, 0.0f, -1.0f, //
                                0.0f, 0.0f, -1.0f, //
                                0.0f, 0.0f, -1.0f //
                        };
                    };
                    mesh.getNormals().setAll(normals);
                    mesh2.getNormals().setAll(normals);

                    float[] texCoords = { //
                            textureLeft, textureBottom, //
                            textureRight, textureBottom, //
                            textureRight, textureTop, //
                            textureLeft, textureTop //
                    };
                    double[] converted = Doubles.toArray(Floats.asList(texCoords));
                    double[] destination = new double[texCoords.length];
                    textureMatrix.transform2DPoints(converted, 0, destination, 0, texCoords.length / 2);
                    texCoords = Floats.toArray(Doubles.asList(destination));
                    mesh.getTexCoords().setAll(texCoords);

                    float[] texCoords2 = { //
                            textureLeft2, textureBottom2, //
                            textureRight2, textureBottom2, //
                            textureRight2, textureTop2, //
                            textureLeft2, textureTop2 //
                    };
                    double[] converted2 = Doubles.toArray(Floats.asList(texCoords2));
                    double[] destination2 = new double[texCoords2.length];
                    textureMatrix.transform2DPoints(converted2, 0, destination2, 0, texCoords2.length / 2);
                    texCoords2 = Floats.toArray(Doubles.asList(destination2));
                    mesh2.getTexCoords().setAll(texCoords2);


                    // FIXME The self-illumination map hides the tint, making stuff like grass appear gray/grey (because, apparently, the grass model disables shading).
                    PhongMaterial material = new PhongMaterial(tint, texture, null, null, shade ? null : texture);

                    // FIXME Despite the mixFactor being the correct value now, the two texture frames do not change opacity at all and thus do not have interpolation.
                    MeshView meshView = new MeshView(mesh);
                    meshView.setCullFace(CullFace.BACK);
                    meshView.getTransforms().add(modelMatrix);
                    meshView.setMaterial(material);
                    meshView.setOpacity(1 - mixFactor);
                    world.getChildren().add(meshView);

                    MeshView meshView2 = new MeshView(mesh2);
                    meshView2.setCullFace(CullFace.BACK);
                    meshView2.getTransforms().add(modelMatrix);
                    meshView2.setMaterial(material);
                    meshView2.setOpacity(mixFactor);
                    world.getChildren().add(meshView2);
                }
                // modelMatrix.popMatrix();
                modelMatrix = pushedMatrix;
            }
        }
    }
}