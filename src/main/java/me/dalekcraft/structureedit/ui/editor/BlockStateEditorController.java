package me.dalekcraft.structureedit.ui.editor;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import me.dalekcraft.structureedit.assets.Registries;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import me.dalekcraft.structureedit.ui.AutoCompleteComboBoxListener;

import java.util.Collections;
import java.util.Map;

public class BlockStateEditorController {

    private final SpinnerValueFactory.IntegerSpinnerValueFactory paletteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0);
    private Schematic schematic;
    private BlockEditorController blockEditorController;
    @FXML
    private Spinner<Integer> paletteSpinner;
    @FXML
    private ComboBox<String> blockStateIdComboBox;
    private AutoCompleteComboBoxListener<String> blockStateIdAutoComplete;
    @FXML
    private TextField blockStatePropertiesTextField;
    @FXML
    private ListView<BlockState> blockStateListView;

    public void initialize() {
        // TODO Palette editor, like the BlockState editor.
        paletteSpinner.setValueFactory(paletteValueFactory);
        paletteSpinner.valueProperty().addListener(this::onPaletteUpdate);
        paletteSpinner.getEditor().setTextFormatter(new TextFormatter<Integer>(change -> {
            try {
                Integer.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        blockStateIdAutoComplete = new AutoCompleteComboBoxListener<>(blockStateIdComboBox);
        blockStateIdComboBox.getSelectionModel().selectedItemProperty().addListener(this::onBlockStateIdUpdate);

        // TODO Perhaps change the properties and NBT text fields to JTrees, and create NBTExplorer-esque editors for them.
        blockStatePropertiesTextField.textProperty().addListener(this::onBlockStatePropertiesUpdate);

        // TODO Make entries addable and removable to and from this.
        blockStateListView.getSelectionModel().selectedItemProperty().addListener(this::onBlockStateSelected);
    }

    public void injectBlockEditorController(BlockEditorController blockEditorController) {
        this.blockEditorController = blockEditorController;
    }

    public void disableComponents() {
        paletteValueFactory.setValue(0);
        paletteSpinner.setDisable(true);
        blockStateIdComboBox.getSelectionModel().select(null);
        blockStateIdComboBox.setDisable(true);
        blockStatePropertiesTextField.setText(null);
        blockStatePropertiesTextField.setDisable(true);
        blockStateListView.setItems(null);
    }

    public void enableComponents() {
        // paletteSpinner.setDisable(false);
        /*blockStateIdComboBox.setDisable(false);
        blockStatePropertiesTextField.setDisable(false);*/
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        disableComponents();
        if (this.schematic != null) {
            enableComponents();
            blockStateListView.setItems(this.schematic.getBlockPalette());
            if (this.schematic.getBlockPalettes().size() > 1) {
                int palettesSize = this.schematic.getBlockPalettes().size();
                paletteSpinner.setDisable(false);
                paletteValueFactory.setMax(palettesSize - 1);
            }
        }
    }

    public void reloadBlockStates() {
        ObservableList<ResourceLocation> items = FXCollections.observableArrayList(Registries.getInstance().getBlockStates().keySet());
        ObservableList<String> stringItems = FXCollections.observableArrayList();
        items.forEach(resourceLocation -> stringItems.add(resourceLocation.toString()));
        Collections.sort(stringItems);
        blockStateIdComboBox.setItems(stringItems);
        blockStateIdAutoComplete.setItems(stringItems);
    }

    public int getPaletteIndex() {
        return paletteSpinner.getValue();
    }

    public void onBlockStateSelected(ObservableValue<? extends BlockState> observable, BlockState oldValue, BlockState newValue) {
        if (newValue != null) {
            blockStateIdComboBox.getSelectionModel().select(newValue.getId().toString());
            blockStateIdComboBox.setDisable(false);

            blockStatePropertiesTextField.setText(BlockState.toPropertyString(newValue.getProperties()));
            blockStatePropertiesTextField.setStyle("-fx-text-inner-color: #000000");
            blockStatePropertiesTextField.setDisable(false);
        }
    }

    @FXML
    public void onPaletteUpdate(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (schematic != null) {
            int selectedIndex = blockStateListView.getSelectionModel().getSelectedIndex();
            blockStateListView.setItems(schematic.getBlockPalette(newValue));
            blockStateListView.getSelectionModel().select(selectedIndex);
            blockEditorController.updateBlockGrid();
        }
    }

    public void onBlockStateIdUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && newValue != null) {
            BlockState blockState = blockStateListView.getSelectionModel().getSelectedItem();
            if (blockState != null) {
                blockState.setId(new ResourceLocation(newValue));
                blockStateListView.refresh();
            }
            blockEditorController.updateBlockGrid();
        }
    }

    public void onBlockStatePropertiesUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && newValue != null) {
            BlockState blockState = blockStateListView.getSelectionModel().getSelectedItem();
            if (blockState != null) {
                try {
                    Map<String, String> properties = BlockState.toPropertyMap(newValue);
                    blockState.setProperties(properties);
                    blockStatePropertiesTextField.setStyle("-fx-text-inner-color: #000000");
                } catch (IllegalArgumentException e1) {
                    blockStatePropertiesTextField.setStyle("-fx-text-inner-color: #FF0000");
                }
                blockStateListView.refresh();
            }
            blockEditorController.updateBlockGrid();
        }
    }
}
