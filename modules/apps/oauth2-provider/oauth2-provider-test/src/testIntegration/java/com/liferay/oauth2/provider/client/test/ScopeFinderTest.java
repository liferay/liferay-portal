/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.TestRunnablePostHandlingApplication;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import java.util.Collections;
import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class ScopeFinderTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUnavailableAssignedScopeAliases() throws Exception {
		String token = getToken(
			_CLIENT_ID_CLIENT_CREDENTIALS, null,
			this::getClientCredentialsResponse, this::parseTokenString);

		Assert.assertNotNull(token);

		WebTarget webTarget = getWebTarget("/annotated");

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), token);

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		// Install the overriding scope finder which does not publish
		// "everything.read"

		invocationBuilder.post(null, String.class);

		// Check that existing tokens remain unaffected

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		// Get a new token. This token should be restricted to current scopes
		// returned by the scope finder.

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				_CLIENT_ID_CLIENT_CREDENTIALS, null,
				this::getClientCredentialsResponse, this::parseTokenString));

		Assert.assertEquals(
			403,
			invocationBuilder.get(
			).getStatus());

		webTarget = getWebTarget();

		webTarget = webTarget.path("o/captcha/v1.0/captcha/challenge");

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				_CLIENT_ID, null, this::getClientCredentialsResponse,
				this::parseTokenString));

		Assert.assertEquals(
			200,
			invocationBuilder.get(
			).getStatus());

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				_oAuth2ApplicationId);

		_oAuth2ApplicationLocalService.updateScopeAliases(
			oAuth2Application.getUserId(), oAuth2Application.getUserName(),
			_oAuth2ApplicationId,
			Collections.singletonList("Liferay.Captcha.REST.everything.write"));

		invocationBuilder = authorize(
			webTarget.request(),
			getToken(
				_CLIENT_ID, null, this::getClientCredentialsResponse,
				this::parseTokenString));

		Assert.assertEquals(
			403,
			invocationBuilder.get(
			).getStatus());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new ScopeFinderTestPreparatorBundleActivator();
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private static final String _CLIENT_ID_CLIENT_CREDENTIALS =
		RandomTestUtil.randomString();

	private long _oAuth2ApplicationId;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	private class ScopeFinderTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			ScopeFinder scopeFinder = Collections::emptyList;

			Dictionary<String, Object> properties1 =
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "annotations"
				).put(
					"osgi.jaxrs.name", "Test.Application"
				).build();

			Dictionary<String, Object> properties2 =
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.name", "Test.Application"
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build();

			registerJaxRsApplication(
				new TestRunnablePostHandlingApplication(
					() -> registerScopeFinder(scopeFinder, properties2)),
				"annotated", properties1);

			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, user, _CLIENT_ID_CLIENT_CREDENTIALS,
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				Collections.singletonList("everything.read"));

			OAuth2Application oAuth2Application = createOAuth2Application(
				companyId, user, _CLIENT_ID,
				Collections.singletonList(
					"Liferay.Captcha.REST.everything.read"));

			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				oAuth2Application.getCompanyId(),
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				"Liferay.Captcha.REST", "com.liferay.captcha.rest.impl", "GET",
				Collections.singletonList(
					"Liferay.Captcha.REST.everything.read"));

			_oAuth2ApplicationId = oAuth2Application.getOAuth2ApplicationId();
		}

	}

}