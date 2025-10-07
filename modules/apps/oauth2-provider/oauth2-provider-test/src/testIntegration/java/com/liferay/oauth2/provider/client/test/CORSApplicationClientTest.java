/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.remote.cors.configuration.PortalCORSConfiguration;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Marta Medio
 */
@RunWith(Arquillian.class)
public class CORSApplicationClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCORSObtainingToken() throws Exception {
		Invocation.Builder tokenInvocationBuilder = getTokenInvocationBuilder(
			null);

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", "oauthTestApplicationRO");
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "password");
		formData.add("password", PropsValues.DEFAULT_ADMIN_PASSWORD);
		formData.add("username", _user.getEmailAddress());

		tokenInvocationBuilder.header("Origin", _TEST_CORS_URI);

		Response response = tokenInvocationBuilder.post(Entity.form(formData));

		Assert.assertEquals(
			_TEST_CORS_URI,
			response.getHeaderString("Access-Control-Allow-Origin"));
	}

	@Test
	public void testCORSRequest() throws Exception {
		WebTarget webTarget = getJsonWebTarget("user", "get-current-user");

		String tokenString = getToken(
			"oauthTestApplicationRO", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), tokenString);

		invocationBuilder.header("Origin", _TEST_CORS_URI);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PortalCORSConfiguration.class.getName() + "~default",
					HashMapDictionaryBuilder.<String, Object>put(
						"headers",
						StringBundler.concat(
							"Access-Control-Allow-Credentials: true|",
							"Access-Control-Allow-Headers: *|",
							"Access-Control-Allow-Methods: *|",
							"Access-Control-Allow-Origin: *")
					).build())) {

			Response response = invocationBuilder.get();

			Assert.assertEquals(
				_TEST_CORS_URI,
				response.getHeaderString("Access-Control-Allow-Origin"));
		}
	}

	@Test
	public void testCORSRequestInvalidToken() throws Exception {
		WebTarget webTarget = getJsonWebTarget("user", "get-current-user");

		String tokenString = "wrong-token";

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), tokenString);

		invocationBuilder.header("Origin", _TEST_CORS_URI);

		Response response = invocationBuilder.get();

		Assert.assertEquals(
			null, response.getHeaderString("Access-Control-Allow-Origin"));

		Assert.assertEquals(401, response.getStatus());
	}

	@Test
	public void testPreflightCORSRequest() throws Exception {
		WebTarget webTarget = getJsonWebTarget("user", "get-current-user");

		String tokenString = getToken(
			"oauthTestApplicationRO", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), tokenString);

		invocationBuilder.header(
			"Access-Control-Request-Method", HttpMethod.OPTIONS);
		invocationBuilder.header("Origin", _TEST_CORS_URI);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PortalCORSConfiguration.class.getName() + "~default",
					HashMapDictionaryBuilder.<String, Object>put(
						"headers",
						StringBundler.concat(
							"Access-Control-Allow-Credentials: true|",
							"Access-Control-Allow-Headers: *|",
							"Access-Control-Allow-Methods: *|",
							"Access-Control-Allow-Origin: *")
					).build())) {

			Response response = invocationBuilder.options();

			Assert.assertEquals(
				_TEST_CORS_URI,
				response.getHeaderString("Access-Control-Allow-Origin"));
		}
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new CORSApplicationTestPreparatorBundleActivator();
	}

	private static final String _TEST_CORS_URI = "http://test-cors.com";

	private User _user;

	private class CORSApplicationTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationRO",
				Collections.singletonList("everything.read"));
		}

	}

}