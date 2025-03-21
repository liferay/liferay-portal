/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class SaveGroupConfigurationMVCActionCommandTest {

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

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_groupAdminUser = UserTestUtil.addGroupAdminUser(_group);

		Group group = _groupLocalService.fetchGroup(
			_company.getCompanyId(), GroupConstants.CONTROL_PANEL);

		_layout = _layoutLocalService.fetchDefaultLayout(
			group.getGroupId(), true);

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_groupAdminUser.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testSaveGroupConfiguration() throws Exception {
		_assertSaveGroupConfiguration(true, true, true, _groupAdminUser);
	}

	@Test
	public void testSaveGroupConfigurationDisablingIncludeCategories()
		throws Exception {

		_assertSaveGroupConfiguration(false, true, true, _groupAdminUser);
	}

	@Test
	public void testSaveGroupConfigurationDisablingIncludePages()
		throws Exception {

		_assertSaveGroupConfiguration(true, false, true, _groupAdminUser);
	}

	@Test
	public void testSaveGroupConfigurationDisablingIncludeWebContent()
		throws Exception {

		_assertSaveGroupConfiguration(true, true, false, _groupAdminUser);
	}

	@Test
	public void testSaveGroupConfigurationNotGroupAdminUser() throws Exception {
		Group group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _groupAdminUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		boolean portletExceptionThrown = false;

		try {
			_assertSaveGroupConfiguration(
				true, true, true, UserTestUtil.addGroupAdminUser(group));
		}
		catch (PortletException portletException) {
			portletExceptionThrown = true;

			Throwable throwable = portletException.getCause();

			Assert.assertNotNull(throwable);
			Assert.assertTrue(
				throwable instanceof PrincipalException.MustBeGroupAdmin);
		}

		Assert.assertTrue(portletExceptionThrown);
	}

	private void _assertGroupConfiguration(
			boolean includeCategories, boolean includePages,
			boolean includeWebContent)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(&(%s=%s)(%s=%d))", ConfigurationAdmin.SERVICE_FACTORYPID,
				_PID_SITEMAP_GROUP_CONFIGURATION + ".scoped",
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
				_group.getGroupId()));

		Assert.assertTrue(ArrayUtil.isNotEmpty(configurations));

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertEquals(
			includeCategories,
			GetterUtil.getBoolean(properties.get("includeCategories")));
		Assert.assertEquals(
			includePages,
			GetterUtil.getBoolean(properties.get("includePages")));
		Assert.assertEquals(
			includeWebContent,
			GetterUtil.getBoolean(properties.get("includeWebContent")));
	}

	private void _assertSaveGroupConfiguration(
			boolean includeCategories, boolean includePages,
			boolean includeWebContent, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				includeCategories, includePages, includeWebContent, user);

		Assert.assertFalse(
			SessionMessages.contains(
				mockLiferayPortletActionRequest, "requestProcessed"));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionMessages.contains(
				mockLiferayPortletActionRequest, "requestProcessed"));

		_assertGroupConfiguration(
			includeCategories, includePages, includeWebContent);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			boolean includeCategories, boolean includePages,
			boolean includeWebContent, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"includeCategories", String.valueOf(includeCategories));
		mockLiferayPortletActionRequest.addParameter(
			"includePages", String.valueOf(includePages));
		mockLiferayPortletActionRequest.addParameter(
			"includeWebContent", String.valueOf(includeWebContent));
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private static final String _PID_SITEMAP_GROUP_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapGroupConfiguration";

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private Group _group;
	private User _groupAdminUser;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "mvc.command.name=/site_sitemap/save_group_configuration")
	private MVCActionCommand _mvcActionCommand;

	private String _originalName;

}