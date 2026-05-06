/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.client.test.BaseClientTestCase;
import com.liferay.oauth2.provider.client.test.BaseTestPreparatorBundleActivator;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class OAuth2ResourceIndicatorsTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIntrospectionEmitsAudAsJSONArray() throws Exception {
		MultivaluedMap<String, String> tokenFormData =
			new MultivaluedHashMap<>();

		tokenFormData.add("client_id", _CLIENT_ID);
		tokenFormData.add("client_secret", _CLIENT_SECRET);
		tokenFormData.add("grant_type", "client_credentials");
		tokenFormData.add("resource", _RESOURCE_URI);

		Invocation.Builder tokenInvocationBuilder =
			getTokenWebTarget().request();

		Response tokenResponse = tokenInvocationBuilder.post(
			Entity.form(tokenFormData));

		Assert.assertEquals(200, tokenResponse.getStatus());

		String accessToken = parseTokenString(tokenResponse);

		Assert.assertNotNull(accessToken);

		MultivaluedMap<String, String> introspectFormData =
			new MultivaluedHashMap<>();

		introspectFormData.add("client_id", _CLIENT_ID);
		introspectFormData.add("client_secret", _CLIENT_SECRET);
		introspectFormData.add("token", accessToken);

		Invocation.Builder introspectInvocationBuilder =
			_getIntrospectionWebTarget().request();

		Response introspectResponse = introspectInvocationBuilder.post(
			Entity.form(introspectFormData));

		Assert.assertEquals(200, introspectResponse.getStatus());

		JSONObject jsonObject = parseJSONObject(introspectResponse);

		Assert.assertTrue(jsonObject.getBoolean("active"));

		JSONArray audJSONArray = jsonObject.getJSONArray("aud");

		Assert.assertNotNull(
			"introspection response missing aud", audJSONArray);
		Assert.assertEquals(1, audJSONArray.length());
		Assert.assertEquals(_RESOURCE_URI, audJSONArray.getString(0));
	}

	@Test
	public void testTokenWithMalformedResourceReturnsInvalidTarget()
		throws Exception {

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", _CLIENT_ID);
		formData.add("client_secret", _CLIENT_SECRET);
		formData.add("grant_type", "client_credentials");
		formData.add("resource", "https://mcp.example.com/#fragment");

		Invocation.Builder invocationBuilder = getTokenWebTarget().request();

		Response response = invocationBuilder.post(Entity.form(formData));

		Assert.assertEquals(400, response.getStatus());
		Assert.assertEquals("invalid_target", parseError(response));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new OAuth2ResourceIndicatorsTestPreparatorBundleActivator();
	}

	private WebTarget _getIntrospectionWebTarget() {
		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path("o");
		webTarget = webTarget.path("oauth2");

		return webTarget.path("introspect");
	}

	private static final String _CLIENT_ID =
		"oauthResourceIndicatorsTestApplication";

	private static final String _CLIENT_SECRET =
		"oauthResourceIndicatorsTestApplicationSecret";

	private static final String _RESOURCE_URI = "https://mcp.example.com/o/mcp";

	private class OAuth2ResourceIndicatorsTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, user, _CLIENT_ID, _CLIENT_SECRET,
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				"client_secret_post", null, Arrays.asList(), false,
				Collections.singletonList("everything"), false);
		}

	}

}