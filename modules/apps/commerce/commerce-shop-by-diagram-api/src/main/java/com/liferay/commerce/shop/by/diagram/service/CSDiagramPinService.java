/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CSDiagramPin. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramPinServiceUtil
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
public interface CSDiagramPinService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramPinServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cs diagram pin remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CSDiagramPinServiceUtil} if injection and service tracking are not available.
	 */
	public CSDiagramPin addCSDiagramPin(
			long cpDefinitionId, double positionX, double positionY,
			String sequence)
		throws PortalException;

	public void deleteCSDiagramPin(long csDiagramPinId) throws PortalException;

	public void deleteCSDiagramPins(long cpDefinitionId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CSDiagramPin fetchCSDiagramPin(long csDiagramPinId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CSDiagramPin getCSDiagramPin(long csDiagramPinId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CSDiagramPin> getCSDiagramPins(
			long cpDefinitionId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCSDiagramPinsCount(long cpDefinitionId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CSDiagramPin updateCSDiagramPin(
			long csDiagramPinId, double positionX, double positionY,
			String sequence)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-297631200