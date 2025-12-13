package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.prefs.Preferences;


public class SceneManager {

    private static final Preferences prefs = Preferences.userNodeForPackage(SceneManager.class);
    private static final String THEME_KEY = "is_dark_mode";
    private static final String LANG_KEY = "language_code";
    private static Locale currentLocale = new Locale(prefs.get(LANG_KEY, "sk"));

    private static boolean isDarkMode = prefs.getBoolean(THEME_KEY, false);

    public static void toggleLanguage() {
        if (currentLocale.getLanguage().equals("sk")) {
            currentLocale = new Locale("en");
        } else {
            currentLocale = new Locale("sk");
        }
        prefs.put(LANG_KEY, currentLocale.getLanguage());
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("preklad", currentLocale);
    }

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        prefs.putBoolean(THEME_KEY, isDarkMode);
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(SceneManager.class.getResource("/sk.upjs.paz/darkMode.css")).toExternalForm());
        }
    }

    public static void changeScene(ActionEvent event, String fxmlPath, String title) {
        try {

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            loader.setResources(getBundle());
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            applyTheme(scene);
            stage.setScene(scene);
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
            loader.setResources(getBundle());
            Parent root = loader.load();


            T controller = loader.getController();


            if (controllerSetup != null) {
                controllerSetup.accept(controller);
            }

            Scene scene = new Scene(root);
            applyTheme(scene);
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(scene);
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

    public static boolean confirm(String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrdenie");
        alert.setHeaderText(null);
        alert.setContentText(question);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            loader.setResources(getBundle());
            Parent root = loader.load();

            Scene scene = new Scene(root);
            applyTheme(scene);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(true);
            stage.setMaximized(true);

            stage.show();

            stage.setFullScreenExitHint("");

        } catch (IOException e) {
            System.err.println("Chyba pri načítaní FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
