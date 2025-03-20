/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.remote.cors.configuration.PortalCORSConfiguration;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.Collections;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
		formData.add("username", "test@liferay.com");

		tokenInvocationBuilder.header("Origin", _TEST_CORS_URI);

		Response response = tokenInvocationBuilder.post(Entity.form(formData));

		String corsHeaderString = response.getHeaderString(
			"Access-Control-Allow-Origin");

		Assert.assertEquals(_TEST_CORS_URI, corsHeaderString);
	}

	@Test
	public void testCORSRequest() throws Exception {
		WebTarget webTarget = getJsonWebTarget("user", "get-current-user");

		String tokenString = getToken(
			"oauthTestApplicationRO", null,
			getResourceOwnerPasswordBiFunction(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), tokenString);

		invocationBuilder.header("Origin", _TEST_CORS_URI);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PortalCORSConfiguration.class.getName() + "~default",
					HashMapDictionaryBuilder.<String, Object>put(
						"headers", _HEADERS
					).build())) {

			Response response = invocationBuilder.get();

			String corsHeaderString = response.getHeaderString(
				"Access-Control-Allow-Origin");

			Assert.assertEquals(_TEST_CORS_URI, corsHeaderString);
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

		String corsHeaderString = response.getHeaderString(
			"Access-Control-Allow-Origin");

		Assert.assertEquals(null, corsHeaderString);

		Assert.assertEquals(401, response.getStatus());
	}

	@Test
	public void testPreflightCORSRequest() throws Exception {
		WebTarget webTarget = getJsonWebTarget("user", "get-current-user");

		String tokenString = getToken(
			"oauthTestApplicationRO", null,
			getResourceOwnerPasswordBiFunction(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD),
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
						"headers", _HEADERS
					).build())) {

			Response response = invocationBuilder.options();

			String corsHeaderString = response.getHeaderString(
				"Access-Control-Allow-Origin");

			Assert.assertEquals(_TEST_CORS_URI, corsHeaderString);
		}
	}

	public static class CORSApplicationTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationRO",
				Collections.singletonList("everything.read"));
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new CORSApplicationTestPreparatorBundleActivator();
	}

	private static final String _HEADERS =
		"Access-Control-Allow-Credentials: true|Access-Control-Allow-Headers:" +
			"*|Access-Control-Allow-Methods: *|Access-Control-Allow-Origin:*";

	private static final String _TEST_CORS_URI = "http://test-cors.com";

}