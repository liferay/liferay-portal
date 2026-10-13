/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test.util;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Date;

/**
 * @author Alberto Javier Moreno Lage
 */
public class TaxonomyVocabularyTestUtil {

	public static AssetVocabulary updateAssetVocabulary(
			AssetVocabulary assetVocabulary)
		throws Exception {

		return AssetVocabularyLocalServiceUtil.updateVocabulary(
			assetVocabulary.getExternalReferenceCode(),
			assetVocabulary.getVocabularyId(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			assetVocabulary.getDescriptionMap(), assetVocabulary.getSettings(),
			assetVocabulary.getVisibilityType());
	}

	public static AssetVocabulary updateAssetVocabularyCreatorUserId(
		AssetVocabulary assetVocabulary, long userId) {

		assetVocabulary.setUserId(userId);

		return AssetVocabularyLocalServiceUtil.updateAssetVocabulary(
			assetVocabulary);
	}

	public static AssetVocabulary updateAssetVocabularyModifiedDate(
		AssetVocabulary assetVocabulary, Date modifiedDate) {

		assetVocabulary.setModifiedDate(modifiedDate);

		return AssetVocabularyLocalServiceUtil.updateAssetVocabulary(
			assetVocabulary);
	}

}