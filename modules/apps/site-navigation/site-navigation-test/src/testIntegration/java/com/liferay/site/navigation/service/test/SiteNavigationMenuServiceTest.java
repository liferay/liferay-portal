/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.navigation.constants.SiteNavigationActionKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.exception.DuplicateSiteNavigationMenuExternalReferenceCodeException;
import com.liferay.site.navigation.exception.NoSuchMenuException;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuService;
import com.liferay.site.navigation.test.util.SiteNavigationMenuTestUtil;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuCreateDateComparator;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuNameComparator;

import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class SiteNavigationMenuServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.POWER_USER);

		_user = UserTestUtil.addUser(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		UserTestUtil.setUser(_user);
	}

	@Test(expected = PrincipalException.class)
	public void testAddSiteNavigationMenuByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			String externalReferenceCode = StringUtil.randomString();

			_siteNavigationMenuService.addSiteNavigationMenu(
				externalReferenceCode, _group.getGroupId(),
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test(
		expected = DuplicateSiteNavigationMenuExternalReferenceCodeException.class
	)
	public void testAddSiteNavigationMenuWithExistingExternalReferenceCode()
		throws Exception {

		_addSiteMemberRoleResourcePermission(
			SiteNavigationConstants.RESOURCE_NAME,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		UserTestUtil.setUser(_groupUser);

		String externalReferenceCode = StringUtil.randomString();

		_siteNavigationMenuService.addSiteNavigationMenu(
			externalReferenceCode, _group.getGroupId(),
			RandomTestUtil.randomString(), SiteNavigationConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
		_siteNavigationMenuService.addSiteNavigationMenu(
			externalReferenceCode, _group.getGroupId(),
			RandomTestUtil.randomString(), SiteNavigationConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	@Test
	public void testAddSiteNavigationMenuWithPermissions1() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId());

		_addSiteMemberRoleResourcePermission(
			SiteNavigationConstants.RESOURCE_NAME,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.addSiteNavigationMenu(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			SiteNavigationConstants.TYPE_DEFAULT, serviceContext);

		_deleteSiteMemberRoleResourcePermissions(
			SiteNavigationConstants.RESOURCE_NAME);
	}

	@Test
	public void testAddSiteNavigationMenuWithPermissions2() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId());

		_addSiteMemberRoleResourcePermission(
			SiteNavigationConstants.RESOURCE_NAME,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.addSiteNavigationMenu(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			serviceContext);

		_deleteSiteMemberRoleResourcePermissions(
			SiteNavigationConstants.RESOURCE_NAME);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSiteNavigationMenuWithoutPermissions1()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId());

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.addSiteNavigationMenu(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			SiteNavigationConstants.TYPE_DEFAULT, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSiteNavigationMenuWithoutPermissions2()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId());

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.addSiteNavigationMenu(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Test(expected = NoSuchMenuException.class)
	public void testDeleteSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		_addSiteMemberRoleResourcePermission(
			SiteNavigationConstants.RESOURCE_NAME,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		UserTestUtil.setUser(_groupUser);

		String externalReferenceCode = StringUtil.randomString();

		_siteNavigationMenuService.addSiteNavigationMenu(
			externalReferenceCode, _group.getGroupId(),
			RandomTestUtil.randomString(), SiteNavigationConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_siteNavigationMenuService.deleteSiteNavigationMenu(
			externalReferenceCode, _group.getGroupId());

		_siteNavigationMenuService.getSiteNavigationMenuByExternalReferenceCode(
			externalReferenceCode, _group.getGroupId());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteSiteNavigationMenuByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_siteNavigationMenuService.addSiteNavigationMenu(
			externalReferenceCode, _group.getGroupId(),
			RandomTestUtil.randomString(), SiteNavigationConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			_siteNavigationMenuService.deleteSiteNavigationMenu(
				externalReferenceCode, _group.getGroupId());

			Assert.assertNull(
				_siteNavigationMenuService.
					getSiteNavigationMenuByExternalReferenceCode(
						externalReferenceCode, _group.getGroupId()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testDeleteSiteNavigationMenuWithPermissions() throws Exception {
		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_groupUser.getUserId(), _group);

		_addSiteMemberRoleResourcePermission(
			SiteNavigationMenu.class.getName(), ActionKeys.DELETE);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.deleteSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId());

		_deleteSiteMemberRoleResourcePermissions(
			SiteNavigationMenu.class.getName());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteSiteNavigationMenuWithoutPermissions()
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_user.getUserId(), _group);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.deleteSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId());
	}

	@Test
	public void testFetchSiteNavigationMenuWithPermissions() throws Exception {
		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_groupUser.getUserId(), _group);

		_addSiteMemberRoleResourcePermission(
			SiteNavigationMenu.class.getName(), ActionKeys.VIEW);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.fetchSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId());

		_deleteSiteMemberRoleResourcePermissions(
			SiteNavigationMenu.class.getName());
	}

	@Test
	public void testGetSiteNavigationMenuByCreateDateComparatorAndKeywords()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		SiteNavigationMenu siteNavigationMenu1 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_group, Timestamp.valueOf(localDateTime), "CC Name");

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_group, Timestamp.valueOf(localDateTime), "BB");

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		SiteNavigationMenu siteNavigationMenu2 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_group, Timestamp.valueOf(localDateTime), "AA Name");

		List<SiteNavigationMenu> siteNavigationMenus =
			_siteNavigationMenuService.getSiteNavigationMenus(
				_group.getGroupId(), "Name", QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				SiteNavigationMenuCreateDateComparator.getInstance(true));

		Assert.assertEquals(
			Arrays.asList(siteNavigationMenu1, siteNavigationMenu2),
			siteNavigationMenus);

		siteNavigationMenus = _siteNavigationMenuService.getSiteNavigationMenus(
			_group.getGroupId(), "Name", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			SiteNavigationMenuCreateDateComparator.getInstance(false));

		Assert.assertEquals(
			Arrays.asList(siteNavigationMenu2, siteNavigationMenu1),
			siteNavigationMenus);
	}

	@Test
	public void testGetSiteNavigationMenus() throws Exception {
		List<SiteNavigationMenu> siteNavigationMenus =
			_siteNavigationMenuService.getSiteNavigationMenus(
				_group.getGroupId());

		int originalSiteNavigationMenusCount = siteNavigationMenus.size();

		SiteNavigationMenu siteNavigationMenu1 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group);
		SiteNavigationMenu siteNavigationMenu2 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group);

		siteNavigationMenus = _siteNavigationMenuService.getSiteNavigationMenus(
			_group.getGroupId());

		int actualSiteNavigationMenusCount = siteNavigationMenus.size();

		Assert.assertEquals(
			originalSiteNavigationMenusCount + 2,
			actualSiteNavigationMenusCount);

		Assert.assertTrue(siteNavigationMenus.contains(siteNavigationMenu1));
		Assert.assertTrue(siteNavigationMenus.contains(siteNavigationMenu2));
	}

	@Test
	public void testGetSiteNavigationMenusByCreateDateComparator()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		SiteNavigationMenu siteNavigationMenu1 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_group, Timestamp.valueOf(localDateTime), "CC Name");

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		SiteNavigationMenu siteNavigationMenu2 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_group, Timestamp.valueOf(localDateTime), "BB Name");

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		SiteNavigationMenu siteNavigationMenu3 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_group, Timestamp.valueOf(localDateTime), "AA Name");

		List<SiteNavigationMenu> siteNavigationMenus =
			_siteNavigationMenuService.getSiteNavigationMenus(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				SiteNavigationMenuCreateDateComparator.getInstance(true));

		Assert.assertEquals(
			Arrays.asList(
				siteNavigationMenu1, siteNavigationMenu2, siteNavigationMenu3),
			siteNavigationMenus);

		siteNavigationMenus = _siteNavigationMenuService.getSiteNavigationMenus(
			_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			SiteNavigationMenuCreateDateComparator.getInstance(false));

		Assert.assertEquals(
			Arrays.asList(
				siteNavigationMenu3, siteNavigationMenu2, siteNavigationMenu1),
			siteNavigationMenus);
	}

	@Test
	public void testGetSiteNavigationMenusByNameComparator() throws Exception {
		SiteNavigationMenu siteNavigationMenu1 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, "CC Name");
		SiteNavigationMenu siteNavigationMenu2 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, "BB Name");
		SiteNavigationMenu siteNavigationMenu3 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, "AA Name");

		List<SiteNavigationMenu> siteNavigationMenus =
			_siteNavigationMenuService.getSiteNavigationMenus(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				SiteNavigationMenuNameComparator.getInstance(true));

		Assert.assertEquals(
			Arrays.asList(
				siteNavigationMenu3, siteNavigationMenu2, siteNavigationMenu1),
			siteNavigationMenus);

		siteNavigationMenus = _siteNavigationMenuService.getSiteNavigationMenus(
			_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			SiteNavigationMenuNameComparator.getInstance(false));

		Assert.assertEquals(
			Arrays.asList(
				siteNavigationMenu1, siteNavigationMenu2, siteNavigationMenu3),
			siteNavigationMenus);
	}

	@Test
	public void testGetSiteNavigationMenusByNameComparatorAndKeywords()
		throws Exception {

		SiteNavigationMenu siteNavigationMenu1 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, "BB Name");

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, "AA");

		SiteNavigationMenu siteNavigationMenu2 =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group, "CC Name");

		List<SiteNavigationMenu> siteNavigationMenus =
			_siteNavigationMenuService.getSiteNavigationMenus(
				_group.getGroupId(), "Name", QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				SiteNavigationMenuNameComparator.getInstance(true));

		Assert.assertEquals(
			Arrays.asList(siteNavigationMenu1, siteNavigationMenu2),
			siteNavigationMenus);

		siteNavigationMenus = _siteNavigationMenuService.getSiteNavigationMenus(
			_group.getGroupId(), "Name", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			SiteNavigationMenuNameComparator.getInstance(false));

		Assert.assertEquals(
			Arrays.asList(siteNavigationMenu2, siteNavigationMenu1),
			siteNavigationMenus);
	}

	@Test
	public void testGetSiteNavigationMenusCount() throws Exception {
		int originalSiteNavigationMenusCount =
			_siteNavigationMenuService.getSiteNavigationMenusCount(
				_group.getGroupId());

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group);
		SiteNavigationMenuTestUtil.addSiteNavigationMenu(_group);

		int actualSiteNavigationMenusCount =
			_siteNavigationMenuService.getSiteNavigationMenusCount(
				_group.getGroupId());

		Assert.assertEquals(
			originalSiteNavigationMenusCount + 2,
			actualSiteNavigationMenusCount);
	}

	@Test
	public void testGetSiteNavigationMenusCountByKeywords() throws Exception {
		int originalSiteNavigationMenusCount =
			_siteNavigationMenuService.getSiteNavigationMenusCount(
				_group.getGroupId());

		SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_group, RandomTestUtil.randomString() + " Name");
		SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_group, RandomTestUtil.randomString() + " Name");

		int actualSiteNavigationMenusCount =
			_siteNavigationMenuService.getSiteNavigationMenusCount(
				_group.getGroupId(), "Name");

		Assert.assertEquals(
			originalSiteNavigationMenusCount + 2,
			actualSiteNavigationMenusCount);
	}

	@Test
	public void testUpdateSiteNavigationMenuWithUpdatePermissions1()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId());

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_groupUser.getUserId(), _group);

		_addSiteMemberRoleResourcePermission(
			SiteNavigationMenu.class.getName(), ActionKeys.UPDATE);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.updateSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId(),
			siteNavigationMenu.getType(), siteNavigationMenu.isAuto(),
			serviceContext);

		_deleteSiteMemberRoleResourcePermissions(
			SiteNavigationMenu.class.getName());
	}

	@Test
	public void testUpdateSiteNavigationMenuWithUpdatePermissions2()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId());

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_groupUser.getUserId(), _group);

		_addSiteMemberRoleResourcePermission(
			SiteNavigationMenu.class.getName(), ActionKeys.UPDATE);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.updateSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId(),
			RandomTestUtil.randomString(), serviceContext);

		_deleteSiteMemberRoleResourcePermissions(
			SiteNavigationMenu.class.getName());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateSiteNavigationMenuWithoutUpdatePermissions1()
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_user.getUserId(), _group);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.updateSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId(),
			siteNavigationMenu.getType(), siteNavigationMenu.isAuto(),
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateSiteNavigationMenuWithoutUpdatePermissions2()
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(
				_user.getUserId(), _group);

		UserTestUtil.setUser(_groupUser);

		_siteNavigationMenuService.updateSiteNavigationMenu(
			siteNavigationMenu.getSiteNavigationMenuId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	private void _addSiteMemberRoleResourcePermission(
			String name, String permission)
		throws Exception {

		Role siteMemberRole = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			TestPropsValues.getCompanyId(), name, ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()), siteMemberRole.getRoleId(),
			permission);
	}

	private void _deleteSiteMemberRoleResourcePermissions(String name)
		throws Exception {

		ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
			TestPropsValues.getCompanyId(), name, ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _groupUser;

	@Inject
	private SiteNavigationMenuService _siteNavigationMenuService;

	@DeleteAfterTestRun
	private User _user;

}