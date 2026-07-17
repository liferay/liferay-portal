/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Fábio Alves
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_RELATED_ASSETS_SECTION,
		"service.ranking:Integer=99"
	},
	service = FDSFilter.class
)
public class AssetCategorySelectionFunnelStageFDSFilter
	extends BaseAssetCategorySelectionFDSFilter {

	@Override
	public String getId() {
		return "cmpFunnelStageCategoryIds";
	}

	@Override
	public String getLabel() {
		return "funnel-stage";
	}

	@Override
	protected List<AssetVocabulary> getAssetVocabularies(long groupId) {
		return getAssetVocabularies("L_CMP_FUNNEL_STAGE", groupId);
	}

}