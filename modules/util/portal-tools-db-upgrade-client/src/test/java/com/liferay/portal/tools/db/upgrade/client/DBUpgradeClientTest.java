/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.upgrade.client;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.db.upgrade.client.util.Properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jline.console.ConsoleReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author István András Dézsi
 */
public class DBUpgradeClientTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_liferayHomeDir = new File(_rootDir, RandomTestUtil.randomString());

		_liferayHomeDir.mkdir();

		_tomcatDir = new File(_liferayHomeDir, "tomcat");

		_tomcatDir.mkdirs();

		File extraLibDir = new File(_tomcatDir, "bin");

		extraLibDir.mkdirs();

		File globalLibDir = new File(_tomcatDir, "lib");

		globalLibDir.mkdirs();

		File portalDir = new File(_tomcatDir, "webapps/ROOT");

		portalDir.mkdirs();

		_shieldedContainerLib = new File(
			portalDir, "WEB-INF/shielded-container-lib");

		Mockito.when(
			_appServer.getPortalShieldedContainerLibDir()
		).thenReturn(
			_shieldedContainerLib
		);

		ReflectionTestUtil.setFieldValue(
			DBUpgradeClient.class, "_jarDir", _rootDir);
	}

	@Before
	public void setUp() throws Exception {
		_consoleByteArrayOutputStream.reset();
		_errorByteArrayOutputStream.reset();

		System.setErr(new PrintStream(_errorByteArrayOutputStream));
		System.setOut(new PrintStream(_consoleByteArrayOutputStream));
	}

	@After
	public void tearDown() throws Exception {
		if (_dbUpgradeClient != null) {
			FileOutputStream fileOutputStream =
				ReflectionTestUtil.getFieldValue(
					_dbUpgradeClient, "_fileOutputStream");

			fileOutputStream.close();

			_dbUpgradeClient = null;
		}

		File appServerPropertiesFile = new File(
			_rootDir, "app-server.properties");

		if (appServerPropertiesFile.exists()) {
			appServerPropertiesFile.delete();
		}

		File portalUpgradeExtPropertiesFile = new File(
			_rootDir, "portal-upgrade-ext.properties");

		if (portalUpgradeExtPropertiesFile.exists()) {
			portalUpgradeExtPropertiesFile.delete();
		}

		System.setErr(_originalErrorOutputStream);
		System.setOut(_originalOutputStream);
	}

	@Test
	public void testAddDefaultJVMOptsDoesNotOverrideCustom() throws Exception {
		List<String> jvmOpts = new ArrayList<>();

		jvmOpts.add("-Dfile.encoding=ISO-8859-1");
		jvmOpts.add("-Xmx8192m");

		ReflectionTestUtil.invoke(
			DBUpgradeClient.class, "_addDefaultJVMOpts",
			new Class<?>[] {List.class}, jvmOpts);

		Assert.assertFalse(jvmOpts.contains("-Dfile.encoding=UTF8"));
		Assert.assertTrue(jvmOpts.contains("-Dfile.encoding=ISO-8859-1"));
		Assert.assertFalse(jvmOpts.contains("-Xmx4096m"));
		Assert.assertTrue(jvmOpts.contains("-Xmx8192m"));
		Assert.assertEquals(jvmOpts.toString(), 5, jvmOpts.size());
	}

	@Test
	public void testAddDefaultJVMOptsWhenEmpty() throws Exception {
		List<String> jvmOpts = new ArrayList<>();

		ReflectionTestUtil.invoke(
			DBUpgradeClient.class, "_addDefaultJVMOpts",
			new Class<?>[] {List.class}, jvmOpts);

		Assert.assertTrue(jvmOpts.contains("-Dfile.encoding=UTF8"));
		Assert.assertTrue(jvmOpts.contains("-Duser.country=US"));
		Assert.assertTrue(jvmOpts.contains("-Duser.language=en"));
		Assert.assertTrue(jvmOpts.contains("-Duser.timezone=GMT"));
		Assert.assertTrue(jvmOpts.contains("-Xmx4096m"));
		Assert.assertEquals(jvmOpts.toString(), 5, jvmOpts.size());
	}

	@Test
	@TestInfo("LPD-109433")
	public void testHelp() throws Exception {
		_testHelp(
			Collections.singletonList("--help"), StringPool.BLANK, 0,
			Collections.singletonList("-Dlauncher=launcher.sh"),
			"usage: launcher.sh [parameters]",
			"Liferay Portal Tools Database Upgrade Client");
	}

	@Test
	@TestInfo("LPD-109433")
	public void testInvalidArguments() throws Exception {
		_testHelp(
			Collections.singletonList("--foo"), "Unrecognized option: --foo", 2,
			Collections.emptyList(), "usage: db_upgrade_client [parameters]");
	}

	@Test
	public void testVerifyAppServerProperties() throws Exception {
		_createPortalUpgradeExtPropertiesFile(true);

		String[] answers = {
			"invalidAppServer", StringPool.BLANK, "invalidAppServerDirName",
			_tomcatDir.getAbsolutePath(), "invalidExtraLibDirName", "bin",
			"globalLibDirName", "lib", "portalDirName", "webapps/ROOT"
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyAppServerProperties", new Class<?>[0]);

		String errorOutput = _errorByteArrayOutputStream.toString();

		Assert.assertTrue(
			errorOutput.contains("is an unsupported application server"));

		Assert.assertTrue(
			errorOutput.contains("does not exist or is not a directory"));

		AppServer appServer = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_appServer");

		Assert.assertNotNull(appServer);
		Assert.assertEquals("tomcat", appServer.getServerDetectorServerId());
		Assert.assertEquals(_tomcatDir, appServer.getDir());
		Assert.assertEquals("bin", appServer.getExtraLibDirNames());
		Assert.assertEquals("lib", appServer.getGlobalLibDirName());
		Assert.assertEquals("webapps/ROOT", appServer.getPortalDirName());
		Assert.assertEquals("tomcat", appServer.getServerDetectorServerId());
	}

	@Test
	public void testVerifyAppServerPropertiesWithEmptyAnswers()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(true);

		String[] answers = {
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyAppServerProperties", new Class<?>[0]);

		AppServer appServer = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_appServer");

		Assert.assertNotNull(appServer);
		Assert.assertEquals(
			new File(_liferayHomeDir, "tomcat"), appServer.getDir());
		Assert.assertEquals("./bin", appServer.getExtraLibDirNames());
		Assert.assertEquals("./lib", appServer.getGlobalLibDirName());
		Assert.assertEquals("./webapps/ROOT", appServer.getPortalDirName());
		Assert.assertEquals("tomcat", appServer.getServerDetectorServerId());
	}

	@Test
	public void testVerifyAppServerPropertiesWithInvalidAppServerName()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(true);

		Properties appServerProperties = new Properties();

		appServerProperties.setProperty(
			"server.detector.server.id", "invalidAppServer");

		appServerProperties.store(new File(_rootDir, "app-server.properties"));

		_dbUpgradeClient = _createDBUpgradeClient(new String[0]);

		try {
			ReflectionTestUtil.invoke(
				_dbUpgradeClient, "_verifyAppServerProperties",
				new Class<?>[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Invalid configuration in app-server.properties",
				exception.getMessage());

			String errorOutput = _errorByteArrayOutputStream.toString();

			Assert.assertTrue(
				errorOutput.contains("is an unsupported application server"));
		}
	}

	@Test
	public void testVerifyAppServerPropertiesWithInvalidPropertiesFile()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(true);

		Properties appServerProperties = new Properties();

		appServerProperties.setProperty("dir", "invalidDir");
		appServerProperties.setProperty("extra.lib.dirs", "bin");
		appServerProperties.setProperty("global.lib.dir", "lib");
		appServerProperties.setProperty("portal.dir", "webapps/ROOT");
		appServerProperties.setProperty("server.detector.server.id", "tomcat");

		appServerProperties.store(new File(_rootDir, "app-server.properties"));

		_dbUpgradeClient = _createDBUpgradeClient(new String[0]);

		try {
			ReflectionTestUtil.invoke(
				_dbUpgradeClient, "_verifyAppServerProperties",
				new Class<?>[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Invalid configuration in app-server.properties",
				exception.getMessage());

			String errorOutput = _errorByteArrayOutputStream.toString();

			Assert.assertTrue(
				errorOutput.contains("does not exist or is not a directory"));
		}
	}

	@Test
	public void testVerifyAppServerPropertiesWithValidPropertiesFile()
		throws Exception {

		_createAppServerPropertiesFile();
		_createPortalUpgradeExtPropertiesFile(true);

		_dbUpgradeClient = _createDBUpgradeClient(new String[0]);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyAppServerProperties", new Class<?>[0]);

		AppServer appServer = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_appServer");

		Assert.assertEquals(_tomcatDir, appServer.getDir());
		Assert.assertEquals("bin", appServer.getExtraLibDirNames());
		Assert.assertEquals("lib", appServer.getGlobalLibDirName());
		Assert.assertEquals("webapps/ROOT", appServer.getPortalDirName());
		Assert.assertEquals("tomcat", appServer.getServerDetectorServerId());
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesDatabase()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(false);

		String[] answers = {
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, "invalidHost", "localhost", "abc", "99999",
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.setFieldValue(
			_dbUpgradeClient, "_appServer", _appServer);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesDatabase",
			new Class<?>[0]);

		String consoleString = _consoleByteArrayOutputStream.toString();

		Assert.assertTrue(consoleString.contains("mariadb mysql postgresql"));

		String errorOutput = _errorByteArrayOutputStream.toString();

		Assert.assertTrue(errorOutput.contains("Unable to resolve host"));
		Assert.assertTrue(errorOutput.contains("is not a valid port number"));
		Assert.assertTrue(
			errorOutput.contains("Port must be between 0 and 65535"));

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeExtProperties");

		Assert.assertNotNull(properties);
		Assert.assertEquals(
			"org.postgresql.Driver",
			properties.getProperty("jdbc.default.driverClassName"));
		Assert.assertEquals(
			"jdbc:postgresql://localhost:5432/lportal",
			properties.getProperty("jdbc.default.url"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.username"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.password"));
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesDatabaseWithEmptyAnswers()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(false);

		Properties portalExtProperties = _createPortalExtPropertiesFile();

		String[] answers = {
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.setFieldValue(
			_dbUpgradeClient, "_appServer", _appServer);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesDatabase",
			new Class<?>[0]);

		String consoleString = _consoleByteArrayOutputStream.toString();

		Assert.assertTrue(
			consoleString.contains(
				"An existing database configuration was found in: " +
					_liferayHomeDir + "/portal-ext.properties"));

		String jdbcDriverClassName = portalExtProperties.getProperty(
			"jdbc.default.driverClassName");

		Assert.assertTrue(
			consoleString.contains(
				"jdbc.default.driverClassName=" + jdbcDriverClassName));

		Assert.assertTrue(consoleString.contains("mariadb mysql postgresql"));

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeExtProperties");

		Assert.assertNotNull(properties);
		Assert.assertEquals(
			"org.postgresql.Driver",
			properties.getProperty("jdbc.default.driverClassName"));
		Assert.assertEquals(
			"jdbc:postgresql://localhost:5432/lportal",
			properties.getProperty("jdbc.default.url"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.username"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.password"));
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesDatabaseWithEmptyAnswersOnDXP()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(false);

		String[] answers = {
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.setFieldValue(
			_dbUpgradeClient, "_appServer", _appServer);

		Path path = _shieldedContainerLib.toPath();

		path = path.resolve("com.liferay.portal.dao.db.jar");

		Files.createDirectories(_shieldedContainerLib.toPath());

		try {
			Files.createFile(path);

			ReflectionTestUtil.invoke(
				_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesDatabase",
				new Class<?>[0]);

			String consoleString = _consoleByteArrayOutputStream.toString();

			Assert.assertTrue(
				consoleString.contains(
					"db2 mariadb mysql oracle postgresql sqlserver"));

			Properties properties = ReflectionTestUtil.getFieldValue(
				_dbUpgradeClient, "_portalUpgradeExtProperties");

			Assert.assertNotNull(properties);
			Assert.assertEquals(
				"org.postgresql.Driver",
				properties.getProperty("jdbc.default.driverClassName"));
			Assert.assertEquals(
				"jdbc:postgresql://localhost:5432/lportal",
				properties.getProperty("jdbc.default.url"));
			Assert.assertEquals(
				StringPool.BLANK,
				properties.getProperty("jdbc.default.username"));
			Assert.assertEquals(
				StringPool.BLANK,
				properties.getProperty("jdbc.default.password"));
		}
		finally {
			Files.deleteIfExists(path);
		}
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesDatabaseWithPortalExtProperties()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(false);

		Properties portalExtProperties = _createPortalExtPropertiesFile();

		_dbUpgradeClient = _createDBUpgradeClient(new String[] {"Y"});

		ReflectionTestUtil.setFieldValue(
			_dbUpgradeClient, "_appServer", _appServer);

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeExtProperties");

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesDatabase",
			new Class<?>[0]);

		String consoleString = _consoleByteArrayOutputStream.toString();

		Assert.assertTrue(
			consoleString.contains(
				"An existing database configuration was found in: " +
					_liferayHomeDir + "/portal-ext.properties"));

		String jdbcDriverClassName = portalExtProperties.getProperty(
			"jdbc.default.driverClassName");

		Assert.assertTrue(
			consoleString.contains(
				"jdbc.default.driverClassName=" + jdbcDriverClassName));

		Assert.assertEquals(
			portalExtProperties.getProperty("jdbc.default.driverClassName"),
			properties.getProperty("jdbc.default.driverClassName"));
		Assert.assertEquals(
			portalExtProperties.getProperty("jdbc.default.password"),
			properties.getProperty("jdbc.default.password"));
		Assert.assertEquals(
			portalExtProperties.getProperty("jdbc.default.url"),
			properties.getProperty("jdbc.default.url"));
		Assert.assertEquals(
			portalExtProperties.getProperty("jdbc.default.userName"),
			properties.getProperty("jdbc.default.userName"));
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesLiferayHome()
		throws Exception {

		File liferayHomeDir = new File(_rootDir, "custom-liferay-home");

		liferayHomeDir.mkdirs();

		String[] answers = {
			RandomTestUtil.randomString(), liferayHomeDir.getAbsolutePath()
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesLiferayHome",
			new Class<?>[0]);

		String errorOutput = _errorByteArrayOutputStream.toString();

		Assert.assertTrue(
			errorOutput.contains("does not exist or is not a directory"));

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeExtProperties");

		Assert.assertNotNull(properties);

		String liferayHomeDirName = properties.getProperty("liferay.home");

		Assert.assertEquals(
			liferayHomeDir.getCanonicalPath(),
			new File(
				liferayHomeDirName
			).getCanonicalPath());
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesLiferayHomeWithEmptyAnswers()
		throws Exception {

		String[] answers = {StringPool.BLANK};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesLiferayHome",
			new Class<?>[0]);

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeExtProperties");

		Assert.assertNotNull(properties);

		File liferayHomeDir = new File(_rootDir, "../../");

		String liferayHomeDirName = properties.getProperty("liferay.home");

		Assert.assertEquals(
			liferayHomeDir.getCanonicalPath(),
			new File(
				liferayHomeDirName
			).getCanonicalPath());
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesLiferayHomeWithInvalidPropertiesFile()
		throws Exception {

		Properties portalUpgradeExtProperties = new Properties();

		portalUpgradeExtProperties.setProperty("liferay.home", "invalidDir");

		portalUpgradeExtProperties.store(
			new File(_rootDir, "portal-upgrade-ext.properties"));

		_dbUpgradeClient = _createDBUpgradeClient(new String[0]);

		try {
			ReflectionTestUtil.invoke(
				_dbUpgradeClient,
				"_verifyPortalUpgradeExtPropertiesLiferayHome",
				new Class<?>[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Invalid configuration in portal-upgrade-ext.properties",
				exception.getMessage());

			String errorOutput = _errorByteArrayOutputStream.toString();

			Assert.assertTrue(
				errorOutput.contains("does not exist or is not a directory"));
		}
	}

	@Test
	public void testVerifyPortalUpgradeExtPropertiesLiferayHomeWithValidPropertiesFile()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile(true);

		_dbUpgradeClient = _createDBUpgradeClient(new String[0]);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtPropertiesLiferayHome",
			new Class<?>[0]);

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeExtProperties");

		Assert.assertNotNull(properties);

		String liferayHomeDirName = properties.getProperty("liferay.home");

		Assert.assertEquals(
			_liferayHomeDir.getCanonicalPath(),
			new File(
				liferayHomeDirName
			).getCanonicalPath());
	}

	private void _createAppServerPropertiesFile() throws Exception {
		Properties appServerProperties = new Properties();

		appServerProperties.setProperty("dir", _tomcatDir.getAbsolutePath());
		appServerProperties.setProperty("extra.lib.dirs", "bin");
		appServerProperties.setProperty("global.lib.dir", "lib");
		appServerProperties.setProperty("portal.dir", "webapps/ROOT");
		appServerProperties.setProperty("server.detector.server.id", "tomcat");

		appServerProperties.store(new File(_rootDir, "app-server.properties"));
	}

	private DBUpgradeClient _createDBUpgradeClient(String[] answers)
		throws Exception {

		StringBundler sb = new StringBundler();

		for (String answer : answers) {
			sb.append(answer);
			sb.append(StringPool.NEW_LINE);
		}

		String consoleInput = sb.toString();

		DBUpgradeClient dbUpgradeClient = new DBUpgradeClient(
			new ArrayList<>(), new File(_rootDir, "test.log"), false);

		ConsoleReader consoleReader = new ConsoleReader(
			new ByteArrayInputStream(consoleInput.getBytes()),
			new PrintStream(new ByteArrayOutputStream()));

		ReflectionTestUtil.setFieldValue(
			dbUpgradeClient, "_consoleReader", consoleReader);

		return dbUpgradeClient;
	}

	private Properties _createPortalExtPropertiesFile() throws Exception {
		Properties portalExtProperties = new Properties();

		portalExtProperties.setProperty(
			"jdbc.default.driverClassName", RandomTestUtil.randomString());
		portalExtProperties.setProperty(
			"jdbc.default.password", RandomTestUtil.randomString());
		portalExtProperties.setProperty(
			"jdbc.default.url", RandomTestUtil.randomString());
		portalExtProperties.setProperty(
			"jdbc.default.username", RandomTestUtil.randomString());

		portalExtProperties.store(
			new File(_liferayHomeDir, "portal-ext.properties"));

		return portalExtProperties;
	}

	private void _createPortalUpgradeExtPropertiesFile(
			boolean includeDatabaseProperties)
		throws Exception {

		Properties portalUpgradeExtProperties = new Properties();

		if (includeDatabaseProperties) {
			portalUpgradeExtProperties.setProperty(
				"jdbc.default.driverClassName", "com.mysql.cj.jdbc.Driver");
			portalUpgradeExtProperties.setProperty(
				"jdbc.default.password", StringPool.BLANK);
			portalUpgradeExtProperties.setProperty(
				"jdbc.default.url",
				StringBundler.concat(
					"jdbc:mysql://localhost/lportal?characterEncoding=UTF-8",
					"&dontTrackOpenResources=true",
					"&holdResultsOpenOverStatementClose=true",
					"&serverTimezone=GMT&useFastDateParsing=false",
					"&useUnicode=true"));
			portalUpgradeExtProperties.setProperty(
				"jdbc.default.username", StringPool.BLANK);
		}

		portalUpgradeExtProperties.setProperty(
			"liferay.home", _liferayHomeDir.getCanonicalPath());

		portalUpgradeExtProperties.store(
			new File(_rootDir, "portal-upgrade-ext.properties"));
	}

	private void _testHelp(
			List<String> args, String errorMessage, int exitCode,
			List<String> jvmArgs, String... outputMessages)
		throws Exception {

		List<String> commands = new ArrayList<>();

		commands.add(
			StringBundler.concat(
				System.getProperty("java.home"), File.separator, "bin",
				File.separator, "java"));
		commands.addAll(jvmArgs);
		commands.add("-cp");
		commands.add(System.getProperty("java.class.path"));
		commands.add(DBUpgradeClient.class.getName());
		commands.addAll(args);

		File errorFile = temporaryFolder.newFile();
		File outputFile = temporaryFolder.newFile();

		Process process = new ProcessBuilder(
			commands
		).redirectError(
			errorFile
		).redirectOutput(
			outputFile
		).start();

		Assert.assertEquals(exitCode, process.waitFor());

		String errorFileContent = new String(
			Files.readAllBytes(errorFile.toPath()), StringPool.UTF8);

		Assert.assertEquals(errorMessage, errorFileContent.trim());

		String outputFileContent = new String(
			Files.readAllBytes(outputFile.toPath()), StringPool.UTF8);

		for (String outputMessage : outputMessages) {
			Assert.assertTrue(outputFileContent.contains(outputMessage));
		}
	}

	private static final AppServer _appServer = Mockito.mock(AppServer.class);
	private static File _liferayHomeDir;
	private static File _rootDir;
	private static File _shieldedContainerLib;
	private static File _tomcatDir;

	private final ByteArrayOutputStream _consoleByteArrayOutputStream =
		new ByteArrayOutputStream();
	private DBUpgradeClient _dbUpgradeClient;
	private final ByteArrayOutputStream _errorByteArrayOutputStream =
		new ByteArrayOutputStream();
	private final PrintStream _originalErrorOutputStream = System.err;
	private final PrintStream _originalOutputStream = System.out;

}