/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.info.item.provider;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"item.class.name=com.liferay.asset.kernel.model.AssetCategory",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class AssetCategoryInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<AssetCategory> {

	@Override
	protected AssetCategory doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					classPKInfoItemIdentifier.getClassPK());

			if (assetCategory == null) {
				throw new NoSuchInfoItemException(
					"Unable to get asset category with info item identifier " +
						infoItemIdentifier);
			}

			return assetCategory;
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(), groupId);

		if (assetCategory == null) {
			throw new NoSuchInfoItemException(
				"Unable to get asset category with info item identifier " +
					infoItemIdentifier);
		}

		return assetCategory;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

}