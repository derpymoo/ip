package shinchan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

public class Main extends Application {

    private final Shinchan shinchan = new Shinchan();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane ap = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setShinchan(shinchan);

        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setTitle("Shinchan");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}