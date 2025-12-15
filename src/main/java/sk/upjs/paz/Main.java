package sk.upjs.paz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/security/LoginView.fxml"));
        fxmlLoader.setResources(SceneManager.getBundle());
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        SceneManager.applyTheme(scene);
        stage.setScene(scene);
        stage.setTitle("ZOOpervisor");
        stage.show();
    }
}