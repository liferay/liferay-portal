/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.layout.display.page;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = LayoutDisplayPageProvider.class)
public class AssetCategoryLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<AssetCategory> {

	@Override
	public String getClassName() {
		return AssetCategory.class.getName();
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_ASSET_CATEGORY;
	}

	@Override
	public LayoutDisplayPageObjectProvider<AssetCategory>
		getLayoutDisplayPageObjectProvider(AssetCategory assetCategory) {

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			assetCategory, _portal);
	}

	@Override
	public LayoutDisplayPageObjectProvider<AssetCategory>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(
				GetterUtil.getLong(urlTitle));

		if (assetCategory == null) {
			return null;
		}

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			assetCategory, _portal);
	}

	@Override
	public LayoutDisplayPageObjectProvider<AssetCategory>
		getParentLayoutDisplayPageObjectProvider(
			InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(
				classPKInfoItemIdentifier.getClassPK());

		if (assetCategory == null) {
			return null;
		}

		AssetCategory parentCategory = assetCategory.getParentCategory();

		if (parentCategory == null) {
			return null;
		}

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			parentCategory, _portal);
	}

	@Override
	public boolean inheritable() {
		return true;
	}

	@Override
	protected AssetCategoryLayoutDisplayPageObjectProvider
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			return null;
		}

		AssetCategory assetCategory = null;

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			assetCategory = _assetCategoryLocalService.fetchAssetCategory(
				classPKInfoItemIdentifier.getClassPK());
		}
		else {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			assetCategory =
				_assetCategoryLocalService.
					fetchAssetCategoryByExternalReferenceCode(
						ercInfoItemIdentifier.getExternalReferenceCode(),
						groupId);
		}

		if (assetCategory == null) {
			return null;
		}

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			assetCategory, _portal);
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private Portal _portal;

}