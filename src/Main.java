import imageviewer.gui.controller.ImageViewController;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args)
    {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ImageView.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        ImageViewController imageViewController = loader.getController();
        imageViewController.setup();
        primaryStage.setTitle("Image Viewer");
        primaryStage.show();
    }
}
