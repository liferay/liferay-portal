/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AssetCategoryFriendlyURLModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(),
			StringUtil.toLowerCase(StringUtil.randomString()));

		_assetVocabularyId = assetVocabulary.getVocabularyId();

		_parentAssetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), _assetVocabularyId,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		_assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), _assetVocabularyId,
			_parentAssetCategory.getCategoryId());
	}

	@Test
	public void testOnAfterUpdateWhenParentCategoryIdIsChanged()
		throws Exception {

		Assert.assertEquals(
			_parentAssetCategory.getCategoryId(), _getParentClassPK());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), _assetVocabularyId,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		_assetCategoryLocalService.moveCategory(
			_assetCategory.getCategoryId(), assetCategory.getCategoryId(),
			_assetVocabularyId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(assetCategory.getCategoryId(), _getParentClassPK());
	}

	@Test
	public void testOnAfterUpdateWhenParentCategoryIdIsUnchanged()
		throws Exception {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization1 =
			_getFriendlyURLEntryLocalization();

		_assetCategoryLocalService.updateCategory(
			null, TestPropsValues.getUserId(), _assetCategory.getCategoryId(),
			_assetCategory.getParentCategoryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(),
				StringUtil.toLowerCase(StringUtil.randomString())
			).build(),
			new HashMap<>(), _assetVocabularyId, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FriendlyURLEntryLocalization friendlyURLEntryLocalization2 =
			_getFriendlyURLEntryLocalization();

		Assert.assertEquals(
			_parentAssetCategory.getCategoryId(),
			friendlyURLEntryLocalization2.getParentClassPK());
		Assert.assertEquals(
			friendlyURLEntryLocalization1.getUrlTitle(),
			friendlyURLEntryLocalization2.getUrlTitle());
	}

	private FriendlyURLEntryLocalization _getFriendlyURLEntryLocalization()
		throws Exception {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_classNameLocalService.getClassNameId(AssetCategory.class),
				_assetCategory.getCategoryId());

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
			friendlyURLEntry.getFriendlyURLEntryId(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()));
	}

	private long _getParentClassPK() throws Exception {
		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_getFriendlyURLEntryLocalization();

		return friendlyURLEntryLocalization.getParentClassPK();
	}

	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	private long _assetVocabularyId;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private AssetCategory _parentAssetCategory;

}