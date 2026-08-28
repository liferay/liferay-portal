/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Saugar
 * @author Jorge García Jiménez
 */
@FeatureFlag("LPD-63415")
@RunWith(Arquillian.class)
public class OAuth2WellKnownAuthorizationServerMetadataFilterTest {

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
	public void testProcessFilter() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String urlString = StringBundler.concat(
			Http.HTTP_WITH_SLASH, company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false), _WELL_KNOWN_PATH);

		HttpResponse<String> httpResponse = _send(urlString, "GET");

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, httpResponse.statusCode());

		String issuer =
			Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString() + ".com";
		String supported = RandomTestUtil.randomString();

		String tokenEndpoint = issuer + "/o/oauth2/token";

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				addOAuthClientASLocalMetadata(
					null, TestPropsValues.getUserId(), issuer, issuer, issuer,
					false, issuer, new String[] {supported},
					new String[] {supported}, new String[] {"public"},
					tokenEndpoint, issuer);

		httpResponse = _send(urlString, "GET");

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, httpResponse.statusCode());

		_oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId(),
				issuer, issuer, issuer, true, issuer, new String[] {supported},
				new String[] {supported}, new String[] {"public"},
				tokenEndpoint, issuer);

		httpResponse = _send(urlString, "GET");

		_assertHeader(
			StringPool.STAR, "Access-Control-Allow-Origin",
			httpResponse.headers());
		_assertHeader(
			"public, max-age=300", "Cache-Control", httpResponse.headers());

		Assert.assertEquals(
			HttpServletResponse.SC_OK, httpResponse.statusCode());
		Assert.assertEquals(
			oAuthClientASLocalMetadata.getOAuthASMetadataJSON(),
			httpResponse.body());

		httpResponse = _send(urlString, "HEAD");

		Assert.assertEquals(
			HttpServletResponse.SC_OK, httpResponse.statusCode());
		Assert.assertEquals(StringPool.BLANK, httpResponse.body());

		httpResponse = _send(urlString, "OPTIONS");

		_assertHeader(
			"Authorization, Content-Type, MCP-Protocol-Version",
			"Access-Control-Allow-Headers", httpResponse.headers());
		_assertHeader(
			"GET, HEAD, OPTIONS", "Access-Control-Allow-Methods",
			httpResponse.headers());
		_assertHeader(
			StringPool.STAR, "Access-Control-Allow-Origin",
			httpResponse.headers());
		_assertHeader("300", "Access-Control-Max-Age", httpResponse.headers());

		Assert.assertEquals(
			HttpServletResponse.SC_NO_CONTENT, httpResponse.statusCode());

		httpResponse = _send(urlString, "POST");

		_assertHeader("GET, HEAD, OPTIONS", "Allow", httpResponse.headers());

		Assert.assertEquals(
			HttpServletResponse.SC_METHOD_NOT_ALLOWED,
			httpResponse.statusCode());
	}

	@Test
	public void testProcessFilterIssuerPath() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String portalURL = StringBundler.concat(
			Http.HTTP_WITH_SLASH, company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false));

		String path = StringBundler.concat(
			StringPool.SLASH, RandomTestUtil.randomString(), StringPool.SLASH,
			RandomTestUtil.randomString());

		String oAuthASLocalWellKnownURI = StringBundler.concat(
			portalURL, _WELL_KNOWN_PATH, path);

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_addOAuthClientASLocalMetadata(
				portalURL + path, oAuthASLocalWellKnownURI);

		HttpResponse<String> httpResponse = _send(
			oAuthASLocalWellKnownURI, "GET");

		Assert.assertEquals(
			HttpServletResponse.SC_OK, httpResponse.statusCode());
		Assert.assertEquals(
			oAuthClientASLocalMetadata.getOAuthASMetadataJSON(),
			httpResponse.body());
	}

	private OAuthClientASLocalMetadata _addOAuthClientASLocalMetadata(
			String issuer, String oAuthASLocalWellKnownURI)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				createOAuthClientASLocalMetadata(
					_counterLocalService.increment());

		oAuthClientASLocalMetadata.setCompanyId(TestPropsValues.getCompanyId());
		oAuthClientASLocalMetadata.setIssuer(issuer);
		oAuthClientASLocalMetadata.setLocalWellKnownEnabled(true);
		oAuthClientASLocalMetadata.setOAuthASLocalWellKnownURI(
			oAuthASLocalWellKnownURI);
		oAuthClientASLocalMetadata.setOAuthASMetadataJSON(
			JSONUtil.put(
				"issuer", issuer
			).toString());

		return _oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	private void _assertHeader(
		String expectedHeaderValue, String headerName,
		HttpHeaders httpHeaders) {

		Map<String, List<String>> map = httpHeaders.map();

		List<String> headerValues = map.get(headerName);

		Assert.assertEquals(expectedHeaderValue, headerValues.get(0));
	}

	private HttpResponse<String> _send(String urlString, String method)
		throws Exception {

		return _httpClient.send(
			HttpRequest.newBuilder(
			).uri(
				URI.create(urlString)
			).method(
				method, HttpRequest.BodyPublishers.noBody()
			).build(),
			HttpResponse.BodyHandlers.ofString());
	}

	private static final String _WELL_KNOWN_PATH =
		"/.well-known/oauth-authorization-server";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	private final HttpClient _httpClient = HttpClient.newBuilder(
	).followRedirects(
		HttpClient.Redirect.NEVER
	).build();

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

}