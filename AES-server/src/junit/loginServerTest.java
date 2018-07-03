package junit;

import control.Server;
import entity.User;
import ocsf.server.ConnectionToClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.Test;

public class loginServerTest {
	private static ServerStub server;
	private Boolean ifDetailsExist;
	private Boolean ifDetailsWrong;

	public loginServerTest() {

		server = new ServerStub("root", "1234");
		ifDetailsExist = false;
		ifDetailsWrong = false;
	}
    /** This method check if the login process work with a correct details. **/
	@Test
	void existingDetails() throws IOException, InterruptedException {

		Object[] msg = new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "k";
		msg[2] = "1234";
		msg[4] = "k";

		server.handleMessageFromClient((Object) msg, null);
		if (server.message[1] != null) {
			if (server.message[0].equals("checkUserDetails")) {
				if (((User) server.message[1]).getUsername().equals(("k"))) {
					ifDetailsExist = true;
				}
			}
		} else {
			ifDetailsExist = false;
		}
		assertTrue(ifDetailsExist);

	}

	/** This method check if the login process work with a wrong password. **/
	@Test
	void wrongPassword() throws IOException, InterruptedException {
		ifDetailsWrong=false;
		Object[] msg = new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "k";
		msg[2] = "123";
		msg[4] = "k";

		server.handleMessageFromClient((Object) msg, null);

		if ((server.message[1] == null) && ((server.message[2].equals("wrong")))) {
			ifDetailsWrong = true;
		}
		assertTrue(ifDetailsWrong);

	}
	/** This method check if the login process work with a wrong username. **/
	@Test
	void wrongUserName() throws IOException, InterruptedException {
		ifDetailsWrong=false;
		Object[] msg = new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "b";  
		msg[2] = "1234";
		msg[4] = "b";

		server.handleMessageFromClient((Object) msg, null);

		if ((server.message[1] == null) && ((server.message[2].equals("wrong")))) {
			ifDetailsWrong = true;
		}
		assertTrue(ifDetailsWrong);
	}
	
	/** This method check if the login process work with a user which already connected. **/
	@Test
	void UserConnected() throws IOException, InterruptedException {
		ifDetailsWrong=false;
		Object[] msg = new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "s";
		msg[2] = "1234";
		msg[4] = "s";
		server.handleMessageFromClient((Object) msg, null);// first dummy login 
		server.handleMessageFromClient((Object) msg, null);//connect again
		if ((server.message[1] == null) && ((server.message[2].equals("connected")))) {
			ifDetailsWrong = true;
		}
		assertTrue(ifDetailsWrong);
	}
	/**
	 * This method check if the user that returned from server 
	 *  is the user that we axpects to returned
	 */
	@Test
	void correctDetailsFromServer() throws IOException, InterruptedException {
		ifDetailsWrong=true;//initialize to true and change to false if they true;
		Object[] msg = new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "AvivGibali";
		msg[2] = "1234";
		msg[4] = "AvivGibali";   
		server.handleMessageFromClient((Object) msg, null);
		User checkUser=new User( "AvivGibali","987654321","Aviv Gibali","1234","connected","teacher");
		User returnFromServer=(User)server.message[1];
		if(checkUser.getUsername().equals(returnFromServer.getUsername())&&
			checkUser.getUserID().equals(returnFromServer.getUserID())&&
			checkUser.getFullname().equals(returnFromServer.getFullname())&&
			checkUser.getPassword().equals(returnFromServer.getPassword())&&
			checkUser.getRole().equals(returnFromServer.getRole())&&
			checkUser.getStatus().equals(returnFromServer.getStatus()))
			ifDetailsWrong=false;
		assertTrue(!ifDetailsWrong);
	}
}
