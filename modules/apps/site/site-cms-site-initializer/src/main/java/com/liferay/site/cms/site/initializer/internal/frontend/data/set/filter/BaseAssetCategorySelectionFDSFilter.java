/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Roberto Díaz
 * @author Fábio Alves
 */
public abstract class BaseAssetCategorySelectionFDSFilter
	extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		Group group = groupLocalService.fetchGroup(
			CompanyThreadLocal.getCompanyId(), GroupConstants.CMS);

		if (group == null) {
			return Collections.emptyList();
		}

		try {
			List<SelectionFDSFilterItem> selectionFDSFilterItems =
				new ArrayList<>();

			for (AssetVocabulary assetVocabulary :
					getAssetVocabularies(group.getGroupId())) {

				for (AssetCategory assetCategory :
						assetCategoryLocalService.getVocabularyCategories(
							assetVocabulary.getVocabularyId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

					selectionFDSFilterItems.add(
						new SelectionFDSFilterItem(
							getSelectionFDSFilterItemLabel(
								assetCategory, assetVocabulary, locale),
							assetCategory.getCategoryId()));
				}
			}

			return selectionFDSFilterItems;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	protected abstract List<AssetVocabulary> getAssetVocabularies(long groupId)
		throws PortalException;

	protected List<AssetVocabulary> getAssetVocabularies(
		String assetVocabularyExternalReferenceCode, long groupId) {

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					assetVocabularyExternalReferenceCode, groupId);

		if (assetVocabulary == null) {
			return Collections.emptyList();
		}

		return Collections.singletonList(assetVocabulary);
	}

	protected String getSelectionFDSFilterItemLabel(
		AssetCategory assetCategory, AssetVocabulary assetVocabulary,
		Locale locale) {

		return assetCategory.getTitle(locale);
	}

	@Reference
	protected AssetCategoryLocalService assetCategoryLocalService;

	@Reference
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Reference
	protected GroupLocalService groupLocalService;

}