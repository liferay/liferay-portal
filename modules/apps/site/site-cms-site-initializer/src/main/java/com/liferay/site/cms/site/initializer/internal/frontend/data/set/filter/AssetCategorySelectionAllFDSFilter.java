/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marco Leo
 * @author Roberto Díaz
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_RELATED_ASSETS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.CONTENTS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.STRUCTURE_USAGES,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_CONTENTS_FOLDER,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_FILES_FOLDER,
		"service.ranking:Integer=99"
	},
	service = FDSFilter.class
)
public class AssetCategorySelectionAllFDSFilter
	extends BaseAssetCategorySelectionFDSFilter {

	@Override
	public String getId() {
		return "taxonomyCategoryIds";
	}

	@Override
	public String getLabel() {
		return "category";
	}

	@Override
	protected List<AssetVocabulary> getAssetVocabularies(long groupId)
		throws PortalException {

		return assetVocabularyLocalService.getGroupVocabularies(groupId);
	}

	@Override
	protected String getSelectionFDSFilterItemLabel(
		AssetCategory assetCategory, AssetVocabulary assetVocabulary,
		Locale locale) {

		return StringBundler.concat(
			assetCategory.getTitle(locale), " (",
			assetVocabulary.getTitle(locale), ")");
	}

}