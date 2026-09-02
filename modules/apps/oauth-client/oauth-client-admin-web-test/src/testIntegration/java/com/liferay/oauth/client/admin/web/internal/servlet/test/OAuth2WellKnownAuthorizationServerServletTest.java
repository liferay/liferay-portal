/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Saugar
 */
@FeatureFlag("LPD-63415")
@RunWith(Arquillian.class)
public class OAuth2WellKnownAuthorizationServerServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				_oAuthClientASLocalMetadataLocalService.
					getCompanyOAuthClientASLocalMetadata(
						TestPropsValues.getCompanyId())) {

			_oAuthClientASLocalMetadataLocalService.
				deleteOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId());
		}
	}

	@Test
	public void testDoGet() throws Exception {
		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String urlString = StringBundler.concat(
			Http.HTTP_WITH_SLASH, company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false),
			"/o/.well-known/oauth-authorization-server");

		options.setLocation(urlString);

		HttpUtil.URLtoString(options);

		List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas =
			_oAuthClientASLocalMetadataLocalService.
				getCompanyOAuthClientASLocalMetadata(
					TestPropsValues.getCompanyId());

		Assert.assertTrue(oAuthClientASLocalMetadatas.isEmpty());

		Http.Response response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());

		String issuer1 = RandomTestUtil.randomString() + ".com";

		String url1 = Http.HTTPS_WITH_SLASH + issuer1;

		String supported1 = RandomTestUtil.randomString();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata1 =
			_oAuthClientASLocalMetadataLocalService.
				addOAuthClientASLocalMetadata(
					null, TestPropsValues.getUserId(), url1, url1, url1, false,
					url1, new String[] {supported1}, new String[] {supported1},
					new String[] {"public"}, url1, url1);

		oAuthClientASLocalMetadatas =
			_oAuthClientASLocalMetadataLocalService.
				getCompanyOAuthClientASLocalMetadata(
					TestPropsValues.getCompanyId());

		Assert.assertFalse(oAuthClientASLocalMetadatas.isEmpty());

		options.setFollowRedirects(false);
		options.setLocation(urlString);

		HttpUtil.URLtoString(options);

		response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());

		oAuthClientASLocalMetadata1 =
			_oAuthClientASLocalMetadataLocalService.
				updateOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata1.
						getOAuthClientASLocalMetadataId(),
					url1, url1, url1, true, url1, new String[] {supported1},
					new String[] {supported1}, new String[] {"public"}, url1,
					url1);

		options.setFollowRedirects(false);
		options.setLocation(urlString);

		response = options.getResponse();
		String responseJSON = HttpUtil.URLtoString(options);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());
		Assert.assertEquals(
			oAuthClientASLocalMetadata1.getOAuthASMetadataJSON(), responseJSON);

		String issuer2 = RandomTestUtil.randomString() + ".com";

		String url2 = Http.HTTPS_WITH_SLASH + issuer2;

		String supported2 = RandomTestUtil.randomString();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata2 =
			_oAuthClientASLocalMetadataLocalService.
				addOAuthClientASLocalMetadata(
					null, TestPropsValues.getUserId(), url2, url2, url2, true,
					url2, new String[] {supported2}, new String[] {supported2},
					new String[] {"public"}, url2, url2);

		response = options.getResponse();
		responseJSON = HttpUtil.URLtoString(options);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());
		Assert.assertEquals(
			oAuthClientASLocalMetadata1.getOAuthASMetadataJSON(), responseJSON);
		Assert.assertNotEquals(
			oAuthClientASLocalMetadata2.getOAuthASMetadataJSON(), responseJSON);

		_oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadata1.getOAuthClientASLocalMetadataId(),
				url1, url1, url1, false, url1, new String[] {supported1},
				new String[] {supported1}, new String[] {"public"}, url1, url1);

		response = options.getResponse();
		responseJSON = HttpUtil.URLtoString(options);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());
		Assert.assertEquals(
			oAuthClientASLocalMetadata2.getOAuthASMetadataJSON(), responseJSON);
		Assert.assertNotEquals(
			oAuthClientASLocalMetadata1.getOAuthASMetadataJSON(), responseJSON);

		options.setFollowRedirects(false);
		options.setLocation(
			StringBundler.concat(
				urlString, StringPool.SLASH, RandomTestUtil.randomString()));

		HttpUtil.URLtoString(options);

		response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());

		options.setLocation(
			StringBundler.concat(urlString, StringPool.SLASH, issuer1));

		HttpUtil.URLtoString(options);

		response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());

		String issuerSegment = URLEncoder.encode(
			issuer2.trim(), StandardCharsets.UTF_8);

		options.setLocation(
			StringBundler.concat(urlString, StringPool.SLASH, issuerSegment));

		response = options.getResponse();
		responseJSON = HttpUtil.URLtoString(options);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());
		Assert.assertEquals(
			oAuthClientASLocalMetadata2.getOAuthASMetadataJSON(), responseJSON);

		String tokenEndpoint2 = url2 + "/o/oauth2/token";

		_oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadata2.getOAuthClientASLocalMetadataId(),
				url2, url2, url2, true, url2, new String[] {supported2},
				new String[] {supported2}, new String[] {"public"},
				tokenEndpoint2, url2);

		options.setLocation(urlString);

		response = options.getResponse();
		responseJSON = HttpUtil.URLtoString(options);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			responseJSON);

		JSONArray codeChallengeMethodsJSONArray =
			responseJSONObject.getJSONArray("code_challenge_methods_supported");

		Assert.assertEquals(1, codeChallengeMethodsJSONArray.length());
		Assert.assertEquals("S256", codeChallengeMethodsJSONArray.getString(0));

		Assert.assertEquals(
			url2 + "/o/oauth2/introspect",
			responseJSONObject.getString("introspection_endpoint"));

		JSONArray responseTypesJSONArray = responseJSONObject.getJSONArray(
			"response_types_supported");

		Assert.assertEquals(1, responseTypesJSONArray.length());
		Assert.assertEquals("code", responseTypesJSONArray.getString(0));

		JSONArray tokenEndpointAuthMethodsJSONArray =
			responseJSONObject.getJSONArray(
				"token_endpoint_auth_methods_supported");

		Assert.assertEquals(3, tokenEndpointAuthMethodsJSONArray.length());

		List<String> tokenEndpointAuthMethods = JSONUtil.toStringList(
			tokenEndpointAuthMethodsJSONArray);

		Assert.assertTrue(
			tokenEndpointAuthMethods.contains("client_secret_basic"));
		Assert.assertTrue(
			tokenEndpointAuthMethods.contains("client_secret_post"));
		Assert.assertTrue(tokenEndpointAuthMethods.contains("none"));
	}

	@FeatureFlag(enable = false, value = "LPD-63415")
	@Test
	public void testDoGetWhenFeatureFlagIsDisabled() throws Exception {
		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String urlString = StringBundler.concat(
			Http.HTTP_WITH_SLASH, company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false),
			"/o/.well-known/oauth-authorization-server");

		options.setLocation(urlString);

		HttpUtil.URLtoString(options);

		Http.Response response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());

		String host = RandomTestUtil.randomString() + ".com";

		String issuer = Http.HTTPS_WITH_SLASH + host;

		String supported = RandomTestUtil.randomString();

		_oAuthClientASLocalMetadataLocalService.addOAuthClientASLocalMetadata(
			null, TestPropsValues.getUserId(), issuer, issuer, issuer, true,
			issuer, new String[] {supported}, new String[] {supported},
			new String[] {"public"}, issuer, issuer);

		options.setLocation(urlString);

		HttpUtil.URLtoString(options);

		response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());

		options.setLocation(
			StringBundler.concat(
				urlString, StringPool.SLASH,
				URLEncoder.encode(host, StandardCharsets.UTF_8)));

		HttpUtil.URLtoString(options);

		response = options.getResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

}