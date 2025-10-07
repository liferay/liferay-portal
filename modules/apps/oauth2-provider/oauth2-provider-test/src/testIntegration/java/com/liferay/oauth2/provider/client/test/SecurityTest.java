/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.core.Response;

import java.net.URI;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class SecurityTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGuestOwnerCreateTokenPermission() {
		Assert.assertEquals(
			"invalid_grant",
			getToken(
				"oauthTestApplicationDefaultUser", null,
				this::getClientCredentialsResponse, this::parseError));
	}

	/**
	 * OAUTH2-99
	 */
	@Test
	public void testPreventClickJacking() {
		Assert.assertEquals(
			"SAMEORIGIN",
			parseXFrameOptionsHeader(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", "oauthTestApplicationCode"
						).queryParam(
							"response_type", "code"
						)))));
	}

	/**
	 * OAUTH2-96
	 */
	@Ignore
	@Test
	public void testPreventCSRFUsingMandatoryStateParam() {
		Assert.assertEquals(
			"invalid_request",
			parseErrorParameter(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", "oauthTestApplicationCode"
						).queryParam(
							"response_type", "code"
						)))));
	}

	/**
	 * OAUTH2-96
	 */
	@Test
	public void testPreventCSRFUsingPKCE() {
		String authorizationCode = parseAuthorizationCodeString(
			getCodeResponse(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null,
				getCodeFunction(
					webTarget -> webTarget.queryParam(
						"client_id", "oauthTestApplicationCodePKCE"
					).queryParam(
						"code_challenge", "correctCodeChallenge"
					).queryParam(
						"response_type", "code"
					))));

		Assert.assertNotNull(authorizationCode);

		Assert.assertEquals(
			"invalid_grant",
			getToken(
				"oauthTestApplicationCodePKCE", null,
				getExchangeAuthorizationCodePKCEBiFunction(
					authorizationCode, null, "wrongCodeVerifier"),
				this::parseError));
	}

	/**
	 * OAUTH2-96
	 */
	@Test
	public void testPreventCSRFUsingStateParam() {
		String state = "csrf_token";

		String responseState = parseStateString(
			getCodeResponse(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null,
				getCodeFunction(
					webTarget -> webTarget.queryParam(
						"client_id", "oauthTestApplicationCode"
					).queryParam(
						"response_type", "code"
					).queryParam(
						"state", state
					))));

		Assert.assertEquals(state, responseState);
	}

	/**
	 * OAUTH2-97
	 */
	@Test
	public void testPreventOpenRedirect() {
		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", "oauthTestApplicationCode"
				).queryParam(
					"redirect_uri", "http://invalid:8080"
				).queryParam(
					"response_type", "code"
				)));

		Assert.assertEquals(400, getStatus(response));
		Assert.assertEquals(
			"{\"error\":\"invalid_request\",\"error_description\":\"Client " +
				"Redirect Uri is invalid\"}",
			getBodyAsString(response));
	}

	@Test
	public void testRedirectUriMustMatch() {
		String authorizationCode = parseAuthorizationCodeString(
			getCodeResponse(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null,
				getCodeFunction(
					webTarget -> webTarget.queryParam(
						"client_id", "oauthTestApplicationCode"
					).queryParam(
						"redirect_uri", "http://redirecturi:8080"
					).queryParam(
						"response_type", "code"
					))));

		Assert.assertNotNull(authorizationCode);

		Assert.assertEquals(
			"invalid_grant",
			getToken(
				"oauthTestApplicationCode", null,
				getExchangeAuthorizationCodeBiFunction(
					authorizationCode, "http://invalid:8080"),
				this::parseError));
	}

	protected String getBodyAsString(Response response) {
		return response.readEntity(String.class);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new SecurityTestPreparatorBundleActivator();
	}

	protected int getStatus(Response response) {
		return response.getStatus();
	}

	protected String parseStateString(Response response) {
		URI uri = response.getLocation();

		if (uri == null) {
			throw new IllegalArgumentException(
				"Authorization service response missing \"Location\" header " +
					"from which state is extracted");
		}

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			uri.getQuery());

		if (!parameterMap.containsKey("state")) {
			return null;
		}

		return parameterMap.get("state")[0];
	}

	protected String parseXFrameOptionsHeader(Response response) {
		return response.getHeaderString("x-frame-options");
	}

	private User _user;

	private class SecurityTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));

			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), false);

			Company company = CompanyLocalServiceUtil.getCompany(companyId);

			createOAuth2Application(
				companyId, company.getGuestUser(),
				"oauthTestApplicationDefaultUser");
		}

	}

}