package me.dalekcraft.structureedit.ui.editor;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.schematic.container.Entity;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

public class EntityEditorController {

    private final SpinnerValueFactory.DoubleSpinnerValueFactory entityXValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0);
    private final SpinnerValueFactory.DoubleSpinnerValueFactory entityYValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0);
    private final SpinnerValueFactory.DoubleSpinnerValueFactory entityZValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0);
    private Schematic schematic;
    @FXML
    private Spinner<Double> entityXSpinner;
    @FXML
    private Spinner<Double> entityYSpinner;
    @FXML
    private Spinner<Double> entityZSpinner;
    @FXML
    private TextField entityIdTextField;
    @FXML
    private TextField entityNbtTextField;
    @FXML
    private ListView<Entity> entityListView;

    public void initialize() {
        entityXSpinner.setValueFactory(entityXValueFactory);
        entityXSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onEntityPositionUpdate(observable, oldValue, newValue, 0));
        entityXSpinner.getEditor().setTextFormatter(new TextFormatter<Double>(change -> {
            try {
                Double.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
        entityYSpinner.setValueFactory(entityYValueFactory);
        entityYSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onEntityPositionUpdate(observable, oldValue, newValue, 1));
        entityYSpinner.getEditor().setTextFormatter(new TextFormatter<Double>(change -> {
            try {
                Double.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
        entityZSpinner.setValueFactory(entityZValueFactory);
        entityZSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onEntityPositionUpdate(observable, oldValue, newValue, 2));
        entityZSpinner.getEditor().setTextFormatter(new TextFormatter<Double>(change -> {
            try {
                Double.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        entityIdTextField.textProperty().addListener(this::onEntityIdUpdate);

        entityNbtTextField.textProperty().addListener(this::onEntityNbtUpdate);

        entityListView.getSelectionModel().selectedItemProperty().addListener(this::onEntitySelected);
    }

    public void disableComponents() {
        entityXValueFactory.setValue(0.0);
        entityXSpinner.setDisable(true);
        entityYValueFactory.setValue(0.0);
        entityYSpinner.setDisable(true);
        entityZValueFactory.setValue(0.0);
        entityZSpinner.setDisable(true);
        entityIdTextField.setText(null);
        entityIdTextField.setDisable(true);
        entityNbtTextField.setText(null);
        entityNbtTextField.setDisable(true);
        entityListView.setItems(null);
    }

    public void enableComponents() {
        /*entityXSpinner.setDisable(false);
        entityYSpinner.setDisable(false);
        entityZSpinner.setDisable(false);
        entityIdTextField.setDisable(false);
        entityNbtTextField.setDisable(false);*/
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        disableComponents();
        if (this.schematic != null) {
            enableComponents();
            entityListView.setItems(this.schematic.getEntities());
            int[] size = this.schematic.getSize();
            entityXValueFactory.setMax(size[0]);
            entityYValueFactory.setMax(size[1]);
            entityZValueFactory.setMax(size[2]);
        }
    }

    public void onEntitySelected(ObservableValue<? extends Entity> observable, Entity oldValue, Entity newValue) {
        if (newValue != null) {
            double[] position = newValue.getPosition();

            entityXValueFactory.setValue(position[0]);
            entityXSpinner.setDisable(false);
            entityYValueFactory.setValue(position[1]);
            entityYSpinner.setDisable(false);
            entityZValueFactory.setValue(position[2]);
            entityZSpinner.setDisable(false);

            entityIdTextField.setText(newValue.getId().toString());
            entityIdTextField.setDisable(false);

            try {
                entityNbtTextField.setText(SNBTUtil.toSNBT(newValue.getNbt()));
            } catch (IOException ignored) {
            }
            entityNbtTextField.setStyle("-fx-text-inner-color: #000000");
            entityNbtTextField.setDisable(false);
        }
    }

    public void onEntityPositionUpdate(ObservableValue<? extends Double> observable, Double oldValue, Double newValue, int index) {
        if (schematic != null) {
            Entity entity = entityListView.getSelectionModel().getSelectedItem();
            if (entity != null) {
                entity.getPosition()[index] = newValue;
                entityListView.refresh();
            }
        }
    }

    public void onEntityIdUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && newValue != null) {
            Entity entity = entityListView.getSelectionModel().getSelectedItem();
            if (entity != null) {
                entity.setId(new ResourceLocation(newValue));
                entityListView.refresh();
            }
        }
    }

    public void onEntityNbtUpdate(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (schematic != null && newValue != null) {
            Entity entity = entityListView.getSelectionModel().getSelectedItem();
            if (entity != null) {
                try {
                    CompoundTag nbt = (CompoundTag) SNBTUtil.fromSNBT(newValue.trim());
                    entity.setNbt(nbt);
                    entityNbtTextField.setStyle("-fx-text-inner-color: #000000");
                } catch (IOException e1) {
                    entityNbtTextField.setStyle("-fx-text-inner-color: #FF0000");
                } catch (StringIndexOutOfBoundsException e1) {
                    e1.printStackTrace();
                    entityNbtTextField.setStyle("-fx-text-inner-color: #FF0000");
                }
                entityListView.refresh();
            }
        }
    }
}
