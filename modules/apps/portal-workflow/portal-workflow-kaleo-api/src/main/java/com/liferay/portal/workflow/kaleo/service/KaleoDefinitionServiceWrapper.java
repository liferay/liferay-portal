/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

/**
 * Provides a wrapper for {@link KaleoDefinitionService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionService
 * @generated
 */
public class KaleoDefinitionServiceWrapper
	implements KaleoDefinitionService, ServiceWrapper<KaleoDefinitionService> {

	public KaleoDefinitionServiceWrapper() {
		this(null);
	}

	public KaleoDefinitionServiceWrapper(
		KaleoDefinitionService kaleoDefinitionService) {

		_kaleoDefinitionService = kaleoDefinitionService;
	}

	@Override
	public KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, boolean system,
			int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.addKaleoDefinition(
			externalReferenceCode, name, title, description, content, scope,
			system, version, serviceContext);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(long kaleoDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.getKaleoDefinition(kaleoDefinitionId);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.getKaleoDefinition(
			externalReferenceCode, companyId);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.getKaleoDefinition(name, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoDefinitionService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<KaleoDefinition> getScopeKaleoDefinitions(
			String scope, boolean active, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
				orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.getScopeKaleoDefinitions(
			scope, active, start, end, orderByComparator, serviceContext);
	}

	@Override
	public java.util.List<KaleoDefinition> getScopeKaleoDefinitions(
			String scope, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
				orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.getScopeKaleoDefinitions(
			scope, start, end, orderByComparator, serviceContext);
	}

	@Override
	public KaleoDefinition updateKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, boolean system,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionService.updateKaleoDefinition(
			externalReferenceCode, kaleoDefinitionId, title, description,
			content, system, serviceContext);
	}

	@Override
	public KaleoDefinitionService getWrappedService() {
		return _kaleoDefinitionService;
	}

	@Override
	public void setWrappedService(
		KaleoDefinitionService kaleoDefinitionService) {

		_kaleoDefinitionService = kaleoDefinitionService;
	}

	private KaleoDefinitionService _kaleoDefinitionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1493128664