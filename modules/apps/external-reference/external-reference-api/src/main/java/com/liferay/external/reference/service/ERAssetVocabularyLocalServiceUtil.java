/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the local service utility for ERAssetVocabulary. This utility wraps
 * <code>com.liferay.external.reference.service.impl.ERAssetVocabularyLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ERAssetVocabularyLocalService
 * @generated
 */
public class ERAssetVocabularyLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.external.reference.service.impl.ERAssetVocabularyLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ERAssetVocabularyLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ERAssetVocabularyLocalService>
		_serviceSnapshot = new Snapshot<>(
			ERAssetVocabularyLocalServiceUtil.class,
			ERAssetVocabularyLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:94277626