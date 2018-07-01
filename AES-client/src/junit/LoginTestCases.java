package junit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.Test;
import control.UserControl;
import entity.User;

public class LoginTestCases extends UserControl {

	private static Semaphore sem;
	private static Semaphore sem1;
	private static boolean ifDetailsExist=false;
	private static boolean ifDetailsWrong=false;
	private static boolean ifUserConnected=false;
	private ChatClientStub chatclientstub;
	
	public LoginTestCases() {
	
		try {
			chatclientstub=new ChatClientStub(ip, DEFAULT_PORT, this);
			chat=chatclientstub;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void existingDetails() throws IOException, InterruptedException {
		loginPressed("k", "1234");
	
//		if(ifDetailsExist)
//			logoutPressed();
//	
		assertTrue(ifDetailsExist);
		System.out.println("aviv");
	
		
	}
	
	@Test
	void wrongPassword() throws IOException, InterruptedException {
		loginPressed("k", "123");
		assertTrue(ifDetailsWrong);
	} 
	
	@Test
	void wrongUserName() throws IOException, InterruptedException {
		loginPressed("s", "1234");
		assertTrue(ifDetailsWrong);
	} 
	@Test
	void userAlreadyConnected() throws IOException, InterruptedException {
		loginPressed("c", "1234");
		assertTrue(ifUserConnected);
	} 
	public void checkMessage(Object message) {
		Object[] msg = (Object[]) message;
		User user = (User) msg[1];
		ifDetailsExist = (user == null ? false : true);
		if( (ifDetailsExist == false) && (((String)msg[2]).equals("wrong")) )
		{
			ifDetailsWrong = true;
		}
		if( (ifDetailsExist == false) && (((String)msg[2]).equals("connected")) )
		{
			ifUserConnected = true;
		}
		setMyUser(user);
		
	}

}
