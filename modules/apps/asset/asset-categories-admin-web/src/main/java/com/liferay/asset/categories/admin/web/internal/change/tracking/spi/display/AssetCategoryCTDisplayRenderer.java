/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.change.tracking.spi.display;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class AssetCategoryCTDisplayRenderer
	extends BaseCTDisplayRenderer<AssetCategory> {

	@Override
	public String[] getAvailableLanguageIds(AssetCategory assetCategory) {
		return assetCategory.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(AssetCategory assetCategory) {
		return assetCategory.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, AssetCategory assetCategory)
		throws Exception {

		Group group = _groupLocalService.getGroup(assetCategory.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

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
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"categoryId", assetCategory.getCategoryId()
		).setParameter(
			"vocabularyId", assetCategory.getVocabularyId()
		).buildString();
	}

	@Override
	public Class<AssetCategory> getModelClass() {
		return AssetCategory.class;
	}

	@Override
	public String getTitle(Locale locale, AssetCategory assetCategory) {
		return assetCategory.getTitle(locale);
	}

	@Override
	protected void buildDisplay(DisplayBuilder<AssetCategory> displayBuilder)
		throws PortalException {

		AssetCategory assetCategory = displayBuilder.getModel();

		displayBuilder.display(
			"category", assetCategory.getTitle(displayBuilder.getLocale())
		).display(
			"description",
			assetCategory.getDescription(displayBuilder.getLocale())
		).display(
			"create-date", assetCategory.getCreateDate()
		).display(
			"path", assetCategory.getPath(displayBuilder.getLocale(), true)
		).display(
			"subcategories",
			_assetCategoryLocalService.getChildCategoriesCount(
				assetCategory.getCategoryId())
		);
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}