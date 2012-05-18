package grisu.jython;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.utils.CommonCLIParser;
import grisu.frontend.utils.CommonCLIParser.OPTIONS;
import grisu.jcommons.dependencies.BouncyCastleTool;
import grisu.jcommons.utils.EnvironmentVariableHelpers;
import grisu.jcommons.utils.JythonHelpers;
import grith.gridsession.CliSessionControl;
import grith.gridsession.SessionClient;
import grith.gridsession.TinySessionServer;
import grith.jgrith.credential.Credential;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.python.util.jython;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.akuma.Daemon;

public class Session {

	public static final Logger myLogger = LoggerFactory
			.getLogger(SessionClient.class);

	public static ServiceInterface serviceInterface = null;

	public static boolean isLoggedIn = false;

	private static void doDaemonStuff(String[] args) {
		try {

			File file = new File("/tmp/jna");
			file.mkdirs();

			file.setWritable(true, false);

		} catch (Exception e) {
			myLogger.error("Can't create dir or change permissions for /tmp/jna: "
					+ e.getLocalizedMessage());
		}

		EnvironmentVariableHelpers.loadEnvironmentVariablesToSystemProperties();
		Thread.currentThread().setName("main");
		JythonHelpers.setJythonCachedir();
		try {
			BouncyCastleTool.initBouncyCastle();
		} catch (ClassNotFoundException e) {
			System.err
			.println("Can't find bouncy castle security provider in classpath, exiting...");
			System.exit(1);
		}

		Daemon d = new Daemon();

		if (d.isDaemonized()) {
			try {
				d.init();
			} catch (Exception e) {
				myLogger.error("Can't initialize daemon.", e);
				System.err
				.println("Can't initialize session deamon. Exiting...");
				System.exit(2);
			}
		} else {

			try {
				d.daemonize();
			} catch (IOException e) {
				myLogger.error("Can't daemonize client.", e);
				System.err
				.println("Can't daemonize session client, exiting...");
				System.exit(3);
			}

			runClient(args);
		}

		// make sure server is started...
		try {
			TinySessionServer server = new TinySessionServer();
		} catch (Exception e) {
			myLogger.error("Error when starting session server.", e);
			System.err.println("Can't start session server. Exiting...");
			System.exit(4);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JythonHelpers.setJythonCachedir();

		CommonCLIParser.parse(args);

		doDaemonStuff(args);
		// runClient();
	}

	private static void runClient(String[] args) {
		try {

			LoginManager.initGrisuClient("grython");

			Map<OPTIONS, Object> options = CommonCLIParser.parse(args);

			if ((options.get(OPTIONS.LOGOUT) != null)
					&& (Boolean) options.get(OPTIONS.LOGOUT)) {
				SessionClient client = SessionClient.create(false);
				client.getSessionManagement().logout();
				System.exit(0);
			}

			if (!((Boolean) options.get(OPTIONS.NOLOGIN))) {

				boolean force_autorenew = true;

				CliSessionControl control = new CliSessionControl(false, false);
				control.setSilent(true);
				SessionClient client = control.getSessionClient();

				if (!client.getSessionManagement().is_logged_in()
						|| (!client.getSessionManagement().is_auto_renew() && force_autorenew)) {

					if (((Boolean) options.get(OPTIONS.LOGIN))) {
						myLogger.debug("Executing login.");
						control.execute("login");
						myLogger.debug("Uploading to myproxy.");
						control.execute("upload");
						myLogger.debug("Logged in.");
					} else {
						System.err
						.println("No login session exists. Use the '-l' option with grython if you want interactive login.");
						System.exit(1);
					}
				}

				String localProxyPath = client.getSessionManagement()
						.proxy_path();
				myLogger.debug("Credential suposed to be: " + localProxyPath);

				Credential c = Credential.load(localProxyPath);
				try {
					ServiceInterface lsi = LoginManager.login(c,
							(String) options.get(OPTIONS.BACKEND), true);
					isLoggedIn = true;
					serviceInterface = lsi;
					myLogger.debug("Logged in...");
				} catch (LoginException le) {
					myLogger.error("Failed to login.", le);
					System.err.println(le.getLocalizedMessage());
					if ( le.getCause() != null ) {
						System.err.println("\tReason: "
								+ le.getCause().getLocalizedMessage());
					}
					System.exit(1);
				}


			}

			String[] rest = ((List<String>) options.get(OPTIONS.REST))
					.toArray(new String[] {});

			jython.main(rest);

		} catch (Exception e) {
			System.err.println("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(2);
		}

		System.exit(0);
	}

}
