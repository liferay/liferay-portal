/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for AssetDisplayPageEntry. This utility wraps
 * <code>com.liferay.asset.display.page.service.impl.AssetDisplayPageEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetDisplayPageEntryService
 * @generated
 */
public class AssetDisplayPageEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.display.page.service.impl.AssetDisplayPageEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AssetDisplayPageEntry addAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().addAssetDisplayPageEntry(
			groupId, classNameId, classPK, layoutPageTemplateEntryId, type,
			serviceContext);
	}

	public static AssetDisplayPageEntry addAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().addAssetDisplayPageEntry(
			groupId, classNameId, classPK, layoutPageTemplateEntryId,
			serviceContext);
	}

	public static void deleteAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws Exception {

		getService().deleteAssetDisplayPageEntry(groupId, classNameId, classPK);
	}

	public static AssetDisplayPageEntry fetchAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws Exception {

		return getService().fetchAssetDisplayPageEntry(
			groupId, classNameId, classPK);
	}

	public static List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getService().getAssetDisplayPageEntries(
			classNameId, classTypeId, layoutPageTemplateEntryId,
			defaultTemplate, start, end, orderByComparator);
	}

	public static List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		return getService().
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId);
	}

	public static List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getService().
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId, start, end, orderByComparator);
	}

	public static int getAssetDisplayPageEntriesCount(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate) {

		return getService().getAssetDisplayPageEntriesCount(
			classNameId, classTypeId, layoutPageTemplateEntryId,
			defaultTemplate);
	}

	public static int
		getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		return getService().
			getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static AssetDisplayPageEntry updateAssetDisplayPageEntry(
			long assetDisplayPageEntryId, long layoutPageTemplateEntryId,
			int type)
		throws Exception {

		return getService().updateAssetDisplayPageEntry(
			assetDisplayPageEntryId, layoutPageTemplateEntryId, type);
	}

	public static AssetDisplayPageEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetDisplayPageEntryService>
		_serviceSnapshot = new Snapshot<>(
			AssetDisplayPageEntryServiceUtil.class,
			AssetDisplayPageEntryService.class);

}