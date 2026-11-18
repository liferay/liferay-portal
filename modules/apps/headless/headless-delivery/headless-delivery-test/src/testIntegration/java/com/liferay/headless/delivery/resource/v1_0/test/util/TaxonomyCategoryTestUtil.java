/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Arrays;
import java.util.Set;

import org.junit.Assert;

/**
 * @author Jürgen Kappler
 */
public class TaxonomyCategoryTestUtil {

	public static AssetCategory addAssetCategory(long groupId)
		throws Exception {

		return _addAssetCategory(
			groupId, ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static AssetCategory addOwnerAssetCategory(long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		return _addAssetCategory(groupId, serviceContext);
	}

	public static void assertTaxonomyCategoryBriefs(
		AssetCategory[] assetCategories,
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs) {

		Set<Long> assetCategoryIds = SetUtil.fromList(
			TransformUtil.transformToList(
				assetCategories, AssetCategory::getCategoryId));
		Set<Long> taxonomyCategoryIds = SetUtil.fromList(
			TransformUtil.transformToList(
				taxonomyCategoryBriefs,
				TaxonomyCategoryBrief::getTaxonomyCategoryId));

		Assert.assertEquals(
			Arrays.toString(taxonomyCategoryBriefs), assetCategoryIds,
			taxonomyCategoryIds);
	}

	private static AssetCategory _addAssetCategory(
			long groupId, ServiceContext serviceContext)
		throws Exception {

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(groupId));

		return AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), groupId, RandomTestUtil.randomString(),
			assetVocabulary.getVocabularyId(), serviceContext);
	}

}