module me.dalekcraft.structureedit {
    requires com.google.common;
    requires com.google.gson;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
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
    // requires org.apache.commons.lang3; // This is for a newer version of commons-lang3, which is provided with commons-configuration2, which I might use

    exports me.dalekcraft.structureedit;
    opens me.dalekcraft.structureedit to javafx.controls, javafx.fxml;
    exports me.dalekcraft.structureedit.ui;
    opens me.dalekcraft.structureedit.ui to javafx.controls, javafx.fxml;
    opens me.dalekcraft.structureedit.schematic.io.legacycompat to com.google.gson;
    exports me.dalekcraft.structureedit.ui.editor;
    opens me.dalekcraft.structureedit.ui.editor to javafx.controls, javafx.fxml;
}
