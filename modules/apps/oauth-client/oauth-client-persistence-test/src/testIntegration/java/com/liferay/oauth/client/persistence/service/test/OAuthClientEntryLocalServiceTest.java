/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientEntryException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthRequestParametersJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthServerWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryOIDCUserInfoMapperJSONException;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() {
		_authRequestParametersJSON = JSONUtil.put(
			"response_type", "code"
		).put(
			"scope", "openid email profile"
		).toString();

		_infoJSON = JSONUtil.put(
			"client_id", RandomTestUtil.randomString()
		).put(
			"client_name", "Client to Google"
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

		_tokenRequestParametersJSON = JSONUtil.put(
			"grant_type", "authorization_code"
		).put(
			"scope", "openid email profile"
		).toString();
	}

	@Test
	public void testAddOAuthClientEntry() throws Exception {
		OAuthClientEntry oAuthClientEntry =
			_oAuthClientEntryLocalService.addOAuthClientEntry(
				TestPropsValues.getUserId(), _authRequestParametersJSON,
				_AUTH_SERVER_WELL_KNOWN_URI, _CUSTOM_CLAIMS_JSON, _infoJSON,
				OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
				OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
				_tokenRequestParametersJSON);

		Assert.assertEquals(
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			oAuthClientEntry.getMetadataCacheTime());
		Assert.assertEquals(
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			oAuthClientEntry.getOIDCUserInfoMapperJSON());
		Assert.assertEquals(
			_authRequestParametersJSON,
			oAuthClientEntry.getAuthRequestParametersJSON());
		Assert.assertEquals(
			_CUSTOM_CLAIMS_JSON, oAuthClientEntry.getCustomClaimsJSON());
		Assert.assertEquals(
			_tokenRequestParametersJSON,
			oAuthClientEntry.getTokenRequestParametersJSON());
		Assert.assertEquals(
			oAuthClientEntry.getUserId(), TestPropsValues.getUserId());
	}

	@Test(expected = DuplicateOAuthClientEntryException.class)
	public void testAddOAuthClientEntryDuplicatedEntry() throws Exception {
		_oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(), _authRequestParametersJSON,
			_AUTH_SERVER_WELL_KNOWN_URI, _CUSTOM_CLAIMS_JSON, _infoJSON,
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			_tokenRequestParametersJSON);
		_oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(), _authRequestParametersJSON,
			_AUTH_SERVER_WELL_KNOWN_URI, _CUSTOM_CLAIMS_JSON, _infoJSON,
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			_tokenRequestParametersJSON);
	}

	@Test(expected = OAuthClientEntryAuthRequestParametersJSONException.class)
	public void testAddOAuthClientEntryWithInvalidAuthRequestParameters()
		throws PortalException {

		_oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(),
			JSONUtil.put(
				"response_type", ""
			).put(
				"scope", "openid email profile"
			).toString(),
			_AUTH_SERVER_WELL_KNOWN_URI, _CUSTOM_CLAIMS_JSON, _infoJSON,
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			_tokenRequestParametersJSON);
	}

	@Test(expected = OAuthClientEntryOIDCUserInfoMapperJSONException.class)
	public void testAddOAuthClientEntryWithInvalidOidcUserInfoMapper()
		throws PortalException {

		_oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(), _authRequestParametersJSON,
			_AUTH_SERVER_WELL_KNOWN_URI, _CUSTOM_CLAIMS_JSON, _infoJSON,
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			JSONUtil.put(
				"user", JSONUtil.put("emailAddress", "")
			).toString(),
			_tokenRequestParametersJSON);
	}

	@Test(expected = OAuthClientEntryAuthServerWellKnownURIException.class)
	public void testAddOAuthClientEntryWithUnreachableWellKnownURI()
		throws PortalException {

		_oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(), _authRequestParametersJSON,
			"http://172.17.0.3:18080/auth/realms/master/." +
				"well-known/openid-configuration",
			_CUSTOM_CLAIMS_JSON, _infoJSON,
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			_tokenRequestParametersJSON);
	}

	private static final String _AUTH_SERVER_WELL_KNOWN_URI =
		"https://accounts.google.com/.well-known/openid-configuration";

	private static final String _CUSTOM_CLAIMS_JSON = "{}";

	private static String _authRequestParametersJSON;
	private static String _infoJSON;
	private static String _tokenRequestParametersJSON;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

}