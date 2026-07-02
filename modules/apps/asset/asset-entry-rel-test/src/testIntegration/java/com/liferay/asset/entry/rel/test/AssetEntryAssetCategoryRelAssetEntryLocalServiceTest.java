/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Samuel Trong Tran
 */
@RunWith(Arquillian.class)
public class AssetEntryAssetCategoryRelAssetEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_creatorUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _creatorUser.getUserId());

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			_creatorUser.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(), StringPool.BLANK, serviceContext);

		for (int i = 0; i < _assetCategoryIds.length; i++) {
			_assetCategoryIds[i] = _addAssetCategory(serviceContext);
		}
	}

	@Test
	public void testUserCategoryIds() throws Exception {
		_assertSize(1, ArrayUtil.subset(_assetCategoryIds, 0, 1));

		_assertSize(2, _assetCategoryIds);

		_assertSize(2, null);

		_assertSize(0, new long[0]);
	}

	@Test
	public void testUserCategoryIdsWithoutViewPermissionsOverCategory()
		throws Exception {

		_assertSize(2, _assetCategoryIds);

		AssetCategory assetCategory1 = _assetCategories.get(0);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, AssetCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetCategory1.getCategoryId()), ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, AssetCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetCategory1.getCategoryId()), ActionKeys.VIEW);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		AssetCategory assetCategory2 = _assetCategories.get(1);

		_assertSize(2, new long[] {assetCategory2.getCategoryId()});
	}

	@Test
	public void testUserCategoryIdsWithoutViewPermissionsOverVocabulary()
		throws Exception {

		_assertSize(2, _assetCategoryIds);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, AssetVocabulary.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_assetVocabulary.getVocabularyId()),
			ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, AssetVocabulary.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_assetVocabulary.getVocabularyId()),
			ActionKeys.VIEW);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_assertSize(2, new long[0]);
	}

	@Inject
	protected static AssetEntryLocalService assetEntryLocalService;

	private long _addAssetCategory(ServiceContext serviceContext)
		throws Exception {

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, _creatorUser.getUserId(), _group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			_assetVocabulary.getVocabularyId(), false, null, serviceContext);

		_assetCategories.add(assetCategory);

		return assetCategory.getCategoryId();
	}

	private void _assertSize(int expectedSize, long[] assetCategoryIds)
		throws Exception {

		assetEntryLocalService.updateEntry(
			_user.getUserId(), _group.getGroupId(), _user.getCreateDate(),
			_user.getModifiedDate(), User.class.getName(), _user.getUserId(),
			_user.getUuid(), 0, assetCategoryIds, null, true, false, null, null,
			null, null, null, _user.getFullName(), null, null, null, null, 0, 0,
			null);

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				User.class.getName(), _user.getUserId());

		Assert.assertEquals(
			assetCategories.toString(), expectedSize, assetCategories.size());
	}

	@DeleteAfterTestRun
	private final List<AssetCategory> _assetCategories = new ArrayList<>();

	private final long[] _assetCategoryIds = new long[2];

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private User _creatorUser;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}