/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.ldap.LDAPConfigurationModelListenerException;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
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

	@Test
	public void testProcessAction() throws Exception {
		PortletURLFactoryUtil portletURLFactoryUtil =
			new PortletURLFactoryUtil();

		PortletURLFactory portletURLFactory =
			PortletURLFactoryUtil.getPortletURLFactory();

		portletURLFactoryUtil.setPortletURLFactory(
			(PortletURLFactory)ProxyUtil.newProxyInstance(
				PortletURLFactory.class.getClassLoader(),
				new Class<?>[] {PortletURLFactory.class},
				(proxy, method, arguments) -> new MockLiferayPortletURL()));

		String serverName = RandomTestUtil.randomString();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				"ldap://" + RandomTestUtil.randomString(), serverName);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));

		LDAPServerConfiguration ldapServerConfiguration =
			_getLDAPServerConfiguration(serverName);

		_ldapServerConfigurationProvider.delete(
			TestPropsValues.getCompanyId(),
			ldapServerConfiguration.ldapServerId());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			String baseProviderURL = "ldap://" + RandomTestUtil.randomString();

			mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest(
					baseProviderURL, serverName);

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			LDAPConfigurationModelListenerException
				ldapConfigurationModelListenerException =
					(LDAPConfigurationModelListenerException)SessionErrors.get(
						mockLiferayPortletActionRequest,
						LDAPConfigurationModelListenerException.class.
							getName());

			Assert.assertArrayEquals(
				new Object[] {baseProviderURL, "ldaps://"},
				ldapConfigurationModelListenerException.getMessageArguments());
			Assert.assertEquals(
				"the-base-provider-url-x-must-use-the-x-scheme-in-fips-mode",
				ldapConfigurationModelListenerException.getMessageKey());

			Assert.assertNull(_getLDAPServerConfiguration(serverName));
		}

		ldapServerConfiguration = _getLDAPServerConfiguration(serverName);

		if (ldapServerConfiguration != null) {
			_ldapServerConfigurationProvider.delete(
				TestPropsValues.getCompanyId(),
				ldapServerConfiguration.ldapServerId());
		}

		portletURLFactoryUtil.setPortletURLFactory(portletURLFactory);
	}

	private LDAPServerConfiguration _getLDAPServerConfiguration(
			String serverName)
		throws Exception {

		List<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(
				TestPropsValues.getCompanyId(), false);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			if (serverName.equals(ldapServerConfiguration.serverName())) {
				return ldapServerConfiguration;
			}
		}

		return null;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String baseProviderURL, String serverName)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest() {

				@Override
				public String[] getParameterValues(String name) {
					Map<String, String[]> parameterMap = getParameterMap();

					return parameterMap.get(name);
				}

			};

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
			"ldap--" + LDAPConstants.SERVER_NAME + "--", serverName);
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Inject(
		filter = "mvc.command.name=/portal_settings_authentication_ldap/edit_ldap_server",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

}