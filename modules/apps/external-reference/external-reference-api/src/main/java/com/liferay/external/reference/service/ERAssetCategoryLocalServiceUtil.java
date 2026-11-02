/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the local service utility for ERAssetCategory. This utility wraps
 * <code>com.liferay.external.reference.service.impl.ERAssetCategoryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ERAssetCategoryLocalService
 * @generated
 */
public class ERAssetCategoryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.external.reference.service.impl.ERAssetCategoryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ERAssetCategoryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ERAssetCategoryLocalService>
		_serviceSnapshot = new Snapshot<>(
			ERAssetCategoryLocalServiceUtil.class,
			ERAssetCategoryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1303967038