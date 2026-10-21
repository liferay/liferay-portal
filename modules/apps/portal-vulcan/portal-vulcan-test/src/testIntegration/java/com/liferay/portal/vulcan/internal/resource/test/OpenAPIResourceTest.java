/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.resource.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.feature.flag.FeatureFlag;
import com.liferay.portal.vulcan.internal.test.util.FeatureFlagManagerTestUtil;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;
import com.liferay.portal.vulcan.resource.OpenAPIResource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class OpenAPIResourceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetOpenAPIServerURL() throws Exception {
		InputStream inputStream = URLConnectionUtil.getInputStream(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-delivery/v1.0/openapi.json");

		String path = _getPath(inputStream, "/servers/0/url");

		Assert.assertTrue(
			path.startsWith(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/"));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"WEB_SERVER_PROTOCOL", "https")) {

			inputStream = URLConnectionUtil.getInputStream(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/o/headless-delivery/v1.0/openapi.json");

			path = _getPath(inputStream, "/servers/0/url");

			Assert.assertTrue(
				path.startsWith(
					"https://localhost:" +
						PortalUtil.getPortalServerPort(false) + "/"));
		}
	}

	@Test
	public void testGetOpenAPIWithUndeclaredFeatureFlag() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.resource." +
					"OpenAPIResourceImpl",
				LoggerTestUtil.WARN);
			SafeCloseable safeCloseable =
				FeatureFlagManagerTestUtil.swapWithSafeCloseable(
					_featureFlagManager)) {

			Response response = _openAPIResource.getOpenAPI(
				Collections.singleton(TestResource.class), "yaml");

			Assert.assertEquals(200, response.getStatus());

			String yaml = (String)response.getEntity();

			Assert.assertTrue(yaml, yaml.contains("/v1.0/tests:"));
			Assert.assertFalse(
				yaml, yaml.contains("/v1.0/undeclared-feature-flag-tests:"));

			List<String> messages = TransformUtil.transform(
				logCapture.getLogEntries(), LogEntry::getMessage);

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					StringBundler.concat(
						"Feature flag ", _FEATURE_FLAG_KEY_UNDECLARED,
						" is not available for company ",
						TestPropsValues.getCompanyId())));
		}
	}

	@Path("/v1.0")
	public static class TestResource {

		@GET
		@Path("/tests")
		@Produces(MediaType.APPLICATION_JSON)
		public String getTestsPage() {
			return "[]";
		}

		@FeatureFlag(_FEATURE_FLAG_KEY_UNDECLARED)
		@GET
		@Path("/undeclared-feature-flag-tests")
		@Produces(MediaType.APPLICATION_JSON)
		public String getUndeclaredFeatureFlagTestsPage() {
			return "[]";
		}

	}

	private String _getPath(InputStream inputStream, String path)
		throws Exception {

		JsonNode jsonNode = _objectMapper.readTree(inputStream);

		jsonNode = jsonNode.at(path);

		return jsonNode.asText();
	}

	private static final String _FEATURE_FLAG_KEY_UNDECLARED = "UNDECLARED-123";

	@Inject
	private FeatureFlagManager _featureFlagManager;

	private final ObjectMapper _objectMapper = new ObjectMapper();

	@Inject
	private OpenAPIResource _openAPIResource;

}