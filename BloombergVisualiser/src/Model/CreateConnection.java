package Model;

import java.io.IOException;

import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;

/**
 * Creates a connection with the VPN and starts a session
 * @author laurenastell
 *
 */
public class CreateConnection {
	
	private Session session;

	public CreateConnection () {
		String serverHost = "10.8.8.1";
		int serverPort = 8194;

		SessionOptions sessionOptions = new SessionOptions();
		sessionOptions.setServerHost(serverHost);
		sessionOptions.setServerPort(serverPort);

		System.out.println("Connecting to " + serverHost + ":" + serverPort);
		 session = new Session(sessionOptions);
		try {
			if (!session.start()) {
				System.err.println("Failed to start session.");
				return;
			}

			if (!session.openService("//blp/refdata")) {
				System.err.println("Failed to open //blp/refdata");
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Session getSession() {
		return this.session;
	}

}
