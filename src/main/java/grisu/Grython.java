package grisu;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManagerNew;
import grisu.frontend.view.cli.GrisuCliClient;
import grith.gridsession.SessionClient;
import grith.jgrith.cred.Cred;

import org.python.util.jython;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grython extends GrisuCliClient {

	public static final Logger myLogger = LoggerFactory
			.getLogger(SessionClient.class);

	public static ServiceInterface serviceInterface = null;

	public static Cred credential = null;

	public static boolean isLoggedIn = false;


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LoginManagerNew.initGrisuClient("grython");

		Grython s = new Grython(args);

		execute(s, false);
		// runClient();
	}


	public Grython(String[] args) {
		super(args);
	}

	@Override
	public void run() {
		try {

			credential = getCredential();
			serviceInterface = getServiceInterface();

			jython.main(getOtherParameters().toArray(new String[] {}));

		} catch (Exception e) {
			System.err.println("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(2);
		}

		System.exit(0);
	}

}
