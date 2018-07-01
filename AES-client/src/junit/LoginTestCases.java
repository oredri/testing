package junit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.Test;
import control.UserControl;
import entity.User;

public class LoginTestCases extends UserControl {

	private static Semaphore  sem;/*This semaphore used in order to wait to the previous test to finish*/
	private static Semaphore sem1;/*This semaphore used in order to wait to the server answer*/
	private static boolean ifDetailsExist=false;
	private static boolean ifDetailsWrong=false;
	private static boolean ifUserConnected=false;
	private ChatClientStub chatclientstub;
	
	public LoginTestCases() {
	
		try {
			chatclientstub=new ChatClientStub(ip, DEFAULT_PORT, this);
			chat=chatclientstub;
			
			sem=new Semaphore(1);
			sem1=new Semaphore(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void existingDetails() throws IOException, InterruptedException {/*check with correct details*/
		sem.acquire();
		userNameFromLogin="k";
		passwordLogin="1234";
		loginPressed(null);
		sem1.acquire();/*wait for the server answer*/
		assertTrue(ifDetailsExist);
		sem.release();
	}
	
	@Test
	void wrongPassword() throws IOException, InterruptedException {/*check with wrong password */
		sem.acquire();
		userNameFromLogin="k";
		passwordLogin="123";
		loginPressed(null);
		sem1.acquire();/*wait for the server answer*/
		assertTrue(ifDetailsWrong);
		sem.release();
	} 
	
	@Test
	void wrongUserName() throws IOException, InterruptedException {/*check with wrong username */
		sem.acquire();
		userNameFromLogin="s";
		passwordLogin="1234";
		loginPressed(null);
		sem1.acquire();/*wait for the server answer*/
		assertTrue(ifDetailsWrong);
		sem.release();
	} 
	@Test
	void userAlreadyConnected() throws IOException, InterruptedException {/*check with user which already connected */
		sem.acquire();
		userNameFromLogin="c";
		passwordLogin="1234";
		loginPressed(null);
		sem1.acquire();/*wait for the server answer*/
		assertTrue(ifUserConnected);
		
	} 
	public void checkMessage(Object message) {
		Object[] msg = (Object[]) message;
		User user = (User) msg[1];
		ifDetailsExist = (user == null ? false : true);
		if( (ifDetailsExist == false) && (((String)msg[2]).equals("wrong")))/*if the user is null and the message is "wrong"*/
		{
			ifDetailsWrong = true;
		}
		if( (ifDetailsExist == false) && (((String)msg[2]).equals("connected")) )/*if the user is null and the message is "connected"*/
		{
			ifUserConnected = true;
		}
		setMyUser(user);
		sem1.release();
	}

}
