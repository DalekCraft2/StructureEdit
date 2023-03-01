module me.dalekcraft.structureedit {
    requires com.google.common;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires NBT;
    requires org.jetbrains.annotations;
    requires org.joml;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.fusesource.jansi;
    requires org.fxmisc.richtext;

    requires datafixerupper;
    requires brigadier;
    requires commons.lang3;
    requires fastutil;

    exports me.dalekcraft.structureedit;
    opens me.dalekcraft.structureedit to javafx.controls, javafx.fxml;
    exports me.dalekcraft.structureedit.ui;
    opens me.dalekcraft.structureedit.ui to javafx.controls, javafx.fxml;
    opens me.dalekcraft.structureedit.schematic.io.legacycompat to com.google.gson;
    exports me.dalekcraft.structureedit.ui.editor;
    opens me.dalekcraft.structureedit.ui.editor to javafx.controls, javafx.fxml;
}
