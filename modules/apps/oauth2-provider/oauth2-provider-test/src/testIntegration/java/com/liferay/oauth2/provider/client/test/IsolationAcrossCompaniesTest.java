/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
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
public class IsolationAcrossCompaniesTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAnnotated() throws Exception {
		WebTarget webTarget = getWebTarget("/annotated");

		String tokenString = getToken("oauthTestApplication", "host1.xyz");

		Invocation.Builder builder = authorize(
			webTarget.request(), tokenString);

		builder = builder.header("Host", "host1.xyz");

		Assert.assertEquals("everything.read", builder.get(String.class));

		builder = authorize(webTarget.request(), tokenString);

		builder = builder.header("Host", "host2.xyz");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Response response = builder.get();

			Assert.assertEquals(401, response.getStatus());
		}
	}

	@Test
	public void testNoScopes() throws Exception {
		WebTarget webTarget = getWebTarget("/no-scopes");

		String tokenString = getToken("oauthTestApplication", "host1.xyz");

		Invocation.Builder builder = authorize(
			webTarget.request(), tokenString);

		builder = builder.header("Host", "host1.xyz");

		Assert.assertEquals("everything.read", builder.get(String.class));

		builder = authorize(webTarget.request(), tokenString);

		builder = builder.header("Host", "host2.xyz");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Response response = builder.get();

			Assert.assertEquals(401, response.getStatus());
		}
	}

	public static class IsolationAccrossCompaniesTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "no-scopes",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "none"
				).build());

			Company company1 = createCompany("host1");

			createOAuth2Application(
				company1.getCompanyId(),
				UserTestUtil.getAdminUser(company1.getCompanyId()),
				"oauthTestApplication");

			Company company2 = createCompany("host2");

			createOAuth2Application(
				company2.getCompanyId(),
				UserTestUtil.getAdminUser(company2.getCompanyId()),
				"oauthTestApplication");
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new IsolationAccrossCompaniesTestPreparatorBundleActivator();
	}

}