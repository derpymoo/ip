package shinchan;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);

        // ✅ Key fixes for "..." truncation
        dialog.setWrapText(true);
        dialog.setTextOverrun(OverrunStyle.CLIP); // avoid ellipsis when constrained
        dialog.setMinWidth(0); // allow the label to shrink within HBox
        HBox.setHgrow(dialog, Priority.ALWAYS); // let label take remaining width

        // Constrain bubble width so text wraps (tweak 0.70 as you like)
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.70));
        dialog.prefWidthProperty().bind(widthProperty().multiply(0.70));

        displayPicture.setImage(img);
        displayPicture.setPreserveRatio(true);
        displayPicture.setFitWidth(32);
        displayPicture.setFitHeight(32);

        // Crop avatar into a circle (32x32)
        Circle clip = new Circle(16, 16, 16);
        displayPicture.setClip(clip);

        // Base style class for the whole message row
        getStyleClass().add("dialog-box");
    }

    /**
     * Formats this dialog as a left-aligned bot message.
     */
    private void formatAsBot() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().addAll("bubble", "bubble-bot");
    }

    /**
     * Formats this dialog as a right-aligned user message.
     * Reorders children so bubble appears before avatar.
     */
    private void formatAsUser() {
        setAlignment(Pos.TOP_RIGHT);
        dialog.getStyleClass().addAll("bubble", "bubble-user");

        ObservableList<Node> children = getChildren();
        if (children.size() == 2) {
            Node avatar = children.get(0);
            Node bubble = children.get(1);
            children.setAll(bubble, avatar);
        }
    }

    /**
     * Formats this dialog as a left-aligned error message.
     */
    private void formatAsError() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().addAll("bubble", "bubble-error");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.formatAsUser();
        return db;
    }

    public static DialogBox getShinchanDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.formatAsBot();
        return db;
    }

    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox("⚠ " + text, img);
        db.formatAsError();
        return db;
    }
}
