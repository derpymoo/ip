package shinchan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Collections;

public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    private void flip() {
        setAlignment(Pos.TOP_RIGHT);
        getChildren().clear();
        getChildren().addAll(dialog, displayPicture);
    }

    public static DialogBox getUserDialog(String text) {
        Image userImg = new Image(DialogBox.class.getResourceAsStream("/images/user.png"));
        DialogBox box = new DialogBox(text, userImg);
        box.flip();
        return box;
    }

    public static DialogBox getShinchanDialog(String text) {
        Image botImg = new Image(DialogBox.class.getResourceAsStream("/images/shinchan.png"));
        return new DialogBox(text, botImg);
    }
}