/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Jorge García Jiménez
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-63311"), @FeatureFlag("LPD-63415")}
)
@RunWith(Arquillian.class)
public class MCPProtectedResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_updateMCPServerConfiguration(true);

		BundleContext bundleContext = FrameworkUtil.getBundle(
			MCPProtectedResourceTest.class
		).getBundleContext();

		_serviceReference = bundleContext.getServiceReference(
			OAuthClientPRLocalMetadataLocalService.class);

		_oAuthClientPRLocalMetadataLocalService = bundleContext.getService(
			_serviceReference);

		_bundleContext = bundleContext;

		_oAuthClientPRLocalMetadata =
			_oAuthClientPRLocalMetadataLocalService.
				addOAuthClientPRLocalMetadata(
					null, TestPropsValues.getUserId(),
					new String[] {"http://localhost:8080"},
					new String[] {"header"}, true, _MCP_RESOURCE,
					"Liferay MCP Server", new String[0]);
	}

	@After
	public void tearDown() throws Exception {
		if (_oAuthClientPRLocalMetadata != null) {
			_oAuthClientPRLocalMetadataLocalService.
				deleteOAuthClientPRLocalMetadata(
					_oAuthClientPRLocalMetadata.
						getOAuthClientPRLocalMetadataId());
		}

		if (_serviceReference != null) {
			_bundleContext.ungetService(_serviceReference);
		}

		_updateMCPServerConfiguration(false);
	}

	@Test
	public void testMCPRequestWithBearerNotInDatabase() throws Exception {
		HttpResponse<String> httpResponse = HttpClient.newHttpClient(
		).send(
			HttpRequest.newBuilder(
			).header(
				"Authorization", "Bearer not-a-real-token"
			).uri(
				URI.create(_MCP_URL)
			).GET(
			).build(),
			HttpResponse.BodyHandlers.ofString()
		);

		Assert.assertEquals(401, httpResponse.statusCode());

		String wwwAuthenticate = httpResponse.headers(
		).firstValue(
			"WWW-Authenticate"
		).orElse(
			null
		);

		Assert.assertNotNull(wwwAuthenticate);
		Assert.assertTrue(
			wwwAuthenticate, wwwAuthenticate.startsWith("Bearer "));
		Assert.assertTrue(
			wwwAuthenticate, wwwAuthenticate.contains("realm=\"mcp\""));
		Assert.assertTrue(
			wwwAuthenticate,
			wwwAuthenticate.contains(
				"resource_metadata=\"http://localhost:8080/.well-known" +
					"/oauth-protected-resource/o/mcp\""));
		Assert.assertTrue(
			wwwAuthenticate,
			wwwAuthenticate.contains("error=\"invalid_token\""));
	}

	@Test
	public void testMCPRequestWithoutAuthorization() throws Exception {
		HttpResponse<String> httpResponse = HttpClient.newHttpClient(
		).send(
			HttpRequest.newBuilder(
			).uri(
				URI.create(_MCP_URL)
			).GET(
			).build(),
			HttpResponse.BodyHandlers.ofString()
		);

		Assert.assertEquals(401, httpResponse.statusCode());

		String wwwAuthenticate = httpResponse.headers(
		).firstValue(
			"WWW-Authenticate"
		).orElse(
			null
		);

		Assert.assertNotNull(wwwAuthenticate);
		Assert.assertTrue(
			wwwAuthenticate, wwwAuthenticate.startsWith("Bearer "));
		Assert.assertTrue(
			wwwAuthenticate,
			wwwAuthenticate.contains("error=\"invalid_token\""));
	}

	@Test
	public void testProtectedResourceMetadata() throws Exception {
		HttpResponse<String> httpResponse = HttpClient.newHttpClient(
		).send(
			HttpRequest.newBuilder(
			).uri(
				URI.create(
					"http://localhost:8080/.well-known" +
						"/oauth-protected-resource/o/mcp")
			).GET(
			).build(),
			HttpResponse.BodyHandlers.ofString()
		);

		Assert.assertEquals(200, httpResponse.statusCode());

		String contentType = httpResponse.headers(
		).firstValue(
			"Content-Type"
		).orElse(
			""
		);

		Assert.assertTrue(
			contentType, contentType.startsWith("application/json"));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			httpResponse.body());

		Assert.assertEquals(_MCP_RESOURCE, jsonObject.getString("resource"));

		JSONArray authorizationServersJSONArray = jsonObject.getJSONArray(
			"authorization_servers");

		Assert.assertEquals(1, authorizationServersJSONArray.length());
		Assert.assertEquals(
			"http://localhost:8080",
			authorizationServersJSONArray.getString(0));

		JSONArray bearerMethodsSupportedJSONArray = jsonObject.getJSONArray(
			"bearer_methods_supported");

		Assert.assertEquals(1, bearerMethodsSupportedJSONArray.length());
		Assert.assertEquals(
			"header", bearerMethodsSupportedJSONArray.getString(0));

		Assert.assertEquals(
			"Liferay MCP Server", jsonObject.getString("resource_name"));
	}

	private void _updateMCPServerConfiguration(boolean enabled)
		throws Exception {

		ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.mcp.server.internal.configuration." +
				"MCPServerConfiguration.scoped",
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"enabled", enabled
			).build());
	}

	private static final String _MCP_RESOURCE = "http://localhost:8080/o/mcp";

	private static final String _MCP_URL = "http://localhost:8080/o/mcp";

	private BundleContext _bundleContext;
	private OAuthClientPRLocalMetadata _oAuthClientPRLocalMetadata;
	private OAuthClientPRLocalMetadataLocalService
		_oAuthClientPRLocalMetadataLocalService;
	private ServiceReference<OAuthClientPRLocalMetadataLocalService>
		_serviceReference;

}