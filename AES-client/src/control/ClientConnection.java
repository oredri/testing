package control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClientConnection implements Initializable {
	@FXML
	private TextField serverIpTextField;
	@FXML
	private Button connectButton;
	@FXML
	private Label errorLabel;
	private Parent home_page_parent;
	private Scene home_page_scene;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		errorLabel.setVisible(false);
	}

	public void connectPressed(ActionEvent e) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Boolean isConnected;
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/boundary/LoginGui.fxml"));
					home_page_parent = loader.load();
					home_page_parent.getStylesheets()
					.add(getClass().getResource("/style.css").toExternalForm());
					UserControl uController = loader.getController();
					uController.setServerIp(serverIpTextField.getText());
					;/* send the name to the controller */
					isConnected = uController.connect(uController);
					if (isConnected == false) {
						errorLabel.setVisible(true);
					} else {
						home_page_scene = new Scene(home_page_parent);
						// sController.setHomePScene(home_page_scene);
						Main.getStage().setTitle("Login");
						Main.getStage().setScene(home_page_scene);
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		});
	}

}
