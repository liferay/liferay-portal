/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.TestRunnablePostHandlingApplication;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceReference;

/**
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class TOCTOUTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	/**
	 * OAUTH2-101 / OAUTH2-102
	 */
	@Test
	public void testPreventTOCTOUWithNewScopes() {

		// Get a token (implicitly for "everything.read") and check success
		// for preinstalled JAX-RS app 1

		WebTarget webTarget1 = getWebTarget("/annotated");

		String token = getToken(
			"oauthTestApplicationCode", null,
			getAuthorizationCodeBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseTokenString);

		Invocation.Builder webTarget1InvocationBuilder = authorize(
			webTarget1.request(), token);

		Assert.assertEquals(
			"everything.read", webTarget1InvocationBuilder.get(String.class));

		// Install JAX-RS app 2

		webTarget1InvocationBuilder.post(null, String.class);

		// Fail to use the token from [1] on JAX-RS app 2 (admin & end-user
		// TOCTOU protection when API grows)

		WebTarget webTarget2 = getWebTarget("/annotated2");

		Invocation.Builder webTarget2InvocationBuilder = authorize(
			webTarget2.request(), token);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			webTarget2InvocationBuilder.get(String.class);

			Assert.fail(
				"Expected request GET /annotated2 to fail through admin & " +
					"end-user TOCTOU protection");
		}
		catch (ClientErrorException clientErrorException) {
			Response response = clientErrorException.getResponse();

			Assert.assertEquals(403, response.getStatus());
		}

		// Try again with a fresh narrowed down token for "everything.read".
		// It should still fail (admin TOCTOU protection when narrowing down).

		webTarget2InvocationBuilder = authorize(
			webTarget2.request(),
			getToken(
				"oauthTestApplicationCode", null,
				getAuthorizationCodeBiFunction(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null, "everything.read"),
				this::parseTokenString));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			webTarget2InvocationBuilder.get(String.class);

			Assert.fail(
				"Expected request GET /annotated2 to fail through admin " +
					"TOCTOU protection");
		}
		catch (ClientErrorException clientErrorException) {
			Response response = clientErrorException.getResponse();

			Assert.assertEquals(403, response.getStatus());
		}

		// Resave the OAuth2 app scope assignment

		webTarget2InvocationBuilder.post(null, String.class);

		// Fail to use the token from [4] on JAX-RS app 2 (end-user TOCTOU
		// protection when OAuth2 app scope assignment grows)

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			webTarget2InvocationBuilder.get(String.class);

			Assert.fail(
				"Expected request GET /annotated2 to fail through end-user " +
					"TOCTOU protection");
		}
		catch (ClientErrorException clientErrorException) {
			Response response = clientErrorException.getResponse();

			Assert.assertEquals(403, response.getStatus());
		}

		// Try again with a fresh token (implicitly for "everything.read"). It
		// should succeed.

		webTarget2InvocationBuilder = authorize(
			webTarget2.request(),
			getToken(
				"oauthTestApplicationCode", null,
				getAuthorizationCodeBiFunction(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null),
				this::parseTokenString));

		Assert.assertEquals(
			"everything.read", webTarget2InvocationBuilder.get(String.class));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new SecurityTestPreparatorBundleActivator();
	}

	private User _user;

	private class SecurityTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		public OAuth2Application updateOAuth2ApplicationScopeAliases(
				OAuth2Application oAuth2Application)
			throws PortalException {

			ServiceReference<OAuth2ApplicationLocalService>
				oAuth2ApplicationLocalServiceServiceReference =
					bundleContext.getServiceReference(
						OAuth2ApplicationLocalService.class);

			OAuth2ApplicationLocalService oAuth2ApplicationLocalService =
				bundleContext.getService(
					oAuth2ApplicationLocalServiceServiceReference);

			ServiceReference<OAuth2ApplicationScopeAliasesLocalService>
				oAuth2AScopeAliasesLocalServiceServiceReference =
					bundleContext.getServiceReference(
						OAuth2ApplicationScopeAliasesLocalService.class);

			OAuth2ApplicationScopeAliasesLocalService
				oAuth2ApplicationScopeAliasesLocalService =
					bundleContext.getService(
						oAuth2AScopeAliasesLocalServiceServiceReference);

			try {
				return oAuth2ApplicationLocalService.updateScopeAliases(
					oAuth2Application.getUserId(),
					oAuth2Application.getUserName(),
					oAuth2Application.getOAuth2ApplicationId(),
					oAuth2ApplicationScopeAliasesLocalService.
						getScopeAliasesList(
							oAuth2Application.
								getOAuth2ApplicationScopeAliasesId()));
			}
			finally {
				bundleContext.ungetService(
					oAuth2ApplicationLocalServiceServiceReference);

				bundleContext.ungetService(
					oAuth2AScopeAliasesLocalServiceServiceReference);
			}
		}

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			OAuth2Application oAuth2Application = createOAuth2Application(
				companyId, _user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything.read"));

			Application application = new TestRunnablePostHandlingApplication(
				() -> {
					try {
						updateOAuth2ApplicationScopeAliases(oAuth2Application);
					}
					catch (PortalException portalException) {
						throw new RuntimeException(portalException);
					}
				});

			Dictionary<String, Object> properties =
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "annotations"
				).build();

			registerJaxRsApplication(
				new TestRunnablePostHandlingApplication(
					() -> registerJaxRsApplication(
						application, "annotated2", properties)),
				"annotated", properties);

			updateOAuth2ApplicationScopeAliases(oAuth2Application);
		}

	}

}