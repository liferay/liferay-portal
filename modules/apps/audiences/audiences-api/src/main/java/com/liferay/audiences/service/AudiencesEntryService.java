/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.audiences.model.AudiencesEntry;
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
 * Provides the remote service interface for AudiencesEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AudiencesEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.audiences.service.impl.AudiencesEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the audiences entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AudiencesEntryServiceUtil} if injection and service tracking are not available.
	 */
	public AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, String json, String name,
			ServiceContext serviceContext)
		throws PortalException;

	public AudiencesEntry deleteAudiencesEntry(long audiencesEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AudiencesEntry> getAudiencesEntries(
			long companyId, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AudiencesEntry> getAudiencesEntries(
			long companyId, String name, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAudiencesEntriesCount(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAudiencesEntriesCount(long companyId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AudiencesEntry getAudiencesEntry(long audiencesEntryId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public AudiencesEntry updateAudiencesEntry(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:430037065