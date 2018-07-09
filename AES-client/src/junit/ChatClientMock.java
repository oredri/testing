package junit;

import java.io.IOException;

import control.ChatClient;
import control.UserControl;
import entity.User;

public class ChatClientMock extends ChatClient{
	public ChatClientMock(String host, int port, UserControl clientUI) throws IOException {
		super(host, port, clientUI);
		// TODO Auto-generated constructor stub
	}
	
	public void handleMessageFromServer(Object msg) {
		clientUI.checkMessage(msg);
	}
	public void handleMessageFromClientUI(Object[] message) {
		Object[] msgToClient=new Object[5];
		User user=new User();
	
			if((((String)message[1]).equals("k")) && ((String)message[2]).equals("1234")) {/*correct details*/
				user.setFullname("k");
				user.setPassword("1234");
				msgToClient[1]=user;
			}
			if((((String)message[1]).equals("k")) && ((String)message[2]).equals("123")) {/*wrong password*/
				msgToClient[1]=null;
				msgToClient[2]="wrong";
			}
			if((((String)message[1]).equals("orPisahov")) && ((String)message[2]).equals("1234")) {/*wrong user name*/
				msgToClient[1]=null;
				msgToClient[2]="wrong";
			}
			if((((String)message[1]).equals("c")) && ((String)message[2]).equals("1234")) {/*user already connected*/
				user.setFullname("c");
				msgToClient[1]=null;
				msgToClient[2]="connected";
			}
			handleMessageFromServer(msgToClient);
		
	}
}
