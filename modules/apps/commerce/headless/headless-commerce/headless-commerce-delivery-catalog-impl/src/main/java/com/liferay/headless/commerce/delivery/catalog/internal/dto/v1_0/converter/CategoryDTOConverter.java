/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Category;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {"default=true", "dto.class.name=AssetCategory"},
	service = DTOConverter.class
)
public class CategoryDTOConverter
	implements DTOConverter<AssetCategory, Category> {

	@Override
	public String getContentType() {
		return Category.class.getSimpleName();
	}

	@Override
	public Category toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		AssetCategory assetCategory = _assetCategoryLocalService.getCategory(
			(Long)dtoConverterContext.getId());

		return new Category() {
			{
				setId(assetCategory::getCategoryId);
				setName(assetCategory::getName);
				setSiteId(assetCategory::getGroupId);
				setTitle(
					() -> assetCategory.getTitle(
						dtoConverterContext.getLocale()));
				setVocabulary(
					() -> {
						AssetVocabulary assetVocabulary =
							_assetVocabularyLocalService.getAssetVocabulary(
								assetCategory.getVocabularyId());

						return assetVocabulary.getName();
					});
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}