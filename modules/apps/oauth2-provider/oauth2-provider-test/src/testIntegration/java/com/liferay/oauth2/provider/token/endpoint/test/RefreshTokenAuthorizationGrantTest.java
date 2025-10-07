/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.AuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.PasswordAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.RefreshTokenAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
public class RefreshTokenAuthorizationGrantTest
	extends BaseAuthorizationGrantTestCase {

	@Test
	public void test() throws Exception {
		JSONObject jsonObject = getToken(
			"oauthTestApplication", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseJSONObject);

		WebTarget webTarget = getWebTarget("/annotated");

		String accessTokenString = jsonObject.getString("access_token");

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), accessTokenString);

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder tokenBuilder = tokenWebTarget.request();

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", "oauthTestApplication");
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "refresh_token");
		formData.add("refresh_token", jsonObject.getString("refresh_token"));

		String tokenString = parseTokenString(
			tokenBuilder.post(Entity.form(formData)));

		Assert.assertNotEquals(tokenString, accessTokenString);

		invocationBuilder = authorize(webTarget.request(), tokenString);

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		invocationBuilder = authorize(webTarget.request(), accessTokenString);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Response response = invocationBuilder.get();

			Assert.assertEquals(401, response.getStatus());
		}
	}

	@Override
	protected AuthorizationGrant getAuthorizationGrant(String clientId) {
		return new RefreshTokenAuthorizationGrant(
			getRefreshToken(
				new PasswordAuthorizationGrant(
					_user.getEmailAddress(),
					PropsValues.DEFAULT_ADMIN_PASSWORD),
				clientAuthentications.get(clientId)));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TokenExpeditionTestPreparatorBundleActivator();
	}

	private User _user;

	private class TokenExpeditionTestPreparatorBundleActivator
		extends BaseTokenEndpointTestCase.TestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", false
				).put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			createOAuth2Application(
				companyId, _user, "oauthTestApplication",
				Arrays.asList(
					GrantType.RESOURCE_OWNER_PASSWORD, GrantType.REFRESH_TOKEN),
				Collections.singletonList("everything"));

			super.prepareTest();
		}

	}

}