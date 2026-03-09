/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.segments.model.SegmentsEntry;

/**
 * Provides a wrapper for {@link SegmentsEntryService}.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryService
 * @generated
 */
public class SegmentsEntryServiceWrapper
	implements SegmentsEntryService, ServiceWrapper<SegmentsEntryService> {

	public SegmentsEntryServiceWrapper() {
		this(null);
	}

	public SegmentsEntryServiceWrapper(
		SegmentsEntryService segmentsEntryService) {

		_segmentsEntryService = segmentsEntryService;
	}

	@Override
	public SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria,
			serviceContext);
	}

	@Override
	public SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria, String source,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria, source,
			serviceContext);
	}

	@Override
	public void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryService.addSegmentsEntryClassPKs(
			segmentsEntryId, classPKs, serviceContext);
	}

	@Override
	public SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.deleteSegmentsEntry(segmentsEntryId);
	}

	@Override
	public void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryService.deleteSegmentsEntryClassPKs(
			segmentsEntryId, classPKs);
	}

	@Override
	public SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.fetchSegmentsEntryByExternalReferenceCode(
			segmentsEntryERC, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _segmentsEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(long groupId) {
		return _segmentsEntryService.getSegmentsEntries(groupId);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryService.getSegmentsEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		long groupId, String source, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryService.getSegmentsEntries(
			groupId, source, start, end, orderByComparator);
	}

	@Override
	public int getSegmentsEntriesCount(long groupId) {
		return _segmentsEntryService.getSegmentsEntriesCount(groupId);
	}

	@Override
	public int getSegmentsEntriesCount(long groupId, String source) {
		return _segmentsEntryService.getSegmentsEntriesCount(groupId, source);
	}

	@Override
	public SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.getSegmentsEntry(segmentsEntryId);
	}

	@Override
	public SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.getSegmentsEntryByExternalReferenceCode(
			segmentsEntryERC, groupId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<SegmentsEntry>
			searchSegmentsEntries(
				long companyId, long groupId, String keywords, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.searchSegmentsEntries(
			companyId, groupId, keywords, start, end, sort);
	}

	@Override
	public SegmentsEntry updateSegmentsEntry(
			long segmentsEntryId, String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryService.updateSegmentsEntry(
			segmentsEntryId, segmentsEntryKey, nameMap, descriptionMap, active,
			criteria, serviceContext);
	}

	@Override
	public SegmentsEntryService getWrappedService() {
		return _segmentsEntryService;
	}

	@Override
	public void setWrappedService(SegmentsEntryService segmentsEntryService) {
		_segmentsEntryService = segmentsEntryService;
	}

	private SegmentsEntryService _segmentsEntryService;

}