/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
public class AssetCategorySelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "taxonomyCategoryIds";
	}

	@Override
	public String getLabel() {
		return "category";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		Group group = _groupLocalService.fetchGroup(
			CompanyThreadLocal.getCompanyId(), GroupConstants.CMS);

		if (group == null) {
			return Collections.emptyList();
		}

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			new ArrayList<>();

		try {
			for (AssetVocabulary assetVocabulary :
					_assetVocabularyLocalService.getGroupVocabularies(
						group.getGroupId())) {

				for (AssetCategory assetCategory :
						_assetCategoryLocalService.getVocabularyCategories(
							assetVocabulary.getVocabularyId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

					selectionFDSFilterItems.add(
						new SelectionFDSFilterItem(
							StringBundler.concat(
								assetCategory.getTitle(locale), " (",
								assetVocabulary.getTitle(locale), ")"),
							assetCategory.getCategoryId()));
				}
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return selectionFDSFilterItems;
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}