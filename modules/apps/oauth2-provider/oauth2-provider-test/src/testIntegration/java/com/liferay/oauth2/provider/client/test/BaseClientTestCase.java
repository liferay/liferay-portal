/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;

import java.net.URI;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl;
import org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Carlos Sierra Andrés
 */
public abstract class BaseClientTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalRestrictedHeaderSet = ReflectionTestUtil.getFieldValue(
			Class.forName("sun.net.www.protocol.http.HttpURLConnection"),
			"restrictedHeaderSet");

		_restrictedHeaderSet = new HashSet<>(_originalRestrictedHeaderSet);

		_originalRestrictedHeaderSet.clear();
	}

	@AfterClass
	public static void tearDownClass() {
		_originalRestrictedHeaderSet.addAll(_restrictedHeaderSet);
	}

	@Before
	public void setUp() throws Exception {
		_bundleActivator = getBundleActivator();

		Bundle bundle = FrameworkUtil.getBundle(BaseClientTestCase.class);

		_bundleContext = bundle.getBundleContext();

		_bundleActivator.start(_bundleContext);
	}

	@After
	public void tearDown() throws Exception {
		_bundleActivator.stop(_bundleContext);
	}

	protected static Invocation.Builder getInvocationBuilder(
		String hostname, WebTarget webTarget,
		Function<Invocation.Builder, Invocation.Builder>
			invocationBuilderFunction) {

		Invocation.Builder invocationBuilder = webTarget.request();

		if (hostname != null) {
			invocationBuilder = invocationBuilder.header("Host", hostname);
		}

		return invocationBuilderFunction.apply(invocationBuilder);
	}

	protected static WebTarget getOAuth2WebTarget() {
		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path("o");
		webTarget = webTarget.path("oauth2");

		return webTarget;
	}

	protected static WebTarget getTokenWebTarget() {
		WebTarget webTarget = getOAuth2WebTarget();

		return webTarget.path("token");
	}

	protected static WebTarget getWebTarget() {
		ClientBuilder clientBuilder = new ClientBuilderImpl();

		Client client = clientBuilder.build();

		RuntimeDelegate runtimeDelegate = new RuntimeDelegateImpl();

		UriBuilder uriBuilder = runtimeDelegate.createUriBuilder();

		try {
			Company company = CompanyLocalServiceUtil.getCompany(
				TestPropsValues.getCompanyId());

			return client.target(
				uriBuilder.uri(
					"http://" + company.getVirtualHostname() + ":8080"));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected Invocation.Builder authorize(
		Invocation.Builder invocationBuilder, String token) {

		return invocationBuilder.header("Authorization", "Bearer " + token);
	}

	protected OAuth2Authorization fetchOAuth2AuthorizationByAccessTokenContent(
		String accessTokenContent) {

		OAuth2AuthorizationLocalService oAuth2AuthorizationLocalService =
			_bundleContext.getService(
				_bundleContext.getServiceReference(
					OAuth2AuthorizationLocalService.class));

		return oAuth2AuthorizationLocalService.
			fetchOAuth2AuthorizationByAccessTokenContent(accessTokenContent);
	}

	protected String generateCodeChallenge(String codeVerifier) {
		return StringUtil.removeChar(
			StringUtil.replace(
				DigesterUtil.digestBase64(DigesterUtil.SHA_256, codeVerifier),
				new char[] {CharPool.PLUS, CharPool.SLASH},
				new char[] {CharPool.MINUS, CharPool.UNDERLINE}),
			CharPool.EQUAL);
	}

	protected Cookie getAuthenticatedCookie(
		String login, String password, String hostname) {

		Invocation.Builder invocationBuilder = getInvocationBuilder(
			hostname, getPortalWebTarget());

		Response response = invocationBuilder.get();

		String pAuthToken = parsePAuthToken(response);

		Map<String, NewCookie> newCookies = response.getCookies();

		NewCookie cookieSupportNewCookie = newCookies.get(
			CookiesConstants.NAME_COOKIE_SUPPORT);
		NewCookie jSessionIdNewCookie = newCookies.get(
			CookiesConstants.NAME_JSESSIONID);

		invocationBuilder = getInvocationBuilder(hostname, getLoginWebTarget());

		invocationBuilder.cookie(cookieSupportNewCookie);
		invocationBuilder.cookie(jSessionIdNewCookie);

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("login", login);
		formData.add("password", password);
		formData.add("p_auth", pAuthToken);

		response = invocationBuilder.post(Entity.form(formData));

		newCookies = response.getCookies();

		jSessionIdNewCookie = newCookies.get(CookiesConstants.NAME_JSESSIONID);

		if (jSessionIdNewCookie == null) {
			return null;
		}

		return jSessionIdNewCookie.toCookie();
	}

	protected Function<WebTarget, Invocation.Builder>
		getAuthenticatedInvocationBuilderFunction(
			String login, String password, String hostname) {

		return getAuthenticatedInvocationBuilderFunction(
			login, password, hostname, Function.identity());
	}

	protected Function<WebTarget, Invocation.Builder>
		getAuthenticatedInvocationBuilderFunction(
			String login, String password, String hostname,
			Function<Invocation.Builder, Invocation.Builder>
				invocationBuilderFunction) {

		Cookie authenticatedCookie = getAuthenticatedCookie(
			login, password, hostname);

		return webtarget -> {
			Invocation.Builder invocationBuilder = getInvocationBuilder(
				hostname, webtarget, invocationBuilderFunction);

			return invocationBuilder.accept(
				"text/html"
			).cookie(
				authenticatedCookie
			);
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodeBiFunction(
			String user, String password, String hostname) {

		return getAuthorizationCodeBiFunction(
			user, password, hostname, (String)null);
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodeBiFunction(
			String user, String password, String hostname, String scope) {

		return (clientId, invocationBuilder) -> {
			String authorizationCode = parseAuthorizationCodeString(
				getCodeResponse(
					user, password, hostname,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", clientId
						).queryParam(
							"response_type", "code"
						).queryParam(
							"scope", scope
						))));

			BiFunction<String, Invocation.Builder, Response>
				authorizationCodePKCEBiFunction =
					getExchangeAuthorizationCodeBiFunction(
						authorizationCode, null);

			return authorizationCodePKCEBiFunction.apply(
				clientId, invocationBuilder);
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodePKCEBiFunction(
			String userName, String password, String hostname) {

		return (clientId, invocationBuilder) -> {
			String codeVerifier = RandomTestUtil.randomString();

			String codeChallenge = generateCodeChallenge(codeVerifier);

			String authorizationCode = parseAuthorizationCodeString(
				getCodeResponse(
					userName, password, hostname,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", clientId
						).queryParam(
							"code_challenge", codeChallenge
						).queryParam(
							"response_type", "code"
						))));

			BiFunction<String, Invocation.Builder, Response>
				authorizationCodePKCEBiFunction =
					getExchangeAuthorizationCodePKCEBiFunction(
						authorizationCode, null, codeVerifier);

			return authorizationCodePKCEBiFunction.apply(
				clientId, invocationBuilder);
		};
	}

	protected WebTarget getAuthorizeDecisionWebTarget() {
		WebTarget webTarget = getAuthorizeWebTarget();

		return webTarget.path("decision");
	}

	protected WebTarget getAuthorizeWebTarget() {
		WebTarget webTarget = getOAuth2WebTarget();

		return webTarget.path("authorize");
	}

	protected abstract BundleActivator getBundleActivator();

	protected Response getClientCredentialsResponse(
		String clientId, Invocation.Builder invocationBuilder) {

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", clientId);
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "client_credentials");

		return invocationBuilder.post(Entity.form(formData));
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getClientCredentialsResponseBiFunction(String scope) {

		return (clientId, invocationBuilder) -> {
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "client_credentials");
			formData.add("scope", scope);

			return invocationBuilder.post(Entity.form(formData));
		};
	}

	protected Function<Function<WebTarget, Invocation.Builder>, Response>
		getCodeFunction(
			Function<WebTarget, WebTarget> authorizeRequestFunction) {

		return getCodeFunction(authorizeRequestFunction, null, false);
	}

	protected Function<Function<WebTarget, Invocation.Builder>, Response>
		getCodeFunction(
			Function<WebTarget, WebTarget> authorizeRequestFunction,
			boolean skipAuthorization) {

		return getCodeFunction(
			authorizeRequestFunction, null, skipAuthorization);
	}

	protected Function<Function<WebTarget, Invocation.Builder>, Response>
		getCodeFunction(
			Function<WebTarget, WebTarget> authorizeRequestFunction,
			MultivaluedMap<String, String> extraParameters,
			boolean skipAuthorization) {

		return invocationBuilderFunction -> {
			Invocation.Builder invocationBuilder =
				invocationBuilderFunction.apply(
					authorizeRequestFunction.apply(getAuthorizeWebTarget()));

			Response response = invocationBuilder.get();

			URI uri = response.getLocation();

			if (uri == null) {
				return response;
			}

			Map<String, String[]> parameterMap =
				HttpComponentsUtil.getParameterMap(uri.getQuery());

			if (parameterMap.containsKey("error") || skipAuthorization) {
				return response;
			}

			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("oauthDecision", "allow");

			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();

				if (!StringUtil.startsWith(key, "oauth2_")) {
					continue;
				}

				formData.add(
					key.substring("oauth2_".length()), entry.getValue()[0]);
			}

			if (extraParameters != null) {
				formData.putAll(extraParameters);
			}

			invocationBuilder = invocationBuilderFunction.apply(
				getAuthorizeDecisionWebTarget());

			return invocationBuilder.post(Entity.form(formData));
		};
	}

	protected Response getCodeResponse(
		String login, String password, String hostname,
		Function<Function<WebTarget, Invocation.Builder>, Response>
			authorizationResponseFunction) {

		return getCodeResponse(
			login, password, hostname, authorizationResponseFunction,
			Function.identity());
	}

	protected Response getCodeResponse(
		String login, String password, String hostname,
		Function<Function<WebTarget, Invocation.Builder>, Response>
			authorizationResponseFunction,
		Function<Invocation.Builder, Invocation.Builder>
			invocationBuilderFunction) {

		return authorizationResponseFunction.apply(
			getAuthenticatedInvocationBuilderFunction(
				login, password, hostname, invocationBuilderFunction));
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getExchangeAuthorizationCodeBiFunction(
			String authorizationCode, String redirectUri) {

		return (clientId, invocationBuilder) -> {
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("code", authorizationCode);
			formData.add("grant_type", "authorization_code");

			if (Validator.isNotNull(redirectUri)) {
				formData.add("redirect_uri", redirectUri);
			}

			return invocationBuilder.post(Entity.form(formData));
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getExchangeAuthorizationCodePKCEBiFunction(
			String authorizationCode, String redirectUri, String codeVerifier) {

		return (clientId, invocationBuilder) -> {
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("code", authorizationCode);
			formData.add("code_verifier", codeVerifier);
			formData.add("grant_type", "authorization_code");

			if (Validator.isNotNull(redirectUri)) {
				formData.add("redirect_uri", redirectUri);
			}

			return invocationBuilder.post(Entity.form(formData));
		};
	}

	protected Invocation.Builder getInvocationBuilder(
		String hostname, WebTarget webTarget) {

		return getInvocationBuilder(hostname, webTarget, Function.identity());
	}

	protected WebTarget getJsonWebTarget(String... paths) {
		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path("api");
		webTarget = webTarget.path("jsonws");

		for (String path : paths) {
			webTarget = webTarget.path(path);
		}

		return webTarget;
	}

	protected WebTarget getLoginWebTarget() {
		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path("c");
		webTarget = webTarget.path("portal");
		webTarget = webTarget.path("login");

		return webTarget;
	}

	protected WebTarget getPortalWebTarget() {
		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path("web");
		webTarget = webTarget.path("guest");

		return webTarget;
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getResourceOwnerPasswordBiFunction(String userName, String password) {

		return (clientId, invocationBuilder) -> {
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "password");
			formData.add("password", password);
			formData.add("username", userName);

			return invocationBuilder.post(Entity.form(formData));
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getResourceOwnerPasswordBiFunction(
			String userName, String password, String scope) {

		return (clientId, invocationBuilder) -> {
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "password");
			formData.add("password", password);
			formData.add("scope", scope);
			formData.add("username", userName);

			return invocationBuilder.post(Entity.form(formData));
		};
	}

	protected String getToken(String clientId) {
		return getToken(clientId, null);
	}

	protected String getToken(String clientId, String hostname) {
		return parseTokenString(
			getClientCredentialsResponse(
				clientId, getTokenInvocationBuilder(hostname)));
	}

	protected <T> T getToken(
		String clientId, String hostname,
		BiFunction<String, Invocation.Builder, Response> credentialsBiFunction,
		Function<Response, T> tokenParserFunction) {

		return tokenParserFunction.apply(
			credentialsBiFunction.apply(
				clientId, getTokenInvocationBuilder(hostname)));
	}

	protected Invocation.Builder getTokenInvocationBuilder(String hostname) {
		return getInvocationBuilder(hostname, getTokenWebTarget());
	}

	protected WebTarget getWebTarget(String... paths) {
		WebTarget target = getWebTarget();

		target = target.path("o");
		target = target.path("oauth2-test");

		for (String path : paths) {
			target = target.path(path);
		}

		return target;
	}

	protected String parseAuthorizationCodeString(Response response) {
		URI uri = response.getLocation();

		if (uri == null) {
			throw new IllegalArgumentException(
				"Authorization service response missing \"Location\" header " +
					"from which code is extracted");
		}

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			uri.getQuery());

		if (!parameterMap.containsKey("code")) {
			return null;
		}

		return parameterMap.get("code")[0];
	}

	protected String parseError(Response response) {
		return parseJsonField(response, "error");
	}

	protected String parseErrorParameter(Response response) {
		URI uri = response.getLocation();

		if (uri == null) {
			throw new IllegalArgumentException(
				"Authorization service response missing \"Location\" header " +
					"from which error is extracted");
		}

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			uri.getQuery());

		if (!parameterMap.containsKey("error")) {
			return null;
		}

		return parameterMap.get("error")[0];
	}

	protected String parseJsonField(Response response, String fieldName) {
		JSONObject jsonObject = parseJSONObject(response);

		return jsonObject.getString(fieldName);
	}

	protected JSONObject parseJSONObject(Response response) {
		String json = response.readEntity(String.class);

		try {
			return new JSONObjectImpl(json);
		}
		catch (JSONException jsonException) {
			throw new IllegalArgumentException(
				"The token service returned " + json);
		}
	}

	protected String parsePAuthToken(Response response) {
		String bodyContent = response.readEntity(String.class);

		Matcher matcher = _pAuthTokenPattern.matcher(bodyContent);

		matcher.find();

		return matcher.group(2);
	}

	protected String parseScopeString(Response response) {
		return parseJsonField(response, "scope");
	}

	protected String parseTokenString(Response response) {
		return parseJsonField(response, "access_token");
	}

	protected void revokeOAuth2AuthorizationByAccessToken(String token)
		throws PortalException {

		_oAuth2AuthorizationLocalService.deleteOAuth2Authorization(
			_oAuth2AuthorizationLocalService.
				getOAuth2AuthorizationByAccessTokenContent(token));
	}

	protected OAuth2Authorization updateOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization) {

		return _oAuth2AuthorizationLocalService.updateOAuth2Authorization(
			oAuth2Authorization);
	}

	private static Set<String> _originalRestrictedHeaderSet;
	private static final Pattern _pAuthTokenPattern = Pattern.compile(
		"authToken:\\s*(['\"])(((?!\\1).)*)\\1,");
	private static Set<String> _restrictedHeaderSet;

	private BundleActivator _bundleActivator;
	private BundleContext _bundleContext;

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

}