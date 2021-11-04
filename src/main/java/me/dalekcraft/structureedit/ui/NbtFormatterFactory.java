package me.dalekcraft.structureedit.ui;

import javax.swing.*;

public class NbtFormatterFactory extends JFormattedTextField.AbstractFormatterFactory {

    private NbtFormatter formatter = new NbtFormatter();

    @Override
    public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
        return formatter;
    }
}
