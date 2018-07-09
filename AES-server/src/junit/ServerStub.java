package junit;

import control.Server;

public class ServerStub extends Server {
	public Object[] message;

	public ServerStub(String user, String pass) {
		super(user, pass);
	}

	public void sendToAllClients(Object msg) {
		message = (Object[]) msg;
	}

}
