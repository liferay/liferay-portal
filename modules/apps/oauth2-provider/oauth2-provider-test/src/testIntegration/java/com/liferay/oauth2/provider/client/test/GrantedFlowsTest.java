/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

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
public class GrantedFlowsTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		String errorString = getToken(
			"oauthTestApplicationPassword", null,
			this::getClientCredentialsResponse, this::parseError);

		Assert.assertEquals("unauthorized_client", errorString);

		String tokenString = getToken(
			"oauthTestApplicationPassword", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		Assert.assertNotNull(tokenString);

		errorString = getToken(
			"oauthTestApplicationClient", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseError);

		Assert.assertEquals("unauthorized_client", errorString);

		tokenString = getToken(
			"oauthTestApplicationClient", null,
			this::getClientCredentialsResponse, this::parseTokenString);

		Assert.assertNotNull(tokenString);

		errorString = getToken(
			"oauthTestApplicationNoGrants", null,
			getAuthorizationCodePKCEBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseError);

		Assert.assertEquals("unauthorized_client", errorString);

		tokenString = getToken(
			"oauthTestApplicationCode", null,
			getAuthorizationCodeBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseTokenString);

		Assert.assertNotNull(tokenString);

		errorString = getToken(
			"oauthTestApplicationPassword", null,
			getAuthorizationCodeBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseError);

		Assert.assertEquals("unauthorized_client", errorString);

		tokenString = getToken(
			"oauthTestApplicationCodePKCE", null,
			getAuthorizationCodePKCEBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseTokenString);

		Assert.assertNotNull(tokenString);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new AnnotatedApplicationTestPreparatorBundleActivator();
	}

	private User _user;

	private class AnnotatedApplicationTestPreparatorBundleActivator
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

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationClient",
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				Collections.singletonList("everything"));

			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestApplicationNoGrants",
				Collections.emptyList(),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), false);

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationPassword",
				Collections.singletonList(GrantType.RESOURCE_OWNER_PASSWORD),
				Collections.singletonList("everything"));
		}

	}

}