/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ERAssetVocabularyLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ERAssetVocabularyLocalService
 * @generated
 */
public class ERAssetVocabularyLocalServiceWrapper
	implements ERAssetVocabularyLocalService,
			   ServiceWrapper<ERAssetVocabularyLocalService> {

	public ERAssetVocabularyLocalServiceWrapper() {
		this(null);
	}

	public ERAssetVocabularyLocalServiceWrapper(
		ERAssetVocabularyLocalService erAssetVocabularyLocalService) {

		_erAssetVocabularyLocalService = erAssetVocabularyLocalService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _erAssetVocabularyLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public ERAssetVocabularyLocalService getWrappedService() {
		return _erAssetVocabularyLocalService;
	}

	@Override
	public void setWrappedService(
		ERAssetVocabularyLocalService erAssetVocabularyLocalService) {

		_erAssetVocabularyLocalService = erAssetVocabularyLocalService;
	}

	private ERAssetVocabularyLocalService _erAssetVocabularyLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:704859479