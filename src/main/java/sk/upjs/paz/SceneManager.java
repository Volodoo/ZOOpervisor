package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SceneManager {
    public static void changeScene(ActionEvent event, String fxmlPath, String title) {
        try {

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(true);

            stage.show();

        } catch (IOException e) {
            System.err.println("Chyba pri načítaní FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
