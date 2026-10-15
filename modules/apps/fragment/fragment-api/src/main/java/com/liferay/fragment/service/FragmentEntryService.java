/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for FragmentEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryServiceUtil
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
public interface FragmentEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the fragment entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FragmentEntryServiceUtil} if injection and service tracking are not available.
	 */
	public FragmentEntry addFragmentEntry(
			long groupId, long fragmentCollectionId, String fragmentEntryKey,
			String name, String css, String html, String js,
			String configuration, long previewFileEntryId, int type, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public FragmentEntry addFragmentEntry(
			String externalReferenceCode, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status, ServiceContext serviceContext)
		throws PortalException;

	public FragmentEntry copyFragmentEntry(
			long groupId, long sourceFragmentEntryId, long fragmentCollectionId,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteFragmentEntries(long[] fragmentEntriesIds)
		throws PortalException;

	public FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws PortalException;

	public FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchDraft(long primaryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry fetchFragmentEntry(long fragmentEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getDraft(long primaryKey) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFragmentCompositionsAndFragmentEntries(
			long groupId, long fragmentCollectionId, int status, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFragmentCompositionsAndFragmentEntries(
			long groupId, long fragmentCollectionId, String name, int status,
			int start, int end, OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCompositionsAndFragmentEntriesCount(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCompositionsAndFragmentEntriesCount(
			long groupId, long fragmentCollectionId, String name, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
			long groupId, long fragmentCollectionId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntries(
			long groupId, long fragmentCollectionId, int start, int end,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByName(
			long groupId, long fragmentCollectionId, String name, int start,
			int end, OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByNameAndStatus(
			long groupId, long fragmentCollectionId, String name, int status,
			int start, int end,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByStatus(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByStatus(
			long groupId, long fragmentCollectionId, int status, int start,
			int end, OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByType(
			long groupId, long fragmentCollectionId, int type, int start,
			int end, OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status,
			int start, int end,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCount(long groupId, long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCountByName(
			long groupId, long fragmentCollectionId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCountByNameAndStatus(
			long groupId, long fragmentCollectionId, String name, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCountByStatus(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCountByType(
			long groupId, long fragmentCollectionId, int type)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntriesCountByTypeAndStatus(
			long groupId, long fragmentCollectionId, int type, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempFileNames(long groupId, String folderName)
		throws PortalException;

	public FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws PortalException;

	public FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException;

	public FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws PortalException;

	public FragmentEntry updateFragmentEntry(long fragmentEntryId, String name)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:2026087120