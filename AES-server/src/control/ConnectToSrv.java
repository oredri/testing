package control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConnectToSrv implements Initializable {
	@FXML
	private TextField serverId;
	@FXML
	private TextField dbName;
	@FXML
	private TextField dbPass;
	@FXML
	private Button connectBtn;
	@FXML
	private Label srv;
	@FXML
	private Label dbn;
	@FXML
	private Label dbp;
	@FXML
	private Label connectedLabel;

	public void initialize(URL url, ResourceBundle rb) {

		connectedLabel.setVisible(false);

	}
	/**
	 * connectPressed(ActionEvent e) 
	 *  Arguments:ActionEvent e 
	 *  The method Handle the connect to server button
	 * The method connect to the server
	 * 
	 * @author Tom Zarhin
	 */
	public void connectPressed(ActionEvent e) {
		Server s = new Server(dbName.getText(), dbPass.getText());
		try {
			s.listen();
			connectBtn.setVisible(false);
			connectedLabel.setVisible(true);
			dbName.setDisable(true);
			dbPass.setDisable(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("ERROR - Could not listen for clients!");
		}

	}
}
