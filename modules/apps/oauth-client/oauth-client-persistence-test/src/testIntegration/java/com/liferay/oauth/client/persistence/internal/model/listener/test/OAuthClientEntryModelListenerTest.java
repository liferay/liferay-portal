/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
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
public class OAuthClientEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				OAuthClientCompanyConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"authServerLocalNetworkAccessEnabled", true
				).build());
		_openIdConnectProviderHttpServer =
			new OpenIdConnectProviderHttpServer();

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

		_companyConfigurationTemporarySwapper.close();
		_openIdConnectProviderHttpServer.close();
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

			_oAuthClientEntry = _addOAuthClientEntry();

			AuditMessage auditMessage = _fetchAuditMessage(EventTypes.ADD);

			_assertAuditMessage(auditMessage, _oAuthClientEntry);

			Assert.assertEquals(
				"instance.oauthcliententry.add",
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
		OAuthClientEntry oAuthClientEntry = _addOAuthClientEntry();

		_oAuthClientEntryLocalService.deleteOAuthClientEntry(
			oAuthClientEntry.getOAuthClientEntryId());

		_assertAuditMessage(
			_fetchAuditMessage(EventTypes.DELETE), oAuthClientEntry);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_oAuthClientEntry = _addOAuthClientEntry();

		_oAuthClientEntry = _updateOAuthClientEntry(
			_oAuthClientEntry.getAuthRequestParametersJSON(),
			_oAuthClientEntry.getInfoJSON(),
			_oAuthClientEntry.getOIDCUserInfoMapperJSON(),
			_oAuthClientEntry.getTokenRequestParametersJSON());

		Assert.assertNull(_fetchAuditMessage(EventTypes.UPDATE));

		String oidcUserInfoMapperJSON =
			_oAuthClientEntry.getOIDCUserInfoMapperJSON();

		String newOIDCUserInfoMapperJSON = JSONUtil.put(
			"user",
			JSONUtil.put(
				"emailAddress", RandomTestUtil.randomString()
			).put(
				"firstName", RandomTestUtil.randomString()
			).put(
				"lastName", RandomTestUtil.randomString()
			)
		).toString();

		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_oAuthClientEntry = _updateOAuthClientEntry(
				_getRequestParametersJSON(),
				_getInfoJSON(
					_oAuthClientEntry.getClientId(),
					RandomTestUtil.randomString()),
				newOIDCUserInfoMapperJSON, _getRequestParametersJSON());
		}

		AuditMessage auditMessage = _fetchAuditMessage(EventTypes.UPDATE);

		Assert.assertEquals(
			_oAuthClientEntry.getCompanyId(), auditMessage.getCompanyId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"name", "OIDCUserInfoMapperJSON"
				).put(
					"newValue", newOIDCUserInfoMapperJSON
				).put(
					"oldValue", oidcUserInfoMapperJSON
				),
				JSONUtil.put(
					"name", "authRequestParametersJSON"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				),
				JSONUtil.put(
					"name", "infoJSON"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				),
				JSONUtil.put(
					"name", "tokenRequestParametersJSON"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				)
			).toString(),
			String.valueOf(additionalInfoJSONObject.getJSONArray("attributes")),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private OAuthClientEntry _addOAuthClientEntry() throws Exception {
		return _oAuthClientEntryLocalService.addOAuthClientEntry(
			null, TestPropsValues.getUserId(), null,
			_openIdConnectProviderHttpServer.getURL(), null,
			_getInfoJSON(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()),
			null, OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT, null);
	}

	private void _assertAuditMessage(
			AuditMessage auditMessage, OAuthClientEntry oAuthClientEntry)
		throws Exception {

		Assert.assertEquals(
			oAuthClientEntry.getCompanyId(), auditMessage.getCompanyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"authServerWellKnownURI",
				oAuthClientEntry.getAuthServerWellKnownURI()
			).put(
				"clientId", oAuthClientEntry.getClientId()
			).put(
				"externalReferenceCode",
				oAuthClientEntry.getExternalReferenceCode()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private AuditMessage _fetchAuditMessage(String eventType) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(
					auditMessage.getClassName(),
					OAuthClientEntry.class.getName()) &&
				Objects.equals(auditMessage.getEventType(), eventType)) {

				return auditMessage;
			}
		}

		return null;
	}

	private String _getInfoJSON(String clientId, String clientSecret) {
		return JSONUtil.put(
			"client_id", clientId
		).put(
			"client_secret", clientSecret
		).toString();
	}

	private String _getRequestParametersJSON() {
		return JSONUtil.put(
			"custom_request_parameters",
			JSONUtil.put(
				RandomTestUtil.randomString(),
				JSONUtil.put(RandomTestUtil.randomString()))
		).toString();
	}

	private OAuthClientEntry _updateOAuthClientEntry(
			String authRequestParametersJSON, String infoJSON,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws Exception {

		return _oAuthClientEntryLocalService.updateOAuthClientEntry(
			_oAuthClientEntry.getOAuthClientEntryId(),
			authRequestParametersJSON,
			_oAuthClientEntry.getAuthServerWellKnownURI(),
			_oAuthClientEntry.getCustomClaimsJSON(), infoJSON,
			_oAuthClientEntry.getMatcherField(),
			_oAuthClientEntry.getMetadataCacheTime(), oidcUserInfoMapperJSON,
			_oAuthClientEntry.getTokenConnectionTimeout(),
			tokenRequestParametersJSON);
	}

	private final List<AuditMessage> _auditMessages =
		new CopyOnWriteArrayList<>();

	@DeleteAfterTestRun
	private Company _company;

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@DeleteAfterTestRun
	private OAuthClientEntry _oAuthClientEntry;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	private OpenIdConnectProviderHttpServer _openIdConnectProviderHttpServer;
	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}