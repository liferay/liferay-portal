/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class UpdateOAuthClientCompanyConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		_configurationProvider.deleteCompanyConfiguration(
			OAuthClientCompanyConfiguration.class, _company.getCompanyId());
	}

	@Test
	public void testUpdateOAuthClientCompanyConfiguration() throws Exception {
		String host = RandomTestUtil.randomString();

		Assert.assertTrue(
			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(
					HashMapBuilder.put(
						"authServerHostsAllowed", new String[] {host}
					).put(
						"authServerLocalNetworkAccessEnabled",
						new String[] {"true"}
					).build()),
				new MockLiferayPortletActionResponse()));

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				OAuthClientCompanyConfiguration
					oAuthClientCompanyConfiguration =
						_configurationProvider.getCompanyConfiguration(
							OAuthClientCompanyConfiguration.class,
							_company.getCompanyId());

				Assert.assertArrayEquals(
					new String[] {host},
					oAuthClientCompanyConfiguration.authServerHostsAllowed());
				Assert.assertTrue(
					oAuthClientCompanyConfiguration.
						authServerLocalNetworkAccessEnabled());

				return null;
			});
	}

	@Test
	public void testUpdateOAuthClientCompanyConfigurationWithoutPermission()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		try {
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest(
					HashMapBuilder.put(
						"authServerHostsAllowed",
						new String[] {RandomTestUtil.randomString()}
					).put(
						"authServerLocalNetworkAccessEnabled",
						new String[] {"true"}
					).build());

			Assert.assertFalse(
				_mvcActionCommand.processAction(
					mockLiferayPortletActionRequest,
					new MockLiferayPortletActionResponse()));

			Assert.assertTrue(
				SessionErrors.contains(
					mockLiferayPortletActionRequest, PrincipalException.class));

			IdempotentRetryAssert.retryAssert(
				5, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
				() -> {
					OAuthClientCompanyConfiguration
						oAuthClientCompanyConfiguration =
							_configurationProvider.getCompanyConfiguration(
								OAuthClientCompanyConfiguration.class,
								_company.getCompanyId());

					Assert.assertFalse(
						oAuthClientCompanyConfiguration.
							authServerLocalNetworkAccessEnabled());

					return null;
				});
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Map<String, String[]> parameters)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.setParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject(
		filter = "mvc.command.name=/oauth_client_admin/update_oauth_client_company_configuration"
	)
	private MVCActionCommand _mvcActionCommand;

	@DeleteAfterTestRun
	private User _user;

}