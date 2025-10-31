/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.reference.service.impl;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.external.reference.service.base.ERAssetVocabularyLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetVocabulary",
	service = AopService.class
)
public class ERAssetVocabularyLocalServiceImpl
	extends ERAssetVocabularyLocalServiceBaseImpl {

	@Override
	public AssetVocabulary addOrUpdateVocabulary(
			String externalReferenceCode, long userId, long groupId,
			String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, user.getCompanyId());

		if (assetVocabulary == null) {
			assetVocabulary = _assetVocabularyLocalService.addVocabulary(
				userId, groupId, title, titleMap, descriptionMap, settings,
				serviceContext);

			assetVocabulary.setExternalReferenceCode(externalReferenceCode);

			assetVocabulary =
				_assetVocabularyLocalService.updateAssetVocabulary(
					assetVocabulary);
		}
		else {
			assetVocabulary = _assetVocabularyLocalService.updateVocabulary(
				assetVocabulary.getVocabularyId(), title, titleMap,
				descriptionMap, settings, assetVocabulary.getVisibilityType(),
				serviceContext);
		}

		return assetVocabulary;
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private UserLocalService _userLocalService;

}