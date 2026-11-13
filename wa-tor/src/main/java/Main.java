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
        Sea sea = new Sea(120, 80);
        sea.initialize();

        SeaView seaView = new SeaView(sea);
        Scene scene = new Scene(seaView, 1200, 800);
        seaView.attach(scene);

        stage.setTitle("Wa-Tor");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
