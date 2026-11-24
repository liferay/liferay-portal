/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
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
 * Provides the remote service interface for CSDiagramEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryServiceUtil
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
public interface CSDiagramEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cs diagram entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CSDiagramEntryServiceUtil} if injection and service tracking are not available.
	 */
	public CSDiagramEntry addCSDiagramEntry(
			long cpDefinitionId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteCSDiagramEntries(long cpDefinitionId)
		throws PortalException;

	public void deleteCSDiagramEntry(long csDiagramEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CSDiagramEntry fetchCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCProductCSDiagramEntriesCount(long cProductId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CSDiagramEntry> getCSDiagramEntries(
			long cpDefinitionId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCSDiagramEntriesCount(long cpDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CSDiagramEntry getCSDiagramEntry(long csDiagramEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:157383040