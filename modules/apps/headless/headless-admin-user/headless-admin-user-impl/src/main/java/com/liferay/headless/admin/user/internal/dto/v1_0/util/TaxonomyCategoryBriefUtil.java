/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Objects;

/**
 * @author Balazs Breier
 */
public class TaxonomyCategoryBriefUtil {

	public static TaxonomyCategoryBrief toTaxonomyCategoryBrief(
			AssetCategory assetCategory,
			DTOConverterContext dtoConverterContext)
		throws Exception {

		return new TaxonomyCategoryBrief() {
			{
				setParentTaxonomyCategoryExternalReferenceCode(
					() -> {
						if (assetCategory.getParentCategoryId() == 0) {
							return null;
						}

						AssetCategory parentAssetCategory =
							AssetCategoryLocalServiceUtil.fetchAssetCategory(
								assetCategory.getParentCategoryId());

						if (parentAssetCategory == null) {
							return null;
						}

						return parentAssetCategory.getExternalReferenceCode();
					});
				setParentVocabularyExternalReferenceCode(
					() -> {
						if (assetCategory.getVocabularyId() == 0) {
							return null;
						}

						AssetVocabulary assetVocabulary =
							AssetVocabularyLocalServiceUtil.
								fetchAssetVocabulary(
									assetCategory.getVocabularyId());

						if (assetVocabulary == null) {
							return null;
						}

						return assetVocabulary.getExternalReferenceCode();
					});
				setTaxonomyCategoryId(assetCategory::getCategoryId);
				setTaxonomyCategoryName(
					() -> assetCategory.getTitle(
						dtoConverterContext.getLocale()));
				setTaxonomyCategoryName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						assetCategory.getTitleMap()));
				setTaxonomyCategoryReference(
					() -> _toTaxonomyCategoryReference(
						assetCategory, dtoConverterContext));
			}
		};
	}

	private static TaxonomyCategoryReference _toTaxonomyCategoryReference(
		AssetCategory assetCategory, DTOConverterContext dtoConverterContext) {

		return new TaxonomyCategoryReference() {
			{
				setExternalReferenceCode(
					assetCategory::getExternalReferenceCode);
				setSiteKey(
					() -> {
						if (Objects.equals(
								GetterUtil.getLong(assetCategory.getGroupId()),
								dtoConverterContext.getAttribute("groupId"))) {

							return null;
						}

						Group group = GroupLocalServiceUtil.fetchGroup(
							assetCategory.getGroupId());

						return group.getGroupKey();
					});
			}
		};
	}

}