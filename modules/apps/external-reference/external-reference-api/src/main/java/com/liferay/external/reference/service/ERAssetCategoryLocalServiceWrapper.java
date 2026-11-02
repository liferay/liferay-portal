/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ERAssetCategoryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ERAssetCategoryLocalService
 * @generated
 */
public class ERAssetCategoryLocalServiceWrapper
	implements ERAssetCategoryLocalService,
			   ServiceWrapper<ERAssetCategoryLocalService> {

	public ERAssetCategoryLocalServiceWrapper() {
		this(null);
	}

	public ERAssetCategoryLocalServiceWrapper(
		ERAssetCategoryLocalService erAssetCategoryLocalService) {

		_erAssetCategoryLocalService = erAssetCategoryLocalService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _erAssetCategoryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public ERAssetCategoryLocalService getWrappedService() {
		return _erAssetCategoryLocalService;
	}

	@Override
	public void setWrappedService(
		ERAssetCategoryLocalService erAssetCategoryLocalService) {

		_erAssetCategoryLocalService = erAssetCategoryLocalService;
	}

	private ERAssetCategoryLocalService _erAssetCategoryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:248834791