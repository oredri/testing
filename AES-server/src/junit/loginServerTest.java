package junit;

import control.Server;
import entity.User;
import ocsf.server.ConnectionToClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.Test;

public class loginServerTest   {
	private static ServerStub server;
	private Boolean ifDetailsExist;
	private Boolean ifDetailsWrong;
	
	public loginServerTest() {
	
			server=new ServerStub("root","1234");
			ifDetailsExist=false;
			ifDetailsWrong=false;
			
	}
	@Test
	void existingDetails() throws IOException, InterruptedException {/*check with correct details*/
		
		Object [] msg=new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "k";
		msg[2] = "1234";
		msg[4] ="k";

		server.handleMessageFromClient((Object)msg,null);
		if(server.message[1]!=null)
		{
		if(server.message[0].equals("checkUserDetails"))
		{
			if(((User)server.message[1]).getUsername().equals(("k")))
					{
					ifDetailsExist=true;
					}
		}
		}
		else {
			ifDetailsExist=false;
		}
		assertTrue(ifDetailsExist);
	
	}

	@Test
	void wrongPassword() throws IOException, InterruptedException {/*check with wrong password */
		
		Object [] msg=new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "k";
		msg[2] = "123";
		msg[4] ="k";
		
		server.handleMessageFromClient((Object)msg,null);
		
		if((server.message[1]==null) && ((server.message[2].equals("wrong"))))
		{
			ifDetailsWrong=true;
		}
		assertTrue(ifDetailsWrong);
		
	}
	@Test
	void wrongUserName() throws IOException, InterruptedException {/*check with wrong userName */
		
		Object [] msg=new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "b";
		msg[2] = "1234";
		msg[4] ="b";
		
		server.handleMessageFromClient((Object)msg,null);
		
		if((server.message[1]==null) && ((server.message[2].equals("wrong"))))
		{
			ifDetailsWrong=true;
		}
		assertTrue(ifDetailsWrong);
	}
	@Test
	void UserConnected() throws IOException, InterruptedException {/*check with user which already connected */
		
		Object [] msg=new Object[5];
		msg[0] = "checkUserDetails";
		msg[1] = "s";
		msg[2] = "1234";
		msg[4] ="s";
		server.handleMessageFromClient((Object)msg,null);
		
		if((server.message[1]==null) && ((server.message[2].equals("connected"))))
		{
			ifDetailsWrong=true;
		}
		assertTrue(ifDetailsWrong);
		
	}
}
