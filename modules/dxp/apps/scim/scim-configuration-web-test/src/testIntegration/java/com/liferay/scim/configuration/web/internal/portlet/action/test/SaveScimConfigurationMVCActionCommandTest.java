/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.configuration.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.scim.rest.util.ScimClientUtil;

import jakarta.portlet.PortletSession;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class SaveScimConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		_testProcessAction(false);
		_testProcessAction(true);
	}

	private MockLiferayPortletActionRequest _processAction(
			String cmd, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(Constants.CMD, cmd);
		mockLiferayPortletActionRequest.addParameter(
			"oAuth2ApplicationName", _OAUTH2_APPLICATION_NAME);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompanyById(
				TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		return mockLiferayPortletActionRequest;
	}

	private void _testProcessAction(boolean fipsEnabled) throws Exception {
		User companyAdminUser = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompanyById(
				TestPropsValues.getCompanyId()));

		ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.scim.rest.internal.configuration." +
				"ScimClientOAuth2ApplicationConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"matcherField", "email"
			).put(
				"oAuth2ApplicationName", _OAUTH2_APPLICATION_NAME
			).put(
				"userId", companyAdminUser.getUserId()
			).build());

		User adminUser = UserTestUtil.getAdminUser(
			TestPropsValues.getCompanyId());

		Assert.assertNotEquals(adminUser, companyAdminUser);

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				TestPropsValues.getCompanyId(),
				ScimClientUtil.generateScimClientId(_OAUTH2_APPLICATION_NAME));

		Assert.assertEquals(
			companyAdminUser.getUserId(),
			oAuth2Application.getClientCredentialUserId());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", fipsEnabled)) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_processAction("generate", companyAdminUser);

			String accessToken =
				(String)mockLiferayPortletActionRequest.getAttribute(
					"SCIM_OAUTH2_ACCESS_TOKEN");

			Assert.assertNotNull(
				_oAuth2AuthorizationLocalService.
					fetchOAuth2AuthorizationByAccessTokenContent(accessToken));

			PortletSession portletSession =
				mockLiferayPortletActionRequest.getPortletSession();

			if (fipsEnabled) {
				Assert.assertEquals(
					accessToken,
					portletSession.getAttribute("SCIM_OAUTH2_ACCESS_TOKEN"));
			}
			else {
				Assert.assertNull(
					portletSession.getAttribute("SCIM_OAUTH2_ACCESS_TOKEN"));
			}

			oAuth2Application.setClientCredentialUserId(adminUser.getUserId());

			oAuth2Application =
				_oAuth2ApplicationLocalService.updateOAuth2Application(
					oAuth2Application);

			_processAction("generate", companyAdminUser);
		}

		List<OAuth2Authorization> oAuth2Authorizations =
			_oAuth2AuthorizationLocalService.getOAuth2Authorizations(
				oAuth2Application.getOAuth2ApplicationId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			oAuth2Authorizations.toString(), 2, oAuth2Authorizations.size());

		OAuth2Authorization oAuth2Authorization = oAuth2Authorizations.get(0);

		Assert.assertEquals(
			10,
			DateUtil.getDaysBetween(
				oAuth2Authorization.getAccessTokenExpirationDate(),
				oAuth2Authorization.getCreateDate()));
		Assert.assertEquals(
			companyAdminUser.getUserId(), oAuth2Authorization.getUserId());

		oAuth2Authorization = oAuth2Authorizations.get(1);

		Assert.assertEquals(
			365,
			DateUtil.getDaysBetween(
				oAuth2Authorization.getAccessTokenExpirationDate(),
				oAuth2Authorization.getCreateDate()));
		Assert.assertEquals(
			adminUser.getUserId(), oAuth2Authorization.getUserId());

		_processAction("reset", companyAdminUser);

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(&(", ConfigurationAdmin.SERVICE_FACTORYPID,
					"=com.liferay.scim.rest.internal.configuration.",
					"ScimClientOAuth2ApplicationConfiguration)(companyId=",
					TestPropsValues.getCompanyId(), "))")));
	}

	private static final String _OAUTH2_APPLICATION_NAME =
		RandomTestUtil.randomString();

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(
		filter = "mvc.command.name=/scim_configuration/save_scim_configuration",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

}