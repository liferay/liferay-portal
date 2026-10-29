/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class OAuth2ApplicationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class, _auditMessages::add,
			HashMapDictionaryBuilder.<String, Object>put(
				"eventTypes", "*"
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_serviceRegistration.unregister();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnAfterCreate() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"logMessageFormat", "JSON"
					).build());
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.security.audit.router.internal." +
					"LoggingAuditMessageProcessor",
				LoggerTestUtil.INFO)) {

			_oAuth2Application = _addOAuth2Application();

			AuditMessage auditMessage = _fetchAuditMessage(EventTypes.ADD);

			_assertAuditMessage(auditMessage, _oAuth2Application);

			Assert.assertEquals(
				"instance.oauth2application.add",
				auditMessage.getResourceAction());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			JSONAssert.assertEquals(
				String.valueOf(auditMessage.toJSONObject()),
				logEntry.getMessage(), JSONCompareMode.NON_EXTENSIBLE);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		OAuth2Application oAuth2Application = _addOAuth2Application();

		_oAuth2ApplicationLocalService.deleteOAuth2Application(
			oAuth2Application.getOAuth2ApplicationId());

		_assertAuditMessage(
			_fetchAuditMessage(EventTypes.DELETE), oAuth2Application);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_oAuth2Application = _addOAuth2Application();

		_auditMessages.clear();

		_oAuth2Application =
			_oAuth2ApplicationLocalService.updateOAuth2Application(
				_oAuth2Application);

		Assert.assertNull(_fetchAuditMessage(EventTypes.UPDATE));

		_oAuth2Application.setClientSecret(RandomTestUtil.randomString());
		_oAuth2Application.setJwks(RandomTestUtil.randomString());

		long newOAuth2ApplicationScopeAliasesId = RandomTestUtil.randomLong();
		long oAuth2ApplicationScopeAliasesId =
			_oAuth2Application.getOAuth2ApplicationScopeAliasesId();

		_oAuth2Application.setOAuth2ApplicationScopeAliasesId(
			newOAuth2ApplicationScopeAliasesId);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_oAuth2Application =
				_oAuth2ApplicationLocalService.updateOAuth2Application(
					_oAuth2Application);
		}

		AuditMessage auditMessage = _fetchAuditMessage(EventTypes.UPDATE);

		Assert.assertEquals(
			_oAuth2Application.getCompanyId(), auditMessage.getCompanyId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"name", "OAuth2ApplicationScopeAliasesId"
				).put(
					"newValue",
					String.valueOf(newOAuth2ApplicationScopeAliasesId)
				).put(
					"oldValue", String.valueOf(oAuth2ApplicationScopeAliasesId)
				),
				JSONUtil.put(
					"name", "clientSecret"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				),
				JSONUtil.put(
					"name", "jwks"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				)
			).toString(),
			String.valueOf(additionalInfoJSONObject.getJSONArray("attributes")),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private OAuth2Application _addOAuth2Application() throws Exception {
		User user = TestPropsValues.getUser();

		return _oAuth2ApplicationLocalService.addOAuth2Application(
			TestPropsValues.getCompanyId(), user.getUserId(),
			user.getFullName(),
			ListUtil.fromArray(GrantType.CLIENT_CREDENTIALS),
			"client_secret_post", user.getUserId(), null, 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, 0, null, RandomTestUtil.randomString(), null, null, false,
			null, false, new ServiceContext());
	}

	private void _assertAuditMessage(
			AuditMessage auditMessage, OAuth2Application oAuth2Application)
		throws Exception {

		Assert.assertEquals(
			oAuth2Application.getCompanyId(), auditMessage.getCompanyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"clientId", oAuth2Application.getClientId()
			).put(
				"externalReferenceCode",
				oAuth2Application.getExternalReferenceCode()
			).put(
				"name", oAuth2Application.getName()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private AuditMessage _fetchAuditMessage(String eventType) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(
					auditMessage.getClassName(),
					OAuth2Application.class.getName()) &&
				Objects.equals(auditMessage.getEventType(), eventType)) {

				return auditMessage;
			}
		}

		return null;
	}

	private final List<AuditMessage> _auditMessages =
		new CopyOnWriteArrayList<>();

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private OAuth2Application _oAuth2Application;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}