/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.dynamic.registration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.client.test.BaseClientTestCase;
import com.liferay.oauth2.provider.client.test.BaseTestPreparatorBundleActivator;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ApplicationConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jorge García Jiménez
 */
@FeatureFlag("LPD-63416")
@RunWith(Arquillian.class)
public class DynamicRegistrationServiceTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_auditMessages = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(
			DynamicRegistrationServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", "*");

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		super.tearDown();
	}

	@Test
	public void testDeleteClientRegistration() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, "oauthDeleteMeApplication");

		WebTarget registerWebTarget = getRegisterWebTarget(
			oAuth2Application.getClientId());

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					companyId, "oauthDynamicRegisterTestApplication")));

		Response response = invocationBuilder.delete();

		Assert.assertEquals(403, response.getStatus());

		invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(_getDynamicRegistratorOAuth2Application()));

		response = invocationBuilder.delete();

		Assert.assertEquals(204, response.getStatus());

		response = invocationBuilder.delete();

		Assert.assertEquals(401, response.getStatus());
	}

	@Test
	public void testRegister() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					TestPropsValues.getCompanyId(),
					"oauthDynamicRegisterTestApplication")));

		Response response = invocationBuilder.method(
			"post",
			Entity.json(
				JSONUtil.put(
					"client_name", RandomTestUtil.randomString()
				).toString()));

		Assert.assertEquals(401, response.getStatus());

		String clientName = RandomTestUtil.randomString();

		String scope =
			RandomTestUtil.randomString() + StringPool.SPACE +
				RandomTestUtil.randomString();

		JSONObject jsonObject = _createAuthenticatedRegistrationJSONObject(
			clientName, scope);

		response = invocationBuilder.method(
			"post", Entity.json(jsonObject.toString()));

		Assert.assertEquals(401, response.getStatus());

		OAuth2Application oAuth2Application =
			_getDynamicRegistratorOAuth2Application();

		invocationBuilder = authorize(
			registerWebTarget.request(), _getToken(oAuth2Application));

		response = invocationBuilder.method(
			"post", Entity.json(jsonObject.toString()));

		Assert.assertEquals(201, response.getStatus());

		JSONObject responseJSONObject = parseJSONObject(response);

		Assert.assertEquals(
			clientName, responseJSONObject.getString("client_name"));

		String[] expectedScopes = StringUtil.split(scope, CharPool.SPACE);

		Arrays.sort(expectedScopes);

		String[] actualScopes = StringUtil.split(
			responseJSONObject.getString("scope"), CharPool.SPACE);

		Arrays.sort(actualScopes);

		Assert.assertArrayEquals(expectedScopes, actualScopes);

		String clientId = responseJSONObject.getString(
			OAuthConstants.CLIENT_ID);

		jsonObject.put(
			"response_types",
			Collections.singletonList(OAuthConstants.CODE_RESPONSE_TYPE));

		response = invocationBuilder.method(
			"post", Entity.json(jsonObject.toString()));

		Assert.assertEquals(400, response.getStatus());

		Assert.assertEquals("invalid_client_metadata", parseError(response));

		registerWebTarget = getRegisterWebTarget(clientId);

		invocationBuilder = authorize(
			registerWebTarget.request(), _getToken(oAuth2Application));

		invocationBuilder.header("Origin", RandomTestUtil.randomString());

		response = invocationBuilder.get();

		Assert.assertEquals(200, response.getStatus());

		responseJSONObject = parseJSONObject(response);

		Assert.assertEquals(
			clientName, responseJSONObject.getString("client_name"));

		Assert.assertNull(
			response.getHeaderString("Access-Control-Allow-Origin"));

		AuditMessage addAuditMessage = _fetchAuditMessage(
			"DYNAMIC_REGISTRATION_ADD");

		Assert.assertEquals(
			OAuth2Application.class.getName(), addAuditMessage.getClassName());
		Assert.assertEquals(clientId, addAuditMessage.getClassPK());

		JSONObject addAdditionalInfoJSONObject =
			addAuditMessage.getAdditionalInfo();

		Assert.assertEquals(
			clientName, addAdditionalInfoJSONObject.getString("clientName"));

		JSONArray grantTypesJSONArray =
			addAdditionalInfoJSONObject.getJSONArray("grantTypes");

		Assert.assertEquals(1, grantTypesJSONArray.length());
		Assert.assertEquals(
			OAuthConstants.CLIENT_CREDENTIALS_GRANT,
			grantTypesJSONArray.getString(0));

		Assert.assertEquals(
			"authenticated", addAdditionalInfoJSONObject.getString("mode"));

		String[] auditScopes = StringUtil.split(
			addAdditionalInfoJSONObject.getString("scope"), CharPool.SPACE);

		Arrays.sort(auditScopes);

		Assert.assertArrayEquals(expectedScopes, auditScopes);

		AuditMessage rejectAuditMessage = _fetchAuditMessage(
			"DYNAMIC_REGISTRATION_REJECT");

		Assert.assertEquals(
			OAuth2Application.class.getName(),
			rejectAuditMessage.getClassName());

		JSONObject rejectAdditionalInfoJSONObject =
			rejectAuditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"invalid_client_metadata",
			rejectAdditionalInfoJSONObject.getString("error"));
	}

	@Test
	public void testRegisterInOpenMode() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		WebTarget registerWebTarget = getRegisterWebTarget();

		JSONObject jsonObject = _createOpenRegistrationJSONObject(
			true, _getRandomRedirectURI());

		String clientName = jsonObject.getString("client_name");

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(companyId)) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			Response response = invocationBuilder.method(
				"post", Entity.json(jsonObject.toString()));

			Assert.assertEquals(201, response.getStatus());

			JSONObject responseJSONObject = parseJSONObject(response);

			Assert.assertEquals(
				clientName, responseJSONObject.getString("client_name"));

			String clientId = responseJSONObject.getString(
				OAuthConstants.CLIENT_ID);

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					companyId, clientId);

			User user = _userLocalService.getUserByScreenName(
				companyId, UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);

			Assert.assertEquals(
				user.getUserId(), oAuth2Application.getUserId());

			Assert.assertFalse(oAuth2Application.isTrustedApplication());

			AuditMessage auditMessage = _fetchAuditMessage(
				"DYNAMIC_REGISTRATION_ADD");

			Assert.assertEquals(
				OAuth2Application.class.getName(), auditMessage.getClassName());
			Assert.assertEquals(clientId, auditMessage.getClassPK());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertEquals(
				clientName, additionalInfoJSONObject.getString("clientName"));
			Assert.assertEquals(
				"open", additionalInfoJSONObject.getString("mode"));
		}
	}

	@Test
	public void testRegisterInOpenModeEnforcesAllowedHosts() throws Exception {
		String allowedHost = RandomTestUtil.randomString();

		String bracketedHostAndPort = StringBundler.concat(
			"[", allowedHost, "]:", PortalUtil.getPortalServerPort(false));

		// Allow when the bracketed IPv6 host is compared with or without a port

		_testRegisterInOpenModeEnforcesAllowedHosts(
			allowedHost, 201, bracketedHostAndPort);
		_testRegisterInOpenModeEnforcesAllowedHosts(
			bracketedHostAndPort, 201, allowedHost);

		// Allow when the port is present on the request host

		_testRegisterInOpenModeEnforcesAllowedHosts(
			allowedHost, 201,
			allowedHost + ":" + PortalUtil.getPortalServerPort(false));

		// Allow when the request host matches exactly

		_testRegisterInOpenModeEnforcesAllowedHosts(
			allowedHost, 201, allowedHost);

		// Deny when the request host does not match

		_testRegisterInOpenModeEnforcesAllowedHosts(
			allowedHost, 403, RandomTestUtil.randomString());
	}

	@Test
	public void testRegisterInOpenModeIgnoresUntrustedProxyHeaders()
		throws Exception {

		String allowedHost = RandomTestUtil.randomString();

		WebTarget registerWebTarget = getRegisterWebTarget();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"dynamic.registration.allowed.hosts",
						new String[] {allowedHost})) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			invocationBuilder.header("X-Forwarded-For", allowedHost);

			Response response = invocationBuilder.method(
				"post",
				Entity.json(
					_createOpenRegistrationJSONObject(
						true, _getRandomRedirectURI()
					).toString()));

			Assert.assertEquals(403, response.getStatus());

			Assert.assertEquals(
				OAuthConstants.ACCESS_DENIED, parseError(response));
		}
	}

	@Test
	public void testRegisterInOpenModeIsCreateOnly() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId())) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			Response response = invocationBuilder.method(
				"post",
				Entity.json(
					_createOpenRegistrationJSONObject(
						true, _getRandomRedirectURI()
					).toString()));

			Assert.assertEquals(201, response.getStatus());

			JSONObject responseJSONObject = parseJSONObject(response);

			Assert.assertFalse(
				responseJSONObject.has("registration_access_token"));
			Assert.assertFalse(
				responseJSONObject.has("registration_client_uri"));

			registerWebTarget = getRegisterWebTarget(
				responseJSONObject.getString(OAuthConstants.CLIENT_ID));

			invocationBuilder = registerWebTarget.request();

			invocationBuilder.header(
				"Authorization", "Bearer " + RandomTestUtil.randomString());

			response = invocationBuilder.get();

			Assert.assertEquals(401, response.getStatus());

			response = invocationBuilder.delete();

			Assert.assertEquals(401, response.getStatus());
		}
	}

	@Test
	public void testRegisterInOpenModeWithInvalidRequest() throws Exception {

		// Deny when the grant type is not allowed

		_testRegisterInOpenModeWithInvalidRequest(
			_createOpenRegistrationJSONObject(
				true, _getRandomRedirectURI()
			).toString(),
			"invalid_client_metadata", 400,
			"dynamic.registration.allowed.grant.types",
			new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT});

		// Deny when the initial access token is missing

		_testRegisterInOpenModeWithInvalidRequest(
			JSONUtil.put(
				"client_name", RandomTestUtil.randomString()
			).toString(),
			null, 401, "dynamic.registration.require.initial.access.token",
			true);

		// Deny when the redirect URI does not match the allowed patterns

		_testRegisterInOpenModeWithInvalidRequest(
			_createOpenRegistrationJSONObject(
				true, _getRandomRedirectURI()
			).toString(),
			"invalid_redirect_uri", 400,
			"dynamic.registration.allowed.redirect.uri.patterns",
			new String[] {"https://*.example.org/*"});

		// Deny when the redirect URI is blank

		_testRegisterInOpenModeWithInvalidRequest(
			_createOpenRegistrationJSONObject(
				true, StringPool.BLANK
			).toString(),
			"invalid_redirect_uri", 400,
			"dynamic.registration.allowed.redirect.uri.patterns",
			new String[] {"https://*.example.org/*"});

		// Deny when the scope is missing, even when all scopes are allowed

		_testRegisterInOpenModeWithInvalidRequest(
			_createOpenRegistrationJSONObject(
				false, _getRandomRedirectURI()
			).toString(),
			"invalid_client_metadata", 400,
			"dynamic.registration.allowed.scopes",
			new String[] {"Liferay.Headless.Delivery.everything"});
		_testRegisterInOpenModeWithInvalidRequest(
			_createOpenRegistrationJSONObject(
				false, _getRandomRedirectURI()
			).toString(),
			"invalid_client_metadata", 400,
			"dynamic.registration.allowed.scopes",
			new String[] {StringPool.STAR});

		// Deny when the scope is not allowed

		_testRegisterInOpenModeWithInvalidRequest(
			JSONUtil.put(
				"client_name", RandomTestUtil.randomString()
			).put(
				"grant_types",
				new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT}
			).put(
				"scope", "Liferay.Headless.Admin.Site.everything"
			).toString(),
			"invalid_scope", 400, "dynamic.registration.allowed.scopes",
			new String[] {"Liferay.Headless.Delivery.everything"});
	}

	@Test
	public void testRegisterInOpenModeWithoutRedirectURIs() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		String bodyJSON = JSONUtil.put(
			"client_name", RandomTestUtil.randomString()
		).put(
			"grant_types",
			new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT}
		).put(
			"scope", RandomTestUtil.randomString()
		).toString();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"dynamic.registration.allowed.grant.types",
						new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT},
						"dynamic.registration.allowed.redirect.uri.patterns",
						new String[0])) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			Response response = invocationBuilder.method(
				"post", Entity.json(bodyJSON));

			Assert.assertEquals(201, response.getStatus());

			AuditMessage auditMessage = _fetchAuditMessage(
				"DYNAMIC_REGISTRATION_ADD");

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertEquals(
				"open", additionalInfoJSONObject.getString("mode"));
		}
	}

	@Test
	public void testRegisterPromotesPublicAuthorizationCode() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		WebTarget registerWebTarget = getRegisterWebTarget();

		String bodyJSON = _createOpenRegistrationJSONObject(
			true, _getRandomRedirectURI()
		).put(
			"token_endpoint_auth_method",
			OAuthConstants.TOKEN_ENDPOINT_AUTH_NONE
		).toString();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(companyId)) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			Response response = invocationBuilder.method(
				"post", Entity.json(bodyJSON));

			Assert.assertEquals(201, response.getStatus());

			JSONObject responseJSONObject = parseJSONObject(response);

			Assert.assertEquals(
				Collections.singletonList(
					OAuthConstants.AUTHORIZATION_CODE_GRANT),
				JSONUtil.toStringList(
					responseJSONObject.getJSONArray("grant_types")));

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					companyId,
					responseJSONObject.getString(OAuthConstants.CLIENT_ID));

			Assert.assertEquals(
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				oAuth2Application.getAllowedGrantTypesList());
		}
	}

	@Test
	public void testRegisterWithAuthenticationInOpenMode() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		String bodyJSON = JSONUtil.put(
			"client_name", RandomTestUtil.randomString()
		).put(
			"grant_types",
			new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT}
		).put(
			"redirect_uris", new String[] {_getRandomRedirectURI()}
		).toString();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId())) {

			Invocation.Builder invocationBuilder = authorize(
				registerWebTarget.request(),
				_getToken(_getDynamicRegistratorOAuth2Application()));

			Response response = invocationBuilder.method(
				"post", Entity.json(bodyJSON));

			Assert.assertEquals(201, response.getStatus());

			AuditMessage auditMessage = _fetchAuditMessage(
				"DYNAMIC_REGISTRATION_ADD");

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertEquals(
				"authenticated", additionalInfoJSONObject.getString("mode"));
		}
	}

	@Test
	public void testRegisterWithInvalidBearerToken() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		Invocation.Builder invocationBuilder = registerWebTarget.request();

		invocationBuilder.header(
			"Authorization", "Bearer " + RandomTestUtil.randomString());

		Response response = invocationBuilder.method(
			"post",
			Entity.json(
				JSONUtil.put(
					"client_name", RandomTestUtil.randomString()
				).toString()));

		Assert.assertEquals(401, response.getStatus());

		AuditMessage auditMessage = _fetchAuditMessage(
			"DYNAMIC_REGISTRATION_REJECT");

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"invalid_token", additionalInfoJSONObject.getString("error"));
		Assert.assertEquals(
			"authenticated", additionalInfoJSONObject.getString("mode"));
	}

	@Test
	public void testUpdateClientRegistration() throws Exception {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				TestPropsValues.getCompanyId(), "oauthDeleteMeApplication");

		WebTarget registerWebTarget = getRegisterWebTarget(
			oAuth2Application.getClientId());

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(_getDynamicRegistratorOAuth2Application()));

		String clientName = RandomTestUtil.randomString();

		Response response = invocationBuilder.method(
			"put",
			Entity.json(
				_createAuthenticatedRegistrationJSONObject(
					clientName, RandomTestUtil.randomString()
				).toString()));

		Assert.assertEquals(200, response.getStatus());

		JSONObject jsonObject = parseJSONObject(response);

		Assert.assertEquals(clientName, jsonObject.getString("client_name"));
	}

	protected static WebTarget getRegisterWebTarget() {
		WebTarget webTarget = getOAuth2WebTarget();

		return webTarget.path("register");
	}

	protected static WebTarget getRegisterWebTarget(String target) {
		WebTarget webTarget = getRegisterWebTarget();

		return webTarget.path(target);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new DynamicRegistrationServiceTestPreparatorBundleActivator();
	}

	private JSONObject _createAuthenticatedRegistrationJSONObject(
		String clientName, String scope) {

		return JSONUtil.put(
			"client_name", clientName
		).put(
			"grant_types",
			new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT}
		).put(
			"logo_uri", RandomTestUtil.randomString()
		).put(
			"redirect_uris",
			new String[] {
				StringBundler.concat(
					Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(),
					StringPool.SLASH, RandomTestUtil.randomString()),
				StringBundler.concat(
					Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(),
					StringPool.SLASH, RandomTestUtil.randomString())
			}
		).put(
			"scope", scope
		);
	}

	private CompanyConfigurationTemporarySwapper
			_createCompanyConfigurationTemporarySwapper(
				long companyId, Object... keysAndValues)
		throws Exception {

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"dynamic.registration.allowed.grant.types",
				new String[] {StringPool.STAR}
			).put(
				"dynamic.registration.allowed.hosts",
				new String[] {StringPool.STAR}
			).put(
				"dynamic.registration.allowed.redirect.uri.patterns",
				new String[] {StringPool.STAR}
			).put(
				"dynamic.registration.allowed.scopes",
				new String[] {StringPool.STAR}
			).put(
				"dynamic.registration.require.initial.access.token", false
			).build();

		for (int i = 0; i < keysAndValues.length; i += 2) {
			properties.put((String)keysAndValues[i], keysAndValues[i + 1]);
		}

		return new CompanyConfigurationTemporarySwapper(
			companyId,
			"com.liferay.oauth2.provider.rest.internal.configuration." +
				"OAuth2DynamicRegistrationConfiguration",
			properties);
	}

	private JSONObject _createOpenRegistrationJSONObject(
		boolean includeScope, String redirectURI) {

		JSONObject jsonObject = JSONUtil.put(
			"client_name", RandomTestUtil.randomString()
		).put(
			"grant_types",
			new String[] {OAuthConstants.AUTHORIZATION_CODE_GRANT}
		).put(
			"redirect_uris", new String[] {redirectURI}
		).put(
			"response_types", new String[] {OAuthConstants.CODE_RESPONSE_TYPE}
		);

		if (includeScope) {
			jsonObject.put("scope", RandomTestUtil.randomString());
		}

		return jsonObject;
	}

	private AuditMessage _fetchAuditMessage(String eventType) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (eventType.equals(auditMessage.getEventType())) {
				return auditMessage;
			}
		}

		return null;
	}

	private OAuth2Application _getDynamicRegistratorOAuth2Application()
		throws Exception {

		DynamicQuery dynamicQuery =
			_oAuth2ApplicationLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(TestPropsValues.getCompanyId()));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		dynamicQuery.add(
			nameProperty.eq(
				OAuth2ApplicationConstants.NAME_DYNAMIC_REGISTRATOR));

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);

		Assert.assertFalse(oAuth2Applications.isEmpty());

		return oAuth2Applications.get(0);
	}

	private String _getRandomRedirectURI() {
		return StringBundler.concat(
			Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(),
			".com/callback");
	}

	private String _getToken(OAuth2Application oAuth2Application) {
		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		String tokenString = parseTokenString(
			invocationBuilder.post(
				Entity.form(
					new MultivaluedHashMap<>(
						HashMapBuilder.put(
							OAuthConstants.CLIENT_ID,
							oAuth2Application.getClientId()
						).put(
							OAuthConstants.CLIENT_SECRET,
							oAuth2Application.getClientSecret()
						).put(
							OAuthConstants.GRANT_TYPE,
							OAuthConstants.CLIENT_CREDENTIALS_GRANT
						).build()))));

		Assert.assertNotNull(tokenString);

		return tokenString;
	}

	private void _testRegisterInOpenModeEnforcesAllowedHosts(
			String allowedHost, int expectedStatus, String requestHost)
		throws Exception {

		WebTarget registerWebTarget = getRegisterWebTarget();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"dynamic.registration.allowed.hosts",
						new String[] {allowedHost},
						"dynamic.registration.trust.proxy.headers", true)) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			invocationBuilder.header("X-Forwarded-For", requestHost);

			Response response = invocationBuilder.method(
				"post",
				Entity.json(
					_createOpenRegistrationJSONObject(
						true, _getRandomRedirectURI()
					).toString()));

			Assert.assertEquals(expectedStatus, response.getStatus());

			if (expectedStatus == 403) {
				Assert.assertEquals(
					OAuthConstants.ACCESS_DENIED, parseError(response));

				AuditMessage auditMessage = _fetchAuditMessage(
					"DYNAMIC_REGISTRATION_REJECT");

				JSONObject additionalInfoJSONObject =
					auditMessage.getAdditionalInfo();

				Assert.assertEquals(
					OAuthConstants.ACCESS_DENIED,
					additionalInfoJSONObject.getString("error"));
				Assert.assertEquals(
					"open", additionalInfoJSONObject.getString("mode"));
			}
		}
	}

	private void _testRegisterInOpenModeWithInvalidRequest(
			String bodyJSON, String expectedError, int expectedStatus,
			Object... keysAndValues)
		throws Exception {

		WebTarget registerWebTarget = getRegisterWebTarget();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_createCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), keysAndValues)) {

			Invocation.Builder invocationBuilder = registerWebTarget.request();

			Response response = invocationBuilder.method(
				"post", Entity.json(bodyJSON));

			Assert.assertEquals(expectedStatus, response.getStatus());

			if (expectedError != null) {
				Assert.assertEquals(expectedError, parseError(response));
			}
		}
	}

	private List<AuditMessage> _auditMessages;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@Inject
	private UserLocalService _userLocalService;

	private class DynamicRegistrationServiceTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, user, "oauthDynamicRegisterTestApplication");
			createOAuth2Application(
				companyId, user, "oauthDeleteMeApplication");
		}

	}

}