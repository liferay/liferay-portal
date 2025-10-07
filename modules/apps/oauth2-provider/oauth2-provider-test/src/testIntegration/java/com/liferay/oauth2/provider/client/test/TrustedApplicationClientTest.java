/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
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

		Assert.assertEquals(locationURI.getHost(), _host);

		response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
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

		Assert.assertNotEquals(locationURI.toString(), _host);
	}

	@Test
	public void testResponseCodeLocationTrustedApplication() {
		Response response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
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

		Assert.assertNotEquals(locationURI.getHost(), _host);

		response = getCodeResponse(
			_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD, null,
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

		Assert.assertNotEquals(locationURI.getHost(), _host);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TrustedApplicationClientTest.
			TrustedApplicationClientTestPreparatorBundleActivator();
	}

	private String _host;
	private User _user;

	private class TrustedApplicationClientTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			Company company = CompanyLocalServiceUtil.getCompany(companyId);

			_host = company.getVirtualHostname();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), false,
				Collections.singletonList("everything"), false);
			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), false);
			createOAuth2Application(
				companyId, _user, "oauthTestTrustedApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE), false,
				Collections.singletonList("everything"), true);
			createOAuth2ApplicationWithNone(
				companyId, _user, "oauthTestTrustedApplicationCodePKCE",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("http://redirecturi:8080"), false,
				Collections.singletonList("everything"), true);
		}

	}

}