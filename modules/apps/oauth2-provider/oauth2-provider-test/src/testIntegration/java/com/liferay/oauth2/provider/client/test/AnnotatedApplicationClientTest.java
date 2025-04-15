/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.oauth2.provider.internal.test.TestInterfaceAnnotatedApplication;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
public class AnnotatedApplicationClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			testNoScopeAnnotation("/annotated-impl/no-scope");
			testRequiresScopeAnnotation("/annotated-impl");

			testNoScopeAnnotation("/annotated-interface/no-scope");
			testRequiresScopeAnnotation("/annotated-interface");
		}
	}

	public static class AnnotatedApplicationTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			Dictionary<String, Object> properties =
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", false
				).put(
					"oauth2.scope.checker.type", "annotations"
				).build();

			registerJaxRsApplication(
				new TestInterfaceAnnotatedApplication(), "annotated-interface",
				properties);

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated-impl", properties);

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication",
				Arrays.asList(
					"everything", "everything.read", "everything.write"));
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new AnnotatedApplicationTestPreparatorBundleActivator();
	}

	protected void testNoScopeAnnotation(String path) {
		WebTarget webTarget = getWebTarget(path);

		Invocation.Builder invocationBuilder = webTarget.request();

		Response response = invocationBuilder.get();

		Assert.assertEquals(403, response.getStatus());

		invocationBuilder = authorize(
			invocationBuilder,
			getToken(
				"oauthTestApplication", null,
				getClientCredentialsResponseBiFunction(StringPool.BLANK),
				this::parseTokenString));

		invocationBuilder.get();

		Assert.assertEquals("no-scope", invocationBuilder.get(String.class));
	}

	protected void testRequiresScopeAnnotation(String path) {
		WebTarget webTarget = getWebTarget(path);

		Invocation.Builder invocationBuilder = webTarget.request();

		Response response = invocationBuilder.get();

		Assert.assertEquals(403, response.getStatus());

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				"oauthTestApplication", null,
				getClientCredentialsResponseBiFunction("everything.write"),
				this::parseTokenString));

		response = invocationBuilder.get();

		Assert.assertEquals(403, response.getStatus());

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				"oauthTestApplication", null,
				getClientCredentialsResponseBiFunction("everything.read"),
				this::parseTokenString));

		invocationBuilder.get();

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));
	}

}