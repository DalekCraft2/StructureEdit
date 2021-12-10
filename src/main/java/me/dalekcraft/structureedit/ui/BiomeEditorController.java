package me.dalekcraft.structureedit.ui;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.dalekcraft.structureedit.drawing.GrassColor;
import me.dalekcraft.structureedit.schematic.container.Biome;
import me.dalekcraft.structureedit.schematic.container.BiomeState;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

public class BiomeEditorController {

    private final SpinnerValueFactory.IntegerSpinnerValueFactory biomeLayerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0);
    private final SpinnerValueFactory.IntegerSpinnerValueFactory biomePaletteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0);
    private Schematic schematic;
    private BiomeButton selectedBiome;
    @FXML
    private Spinner<Integer> biomeLayerSpinner;
    @FXML
    private Spinner<Integer> biomePaletteSpinner;
    @FXML
    private TextField biomePositionTextField;
    @FXML
    private GridPane biomeGrid;

    public void initialize() {
        biomeLayerSpinner.setValueFactory(biomeLayerValueFactory);
        biomeLayerSpinner.valueProperty().addListener(this::onBiomeLayerUpdate);
        biomeLayerSpinner.getEditor().setTextFormatter(new TextFormatter<Integer>(change -> {
            try {
                Integer.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        biomePaletteSpinner.setValueFactory(biomePaletteValueFactory);
        biomePaletteSpinner.valueProperty().addListener(this::onBiomePaletteUpdate);
        biomePaletteSpinner.getEditor().setTextFormatter(new TextFormatter<Integer>(change -> {
            try {
                Integer.valueOf(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    public void disableComponents() {
        selectedBiome = null;
        biomeLayerValueFactory.setValue(0);
        biomeLayerSpinner.setDisable(true);
        biomePaletteValueFactory.setValue(0);
        biomePaletteSpinner.setDisable(true);
        biomePositionTextField.setText(null);
        biomePositionTextField.setDisable(true);
        biomeGrid.getChildren().clear();
    }

    public void enableComponents() {
        biomeLayerSpinner.setDisable(false);
        /*biomePaletteSpinner.setDisable(false);
        biomePositionTextField.setDisable(false);*/
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        disableComponents();
        if (this.schematic != null) {
            enableComponents();
            int[] size = this.schematic.getSize();
            biomeLayerValueFactory.setMax(size[1] - 1);
            int biomePaletteSize = this.schematic.getBiomePalette().size();
            biomePaletteValueFactory.setMax(biomePaletteSize - 1);
        }
    }

    private void onBiomeSelected(@NotNull Event e) {
        if (selectedBiome != null) {
            selectedBiome.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        }
        selectedBiome = (BiomeButton) e.getSource();
        Biome biome = selectedBiome.getBiome();

        if (biome != null) {
            selectedBiome.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));

            biomePositionTextField.setText(Arrays.toString(selectedBiome.getPosition()));
            biomePositionTextField.setDisable(false);

            biomePaletteValueFactory.setValue(biome.getBiomeStateIndex());
            biomePaletteSpinner.setDisable(false);
        }
    }

    @FXML
    public void onBiomeLayerUpdate(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (schematic != null) {
            updateBiomeGrid();
        }
    }

    @FXML
    public void onBiomePaletteUpdate(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (schematic != null && selectedBiome != null) {
            Biome biome = selectedBiome.getBiome();
            if (biome != null) {
                biome.setBiomeStateIndex(newValue);
            }
            updateBiomeGrid();
        }
    }

    public void updateBiomeGrid() {
        biomeGrid.getChildren().clear();
        if (schematic != null) {
            int[] size = schematic.getSize();
            int currentLayer = biomeLayerSpinner.getValue();
            for (int x = 0; x < size[0]; x++) {
                for (int z = 0; z < size[2]; z++) {
                    Biome biome = schematic.getBiome(x, currentLayer, z);
                    if (biome != null) {
                        BiomeState biomeState = schematic.getBiomeState(biome.getBiomeStateIndex());

                        String biomeId = biomeState.getId();
                        String biomeName = biomeId.substring(biomeId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                        Color color;
                        try {
                            color = GrassColor.valueOf(biomeName).getColor();
                        } catch (IllegalArgumentException e) {
                            color = MainController.MISSING_COLOR;
                        }
                        color = Color.color(color.getRed(), color.getGreen(), color.getBlue());
                        BiomeButton biomeButton = new BiomeButton(biome, x, currentLayer, z);
                        biomeButton.setText(!biomeName.isEmpty() ? biomeName.substring(0, 1) : "?");
                        biomeButton.setTooltip(new Tooltip(biomeId));
                        biomeButton.setTextOverrun(OverrunStyle.CLIP);
                        biomeButton.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                        biomeButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
                        biomeButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        biomeButton.setPrefSize(30.0, 30.0);
                        biomeButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, this::onBiomeSelected);
                        biomeGrid.add(biomeButton, x, z);
                        if (selectedBiome != null) {
                            int[] position = selectedBiome.getPosition();
                            if (Arrays.equals(position, new int[]{x, currentLayer, z})) {
                                selectedBiome = biomeButton;
                                // Set selected tile's border color to red
                                biomeButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
                            }
                        }
                    } else {
                        Color color = Color.WHITE;
                        BiomeButton biomeButton = new BiomeButton(null, x, currentLayer, z);
                        biomeButton.setText("");
                        biomeButton.setTextOverrun(OverrunStyle.CLIP);
                        biomeButton.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                        biomeButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
                        biomeButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        biomeButton.setPrefSize(30.0, 30.0);
                        biomeButton.setDisable(true);
                        biomeGrid.add(biomeButton, x, z);
                    }
                }
            }
        }
    }
}
