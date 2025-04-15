/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class OAuth2AuthorizationClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIsNotPossibleToUseAnExpiredToken() {
		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		WebTarget webTarget = getWebTarget("/annotated");

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", "oauthTestApplication");
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "client_credentials");

		String tokenString = parseTokenString(
			invocationBuilder.post(Entity.form(formData)));

		OAuth2Authorization oAuth2Authorization =
			fetchOAuth2AuthorizationByAccessTokenContent(tokenString);

		oAuth2Authorization.setAccessTokenExpirationDate(
			new Date(System.currentTimeMillis() - (Time.HOUR * 2)));

		oAuth2Authorization = updateOAuth2Authorization(oAuth2Authorization);

		invocationBuilder = webTarget.request(
		).header(
			"Authorization",
			"Bearer " + oAuth2Authorization.getAccessTokenContent()
		);

		Response response = invocationBuilder.get();

		Assert.assertEquals(401, response.getStatus());
	}

	public static class ExpiredAuthorizationTestPreparator
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
		return new ExpiredAuthorizationTestPreparator();
	}

}