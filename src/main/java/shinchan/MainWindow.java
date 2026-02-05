package shinchan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainWindow {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Shinchan shinchan;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    public void setShinchan(Shinchan shinchan) {
        this.shinchan = shinchan;
        dialogContainer.getChildren().add(
                DialogBox.getShinchanDialog(shinchan.getWelcomeMessage())
        );
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = shinchan.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getShinchanDialog(response)
        );
        userInput.clear();

        if (shinchan.isExit()) {
            Platform.exit();
        }
    }
}