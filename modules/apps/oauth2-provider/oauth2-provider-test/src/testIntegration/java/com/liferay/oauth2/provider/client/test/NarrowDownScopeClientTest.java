/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.TestApplication;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
public class NarrowDownScopeClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		Assert.assertEquals(
			"GET",
			getToken(
				"oauthTestApplication", null,
				getAuthorizationCodeBiFunction(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null, "GET"),
				this::parseScopeString));

		Assert.assertEquals(
			"GET",
			getToken(
				"oauthTestApplication", null,
				getClientCredentialsResponseBiFunction("GET"),
				this::parseScopeString));

		Response response = getToken(
			"oauthTestApplication", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				"GET"),
			Function.identity());

		JSONObject jsonObject = parseJSONObject(response);

		Assert.assertEquals("GET", jsonObject.getString("scope"));

		WebTarget webTarget = getWebTarget("methods");

		Invocation.Builder builder = authorize(
			webTarget.request(), jsonObject.getString("access_token"));

		Response postResponse = builder.post(
			Entity.entity("", MediaType.TEXT_PLAIN_TYPE));

		Assert.assertEquals(403, postResponse.getStatus());

		String scopeString = getToken(
			"oauthTestApplication", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseScopeString);

		Assert.assertEquals(
			new HashSet<>(Arrays.asList("GET", "POST")),
			new HashSet<>(Arrays.asList(scopeString.split(" "))));

		Assert.assertEquals(
			"invalid_grant",
			getToken(
				"oauthTestApplication", null,
				getResourceOwnerPasswordBiFunction(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					"GET POST PUT"),
				this::parseError));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new NarrowDownScopeTestPreparatorBundleActivator();
	}

	private User _user;

	private class NarrowDownScopeTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			registerJaxRsApplication(
				new TestApplication(), "methods",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.test.application", true
				).build());

			createOAuth2Application(
				companyId, _user, "oauthTestApplication",
				Arrays.asList(
					GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS,
					GrantType.RESOURCE_OWNER_PASSWORD),
				Arrays.asList("GET", "POST"));
		}

	}

}