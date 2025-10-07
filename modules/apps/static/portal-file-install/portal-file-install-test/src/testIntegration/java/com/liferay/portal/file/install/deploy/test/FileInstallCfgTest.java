/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.deploy.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class FileInstallCfgTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(FileInstallCfgTest.class);

		_bundleContext = bundle.getBundleContext();

		_originalModuleFrameworkFileInstallCfgEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "MODULE_FRAMEWORK_FILE_INSTALL_CFG_ENABLED",
				true);
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "MODULE_FRAMEWORK_FILE_INSTALL_CFG_ENABLED",
			_originalModuleFrameworkFileInstallCfgEnabled);
	}

	@After
	public void tearDown() throws Exception {
		_deleteConfiguration();
	}

	@Test
	public void testConfiguration() throws Exception {
		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfiguration");

		_configurationPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".cfg"));

		String testKey = "testKey";
		String testValue = "testValue";

		_configuration = _createConfiguration(
			configurationPid, testKey + StringPool.EQUAL + testValue);

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals("testValue", dictionary.get(testKey));
	}

	private Configuration _createConfiguration(
			String configurationPid, String content)
		throws Exception {

		return ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.write(
				_configurationPath,
				content.getBytes(Charset.defaultCharset())));
	}

	private void _deleteConfiguration() throws Exception {
		if (_configurationPath != null) {
			Files.deleteIfExists(_configurationPath);
		}

		if (_configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(_configuration);
		}
	}

	private static final String _CONFIGURATION_PID_PREFIX =
		FileInstallCfgTest.class.getName() + "Configuration";

	private static BundleContext _bundleContext;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static boolean _originalModuleFrameworkFileInstallCfgEnabled;

	private Configuration _configuration;
	private Path _configurationPath;

}