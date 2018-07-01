package junit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.Test;
import control.UserControl;
import entity.User;

class LoginTestCases extends UserControl {

	private static Semaphore sem;
	private static boolean ifDetailsExist=false;

	public LoginTestCases() {
		sem = new Semaphore(0);
	}

	@Test
	void test() throws IOException, InterruptedException {
		LoginTestCases user = new LoginTestCases();
		user.loginPressed("k", "1234");
		sem.acquire();
		assertTrue(ifDetailsExist);
	}

	public void checkMessage(Object message) {
		Object[] msg = (Object[]) message;
		User user = (User) msg[1];
		ifDetailsExist = (user == null ? false : true);
		sem.release();
	}

}
