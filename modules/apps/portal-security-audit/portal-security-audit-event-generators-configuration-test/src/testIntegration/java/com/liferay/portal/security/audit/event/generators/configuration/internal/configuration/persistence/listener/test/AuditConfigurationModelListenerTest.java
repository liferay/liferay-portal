/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.configuration.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class AuditConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = bundle.getBundleContext();

		_serviceRegistration = _bundleContext.registerService(
			AuditMessageProcessor.class, _auditMessages::add,
			HashMapDictionaryBuilder.<String, Object>put(
				"eventTypes", "*"
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_serviceRegistration.unregister();

		for (Configuration configuration : _configurations) {
			configuration.delete();
		}
	}

	@Test
	public void testOnAfterDelete() throws Exception {
		_testOnAfterDelete();
		_testOnAfterDeleteWithAuditConfiguration();
	}

	@Test
	public void testOnAfterSave() throws Exception {
		_testOnAfterSave();
		_testOnAfterSaveWithAuditConfiguration();
		_testOnAfterSaveWithCompanyScope();
		_testOnAfterSaveWithFileName();
		_testOnAfterSaveWithGroupScope();
		_testOnAfterSaveWithLock();
		_testOnAfterSaveWithLoggingAuditMessageProcessor();
		_testOnAfterSaveWithPortletInstanceScope();
		_testOnAfterSaveWithRejection();
		_testOnAfterSaveWithReload();
		_testOnAfterSaveWithRewrite();
	}

	private Configuration _addConfiguration(
			String factoryPid, Dictionary<String, Object> properties)
		throws Exception {

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				factoryPid, StringPool.QUESTION);

		_configurations.add(configuration);

		configuration.update(properties);

		return configuration;
	}

	private Configuration _addConfiguration(String factoryPid, String value)
		throws Exception {

		return _addConfiguration(
			factoryPid,
			HashMapDictionaryBuilder.<String, Object>put(
				"key", value
			).build());
	}

	private void _assertAttribute(AuditMessage auditMessage, String name) {
		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONArray attributesJSONArray = additionalInfoJSONObject.getJSONArray(
			"attributes");

		Assert.assertEquals(1, attributesJSONArray.length());

		JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(0);

		Assert.assertEquals(name, attributeJSONObject.getString("name"));
		Assert.assertEquals(
			StringPool.BLANK, attributeJSONObject.getString("newValue"));
		Assert.assertEquals(
			StringPool.BLANK, attributeJSONObject.getString("oldValue"));
	}

	private void _assertNotContains(AuditMessage auditMessage, String value) {
		String json = String.valueOf(auditMessage.toJSONObject());

		Assert.assertFalse(json.contains(value));
	}

	private AuditMessage _fetchAuditMessage(String eventType, String pid) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(auditMessage.getClassPK(), pid) &&
				Objects.equals(auditMessage.getEventType(), eventType)) {

				return auditMessage;
			}
		}

		return null;
	}

	private AuditMessage _getAuditMessage(String eventType, String pid) {
		AuditMessage auditMessage = _fetchAuditMessage(eventType, pid);

		Assert.assertNotNull(auditMessage);

		return auditMessage;
	}

	private void _testOnAfterDelete() throws Exception {
		String value = RandomTestUtil.randomString();

		Configuration configuration = _addConfiguration(
			RandomTestUtil.randomString(), value);

		_configurations.remove(configuration);

		String pid = configuration.getPid();

		configuration.delete();

		AuditMessage auditMessage = _getAuditMessage(EventTypes.DELETE, pid);

		_assertAttribute(auditMessage, "key");
		_assertNotContains(auditMessage, value);

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"system", additionalInfoJSONObject.getString("scope"));
	}

	private void _testOnAfterDeleteWithAuditConfiguration() throws Exception {
		Configuration configuration = _addConfiguration(
			"com.liferay.portal.security.audit." +
				RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_configurations.remove(configuration);

		String pid = configuration.getPid();

		_auditMessages.clear();

		configuration.delete();

		AuditMessage auditMessage = _getAuditMessage(
			"AUDIT_CONFIG_CHANGE", pid);

		_assertAttribute(auditMessage, "key");

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"delete", additionalInfoJSONObject.getString("action"));
	}

	private void _testOnAfterSave() throws Exception {
		String factoryPid = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		Configuration configuration = _addConfiguration(factoryPid, value);

		AuditMessage auditMessage = _getAuditMessage(
			EventTypes.ADD, configuration.getPid());

		Assert.assertEquals(factoryPid, auditMessage.getClassName());

		_assertAttribute(auditMessage, "key");
		_assertNotContains(auditMessage, value);

		_auditMessages.clear();

		String newValue = RandomTestUtil.randomString();

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				"key", newValue
			).build());

		auditMessage = _getAuditMessage(
			EventTypes.UPDATE, configuration.getPid());

		_assertAttribute(auditMessage, "key");
		_assertNotContains(auditMessage, newValue);
		_assertNotContains(auditMessage, value);

		_auditMessages.clear();

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				"key", newValue
			).build());

		Assert.assertNull(
			_fetchAuditMessage(EventTypes.UPDATE, configuration.getPid()));
	}

	private void _testOnAfterSaveWithAuditConfiguration() throws Exception {
		Configuration configuration = _addConfiguration(
			"com.liferay.portal.security.audit." +
				RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		AuditMessage auditMessage = _getAuditMessage(
			"AUDIT_CONFIG_CHANGE", configuration.getPid());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"add", additionalInfoJSONObject.getString("action"));
	}

	private void _testOnAfterSaveWithCompanyScope() throws Exception {
		String factoryPid = RandomTestUtil.randomString();
		Configuration configuration = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					CompanyConstants.SYSTEM)) {

			configuration = _addConfiguration(
				factoryPid + ".scoped",
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", TestPropsValues.getCompanyId()
				).put(
					"key", RandomTestUtil.randomString()
				).build());
		}

		AuditMessage auditMessage = _getAuditMessage(
			EventTypes.ADD, configuration.getPid());

		Assert.assertEquals(factoryPid, auditMessage.getClassName());
		Assert.assertEquals(
			TestPropsValues.getCompanyId(), auditMessage.getCompanyId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"company", additionalInfoJSONObject.getString("scope"));
	}

	private void _testOnAfterSaveWithFileName() throws Exception {
		String fileName = RandomTestUtil.randomString() + ".config";

		Configuration configuration = _addConfiguration(
			RandomTestUtil.randomString(),
			HashMapDictionaryBuilder.<String, Object>put(
				FileInstallConstants.FELIX_FILE_INSTALL_FILENAME, fileName
			).put(
				"key", RandomTestUtil.randomString()
			).build());

		AuditMessage auditMessage = _getAuditMessage(
			EventTypes.ADD, configuration.getPid());

		_assertAttribute(auditMessage, "key");

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			fileName, additionalInfoJSONObject.getString("fileName"));
	}

	private void _testOnAfterSaveWithGroupScope() throws Exception {
		Configuration configuration = _addConfiguration(
			RandomTestUtil.randomString() + ".scoped",
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"groupId", TestPropsValues.getGroupId()
			).put(
				"key", RandomTestUtil.randomString()
			).build());

		AuditMessage auditMessage = _getAuditMessage(
			EventTypes.ADD, configuration.getPid());

		Assert.assertEquals(
			TestPropsValues.getGroupId(), auditMessage.getGroupId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"group", additionalInfoJSONObject.getString("scope"));
	}

	private void _testOnAfterSaveWithLock() throws Exception {
		Configuration configuration = _addConfiguration(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_auditMessages.clear();

		configuration.addAttributes(
			Configuration.ConfigurationAttribute.READ_ONLY);

		Dictionary<?, ?> dictionary = _reloadablePersistenceManager.load(
			configuration.getPid());

		Assert.assertNotNull(
			dictionary.get(":org.apache.felix.configadmin.locked:"));

		configuration.removeAttributes(
			Configuration.ConfigurationAttribute.READ_ONLY);

		Assert.assertNull(
			_fetchAuditMessage(EventTypes.UPDATE, configuration.getPid()));
	}

	private void _testOnAfterSaveWithLoggingAuditMessageProcessor()
		throws Exception {

		PrintStream printStream = System.out;

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"logMessageFormat", "JSON"
					).put(
						"outputToConsole", true
					).build())) {

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			System.setOut(new PrintStream(byteArrayOutputStream));

			Configuration configuration = _addConfiguration(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			String logMessage = null;

			for (String line :
					StringUtil.splitLines(byteArrayOutputStream.toString())) {

				if (!line.startsWith("LoggingAuditMessageProcessor: ") ||
					!line.contains(configuration.getPid())) {

					continue;
				}

				logMessage = line.substring(
					line.indexOf(CharPool.OPEN_CURLY_BRACE));

				break;
			}

			AuditMessage auditMessage = _getAuditMessage(
				EventTypes.ADD, configuration.getPid());

			JSONAssert.assertEquals(
				String.valueOf(auditMessage.toJSONObject()), logMessage,
				JSONCompareMode.STRICT);
		}
		finally {
			System.setOut(printStream);
		}
	}

	private void _testOnAfterSaveWithPortletInstanceScope() throws Exception {
		Configuration configuration = _addConfiguration(
			RandomTestUtil.randomString() + ".scoped",
			HashMapDictionaryBuilder.<String, Object>put(
				"key", RandomTestUtil.randomString()
			).put(
				"portletInstanceId", RandomTestUtil.randomString()
			).build());

		AuditMessage auditMessage = _getAuditMessage(
			EventTypes.ADD, configuration.getPid());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"portlet-instance", additionalInfoJSONObject.getString("scope"));
	}

	private void _testOnAfterSaveWithRejection() throws Exception {
		String factoryPid = RandomTestUtil.randomString();

		ServiceRegistration<ConfigurationModelListener> serviceRegistration =
			_bundleContext.registerService(
				ConfigurationModelListener.class,
				new ConfigurationModelListener() {

					@Override
					public void onBeforeSave(
							String pid, Dictionary<String, Object> properties)
						throws ConfigurationModelListenerException {

						throw new ConfigurationModelListenerException(
							RandomTestUtil.randomString(), getClass(),
							getClass(), properties);
					}

				},
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", factoryPid
				).build());

		try {
			Configuration configuration =
				_configurationAdmin.createFactoryConfiguration(
					factoryPid, StringPool.QUESTION);

			_configurations.add(configuration);

			Assert.assertThrows(
				IOException.class,
				() -> configuration.update(
					HashMapDictionaryBuilder.<String, Object>put(
						"key", RandomTestUtil.randomString()
					).build()));

			Assert.assertNull(
				_fetchAuditMessage(EventTypes.ADD, configuration.getPid()));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testOnAfterSaveWithReload() throws Exception {
		Configuration configuration = _addConfiguration(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_auditMessages.clear();

		Dictionary<String, Object> dictionary =
			(Dictionary<String, Object>)_reloadablePersistenceManager.reload(
				configuration.getPid());

		configuration.update(dictionary);

		Assert.assertNull(
			_fetchAuditMessage(EventTypes.UPDATE, configuration.getPid()));
	}

	private void _testOnAfterSaveWithRewrite() throws Exception {
		String factoryPid = RandomTestUtil.randomString();
		String rewrittenValue = RandomTestUtil.randomString();

		ServiceRegistration<ConfigurationModelListener> serviceRegistration =
			_bundleContext.registerService(
				ConfigurationModelListener.class,
				new ConfigurationModelListener() {

					@Override
					public void onBeforeSave(
						String pid, Dictionary<String, Object> properties) {

						if (Objects.equals(
								properties.get(
									ConfigurationAdmin.SERVICE_FACTORYPID),
								factoryPid)) {

							properties.put("key", rewrittenValue);
						}
					}

				},
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", "*"
				).put(
					"service.ranking", 1
				).build());

		try {
			Configuration configuration = _addConfiguration(
				factoryPid, RandomTestUtil.randomString());

			_auditMessages.clear();

			configuration.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"key", RandomTestUtil.randomString()
				).build());

			_assertAttribute(
				_getAuditMessage(EventTypes.UPDATE, configuration.getPid()),
				"key");
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private final List<AuditMessage> _auditMessages =
		new CopyOnWriteArrayList<>();
	private BundleContext _bundleContext;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private final List<Configuration> _configurations = new ArrayList<>();

	@Inject
	private ReloadablePersistenceManager _reloadablePersistenceManager;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}