/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link FragmentEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryService
 * @generated
 */
public class FragmentEntryServiceWrapper
	implements FragmentEntryService, ServiceWrapper<FragmentEntryService> {

	public FragmentEntryServiceWrapper() {
		this(null);
	}

	public FragmentEntryServiceWrapper(
		FragmentEntryService fragmentEntryService) {

		_fragmentEntryService = fragmentEntryService;
	}

	@Override
	public FragmentEntry addFragmentEntry(
			long groupId, long fragmentCollectionId, String fragmentEntryKey,
			String name, String css, String html, String js,
			String configuration, long previewFileEntryId, int type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.addFragmentEntry(
			groupId, fragmentCollectionId, fragmentEntryKey, name, css, html,
			js, configuration, previewFileEntryId, type, status,
			serviceContext);
	}

	@Override
	public FragmentEntry addFragmentEntry(
			String externalReferenceCode, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.addFragmentEntry(
			externalReferenceCode, groupId, fragmentCollectionId,
			fragmentEntryKey, name, css, html, js, cacheable, configuration,
			icon, previewFileEntryId, marketplace, readOnly, type, typeOptions,
			status, serviceContext);
	}

	@Override
	public FragmentEntry copyFragmentEntry(
			long groupId, long sourceFragmentEntryId, long fragmentCollectionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.copyFragmentEntry(
			groupId, sourceFragmentEntryId, fragmentCollectionId,
			serviceContext);
	}

	@Override
	public void deleteFragmentEntries(long[] fragmentEntriesIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_fragmentEntryService.deleteFragmentEntries(fragmentEntriesIds);
	}

	@Override
	public FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.deleteFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.deleteFragmentEntry(
			externalReferenceCode, groupId);
	}

	@Override
	public FragmentEntry fetchDraft(long primaryKey) {
		return _fragmentEntryService.fetchDraft(primaryKey);
	}

	@Override
	public FragmentEntry fetchFragmentEntry(long fragmentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.fetchFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry getDraft(long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getDraft(primaryKey);
	}

	@Override
	public java.util.List<Object> getFragmentCompositionsAndFragmentEntries(
			long groupId, long fragmentCollectionId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<?>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<Object> getFragmentCompositionsAndFragmentEntries(
			long groupId, long fragmentCollectionId, String name, int status,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<?>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	@Override
	public int getFragmentCompositionsAndFragmentEntriesCount(
			long groupId, long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.
			getFragmentCompositionsAndFragmentEntriesCount(
				groupId, fragmentCollectionId, status);
	}

	@Override
	public int getFragmentCompositionsAndFragmentEntriesCount(
			long groupId, long fragmentCollectionId, String name, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.
			getFragmentCompositionsAndFragmentEntriesCount(
				groupId, fragmentCollectionId, name, status);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
			long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntries(fragmentCollectionId);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
			long groupId, long fragmentCollectionId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntries(
			groupId, fragmentCollectionId, start, end);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
			long groupId, long fragmentCollectionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntries(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByName(
			long groupId, long fragmentCollectionId, String name, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByName(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByNameAndStatus(
			long groupId, long fragmentCollectionId, String name, int status,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByNameAndStatus(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByStatus(
			long groupId, long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByStatus(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByStatus(
			long groupId, long fragmentCollectionId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByStatus(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByType(
			long groupId, long fragmentCollectionId, int type, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByType(
			groupId, fragmentCollectionId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByTypeAndStatus(
			groupId, fragmentCollectionId, type, status);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesByTypeAndStatus(
			groupId, fragmentCollectionId, type, status, start, end,
			orderByComparator);
	}

	@Override
	public int getFragmentEntriesCount(long groupId, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesCount(
			groupId, fragmentCollectionId);
	}

	@Override
	public int getFragmentEntriesCountByName(
			long groupId, long fragmentCollectionId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesCountByName(
			groupId, fragmentCollectionId, name);
	}

	@Override
	public int getFragmentEntriesCountByNameAndStatus(
			long groupId, long fragmentCollectionId, String name, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesCountByNameAndStatus(
			groupId, fragmentCollectionId, name, status);
	}

	@Override
	public int getFragmentEntriesCountByStatus(
			long groupId, long fragmentCollectionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesCountByStatus(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public int getFragmentEntriesCountByType(
			long groupId, long fragmentCollectionId, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesCountByType(
			groupId, fragmentCollectionId, type);
	}

	@Override
	public int getFragmentEntriesCountByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntriesCountByTypeAndStatus(
			groupId, fragmentCollectionId, type, status);
	}

	@Override
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getFragmentEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _fragmentEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public String[] getTempFileNames(long groupId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.getTempFileNames(groupId, folderName);
	}

	@Override
	public FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.moveFragmentEntry(
			fragmentEntryId, fragmentCollectionId);
	}

	@Override
	public FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.publishDraft(draftFragmentEntry);
	}

	@Override
	public FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.updateDraft(draftFragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(FragmentEntry fragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.updateFragmentEntry(fragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.updateFragmentEntry(
			fragmentEntryId, cacheable);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.updateFragmentEntry(
			fragmentEntryId, previewFileEntryId);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.updateFragmentEntry(
			fragmentEntryId, fragmentCollectionId, name, css, html, js,
			cacheable, configuration, icon, previewFileEntryId, readOnly,
			typeOptions, status);
	}

	@Override
	public FragmentEntry updateFragmentEntry(long fragmentEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryService.updateFragmentEntry(fragmentEntryId, name);
	}

	@Override
	public FragmentEntryService getWrappedService() {
		return _fragmentEntryService;
	}

	@Override
	public void setWrappedService(FragmentEntryService fragmentEntryService) {
		_fragmentEntryService = fragmentEntryService;
	}

	private FragmentEntryService _fragmentEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1319418075