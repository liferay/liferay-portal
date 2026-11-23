/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataModelListenerTest {

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
	public void testOnAfterUpdate() throws Exception {
		String clientId = RandomTestUtil.randomString();
		String issuerURL = "http://" + RandomTestUtil.randomString();

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
				"openIdConnectClientId", clientId
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

		OAuthClientEntry oAuthClientEntry =
			_oAuthClientEntryLocalService.fetchOAuthClientEntry(
				TestPropsValues.getCompanyId(), localWellKnownURI, clientId);

		OpenIdConnectSession openIdConnectSession =
			_openIdConnectSessionLocalService.createOpenIdConnectSession(
				_counterLocalService.increment());

		openIdConnectSession.setCompanyId(TestPropsValues.getCompanyId());
		openIdConnectSession.setUserId(TestPropsValues.getUserId());
		openIdConnectSession.setAuthServerWellKnownURI(localWellKnownURI);
		openIdConnectSession.setClientId(clientId);
		openIdConnectSession.setIssuer(issuerURL);

		openIdConnectSession =
			_openIdConnectSessionLocalService.updateOpenIdConnectSession(
				openIdConnectSession);

		try {
			String metadataJSON = oAuthClientASLocalMetadata.getMetadataJSON();

			JSONObject metadataJSONObject = JSONFactoryUtil.createJSONObject(
				metadataJSON);

			metadataJSONObject.put(
				"token_endpoint", "http://" + RandomTestUtil.randomString());

			oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					updateOAuthClientASLocalMetadata(
						oAuthClientASLocalMetadata.
							getOAuthClientASLocalMetadataId(),
						metadataJSONObject.toString(), "openid-configuration");

			Assert.assertNotEquals(
				localWellKnownURI,
				oAuthClientASLocalMetadata.getLocalWellKnownURI());

			oAuthClientEntry =
				_oAuthClientEntryLocalService.getOAuthClientEntry(
					oAuthClientEntry.getOAuthClientEntryId());

			Assert.assertEquals(
				oAuthClientASLocalMetadata.getLocalWellKnownURI(),
				oAuthClientEntry.getAuthServerWellKnownURI());

			openIdConnectSession =
				_openIdConnectSessionLocalService.getOpenIdConnectSession(
					openIdConnectSession.getOpenIdConnectSessionId());

			Assert.assertEquals(
				oAuthClientASLocalMetadata.getLocalWellKnownURI(),
				openIdConnectSession.getAuthServerWellKnownURI());

			_oAuthClientASLocalMetadataLocalService.
				updateOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId(),
					metadataJSON, "openid-configuration");
		}
		finally {
			_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
				openIdConnectSession);
		}
	}

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

}