package mvc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mvc.screens.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaFXApp extends Application {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main (String[] args){
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        LOGGER.info("in init");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        LOGGER.info("in stop");
    }

    @Override
    public void start(Stage stage) throws Exception {
        LOGGER.info("before start");

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/main_view.fxml"));
        loader.setController(MainController.getInstance());
        Parent rootNode = loader.load();
        stage.setScene(new Scene(rootNode));

        stage.setTitle("CS 4743 Fall 2021");
        stage.show();


        LOGGER.info("after start");
    }
}
