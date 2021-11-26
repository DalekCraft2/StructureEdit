module me.dalekcraft.structureedit {
    // requires com.jogamp.opengl;
    requires com.google.common;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires jogl.all;
    requires NBT;
    requires org.jetbrains.annotations;
    requires org.joml;
    requires org.json;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.everit.json.schema;
    requires org.fusesource.jansi;
    requires org.fxmisc.richtext;

    exports me.dalekcraft.structureedit;
    opens me.dalekcraft.structureedit to javafx.controls, javafx.fxml;
    exports me.dalekcraft.structureedit.ui;
    opens me.dalekcraft.structureedit.ui to javafx.controls, javafx.fxml;
}
