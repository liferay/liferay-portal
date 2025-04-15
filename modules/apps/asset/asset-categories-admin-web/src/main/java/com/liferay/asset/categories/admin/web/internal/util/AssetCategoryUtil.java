/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AssetCategoryUtil {

	public static List<BreadcrumbEntry> getAssetCategoriesBreadcrumbEntries(
		AssetVocabulary vocabulary, AssetCategory category,
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		if (category == null) {
			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					vocabulary.getTitle(themeDisplay.getLocale()));
				breadcrumbEntry.setURL(
					PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCPath(
						"/view.jsp"
					).setNavigation(
						() -> {
							String navigation = ParamUtil.getString(
								httpServletRequest, "navigation");

							if (Validator.isNotNull(navigation)) {
								return navigation;
							}

							return null;
						}
					).setParameter(
						"vocabularyId", vocabulary.getVocabularyId()
					).buildString());
			}
		).addAll(
			() -> {
				List<AssetCategory> assetCategories = category.getAncestors();

				Collections.reverse(assetCategories);

				return TransformUtil.transform(
					assetCategories,
					curCategory -> BreadcrumbEntryBuilder.setTitle(
						curCategory.getTitle(themeDisplay.getLocale())
					).setURL(
						PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCPath(
							"/view.jsp"
						).setNavigation(
							() -> {
								String navigation = ParamUtil.getString(
									httpServletRequest, "navigation");

								if (Validator.isNotNull(navigation)) {
									return navigation;
								}

								return null;
							}
						).setParameter(
							"categoryId", curCategory.getCategoryId()
						).setParameter(
							"vocabularyId", vocabulary.getVocabularyId()
						).buildString()
					).build());
			}
		).add(
			breadcrumbEntry -> breadcrumbEntry.setTitle(
				category.getTitle(themeDisplay.getLocale()))
		).build();
	}

}