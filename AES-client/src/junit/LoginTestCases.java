package junit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import control.UserControl;
import entity.User;

public class LoginTestCases extends UserControl {

	private static boolean ifDetailsExist = false;
	private static boolean ifDetailsWrong = false;
	private static boolean ifUserConnected = false;
	
	private ChatClientMock chatclientmock;

	public LoginTestCases() {
		try {
			chatclientmock = new ChatClientMock(ip, DEFAULT_PORT, this);
			chat = chatclientmock;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** This method check if the login process work with a correct details. **/
	@Test
	void existingDetails() throws IOException, InterruptedException {

		userNameFromLogin = "k";
		passwordLogin = "1234";
		loginPressed(null);
		assertTrue(ifDetailsExist);

	}

	/** This method check if the login process work with a wrong password. **/
	@Test
	void wrongPassword() throws IOException, InterruptedException {

		userNameFromLogin = "k";
		passwordLogin = "123";

		loginPressed(null);
		assertTrue(ifDetailsWrong);
	}

	/** This method check if the login process work with a wrong username. **/
	@Test
	void wrongUserName() throws IOException, InterruptedException {
		
		userNameFromLogin = "orPisahov";
		passwordLogin = "1234";
		
		loginPressed(null);
		assertTrue(ifDetailsWrong);

	}

	/**
	 * This method check if the login process work with a user which already
	 * connected.
	 **/
	@Test
	void userAlreadyConnected() throws IOException, InterruptedException {

		userNameFromLogin = "c";
		passwordLogin = "1234";
		
		loginPressed(null);
		assertTrue(ifUserConnected);

	}

	/** This method simulates receiving a message from the server **/
	public void checkMessage(Object message) {
		Object[] msg = (Object[]) message;
		User user = (User) msg[1];
		ifDetailsExist = (user == null ? false : true);
		if ((ifDetailsExist == false)
				&& (((String) msg[2]).equals("wrong")))/* if the user is null and the message is "wrong" */
		{
			ifDetailsWrong = true;
		}
		if ((ifDetailsExist == false)
				&& (((String) msg[2]).equals("connected")))/* if the user is null and the message is "connected" */
		{
			ifUserConnected = true;
		}
		setMyUser(user);

	}

	public Boolean connect(UserControl user) {
		try {
			chat = new ChatClientMock(ip, DEFAULT_PORT, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}