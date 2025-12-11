package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


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

    public static <T> void changeSceneWithData(Node sourceNode, String fxmlPath, String title, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();


            T controller = loader.getController();


            if (controllerSetup != null) {
                controllerSetup.accept(controller);
            }


            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            System.err.println("Chyba pri výmene scény s dátami: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static <T, C> void setupDoubleClick(TableView<T> tableView,
                                               String fxmlPath,
                                               String title,
                                               BiConsumer<C, T> dataTransfer) {

        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    T selectedItem = row.getItem();

                    changeSceneWithData(
                            tableView,
                            fxmlPath,
                            title,
                            (C controller) -> dataTransfer.accept(controller, selectedItem)
                    );
                }
            });
            return row;
        });
    }



}
