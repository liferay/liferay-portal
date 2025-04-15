/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.configuration.MenuAccessConfiguration;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Arrays;
import java.util.Dictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
@Sync
public class EditMenuAccessConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDoProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.ANALYTICS_ADMINISTRATOR);

		mockLiferayPortletActionRequest.addParameter(
			"roleSearchContainerPrimaryKeys",
			new String[] {String.valueOf(role.getRoleId())});

		mockLiferayPortletActionRequest.addParameter(
			"showControlMenuByRole", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(TestPropsValues.getUser()));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		_assertConfiguration(new String[] {String.valueOf(role.getRoleId())});
	}

	@Test(expected = PortalException.class)
	public void testDoProcessActionWithoutPermission() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.ANALYTICS_ADMINISTRATOR);

		mockLiferayPortletActionRequest.addParameter(
			"roleSearchContainerPrimaryKeys",
			new String[] {String.valueOf(role.getRoleId())});

		mockLiferayPortletActionRequest.addParameter(
			"showControlMenuByRole", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(UserTestUtil.addUser()));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private void _assertConfiguration(String[] expectedRolesCanSeeControlMenu)
		throws Exception {

		String filterString = StringBundler.concat(
			"(&(service.factoryPid=", MenuAccessConfiguration.class.getName(),
			".scoped)(",
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(), "=",
			_group.getGroupId(), "))");

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			filterString);

		Assert.assertNotNull(configurations);
		Assert.assertEquals(
			Arrays.toString(configurations), 1, configurations.length);

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		String[] accessToControlMenuRoleIds = (String[])properties.get(
			"accessToControlMenuRoleIds");

		Assert.assertArrayEquals(
			Arrays.toString(accessToControlMenuRoleIds),
			expectedRolesCanSeeControlMenu, accessToControlMenuRoleIds);

		Assert.assertTrue(
			GetterUtil.getBoolean(properties.get("showControlMenuByRole")));
	}

	private ThemeDisplay _getThemeDisplay(User user) {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/site_settings/edit_menu_access_configuration"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private RoleLocalService _roleLocalService;

}