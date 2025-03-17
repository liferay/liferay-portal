/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl;
import org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marta Medio
 */
@RunWith(Arquillian.class)
public class PortalConfigurationCORSClientTest extends BaseCORSClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCORSUsingBasicWithDefaultConfig() throws Exception {
		assertJsonWSUrl("/user/get-current-user", HttpMethod.OPTIONS, false);
		assertJsonWSUrl("/user/get-current-user", HttpMethod.GET, false);

		assertJsonWSUrl(
			"/user/get-current-user", HttpMethod.OPTIONS, true,
			"http://localhost:8080");
		assertJsonWSUrl(
			"/user/get-current-user", HttpMethod.GET, false,
			"http://localhost:8080");

		assertJsonWSUrl(
			"/user/get-current-user", HttpMethod.OPTIONS, true,
			"http://127.0.0.1:8080");
		assertJsonWSUrl(
			"/user/get-current-user", HttpMethod.GET, false,
			"http://127.0.0.1:8080");

		assertJsonWSUrl(
			"/user/get-current-user", HttpMethod.OPTIONS, true, "::1");
		assertJsonWSUrl("/user/get-current-user", HttpMethod.GET, false, "::1");
	}

	@Test
	public void testCORSUsingBasicWithDisableAuthorization() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"CORS_DISABLE_AUTHORIZATION_CONTEXT_CHECK", true)) {

			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.OPTIONS, false);
			assertJsonWSUrl("/user/get-current-user", HttpMethod.GET, false);

			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.OPTIONS, true,
				"http://localhost:8080");
			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.GET, true,
				"http://localhost:8080");

			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.OPTIONS, true,
				"http://127.0.0.1:8080");
			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.GET, true,
				"http://127.0.0.1:8080");

			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.OPTIONS, true, "::1");
			assertJsonWSUrl(
				"/user/get-current-user", HttpMethod.GET, true, "::1");
		}
	}

	@Test
	public void testNoCORSUsingPortalSession() throws Exception {
		Cookie authenticatedCookie = _getAuthenticatedCookie(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD);

		Invocation.Builder invocationBuilder = _getWebTarget(
			"web", "guest"
		).request();

		invocationBuilder = invocationBuilder.cookie(authenticatedCookie);

		Response response = invocationBuilder.get();

		_pAuth = _parsePAuthToken(response);

		invocationBuilder = _getJsonWebTarget(
			"user", "get-current-user"
		).request();

		invocationBuilder = invocationBuilder.cookie(authenticatedCookie);

		invocationBuilder = invocationBuilder.header(
			"Origin", "http://test-cors.com");

		response = invocationBuilder.get();

		String corsHeaderString = response.getHeaderString(
			"Access-Control-Allow-Origin");

		Assert.assertNull(corsHeaderString);
	}

	private Cookie _getAuthenticatedCookie(String login, String password) {
		Invocation.Builder invocationBuilder = _getWebTarget(
			"web", "guest"
		).request();

		Response response = invocationBuilder.get();

		_pAuth = _parsePAuthToken(response);

		Map<String, NewCookie> newCookies = response.getCookies();

		NewCookie cookieSupportNewCookie = newCookies.get(
			CookiesConstants.NAME_COOKIE_SUPPORT);
		NewCookie jSessionIdNewCookie = newCookies.get(
			CookiesConstants.NAME_JSESSIONID);

		invocationBuilder = _getWebTarget(
			"c", "portal", "login"
		).request();

		invocationBuilder = invocationBuilder.cookie(cookieSupportNewCookie);
		invocationBuilder = invocationBuilder.cookie(jSessionIdNewCookie);

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("login", login);
		formData.add("password", password);
		formData.add("p_auth", _pAuth);

		response = invocationBuilder.post(Entity.form(formData));

		newCookies = response.getCookies();

		jSessionIdNewCookie = newCookies.get(CookiesConstants.NAME_JSESSIONID);

		if (jSessionIdNewCookie == null) {
			return null;
		}

		return jSessionIdNewCookie.toCookie();
	}

	private WebTarget _getJsonWebTarget(String... paths) {
		WebTarget webTarget = _getLocalhostWebTarget();

		webTarget = webTarget.path("api");
		webTarget = webTarget.path("jsonws");

		for (String path : paths) {
			webTarget = webTarget.path(path);
		}

		return webTarget.queryParam("p_auth", _pAuth);
	}

	private WebTarget _getLocalhostWebTarget() {
		ClientBuilder clientBuilder = new ClientBuilderImpl();

		Client client = clientBuilder.build();

		RuntimeDelegate runtimeDelegate = new RuntimeDelegateImpl();

		UriBuilder uriBuilder = runtimeDelegate.createUriBuilder();

		return client.target(uriBuilder.uri("http://localhost:8080"));
	}

	private WebTarget _getWebTarget(String... paths) {
		WebTarget webTarget = _getLocalhostWebTarget();

		for (String path : paths) {
			webTarget = webTarget.path(path);
		}

		return webTarget;
	}

	private String _parsePAuthToken(Response response) {
		String bodyContent = response.readEntity(String.class);

		Matcher matcher = _pAuthTokenPattern.matcher(bodyContent);

		matcher.find();

		return matcher.group(2);
	}

	private static final Pattern _pAuthTokenPattern = Pattern.compile(
		"Liferay.authToken\\s*=\\s*(['\"])(((?!\\1).)*)\\1;");

	private String _pAuth;

}