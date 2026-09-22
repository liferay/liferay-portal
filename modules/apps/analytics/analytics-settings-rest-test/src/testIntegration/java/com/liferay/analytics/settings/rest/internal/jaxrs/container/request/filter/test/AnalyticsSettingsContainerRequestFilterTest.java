/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.jaxrs.container.request.filter.test;

import com.liferay.analytics.settings.rest.client.http.HttpInvoker;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class AnalyticsSettingsContainerRequestFilterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = CompanyLocalServiceUtil.getCompany(_group.getCompanyId());

		_analyticsAdminUser = UserTestUtil.addUser(_company, _PASSWORD);
		_companyAdminUser = UserTestUtil.getAdminUser(_company.getCompanyId());
		_user = UserTestUtil.addUser(_company, _PASSWORD);

		Role role = RoleLocalServiceUtil.getRole(
			_company.getCompanyId(), RoleConstants.ANALYTICS_ADMINISTRATOR);

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), _analyticsAdminUser);
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(_group);

		UserLocalServiceUtil.deleteUser(_analyticsAdminUser);
		UserLocalServiceUtil.deleteUser(_user);
	}

	@Test
	public void testAnalyticsSettingsRESTRequiresAdministratorRole()
		throws Exception {

		_assertForbidden(HttpInvoker.HttpMethod.DELETE, "/data-sources", null);
		_assertForbidden(HttpInvoker.HttpMethod.GET, "/channels", null);
		_assertForbidden(
			HttpInvoker.HttpMethod.GET, "/contacts/account-groups", null);
		_assertForbidden(
			HttpInvoker.HttpMethod.GET, "/contacts/configuration", null);
		_assertForbidden(
			HttpInvoker.HttpMethod.GET, "/contacts/organizations", null);
		_assertForbidden(
			HttpInvoker.HttpMethod.GET, "/contacts/user-groups", null);
		_assertForbidden(HttpInvoker.HttpMethod.GET, "/fields", null);
		_assertForbidden(HttpInvoker.HttpMethod.GET, "/fields/accounts", null);
		_assertForbidden(HttpInvoker.HttpMethod.GET, "/fields/people", null);
		_assertForbidden(
			HttpInvoker.HttpMethod.GET, "/recommendation/configuration", null);
		_assertForbidden(HttpInvoker.HttpMethod.GET, "/sites", null);
		_assertForbidden(HttpInvoker.HttpMethod.PATCH, "/channels", "{}");
		_assertForbidden(
			HttpInvoker.HttpMethod.PATCH, "/fields/accounts", "[]");
		_assertForbidden(HttpInvoker.HttpMethod.PATCH, "/fields/people", "[]");
		_assertForbidden(HttpInvoker.HttpMethod.POST, "/channels", "{}");
		_assertForbidden(
			HttpInvoker.HttpMethod.POST, "/configuration/wizard-mode", null);
		_assertForbidden(HttpInvoker.HttpMethod.POST, "/data-sources", "{}");
		_assertForbidden(
			HttpInvoker.HttpMethod.PUT, "/contacts/configuration", "{}");
		_assertForbidden(
			HttpInvoker.HttpMethod.PUT, "/recommendation/configuration", "{}");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD,
			"/contacts/account-groups");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD,
			"/contacts/configuration");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD,
			"/contacts/organizations");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD,
			"/contacts/user-groups");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD, "/fields");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD,
			"/fields/accounts");
		_assertReadsAreAllowed(
			_analyticsAdminUser.getEmailAddress(), _PASSWORD, "/fields/people");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/contacts/account-groups");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/contacts/configuration");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/contacts/organizations");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/contacts/user-groups");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/fields");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/fields/accounts");
		_assertReadsAreAllowed(
			_companyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, "/fields/people");
	}

	private void _assertForbidden(
			HttpInvoker.HttpMethod httpMethod, String path, String body)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse = _invoke(
			body, _user.getEmailAddress(), httpMethod, path, _PASSWORD);

		Assert.assertEquals(
			StringBundler.concat(
				httpMethod, " ", path,
				" must be denied for a user who is neither a company ",
				"administrator nor an analytics administrator"),
			403, httpResponse.getStatusCode());
	}

	private void _assertReadsAreAllowed(
			String emailAddress, String password, String path)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse = _invoke(
			null, emailAddress, HttpInvoker.HttpMethod.GET, path, password);

		Assert.assertEquals(
			StringBundler.concat(
				"GET ", path, " must stay readable by ", emailAddress),
			200, httpResponse.getStatusCode());
	}

	private HttpInvoker.HttpResponse _invoke(
			String body, String login, HttpInvoker.HttpMethod httpMethod,
			String path, String password)
		throws Exception {

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		if (body != null) {
			httpInvoker.body(body, "application/json");
		}

		httpInvoker.httpMethod(httpMethod);
		httpInvoker.path(
			StringBundler.concat(
				"http://", _company.getVirtualHostname(), ":",
				PortalUtil.getPortalServerPort(false),
				"/o/analytics-settings-rest/v1.0", path));
		httpInvoker.userNameAndPassword(login + ":" + password);

		return httpInvoker.invoke();
	}

	private static final String _PASSWORD = "test";

	private User _analyticsAdminUser;
	private Company _company;
	private User _companyAdminUser;
	private Group _group;
	private User _user;

}