/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ERUserLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ERUserLocalService
 * @generated
 */
public class ERUserLocalServiceWrapper
	implements ERUserLocalService, ServiceWrapper<ERUserLocalService> {

	public ERUserLocalServiceWrapper() {
		this(null);
	}

	public ERUserLocalServiceWrapper(ERUserLocalService erUserLocalService) {
		_erUserLocalService = erUserLocalService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _erUserLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public ERUserLocalService getWrappedService() {
		return _erUserLocalService;
	}

	@Override
	public void setWrappedService(ERUserLocalService erUserLocalService) {
		_erUserLocalService = erUserLocalService;
	}

	private ERUserLocalService _erUserLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-679089589