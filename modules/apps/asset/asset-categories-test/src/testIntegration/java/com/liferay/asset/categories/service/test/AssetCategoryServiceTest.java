/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetCategoryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAddCategory() throws Exception {
		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_testAddCategory(
			String.valueOf(assetCategory.getPrimaryKey()), RoleConstants.GUEST);
		_testAddCategory(
			String.valueOf(assetCategory.getPrimaryKey()), RoleConstants.OWNER);
		_testAddCategory(
			String.valueOf(assetCategory.getPrimaryKey()),
			RoleConstants.SITE_MEMBER);
	}

	@Test
	public void testGetOrAddEmptyCategory() throws Exception {

		// Lazy referencing disabled

		try {
			_assetCategoryService.getOrAddEmptyCategory(
				RandomTestUtil.randomString(), _group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchCategoryException noSuchCategoryException) {
			Assert.assertNotNull(noSuchCategoryException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			AssetCategory assetCategory =
				_assetCategoryService.getOrAddEmptyCategory(
					RandomTestUtil.randomString(), _group.getGroupId());

			Assert.assertNotNull(assetCategory);

			// Without permissions

			User user = UserTestUtil.addGroupUser(
				_group, RoleConstants.SITE_MEMBER);

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, permissionChecker)) {

				_assetCategoryService.getOrAddEmptyCategory(
					RandomTestUtil.randomString(), user.getGroupId());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}

			// Without permissions, existing category

			String externalReferenceCode = RandomTestUtil.randomString();

			_assetCategoryLocalService.addCategory(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(),
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				_assetVocabulary.getVocabularyId(), null, new ServiceContext());

			assetCategory = _assetCategoryService.getOrAddEmptyCategory(
				externalReferenceCode, _group.getGroupId());

			Assert.assertNotNull(assetCategory);
		}
	}

	@Test
	public void testSearchWithoutParentGroupPermission() throws Exception {
		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, AssetCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetCategory1.getCategoryId()), ActionKeys.VIEW);

		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		User siteAdministratorUser = UserTestUtil.addUser(
			childGroup.getGroupId());

		Role siteAdministratorRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		_userGroupRoleLocalService.addUserGroupRole(
			siteAdministratorUser.getUserId(), childGroup.getGroupId(),
			siteAdministratorRole.getRoleId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(siteAdministratorUser));

			JSONArray jsonArray = _assetCategoryService.search(
				new long[] {_group.getGroupId(), childGroup.getGroupId()},
				StringPool.BLANK,
				new long[] {_assetVocabulary.getVocabularyId()},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				String.valueOf(jsonArray.get(0)));

			Assert.assertEquals(
				assetCategory2.getCategoryId(),
				jsonObject.getLong("categoryId"));
		}
		finally {
			_userLocalService.deleteUser(siteAdministratorUser.getUserId());

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private void _testAddCategory(String primKey, String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(_group.getCompanyId(), roleName);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_group.getCompanyId(), AssetCategory.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, primKey, role.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetCategoryService _assetCategoryService;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}