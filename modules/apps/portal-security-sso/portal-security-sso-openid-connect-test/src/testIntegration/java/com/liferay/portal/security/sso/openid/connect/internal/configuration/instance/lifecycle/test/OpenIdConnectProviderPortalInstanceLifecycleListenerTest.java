/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.configuration.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.URI;

import java.security.MessageDigest;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class OpenIdConnectProviderPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (Validator.isNotNull(_pid1)) {
			ConfigurationTestUtil.deleteConfiguration(_pid1);
		}

		if (Validator.isNotNull(_pid2)) {
			ConfigurationTestUtil.deleteConfiguration(_pid2);
		}
	}

	@Test
	public void testGetTokenConnectionTimeout() throws Exception {
		String clientId1 = RandomTestUtil.randomString();
		String issuerURL1 = "http://" + RandomTestUtil.randomString();
		int tokenConnectionTimeout = RandomTestUtil.randomInt();
		String tokenEndpoint1 = "http://" + RandomTestUtil.randomString();

		_pid1 = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.security.sso.openid.connect.internal." +
				"configuration.OpenIdConnectProviderConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"authorizationEndpoint",
				"http://" + RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"issuerURL", issuerURL1
			).put(
				"jwksURI", "http://" + RandomTestUtil.randomString()
			).put(
				"openIdConnectClientId", clientId1
			).put(
				"subjectTypes", new String[] {"public"}
			).put(
				"tokenConnectionTimeout", tokenConnectionTimeout
			).put(
				"tokenEndpoint", tokenEndpoint1
			).put(
				"userInfoEndpoint", "http://" + RandomTestUtil.randomString()
			).build());

		OAuthClientEntry oAuthClientEntry1 =
			_oAuthClientEntryLocalService.fetchOAuthClientEntry(
				TestPropsValues.getCompanyId(),
				_generateLocalWellKnownURI(issuerURL1, tokenEndpoint1),
				clientId1);

		Assert.assertEquals(
			tokenConnectionTimeout,
			oAuthClientEntry1.getTokenConnectionTimeout());

		String clientId2 = RandomTestUtil.randomString();
		String issuerURL2 = "http://" + RandomTestUtil.randomString();
		String tokenEndpoint2 = "http://" + RandomTestUtil.randomString();

		_pid2 = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.security.sso.openid.connect.internal." +
				"configuration.OpenIdConnectProviderConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"authorizationEndpoint",
				"http://" + RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"issuerURL", issuerURL2
			).put(
				"jwksURI", "http://" + RandomTestUtil.randomString()
			).put(
				"openIdConnectClientId", clientId2
			).put(
				"subjectTypes", new String[] {"public"}
			).put(
				"tokenEndpoint", tokenEndpoint2
			).put(
				"userInfoEndpoint", "http://" + RandomTestUtil.randomString()
			).build());

		OAuthClientEntry oAuthClientEntry2 =
			_oAuthClientEntryLocalService.fetchOAuthClientEntry(
				TestPropsValues.getCompanyId(),
				_generateLocalWellKnownURI(issuerURL2, tokenEndpoint2),
				clientId2);

		Assert.assertEquals(
			OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
			oAuthClientEntry2.getTokenConnectionTimeout());
	}

	@Test
	public void testGetTokenRequestParametersJSON() throws Exception {
		String clientId1 = RandomTestUtil.randomString();
		String issuerURL1 = "http://" + RandomTestUtil.randomString();
		String tokenEndpoint1 = "http://" + RandomTestUtil.randomString();

		_pid1 = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.security.sso.openid.connect.internal." +
				"configuration.OpenIdConnectProviderConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"allowUpstreamTokenForwarding", true
			).put(
				"authorizationEndpoint",
				"http://" + RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"issuerURL", issuerURL1
			).put(
				"jwksURI", "http://" + RandomTestUtil.randomString()
			).put(
				"openIdConnectClientId", clientId1
			).put(
				"subjectTypes", new String[] {"public"}
			).put(
				"tokenEndpoint", tokenEndpoint1
			).put(
				"userInfoEndpoint", "http://" + RandomTestUtil.randomString()
			).build());

		OAuthClientEntry oAuthClientEntry1 =
			_oAuthClientEntryLocalService.fetchOAuthClientEntry(
				TestPropsValues.getCompanyId(),
				_generateLocalWellKnownURI(issuerURL1, tokenEndpoint1),
				clientId1);

		JSONObject tokenRequestParametersJSONObject1 =
			JSONFactoryUtil.createJSONObject(
				oAuthClientEntry1.getTokenRequestParametersJSON());

		Assert.assertEquals(
			Boolean.TRUE,
			tokenRequestParametersJSONObject1.get(
				"allow_upstream_token_forwarding"));

		String clientId2 = RandomTestUtil.randomString();
		String issuerURL2 = "http://" + RandomTestUtil.randomString();
		String tokenEndpoint2 = "http://" + RandomTestUtil.randomString();

		_pid2 = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.security.sso.openid.connect.internal." +
				"configuration.OpenIdConnectProviderConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"authorizationEndpoint",
				"http://" + RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"issuerURL", issuerURL2
			).put(
				"jwksURI", "http://" + RandomTestUtil.randomString()
			).put(
				"openIdConnectClientId", clientId2
			).put(
				"subjectTypes", new String[] {"public"}
			).put(
				"tokenEndpoint", tokenEndpoint2
			).put(
				"userInfoEndpoint", "http://" + RandomTestUtil.randomString()
			).build());

		OAuthClientEntry oAuthClientEntry2 =
			_oAuthClientEntryLocalService.fetchOAuthClientEntry(
				TestPropsValues.getCompanyId(),
				_generateLocalWellKnownURI(issuerURL2, tokenEndpoint2),
				clientId2);

		JSONObject tokenRequestParametersJSONObject2 =
			JSONFactoryUtil.createJSONObject(
				oAuthClientEntry2.getTokenRequestParametersJSON());

		Assert.assertFalse(
			tokenRequestParametersJSONObject2.has(
				"allow_upstream_token_forwarding"));
	}

	private String _generateLocalWellKnownURI(
			String issuerURL, String tokenEndpoint)
		throws Exception {

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		URI issuerURI = URI.create(issuerURL);

		return StringBundler.concat(
			issuerURI.getScheme(), "://", issuerURI.getAuthority(),
			"/.well-known/openid-configuration", issuerURI.getPath(), '/',
			Base64.encodeToURL(messageDigest.digest(tokenEndpoint.getBytes())),
			"/local");
	}

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	private String _pid1;
	private String _pid2;

}