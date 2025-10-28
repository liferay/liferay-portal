/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.upgrade.client;

import com.liferay.gogo.shell.client.GogoShellClient;
import com.liferay.portal.tools.db.upgrade.client.util.Properties;
import com.liferay.portal.tools.db.upgrade.client.util.TeePrintStream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jline.console.ConsoleReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author David Truong
 */
public class DBUpgradeClient {

	public static void main(String[] args) {
		Options options = _getOptions();

		try {
			CommandLineParser commandLineParser = new DefaultParser();

			CommandLine commandLine = commandLineParser.parse(options, args);

			if (commandLine.hasOption("help")) {
				new HelpFormatter(
				).printHelp(
					"Liferay Portal Tools Database Upgrade Client", options
				);

				return;
			}

			List<String> jvmOpts = new ArrayList<>();

			if (commandLine.hasOption("jvm-opts")) {
				String optionValue = commandLine.getOptionValue("jvm-opts");

				Collections.addAll(jvmOpts, optionValue.split(" "));
			}
			else {
				jvmOpts.add("-Dfile.encoding=UTF8");
				jvmOpts.add("-Duser.country=US");
				jvmOpts.add("-Duser.language=en");
				jvmOpts.add("-Duser.timezone=GMT");
				jvmOpts.add("-Xmx4096m");
			}

			if (commandLine.hasOption("debug")) {
				jvmOpts.add(
					"-agentlib:jdwp=transport=dt_socket,address=8001,server=" +
						"y,suspend=y");
			}

			File logDir = new File(_jarDir, "logs");

			if ((logDir != null) && !logDir.exists()) {
				logDir.mkdirs();
			}

			File logFile = null;

			if (commandLine.hasOption("log-file")) {
				logFile = new File(
					logDir, commandLine.getOptionValue("log-file"));
			}
			else {
				logFile = new File(logDir, "upgrade.log");
			}

			if (logFile.exists()) {
				String logFileName = logFile.getName();

				logFile.renameTo(
					new File(
						logDir, logFileName + "." + logFile.lastModified()));

				logFile = new File(logDir, logFileName);
			}

			boolean shell = false;

			if (commandLine.hasOption("shell")) {
				shell = true;
			}

			DBUpgradeClient dbUpgradeClient = new DBUpgradeClient(
				jvmOpts, logFile, shell);

			dbUpgradeClient.upgrade();
		}
		catch (ParseException parseException) {
			System.err.println("Unable to parse command line properties:");

			parseException.printStackTrace();

			new HelpFormatter(
			).printHelp(
				"Liferay Portal Tools Database Upgrade Client", options
			);
		}
		catch (Exception exception) {
			System.err.println("Error running upgrade:");

			exception.printStackTrace();
		}
	}

	public DBUpgradeClient(List<String> jvmOpts, File logFile, boolean shell)
		throws IOException {

		_jvmOpts = jvmOpts;
		_logFile = logFile;
		_shell = shell;

		_appServerPropertiesFile = new File(_jarDir, "app-server.properties");

		_appServerProperties = _readProperties(_appServerPropertiesFile);

		_fileOutputStream = new FileOutputStream(_logFile);

		_portalUpgradeDatabasePropertiesFile = new File(
			_jarDir, "portal-upgrade-database.properties");

		_portalUpgradeDatabaseProperties = _readProperties(
			_portalUpgradeDatabasePropertiesFile);

		_portalUpgradeExtPropertiesFile = new File(
			_jarDir, "portal-upgrade-ext.properties");

		_portalUpgradeExtProperties = _readProperties(
			_portalUpgradeExtPropertiesFile);
	}

	public void upgrade() throws IOException {
		verifyProperties();

		System.setOut(new TeePrintStream(_fileOutputStream, System.out));

		ProcessBuilder processBuilder = new ProcessBuilder();

		List<String> commands = new ArrayList<>();

		String javaHome = _JAVA_HOME;

		if (javaHome == null) {
			javaHome = System.getProperty("java.home");
		}

		commands.add(javaHome + "/bin/java");

		_jvmOpts.add("-Dexternal-properties=portal-upgrade.properties");
		_jvmOpts.add(
			"-Dliferay.shielded.container.lib.portal.dir=" +
				_appServer.getPortalShieldedContainerLibDir());
		_jvmOpts.add(
			"-Dserver.detector.server.id=" +
				_appServer.getServerDetectorServerId());

		commands.add("-cp");
		commands.add(_getBootstrapClassPath());

		System.out.println("JVM arguments: " + _jvmOpts.toString());

		commands.addAll(_jvmOpts);

		commands.add(DBUpgraderLauncher.class.getName());

		processBuilder.command(commands);
		processBuilder.directory(_jarDir);

		processBuilder.redirectErrorStream(true);

		Map<String, String> environment = processBuilder.environment();

		if (_isGTJDK8()) {
			environment.put("JDK_JAVA_OPTIONS", _buildJDKJavaOptions());
		}

		Process process = processBuilder.start();

		boolean upgradeFailed = false;

		try (ObjectOutputStream bootstrapObjectOutputStream =
				new ObjectOutputStream(process.getOutputStream());
			InputStreamReader inputStreamReader = new InputStreamReader(
				process.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader)) {

			bootstrapObjectOutputStream.writeObject(_getClassPath());

			bootstrapObjectOutputStream.flush();

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("UpgradeRecorder") &&
					(line.contains("fail") || line.contains("unresolved"))) {

					upgradeFailed = true;
				}

				if (line.equals("Exiting DBUpgrader#main(String[]).")) {
					break;
				}

				System.out.println(line);
			}

			System.out.flush();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		if (upgradeFailed || _shell) {
			String message = "Connecting to Gogo shell";

			if (upgradeFailed) {
				message += " because the upgrade failed or is incomplete";
			}

			System.out.println(message);

			try (GogoShellClient gogoShellClient = _initGogoShellClient()) {
				if (_isPortalUpgradeFinished(gogoShellClient)) {
					_printHelp();

					_consoleReader.setPrompt(_GOGO_SHELL_PREFIX);

					String line = _consoleReader.readLine();

					if (line == null) {
						System.out.println("Unable to open Gogo shell");
					}

					while (line != null) {
						if (!_processGogoShellCommand(gogoShellClient, line)) {
							break;
						}

						line = _consoleReader.readLine();
					}
				}
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		_close(process.getErrorStream());
		_close(process.getInputStream());
		_close(process.getOutputStream());

		process.destroy();
	}

	public void verifyProperties() {
		File file = new File(
			System.getProperty("user.home") + "/portal-ext.properties");

		if (file.exists()) {
			System.err.println(
				"Remove " + file + " prior to running an upgrade to prevent " +
					"possible conflicts.");

			System.exit(0);
		}

		try {
			_verifyPortalUpgradeExtProperties();

			_verifyAppServerProperties();

			_verifyPortalUpgradeDatabaseProperties();

			_saveProperties();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static Options _getOptions() {
		Options options = new Options();

		options.addOption(
			new Option("d", "debug", false, "Debug the upgrade JVM."));
		options.addOption(
			new Option("h", "help", false, "Print help message."));
		options.addOption(
			new Option(
				"j", "jvm-opts", true,
				"Set the JVM_OPTS used for the upgrade."));
		options.addOption(
			new Option("l", "log-file", true, "Set the name of log file."));
		options.addOption(
			new Option(
				"s", "shell", false, "Automatically connect to GoGo shell."));

		return options;
	}

	private void _appendClassPath(StringBuilder sb, File dir)
		throws IOException {

		if (dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				String fileName = file.getName();

				if (file.isFile() && fileName.endsWith("jar")) {
					sb.append(file.getCanonicalPath());
					sb.append(File.pathSeparator);
				}
				else if (file.isDirectory()) {
					_appendClassPath(sb, file);
				}
			}
		}
	}

	private void _appendClassPath(StringBuilder sb, List<File> dirs)
		throws IOException {

		for (File dir : dirs) {
			_appendClassPath(sb, dir);
		}
	}

	private String _buildJDKJavaOptions() {
		StringBuilder sb = new StringBuilder();

		for (String reflectionOpen : _reflectionOpens) {
			sb.append(reflectionOpen);
			sb.append(' ');
		}

		if (!_reflectionOpens.isEmpty()) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	private void _close(Closeable closeable) throws IOException {
		closeable.close();
	}

	private String _getBootstrapClassPath() throws IOException {
		StringBuilder sb = new StringBuilder();

		_appendClassPath(sb, _jarDir);

		return sb.toString();
	}

	private String _getClassPath() throws IOException {
		StringBuilder sb = new StringBuilder();

		String liferayClassPath = System.getenv("LIFERAY_CLASSPATH");

		if ((liferayClassPath != null) && !liferayClassPath.isEmpty()) {
			sb.append(liferayClassPath);
			sb.append(File.pathSeparator);
		}

		_appendClassPath(sb, new File(_jarDir, "lib"));

		_appendClassPath(sb, _jarDir);

		sb.append(_appServer.getPortalClassesDir());
		sb.append(File.pathSeparator);

		_appendClassPath(sb, _appServer.getPortalLibDir());

		_appendClassPath(sb, _appServer.getPortalShieldedContainerLibDir());

		_appendClassPath(sb, _appServer.getGlobalLibDir());

		_appendClassPath(sb, _appServer.getExtraLibDirs());

		return sb.toString();
	}

	private File _getResolvedDir(
		boolean allowAbsolutePaths, File baseDir, String dirName) {

		File dir = new File(dirName);

		if (dir.isAbsolute()) {
			if (allowAbsolutePaths) {
				return dir;
			}

			System.err.println(dirName + " is not a relative path.");

			return null;
		}

		return new File(baseDir, dirName);
	}

	private GogoShellClient _initGogoShellClient() throws IOException {
		String value = _portalUpgradeExtProperties.getProperty(
			"module.framework.properties.osgi.console");

		if (value == null) {
			return new GogoShellClient();
		}

		Matcher matcher = _gogoShellAddressPattern.matcher(value);

		if (!matcher.find()) {
			return new GogoShellClient();
		}

		String host = matcher.group(1);
		int port = Integer.parseInt(matcher.group(2));

		return new GogoShellClient(host, port);
	}

	private boolean _isGTJDK8() {
		String javaVersion = System.getProperty("java.version");

		int majorVersion = Integer.parseInt(
			javaVersion.substring(0, javaVersion.indexOf('.')));

		if (majorVersion > 8) {
			return true;
		}

		return false;
	}

	private boolean _isPortalUpgradeFinished(GogoShellClient gogoShellClient)
		throws IOException {

		String upgradeList = gogoShellClient.send("upgrade:list");

		if (upgradeList.contains("CommandNotFoundException")) {
			System.out.print("Portal upgrade failed. Fix the issue and retry.");

			return false;
		}

		return true;
	}

	private boolean _isValidDir(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}

		System.err.println(
			dir.getAbsolutePath() + " does not exist or is not a directory.");

		return false;
	}

	private void _printHelp() {
		System.out.println(
			"\nType \"help\" to get available upgrade and verify commands.");

		System.out.println(
			"Type \"help {command}\" to get additional information about the " +
				"command. For example, \"help upgrade:list\".");

		System.out.println("Enter \"exit\" or \"quit\" to exit.");
	}

	private boolean _processGogoShellCommand(
			GogoShellClient gogoShellClient, String command)
		throws IOException {

		if (command.equals("")) {
			return true;
		}

		String line = _GOGO_SHELL_PREFIX + command + System.lineSeparator();

		_fileOutputStream.write(line.getBytes());

		if (command.equals("exit") || command.equals("quit")) {
			return false;
		}

		String output = gogoShellClient.send(command);

		int index = output.indexOf(System.lineSeparator());

		if (index == -1) {
			return true;
		}

		output = output.substring(index + 1);

		System.out.println(output);

		return true;
	}

	private Properties _readProperties(File file) {
		Properties properties = new Properties();

		if (file.exists()) {
			try {
				properties.load(file);
			}
			catch (IOException ioException) {
				System.err.println("Unable to load " + file);
			}
		}

		return properties;
	}

	private String _requestDirName(
			boolean allowAbsolutePaths, File baseDir, String defaultDirName,
			String prompt)
		throws IOException {

		while (true) {
			System.out.println(prompt + " (" + defaultDirName + "): ");

			String response = _consoleReader.readLine();

			if (response.isEmpty()) {
				return null;
			}

			String dirName = response.trim();

			File testDir = _getResolvedDir(
				allowAbsolutePaths, baseDir, dirName);

			if ((testDir != null) && _isValidDir(testDir)) {
				if (allowAbsolutePaths) {
					return testDir.getCanonicalPath();
				}

				return dirName;
			}
		}
	}

	private String _requestDirNames(
			boolean allowAbsolutePaths, File baseDir, String defaultDirNames,
			String prompt)
		throws IOException {

		while (true) {
			System.out.println(prompt + " (" + defaultDirNames + "): ");

			String response = _consoleReader.readLine();

			if (response.isEmpty()) {
				return null;
			}

			boolean hasErrors = false;

			String dirNames = response.trim();

			for (String dirName : dirNames.split(",")) {
				dirName = dirName.trim();

				File testDir = _getResolvedDir(
					allowAbsolutePaths, baseDir, dirName);

				if ((testDir == null) || !_isValidDir(testDir)) {
					hasErrors = true;
				}
			}

			if (!hasErrors) {
				return dirNames;
			}
		}
	}

	private String _requestHost(String defaultHost, String prompt)
		throws IOException {

		while (true) {
			System.out.println(prompt + " (" + defaultHost + "): ");

			String response = _consoleReader.readLine();

			if (response.isEmpty()) {
				return null;
			}

			String name = response.trim();

			try {
				InetAddress.getByName(name);

				return name;
			}
			catch (Exception exception) {
				System.err.println(
					"Unable to resolve host " + name + ": " +
						exception.getMessage());
			}
		}
	}

	private Integer _requestPort(int defaultPort, String prompt)
		throws IOException {

		String port = null;

		if (defaultPort > 0) {
			port = String.valueOf(defaultPort);
		}
		else {
			port = "none";
		}

		while (true) {
			System.out.println(prompt + " (" + port + "): ");

			String response = _consoleReader.readLine();

			if (response.isEmpty()) {
				return null;
			}

			if (response.equals("none")) {
				return 0;
			}

			try {
				int portInt = Integer.parseInt(response);

				if ((portInt < 0) || (portInt > 65535)) {
					System.err.println("Port must be between 0 and 65535.");

					continue;
				}

				return portInt;
			}
			catch (NumberFormatException numberFormatException) {
				System.err.println(response + " is not a valid port number.");
			}
		}
	}

	private void _saveProperties() throws IOException {
		_appServerProperties.store(_appServerPropertiesFile);
		_portalUpgradeDatabaseProperties.store(
			_portalUpgradeDatabasePropertiesFile);
		_portalUpgradeExtProperties.store(_portalUpgradeExtPropertiesFile);
	}

	private void _verifyAppServerProperties() throws IOException {
		String value = _appServerProperties.getProperty(
			"server.detector.server.id");

		if ((value == null) || value.isEmpty()) {
			String response = null;

			while (_appServer == null) {
				System.out.print("[ ");

				for (String appServerName : _APP_SERVER_NAMES) {
					System.out.print(appServerName + " ");
				}

				System.out.println("]");
				System.out.println(
					"Please enter your application server (tomcat): ");

				response = _consoleReader.readLine();

				if (response.isEmpty()) {
					response = "tomcat";
				}

				_appServer = AppServer.getAppServer(
					new File(
						_portalUpgradeExtProperties.getProperty(
							"liferay.home")),
					response);

				if (_appServer == null) {
					System.err.println(
						response + " is an unsupported application server.");
				}
			}

			String appServerDirName = _requestDirName(
				true,
				new File(
					_portalUpgradeExtProperties.getProperty("liferay.home")),
				_appServer.getDir(
				).getPath(),
				"Please enter your application server directory");

			if (appServerDirName != null) {
				_appServer.setDirName(appServerDirName);
			}

			File dir = _appServer.getDir();

			String extraLibDirNames = _requestDirNames(
				false, dir, _appServer.getExtraLibDirNames(),
				"Please enter your extra library directories in application " +
					"server directory");

			if (extraLibDirNames != null) {
				_appServer.setExtraLibDirNames(extraLibDirNames);
			}

			String globalLibDirName = _requestDirName(
				false, dir, _appServer.getGlobalLibDirName(),
				"Please enter your global library directory in application " +
					"server directory");

			if (globalLibDirName != null) {
				_appServer.setGlobalLibDirName(globalLibDirName);
			}

			String portalDirName = _requestDirName(
				false, dir, _appServer.getPortalDirName(),
				"Please enter your portal directory in application server " +
					"directory");

			if (portalDirName != null) {
				_appServer.setPortalDirName(portalDirName);
			}

			_appServerProperties.setProperty("dir", dir.getCanonicalPath());

			_appServerProperties.setProperty(
				"extra.lib.dirs", _appServer.getExtraLibDirNames());
			_appServerProperties.setProperty(
				"global.lib.dir", _appServer.getGlobalLibDirName());
			_appServerProperties.setProperty(
				"portal.dir", _appServer.getPortalDirName());
			_appServerProperties.setProperty(
				"server.detector.server.id",
				_appServer.getServerDetectorServerId());
		}
		else {
			String dirName = _appServerProperties.getProperty("dir");

			File dir = new File(dirName);

			if (!dir.isAbsolute()) {
				dir = new File(_jarDir, dirName);
			}

			dirName = dir.getCanonicalPath();

			_appServerProperties.setProperty("dir", dirName);

			_appServer = new AppServer(
				dirName, _appServerProperties.getProperty("extra.lib.dirs"),
				_appServerProperties.getProperty("global.lib.dir"),
				_appServerProperties.getProperty("portal.dir"), value);
		}
	}

	private void _verifyPortalUpgradeDatabaseProperties() throws IOException {
		String value = _portalUpgradeDatabaseProperties.getProperty(
			"jdbc.default.driverClassName");

		if ((value != null) && !value.isEmpty()) {
			return;
		}

		String response = null;

		Database dataSource = null;

		while (dataSource == null) {
			System.out.print("[ ");

			for (String databaseType : _DATABASE_TYPES) {
				System.out.print(databaseType + " ");
			}

			System.out.println("]");

			System.out.println("Please enter your database (mysql): ");

			response = _consoleReader.readLine();

			if (response.isEmpty()) {
				response = "mysql";
			}

			dataSource = Database.getDatabase(response);

			if (dataSource == null) {
				System.err.println(response + " is an unsupported database.");
			}
		}

		System.out.println(
			"Please enter your database JDBC driver class name (" +
				dataSource.getClassName() + "): ");

		response = _consoleReader.readLine();

		if (!response.isEmpty()) {
			dataSource.setClassName(response);
		}

		System.out.println(
			"Please enter your database JDBC driver protocol (" +
				dataSource.getProtocol() + "): ");

		response = _consoleReader.readLine();

		if (!response.isEmpty()) {
			dataSource.setProtocol(response);
		}

		String host = _requestHost(
			dataSource.getHost(), "Please enter your database host");

		if (host != null) {
			dataSource.setHost(host);
		}

		Integer port = _requestPort(
			dataSource.getPort(), "Please enter your database port");

		if (port != null) {
			dataSource.setPort(port);
		}

		System.out.println(
			"Please enter your database name (" + dataSource.getSchemaName() +
				"): ");

		response = _consoleReader.readLine();

		if (!response.isEmpty()) {
			dataSource.setSchemaName(response);
		}

		System.out.println("Please enter your database username: ");

		String userName = _consoleReader.readLine();

		System.out.println("Please enter your database password: ");

		String password = _consoleReader.readLine('*');

		_portalUpgradeDatabaseProperties.setProperty(
			"jdbc.default.driverClassName", dataSource.getClassName());
		_portalUpgradeDatabaseProperties.setProperty(
			"jdbc.default.password", password);
		_portalUpgradeDatabaseProperties.setProperty(
			"jdbc.default.url", dataSource.getURL());
		_portalUpgradeDatabaseProperties.setProperty(
			"jdbc.default.username", userName);
	}

	private void _verifyPortalUpgradeExtProperties() throws IOException {
		String value = _portalUpgradeExtProperties.getProperty("liferay.home");

		File baseDir = new File(".");

		if ((value == null) || value.isEmpty()) {
			File defaultLiferayHomeDir = new File(_jarDir, "../../");

			String liferayHomeDirName = _requestDirName(
				true, baseDir, defaultLiferayHomeDir.getCanonicalPath(),
				"Please enter your Liferay home");

			if (liferayHomeDirName != null) {
				value = liferayHomeDirName;
			}
			else {
				value = defaultLiferayHomeDir.getCanonicalPath();
			}
		}
		else {
			baseDir = _jarDir;
		}

		File liferayHome = new File(value);

		if (!liferayHome.isAbsolute()) {
			liferayHome = new File(baseDir, value);
		}

		_portalUpgradeExtProperties.setProperty(
			"liferay.home", liferayHome.getCanonicalPath());
	}

	private static final String[] _APP_SERVER_NAMES = {
		"jboss", "tomcat", "weblogic", "wildfly"
	};

	private static final String[] _DATABASE_TYPES = {
		"db2", "mariadb", "mysql", "oracle", "postgresql", "sqlserver"
	};

	private static final String _GOGO_SHELL_PREFIX = "g! ";

	private static final String _JAVA_HOME = System.getenv("JAVA_HOME");

	private static final Pattern _gogoShellAddressPattern = Pattern.compile(
		"^([^\\:]+):([0-9]{1,5})$");
	private static File _jarDir;
	private static final List<String> _reflectionOpens = Arrays.asList(
		"--add-opens=java.base/java.lang=ALL-UNNAMED",
		"--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
		"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
		"--add-opens=java.base/java.net=ALL-UNNAMED",
		"--add-opens java.base/java.util=ALL-UNNAMED",
		"--add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED",
		"--add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED",
		"--add-opens=java.base/sun.util.calendar=ALL-UNNAMED",
		"--add-opens=jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED");

	static {
		ProtectionDomain protectionDomain =
			DBUpgradeClient.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		try {
			Path path = Paths.get(url.toURI());

			File jarFile = path.toFile();

			_jarDir = jarFile.getParentFile();
		}
		catch (URISyntaxException uriSyntaxException) {
			throw new ExceptionInInitializerError(uriSyntaxException);
		}
	}

	private AppServer _appServer;
	private final Properties _appServerProperties;
	private final File _appServerPropertiesFile;
	private final ConsoleReader _consoleReader = new ConsoleReader();
	private final FileOutputStream _fileOutputStream;
	private List<String> _jvmOpts = new ArrayList<>();
	private final File _logFile;
	private final Properties _portalUpgradeDatabaseProperties;
	private final File _portalUpgradeDatabasePropertiesFile;
	private final Properties _portalUpgradeExtProperties;
	private final File _portalUpgradeExtPropertiesFile;
	private final boolean _shell;

}