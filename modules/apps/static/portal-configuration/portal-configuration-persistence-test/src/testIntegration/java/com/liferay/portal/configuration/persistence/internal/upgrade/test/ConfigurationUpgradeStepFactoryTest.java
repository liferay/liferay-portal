/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.net.URI;

import java.util.Dictionary;

import org.apache.felix.cm.PersistenceManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class ConfigurationUpgradeStepFactoryTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_configsDir = new File(
			_props.get(PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR));
	}

	@After
	public void tearDown() throws Exception {
		for (File file : _configsDir.listFiles()) {
			String fileName = file.getName();

			if (fileName.startsWith(_TEST_PID)) {
				file.delete();
			}
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.pid=" + _TEST_PID + "*)");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testUpgradeConfigWithDBAndFile() throws Exception {
		_testUpgradeConfig(false, true, true, true);
	}

	@Test
	public void testUpgradeConfigWithDBWithoutFile() throws Exception {
		_testUpgradeConfig(false, true, false, false);
	}

	@Test
	public void testUpgradeConfigWithDBWithoutFileWithFileName()
		throws Exception {

		_testUpgradeConfig(false, true, false, true);
	}

	@Test
	public void testUpgradeConfigWithoutDBWithFile() throws Exception {
		_testUpgradeConfig(false, false, true, true);
	}

	@Test
	public void testUpgradeFactoryConfigWithDBAndFile() throws Exception {
		_testUpgradeConfig(true, true, true, true);
	}

	@Test
	public void testUpgradeFactoryConfigWithDBWithoutFile() throws Exception {
		_testUpgradeConfig(true, true, false, false);
	}

	@Test
	public void testUpgradeFactoryConfigWithDBWithoutFileWithFileName()
		throws Exception {

		_testUpgradeConfig(true, true, false, true);
	}

	@Test
	public void testUpgradeFactoryConfigWithoutDBWithFile() throws Exception {
		_testUpgradeConfig(true, false, true, true);
	}

	private void _assert(
			boolean factory, boolean data, boolean configFile,
			boolean felixFileName, boolean upgraded)
		throws Exception {

		File oldConfigFile = _getConfigFile(_TEST_PID_OLD, factory);
		File newConfigFile = _getConfigFile(_TEST_PID_NEW, factory);

		if (!configFile) {
			Assert.assertFalse(
				"Configuration file " + oldConfigFile + " exists",
				oldConfigFile.exists());
			Assert.assertFalse(
				"Configuration file " + newConfigFile + " exists",
				newConfigFile.exists());
		}
		else if (upgraded) {
			Assert.assertFalse(
				"Configuration file " + oldConfigFile + " exists",
				oldConfigFile.exists());
			Assert.assertTrue(
				"Configuration file " + newConfigFile + " does not exist",
				newConfigFile.exists());
		}
		else {
			Assert.assertTrue(
				"Configuration file " + oldConfigFile + " does not exist",
				oldConfigFile.exists());
			Assert.assertFalse(
				"Configuration file " + newConfigFile + " exists",
				newConfigFile.exists());
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.pid=" + _TEST_PID + "*)");

		if (!data) {
			Assert.assertNull(
				StringBundler.concat(
					"Configurations with ", _TEST_PID, " as prefix of service ",
					"pid should not exist"),
				configurations);

			return;
		}

		Assert.assertNotNull(
			StringBundler.concat(
				"Configurations with ", _TEST_PID, " as prefix of service pid ",
				"should exist"),
			configurations);

		Assert.assertEquals(
			configurations.toString(), 1, configurations.length);

		Configuration configuration = configurations[0];

		String existentPid = configuration.getPid();

		String nonexistentPid = null;

		if (upgraded) {
			nonexistentPid = StringUtil.replace(
				existentPid, _TEST_PID_NEW, _TEST_PID_OLD);
		}
		else {
			nonexistentPid = StringUtil.replace(
				existentPid, _TEST_PID_OLD, _TEST_PID_NEW);
		}

		Assert.assertFalse(
			"Configuration " + nonexistentPid + " still exists",
			_persistenceManager.exists(nonexistentPid));
		Assert.assertTrue(
			"Configuration " + existentPid + " does not exist",
			_persistenceManager.exists(existentPid));

		Dictionary<String, String> dictionary = _persistenceManager.load(
			existentPid);

		String fileName = dictionary.get(
			FileInstallConstants.FELIX_FILE_INSTALL_FILENAME);

		if (!felixFileName) {
			Assert.assertNull(
				"Configuration property " +
					FileInstallConstants.FELIX_FILE_INSTALL_FILENAME +
						" should not exist",
				fileName);

			return;
		}

		File existentConfigFile;

		if (upgraded) {
			existentConfigFile = newConfigFile;
		}
		else {
			existentConfigFile = oldConfigFile;
		}

		URI uri = existentConfigFile.toURI();

		Assert.assertEquals(uri.toString(), fileName);
	}

	private File _getConfigFile(String pid, boolean factory) {
		if (factory) {
			return new File(_configsDir, pid + "-instance.config");
		}

		return new File(_configsDir, pid + ".config");
	}

	private void _initialize(
			boolean factory, boolean data, boolean configFile,
			boolean felixFileName)
		throws Exception {

		File oldConfigFile = _getConfigFile(_TEST_PID_OLD, factory);

		if (configFile) {
			oldConfigFile.createNewFile();
		}

		if (data) {
			Dictionary<String, Object> properties;

			if (felixFileName) {
				URI uri = oldConfigFile.toURI();

				properties = MapUtil.singletonDictionary(
					FileInstallConstants.FELIX_FILE_INSTALL_FILENAME,
					uri.toString());
			}
			else {
				properties = new HashMapDictionary<>();
			}

			if (factory) {
				ConfigurationTestUtil.createFactoryConfiguration(
					_TEST_PID_OLD, properties);
			}
			else {
				ConfigurationTestUtil.saveConfiguration(
					_configurationAdmin.getConfiguration(_TEST_PID_OLD),
					properties);
			}
		}
	}

	private void _testUpgradeConfig(
			boolean factory, boolean data, boolean configFile,
			boolean felixFileName)
		throws Exception {

		_initialize(factory, data, configFile, felixFileName);

		_assert(factory, data, configFile, felixFileName, false);

		UpgradeStep upgradeStep =
			_configurationUpgradeStepFactory.createUpgradeStep(
				_TEST_PID_OLD, _TEST_PID_NEW);

		upgradeStep.upgrade();

		_assert(factory, data, configFile, felixFileName, true);
	}

	private static final String _TEST_PID = RandomTestUtil.randomString();

	private static final String _TEST_PID_NEW = _TEST_PID + ".new";

	private static final String _TEST_PID_OLD = _TEST_PID + ".old";

	private File _configsDir;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Inject
	private PersistenceManager _persistenceManager;

	@Inject
	private Props _props;

}