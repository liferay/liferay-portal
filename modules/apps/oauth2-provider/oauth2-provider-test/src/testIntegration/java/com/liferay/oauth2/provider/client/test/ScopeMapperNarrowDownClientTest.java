/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.oauth2.provider.internal.test.TestApplication;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import java.util.Arrays;
import java.util.Collections;
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
public class ScopeMapperNarrowDownClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		WebTarget webTarget = getWebTarget("/annotated");

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				_CLIENT_ID, null,
				getClientCredentialsResponseBiFunction("everything"),
				this::parseTokenString));

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		String error = getToken(
			_CLIENT_ID, null,
			getClientCredentialsResponseBiFunction("everything.read"),
			this::parseError);

		Assert.assertEquals("invalid_grant", error);

		String scopeString = getToken(
			"oauthTestApplicationNarrowed", null,
			getClientCredentialsResponseBiFunction("everything"),
			this::parseScopeString);

		Assert.assertEquals("everything", scopeString);

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				"oauthTestApplicationNarrowed", null,
				getClientCredentialsResponseBiFunction("everything"),
				this::parseTokenString));

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		scopeString = getToken(
			"oauthTestApplicationNarrowed", null,
			getClientCredentialsResponseBiFunction("everything.read"),
			this::parseScopeString);

		Assert.assertEquals("everything.read", scopeString);

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				"oauthTestApplicationNarrowed", null,
				getClientCredentialsResponseBiFunction("everything.read"),
				this::parseTokenString));

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new ScopeMapperNarrowDownClientTestPreparatorBundleActivator();
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private class ScopeMapperNarrowDownClientTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			Dictionary<String, Object> applicationProperties =
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "annotations"
				).build();

			createFactoryConfiguration(
				"com.liferay.oauth2.provider.scope.internal.configuration." +
					"ConfigurableScopeMapperConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.name", TestApplication.class.getName()
				).build());

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated",
				applicationProperties);

			createOAuth2Application(
				companyId, user, _CLIENT_ID,
				Collections.singletonList("everything"));

			createOAuth2Application(
				companyId, user, "oauthTestApplicationNarrowed",
				Arrays.asList("everything", "everything.read"));
		}

	}

}