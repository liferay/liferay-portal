/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.ws.rs.core.Response;

import java.net.URI;

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
public class TrustedApplicationClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testResponseCodeLocationApplication() {
		Response response = getCodeResponse(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", "oauthTestApplicationCode"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true));

		URI locationURI = response.getLocation();

		Assert.assertEquals(locationURI.getHost(), _HOST);

		response = getCodeResponse(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", "oauthTestApplicationCodePKCE"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true));

		locationURI = response.getLocation();

		Assert.assertNotEquals(locationURI.toString(), _HOST);
	}

	@Test
	public void testResponseCodeLocationTrustedApplication() {
		Response response = getCodeResponse(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", "oauthTestTrustedApplicationCode"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true));

		URI locationURI = response.getLocation();

		Assert.assertNotEquals(locationURI.getHost(), _HOST);

		response = getCodeResponse(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD, null,
			getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", "oauthTestTrustedApplicationCodePKCE"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"response_type", "code"
				),
				true));

		locationURI = response.getLocation();

		Assert.assertNotEquals(locationURI.getHost(), _HOST);
	}

	public static class TrustedApplicationClientTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), false,
				Collections.singletonList("everything"), false);
			createOAuth2ApplicationWithNone(
				defaultCompanyId, user, "oauthTestApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), false);
			createOAuth2Application(
				defaultCompanyId, user, "oauthTestTrustedApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), false,
				Collections.singletonList("everything"), true);
			createOAuth2ApplicationWithNone(
				defaultCompanyId, user, "oauthTestTrustedApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), true);
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TrustedApplicationClientTest.
			TrustedApplicationClientTestPreparatorBundleActivator();
	}

	private static final String _HOST = "localhost";

}