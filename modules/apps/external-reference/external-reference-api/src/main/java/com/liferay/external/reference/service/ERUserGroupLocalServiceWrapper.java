/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ERUserGroupLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ERUserGroupLocalService
 * @generated
 */
public class ERUserGroupLocalServiceWrapper
	implements ERUserGroupLocalService,
			   ServiceWrapper<ERUserGroupLocalService> {

	public ERUserGroupLocalServiceWrapper() {
		this(null);
	}

	public ERUserGroupLocalServiceWrapper(
		ERUserGroupLocalService erUserGroupLocalService) {

		_erUserGroupLocalService = erUserGroupLocalService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _erUserGroupLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public ERUserGroupLocalService getWrappedService() {
		return _erUserGroupLocalService;
	}

	@Override
	public void setWrappedService(
		ERUserGroupLocalService erUserGroupLocalService) {

		_erUserGroupLocalService = erUserGroupLocalService;
	}

	private ERUserGroupLocalService _erUserGroupLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-810762533