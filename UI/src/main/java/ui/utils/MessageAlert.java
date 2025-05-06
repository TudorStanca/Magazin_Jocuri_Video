package ui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class MessageAlert {

    public static void showMessage(Stage owner, String title, String content) {
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.initOwner(owner);
        message.setTitle(title);
        message.setHeaderText(null);
        message.setContentText(content);
        message.showAndWait();
    }

    public static void showError(Stage owner, String error) {
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error");
        message.setHeaderText(null);
        message.setContentText(error);
        message.showAndWait();
    }

    public static boolean showConfirmation(Stage stage, String title, String content) {
        Alert message = new Alert(Alert.AlertType.CONFIRMATION);
        message.initOwner(stage);
        message.setTitle(title);
        message.setHeaderText(null);
        message.setContentText(content);

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");

        message.getButtonTypes().setAll(yes, no);

        Optional<ButtonType> result = message.showAndWait();
        return result.isPresent() && result.get() == yes;
    }
}