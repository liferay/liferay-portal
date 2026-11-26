/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (Validator.isNotNull(_pid)) {
			ConfigurationTestUtil.deleteConfiguration(_pid);
		}
	}

	@Test
	public void testPortalInstanceRegistered() throws Exception {
		_testPortalInstanceRegistered();
		_testPortalInstanceRegisteredWithLongIssuerURL();
	}

	private void _disableComponent(
			ComponentDescriptionDTO componentDescriptionDTO)
		throws Exception {

		if (!_serviceComponentRuntime.isComponentEnabled(
				componentDescriptionDTO)) {

			return;
		}

		Promise<Void> promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private void _enableComponent(
			ComponentDescriptionDTO componentDescriptionDTO)
		throws Exception {

		if (_serviceComponentRuntime.isComponentEnabled(
				componentDescriptionDTO)) {

			return;
		}

		Promise<Void> promise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private void _testPortalInstanceRegistered() throws Exception {
		String clientId = RandomTestUtil.randomString();
		String issuerURL = "http://" + RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"authorizationEndpoint",
				"http://" + RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"issuerURL", issuerURL
			).put(
				"jwksURI", "http://" + RandomTestUtil.randomString()
			).put(
				"openIdConnectClientId", clientId
			).put(
				"subjectTypes", new String[] {"public"}
			).put(
				"tokenEndpoint", "http://" + RandomTestUtil.randomString()
			).put(
				"userInfoEndpoint", "http://" + RandomTestUtil.randomString()
			).build();

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.security.sso.openid.connect.internal." +
				"configuration.OpenIdConnectProviderConfiguration",
			properties);

		OpenIdConnectSession openIdConnectSession =
			_openIdConnectSessionLocalService.createOpenIdConnectSession(
				_counterLocalService.increment());

		openIdConnectSession.setCompanyId(TestPropsValues.getCompanyId());
		openIdConnectSession.setUserId(TestPropsValues.getUserId());

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				fetchOAuthClientASLocalMetadata(
					TestPropsValues.getCompanyId(), issuerURL);

		String localWellKnownURI =
			oAuthClientASLocalMetadata.getLocalWellKnownURI();

		openIdConnectSession.setAuthServerWellKnownURI(localWellKnownURI);

		openIdConnectSession.setClientId(clientId);
		openIdConnectSession.setIssuer(issuerURL);

		openIdConnectSession =
			_openIdConnectSessionLocalService.updateOpenIdConnectSession(
				openIdConnectSession);

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				BundleUtil.getBundle(
					SystemBundleUtil.getBundleContext(),
					"com.liferay.portal.security.sso.openid.connect.impl"),
				_CLASS_NAME);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			OAuthClientEntry oAuthClientEntry =
				_oAuthClientEntryLocalService.getOAuthClientEntry(
					TestPropsValues.getCompanyId(), localWellKnownURI,
					clientId);

			_enableComponent(componentDescriptionDTO);

			oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					fetchOAuthClientASLocalMetadata(
						TestPropsValues.getCompanyId(), issuerURL);

			String newLocalWellKnownURI =
				oAuthClientASLocalMetadata.getLocalWellKnownURI();

			Assert.assertNotEquals(localWellKnownURI, newLocalWellKnownURI);

			OAuthClientEntry newOAuthClientEntry =
				_oAuthClientEntryLocalService.getOAuthClientEntry(
					TestPropsValues.getCompanyId(), newLocalWellKnownURI,
					clientId);

			Assert.assertEquals(
				oAuthClientEntry.getOAuthClientEntryId(),
				newOAuthClientEntry.getOAuthClientEntryId());

			openIdConnectSession =
				_openIdConnectSessionLocalService.getOpenIdConnectSession(
					openIdConnectSession.getOpenIdConnectSessionId());

			Assert.assertEquals(
				newLocalWellKnownURI,
				openIdConnectSession.getAuthServerWellKnownURI());

			Class<?> clazz = _portalInstanceLifecycleListener.getClass();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					clazz.getName(), LoggerTestUtil.WARN)) {

				_portalInstanceLifecycleListener.portalInstanceRegistered(
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()));

				List<String> messages = logCapture.getMessages();

				Assert.assertEquals(messages.toString(), 0, messages.size());
			}

			_disableComponent(componentDescriptionDTO);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME, LoggerTestUtil.WARN)) {

				_enableComponent(componentDescriptionDTO);

				List<String> messages = logCapture.getMessages();

				Assert.assertEquals(messages.toString(), 0, messages.size());
			}

			oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					fetchOAuthClientASLocalMetadata(
						TestPropsValues.getCompanyId(), issuerURL);

			Assert.assertEquals(
				newLocalWellKnownURI,
				oAuthClientASLocalMetadata.getLocalWellKnownURI());

			int tokenConnectionTimeout = RandomTestUtil.randomInt();

			properties.put("tokenConnectionTimeout", tokenConnectionTimeout);

			ConfigurationTestUtil.saveConfiguration(_pid, properties);

			newOAuthClientEntry =
				_oAuthClientEntryLocalService.getOAuthClientEntry(
					TestPropsValues.getCompanyId(), newLocalWellKnownURI,
					clientId);

			Assert.assertEquals(
				tokenConnectionTimeout,
				newOAuthClientEntry.getTokenConnectionTimeout());

			String orphanedLocalWellKnownURI = StringBundler.concat(
				"http://", RandomTestUtil.randomString(),
				"/.well-known/openid-configuration/",
				RandomTestUtil.randomString(), "/local");

			newOAuthClientEntry.setAuthServerWellKnownURI(
				orphanedLocalWellKnownURI);

			newOAuthClientEntry =
				_oAuthClientEntryLocalService.updateOAuthClientEntry(
					newOAuthClientEntry);

			_disableComponent(componentDescriptionDTO);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME, LoggerTestUtil.WARN)) {

				_enableComponent(componentDescriptionDTO);

				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"OAuth client entry ",
							newOAuthClientEntry.getOAuthClientEntryId(),
							" points to the local well known URI ",
							orphanedLocalWellKnownURI,
							", which has no OAuth 2 client authorization ",
							"server local metadata")));
			}

			newOAuthClientEntry.setAuthServerWellKnownURI(newLocalWellKnownURI);

			_oAuthClientEntryLocalService.updateOAuthClientEntry(
				newOAuthClientEntry);

			ConfigurationTestUtil.deleteConfiguration(_pid);

			_pid = null;

			Assert.assertNull(
				_oAuthClientASLocalMetadataLocalService.
					fetchOAuthClientASLocalMetadata(
						TestPropsValues.getCompanyId(), issuerURL));
			Assert.assertNull(
				_oAuthClientEntryLocalService.fetchOAuthClientEntry(
					TestPropsValues.getCompanyId(), newLocalWellKnownURI,
					clientId));
		}
		finally {
			_disableComponent(componentDescriptionDTO);

			_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
				openIdConnectSession);
		}
	}

	private void _testPortalInstanceRegisteredWithLongIssuerURL()
		throws Exception {

		String issuerURL = "http://" + RandomTestUtil.randomString(180);

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.security.sso.openid.connect.internal." +
				"configuration.OpenIdConnectProviderConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"authorizationEndpoint",
				"http://" + RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"issuerURL", issuerURL
			).put(
				"jwksURI", "http://" + RandomTestUtil.randomString()
			).put(
				"openIdConnectClientId", RandomTestUtil.randomString()
			).put(
				"subjectTypes", new String[] {"public"}
			).put(
				"tokenEndpoint", "http://" + RandomTestUtil.randomString()
			).put(
				"userInfoEndpoint", "http://" + RandomTestUtil.randomString()
			).build());

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				fetchOAuthClientASLocalMetadata(
					TestPropsValues.getCompanyId(), issuerURL);

		String localWellKnownURI =
			oAuthClientASLocalMetadata.getLocalWellKnownURI();

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				BundleUtil.getBundle(
					SystemBundleUtil.getBundleContext(),
					"com.liferay.portal.security.sso.openid.connect.impl"),
				_CLASS_NAME);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable("FIPS_ENABLED", true);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			_enableComponent(componentDescriptionDTO);

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Unable to update the local well known URI of OAuth 2 ",
						"client authorization server local metadata ",
						oAuthClientASLocalMetadata.
							getOAuthClientASLocalMetadataId(),
						" because it would exceed 255 characters")));
		}
		finally {
			_disableComponent(componentDescriptionDTO);
		}

		oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId());

		Assert.assertEquals(
			localWellKnownURI,
			oAuthClientASLocalMetadata.getLocalWellKnownURI());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		_pid = null;
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.security.sso.openid.connect.internal.instance." +
			"lifecycle." +
				"OAuthClientASLocalMetadataPortalInstanceLifecycleListener";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Inject
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

	private String _pid;

	@Inject(
		filter = "component.name=com.liferay.portal.security.sso.openid.connect.internal.configuration.instance.lifecycle.OpenIdConnectProviderPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}