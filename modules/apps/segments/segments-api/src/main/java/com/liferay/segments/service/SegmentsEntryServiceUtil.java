/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsEntry;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for SegmentsEntry. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryService
 * @generated
 */
public class SegmentsEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria,
			serviceContext);
	}

	public static SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String criteria, String source,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria, source,
			serviceContext);
	}

	public static void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addSegmentsEntryClassPKs(
			segmentsEntryId, classPKs, serviceContext);
	}

	public static SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws PortalException {

		return getService().deleteSegmentsEntry(segmentsEntryId);
	}

	public static void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
		throws PortalException {

		getService().deleteSegmentsEntryClassPKs(segmentsEntryId, classPKs);
	}

	public static SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws PortalException {

		return getService().fetchSegmentsEntryByExternalReferenceCode(
			segmentsEntryERC, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<SegmentsEntry> getSegmentsEntries(long groupId) {
		return getService().getSegmentsEntries(groupId);
	}

	public static List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<SegmentsEntry> getSegmentsEntries(
		long groupId, String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntries(
			groupId, source, start, end, orderByComparator);
	}

	public static int getSegmentsEntriesCount(long groupId) {
		return getService().getSegmentsEntriesCount(groupId);
	}

	public static int getSegmentsEntriesCount(long groupId, String source) {
		return getService().getSegmentsEntriesCount(groupId, source);
	}

	public static SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws PortalException {

		return getService().getSegmentsEntry(segmentsEntryId);
	}

	public static SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws PortalException {

		return getService().getSegmentsEntryByExternalReferenceCode(
			segmentsEntryERC, groupId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<SegmentsEntry> searchSegmentsEntries(
				long companyId, long groupId, String keywords, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchSegmentsEntries(
			companyId, groupId, keywords, start, end, sort);
	}

	public static SegmentsEntry updateSegmentsEntry(
			long segmentsEntryId, String segmentsEntryKey,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSegmentsEntry(
			segmentsEntryId, segmentsEntryKey, nameMap, descriptionMap, active,
			criteria, serviceContext);
	}

	public static SegmentsEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SegmentsEntryService> _serviceSnapshot =
		new Snapshot<>(
			SegmentsEntryServiceUtil.class, SegmentsEntryService.class);

}