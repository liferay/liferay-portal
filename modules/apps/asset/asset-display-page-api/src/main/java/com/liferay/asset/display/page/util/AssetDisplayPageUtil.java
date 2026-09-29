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
import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
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
					CompanyThreadLocal.getCompanyId(),
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
			_fetchDefaultLayoutPageTemplateEntry(
				layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassTypeId(), groupId);

		return _getAssetDisplayPage(
			groupId, layoutDisplayPageObjectProvider.getClassNameId(),
			layoutDisplayPageObjectProvider.getClassPK(),
			defaultLayoutPageTemplateEntry, layoutDisplayPageProvider);
	}

	public static LayoutPageTemplateEntry
		getAssetDisplayPageLayoutPageTemplateEntry(
			long groupId, long classNameId, long classPK, long classTypeId) {

		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			_fetchDefaultLayoutPageTemplateEntry(
				classNameId, classTypeId, groupId);

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			LayoutDisplayPageProviderRegistryUtil.
				getLayoutDisplayPageProviderRegistry();

		return _getAssetDisplayPage(
			groupId, classNameId, classPK, defaultLayoutPageTemplateEntry,
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					CompanyThreadLocal.getCompanyId(),
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
					CompanyThreadLocal.getCompanyId(),
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

	private static LayoutPageTemplateEntry _fetchDefaultLayoutPageTemplateEntry(
		long classNameId, long classTypeId, long groupId) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					groupId, classNameId, classTypeId);

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry;
		}

		for (long designLibraryGroupId :
				_getConnectedDesignLibraryGroupIds(groupId)) {

			layoutPageTemplateEntry =
				LayoutPageTemplateEntryServiceUtil.
					fetchDefaultLayoutPageTemplateEntry(
						designLibraryGroupId, classNameId, classTypeId);

			if (layoutPageTemplateEntry != null) {
				return layoutPageTemplateEntry;
			}
		}

		return null;
	}

	private static LayoutPageTemplateEntry _getAssetDisplayPage(
		long groupId, long classNameId, long classPK,
		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry,
		LayoutDisplayPageProvider<?> layoutDisplayPageProvider) {

		int count =
			AssetDisplayPageEntryLocalServiceUtil.
				getAssetDisplayPageEntriesCount(groupId, classNameId);

		if (count == 0) {
			return defaultLayoutPageTemplateEntry;
		}

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

	private static long[] _getConnectedDesignLibraryGroupIds(long groupId) {
		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return new long[0];
		}

		try {
			return DesignLibraryUtil.getConnectedDesignLibraryGroupIds(
				group.getCompanyId(), groupId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return new long[0];
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetDisplayPageUtil.class);

}