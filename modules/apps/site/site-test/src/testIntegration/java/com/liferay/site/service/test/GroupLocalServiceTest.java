/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Miguel Pastor
 */
@RunWith(Arquillian.class)
public class GroupLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-82960")
	@Test
	public void testActivatingSiteClearsMaintenanceMode() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Group updatedGroup = _groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), group.getNameMap(),
			group.getDescriptionMap(), group.getType(),
			UnicodePropertiesBuilder.setProperty(
				GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE,
				Boolean.TRUE.toString()
			).build(
			).toString(),
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), false,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertFalse(updatedGroup.isActive());
		Assert.assertTrue(updatedGroup.isMaintenanceMode());

		updatedGroup = _groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), group.getNameMap(),
			group.getDescriptionMap(), group.getType(),
			updatedGroup.getTypeSettings(), group.isManualMembership(),
			group.getMembershipRestriction(), group.getFriendlyURL(),
			group.isInheritContent(), true,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertTrue(updatedGroup.isActive());
		Assert.assertFalse(updatedGroup.isMaintenanceMode());
	}

	@Test
	public void testAddGroup() throws Exception {
		String groupName = RandomTestUtil.randomString();

		Group group1 = _addGroup(groupName);

		AssertUtils.assertFailure(
			DuplicateGroupException.class,
			StringBundler.concat(
				"{companyId=", group1.getCompanyId(), ", groupId=",
				group1.getGroupId(), ", groupKey=", group1.getGroupKey(), "}"),
			() -> _addGroup(groupName));

		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("*"));
		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("22222"));
		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("null"));
		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("test*"));

		group1 = GroupTestUtil.addGroup();

		Group group2 = GroupTestUtil.addGroup(group1.getGroupId());

		Group group3 = GroupTestUtil.addGroup(group2.getGroupId());

		Group group4 = GroupTestUtil.addGroup(group1.getGroupId());

		_assertDescendantGroups(group1, group2, group3, group4);

		_assertDescendantGroups(group2, group3);
		_assertDescendantGroups(group3);
		_assertDescendantGroups(group4);

		String externalReferenceCode = RandomTestUtil.randomString();

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.setProperty(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).setProperty(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES, "false"
			).build();

		group1 = _addGroup(
			externalReferenceCode, RandomTestUtil.randomString(),
			typeSettingsUnicodeProperties.toString());

		Assert.assertEquals(
			externalReferenceCode, group1.getExternalReferenceCode());
		Assert.assertEquals(
			typeSettingsUnicodeProperties, group1.getTypeSettingsProperties());
	}

	@Test
	public void testAddGroupWithReservedKeywordFriendlyURL() throws Exception {
		_assertAddGroupRejectsReservedKeywords();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			_assertAddGroupRejectsReservedKeywords();
		}
	}

	@Test
	public void testAddUserGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);

		_groupLocalService.addUserGroup(user.getUserId(), group.getGroupId());

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);

		_groupLocalService.deleteUserGroup(
			user.getUserId(), group.getGroupId());

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);

		_groupLocalService.addUserGroup(user.getUserId(), group);

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);
	}

	@Test
	public void testAddUserGroupGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroups(
			userGroup.getUserGroupId(), new long[] {group.getGroupId()});

		_assertSearchUserGroup(true, group, userGroup);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_groupLocalService.deleteUserGroupGroups(
			userGroup.getUserGroupId(), new long[] {group.getGroupId()});

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
		_assertSearchUserGroup(false, group, userGroup);

		_groupLocalService.addUserGroupGroups(
			userGroup.getUserGroupId(), Collections.singletonList(group));

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);
		_assertSearchUserGroup(true, group, userGroup);
	}

	@Test
	public void testAddUserGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroups(
			user.getUserId(), new long[] {group.getGroupId()});

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);

		_groupLocalService.deleteUserGroups(
			user.getUserId(), new long[] {group.getGroupId()});

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);

		_groupLocalService.addUserGroups(
			user.getUserId(), Collections.singletonList(group));

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);
	}

	@Test
	public void testCheckSystemGroups() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			// Verify initial state after company creation

			Group companyGroup1 = company.getGroup();

			long companyId = company.getCompanyId();

			_assertGlobalGroup(companyId, companyGroup1);

			// Verify idempotency

			_groupLocalService.checkSystemGroups(companyId);

			Group companyGroup2 = company.getGroup();

			Assert.assertEquals(
				companyGroup1.getGroupId(), companyGroup2.getGroupId());

			_assertGlobalGroup(companyId, companyGroup2);
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testClearUserGroupGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroup(
			userGroup1.getUserGroupId(), group.getGroupId());

		_assertSearchUserGroup(true, group, userGroup1);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup1);

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroup(
			userGroup2.getUserGroupId(), group.getGroupId());

		_assertSearchUserGroup(true, group, userGroup2);

		_groupLocalService.clearUserGroupGroups(userGroup1.getUserGroupId());

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
		_assertSearchUserGroup(false, group, userGroup1);
		_assertSearchUserGroup(true, group, userGroup2);

		_groupLocalService.clearUserGroupGroups(userGroup2.getUserGroupId());

		_assertSearchUserGroup(false, group, userGroup2);
	}

	@Test
	public void testClearUserGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(user.getUserId(), group.getGroupId());

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);

		_groupLocalService.clearUserGroups(user.getUserId());

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);
	}

	@Test
	public void testDeleteGroup() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setMultiValued(true);
		assetVocabularySettingsHelper.setSystem(true);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				null, TestPropsValues.getUserId(), group1.getGroupId(),
				RandomTestUtil.randomString(), null,
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(),
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				ServiceContextTestUtil.getServiceContext(
					group1.getGroupId(), TestPropsValues.getUserId()));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), group1.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), StringPool.BLANK
			).build(),
			assetVocabulary.getVocabularyId(), true, null,
			ServiceContextTestUtil.getServiceContext(
				group1.getGroupId(), TestPropsValues.getUserId()));

		Layout layout = _layoutLocalService.createLayout(group1.getGroupId());

		Group group2 = GroupTestUtil.addGroup();

		layout.setGroupId(group2.getGroupId());
		layout.setCompanyId(group2.getCompanyId());

		layout.setLayoutId(RandomTestUtil.nextLong());

		_layoutLocalService.addLayout(layout);

		Role role = _roleLocalService.getRole(
			group1.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			group1.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(group1.getGroupId()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			group1.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(group1.getGroupId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		Organization organization = OrganizationTestUtil.addOrganization();

		_groupLocalService.addOrganizationGroup(
			organization.getOrganizationId(), group1.getGroupId());

		User user1 = UserTestUtil.addUser();

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), user1);

		_assertSearchUser(
			true, "inheritUsersGroups", group1.getGroupId(), user1);

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroup(
			userGroup.getUserGroupId(), group1.getGroupId());

		User user2 = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user2.getUserId(), userGroup);

		_assertSearchUser(
			true, "inheritUsersGroups", group1.getGroupId(), user2);

		_groupLocalService.deleteGroup(group1);

		_assertSearchUser(
			false, "inheritUsersGroups", group1.getGroupId(), user1);
		_assertSearchUser(
			false, "inheritUsersGroups", group1.getGroupId(), user2);

		Assert.assertNull(
			_assetCategoryLocalService.fetchAssetCategory(
				assetCategory.getCategoryId()));
		Assert.assertNull(
			_assetVocabularyLocalService.fetchAssetVocabulary(
				assetVocabulary.getVocabularyId()));
		Assert.assertEquals(
			0,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				group1.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(group1.getGroupId())));
		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				group1.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group1.getGroupId())));
	}

	@Test
	public void testDeleteUserGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(user.getUserId(), group);

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);

		_groupLocalService.deleteUserGroup(user.getUserId(), group);

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);
	}

	@Test
	public void testDeleteUserGroupGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroup(
			userGroup.getUserGroupId(), group.getGroupId());

		_assertSearchUserGroup(true, group, userGroup);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_groupLocalService.deleteUserGroupGroups(
			userGroup.getUserGroupId(), Collections.singletonList(group));

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
		_assertSearchUserGroup(false, group, userGroup);
	}

	@Test
	public void testDeleteUserGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(user.getUserId(), group);

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);

		_groupLocalService.deleteUserGroups(
			user.getUserId(), Collections.singletonList(group));

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);
	}

	@FeatureFlag("LPD-82960")
	@Test
	public void testEnablingMaintenanceModeDeactivatesSite() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Assert.assertTrue(group.isActive());

		Group updatedGroup = _groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), group.getNameMap(),
			group.getDescriptionMap(), group.getType(),
			UnicodePropertiesBuilder.setProperty(
				GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE,
				Boolean.TRUE.toString()
			).build(
			).toString(),
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), true,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertFalse(updatedGroup.isActive());
		Assert.assertTrue(updatedGroup.isMaintenanceMode());
	}

	@Test
	public void testGetStagedSites() {
		List<Group> groups = _groupLocalService.getStagedSites();

		Assert.assertTrue(groups.toString(), groups.isEmpty());
	}

	@Test
	public void testSetUserGroupGroups() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.setUserGroupGroups(
			userGroup.getUserGroupId(), new long[] {group1.getGroupId()});

		_assertSearchUserGroup(true, group1, userGroup);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(
			true, "inheritUsersGroups", group1.getGroupId(), user);

		Group group2 = GroupTestUtil.addGroup();

		_assertSearchUser(
			false, "inheritUsersGroups", group2.getGroupId(), user);
		_assertSearchUserGroup(false, group2, userGroup);

		_groupLocalService.setUserGroupGroups(
			userGroup.getUserGroupId(), new long[] {group2.getGroupId()});

		_assertSearchUser(
			false, "inheritUsersGroups", group1.getGroupId(), user);
		_assertSearchUser(
			true, "inheritUsersGroups", group2.getGroupId(), user);
		_assertSearchUserGroup(false, group1, userGroup);
		_assertSearchUserGroup(true, group2, userGroup);
	}

	@Test
	public void testSetUserGroups() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_groupLocalService.setUserGroups(
			user.getUserId(), new long[] {group1.getGroupId()});

		_assertSearchUser(true, "usersGroups", group1.getGroupId(), user);

		Group group2 = GroupTestUtil.addGroup();

		_assertSearchUser(false, "usersGroups", group2.getGroupId(), user);

		_groupLocalService.setUserGroups(
			user.getUserId(), new long[] {group2.getGroupId()});

		_assertSearchUser(false, "usersGroups", group1.getGroupId(), user);
		_assertSearchUser(true, "usersGroups", group2.getGroupId(), user);
	}

	@Test
	public void testUnsetUserGroups() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(user.getUserId(), group.getGroupId());

		_assertSearchUser(true, "usersGroups", group.getGroupId(), user);

		_groupLocalService.unsetUserGroups(
			user.getUserId(), new long[] {group.getGroupId()});

		_assertSearchUser(false, "usersGroups", group.getGroupId(), user);
	}

	@Test
	public void testUpdateFriendlyURLWithReservedKeyword() throws Exception {
		_assertUpdateFriendlyURLRejectsReservedKeywords();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			_assertUpdateFriendlyURLRejectsReservedKeywords();
		}
	}

	private Group _addGroup(String name) throws Exception {
		return _addGroup(StringPool.BLANK, name, null);
	}

	private Group _addGroup(
			String externalReferenceCode, String name, String typeSettings)
		throws Exception {

		return _groupLocalService.addGroup(
			externalReferenceCode, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			GroupConstants.TYPE_SITE_OPEN, typeSettings, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name), true,
			false, true, ServiceContextTestUtil.getServiceContext());
	}

	private Group _addGroupWithFriendlyURL(String friendlyURL)
		throws Exception {

		return _groupLocalService.addGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			GroupConstants.TYPE_SITE_OPEN, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true,
			false, true, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertAddGroupRejectsReservedKeywords() throws Exception {
		for (String reservedKeyword : _RESERVED_KEYWORDS) {
			try {
				_addGroupWithFriendlyURL(reservedKeyword);

				Assert.fail();
			}
			catch (GroupFriendlyURLException groupFriendlyURLException) {
				Assert.assertEquals(
					GroupFriendlyURLException.KEYWORD_CONFLICT,
					groupFriendlyURLException.getType());
			}
		}
	}

	private void _assertDescendantGroups(
		Group parentGroup, Group... expectedDescendantGroups) {

		List<Group> actualDescendantGroups = parentGroup.getDescendants(true);

		Assert.assertEquals(
			actualDescendantGroups.toString(), expectedDescendantGroups.length,
			actualDescendantGroups.size());

		for (Group expectedDescendantGroup : expectedDescendantGroups) {
			Assert.assertTrue(
				actualDescendantGroups.contains(expectedDescendantGroup));
		}
	}

	private void _assertGlobalGroup(long companyId, Group group)
		throws PortalException {

		Assert.assertEquals("L_GLOBAL", group.getExternalReferenceCode());
		Assert.assertEquals(
			_classNameLocalService.getClassNameId(Company.class),
			group.getClassNameId());
		Assert.assertEquals(companyId, group.getClassPK());
		Assert.assertEquals("Global", group.getName(LocaleUtil.getDefault()));
		Assert.assertEquals("/global", group.getFriendlyURL());

		Assert.assertNotNull(_groupLocalService.getCompanyGroup(companyId));
	}

	private void _assertSearchUser(
			boolean expected, String paramKey, long paramValue, User user)
		throws Exception {

		Hits hits = _userLocalService.search(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				paramKey, paramValue
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (Sort[])null);

		List<Long> userIds = TransformUtil.transform(
			hits.toList(),
			document -> GetterUtil.getLong(document.get(Field.USER_ID)));

		Assert.assertEquals(
			userIds.toString(), expected, userIds.contains(user.getUserId()));
	}

	private void _assertSearchUserGroup(
		boolean expected, Group group, UserGroup userGroup) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				userGroup.getCompanyId()
			).emptySearchEnabled(
				true
			).modelIndexerClasses(
				UserGroup.class
			).query(
				QueriesUtil.term("groupIds", group.getGroupId())
			).build());

		SearchHits searchHits = searchResponse.getSearchHits();

		List<Long> userGroupIds = TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				return document.getLong(Field.USER_GROUP_ID);
			});

		Assert.assertEquals(
			userGroupIds.toString(), expected,
			userGroupIds.contains(userGroup.getUserGroupId()));
	}

	private void _assertUpdateFriendlyURLRejectsReservedKeywords()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		for (String reservedKeyword : _RESERVED_KEYWORDS) {
			try {
				_groupLocalService.updateFriendlyURL(
					group.getGroupId(), reservedKeyword);

				Assert.fail();
			}
			catch (GroupFriendlyURLException groupFriendlyURLException) {
				Assert.assertEquals(
					GroupFriendlyURLException.KEYWORD_CONFLICT,
					groupFriendlyURLException.getType());
			}
		}
	}

	private static final String[] _RESERVED_KEYWORDS = {
		"/api", "/c", "/combo", "/documents", "/group", "/html", "/image",
		"/layouttpl", "/o", "/web", "/webdav"
	};

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}