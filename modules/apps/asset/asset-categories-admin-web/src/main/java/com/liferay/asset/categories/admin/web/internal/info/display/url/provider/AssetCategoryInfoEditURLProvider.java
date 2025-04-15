/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.info.display.url.provider;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetCategory",
	service = InfoEditURLProvider.class
)
public class AssetCategoryInfoEditURLProvider
	implements InfoEditURLProvider<AssetCategory> {

	@Override
	public String getURL(
		AssetCategory assetCategory, HttpServletRequest httpServletRequest) {

		Group group = _groupLocalService.fetchGroup(assetCategory.getGroupId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (group.isCompany()) {
			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_asset_category.jsp"
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					httpServletRequest, "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return themeDisplay.getURLCurrent();
			}
		).setParameter(
			"categoryId", assetCategory.getCategoryId()
		).setParameter(
			"vocabularyId", assetCategory.getVocabularyId()
		).buildString();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}