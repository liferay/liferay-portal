/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_3_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_3_x.UpgradeAssetCategory;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class UpgradeAssetCategoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetVocabulary = AssetTestUtil.addVocabulary(_group.getGroupId());

		AssetCategory category = _createCategory(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		category = _createCategory(category.getCategoryId());

		_createCategory(category.getCategoryId());
	}

	@Test
	public void testUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = new UpgradeAssetCategory();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		StringBundler sb = new StringBundler((2 * _assetCategories.size()) + 1);

		sb.append("/");

		for (AssetCategory category : _assetCategories) {
			category = _assetCategoryLocalService.getAssetCategory(
				category.getCategoryId());

			sb.append(category.getCategoryId());

			sb.append("/");

			Assert.assertEquals(sb.toString(), category.getTreePath());
		}
	}

	private AssetCategory _createCategory(long parentCategoryId) {
		AssetCategory category = _assetCategoryLocalService.createAssetCategory(
			_counterLocalService.increment());

		category.setGroupId(_group.getGroupId());
		category.setCompanyId(_group.getCompanyId());
		category.setParentCategoryId(parentCategoryId);
		category.setVocabularyId(_assetVocabulary.getVocabularyId());

		category = _assetCategoryLocalService.updateAssetCategory(category);

		_assetCategories.add(category);

		return category;
	}

	@DeleteAfterTestRun
	private final List<AssetCategory> _assetCategories = new ArrayList<>();

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private CounterLocalService _counterLocalService;

	@DeleteAfterTestRun
	private Group _group;

}