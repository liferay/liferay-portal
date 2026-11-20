/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.file.install.properties.ConfigurationHandler;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class LoggingAuditMessageProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testProcess() throws Exception {
		Dictionary<String, Object> properties = _getFedRAMPProperties();

		Assert.assertEquals("?", properties.remove("service.bundleLocation"));
		Assert.assertEquals(properties.toString(), 2, properties.size());

		Log log = LogFactoryUtil.getLog(_CLASS_NAME);

		Assert.assertTrue(log.isInfoEnabled());

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					properties);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.INFO)) {

			String eventType = StringUtil.toUpperCase(
				RandomTestUtil.randomString());

			_auditRouter.route(
				new AuditMessage(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					RandomTestUtil.randomString(), eventType));

			List<String> messages = TransformUtil.transform(
				logCapture.getMessages(),
				message -> {
					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						message);

					if (!Objects.equals(
							jsonObject.getString("eventType"), eventType)) {

						return null;
					}

					return message;
				});

			Assert.assertEquals(messages.toString(), 1, messages.size());

			_testProcessWhenAuditIsDisabled(logCapture);
			_testProcessWhenProcessorIsDisabled(logCapture);
		}
	}

	private List<String> _getActions(LogCapture logCapture, String pid) {
		return TransformUtil.transform(
			logCapture.getMessages(),
			message -> {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					message);

				JSONObject additionalInfoJSONObject = jsonObject.getJSONObject(
					"additionalInfo");

				if (!Objects.equals(
						jsonObject.getString("eventType"),
						EventTypes.AUDIT_CONFIG_CHANGE) ||
					!Objects.equals(
						additionalInfoJSONObject.getString("pid"), pid)) {

					return null;
				}

				return additionalInfoJSONObject.getString("action");
			});
	}

	private Dictionary<String, Object> _getFedRAMPProperties()
		throws Exception {

		Dictionary<String, Object> fedRAMPProperties =
			new HashMapDictionary<>();

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();
		Properties properties = new Properties();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"portal-fedramp.properties")) {

			properties.load(inputStream);
		}

		String prefix =
			"configuration.override." +
				LoggingAuditMessageProcessorConfiguration.class.getName() + "_";

		for (String key : properties.stringPropertyNames()) {
			Assert.assertTrue(key.startsWith(prefix));

			fedRAMPProperties.put(
				key.substring(prefix.length()),
				ConfigurationHandler.read(properties.getProperty(key)));
		}

		return fedRAMPProperties;
	}

	private void _testProcessWhenAuditIsDisabled(LogCapture logCapture)
		throws Exception {

		logCapture.resetPriority(LoggerTestUtil.INFO);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					AuditConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).build())) {

			List<String> actions = _getActions(
				logCapture, AuditConfiguration.class.getName());

			Assert.assertEquals(actions.toString(), 1, actions.size());
		}

		List<String> actions = _getActions(
			logCapture, AuditConfiguration.class.getName());

		Assert.assertEquals(actions.toString(), 2, actions.size());
	}

	private void _testProcessWhenProcessorIsDisabled(LogCapture logCapture)
		throws Exception {

		logCapture.resetPriority(LoggerTestUtil.INFO);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).put(
						"logMessageFormat", "JSON"
					).build())) {

			List<String> actions = _getActions(
				logCapture,
				LoggingAuditMessageProcessorConfiguration.class.getName());

			Assert.assertEquals(actions.toString(), 1, actions.size());
		}

		List<String> actions = _getActions(
			logCapture,
			LoggingAuditMessageProcessorConfiguration.class.getName());

		Assert.assertEquals(actions.toString(), 2, actions.size());
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.security.audit.router.internal." +
			"LoggingAuditMessageProcessor";

	@Inject
	private AuditRouter _auditRouter;

}