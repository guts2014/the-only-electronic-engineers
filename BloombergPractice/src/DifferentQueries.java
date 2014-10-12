
	import com.bloomberglp.blpapi.Session;
	import com.bloomberglp.blpapi.SessionOptions;

	import java.util.ArrayList;
	import java.util.Scanner;



	public class DifferentQueries {

		public static void main (String[] args) throws Exception {
			String serverHost = "10.8.8.1";
			int serverPort = 8194;

			SessionOptions sessionOptions = new SessionOptions();
			sessionOptions.setServerHost(serverHost);
			sessionOptions.setServerPort(serverPort);

			System.out.println("Connecting to " + serverHost + ":" + serverPort);
			Session session = new Session(sessionOptions);
			if (!session.start()) {
				System.err.println("Failed to start session.");
				return;
			}
			if (!session.openService("//blp/refdata")) {
				System.err.println("Failed to open //blp/refdata");
				return;
			}
			

			String querey
			
			SecurityLookup sl = new SecurityLookup(session, query, results);
			
			SecurityDataRetreval data = new SecurityDataRetreval(session, sl.getSymbols());
			
			System.out.println(data.getDataValues());
			

		}
	}


}
