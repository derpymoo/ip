package shinchan;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Shinchan shinchan;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image shinchanImage = new Image(this.getClass().getResourceAsStream("/images/shinchan.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Shinchan instance and shows the welcome message immediately.
     */
    public void setShinchan(Shinchan d) {
        shinchan = d;

        // Show welcome message on startup
        String welcome = shinchan.getWelcomeMessage();
        dialogContainer.getChildren().add(
                DialogBox.getShinchanDialog(welcome, shinchanImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Shinchan's reply,
     * then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        // Optional: ignore empty input (prevents ugly blank bubbles)
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String response = shinchan.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getShinchanDialog(response, shinchanImage)
        );

        userInput.clear();

        // If user typed bye, close the window
        if (shinchan.isExit()) {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.close();
        }
    }
}