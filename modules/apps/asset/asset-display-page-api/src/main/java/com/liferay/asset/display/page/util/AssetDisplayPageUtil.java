/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.util;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.info.display.contributor.LayoutDisplayPageProviderRegistryUtil;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalServiceUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Jürgen Kappler
 */
public class AssetDisplayPageUtil {

	public static LayoutPageTemplateEntry
		getAssetDisplayPageLayoutPageTemplateEntry(
			long groupId, InfoItemReference infoItemReference) {

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			LayoutDisplayPageProviderRegistryUtil.
				getLayoutDisplayPageProviderRegistry();

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					infoItemReference.getClassName());

		if (layoutDisplayPageProvider == null) {
			return null;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				groupId, infoItemReference);

		if (layoutDisplayPageObjectProvider == null) {
			return null;
		}

		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					groupId, layoutDisplayPageObjectProvider.getClassNameId(),
					layoutDisplayPageObjectProvider.getClassTypeId());

		return _getAssetDisplayPage(
			groupId, layoutDisplayPageObjectProvider.getClassNameId(),
			layoutDisplayPageObjectProvider.getClassPK(),
			defaultLayoutPageTemplateEntry, layoutDisplayPageProvider);
	}

	public static LayoutPageTemplateEntry
		getAssetDisplayPageLayoutPageTemplateEntry(
			long groupId, long classNameId, long classPK, long classTypeId) {

		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					groupId, classNameId, classTypeId);

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			LayoutDisplayPageProviderRegistryUtil.
				getLayoutDisplayPageProviderRegistry();

		return _getAssetDisplayPage(
			groupId, classNameId, classPK, defaultLayoutPageTemplateEntry,
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					PortalUtil.getClassName(classNameId)));
	}

	public static boolean hasAssetDisplayPage(
		long groupId, AssetEntry assetEntry) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getAssetDisplayPageLayoutPageTemplateEntry(
				groupId, assetEntry.getClassNameId(), assetEntry.getClassPK(),
				assetEntry.getClassTypeId());

		if (layoutPageTemplateEntry != null) {
			return true;
		}

		return false;
	}

	public static boolean hasAssetDisplayPage(
		long groupId, InfoItemReference infoItemReference) {

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			LayoutDisplayPageProviderRegistryUtil.
				getLayoutDisplayPageProviderRegistry();

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					infoItemReference.getClassName());

		if (layoutDisplayPageProvider == null) {
			return false;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				groupId, infoItemReference);

		if (layoutDisplayPageObjectProvider == null) {
			return false;
		}

		return hasAssetDisplayPage(
			groupId, layoutDisplayPageObjectProvider.getClassNameId(),
			layoutDisplayPageObjectProvider.getClassPK(),
			layoutDisplayPageObjectProvider.getClassTypeId());
	}

	public static boolean hasAssetDisplayPage(
		long groupId, long classNameId, long classPK, long classTypeId) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getAssetDisplayPageLayoutPageTemplateEntry(
				groupId, classNameId, classPK, classTypeId);

		if (layoutPageTemplateEntry != null) {
			return true;
		}

		return false;
	}

	private static LayoutPageTemplateEntry _getAssetDisplayPage(
		long groupId, long classNameId, long classPK,
		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry,
		LayoutDisplayPageProvider<?> layoutDisplayPageProvider) {

		AssetDisplayPageEntry assetDisplayPageEntry =
			AssetDisplayPageEntryLocalServiceUtil.fetchAssetDisplayPageEntry(
				groupId, classNameId, classPK);

		if ((assetDisplayPageEntry == null) ||
			(layoutDisplayPageProvider == null)) {

			return defaultLayoutPageTemplateEntry;
		}

		if (layoutDisplayPageProvider.inheritable() &&
			(assetDisplayPageEntry.getType() ==
				AssetDisplayPageConstants.TYPE_INHERITED)) {

			InfoItemReference infoItemReference = new InfoItemReference(
				PortalUtil.getClassName(classNameId), classPK);

			LayoutDisplayPageObjectProvider<?>
				parentLayoutDisplayPageObjectProvider =
					layoutDisplayPageProvider.
						getParentLayoutDisplayPageObjectProvider(
							infoItemReference);

			if (parentLayoutDisplayPageObjectProvider != null) {
				return _getAssetDisplayPage(
					groupId, classNameId,
					parentLayoutDisplayPageObjectProvider.getClassPK(),
					defaultLayoutPageTemplateEntry, layoutDisplayPageProvider);
			}
		}

		if (assetDisplayPageEntry.getType() ==
				AssetDisplayPageConstants.TYPE_NONE) {

			return null;
		}

		if (assetDisplayPageEntry.getType() ==
				AssetDisplayPageConstants.TYPE_SPECIFIC) {

			return LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(
					assetDisplayPageEntry.getLayoutPageTemplateEntryId());
		}

		return defaultLayoutPageTemplateEntry;
	}

}