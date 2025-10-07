/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

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
public class RememberDeviceApplicationClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCookieResponseApplicationCode() {
		String applicationClientId = "oauthTestApplicationCode";

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response.getCookies();

		Assert.assertNull(
			newCookies.get(_COOKIE_NAME_PREFIX.concat(applicationClientId)));
	}

	@Test
	public void testCookieResponseApplicationCodePKCE() {
		String applicationClientId = "oauthTestApplicationCodePKCE";

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge",
					generateCodeChallenge(RandomTestUtil.randomString())
				).queryParam(
					"response_type", "code"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response.getCookies();

		Assert.assertNull(
			newCookies.get(_COOKIE_NAME_PREFIX.concat(applicationClientId)));
	}

	@Test
	public void testRememberApplicationCode() {
		String applicationClientId = "oauthTestRememberApplicationCode";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response.getCookies();

		NewCookie newCookie1 = newCookies.get(cookieName);

		Assert.assertNotNull(newCookie1);

		String authorizationCodeString = parseAuthorizationCodeString(response);

		Assert.assertNotNull(authorizationCodeString);

		getToken(
			applicationClientId, null,
			(clientId, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", applicationClientId);
				formData.add("client_secret", "oauthTestApplicationSecret");
				formData.add("code", authorizationCodeString);
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				null, true),
			invocationBuilder -> invocationBuilder.cookie(
				cookieName, newCookie1.getValue()));

		Assert.assertNotNull(parseAuthorizationCodeString(response));

		newCookies = response.getCookies();

		NewCookie newCookie2 = newCookies.get(cookieName);

		Assert.assertNotEquals(newCookie1.getValue(), newCookie2.getValue());
	}

	@Test
	public void testRememberApplicationCodePKCE() {
		String applicationClientId = "oauthTestRememberApplicationCodePKCE";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		String codeVerifierString = RandomTestUtil.randomString();

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", generateCodeChallenge(codeVerifierString)
				).queryParam(
					"response_type", "code"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response.getCookies();

		NewCookie newCookie1 = newCookies.get(cookieName);

		Assert.assertNotNull(newCookie1);

		String authorizationCodeString = parseAuthorizationCodeString(response);

		Assert.assertNotNull(authorizationCodeString);

		getToken(
			applicationClientId, null,
			(clientId, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", clientId);
				formData.add("code", authorizationCodeString);
				formData.add("code_verifier", codeVerifierString);
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", codeVerifierString
				).queryParam(
					"response_type", "code"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				),
				true),
			invocationBuilder -> invocationBuilder.cookie(
				cookieName, newCookie1.getValue()));

		Assert.assertNotNull(parseAuthorizationCodeString(response));

		newCookies = response.getCookies();

		NewCookie newCookie2 = newCookies.get(cookieName);

		Assert.assertNotEquals(newCookie1.getValue(), newCookie2.getValue());
	}

	@Test
	public void testRequestTokenInvalidatePreviousTokenRememberApplicationCode() {
		String applicationClientId = "oauthTestRememberApplicationCode";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		Response response1 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response1.getCookies();

		NewCookie newCookie = newCookies.get(cookieName);

		String token = getToken(
			applicationClientId, null,
			(client, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", applicationClientId);
				formData.add("client_secret", "oauthTestApplicationSecret");
				formData.add("code", parseAuthorizationCodeString(response1));
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		Assert.assertNotNull(token);

		Response response2 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true),
			invocationBuilder -> invocationBuilder.cookie(
				cookieName, newCookie.getValue()));

		getToken(
			applicationClientId, null,
			(client, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", applicationClientId);
				formData.add("client_secret", "oauthTestApplicationSecret");
				formData.add("code", parseAuthorizationCodeString(response2));
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		Assert.assertNull(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(token));
	}

	@Test
	public void testRequestTokenInvalidatePreviousTokenRememberApplicationCodePKCE() {
		String applicationClientId = "oauthTestRememberApplicationCodePKCE";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		String codeVerifierString = RandomTestUtil.randomString();

		Response response1 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", generateCodeChallenge(codeVerifierString)
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response1.getCookies();

		NewCookie newCookie = newCookies.get(cookieName);

		String token = getToken(
			applicationClientId, null,
			(clientId, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", clientId);
				formData.add("code", parseAuthorizationCodeString(response1));
				formData.add("code_verifier", codeVerifierString);
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		Assert.assertNotNull(token);

		Response response2 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", generateCodeChallenge(codeVerifierString)
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true),
			invocationBuilder -> invocationBuilder.cookie(
				cookieName, newCookie.getValue()));

		getToken(
			applicationClientId, null,
			(clientId, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", applicationClientId);
				formData.add("client_secret", "oauthTestApplicationSecret");
				formData.add("code", parseAuthorizationCodeString(response2));
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		Assert.assertNull(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(token));
	}

	@Test
	public void testRevokeTokenInvalidateCookieRememberApplicationCode()
		throws PortalException {

		String applicationClientId = "oauthTestRememberApplicationCode";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response.getCookies();

		NewCookie newCookie = newCookies.get(cookieName);

		revokeOAuth2AuthorizationByAccessToken(
			getToken(
				applicationClientId, null,
				(clientId, tokenInvocationBuilder) -> {
					MultivaluedMap<String, String> formData =
						new MultivaluedHashMap<>();

					formData.add("client_id", applicationClientId);
					formData.add("client_secret", "oauthTestApplicationSecret");
					formData.add(
						"code", parseAuthorizationCodeString(response));
					formData.add("grant_type", "authorization_code");

					return tokenInvocationBuilder.post(Entity.form(formData));
				},
				this::parseTokenString));

		Assert.assertNull(
			parseAuthorizationCodeString(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", applicationClientId
						).queryParam(
							"redirect_uri", "http://redirecturi:8080"
						).queryParam(
							"response_type", "code"
						),
						true),
					invocationBuilder -> invocationBuilder.cookie(
						cookieName, newCookie.getValue()))));
	}

	@Test
	public void testRevokeTokenInvalidateCookieRememberApplicationCodePKCE()
		throws PortalException {

		String applicationClientId = "oauthTestRememberApplicationCodePKCE";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		String codeVerifierString = RandomTestUtil.randomString();

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", generateCodeChallenge(codeVerifierString)
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response.getCookies();

		NewCookie newCookie = newCookies.get(cookieName);

		revokeOAuth2AuthorizationByAccessToken(
			getToken(
				applicationClientId, null,
				(clientId, tokenInvocationBuilder) -> {
					MultivaluedMap<String, String> formData =
						new MultivaluedHashMap<>();

					formData.add("client_id", clientId);
					formData.add(
						"code", parseAuthorizationCodeString(response));
					formData.add("code_verifier", codeVerifierString);
					formData.add("grant_type", "authorization_code");

					return tokenInvocationBuilder.post(Entity.form(formData));
				},
				this::parseTokenString));

		Assert.assertNull(
			parseAuthorizationCodeString(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", applicationClientId
						).queryParam(
							"code_challenge",
							generateCodeChallenge(codeVerifierString)
						).queryParam(
							"redirect_uri", "http://redirecturi:8080"
						).queryParam(
							"response_type", "code"
						),
						true),
					invocationBuilder -> invocationBuilder.cookie(
						cookieName, newCookie.getValue()))));
	}

	@Test
	public void testSingleUseCookieRememberApplicationCode() {
		String applicationClientId = "oauthTestRememberApplicationCode";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		Response response1 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget1 -> webTarget1.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response1.getCookies();

		NewCookie newCookie = newCookies.get(cookieName);

		getToken(
			applicationClientId, null,
			(client, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData1 =
					new MultivaluedHashMap<>();

				formData1.add("client_id", applicationClientId);
				formData1.add("client_secret", "oauthTestApplicationSecret");
				formData1.add("code", parseAuthorizationCodeString(response1));
				formData1.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData1));
			},
			this::parseTokenString);

		Response response2 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true),
			invocationBuilder -> invocationBuilder.cookie(
				cookieName, newCookie.getValue()));

		Assert.assertNotNull(
			getToken(
				applicationClientId, null,
				(client, tokenInvocationBuilder) -> {
					MultivaluedMap<String, String> formData =
						new MultivaluedHashMap<>();

					formData.add("client_id", applicationClientId);
					formData.add("client_secret", "oauthTestApplicationSecret");
					formData.add(
						"code", parseAuthorizationCodeString(response2));
					formData.add("grant_type", "authorization_code");

					return tokenInvocationBuilder.post(Entity.form(formData));
				},
				this::parseTokenString));

		Assert.assertNull(
			parseAuthorizationCodeString(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", applicationClientId
						).queryParam(
							"redirect_uri", "http://redirecturi:8080"
						).queryParam(
							"response_type", "code"
						),
						null, true),
					invocationBuilder -> invocationBuilder.cookie(
						cookieName, newCookie.getValue()))));
	}

	@Test
	public void testSingleUseCookieRememberApplicationCodePKCE() {
		String applicationClientId = "oauthTestRememberApplicationCodePKCE";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		String codeVerifierString = RandomTestUtil.randomString();

		Response response1 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget1 -> webTarget1.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", generateCodeChallenge(codeVerifierString)
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> newCookies = response1.getCookies();

		NewCookie newCookie = newCookies.get(cookieName);

		getToken(
			applicationClientId, null,
			(clientId, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", clientId);
				formData.add("code", parseAuthorizationCodeString(response1));
				formData.add("code_verifier", codeVerifierString);
				formData.add("grant_type", "authorization_code");

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		Response response2 = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"code_challenge", codeVerifierString
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true),
			invocationBuilder -> invocationBuilder.cookie(
				cookieName, newCookie.getValue()));

		String authorizationCodeString = parseAuthorizationCodeString(
			response2);

		Assert.assertNotNull(authorizationCodeString);

		Assert.assertNotNull(
			getToken(
				applicationClientId, null,
				(clientId, tokenInvocationBuilder) -> {
					MultivaluedMap<String, String> formData =
						new MultivaluedHashMap<>();

					formData.add("client_id", clientId);
					formData.add("code", authorizationCodeString);
					formData.add("code_verifier", codeVerifierString);
					formData.add("grant_type", "authorization_code");

					return tokenInvocationBuilder.post(Entity.form(formData));
				},
				this::parseTokenString));

		Assert.assertNull(
			parseAuthorizationCodeString(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", applicationClientId
						).queryParam(
							"code_challenge", codeVerifierString
						).queryParam(
							"redirect_uri", "http://redirecturi:8080"
						).queryParam(
							"response_type", "code"
						),
						true),
					invocationBuilder -> invocationBuilder.cookie(
						cookieName, newCookie.getValue()))));
	}

	@Test
	public void testUseExistingDifferentCookieRememberApplicationCode() {
		String applicationClientId = "oauthTestRememberApplicationCode";

		String cookieName = _COOKIE_NAME_PREFIX.concat(applicationClientId);

		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", applicationClientId
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				_getExtraParameters(), false));

		Map<String, NewCookie> cookies = response.getCookies();

		NewCookie newCookie = cookies.get(cookieName);

		String authorizationCodeString = parseAuthorizationCodeString(response);

		getToken(
			applicationClientId, null,
			(clientId, tokenInvocationBuilder) -> {
				MultivaluedMap<String, String> formData =
					new MultivaluedHashMap<>();

				formData.add("client_id", applicationClientId);
				formData.add("client_secret", "oauthTestApplicationSecret");
				formData.add("grant_type", "authorization_code");
				formData.add("code", authorizationCodeString);

				return tokenInvocationBuilder.post(Entity.form(formData));
			},
			this::parseTokenString);

		String applicationClientIdPKCE = "oauthTestRememberApplicationCodePKCE";

		Assert.assertNull(
			parseAuthorizationCodeString(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", applicationClientIdPKCE
						).queryParam(
							"code_challenge", RandomTestUtil.randomString()
						).queryParam(
							"response_type", "code"
						).queryParam(
							"redirect_uri", "http://redirecturi:8080"
						),
						true),
					invocationBuilder -> invocationBuilder.cookie(
						cookieName, newCookie.getValue()))));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new RememberApplicationClientTestPreparatorBundleActivator();
	}

	private MultivaluedMap<String, String> _getExtraParameters() {
		MultivaluedMap<String, String> multivaluedMap =
			new MultivaluedHashMap<>();

		multivaluedMap.add(
			"_com_liferay_oauth2_provider_web_internal_portlet_" +
				"OAuth2AuthorizePortlet_rememberDevice",
			StringPool.TRUE);

		return multivaluedMap;
	}

	private static final String _COOKIE_NAME_PREFIX = "OAUTH2_REMEMBER_DEVICE_";

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	private User _user;

	private class RememberApplicationClientTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), false,
				Collections.singletonList("everything"), false);
			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), false);
			createOAuth2Application(
				companyId, _user, "oauthTestRememberApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), true,
				Collections.singletonList("everything"), false);
			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestRememberApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), true,
				Collections.singletonList("everything"), false);
		}

	}

}