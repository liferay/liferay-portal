/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.provider.GroupSearchProvider;
import com.liferay.site.search.GroupSearch;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class GroupSearchProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		_originalGroupsComplexSQLClassNames =
			PropsValues.GROUPS_COMPLEX_SQL_CLASS_NAMES;
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "GROUPS_COMPLEX_SQL_CLASS_NAMES",
			_originalGroupsComplexSQLClassNames);

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testSearchPrivateMembershipGroups() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup1 = GroupTestUtil.addGroup(parentGroup.getGroupId());

		childGroup1.setType(GroupConstants.TYPE_SITE_PRIVATE);

		childGroup1 = _groupLocalService.updateGroup(childGroup1);

		Group childGroup2 = GroupTestUtil.addGroup(parentGroup.getGroupId());

		childGroup2.setType(GroupConstants.TYPE_SITE_PRIVATE);

		_groupLocalService.updateGroup(childGroup2);

		Group childGroup3 = GroupTestUtil.addGroup(parentGroup.getGroupId());

		childGroup3.setType(GroupConstants.TYPE_SITE_PRIVATE);

		_groupLocalService.updateGroup(childGroup3);

		User user = UserTestUtil.addGroupUser(
			parentGroup, RoleConstants.SITE_MEMBER);

		Role role = RoleLocalServiceUtil.getRole(
			childGroup1.getCompanyId(), RoleConstants.SITE_MEMBER);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			new long[] {user.getUserId()}, childGroup1.getGroupId(),
			role.getRoleId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(parentGroup, user));
		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(parentGroup.getGroupId()));

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "GROUPS_COMPLEX_SQL_CLASS_NAMES",
			new String[] {"com.liferay.portal.kernel.model.User"});

		GroupSearch groupSearch = new GroupSearch(
			mockLiferayPortletActionRequest, new MockLiferayPortletURL());

		GroupSearchProvider.setResultsAndTotal(
			Arrays.asList(
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName()),
			null, groupSearch, mockLiferayPortletActionRequest);

		_assertGroupSearch(childGroup1, groupSearch);

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "GROUPS_COMPLEX_SQL_CLASS_NAMES",
			new String[] {
				"com.liferay.portal.kernel.model.User",
				"com.liferay.portal.kernel.model.Organization",
				"com.liferay.portal.kernel.model.UserGroup",
				"com.liferay.portal.kernel.model.Company"
			});

		GroupSearch complexSQLGroupSearch = new GroupSearch(
			mockLiferayPortletActionRequest, new MockLiferayPortletURL());

		GroupSearchProvider.setResultsAndTotal(
			Arrays.asList(
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName()),
			null, complexSQLGroupSearch, mockLiferayPortletActionRequest);

		_assertGroupSearch(childGroup1, complexSQLGroupSearch);
	}

	private void _assertGroupSearch(
		Group childGroup1, GroupSearch groupSearch) {

		List<Group> results = groupSearch.getResults();

		Assert.assertEquals(results.toString(), 1, results.size());

		Group group = results.get(0);

		Assert.assertEquals(childGroup1.getGroupId(), group.getGroupId());
	}

	private ThemeDisplay _getThemeDisplay(Group group, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private static String[] _originalGroupsComplexSQLClassNames;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}