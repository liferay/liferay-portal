/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CSDiagramEntryService}.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryService
 * @generated
 */
public class CSDiagramEntryServiceWrapper
	implements CSDiagramEntryService, ServiceWrapper<CSDiagramEntryService> {

	public CSDiagramEntryServiceWrapper() {
		this(null);
	}

	public CSDiagramEntryServiceWrapper(
		CSDiagramEntryService csDiagramEntryService) {

		_csDiagramEntryService = csDiagramEntryService;
	}

	@Override
	public CSDiagramEntry addCSDiagramEntry(
			long cpDefinitionId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.addCSDiagramEntry(
			cpDefinitionId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	@Override
	public void deleteCSDiagramEntries(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_csDiagramEntryService.deleteCSDiagramEntries(cpDefinitionId);
	}

	@Override
	public void deleteCSDiagramEntry(long csDiagramEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_csDiagramEntryService.deleteCSDiagramEntry(csDiagramEntryId);
	}

	@Override
	public CSDiagramEntry fetchCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.fetchCSDiagramEntry(
			cpDefinitionId, sequence);
	}

	@Override
	public java.util.List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.getCProductCSDiagramEntries(
			cProductId, start, end, orderByComparator);
	}

	@Override
	public int getCProductCSDiagramEntriesCount(long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.getCProductCSDiagramEntriesCount(
			cProductId);
	}

	@Override
	public java.util.List<CSDiagramEntry> getCSDiagramEntries(
			long cpDefinitionId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.getCSDiagramEntries(
			cpDefinitionId, start, end);
	}

	@Override
	public int getCSDiagramEntriesCount(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.getCSDiagramEntriesCount(cpDefinitionId);
	}

	@Override
	public CSDiagramEntry getCSDiagramEntry(long csDiagramEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.getCSDiagramEntry(csDiagramEntryId);
	}

	@Override
	public CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.getCSDiagramEntry(
			cpDefinitionId, sequence);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _csDiagramEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryService.updateCSDiagramEntry(
			csDiagramEntryId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	@Override
	public CSDiagramEntryService getWrappedService() {
		return _csDiagramEntryService;
	}

	@Override
	public void setWrappedService(CSDiagramEntryService csDiagramEntryService) {
		_csDiagramEntryService = csDiagramEntryService;
	}

	private CSDiagramEntryService _csDiagramEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1273106349