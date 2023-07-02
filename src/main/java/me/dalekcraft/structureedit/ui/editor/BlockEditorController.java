package me.dalekcraft.structureedit.ui.editor;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.drawing.BlockColor;
import me.dalekcraft.structureedit.schematic.container.Block;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import me.dalekcraft.structureedit.ui.BlockButton;
import me.dalekcraft.structureedit.ui.MainController;
import me.dalekcraft.structureedit.util.Constants;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class BlockEditorController {

    private final SpinnerValueFactory.IntegerSpinnerValueFactory blockLayerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0);
    private final SpinnerValueFactory.IntegerSpinnerValueFactory blockPaletteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0);
    private Schematic schematic;
    private BlockStateEditorController blockStateEditorController;
    private BlockButton selectedBlock;
    @FXML
    private Spinner<Integer> blockLayerSpinner;
    @FXML
    private Spinner<Integer> blockPaletteSpinner;
    @FXML
    private TextField blockPositionTextField;
    @FXML
    private TextField blockEntityIdTextField;
    @FXML
    private TextField blockEntityNbtTextField;
    @FXML
    private GridPane blockGrid;

    public void initialize() {
        blockLayerSpinner.setValueFactory(blockLayerValueFactory);
        blockLayerSpinner.valueProperty().addListener(this::onBlockLayerUpdate);
        blockLayerSpinner.getEditor().setTextFormatter(new TextFormatter<Integer>(change -> {
            try {
                Integer.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        blockPaletteSpinner.setValueFactory(blockPaletteValueFactory);
        blockPaletteSpinner.valueProperty().addListener(this::onBlockPaletteUpdate);
        blockPaletteSpinner.getEditor().setTextFormatter(new TextFormatter<Integer>(change -> {
            try {
                Integer.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        blockEntityIdTextField.textProperty().addListener(this::onBlockEntityIdUpdate);

        blockEntityNbtTextField.textProperty().addListener(this::onBlockEntityNbtUpdate);
        /*blockEntityNbtTextField.setTextFormatter(new TextFormatter<CompoundTag>(new StringConverter<>() {
            @Override
            public String toString(CompoundTag tag) {
                System.out.println("toString");
                if (tag != null) {
                    try {
                        return SNBTUtil.toSNBT(tag);
                    } catch (IOException e) {
                        return null;
                    }
                }
                return null;
            }

            @Override
            public CompoundTag fromString(String string) {
                System.out.println("fromString");
                try {
                    return (CompoundTag) SNBTUtil.fromSNBT(string);
                } catch (IOException | StringIndexOutOfBoundsException e) {
                    return null;
                }
            }
        }));*/
    }

    public void injectBlockStateEditorController(BlockStateEditorController blockStateEditorController) {
        this.blockStateEditorController = blockStateEditorController;
    }

    public void disableComponents() {
        selectedBlock = null;
        blockLayerValueFactory.setValue(0);
        blockLayerSpinner.setDisable(true);
        blockPaletteValueFactory.setValue(0);
        blockPaletteSpinner.setDisable(true);
        blockPositionTextField.setText(null);
        blockPositionTextField.setDisable(true);
        blockEntityIdTextField.setText(null);
        blockEntityIdTextField.setDisable(true);
        blockEntityNbtTextField.setText(null);
        blockEntityNbtTextField.setDisable(true);
        blockGrid.getChildren().clear();
    }

    public void enableComponents() {
        blockLayerSpinner.setDisable(false);
        /*blockPaletteSpinner.setDisable(false);
        blockPositionTextField.setDisable(false);
        blockEntityIdTextField.setDisable(false);
        blockEntityNbtTextField.setDisable(false);*/
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        disableComponents();
        if (this.schematic != null) {
            enableComponents();
            int[] size = this.schematic.getSize();
            blockLayerValueFactory.setMax(size[1] - 1);
            int blockPaletteSize = this.schematic.getBlockPalette().size();
            blockPaletteValueFactory.setMax(blockPaletteSize - 1);
            updateBlockGrid();
        }
    }

    private void onBlockSelected(@NotNull Event e) {
        if (selectedBlock != null) {
            selectedBlock.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        }
        selectedBlock = (BlockButton) e.getSource();
        Block block = selectedBlock.getBlock();

        if (block != null) {
            selectedBlock.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));

            blockEntityIdTextField.setText(block.getBlockEntity().getId().toString());
            blockEntityIdTextField.setDisable(false);

            try {
                blockEntityNbtTextField.setText(SNBTUtil.toSNBT(block.getBlockEntity().getNbt()));
            } catch (IOException ignored) {
            }
            blockEntityNbtTextField.setDisable(false);
            blockEntityNbtTextField.setStyle("-fx-text-inner-color: #000000");

            blockPositionTextField.setText(Arrays.toString(selectedBlock.getPosition()));
            blockPositionTextField.setDisable(false);

            blockPaletteValueFactory.setValue(block.getBlockStateIndex());
            blockPaletteSpinner.setDisable(false);
        }
    }

    @FXML
    public void onBlockLayerUpdate(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (schematic != null) {
            updateBlockGrid();
        }
    }

    @FXML
    public void onBlockPaletteUpdate(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (schematic != null && selectedBlock != null) {
            Block block = selectedBlock.getBlock();
            if (block != null) {
                block.setBlockStateIndex(newValue);
            }
            updateBlockGrid();
            updateSelectedBlock();
        }
    }

    public void onBlockEntityIdUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && selectedBlock != null) {
            Block block = selectedBlock.getBlock();
            if (block != null) {
                block.getBlockEntity().setId(new ResourceLocation(newValue));
            }
        }
    }

    public void onBlockEntityNbtUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && selectedBlock != null) {
            Block block = selectedBlock.getBlock();
            if (block != null) {
                try {
                    CompoundTag nbt = (CompoundTag) SNBTUtil.fromSNBT(newValue.trim());
                    block.getBlockEntity().setNbt(nbt);
                    blockEntityNbtTextField.setStyle("-fx-text-inner-color: #000000");
                } catch (IOException e1) {
                    blockEntityNbtTextField.setStyle("-fx-text-inner-color: #FF0000");
                } catch (StringIndexOutOfBoundsException e1) {
                    e1.printStackTrace();
                    blockEntityNbtTextField.setStyle("-fx-text-inner-color: #FF0000");
                }
            }
        }
    }

    public void updateSelectedBlock() {
        if (selectedBlock != null) {
            Block block = selectedBlock.getBlock();

            blockEntityIdTextField.setText(block.getBlockEntity().getId().toString());
            blockEntityIdTextField.setDisable(false);

            try {
                blockEntityNbtTextField.setText(SNBTUtil.toSNBT(block.getBlockEntity().getNbt()));
            } catch (IOException ignored) {
            }
            blockEntityNbtTextField.setDisable(false);
        }
    }

    // TODO Make the editor built into the 3D view instead of being a layer-by-layer editor.
    public void updateBlockGrid() {
        blockGrid.getChildren().clear();
        if (schematic != null) {
            int[] size = schematic.getSize();
            int currentLayer = blockLayerSpinner.getValue();
            for (int x = 0; x < size[0]; x++) {
                for (int z = 0; z < size[2]; z++) {
                    Block block = schematic.getBlock(x, currentLayer, z);
                    if (block != null) {
                        BlockState blockState = schematic.getBlockState(block.getBlockStateIndex(), blockStateEditorController.getPaletteIndex());

                        ResourceLocation blockId = blockState.getId();
                        String blockName = blockId.getPath().toUpperCase(Locale.ROOT);
                        Color color;
                        try {
                            color = BlockColor.valueOf(blockName).getColor();
                        } catch (IllegalArgumentException e) {
                            color = MainController.MISSING_COLOR;
                        }
                        color = Color.color(color.getRed(), color.getGreen(), color.getBlue());
                        BlockButton blockButton = new BlockButton(block, x, currentLayer, z);
                        blockButton.setText(blockName.isEmpty() ? "?" : blockName.substring(0, 1));
                        blockButton.setTooltip(new Tooltip(blockId.toString()));
                        blockButton.setTextOverrun(OverrunStyle.CLIP);
                        blockButton.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                        blockButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
                        blockButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        blockButton.setPrefSize(Constants.EDITOR_TILE_SIZE, Constants.EDITOR_TILE_SIZE);
                        blockButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onBlockSelected);
                        blockGrid.add(blockButton, x, z);
                        if (selectedBlock != null) {
                            int[] position = selectedBlock.getPosition();
                            if (Arrays.equals(position, new int[]{x, currentLayer, z})) {
                                selectedBlock = blockButton;
                                // Set selected tile's border color to red
                                blockButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
                            }
                        }
                    } else {
                        Color color = Color.WHITE;
                        BlockButton blockButton = new BlockButton(null, x, currentLayer, z);
                        blockButton.setText("");
                        blockButton.setTextOverrun(OverrunStyle.CLIP);
                        blockButton.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                        blockButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
                        blockButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        blockButton.setPrefSize(Constants.EDITOR_TILE_SIZE, Constants.EDITOR_TILE_SIZE);
                        blockButton.setDisable(true);
                        blockGrid.add(blockButton, x, z);
                    }
                }
            }
        }
    }
}
