/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.CategoryDisplayPage;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.admin.channel.dto.v1_0.CategoryDisplayPage"
	},
	service = DTOConverter.class
)
public class CategoryDisplayPageDTOConverter
	implements DTOConverter<CPDisplayLayout, CategoryDisplayPage> {

	@Override
	public String getContentType() {
		return CategoryDisplayPage.class.getSimpleName();
	}

	@Override
	public CategoryDisplayPage toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDisplayLayout cpDisplayLayout =
			_cpDisplayLayoutLocalService.getCPDisplayLayout(
				(Long)dtoConverterContext.getId());

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(
				cpDisplayLayout.getClassPK());

		return new CategoryDisplayPage() {
			{
				setActions(dtoConverterContext::getActions);
				setCategoryExternalReferenceCode(
					() -> {
						if (assetCategory == null) {
							return null;
						}

						return assetCategory.getExternalReferenceCode();
					});
				setCategoryId(cpDisplayLayout::getClassPK);
				setGroupExternalReferenceCode(
					() -> {
						if (assetCategory == null) {
							return null;
						}

						Group group = _groupLocalService.getGroup(
							assetCategory.getGroupId());

						return group.getExternalReferenceCode();
					});
				setId(cpDisplayLayout::getCPDisplayLayoutId);
				setPageUuid(cpDisplayLayout::getLayoutUuid);
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}