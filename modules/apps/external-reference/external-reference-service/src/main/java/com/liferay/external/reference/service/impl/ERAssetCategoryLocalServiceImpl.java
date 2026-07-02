/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.external.reference.service.base.ERAssetCategoryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetCategory",
	service = AopService.class
)
public class ERAssetCategoryLocalServiceImpl
	extends ERAssetCategoryLocalServiceBaseImpl {

	@Override
	public AssetCategory addOrUpdateCategory(
			String externalReferenceCode, long userId, long groupId,
			long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, user.getCompanyId());

		if (assetCategory == null) {
			assetCategory = _assetCategoryLocalService.addCategory(
				null, userId, groupId, parentCategoryId, titleMap,
				descriptionMap, vocabularyId, false, categoryProperties,
				serviceContext);

			assetCategory.setExternalReferenceCode(externalReferenceCode);

			assetCategory = _assetCategoryLocalService.updateAssetCategory(
				assetCategory);
		}
		else {
			assetCategory = _assetCategoryLocalService.updateCategory(
				assetCategory.getExternalReferenceCode(), userId,
				assetCategory.getCategoryId(), parentCategoryId, titleMap,
				descriptionMap, vocabularyId, categoryProperties,
				serviceContext);
		}

		return assetCategory;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}