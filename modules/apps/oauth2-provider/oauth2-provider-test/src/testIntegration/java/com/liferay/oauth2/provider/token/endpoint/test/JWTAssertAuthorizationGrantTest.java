/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.AuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.JWTAssertionAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.client.WebTarget;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Arthur Chan
 */
@RunWith(Arquillian.class)
public class JWTAssertAuthorizationGrantTest
	extends BaseAuthorizationGrantTestCase {

	@Test
	public void testGrant() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		_testGrant(TEST_CLIENT_ID_1, user.getUuid());
		_testGrant(TEST_CLIENT_ID_5, user.getEmailAddress());
		_testGrant(TEST_CLIENT_ID_6, user.getScreenName());
	}

	@Test
	public void testGrantSubject() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		_testGrantSubject(TEST_CLIENT_ID_5, user.getEmailAddress(), user);
		_testGrantSubject(TEST_CLIENT_ID_6, user.getScreenName(), user);
	}

	@Test
	public void testGrantWithoutSubject() {
		JSONObject jsonObject = _getTokenResponseJSONObject(
			getTokenWebTarget(), TEST_CLIENT_ID_1, null);

		Assert.assertEquals("invalid_grant", jsonObject.getString("error"));
	}

	@Test
	public void testGrantWithWrongAudience() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		JSONObject jsonObject = _getTokenResponseJSONObject(
			getJsonWebTarget("wrongPath"), TEST_CLIENT_ID_1, user.getUuid());

		Assert.assertEquals("invalid_grant", jsonObject.getString("error"));
	}

	@Override
	protected AuthorizationGrant getAuthorizationGrant(String clientId) {
		User user = null;

		try {
			user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

			return new JWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_1, null, user.getUuid(), getTokenWebTarget());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new JWTBearerGrantTestPreparatorBundleActivator();
	}

	private JSONObject _getTokenResponseJSONObject(
		WebTarget audienceWebTarget, String clientId, String subject) {

		JWTAssertionAuthorizationGrant jwtAssertionAuthorizationGrant =
			new JWTAssertionAuthorizationGrant(
				clientId, null, subject, audienceWebTarget);

		return parseJSONObject(
			getTokenResponse(
				jwtAssertionAuthorizationGrant,
				clientAuthentications.get(clientId)));
	}

	private void _testGrant(String clientId, String subject) {
		JSONObject jsonObject = _getTokenResponseJSONObject(
			getTokenWebTarget(), clientId, subject);

		Assert.assertEquals(StringPool.BLANK, jsonObject.getString("error"));

		Assert.assertTrue(
			Validator.isNotNull(jsonObject.getString("access_token")));
	}

	private void _testGrantSubject(String clientId, String subject, User user)
		throws Exception {

		JSONObject responseJSONObject = _getTokenResponseJSONObject(
			getTokenWebTarget(), clientId, subject);

		String accessToken = responseJSONObject.getString("access_token");

		String[] parts = accessToken.split("\\.");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			new String(Base64.decode(parts[1])));

		Assert.assertEquals(user.getUserId(), jsonObject.getLong("sub"));
		Assert.assertEquals(
			user.getScreenName(), jsonObject.getString("username"));
	}

	private class JWTBearerGrantTestPreparatorBundleActivator
		extends BaseTokenEndpointTestCase.TestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			createFactoryConfiguration(
				"com.liferay.oauth2.provider.rest.internal.configuration." +
					"OAuth2InAssertionConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.in.assertion.issuer", TEST_CLIENT_ID_1
				).put(
					"oauth2.in.assertion.signature.json.web.key.set",
					JWTAssertionUtil.JWKS
				).put(
					"oauth2.in.assertion.user.auth.type", "UUID"
				).build());
			createFactoryConfiguration(
				"com.liferay.oauth2.provider.rest.internal.configuration." +
					"OAuth2InAssertionConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.in.assertion.issuer", TEST_CLIENT_ID_5
				).put(
					"oauth2.in.assertion.signature.json.web.key.set",
					JWTAssertionUtil.JWKS
				).put(
					"oauth2.in.assertion.user.auth.type", "emailAddress"
				).build());
			createFactoryConfiguration(
				"com.liferay.oauth2.provider.rest.internal.configuration." +
					"OAuth2InAssertionConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.in.assertion.issuer", TEST_CLIENT_ID_6
				).put(
					"oauth2.in.assertion.signature.json.web.key.set",
					JWTAssertionUtil.JWKS
				).put(
					"oauth2.in.assertion.user.auth.type", "screenName"
				).build());

			super.prepareTest();
		}

	}

}