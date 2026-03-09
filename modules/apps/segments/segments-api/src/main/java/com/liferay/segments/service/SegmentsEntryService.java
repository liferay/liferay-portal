/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsEntry;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for SegmentsEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SegmentsEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the segments entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SegmentsEntryServiceUtil} if injection and service tracking are not available.
	 */
	public SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String criteria,
			ServiceContext serviceContext)
		throws PortalException;

	public SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String criteria,
			String source, ServiceContext serviceContext)
		throws PortalException;

	public void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			ServiceContext serviceContext)
		throws PortalException;

	public SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws PortalException;

	public void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(
		long groupId, String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsEntriesCount(long groupId, String source);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<SegmentsEntry> searchSegmentsEntries(
			long companyId, long groupId, String keywords, int start, int end,
			Sort sort)
		throws PortalException;

	public SegmentsEntry updateSegmentsEntry(
			long segmentsEntryId, String segmentsEntryKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			boolean active, String criteria, ServiceContext serviceContext)
		throws PortalException;

}