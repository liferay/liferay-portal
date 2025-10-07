/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.operation.test;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.rule.Inject;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.felix.cm.PersistenceManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class BasePortalInstanceOperationTestCase
	extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		BaseDBPartitionTestCase.setUpDBPartitions();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		BaseDBPartitionTestCase.tearDownDBPartitions();
	}

	@After
	public void tearDown() throws Exception {
		_deleteConfiguration();
	}

	protected void assertConfigurationFileIsDeletedAfterDeploy(String pid)
		throws Exception {

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));
		Assert.assertFalse(Files.exists(_configurationPath));
		Assert.assertNull(
			ReflectionTestUtil.invoke(
				_persistenceManager, "_getDictionary",
				new Class<?>[] {String.class}, pid));
	}

	protected void assertConfigurationIsDeletedAfterDeploy(String pid)
		throws Exception {

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));

		Dictionary<Object, Object> dictionary = ReflectionTestUtil.invoke(
			_persistenceManager, "_getDictionary",
			new Class<?>[] {String.class}, pid);

		Assert.assertTrue(
			(boolean)dictionary.get("_felix_.cm.newConfiguration"));
	}

	protected void assertLog(LogCapture logCapture, String expectedMessage) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(expectedMessage, logEntry.getMessage());
	}

	protected void assertLogException(
		LogCapture logCapture, String expectedMessage) {

		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Throwable throwable = logEntry.getThrowable();

		Assert.assertEquals(expectedMessage, throwable.getMessage());
	}

	protected void deployConfiguration(
			String pid, Dictionary<String, Object> properties)
		throws Exception {

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));

		try (AutoCloseable autoCloseable =
				_registerOnAfterDeleteConfigurationModelListener(pid)) {

			ConfigurationTestUtil.saveConfiguration(pid, properties);

			_countDownLatch.await(180, TimeUnit.SECONDS);
		}
	}

	protected void deployConfigurationFile(String pid, String content)
		throws Exception {

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));

		_configurationPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, pid.concat(".config"));

		try (AutoCloseable autoCloseable =
				_registerOnAfterDeleteConfigurationModelListener(pid)) {

			_createConfigurationFile(pid, content);

			Assert.assertNotNull(
				_configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")"));

			_countDownLatch.await(180, TimeUnit.SECONDS);
		}
	}

	protected abstract String getComponentName();

	private Configuration _createConfigurationFile(
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
	}

	private AutoCloseable _registerOnAfterDeleteConfigurationModelListener(
		String pid) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_countDownLatch = new CountDownLatch(1);

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ConfigurationModelListener.class,
				new ConfigurationModelListener() {

					@Override
					public void onAfterDelete(String pid) {
						_countDownLatch.countDown();
					}

				},
				HashMapDictionaryBuilder.put(
					"model.class.name", pid
				).build());

		return serviceRegistration::unregister;
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private Path _configurationPath;
	private CountDownLatch _countDownLatch;

	@Inject
	private PersistenceManager _persistenceManager;

}