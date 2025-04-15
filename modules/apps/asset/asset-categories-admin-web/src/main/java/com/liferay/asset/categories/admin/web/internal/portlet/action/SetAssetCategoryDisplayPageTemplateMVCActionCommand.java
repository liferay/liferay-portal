/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.portlet.action;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Barbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/asset_categories_admin/set_asset_category_display_page_template"
	},
	service = MVCActionCommand.class
)
public class SetAssetCategoryDisplayPageTemplateMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] categoryIds = ParamUtil.getLongValues(
			actionRequest, "categoryIds");

		for (long categoryId : categoryIds) {
			AssetCategory category = _assetCategoryLocalService.getCategory(
				categoryId);

			_assetDisplayPageEntryFormProcessor.process(
				AssetCategory.class.getName(), category.getCategoryId(),
				actionRequest);

			category.setModifiedDate(new Date());

			_assetCategoryLocalService.updateAssetCategory(category);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

}