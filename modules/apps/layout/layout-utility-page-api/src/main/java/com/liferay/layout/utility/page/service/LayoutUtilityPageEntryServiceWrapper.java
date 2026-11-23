/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service;

import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link LayoutUtilityPageEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutUtilityPageEntryService
 * @generated
 */
public class LayoutUtilityPageEntryServiceWrapper
	implements LayoutUtilityPageEntryService,
			   ServiceWrapper<LayoutUtilityPageEntryService> {

	public LayoutUtilityPageEntryServiceWrapper() {
		this(null);
	}

	public LayoutUtilityPageEntryServiceWrapper(
		LayoutUtilityPageEntryService layoutUtilityPageEntryService) {

		_layoutUtilityPageEntryService = layoutUtilityPageEntryService;
	}

	@Override
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, String masterLayoutPageTemplateEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
			externalReferenceCode, groupId, plid, previewFileEntryId,
			defaultLayoutUtilityPageEntry, name, type,
			masterLayoutPageTemplateEntryERC, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long groupId, long sourceLayoutUtilityPageEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _layoutUtilityPageEntryService.copyLayoutUtilityPageEntry(
			groupId, sourceLayoutUtilityPageEntryId, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.deleteLayoutUtilityPageEntry(
			externalReferenceCode, groupId);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.fetchLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.getDefaultLayoutUtilityPageEntry(
			groupId, type);
	}

	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId) {

		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntries(
			groupId);
	}

	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutUtilityPageEntry>
			orderByComparator) {

		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutUtilityPageEntry>
			orderByComparator) {

		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntries(
			groupId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String keyword, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutUtilityPageEntry>
			orderByComparator) {

		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntries(
			groupId, keyword, types, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutUtilityPageEntry>
			orderByComparator) {

		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntries(
			groupId, types, start, end, orderByComparator);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(long groupId) {
		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntriesCount(
			groupId);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(
		long groupId, String keyword, String[] types) {

		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntriesCount(
			groupId, keyword, types);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(long groupId, String[] types) {
		return _layoutUtilityPageEntryService.getLayoutUtilityPageEntriesCount(
			groupId, types);
	}

	@Override
	public LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.
			getLayoutUtilityPageEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutUtilityPageEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.setDefaultLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry unsetDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.
			unsetDefaultLayoutUtilityPageEntry(layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, previewFileEntryId, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, name, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntryService getWrappedService() {
		return _layoutUtilityPageEntryService;
	}

	@Override
	public void setWrappedService(
		LayoutUtilityPageEntryService layoutUtilityPageEntryService) {

		_layoutUtilityPageEntryService = layoutUtilityPageEntryService;
	}

	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:72606788