package me.dalekcraft.structureedit.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import me.dalekcraft.structureedit.schematic.container.Schematic;

import java.util.Arrays;

public class SchematicInfoController {

    private Schematic schematic;
    @FXML
    private TextField sizeTextField;
    @FXML
    private TextField offsetTextField;
    @FXML
    private TextField dataVersionTextField;

    public void initialize() {

    }

    public void disableComponents() {
        sizeTextField.setText(null);
        sizeTextField.setDisable(true);
        offsetTextField.setText(null);
        offsetTextField.setDisable(true);
        dataVersionTextField.setText(null);
        dataVersionTextField.setDisable(true);
    }

    public void enableComponents() {
        sizeTextField.setDisable(false);
        offsetTextField.setDisable(false);
        dataVersionTextField.setDisable(false);
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        disableComponents();
        if (schematic != null) {
            enableComponents();
            updateSchematicInfo();
        }
    }

    public void updateSchematicInfo() {
        int[] size = schematic.getSize();
        int[] offset = schematic.getOffset();
        sizeTextField.setText(Arrays.toString(size));
        offsetTextField.setText(Arrays.toString(offset));
        dataVersionTextField.setText(String.valueOf(schematic.getDataVersion()));
    }
}
