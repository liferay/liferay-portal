/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class CategorizationBreadcrumbUtil {

	public static JSONArray getNavigationBreadcrumbsJSONArray(
			long assetVocabularyId, long categoryId, ThemeDisplay themeDisplay)
		throws PortalException {

		if (categoryId == 0) {
			return _getPrimaryBreadcrumbsJSONArray(
				true, assetVocabularyId, themeDisplay);
		}

		AssetCategory assetCategory =
			AssetCategoryLocalServiceUtil.getAssetCategory(categoryId);

		return _getBreadcrumbsJSONArray(
			assetCategory, themeDisplay
		).put(
			JSONUtil.put(
				"active", true
			).put(
				"label", assetCategory.getName()
			)
		);
	}

	public static JSONArray getUsagesBreadcrumbsJSONArray(
			long categoryId, ThemeDisplay themeDisplay)
		throws PortalException {

		AssetCategory assetCategory =
			AssetCategoryLocalServiceUtil.getAssetCategory(categoryId);

		return _getBreadcrumbsJSONArray(
			assetCategory, themeDisplay
		).put(
			JSONUtil.put(
				"active", true
			).put(
				"label",
				LanguageUtil.format(
					themeDisplay.getLocale(), "x-usages",
					assetCategory.getName())
			)
		);
	}

	private static JSONArray _getBreadcrumbsJSONArray(
			AssetCategory assetCategory, ThemeDisplay themeDisplay)
		throws PortalException {

		JSONArray jsonArray = _getPrimaryBreadcrumbsJSONArray(
			false, assetCategory.getVocabularyId(), themeDisplay);

		List<AssetCategory> ancestorCategories = assetCategory.getAncestors();

		Collections.reverse(ancestorCategories);

		for (AssetCategory category : ancestorCategories) {
			jsonArray.put(
				JSONUtil.put(
					"active", false
				).put(
					"href",
					HttpComponentsUtil.addParameters(
						PortalUtil.getLayoutFullURL(
							LayoutLocalServiceUtil.getLayoutByFriendlyURL(
								themeDisplay.getScopeGroupId(), false,
								"/categorization/view-categories"),
							themeDisplay),
						"vocabularyId", category.getVocabularyId(),
						"categoryId", category.getCategoryId())
				).put(
					"label", category.getName()
				));
		}

		return jsonArray;
	}

	private static JSONArray _getPrimaryBreadcrumbsJSONArray(
			boolean activeLastItem, long assetVocabularyId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		JSONArray jsonArray = JSONUtil.put(
			JSONUtil.put(
				"active", false
			).put(
				"href",
				() -> PortalUtil.getLayoutFullURL(
					LayoutLocalServiceUtil.getLayoutByFriendlyURL(
						themeDisplay.getScopeGroupId(), false,
						"/categorization/view-vocabularies"),
					themeDisplay)
			).put(
				"label",
				LanguageUtil.get(themeDisplay.getLocale(), "categorization")
			));

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.getAssetVocabulary(
				assetVocabularyId);

		return jsonArray.put(
			JSONUtil.put(
				"active", activeLastItem
			).put(
				"href",
				() -> {
					if (activeLastItem) {
						return null;
					}

					return HttpComponentsUtil.addParameter(
						PortalUtil.getLayoutFullURL(
							LayoutLocalServiceUtil.getLayoutByFriendlyURL(
								themeDisplay.getScopeGroupId(), false,
								"/categorization/view-categories"),
							themeDisplay),
						"vocabularyId", assetVocabularyId);
				}
			).put(
				"label", assetVocabulary.getName()
			));
	}

}