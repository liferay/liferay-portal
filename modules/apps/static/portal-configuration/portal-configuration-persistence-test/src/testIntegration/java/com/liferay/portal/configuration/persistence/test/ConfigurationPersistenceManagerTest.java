/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.configuration.persistence.InMemoryOnlyConfigurationThreadLocal;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Closeable;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.felix.cm.PersistenceManager;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class ConfigurationPersistenceManagerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testConfigurationOverride() throws IOException {

		// Override nonexistent configuration

		String testPid = "testPid";

		_persistenceManager.delete(testPid);

		Map<String, String> overrideProperties = HashMapBuilder.put(
			"a", "A"
		).put(
			"b", "B"
		).build();

		try (Closeable closeable = _reloadWithOverrideProperties(
				testPid, overrideProperties)) {

			// Initial population

			Dictionary<Object, Object> dictionary = _persistenceManager.load(
				testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));

			// Runtime reload

			ReflectionTestUtil.invoke(
				_persistenceManager, "reload", new Class<?>[] {String.class},
				testPid);

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));

			// Runtime creation should not affect override

			Properties newProperties = new Properties();

			newProperties.put("a", "a");
			newProperties.put("b", "b");

			_persistenceManager.store(testPid, newProperties);

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));

			ReflectionTestUtil.invoke(
				_persistenceManager, "reload", new Class<?>[] {String.class},
				testPid);

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));
		}
		finally {
			_persistenceManager.delete(testPid);
		}

		// Override existing configuration

		Properties existingProperties = new Properties();

		existingProperties.put("a", "a");
		existingProperties.put("b", "b");

		_persistenceManager.store(testPid, existingProperties);

		Dictionary<Object, Object> dictionary = _persistenceManager.load(
			testPid);

		Assert.assertNotNull(dictionary);
		Assert.assertEquals("a", dictionary.get("a"));
		Assert.assertEquals("b", dictionary.get("b"));

		try (Closeable closeable = _reloadWithOverrideProperties(
				testPid, overrideProperties)) {

			// Initial population

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));

			// Runtime reload

			ReflectionTestUtil.invoke(
				_persistenceManager, "reload", new Class<?>[] {String.class},
				testPid);

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));

			// Runtime update should not affect override

			Properties newProperties = new Properties();

			newProperties.put("a", "c");
			newProperties.put("b", "d");

			_persistenceManager.store(testPid, newProperties);

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));

			ReflectionTestUtil.invoke(
				_persistenceManager, "reload", new Class<?>[] {String.class},
				testPid);

			dictionary = _persistenceManager.load(testPid);

			Assert.assertNotNull(dictionary);
			Assert.assertEquals("A", dictionary.get("a"));
			Assert.assertEquals("B", dictionary.get("b"));
		}
		finally {
			_persistenceManager.delete(testPid);
		}
	}

	@Test
	public void testConfigurationPersistenceManager() {
		Class<?> clazz = _persistenceManager.getClass();

		Assert.assertEquals(
			"com.liferay.portal.configuration.persistence.internal." +
				"ConfigurationPersistenceManager",
			clazz.getName());
	}

	@Test
	public void testCreateFactoryConfiguration() throws Exception {
		_assertConfiguration(true, true);
	}

	@Test
	public void testGetConfiguration() throws Exception {
		_assertConfiguration(false, true);
	}

	@Test
	public void testMemoryOnlyConfiguration() throws Exception {
		try (SafeCloseable safeCloseable =
				InMemoryOnlyConfigurationThreadLocal.
					setInMemoryOnlyWithSafeCloseable(true)) {

			_assertConfiguration(false, false);
		}
	}

	@Test
	public void testWhiteSpacedFelixFileInstallFileName() throws Exception {
		ReflectionTestUtil.invoke(
			_persistenceManager, "_verifyDictionary",
			new Class<?>[] {String.class, String.class}, "whitespace.pid",
			FileInstallConstants.FELIX_FILE_INSTALL_FILENAME +
				"=\"file:/whitespace path/file.config\"");
	}

	private void _assertConfiguration(boolean factory, boolean shouldBeStored)
		throws Exception {

		_assertConfiguration(new Hashtable<>(), factory, shouldBeStored);
	}

	private void _assertConfiguration(
			Map<String, Object> additionalProperties, boolean factory,
			boolean shouldBeStored)
		throws Exception {

		Configuration configuration = null;

		if (factory) {
			configuration = _configurationAdmin.getConfiguration("test.pid");

			Assert.assertTrue(_persistenceManager.exists("test.pid"));
		}
		else {
			configuration = _configurationAdmin.createFactoryConfiguration(
				"test.pid", StringPool.QUESTION);
		}

		String pid = configuration.getPid();

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.putAll(
				additionalProperties
			).put(
				"foo", "bar"
			).build());

		Assert.assertTrue(_persistenceManager.exists(pid));

		Dictionary<String, Object> properties = _persistenceManager.load(pid);

		Assert.assertEquals("bar", properties.get("foo"));

		_assertStoredInDatabase(pid, shouldBeStored);

		properties.put("fee", "fum");

		ConfigurationTestUtil.saveConfiguration(configuration, properties);

		properties = _persistenceManager.load(pid);

		Assert.assertEquals("bar", properties.get("foo"));
		Assert.assertEquals("fum", properties.get("fee"));

		ConfigurationTestUtil.deleteConfiguration(configuration);

		Assert.assertFalse(_persistenceManager.exists(pid));

		_assertStoredInDatabase(pid, false);
	}

	private void _assertStoredInDatabase(String pid, boolean shouldBeStored)
		throws Exception {

		try (Connection connection = _dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select configurationId, dictionary from Configuration_ " +
					"where configurationId = ?")) {

			preparedStatement.setString(1, pid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				boolean stored = resultSet.next();

				Assert.assertFalse(!shouldBeStored && stored);
				Assert.assertFalse(shouldBeStored && !stored);
			}
		}
	}

	private void _reload() {
		ReflectionTestUtil.invoke(_persistenceManager, "stop", new Class<?>[0]);

		ReflectionTestUtil.invoke(
			_persistenceManager, "start", new Class<?>[0]);
	}

	private Closeable _reloadWithOverrideProperties(
		String pid, Map<String, String> properties) {

		Map<String, Map<String, String>> innerMap =
			ReflectionTestUtil.getFieldValue(
				ConfigurationOverridePropertiesUtil.getOverridePropertiesMap(),
				"m");

		Map<String, Map<String, String>> backupMap = new HashMap<>(innerMap);

		innerMap.clear();

		innerMap.put(pid, properties);

		_reload();

		return () -> {
			innerMap.clear();

			innerMap.putAll(backupMap);

			_reload();
		};
	}

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private static DataSource _dataSource;

	@Inject
	private static PersistenceManager _persistenceManager;

}