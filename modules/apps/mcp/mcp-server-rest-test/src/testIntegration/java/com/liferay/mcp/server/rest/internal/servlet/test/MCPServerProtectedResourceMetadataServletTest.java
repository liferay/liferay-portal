/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christopher Kian
 */
@FeatureFlag("LPD-63415")
@RunWith(Arquillian.class)
public class MCPServerProtectedResourceMetadataServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testService() throws Exception {
		HttpResponse<String> httpResponse = _send("GET");

		_assertHeader(
			StringPool.STAR, "Access-Control-Allow-Origin",
			httpResponse.headers());
		_assertHeader(
			"public, max-age=300", "Cache-Control", httpResponse.headers());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, httpResponse.statusCode());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			httpResponse.body());

		Assert.assertEquals(
			"Liferay MCP Server", jsonObject.getString("resource_name"));

		JSONArray bearerMethodsSupportedJSONArray = jsonObject.getJSONArray(
			"bearer_methods_supported");

		Assert.assertEquals(
			"header", bearerMethodsSupportedJSONArray.getString(0));

		String portalURL =
			"http://localhost:" + PortalUtil.getPortalServerPort(false);

		Assert.assertEquals(
			portalURL + "/o/mcp", jsonObject.getString("resource"));

		JSONArray authorizationServersJSONArray = jsonObject.getJSONArray(
			"authorization_servers");

		Assert.assertEquals(
			portalURL, authorizationServersJSONArray.getString(0));

		httpResponse = _send("HEAD");

		_assertHeader(
			"public, max-age=300", "Cache-Control", httpResponse.headers());

		Assert.assertEquals(
			HttpServletResponse.SC_OK, httpResponse.statusCode());
		Assert.assertEquals(StringPool.BLANK, httpResponse.body());

		httpResponse = _send("OPTIONS");

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

		httpResponse = _send("POST");

		_assertHeader("GET, HEAD, OPTIONS", "Allow", httpResponse.headers());
		Assert.assertEquals(
			HttpServletResponse.SC_METHOD_NOT_ALLOWED,
			httpResponse.statusCode());
	}

	private void _assertHeader(
		String expectedHeaderValue, String headerName,
		HttpHeaders httpHeaders) {

		Map<String, List<String>> map = httpHeaders.map();

		List<String> headerValues = map.get(headerName);

		Assert.assertEquals(expectedHeaderValue, headerValues.get(0));
	}

	private HttpResponse<String> _send(String method) throws Exception {
		return _httpClient.send(
			HttpRequest.newBuilder(
			).uri(
				URI.create(
					"http://localhost:" +
						PortalUtil.getPortalServerPort(false) +
							"/o/.well-known/oauth-protected-resource/mcp")
			).method(
				method, HttpRequest.BodyPublishers.noBody()
			).build(),
			HttpResponse.BodyHandlers.ofString());
	}

	private final HttpClient _httpClient = HttpClient.newBuilder(
	).followRedirects(
		HttpClient.Redirect.NEVER
	).build();

}