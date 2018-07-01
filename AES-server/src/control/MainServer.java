package control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class MainServer extends Application {
	private static Stage guiStage;
	/**
	 *  start(Stage primaryStage)
	 *  Arguments:Stage primaryStage
	 *  The method set the gui screen of the server connection
	 * 
	 * @author Aviv Mahulya
	 */
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/boundary/ConnectToDB.fxml"));
			guiStage = primaryStage;
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
			guiStage.setTitle("AES - Connect Server");
			guiStage.setScene(scene);
			guiStage.getIcons().setAll(new Image("Owl.png"));
			guiStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		launch(args);
	}

	public static Stage getStage() {
		return guiStage;
	}

}
