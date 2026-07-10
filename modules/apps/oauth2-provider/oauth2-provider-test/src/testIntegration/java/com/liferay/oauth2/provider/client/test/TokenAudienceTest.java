/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class TokenAudienceTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testTokenIntrospectionAudience() throws Exception {
		_testTokenIntrospectionAudience(
			Collections.singletonList(_RESOURCE_URI));
		_testTokenIntrospectionAudience(
			Arrays.asList(
				_RESOURCE_URI,
				Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString()));
	}

	@Test
	public void testTokenIntrospectionAudienceWithAuthorizationCodeGrant()
		throws Exception {

		String authorizationCode = parseAuthorizationCodeString(
			getCodeResponse(
				TestPropsValues.getUser(
				).getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD, null,
				getCodeFunction(
					webTarget -> webTarget.queryParam(
						"client_id", _CLIENT_ID
					).queryParam(
						"response_type", "code"
					))));

		Assert.assertNotNull(authorizationCode);

		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder tokenInvocationBuilder = tokenWebTarget.request();

		Response tokenResponse = tokenInvocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", _CLIENT_ID
					).put(
						"client_secret", _CLIENT_SECRET
					).put(
						"code", authorizationCode
					).put(
						"grant_type", "authorization_code"
					).put(
						"resource", _RESOURCE_URI
					).build())));

		Assert.assertEquals(200, tokenResponse.getStatus());

		String accessToken = parseTokenString(tokenResponse);

		Assert.assertNotNull(accessToken);

		WebTarget introspectWebTarget = getIntrospectWebTarget();

		Invocation.Builder introspectInvocationBuilder =
			introspectWebTarget.request();

		Response introspectResponse = introspectInvocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", _CLIENT_ID
					).put(
						"client_secret", _CLIENT_SECRET
					).put(
						"token", accessToken
					).build())));

		Assert.assertEquals(200, introspectResponse.getStatus());

		JSONObject jsonObject = parseJSONObject(introspectResponse);

		Assert.assertTrue(jsonObject.getBoolean("active"));

		JSONArray audJSONArray = jsonObject.getJSONArray("aud");

		Assert.assertEquals(1, audJSONArray.length());
		Assert.assertEquals(_RESOURCE_URI, audJSONArray.getString(0));
	}

	@Test
	public void testTokenRequestWithInvalidResource() throws Exception {
		_testTokenRequestWithInvalidResource(StringPool.SPACE);
		_testTokenRequestWithInvalidResource(
			StringBundler.concat(
				Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(), "/#",
				RandomTestUtil.randomString()));
		_testTokenRequestWithInvalidResource(RandomTestUtil.randomString());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TokenAudienceTestPreparatorBundleActivator();
	}

	private void _testTokenIntrospectionAudience(List<String> resources)
		throws Exception {

		MultivaluedHashMap<String, String> tokenFormData =
			new MultivaluedHashMap<>(
				HashMapBuilder.put(
					"client_id", _CLIENT_ID
				).put(
					"client_secret", _CLIENT_SECRET
				).put(
					"grant_type", "client_credentials"
				).build());

		for (String resource : resources) {
			tokenFormData.add("resource", resource);
		}

		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder tokenInvocationBuilder = tokenWebTarget.request();

		Response tokenResponse = tokenInvocationBuilder.post(
			Entity.form(tokenFormData));

		Assert.assertEquals(200, tokenResponse.getStatus());

		String accessToken = parseTokenString(tokenResponse);

		Assert.assertNotNull(accessToken);

		WebTarget introspectWebTarget = getIntrospectWebTarget();

		Invocation.Builder introspectInvocationBuilder =
			introspectWebTarget.request();

		Response introspectResponse = introspectInvocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", _CLIENT_ID
					).put(
						"client_secret", _CLIENT_SECRET
					).put(
						"token", accessToken
					).build())));

		Assert.assertEquals(200, introspectResponse.getStatus());

		JSONObject jsonObject = parseJSONObject(introspectResponse);

		Assert.assertTrue(jsonObject.getBoolean("active"));

		JSONArray audJSONArray = jsonObject.getJSONArray("aud");

		Assert.assertEquals(resources.size(), audJSONArray.length());

		List<String> audiences = JSONUtil.toStringList(audJSONArray);

		Assert.assertTrue(audiences.containsAll(resources));
	}

	private void _testTokenRequestWithInvalidResource(String resource)
		throws Exception {

		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		Response response = invocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", _CLIENT_ID
					).put(
						"client_secret", _CLIENT_SECRET
					).put(
						"grant_type", "client_credentials"
					).put(
						"resource", resource
					).build())));

		Assert.assertEquals(400, response.getStatus());
		Assert.assertEquals("invalid_target", parseError(response));
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private static final String _CLIENT_SECRET = RandomTestUtil.randomString();

	private static final String _RESOURCE_URI =
		Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString();

	private class TokenAudienceTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, user, _CLIENT_ID, _CLIENT_SECRET,
				Arrays.asList(
					GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE),
				"client_secret_post", null,
				Collections.singletonList(
					Http.HTTP_WITH_SLASH + RandomTestUtil.randomString()),
				false, Collections.singletonList("everything"), false);
		}

	}

}