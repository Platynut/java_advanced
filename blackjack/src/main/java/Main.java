import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Sea;
import view.SeaView;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();

        stage.setTitle("Wa-Tor");
        stage.setScene(pane);
        stage.setFullScreen(true);
        stage.show();
    }
}
