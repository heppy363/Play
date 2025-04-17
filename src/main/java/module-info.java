module com.matteorossi.play {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;
    requires telegrambots.meta;
    requires telegrambots;
    requires org.slf4j;
    requires jersey.common;

    opens com.matteorossi.play to javafx.fxml;
    exports com.matteorossi.play;
    exports com.matteorossi.play.controllers;
    opens com.matteorossi.play.controllers to javafx.fxml;
    opens com.matteorossi.play.models to javafx.base;
}