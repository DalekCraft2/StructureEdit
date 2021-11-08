module me.dalekcraft.structureedit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.fusesource.jansi;
    requires jogl.all;
    requires NBT;
    requires org.joml;
    requires org.json;
    requires org.everit.json.schema;

    exports me.dalekcraft.structureedit;
    opens me.dalekcraft.structureedit to javafx.controls, javafx.fxml;
    exports me.dalekcraft.structureedit.ui;
    opens me.dalekcraft.structureedit.ui to javafx.controls, javafx.fxml;
}
