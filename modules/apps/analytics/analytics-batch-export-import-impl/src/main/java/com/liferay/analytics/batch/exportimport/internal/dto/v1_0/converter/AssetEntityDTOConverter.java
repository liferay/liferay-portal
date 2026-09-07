/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.AssetEntity;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(service = DTOConverter.class)
public class AssetEntityDTOConverter
	implements DTOConverter<AssetEntry, AssetEntity> {

	@Override
	public String getContentType() {
		return AssetEntity.class.getSimpleName();
	}

	@Override
	public AssetEntry getObject(String externalReferenceCode) throws Exception {
		return _assetEntryLocalService.getAssetEntry(
			GetterUtil.getLong(externalReferenceCode));
	}

	@Override
	public AssetEntity toDTO(
			DTOConverterContext dtoConverterContext, AssetEntry assetEntry)
		throws Exception {

		if (assetEntry == null) {
			return null;
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			assetEntry.getClassTypeId());

		return new AssetEntity() {
			{
				setAssetCategoryIds(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							assetEntry.getClassNameId(),
							assetEntry.getClassPK()),
						AssetCategory::getCategoryId, Long.class));
				setAssetTagNames(
					() -> TransformUtil.transformToArray(
						_assetTagLocalService.getTags(
							assetEntry.getClassNameId(),
							assetEntry.getClassPK()),
						AssetTag::getName, String.class));
				setClassName(
					() -> _portal.getClassName(assetEntry.getClassNameId()));
				setClassPK(assetEntry::getClassPK);
				setClassTypeId(assetEntry::getClassTypeId);
				setClassTypeName(
					() -> {
						if (ddmStructure == null) {
							return null;
						}

						return ddmStructure.getStructureKey();
					});
				setCreateDate(assetEntry::getCreateDate);
				setExpirationDate(assetEntry::getExpirationDate);
				setGroupId(assetEntry::getGroupId);
				setId(assetEntry::getEntryId);
				setModifiedDate(assetEntry::getModifiedDate);
				setPublishDate(assetEntry::getPublishDate);
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private Portal _portal;

}