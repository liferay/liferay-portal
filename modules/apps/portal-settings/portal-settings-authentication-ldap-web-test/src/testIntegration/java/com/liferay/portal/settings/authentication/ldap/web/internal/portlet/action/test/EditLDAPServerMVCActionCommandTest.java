/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.ldap.LDAPConfigurationModelListenerException;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Caio Farias
 */
@RunWith(Arquillian.class)
public class EditLDAPServerMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			String baseProviderURL =
				"ldap://" + RandomTestUtil.randomString() + ":389";

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest(baseProviderURL);

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			Assert.assertTrue(
				SessionErrors.contains(
					mockLiferayPortletActionRequest,
					LDAPConfigurationModelListenerException.class.getName()));

			LDAPConfigurationModelListenerException
				ldapConfigurationModelListenerException =
					(LDAPConfigurationModelListenerException)SessionErrors.get(
						mockLiferayPortletActionRequest,
						LDAPConfigurationModelListenerException.class.
							getName());

			Assert.assertEquals(
				"the-base-provider-url-x-must-use-the-x-scheme-in-fips-mode",
				ldapConfigurationModelListenerException.getMessageKey());
			Assert.assertArrayEquals(
				new Object[] {baseProviderURL, "ldaps://"},
				ldapConfigurationModelListenerException.getMessageArguments());

			Assert.assertTrue(
				ListUtil.isEmpty(
					_configurationProvider.getConfigurations(
						_company.getCompanyId())));
		}
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String baseProviderURL)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID,
			ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			Constants.CMD, Constants.ADD);
		mockLiferayPortletActionRequest.setParameter(
			"ldap--" + LDAPConstants.BASE_PROVIDER_URL + "--", baseProviderURL);
		mockLiferayPortletActionRequest.setParameter(
			"ldap--" + LDAPConstants.SERVER_NAME + "--",
			RandomTestUtil.randomString());

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());

		return themeDisplay;
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject(
		filter = "factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_configurationProvider;

	@Inject(
		filter = "mvc.command.name=/portal_settings_authentication_ldap/edit_ldap_server",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

}