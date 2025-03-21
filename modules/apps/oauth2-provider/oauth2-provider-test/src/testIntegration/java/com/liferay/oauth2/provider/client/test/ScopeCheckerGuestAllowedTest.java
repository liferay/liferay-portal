/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ProviderConstants;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.oauth2.provider.internal.test.TestApplication;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class ScopeCheckerGuestAllowedTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		testApplication("/annotated-guest-allowed/", "everything.read", 200);

		testApplication("/annotated-guest-allowed/no-scope", "no-scope", 200);

		testApplication("/annotated-guest-default/", "everything.read", 200);

		testApplication("/annotated-guest-default/no-scope", "no-scope", 200);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			testApplication(
				"/annotated-guest-not-allowed/", "everything.read", 403);

			testApplication(
				"/annotated-guest-not-allowed/no-scope", "no-scope", 403);
		}

		testApplication("/default-jaxrs-app-guest-allowed/", "get", 200);

		testApplication("/default-jaxrs-app-guest-default/", "get", 200);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			testApplication(
				"/default-jaxrs-app-guest-not-allowed/", "get", 403);
		}

		testApplication("/methods-guest-allowed/", "get", 200);

		testApplication("/methods-guest-default/", "get", 200);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			testApplication("/methods-guest-not-allowed/", "get", 403);
		}
	}

	public static class ScopeCheckerGuestAllowedTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated-guest-allowed",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", true
				).put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated-guest-default",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated-guest-not-allowed",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", false
				).put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			registerJaxRsApplication(
				new TestApplication(), "default-jaxrs-app-guest-allowed",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", true
				).build());

			registerJaxRsApplication(
				new TestApplication(), "default-jaxrs-app-guest-default",
				new HashMapDictionary<>());

			registerJaxRsApplication(
				new TestApplication(), "default-jaxrs-app-guest-not-allowed",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", false
				).build());

			registerJaxRsApplication(
				new TestApplication(), "methods-guest-allowed",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", true
				).put(
					"oauth2.scope.checker.type", "http.method"
				).build());

			registerJaxRsApplication(
				new TestApplication(), "methods-guest-default",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "http.method"
				).build());

			registerJaxRsApplication(
				new TestApplication(), "methods-guest-not-allowed",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", false
				).put(
					"oauth2.scope.checker.type", "http.method"
				).build());

			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication",
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				Arrays.asList("everything.read", "GET"));

			createServiceAccessProfile(
				user.getUserId(), "#get*", true, true, "GUEST_OAUTH2_TEST");
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new ScopeCheckerGuestAllowedTestPreparatorBundleActivator();
	}

	protected void testApplication(
		String path, String expectedValidTokenResponse,
		int expectedNoTokenStatus) {

		WebTarget webTarget = getWebTarget(path);

		Invocation.Builder invocationBuilder = webTarget.request();

		Response response = invocationBuilder.get();

		Assert.assertEquals(
			"No token: ", expectedNoTokenStatus, response.getStatus());

		for (String invalidToken : _INVALID_TOKENS) {
			invocationBuilder = webTarget.request();

			if (invalidToken != null) {
				invocationBuilder = authorize(invocationBuilder, invalidToken);
			}

			response = invocationBuilder.get();

			Assert.assertEquals(
				"Token: " + invalidToken, 401, response.getStatus());
		}

		invocationBuilder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Assert.assertEquals(
			expectedValidTokenResponse, invocationBuilder.get(String.class));
	}

	private static final String[] _INVALID_TOKENS = {
		OAuth2ProviderConstants.EXPIRED_TOKEN, StringPool.BLANK,
		StringPool.NULL, "Invalid Token"
	};

}