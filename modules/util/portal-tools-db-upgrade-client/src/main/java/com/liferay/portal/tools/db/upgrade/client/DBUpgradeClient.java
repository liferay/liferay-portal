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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
				_printHelpAndExit(_LIFERAY_COMMON_EXIT_CODE_OK, options);
			}

			List<String> jvmOpts = new ArrayList<>();

			if (commandLine.hasOption("jvm-opts")) {
				String optionValue = commandLine.getOptionValue("jvm-opts");

				if (optionValue != null) {
					optionValue = optionValue.trim();

					if (!optionValue.isEmpty()) {
						Collections.addAll(jvmOpts, optionValue.split(" "));
					}
				}
			}

			_addDefaultJVMOpts(jvmOpts);

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

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyyMMdd_HHmmss", Locale.US);

				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

				String timestamp = simpleDateFormat.format(
					new Date(logFile.lastModified()));

				logFile.renameTo(
					new File(logDir, logFileName + "." + timestamp));

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
			System.err.println(parseException.getMessage());

			_printHelpAndExit(_LIFERAY_COMMON_EXIT_CODE_HELP, options);
		}
		catch (Exception exception) {
			System.err.println("Error running upgrade:");

			exception.printStackTrace();

			System.exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
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

		environment.put("JDK_JAVA_OPTIONS", _buildJDKJavaOptions());

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

			System.exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}

		try {
			_verifyPortalUpgradeExtPropertiesLiferayHome();

			_verifyAppServerProperties();

			_verifyPortalUpgradeExtPropertiesDatabase();

			_saveProperties();
		}
		catch (IOException ioException) {
			System.err.println(ioException.getMessage());
			System.err.println(
				"Stopping the upgrade process. Please fix the errors and try " +
					"again.");

			System.exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}
	}

	private static void _addDefaultJVMOpts(List<String> jvmOpts) {
		Map<String, String> defaultJVMOpts = new LinkedHashMap<>();

		defaultJVMOpts.put("-Dfile.encoding=", "-Dfile.encoding=UTF8");
		defaultJVMOpts.put("-Duser.country=", "-Duser.country=US");
		defaultJVMOpts.put("-Duser.language=", "-Duser.language=en");
		defaultJVMOpts.put("-Duser.timezone=", "-Duser.timezone=GMT");
		defaultJVMOpts.put("-Xmx", "-Xmx4096m");

		for (Map.Entry<String, String> entry : defaultJVMOpts.entrySet()) {
			if (_containsPrefix(jvmOpts, entry.getKey())) {
				continue;
			}

			jvmOpts.add(entry.getValue());
		}
	}

	private static boolean _containsPrefix(
		List<String> jvmOpts, String prefix) {

		for (String jvmOpt : jvmOpts) {
			if ((jvmOpt != null) && jvmOpt.startsWith(prefix)) {
				return true;
			}
		}

		return false;
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

	private static void _printHelpAndExit(int exitCode, Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();

		helpFormatter.setSyntaxPrefix("Usage: ");

		helpFormatter.printHelp(
			System.getProperty("launcher", "db_upgrade_client") +
				" [parameters]",
			"Liferay Portal Tools Database Upgrade Client", options, null);

		System.exit(exitCode);
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
		Set<String> jdkOptions = new LinkedHashSet<>(_reflectionOpens);

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		for (int i = 0; i < inputArguments.size(); i++) {
			String inputArgument = inputArguments.get(i);

			if (inputArgument.startsWith("--add-opens=")) {
				jdkOptions.add(inputArgument);
			}
			else if (inputArgument.equals("--add-opens")) {
				if ((i + 1) < inputArguments.size()) {
					String nextInputArgument = inputArguments.get(i + 1);

					jdkOptions.add(inputArgument + "=" + nextInputArgument);

					i++;
				}
			}
		}

		return String.join(" ", jdkOptions);
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

	private String[] _getDBTypes() {
		if (_appServer == null) {
			return _DATABASE_TYPES_PORTAL;
		}

		File portalShieldedContainerLibDir =
			_appServer.getPortalShieldedContainerLibDir();

		if (portalShieldedContainerLibDir != null) {
			Path path = portalShieldedContainerLibDir.toPath();

			if (Files.exists(path.resolve("com.liferay.portal.dao.db.jar"))) {
				return _DATABASE_TYPES_DXP;
			}
		}

		return _DATABASE_TYPES_PORTAL;
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

	private boolean _isEmpty(String value) {
		if ((value == null) || value.isEmpty()) {
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

	private boolean _isValidDir(File dir) throws IOException {
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}

		System.err.println(
			dir.getCanonicalPath() + " does not exist or is not a directory.");

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

				if ((testDir != null) && _isValidDir(testDir)) {
					continue;
				}

				hasErrors = true;
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
		_portalUpgradeExtProperties.store(_portalUpgradeExtPropertiesFile);
	}

	private void _verifyAppServerProperties() throws IOException {
		String value = _appServerProperties.getProperty(
			"server.detector.server.id");

		if (_isEmpty(value)) {
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

				if (_appServer != null) {
					continue;
				}

				System.err.println(
					response + " is an unsupported application server.");
			}

			File dir = _appServer.getDir();

			String appServerDirName = _requestDirName(
				true,
				new File(
					_portalUpgradeExtProperties.getProperty("liferay.home")),
				dir.getPath(),
				"Please enter your application server directory");

			if (appServerDirName != null) {
				_appServer.setDirName(appServerDirName);

				dir = _appServer.getDir();
			}

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
			File liferayHome = new File(
				_portalUpgradeExtProperties.getProperty("liferay.home"));

			_appServer = AppServer.getAppServer(liferayHome, value);

			if (_appServer == null) {
				System.err.println(
					value + " is an unsupported application server.");

				throw new IOException(
					"Invalid configuration in " +
						_appServerPropertiesFile.getName());
			}

			String dirName = _appServerProperties.getProperty("dir");

			if (!_verifyDirName(
					true, liferayHome, dirName,
					_appServerPropertiesFile.getName(), "dir")) {

				throw new IOException(
					"Invalid configuration in " +
						_appServerPropertiesFile.getName());
			}

			File dir = new File(dirName);

			if (!dir.isAbsolute()) {
				dir = new File(liferayHome, dirName);
			}

			dirName = dir.getCanonicalPath();

			_appServer.setDirName(dirName);

			_appServerProperties.setProperty("dir", dirName);

			String extraLibDirNames = _appServerProperties.getProperty(
				"extra.lib.dirs");
			String globalLibDirName = _appServerProperties.getProperty(
				"global.lib.dir");
			String portalDirName = _appServerProperties.getProperty(
				"portal.dir");

			if (!_verifyDirNames(
					false, dir, extraLibDirNames,
					_appServerPropertiesFile.getName(), "extra.lib.dirs") |
				!_verifyDirName(
					false, dir, globalLibDirName,
					_appServerPropertiesFile.getName(), "global.lib.dir") |
				!_verifyDirName(
					false, dir, portalDirName,
					_appServerPropertiesFile.getName(), "portal.dir")) {

				throw new IOException(
					"Invalid configuration in " +
						_appServerPropertiesFile.getName());
			}

			_appServer.setExtraLibDirNames(extraLibDirNames);
			_appServer.setGlobalLibDirName(globalLibDirName);
			_appServer.setPortalDirName(portalDirName);
		}
	}

	private boolean _verifyDirName(
			boolean allowAbsolutePaths, File baseDir, String dirName,
			String propertiesFileName, String propertyName)
		throws IOException {

		if (dirName != null) {
			dirName = dirName.trim();
		}

		if ((dirName == null) || dirName.isEmpty()) {
			System.err.println(
				"Property \"" + propertyName + "\" is not set in " +
					propertiesFileName + ".");

			return false;
		}

		File testDir = _getResolvedDir(allowAbsolutePaths, baseDir, dirName);

		if (testDir == null) {
			System.err.println(
				"Property '" + propertyName + "' in " + propertiesFileName +
					" contains an invalid path: " + dirName);

			return false;
		}

		return _isValidDir(testDir);
	}

	private boolean _verifyDirNames(
			boolean allowAbsolutePaths, File baseDir, String dirNames,
			String propertiesFileName, String propertyName)
		throws IOException {

		if ((dirNames == null) ||
			dirNames.trim(
			).isEmpty()) {

			return true;
		}

		boolean hasErrors = false;

		for (String dirName : dirNames.split(",")) {
			dirName = dirName.trim();

			if (dirName.isEmpty()) {
				continue;
			}

			File testDir = _getResolvedDir(
				allowAbsolutePaths, baseDir, dirName);

			if (testDir == null) {
				System.err.println(
					"Property \"" + propertyName + "\" in " +
						propertiesFileName + " contains an invalid path: " +
							dirName);

				hasErrors = true;
			}
			else if (!_isValidDir(testDir)) {
				hasErrors = true;
			}
		}

		return !hasErrors;
	}

	private void _verifyPortalUpgradeExtPropertiesDatabase()
		throws IOException {

		File portalUpgradeDatabasePropertiesFile = new File(
			_jarDir, "portal-upgrade-database.properties");

		if (portalUpgradeDatabasePropertiesFile.exists()) {
			System.err.println(
				"The portal-upgrade-database.properties file is deprecated " +
					"and will be ignored. Please move all database " +
						"configuration properties to " +
							"portal-upgrade-ext.properties.");
		}

		String driverClassName = _portalUpgradeExtProperties.getProperty(
			"jdbc.default.driverClassName");

		if (!_isEmpty(driverClassName)) {
			return;
		}

		String response = null;

		File portalExtPropertiesFile = new File(
			_portalUpgradeExtProperties.getProperty("liferay.home"),
			"portal-ext.properties");

		Properties portalExtProperties = _readProperties(
			portalExtPropertiesFile);

		driverClassName = portalExtProperties.getProperty(
			"jdbc.default.driverClassName");

		String password = null;
		String url = null;
		String userName = null;

		if (!_isEmpty(driverClassName)) {
			password = portalExtProperties.getProperty("jdbc.default.password");
			url = portalExtProperties.getProperty("jdbc.default.url");
			userName = portalExtProperties.getProperty("jdbc.default.username");
		}

		if (!_isEmpty(driverClassName) && !_isEmpty(password) &&
			!_isEmpty(url) && !_isEmpty(userName)) {

			System.out.println(
				"An existing database configuration was found in: " +
					portalExtPropertiesFile.getAbsolutePath());
			System.out.println(
				"\tjdbc.default.driverClassName=" + driverClassName);
			System.out.println("\tjdbc.default.url=" + url);
			System.out.println("\tjdbc.default.username=" + userName);
			System.out.println(
				"\tjdbc.default.password=" + password.replaceAll(".", "*"));

			System.out.println("\nUse existing JDBC properties (y/N):");

			response = _consoleReader.readLine();

			if (response.equalsIgnoreCase("y") ||
				response.equalsIgnoreCase("yes")) {

				_portalUpgradeExtProperties.setProperty(
					"jdbc.default.driverClassName", driverClassName);
				_portalUpgradeExtProperties.setProperty(
					"jdbc.default.password", password);
				_portalUpgradeExtProperties.setProperty(
					"jdbc.default.url", url);
				_portalUpgradeExtProperties.setProperty(
					"jdbc.default.username", userName);

				return;
			}
		}

		Database dataSource = null;

		while (dataSource == null) {
			System.out.print("[ ");

			for (String databaseType : _getDBTypes()) {
				System.out.print(databaseType + " ");
			}

			System.out.println("]");

			System.out.println("Please enter your database (postgresql): ");

			response = _consoleReader.readLine();

			if (response.isEmpty()) {
				response = "postgresql";
			}

			dataSource = Database.getDatabase(response);

			if (dataSource != null) {
				continue;
			}

			System.err.println(response + " is an unsupported database.");
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

		userName = _consoleReader.readLine();

		System.out.println("Please enter your database password: ");

		password = _consoleReader.readLine('*');

		_portalUpgradeExtProperties.setProperty(
			"jdbc.default.driverClassName", dataSource.getClassName());
		_portalUpgradeExtProperties.setProperty(
			"jdbc.default.password", password);
		_portalUpgradeExtProperties.setProperty(
			"jdbc.default.url", dataSource.getURL());
		_portalUpgradeExtProperties.setProperty(
			"jdbc.default.username", userName);
	}

	private void _verifyPortalUpgradeExtPropertiesLiferayHome()
		throws IOException {

		String value = _portalUpgradeExtProperties.getProperty("liferay.home");

		File baseDir = new File(".");

		if (_isEmpty(value)) {
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

			if (!_verifyDirName(
					true, baseDir, value,
					_portalUpgradeExtPropertiesFile.getName(),
					"liferay.home")) {

				throw new IOException(
					"Invalid configuration in " +
						_portalUpgradeExtPropertiesFile.getName());
			}
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

	private static final String[] _DATABASE_TYPES_DXP = {
		"db2", "mariadb", "mysql", "oracle", "postgresql", "sqlserver"
	};

	private static final String[] _DATABASE_TYPES_PORTAL = {
		"mariadb", "mysql", "postgresql"
	};

	private static final String _GOGO_SHELL_PREFIX = "g! ";

	private static final String _JAVA_HOME = System.getenv("JAVA_HOME");

	/**
	 * https://github.com/liferay/liferay-docker/blob/master/_liferay_common.sh
	 */
	private static final int _LIFERAY_COMMON_EXIT_CODE_BAD = 1;

	private static final int _LIFERAY_COMMON_EXIT_CODE_HELP = 2;

	private static final int _LIFERAY_COMMON_EXIT_CODE_OK = 0;

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
	private final Properties _portalUpgradeExtProperties;
	private final File _portalUpgradeExtPropertiesFile;
	private final boolean _shell;

}