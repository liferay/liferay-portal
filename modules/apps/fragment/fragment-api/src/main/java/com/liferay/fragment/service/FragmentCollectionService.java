/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.repository.model.FileEntry;
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
 * Provides the remote service interface for FragmentCollection. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCollectionServiceUtil
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
public interface FragmentCollectionService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCollectionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the fragment collection remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FragmentCollectionServiceUtil} if injection and service tracking are not available.
	 */
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException;

	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace, ServiceContext serviceContext)
		throws PortalException;

	public FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws PortalException;

	public FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws PortalException;

	public void deleteFragmentCollections(long[] fragmentCollectionIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection fetchFragmentCollection(long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExportableFragmentCollectionsCountByGroupId(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection getFragmentCollection(long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection getFragmentCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFragmentCollectionFileEntries(
			long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long groupId, boolean includeSystem);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long[] groupIds, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempFileNames(long groupId, String folderName)
		throws PortalException;

	public FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:363650178