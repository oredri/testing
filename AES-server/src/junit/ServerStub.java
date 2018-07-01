package junit;

import java.util.concurrent.Semaphore;

import control.Server;

public class ServerStub extends Server{
public Object[] message;

	public ServerStub(String user, String pass) {
		super(user, pass);
		
		// TODO Auto-generated constructor stub
	}
public void	sendToAllClients(Object msg)
{
	message=(Object[])msg;
}

}
