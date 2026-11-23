/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service;

import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for LayoutUtilityPageEntry. This utility wraps
 * <code>com.liferay.layout.utility.page.service.impl.LayoutUtilityPageEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutUtilityPageEntryService
 * @generated
 */
public class LayoutUtilityPageEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.utility.page.service.impl.LayoutUtilityPageEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, String masterLayoutPageTemplateEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutUtilityPageEntry(
			externalReferenceCode, groupId, plid, previewFileEntryId,
			defaultLayoutUtilityPageEntry, name, type,
			masterLayoutPageTemplateEntryERC, serviceContext);
	}

	public static LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long groupId, long sourceLayoutUtilityPageEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().copyLayoutUtilityPageEntry(
			groupId, sourceLayoutUtilityPageEntryId, serviceContext);
	}

	public static LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		return getService().deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	public static LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteLayoutUtilityPageEntry(
			externalReferenceCode, groupId);
	}

	public static LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		return getService().fetchLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	public static LayoutUtilityPageEntry
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchLayoutUtilityPageEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws PortalException {

		return getService().getDefaultLayoutUtilityPageEntry(groupId, type);
	}

	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId) {

		return getService().getLayoutUtilityPageEntries(groupId);
	}

	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return getService().getLayoutUtilityPageEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return getService().getLayoutUtilityPageEntries(
			groupId, type, start, end, orderByComparator);
	}

	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String keyword, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return getService().getLayoutUtilityPageEntries(
			groupId, keyword, types, start, end, orderByComparator);
	}

	public static List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return getService().getLayoutUtilityPageEntries(
			groupId, types, start, end, orderByComparator);
	}

	public static int getLayoutUtilityPageEntriesCount(long groupId) {
		return getService().getLayoutUtilityPageEntriesCount(groupId);
	}

	public static int getLayoutUtilityPageEntriesCount(
		long groupId, String keyword, String[] types) {

		return getService().getLayoutUtilityPageEntriesCount(
			groupId, keyword, types);
	}

	public static int getLayoutUtilityPageEntriesCount(
		long groupId, String[] types) {

		return getService().getLayoutUtilityPageEntriesCount(groupId, types);
	}

	public static LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getLayoutUtilityPageEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		return getService().setDefaultLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	public static LayoutUtilityPageEntry unsetDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		return getService().unsetDefaultLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	public static LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, previewFileEntryId, serviceContext);
	}

	public static LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, name, serviceContext);
	}

	public static LayoutUtilityPageEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutUtilityPageEntryService>
		_serviceSnapshot = new Snapshot<>(
			LayoutUtilityPageEntryServiceUtil.class,
			LayoutUtilityPageEntryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:219290470