package np.edu.kathford.www;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import np.edu.kathford.www.util.Log;

public class HideApplication extends Application {
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hide It");
        primaryStage.getIcons().add(new Image("/logo.png"));
        BorderPane root = FXMLLoader.load(getClass().getResource("/main.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(600);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        primaryStage.toFront();
    }

    public static void main(String[] args) {
        Log.debug = false;
        LauncherImpl.launchApplication(HideApplication.class, SplashScreenLoader.class, args);
    }
}
