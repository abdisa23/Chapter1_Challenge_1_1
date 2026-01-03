module com.example.chapter3_challenge_brandedweatherwidget {
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

    opens com.example.chapter3_challenge_brandedweatherwidget to javafx.fxml;
    exports com.example.chapter3_challenge_brandedweatherwidget;
}