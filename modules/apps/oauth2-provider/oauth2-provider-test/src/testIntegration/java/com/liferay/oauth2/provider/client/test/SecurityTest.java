/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.url.provider.DLFileVersionURLProvider;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.awt.image.BufferedImage;

import java.net.URI;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceRegistration;

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
	public void testEscapeOAuth2ApplicationName() {
		_assertAuthorizationPageEscapesInjectedScript(
			webTarget -> webTarget.queryParam(
				"client_id", _CLIENT_ID_UNESCAPED_NAME
			).queryParam(
				"response_type", "code"
			));
	}

	@Test
	public void testEscapeOAuth2ApplicationThumbnailURL() throws Exception {
		_assertThumbnailURLIsEscaped(
			_getAuthorizationPageBodyString(
				webTarget -> webTarget.queryParam(
					"client_id", _CLIENT_ID_THUMBNAIL_URL
				).queryParam(
					"response_type", "code"
				)));
		_assertThumbnailURLIsEscaped(_getConnectedApplicationPageBodyString());
	}

	@Test
	public void testEscapeOAuth2ScopeDescription() {
		_assertAuthorizationPageEscapesInjectedScript(
			webTarget -> webTarget.queryParam(
				"client_id", _CLIENT_ID_UNESCAPED_SCOPE
			).queryParam(
				"response_type", "code"
			).queryParam(
				"scope", "Liferay.Captcha.REST.everything.read"
			));
	}

	@Test
	public void testGuestOwnerCreateTokenPermission() {
		Assert.assertEquals(
			"invalid_grant",
			getToken(
				_CLIENT_ID_DEFAULT_USER, null,
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
							"client_id", _CLIENT_ID_CODE
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
							"client_id", _CLIENT_ID_CODE
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
						"client_id", _CLIENT_ID_CODE_PKCE
					).queryParam(
						"code_challenge", "correctCodeChallenge"
					).queryParam(
						"response_type", "code"
					))));

		Assert.assertNotNull(authorizationCode);

		Assert.assertEquals(
			"invalid_grant",
			getToken(
				_CLIENT_ID_CODE_PKCE, null,
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
						"client_id", _CLIENT_ID_CODE
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
					"client_id", _CLIENT_ID_CODE
				).queryParam(
					"redirect_uri",
					"http://invalid:" + PortalUtil.getPortalServerPort(false)
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
						"client_id", _CLIENT_ID_CODE
					).queryParam(
						"redirect_uri",
						"http://redirecturi:" +
							PortalUtil.getPortalServerPort(false)
					).queryParam(
						"response_type", "code"
					))));

		Assert.assertNotNull(authorizationCode);

		Assert.assertEquals(
			"invalid_grant",
			getToken(
				_CLIENT_ID_CODE, null,
				getExchangeAuthorizationCodeBiFunction(
					authorizationCode,
					"http://invalid:" + PortalUtil.getPortalServerPort(false)),
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

	private void _assertAuthorizationPageEscapesInjectedScript(
		Function<WebTarget, WebTarget> authorizeRequestFunction) {

		String bodyString = _getAuthorizationPageBodyString(
			authorizeRequestFunction);

		Assert.assertFalse(bodyString.contains(_INJECTED_SCRIPT));
		Assert.assertTrue(
			bodyString.contains(HtmlUtil.escape(_INJECTED_SCRIPT)));
	}

	private void _assertThumbnailURLIsEscaped(String bodyString) {
		Assert.assertTrue(
			bodyString.contains(
				"src=\"http://localhost/documents/1/2/icon.png?version=1.0" +
					"&amp;t=1&amp;imageThumbnail=1&#39;\""));
	}

	private String _getAuthorizationPageBodyString(
		Function<WebTarget, WebTarget> authorizeRequestFunction) {

		Function<WebTarget, Invocation.Builder> invocationBuilderFunction =
			getAuthenticatedInvocationBuilderFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null);

		Response response = getCodeFunction(
			authorizeRequestFunction, true
		).apply(
			invocationBuilderFunction
		);

		URI uri = response.getLocation();

		if (uri == null) {
			throw new IllegalArgumentException(
				"Authorization service response missing \"Location\" header " +
					"from which the authorization page URL is extracted");
		}

		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path(uri.getPath());

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			uri.getRawQuery());

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			webTarget = webTarget.queryParam(
				entry.getKey(), (Object[])entry.getValue());
		}

		Invocation.Builder invocationBuilder = invocationBuilderFunction.apply(
			webTarget);

		return getBodyAsString(invocationBuilder.get());
	}

	private String _getConnectedApplicationPageBodyString() throws Exception {
		URI uri = new URI(
			PortalUtil.getControlPanelFullURL(
				TestPropsValues.getGroupId(),
				_CONNECTED_APPLICATIONS_PORTLET_ID,
				HashMapBuilder.put(
					"_" + _CONNECTED_APPLICATIONS_PORTLET_ID +
						"_mvcRenderCommandName",
					new String[] {
						"/oauth2_provider/view_connected_applications"
					}
				).put(
					"_" + _CONNECTED_APPLICATIONS_PORTLET_ID +
						"_oAuth2ApplicationId",
					new String[] {String.valueOf(_oAuth2ApplicationId)}
				).put(
					"_" + _CONNECTED_APPLICATIONS_PORTLET_ID +
						"_oAuth2AuthorizationId",
					new String[] {String.valueOf(_oAuth2AuthorizationId)}
				).build()));

		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path(uri.getPath());

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			uri.getRawQuery());

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			webTarget = webTarget.queryParam(
				entry.getKey(), (Object[])entry.getValue());
		}

		Invocation.Builder invocationBuilder =
			getAuthenticatedInvocationBuilderFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null
			).apply(
				webTarget
			);

		return getBodyAsString(invocationBuilder.get());
	}

	private static final String _CLIENT_ID_CODE = RandomTestUtil.randomString();

	private static final String _CLIENT_ID_CODE_PKCE =
		RandomTestUtil.randomString();

	private static final String _CLIENT_ID_DEFAULT_USER =
		RandomTestUtil.randomString();

	private static final String _CLIENT_ID_THUMBNAIL_URL =
		RandomTestUtil.randomString();

	private static final String _CLIENT_ID_UNESCAPED_NAME =
		RandomTestUtil.randomString();

	private static final String _CLIENT_ID_UNESCAPED_SCOPE =
		RandomTestUtil.randomString();

	private static final String _CONNECTED_APPLICATIONS_PORTLET_ID =
		"com_liferay_oauth2_provider_web_internal_portlet_" +
			"OAuth2ConnectedApplicationsPortlet";

	private static final String _INJECTED_SCRIPT = "<script>alert(1)</script>";

	private long _oAuth2ApplicationId;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private long _oAuth2AuthorizationId;

	@Inject
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	private User _user;

	private class SecurityTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, _CLIENT_ID_CODE,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));

			createOAuth2ApplicationWithNone(
				companyId, _user, _CLIENT_ID_CODE_PKCE,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList(
					"http://redirecturi:" +
						PortalUtil.getPortalServerPort(false)),
				false, Collections.singletonList("everything"), false);

			Company company = CompanyLocalServiceUtil.getCompany(companyId);

			createOAuth2Application(
				companyId, company.getGuestUser(), _CLIENT_ID_DEFAULT_USER);

			_createThumbnailURLOAuth2Application(companyId);

			createOAuth2Application(
				companyId, _user, _CLIENT_ID_UNESCAPED_NAME,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				_INJECTED_SCRIPT, Collections.singletonList("everything"));

			OAuth2Application oAuth2Application = createOAuth2Application(
				companyId, _user, _CLIENT_ID_UNESCAPED_SCOPE,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList(
					"Liferay.Captcha.REST.everything.read"));

			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				companyId,
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				"Liferay.Captcha.REST", "com.liferay.captcha.rest.impl", "GET",
				Collections.singletonList(
					"Liferay.Captcha.REST.everything.read"));

			registerScopeDescriptor(
				(scope, locale) -> _INJECTED_SCRIPT,
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.name", "Liferay.Captcha.REST"
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());
		}

		private void _createThumbnailURLOAuth2Application(long companyId)
			throws Exception {

			OAuth2Application oAuth2Application = createOAuth2Application(
				companyId, _user, _CLIENT_ID_THUMBNAIL_URL,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));

			_oAuth2ApplicationLocalService.updateIcon(
				oAuth2Application.getOAuth2ApplicationId(),
				new UnsyncByteArrayInputStream(
					ImageToolUtil.getBytes(
						new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB),
						"png")));

			ServiceRegistration<DLFileVersionURLProvider> serviceRegistration =
				bundleContext.registerService(
					DLFileVersionURLProvider.class,
					new DLFileVersionURLProvider() {

						@Override
						public List<Type> getTypes() {
							return Collections.singletonList(Type.THUMBNAIL);
						}

						@Override
						public String getURL(
							FileVersion fileVersion,
							ThemeDisplay themeDisplay) {

							return "http://localhost/documents/1/2/icon.png?" +
								"version=1.0&t=1&imageThumbnail=1'";
						}

					},
					HashMapDictionaryBuilder.<String, Object>put(
						"service.ranking", Integer.MAX_VALUE
					).build());

			autoCloseables.add(serviceRegistration::unregister);

			OAuth2Authorization oAuth2Authorization = addOAuth2Authorization(
				companyId, _user, oAuth2Application,
				RandomTestUtil.randomString(), new Date(),
				new Date(System.currentTimeMillis() + Time.HOUR));

			_oAuth2ApplicationId = oAuth2Application.getOAuth2ApplicationId();
			_oAuth2AuthorizationId =
				oAuth2Authorization.getOAuth2AuthorizationId();
		}

	}

}