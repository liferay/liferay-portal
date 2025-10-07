/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.client.test.BaseClientTestCase;
import com.liferay.oauth2.provider.client.test.BaseTestPreparatorBundleActivator;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.AuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.ClientAuthentication;
import com.liferay.oauth2.provider.internal.test.ClientPasswordClientAuthentication;
import com.liferay.oauth2.provider.internal.test.JWTAssertionClientAuthentication;
import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Arthur Chan
 */
@RunWith(Arquillian.class)
public abstract class BaseTokenEndpointTestCase extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	public abstract static class TestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			User user = UserTestUtil.getAdminUser(
				TestPropsValues.getCompanyId());

			clientAuthentications.put(
				TEST_CLIENT_ID_1,
				new ClientPasswordClientAuthentication(
					TEST_CLIENT_ID_1, _TEST_CLIENT_SECRET));
			clientAuthentications.put(
				TEST_CLIENT_ID_2,
				new JWTAssertionClientAuthentication(
					getTokenWebTarget(), TEST_CLIENT_ID_2, false,
					TEST_CLIENT_ID_2, _TEST_CLIENT_SECRET, true));
			clientAuthentications.put(
				TEST_CLIENT_ID_3,
				new JWTAssertionClientAuthentication(
					getTokenWebTarget(), TEST_CLIENT_ID_3, false,
					TEST_CLIENT_ID_3, JWTAssertionUtil.JWKS, false));
			clientAuthentications.put(
				TEST_CLIENT_ID_4,
				new JWTAssertionClientAuthentication(
					getTokenWebTarget(), TEST_CLIENT_ID_4, true,
					TEST_CLIENT_ID_4,
					Base64.encode(
						_TEST_CLIENT_SECRET_NOT_BASE64.getBytes(
							StandardCharsets.UTF_8)),
					true));
			clientAuthentications.put(
				TEST_CLIENT_ID_5,
				new ClientPasswordClientAuthentication(
					TEST_CLIENT_ID_5, _TEST_CLIENT_SECRET));
			clientAuthentications.put(
				TEST_CLIENT_ID_6,
				new ClientPasswordClientAuthentication(
					TEST_CLIENT_ID_6, _TEST_CLIENT_SECRET));

			createOAuth2ApplicationWithClientSecretPost(
				user.getCompanyId(), user, TEST_CLIENT_ID_1,
				_TEST_CLIENT_SECRET,
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN,
					GrantType.JWT_BEARER),
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
			createOAuth2ApplicationWithClientSecretJWT(
				user.getCompanyId(), user, TEST_CLIENT_ID_2,
				_TEST_CLIENT_SECRET,
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN,
					GrantType.JWT_BEARER),
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
			createOAuth2ApplicationWithPrivateKeyJWT(
				user.getCompanyId(), user, TEST_CLIENT_ID_3,
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN,
					GrantType.JWT_BEARER),
				JWTAssertionUtil.JWKS,
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
			createOAuth2ApplicationWithClientSecretJWT(
				user.getCompanyId(), user, TEST_CLIENT_ID_4,
				_TEST_CLIENT_SECRET_NOT_BASE64,
				Arrays.asList(
					GrantType.CLIENT_CREDENTIALS, GrantType.JWT_BEARER),
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
			createOAuth2ApplicationWithClientSecretPost(
				user.getCompanyId(), user, TEST_CLIENT_ID_5,
				_TEST_CLIENT_SECRET,
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN,
					GrantType.JWT_BEARER),
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
			createOAuth2ApplicationWithClientSecretPost(
				user.getCompanyId(), user, TEST_CLIENT_ID_6,
				_TEST_CLIENT_SECRET,
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN,
					GrantType.JWT_BEARER),
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
		}

	}

	protected String getAccessToken(
		AuthorizationGrant authorizationGrant,
		ClientAuthentication clientAuthentication) {

		return parseAccessTokenString(
			getTokenResponse(authorizationGrant, clientAuthentication));
	}

	protected String getRefreshToken(
		AuthorizationGrant authorizationGrant,
		ClientAuthentication clientAuthentication) {

		return parseRefreshTokenString(
			getTokenResponse(authorizationGrant, clientAuthentication));
	}

	protected Response getTokenResponse(
		AuthorizationGrant authorizationGrant,
		ClientAuthentication clientAuthentication) {

		Invocation.Builder invocationBuilder = _getInvocationBuilder();

		MultivaluedMap<String, String> multivaluedMap =
			new MultivaluedHashMap<>();

		multivaluedMap.putAll(
			authorizationGrant.getAuthorizationGrantParameters());
		multivaluedMap.putAll(
			clientAuthentication.getClientAuthenticationParameters());

		return invocationBuilder.post(Entity.form(multivaluedMap));
	}

	protected String parseAccessTokenString(Response response) {
		return parseJsonField(response, "access_token");
	}

	protected String parseRefreshTokenString(Response response) {
		return parseJsonField(response, "refresh_token");
	}

	protected static final String TEST_CLIENT_ID_1 = "test_client_id_1";

	protected static final String TEST_CLIENT_ID_2 = "test_client_id_2";

	protected static final String TEST_CLIENT_ID_3 = "test_client_id_3";

	protected static final String TEST_CLIENT_ID_4 = "test_client_id_4";

	protected static final String TEST_CLIENT_ID_5 = "test_client_id_5";

	protected static final String TEST_CLIENT_ID_6 = "test_client_id_6";

	protected static final Map<String, ClientAuthentication>
		clientAuthentications = new HashMap<>();

	private Invocation.Builder _getInvocationBuilder() {
		return getInvocationBuilder(
			null, getTokenWebTarget(), Function.identity());
	}

	private static final String _TEST_CLIENT_SECRET =
		"oauthTestApplicationSecret";

	private static final String _TEST_CLIENT_SECRET_NOT_BASE64 =
		"secret-2527c3ad-be54-dcea-18a3-ab349ff637ac";

}