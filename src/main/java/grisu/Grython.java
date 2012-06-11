package grisu;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.cli.DefaultCliParameters;
import grisu.frontend.view.cli.GrisuCliClient;
import grith.gridsession.SessionClient;
import grith.jgrith.cred.Cred;

import org.python.util.jython;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grython extends GrisuCliClient<DefaultCliParameters> {

	public static final Logger myLogger = LoggerFactory
			.getLogger(SessionClient.class);

	public static ServiceInterface serviceInterface = null;

	public static Cred credential = null;

	public static boolean isLoggedIn = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LoginManager.initGrisuClient("grython");

		DefaultCliParameters params = new DefaultCliParameters();

		Grython s = null;
		try {
			s = new Grython(params, args);
		} catch (Exception e) {
			System.err.println("Could not start grython: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}
		s.run();

	}

	public Grython(DefaultCliParameters params, String[] args) throws Exception {
		super(params, args);
	}

	@Override
	protected void run() {
		try {

			credential = getCredential();
			serviceInterface = getServiceInterface();

			jython.main(getCliParameters().getOtherParams().toArray(
					new String[] {}));

		} catch (Exception e) {
			System.err.println("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(2);
		}

		System.exit(0);
	}

}
