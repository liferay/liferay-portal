/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.upgrade.client;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.db.upgrade.client.util.Properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;

import jline.console.ConsoleReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

		ReflectionTestUtil.setFieldValue(
			DBUpgradeClient.class, "_jarDir", _rootDir);
	}

	@Before
	public void setUp() throws Exception {
		System.setErr(new PrintStream(_errorOutputStream));
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
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

		File portalUpgradeExtPropertiesFile = new File(
			_rootDir, "portal-upgrade-ext.properties");

		if (portalUpgradeExtPropertiesFile.exists()) {
			portalUpgradeExtPropertiesFile.delete();
		}

		System.setErr(_originalErrorOutputStream);
		System.setOut(_originalOutputStream);
	}

	@Test
	public void testVerifyAppServerProperties() throws Exception {
		_createPortalUpgradeExtPropertiesFile();

		String[] answers = {
			StringPool.BLANK, "invalidAppServerDirName",
			_tomcatDir.getAbsolutePath(), "invalidExtraLibDirName", "bin",
			"globalLibDirName", "lib", "portalDirNsme", "webapps/ROOT"
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyAppServerProperties", new Class<?>[0]);

		String errorOutput = _errorOutputStream.toString();

		Assert.assertTrue(
			errorOutput.contains("does not exist or is not a directory"));

		AppServer appServer = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_appServer");

		Assert.assertNotNull(appServer);
		Assert.assertEquals(_tomcatDir, appServer.getDir());
		Assert.assertEquals("bin", appServer.getExtraLibDirNames());
		Assert.assertEquals("lib", appServer.getGlobalLibDirName());
		Assert.assertEquals("webapps/ROOT", appServer.getPortalDirName());
		Assert.assertEquals("tomcat", appServer.getServerDetectorServerId());
	}

	@Test
	public void testVerifyAppServerPropertiesWithEmptyAnswers()
		throws Exception {

		_createPortalUpgradeExtPropertiesFile();

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
		Assert.assertEquals("/bin", appServer.getExtraLibDirNames());
		Assert.assertEquals("/lib", appServer.getGlobalLibDirName());
		Assert.assertEquals("/webapps/ROOT", appServer.getPortalDirName());
		Assert.assertEquals("tomcat", appServer.getServerDetectorServerId());
	}

	@Test
	public void testVerifyPortalUpgradeDatabaseProperties() throws Exception {
		String[] answers = {
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, "invalidHost",
			"localhost", "abc", "99999", StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeDatabaseProperties",
			new Class<?>[0]);

		String errorOutput = _errorOutputStream.toString();

		Assert.assertTrue(errorOutput.contains("Unable to resolve host"));
		Assert.assertTrue(errorOutput.contains("is not a valid port number"));
		Assert.assertTrue(
			errorOutput.contains("Port must be between 0 and 65535"));

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeDatabaseProperties");

		Assert.assertNotNull(properties);
		Assert.assertEquals(
			"com.mysql.cj.jdbc.Driver",
			properties.getProperty("jdbc.default.driverClassName"));
		Assert.assertEquals(
			StringBundler.concat(
				"jdbc:mysql://localhost/lportal?characterEncoding=UTF-8",
				"&dontTrackOpenResources=true",
				"&holdResultsOpenOverStatementClose=true",
				"&serverTimezone=GMT&useFastDateParsing=false",
				"&useUnicode=true"),
			properties.getProperty("jdbc.default.url"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.username"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.password"));
	}

	@Test
	public void testVerifyPortalUpgradeDatabasePropertiesWithEmptyAnswers()
		throws Exception {

		String[] answers = {
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeDatabaseProperties",
			new Class<?>[0]);

		Properties properties = ReflectionTestUtil.getFieldValue(
			_dbUpgradeClient, "_portalUpgradeDatabaseProperties");

		Assert.assertNotNull(properties);
		Assert.assertEquals(
			"com.mysql.cj.jdbc.Driver",
			properties.getProperty("jdbc.default.driverClassName"));
		Assert.assertEquals(
			StringBundler.concat(
				"jdbc:mysql://localhost/lportal?characterEncoding=UTF-8",
				"&dontTrackOpenResources=true",
				"&holdResultsOpenOverStatementClose=true",
				"&serverTimezone=GMT&useFastDateParsing=false",
				"&useUnicode=true"),
			properties.getProperty("jdbc.default.url"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.username"));
		Assert.assertEquals(
			StringPool.BLANK, properties.getProperty("jdbc.default.password"));
	}

	@Test
	public void testVerifyPortalUpgradeExtProperties() throws Exception {
		File liferayHomeDir = new File(_rootDir, "custom-liferay-home");

		liferayHomeDir.mkdirs();

		String[] answers = {
			RandomTestUtil.randomString(), liferayHomeDir.getAbsolutePath()
		};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtProperties",
			new Class<?>[0]);

		String errorOutput = _errorOutputStream.toString();

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
	public void testVerifyPortalUpgradeExtPropertiesWithEmptyAnswers()
		throws Exception {

		String[] answers = {StringPool.BLANK};

		_dbUpgradeClient = _createDBUpgradeClient(answers);

		ReflectionTestUtil.invoke(
			_dbUpgradeClient, "_verifyPortalUpgradeExtProperties",
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

	private void _createPortalUpgradeExtPropertiesFile() throws Exception {
		File portalUpgradeExtPropertiesFile = new File(
			_rootDir, "portal-upgrade-ext.properties");

		Properties portalUpgradeExtProperties = new Properties();

		portalUpgradeExtProperties.setProperty(
			"liferay.home", _liferayHomeDir.getCanonicalPath());

		portalUpgradeExtProperties.store(portalUpgradeExtPropertiesFile);
	}

	private static File _liferayHomeDir;
	private static File _rootDir;
	private static File _tomcatDir;

	private DBUpgradeClient _dbUpgradeClient;
	private final OutputStream _errorOutputStream = new ByteArrayOutputStream();
	private final PrintStream _originalErrorOutputStream = System.err;
	private final PrintStream _originalOutputStream = System.out;

}