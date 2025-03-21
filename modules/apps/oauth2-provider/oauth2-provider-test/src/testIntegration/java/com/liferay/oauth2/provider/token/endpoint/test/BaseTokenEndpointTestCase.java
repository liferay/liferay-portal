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
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

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
				PortalUtil.getDefaultCompanyId());

			clientAuthentications.put(
				TEST_CLIENT_ID_1,
				new ClientPasswordClientAuthentication(
					TEST_CLIENT_ID_1, TEST_CLIENT_SECRET));
			clientAuthentications.put(
				TEST_CLIENT_ID_2,
				new JWTAssertionClientAuthentication(
					getTokenWebTarget(), TEST_CLIENT_ID_2, false,
					TEST_CLIENT_ID_2, TEST_CLIENT_SECRET, true));
			clientAuthentications.put(
				TEST_CLIENT_ID_3,
				new JWTAssertionClientAuthentication(
					getTokenWebTarget(), TEST_CLIENT_ID_3, false,
					TEST_CLIENT_ID_3, JWTAssertionUtil.JWKS, false));

			createOAuth2ApplicationWithClientSecretPost(
				user.getCompanyId(), user, TEST_CLIENT_ID_1, TEST_CLIENT_SECRET,
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN,
					GrantType.JWT_BEARER),
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
			createOAuth2ApplicationWithClientSecretJWT(
				user.getCompanyId(), user, TEST_CLIENT_ID_2, TEST_CLIENT_SECRET,
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

		MultivaluedMap<String, String> multivaluedMap =
			new MultivaluedHashMap<>();

		multivaluedMap.putAll(
			authorizationGrant.getAuthorizationGrantParameters());
		multivaluedMap.putAll(
			clientAuthentication.getClientAuthenticationParameters());

		return _invocationBuilder.post(Entity.form(multivaluedMap));
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

	protected static final String TEST_CLIENT_SECRET =
		"oauthTestApplicationSecret";

	protected static final Map<String, ClientAuthentication>
		clientAuthentications = new HashMap<>();

	private static Invocation.Builder _getInvocationBuilder() {
		return getInvocationBuilder(
			null, getTokenWebTarget(), Function.identity());
	}

	private static final Invocation.Builder _invocationBuilder =
		_getInvocationBuilder();

}