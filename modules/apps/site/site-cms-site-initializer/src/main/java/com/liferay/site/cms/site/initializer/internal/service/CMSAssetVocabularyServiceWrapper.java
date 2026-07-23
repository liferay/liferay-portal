/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyServiceWrapper;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cms.site.initializer.internal.util.CMSAssetVocabularyUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alicia García
 */
@Component(service = ServiceWrapper.class)
public class CMSAssetVocabularyServiceWrapper
	extends AssetVocabularyServiceWrapper {

	@Override
	public List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		long companyId = CMSAssetVocabularyUtil.getCompanyId(groupIds);

		if (companyId == CompanyConstants.SYSTEM) {
			return super.getGroupVocabularies(groupIds, visibilityTypes);
		}

		List<AssetVocabulary> assetVocabularies = super.getGroupVocabularies(
			groupIds, visibilityTypes);

		List<AssetVocabulary> cmsAssetVocabularies = ListUtil.filter(
			CMSAssetVocabularyUtil.getCMSAssetVocabularies(groupIds),
			assetVocabulary ->
				(assetVocabulary.getVisibilityType() ==
					AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC) &&
				ArrayUtil.contains(
					visibilityTypes, assetVocabulary.getVisibilityType()));

		if (cmsAssetVocabularies.isEmpty()) {
			return assetVocabularies;
		}

		return ListUtil.unique(
			ListUtil.concat(assetVocabularies, cmsAssetVocabularies));
	}

}