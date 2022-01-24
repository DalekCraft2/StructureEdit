package me.dalekcraft.structureedit.ui.editor;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.schematic.container.BiomeState;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import me.dalekcraft.structureedit.ui.editor.BiomeEditorController;

public class BiomeStateEditorController {

    private Schematic schematic;
    private BiomeEditorController biomeEditorController;
    @FXML
    private TextField biomeStateIdTextField;
    @FXML
    private ListView<BiomeState> biomeStateListView;

    public void initialize() {
        biomeStateIdTextField.textProperty().addListener(this::onBiomeStateIdUpdate);

        biomeStateListView.getSelectionModel().selectedItemProperty().addListener(this::onBiomeStateSelected);
    }

    public void injectBiomeEditorController(BiomeEditorController biomeEditorController) {
        this.biomeEditorController = biomeEditorController;
    }

    public void disableComponents() {
        biomeStateIdTextField.setText(null);
        biomeStateIdTextField.setDisable(true);
        biomeStateListView.setItems(null);
    }

    public void enableComponents() {
        // biomeStateIdTextField.setDisable(false);
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        disableComponents();
        if (this.schematic != null) {
            enableComponents();
            biomeStateListView.setItems(this.schematic.getBiomePalette());
        }
    }

    public void onBiomeStateSelected(ObservableValue<? extends BiomeState> observable, BiomeState oldValue, BiomeState newValue) {
        if (newValue != null) {
            biomeStateIdTextField.setText(newValue.getId().toString());
            biomeStateIdTextField.setDisable(false);
        }
    }

    public void onBiomeStateIdUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && newValue != null) {
            BiomeState biomeState = biomeStateListView.getSelectionModel().getSelectedItem();
            if (biomeState != null) {
                biomeState.setId(new ResourceLocation(newValue));
                biomeStateListView.refresh();
            }
            biomeEditorController.updateBiomeGrid();
        }
    }
}
