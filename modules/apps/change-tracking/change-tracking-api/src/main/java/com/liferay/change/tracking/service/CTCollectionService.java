/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CTCollection. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see CTCollectionServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CTCollectionService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.change.tracking.service.impl.CTCollectionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the ct collection remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CTCollectionServiceUtil} if injection and service tracking are not available.
	 */
	public CTCollection addCTCollection(
			String externalReferenceCode, long ctRemoteId, String name,
			String description)
		throws PortalException;

	public void deleteCTAutoResolutionInfo(long ctAutoResolutionInfoId)
		throws PortalException;

	public CTCollection deleteCTCollection(CTCollection ctCollection)
		throws PortalException;

	public void discardCTEntry(long ctCollectionId, List<CTEntry> ctEntries)
		throws PortalException;

	public void discardCTEntry(
			long ctCollectionId, long modelClassNameId, long modelClassPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CTCollection> getCTCollections(
			int[] statuses, int start, int end,
			OrderByComparator<CTCollection> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CTCollection> getCTCollections(
			int[] statuses, String keywords, int start, int end,
			OrderByComparator<CTCollection> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCTCollectionsCount(int[] statuses, String keywords)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public void moveCTEntries(
			long fromCTCollectionId, long toCTCollectionId,
			List<CTEntry> ctEntries)
		throws PortalException;

	public void moveCTEntry(
			long fromCTCollectionId, long toCTCollectionId,
			long modelClassNameId, long modelClassPK)
		throws PortalException;

	public void publishCTCollection(long userId, long ctCollectionId)
		throws PortalException;

	public CTCollection undoCTCollection(
			long ctCollectionId, long userId, String name, String description)
		throws PortalException;

	public CTCollection updateCTCollection(
			long userId, long ctCollectionId, String name, String description)
		throws PortalException;

}