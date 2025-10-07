/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.LoginPostAction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class UserServiceWhenAddingUserWithDefaultRolesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testMultipleDefaultAssociations() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				company.getUserId(), true);

		Group group = GroupTestUtil.addGroup();

		portalPreferences.setValue(
			StringPool.BLANK, PropsKeys.ADMIN_DEFAULT_GROUP_NAMES,
			group.getDescriptiveName());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		portalPreferences.setValue(
			StringPool.BLANK, PropsKeys.ADMIN_DEFAULT_USER_GROUP_NAMES,
			userGroup.getName());

		portalPreferences.setValue(
			StringPool.BLANK, PropsKeys.ADMIN_SYNC_DEFAULT_ASSOCIATIONS,
			"true");

		PortalPreferencesLocalServiceUtil.updatePreferences(
			company.getCompanyId(), PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			PortletPreferencesFactoryUtil.toXML(portalPreferences));

		_user = UserTestUtil.addUser(company);

		_groupLocalService.deleteUserGroup(_user.getUserId(), group);

		_userGroupLocalService.deleteUserUserGroup(
			_user.getUserId(), userGroup);

		_user = _userLocalService.getUser(_user.getUserId());

		Assert.assertFalse(
			ArrayUtil.contains(_user.getGroupIds(), group.getGroupId()));
		Assert.assertFalse(
			ArrayUtil.contains(
				_user.getUserGroupIds(), userGroup.getUserGroupId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.USER_ID, _user.getUserId());

		LoginPostAction loginPostAction = new LoginPostAction();

		loginPostAction.run(
			mockHttpServletRequest, new MockHttpServletResponse());

		_user = _userLocalService.getUser(_user.getUserId());

		Assert.assertTrue(
			ArrayUtil.contains(_user.getGroupIds(), group.getGroupId()));
		Assert.assertTrue(
			ArrayUtil.contains(
				_user.getUserGroupIds(), userGroup.getUserGroupId()));
	}

	@Test
	public void testShouldAlwaysReceiveDefaultUserRole() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				company.getUserId(), true);

		portalPreferences.setValue(
			"", PropsKeys.ADMIN_DEFAULT_ROLE_NAMES, StringPool.BLANK);

		PortalPreferencesLocalServiceUtil.updatePreferences(
			company.getCompanyId(), PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			PortletPreferencesFactoryUtil.toXML(portalPreferences));

		String[] roleNames = _prefsProps.getStringArray(
			company.getCompanyId(), PropsKeys.ADMIN_DEFAULT_ROLE_NAMES,
			StringPool.NEW_LINE, PropsValues.ADMIN_DEFAULT_ROLE_NAMES);

		Assert.assertFalse(ArrayUtil.contains(roleNames, RoleConstants.USER));

		_user = UserTestUtil.addUser(company);

		long[] userRoleIds = _user.getRoleIds();

		Role userRole = RoleLocalServiceUtil.getRole(
			_user.getCompanyId(), RoleConstants.USER);

		Assert.assertTrue(
			ArrayUtil.contains(userRoleIds, userRole.getRoleId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PrefsProps _prefsProps;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}