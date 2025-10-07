/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.AuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.JWTAssertionAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;

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
	public void testGrantWithCorrectAudience1() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		JWTAssertionAuthorizationGrant jwtAssertionAuthorizationGrant =
			new JWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_1, null, user.getUuid(), getTokenWebTarget());

		Assert.assertTrue(
			Validator.isNotNull(
				getAccessToken(
					jwtAssertionAuthorizationGrant,
					clientAuthentications.get(TEST_CLIENT_ID_1))));
	}

	@Test
	public void testGrantWithCorrectAudience2() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		JWTAssertionAuthorizationGrant jwtAssertionAuthorizationGrant =
			new JWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_5, null, user.getEmailAddress(),
				getTokenWebTarget());

		String accessToken = getAccessToken(
			jwtAssertionAuthorizationGrant,
			clientAuthentications.get(TEST_CLIENT_ID_5));

		Assert.assertTrue(Validator.isNotNull(accessToken));

		String[] parts = accessToken.split("\\.");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			new String(Base64.decode(parts[1])));

		Assert.assertEquals(user.getUserId(), jsonObject.getLong("sub"));
		Assert.assertEquals(
			user.getScreenName(), jsonObject.getString("username"));
	}

	@Test
	public void testGrantWithCorrectAudience3() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		JWTAssertionAuthorizationGrant jwtAssertionAuthorizationGrant =
			new JWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_6, null, user.getScreenName(),
				getTokenWebTarget());

		String accessToken = getAccessToken(
			jwtAssertionAuthorizationGrant,
			clientAuthentications.get(TEST_CLIENT_ID_6));

		Assert.assertTrue(Validator.isNotNull(accessToken));

		String[] parts = accessToken.split("\\.");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			new String(Base64.decode(parts[1])));

		Assert.assertEquals(user.getUserId(), jsonObject.getLong("sub"));
		Assert.assertEquals(
			user.getScreenName(), jsonObject.getString("username"));
	}

	@Test
	public void testGrantWithWrongAudience() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		JWTAssertionAuthorizationGrant jwtAssertionAuthorizationGrant =
			new JWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_1, null, user.getUuid(),
				getJsonWebTarget("wrongPath"));

		Assert.assertTrue(
			Validator.isNull(
				getAccessToken(
					jwtAssertionAuthorizationGrant,
					clientAuthentications.get(TEST_CLIENT_ID_1))));
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