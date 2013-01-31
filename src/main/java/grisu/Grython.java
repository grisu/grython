package grisu;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.cli.GrisuCliClient;
import grisu.frontend.view.cli.GrisuCliParameters;
import grith.gridsession.SessionClient;
import grith.jgrith.cred.Cred;
import grith.jgrith.utils.CommandlineArgumentHelpers;

import java.util.Arrays;
import java.util.List;

import org.python.util.jython;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grython extends GrisuCliClient<GrisuCliParameters> {

	public static final Logger myLogger = LoggerFactory
			.getLogger(SessionClient.class);

	public static ServiceInterface serviceInterface = null;

	public static Cred credential = null;

	public static boolean isLoggedIn = false;
	
	public static List<String> cli_parameters;
	
	public final String[] args;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LoginManager.initGrisuClient("grython");

		GrisuCliParameters params = new GrisuCliParameters();
		
		Grython s = null;
		try {
			s = new Grython(params, args);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not start grython: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}
		s.run();

	}

	public Grython(GrisuCliParameters params, String[] args) throws Exception {
		super(params, args);
		this.args = args;
		cli_parameters = Arrays.asList(CommandlineArgumentHelpers.extractNonGridParameters(new GrisuCliParameters(), args));
		
		
	}

	@Override
	protected void run() {
		try {
			
			if (getLoginParameters().isNologin()) {
				jython.run(args);
			} else {

				credential = getCredential();
				serviceInterface = getServiceInterface();
			
				jython.run(cli_parameters.toArray(new String[]{}));
			}

		} catch (Exception e) {
			System.err.println("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(2);
		}

		System.exit(0);
	}

}
