/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.portlet.action;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Barbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/asset_categories_admin/move_asset_category"
	},
	service = MVCActionCommand.class
)
public class MoveAssetCategoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long parentCategoryId = ParamUtil.getLong(
			actionRequest, "parentCategoryId");
		long vocabularyId = ParamUtil.getLong(actionRequest, "vocabularyId");

		if ((vocabularyId <= 0) && (parentCategoryId <= 0)) {
			throw new NoSuchVocabularyException();
		}

		long categoryId = ParamUtil.getLong(actionRequest, "categoryId");

		if (vocabularyId <= 0) {
			AssetCategory parentCategory = _assetCategoryService.fetchCategory(
				parentCategoryId);

			vocabularyId = parentCategory.getVocabularyId();
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AssetCategory.class.getName(), actionRequest);

		_assetCategoryService.moveCategory(
			categoryId, parentCategoryId, vocabularyId, serviceContext);
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

}