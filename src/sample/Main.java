package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class Main extends Application {
    private Controller controller;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = (Parent) loader.load();
        controller = loader.getController();
        primaryStage.setTitle("TailIt");
        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add("style/style.css");
        primaryStage.setScene(scene);

        resetLastLocation(root);

        primaryStage.show();
    }

    private void resetLastLocation(Parent root) {
        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        // get window location from user preferences: use x=100, y=100, width=400, height=400 as default
        double x = userPrefs.getDouble(ApplicationPreferences.STAGE_X.toString(), 100);
        double y = userPrefs.getDouble(ApplicationPreferences.STAGE_Y.toString(), 100);
        double w = userPrefs.getDouble(ApplicationPreferences.STAGE_WIDTH.toString(), 400);
        double h = userPrefs.getDouble(ApplicationPreferences.STAGE_HEIGHT.toString(), 400);

        primaryStage.setX(x);
        primaryStage.setY(y);
        primaryStage.setWidth(w);
        primaryStage.setHeight(h);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.stopExecutors();

        Preferences userPreferences = Preferences.userNodeForPackage(getClass());
        userPreferences.putDouble(ApplicationPreferences.STAGE_X.toString(), primaryStage.getX());
        userPreferences.putDouble(ApplicationPreferences.STAGE_Y.toString(), primaryStage.getY());
        userPreferences.putDouble(ApplicationPreferences.STAGE_WIDTH.toString(), primaryStage.getWidth());
        userPreferences.putDouble(ApplicationPreferences.STAGE_HEIGHT.toString(), primaryStage.getHeight());
    }
}
