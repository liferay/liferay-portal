/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class RevokedTokenTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws PortalException {
		WebTarget webTarget = getJsonWebTarget("user", "get-current-user");

		String tokenString = getToken(
			"oauthTestApplication", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), tokenString);

		Response response = invocationBuilder.get();

		Assert.assertEquals(200, response.getStatus());

		revokeOAuth2AuthorizationByAccessToken(tokenString);

		response = invocationBuilder.get();

		Assert.assertEquals(401, response.getStatus());

		// Assert that revoked token returns a 401 when trying to reuse it.

		response = invocationBuilder.get();

		Assert.assertEquals(401, response.getStatus());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new RevokedTokenTestPreparatorBundleActivator();
	}

	private User _user;

	private class RevokedTokenTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, "oauthTestApplication",
				Collections.singletonList("everything.read"));
		}

	}

}