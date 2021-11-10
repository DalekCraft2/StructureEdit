/*
 * Created by JulianG (https://stackoverflow.com/users/2039619/juliang)
 * https://stackoverflow.com/a/20282301
 */
package me.dalekcraft.structureedit.ui;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    private ObservableList<T> items;
    private boolean moveCaretToPos;
    private int caretPos;

    public AutoCompleteComboBoxListener(final @NotNull ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        items = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(this);
    }

    @Override
    public void handle(@NotNull KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.isControlDown() || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        FilteredList<T> list = new FilteredList<>(items, t -> t.toString().toLowerCase(Locale.ROOT).contains(comboBox.getEditor().getText().toLowerCase(Locale.ROOT)));
        String t = comboBox.getEditor().getText();

        comboBox.setItems(list);
        comboBox.getEditor().setText(t);
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if (!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

    public void setItems(ObservableList<T> items) {
        this.items = items;
    }
}
