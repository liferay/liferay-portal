/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ResourcePermissionTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 * @author Julio Camarero
 * @author Roberto Díaz
 * @author Sergio González
 */
@LanguageIds(
	availableLanguageIds = {"de_DE", "en", "en_US", "es_ES", "pt_BR", "zh_CN"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class GroupServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddCompanyStagingGroup() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("staging", Boolean.TRUE);

		_group = _groupService.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, companyGroup.getGroupId(),
			companyGroup.getNameMap(), companyGroup.getDescriptionMap(),
			companyGroup.getType(), companyGroup.isManualMembership(),
			companyGroup.getMembershipRestriction(),
			companyGroup.getFriendlyURL(), false, companyGroup.isActive(),
			serviceContext);

		Assert.assertTrue(_group.isCompanyStagingGroup());

		Assert.assertEquals(companyGroup.getGroupId(), _group.getLiveGroupId());
	}

	@Test
	public void testAddParentToStagedGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		Group childGroup = GroupTestUtil.addGroup();

		_groups.addFirst(childGroup);

		GroupTestUtil.enableLocalStaging(childGroup);

		childGroup = _groupService.updateGroup(
			childGroup.getGroupId(), parentGroup.getGroupId(),
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		Group stagingGroup = childGroup.getStagingGroup();

		Assert.assertEquals(
			parentGroup.getGroupId(), stagingGroup.getParentGroupId());
	}

	@Test
	public void testAddStagedParentToStagedGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		Group childGroup = GroupTestUtil.addGroup();

		_groups.addFirst(childGroup);

		GroupTestUtil.enableLocalStaging(childGroup);

		GroupTestUtil.enableLocalStaging(parentGroup);

		childGroup = _groupService.updateGroup(
			childGroup.getGroupId(), parentGroup.getGroupId(),
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		Group childGroupStagingGroup = childGroup.getStagingGroup();

		Assert.assertEquals(
			parentGroup.getGroupId(),
			childGroupStagingGroup.getParentGroupId());
	}

	@Test
	public void testChangeLocaleFromCurrentToAvailableAndBackAgain()
		throws Exception {

		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testUpdateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.US), LocaleUtil.US, false);
		_testUpdateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US), LocaleUtil.US,
			false);
	}

	@Test
	public void testDeleteGroupRemovesSharedPortletPreferences()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				group.getCompanyId(), group.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED, RandomTestUtil.randomString(),
				null, null);

		_groupService.deleteGroup(group.getGroupId());

		Assert.assertNull(
			"Deleting the group should also delete layout type portlet " +
				"preferences that do no belong to a single layout",
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test(expected = NoSuchResourcePermissionException.class)
	public void testDeleteGroupWithStagingGroupRemovesStagingResource()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group);

		Assert.assertTrue(group.hasStagingGroup());

		Group stagingGroup = group.getStagingGroup();

		_groupService.deleteGroup(group.getGroupId());

		Role role = _roleLocalService.getRole(
			stagingGroup.getCompanyId(), RoleConstants.OWNER);

		_resourcePermissionLocalService.getResourcePermission(
			stagingGroup.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(stagingGroup.getGroupId()), role.getRoleId());
	}

	@Test
	public void testDeleteGroupWithStagingGroupRemovesStagingUserGroupRoles()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group);

		Assert.assertTrue(group.hasStagingGroup());

		Group stagingGroup = group.getStagingGroup();

		List<UserGroupRole> stagingUserGroupRoles =
			_userGroupRoleLocalService.getUserGroupRolesByGroup(
				stagingGroup.getGroupId());

		int stagingUserGroupRolesCount = stagingUserGroupRoles.size();

		Assert.assertEquals(1, stagingUserGroupRolesCount);

		_groupService.deleteGroup(group.getGroupId());

		stagingUserGroupRoles =
			_userGroupRoleLocalService.getUserGroupRolesByGroup(
				stagingGroup.getGroupId());

		stagingUserGroupRolesCount = stagingUserGroupRoles.size();

		Assert.assertEquals(0, stagingUserGroupRolesCount);
	}

	@Test
	public void testDeleteOrganizationSiteOnlyRemovesSiteRoles()
		throws Exception {

		Organization organization = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		Group organizationSite = _groupLocalService.getOrganizationGroup(
			TestPropsValues.getCompanyId(), organization.getOrganizationId());

		organizationSite.setManualMembership(true);

		User user = UserTestUtil.addOrganizationOwnerUser(organization);

		try {
			_userLocalService.addGroupUser(
				organizationSite.getGroupId(), user.getUserId());
			_userLocalService.addOrganizationUsers(
				organization.getOrganizationId(),
				new long[] {user.getUserId()});

			_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), organizationSite.getGroupId(),
				new long[] {_role.getRoleId()});

			_groupService.deleteGroup(organizationSite.getGroupId());

			Assert.assertEquals(
				1,
				_userGroupRoleLocalService.getUserGroupRolesCount(
					user.getUserId(), organizationSite.getGroupId()));
		}
		finally {
			_userLocalService.deleteUser(user);

			_organizationLocalService.deleteOrganization(organization);
		}
	}

	@Test
	public void testDeleteSite() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		int initialTagsCount = _assetTagLocalService.getGroupTagsCount(
			group.getGroupId());

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		Assert.assertEquals(
			initialTagsCount + 1,
			_assetTagLocalService.getGroupTagsCount(group.getGroupId()));

		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				group.getCompanyId(), Group.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getGroupId())));

		ResourcePermissionTestUtil.addResourcePermission(
			2L, DLConstants.RESOURCE_NAME, String.valueOf(group.getGroupId()),
			ResourceConstants.SCOPE_INDIVIDUAL);

		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				group.getCompanyId(), DLConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getGroupId())));

		_groupService.deleteGroup(group.getGroupId());

		Assert.assertEquals(
			initialTagsCount,
			_assetTagLocalService.getGroupTagsCount(group.getGroupId()));

		Assert.assertEquals(
			0,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				group.getCompanyId(), DLConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getGroupId())));
		Assert.assertEquals(
			0,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				group.getCompanyId(), Group.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getGroupId())));
	}

	@Test
	public void testFindGroupByDescription() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), null,
				_group.getDescription(_getLocale()),
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByDescriptionWithSpaces() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_group.setDescription(
			RandomTestUtil.randomString() + StringPool.SPACE +
				RandomTestUtil.randomString());

		_group = _groupLocalService.updateGroup(_group);

		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), null,
				_group.getDescription(_getLocale()),
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByName() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), _group.getName(_getLocale()),
				null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByNameWithSpaces() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_group.setName(
			RandomTestUtil.randomString() + StringPool.SPACE +
				RandomTestUtil.randomString());

		_group = _groupLocalService.updateGroup(_group);

		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), _group.getName(_getLocale()),
				null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByRole() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		long roleId = RoleTestUtil.addGroupRole(_group.getGroupId());

		_role = _roleLocalService.fetchRole(roleId);

		String[] groupParams = {
			"groupsRoles:" + String.valueOf(roleId) + ":long",
			"site:true:boolean"
		};

		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), null, null, groupParams));

		List<Group> groups = _groupService.search(
			TestPropsValues.getCompanyId(), null, null, groupParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(groups.toString(), 1, groups.size());
		Assert.assertEquals(_group, groups.get(0));

		Assert.assertEquals(1, _groupLocalService.getRoleGroupsCount(roleId));

		groups = _groupLocalService.getRoleGroups(roleId);

		Assert.assertEquals(groups.toString(), 1, groups.size());
		Assert.assertEquals(_group, groups.get(0));
	}

	@Test
	public void testFindGuestGroupByCompanyName() throws Exception {
		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), "liferay%", null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGuestGroupByCompanyNameCapitalized() throws Exception {
		Assert.assertEquals(
			1,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), "Liferay%", null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindNonexistentGroup() throws Exception {
		Assert.assertEquals(
			0,
			_groupService.searchCount(
				TestPropsValues.getCompanyId(), "cabina14", null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFriendlyURLDefaults() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.add(group1);

		long companyId = group1.getCompanyId();

		String defaultNewGroupFriendlyURL = _friendlyURLNormalizer.normalize(
			group1.getName(LocaleUtil.getDefault()));

		defaultNewGroupFriendlyURL =
			StringPool.SLASH + defaultNewGroupFriendlyURL;

		Assert.assertNotNull(
			_groupLocalService.fetchFriendlyURLGroup(
				companyId, defaultNewGroupFriendlyURL));

		_groupService.updateFriendlyURL(group1.getGroupId(), null);

		Assert.assertNull(
			_groupLocalService.fetchFriendlyURLGroup(
				companyId, defaultNewGroupFriendlyURL));

		String defaultFriendlyURL = "/group-" + group1.getGroupId();

		Assert.assertNotNull(
			_groupLocalService.fetchFriendlyURLGroup(
				companyId, defaultFriendlyURL));

		_groupService.updateFriendlyURL(
			group1.getGroupId(),
			StringPool.SLASH + RandomTestUtil.randomString());

		Group group2 = GroupTestUtil.addGroup();

		_groups.add(group2);

		_groupService.updateFriendlyURL(
			group2.getGroupId(), defaultFriendlyURL);

		_groupService.updateFriendlyURL(group1.getGroupId(), null);

		Assert.assertNotNull(
			_groupLocalService.fetchFriendlyURLGroup(
				companyId, defaultFriendlyURL + "-1"));
	}

	@Test(expected = GroupFriendlyURLException.class)
	public void testFriendlyURLSetToGroupId() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupService.updateFriendlyURL(
			_group.getGroupId(), "/" + _group.getGroupId());
	}

	@Test(expected = GroupFriendlyURLException.class)
	public void testFriendlyURLSetToLanguageKey() throws Exception {
		_group = GroupTestUtil.addGroup();

		Locale locale = LocaleUtil.US;

		String languageId = StringUtil.toLowerCase(
			LocaleUtil.toLanguageId(locale));

		String i18nPathLanguageId =
			StringPool.SLASH +
				PortalUtil.getI18nPathLanguageId(locale, languageId);

		_groupService.updateFriendlyURL(
			_group.getGroupId(), i18nPathLanguageId);
	}

	@Test(expected = GroupFriendlyURLException.class)
	public void testFriendlyURLSetToRandomLong() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupService.updateFriendlyURL(
			_group.getGroupId(), "/" + RandomTestUtil.nextLong());
	}

	@Test
	public void testGetGlobalSiteDefaultLocale() throws Exception {
		_group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		Assert.assertEquals(
			company.getLocale(),
			_portal.getSiteDefaultLocale(company.getGroupId()));
	}

	@Test
	public void testGetGlobalSiteDefaultLocaleWhenCompanyLocaleModified()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		User guestUser = company.getGuestUser();

		String languageId = guestUser.getLanguageId();

		try {
			guestUser.setLanguageId(_language.getLanguageId(LocaleUtil.BRAZIL));

			guestUser = _userLocalService.updateUser(guestUser);

			Assert.assertEquals(
				LocaleUtil.BRAZIL,
				_portal.getSiteDefaultLocale(company.getGroupId()));
		}
		finally {
			guestUser.setLanguageId(languageId);

			_userLocalService.updateUser(guestUser);
		}
	}

	@Test
	public void testGetGroupsLikeName() throws Exception {
		List<Group> allChildGroups = new ArrayList<>();

		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		List<Group> allGroups = new ArrayList<>(
			_groupLocalService.getGroups(
				TestPropsValues.getCompanyId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, true));

		String name = RandomTestUtil.randomString(10);

		long parentGroupId = parentGroup.getGroupId();

		List<Group> likeNameChildGroups = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Group group = GroupTestUtil.addGroup(parentGroupId);

			group.setName(name + i);

			group = _groupLocalService.updateGroup(group);

			likeNameChildGroups.add(group);
		}

		allChildGroups.addAll(likeNameChildGroups);
		allChildGroups.add(GroupTestUtil.addGroup(parentGroupId));
		allChildGroups.add(GroupTestUtil.addGroup(parentGroupId));
		allChildGroups.add(GroupTestUtil.addGroup(parentGroupId));

		allGroups.addAll(allChildGroups);

		_groups.addAll(0, allChildGroups);

		_assertExpectedGroups(likeNameChildGroups, parentGroupId, name + "%");
		_assertExpectedGroups(
			likeNameChildGroups, parentGroupId,
			StringUtil.toLowerCase(name) + "%");
		_assertExpectedGroups(
			likeNameChildGroups, parentGroupId,
			StringUtil.toUpperCase(name) + "%");
		_assertExpectedGroups(
			likeNameChildGroups, GroupConstants.ANY_PARENT_GROUP_ID,
			name + "%");
		_assertExpectedGroups(allChildGroups, parentGroupId, null);
		_assertExpectedGroups(allChildGroups, parentGroupId, "");
		_assertExpectedGroups(
			allGroups, GroupConstants.ANY_PARENT_GROUP_ID, "");
	}

	@Test
	public void testGetGtGroups() throws Exception {
		for (int i = 0; i < 10; i++) {
			_groups.add(GroupTestUtil.addGroup());
		}

		long parentGroupId = 0;
		int size = 5;

		List<Group> groups = _groupService.getGtGroups(
			0, TestPropsValues.getCompanyId(), parentGroupId, true, size);

		Assert.assertFalse(groups.isEmpty());
		Assert.assertEquals(groups.toString(), size, groups.size());

		Group lastGroup = groups.get(groups.size() - 1);

		groups = _groupService.getGtGroups(
			lastGroup.getGroupId(), TestPropsValues.getCompanyId(),
			parentGroupId, true, size);

		Assert.assertFalse(groups.isEmpty());
		Assert.assertEquals(groups.toString(), size, groups.size());

		long previousGroupId = 0;

		for (Group group : groups) {
			long groupId = group.getGroupId();

			Assert.assertTrue(groupId > lastGroup.getGroupId());
			Assert.assertTrue(groupId > previousGroupId);

			previousGroupId = groupId;
		}
	}

	@Test
	public void testGetSiteDefaultInheritLocale() throws Exception {
		_group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		Assert.assertEquals(
			company.getLocale(),
			_portal.getSiteDefaultLocale(_group.getGroupId()));
	}

	@Test
	public void testGetSiteDefaultInheritLocaleWhenCompanyLocaleModified()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		User guestUser = company.getGuestUser();

		String languageId = guestUser.getLanguageId();

		try {
			guestUser.setLanguageId(_language.getLanguageId(LocaleUtil.CHINA));

			guestUser = _userLocalService.updateUser(guestUser);

			Assert.assertEquals(
				LocaleUtil.CHINA,
				_portal.getSiteDefaultLocale(_group.getGroupId()));
		}
		finally {
			guestUser.setLanguageId(languageId);

			_userLocalService.updateUser(guestUser);
		}
	}

	@Test
	public void testGroupDefaultLanguageIdRemainsWhenChangingNameMap()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN);

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));

		_group = _groupService.updateGroup(
			_group.getGroupId(), _group.getParentGroupId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), _group.getGroupKey()
			).put(
				LocaleUtil.SPAIN, _group.getGroupKey()
			).build(),
			_group.getDescriptionMap(), _group.getType(),
			_group.isManualMembership(), _group.getMembershipRestriction(),
			_group.getFriendlyURL(), _group.isInheritContent(),
			_group.isActive(), ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
	}

	@Test
	public void testGroupHasCurrentPageScopeDescriptiveName() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.addFirst(group1);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group2 = GroupTestUtil.addGroup();

		_groups.addFirst(group2);

		Group scopeGroup = _addScopeGroup(group2);

		_groups.addFirst(scopeGroup);

		themeDisplay.setPlid(scopeGroup.getClassPK());

		themeDisplay.setScopeGroupId(group1.getGroupId());

		String scopeDescriptiveName = scopeGroup.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName,
			scopeDescriptiveName.contains("current-page"));
	}

	@Test
	public void testGroupHasCurrentSiteScopeDescriptiveName() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		_group = GroupTestUtil.addGroup();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeDescriptiveName = _group.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName,
			scopeDescriptiveName.contains("current-site"));
	}

	@Test
	public void testGroupHasDefaultScopeDescriptiveName() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.add(group1);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group2 = GroupTestUtil.addGroup();

		_groups.add(group2);

		group2.setClassName(LayoutPrototype.class.getName());

		themeDisplay.setScopeGroupId(group1.getGroupId());

		String scopeDescriptiveName = group2.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName, scopeDescriptiveName.contains("default"));
	}

	@Test
	public void testGroupHasLocalizedName() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		_group = GroupTestUtil.addGroup();

		String scopeDescriptiveName = _group.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName.equals(
				_group.getName(themeDisplay.getLocale())));
	}

	@Test
	public void testGroupIsChildSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		themeDisplay.setScopeGroupId(parentGroup.getGroupId());

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		_groups.addFirst(childGroup);

		Assert.assertEquals(
			"child-site", childGroup.getScopeLabel(themeDisplay));
	}

	@Test
	public void testGroupIsCurrentSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		_group = GroupTestUtil.addGroup();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		Assert.assertEquals("current-site", _group.getScopeLabel(themeDisplay));
	}

	@Test
	public void testGroupIsGlobalScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		_group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		Group companyGroup = company.getGroup();

		Assert.assertEquals("global", companyGroup.getScopeLabel(themeDisplay));
	}

	@Test
	public void testGroupIsPageScopeLabel() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.addFirst(group1);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group2 = GroupTestUtil.addGroup();

		_groups.addFirst(group2);

		Group scopeGroup = _addScopeGroup(group2);

		_groups.addFirst(scopeGroup);

		themeDisplay.setPlid(scopeGroup.getClassPK());

		themeDisplay.setScopeGroupId(group1.getGroupId());

		Assert.assertEquals("page", scopeGroup.getScopeLabel(themeDisplay));
	}

	@Test
	public void testGroupIsParentSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		_groups.addFirst(childGroup);

		themeDisplay.setScopeGroupId(childGroup.getGroupId());

		Assert.assertEquals(
			"parent-site", parentGroup.getScopeLabel(themeDisplay));
	}

	@Test
	public void testGroupIsSiteScopeLabel() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.add(group1);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group2 = GroupTestUtil.addGroup();

		_groups.add(group2);

		themeDisplay.setScopeGroupId(group1.getGroupId());

		Assert.assertEquals("site", group2.getScopeLabel(themeDisplay));
	}

	@Test
	public void testGroupValidSiteFriendlyURLI18nPath() throws Exception {
		_group = GroupTestUtil.addGroup();

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN),
			LocaleUtil.SPAIN);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		String languageId = _language.getLanguageId(LocaleUtil.ENGLISH);

		themeDisplay.setI18nLanguageId(languageId);
		themeDisplay.setI18nPath(
			StringPool.SLASH.concat(LocaleUtil.toW3cLanguageId(languageId)));

		themeDisplay.setSiteGroupId(_group.getGroupId());

		String groupFriendlyURL = _portal.getGroupFriendlyURL(
			_group.getPublicLayoutSet(), themeDisplay, false, false);

		Assert.assertFalse(
			groupFriendlyURL + " should not contain " +
				themeDisplay.getI18nPath(),
			groupFriendlyURL.contains(themeDisplay.getI18nPath()));
	}

	@Test
	public void testIndividualResourcePermission() throws Exception {
		_group = GroupTestUtil.addGroup();

		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				_group.getCompanyId(), Group.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_group.getGroupId())));
	}

	@Test
	public void testInheritLocalesByDefault() throws Exception {
		_group = GroupTestUtil.addGroup();

		Assert.assertTrue(_language.isInheritLocales(_group.getGroupId()));
		Assert.assertEquals(
			_language.getAvailableLocales(),
			_language.getAvailableLocales(_group.getGroupId()));
	}

	@Test
	public void testInvalidChangeAvailableLanguageIds() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testUpdateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.US), null, true);
	}

	@Test
	public void testInvalidChangeDefaultLanguageId() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testUpdateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US), LocaleUtil.GERMANY,
			true);
	}

	@Test
	public void testRemoveParentFromStagedGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		Group childGroup = GroupTestUtil.addGroup();

		_groups.addFirst(childGroup);

		GroupTestUtil.enableLocalStaging(childGroup);

		_groupService.updateGroup(
			childGroup.getGroupId(), parentGroup.getGroupId(),
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		childGroup = _groupService.updateGroup(
			childGroup.getGroupId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		Group childGroupStagingGroup = childGroup.getStagingGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			childGroupStagingGroup.getParentGroupId());
	}

	@Test
	public void testRemoveStagedParentFromStagedGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		Group childGroup = GroupTestUtil.addGroup();

		_groups.addFirst(childGroup);

		GroupTestUtil.enableLocalStaging(childGroup);

		GroupTestUtil.enableLocalStaging(parentGroup);

		_groupService.updateGroup(
			childGroup.getGroupId(), parentGroup.getGroupId(),
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		childGroup = _groupService.updateGroup(
			childGroup.getGroupId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		Group childGroupStagingGroup = childGroup.getStagingGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			childGroupStagingGroup.getParentGroupId());
	}

	@Test
	public void testScopes() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.addFirst(group);

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		Assert.assertFalse(layout.hasScopeGroup());

		Group scopeGroup = _groupLocalService.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			Layout.class.getName(), layout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), layout.getName(LocaleUtil.getDefault())
			).build(),
			null, 0, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null,
			false, true, null);

		_groups.addFirst(scopeGroup);

		Assert.assertFalse(scopeGroup.isRoot());
		Assert.assertEquals(scopeGroup.getParentGroupId(), group.getGroupId());
	}

	@Test
	public void testSelectableParentSites() throws Exception {
		_testSelectableParentSites(false);
	}

	@Test
	public void testSelectableParentSitesStaging() throws Exception {
		_testSelectableParentSites(true);
	}

	@Test(expected = GroupParentException.MustNotHaveChildParent.class)
	public void testSelectFirstChildGroupAsParentSite() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		_groups.addFirst(parentGroup);

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		_groups.addFirst(childGroup);

		_groupService.updateGroup(
			parentGroup.getGroupId(), childGroup.getGroupId(),
			parentGroup.getNameMap(), parentGroup.getDescriptionMap(),
			parentGroup.getType(), parentGroup.isManualMembership(),
			parentGroup.getMembershipRestriction(),
			parentGroup.getFriendlyURL(), parentGroup.isInheritContent(),
			parentGroup.isActive(), ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = GroupParentException.MustNotHaveChildParent.class)
	public void testSelectLastChildGroupAsParentSite() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.addFirst(group1);

		Group group11 = GroupTestUtil.addGroup(group1.getGroupId());

		_groups.addFirst(group11);

		Group group111 = GroupTestUtil.addGroup(group11.getGroupId());

		_groups.addFirst(group111);

		Group group1111 = GroupTestUtil.addGroup(group111.getGroupId());

		_groups.addFirst(group1111);

		_groupService.updateGroup(
			group1.getGroupId(), group1111.getGroupId(), group1.getNameMap(),
			group1.getDescriptionMap(), group1.getType(),
			group1.isManualMembership(), group1.getMembershipRestriction(),
			group1.getFriendlyURL(), group1.isInheritContent(),
			group1.isActive(), ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = GroupParentException.MustNotHaveStagingParent.class)
	public void testSelectLiveGroupAsParentSite() throws Exception {
		_group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(_group);

		Assert.assertTrue(_group.hasStagingGroup());

		Group stagingGroup = _group.getStagingGroup();

		_groupService.updateGroup(
			stagingGroup.getGroupId(), _group.getGroupId(),
			stagingGroup.getNameMap(), stagingGroup.getDescriptionMap(),
			stagingGroup.getType(), stagingGroup.isManualMembership(),
			stagingGroup.getMembershipRestriction(),
			stagingGroup.getFriendlyURL(), stagingGroup.isInheritContent(),
			stagingGroup.isActive(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = GroupParentException.MustNotBeOwnParent.class)
	public void testSelectOwnGroupAsParentSite() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupService.updateGroup(
			_group.getGroupId(), _group.getGroupId(), _group.getNameMap(),
			_group.getDescriptionMap(), _group.getType(),
			_group.isManualMembership(), _group.getMembershipRestriction(),
			_group.getFriendlyURL(), _group.isInheritContent(),
			_group.isActive(), ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testSubsites() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		_groups.addFirst(group1);

		Group group11 = GroupTestUtil.addGroup(group1.getGroupId());

		_groups.addFirst(group11);

		Group group111 = GroupTestUtil.addGroup(group11.getGroupId());

		_groups.addFirst(group111);

		Assert.assertTrue(group1.isRoot());
		Assert.assertFalse(group11.isRoot());
		Assert.assertFalse(group111.isRoot());
		Assert.assertEquals(group1.getGroupId(), group11.getParentGroupId());
		Assert.assertEquals(group11.getGroupId(), group111.getParentGroupId());
	}

	@Test
	public void testUpdateAvailableLocales() throws Exception {
		_group = GroupTestUtil.addGroup();

		List<Locale> availableLocales = Arrays.asList(
			LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US);

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), availableLocales, null);

		Assert.assertEquals(
			new HashSet<>(availableLocales),
			_language.getAvailableLocales(_group.getGroupId()));
	}

	@Test
	public void testUpdateDefaultLocale() throws Exception {
		_group = GroupTestUtil.addGroup();

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, LocaleUtil.SPAIN);

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			_portal.getSiteDefaultLocale(_group.getGroupId()));
	}

	@Test
	public void testUpdateFriendlyURLWithJapaneseCharacters() throws Exception {
		_group = GroupTestUtil.addGroup();

		String friendlyURL = "/平仮名片仮名漢字";

		_group = _groupService.updateFriendlyURL(
			_group.getGroupId(), friendlyURL);

		Assert.assertEquals(
			friendlyURL, HttpComponentsUtil.decodeURL(_group.getFriendlyURL()));
	}

	@Test
	public void testUpdateGroupParentFromStagedParentToStagedParentInStagedGroup()
		throws Exception {

		Group childGroup = GroupTestUtil.addGroup();

		_groups.add(childGroup);

		Group parentGroup1 = GroupTestUtil.addGroup();

		_groups.add(parentGroup1);

		Group parentGroup2 = GroupTestUtil.addGroup();

		_groups.add(parentGroup2);

		GroupTestUtil.enableLocalStaging(childGroup);
		GroupTestUtil.enableLocalStaging(parentGroup1);
		GroupTestUtil.enableLocalStaging(parentGroup2);

		_groupService.updateGroup(
			childGroup.getGroupId(), parentGroup1.getGroupId(),
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		childGroup = _groupService.updateGroup(
			childGroup.getGroupId(), parentGroup2.getGroupId(),
			childGroup.getNameMap(), childGroup.getDescriptionMap(),
			childGroup.getType(), childGroup.isManualMembership(),
			childGroup.getMembershipRestriction(), childGroup.getFriendlyURL(),
			childGroup.isInheritContent(), childGroup.isActive(), null);

		Group childGroupStagingGroup = childGroup.getStagingGroup();

		Assert.assertEquals(
			parentGroup2.getGroupId(),
			childGroupStagingGroup.getParentGroupId());
	}

	@Test
	public void testUpdateGroupWithDifferentDefaultLocale() throws Exception {
		_testUpdateGroupWithDifferentDefaultLocale(
			"Spanish",
			_groupLocalService.addGroup(
				TestPropsValues.getUserId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.SPAIN, "Spanish"
				).put(
					LocaleUtil.US, "English"
				).build(),
				null, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, true, true,
				ServiceContextTestUtil.getServiceContext()));

		long classPK = RandomTestUtil.nextLong();

		_testUpdateGroupWithDifferentDefaultLocale(
			String.valueOf(classPK),
			_groupLocalService.addGroup(
				TestPropsValues.getUserId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, Company.class.getName(),
				classPK, GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					() -> {
						Group group1 = GroupTestUtil.addGroup();

						return group1.getName(LocaleUtil.getDefault());
					}
				).build(),
				null, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, true, true,
				ServiceContextTestUtil.getServiceContext()));
	}

	@Test
	public void testValidChangeAvailableLanguageIds() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testUpdateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US), null, false);
	}

	@Test
	public void testValidChangeDefaultLanguageId() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testUpdateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.GERMANY, false);
	}

	private Group _addScopeGroup(Group group) throws Exception {
		Layout scopeLayout = LayoutTestUtil.addTypePortletLayout(group);

		return _groupLocalService.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			Layout.class.getName(), scopeLayout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			null, 0, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null,
			false, true, null);
	}

	private void _assertExpectedGroups(
			List<Group> expectedGroups, long parentGroupId, String nameSearch)
		throws Exception {

		List<Group> actualGroups = _groupService.getGroups(
			TestPropsValues.getCompanyId(), parentGroupId, nameSearch, true,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			actualGroups.toString(), expectedGroups.size(),
			actualGroups.size());
		Assert.assertTrue(
			actualGroups.toString(), actualGroups.containsAll(expectedGroups));

		Assert.assertEquals(
			expectedGroups.size(),
			_groupService.getGroupsCount(
				TestPropsValues.getCompanyId(), parentGroupId, nameSearch,
				true));
	}

	private Locale _getLocale() {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		return themeDisplay.getLocale();
	}

	private void _testSelectableParentSites(boolean staging) throws Exception {
		_group = GroupTestUtil.addGroup();

		Assert.assertTrue(_group.isRoot());

		List<Long> excludedGroupIds = new ArrayList<>();

		excludedGroupIds.add(_group.getGroupId());

		if (staging) {
			GroupTestUtil.enableLocalStaging(_group);

			Assert.assertTrue(_group.hasStagingGroup());

			Group stagingGroup = _group.getStagingGroup();

			excludedGroupIds.add(stagingGroup.getGroupId());
		}

		List<Group> selectableGroups = _groupService.search(
			_group.getCompanyId(), null, StringPool.BLANK,
			LinkedHashMapBuilder.<String, Object>put(
				"site", Boolean.TRUE
			).put(
				"excludedGroupIds", excludedGroupIds
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (Group selectableGroup : selectableGroups) {
			long selectableGroupId = selectableGroup.getGroupId();

			Assert.assertNotEquals(
				"A group cannot be its own parent", _group.getGroupId(),
				selectableGroupId);

			if (staging) {
				Assert.assertNotEquals(
					"A group cannot have its live group as parent",
					_group.getLiveGroupId(), selectableGroupId);
			}
		}
	}

	private void _testUpdateDisplaySettings(
			long groupId, Collection<Locale> portalAvailableLocales,
			Collection<Locale> groupAvailableLocales, Locale groupDefaultLocale,
			boolean expectFailure)
		throws Exception {

		Set<Locale> availableLocales = _language.getAvailableLocales();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), portalAvailableLocales,
			LocaleUtil.getDefault());

		try {
			GroupTestUtil.updateDisplaySettings(
				groupId, groupAvailableLocales, groupDefaultLocale);

			Assert.assertFalse(expectFailure);
		}
		catch (LocaleException localeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(localeException);
			}

			Assert.assertTrue(expectFailure);
		}
		finally {
			CompanyTestUtil.resetCompanyLocales(
				TestPropsValues.getCompanyId(), availableLocales,
				LocaleUtil.getDefault());
		}
	}

	private void _testUpdateGroupWithDifferentDefaultLocale(
			String expectedGroupKey, Group group)
		throws Exception {

		Locale defaultLocale = LocaleUtil.getDefault();

		try {
			LocaleUtil.setDefault(
				LocaleUtil.SPAIN.getLanguage(), LocaleUtil.SPAIN.getCountry(),
				LocaleUtil.SPAIN.getVariant());

			group = _groupLocalService.updateGroup(
				group.getGroupId(), group.getTypeSettings());

			Assert.assertEquals(expectedGroupKey, group.getGroupKey());
		}
		finally {
			LocaleUtil.setDefault(
				defaultLocale.getLanguage(), defaultLocale.getCountry(),
				defaultLocale.getVariant());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupServiceTest.class);

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private final LinkedList<Group> _groups = new LinkedList<>();

	@Inject
	private GroupService _groupService;

	@Inject
	private Language _language;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}