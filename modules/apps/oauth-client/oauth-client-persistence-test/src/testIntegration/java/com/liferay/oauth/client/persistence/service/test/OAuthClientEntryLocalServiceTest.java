/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientEntryException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthRequestParametersJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthServerWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryOIDCUserInfoMapperJSONException;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OAuthClientEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddOAuthClientEntry() throws Exception {
		try (OpenIdConnectProviderHttpServer openIdConnectProviderHttpServer =
				new OpenIdConnectProviderHttpServer();
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						OAuthClientCompanyConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"authServerLocalNetworkAccessEnabled", true
						).build())) {

			String authRequestParametersJSON = JSONUtil.put(
				"response_type", "code"
			).put(
				"scope", "openid email profile"
			).toString();

			String authServerWellKnownURI =
				openIdConnectProviderHttpServer.getURL();

			String customClaimsJSON = "{}";

			String clientId = RandomTestUtil.randomString();

			String infoJSON = JSONUtil.put(
				"client_id", clientId
			).put(
				"client_name", RandomTestUtil.randomString()
			).put(
				"client_secret", RandomTestUtil.randomString()
			).put(
				"grant_types",
				JSONUtil.putAll("authorization_code", "refresh_token")
			).put(
				"response_types", JSONUtil.putAll("code")
			).put(
				"scope", "openid email profile"
			).toString();

			int tokenConnectionTimeout = RandomTestUtil.randomInt();

			String tokenRequestParametersJSON = JSONUtil.put(
				"grant_type", "authorization_code"
			).put(
				"scope", "openid email profile"
			).toString();

			OAuthClientEntry oAuthClientEntry =
				_oAuthClientEntryLocalService.addOAuthClientEntry(
					null, TestPropsValues.getUserId(),
					authRequestParametersJSON, authServerWellKnownURI,
					customClaimsJSON, infoJSON, "email",
					OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
					OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
					tokenConnectionTimeout, tokenRequestParametersJSON);

			Assert.assertEquals(
				TestPropsValues.getUserId(), oAuthClientEntry.getUserId());
			Assert.assertEquals(
				authRequestParametersJSON,
				oAuthClientEntry.getAuthRequestParametersJSON());
			Assert.assertEquals(
				authServerWellKnownURI,
				oAuthClientEntry.getAuthServerWellKnownURI());
			Assert.assertEquals(clientId, oAuthClientEntry.getClientId());
			Assert.assertEquals(
				customClaimsJSON, oAuthClientEntry.getCustomClaimsJSON());
			Assert.assertEquals("email", oAuthClientEntry.getMatcherField());
			Assert.assertEquals(
				OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
				oAuthClientEntry.getMetadataCacheTime());
			Assert.assertEquals(
				OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
				oAuthClientEntry.getOIDCUserInfoMapperJSON());
			Assert.assertEquals(
				tokenConnectionTimeout,
				oAuthClientEntry.getTokenConnectionTimeout());
			Assert.assertEquals(
				tokenRequestParametersJSON,
				oAuthClientEntry.getTokenRequestParametersJSON());

			AssertUtils.assertFailure(
				DuplicateOAuthClientEntryException.class,
				"Client ID " + clientId,
				() -> _oAuthClientEntryLocalService.addOAuthClientEntry(
					null, TestPropsValues.getUserId(),
					authRequestParametersJSON, authServerWellKnownURI,
					customClaimsJSON, infoJSON, "email",
					OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
					OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
					OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
					tokenRequestParametersJSON));

			_oAuthClientEntryLocalService.deleteOAuthClientEntry(
				oAuthClientEntry.getOAuthClientEntryId());

			AssertUtils.assertFailure(
				OAuthClientEntryAuthRequestParametersJSONException.class,
				"Null or empty response type string",
				() -> _oAuthClientEntryLocalService.addOAuthClientEntry(
					null, TestPropsValues.getUserId(),
					JSONUtil.put(
						"response_type", ""
					).put(
						"scope", "openid email profile"
					).toString(),
					authServerWellKnownURI, customClaimsJSON, infoJSON, "email",
					OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
					OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
					OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
					tokenRequestParametersJSON));
			Assert.assertThrows(
				OAuthClientEntryAuthServerWellKnownURIException.class,
				() -> _oAuthClientEntryLocalService.addOAuthClientEntry(
					null, TestPropsValues.getUserId(),
					authRequestParametersJSON,
					Http.HTTP_WITH_SLASH + RandomTestUtil.randomString() +
						"/.well-known/openid-configuration",
					customClaimsJSON, infoJSON, "email",
					OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
					OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
					OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
					tokenRequestParametersJSON));
			AssertUtils.assertFailure(
				OAuthClientEntryOIDCUserInfoMapperJSONException.class,
				"emailAddress is required for user",
				() -> _oAuthClientEntryLocalService.addOAuthClientEntry(
					null, TestPropsValues.getUserId(),
					authRequestParametersJSON, authServerWellKnownURI,
					customClaimsJSON, infoJSON, "email",
					OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
					JSONUtil.put(
						"user", JSONUtil.put("emailAddress", "")
					).toString(),
					OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
					tokenRequestParametersJSON));
		}
	}

	@Test(expected = OAuthClientEntryAuthServerWellKnownURIException.class)
	public void testAddOAuthClientEntryWithLocalNetworkAccessDisabled()
		throws Exception {

		try (OpenIdConnectProviderHttpServer openIdConnectProviderHttpServer =
				new OpenIdConnectProviderHttpServer()) {

			_addOAuthClientEntry(openIdConnectProviderHttpServer.getURL());
		}
	}

	private OAuthClientEntry _addOAuthClientEntry(String authServerWellKnownURI)
		throws Exception {

		return _oAuthClientEntryLocalService.addOAuthClientEntry(
			null, TestPropsValues.getUserId(),
			JSONUtil.put(
				"response_type", "code"
			).put(
				"scope", "openid email profile"
			).toString(),
			authServerWellKnownURI, "{}",
			JSONUtil.put(
				"client_id", RandomTestUtil.randomString()
			).put(
				"client_name", RandomTestUtil.randomString()
			).put(
				"client_secret", RandomTestUtil.randomString()
			).put(
				"grant_types",
				JSONUtil.putAll("authorization_code", "refresh_token")
			).put(
				"response_types", JSONUtil.putAll("code")
			).put(
				"scope", "openid email profile"
			).toString(),
			"email", OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
			JSONUtil.put(
				"grant_type", "authorization_code"
			).put(
				"scope", "openid email profile"
			).toString());
	}

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

}