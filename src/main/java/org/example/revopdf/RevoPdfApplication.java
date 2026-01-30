package org.example.revopdf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RevoPdfApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                RevoPdfApplication.class.getResource("view/revo-pdf-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Revo PDF Editor");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
