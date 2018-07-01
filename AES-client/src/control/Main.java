package control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	private static Stage guiStage;

	public void start(Stage primaryStage) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource("/boundary/ClientConnect.fxml"));
			guiStage = primaryStage;
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
			guiStage.setTitle("AES-Login");
			guiStage.setScene(scene);
			Main.getStage().getIcons().setAll(new Image("Owl.png"));
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