/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

/**
 * Provides a wrapper for {@link KaleoDefinitionVersionService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionVersionService
 * @generated
 */
public class KaleoDefinitionVersionServiceWrapper
	implements KaleoDefinitionVersionService,
			   ServiceWrapper<KaleoDefinitionVersionService> {

	public KaleoDefinitionVersionServiceWrapper() {
		this(null);
	}

	public KaleoDefinitionVersionServiceWrapper(
		KaleoDefinitionVersionService kaleoDefinitionVersionService) {

		_kaleoDefinitionVersionService = kaleoDefinitionVersionService;
	}

	@Override
	public void deleteKaleoDefinitionVersions(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoDefinitionVersionService.deleteKaleoDefinitionVersions(
			companyId, name);
	}

	@Override
	public KaleoDefinitionVersion getKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionService.getKaleoDefinitionVersion(
			companyId, name, version);
	}

	@Override
	public java.util.List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionService.getKaleoDefinitionVersions(
			companyId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoDefinitionVersionService.getOSGiServiceIdentifier();
	}

	@Override
	public KaleoDefinitionVersionService getWrappedService() {
		return _kaleoDefinitionVersionService;
	}

	@Override
	public void setWrappedService(
		KaleoDefinitionVersionService kaleoDefinitionVersionService) {

		_kaleoDefinitionVersionService = kaleoDefinitionVersionService;
	}

	private KaleoDefinitionVersionService _kaleoDefinitionVersionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1276216675