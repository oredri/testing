package junit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.Test;
import control.UserControl;
import entity.User;

class LoginTestCases extends UserControl {

	private static Semaphore sem;
	private static Semaphore sem1;
	private static boolean ifDetailsExist=false;
	private static boolean ifDetailsWrong=false;

	public LoginTestCases() {
		sem = new Semaphore(0);
		sem1 = new Semaphore(0);
	}

	@Test
	void existingDetails() throws IOException, InterruptedException {
		loginPressed("k", "1234");
		sem.acquire();
		assertTrue(ifDetailsExist);
		if(ifDetailsExist)
			logoutPressed();
		System.out.println("aviv kah");
		sem1.release();
		System.out.println("aviv kah");
	}
	
	@Test
	void wrongPassword() throws IOException, InterruptedException {
		sem1.acquire();
		System.out.println("aviv");
		LoginTestCases user = new LoginTestCases();
		user.loginPressed("k", "123");
		sem.acquire();
		
		assertTrue(ifDetailsWrong);
	} 

	public void checkMessage(Object message) {
		Object[] msg = (Object[]) message;
		User user = (User) msg[1];
		ifDetailsExist = (user == null ? false : true);
		if( (ifDetailsExist == false) && (((String)msg[2]).equals("wrong")) )
		{
			ifDetailsWrong = true;
		}
		setMyUser(user);
		sem.release();
	}

}
