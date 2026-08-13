/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Marta Medio
 */
@RunWith(Arquillian.class)
public class TokenIntrospectionTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testTokenIntrospectionApplicationCode() {
		String applicationClientId = "oauthTestApplicationCode";

		String token = getToken(
			applicationClientId, null,
			getAuthorizationCodeBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseTokenString);

		Assert.assertNotNull(token);

		WebTarget webTarget = getIntrospectWebTarget();

		Invocation.Builder invocationBuilder = webTarget.request();

		invocationBuilder.header("Origin", RandomTestUtil.randomString());

		Response response = invocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", applicationClientId
					).put(
						"client_secret", CLIENT_SECRET
					).put(
						"token", token
					).build())));

		Assert.assertEquals(
			Response.Status.OK.getStatusCode(), response.getStatus());

		Assert.assertEquals(
			applicationClientId, parseJsonField(response, "client_id"));

		Assert.assertNull(
			response.getHeaderString("Access-Control-Allow-Origin"));
	}

	@Test
	public void testTokenIntrospectionApplicationCodePKCE() {
		String applicationClientId = "oauthTestApplicationCodePKCE";

		String token = getToken(
			applicationClientId, null,
			getAuthorizationCodePKCEBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null),
			this::parseTokenString);

		Assert.assertNotNull(token);

		WebTarget webTarget = getIntrospectWebTarget();

		Invocation.Builder invocationBuilder = webTarget.request();

		Response response = invocationBuilder.post(
			Entity.form(
				new MultivaluedHashMap<>(
					HashMapBuilder.put(
						"client_id", applicationClientId
					).put(
						"client_secret", StringPool.BLANK
					).put(
						"token", token
					).build())));

		Assert.assertEquals(
			Response.Status.OK.getStatusCode(), response.getStatus());

		Assert.assertEquals(
			applicationClientId, parseJsonField(response, "client_id"));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TokenIntrospectionTest.
			TokenIntrospectionTestPreparatorBundleActivator();
	}

	private User _user;

	private class TokenIntrospectionTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), false,
				Collections.singletonList("everything"), false);
			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList(
					"http://redirecturi:" +
						PortalUtil.getPortalServerPort(false)),
				false, Collections.singletonList("everything"), false);
		}

	}

}