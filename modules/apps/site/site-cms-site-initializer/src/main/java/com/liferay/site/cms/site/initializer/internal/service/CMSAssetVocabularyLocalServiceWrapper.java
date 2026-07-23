/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceWrapper;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.internal.util.CMSAssetVocabularyUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = ServiceWrapper.class)
public class CMSAssetVocabularyLocalServiceWrapper
	extends AssetVocabularyLocalServiceWrapper {

	@Override
	public List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK) {

		long companyId = CMSAssetVocabularyUtil.getCompanyId(groupIds);

		if (companyId == CompanyConstants.SYSTEM) {
			return super.getGroupsVocabularies(
				groupIds, className, classTypePK);
		}

		List<AssetVocabulary> assetVocabularies = super.getGroupsVocabularies(
			groupIds, className, classTypePK);

		List<AssetVocabulary> cmsAssetVocabularies =
			CMSAssetVocabularyUtil.getCMSAssetVocabularies(groupIds);

		if (Validator.isNotNull(className)) {
			long classNameId = _classNameLocalService.getClassNameId(className);

			cmsAssetVocabularies = ListUtil.filter(
				cmsAssetVocabularies,
				assetVocabulary ->
					assetVocabulary.isAssociatedToClassNameIdAndClassTypePK(
						classNameId, classTypePK));
		}

		if (cmsAssetVocabularies.isEmpty()) {
			return assetVocabularies;
		}

		return ListUtil.unique(
			ListUtil.concat(assetVocabularies, cmsAssetVocabularies));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

}