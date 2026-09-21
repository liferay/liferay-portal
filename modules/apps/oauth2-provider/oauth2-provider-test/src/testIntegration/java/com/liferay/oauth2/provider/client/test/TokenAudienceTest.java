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
import java.util.Map;

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
	public void testRefreshTokenPreservesAudiences() throws Exception {
		JSONObject authorizationCodeTokenJSONObject =
			_getAuthorizationCodeTokenJSONObject();

		Assert.assertEquals(
			Collections.singletonList(_RESOURCE_URI),
			_getAudiences(
				authorizationCodeTokenJSONObject.getString("access_token")));

		JSONObject refreshTokenJSONObject = _getRefreshTokenJSONObject(
			authorizationCodeTokenJSONObject.getString("refresh_token"),
			Collections.emptyList());

		Assert.assertEquals(
			Collections.singletonList(_RESOURCE_URI),
			_getAudiences(refreshTokenJSONObject.getString("access_token")));
	}

	@Test
	public void testRefreshTokenWithGrantedResource() throws Exception {
		JSONObject authorizationCodeTokenJSONObject =
			_getAuthorizationCodeTokenJSONObject();

		JSONObject refreshTokenJSONObject = _getRefreshTokenJSONObject(
			authorizationCodeTokenJSONObject.getString("refresh_token"),
			Collections.singletonList(_RESOURCE_URI));

		Assert.assertEquals(
			Collections.singletonList(_RESOURCE_URI),
			_getAudiences(refreshTokenJSONObject.getString("access_token")));
	}

	@Test
	public void testRefreshTokenWithInvalidResource() throws Exception {
		_testRefreshTokenWithInvalidResource(RandomTestUtil.randomString());
		_testRefreshTokenWithInvalidResource(
			StringBundler.concat(
				Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(), "/#",
				RandomTestUtil.randomString()));
		_testRefreshTokenWithInvalidResource(StringPool.SPACE);
	}

	@Test
	public void testRefreshTokenWithUngrantedResource() throws Exception {
		JSONObject authorizationCodeTokenJSONObject =
			_getAuthorizationCodeTokenJSONObject();

		Response response = _getRefreshTokenResponse(
			authorizationCodeTokenJSONObject.getString("refresh_token"),
			Collections.singletonList(
				Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString()));

		Assert.assertEquals(400, response.getStatus());
		Assert.assertEquals("invalid_target", parseError(response));
	}

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

		JSONObject authorizationCodeTokenJSONObject =
			_getAuthorizationCodeTokenJSONObject();

		Assert.assertEquals(
			Collections.singletonList(_RESOURCE_URI),
			_getAudiences(
				authorizationCodeTokenJSONObject.getString("access_token")));
	}

	@Test
	public void testTokenRequestWithInvalidResource() throws Exception {
		_testTokenRequestWithInvalidResource(RandomTestUtil.randomString());
		_testTokenRequestWithInvalidResource(
			StringBundler.concat(
				Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(), "/#",
				RandomTestUtil.randomString()));
		_testTokenRequestWithInvalidResource(StringPool.SPACE);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TokenAudienceTestPreparatorBundleActivator();
	}

	private List<String> _getAudiences(String accessToken) throws Exception {
		WebTarget introspectWebTarget = getIntrospectWebTarget();

		Invocation.Builder invocationBuilder = introspectWebTarget.request();

		Response response = invocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", _CLIENT_ID
					).put(
						"client_secret", _CLIENT_SECRET
					).put(
						"token", accessToken
					).build())));

		Assert.assertEquals(200, response.getStatus());

		JSONObject jsonObject = parseJSONObject(response);

		Assert.assertTrue(jsonObject.getBoolean("active"));

		JSONArray audJSONArray = jsonObject.getJSONArray("aud");

		if (audJSONArray == null) {
			return Collections.emptyList();
		}

		return JSONUtil.toStringList(audJSONArray);
	}

	private JSONObject _getAuthorizationCodeTokenJSONObject() throws Exception {
		String authorizationCode = parseAuthorizationCodeString(
			getCodeResponse(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null,
				getCodeFunction(
					webTarget -> webTarget.queryParam(
						"client_id", _CLIENT_ID
					).queryParam(
						"response_type", "code"
					))));

		Assert.assertNotNull(authorizationCode);

		Response response = _getTokenResponse(
			HashMapBuilder.put(
				"code", authorizationCode
			).put(
				"grant_type", "authorization_code"
			).build(),
			Collections.singletonList(_RESOURCE_URI));

		Assert.assertEquals(200, response.getStatus());

		return parseJSONObject(response);
	}

	private JSONObject _getRefreshTokenJSONObject(
		String refreshToken, List<String> resources) {

		Response response = _getRefreshTokenResponse(refreshToken, resources);

		Assert.assertEquals(200, response.getStatus());

		return parseJSONObject(response);
	}

	private Response _getRefreshTokenResponse(
		String refreshToken, List<String> resources) {

		return _getTokenResponse(
			HashMapBuilder.put(
				"grant_type", "refresh_token"
			).put(
				"refresh_token", refreshToken
			).build(),
			resources);
	}

	private Response _getTokenResponse(
		Map<String, String> parameters, List<String> resources) {

		MultivaluedHashMap<String, String> tokenFormData =
			new MultivaluedHashMap<>(
				HashMapBuilder.put(
					"client_id", _CLIENT_ID
				).put(
					"client_secret", _CLIENT_SECRET
				).putAll(
					parameters
				).build());

		for (String resource : resources) {
			tokenFormData.add("resource", resource);
		}

		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		return invocationBuilder.post(Entity.form(tokenFormData));
	}

	private void _testRefreshTokenWithInvalidResource(String resource)
		throws Exception {

		JSONObject authorizationCodeTokenJSONObject =
			_getAuthorizationCodeTokenJSONObject();

		Response response = _getRefreshTokenResponse(
			authorizationCodeTokenJSONObject.getString("refresh_token"),
			Collections.singletonList(resource));

		Assert.assertEquals(400, response.getStatus());
		Assert.assertEquals("invalid_target", parseError(response));
	}

	private void _testTokenIntrospectionAudience(List<String> resources)
		throws Exception {

		Response tokenResponse = _getTokenResponse(
			HashMapBuilder.put(
				"grant_type", "client_credentials"
			).build(),
			resources);

		Assert.assertEquals(200, tokenResponse.getStatus());

		String accessToken = parseTokenString(tokenResponse);

		Assert.assertNotNull(accessToken);

		List<String> audiences = _getAudiences(accessToken);

		Assert.assertEquals(
			audiences.toString(), resources.size(), audiences.size());
		Assert.assertTrue(audiences.containsAll(resources));
	}

	private void _testTokenRequestWithInvalidResource(String resource)
		throws Exception {

		Response response = _getTokenResponse(
			HashMapBuilder.put(
				"grant_type", "client_credentials"
			).build(),
			Collections.singletonList(resource));

		Assert.assertEquals(400, response.getStatus());
		Assert.assertEquals("invalid_target", parseError(response));
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private static final String _CLIENT_SECRET = RandomTestUtil.randomString();

	private static final String _RESOURCE_URI =
		Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString();

	private User _user;

	private class TokenAudienceTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, _CLIENT_ID, _CLIENT_SECRET,
				Arrays.asList(
					GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE,
					GrantType.REFRESH_TOKEN),
				"client_secret_post", null,
				Collections.singletonList(
					Http.HTTP_WITH_SLASH + RandomTestUtil.randomString()),
				false, Collections.singletonList("everything"), false);
		}

	}

}