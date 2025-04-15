/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

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
public class TokenExpeditionTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", "");
		formData.add("client_secret", "");
		formData.add("grant_type", "client_credentials");

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.oauth2.provider.rest.internal.endpoint.liferay." +
					"LiferayOAuthDataProvider",
				LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper",
				LoggerTestUtil.WARN);
			LogCapture logCapture3 = LoggerTestUtil.configureLog4JLogger(
				"org.apache.cxf.rs.security.oauth2.services." +
					"AbstractOAuthService",
				LoggerTestUtil.WARN)) {

			Response response = invocationBuilder.post(Entity.form(formData));

			Assert.assertEquals(401, response.getStatus());

			formData = new MultivaluedHashMap<>();

			formData.add("client_id", "");
			formData.add("client_secret", "wrong");
			formData.add("grant_type", "client_credentials");

			String errorString = parseError(
				invocationBuilder.post(Entity.form(formData)));

			Assert.assertEquals("invalid_client", errorString);

			formData = new MultivaluedHashMap<>();

			formData.add("client_id", "oauthTestApplication");
			formData.add("client_secret", "");
			formData.add("grant_type", "client_credentials");

			response = invocationBuilder.post(Entity.form(formData));

			Assert.assertEquals(401, response.getStatus());

			formData = new MultivaluedHashMap<>();

			formData.add("client_id", "oauthTestApplication");
			formData.add("client_secret", "wrong");
			formData.add("grant_type", "client_credentials");

			errorString = parseError(
				invocationBuilder.post(Entity.form(formData)));

			Assert.assertEquals("invalid_client", errorString);

			formData = new MultivaluedHashMap<>();

			formData.add("client_id", "wrong");
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "client_credentials");

			errorString = parseError(
				invocationBuilder.post(Entity.form(formData)));

			Assert.assertEquals("invalid_client", errorString);
		}

		formData = new MultivaluedHashMap<>();

		formData.add("client_id", "oauthTestApplication");
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "client_credentials");

		WebTarget webTarget = getWebTarget("/annotated");

		String tokenString = parseTokenString(
			invocationBuilder.post(Entity.form(formData)));

		invocationBuilder = authorize(webTarget.request(), tokenString);

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		invocationBuilder = webTarget.request(
		).header(
			"Authorization", "Bearer "
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Response response = invocationBuilder.get();

			Assert.assertEquals(401, response.getStatus());

			invocationBuilder = webTarget.request(
			).header(
				"Authorization", "Bearer wrong"
			);

			response = invocationBuilder.get();

			Assert.assertEquals(401, response.getStatus());
		}
	}

	public static class TokenExpeditionTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated",
				HashMapDictionaryBuilder.<String, Object>put(
					"auth.verifier.guest.allowed", false
				).put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication");
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TokenExpeditionTestPreparatorBundleActivator();
	}

}