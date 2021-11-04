package me.dalekcraft.structureedit.ui;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.Tag;

import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;

public class NbtFormatter extends JFormattedTextField.AbstractFormatter {

    /*@Override
    public Tag<?> stringToValue(String text) throws ParseException {
        try {
            return SNBTUtil.fromSNBT(text);
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        try {
            return SNBTUtil.toSNBT((Tag<?>) value);
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }
    }*/

    @Override
    public Object stringToValue(String text) throws ParseException {
        return text.trim();
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        return (String)value;
    }
}
