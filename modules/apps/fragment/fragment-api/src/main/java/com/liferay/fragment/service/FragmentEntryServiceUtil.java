/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for FragmentEntry. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryService
 * @generated
 */
public class FragmentEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static FragmentEntry addFragmentEntry(
			long groupId, long fragmentCollectionId, String fragmentEntryKey,
			String name, String css, String html, String js,
			String configuration, long previewFileEntryId, int type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentEntry(
			groupId, fragmentCollectionId, fragmentEntryKey, name, css, html,
			js, configuration, previewFileEntryId, type, status,
			serviceContext);
	}

	public static FragmentEntry addFragmentEntry(
			String externalReferenceCode, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentEntry(
			externalReferenceCode, groupId, fragmentCollectionId,
			fragmentEntryKey, name, css, html, js, cacheable, configuration,
			icon, previewFileEntryId, marketplace, readOnly, type, typeOptions,
			status, serviceContext);
	}

	public static FragmentEntry copyFragmentEntry(
			long groupId, long sourceFragmentEntryId, long fragmentCollectionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyFragmentEntry(
			groupId, sourceFragmentEntryId, fragmentCollectionId,
			serviceContext);
	}

	public static void deleteFragmentEntries(long[] fragmentEntriesIds)
		throws PortalException {

		getService().deleteFragmentEntries(fragmentEntriesIds);
	}

	public static FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws PortalException {

		return getService().deleteFragmentEntry(fragmentEntryId);
	}

	public static FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentEntry(externalReferenceCode, groupId);
	}

	public static FragmentEntry fetchDraft(long primaryKey) {
		return getService().fetchDraft(primaryKey);
	}

	public static FragmentEntry fetchFragmentEntry(long fragmentEntryId)
		throws PortalException {

		return getService().fetchFragmentEntry(fragmentEntryId);
	}

	public static FragmentEntry getDraft(long primaryKey)
		throws PortalException {

		return getService().getDraft(primaryKey);
	}

	public static List<Object> getFragmentCompositionsAndFragmentEntries(
			long groupId, long fragmentCollectionId, int status, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getService().getFragmentCompositionsAndFragmentEntries(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	public static List<Object> getFragmentCompositionsAndFragmentEntries(
			long groupId, long fragmentCollectionId, String name, int status,
			int start, int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getService().getFragmentCompositionsAndFragmentEntries(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	public static int getFragmentCompositionsAndFragmentEntriesCount(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException {

		return getService().getFragmentCompositionsAndFragmentEntriesCount(
			groupId, fragmentCollectionId, status);
	}

	public static int getFragmentCompositionsAndFragmentEntriesCount(
			long groupId, long fragmentCollectionId, String name, int status)
		throws PortalException {

		return getService().getFragmentCompositionsAndFragmentEntriesCount(
			groupId, fragmentCollectionId, name, status);
	}

	public static List<FragmentEntry> getFragmentEntries(
			long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentEntries(fragmentCollectionId);
	}

	public static List<FragmentEntry> getFragmentEntries(
			long groupId, long fragmentCollectionId, int start, int end)
		throws PortalException {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, start, end);
	}

	public static List<FragmentEntry> getFragmentEntries(
			long groupId, long fragmentCollectionId, int start, int end,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntriesByName(
			long groupId, long fragmentCollectionId, String name, int start,
			int end, OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException {

		return getService().getFragmentEntriesByName(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntriesByNameAndStatus(
			long groupId, long fragmentCollectionId, String name, int status,
			int start, int end,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException {

		return getService().getFragmentEntriesByNameAndStatus(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntriesByStatus(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException {

		return getService().getFragmentEntriesByStatus(
			groupId, fragmentCollectionId, status);
	}

	public static List<FragmentEntry> getFragmentEntriesByStatus(
			long groupId, long fragmentCollectionId, int status, int start,
			int end, OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException {

		return getService().getFragmentEntriesByStatus(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntriesByType(
			long groupId, long fragmentCollectionId, int type, int start,
			int end, OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException {

		return getService().getFragmentEntriesByType(
			groupId, fragmentCollectionId, type, start, end, orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status)
		throws PortalException {

		return getService().getFragmentEntriesByTypeAndStatus(
			groupId, fragmentCollectionId, type, status);
	}

	public static List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status,
			int start, int end,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException {

		return getService().getFragmentEntriesByTypeAndStatus(
			groupId, fragmentCollectionId, type, status, start, end,
			orderByComparator);
	}

	public static int getFragmentEntriesCount(
			long groupId, long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentEntriesCount(
			groupId, fragmentCollectionId);
	}

	public static int getFragmentEntriesCountByName(
			long groupId, long fragmentCollectionId, String name)
		throws PortalException {

		return getService().getFragmentEntriesCountByName(
			groupId, fragmentCollectionId, name);
	}

	public static int getFragmentEntriesCountByNameAndStatus(
			long groupId, long fragmentCollectionId, String name, int status)
		throws PortalException {

		return getService().getFragmentEntriesCountByNameAndStatus(
			groupId, fragmentCollectionId, name, status);
	}

	public static int getFragmentEntriesCountByStatus(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException {

		return getService().getFragmentEntriesCountByStatus(
			groupId, fragmentCollectionId, status);
	}

	public static int getFragmentEntriesCountByType(
			long groupId, long fragmentCollectionId, int type)
		throws PortalException {

		return getService().getFragmentEntriesCountByType(
			groupId, fragmentCollectionId, type);
	}

	public static int getFragmentEntriesCountByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status)
		throws PortalException {

		return getService().getFragmentEntriesCountByTypeAndStatus(
			groupId, fragmentCollectionId, type, status);
	}

	public static FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentEntryByExternalReferenceCode(
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

	public static String[] getTempFileNames(long groupId, String folderName)
		throws PortalException {

		return getService().getTempFileNames(groupId, folderName);
	}

	public static FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws PortalException {

		return getService().moveFragmentEntry(
			fragmentEntryId, fragmentCollectionId);
	}

	public static FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		return getService().publishDraft(draftFragmentEntry);
	}

	public static FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		return getService().updateDraft(draftFragmentEntry);
	}

	public static FragmentEntry updateFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException {

		return getService().updateFragmentEntry(fragmentEntry);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws PortalException {

		return getService().updateFragmentEntry(fragmentEntryId, cacheable);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws PortalException {

		return getService().updateFragmentEntry(
			fragmentEntryId, previewFileEntryId);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws PortalException {

		return getService().updateFragmentEntry(
			fragmentEntryId, fragmentCollectionId, name, css, html, js,
			cacheable, configuration, icon, previewFileEntryId, readOnly,
			typeOptions, status);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, String name)
		throws PortalException {

		return getService().updateFragmentEntry(fragmentEntryId, name);
	}

	public static FragmentEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentEntryService> _serviceSnapshot =
		new Snapshot<>(
			FragmentEntryServiceUtil.class, FragmentEntryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-812016971