/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CSDiagramPinService}.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramPinService
 * @generated
 */
public class CSDiagramPinServiceWrapper
	implements CSDiagramPinService, ServiceWrapper<CSDiagramPinService> {

	public CSDiagramPinServiceWrapper() {
		this(null);
	}

	public CSDiagramPinServiceWrapper(CSDiagramPinService csDiagramPinService) {
		_csDiagramPinService = csDiagramPinService;
	}

	@Override
	public CSDiagramPin addCSDiagramPin(
			long cpDefinitionId, double positionX, double positionY,
			String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramPinService.addCSDiagramPin(
			cpDefinitionId, positionX, positionY, sequence);
	}

	@Override
	public void deleteCSDiagramPin(long csDiagramPinId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_csDiagramPinService.deleteCSDiagramPin(csDiagramPinId);
	}

	@Override
	public void deleteCSDiagramPins(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_csDiagramPinService.deleteCSDiagramPins(cpDefinitionId);
	}

	@Override
	public CSDiagramPin fetchCSDiagramPin(long csDiagramPinId) {
		return _csDiagramPinService.fetchCSDiagramPin(csDiagramPinId);
	}

	@Override
	public CSDiagramPin getCSDiagramPin(long csDiagramPinId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramPinService.getCSDiagramPin(csDiagramPinId);
	}

	@Override
	public java.util.List<CSDiagramPin> getCSDiagramPins(
			long cpDefinitionId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramPinService.getCSDiagramPins(
			cpDefinitionId, start, end);
	}

	@Override
	public int getCSDiagramPinsCount(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramPinService.getCSDiagramPinsCount(cpDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _csDiagramPinService.getOSGiServiceIdentifier();
	}

	@Override
	public CSDiagramPin updateCSDiagramPin(
			long csDiagramPinId, double positionX, double positionY,
			String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramPinService.updateCSDiagramPin(
			csDiagramPinId, positionX, positionY, sequence);
	}

	@Override
	public CSDiagramPinService getWrappedService() {
		return _csDiagramPinService;
	}

	@Override
	public void setWrappedService(CSDiagramPinService csDiagramPinService) {
		_csDiagramPinService = csDiagramPinService;
	}

	private CSDiagramPinService _csDiagramPinService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1891200749