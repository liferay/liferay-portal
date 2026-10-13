/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Date;

/**
 * @author Alberto Javier Moreno Lage
 */
public class TaxonomyCategoryTestUtil {

	public static AssetCategory addAssetCategory(
			String externalReferenceCode, long groupId)
		throws Exception {

		return AssetTestUtil.addCategory(
			externalReferenceCode, groupId, _getVocabularyId(groupId));
	}

	public static AssetCategory updateAssetCategory(AssetCategory assetCategory)
		throws Exception {

		return AssetCategoryLocalServiceUtil.updateCategory(
			assetCategory.getExternalReferenceCode(), assetCategory.getUserId(),
			assetCategory.getCategoryId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			assetCategory.getDescriptionMap(),
			_getVocabularyId(assetCategory.getGroupId()), new String[0],
			ServiceContextTestUtil.getServiceContext(
				assetCategory.getGroupId(), assetCategory.getUserId()));
	}

	public static AssetCategory updateAssetCategoryCreatorUserId(
		AssetCategory assetCategory, long userId) {

		assetCategory.setUserId(userId);

		return AssetCategoryLocalServiceUtil.updateAssetCategory(assetCategory);
	}

	public static AssetCategory updateAssetCategoryModifiedDate(
		AssetCategory assetCategory, Date modifiedDate) {

		assetCategory.setModifiedDate(modifiedDate);

		return AssetCategoryLocalServiceUtil.updateAssetCategory(assetCategory);
	}

	private static long _getVocabularyId(long groupId) throws Exception {
		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.
				fetchAssetVocabularyByExternalReferenceCode(
					_VOCABULARY_EXTERNAL_REFERENCE_CODE, groupId);

		if (assetVocabulary == null) {
			assetVocabulary = AssetTestUtil.addVocabulary(
				_VOCABULARY_EXTERNAL_REFERENCE_CODE, groupId);
		}

		return assetVocabulary.getVocabularyId();
	}

	private static final String _VOCABULARY_EXTERNAL_REFERENCE_CODE =
		"x" + RandomTestUtil.randomString();

}